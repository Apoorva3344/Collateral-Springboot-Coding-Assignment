// File: src/main/java/com/example/collateral/demo/CollateralDemo.java
package com.example.collateral.demo;

import com.example.collateral.model.CollateralResult;
import com.example.collateral.service.CollateralCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.List;

/**
 * CollateralDemo
 * --------------
 * Purpose:
 * - Provides a simple console-based demonstration of the collateral calculation
 *   logic when the application is run in 'demo' mode.
 * - Uses CommandLineRunner to execute code after Spring Boot startup.
 *
 * Technical Rationale:
 * - Implements CommandLineRunner to hook into the Spring Boot lifecycle at startup.
 *   Alternative approaches (e.g., @EventListener(ApplicationReadyEvent.class)) could be
 *   used, but CommandLineRunner offers straightforward argument inspection.
 * - Marked as @Component so Spring auto-detects and executes it when the demo flag is present.
 * - Conditional execution based on program arguments (--demo) to avoid running in production.
 *
 * Assumptions Made:
 * 1. Demo mode is triggered by passing "--demo" as a command-line argument.
 *    In production, the demo runner will immediately return without side effects.
 * 2. Account IDs used in the demo ("E1","E2") are valid and correspond to mocked
 *    data in PositionServiceClient, EligibilityServiceClient, and PriceServiceClient.
 * 3. Console output using System.out/err is acceptable for demo; real applications
 *    might use a proper logging framework (e.g., SLF4J) or UI layer.
 * 4. Currency formatting in System.out.printf uses US locale conventions ("$" prefix,
 *    two decimal places). Adapt for internationalization if needed.
 *
 * Domain Knowledge:
 * - Demonstrates how collateral values are derived step-by-step for financial reviews
 *   or stakeholder presentations.
 * - Breaks down each position: quantity × price × discount, aligning with risk management
 *   practices (haircut application).
 * - Helps validate that the service correctly handles eligible, ineligible, and missing-price
 *   scenarios before integrating into larger systems.
 */
@Component
public class CollateralDemo implements CommandLineRunner {

    @Autowired
    private CollateralCalculationService collateralCalculationService;

    /**
     * Executes the demo logic after application startup when '--demo' arg is provided.
     *
     * @param args Program arguments passed to Spring Boot
     * @throws Exception Propagates any errors encountered during demo execution
     */
    @Override
    public void run(String... args) throws Exception {
        // Only execute demo when '--demo' flag is passed; prevents accidental runs
        if (!Arrays.asList(args).contains("--demo")) {
            return; // Exit immediately if not in demo mode
        }

        // Demo header for clarity
        System.out.println("=== Collateral Calculation Service Demo ===\n");

        // Demo input: two account IDs chosen to illustrate various cases
        List<String> accountIds = Arrays.asList("E1", "E2");
        System.out.println("Input Account IDs: " + accountIds);
        System.out.println("\nCalculating collateral values...\n");

        // Invoke core service to compute collateral values
        List<CollateralResult> results = collateralCalculationService.calculateCollateralValue(accountIds);

        // Display results in tabular console format
        System.out.println("=== COLLATERAL CALCULATION RESULTS ===");
        System.out.println("┌─────────────┬──────────────────┐");
        System.out.println("│ Account ID  │ Collateral Value │");
        System.out.println("├─────────────┼──────────────────┤");
        for (CollateralResult result : results) {
            // %-11s ensures left padding for accountId, %15.2f formats value to two decimals
            System.out.printf("│ %-11s │ %15.2f │%n",
                              result.getAccountId(),
                              result.getCollateralValue());
        }
        System.out.println("└─────────────┴──────────────────┘");

        // Detailed calculation breakdown for transparency
        System.out.println("\n=== CALCULATION BREAKDOWN ===");
        System.out.println("Based on the mock data:");
        System.out.println();

        // Hardcoded breakdown to match mock inputs and service logic
        System.out.println("Account E1 Positions:");
        System.out.println("  - S1: 100 units × $50.50 × 0.90 (discount) = $4,545.00 (eligible)");
        System.out.println("  - S3: 100 units × $10.40 × 0.90 (discount) = $936.00 (eligible)");
        System.out.println("  - S4: 100 units × $15.50 × 0.00 (ineligible) = $0.00 (ineligible)");
        System.out.println("  Total E1: $5,481.00");
        System.out.println();

        System.out.println("Account E2 Positions:");
        System.out.println("  - S1: 200 units × $50.50 × 0.90 (discount) = $9,090.00 (eligible)");
        System.out.println("  - S2: 150 units × $20.20 × 0.90 (discount) = $2,727.00 (eligible)");
        System.out.println("  Total E2: $11,817.00");
        System.out.println();

        System.out.println("=== Demo completed successfully! ===");
    }
}
