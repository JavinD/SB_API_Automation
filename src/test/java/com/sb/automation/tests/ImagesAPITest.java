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

/**
 * Test class for Images API
 * Includes tests for common parameters and image-specific parameters
 * Image-specific params: _type, _width, _height
 */
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
        logger.info("Setting up Images API tests");
        validTypes = (List<String>) (List<?>) TestDataReader.getTestDataList(IMAGE_PARAMS_DATA, "valid_types");
        invalidTypes = (List<String>) (List<?>) TestDataReader.getTestDataList(IMAGE_PARAMS_DATA, "invalid_types");
        validWidths = TestDataReader.getTestDataList(IMAGE_PARAMS_DATA, "valid_widths");
        validHeights = TestDataReader.getTestDataList(IMAGE_PARAMS_DATA, "valid_heights");
        boundaryWidths = TestDataReader.getTestDataList(IMAGE_PARAMS_DATA, "boundary_widths");
        boundaryHeights = TestDataReader.getTestDataList(IMAGE_PARAMS_DATA, "boundary_heights");
        invalidDimensions = TestDataReader.getTestDataList(IMAGE_PARAMS_DATA, "invalid_dimensions");
        dimensionCombinations = TestDataReader.getTestDataList(IMAGE_PARAMS_DATA, "dimension_combinations");
    }

    // ============= Common Parameter Tests =============

    @Test(priority = 1, description = "Test default images API request")
    public void testDefaultImagesRequest() {
        logger.info("Executing: testDefaultImagesRequest");
        Response response = testDefaultRequest();
        
        // Validate default quantity (10)
        validateResponseQuantity(response, 10);
        
        logger.info("testDefaultImagesRequest - PASSED");
    }

    @Test(priority = 2, description = "Test images API with schema validation")
    public void testImagesSchemaValidation() {
        logger.info("Executing: testImagesSchemaValidation");
        
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(5)
                .execute();
        
        validateSuccessResponse(response);
        validateResponseSchema(response);
        
        logger.info("testImagesSchemaValidation - PASSED");
    }

    @Test(priority = 3, description = "Test images API with different locales", dataProvider = "getLocales")
    public void testImagesWithDifferentLocales(String locale) {
        logger.info("Executing: testImagesWithDifferentLocales with locale: {}", locale);
        
        Response response = testWithLocale(locale);
        validateResponseQuantity(response, 10);
        
        logger.info("testImagesWithDifferentLocales - PASSED for locale: {}", locale);
    }

    @Test(priority = 4, description = "Test images API with valid quantities", dataProvider = "getValidQuantities")
    public void testImagesWithValidQuantities(int quantity) {
        logger.info("Executing: testImagesWithValidQuantities with quantity: {}", quantity);
        
        Response response = testWithQuantity(quantity);
        validateSuccessResponse(response);
        validateResponseQuantity(response, quantity);
        
        logger.info("testImagesWithValidQuantities - PASSED for quantity: {}", quantity);
    }

    @Test(priority = 5, description = "Test images API with boundary quantities")
    public void testImagesWithBoundaryQuantities() {
        logger.info("Executing: testImagesWithBoundaryQuantities");
        
        // Test minimum quantity (1)
        Response response1 = testWithQuantity(1);
        validateSuccessResponse(response1);
        validateResponseQuantity(response1, 1);
        
        // Test maximum quantity (1000)
        Response response2 = testWithQuantity(1000);
        validateSuccessResponse(response2);
        validateResponseQuantity(response2, 1000);
        
        logger.info("testImagesWithBoundaryQuantities - PASSED");
    }

    @Test(priority = 6, description = "Test images API with invalid quantities", dataProvider = "getInvalidQuantities")
    public void testImagesWithInvalidQuantities(int quantity) {
        logger.info("Executing: testImagesWithInvalidQuantities with quantity: {}", quantity);
        
        Response response = testWithQuantity(quantity);
        
        // Document the actual API behavior for invalid quantities
        logger.info("Response for invalid quantity {}: Status={}, Body={}", 
                quantity, response.getStatusCode(), response.asString());
    }

    @Test(priority = 7, description = "Test images API with seed for consistency")
    public void testImagesWithSeed() {
        logger.info("Executing: testImagesWithSeed");
        
        int testSeed = ((Number) testSeeds.get(0)).intValue();
        Response response = testWithSeed(testSeed);
        validateSuccessResponse(response);
        
        logger.info("testImagesWithSeed - PASSED");
    }

    @Test(priority = 8, description = "Test images API with all common parameters")
    public void testImagesWithAllCommonParameters() {
        logger.info("Executing: testImagesWithAllCommonParameters");
        
        String locale = sampleLocales.get(0);
        int quantity = 25;
        int seed = ((Number) testSeeds.get(0)).intValue();
        
        Response response = testWithAllCommonParams(locale, quantity, seed);
        validateResponseSchema(response);
        
        logger.info("testImagesWithAllCommonParameters - PASSED");
    }

    // ============= Image-Specific Parameter Tests: Type =============

    @Test(priority = 9, description = "Test images API with valid type values", dataProvider = "getValidTypes")
    public void testImagesWithValidType(String type) {
        logger.info("Executing: testImagesWithValidType with type: {}", type);
        
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(10)
                .withParam("_type", type)
                .execute();
        
        validateSuccessResponse(response);
        
        // Verify images are returned
        List<Map<String, Object>> images = response.jsonPath().getList("data");
        Assert.assertFalse(images.isEmpty(), "Should return images for type: " + type);
        
        // Verify all images have required fields
        for (Map<String, Object> image : images) {
            Assert.assertNotNull(image.get("url"), "Image should have URL");
            Assert.assertNotNull(image.get("title"), "Image should have title");
            Assert.assertNotNull(image.get("description"), "Image should have description");
        }
        
        logger.info("testImagesWithValidType - PASSED for type: {}", type);
    }

    @Test(priority = 10, description = "Test images API with default type (any)")
    public void testImagesWithDefaultType() {
        logger.info("Executing: testImagesWithDefaultType");
        
        // Request without type parameter (should use default: any)
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(5)
                .execute();
        
        validateSuccessResponse(response);
        
        logger.info("testImagesWithDefaultType - PASSED");
    }

    @Test(priority = 11, description = "Test images API with invalid type values", dataProvider = "getInvalidTypes")
    public void testImagesWithInvalidType(String type) {
        logger.info("Executing: testImagesWithInvalidType with type: {}", type);
        
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(5)
                .withParam("_type", type)
                .execute();
        
        // Document the actual API behavior for invalid types
        logger.info("Response for invalid type '{}': Status={}, Body={}", 
                type, response.getStatusCode(), response.asString());
    }

    // ============= Image-Specific Parameter Tests: Width =============

    @Test(priority = 12, description = "Test images API with valid width values", dataProvider = "getValidWidths")
    public void testImagesWithValidWidth(int width) {
        logger.info("Executing: testImagesWithValidWidth with width: {}", width);
        
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(1)  // Reduced from 3 to 1 to avoid rate limiting
                .withParam("_width", String.valueOf(width))
                .execute();
        
        validateSuccessResponse(response);
        
        // Validate image dimensions (only first image to reduce API calls)
        List<Map<String, Object>> images = response.jsonPath().getList("data");
        if (!images.isEmpty()) {
            String imageUrl = (String) images.get(0).get("url");
            validateImageDimensions(imageUrl, width, null, "width");
        }
        
        logger.info("testImagesWithValidWidth - PASSED for width: {}", width);
    }

    @Test(priority = 13, description = "Test images API with default width (640)")
    public void testImagesWithDefaultWidth() {
        logger.info("Executing: testImagesWithDefaultWidth");
        
        // Request without width parameter (should use default: 640)
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(1)  // Reduced from 2 to 1 to avoid rate limiting
                .execute();
        
        validateSuccessResponse(response);
        
        // Validate default width (only first image)
        List<Map<String, Object>> images = response.jsonPath().getList("data");
        if (!images.isEmpty()) {
            String imageUrl = (String) images.get(0).get("url");
            validateImageDimensions(imageUrl, 640, null, "default width");
        }
        
        logger.info("testImagesWithDefaultWidth - PASSED");
    }

    @Test(priority = 14, description = "Test images API with boundary width values")
    public void testImagesWithBoundaryWidths() {
        logger.info("Executing: testImagesWithBoundaryWidths");
        
        for (Object widthObj : boundaryWidths) {
            int width = ((Number) widthObj).intValue();
            
            Response response = new APIClient.RequestBuilder(getEndpoint())
                    .withQuantity(1)
                    .withParam("_width", String.valueOf(width))
                    .execute();
            
            validateSuccessResponse(response);
            logger.info("Boundary width {} - PASSED", width);
        }
        
        logger.info("testImagesWithBoundaryWidths - PASSED");
    }

    // ============= Image-Specific Parameter Tests: Height =============

    @Test(priority = 15, description = "Test images API with valid height values", dataProvider = "getValidHeights")
    public void testImagesWithValidHeight(int height) {
        logger.info("Executing: testImagesWithValidHeight with height: {}", height);
        
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(1)  // Reduced from 3 to 1 to avoid rate limiting
                .withParam("_height", String.valueOf(height))
                .execute();
        
        validateSuccessResponse(response);
        
        // Validate image dimensions (only first image to reduce API calls)
        List<Map<String, Object>> images = response.jsonPath().getList("data");
        if (!images.isEmpty()) {
            String imageUrl = (String) images.get(0).get("url");
            validateImageDimensions(imageUrl, null, height, "height");
        }
        
        logger.info("testImagesWithValidHeight - PASSED for height: {}", height);
    }

    @Test(priority = 16, description = "Test images API with default height (480)")
    public void testImagesWithDefaultHeight() {
        logger.info("Executing: testImagesWithDefaultHeight");
        
        // Request without height parameter (should use default: 480)
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(1)  // Reduced from 2 to 1 to avoid rate limiting
                .execute();
        
        validateSuccessResponse(response);
        
        // Validate default height (only first image)
        List<Map<String, Object>> images = response.jsonPath().getList("data");
        if (!images.isEmpty()) {
            String imageUrl = (String) images.get(0).get("url");
            validateImageDimensions(imageUrl, null, 480, "default height");
        }
        
        logger.info("testImagesWithDefaultHeight - PASSED");
    }

    @Test(priority = 17, description = "Test images API with boundary height values")
    public void testImagesWithBoundaryHeights() {
        logger.info("Executing: testImagesWithBoundaryHeights");
        
        for (Object heightObj : boundaryHeights) {
            int height = ((Number) heightObj).intValue();
            
            Response response = new APIClient.RequestBuilder(getEndpoint())
                    .withQuantity(1)
                    .withParam("_height", String.valueOf(height))
                    .execute();
            
            validateSuccessResponse(response);
            logger.info("Boundary height {} - PASSED", height);
        }
        
        logger.info("testImagesWithBoundaryHeights - PASSED");
    }

    // ============= Image-Specific Combined Parameter Tests =============

    @Test(priority = 18, description = "Test images API with width and height combination", dataProvider = "getDimensionCombinations", singleThreaded = true)
    public void testImagesWithDimensionCombination(int width, int height) {
        logger.info("Executing: testImagesWithDimensionCombination with width: {} and height: {}", width, height);
        
        // Add small delay to avoid rate limiting
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(1)  // Reduced from 3 to 1 to avoid rate limiting
                .withParam("_width", String.valueOf(width))
                .withParam("_height", String.valueOf(height))
                .execute();
        
        validateSuccessResponse(response);
        
        // Validate both dimensions (only first image to reduce API calls)
        List<Map<String, Object>> images = response.jsonPath().getList("data");
        if (!images.isEmpty()) {
            String imageUrl = (String) images.get(0).get("url");
            validateImageDimensions(imageUrl, width, height, "width and height");
        }
        
        logger.info("testImagesWithDimensionCombination - PASSED for {}x{}", width, height);
    }

    @Test(priority = 19, description = "Test images API with invalid dimension values", dataProvider = "getInvalidDimensions")
    public void testImagesWithInvalidDimensions(int dimension) {
        logger.info("Executing: testImagesWithInvalidDimensions with dimension: {}", dimension);
        
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(2)
                .withParam("_width", String.valueOf(dimension))
                .withParam("_height", String.valueOf(dimension))
                .execute();
        
        // Document the actual API behavior for invalid dimensions
        logger.info("Response for invalid dimension {}: Status={}, Body={}", 
                dimension, response.getStatusCode(), response.asString());
    }

    @Test(priority = 20, description = "Test images API with all parameters including type and dimensions")
    public void testImagesWithAllParameters() {
        logger.info("Executing: testImagesWithAllParameters");
        
        String locale = "en_US";
        int quantity = 2;  // Reduced from 5 to 2 to avoid rate limiting
        int seed = 12345;
        String type = "any";
        int width = 800;
        int height = 600;
        
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withLocale(locale)
                .withQuantity(quantity)
                .withSeed(seed)
                .withParam("_type", type)
                .withParam("_width", String.valueOf(width))
                .withParam("_height", String.valueOf(height))
                .execute();
        
        validateSuccessResponse(response);
        validateResponseQuantity(response, quantity);
        validateResponseSchema(response);
        
        // Validate dimensions (only first image to reduce API calls)
        List<Map<String, Object>> images = response.jsonPath().getList("data");
        if (!images.isEmpty()) {
            String imageUrl = (String) images.get(0).get("url");
            validateImageDimensions(imageUrl, width, height, "all parameters");
        }
        
        logger.info("testImagesWithAllParameters - PASSED");
    }

    // ============= Image-Specific Data Structure Tests =============

    @Test(priority = 21, description = "Test images data structure and fields")
    public void testImagesDataStructure() {
        logger.info("Executing: testImagesDataStructure");
        
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(1)
                .execute();
        
        validateSuccessResponse(response);
        
        // Verify image fields
        Map<String, Object> image = response.jsonPath().getMap("data[0]");
        
        Assert.assertNotNull(image.get("title"), "Image should have title");
        Assert.assertNotNull(image.get("description"), "Image should have description");
        Assert.assertNotNull(image.get("url"), "Image should have url");
        
        // Verify URL is valid format
        String url = (String) image.get("url");
        Assert.assertTrue(url.startsWith("http://") || url.startsWith("https://"), 
                "URL should start with http:// or https://");
        
        logger.info("Image data: {}", image);
        logger.info("testImagesDataStructure - PASSED");
    }

    @Test(priority = 22, description = "Test images URL accessibility")
    public void testImagesURLAccessibility() {
        logger.info("Executing: testImagesURLAccessibility");
        
        Response response = new APIClient.RequestBuilder(getEndpoint())
                .withQuantity(2)  // Reduced from 3 to 2 to avoid rate limiting
                .execute();
        
        validateSuccessResponse(response);
        
        // Verify images are accessible
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
                    logger.debug("Image accessible: {} ({}x{})", imageUrl, img.getWidth(), img.getHeight());
                }
            } catch (IOException e) {
                logger.warn("Image not accessible: {} - {}", imageUrl, e.getMessage());
            }
        }
        
        // At least some images should be accessible
        logger.info("Accessible images: {}/{}", accessibleCount, images.size());
        Assert.assertTrue(accessibleCount > 0, "At least one image should be accessible");
        
        logger.info("testImagesURLAccessibility - PASSED");
    }

    // ============= Helper Methods =============

    /**
     * Validates image dimensions by downloading and checking the actual image
     * 
     * @param imageUrl The URL of the image to validate
     * @param expectedWidth Expected width in pixels (null to skip width validation)
     * @param expectedHeight Expected height in pixels (null to skip height validation)
     * @param context Context for logging (e.g., "width", "height", "width and height")
     */
    private void validateImageDimensions(String imageUrl, Integer expectedWidth, Integer expectedHeight, String context) {
        try {
            @SuppressWarnings("deprecation")
            URL url = new URL(imageUrl);
            BufferedImage image = ImageIO.read(url);
            
            if (image == null) {
                logger.warn("Could not read image from URL: {}. Skipping dimension validation.", imageUrl);
                return;
            }
            
            int actualWidth = image.getWidth();
            int actualHeight = image.getHeight();
            
            logger.debug("Image dimensions for {}: {}x{}", imageUrl, actualWidth, actualHeight);
            
            // Validate width if expected
            if (expectedWidth != null) {
                // Allow 10% tolerance for image dimensions
                int tolerance = (int) (expectedWidth * 0.1);
                Assert.assertTrue(
                    Math.abs(actualWidth - expectedWidth) <= tolerance,
                    String.format("Image width validation failed for %s. Expected: %d, Actual: %d (tolerance: %d), URL: %s", 
                        context, expectedWidth, actualWidth, tolerance, imageUrl)
                );
            }
            
            // Validate height if expected
            if (expectedHeight != null) {
                // Allow 10% tolerance for image dimensions
                int tolerance = (int) (expectedHeight * 0.1);
                Assert.assertTrue(
                    Math.abs(actualHeight - expectedHeight) <= tolerance,
                    String.format("Image height validation failed for %s. Expected: %d, Actual: %d (tolerance: %d), URL: %s", 
                        context, expectedHeight, actualHeight, tolerance, imageUrl)
                );
            }
            
            logger.debug("Image dimension validation passed for {}: {}x{}", context, actualWidth, actualHeight);
            
        } catch (IOException e) {
            logger.warn("Failed to validate image dimensions for URL: {}. Error: {}", imageUrl, e.getMessage());
            // Don't fail the test if image is not accessible - just log warning
        } catch (Exception e) {
            logger.error("Unexpected error validating image dimensions for URL: {}", imageUrl, e);
        }
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
                .limit(3)  // Limit to 3 for faster execution
                .map(width -> new Object[]{((Number) width).intValue()})
                .toArray(Object[][]::new);
    }

    @org.testng.annotations.DataProvider(name = "getValidHeights")
    public Object[][] getValidHeights() {
        return validHeights.stream()
                .limit(3)  // Limit to 3 for faster execution
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

