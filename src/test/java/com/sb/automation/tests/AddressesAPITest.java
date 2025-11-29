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
 * Test class for Addresses API
 * Includes tests for common parameters and address-specific parameters like country_code
 */
public class AddressesAPITest extends BaseAPITest {
    
    private static final String COUNTRY_CODES_DATA = "src/test/resources/testdata/country_codes.json";
    private List<String> validCountryCodes;
    private List<String> invalidCountryCodes;

    @Override
    protected String getEndpoint() {
        return APIConfig.ADDRESSES_ENDPOINT;
    }

    @Override
    protected String getSchemaPath() {
        return "src/test/resources/schemas/addresses-schema.json";
    }

    @BeforeClass
    @SuppressWarnings("unchecked")
    public void setup() {
        logger.info("Setting up Addresses API tests");
        validCountryCodes = (List<String>) (List<?>) TestDataReader.getTestDataList(COUNTRY_CODES_DATA, "valid_country_codes");
        invalidCountryCodes = (List<String>) (List<?>) TestDataReader.getTestDataList(COUNTRY_CODES_DATA, "invalid_country_codes");
    }

    // ============= Common Parameter Tests =============

    @Test(priority = 1, description = "Test default addresses API request")
    public void testDefaultAddressesRequest() {
        logger.info("Executing: testDefaultAddressesRequest");
        Response response = testDefaultRequest();
        
        // Validate default quantity (10)
        validateResponseQuantity(response, 10);
        
        logger.info("testDefaultAddressesRequest - PASSED");
    }

    @Test(priority = 2, description = "Test addresses API with schema validation")
    public void testAddressesSchemaValidation() {
        logger.info("Executing: testAddressesSchemaValidation");
        
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(5)
                .execute();
        
        validateSuccessResponse(response);
        validateResponseSchema(response);
        
        logger.info("testAddressesSchemaValidation - PASSED");
    }

    @Test(priority = 3, description = "Test addresses API with different locales", dataProvider = "getLocales")
    public void testAddressesWithDifferentLocales(String locale) {
        logger.info("Executing: testAddressesWithDifferentLocales with locale: {}", locale);
        
        Response response = testWithLocale(locale);
        validateResponseQuantity(response, 10);
        
        logger.info("testAddressesWithDifferentLocales - PASSED for locale: {}", locale);
    }

    @Test(priority = 4, description = "Test addresses API with valid quantities", dataProvider = "getValidQuantities")
    public void testAddressesWithValidQuantities(int quantity) {
        logger.info("Executing: testAddressesWithValidQuantities with quantity: {}", quantity);
        
        Response response = testWithQuantity(quantity);
        validateSuccessResponse(response);
        validateResponseQuantity(response, quantity);
        
        logger.info("testAddressesWithValidQuantities - PASSED for quantity: {}", quantity);
    }

    @Test(priority = 5, description = "Test addresses API with boundary quantities")
    public void testAddressesWithBoundaryQuantities() {
        logger.info("Executing: testAddressesWithBoundaryQuantities");
        
        // Test minimum quantity (1)
        Response response1 = testWithQuantity(1);
        validateSuccessResponse(response1);
        validateResponseQuantity(response1, 1);
        
        // Test maximum quantity (1000)
        Response response2 = testWithQuantity(1000);
        validateSuccessResponse(response2);
        validateResponseQuantity(response2, 1000);
        
        logger.info("testAddressesWithBoundaryQuantities - PASSED");
    }

    @Test(priority = 6, description = "Test addresses API with invalid quantities", dataProvider = "getInvalidQuantities")
    public void testAddressesWithInvalidQuantities(int quantity) {
        logger.info("Executing: testAddressesWithInvalidQuantities with quantity: {}", quantity);
        
        Response response = testWithQuantity(quantity);
        
        // For invalid quantities, API might return error or default behavior
        // Document the actual API behavior
        logger.info("Response for invalid quantity {}: Status={}, Body={}", 
                quantity, response.getStatusCode(), response.asString());
        
        // Note: Adjust assertions based on actual API behavior
        // Some APIs return 400, some return empty data, some return default quantity
    }

    @Test(priority = 7, description = "Test addresses API with seed for consistency")
    public void testAddressesWithSeed() {
        logger.info("Executing: testAddressesWithSeed");
        
        int testSeed = ((Number) testSeeds.get(0)).intValue();
        Response response = testWithSeed(testSeed);
        validateSuccessResponse(response);
        
        logger.info("testAddressesWithSeed - PASSED");
    }

    @Test(priority = 8, description = "Test addresses API with all common parameters")
    public void testAddressesWithAllCommonParameters() {
        logger.info("Executing: testAddressesWithAllCommonParameters");
        
        String locale = sampleLocales.get(0);
        int quantity = 25;
        int seed = ((Number) testSeeds.get(0)).intValue();
        
        Response response = testWithAllCommonParams(locale, quantity, seed);
        validateResponseSchema(response);
        
        logger.info("testAddressesWithAllCommonParameters - PASSED");
    }

    // ============= Address-Specific Parameter Tests =============

    @Test(priority = 9, description = "Test addresses API with valid country code", dataProvider = "getValidCountryCodes")
    public void testAddressesWithValidCountryCode(String countryCode) {
        logger.info("Executing: testAddressesWithValidCountryCode with country: {}", countryCode);
        
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(10)
                .withParam("_country_code", countryCode)
                .execute();
        
        validateSuccessResponse(response);
        
        // Verify all addresses have the specified country code
        List<Map<String, Object>> addresses = response.jsonPath().getList("data");
        for (Map<String, Object> address : addresses) {
            String actualCountryCode = (String) address.get("county_code");
            Assert.assertEquals(actualCountryCode, countryCode,
                    "Address country code should match requested country code");
        }
        
        logger.info("testAddressesWithValidCountryCode - PASSED for country: {}", countryCode);
    }

    @Test(priority = 10, description = "Test addresses API with country code and locale")
    public void testAddressesWithCountryCodeAndLocale() {
        logger.info("Executing: testAddressesWithCountryCodeAndLocale");
        
        String countryCode = validCountryCodes.get(0);
        String locale = "en_US";
        
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withLocale(locale)
                .withQuantity(10)
                .withParam("_country_code", countryCode)
                .execute();
        
        validateSuccessResponse(response);
        validateResponseQuantity(response, 10);
        
        // Verify country code
        List<Map<String, Object>> addresses = response.jsonPath().getList("data");
        for (Map<String, Object> address : addresses) {
            String actualCountryCode = (String) address.get("county_code");
            Assert.assertEquals(actualCountryCode, countryCode,
                    "Address country code should match requested country code");
        }
        
        logger.info("testAddressesWithCountryCodeAndLocale - PASSED");
    }

    @Test(priority = 11, description = "Test addresses API with all parameters including country code")
    public void testAddressesWithAllParameters() {
        logger.info("Executing: testAddressesWithAllParameters");
        
        String locale = "en_GB";
        int quantity = 20;
        int seed = 12345;
        String countryCode = "GB";
        
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withLocale(locale)
                .withQuantity(quantity)
                .withSeed(seed)
                .withParam("_country_code", countryCode)
                .execute();
        
        validateSuccessResponse(response);
        validateResponseQuantity(response, quantity);
        validateResponseSchema(response);
        
        // Verify country code
        List<Map<String, Object>> addresses = response.jsonPath().getList("data");
        for (Map<String, Object> address : addresses) {
            String actualCountryCode = (String) address.get("county_code");
            Assert.assertEquals(actualCountryCode, countryCode,
                    "Address country code should match requested country code");
        }
        
        logger.info("testAddressesWithAllParameters - PASSED");
    }

    @Test(priority = 12, description = "Test addresses API with invalid country code", dataProvider = "getInvalidCountryCodes")
    public void testAddressesWithInvalidCountryCode(String countryCode) {
        logger.info("Executing: testAddressesWithInvalidCountryCode with country: {}", countryCode);
        
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(5)
                .withParam("_country_code", countryCode)
                .execute();
        
        // Document the actual API behavior for invalid country codes
        logger.info("Response for invalid country code '{}': Status={}, Body={}", 
                countryCode, response.getStatusCode(), response.asString());
        
        // Note: Adjust assertions based on actual API behavior
    }

    @Test(priority = 13, description = "Test addresses data structure and fields")
    public void testAddressesDataStructure() {
        logger.info("Executing: testAddressesDataStructure");
        
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(1)
                .execute();
        
        validateSuccessResponse(response);
        
        // Verify address fields
        Map<String, Object> address = response.jsonPath().getMap("data[0]");
        
        Assert.assertNotNull(address.get("id"), "Address should have id");
        Assert.assertNotNull(address.get("street"), "Address should have street");
        Assert.assertNotNull(address.get("streetName"), "Address should have streetName");
        Assert.assertNotNull(address.get("buildingNumber"), "Address should have buildingNumber");
        Assert.assertNotNull(address.get("city"), "Address should have city");
        Assert.assertNotNull(address.get("zipcode"), "Address should have zipcode");
        Assert.assertNotNull(address.get("country"), "Address should have country");
        Assert.assertNotNull(address.get("county_code"), "Address should have county_code");
        Assert.assertNotNull(address.get("latitude"), "Address should have latitude");
        Assert.assertNotNull(address.get("longitude"), "Address should have longitude");
        
        logger.info("Address data: {}", address);
        logger.info("testAddressesDataStructure - PASSED");
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

    @org.testng.annotations.DataProvider(name = "getValidCountryCodes")
    public Object[][] getValidCountryCodes() {
        // Return subset for faster execution
        return validCountryCodes.stream()
                .limit(5)
                .map(code -> new Object[]{code})
                .toArray(Object[][]::new);
    }

    @org.testng.annotations.DataProvider(name = "getInvalidCountryCodes")
    public Object[][] getInvalidCountryCodes() {
        return invalidCountryCodes.stream()
                .map(code -> new Object[]{code})
                .toArray(Object[][]::new);
    }
}

