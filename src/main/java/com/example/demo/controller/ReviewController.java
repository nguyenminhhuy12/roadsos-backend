package com.example.demo.controller;

import com.example.demo.dto.ReviewDTO;
import com.example.demo.entity.Review;
import com.example.demo.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/review")
@CrossOrigin(origins = "*")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    // User gửi đánh giá
    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody ReviewDTO dto) {
        try {
            return ResponseEntity.ok(reviewService.createReview(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Xem điểm trung bình thợ
    @GetMapping("/technician/{technicianId}/rating")
    public ResponseEntity<?> getAverageRating(@PathVariable Long technicianId) {
        return ResponseEntity.ok(reviewService.getAverageRating(technicianId));
    }

    // Xem danh sách đánh giá của thợ
    @GetMapping("/technician/{technicianId}")
    public ResponseEntity<List<Review>> getTechnicianReviews(
            @PathVariable Long technicianId) {
        return ResponseEntity.ok(reviewService.getTechnicianReviews(technicianId));
    }

    // Lấy đánh giá theo requestId  ← sửa chỗ này
    @GetMapping("/request/{requestId}")
    public ResponseEntity<?> getByRequestId(@PathVariable Long requestId) {
        try {
            return reviewService.getByRequestId(requestId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}