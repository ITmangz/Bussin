package com.bussin.bussin_api.service;

import org.springframework.stereotype.Service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;

@Service
public class FirebaseAuthService {

    public FirebaseToken verifyIdToken(String idToken) {

        try {

            return FirebaseAuth
                    .getInstance()
                    .verifyIdToken(idToken);

        } catch (Exception exception) {

            exception.printStackTrace();

            throw new IllegalArgumentException(
                    "Invalid Firebase ID token",
                    exception);
        }
    }
}