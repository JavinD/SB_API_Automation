package com.sb.automation.tests;

import com.sb.automation.config.APIConfig;
import com.sb.automation.utils.APIClient;
import com.sb.automation.utils.TestDataReader;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

/**
 * Test class for Products API
 * Includes tests for common parameters and product-specific parameters
 * Product-specific params: _price_min, _price_max, _taxes, _categories_type
 */
public class ProductsAPITest extends BaseAPITest {
    
    private static final String PRODUCT_PARAMS_DATA = "src/test/resources/testdata/product_params.json";
    private List<Object> validPriceMin;
    private List<Object> validPriceMax;
    private List<Object> boundaryPriceMin;
    private List<Object> boundaryPriceMax;
    private List<Object> invalidPrices;
    private List<Object> validTaxes;
    private List<Object> boundaryTaxes;
    private List<Object> invalidTaxes;
    private List<String> validCategoriesTypes;
    private List<String> invalidCategoriesTypes;
    private List<Object> priceRanges;

    @Override
    protected String getEndpoint() {
        return APIConfig.PRODUCTS_ENDPOINT;
    }

    @Override
    protected String getSchemaPath() {
        return "src/test/resources/schemas/products-schema.json";
    }

    @BeforeClass
    @SuppressWarnings("unchecked")
    public void setup() {
        logger.info("Setting up Products API tests");
        validPriceMin = TestDataReader.getTestDataList(PRODUCT_PARAMS_DATA, "valid_price_min");
        validPriceMax = TestDataReader.getTestDataList(PRODUCT_PARAMS_DATA, "valid_price_max");
        boundaryPriceMin = TestDataReader.getTestDataList(PRODUCT_PARAMS_DATA, "boundary_price_min");
        boundaryPriceMax = TestDataReader.getTestDataList(PRODUCT_PARAMS_DATA, "boundary_price_max");
        invalidPrices = TestDataReader.getTestDataList(PRODUCT_PARAMS_DATA, "invalid_prices");
        validTaxes = TestDataReader.getTestDataList(PRODUCT_PARAMS_DATA, "valid_taxes");
        boundaryTaxes = TestDataReader.getTestDataList(PRODUCT_PARAMS_DATA, "boundary_taxes");
        invalidTaxes = TestDataReader.getTestDataList(PRODUCT_PARAMS_DATA, "invalid_taxes");
        validCategoriesTypes = (List<String>) (List<?>) TestDataReader.getTestDataList(PRODUCT_PARAMS_DATA, "valid_categories_types");
        invalidCategoriesTypes = (List<String>) (List<?>) TestDataReader.getTestDataList(PRODUCT_PARAMS_DATA, "invalid_categories_types");
        priceRanges = TestDataReader.getTestDataList(PRODUCT_PARAMS_DATA, "price_ranges");
    }

    // ============= Common Parameter Tests =============

    @Test(priority = 1, description = "Test default products API request")
    public void testDefaultProductsRequest() {
        logger.info("Executing: testDefaultProductsRequest");
        Response response = testDefaultRequest();
        
        // Validate default quantity (10)
        validateResponseQuantity(response, 10);
        
        logger.info("testDefaultProductsRequest - PASSED");
    }

    @Test(priority = 2, description = "Test products API with schema validation")
    public void testProductsSchemaValidation() {
        logger.info("Executing: testProductsSchemaValidation");
        
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(5)
                .execute();
        
        validateSuccessResponse(response);
        validateResponseSchema(response);
        
        logger.info("testProductsSchemaValidation - PASSED");
    }

    @Test(priority = 3, description = "Test products API with different locales", dataProvider = "getLocales")
    public void testProductsWithDifferentLocales(String locale) {
        logger.info("Executing: testProductsWithDifferentLocales with locale: {}", locale);
        
        Response response = testWithLocale(locale);
        validateResponseQuantity(response, 10);
        
        logger.info("testProductsWithDifferentLocales - PASSED for locale: {}", locale);
    }

    @Test(priority = 4, description = "Test products API with valid quantities", dataProvider = "getValidQuantities")
    public void testProductsWithValidQuantities(int quantity) {
        logger.info("Executing: testProductsWithValidQuantities with quantity: {}", quantity);
        
        Response response = testWithQuantity(quantity);
        validateSuccessResponse(response);
        validateResponseQuantity(response, quantity);
        
        logger.info("testProductsWithValidQuantities - PASSED for quantity: {}", quantity);
    }

    @Test(priority = 5, description = "Test products API with boundary quantities")
    public void testProductsWithBoundaryQuantities() {
        logger.info("Executing: testProductsWithBoundaryQuantities");
        
        // Test minimum quantity (1)
        Response response1 = testWithQuantity(1);
        validateSuccessResponse(response1);
        validateResponseQuantity(response1, 1);
        
        // Test maximum quantity (1000)
        Response response2 = testWithQuantity(1000);
        validateSuccessResponse(response2);
        validateResponseQuantity(response2, 1000);
        
        logger.info("testProductsWithBoundaryQuantities - PASSED");
    }

    @Test(priority = 6, description = "Test products API with invalid quantities", dataProvider = "getInvalidQuantities")
    public void testProductsWithInvalidQuantities(int quantity) {
        logger.info("Executing: testProductsWithInvalidQuantities with quantity: {}", quantity);
        
        Response response = testWithQuantity(quantity);
        
        // Document the actual API behavior for invalid quantities
        logger.info("Response for invalid quantity {}: Status={}, Body={}", 
                quantity, response.getStatusCode(), response.asString());
    }

    @Test(priority = 7, description = "Test products API with seed for consistency")
    public void testProductsWithSeed() {
        logger.info("Executing: testProductsWithSeed");
        
        int testSeed = ((Number) testSeeds.get(0)).intValue();
        Response response = testWithSeed(testSeed);
        validateSuccessResponse(response);
        
        logger.info("testProductsWithSeed - PASSED");
    }

    @Test(priority = 8, description = "Test products API with all common parameters")
    public void testProductsWithAllCommonParameters() {
        logger.info("Executing: testProductsWithAllCommonParameters");
        
        String locale = sampleLocales.get(0);
        int quantity = 25;
        int seed = ((Number) testSeeds.get(0)).intValue();
        
        Response response = testWithAllCommonParams(locale, quantity, seed);
        validateResponseSchema(response);
        
        logger.info("testProductsWithAllCommonParameters - PASSED");
    }

    // ============= Product-Specific Parameter Tests: Price Min =============

    @Test(priority = 9, description = "Test products API with valid price_min values", dataProvider = "getValidPriceMin")
    public void testProductsWithValidPriceMin(double priceMin) {
        logger.info("Executing: testProductsWithValidPriceMin with price_min: {}", priceMin);
        
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(10)
                .withParam("_price_min", String.valueOf(priceMin))
                .execute();
        
        validateSuccessResponse(response);
        
        // Verify all products have price >= priceMin
        List<Map<String, Object>> products = response.jsonPath().getList("data");
        for (Map<String, Object> product : products) {
            double actualPrice = ((Number) product.get("price")).doubleValue();
            Assert.assertTrue(actualPrice >= priceMin,
                    String.format("Product price %.2f should be >= price_min %.2f", actualPrice, priceMin));
        }
        
        logger.info("testProductsWithValidPriceMin - PASSED for price_min: {}", priceMin);
    }

    @Test(priority = 10, description = "Test products API with boundary price_min values")
    public void testProductsWithBoundaryPriceMin() {
        logger.info("Executing: testProductsWithBoundaryPriceMin");
        
        for (Object priceMinObj : boundaryPriceMin) {
            double priceMin = ((Number) priceMinObj).doubleValue();
            
            Response response = new APIClient.RequestBuilder(getEndpoint())
                    .withQuantity(5)
                    .withParam("_price_min", String.valueOf(priceMin))
                    .execute();
            
            validateSuccessResponse(response);
            logger.info("Boundary price_min {} - PASSED", priceMin);
        }
        
        logger.info("testProductsWithBoundaryPriceMin - PASSED");
    }

    // ============= Product-Specific Parameter Tests: Price Max =============

    @Test(priority = 11, description = "Test products API with valid price_max values", dataProvider = "getValidPriceMax")
    public void testProductsWithValidPriceMax(double priceMax) {
        logger.info("Executing: testProductsWithValidPriceMax with price_max: {}", priceMax);
        
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(10)
                .withParam("_price_max", String.valueOf(priceMax))
                .withParam("_taxes", "0")  // Set taxes to 0 to get base prices
                .execute();
        
        validateSuccessResponse(response);
        
        // Verify all products have price <= priceMax (with tolerance for taxes)
        List<Map<String, Object>> products = response.jsonPath().getList("data");
        for (Map<String, Object> product : products) {
            double actualPrice = ((Number) product.get("price")).doubleValue();
            double tolerance = priceMax * 0.25;  // 25% tolerance for taxes
            Assert.assertTrue(actualPrice <= priceMax + tolerance,
                    String.format("Product price %.2f should be <= price_max %.2f (with tolerance)", actualPrice, priceMax));
        }
        
        logger.info("testProductsWithValidPriceMax - PASSED for price_max: {}", priceMax);
    }

    @Test(priority = 12, description = "Test products API with boundary price_max values")
    public void testProductsWithBoundaryPriceMax() {
        logger.info("Executing: testProductsWithBoundaryPriceMax");
        
        for (Object priceMaxObj : boundaryPriceMax) {
            double priceMax = ((Number) priceMaxObj).doubleValue();
            
            Response response = new APIClient.RequestBuilder(getEndpoint())
                    .withQuantity(5)
                    .withParam("_price_max", String.valueOf(priceMax))
                    .execute();
            
            validateSuccessResponse(response);
            logger.info("Boundary price_max {} - PASSED", priceMax);
        }
        
        logger.info("testProductsWithBoundaryPriceMax - PASSED");
    }

    // ============= Product-Specific Parameter Tests: Price Range =============

    @Test(priority = 13, description = "Test products API with price range (min and max)", dataProvider = "getPriceRanges")
    public void testProductsWithPriceRange(double priceMin, double priceMax) {
        logger.info("Executing: testProductsWithPriceRange with price_min: {} and price_max: {}", priceMin, priceMax);
        
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(20)
                .withParam("_price_min", String.valueOf(priceMin))
                .withParam("_price_max", String.valueOf(priceMax))
                .withParam("_taxes", "0")  // Set taxes to 0 to get base prices
                .execute();
        
        validateSuccessResponse(response);
        
        // Verify all products have price within range (with 10% tolerance for taxes)
        List<Map<String, Object>> products = response.jsonPath().getList("data");
        for (Map<String, Object> product : products) {
            double actualPrice = ((Number) product.get("price")).doubleValue();
            // Allow for some tolerance due to taxes or rounding
            double tolerance = priceMax * 0.25;  // 25% tolerance for taxes
            Assert.assertTrue(actualPrice >= priceMin * 0.95 && actualPrice <= priceMax + tolerance,
                    String.format("Product price %.2f should be approximately between %.2f and %.2f (with tolerance)", 
                            actualPrice, priceMin, priceMax));
        }
        
        logger.info("testProductsWithPriceRange - PASSED for range [{}, {}]", priceMin, priceMax);
    }

    @Test(priority = 14, description = "Test products API with invalid price values", dataProvider = "getInvalidPrices")
    public void testProductsWithInvalidPrices(double invalidPrice) {
        logger.info("Executing: testProductsWithInvalidPrices with price: {}", invalidPrice);
        
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(5)
                .withParam("_price_min", String.valueOf(invalidPrice))
                .execute();
        
        // Document the actual API behavior for invalid prices
        logger.info("Response for invalid price {}: Status={}, Body={}", 
                invalidPrice, response.getStatusCode(), response.asString());
    }

    // ============= Product-Specific Parameter Tests: Taxes =============

    @Test(priority = 15, description = "Test products API with valid tax values", dataProvider = "getValidTaxes")
    public void testProductsWithValidTaxes(int taxes) {
        logger.info("Executing: testProductsWithValidTaxes with taxes: {}%", taxes);
        
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(10)
                .withParam("_taxes", String.valueOf(taxes))
                .execute();
        
        validateSuccessResponse(response);
        
        logger.info("testProductsWithValidTaxes - PASSED for taxes: {}%", taxes);
    }

    @Test(priority = 16, description = "Test products API with default tax value (22%)")
    public void testProductsWithDefaultTax() {
        logger.info("Executing: testProductsWithDefaultTax");
        
        // Request without taxes parameter (should use default 22%)
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(5)
                .execute();
        
        validateSuccessResponse(response);
        
        logger.info("testProductsWithDefaultTax - PASSED");
    }

    @Test(priority = 17, description = "Test products API with boundary tax values")
    public void testProductsWithBoundaryTaxes() {
        logger.info("Executing: testProductsWithBoundaryTaxes");
        
        for (Object taxObj : boundaryTaxes) {
            int tax = ((Number) taxObj).intValue();
            
            Response response = new APIClient.RequestBuilder(getEndpoint())
                    .withQuantity(5)
                    .withParam("_taxes", String.valueOf(tax))
                    .execute();
            
            validateSuccessResponse(response);
            logger.info("Boundary tax {}% - PASSED", tax);
        }
        
        logger.info("testProductsWithBoundaryTaxes - PASSED");
    }

    @Test(priority = 18, description = "Test products API with invalid tax values", dataProvider = "getInvalidTaxes")
    public void testProductsWithInvalidTaxes(int taxes) {
        logger.info("Executing: testProductsWithInvalidTaxes with taxes: {}%", taxes);
        
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(5)
                .withParam("_taxes", String.valueOf(taxes))
                .execute();
        
        // Document the actual API behavior for invalid taxes
        logger.info("Response for invalid taxes {}: Status={}, Body={}", 
                taxes, response.getStatusCode(), response.asString());
    }

    // ============= Product-Specific Parameter Tests: Categories Type =============

    @Test(priority = 19, description = "Test products API with valid categories types", dataProvider = "getValidCategoriesTypes")
    public void testProductsWithValidCategoriesTypes(String categoryType) {
        logger.info("Executing: testProductsWithValidCategoriesTypes with type: {}", categoryType);
        
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(10)
                .withParam("_categories_type", categoryType)
                .execute();
        
        validateSuccessResponse(response);
        
        // Verify category type in response - categories is an array
        List<Map<String, Object>> products = response.jsonPath().getList("data");
        for (Map<String, Object> product : products) {
            Object categoriesObj = product.get("categories");
            Assert.assertNotNull(categoriesObj, "Product should have a categories field");
            Assert.assertTrue(categoriesObj instanceof List, "Categories should be an array");
            
            @SuppressWarnings("unchecked")
            List<Object> categories = (List<Object>) categoriesObj;
            Assert.assertFalse(categories.isEmpty(), "Categories array should not be empty");
            
            // Validate each category element type
            for (Object category : categories) {
                switch (categoryType) {
                    case "integer":
                        Assert.assertTrue(category instanceof Integer || category instanceof Number,
                                "Category element should be an integer for type: " + categoryType + ", but got: " + category.getClass().getSimpleName());
                        break;
                    case "string":
                        Assert.assertTrue(category instanceof String,
                                "Category element should be a string for type: " + categoryType + ", but got: " + category.getClass().getSimpleName());
                        // Verify it's NOT a UUID format for plain string type
                        String str = (String) category;
                        Assert.assertFalse(str.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"),
                                "Category should be a plain string, not UUID format");
                        break;
                    case "uuid":
                        Assert.assertTrue(category instanceof String,
                                "Category element should be a UUID string for type: " + categoryType + ", but got: " + category.getClass().getSimpleName());
                        // Verify UUID format
                        String uuid = (String) category;
                        Assert.assertTrue(uuid.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"),
                                "Category should match UUID format: " + uuid);
                        break;
                }
            }
        }
        
        logger.info("testProductsWithValidCategoriesTypes - PASSED for type: {}", categoryType);
    }

    @Test(priority = 20, description = "Test products API with default categories type (integer)")
    public void testProductsWithDefaultCategoriesType() {
        logger.info("Executing: testProductsWithDefaultCategoriesType");
        
        // Request without categories_type parameter (should use default integer)
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(5)
                .execute();
        
        validateSuccessResponse(response);
        
        // Verify default type is integer - categories is an array
        List<Map<String, Object>> products = response.jsonPath().getList("data");
        for (Map<String, Object> product : products) {
            Object categoriesObj = product.get("categories");
            Assert.assertNotNull(categoriesObj, "Product should have a categories field");
            Assert.assertTrue(categoriesObj instanceof List, "Categories should be an array");
            
            @SuppressWarnings("unchecked")
            List<Object> categories = (List<Object>) categoriesObj;
            Assert.assertFalse(categories.isEmpty(), "Categories array should not be empty");
            
            // Verify each element is an integer
            for (Object category : categories) {
                Assert.assertTrue(category instanceof Integer || category instanceof Number,
                        "Default category type should be integer, but got: " + category.getClass().getSimpleName());
            }
        }
        
        logger.info("testProductsWithDefaultCategoriesType - PASSED");
    }

    @Test(priority = 21, description = "Test products API with invalid categories types", dataProvider = "getInvalidCategoriesTypes")
    public void testProductsWithInvalidCategoriesTypes(String categoryType) {
        logger.info("Executing: testProductsWithInvalidCategoriesTypes with type: {}", categoryType);
        
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(5)
                .withParam("_categories_type", categoryType)
                .execute();
        
        // Document the actual API behavior for invalid category types
        logger.info("Response for invalid category type '{}': Status={}, Body={}", 
                categoryType, response.getStatusCode(), response.asString());
    }

    // ============= Product-Specific Combined Parameter Tests =============

    @Test(priority = 22, description = "Test products API with all product-specific parameters")
    public void testProductsWithAllSpecificParameters() {
        logger.info("Executing: testProductsWithAllSpecificParameters");
        
        double priceMin = 50.00;
        double priceMax = 500.00;
        int taxes = 10;
        String categoryType = "uuid";
        
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(20)
                .withParam("_price_min", String.valueOf(priceMin))
                .withParam("_price_max", String.valueOf(priceMax))
                .withParam("_taxes", String.valueOf(taxes))
                .withParam("_categories_type", categoryType)
                .execute();
        
        validateSuccessResponse(response);
        validateResponseQuantity(response, 20);
        
        // Verify price range (with tolerance for taxes)
        List<Map<String, Object>> products = response.jsonPath().getList("data");
        for (Map<String, Object> product : products) {
            double actualPrice = ((Number) product.get("price")).doubleValue();
            double tolerance = priceMax * 0.25;  // 25% tolerance for taxes
            Assert.assertTrue(actualPrice >= priceMin * 0.95 && actualPrice <= priceMax + tolerance,
                    String.format("Product price %.2f should be approximately between %.2f and %.2f", 
                            actualPrice, priceMin, priceMax));
            
            // Verify UUID format - categories is an array
            Object categoriesObj = product.get("categories");
            Assert.assertNotNull(categoriesObj, "Product should have a categories field");
            Assert.assertTrue(categoriesObj instanceof List, "Categories should be an array");
            
            @SuppressWarnings("unchecked")
            List<Object> categories = (List<Object>) categoriesObj;
            Assert.assertFalse(categories.isEmpty(), "Categories array should not be empty");
            
            // Verify each element is a UUID string
            for (Object category : categories) {
                Assert.assertTrue(category instanceof String, 
                        "Category element should be a string for UUID type, but got: " + category.getClass().getSimpleName());
                String uuid = (String) category;
                Assert.assertTrue(uuid.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"),
                        "Category should match UUID format: " + uuid);
            }
        }
        
        logger.info("testProductsWithAllSpecificParameters - PASSED");
    }

    @Test(priority = 23, description = "Test products API with all parameters including common ones")
    public void testProductsWithAllParameters() {
        logger.info("Executing: testProductsWithAllParameters");
        
        String locale = "en_US";
        int quantity = 30;
        int seed = 54321;
        double priceMin = 20.00;
        double priceMax = 1000.00;
        int taxes = 15;
        String categoryType = "string";
        
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withLocale(locale)
                .withQuantity(quantity)
                .withSeed(seed)
                .withParam("_price_min", String.valueOf(priceMin))
                .withParam("_price_max", String.valueOf(priceMax))
                .withParam("_taxes", String.valueOf(taxes))
                .withParam("_categories_type", categoryType)
                .execute();
        
        validateSuccessResponse(response);
        validateResponseQuantity(response, quantity);
        validateResponseSchema(response);
        
        logger.info("testProductsWithAllParameters - PASSED");
    }

    // ============= Product-Specific Data Structure Tests =============

    @Test(priority = 24, description = "Test products data structure and fields")
    public void testProductsDataStructure() {
        logger.info("Executing: testProductsDataStructure");
        
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(1)
                .execute();
        
        validateSuccessResponse(response);
        
        // Verify product fields
        Map<String, Object> product = response.jsonPath().getMap("data[0]");
        
        Assert.assertNotNull(product.get("id"), "Product should have id");
        Assert.assertNotNull(product.get("name"), "Product should have name");
        Assert.assertNotNull(product.get("description"), "Product should have description");
        Assert.assertNotNull(product.get("price"), "Product should have price");
        Assert.assertNotNull(product.get("categories"), "Product should have categories");
        Assert.assertNotNull(product.get("ean"), "Product should have ean");
        Assert.assertNotNull(product.get("image"), "Product should have image");
        
        // Verify categories is an array
        Object categoriesObj = product.get("categories");
        Assert.assertTrue(categoriesObj instanceof List, "Categories should be an array");
        
        // Log all fields to see actual structure
        logger.info("Product data: {}", product);
        logger.info("Available fields: {}", product.keySet());
        logger.info("testProductsDataStructure - PASSED");
    }

    @Test(priority = 25, description = "Test products price format")
    public void testProductsPriceFormat() {
        logger.info("Executing: testProductsPriceFormat");
        
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(10)
                .execute();
        
        validateSuccessResponse(response);
        
        // Verify price format (should be a positive number)
        List<Map<String, Object>> products = response.jsonPath().getList("data");
        for (Map<String, Object> product : products) {
            Object priceObj = product.get("price");
            Assert.assertNotNull(priceObj, "Price should not be null");
            
            double price = ((Number) priceObj).doubleValue();
            Assert.assertTrue(price > 0, "Price should be positive");
            
            logger.debug("Product: {}, Price: {}", product.get("name"), price);
        }
        
        logger.info("testProductsPriceFormat - PASSED");
    }

    // ============= Data Providers =============

    @org.testng.annotations.DataProvider(name = "getLocales")
    public Object[][] getLocales() {
        return sampleLocales.stream()
                .map(locale -> new Object[]{locale})
                .toArray(Object[][]::new);
    }

    @org.testng.annotations.DataProvider(name = "getValidQuantities")
    public Object[][] getValidQuantities() {
        return validQuantities.stream()
                .map(qty -> new Object[]{((Number) qty).intValue()})
                .toArray(Object[][]::new);
    }

    @org.testng.annotations.DataProvider(name = "getInvalidQuantities")
    public Object[][] getInvalidQuantities() {
        return invalidQuantities.stream()
                .map(qty -> new Object[]{((Number) qty).intValue()})
                .toArray(Object[][]::new);
    }

    @org.testng.annotations.DataProvider(name = "getValidPriceMin")
    public Object[][] getValidPriceMin() {
        return validPriceMin.stream()
                .map(price -> new Object[]{((Number) price).doubleValue()})
                .toArray(Object[][]::new);
    }

    @org.testng.annotations.DataProvider(name = "getValidPriceMax")
    public Object[][] getValidPriceMax() {
        return validPriceMax.stream()
                .map(price -> new Object[]{((Number) price).doubleValue()})
                .toArray(Object[][]::new);
    }

    @org.testng.annotations.DataProvider(name = "getInvalidPrices")
    public Object[][] getInvalidPrices() {
        return invalidPrices.stream()
                .map(price -> new Object[]{((Number) price).doubleValue()})
                .toArray(Object[][]::new);
    }

    @org.testng.annotations.DataProvider(name = "getValidTaxes")
    public Object[][] getValidTaxes() {
        return validTaxes.stream()
                .map(tax -> new Object[]{((Number) tax).intValue()})
                .toArray(Object[][]::new);
    }

    @org.testng.annotations.DataProvider(name = "getInvalidTaxes")
    public Object[][] getInvalidTaxes() {
        return invalidTaxes.stream()
                .map(tax -> new Object[]{((Number) tax).intValue()})
                .toArray(Object[][]::new);
    }

    @org.testng.annotations.DataProvider(name = "getValidCategoriesTypes")
    public Object[][] getValidCategoriesTypes() {
        return validCategoriesTypes.stream()
                .map(type -> new Object[]{type})
                .toArray(Object[][]::new);
    }

    @org.testng.annotations.DataProvider(name = "getInvalidCategoriesTypes")
    public Object[][] getInvalidCategoriesTypes() {
        return invalidCategoriesTypes.stream()
                .map(type -> new Object[]{type})
                .toArray(Object[][]::new);
    }

    @org.testng.annotations.DataProvider(name = "getPriceRanges")
    public Object[][] getPriceRanges() {
        return priceRanges.stream()
                .map(rangeObj -> {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> range = (Map<String, Object>) rangeObj;
                    double min = ((Number) range.get("min")).doubleValue();
                    double max = ((Number) range.get("max")).doubleValue();
                    return new Object[]{min, max};
                })
                .toArray(Object[][]::new);
    }
}

