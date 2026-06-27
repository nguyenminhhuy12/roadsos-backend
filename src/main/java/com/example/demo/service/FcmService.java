package com.example.demo.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MessagingErrorCode;

import java.util.List;

@Service
public class FcmService {

    // Gửi notification đến 1 thợ
	public void sendToTechnician(String fcmToken, String title,
	                              String body, String requestId) {
	    if (fcmToken == null || fcmToken.isEmpty()) return;
	    Message message = Message.builder()
	        .setToken(fcmToken)
	        .setNotification(Notification.builder()
	            .setTitle(title)
	            .setBody(body)
	            .build())
	        .putData("requestId", requestId)
	        .putData("type", "NEW_RESCUE_REQUEST")
	        .build();
	    try {
	        FirebaseMessaging.getInstance().send(message);
	        System.out.println("✅ Gửi FCM đến: " + fcmToken);
	    } catch (FirebaseMessagingException e) {
	        if (e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED
	                || e.getMessagingErrorCode() == MessagingErrorCode.INVALID_ARGUMENT) {
	            System.out.println("⚠️ Token không hợp lệ, cần xoá khỏi DB: " + fcmToken);
	            // gọi userRepository để xoá/null fcm_token của user này tại đây
	        } else {
	            System.out.println("❌ FCM lỗi: " + e.getMessage());
	        }
	    }
	}

    // Gửi đến nhiều thợ
    public void sendToMany(List<String> tokens, String title,
                           String body, String requestId) {
        tokens.forEach(token ->
            sendToTechnician(token, title, body, requestId));
    }
}