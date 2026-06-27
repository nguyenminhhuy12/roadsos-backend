package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import com.example.demo.entity.User.Role;
@Data
@AllArgsConstructor
public class LoginResponse {
	private long id;
    private String token;
    private String phone;
    private String fullName;
    private Role role;
}