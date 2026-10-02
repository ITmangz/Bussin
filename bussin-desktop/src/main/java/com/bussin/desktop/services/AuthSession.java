package com.bussin.desktop.services;

public final class AuthSession {

    private static String idToken;
    private static String refreshToken;
    private static String firebaseUid;
    private static String email;

    private AuthSession() {
    }

    public static void start(
            String idToken,
            String refreshToken,
            String firebaseUid,
            String email) {

        AuthSession.idToken = idToken;
        AuthSession.refreshToken = refreshToken;
        AuthSession.firebaseUid = firebaseUid;
        AuthSession.email = email;
    }

    public static boolean isAuthenticated() {
        return idToken != null && !idToken.isBlank();
    }

    public static String getIdToken() {
        return idToken;
    }

    public static String getRefreshToken() {
        return refreshToken;
    }

    public static String getFirebaseUid() {
        return firebaseUid;
    }

    public static String getEmail() {
        return email;
    }

    public static void printIdToken() {
        System.out.println("FIREBASE ID TOKEN:");
        System.out.println(idToken);
    }

    public static void clear() {
        idToken = null;
        refreshToken = null;
        firebaseUid = null;
        email = null;
    }
}