package com.sb.automation.models;

import lombok.Data;

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
