package com.sb.automation.tests;

import com.sb.automation.utils.APIClient;
import com.sb.automation.utils.SchemaValidator;
import com.sb.automation.utils.TestDataReader;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;

import java.util.List;

/**
 * Base test class with reusable test methods for common parameters
 * All API test classes should extend this class
 */
public abstract class BaseAPITest {
    protected static final Logger logger = LoggerFactory.getLogger(BaseAPITest.class);
    
    // Test data paths
    protected static final String LOCALES_DATA = "src/test/resources/testdata/locales.json";
    protected static final String QUANTITIES_DATA = "src/test/resources/testdata/quantities.json";
    protected static final String SEEDS_DATA = "src/test/resources/testdata/seeds.json";
    
    protected List<String> sampleLocales;
    protected List<Object> validQuantities;
    protected List<Object> invalidQuantities;
    protected List<Object> testSeeds;

    @BeforeClass
    @SuppressWarnings("unchecked")
    public void baseSetup() {
        logger.info("Setting up base test data");
        sampleLocales = (List<String>) (List<?>) TestDataReader.getTestDataList(LOCALES_DATA, "sample_locales");
        validQuantities = TestDataReader.getTestDataList(QUANTITIES_DATA, "valid_quantities");
        invalidQuantities = TestDataReader.getTestDataList(QUANTITIES_DATA, "invalid_quantities");
        testSeeds = TestDataReader.getTestDataList(SEEDS_DATA, "test_seeds");
    }

    /**
     * Get the endpoint for the specific API
     */
    protected abstract String getEndpoint();

    /**
     * Get the schema file path for the specific API
     */
    protected abstract String getSchemaPath();

    /**
     * Validates common response structure
     */
    protected void validateCommonResponseStructure(Response response) {
        Assert.assertNotNull(response, "Response should not be null");
        Assert.assertTrue(SchemaValidator.hasExpectedStructure(response, "status", "code", "total", "data"),
                "Response should have expected structure");
    }

    /**
     * Validates successful response
     */
    protected void validateSuccessResponse(Response response) {
        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");
        Assert.assertEquals(response.jsonPath().getString("status"), "OK", "Status should be OK");
        Assert.assertNotNull(response.jsonPath().getList("data"), "Data array should not be null");
    }

    /**
     * Validates response with schema
     */
    protected void validateResponseSchema(Response response) {
        SchemaValidator.validateSchema(response, getSchemaPath());
    }

    /**
     * Validates quantity in response
     */
    protected void validateResponseQuantity(Response response, int expectedQuantity) {
        List<?> data = response.jsonPath().getList("data");
        int actualQuantity = data.size();
        
        // If requested quantity is over max (1000), we should get 1000
        int maxQuantity = 1000;
        int expectedActualQuantity = Math.min(expectedQuantity, maxQuantity);
        
        Assert.assertEquals(actualQuantity, expectedActualQuantity,
                String.format("Expected %d items but got %d", expectedActualQuantity, actualQuantity));
    }

    /**
     * Tests default request without parameters
     */
    protected Response testDefaultRequest() {
        logger.info("Testing default request for endpoint: {}", getEndpoint());
        
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .execute();
        
        validateSuccessResponse(response);
        validateCommonResponseStructure(response);
        
        return response;
    }

    /**
     * Tests request with specific locale
     */
    protected Response testWithLocale(String locale) {
        logger.info("Testing request with locale: {}", locale);
        
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withLocale(locale)
                .execute();
        
        validateSuccessResponse(response);
        
        return response;
    }

    /**
     * Tests request with specific quantity
     */
    protected Response testWithQuantity(int quantity) {
        logger.info("Testing request with quantity: {}", quantity);
        
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(quantity)
                .execute();
        
        return response;
    }

    /**
     * Tests request with seed to ensure consistent results
     */
    protected Response testWithSeed(int seed) {
        logger.info("Testing request with seed: {}", seed);
        
        Response response1 = new APIClient.RequestBuilder(getEndpoint())
                .withSeed(seed)
                .withQuantity(5)
                .execute();
        
        Response response2 = new APIClient.RequestBuilder(getEndpoint())
                .withSeed(seed)
                .withQuantity(5)
                .execute();
        
        // With same seed, responses should be identical
        Assert.assertEquals(response1.asString(), response2.asString(),
                "Responses with same seed should be identical");
        
        return response1;
    }

    /**
     * Tests request with all common parameters
     */
    protected Response testWithAllCommonParams(String locale, int quantity, int seed) {
        logger.info("Testing request with locale: {}, quantity: {}, seed: {}", locale, quantity, seed);
        
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withLocale(locale)
                .withQuantity(quantity)
                .withSeed(seed)
                .execute();
        
        validateSuccessResponse(response);
        validateResponseQuantity(response, quantity);
        
        return response;
    }
}

