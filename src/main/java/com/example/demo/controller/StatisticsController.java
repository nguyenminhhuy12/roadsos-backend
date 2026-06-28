package com.example.demo.controller;

import com.example.demo.repository.RescueRequestRepository;
import com.example.demo.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final RescueRequestRepository rescueRequestRepository;
    private final ReviewRepository reviewRepository;

    // Số ca theo trạng thái
    @GetMapping("/rescue-summary")
    public List<Object[]> getRescueSummary() {
        return rescueRequestRepository.countByStatus();
    }

    // Loại lỗi xe hay gặp nhất
    @GetMapping("/issue-types")
    public List<Object[]> getIssueTypes() {
        return rescueRequestRepository.countByIssueType();
    }

    // Đánh giá trung bình từng thợ
    @GetMapping("/technician-ratings")
    public List<Object[]> getTechnicianRatings() {
        return reviewRepository.avgRatingByTechnician();
    }
}