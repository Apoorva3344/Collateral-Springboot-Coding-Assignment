// File: src/main/java/com/example/collateral/client/PriceServiceClient.java
package com.example.collateral.client;

import com.example.collateral.model.AssetPrice;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.List;

/**
 * PriceServiceClient
 * ------------------
 * Purpose:
 * - Retrieves current market prices for a set of asset identifiers.
 * - Enables valuation of collateral positions by providing unit prices.
 *
 * Technical Rationale:
 * - Annotated with @Component for Spring-managed injection into service layers.
 * - Uses synchronous signature List<AssetPrice> getPrices(List<String>) for simplicity.
 *   In production scenarios where low-latency or high-throughput is critical, consider:
 *   - Reactive WebClient for non-blocking I/O.
 *   - Batching requests with caching (e.g., Caffeine, Redis) to reduce network overhead.
 *   - Circuit breakers (Resilience4j) to degrade gracefully if price service is unavailable.
 *
 * Assumptions Made:
 * 1. Input assetIds list is non-null and contains unique, valid asset tickers.
 * 2. The price service will return a price for every requested asset; if an asset is missing,
 *    the caller (CollateralCalculationService) should detect absence and skip that asset.
 * 3. Prices are represented as doubles (two-decimal currency precision managed downstream via
 *    BigDecimal in calculation service).
 * 4. No error handling (e.g., HTTP timeouts, 404s) is implemented in this mock; real client would
 *    wrap calls in try/catch and handle exceptions.
 * 5. No pagination or streaming; assumes full list returned in a single payload.
 *
 * Domain Knowledge:
 * - Asset prices reflect current market value per unit of collateral (e.g., shares, bonds).
 * - Collateral valuation requires up-to-date prices; stale data can misrepresent risk exposure.
 * - In real financial systems, price feeds may come from dedicated Market Data services,
 *   often with SLAs, subscription costs, and SLAs for data freshness.
 */
@Component
public class PriceServiceClient {

    /**
     * getPrices
     * ---------
     * Returns mock prices for given asset IDs. In a live system, this method would:
     * 1. Construct an HTTP request to the Price API endpoint, including required headers,
     *    authentication tokens, and the list of asset IDs as JSON or query parameters.
     * 2. Parse the JSON response into AssetPrice[] using Jackson.
     * 3. Handle HTTP errors, retry on transient failures, and log metrics.
     *
     * @param assetIds List of asset identifiers to price (e.g., ["S1","S3","S4"]).
     *                 Assumes IDs are valid and recognized by price service.
     * @return List of AssetPrice objects, containing assetId and unit price.
     *
     * Mock Data Verification:
     * - Includes S1-S5 to align with positions and eligibility mock services:
     *   * S1 (50.5), S2 (20.2), S3 (10.4), S4 (15.5) cover primary assets.
     *   * Added S5 (25.0) so that positions referencing S5 can test the missing-price skip logic.
     */
    public List<AssetPrice> getPrices(List<String> assetIds) {
        // Mock response matching sample assignment data plus additional S5 for edge case
        AssetPrice p1 = new AssetPrice("S1", 50.5);  // Eligible & priced
        AssetPrice p2 = new AssetPrice("S2", 20.2);  // Eligible & priced
        AssetPrice p3 = new AssetPrice("S3", 10.4);  // Eligible & priced
        AssetPrice p4 = new AssetPrice("S4", 15.5);  // Ineligible -> will be filtered by eligibility logic
        AssetPrice p5 = new AssetPrice("S5", 25.0);  // Price available; eligibility client marks S5 ineligible by mock

        return Arrays.asList(p1, p2, p3, p4, p5);
    }
}
