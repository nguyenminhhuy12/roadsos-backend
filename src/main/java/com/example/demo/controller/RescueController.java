package com.example.demo.controller;

import com.example.demo.dto.RescueRequestDTO;
import com.example.demo.entity.RescueRequest;
import com.example.demo.entity.RescueStatus;
import com.example.demo.service.RescueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rescue")
@CrossOrigin(origins = "*")  // cho Flutter gọi được
public class RescueController {

    @Autowired
    private RescueService rescueService;

    @PostMapping("/request")
    public ResponseEntity<RescueRequest> createRequest(
            @RequestParam Long userId,
            @RequestBody RescueRequestDTO dto) {
        return ResponseEntity.ok(rescueService.createRequest(userId, dto));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<RescueRequest>> getPending() {
        return ResponseEntity.ok(rescueService.getPendingRequests());
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<RescueRequest> accept(
            @PathVariable Long id,
            @RequestParam Long technicianId) {
        return ResponseEntity.ok(rescueService.acceptRequest(id, technicianId));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<RescueRequest> updateStatus(
            @PathVariable Long id,
            @RequestParam RescueStatus status) {
        return ResponseEntity.ok(rescueService.updateStatus(id, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RescueRequest> getById(@PathVariable Long id) {
        return ResponseEntity.ok(rescueService.getById(id));
    }
    @PutMapping("/{id}/done")
    public ResponseEntity<?> markDone(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(rescueService.updateStatus(id, RescueStatus.DONE));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
 // Thợ bấm hoàn thành
    @PutMapping("/{id}/technician-done")
    public ResponseEntity<?> technicianDone(
            @PathVariable Long id,
            @RequestParam Long technicianId) {
        try {
            return ResponseEntity.ok(
                rescueService.technicianDone(id, technicianId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // User xác nhận hoàn thành
    @PutMapping("/{id}/user-confirm")
    public ResponseEntity<?> userConfirm(
            @PathVariable Long id,
            @RequestParam Long userId) {
        try {
            return ResponseEntity.ok(
                rescueService.userConfirmDone(id, userId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
 // User xem lịch sử của mình
    @GetMapping("/user/{userId}/history")
    public ResponseEntity<List<RescueRequest>> getUserHistory(
            @PathVariable Long userId) {
        return ResponseEntity.ok(rescueService.getUserHistory(userId));
    }

    // Thợ xem lịch sử của mình
    @GetMapping("/technician/{technicianId}/history")
    public ResponseEntity<List<RescueRequest>> getTechnicianHistory(
            @PathVariable Long technicianId) {
        return ResponseEntity.ok(rescueService.getTechnicianHistory(technicianId));
    }
}