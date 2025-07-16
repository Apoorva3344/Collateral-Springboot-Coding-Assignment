package com.example.collateral;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Collateral Calculation Service.
 * Uses @SpringBootApplication to enable:
 *  - @Configuration: defines this as a source of Bean definitions.
 *  - @EnableAutoConfiguration: auto-configures based on classpath, properties, and beans.
 *  - @ComponentScan: scans this package and sub-packages for Spring-managed components.
 *
 * Assumptions:
 * 1. Base package 'com.example.collateral' contains all sub-packages, so default component scanning covers all modules.
 * 2. Default port (8080) is acceptable; overridden via application.properties if needed.
 *
 * Domain Note:
 * Collateral Calculation Service ingests financial account positions, eligibility rules, and asset prices
 * to compute daily collateral valuations for risk management.
 */
@SpringBootApplication  
public class CollateralApplication {
    
	/**
     * Application entry point.
     * SpringApplication.run bootstraps the context, starts embedded Tomcat,
     * registers all discovered components and triggers auto-configuration.
     * Chosen for simplicity and convention over configuration in Spring Boot.
     */
    public static void main(String[] args) {
        SpringApplication.run(CollateralApplication.class, args);
    }
}