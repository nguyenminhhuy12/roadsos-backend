package com.example.demo.service;

import com.example.demo.dto.ReviewDTO;
import com.example.demo.entity.Review;
import com.example.demo.entity.RescueRequest;
import com.example.demo.entity.RescueStatus;
import com.example.demo.repository.ReviewRepository;
import com.example.demo.repository.RescueRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private RescueRequestRepository rescueRequestRepository;

    public Review createReview(ReviewDTO dto) {
        // Kiểm tra request tồn tại và đã DONE chưa
        RescueRequest req = rescueRequestRepository.findById(dto.getRequestId())
            .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu!"));

        if (req.getStatus() != RescueStatus.DONE) {
            throw new RuntimeException("Chỉ đánh giá được khi dịch vụ đã hoàn thành!");
        }

        // Kiểm tra đã đánh giá chưa
        Optional<Review> existing = reviewRepository.findByRequestId(dto.getRequestId());
        if (existing.isPresent()) {
            throw new RuntimeException("Bạn đã đánh giá rồi!");
        }

        // Validate rating
        if (dto.getRating() < 1 || dto.getRating() > 5) {
            throw new RuntimeException("Đánh giá phải từ 1 đến 5 sao!");
        }

        Review review = new Review();
        review.setRequestId(dto.getRequestId());
        review.setUserId(dto.getUserId());
        review.setTechnicianId(dto.getTechnicianId());
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());

        return reviewRepository.save(review);
    }

    // Lấy điểm trung bình của thợ
    public Double getAverageRating(Long technicianId) {
        Double avg = reviewRepository.getAverageRating(technicianId);
        return avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0;
    }

    public List<Review> getTechnicianReviews(Long technicianId) {
        return reviewRepository.findByTechnicianId(technicianId);
    }
    public Optional<Review> getByRequestId(Long requestId) {
        return reviewRepository.findByRequestId(requestId);
    }    
}