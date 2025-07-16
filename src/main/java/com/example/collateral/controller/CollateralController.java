// File: src/main/java/com/example/collateral/controller/CollateralController.java
package com.example.collateral.controller;

import com.example.collateral.model.CollateralResult;
import com.example.collateral.service.CollateralCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * CollateralController
 * --------------------
 * REST Controller exposing endpoints for collateral calculations.
 * Maps HTTP requests to service layer and handles response formatting.
 *
 * Technical Rationale:
 * - Annotated with @RestController to combine @Controller and @ResponseBody,
 *   enabling JSON serialization of return values automatically.
 * - Base path '/api/collateral' groups related endpoints and follows RESTful conventions.
 * - Uses ResponseEntity<T> for fine-grained HTTP status control (200, 400, 500).
 *
 * Assumptions Made:
 * 1. Input payload is JSON array of non-null, unique account IDs.
 * 2. Caller adheres to API contract; invalid or missing IDs result in Bad Request (400).
 * 3. Exceptions from downstream services bubble up as generic Exception;
 *    production code should catch specific exceptions and map to appropriate status codes.
 * 4. Logging here uses System.err for brevity; in real apps, use a logging framework
 *    (e.g., SLF4J with Logback) for log levels, correlation IDs, and structured logs.
 *
 * Domain Knowledge:
 * - Collateral valuation APIs typically require authentication/authorization (e.g., OAuth2),
 *   which is omitted for brevity. Secure endpoints with Spring Security in production.
 * - Input validation ensures that risk calculations are only triggered for valid accounts,
 *   preventing unnecessary downstream load and guarding against erroneous data.
 * - Health endpoint allows orchestration tools (e.g., Kubernetes, load balancers)
 *   to verify service readiness/health.
 */
@RestController
@RequestMapping("/api/collateral")
public class CollateralController {

    @Autowired
    private CollateralCalculationService collateralCalculationService;

    /**
     * calculateCollateral
     * -------------------
     * POST /api/collateral/calculate
     * Accepts a list of account IDs and returns their computed collateral values.
     *
     * @param accountIds JSON array of account identifiers (e.g., ["E1", "E2"]).
     * @return 200 OK with list of CollateralResult on success,
     *         400 Bad Request if input is null/empty,
     *         500 Internal Server Error on processing failure.
     *
     * Technical Notes:
     * - @RequestBody binds JSON payload to List<String>.
     * - Input validation prevents unnecessary service invocation.
     * - ResponseEntity provides flexibility for status and headers.
     */
    @PostMapping("/calculate")
    public ResponseEntity<List<CollateralResult>> calculateCollateral(
            @RequestBody List<String> accountIds) {
        try {
            // Validate that client provided at least one account ID
            if (accountIds == null || accountIds.isEmpty()) {
                // Domain: empty account list yields no collateral to calculate
                return ResponseEntity.badRequest().build();
            }

            // Delegate to business logic, which orchestrates position, eligibility, and price services
            List<CollateralResult> results = collateralCalculationService.calculateCollateralValue(accountIds);

            // Domain: even if some accounts return zero collateral, return 200 with results list
            return ResponseEntity.ok(results);

        } catch (Exception e) {
            // Technical: catch-all to prevent stack traces leaking to clients
            // In production, catch specific exceptions (e.g., HttpClientErrorException) and map accordingly
            System.err.println("Error calculating collateral: " + e.getMessage());
            e.printStackTrace();

            // Domain: collateral service failures are critical; return 500 for orchestrator retries/logs
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * health
     * ------
     * GET /api/collateral/health
     * Simple endpoint to verify service is up and running.
     *
     * @return 200 OK with status message indicating operational status.
     *
     * Technical Rationale:
     * - Essential for readiness/liveness probes in containerized deployments.
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        // Domain: health checks should be lightweight and not depend on downstream systems
        return ResponseEntity.ok("Collateral Service is running");
    }
}
