package com.sb.automation.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Utility class for reading test data from JSON files
 */
public class TestDataReader {
    private static final Logger logger = LoggerFactory.getLogger(TestDataReader.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Reads test data from a JSON file
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> readTestData(String filePath) {
        try {
            File file = new File(filePath);
            logger.info("Reading test data from: {}", filePath);
            return objectMapper.readValue(file, Map.class);
        } catch (IOException e) {
            logger.error("Error reading test data from file: {}", filePath, e);
            throw new RuntimeException("Failed to read test data: " + filePath, e);
        }
    }

    /**
     * Reads a list from test data file
     */
    @SuppressWarnings("unchecked")
    public static List<Object> readTestDataList(String filePath) {
        try {
            File file = new File(filePath);
            logger.info("Reading test data list from: {}", filePath);
            return objectMapper.readValue(file, List.class);
        } catch (IOException e) {
            logger.error("Error reading test data list from file: {}", filePath, e);
            throw new RuntimeException("Failed to read test data list: " + filePath, e);
        }
    }

    /**
     * Gets a specific value from test data
     */
    public static Object getTestDataValue(String filePath, String key) {
        Map<String, Object> testData = readTestData(filePath);
        return testData.get(key);
    }

    /**
     * Gets a list of values from test data
     */
    @SuppressWarnings("unchecked")
    public static List<Object> getTestDataList(String filePath, String key) {
        Map<String, Object> testData = readTestData(filePath);
        return (List<Object>) testData.get(key);
    }
}

