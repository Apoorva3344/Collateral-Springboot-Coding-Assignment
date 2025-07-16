// File: src/test/java/com/example/collateral/service/CollateralCalculationServiceTest.java
package com.example.collateral.service;

import com.example.collateral.client.EligibilityServiceClient;
import com.example.collateral.client.PositionServiceClient;
import com.example.collateral.client.PriceServiceClient;
import com.example.collateral.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

/**
 * CollateralCalculationServiceTest
 * --------------------------------
 * Purpose:
 * - Unit test the core business logic in CollateralCalculationService in isolation.
 * - Verify that collateral calculations correctly handle eligible/ineligible positions,
 *   missing prices, and rounding behavior.
 *
 * Technical Rationale:
 * - Annotated with @ExtendWith(MockitoExtension.class) to enable Mockito annotations
 *   without loading the full Spring context, improving test speed.
 * - @Mock creates Mockito mocks for dependent clients (PositionServiceClient,
 *   EligibilityServiceClient, PriceServiceClient). This isolates service logic from
 *   external I/O and ensures deterministic behavior.
 * - @InjectMocks injects the mocks into the CollateralCalculationService instance,
 *   wiring dependencies without manual setup.
 * - Uses JUnit 5 (Jupiter) for modern testing features and clear assertion methods.
 *
 * Assumptions Made:
 * 1. Mock clients return complete lists (positions, eligibilityData, prices) in one call.
 * 2. Quantity, price, and discount values in test data reflect real-world scenarios:
 *    - Eligible positions with positive quantities and prices.
 *    - Ineligible positions with discount 0.0.
 * 3. Rounding is to two decimal places using HALF_UP; tested via delta in assertEquals.
 * 4. Duplicate and null checks are out of scope; inputs are well-formed.
 *
 * Domain Knowledge:
 * - Financial collateral calculations often involve haircuts (discount factors) to mitigate
 *   market risk; test data applies common 10% haircut (0.9 factor).
 * - Missing price scenarios should exclude assets from valuation rather than fail.
 * - Ineligible assets must contribute zero to avoid overstatement of collateral.
 */
@ExtendWith(MockitoExtension.class)
public class CollateralCalculationServiceTest {
    // Mock downstream clients to isolate service logic
    @Mock
    private PositionServiceClient positionServiceClient;

    @Mock
    private EligibilityServiceClient eligibilityServiceClient;

    @Mock
    private PriceServiceClient priceServiceClient;

    // Service under test; mocks are injected here
    @InjectMocks
    private CollateralCalculationService collateralCalculationService;

    // Shared test data
    private List<String> testAccountIds;
    private List<AccountPosition> testPositions;
    private List<EligibilityData> testEligibilityData;
    private List<AssetPrice> testPrices;

    /**
     * setUp
     * -----
     * Prepares common mock data for all tests.
     * - testAccountIds: accounts E1 and E2
     * - testPositions: mix of eligible and ineligible positions
     * - testEligibilityData: grouping eligibility rules for E1/E2 and assets S1-S5
     * - testPrices: prices matching assets S1-S4
     */
    @BeforeEach
    void setUp() {
        testAccountIds = Arrays.asList("E1", "E2");

        // Positions: cover eligible (S1,S3), ineligible (S4), and second account assets
        testPositions = Arrays.asList(
            new AccountPosition("E1", Arrays.asList(
                new Position("S1", 100),  // 100 × 50.5 × 0.9 = 4545.0
                new Position("S3", 100),  // 100 × 10.4 × 0.9 = 936.0
                new Position("S4", 100)   // ineligible → 0.0
            )),
            new AccountPosition("E2", Arrays.asList(
                new Position("S1", 200),  // 200 × 50.5 × 0.9 = 9090.0
                new Position("S2", 150)   // 150 × 20.2 × 0.9 = 2727.0
            ))
        );

        // Eligibility: S1,S2,S3 eligible with 0.9 discount; S4,S5 ineligible
        testEligibilityData = Arrays.asList(
            new EligibilityData(true, Arrays.asList("S1","S2","S3"),
                                Arrays.asList("E1","E2"), 0.9),
            new EligibilityData(false, Arrays.asList("S4","S5"),
                                Arrays.asList("E1","E2"), 0.0)
        );

        // Price data: provides prices for S1-S4; S5 price intentionally omitted in some tests
        testPrices = Arrays.asList(
            new AssetPrice("S1", 50.5),
            new AssetPrice("S2", 20.2),
            new AssetPrice("S3", 10.4),
            new AssetPrice("S4", 15.5)
        );
    }

    /**
     * testCalculateCollateralValue_Success
     * -------------------------------------
     * Validates that the service computes correct totals for valid input.
     */
    @Test
    void testCalculateCollateralValue_Success() {
        // Stub mocks to return prepared data
        when(positionServiceClient.getPositions(anyList())).thenReturn(testPositions);
        when(eligibilityServiceClient.getEligibility(anyList(), anyList()))
            .thenReturn(testEligibilityData);
        when(priceServiceClient.getPrices(anyList())).thenReturn(testPrices);

        // Invoke service
        List<CollateralResult> results = collateralCalculationService
            .calculateCollateralValue(testAccountIds);

        // Verify results exist and correct count
        assertNotNull(results);
        assertEquals(2, results.size());

        // Verify E1 result: 4545.0 + 936.0 + 0.0 = 5481.0
        CollateralResult e1 = results.stream()
            .filter(r -> "E1".equals(r.getAccountId()))
            .findFirst().orElseThrow();
        assertEquals(5481.0, e1.getCollateralValue(), 0.01);

        // Verify E2 result: 9090.0 + 2727.0 = 11817.0
        CollateralResult e2 = results.stream()
            .filter(r -> "E2".equals(r.getAccountId()))
            .findFirst().orElseThrow();
        assertEquals(11817.0, e2.getCollateralValue(), 0.01);
    }

    /**
     * testCalculateCollateralValue_WithIneligiblePositions
     * -----------------------------------------------------
     * Ensures that when all positions are marked ineligible, totals are zero.
     */
    @Test
    void testCalculateCollateralValue_WithIneligiblePositions() {
        // Mock all positions as ineligible
        List<EligibilityData> ineligible = Arrays.asList(
            new EligibilityData(false, testPrices.stream()
                                        .map(AssetPrice::getAssetId)
                                        .toList(),
                                testAccountIds, 0.0)
        );

        when(positionServiceClient.getPositions(anyList())).thenReturn(testPositions);
        when(eligibilityServiceClient.getEligibility(anyList(), anyList()))
            .thenReturn(ineligible);
        when(priceServiceClient.getPrices(anyList())).thenReturn(testPrices);

        List<CollateralResult> results = collateralCalculationService
            .calculateCollateralValue(testAccountIds);

        assertNotNull(results);
        assertEquals(2, results.size());
        // All results should be zero
        results.forEach(r -> assertEquals(0.0, r.getCollateralValue(), 0.01));
    }

    /**
     * testCalculateCollateralValue_WithMissingPrices
     * ----------------------------------------------
     * Verifies the service skips assets without price entries rather than failing.
     */
    @Test
    void testCalculateCollateralValue_WithMissingPrices() {
        // Only S1 price provided; others missing
        when(positionServiceClient.getPositions(anyList())).thenReturn(testPositions);
        when(eligibilityServiceClient.getEligibility(anyList(), anyList()))
            .thenReturn(testEligibilityData);
        // Provide limited price data to simulate missing entries
        when(priceServiceClient.getPrices(anyList()))
            .thenReturn(List.of(new AssetPrice("S1", 50.5)));

        List<CollateralResult> results = collateralCalculationService
            .calculateCollateralValue(testAccountIds);

        assertNotNull(results);
        assertEquals(2, results.size());

        // E1: only S1 counted → 100 × 50.5 × 0.9 = 4545.0
        CollateralResult e1 = results.stream()
            .filter(r -> "E1".equals(r.getAccountId()))
            .findFirst().orElseThrow();
        assertEquals(4545.0, e1.getCollateralValue(), 0.01);

        // E2: only S1 counted → 200 × 50.5 × 0.9 = 9090.0
        CollateralResult e2 = results.stream()
            .filter(r -> "E2".equals(r.getAccountId()))
            .findFirst().orElseThrow();
        assertEquals(9090.0, e2.getCollateralValue(), 0.01);
    }
}
