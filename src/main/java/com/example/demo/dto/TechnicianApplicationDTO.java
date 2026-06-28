package com.example.demo.dto;

import lombok.Data;

@Data
public class TechnicianApplicationDTO {
    private String fullName;
    private String phone;
    private String email;
    private String address;
    private Integer experience;
    private String specialization;
    private String idCard;
    private String note;
    private Integer age;
    private String imageUrl;
}