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
        validCountryCodes = (List<String>) (List<?>) TestDataReader.getTestDataList(COUNTRY_CODES_DATA, "valid_country_codes");
        invalidCountryCodes = (List<String>) (List<?>) TestDataReader.getTestDataList(COUNTRY_CODES_DATA, "invalid_country_codes");
    }

    @Test(priority = 1)
    public void testDefaultAddressesRequest() {
        Response response = testDefaultRequest();
        validateResponseQuantity(response, 10);
    }

    @Test(priority = 2)
    public void testAddressesSchemaValidation() {
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(5)
                .execute();
        
        validateSuccessResponse(response);
        validateResponseSchema(response);
    }

    @Test(priority = 3, dataProvider = "getLocales")
    public void testAddressesWithDifferentLocales(String locale) {
        Response response = testWithLocale(locale);
        validateResponseQuantity(response, 10);
    }

    @Test(priority = 4, dataProvider = "getValidQuantities")
    public void testAddressesWithValidQuantities(int quantity) {
        Response response = testWithQuantity(quantity);
        validateSuccessResponse(response);
        validateResponseQuantity(response, quantity);
    }

    @Test(priority = 5)
    public void testAddressesWithBoundaryQuantities() {
        Response response1 = testWithQuantity(1);
        validateSuccessResponse(response1);
        validateResponseQuantity(response1, 1);
        
        Response response2 = testWithQuantity(1000);
        validateSuccessResponse(response2);
        validateResponseQuantity(response2, 1000);
    }

    @Test(priority = 6, dataProvider = "getInvalidQuantities")
    public void testAddressesWithInvalidQuantities(int quantity) {
        Response response = testWithQuantity(quantity);
        logger.info("Invalid quantity {}: status={}", quantity, response.getStatusCode());
    }

    @Test(priority = 7)
    public void testAddressesWithSeed() {
        int testSeed = ((Number) testSeeds.get(0)).intValue();
        Response response = testWithSeed(testSeed);
        validateSuccessResponse(response);
    }

    @Test(priority = 8)
    public void testAddressesWithAllCommonParameters() {
        String locale = sampleLocales.get(0);
        int seed = ((Number) testSeeds.get(0)).intValue();
        
        Response response = testWithAllCommonParams(locale, 25, seed);
        validateResponseSchema(response);
    }

    @Test(priority = 9, dataProvider = "getValidCountryCodes")
    public void testAddressesWithValidCountryCode(String countryCode) {
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(10)
                .withParam("_country_code", countryCode)
                .execute();
        
        validateSuccessResponse(response);
        
        List<Map<String, Object>> addresses = response.jsonPath().getList("data");
        for (Map<String, Object> address : addresses) {
            String actualCountryCode = (String) address.get("country_code");
            if (actualCountryCode == null || !actualCountryCode.equals(countryCode)) {
                logger.warn("Country code mismatch: expected={}, actual={}", countryCode, actualCountryCode);
            }
        }
    }

    @Test(priority = 10)
    public void testAddressesWithCountryCodeAndLocale() {
        String countryCode = validCountryCodes.get(0);
        
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withLocale("en_US")
                .withQuantity(10)
                .withParam("_country_code", countryCode)
                .execute();
        
        validateSuccessResponse(response);
        validateResponseQuantity(response, 10);
    }

    @Test(priority = 11)
    public void testAddressesWithAllParameters() {
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withLocale("en_GB")
                .withQuantity(20)
                .withSeed(12345)
                .withParam("_country_code", "GB")
                .execute();
        
        validateSuccessResponse(response);
        validateResponseQuantity(response, 20);
        validateResponseSchema(response);
    }

    @Test(priority = 12, dataProvider = "getInvalidCountryCodes")
    public void testAddressesWithInvalidCountryCode(String countryCode) {
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(5)
                .withParam("_country_code", countryCode)
                .execute();
        
        logger.info("Invalid country code '{}': status={}", countryCode, response.getStatusCode());
    }

    @Test(priority = 13)
    public void testAddressesDataStructure() {
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(1)
                .execute();
        
        validateSuccessResponse(response);
        
        Map<String, Object> address = response.jsonPath().getMap("data[0]");
        
        Assert.assertNotNull(address.get("id"));
        Assert.assertNotNull(address.get("street"));
        Assert.assertNotNull(address.get("streetName"));
        Assert.assertNotNull(address.get("buildingNumber"));
        Assert.assertNotNull(address.get("city"));
        Assert.assertNotNull(address.get("zipcode"));
        Assert.assertNotNull(address.get("country"));
        Assert.assertNotNull(address.get("country_code"));
        Assert.assertNotNull(address.get("latitude"));
        Assert.assertNotNull(address.get("longitude"));
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

    @org.testng.annotations.DataProvider(name = "getValidCountryCodes")
    public Object[][] getValidCountryCodes() {
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
