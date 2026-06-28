package com.example.demo.repository;

import com.example.demo.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByRequestId(Long requestId);  // 1 request chỉ đánh giá 1 lần
    List<Review> findByTechnicianId(Long technicianId);

    // Tính điểm trung bình của thợ
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.technicianId = :technicianId")
    Double getAverageRating(Long technicianId);
    @Query("SELECT r.technicianId, AVG(r.rating) FROM Review r GROUP BY r.technicianId")
    List<Object[]> avgRatingByTechnician();
}