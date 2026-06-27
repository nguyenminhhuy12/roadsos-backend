package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "rescue_requests")
@Data  // Lombok: tự gen getter/setter/toString
public class RescueRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long technicianId;

    private Double latitude;
    private Double longitude;
    private String address;
    private String issueType;
    private String description;
    private String vehicleInfo;

    @Enumerated(EnumType.STRING)
    private RescueStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime acceptedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.status = RescueStatus.PENDING;
    }
}
