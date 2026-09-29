package com.bussin.desktop.services;

import java.util.HashMap;
import java.util.Map;

import com.bussin.desktop.ui.auth.RegistrationData;

public class MockAuthService {

    private static final Map<String, RegistrationData> USERS = new HashMap<>();

    static {

        // ============================================================
        // ADMIN
        // ============================================================

        RegistrationData admin = new RegistrationData();

        admin.setEmail("admin@bussin.com");
        admin.setPassword("admin123");
        admin.setRole("ADMIN");

        admin.setFirstName("BUSSIN");
        admin.setLastName("Administrator");
        admin.setGender("Prefer not to say");
        admin.setAge("25");

        USERS.put(
                admin.getEmail().toLowerCase(),
                admin);

        // ============================================================
        // EMPLOYEE
        // ============================================================

        RegistrationData employee = new RegistrationData();

        employee.setEmail("employee@bussin.com");
        employee.setPassword("employee123");
        employee.setRole("EMPLOYEE");

        employee.setFirstName("BUSSIN");
        employee.setLastName("Employee");
        employee.setGender("Prefer not to say");
        employee.setAge("25");

        USERS.put(
                employee.getEmail().toLowerCase(),
                employee);

        // ============================================================
        // USER
        // ============================================================

        RegistrationData user = new RegistrationData();

        user.setEmail("user@bussin.com");
        user.setPassword("user123");
        user.setRole("USER");

        user.setFirstName("BUSSIN");
        user.setLastName("User");
        user.setGender("Prefer not to say");
        user.setAge("25");

        USERS.put(
                user.getEmail().toLowerCase(),
                user);
    }

    private MockAuthService() {
    }

    // ================================================================
    // LOGIN
    // ================================================================

    public static boolean login(
            String email,
            String password) {

        if (email == null
                || password == null) {

            return false;
        }

        RegistrationData user = USERS.get(
                email.trim().toLowerCase());

        return user != null
                && user.getPassword().equals(password);
    }

    // ================================================================
    // GET USER
    // ================================================================

    public static RegistrationData getUser(
            String email) {

        if (email == null) {
            return null;
        }

        return USERS.get(
                email.trim().toLowerCase());
    }

    // ================================================================
    // REGISTER
    // ================================================================

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

        /*
         * All accounts created through the normal
         * registration process are regular USERS.
         */
        if (data.getRole() == null
                || data.getRole().isBlank()) {

            data.setRole("USER");
        }

        USERS.put(
                email,
                data);

        return true;
    }

    // ================================================================
    // USER EXISTS
    // ================================================================

    public static boolean userExists(
            String email) {

        if (email == null) {
            return false;
        }

        return USERS.containsKey(
                email.trim().toLowerCase());
    }

    // ================================================================
    // PASSWORD RESET
    // ================================================================

    public static boolean sendPasswordReset(
            String email) {

        return userExists(email);
    }
}