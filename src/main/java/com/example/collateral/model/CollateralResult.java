// File: src/main/java/com/example/collateral/model/CollateralResult.java
package com.example.collateral.model;

/**
 * CollateralResult
 * ----------------
 * Purpose:
 * - DTO representing the outcome of collateral valuation for a given account.
 * - Contains the account identifier and the computed collateral value.
 *
 * Technical Rationale:
 * - Simple POJO with private fields, getters, setters, and a no-arg constructor to support
 *   JSON serialization/deserialization with Jackson.
 * - Uses primitive double for collateralValue to minimize overhead; precision and rounding
 *   are managed in the service layer using BigDecimal before assignment to this DTO.
 * - toString() override provides clear, structured representation useful in logs and tests.
 *
 * Assumptions Made:
 * 1. accountId is a non-null, valid identifier corresponding to an account in Position and
 *    Eligibility contexts.
 * 2. collateralValue is a non-negative double, already rounded to two decimal places (currency format).
 * 3. No additional metadata (e.g., currency code, timestamp) is required; these aspects are
 *    handled at higher layers or in separate response envelopes.
 * 4. Validation (e.g., ensuring collateralValue >= 0) is performed in the business logic before
 *    constructing this object.
 *
 * Domain Knowledge:
 * - Collateral values are critical for margin calls, risk reporting, and regulatory
 *   compliance in financial institutions.
 * - Rounding to two decimal places aligns with typical currency conventions (e.g., dollars, pounds).
 */
public class CollateralResult {

    /**
     * Unique identifier for the account whose collateral was calculated (e.g., "E1").
     */
    private String accountId;

    /**
     * Computed collateral value for the account, rounded to two decimal places.
     * Represents the sum of (quantity × price × discount) for eligible positions.
     */
    private double collateralValue;

    /**
     * Default constructor needed for JSON serialization/deserialization.
     */
    public CollateralResult() {}

    /**
     * Parameterized constructor for creating instances after calculation.
     *
     * @param accountId       the account identifier
     * @param collateralValue the computed collateral amount, two-decimal precision
     */
    public CollateralResult(String accountId, double collateralValue) {
        this.accountId = accountId;
        this.collateralValue = collateralValue;
    }

    /**
     * @return the account identifier
     */
    public String getAccountId() {
        return accountId;
    }

    /**
     * @param accountId sets the account identifier
     */
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    /**
     * @return the calculated collateral value
     */
    public double getCollateralValue() {
        return collateralValue;
    }

    /**
     * @param collateralValue sets the collateral value; expected to be non-negative
     */
    public void setCollateralValue(double collateralValue) {
        this.collateralValue = collateralValue;
    }

    /**
     * @return human-readable representation of the collateral result
     */
    @Override
    public String toString() {
        return "CollateralResult{" +
                "accountId='" + accountId + '\'' +
                ", collateralValue=" + collateralValue +
                '}';
    }
}
