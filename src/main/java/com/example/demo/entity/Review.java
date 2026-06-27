package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Data
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long requestId;     // liên kết với rescue_request
    private Long userId;        // người đánh giá
    private Long technicianId;  // thợ được đánh giá
    private Integer rating;     // 1-5 sao
    private String comment;     // nhận xét

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}