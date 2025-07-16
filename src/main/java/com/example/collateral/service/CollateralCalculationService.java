// File: src/main/java/com/example/collateral/service/CollateralCalculationService.java
package com.example.collateral.service;

import com.example.collateral.model.AccountPosition;
import com.example.collateral.model.AssetPrice;
import com.example.collateral.model.CollateralResult;
import com.example.collateral.model.EligibilityData;
import com.example.collateral.client.PositionServiceClient;
import com.example.collateral.client.EligibilityServiceClient;
import com.example.collateral.client.PriceServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * CollateralCalculationService
 * -----------------------------
 * Purpose:
 * - Orchestrates data retrieval and business logic to compute collateral values
 *   for a list of financial accounts based on their positions, eligibility rules,
 *   and market prices.
 *
 * Technical Rationale:
 * - Annotated as @Service for Spring to detect and manage lifecycle,
 *   enabling dependency injection of client components.
 * - Uses synchronous calls to clients for simplicity; acceptable if downstream
 *   services are performant. For high-throughput use cases, consider async/reactive.
 * - BigDecimal used for precise financial arithmetic, avoiding floating-point errors.
 * - Splits logic into small private methods (createPriceMap, findEligibilityInfo,
 *   calculatePositionCollateralValue) to improve readability and testability.
 *
 * Assumptions Made:
 * 1. accountIds list is non-null and contains valid, unique identifiers.
 * 2. Downstream clients return complete datasets (no pagination needed). Missing
 *    prices or eligibility entries imply zero collateral contribution.
 * 3. Discount factors are in [0.0,1.0]; invalid values filtered or defaulted upstream.
 * 4. Duplicate asset entries in price list keep first occurrence (merge strategy).
 * 5. Exceptions from client calls propagate; should be handled by controller or
 *    higher layers in production (e.g., circuit breakers, retries).
 *
 * Domain Knowledge:
 * - Collateral valuation uses haircut factors (discount) to mitigate risk of asset
 *   price volatility.
 * - Ineligible positions (no discount entry) are excluded to avoid unapproved collateral.
 * - Two-decimal rounding (HALF_UP) aligns with typical currency precision.
 */
@Service
public class CollateralCalculationService {

    @Autowired
    private PositionServiceClient positionServiceClient;
    @Autowired
    private EligibilityServiceClient eligibilityServiceClient;
    @Autowired
    private PriceServiceClient priceServiceClient;

    /**
     * calculateCollateralValue
     * ------------------------
     * Fetches required data and computes the total collateral for each account.
     *
     * @param accountIds list of account identifiers to process
     * @return list of CollateralResult containing accountId and computed value
     */
    public List<CollateralResult> calculateCollateralValue(List<String> accountIds) {
        // 1. Retrieve positions for each account
        List<AccountPosition> accountPositions = positionServiceClient.getPositions(accountIds);

        // 2. Aggregate unique asset IDs across all accounts
        Set<String> allAssetIds = accountPositions.stream()
            .flatMap(ap -> ap.getPositions().stream().map(p -> p.getAssetId()))
            .collect(Collectors.toSet());

        // 3. Fetch eligibility rules for account-asset combinations
        List<EligibilityData> eligibilityDataList = eligibilityServiceClient
            .getEligibility(accountIds, new ArrayList<>(allAssetIds));

        // 4. Fetch current market prices for all relevant assets
        List<AssetPrice> assetPrices = priceServiceClient.getPrices(new ArrayList<>(allAssetIds));

        // 5. Prepare a lookup map for fast price retrieval
        Map<String, Double> priceMap = createPriceMap(assetPrices);

        // 6. Compute collateral per account
        List<CollateralResult> results = new ArrayList<>();
        for (AccountPosition ap : accountPositions) {
            BigDecimal totalCollateral = BigDecimal.ZERO;
            for (var position : ap.getPositions()) {
                // Extract details
                String assetId = position.getAssetId();
                int quantity = position.getQuantity();

                // Lookup price; default to zero if absent
                double price = priceMap.getOrDefault(assetId, 0.0);

                // Determine eligibility and discount
                EligibilityInfo info = findEligibilityInfo(eligibilityDataList,
                                                           ap.getAccountId(), assetId);

                // Calculate position-level collateral
                BigDecimal collateral = calculatePositionCollateralValue(
                    quantity, price, info);

                totalCollateral = totalCollateral.add(collateral);
            }
            // Apply two-decimal rounding for final result
            BigDecimal rounded = totalCollateral.setScale(2, RoundingMode.HALF_UP);
            results.add(new CollateralResult(ap.getAccountId(), rounded.doubleValue()));
        }

        return results;
    }

    /**
     * createPriceMap
     * --------------
     * Converts a list of AssetPrice into a Map for quick lookups.
     *
     * @param assetPrices list of AssetPrice DTOs
     * @return map of assetId to price; duplicates keep first occurrence
     */
    private Map<String, Double> createPriceMap(List<AssetPrice> assetPrices) {
        return assetPrices.stream()
            .collect(Collectors.toMap(
                AssetPrice::getAssetId,
                AssetPrice::getPrice,
                (first, second) -> first  // merge duplicate keys by keeping first
            ));
    }

    /**
     * findEligibilityInfo
     * -------------------
     * Searches the eligibility dataset for a matching account-asset rule.
     *
     * @param eligibilityDataList list of grouped eligibility entries
     * @param accountId the account in question
     * @param assetId   the asset in question
     * @return EligibilityInfo with eligibility flag and discount
     */
    private EligibilityInfo findEligibilityInfo(List<EligibilityData> eligibilityDataList,
                                               String accountId, String assetId) {
        for (EligibilityData ed : eligibilityDataList) {
            if (ed.getAccountIDs().contains(accountId) &&
                ed.getAssetIDs().contains(assetId)) {
                return new EligibilityInfo(ed.isEligible(), ed.getDiscount());
            }
        }
        // Default assumption: ineligible if no rule found
        return new EligibilityInfo(false, 0.0);
    }

    /**
     * calculatePositionCollateralValue
     * ---------------------------------
     * Computes collateral for a single position: quantity × price × discount.
     *
     * @param quantity        number of units held
     * @param price           unit price of the asset
     * @param eligibilityInfo eligibility and discount metadata
     * @return BigDecimal representing collateral for this position
     */
    private BigDecimal calculatePositionCollateralValue(int quantity,
                                                        double price,
                                                        EligibilityInfo eligibilityInfo) {
        // Exclude ineligible positions immediately
        if (!eligibilityInfo.isEligible()) {
            return BigDecimal.ZERO;
        }
        // Perform high-precision math
        BigDecimal qtyBD = BigDecimal.valueOf(quantity);
        BigDecimal priceBD = BigDecimal.valueOf(price);
        BigDecimal discountBD = BigDecimal.valueOf(eligibilityInfo.getDiscountFactor());

        return qtyBD.multiply(priceBD).multiply(discountBD);
    }

    /**
     * EligibilityInfo
     * ----------------
     * Internal DTO capturing eligibility status and discount factor for a position.
     */
    private static class EligibilityInfo {
        private final boolean eligible;
        private final double discountFactor;

        EligibilityInfo(boolean eligible, double discountFactor) {
            this.eligible = eligible;
            this.discountFactor = discountFactor;
        }

        public boolean isEligible() { return eligible; }
        public double getDiscountFactor() { return discountFactor; }
    }
}
