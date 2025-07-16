// File: src/main/java/com/example/collateral/model/AccountPosition.java
package com.example.collateral.model;

import java.util.List;

/**
 * AccountPosition
 * ---------------
 * Purpose:
 * - Data Transfer Object representing all asset positions held by a specific financial account.
 * - Encapsulates an account ID and a list of Position objects for collateral valuation.
 *
 * Technical Rationale:
 * - Simple POJO with getters/setters to allow Jackson to serialize/deserialize JSON payloads.
 * - Default constructor is required by Jackson during deserialization of HTTP responses or requests.
 * - toString() override aids in debugging, logging, and test assertions by providing readable output.
 * - No business logic here; this class follows the DTO pattern exclusively.
 *
 * Assumptions Made:
 * 1. accountId is a unique, non-null identifier used consistently across services.
 * 2. positions list contains one Position per distinct assetId; duplicates are not expected.
 * 3. Position.quantity values are non-negative integers; zero indicates no holding.
 * 4. No validation is performed here; validation (e.g., non-null, size>0) occurs in service layer if needed.
 *
 * Domain Knowledge:
 * - In finance, an account may hold multiple asset types (stocks, bonds, derivatives), each of which
 *   contributes to overall collateral based on quantity and market value.
 * - Accurate modeling of positions is critical for risk management and margin calculations.
 */
public class AccountPosition {

    /**
     * Unique identifier for the account (e.g., "E1").
     * Used to correlate positions with eligibility and price data.
     */
    private String accountId;

    /**
     * List of Position entries, each specifying an assetId and quantity held.
     * Assumes positions list is non-null; empty list signifies no holdings.
     */
    private List<Position> positions;

    /**
     * Default constructor for JSON (de)serialization.
     * Jackson and other frameworks require a no-arg constructor to instantiate the object
     * before setting properties via setters or reflection.
     */
    public AccountPosition() {
    }

    /**
     * Convenience constructor to create fully-populated AccountPosition instances.
     * Helpful for manual instantiation in mocks, tests, or demo code.
     *
     * @param accountId unique account identifier
     * @param positions list of Position objects for the account
     */
    public AccountPosition(String accountId, List<Position> positions) {
        this.accountId = accountId;
        this.positions = positions;
    }

    /**
     * @return the accountId for this AccountPosition
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
     * @return the list of Position objects held by the account
     */
    public List<Position> getPositions() {
        return positions;
    }

    /**
     * @param positions sets the list of Position objects; null or empty indicates no holdings
     */
    public void setPositions(List<Position> positions) {
        this.positions = positions;
    }

    /**
     * Provides a human-readable string representation of this object.
     * Useful for logging, debugging, and unit test failure messages.
     *
     * @return string in format AccountPosition{accountId='E1', positions=[...]}
     */
    @Override
    public String toString() {
        return "AccountPosition{" +
                "accountId='" + accountId + '\'' +
                ", positions=" + positions +
                '}';
    }
}
