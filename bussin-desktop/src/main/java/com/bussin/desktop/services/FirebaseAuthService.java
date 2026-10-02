package com.bussin.desktop.services;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public final class FirebaseAuthService {

        private static final String API_KEY = "AIzaSyAHq7KEgGZnN6MPImmniDN2MuXgFiaDdSQ";

        private static final String SIGN_IN_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key="
                        + API_KEY;

        private static final String SIGN_UP_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key="
                        + API_KEY;

        private static final String PASSWORD_RESET_URL = "https://identitytoolkit.googleapis.com/v1/accounts:sendOobCode?key="
                        + API_KEY;

        private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

        private static final Gson GSON = new Gson();

        private FirebaseAuthService() {
        }

        // ============================================================
        // LOGIN
        // ============================================================

        public static boolean login(
                        String email,
                        String password)
                        throws IOException, InterruptedException {

                JsonObject requestBody = new JsonObject();

                requestBody.addProperty(
                                "email",
                                email);

                requestBody.addProperty(
                                "password",
                                password);

                requestBody.addProperty(
                                "returnSecureToken",
                                true);

                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(SIGN_IN_URL))
                                .header(
                                                "Content-Type",
                                                "application/json")
                                .POST(
                                                HttpRequest.BodyPublishers.ofString(
                                                                GSON.toJson(requestBody)))
                                .build();

                HttpResponse<String> response = HTTP_CLIENT.send(
                                request,
                                HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {

                        System.out.println(
                                        "Firebase Login HTTP Status: "
                                                        + response.statusCode());

                        System.out.println(
                                        "Firebase Login Response: "
                                                        + response.body());

                        return false;
                }

                JsonObject json = JsonParser.parseString(
                                response.body())
                                .getAsJsonObject();

                String idToken = json.get("idToken")
                                .getAsString();

                String refreshToken = json.get("refreshToken")
                                .getAsString();

                String firebaseUid = json.get("localId")
                                .getAsString();

                String authenticatedEmail = json.get("email")
                                .getAsString();

                AuthSession.start(
                                idToken,
                                refreshToken,
                                firebaseUid,
                                authenticatedEmail);

                return true;
        }

        // ============================================================
        // PASSWORD RESET
        // ============================================================

        public static boolean sendPasswordReset(
                        String email)
                        throws IOException, InterruptedException {

                JsonObject requestBody = new JsonObject();

                requestBody.addProperty(
                                "requestType",
                                "PASSWORD_RESET");

                requestBody.addProperty(
                                "email",
                                email);

                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(PASSWORD_RESET_URL))
                                .header(
                                                "Content-Type",
                                                "application/json")
                                .POST(
                                                HttpRequest.BodyPublishers.ofString(
                                                                GSON.toJson(requestBody)))
                                .build();

                HttpResponse<String> response = HTTP_CLIENT.send(
                                request,
                                HttpResponse.BodyHandlers.ofString());

                System.out.println(
                                "Firebase Password Reset HTTP Status: "
                                                + response.statusCode());

                System.out.println(
                                "Firebase Password Reset Response: "
                                                + response.body());

                if (response.statusCode() != 200) {
                        return false;
                }

                return true;
        }

        // ============================================================
        // REGISTER
        // ============================================================

        public static boolean register(
                        String email,
                        String password)
                        throws IOException, InterruptedException {

                JsonObject requestBody = new JsonObject();

                requestBody.addProperty(
                                "email",
                                email);

                requestBody.addProperty(
                                "password",
                                password);

                requestBody.addProperty(
                                "returnSecureToken",
                                true);

                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(SIGN_UP_URL))
                                .header(
                                                "Content-Type",
                                                "application/json")
                                .POST(
                                                HttpRequest.BodyPublishers.ofString(
                                                                GSON.toJson(requestBody)))
                                .build();

                HttpResponse<String> response = HTTP_CLIENT.send(
                                request,
                                HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {

                        System.out.println(
                                        "Firebase Registration HTTP Status: "
                                                        + response.statusCode());

                        System.out.println(
                                        "Firebase Registration Response: "
                                                        + response.body());

                        return false;
                }

                JsonObject json = JsonParser.parseString(
                                response.body())
                                .getAsJsonObject();

                String idToken = json.get("idToken")
                                .getAsString();

                String refreshToken = json.get("refreshToken")
                                .getAsString();

                String firebaseUid = json.get("localId")
                                .getAsString();

                String registeredEmail = json.get("email")
                                .getAsString();

                AuthSession.start(
                                idToken,
                                refreshToken,
                                firebaseUid,
                                registeredEmail);

                return true;
        }

        // ============================================================
        // LOGOUT
        // ============================================================

        public static void logout() {
                AuthSession.clear();
        }
}