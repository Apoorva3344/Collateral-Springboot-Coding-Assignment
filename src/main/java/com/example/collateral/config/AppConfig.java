// File: src/main/java/com/example/collateral/config/AppConfig.java
package com.example.collateral.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * AppConfig
 * ---------
 * Purpose:
 * - Centralizes web MVC configuration for the Collateral Calculation Service.
 * - Specifically enables CORS (Cross-Origin Resource Sharing) so browser-based clients
 *   hosted on different origins can interact with the API under /api/**.
 *
 * Technical Rationale:
 * - Implements WebMvcConfigurer to customize Spring MVC settings without losing default behavior.
 * - Chosen over Filter-based CORS configuration for simplicity and finer-grained control
 *   directly on MVC handler mappings.
 * - Method-level CORS via @CrossOrigin annotations could work, but global mapping
 *   ensures consistency across all controllers under /api/**.
 *
 * Assumptions Made:
 * 1. Front-end development servers run on http://localhost:3000 (e.g., React) or
 *    http://localhost:8080 (e.g., Angular/Vue dev server).
 * 2. In production, origins will differ; this mock config should be externalized
 *    in application.properties or an environment-specific @Profile to avoid hardcoding.
 * 3. All API endpoints under /api/ are safe to expose to listed origins; sensitive
 *    endpoints requiring stricter control should be secured separately (e.g., via Spring Security).
 * 4. Wildcard headers (*) and allowCredentials=true assume no use of wildcard origin in production,
 *    as CORS spec prohibits credentials with wildcard origins.
 *
 * Domain Knowledge:
 * - Single-Page Applications (SPAs) typically run on different hosts/ports during local development,
 *   necessitating CORS to allow Ajax/fetch/XHR calls to backend microservices.
 * - Collateral service may require auth tokens or cookies; allowCredentials(true) permits such
 *   credentials to be sent if front-end sets withCredentials.
 */
@Configuration
public class AppConfig implements WebMvcConfigurer {
    /**
     * addCorsMappings
     * ---------------
     * Registers global CORS configuration for API endpoints.
     *
     * @param registry the CorsRegistry to configure CORS mappings
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
            .addMapping("/api/**")                     // Apply CORS to all /api/* endpoints
            .allowedOrigins(
                "http://localhost:3000",              // React dev server
                "http://localhost:8080"               // Alternative dev server
            )
            .allowedMethods(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"
            )                                        // HTTP methods allowed cross-origin
            .allowedHeaders("*")                    // Allow all headers (e.g., Authorization, Content-Type)
            .allowCredentials(true);                  // Allow cookies/auth tokens in CORS requests
    }
}
