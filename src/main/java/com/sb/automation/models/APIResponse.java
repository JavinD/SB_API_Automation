package com.sb.automation.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Generic API response model
 */
@Data
public class APIResponse<T> {
    private String status;
    private int code;
    private int total;
    private List<T> data;
}

