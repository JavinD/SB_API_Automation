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

public abstract class BaseAPITest {
    protected static final Logger logger = LoggerFactory.getLogger(BaseAPITest.class);
    
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
        sampleLocales = (List<String>) (List<?>) TestDataReader.getTestDataList(LOCALES_DATA, "sample_locales");
        validQuantities = TestDataReader.getTestDataList(QUANTITIES_DATA, "valid_quantities");
        invalidQuantities = TestDataReader.getTestDataList(QUANTITIES_DATA, "invalid_quantities");
        testSeeds = TestDataReader.getTestDataList(SEEDS_DATA, "test_seeds");
    }

    protected abstract String getEndpoint();
    protected abstract String getSchemaPath();

    protected void validateCommonResponseStructure(Response response) {
        Assert.assertNotNull(response);
        Assert.assertTrue(SchemaValidator.hasExpectedStructure(response, "status", "code", "total", "data"));
    }

    protected void validateSuccessResponse(Response response) {
        Assert.assertNotNull(response);
        
        if (response.getStatusCode() == 429) {
            Assert.fail("Rate limit exceeded");
        }
        
        Assert.assertEquals(response.getStatusCode(), 200, 
                "Expected 200, got " + response.getStatusCode());
        
        try {
            Assert.assertEquals(response.jsonPath().getString("status"), "OK");
            Assert.assertNotNull(response.jsonPath().get("data"));
        } catch (Exception e) {
            Assert.fail("Failed to parse response: " + e.getMessage());
        }
    }

    protected void validateResponseSchema(Response response) {
        SchemaValidator.validateSchema(response, getSchemaPath());
    }

    protected void validateResponseQuantity(Response response, int expectedQuantity) {
        List<?> data = response.jsonPath().getList("data");
        int expectedActual = Math.min(expectedQuantity, 1000);
        Assert.assertEquals(data.size(), expectedActual,
                String.format("Expected %d items but got %d", expectedActual, data.size()));
    }

    protected Response testDefaultRequest() {
        Response response = new APIClient.RequestBuilder(getEndpoint()).execute();
        validateSuccessResponse(response);
        validateCommonResponseStructure(response);
        return response;
    }

    protected Response testWithLocale(String locale) {
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withLocale(locale)
                .execute();
        validateSuccessResponse(response);
        return response;
    }

    protected Response testWithQuantity(int quantity) {
        return new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(quantity)
                .execute();
    }

    protected Response testWithSeed(int seed) {
        Response response1 = new APIClient.RequestBuilder(getEndpoint())
                .withSeed(seed)
                .withQuantity(5)
                .execute();
        
        Response response2 = new APIClient.RequestBuilder(getEndpoint())
                .withSeed(seed)
                .withQuantity(5)
                .execute();
        
        Assert.assertEquals(response1.asString(), response2.asString(),
                "Same seed should produce identical responses");
        
        return response1;
    }

    protected Response testWithAllCommonParams(String locale, int quantity, int seed) {
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
