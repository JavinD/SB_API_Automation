package com.sb.automation.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class SchemaValidator {
    private static final Logger logger = LoggerFactory.getLogger(SchemaValidator.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void validateSchema(Response response, String schemaFilePath) {
        File schemaFile = new File(schemaFilePath);
        if (!schemaFile.exists()) {
            throw new RuntimeException("Schema file not found: " + schemaFilePath);
        }
        
        response.then().assertThat()
                .body(JsonSchemaValidator.matchesJsonSchema(schemaFile));
    }

    public static boolean hasExpectedStructure(Response response, String... expectedFields) {
        try {
            JsonNode rootNode = objectMapper.readTree(response.asString());
            
            for (String field : expectedFields) {
                if (!rootNode.has(field)) {
                    logger.error("Missing field: {}", field);
                    return false;
                }
            }
            
            return true;
        } catch (IOException e) {
            logger.error("JSON parse error", e);
            return false;
        }
    }
}
