package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin(origins = "*")
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;

    // Xem tất cả user
    @GetMapping
    public ResponseEntity<List<User>> getAll() {
        return ResponseEntity.ok(adminUserService.getAll());
    }

    // Xem chi tiết 1 user
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(adminUserService.getById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Tìm kiếm theo tên/SĐT
    @GetMapping("/search")
    public ResponseEntity<List<User>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(adminUserService.search(keyword));
    }

    // Thêm user thủ công
    @PostMapping
    public ResponseEntity<?> create(@RequestBody User user) {
        try {
            return ResponseEntity.ok(adminUserService.create(user));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Sửa thông tin user
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {
        try {
            return ResponseEntity.ok(adminUserService.update(id, updates));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Ban user
    @PutMapping("/{id}/ban")
    public ResponseEntity<?> ban(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        try {
            return ResponseEntity.ok(
                adminUserService.ban(id, body.get("reason")));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Unban user
    @PutMapping("/{id}/unban")
    public ResponseEntity<?> unban(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(adminUserService.unban(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Xóa user
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            adminUserService.delete(id);
            return ResponseEntity.ok("Xóa thành công!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}