package com.bussin.desktop.services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public final class UserApiService {

        private static final Gson GSON = new Gson();

        private UserApiService() {
        }

        // ============================================================
        // GET CURRENT USER
        // ============================================================

        public static UserResponse getCurrentUser()
                        throws Exception {

                var response = ApiClient.get(
                                "/users/me");

                if (response.statusCode() != 200) {

                        throw new IllegalStateException(
                                        "Failed to retrieve current user. "
                                                        + "HTTP "
                                                        + response.statusCode()
                                                        + ": "
                                                        + response.body());
                }

                return GSON.fromJson(
                                response.body(),
                                UserResponse.class);
        }

        // ============================================================
        // CREATE CURRENT USER
        // ============================================================

        public static UserResponse createCurrentUser(
                        String firstName,
                        String lastName)
                        throws Exception {

                JsonObject requestBody = new JsonObject();

                requestBody.addProperty(
                                "firstName",
                                firstName);

                requestBody.addProperty(
                                "lastName",
                                lastName);

                var response = ApiClient.post(
                                "/users",
                                GSON.toJson(requestBody));

                if (response.statusCode() != 200
                                && response.statusCode() != 201) {

                        throw new IllegalStateException(
                                        "Failed to create BUSSIN user profile. "
                                                        + "HTTP "
                                                        + response.statusCode()
                                                        + ": "
                                                        + response.body());
                }

                return GSON.fromJson(
                                response.body(),
                                UserResponse.class);
        }

        // ============================================================
        // UPDATE CURRENT USER
        // ============================================================

        public static UserResponse updateCurrentUser(
                        String firstName,
                        String lastName)
                        throws Exception {

                JsonObject requestBody = new JsonObject();

                requestBody.addProperty(
                                "firstName",
                                firstName);

                requestBody.addProperty(
                                "lastName",
                                lastName);

                var response = ApiClient.put(
                                "/users/me",
                                GSON.toJson(requestBody));

                if (response.statusCode() != 200) {

                        throw new IllegalStateException(
                                        "Failed to update current user. "
                                                        + "HTTP "
                                                        + response.statusCode()
                                                        + ": "
                                                        + response.body());
                }

                return GSON.fromJson(
                                response.body(),
                                UserResponse.class);
        }

        // ============================================================
        // USER RESPONSE
        // ============================================================

        public static class UserResponse {

                private Long id;
                private String firebaseUid;
                private String email;
                private String firstName;
                private String lastName;
                private String role;

                public Long getId() {
                        return id;
                }

                public String getFirebaseUid() {
                        return firebaseUid;
                }

                public String getEmail() {
                        return email;
                }

                public String getFirstName() {
                        return firstName;
                }

                public String getLastName() {
                        return lastName;
                }

                public String getRole() {
                        return role;
                }
        }
}