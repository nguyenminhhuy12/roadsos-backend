package com.example.demo.controller;

import com.example.demo.dto.TechnicianApplicationDTO;
import com.example.demo.entity.TechnicianApplication;
import com.example.demo.service.TechnicianApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/technician-application")
@CrossOrigin(origins = "*")
public class TechnicianApplicationController {

    @Autowired
    private TechnicianApplicationService service;

    // Thợ gửi đơn đăng ký
    @PostMapping("/apply")
    public ResponseEntity<?> apply(@RequestBody TechnicianApplicationDTO dto) {
        try {
            return ResponseEntity.ok(service.apply(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Thợ kiểm tra trạng thái đơn
    @GetMapping("/status/{phone}")
    public ResponseEntity<?> checkStatus(@PathVariable String phone) {
        try {
            return ResponseEntity.ok(service.checkStatus(phone));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Admin: xem danh sách chờ duyệt
    @GetMapping("/pending")
    public ResponseEntity<List<TechnicianApplication>> getPending() {
        return ResponseEntity.ok(service.getPending());
    }

    // Admin: xem tất cả đơn
    @GetMapping("/all")
    public ResponseEntity<List<TechnicianApplication>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // Admin: duyệt đơn
    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approve(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.approve(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Admin: từ chối
    @PutMapping("/{id}/reject")
    public ResponseEntity<?> reject(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        try {
            return ResponseEntity.ok(
                service.reject(id, body.get("reason")));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}