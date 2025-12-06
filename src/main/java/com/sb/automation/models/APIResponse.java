package com.sb.automation.models;

import lombok.Data;
import java.util.List;

@Data
public class APIResponse<T> {
    private String status;
    private int code;
    private int total;
    private List<T> data;
}
