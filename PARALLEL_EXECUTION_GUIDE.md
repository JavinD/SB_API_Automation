# TestNG Parallel Execution Guide

## Current Configuration

The test suite is configured for **parallel execution** in `testng.xml` with the following settings:

```xml
<suite name="Faker API Test Suite" parallel="methods" thread-count="5">
```

### Key Parameters:

- **`parallel="methods"`**: Runs individual test methods in parallel
- **`thread-count="5"`**: Uses 5 concurrent threads
- **`verbose="1"`**: Provides basic logging information

## Parallel Execution Options

TestNG supports different parallel execution modes:

### 1. **`parallel="methods"`** (Current - Fastest)
- **What it does**: Runs all test methods across all classes in parallel
- **Pros**: Maximum parallelization, fastest execution
- **Cons**: Requires thread-safe tests
- **Best for**: Independent API tests (like ours)

### 2. **`parallel="classes"`** (Previous setting)
- **What it does**: Runs test classes in parallel, but methods within each class sequentially
- **Pros**: Safer if methods within a class depend on each other
- **Cons**: Slower than methods-level parallelization

### 3. **`parallel="tests"`**
- **What it does**: Runs different test tags in parallel
- **Pros**: Good for organizing different test suites
- **Cons**: Less parallelization than methods or classes

### 4. **`parallel="instances"`**
- **What it does**: Runs instances of test classes in parallel
- **Pros**: Useful with @Factory annotation
- **Cons**: Not typically needed for API tests

## Thread Count Guidelines

The `thread-count` parameter determines how many tests run simultaneously:

- **Conservative**: `thread-count="3"` - Safe for most machines
- **Moderate**: `thread-count="5"` - Good balance (current setting)
- **Aggressive**: `thread-count="10"` - For powerful machines
- **Maximum**: `thread-count="20"` - Only for very powerful machines or many tests

### Recommended Settings by Machine:

```xml
<!-- For 4-core CPU -->
<suite name="Suite" parallel="methods" thread-count="4">

<!-- For 8-core CPU -->
<suite name="Suite" parallel="methods" thread-count="8">

<!-- For 16-core CPU or cloud CI/CD -->
<suite name="Suite" parallel="methods" thread-count="10">
```

## Alternative Configuration Options

### Option A: Maximum Speed (Recommended for CI/CD)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="Faker API Test Suite" parallel="methods" thread-count="10" verbose="1">
    <test name="Faker API Tests">
        <classes>
            <class name="com.sb.automation.tests.AddressesAPITest"/>
            <class name="com.sb.automation.tests.BooksAPITest"/>
            <class name="com.sb.automation.tests.ProductsAPITest"/>
        </classes>
    </test>
</suite>
```

### Option B: Safe Parallel (Classes level)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="Faker API Test Suite" parallel="classes" thread-count="3" verbose="1">
    <test name="Faker API Tests">
        <classes>
            <class name="com.sb.automation.tests.AddressesAPITest"/>
            <class name="com.sb.automation.tests.BooksAPITest"/>
            <class name="com.sb.automation.tests.ProductsAPITest"/>
        </classes>
    </test>
</suite>
```

### Option C: Separate Test Groups

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="Faker API Test Suite" parallel="tests" thread-count="3" verbose="1">
    <test name="Addresses API Tests" parallel="methods" thread-count="5">
        <classes>
            <class name="com.sb.automation.tests.AddressesAPITest"/>
        </classes>
    </test>
    <test name="Books API Tests" parallel="methods" thread-count="5">
        <classes>
            <class name="com.sb.automation.tests.BooksAPITest"/>
        </classes>
    </test>
    <test name="Products API Tests" parallel="methods" thread-count="5">
        <classes>
            <class name="com.sb.automation.tests.ProductsAPITest"/>
        </classes>
    </test>
</suite>
```

## Additional Performance Tips

### 1. **Disable Unnecessary Logging**
Add to your test classes:
```java
@BeforeClass
public void setup() {
    // Reduce log level for faster execution
    ((ch.qos.logback.classic.Logger) LoggerFactory
        .getLogger(Logger.ROOT_LOGGER_NAME))
        .setLevel(Level.WARN);
}
```

### 2. **Use Data Provider Parallelization**
Add to data provider methods:
```java
@DataProvider(name = "getLocales", parallel = true)
public Object[][] getLocales() {
    return sampleLocales.stream()
        .map(locale -> new Object[]{locale})
        .toArray(Object[][]::new);
}
```

### 3. **Reduce Test Data Size**
For faster smoke tests, create a separate testng-smoke.xml:
```xml
<suite name="Smoke Tests" parallel="methods" thread-count="10">
    <test name="Quick API Tests">
        <classes>
            <class name="com.sb.automation.tests.AddressesAPITest">
                <methods>
                    <include name="testDefaultAddressesRequest"/>
                    <include name="testAddressesSchemaValidation"/>
                </methods>
            </class>
            <class name="com.sb.automation.tests.BooksAPITest">
                <methods>
                    <include name="testDefaultBooksRequest"/>
                    <include name="testBooksSchemaValidation"/>
                </methods>
            </class>
            <class name="com.sb.automation.tests.ProductsAPITest">
                <methods>
                    <include name="testDefaultProductsRequest"/>
                    <include name="testProductsSchemaValidation"/>
                </methods>
            </class>
        </classes>
    </test>
</suite>
```

### 4. **Maven Parallel Execution**
Add to `pom.xml` under `maven-surefire-plugin` configuration:
```xml
<configuration>
    <parallel>methods</parallel>
    <threadCount>5</threadCount>
    <perCoreThreadCount>true</perCoreThreadCount>
</configuration>
```

## Running Tests

### With Default Configuration:
```bash
mvn clean test
```

### With Custom Thread Count:
```bash
mvn clean test -DthreadCount=10
```

### With Specific Suite:
```bash
mvn clean test -DsuiteXmlFile=testng-smoke.xml
```

### Generate Reports:
```bash
mvn clean test
# Reports will be in: target/surefire-reports/
```

## Expected Performance Improvements

With current configuration (`parallel="methods"`, `thread-count="5"`):

- **Before (Sequential)**: ~5-10 minutes for all tests
- **After (Parallel)**: ~1-2 minutes for all tests
- **Improvement**: **5-8x faster** ⚡

### Actual timing will depend on:
- Number of test methods
- API response times
- Machine CPU cores
- Network latency

## Thread Safety Considerations

Our tests are thread-safe because:
✅ Each test uses the APIClient builder pattern (creates new instances)
✅ No shared mutable state between tests
✅ TestNG @BeforeClass runs once per class (not shared across threads)
✅ Test data is read-only

## Monitoring Parallel Execution

Add this to see parallel execution in action:
```java
@BeforeMethod
public void logThreadInfo() {
    logger.info("Test running on thread: {}", Thread.currentThread().getName());
}
```

## Troubleshooting

### Issue: Tests fail when run in parallel but pass sequentially
**Solution**: Check for shared state or dependencies between tests

### Issue: Out of memory errors
**Solution**: Reduce thread count or increase JVM heap:
```bash
export MAVEN_OPTS="-Xmx2048m"
mvn clean test
```

### Issue: Inconsistent test results
**Solution**: Ensure tests are truly independent and don't rely on execution order

