package com.example.demo.service;

import com.example.demo.dto.TechnicianApplicationDTO;
import com.example.demo.entity.TechnicianApplication;
import com.example.demo.entity.TechnicianApplication.ApplicationStatus;
import com.example.demo.entity.User;
import com.example.demo.repository.TechnicianApplicationRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TechnicianApplicationService {

    @Autowired
    private TechnicianApplicationRepository applicationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FcmService fcmService;

    // Thợ gửi đơn đăng ký
    public TechnicianApplication apply(TechnicianApplicationDTO dto) {
        // Kiểm tra SĐT đã đăng ký chưa
        if (applicationRepository.findByPhone(dto.getPhone()).isPresent()) {
            throw new RuntimeException("Số điện thoại này đã gửi đơn đăng ký rồi!");
        }
        if (userRepository.findByPhone(dto.getPhone()).isPresent()) {
            throw new RuntimeException("Số điện thoại này đã có tài khoản rồi!");
        }

        // ✅ Bước 5: Validate tuổi — đặt ngay sau 2 check trên, trước khi tạo entity
        if (dto.getAge() == null || dto.getAge() < 18 || dto.getAge() > 70) {
            throw new RuntimeException("Tuổi không hợp lệ (phải từ 18 đến 70)!");
        }

        TechnicianApplication app = new TechnicianApplication();
        app.setFullName(dto.getFullName());
        app.setPhone(dto.getPhone());
        app.setEmail(dto.getEmail());
        app.setAddress(dto.getAddress());
        app.setAge(dto.getAge());            // ✅ thêm dòng này
        app.setExperience(dto.getExperience());
        app.setSpecialization(dto.getSpecialization());
        app.setIdCard(dto.getIdCard());
        app.setNote(dto.getNote());

        return applicationRepository.save(app);
    }

    // Admin xem danh sách chờ duyệt
    public List<TechnicianApplication> getPending() {
        return applicationRepository.findByStatus(ApplicationStatus.PENDING);
    }

    // Admin xem tất cả
    public List<TechnicianApplication> getAll() {
        return applicationRepository.findAll();
    }

    // Admin duyệt → tự động tạo tài khoản
    public TechnicianApplication approve(Long applicationId) {
        TechnicianApplication app = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn!"));

        if (app.getStatus() != ApplicationStatus.PENDING) {
            throw new RuntimeException("Đơn này đã được xử lý rồi!");
        }

        // Tạo tài khoản thợ
        // Mật khẩu mặc định = 4 số cuối SĐT
        String defaultPassword = app.getPhone()
            .substring(app.getPhone().length() - 4);

        User technician = new User();
        technician.setPhone(app.getPhone());
        technician.setName(app.getFullName());
        technician.setEmail(app.getEmail());
        technician.setAddress(app.getAddress());
        technician.setAge(app.getAge());     // ✅ thêm dòng này
        technician.setPassword(passwordEncoder.encode(defaultPassword));
        technician.setRole(User.Role.TECHNICIAN);
        userRepository.save(technician);

        // Cập nhật trạng thái đơn
        app.setStatus(ApplicationStatus.APPROVED);
        app.setReviewedAt(LocalDateTime.now());
        applicationRepository.save(app);

        System.out.println("✅ Tạo tài khoản thợ: " + app.getPhone()
            + " | Mật khẩu: " + defaultPassword);

        return app;
    }

    // Admin từ chối
    public TechnicianApplication reject(Long applicationId, String reason) {
        TechnicianApplication app = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn!"));

        if (app.getStatus() != ApplicationStatus.PENDING) {
            throw new RuntimeException("Đơn này đã được xử lý rồi!");
        }

        app.setStatus(ApplicationStatus.REJECTED);
        app.setRejectReason(reason);
        app.setReviewedAt(LocalDateTime.now());

        return applicationRepository.save(app);
    }

    // Thợ kiểm tra trạng thái đơn của mình
    public TechnicianApplication checkStatus(String phone) {
        return applicationRepository.findByPhone(phone)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đăng ký!"));
    }
}