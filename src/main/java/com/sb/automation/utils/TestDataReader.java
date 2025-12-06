package com.sb.automation.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class TestDataReader {
    private static final Logger logger = LoggerFactory.getLogger(TestDataReader.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @SuppressWarnings("unchecked")
    public static Map<String, Object> readTestData(String filePath) {
        try {
            return objectMapper.readValue(new File(filePath), Map.class);
        } catch (IOException e) {
            logger.error("Failed to read: {}", filePath, e);
            throw new RuntimeException("Failed to read test data: " + filePath, e);
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Object> readTestDataList(String filePath) {
        try {
            return objectMapper.readValue(new File(filePath), List.class);
        } catch (IOException e) {
            logger.error("Failed to read: {}", filePath, e);
            throw new RuntimeException("Failed to read test data list: " + filePath, e);
        }
    }

    public static Object getTestDataValue(String filePath, String key) {
        return readTestData(filePath).get(key);
    }

    @SuppressWarnings("unchecked")
    public static List<Object> getTestDataList(String filePath, String key) {
        return (List<Object>) readTestData(filePath).get(key);
    }
}
