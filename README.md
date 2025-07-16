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

API Endpoints
1. Mock External Services
These three endpoints return static sample data for local testing.

Positions
POST /api/mock/positions
Body:

["E1","E2"]
Response:

[
  { "accountId":"E1", "positions":[{"assetId":"S1","quantity":100},…] },
  { "accountId":"E2", "positions":[{"assetId":"S1","quantity":200},…] }
]
Eligibility
POST /api/mock/eligibility
Body:

{
  "accountIds":["E1","E2"],
  "assetIds":["S1","S2","S3","S4","S5"]
}
Response:
[
  { "eligible":true,  "assetIDs":["S1","S2","S3"], "accountIDs":["E1","E2"], "discount":0.9 },
  { "eligible":false, "assetIDs":["S4","S5"],         "accountIDs":["E1","E2"], "discount":0.0 }
]
Prices
POST /api/mock/prices
Body:

["S1","S2","S3","S4","S5"]
Response:

[
  { "assetId":"S1","price":50.5 },
  { "assetId":"S2","price":20.2 },
  { "assetId":"S3","price":10.4 },
  { "assetId":"S4","price":15.5 },
  { "assetId":"S5","price":25.0 }
]
2. Main Collateral API
Calculate Collateral
POST /api/collateral/calculate
Body:

["E1","E2"]
Success (200 OK):

[
  { "accountId":"E1", "collateralValue":5481.00 },
  { "accountId":"E2", "collateralValue":11817.00 }
]
Error Cases:

[] or null → 400 Bad Request

Health Check
GET /api/collateral/health
Response: Collateral Service is running

Testing
Automated
Command line

mvn clean test
Service tests cover normal, all-ineligible, missing-price scenarios.

Controller tests verify HTTP status codes and JSON payloads.

In Eclipse

Right-click project → Run As → Maven test

Or run individual JUnit classes under src/test/java

Troubleshooting:
If you see Byte-Buddy errors on Java versions >17, either switch to Java 17 or add
-Dnet.bytebuddy.experimental=true to your test JVM args.

Postman Collection
Import this repo’s Collateral-Service.postman_collection.json

Set {{baseUrl}} = http://localhost:8081 in your environment

Execute requests in order:

Mock /positions

Mock /eligibility

Mock /prices

/calculate

Contributing
Fork the repo

Create a feature branch

Submit a pull request
