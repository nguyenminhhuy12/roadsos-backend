package com.example.demo.dto;

import lombok.Data;

@Data
public class RescueRequestDTO {
    private Double latitude;
    private Double longitude;
    private String address;
    private String issueType;
    private String description;
    private String vehicleInfo;
}