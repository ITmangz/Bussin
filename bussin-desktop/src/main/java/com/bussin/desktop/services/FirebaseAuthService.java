package com.bussin.desktop.services;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

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

        private static final String ACCOUNT_LOOKUP_URL = "https://identitytoolkit.googleapis.com/v1/accounts:lookup?key="
                        + API_KEY;

        private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

        private FirebaseAuthService() {
        }

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
                                                                requestBody.toString()))
                                .build();

                HttpResponse<String> response = HTTP_CLIENT.send(
                                request,
                                HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {

                        throw new IllegalStateException(
                                        extractFirebaseError(
                                                        response.body()));
                }

                JsonObject responseBody = JsonParser.parseString(
                                response.body())
                                .getAsJsonObject();

                String idToken = responseBody.get("idToken")
                                .getAsString();

                String refreshToken = responseBody.get("refreshToken")
                                .getAsString();

                String firebaseUid = responseBody.get("localId")
                                .getAsString();

                String responseEmail = responseBody.has("email")
                                ? responseBody.get("email")
                                                .getAsString()
                                : email;

                AuthSession.start(
                                idToken,
                                refreshToken,
                                firebaseUid,
                                responseEmail);

                return true;
        }

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
                                                                requestBody.toString()))
                                .build();

                HttpResponse<String> response = HTTP_CLIENT.send(
                                request,
                                HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {

                        throw new IllegalStateException(
                                        extractFirebaseError(
                                                        response.body()));
                }

                JsonObject responseBody = JsonParser.parseString(
                                response.body())
                                .getAsJsonObject();

                String idToken = responseBody.get("idToken")
                                .getAsString();

                String refreshToken = responseBody.get("refreshToken")
                                .getAsString();

                String firebaseUid = responseBody.get("localId")
                                .getAsString();

                String responseEmail = responseBody.has("email")
                                ? responseBody.get("email")
                                                .getAsString()
                                : email;

                AuthSession.start(
                                idToken,
                                refreshToken,
                                firebaseUid,
                                responseEmail);

                return true;
        }

        public static boolean authenticateWithIdToken(
                        String idToken)
                        throws IOException, InterruptedException {

                if (idToken == null
                                || idToken.isBlank()) {

                        throw new IllegalArgumentException(
                                        "Firebase ID token is missing.");
                }

                JsonObject requestBody = new JsonObject();

                requestBody.addProperty(
                                "idToken",
                                idToken);

                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(
                                                ACCOUNT_LOOKUP_URL))
                                .header(
                                                "Content-Type",
                                                "application/json")
                                .POST(
                                                HttpRequest.BodyPublishers.ofString(
                                                                requestBody.toString()))
                                .build();

                HttpResponse<String> response = HTTP_CLIENT.send(
                                request,
                                HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {

                        throw new IllegalStateException(
                                        extractFirebaseError(
                                                        response.body()));
                }

                JsonObject responseBody = JsonParser.parseString(
                                response.body())
                                .getAsJsonObject();

                if (!responseBody.has("users")
                                || responseBody.get("users")
                                                .getAsJsonArray()
                                                .isEmpty()) {

                        throw new IllegalStateException(
                                        "Firebase user was not found.");
                }

                JsonObject user = responseBody.getAsJsonArray(
                                "users")
                                .get(0)
                                .getAsJsonObject();

                String firebaseUid = user.get("localId")
                                .getAsString();

                String email = user.has("email")
                                ? user.get("email")
                                                .getAsString()
                                : null;

                AuthSession.start(
                                idToken,
                                null,
                                firebaseUid,
                                email);

                return true;
        }

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
                                .uri(URI.create(
                                                PASSWORD_RESET_URL))
                                .header(
                                                "Content-Type",
                                                "application/json")
                                .POST(
                                                HttpRequest.BodyPublishers.ofString(
                                                                requestBody.toString()))
                                .build();

                HttpResponse<String> response = HTTP_CLIENT.send(
                                request,
                                HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {

                        throw new IllegalStateException(
                                        extractFirebaseError(
                                                        response.body()));
                }

                return true;
        }

        public static void logout() {

                AuthSession.clear();
        }

        private static String extractFirebaseError(
                        String responseBody) {

                try {

                        JsonObject json = JsonParser.parseString(
                                        responseBody)
                                        .getAsJsonObject();

                        if (json.has("error")) {

                                JsonObject error = json.getAsJsonObject(
                                                "error");

                                if (error.has("message")) {

                                        return error.get(
                                                        "message")
                                                        .getAsString();
                                }
                        }

                } catch (Exception ignored) {
                }

                return "Firebase authentication request failed.";
        }
}