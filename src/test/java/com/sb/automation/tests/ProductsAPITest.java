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

    @Test(priority = 1)
    public void testDefaultProductsRequest() {
        Response response = testDefaultRequest();
        validateResponseQuantity(response, 10);
    }

    @Test(priority = 2)
    public void testProductsSchemaValidation() {
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(5)
                .execute();
        
        validateSuccessResponse(response);
        validateResponseSchema(response);
    }

    @Test(priority = 3, dataProvider = "getLocales")
    public void testProductsWithDifferentLocales(String locale) {
        Response response = testWithLocale(locale);
        validateResponseQuantity(response, 10);
    }

    @Test(priority = 4, dataProvider = "getValidQuantities")
    public void testProductsWithValidQuantities(int quantity) {
        Response response = testWithQuantity(quantity);
        validateSuccessResponse(response);
        validateResponseQuantity(response, quantity);
    }

    @Test(priority = 5)
    public void testProductsWithBoundaryQuantities() {
        Response response1 = testWithQuantity(1);
        validateSuccessResponse(response1);
        validateResponseQuantity(response1, 1);
        
        Response response2 = testWithQuantity(1000);
        validateSuccessResponse(response2);
        validateResponseQuantity(response2, 1000);
    }

    @Test(priority = 6, dataProvider = "getInvalidQuantities")
    public void testProductsWithInvalidQuantities(int quantity) {
        Response response = testWithQuantity(quantity);
        logger.info("Invalid quantity {}: status={}", quantity, response.getStatusCode());
    }

    @Test(priority = 7)
    public void testProductsWithSeed() {
        int testSeed = ((Number) testSeeds.get(0)).intValue();
        Response response = testWithSeed(testSeed);
        validateSuccessResponse(response);
    }

    @Test(priority = 8)
    public void testProductsWithAllCommonParameters() {
        String locale = sampleLocales.get(0);
        int seed = ((Number) testSeeds.get(0)).intValue();
        
        Response response = testWithAllCommonParams(locale, 25, seed);
        validateResponseSchema(response);
    }

    @Test(priority = 9, dataProvider = "getValidPriceMin")
    public void testProductsWithValidPriceMin(double priceMin) {
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(10)
                .withParam("_price_min", String.valueOf(priceMin))
                .execute();
        
        validateSuccessResponse(response);
        
        List<Map<String, Object>> products = response.jsonPath().getList("data");
        for (Map<String, Object> product : products) {
            double actualPrice = ((Number) product.get("price")).doubleValue();
            Assert.assertTrue(actualPrice >= priceMin,
                    String.format("Price %.2f should be >= %.2f", actualPrice, priceMin));
        }
    }

    @Test(priority = 10)
    public void testProductsWithBoundaryPriceMin() {
        for (Object priceMinObj : boundaryPriceMin) {
            double priceMin = ((Number) priceMinObj).doubleValue();
            
            Response response = new APIClient.RequestBuilder(getEndpoint())
                    .withQuantity(5)
                    .withParam("_price_min", String.valueOf(priceMin))
                    .execute();
            
            validateSuccessResponse(response);
        }
    }

    @Test(priority = 11, dataProvider = "getValidPriceMax")
    public void testProductsWithValidPriceMax(double priceMax) {
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(10)
                .withParam("_price_max", String.valueOf(priceMax))
                .withParam("_taxes", "0")
                .execute();
        
        validateSuccessResponse(response);
        
        List<Map<String, Object>> products = response.jsonPath().getList("data");
        for (Map<String, Object> product : products) {
            double actualPrice = ((Number) product.get("price")).doubleValue();
            double tolerance = priceMax * 0.25;
            Assert.assertTrue(actualPrice <= priceMax + tolerance,
                    String.format("Price %.2f should be <= %.2f", actualPrice, priceMax));
        }
    }

    @Test(priority = 12)
    public void testProductsWithBoundaryPriceMax() {
        for (Object priceMaxObj : boundaryPriceMax) {
            double priceMax = ((Number) priceMaxObj).doubleValue();
            
            Response response = new APIClient.RequestBuilder(getEndpoint())
                    .withQuantity(5)
                    .withParam("_price_max", String.valueOf(priceMax))
                    .execute();
            
            validateSuccessResponse(response);
        }
    }

    @Test(priority = 13, dataProvider = "getPriceRanges")
    public void testProductsWithPriceRange(double priceMin, double priceMax) {
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(20)
                .withParam("_price_min", String.valueOf(priceMin))
                .withParam("_price_max", String.valueOf(priceMax))
                .withParam("_taxes", "0")
                .execute();
        
        validateSuccessResponse(response);
        
        List<Map<String, Object>> products = response.jsonPath().getList("data");
        for (Map<String, Object> product : products) {
            double actualPrice = ((Number) product.get("price")).doubleValue();
            double tolerance = priceMax * 0.25;
            Assert.assertTrue(actualPrice >= priceMin * 0.95 && actualPrice <= priceMax + tolerance,
                    String.format("Price %.2f outside range [%.2f, %.2f]", actualPrice, priceMin, priceMax));
        }
    }

    @Test(priority = 14, dataProvider = "getInvalidPrices")
    public void testProductsWithInvalidPrices(double invalidPrice) {
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(5)
                .withParam("_price_min", String.valueOf(invalidPrice))
                .execute();
        
        logger.info("Invalid price {}: status={}", invalidPrice, response.getStatusCode());
    }

    @Test(priority = 15, dataProvider = "getValidTaxes")
    public void testProductsWithValidTaxes(int taxes) {
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(10)
                .withParam("_taxes", String.valueOf(taxes))
                .execute();
        
        validateSuccessResponse(response);
    }

    @Test(priority = 16)
    public void testProductsWithDefaultTax() {
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(5)
                .execute();
        
        validateSuccessResponse(response);
    }

    @Test(priority = 17)
    public void testProductsWithBoundaryTaxes() {
        for (Object taxObj : boundaryTaxes) {
            int tax = ((Number) taxObj).intValue();
            
            Response response = new APIClient.RequestBuilder(getEndpoint())
                    .withQuantity(5)
                    .withParam("_taxes", String.valueOf(tax))
                    .execute();
            
            validateSuccessResponse(response);
        }
    }

    @Test(priority = 18, dataProvider = "getInvalidTaxes")
    public void testProductsWithInvalidTaxes(int taxes) {
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(5)
                .withParam("_taxes", String.valueOf(taxes))
                .execute();
        
        logger.info("Invalid taxes {}: status={}", taxes, response.getStatusCode());
    }

    @Test(priority = 19, dataProvider = "getValidCategoriesTypes")
    public void testProductsWithValidCategoriesTypes(String categoryType) {
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(10)
                .withParam("_categories_type", categoryType)
                .execute();
        
        validateSuccessResponse(response);
        
        List<Map<String, Object>> products = response.jsonPath().getList("data");
        for (Map<String, Object> product : products) {
            Object categoriesObj = product.get("categories");
            Assert.assertNotNull(categoriesObj);
            Assert.assertTrue(categoriesObj instanceof List);
            
            @SuppressWarnings("unchecked")
            List<Object> categories = (List<Object>) categoriesObj;
            Assert.assertFalse(categories.isEmpty());
            
            for (Object category : categories) {
                switch (categoryType) {
                    case "integer":
                        Assert.assertTrue(category instanceof Integer || category instanceof Number);
                        break;
                    case "string":
                        Assert.assertTrue(category instanceof String);
                        String str = (String) category;
                        Assert.assertFalse(str.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"));
                        break;
                    case "uuid":
                        Assert.assertTrue(category instanceof String);
                        String uuid = (String) category;
                        Assert.assertTrue(uuid.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"));
                        break;
                }
            }
        }
    }

    @Test(priority = 20)
    public void testProductsWithDefaultCategoriesType() {
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(5)
                .execute();
        
        validateSuccessResponse(response);
        
        List<Map<String, Object>> products = response.jsonPath().getList("data");
        for (Map<String, Object> product : products) {
            Object categoriesObj = product.get("categories");
            Assert.assertNotNull(categoriesObj);
            Assert.assertTrue(categoriesObj instanceof List);
            
            @SuppressWarnings("unchecked")
            List<Object> categories = (List<Object>) categoriesObj;
            Assert.assertFalse(categories.isEmpty());
            
            for (Object category : categories) {
                Assert.assertTrue(category instanceof Integer || category instanceof Number);
            }
        }
    }

    @Test(priority = 21, dataProvider = "getInvalidCategoriesTypes")
    public void testProductsWithInvalidCategoriesTypes(String categoryType) {
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(5)
                .withParam("_categories_type", categoryType)
                .execute();
        
        logger.info("Invalid category type '{}': status={}", categoryType, response.getStatusCode());
    }

    @Test(priority = 22)
    public void testProductsWithAllSpecificParameters() {
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(20)
                .withParam("_price_min", "50.00")
                .withParam("_price_max", "500.00")
                .withParam("_taxes", "10")
                .withParam("_categories_type", "uuid")
                .execute();
        
        validateSuccessResponse(response);
        validateResponseQuantity(response, 20);
        
        List<Map<String, Object>> products = response.jsonPath().getList("data");
        for (Map<String, Object> product : products) {
            double actualPrice = ((Number) product.get("price")).doubleValue();
            Assert.assertTrue(actualPrice >= 47.5 && actualPrice <= 625.0);
            
            @SuppressWarnings("unchecked")
            List<Object> categories = (List<Object>) product.get("categories");
            Assert.assertFalse(categories.isEmpty());
            
            for (Object category : categories) {
                Assert.assertTrue(category instanceof String);
                String uuid = (String) category;
                Assert.assertTrue(uuid.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"));
            }
        }
    }

    @Test(priority = 23)
    public void testProductsWithAllParameters() {
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withLocale("en_US")
                .withQuantity(30)
                .withSeed(54321)
                .withParam("_price_min", "20.00")
                .withParam("_price_max", "1000.00")
                .withParam("_taxes", "15")
                .withParam("_categories_type", "string")
                .execute();
        
        validateSuccessResponse(response);
        validateResponseQuantity(response, 30);
        validateResponseSchema(response);
    }

    @Test(priority = 24)
    public void testProductsDataStructure() {
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(1)
                .execute();
        
        validateSuccessResponse(response);
        
        Map<String, Object> product = response.jsonPath().getMap("data[0]");
        
        Assert.assertNotNull(product.get("id"));
        Assert.assertNotNull(product.get("name"));
        Assert.assertNotNull(product.get("description"));
        Assert.assertNotNull(product.get("price"));
        Assert.assertNotNull(product.get("categories"));
        Assert.assertNotNull(product.get("ean"));
        Assert.assertNotNull(product.get("image"));
        
        Assert.assertTrue(product.get("categories") instanceof List);
    }

    @Test(priority = 25)
    public void testProductsPriceFormat() {
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(10)
                .execute();
        
        validateSuccessResponse(response);
        
        List<Map<String, Object>> products = response.jsonPath().getList("data");
        for (Map<String, Object> product : products) {
            Object priceObj = product.get("price");
            Assert.assertNotNull(priceObj);
            double price = ((Number) priceObj).doubleValue();
            Assert.assertTrue(price > 0);
        }
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
