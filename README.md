# Faker API Automation Framework

A comprehensive Java-based API automation framework for testing the [FakerAPI.it](https://fakerapi.it/) REST APIs using REST Assured, TestNG, and JSON Schema validation.

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Setup and Installation](#setup-and-installation)
- [Running Tests](#running-tests)
- [Test Coverage](#test-coverage)
- [Framework Architecture](#framework-architecture)
- [Test Data](#test-data)
- [Extending the Framework](#extending-the-framework)

## 🎯 Overview

This framework provides automated API testing for FakerAPI.it, which is a collection of free APIs that help developers generate mock data. The framework is designed with reusability, maintainability, and scalability in mind.

Currently supports testing for:
- **Addresses API** (with custom `_country_code` parameter)
- **Books API**

## ✨ Features

- **Reusable Base Test Class**: Common test methods for all APIs with standard parameters (`_locale`, `_quantity`, `_seed`)
- **Customized Test Classes**: API-specific test classes with custom parameter support
- **JSON Schema Validation**: Automated response structure validation
- **Data-Driven Testing**: TestNG data providers with external test data files
- **Comprehensive Test Coverage**: 
  - Default requests
  - Boundary value testing
  - Invalid input testing
  - Locale variations
  - Seed consistency testing
  - Response structure validation
- **Detailed Logging**: SLF4J logging for test execution tracking
- **Maven Build System**: Easy dependency management and test execution

## 📁 Project Structure

```
faker-api-automation/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/sb/automation/
│   │           ├── config/
│   │           │   └── APIConfig.java           # API configuration and constants
│   │           └── utils/
│   │               ├── APIClient.java           # REST API client with RequestBuilder
│   │               ├── SchemaValidator.java     # JSON schema validation utilities
│   │               └── TestDataReader.java      # Test data file reader
│   └── test/
│       ├── java/
│       │   └── com/sb/automation/tests/
│       │       ├── BaseAPITest.java             # Base test class with reusable methods
│       │       ├── AddressesAPITest.java        # Addresses API test class
│       │       └── BooksAPITest.java            # Books API test class
│       └── resources/
│           ├── testdata/
│           │   ├── locales.json                 # All available locales
│           │   ├── quantities.json              # Valid and invalid quantities
│           │   ├── seeds.json                   # Test seed values
│           │   └── country_codes.json           # Country codes for addresses
│           └── schemas/
│               ├── addresses-schema.json        # JSON schema for addresses response
│               └── books-schema.json            # JSON schema for books response
├── pom.xml                                      # Maven dependencies
├── testng.xml                                   # TestNG suite configuration
├── .gitignore
└── README.md
```

## 🔧 Prerequisites

- **Java**: JDK 11 or higher
- **Maven**: 3.6 or higher
- **Internet Connection**: Required to access FakerAPI.it

## 🚀 Setup and Installation

1. **Clone or navigate to the project directory**:
   ```bash
   cd d:\Javin\Job\SB\SB_API_Automation
   ```

2. **Install dependencies**:
   ```bash
   mvn clean install -DskipTests
   ```

3. **Verify installation**:
   ```bash
   mvn clean compile
   ```

## 🧪 Running Tests

### Run All Tests
```bash
mvn clean test
```

### Run Specific Test Class
```bash
# Run Addresses API tests only
mvn test -Dtest=AddressesAPITest

# Run Books API tests only
mvn test -Dtest=BooksAPITest
```

### Run Specific Test Method
```bash
mvn test -Dtest=AddressesAPITest#testDefaultAddressesRequest
```

### Run Tests with TestNG XML
```bash
mvn clean test -DsuiteXmlFile=testng.xml
```

### Generate Test Reports
TestNG generates HTML reports automatically in:
```
target/surefire-reports/
```

## 📊 Test Coverage

### Common Parameter Tests (All APIs)
- ✅ Default request (no parameters)
- ✅ Different locales (74 locales available)
- ✅ Valid quantities (1, 5, 10, 50, 100, 500, 1000)
- ✅ Boundary quantities (1 and 1000)
- ✅ Invalid quantities (0, -1, 1001, 5000)
- ✅ Seed consistency (same seed = same results)
- ✅ All parameters combined
- ✅ JSON schema validation

### Addresses API Specific Tests
- ✅ Valid country codes (US, GB, FR, DE, etc.)
- ✅ Invalid country codes
- ✅ Country code with locale combination
- ✅ All parameters including country code
- ✅ Data structure validation (10 fields)

### Books API Specific Tests
- ✅ Multiple seeds comparison
- ✅ ISBN format validation
- ✅ Response time validation
- ✅ Locale impact on content
- ✅ Total field accuracy
- ✅ Data structure validation (9 fields)

## 🏗️ Framework Architecture

### 1. Base Test Class (`BaseAPITest`)
Provides reusable test methods that all API test classes inherit:
- `testDefaultRequest()`: Test API with default parameters
- `testWithLocale()`: Test with specific locale
- `testWithQuantity()`: Test with specific quantity
- `testWithSeed()`: Test seed consistency
- `testWithAllCommonParams()`: Test all parameters together
- `validateSuccessResponse()`: Validate 200 OK response
- `validateResponseQuantity()`: Validate response data count
- `validateResponseSchema()`: Validate against JSON schema

### 2. API Client (`APIClient`)
- RESTful API client using REST Assured
- Builder pattern for constructing requests
- Support for common parameters (`_locale`, `_quantity`, `_seed`)
- Custom parameter support via `withParam()`

### 3. Test Data Management
- JSON files for test data
- `TestDataReader` utility for reading test data
- Centralized test data management

### 4. Schema Validation
- JSON Schema validation using `json-schema-validator`
- Separate schema files for each API endpoint
- Validates response structure and data types

## 📝 Test Data

### Locales (`locales.json`)
- **All Locales**: 74 supported locales
- **Sample Locales**: 6 commonly used locales for quick testing
- **Invalid Locales**: Test data for negative scenarios

### Quantities (`quantities.json`)
- **Valid Quantities**: 1, 5, 10, 50, 100, 500, 1000
- **Boundary Quantities**: 1 (min), 1000 (max)
- **Invalid Quantities**: 0, -1, 1001, 5000
- **Edge Cases**: 999, 1000, 1001

### Seeds (`seeds.json`)
- **Valid Seeds**: Various integer seeds for consistency testing

### Country Codes (`country_codes.json`)
- **Valid Codes**: US, GB, FR, DE, IT, ES, JP, CN, IN, BR, CA, AU, MX, RU, ZA
- **Invalid Codes**: XX, ZZ, "", "123", "USA", "invalid"

## 🔄 Extending the Framework

### Adding a New API Endpoint

1. **Add endpoint constant to `APIConfig.java`**:
```java
public static final String NEW_ENDPOINT = "/newapi";
```

2. **Create JSON schema** in `src/test/resources/schemas/newapi-schema.json`

3. **Create test data files** (if API has specific parameters)

4. **Create test class extending `BaseAPITest`**:
```java
public class NewAPITest extends BaseAPITest {
    @Override
    protected String getEndpoint() {
        return APIConfig.NEW_ENDPOINT;
    }
    
    @Override
    protected String getSchemaPath() {
        return "src/test/resources/schemas/newapi-schema.json";
    }
    
    // Add API-specific tests
}
```

5. **Add to `testng.xml`**:
```xml
<class name="com.sb.automation.tests.NewAPITest"/>
```

### Adding Custom Parameters

Use the `RequestBuilder.withParam()` method:
```java
Response response = new APIClient.RequestBuilder(endpoint)
    .withLocale("en_US")
    .withQuantity(10)
    .withParam("_custom_param", "value")
    .execute();
```

## 📚 Dependencies

- **REST Assured**: 5.3.2 - REST API testing
- **TestNG**: 7.8.0 - Test framework
- **Jackson**: 2.15.3 - JSON processing
- **JSON Schema Validator**: 5.3.2 - Schema validation
- **SLF4J**: 2.0.9 - Logging
- **Lombok**: 1.18.30 - Code generation (optional)

## 🎓 Best Practices

1. **Reusability**: Common test logic in `BaseAPITest`
2. **Data-Driven**: TestNG data providers with external data files
3. **Maintainability**: Centralized configuration in `APIConfig`
4. **Validation**: Multi-level validation (status code, structure, schema)
5. **Logging**: Comprehensive logging for debugging
6. **Separation of Concerns**: Utils, config, tests in separate packages

## 📧 API Reference

Base URL: `https://fakerapi.it/api/v2`

### Common Parameters
- `_locale`: Language/locale (e.g., en_US, fr_FR) - Default: en_US
- `_quantity`: Number of records (1-1000) - Default: 10
- `_seed`: Integer seed for consistent results - Default: null

### Addresses Endpoint
```
GET /addresses
Additional Parameter: _country_code (e.g., US, GB, FR)
```

### Books Endpoint
```
GET /books
No additional parameters
```

## 🐛 Troubleshooting

1. **Build Failures**: Run `mvn clean install -DskipTests`
2. **Test Failures**: Check network connection to fakerapi.it
3. **Schema Validation Errors**: Verify schema files exist and are valid JSON
4. **Data Provider Errors**: Ensure test data JSON files are properly formatted

## 📄 License

This project is created for educational and testing purposes.

## 🔗 Resources

- [FakerAPI Documentation](https://fakerapi.it/)
- [REST Assured Documentation](https://rest-assured.io/)
- [TestNG Documentation](https://testng.org/)
- [JSON Schema Specification](https://json-schema.org/)

---

**Created by**: SB API Automation Team  
**Last Updated**: November 2025

