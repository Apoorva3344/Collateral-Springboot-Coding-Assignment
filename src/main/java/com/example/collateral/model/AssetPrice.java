// File: src/main/java/com/example/collateral/model/AssetPrice.java
package com.example.collateral.model;

/**
 * AssetPrice
 * ----------
 * Purpose:
 * - Data Transfer Object representing the market price of a single asset.
 * - Encapsulates an asset identifier and its current unit price for collateral valuation.
 *
 * Technical Rationale:
 * - Simple POJO with private fields, getters, setters, and a no-arg constructor to support
 *   JSON (de)serialization via Jackson or similar libraries.
 * - Uses primitive double for price to balance performance; high-precision calculations are performed
 *   downstream using BigDecimal in the service layer to avoid floating-point issues.
 * - toString() override helps with logging, debugging, and unit test diagnostics.
 *
 * Assumptions Made:
 * 1. assetId is a non-null, non-empty string that matches those used in Position and Eligibility models.
 * 2. price is always a non-negative value representing the current market price per unit.
 * 3. No currency code is stored; price is assumed to be in a consistent currency (e.g., USD) across the system.
 * 4. Validation (e.g., price != NaN or infinite) is handled upstream or in service logic; this DTO assumes clean data.
 *
 * Domain Knowledge:
 * - Accurate asset prices are critical for collateral valuation, risk management, and margin calculations.
 * - Prices typically originate from market data feeds and may require caching, throttling, or reconciliation
 *   to ensure data freshness and SLA compliance.
 */
public class AssetPrice {

    /**
     * Unique identifier for the asset (e.g., "S1").
     * Used to correlate price data with positions and eligibility rules.
     */
    private String assetId;

    /**
     * Current market price per unit of the asset.
     * Represented as a double; two-decimal precision enforced later.
     */
    private double price;

    /**
     * Default constructor required for JSON deserialization.
     * Instantiates the object before populating fields via setters.
     */
    public AssetPrice() {}

    /**
     * Convenience constructor for manual instantiation (e.g., in mocks or demo).
     *
     * @param assetId the asset identifier
     * @param price   the market price for the asset
     */
    public AssetPrice(String assetId, double price) {
        this.assetId = assetId;
        this.price = price;
    }

    /**
     * @return the asset identifier
     */
    public String getAssetId() {
        return assetId;
    }

    /**
     * @param assetId sets the asset identifier
     */
    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    /**
     * @return the market price per unit
     */
    public double getPrice() {
        return price;
    }

    /**
     * @param price sets the market price; should be non-negative
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Provides a human-readable representation of this object.
     * Useful for logging and debugging.
     *
     * @return string in format AssetPrice{assetId='S1', price=50.5}
     */
    @Override
    public String toString() {
        return "AssetPrice{" +
                "assetId='" + assetId + '\'' +
                ", price=" + price +
                '}';
    }
}
