package com.example.demo.entity;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
@Entity
@Table(name = "technician_applications")
@Data
public class TechnicianApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;
    private String phone;
    private String email;
    private String address;
    private Integer age;            // tuổi
    private Integer experience;     // số năm kinh nghiệm
    private String specialization;  // chuyên môn: honda, yamaha, all...
    private String idCard;          // CCCD
    private String note;            // ghi chú thêm
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status = ApplicationStatus.PENDING;
    private String rejectReason;    // lý do từ chối
    private LocalDateTime createdAt;
    private LocalDateTime reviewedAt;
    public enum ApplicationStatus {
        PENDING,    // chờ duyệt
        APPROVED,   // đã duyệt
        REJECTED    // từ chối
    }
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}