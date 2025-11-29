package com.sb.automation.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Utility class for JSON Schema validation
 */
public class SchemaValidator {
    private static final Logger logger = LoggerFactory.getLogger(SchemaValidator.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Validates response against a JSON schema file
     */
    public static void validateSchema(Response response, String schemaFilePath) {
        logger.info("Validating response against schema: {}", schemaFilePath);
        
        File schemaFile = new File(schemaFilePath);
        if (!schemaFile.exists()) {
            logger.error("Schema file not found: {}", schemaFilePath);
            throw new RuntimeException("Schema file not found: " + schemaFilePath);
        }
        
        response.then().assertThat()
                .body(JsonSchemaValidator.matchesJsonSchema(schemaFile));
        
        logger.info("Schema validation successful");
    }

    /**
     * Validates response structure without strict schema validation
     */
    public static boolean hasExpectedStructure(Response response, String... expectedFields) {
        try {
            JsonNode rootNode = objectMapper.readTree(response.asString());
            
            for (String field : expectedFields) {
                if (!rootNode.has(field)) {
                    logger.error("Expected field '{}' not found in response", field);
                    return false;
                }
            }
            
            return true;
        } catch (IOException e) {
            logger.error("Error parsing response JSON", e);
            return false;
        }
    }
}

