package com.sb.automation.tests;

import com.sb.automation.config.APIConfig;
import com.sb.automation.utils.APIClient;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;

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

    @Test(priority = 1)
    public void testDefaultBooksRequest() {
        Response response = testDefaultRequest();
        validateResponseQuantity(response, 10);
    }

    @Test(priority = 2)
    public void testBooksSchemaValidation() {
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(5)
                .execute();
        
        validateSuccessResponse(response);
        validateResponseSchema(response);
    }

    @Test(priority = 3, dataProvider = "getLocales")
    public void testBooksWithDifferentLocales(String locale) {
        Response response = testWithLocale(locale);
        validateResponseQuantity(response, 10);
    }

    @Test(priority = 4, dataProvider = "getValidQuantities")
    public void testBooksWithValidQuantities(int quantity) {
        Response response = testWithQuantity(quantity);
        validateSuccessResponse(response);
        validateResponseQuantity(response, quantity);
    }

    @Test(priority = 5)
    public void testBooksWithBoundaryQuantities() {
        Response response1 = testWithQuantity(1);
        validateSuccessResponse(response1);
        validateResponseQuantity(response1, 1);
        
        Response response2 = testWithQuantity(1000);
        validateSuccessResponse(response2);
        validateResponseQuantity(response2, 1000);
    }

    @Test(priority = 6)
    public void testBooksWithEdgeCaseQuantity() {
        Response response = testWithQuantity(999);
        validateSuccessResponse(response);
        validateResponseQuantity(response, 999);
    }

    @Test(priority = 7, dataProvider = "getInvalidQuantities")
    public void testBooksWithInvalidQuantities(int quantity) {
        Response response = testWithQuantity(quantity);
        logger.info("Invalid quantity {}: status={}", quantity, response.getStatusCode());
    }

    @Test(priority = 8)
    public void testBooksWithSeed() {
        int testSeed = ((Number) testSeeds.get(0)).intValue();
        Response response = testWithSeed(testSeed);
        validateSuccessResponse(response);
    }

    @Test(priority = 9)
    public void testBooksWithAllCommonParameters() {
        String locale = sampleLocales.get(0);
        int seed = ((Number) testSeeds.get(0)).intValue();
        
        Response response = testWithAllCommonParams(locale, 25, seed);
        validateResponseSchema(response);
    }

    @Test(priority = 10)
    public void testBooksWithMultipleSeeds() {
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
        
        Assert.assertNotEquals(response1.asString(), response2.asString());
    }

    @Test(priority = 11)
    public void testBooksDataStructure() {
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(1)
                .execute();
        
        validateSuccessResponse(response);
        
        Map<String, Object> book = response.jsonPath().getMap("data[0]");
        
        Assert.assertNotNull(book.get("id"));
        Assert.assertNotNull(book.get("title"));
        Assert.assertNotNull(book.get("author"));
        Assert.assertNotNull(book.get("genre"));
        Assert.assertNotNull(book.get("description"));
        Assert.assertNotNull(book.get("isbn"));
        Assert.assertNotNull(book.get("image"));
        Assert.assertNotNull(book.get("published"));
        Assert.assertNotNull(book.get("publisher"));
    }

    @Test(priority = 12)
    public void testBooksISBNFormat() {
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(10)
                .execute();
        
        validateSuccessResponse(response);
        
        response.jsonPath().getList("data").forEach(bookObj -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> book = (Map<String, Object>) bookObj;
            String isbn = (String) book.get("isbn");
            Assert.assertNotNull(isbn);
            Assert.assertFalse(isbn.isEmpty());
        });
    }

    @Test(priority = 13)
    public void testBooksResponseTime() {
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(100)
                .execute();
        
        validateSuccessResponse(response);
        Assert.assertTrue(response.getTime() < 5000, "Response took too long: " + response.getTime() + "ms");
    }

    @Test(priority = 14)
    public void testBooksLocaleImpact() {
        Response responseEN = new APIClient.RequestBuilder(getEndpoint())
                .withLocale("en_US")
                .withQuantity(5)
                .withSeed(12345)
                .execute();
        
        Response responseFR = new APIClient.RequestBuilder(getEndpoint())
                .withLocale("fr_FR")
                .withQuantity(5)
                .withSeed(12345)
                .execute();
        
        validateSuccessResponse(responseEN);
        validateSuccessResponse(responseFR);
    }

    @Test(priority = 15)
    public void testBooksTotalFieldAccuracy() {
        int requestedQuantity = 50;
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(requestedQuantity)
                .execute();
        
        validateSuccessResponse(response);
        
        int total = response.jsonPath().getInt("total");
        int dataSize = response.jsonPath().getList("data").size();
        
        Assert.assertEquals(total, dataSize);
        Assert.assertEquals(dataSize, requestedQuantity);
    }

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
