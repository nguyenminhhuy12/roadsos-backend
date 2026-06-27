package com.example.demo.dto;

import lombok.Data;

@Data
public class ReviewDTO {
    private Long requestId;
    private Long userId;
    private Long technicianId;
    private Integer rating;   // 1-5
    private String comment;
}