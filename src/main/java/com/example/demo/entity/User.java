package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String phone;
    private String password;
    private String name;      // ← đổi fullName thành name cho khớp AuthService
    private String email;
    private String address;
    private Integer age;
    private Double latitude;   // ← thêm
    private Double longitude;  // ← thêm
    private String fcmToken;   // ← thêm (để gửi notification)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    public enum Role {
        USER,
        TECHNICIAN,
        ADMIN,
    }
    private Boolean banned = false;       // ← thêm
    private String banReason;             // ← thêm
    private LocalDateTime bannedAt;
}