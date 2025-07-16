# Collateral Calculation Service

**Version**: 1.0.0  
**Last Updated:** July 16, 2025

A Spring Boot application that automates the calculation of collateral values for financial accounts. It combines positions, eligibility rules, and market prices to produce precise, two-decimal collateral valuations.

---

## Features

- **REST API** to calculate collateral for one or more accounts  
- **Mock endpoints** for Positions, Eligibility & Price services  
- **High-precision arithmetic** using `BigDecimal`  
- **Clean, layered architecture** (controller → service → client → model)  
- **Automated tests** (unit + integration) with JUnit 5 & Mockito  
- **Health check** endpoint

---

## Tech Stack

- **Java 17 (LTS)**  
- **Spring Boot 3.2.0**  
- **Maven** for build & dependency management  
- **Jackson** for JSON (de)serialization  
- **JUnit 5** & **Mockito** for testing  
- **BigDecimal** for financial precision

---

## Prerequisites

- JDK 17 installed and `JAVA_HOME` configured  
- Maven 3.6+  
- (Optional) Postman or other HTTP client  

---

## Installation & Running

1. **Clone the repo**  
   ```bash
   git clone https://github.com/Apoorva3344/Collateral-Springboot-Coding-Assignment.git
   cd Collateral-Springboot-Coding-Assignment
   
Build
mvn clean package

Run
mvn spring-boot:run -Dspring-boot.run.profiles=dev
The service will start on http://localhost:8084.

## API Endpoints

Mock External Services

POST /api/mock/positions
Request body: ["E1","E2"]
Returns account holdings.

POST /api/mock/eligibility
Request body:
{
  "accountIds":["E1","E2"],
  "assetIds":["S1","S2","S3","S4","S5"]
}
Returns eligibility rules.

POST /api/mock/prices
Request body: ["S1","S2","S3","S4","S5"]
Returns asset prices.

POST /api/collateral/calculate
Request body: ["E1","E2"]
Response:
[
  { "accountId": "E1", "collateralValue": 5481.00 },
  { "accountId": "E2", "collateralValue": 11817.00 }
]

GET /api/collateral/health → "Collateral Service is running"


Testing
mvn clean test

Service tests cover normal, all-ineligible, missing-price scenarios.

Controller tests verify HTTP status codes and JSON payloads.

In Eclipse

Right-click project → Run As → Maven test

Or run individual JUnit classes under src/test/java

Troubleshooting:
If you see Byte-Buddy errors on Java versions >17, either switch to Java 17 or add
-Dnet.bytebuddy.experimental=true to your test JVM args.

Postman Testing:

Execute requests in order:

Mock /positions

Mock /eligibility

Mock /prices

/calculate

