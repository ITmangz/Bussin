package com.bussin.bussin_api.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.stereotype.Service;

@Service
public class FirebaseAuthService {

    public FirebaseToken verifyIdToken(String idToken) {

        try {

            return FirebaseAuth
                    .getInstance()
                    .verifyIdToken(idToken);

        } catch (Exception exception) {

            throw new IllegalArgumentException(
                    "Invalid Firebase ID token");
        }
    }
}