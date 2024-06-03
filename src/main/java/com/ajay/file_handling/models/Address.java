package com.ajay.file_handling.models;

import lombok.Data;

@Data
public class Address {

    private String addressLane1;
    private String addressLane2;
    private String city;
    private String state;
    private String zipcode;
    private String addressType;
}
