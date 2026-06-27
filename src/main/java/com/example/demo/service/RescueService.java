package com.example.demo.service;

import com.example.demo.dto.RescueRequestDTO;
import com.example.demo.entity.RescueRequest;
import com.example.demo.entity.RescueStatus;
import com.example.demo.entity.User;
import com.example.demo.repository.RescueRequestRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.DistanceUtil;
import com.google.cloud.firestore.FieldValue;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Set;

@Service
public class RescueService {

    @Autowired
    private RescueRequestRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FcmService fcmService;

    public RescueRequest createRequest(Long userId, RescueRequestDTO dto) {
        // Chặn tạo trùng
        Optional<RescueRequest> existing =
            repository.findByUserIdAndStatus(userId, RescueStatus.PENDING);
        if (existing.isPresent()) {
            throw new RuntimeException("Bạn đã có yêu cầu cứu hộ đang chờ xử lý!");
        }

        RescueRequest req = new RescueRequest();
        req.setUserId(userId);
        req.setLatitude(dto.getLatitude());
        req.setLongitude(dto.getLongitude());
        req.setAddress(dto.getAddress());
        req.setIssueType(dto.getIssueType());
        req.setDescription(dto.getDescription());
        req.setVehicleInfo(dto.getVehicleInfo());

        RescueRequest saved = repository.save(req);
        syncToFirestore(saved);
        notifyNearbyTechnicians(saved);  // gửi notification thợ 5km
        return saved;
    }

    public List<RescueRequest> getPendingRequests() {
        return repository.findByStatus(RescueStatus.PENDING);
    }

    public synchronized RescueRequest acceptRequest(Long requestId, Long technicianId) {
        RescueRequest req = repository.findById(requestId)
            .orElseThrow(() -> new RuntimeException("Request not found"));

        if (req.getStatus() != RescueStatus.PENDING) {
            throw new RuntimeException("Yêu cầu đã có người nhận rồi!");
        }

        req.setTechnicianId(technicianId);
        req.setStatus(RescueStatus.ACCEPTED);
        req.setAcceptedAt(LocalDateTime.now());

        RescueRequest saved = repository.save(req);
        updateFirestoreStatus(saved);  // real-time cho tất cả thợ + user
        notifyUserAccepted(saved);     // FCM báo cho user
        return saved;
    }

    public RescueRequest updateStatus(Long requestId, RescueStatus status) {
        RescueRequest req = repository.findById(requestId)
            .orElseThrow(() -> new RuntimeException("Not found"));
        req.setStatus(status);
        RescueRequest saved = repository.save(req);
        updateFirestoreStatus(saved);
        return saved;
    }

    public RescueRequest getById(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Not found"));
    }

    // ---- Notification helpers ----

    private void notifyNearbyTechnicians(RescueRequest req) {
        // Lấy danh sách technicianId đang có ca (chưa hoàn tất hẳn)
        List<RescueRequest> activeJobs = repository.findByStatusIn(
            List.of(RescueStatus.ACCEPTED, RescueStatus.TECHNICIAN_DONE)
        );
        Set<Long> busyTechnicianIds = activeJobs.stream()
            .map(RescueRequest::getTechnicianId)
            .filter(id -> id != null)
            .collect(Collectors.toSet());

        List<User> technicians = userRepository
            .findByRoleAndLatitudeNotNull(User.Role.TECHNICIAN);

        List<String> nearbyTokens = technicians.stream()
            .filter(t -> t.getLatitude() != null && t.getLongitude() != null)
            .filter(t -> !busyTechnicianIds.contains(t.getId()))   // 👈 lọc thợ đang bận
            .filter(t -> {
                double distance = DistanceUtil.calculate(
                    req.getLatitude(), req.getLongitude(),
                    t.getLatitude(), t.getLongitude()
                );
                return distance <= 5.0;
            })
            .filter(t -> t.getFcmToken() != null)
            .map(User::getFcmToken)
            .collect(Collectors.toList());

        System.out.println("📍 Tìm thấy " + nearbyTokens.size() + " thợ rảnh trong 5km");

        fcmService.sendToMany(
            nearbyTokens,
            "🛵 Có yêu cầu cứu hộ mới!",
            "Sự cố: " + req.getIssueType() + " - " + req.getAddress(),
            req.getId().toString()
        );
    }

    private void notifyUserAccepted(RescueRequest req) {
        userRepository.findById(req.getUserId()).ifPresent(user -> {
            if (user.getFcmToken() != null) {
                fcmService.sendToTechnician(
                    user.getFcmToken(),
                    "✅ Đã có thợ nhận ca!",
                    "Thợ đang trên đường đến chỗ bạn",
                    req.getId().toString()
                );
            }
        });
    }

    // ---- Firestore helpers ----

    private void syncToFirestore(RescueRequest req) {
        Map<String, Object> data = new HashMap<>();
        data.put("userId", req.getUserId());
        data.put("status", req.getStatus().name());
        data.put("latitude", req.getLatitude());
        data.put("longitude", req.getLongitude());
        data.put("address", req.getAddress());
        data.put("issueType", req.getIssueType());
        data.put("vehicleInfo", req.getVehicleInfo());
        data.put("technicianId", req.getTechnicianId());
        data.put("createdAt", FieldValue.serverTimestamp());

        FirestoreClient.getFirestore()
            .collection("rescue_requests")
            .document(req.getId().toString())
            .set(data);
    }

    private void updateFirestoreStatus(RescueRequest req) {
        Map<String, Object> update = new HashMap<>();
        update.put("status", req.getStatus().name());
        update.put("technicianId", req.getTechnicianId());

        FirestoreClient.getFirestore()
            .collection("rescue_requests")
            .document(req.getId().toString())
            .update(update);
    }
 // Thợ bấm hoàn thành
    public RescueRequest technicianDone(Long requestId, Long technicianId) {
        RescueRequest req = repository.findById(requestId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy ca!"));

        if (!req.getTechnicianId().equals(technicianId)) {
            throw new RuntimeException("Bạn không phải thợ của ca này!");
        }

        req.setStatus(RescueStatus.TECHNICIAN_DONE);
        RescueRequest saved = repository.save(req);
        updateFirestoreStatus(saved);  // real-time báo user
        return saved;
    }

    // User xác nhận hoàn thành
    public RescueRequest userConfirmDone(Long requestId, Long userId) {
        RescueRequest req = repository.findById(requestId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy ca!"));

        if (!req.getUserId().equals(userId)) {
            throw new RuntimeException("Bạn không phải user của ca này!");
        }

        if (req.getStatus() != RescueStatus.TECHNICIAN_DONE) {
            throw new RuntimeException("Thợ chưa xác nhận hoàn thành!");
        }

        req.setStatus(RescueStatus.DONE);
        RescueRequest saved = repository.save(req);
        updateFirestoreStatus(saved);  // real-time → TrackingScreen chuyển sang Review
        return saved;
    }
    public List<RescueRequest> getUserHistory(Long userId) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<RescueRequest> getTechnicianHistory(Long technicianId) {
        return repository.findByTechnicianIdOrderByCreatedAtDesc(technicianId);
    }
}