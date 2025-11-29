package com.sb.automation.config;

/**
 * Configuration class for API base URL and version
 */
public class APIConfig {
    public static final String BASE_URL = "https://fakerapi.it/api";
    public static final String API_VERSION = "v2";
    public static final String FULL_BASE_URL = BASE_URL + "/" + API_VERSION;
    
    // Endpoints
    public static final String ADDRESSES_ENDPOINT = "/addresses";
    public static final String BOOKS_ENDPOINT = "/books";
    public static final String COMPANIES_ENDPOINT = "/companies";
    public static final String CREDIT_CARDS_ENDPOINT = "/creditCards";
    public static final String IMAGES_ENDPOINT = "/images";
    public static final String PERSONS_ENDPOINT = "/persons";
    public static final String PLACES_ENDPOINT = "/places";
    public static final String PRODUCTS_ENDPOINT = "/products";
    public static final String TEXTS_ENDPOINT = "/texts";
    public static final String USERS_ENDPOINT = "/users";
    
    // Default values
    public static final String DEFAULT_LOCALE = "en_US";
    public static final int DEFAULT_QUANTITY = 10;
    public static final int MIN_QUANTITY = 1;
    public static final int MAX_QUANTITY = 1000;
}

