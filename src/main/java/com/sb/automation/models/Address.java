package com.sb.automation.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Address {
    private int id;
    private String street;
    private String streetName;
    private String buildingNumber;
    private String city;
    private String zipcode;
    private String country;
    
    @JsonProperty("county_code")
    private String countyCode;
    
    private double latitude;
    private double longitude;
}
