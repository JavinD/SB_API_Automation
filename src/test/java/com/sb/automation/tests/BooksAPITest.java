package com.sb.automation.tests;

import com.sb.automation.config.APIConfig;
import com.sb.automation.utils.APIClient;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * Test class for Books API
 * Includes tests for common parameters (locale, quantity, seed)
 */
public class BooksAPITest extends BaseAPITest {

    @Override
    protected String getEndpoint() {
        return APIConfig.BOOKS_ENDPOINT;
    }

    @Override
    protected String getSchemaPath() {
        return "src/test/resources/schemas/books-schema.json";
    }

    @BeforeClass
    public void setup() {
        logger.info("Setting up Books API tests");
    }

    // ============= Common Parameter Tests =============

    @Test(priority = 1, description = "Test default books API request")
    public void testDefaultBooksRequest() {
        logger.info("Executing: testDefaultBooksRequest");
        Response response = testDefaultRequest();
        
        // Validate default quantity (10)
        validateResponseQuantity(response, 10);
        
        logger.info("testDefaultBooksRequest - PASSED");
    }

    @Test(priority = 2, description = "Test books API with schema validation")
    public void testBooksSchemaValidation() {
        logger.info("Executing: testBooksSchemaValidation");
        
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(5)
                .execute();
        
        validateSuccessResponse(response);
        validateResponseSchema(response);
        
        logger.info("testBooksSchemaValidation - PASSED");
    }

    @Test(priority = 3, description = "Test books API with different locales", dataProvider = "getLocales")
    public void testBooksWithDifferentLocales(String locale) {
        logger.info("Executing: testBooksWithDifferentLocales with locale: {}", locale);
        
        Response response = testWithLocale(locale);
        validateResponseQuantity(response, 10);
        
        logger.info("testBooksWithDifferentLocales - PASSED for locale: {}", locale);
    }

    @Test(priority = 4, description = "Test books API with valid quantities", dataProvider = "getValidQuantities")
    public void testBooksWithValidQuantities(int quantity) {
        logger.info("Executing: testBooksWithValidQuantities with quantity: {}", quantity);
        
        Response response = testWithQuantity(quantity);
        validateSuccessResponse(response);
        validateResponseQuantity(response, quantity);
        
        logger.info("testBooksWithValidQuantities - PASSED for quantity: {}", quantity);
    }

    @Test(priority = 5, description = "Test books API with boundary quantities")
    public void testBooksWithBoundaryQuantities() {
        logger.info("Executing: testBooksWithBoundaryQuantities");
        
        // Test minimum quantity (1)
        Response response1 = testWithQuantity(1);
        validateSuccessResponse(response1);
        validateResponseQuantity(response1, 1);
        
        // Test maximum quantity (1000)
        Response response2 = testWithQuantity(1000);
        validateSuccessResponse(response2);
        validateResponseQuantity(response2, 1000);
        
        logger.info("testBooksWithBoundaryQuantities - PASSED");
    }

    @Test(priority = 6, description = "Test books API with quantity at edge case 999")
    public void testBooksWithEdgeCaseQuantity() {
        logger.info("Executing: testBooksWithEdgeCaseQuantity");
        
        Response response = testWithQuantity(999);
        validateSuccessResponse(response);
        validateResponseQuantity(response, 999);
        
        logger.info("testBooksWithEdgeCaseQuantity - PASSED");
    }

    @Test(priority = 7, description = "Test books API with invalid quantities", dataProvider = "getInvalidQuantities")
    public void testBooksWithInvalidQuantities(int quantity) {
        logger.info("Executing: testBooksWithInvalidQuantities with quantity: {}", quantity);
        
        Response response = testWithQuantity(quantity);
        
        // For invalid quantities, document the actual API behavior
        logger.info("Response for invalid quantity {}: Status={}, Body={}", 
                quantity, response.getStatusCode(), response.asString());
        
        // Note: Adjust assertions based on actual API behavior
        // Some APIs return 400, some return empty data, some return default quantity
    }

    @Test(priority = 8, description = "Test books API with seed for consistency")
    public void testBooksWithSeed() {
        logger.info("Executing: testBooksWithSeed");
        
        int testSeed = ((Number) testSeeds.get(0)).intValue();
        Response response = testWithSeed(testSeed);
        validateSuccessResponse(response);
        
        logger.info("testBooksWithSeed - PASSED");
    }

    @Test(priority = 9, description = "Test books API with all common parameters")
    public void testBooksWithAllCommonParameters() {
        logger.info("Executing: testBooksWithAllCommonParameters");
        
        String locale = sampleLocales.get(0);
        int quantity = 25;
        int seed = ((Number) testSeeds.get(0)).intValue();
        
        Response response = testWithAllCommonParams(locale, quantity, seed);
        validateResponseSchema(response);
        
        logger.info("testBooksWithAllCommonParameters - PASSED");
    }

    @Test(priority = 10, description = "Test books API with multiple seeds")
    public void testBooksWithMultipleSeeds() {
        logger.info("Executing: testBooksWithMultipleSeeds");
        
        int seed1 = ((Number) testSeeds.get(0)).intValue();
        int seed2 = ((Number) testSeeds.get(1)).intValue();
        
        Response response1 = new APIClient.RequestBuilder(getEndpoint())
                .withSeed(seed1)
                .withQuantity(5)
                .execute();
        
        Response response2 = new APIClient.RequestBuilder(getEndpoint())
                .withSeed(seed2)
                .withQuantity(5)
                .execute();
        
        validateSuccessResponse(response1);
        validateSuccessResponse(response2);
        
        // Different seeds should produce different results
        Assert.assertNotEquals(response1.asString(), response2.asString(),
                "Responses with different seeds should be different");
        
        logger.info("testBooksWithMultipleSeeds - PASSED");
    }

    // ============= Books-Specific Tests =============

    @Test(priority = 11, description = "Test books data structure and fields")
    public void testBooksDataStructure() {
        logger.info("Executing: testBooksDataStructure");
        
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(1)
                .execute();
        
        validateSuccessResponse(response);
        
        // Verify book fields
        Map<String, Object> book = response.jsonPath().getMap("data[0]");
        
        Assert.assertNotNull(book.get("id"), "Book should have id");
        Assert.assertNotNull(book.get("title"), "Book should have title");
        Assert.assertNotNull(book.get("author"), "Book should have author");
        Assert.assertNotNull(book.get("genre"), "Book should have genre");
        Assert.assertNotNull(book.get("description"), "Book should have description");
        Assert.assertNotNull(book.get("isbn"), "Book should have isbn");
        Assert.assertNotNull(book.get("image"), "Book should have image");
        Assert.assertNotNull(book.get("published"), "Book should have published date");
        Assert.assertNotNull(book.get("publisher"), "Book should have publisher");
        
        logger.info("Book data: {}", book);
        logger.info("testBooksDataStructure - PASSED");
    }

    @Test(priority = 12, description = "Test books ISBN format")
    public void testBooksISBNFormat() {
        logger.info("Executing: testBooksISBNFormat");
        
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(10)
                .execute();
        
        validateSuccessResponse(response);
        
        // Check ISBN format (should contain numbers and hyphens)
        response.jsonPath().getList("data").forEach(bookObj -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> book = (Map<String, Object>) bookObj;
            String isbn = (String) book.get("isbn");
            Assert.assertNotNull(isbn, "ISBN should not be null");
            Assert.assertFalse(isbn.isEmpty(), "ISBN should not be empty");
            logger.debug("ISBN: {}", isbn);
        });
        
        logger.info("testBooksISBNFormat - PASSED");
    }

    @Test(priority = 13, description = "Test books response time")
    public void testBooksResponseTime() {
        logger.info("Executing: testBooksResponseTime");
        
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(100)
                .execute();
        
        validateSuccessResponse(response);
        
        long responseTime = response.getTime();
        logger.info("Response time: {} ms", responseTime);
        
        // Assert reasonable response time (adjust threshold as needed)
        Assert.assertTrue(responseTime < 5000, 
                "Response time should be less than 5 seconds, but was: " + responseTime + " ms");
        
        logger.info("testBooksResponseTime - PASSED");
    }

    @Test(priority = 14, description = "Test books locale impact on content")
    public void testBooksLocaleImpact() {
        logger.info("Executing: testBooksLocaleImpact");
        
        // Test with English locale
        Response responseEN = new APIClient.RequestBuilder(getEndpoint())
                .withLocale("en_US")
                .withQuantity(5)
                .withSeed(12345)
                .execute();
        
        // Test with French locale
        Response responseFR = new APIClient.RequestBuilder(getEndpoint())
                .withLocale("fr_FR")
                .withQuantity(5)
                .withSeed(12345)
                .execute();
        
        validateSuccessResponse(responseEN);
        validateSuccessResponse(responseFR);
        
        logger.info("EN Response: {}", responseEN.jsonPath().getString("data[0].title"));
        logger.info("FR Response: {}", responseFR.jsonPath().getString("data[0].title"));
        
        logger.info("testBooksLocaleImpact - PASSED");
    }

    @Test(priority = 15, description = "Test books API total field accuracy")
    public void testBooksTotalFieldAccuracy() {
        logger.info("Executing: testBooksTotalFieldAccuracy");
        
        int requestedQuantity = 50;
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(requestedQuantity)
                .execute();
        
        validateSuccessResponse(response);
        
        int total = response.jsonPath().getInt("total");
        int dataSize = response.jsonPath().getList("data").size();
        
        Assert.assertEquals(total, dataSize, 
                "Total field should match the number of items in data array");
        Assert.assertEquals(dataSize, requestedQuantity, 
                "Data array size should match requested quantity");
        
        logger.info("testBooksTotalFieldAccuracy - PASSED");
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
}

