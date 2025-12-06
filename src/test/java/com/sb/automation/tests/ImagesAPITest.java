package com.sb.automation.tests;

import com.sb.automation.config.APIConfig;
import com.sb.automation.utils.APIClient;
import com.sb.automation.utils.TestDataReader;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class ImagesAPITest extends BaseAPITest {
    
    private static final String IMAGE_PARAMS_DATA = "src/test/resources/testdata/image_params.json";
    private List<String> validTypes;
    private List<String> invalidTypes;
    private List<Object> validWidths;
    private List<Object> validHeights;
    private List<Object> boundaryWidths;
    private List<Object> boundaryHeights;
    private List<Object> invalidDimensions;
    private List<Object> dimensionCombinations;

    @Override
    protected String getEndpoint() {
        return APIConfig.IMAGES_ENDPOINT;
    }

    @Override
    protected String getSchemaPath() {
        return "src/test/resources/schemas/images-schema.json";
    }

    @BeforeClass
    @SuppressWarnings("unchecked")
    public void setup() {
        validTypes = (List<String>) (List<?>) TestDataReader.getTestDataList(IMAGE_PARAMS_DATA, "valid_types");
        invalidTypes = (List<String>) (List<?>) TestDataReader.getTestDataList(IMAGE_PARAMS_DATA, "invalid_types");
        validWidths = TestDataReader.getTestDataList(IMAGE_PARAMS_DATA, "valid_widths");
        validHeights = TestDataReader.getTestDataList(IMAGE_PARAMS_DATA, "valid_heights");
        boundaryWidths = TestDataReader.getTestDataList(IMAGE_PARAMS_DATA, "boundary_widths");
        boundaryHeights = TestDataReader.getTestDataList(IMAGE_PARAMS_DATA, "boundary_heights");
        invalidDimensions = TestDataReader.getTestDataList(IMAGE_PARAMS_DATA, "invalid_dimensions");
        dimensionCombinations = TestDataReader.getTestDataList(IMAGE_PARAMS_DATA, "dimension_combinations");
    }

    @Test(priority = 1)
    public void testDefaultImagesRequest() {
        Response response = testDefaultRequest();
        validateResponseQuantity(response, 10);
    }

    @Test(priority = 2)
    public void testImagesSchemaValidation() {
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(5)
                .execute();
        
        validateSuccessResponse(response);
        validateResponseSchema(response);
    }

    @Test(priority = 3, dataProvider = "getLocales")
    public void testImagesWithDifferentLocales(String locale) {
        Response response = testWithLocale(locale);
        validateResponseQuantity(response, 10);
    }

    @Test(priority = 4, dataProvider = "getValidQuantities")
    public void testImagesWithValidQuantities(int quantity) {
        Response response = testWithQuantity(quantity);
        validateSuccessResponse(response);
        validateResponseQuantity(response, quantity);
    }

    @Test(priority = 5)
    public void testImagesWithBoundaryQuantities() {
        Response response1 = testWithQuantity(1);
        validateSuccessResponse(response1);
        validateResponseQuantity(response1, 1);
        
        Response response2 = testWithQuantity(1000);
        validateSuccessResponse(response2);
        validateResponseQuantity(response2, 1000);
    }

    @Test(priority = 6, dataProvider = "getInvalidQuantities")
    public void testImagesWithInvalidQuantities(int quantity) {
        Response response = testWithQuantity(quantity);
        logger.info("Invalid quantity {}: status={}", quantity, response.getStatusCode());
    }

    @Test(priority = 7)
    public void testImagesWithSeed() {
        int testSeed = ((Number) testSeeds.get(0)).intValue();
        Response response = testWithSeed(testSeed);
        validateSuccessResponse(response);
    }

    @Test(priority = 8)
    public void testImagesWithAllCommonParameters() {
        String locale = sampleLocales.get(0);
        int seed = ((Number) testSeeds.get(0)).intValue();
        
        Response response = testWithAllCommonParams(locale, 25, seed);
        validateResponseSchema(response);
    }

    @Test(priority = 9, dataProvider = "getValidTypes")
    public void testImagesWithValidType(String type) {
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(10)
                .withParam("_type", type)
                .execute();
        
        validateSuccessResponse(response);
        
        List<Map<String, Object>> images = response.jsonPath().getList("data");
        Assert.assertFalse(images.isEmpty());
        
        for (Map<String, Object> image : images) {
            Assert.assertNotNull(image.get("url"));
            Assert.assertNotNull(image.get("title"));
            Assert.assertNotNull(image.get("description"));
        }
    }

    @Test(priority = 10)
    public void testImagesWithDefaultType() {
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(5)
                .execute();
        
        validateSuccessResponse(response);
    }

    @Test(priority = 11, dataProvider = "getInvalidTypes")
    public void testImagesWithInvalidType(String type) {
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(5)
                .withParam("_type", type)
                .execute();
        
        logger.info("Invalid type '{}': status={}", type, response.getStatusCode());
    }

    @Test(priority = 12, dataProvider = "getValidWidths")
    public void testImagesWithValidWidth(int width) {
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(1)
                .withParam("_width", String.valueOf(width))
                .execute();
        
        validateSuccessResponse(response);
        
        List<Map<String, Object>> images = response.jsonPath().getList("data");
        if (!images.isEmpty()) {
            String imageUrl = (String) images.get(0).get("url");
            validateImageDimensions(imageUrl, width, null);
        }
    }

    @Test(priority = 13)
    public void testImagesWithDefaultWidth() {
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(1)
                .execute();
        
        validateSuccessResponse(response);
        
        List<Map<String, Object>> images = response.jsonPath().getList("data");
        if (!images.isEmpty()) {
            String imageUrl = (String) images.get(0).get("url");
            validateImageDimensions(imageUrl, 640, null);
        }
    }

    @Test(priority = 14)
    public void testImagesWithBoundaryWidths() {
        for (Object widthObj : boundaryWidths) {
            int width = ((Number) widthObj).intValue();
            
            Response response = new APIClient.RequestBuilder(getEndpoint())
                    .withQuantity(1)
                    .withParam("_width", String.valueOf(width))
                    .execute();
            
            validateSuccessResponse(response);
        }
    }

    @Test(priority = 15, dataProvider = "getValidHeights")
    public void testImagesWithValidHeight(int height) {
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(1)
                .withParam("_height", String.valueOf(height))
                .execute();
        
        validateSuccessResponse(response);
        
        List<Map<String, Object>> images = response.jsonPath().getList("data");
        if (!images.isEmpty()) {
            String imageUrl = (String) images.get(0).get("url");
            validateImageDimensions(imageUrl, null, height);
        }
    }

    @Test(priority = 16)
    public void testImagesWithDefaultHeight() {
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(1)
                .execute();
        
        validateSuccessResponse(response);
        
        List<Map<String, Object>> images = response.jsonPath().getList("data");
        if (!images.isEmpty()) {
            String imageUrl = (String) images.get(0).get("url");
            validateImageDimensions(imageUrl, null, 480);
        }
    }

    @Test(priority = 17)
    public void testImagesWithBoundaryHeights() {
        for (Object heightObj : boundaryHeights) {
            int height = ((Number) heightObj).intValue();
            
            Response response = new APIClient.RequestBuilder(getEndpoint())
                    .withQuantity(1)
                    .withParam("_height", String.valueOf(height))
                    .execute();
            
            validateSuccessResponse(response);
        }
    }

    @Test(priority = 18, dataProvider = "getDimensionCombinations", singleThreaded = true)
    public void testImagesWithDimensionCombination(int width, int height) {
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(1)
                .withParam("_width", String.valueOf(width))
                .withParam("_height", String.valueOf(height))
                .execute();
        
        validateSuccessResponse(response);
        
        List<Map<String, Object>> images = response.jsonPath().getList("data");
        if (!images.isEmpty()) {
            String imageUrl = (String) images.get(0).get("url");
            validateImageDimensions(imageUrl, width, height);
        }
    }

    @Test(priority = 19, dataProvider = "getInvalidDimensions")
    public void testImagesWithInvalidDimensions(int dimension) {
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(2)
                .withParam("_width", String.valueOf(dimension))
                .withParam("_height", String.valueOf(dimension))
                .execute();
        
        logger.info("Invalid dimension {}: status={}", dimension, response.getStatusCode());
    }

    @Test(priority = 20)
    public void testImagesWithAllParameters() {
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withLocale("en_US")
                .withQuantity(2)
                .withSeed(12345)
                .withParam("_type", "any")
                .withParam("_width", "800")
                .withParam("_height", "600")
                .execute();
        
        validateSuccessResponse(response);
        validateResponseQuantity(response, 2);
        validateResponseSchema(response);
        
        List<Map<String, Object>> images = response.jsonPath().getList("data");
        if (!images.isEmpty()) {
            String imageUrl = (String) images.get(0).get("url");
            validateImageDimensions(imageUrl, 800, 600);
        }
    }

    @Test(priority = 21)
    public void testImagesDataStructure() {
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(1)
                .execute();
        
        validateSuccessResponse(response);
        
        Map<String, Object> image = response.jsonPath().getMap("data[0]");
        
        Assert.assertNotNull(image.get("title"));
        Assert.assertNotNull(image.get("description"));
        Assert.assertNotNull(image.get("url"));
        
        String url = (String) image.get("url");
        Assert.assertTrue(url.startsWith("http://") || url.startsWith("https://"));
    }

    @Test(priority = 22)
    public void testImagesURLAccessibility() {
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(2)
                .execute();
        
        validateSuccessResponse(response);
        
        List<Map<String, Object>> images = response.jsonPath().getList("data");
        int accessibleCount = 0;
        
        for (Map<String, Object> image : images) {
            String imageUrl = (String) image.get("url");
            try {
                @SuppressWarnings("deprecation")
                URL url = new URL(imageUrl);
                BufferedImage img = ImageIO.read(url);
                if (img != null) {
                    accessibleCount++;
                }
            } catch (IOException e) {
                logger.warn("Image not accessible: {}", imageUrl);
            }
        }
        
        Assert.assertTrue(accessibleCount > 0);
    }

    private void validateImageDimensions(String imageUrl, Integer expectedWidth, Integer expectedHeight) {
        try {
            @SuppressWarnings("deprecation")
            URL url = new URL(imageUrl);
            BufferedImage image = ImageIO.read(url);
            
            if (image == null) {
                logger.warn("Could not read image: {}", imageUrl);
                return;
            }
            
            int actualWidth = image.getWidth();
            int actualHeight = image.getHeight();
            
            if (expectedWidth != null) {
                int tolerance = (int) (expectedWidth * 0.1);
                Assert.assertTrue(Math.abs(actualWidth - expectedWidth) <= tolerance,
                        String.format("Width mismatch: expected %d, got %d", expectedWidth, actualWidth));
            }
            
            if (expectedHeight != null) {
                int tolerance = (int) (expectedHeight * 0.1);
                Assert.assertTrue(Math.abs(actualHeight - expectedHeight) <= tolerance,
                        String.format("Height mismatch: expected %d, got %d", expectedHeight, actualHeight));
            }
            
        } catch (IOException e) {
            logger.warn("Failed to validate dimensions: {}", imageUrl);
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

    @org.testng.annotations.DataProvider(name = "getValidTypes")
    public Object[][] getValidTypes() {
        return validTypes.stream()
                .map(type -> new Object[]{type})
                .toArray(Object[][]::new);
    }

    @org.testng.annotations.DataProvider(name = "getInvalidTypes")
    public Object[][] getInvalidTypes() {
        return invalidTypes.stream()
                .map(type -> new Object[]{type})
                .toArray(Object[][]::new);
    }

    @org.testng.annotations.DataProvider(name = "getValidWidths")
    public Object[][] getValidWidths() {
        return validWidths.stream()
                .limit(3)
                .map(width -> new Object[]{((Number) width).intValue()})
                .toArray(Object[][]::new);
    }

    @org.testng.annotations.DataProvider(name = "getValidHeights")
    public Object[][] getValidHeights() {
        return validHeights.stream()
                .limit(3)
                .map(height -> new Object[]{((Number) height).intValue()})
                .toArray(Object[][]::new);
    }

    @org.testng.annotations.DataProvider(name = "getInvalidDimensions")
    public Object[][] getInvalidDimensions() {
        return invalidDimensions.stream()
                .map(dim -> new Object[]{((Number) dim).intValue()})
                .toArray(Object[][]::new);
    }

    @org.testng.annotations.DataProvider(name = "getDimensionCombinations")
    public Object[][] getDimensionCombinations() {
        return dimensionCombinations.stream()
                .map(combObj -> {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> comb = (Map<String, Object>) combObj;
                    int width = ((Number) comb.get("width")).intValue();
                    int height = ((Number) comb.get("height")).intValue();
                    return new Object[]{width, height};
                })
                .toArray(Object[][]::new);
    }
}
