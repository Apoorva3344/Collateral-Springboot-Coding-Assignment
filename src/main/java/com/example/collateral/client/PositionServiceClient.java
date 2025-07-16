// File: src/main/java/com/example/collateral/client/PositionServiceClient.java
package com.example.collateral.client;

import com.example.collateral.model.AccountPosition;
import com.example.collateral.model.Position;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.List;

/**
 * PositionServiceClient
 * ---------------------
 * Purpose:
 * - Fetches position data for requested account IDs, returning the quantity
 *   of each asset held in those accounts.
 * - Positions represent holdings that may be used as collateral.
 *
 * Technical Rationale:
 * - Annotated as @Component for Spring to manage and inject this client.
 * - Synchronous signature (List<AccountPosition> getPositions) simplifies orchestration
 *   in service layer; acceptable if position-service latency is low.
 * - In production, this would likely use RestTemplate or WebClient to call an external API.
 *   Consider retry/backoff (e.g., Spring Retry) and caching for performance and resilience.
 *
 * Assumptions:
 * 1. Input accountIds list is non-null, contains unique, valid account identifiers.
 * 2. Positions service returns a complete list of positions for each account in one call.
 * 3. Quantity values are non-negative integers; zero quantities indicate no holding.
 * 4. The mock data here aims to cover key scenarios:
 *    - Account "E1": mix of eligible (S1,S3) and ineligible (S4) assets.
 *    - Account "E2": includes asset with missing price (S5) to test skip logic,
 *      and eligible (S1,S2) assets to test discount application.
 *
 * Domain Knowledge:
 * - Accounts hold positions in various securities/assets identified by assetId (e.g., S1).
 * - Collateral calculations require inventory of holdings (position quantity).
 * - Holdings with zero quantity contribute nothing but still validate asset coverage.
 */
@Component
public class PositionServiceClient {
    
	/**
     * getPositions
     * -------------
     * Fetches positions (asset holdings) for each account in accountIds.
     *
     * @param accountIds List of account identifiers (e.g., ["E1","E2"]).
     *                   Assumes these IDs are recognized by the positions service.
     * @return List of AccountPosition, pairing accountId with its list of Positions.
     *
     * Technical Notes:
     * - Uses hardcoded mock data matching assignment samples and edge cases.
     * - In real implementation, would perform:
     *     restTemplate.postForObject(POSITIONS_URL, accountIds, AccountPosition[].class)
     *   and handle exceptions/timeouts.
     */
	public List<AccountPosition> getPositions(List<String> accountIds) {
        // Mock response: two accounts
        // Account E1: demonstrates eligible and ineligible positions (S1, S3 eligible; S4 ineligible)
        AccountPosition e1 = new AccountPosition(
            "E1",
            Arrays.asList(
                new Position("S1", 100),  // Eligible & priced
                new Position("S3", 100),  // Eligible & priced
                new Position("S4", 100)   // Ineligible -> collateral=0
            )
        );
        // Account E2: includes missing-price asset (S5) and eligible assets (S1,S2)
        AccountPosition e2 = new AccountPosition(
            "E2",
            Arrays.asList(
                new Position("S1", 200),  // Eligible & priced
                new Position("S2", 150),  // Eligible & priced
                new Position("S5", 50)    // Eligible? depends on eligibility mock; price missing -> skipped
            )
        );
        return Arrays.asList(e1, e2);
    }
}
