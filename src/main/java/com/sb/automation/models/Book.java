package com.sb.automation.models;

import lombok.Data;

/**
 * Book model representing the response data from /books endpoint
 */
@Data
public class Book {
    private int id;
    private String title;
    private String author;
    private String genre;
    private String description;
    private String isbn;
    private String image;
    private String published;
    private String publisher;
}

