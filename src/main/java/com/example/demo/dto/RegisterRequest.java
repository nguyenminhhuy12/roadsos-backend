package com.example.demo.dto;

import com.example.demo.entity.User.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String phone;
    private String password;
    private String email;
    private Integer age;
    private String address;
    private Role role;
}
