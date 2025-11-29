# Faker API Automation Framework

A Java-based API automation framework for testing [FakerAPI.it](https://fakerapi.it/) REST APIs using REST Assured, TestNG, and JSON Schema validation.

## Overview

API automation framework for FakerAPI.it mock data generation APIs.

**Supported APIs:**
- Addresses API (with `_country_code` parameter)
- Books API
- Products API (`_price_min`, `_price_max`, `_taxes`, `_categories_type`)
- Images API (`_type`, `_width`, `_height` with dimension validation)

## Demo Video

[View Demo Video](https://drive.google.com/file/d/1tNeZpMRsm_NgNWrQj26Th2tcDxD4iZNh/view?usp=drive_link) - Smoke test execution (13 tests in ~30-60 seconds)



## Features

- Reusable base test class with common test methods
- API-specific test classes with custom parameters
- JSON Schema validation
- Data-driven testing with TestNG
- Parallel test execution (3 threads)
- Image dimension validation using ImageIO
- Comprehensive coverage: defaults, boundaries, invalid inputs, locales, seeds
- Rate limiting handling
- SLF4J logging
- Maven build system

## Project Structure

```
SB_API_Automation/
├── src/
│   ├── main/java/com/sb/automation/
│   │   ├── config/APIConfig.java
│   │   ├── models/APIResponse.java
│   │   └── utils/
│   │       ├── APIClient.java
│   │       ├── SchemaValidator.java
│   │       └── TestDataReader.java
│   └── test/
│       ├── java/com/sb/automation/tests/
│       │   ├── BaseAPITest.java
│       │   ├── AddressesAPITest.java (13 tests)
│       │   ├── BooksAPITest.java (15 tests)
│       │   ├── ProductsAPITest.java (25 tests)
│       │   └── ImagesAPITest.java (22 tests)
│       └── resources/
│           ├── testdata/ (JSON test data files)
│           └── schemas/ (JSON schema files)
├── pom.xml
├── testng.xml (default configuration)
├── testng-fast.xml (fast execution)
└── testng-smoke.xml (smoke tests only)
```

## Prerequisites

- Java JDK 11+
- Apache Maven 3.6+
- Internet connection

```bash
java -version
mvn -version
```

## Installation

### Windows
1. Install JDK 11+ and set `JAVA_HOME`
2. Install Maven and set `MAVEN_HOME`
3. Add both to PATH

### macOS
```bash
brew install openjdk@11 maven
```

### Linux
```bash
sudo apt install openjdk-11-jdk maven
```

## Setup

```bash
cd d:\Javin\Job\SB\SB_API_Automation\SB_API_Automation
mvn clean install -DskipTests
```

## Running Tests

**All tests:**
```bash
mvn clean test
```

**Smoke tests:**
```bash
mvn clean test "-DsuiteXmlFile=testng-smoke.xml"
```

**Fast suite:**
```bash
mvn clean test "-DsuiteXmlFile=testng-fast.xml"
```

**Specific test class:**
```bash
mvn test -Dtest=ImagesAPITest
```

**Specific test method:**
```bash
mvn test -Dtest=ImagesAPITest#testDefaultImagesRequest
```

**Custom thread count:**
```bash
mvn clean test "-DthreadCount=5"
```

**View reports:** `target/surefire-reports/index.html`

## Test Coverage

| API | Tests | Special Features |
|-----|-------|-----------------|
| Addresses | 13 | Country code validation |
| Books | 15 | ISBN format validation |
| Products | 25 | Price/tax/category params |
| Images | 22 | Image dimension validation |

**Total: 75 tests** covering defaults, locales, quantities, boundaries, seeds, and schema validation.

## Framework Architecture

**Core Components:**
1. **BaseAPITest** - Reusable test methods
2. **APIClient** - REST client with builder pattern
3. **TestDataReader** - JSON test data management
4. **SchemaValidator** - JSON schema validation
5. **APIConfig** - Centralized configuration

## Test Data Files

`src/test/resources/testdata/` contains:
- `locales.json` - Locale variations
- `quantities.json` - Valid/invalid quantities
- `seeds.json` - Consistency test seeds
- `country_codes.json` - Address country codes
- `product_params.json` - Product parameters
- `image_params.json` - Image dimensions

## Parallel Execution

Configured in `testng.xml` with `parallel="methods"` and `thread-count="3"`.

**Performance:** Sequential (~8-10 min) vs Parallel (~2-3 min) = 3-5x faster

Customize: `mvn clean test "-DthreadCount=5"`

## Extending the Framework

1. Add endpoint to `APIConfig.java`
2. Create schema file: `src/test/resources/schemas/newapi-schema.json`
3. Create test class extending `BaseAPITest`
4. Add to `testng.xml`

## Dependencies

| Library | Version | Purpose |
|---------|---------|---------|
| REST Assured | 5.3.2 | API testing |
| TestNG | 7.8.0 | Test framework |
| Jackson | 2.15.3 | JSON processing |
| JSON Schema Validator | 5.3.2 | Schema validation |
| SLF4J | 2.0.9 | Logging |

## Troubleshooting

**Build failures:**
```bash
mvn clean install -DskipTests -U
```

**Rate limiting (429 errors):**
```bash
mvn clean test "-DthreadCount=2"
```

**Network issues:**
- Check internet connection
- Verify https://fakerapi.it is accessible

**Schema validation errors:**
- Verify schema files exist in `src/test/resources/schemas/`
- Check schema matches API response

## API Reference

**Base URL:** `https://fakerapi.it/api/v2`

**Common Parameters:**
- `_locale` - Language locale (default: en_US)
- `_quantity` - Number of records 1-1000 (default: 10)
- `_seed` - Integer seed for consistency (default: null)

**Endpoints:**

1. **Addresses:** `/addresses`
   - Additional: `_country_code` (e.g., US, GB, FR)

2. **Books:** `/books`
   - No additional parameters

3. **Products:** `/products`
   - `_price_min` - Minimum price (default: 0.01)
   - `_price_max` - Maximum price (default: none)
   - `_taxes` - Tax percentage (default: 22)
   - `_categories_type` - integer|string|uuid (default: integer)

4. **Images:** `/images`
   - `_type` - any|pokemon (default: any)
   - `_width` - Width in pixels (default: 640)
   - `_height` - Height in pixels (default: 480)

## Resources

- [FakerAPI Documentation](https://fakerapi.it/)
- [REST Assured Documentation](https://rest-assured.io/)
- [TestNG Documentation](https://testng.org/)
- [JSON Schema Specification](https://json-schema.org/)

---

**Last Updated:** November 2025  
**Total Tests:** 75 comprehensive test cases across 4 APIs
