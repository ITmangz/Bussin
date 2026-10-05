package com.bussin.bussin_api.config;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        if (!FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.getInstance();
        }

        String serviceAccountJson = System.getenv("FIREBASE_SERVICE_ACCOUNT");

        GoogleCredentials credentials;

        if (serviceAccountJson != null && !serviceAccountJson.isBlank()) {
            credentials = GoogleCredentials.fromStream(
                    new ByteArrayInputStream(
                            serviceAccountJson.getBytes(StandardCharsets.UTF_8)));
        } else {
            try (FileInputStream serviceAccount =
                         new FileInputStream("secrets/service-account-key.json")) {
                credentials = GoogleCredentials.fromStream(serviceAccount);
            }
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .build();

        return FirebaseApp.initializeApp(options);
    }
}
