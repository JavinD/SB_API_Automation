# Quick Start Guide

## First Time Setup

1. **Install Maven dependencies**:
```bash
mvn clean install -DskipTests
```

2. **Run a quick test**:
```bash
mvn test -Dtest=BooksAPITest#testDefaultBooksRequest
```

## Running Tests

### Run all tests:
```bash
mvn clean test
```

### Run specific API tests:
```bash
# Addresses API only
mvn test -Dtest=AddressesAPITest

# Books API only
mvn test -Dtest=BooksAPITest
```

### View Test Reports:
After running tests, open:
```
target/surefire-reports/index.html
```

## Project Structure Overview

- `src/main/java/` - Framework code (config, utils, models)
- `src/test/java/` - Test classes
- `src/test/resources/testdata/` - Test data JSON files
- `src/test/resources/schemas/` - JSON schema files for validation

## Key Features

✅ Reusable base test class  
✅ Data-driven testing with JSON files  
✅ JSON schema validation  
✅ Comprehensive test coverage  
✅ Detailed logging

For full documentation, see [README.md](../README.md)

