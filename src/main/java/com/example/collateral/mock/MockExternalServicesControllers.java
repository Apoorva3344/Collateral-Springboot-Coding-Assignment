// File: src/main/java/com/example/collateral/mock/MockExternalServicesControllers.java
package com.example.collateral.mock;

import com.example.collateral.model.AccountPosition;
import com.example.collateral.model.EligibilityData;
import com.example.collateral.model.AssetPrice;
import com.example.collateral.model.Position;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
import java.util.List;

/**
 * Mock Controllers for external services: Positions, Eligibility, Price.
 * These endpoints can be called via Postman or HTTP clients to retrieve sample data.
 */
@RestController
@RequestMapping("/api/mock")
public class MockExternalServicesControllers {

    /**
     * Mock Positions Service
     * URL: POST /api/mock/positions
     * Body: JSON array of account IDs (e.g., ["E1","E2"])
     * Returns: List of AccountPosition sample data.
     */
    @PostMapping(path = "/positions", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AccountPosition> getMockPositions(@RequestBody List<String> accountIds) {
        AccountPosition e1 = new AccountPosition(
            "E1",
            Arrays.asList(
                new Position("S1", 100),
                new Position("S3", 100),
                new Position("S4", 100)
            )
        );
        AccountPosition e2 = new AccountPosition(
            "E2",
            Arrays.asList(
                new Position("S1", 200),
                new Position("S2", 150),
                new Position("S5", 50)
            )
        );
        return Arrays.asList(e1, e2);
    }

    /**
     * Mock Eligibility Service
     * URL: POST /api/mock/eligibility
     * Body: JSON object with accountIds and assetIds lists:
     *   { "accountIds": [...], "assetIds": [...] }
     * Returns: List of EligibilityData sample data.
     */
    public static class EligibilityRequest {
        public List<String> accountIds;
        public List<String> assetIds;
        public EligibilityRequest() {}
        public EligibilityRequest(List<String> accountIds, List<String> assetIds) {
            this.accountIds = accountIds;
            this.assetIds = assetIds;
        }
    }

    @PostMapping(path = "/eligibility", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EligibilityData> getMockEligibility(@RequestBody EligibilityRequest req) {
        EligibilityData eligible = new EligibilityData(
            true,
            Arrays.asList("S1", "S2", "S3"),
            req.accountIds,
            0.9
        );
        EligibilityData ineligible = new EligibilityData(
            false,
            Arrays.asList("S4", "S5"),
            req.accountIds,
            0.0
        );
        return Arrays.asList(eligible, ineligible);
    }

    /**
     * Mock Price Service
     * URL: POST /api/mock/prices
     * Body: JSON array of asset IDs (e.g., ["S1","S2","S3","S4"])
     * Returns: List of AssetPrice sample data.
     */
    @PostMapping(path = "/prices", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AssetPrice> getMockPrices(@RequestBody List<String> assetIds) {
        AssetPrice p1 = new AssetPrice("S1", 50.5);
        AssetPrice p2 = new AssetPrice("S2", 20.2);
        AssetPrice p3 = new AssetPrice("S3", 10.4);
        AssetPrice p4 = new AssetPrice("S4", 15.5);
        // Include S5 if requested
        AssetPrice p5 = new AssetPrice("S5", 25.0);
        return Arrays.asList(p1, p2, p3, p4, p5);
    }
}
