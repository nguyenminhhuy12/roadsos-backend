package com.example.demo.service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest request) {
        log.info("📡 Bắt đầu đăng nhập với SĐT: {}", request.getPhone());

        User user = userRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> {
                    log.warn("❌ Không tìm thấy SĐT: {}", request.getPhone());
                    return new RuntimeException("Số điện thoại không tồn tại!");
                });

        log.info("✅ Tìm thấy user: {}", user.getName());
        if (Boolean.TRUE.equals(user.getBanned())) {
            throw new RuntimeException(
                "Tài khoản bị khóa! Lý do: " + user.getBanReason());
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("❌ Mật khẩu không đúng cho SĐT: {}", request.getPhone());
            throw new RuntimeException("Mật khẩu không đúng!");
        }

        String token = jwtUtil.generateToken(user.getPhone());
        log.info("✅ Đăng nhập thành công! Token đã tạo cho: {}", user.getPhone());

        return new LoginResponse(user.getId(), token, user.getPhone(), user.getName(), user.getRole());
    }

    public String register(RegisterRequest request) {
        log.info("📡 Bắt đầu đăng ký với SĐT: {}", request.getPhone());

        if (userRepository.findByPhone(request.getPhone()).isPresent()) {
            log.warn("❌ SĐT đã tồn tại: {}", request.getPhone());
            throw new RuntimeException("Số điện thoại đã được đăng ký!");
        }

        User user = new User();
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setAge(request.getAge());
        user.setAddress(request.getAddress());
        user.setRole(request.getRole() != null ? request.getRole() : User.Role.USER);
        userRepository.save(user);
        log.info("✅ Đăng ký thành công cho: {}", request.getPhone());

        return "Đăng ký thành công!";
    }
    public LoginResponse otpLogin(LoginRequest request) {
        log.info("📡 Bắt đầu đăng nhập OTP với SĐT: {}", request.getPhone());
        User user = userRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> {
                    log.warn("❌ Không tìm thấy SĐT (OTP): {}", request.getPhone());
                    return new RuntimeException("Số điện thoại chưa đăng ký tài khoản!");
                });
        log.info("✅ Tìm thấy user (OTP): {}", user.getName());
        if (Boolean.TRUE.equals(user.getBanned())) {
            throw new RuntimeException(
                "Tài khoản bị khóa! Lý do: " + user.getBanReason());
        }
        String token = jwtUtil.generateToken(user.getPhone());
        log.info("✅ Đăng nhập OTP thành công! Token đã tạo cho: {}", user.getPhone());
        return new LoginResponse(user.getId(), token, user.getPhone(), user.getName(), user.getRole());
    }
}