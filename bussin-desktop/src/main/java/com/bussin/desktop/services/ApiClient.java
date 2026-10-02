package com.bussin.desktop.services;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public final class ApiClient {

        private static final String BASE_URL = "http://localhost:8081/api";

        private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

        private ApiClient() {
        }

        public static HttpResponse<String> get(
                        String endpoint)
                        throws IOException, InterruptedException {

                return send(
                                "GET",
                                endpoint,
                                null);
        }

        public static HttpResponse<String> post(
                        String endpoint,
                        String jsonBody)
                        throws IOException, InterruptedException {

                return send(
                                "POST",
                                endpoint,
                                jsonBody);
        }

        public static HttpResponse<String> put(
                        String endpoint,
                        String jsonBody)
                        throws IOException, InterruptedException {

                return send(
                                "PUT",
                                endpoint,
                                jsonBody);
        }

        public static HttpResponse<String> delete(
                        String endpoint)
                        throws IOException, InterruptedException {

                return send(
                                "DELETE",
                                endpoint,
                                null);
        }

        private static HttpResponse<String> send(
                        String method,
                        String endpoint,
                        String jsonBody)
                        throws IOException, InterruptedException {

                if (!AuthSession.isAuthenticated()) {

                        throw new IllegalStateException(
                                        "No authenticated Firebase session.");
                }

                String url = BASE_URL + endpoint;

                HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                                .uri(URI.create(url))
                                .header(
                                                "Authorization",
                                                "Bearer " + AuthSession.getIdToken())
                                .header(
                                                "Accept",
                                                "application/json");

                if (jsonBody != null) {

                        requestBuilder
                                        .header(
                                                        "Content-Type",
                                                        "application/json")
                                        .method(
                                                        method,
                                                        HttpRequest.BodyPublishers.ofString(
                                                                        jsonBody));

                } else {

                        requestBuilder
                                        .method(
                                                        method,
                                                        HttpRequest.BodyPublishers.noBody());
                }

                return HTTP_CLIENT.send(
                                requestBuilder.build(),
                                HttpResponse.BodyHandlers.ofString());
        }
}