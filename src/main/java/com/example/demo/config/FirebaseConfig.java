package com.example.demo.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {
    @PostConstruct
    public void initFirebase() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            
            String firebaseJson = System.getenv("FIREBASE_SERVICE_ACCOUNT");
            
            InputStream serviceAccount;
            if (firebaseJson != null && !firebaseJson.isEmpty()) {
                // Trên Render: đọc từ environment variable
                serviceAccount = new ByteArrayInputStream(firebaseJson.getBytes());
                System.out.println("✅ Firebase: đọc từ environment variable");
            } else {
                // Local: đọc từ file JSON như cũ
                serviceAccount = getClass()
                    .getClassLoader()
                    .getResourceAsStream("firebase-service-account.json");
                if (serviceAccount == null) {
                    throw new RuntimeException("❌ Không tìm thấy firebase-service-account.json!");
                }
                System.out.println("✅ Firebase: đọc từ file local");
            }

            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://anbatocom-7ff5f-default-rtdb.asia-southeast1.firebasedatabase.app")
                .build();

            FirebaseApp.initializeApp(options);
            System.out.println("✅ Firebase initialized!");
        }
    }
}