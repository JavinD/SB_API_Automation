package com.sb.automation.utils;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.sb.automation.config.APIConfig.FULL_BASE_URL;

/**
 * Base API client for making REST API calls
 */
public class APIClient {
    private static final Logger logger = LoggerFactory.getLogger(APIClient.class);

    /**
     * Creates a base request specification with common headers
     */
    public static RequestSpecification getRequestSpec() {
        return RestAssured
                .given()
                .baseUri(FULL_BASE_URL)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json");
    }

    /**
     * Makes a GET request with query parameters
     */
    public static Response get(String endpoint, Map<String, Object> queryParams) {
        logger.info("Making GET request to: {} with params: {}", endpoint, queryParams);
        
        RequestSpecification request = getRequestSpec();
        
        if (queryParams != null && !queryParams.isEmpty()) {
            request.queryParams(queryParams);
        }
        
        Response response = request
                .when()
                .get(endpoint)
                .then()
                .extract()
                .response();
        
        logger.info("Response Status Code: {}", response.getStatusCode());
        logger.info("Response Time: {} ms", response.getTime());
        
        return response;
    }

    /**
     * Makes a GET request without query parameters
     */
    public static Response get(String endpoint) {
        return get(endpoint, new HashMap<>());
    }

    /**
     * Builder class for creating API requests with common parameters
     */
    public static class RequestBuilder {
        private final String endpoint;
        private final Map<String, Object> queryParams;

        public RequestBuilder(String endpoint) {
            this.endpoint = endpoint;
            this.queryParams = new HashMap<>();
        }

        public RequestBuilder withLocale(String locale) {
            queryParams.put("_locale", locale);
            return this;
        }

        public RequestBuilder withQuantity(int quantity) {
            queryParams.put("_quantity", quantity);
            return this;
        }

        public RequestBuilder withSeed(int seed) {
            queryParams.put("_seed", seed);
            return this;
        }

        public RequestBuilder withParam(String key, Object value) {
            queryParams.put(key, value);
            return this;
        }

        public Response execute() {
            return APIClient.get(endpoint, queryParams);
        }

        public Map<String, Object> getQueryParams() {
            return queryParams;
        }
    }
}

