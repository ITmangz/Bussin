package com.bussin.desktop.services;

import java.util.HashMap;
import java.util.Map;

import com.bussin.desktop.ui.auth.RegistrationData;

public class MockAuthService {

    private static final Map<String, RegistrationData> USERS = new HashMap<>();

    static {

        RegistrationData admin = new RegistrationData();

        admin.setEmail("admin@bussin.com");
        admin.setPassword("admin123");

        admin.setFirstName("BUSSIN");
        admin.setLastName("Administrator");
        admin.setGender("Prefer not to say");
        admin.setAge("25");

        USERS.put(
                admin.getEmail().toLowerCase(),
                admin);
    }

    private MockAuthService() {
    }

    public static boolean login(
            String email,
            String password) {

        if (email == null || password == null) {
            return false;
        }

        RegistrationData user = USERS.get(
                email.trim().toLowerCase());

        return user != null
                && user.getPassword().equals(password);
    }

    public static boolean register(
            RegistrationData data) {

        if (data == null
                || data.getEmail() == null
                || data.getPassword() == null) {

            return false;
        }

        String email = data.getEmail()
                .trim()
                .toLowerCase();

        if (USERS.containsKey(email)) {
            return false;
        }

        data.setEmail(email);

        USERS.put(
                email,
                data);

        return true;
    }

    public static boolean userExists(
            String email) {

        if (email == null) {
            return false;
        }

        return USERS.containsKey(
                email.trim().toLowerCase());
    }

    public static boolean sendPasswordReset(
            String email) {

        return userExists(email);
    }
}