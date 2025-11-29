# Quick Test Execution Guide

## ⚡ Run Tests in Parallel (Default - Fast)

```bash
mvn clean test
```

**Configuration**: Uses `testng.xml` with 5 parallel threads
**Execution Time**: ~1-2 minutes
**Runs**: All tests (Addresses, Books, Products APIs)

---

## 🚀 Run Tests with Maximum Speed

```bash
mvn clean test -DsuiteXmlFile=testng-fast.xml
```

**Configuration**: 10 parallel threads
**Execution Time**: ~30-60 seconds
**Runs**: All tests with maximum parallelization

---

## 💨 Run Smoke Tests Only (Fastest)

```bash
mvn clean test -DsuiteXmlFile=testng-smoke.xml
```

**Configuration**: 10 parallel threads, essential tests only
**Execution Time**: ~10-20 seconds
**Runs**: Only critical smoke tests (default requests, schema validation, boundary tests)

---

## 🎛️ Custom Thread Count

```bash
mvn clean test -DthreadCount=8
```

**Note**: Adjust thread count based on your machine's CPU cores

---

## 📊 Run with HTML Reports

```bash
mvn clean test
mvn surefire-report:report
```

**Output**: `target/surefire-reports/index.html`

---

## 🎯 Run Specific Test Class

```bash
mvn test -Dtest=ProductsAPITest
```

**Runs**: Only ProductsAPITest class

---

## 🔍 Run Specific Test Method

```bash
mvn test -Dtest=ProductsAPITest#testDefaultProductsRequest
```

**Runs**: Single test method

---

## 📝 Run with Verbose Output

```bash
mvn clean test -Dverbose=2
```

**Shows**: Detailed test execution logs

---

## Performance Comparison

| Configuration | Threads | Tests | Est. Time | Use Case |
|--------------|---------|-------|-----------|----------|
| Sequential (old) | 1 | All | ~8-10 min | Debugging |
| Default | 5 | All | ~1-2 min | Regular testing |
| Fast | 10 | All | ~30-60 sec | CI/CD |
| Smoke | 10 | Essential | ~10-20 sec | Quick validation |

---

## Tips for Faster Execution

1. **Increase thread count** for powerful machines:
   ```bash
   mvn clean test -DthreadCount=10
   ```

2. **Use smoke tests** during development:
   ```bash
   mvn clean test -DsuiteXmlFile=testng-smoke.xml
   ```

3. **Skip compilation** if code hasn't changed:
   ```bash
   mvn test -DskipTests=false
   ```

4. **Run tests offline** to avoid Maven updates:
   ```bash
   mvn -o clean test
   ```

---

## Available Test Suites

| File | Description | Thread Count | Tests |
|------|-------------|--------------|-------|
| `testng.xml` | Default configuration | 5 | All tests |
| `testng-fast.xml` | Maximum speed | 10 | All tests |
| `testng-smoke.xml` | Quick validation | 10 | Essential tests only |

---

## Troubleshooting

### Tests are slow
- Increase thread count: `-DthreadCount=10`
- Use faster suite: `-DsuiteXmlFile=testng-fast.xml`

### Out of memory errors
- Reduce thread count: `-DthreadCount=3`
- Increase heap size: `export MAVEN_OPTS="-Xmx2048m"`

### Tests fail in parallel but pass sequentially
- Check test independence
- Look for shared state issues
- Review logs for race conditions

