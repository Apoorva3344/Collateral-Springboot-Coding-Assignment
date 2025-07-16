// File: src/test/java/com/example/collateral/controller/CollateralControllerTest.java
package com.example.collateral.controller;

import com.example.collateral.model.CollateralResult;
import com.example.collateral.service.CollateralCalculationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * CollateralControllerTest
 * ------------------------
 * Purpose:
 * - Validate REST endpoints in CollateralController in isolation.
 * - Ensure correct HTTP status codes and JSON payloads are returned based on service behavior.
 *
 * Technical Rationale:
 * - Annotated with @WebMvcTest to load only web layer components (controllers, filters),
 *   excluding full Spring context for faster execution.
 * - Uses MockMvc for fluent HTTP request simulation without starting an actual server.
 * - @MockBean injects a Mockito mock of CollateralCalculationService into the Spring context,
 *   allowing control over service responses.
 * - ObjectMapper auto-wired to convert Java objects to/from JSON, ensuring test JSON matches
 *   production serialization settings.
 *
 * Assumptions Made:
 * 1. CollateralCalculationService.calculateCollateralValue returns deterministic results
 *    when given any list of account IDs (mocked by Mockito stub).
 * 2. JSON serialization for List<String> and List<CollateralResult> uses default Jackson settings.
 * 3. Error scenarios (e.g., invalid JSON, service exceptions) are handled elsewhere or generate
 *    specific exception handlers; tests focus on primary and boundary cases.
 *
 * Domain Knowledge:
 * - HTTP 200 OK for successful calculations, HTTP 400 Bad Request for invalid inputs,
 *   HTTP 500 Internal Server Error for unhandled exceptions.
 * - The health endpoint is used by orchestration tools to verify service liveness.
 */
@WebMvcTest(CollateralController.class)
public class CollateralControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CollateralCalculationService collateralCalculationService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * testCalculateCollateral_Success
     * -------------------------------
     * Verifies POST /api/collateral/calculate returns 200 and correct JSON when
     * the service layer returns valid CollateralResult objects.
     */
    @Test
    void testCalculateCollateral_Success() throws Exception {
        // Given: prepare input account IDs and expected results
        List<String> accountIds = Arrays.asList("E1", "E2");
        List<CollateralResult> expectedResults = Arrays.asList(
            new CollateralResult("E1", 5481.0),
            new CollateralResult("E2", 11817.0)
        );
        // Stub service to return expected results for any input list
        when(collateralCalculationService.calculateCollateralValue(anyList()))
            .thenReturn(expectedResults);

        // When & Then: perform POST request and assert response
        mockMvc.perform(post("/api/collateral/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountIds)))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$[0].accountId").value("E1"))
               .andExpect(jsonPath("$[0].collateralValue").value(5481.0))
               .andExpect(jsonPath("$[1].accountId").value("E2"))
               .andExpect(jsonPath("$[1].collateralValue").value(11817.0));
    }

    /**
     * testCalculateCollateral_EmptyInput
     * ----------------------------------
     * Verifies that an empty JSON array input yields HTTP 400 Bad Request
     * without invoking the calculation service.
     */
    @Test
    void testCalculateCollateral_EmptyInput() throws Exception {
        mockMvc.perform(post("/api/collateral/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[]"))
               .andExpect(status().isBadRequest());
    }

    /**
     * testCalculateCollateral_NullInput
     * ---------------------------------
     * Verifies that a null JSON payload yields HTTP 400 Bad Request.
     */
    @Test
    void testCalculateCollateral_NullInput() throws Exception {
        mockMvc.perform(post("/api/collateral/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("null"))
               .andExpect(status().isBadRequest());
    }

    /**
     * testHealthEndpoint
     * ------------------
     * Verifies GET /api/collateral/health returns 200 OK with expected message.
     */
    @Test
    void testHealthEndpoint() throws Exception {
        mockMvc.perform(get("/api/collateral/health"))
               .andExpect(status().isOk())
               .andExpect(content().string("Collateral Service is running"));
    }
}
