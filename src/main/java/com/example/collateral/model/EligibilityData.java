// File: src/main/java/com/example/collateral/model/EligibilityData.java
package com.example.collateral.model;

import java.util.List;

/**
 * EligibilityData
 * ---------------
 * Purpose:
 * - Data Transfer Object representing eligibility criteria and discount factors for combinations
 *   of accounts and assets.
 * - Used by CollateralCalculationService to determine which positions contribute to collateral and how.
 *
 * Technical Rationale:
 * - Implements a simple POJO pattern with private fields, getters/setters, and a no-arg constructor
 *   to support JSON (de)serialization via Jackson.
 * - Uses List<String> for assetIDs and accountIDs to allow grouping multiple entities in a single DTO,
 *   reducing the number of API calls and response payload size.
 * - The discount field is a primitive double for performance; precise financial calculations are handled
 *   later with BigDecimal in service layer.
 * - Overrides toString() to aid logging and debugging, especially for complex grouped data.
 *
 * Assumptions Made:
 * 1. assetIDs and accountIDs lists are non-null and contain unique identifiers;
 *    duplicate entries are not expected.
 * 2. The eligible boolean indicates whether assets in assetIDs are acceptable as collateral
 *    for any account in accountIDs.
 * 3. The discount factor applies uniformly to all assetIDs/accountIDs in this DTO when eligible=true;
 *    for ineligible entries, discount should be treated as 0.0.
 * 4. No validation logic within this DTO; validation (e.g., discount in [0,1]) occurs in service layer or
 *    upstream API.
 * 5. No pagination or partial responses assumed; API returns complete eligibility dataset for requested IDs.
 *
 * Domain Knowledge:
 * - Collateral haircuts: discount factor (e.g., 0.9) reduces asset value by 10% to account for market risk.
 * - Ineligible assets (eligible=false) contribute zero to collateral, guarding against high-risk or illiquid assets.
 * - Grouping eligibility by account and asset reduces network chatter and leverages domain rules that often
 *   apply uniformly across portfolios.
 */
public class EligibilityData {

    /**
     * Flag indicating if the specified assets are eligible as collateral for specified accounts.
     */
    private boolean eligible;

    /**
     * List of asset identifiers (e.g., ["S1","S2"]) covered by this eligibility rule.
     */
    private List<String> assetIDs;

    /**
     * List of account identifiers (e.g., ["E1","E2"]) to which this eligibility rule applies.
     */
    private List<String> accountIDs;

    /**
     * Discount factor to apply when eligible=true (e.g., 0.9 means 10% haircut).
     * Should be in the range [0.0, 1.0].
     */
    private double discount;

    /**
     * Default constructor for JSON deserialization.
     * Required by Jackson to instantiate before setting properties.
     */
    public EligibilityData() {}

    /**
     * Parameterized constructor for manual instantiation in mocks, tests, or demos.
     *
     * @param eligible   eligibility flag for these asset-account combinations
     * @param assetIDs   list of asset identifiers
     * @param accountIDs list of account identifiers
     * @param discount   discount factor to apply when eligible=true
     */
    public EligibilityData(boolean eligible, List<String> assetIDs,
                           List<String> accountIDs, double discount) {
        this.eligible = eligible;
        this.assetIDs = assetIDs;
        this.accountIDs = accountIDs;
        this.discount = discount;
    }

    /**
     * @return true if assets are eligible for collateral
     */
    public boolean isEligible() {
        return eligible;
    }

    /**
     * @param eligible sets eligibility flag
     */
    public void setEligible(boolean eligible) {
        this.eligible = eligible;
    }

    /**
     * @return list of asset IDs to which this rule applies
     */
    public List<String> getAssetIDs() {
        return assetIDs;
    }

    /**
     * @param assetIDs sets the list of asset identifiers
     */
    public void setAssetIDs(List<String> assetIDs) {
        this.assetIDs = assetIDs;
    }

    /**
     * @return list of account IDs to which this rule applies
     */
    public List<String> getAccountIDs() {
        return accountIDs;
    }

    /**
     * @param accountIDs sets the list of account identifiers
     */
    public void setAccountIDs(List<String> accountIDs) {
        this.accountIDs = accountIDs;
    }

    /**
     * @return discount factor for eligible assets
     */
    public double getDiscount() {
        return discount;
    }

    /**
     * @param discount sets the discount factor; expected between 0.0 and 1.0
     */
    public void setDiscount(double discount) {
        this.discount = discount;
    }

    /**
     * String representation for debugging and logging.
     *
     * @return formatted string showing eligibility, assetIDs, accountIDs, and discount
     */
    @Override
    public String toString() {
        return "EligibilityData{" +
               "eligible=" + eligible +
               ", assetIDs=" + assetIDs +
               ", accountIDs=" + accountIDs +
               ", discount=" + discount +
               '}';
    }
}
