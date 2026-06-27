package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByPhone(String phone);
    List<User> findByRoleAndLatitudeNotNull(User.Role role);  // ← thêm dòng này
    List<User> findByNameContainingOrPhoneContaining(String name, String phone);
    List<User> findByBanned(Boolean banned);
}