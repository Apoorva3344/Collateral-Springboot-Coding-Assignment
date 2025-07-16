// File: src/main/java/com/example/collateral/model/Position.java
package com.example.collateral.model;

/**
 * Position
 * --------
 * Purpose:
 * - Data Transfer Object representing a holding of a specific asset in an account.
 * - Encapsulates asset identifier and quantity held, forming the basis for valuation.
 *
 * Technical Rationale:
 * - Simple POJO with private fields, getters/setters, and a no-arg constructor to support
 *   JSON (de)serialization by Jackson.
 * - Uses primitive int for quantity due to expected whole-unit asset counts; avoids unnecessary
 *   boxing overhead.
 * - toString() override provides clear, structured output for logging, debugging, and tests.
 *
 * Assumptions Made:
 * 1. assetId is a non-null, non-empty string matching identifiers used across services.
 * 2. quantity is a non-negative integer; zero implies no actual holding (can be filtered upstream).
 * 3. No bounds checking here; validation (e.g., quantity > 0) is handled in the business layer or
 *    upstream API, ensuring clean data arrives at service logic.
 * 4. This DTO does not record fractional quantities; supports only whole-unit assets, which aligns
 *    with many collateralized assets (e.g., shares). For fractional or currency amounts, a decimal
 *    type would be required.
 *
 * Domain Knowledge:
 * - Positions represent the number of units of an asset (e.g., stocks, bonds) held in a financial account.
 * - Accurate position data is critical for calculating exposure, collateral requirements, and risk metrics.
 * - Systems may aggregate multiple position entries by asset; this DTO assumes each Position is unique per asset.
 */
public class Position {

    /**
     * Unique identifier for the asset (e.g., "S1").
     * Used to correlate with price and eligibility data.
     */
    private String assetId;

    /**
     * Quantity of the asset held in the account.
     * Expected to be a whole number representing units owned.
     */
    private int quantity;

    /**
     * Default constructor required for JSON deserialization.
     * Jackson instantiates the object before populating fields.
     */
    public Position() {}

    /**
     * Convenience constructor for manual instantiation (e.g., in mocks, tests, or demos).
     *
     * @param assetId  the asset identifier
     * @param quantity the number of units held; non-negative
     */
    public Position(String assetId, int quantity) {
        this.assetId = assetId;
        this.quantity = quantity;
    }

    /**
     * @return the asset identifier
     */
    public String getAssetId() {
        return assetId;
    }

    /**
     * @param assetId sets the asset identifier; should not be null or empty
     */
    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    /**
     * @return the quantity of units held
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * @param quantity sets the units held; should be non-negative
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Provides a human-readable string representation of this object.
     * Useful for logging, debugging, and test output.
     *
     * @return string in format Position{assetId='S1', quantity=100}
     */
    @Override
    public String toString() {
        return "Position{" +
                "assetId='" + assetId + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
