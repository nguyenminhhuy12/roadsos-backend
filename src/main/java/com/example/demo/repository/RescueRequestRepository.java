package com.example.demo.repository;

import com.example.demo.entity.RescueRequest;
import com.example.demo.entity.RescueStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;  // ← thêm dòng này


@Repository
public interface RescueRequestRepository extends JpaRepository<RescueRequest, Long> {
    List<RescueRequest> findByStatus(RescueStatus status);
    List<RescueRequest> findByTechnicianId(Long technicianId);
    List<RescueRequest> findByUserId(Long userId);
    Optional<RescueRequest> findByUserIdAndStatus(Long userId, RescueStatus status);  // ✅
    List<RescueRequest> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<RescueRequest> findByTechnicianIdOrderByCreatedAtDesc(Long technicianId);
    List<RescueRequest> findByStatusIn(List<RescueStatus> statuses);
    @Query("SELECT r.status, COUNT(r) FROM RescueRequest r GROUP BY r.status")
    List<Object[]> countByStatus();

    @Query("SELECT r.issueType, COUNT(r) FROM RescueRequest r GROUP BY r.issueType ORDER BY COUNT(r) DESC")
    List<Object[]> countByIssueType();
}