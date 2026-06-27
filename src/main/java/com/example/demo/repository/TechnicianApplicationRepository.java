package com.example.demo.repository;

import com.example.demo.entity.TechnicianApplication;
import com.example.demo.entity.TechnicianApplication.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TechnicianApplicationRepository
        extends JpaRepository<TechnicianApplication, Long> {
    List<TechnicianApplication> findByStatus(ApplicationStatus status);
    Optional<TechnicianApplication> findByPhone(String phone);
}