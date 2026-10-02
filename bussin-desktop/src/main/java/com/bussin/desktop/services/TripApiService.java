package com.bussin.desktop.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;

public final class TripApiService {

        private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        private static final Gson GSON = new GsonBuilder()
                        .registerTypeAdapter(
                                        LocalDateTime.class,
                                        (JsonDeserializer<LocalDateTime>) (json, type, context) -> LocalDateTime.parse(
                                                        json.getAsString(),
                                                        DATE_TIME_FORMATTER))
                        .create();

        private TripApiService() {
        }

        public static List<TripResponse> getAllTrips()
                        throws Exception {

                var response = ApiClient.get("/trips");

                if (response.statusCode() != 200) {
                        throw new IllegalStateException(
                                        "Failed to retrieve trips. HTTP "
                                                        + response.statusCode()
                                                        + ": "
                                                        + response.body());
                }

                TripResponse[] trips = GSON.fromJson(
                                response.body(),
                                TripResponse[].class);

                return Arrays.asList(trips);
        }

        public static TripResponse getTrip(
                        long tripId)
                        throws Exception {

                var response = ApiClient.get(
                                "/trips/" + tripId);

                if (response.statusCode() != 200) {
                        throw new IllegalStateException(
                                        "Failed to retrieve trip. HTTP "
                                                        + response.statusCode()
                                                        + ": "
                                                        + response.body());
                }

                return GSON.fromJson(
                                response.body(),
                                TripResponse.class);
        }

        public static TripResponse createTrip(
                        long busId,
                        long routeId,
                        LocalDateTime scheduledDeparture,
                        LocalDateTime scheduledArrival,
                        String status)
                        throws Exception {

                JsonObject requestBody = new JsonObject();

                requestBody.addProperty(
                                "busId",
                                busId);

                requestBody.addProperty(
                                "routeId",
                                routeId);

                requestBody.addProperty(
                                "scheduledDeparture",
                                scheduledDeparture.format(
                                                DATE_TIME_FORMATTER));

                requestBody.addProperty(
                                "scheduledArrival",
                                scheduledArrival.format(
                                                DATE_TIME_FORMATTER));

                if (status != null && !status.isBlank()) {
                        requestBody.addProperty(
                                        "status",
                                        status);
                }

                var response = ApiClient.post(
                                "/trips",
                                GSON.toJson(requestBody));

                if (response.statusCode() != 201) {
                        throw new IllegalStateException(
                                        "Failed to create trip. HTTP "
                                                        + response.statusCode()
                                                        + ": "
                                                        + response.body());
                }

                return GSON.fromJson(
                                response.body(),
                                TripResponse.class);
        }

        public static TripResponse updateTrip(
                        long tripId,
                        long busId,
                        long routeId,
                        LocalDateTime scheduledDeparture,
                        LocalDateTime scheduledArrival,
                        String status)
                        throws Exception {

                JsonObject requestBody = new JsonObject();

                requestBody.addProperty(
                                "busId",
                                busId);

                requestBody.addProperty(
                                "routeId",
                                routeId);

                requestBody.addProperty(
                                "scheduledDeparture",
                                scheduledDeparture.format(
                                                DATE_TIME_FORMATTER));

                requestBody.addProperty(
                                "scheduledArrival",
                                scheduledArrival.format(
                                                DATE_TIME_FORMATTER));

                requestBody.addProperty(
                                "status",
                                status);

                var response = ApiClient.put(
                                "/trips/" + tripId,
                                GSON.toJson(requestBody));

                if (response.statusCode() != 200) {
                        throw new IllegalStateException(
                                        "Failed to update trip. HTTP "
                                                        + response.statusCode()
                                                        + ": "
                                                        + response.body());
                }

                return GSON.fromJson(
                                response.body(),
                                TripResponse.class);
        }

        public static void deleteTrip(
                        long tripId)
                        throws Exception {

                var response = ApiClient.delete(
                                "/trips/" + tripId);

                if (response.statusCode() != 204) {
                        throw new IllegalStateException(
                                        "Failed to delete trip. HTTP "
                                                        + response.statusCode()
                                                        + ": "
                                                        + response.body());
                }
        }

        public static void testConnection() throws Exception {
                List<TripResponse> trips = getAllTrips();

                System.out.println(
                                "Trip API returned "
                                                + trips.size()
                                                + " trips.");

                for (TripResponse trip : trips) {
                        System.out.println(
                                        "Trip "
                                                        + trip.getId()
                                                        + " | "
                                                        + trip.getRouteIdentifier()
                                                        + " | "
                                                        + trip.getBusPlateNumber()
                                                        + " | "
                                                        + trip.getStatus());
                }
        }

        public static final class TripResponse {

                private Long id;
                private Long busId;
                private String busPlateNumber;
                private Integer busCapacity;
                private Long routeId;
                private String routeIdentifier;
                private LocalDateTime scheduledDeparture;
                private LocalDateTime scheduledArrival;
                private String status;
                private LocalDateTime createdAt;
                private LocalDateTime updatedAt;

                public Long getId() {
                        return id;
                }

                public Long getBusId() {
                        return busId;
                }

                public String getBusPlateNumber() {
                        return busPlateNumber;
                }

                public Integer getBusCapacity() {
                        return busCapacity;
                }

                public Long getRouteId() {
                        return routeId;
                }

                public String getRouteIdentifier() {
                        return routeIdentifier;
                }

                public LocalDateTime getScheduledDeparture() {
                        return scheduledDeparture;
                }

                public LocalDateTime getScheduledArrival() {
                        return scheduledArrival;
                }

                public String getStatus() {
                        return status;
                }

                public LocalDateTime getCreatedAt() {
                        return createdAt;
                }

                public LocalDateTime getUpdatedAt() {
                        return updatedAt;
                }
        }
}