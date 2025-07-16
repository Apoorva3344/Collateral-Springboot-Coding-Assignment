// File: src/main/java/com/example/collateral/client/EligibilityServiceClient.java
package com.example.collateral.client;

import com.example.collateral.model.EligibilityData;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.List;

/**
 * EligibilityServiceClient
 * ------------------------
 * Purpose:
 * - Fetches collateral eligibility and associated discount factors for given accounts and assets.
 * - Determines which positions a financial institution can include in collateral calculations.
 *
 * Technical Rationale:
 * - Marked as @Component to allow Spring to manage its lifecycle and inject it where needed.
 * - In a production environment, this client would encapsulate HTTP calls (e.g., RestTemplate or WebClient)
 *   to an external Eligibility microservice. Here, we mock responses to focus on core business logic.
 * - Synchronous method signature chosen for simplicity;
 *
 * Assumptions Made:
 * 1. EligibilityData covers both eligible and ineligible assets in separate entries.
 * 2. Discount value is only meaningful when "eligible" is true; otherwise, treated as 0.0.
 * 3. The same EligibilityData entry can enumerate multiple accountIDs and assetIDs to reduce API payload.
 *
 * Domain Knowledge:
 * - Financial collateral haircuts: a discount factor (e.g., 0.9) represents a 10% haircut for risk management.
 * - Ineligible assets (eligible=false) are excluded entirely (zero collateral contribution).
 * - Eligibility criteria might include asset type, market volatility, or account-specific risk parameters.
 */
@Component
public class EligibilityServiceClient {

    /**
     * getEligibility
     * ---------------
     * Fetches eligibility metadata for each combination of account and asset.
     *
     * @param accountIds List of account identifiers (e.g., ["E1","E2"]).
     *                   
     *                   Assumption: Contains unique, valid account IDs recognized by eligibility service.
     *
     * @param assetIds   List of asset identifiers (e.g., ["S1","S3","S4"]).
     *                   
     *                   Assumption: Contains unique, valid asset tickers. No null or empty strings.
     *
     * @return List of EligibilityData:
     *         - eligible: boolean indicating if asset can be used as collateral.
     *         - assetIDs: group of assets covered by this entry.
     *         - accountIDs: group of accounts covered by this entry.
     *         - discount: factor applied to eligible positions (e.g., 0.9 => 10% haircut).
     *
     * Technical Notes:
     * - Grouping multiple IDs reduces number of API calls and response size.
     * - In real implementation, would construct a request DTO and call external REST endpoint:
     *     restTemplate.postForObject(ELIGIBILITY_URL, requestPayload, EligibilityData[].class)
     * - Response mapping handled by Jackson (default in Spring Boot) to convert JSON array into List<EligibilityData>.
     */
    public List<EligibilityData> getEligibility(List<String> accountIds, List<String> assetIds) {
        // Mock implementation: returns hardcoded eligibility entries matching sample data
        // Ineligible group has discount explicitly set to 0.0 for clarity
        return Arrays.asList(
            new EligibilityData(
                /* eligible= */ true,
                /* assetIDs= */ Arrays.asList("S1", "S2", "S3"),
                /* accountIDs= */ accountIds,
                /* discount= */ 0.9
            ),
            new EligibilityData(
                /* eligible= */ false,
                /* assetIDs= */ Arrays.asList("S4", "S5"),
                /* accountIDs= */ accountIds,
                /* discount= */ 0.0
            )
        );
    }
}
