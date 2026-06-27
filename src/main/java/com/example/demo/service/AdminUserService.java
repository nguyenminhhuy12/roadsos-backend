package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Map;

@Service
public class AdminUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User getById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy user!"));
    }

    public List<User> search(String keyword) {
        return userRepository
            .findByNameContainingOrPhoneContaining(keyword, keyword);
    }

    public User create(User user) {
        if (userRepository.findByPhone(user.getPhone()).isPresent()) {
            throw new RuntimeException("SĐT đã tồn tại!");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setBanned(false);
        return userRepository.save(user);
    }

    public User update(Long id, Map<String, Object> updates) {
        User user = getById(id);

        if (updates.containsKey("name"))
            user.setName((String) updates.get("name"));
        if (updates.containsKey("email"))
            user.setEmail((String) updates.get("email"));
        if (updates.containsKey("address"))
            user.setAddress((String) updates.get("address"));
        if (updates.containsKey("phone"))
            user.setPhone((String) updates.get("phone"));
        if (updates.containsKey("role"))
            user.setRole(User.Role.valueOf((String) updates.get("role")));
        if (updates.containsKey("password"))
            user.setPassword(passwordEncoder.encode(
                (String) updates.get("password")));

        return userRepository.save(user);
    }

    public User ban(Long id, String reason) {
        User user = getById(id);
        user.setBanned(true);
        user.setBanReason(reason);
        user.setBannedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public User unban(Long id) {
        User user = getById(id);
        user.setBanned(false);
        user.setBanReason(null);
        user.setBannedAt(null);
        return userRepository.save(user);
    }

    public void delete(Long id) {
        User user = getById(id);
        userRepository.delete(user);
    }
}