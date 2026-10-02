package com.bussin.desktop.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonDeserializer;

public final class QueueApiService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(
                    LocalDateTime.class,
                    (JsonDeserializer<LocalDateTime>) (json, type, context) -> LocalDateTime.parse(
                            json.getAsString(),
                            DATE_TIME_FORMATTER))
            .create();

    private QueueApiService() {
    }

    public static QueueResponse joinQueue(
            long tripId)
            throws Exception {

        JsonObject requestBody = new JsonObject();

        requestBody.addProperty(
                "tripId",
                tripId);

        var response = ApiClient.post(
                "/queue",
                GSON.toJson(requestBody));

        if (response.statusCode() != 201) {
            throw new IllegalStateException(
                    "Failed to join queue. HTTP "
                            + response.statusCode()
                            + ": "
                            + response.body());
        }

        return GSON.fromJson(
                response.body(),
                QueueResponse.class);
    }

    public static List<QueueResponse> getTripQueue(
            long tripId)
            throws Exception {

        var response = ApiClient.get(
                "/queue/trip/" + tripId);

        if (response.statusCode() != 200) {
            throw new IllegalStateException(
                    "Failed to retrieve queue. HTTP "
                            + response.statusCode()
                            + ": "
                            + response.body());
        }

        QueueResponse[] entries = GSON.fromJson(
                response.body(),
                QueueResponse[].class);

        return Arrays.asList(entries);
    }

    public static QueueResponse getQueueEntry(
            long queueEntryId)
            throws Exception {

        var response = ApiClient.get(
                "/queue/" + queueEntryId);

        if (response.statusCode() != 200) {
            throw new IllegalStateException(
                    "Failed to retrieve queue entry. HTTP "
                            + response.statusCode()
                            + ": "
                            + response.body());
        }

        return GSON.fromJson(
                response.body(),
                QueueResponse.class);
    }

    public static QueueResponse getMyQueueEntry(
            long tripId)
            throws Exception {

        var response = ApiClient.get(
                "/queue/trip/" + tripId + "/me");

        if (response.statusCode() != 200) {
            throw new IllegalStateException(
                    "Failed to retrieve your queue entry. HTTP "
                            + response.statusCode()
                            + ": "
                            + response.body());
        }

        return GSON.fromJson(
                response.body(),
                QueueResponse.class);
    }

    public static QueueResponse updateQueueStatus(
            long queueEntryId,
            String status)
            throws Exception {

        JsonObject requestBody = new JsonObject();

        requestBody.addProperty(
                "status",
                status);

        var response = ApiClient.put(
                "/queue/" + queueEntryId + "/status",
                GSON.toJson(requestBody));

        if (response.statusCode() != 200) {
            throw new IllegalStateException(
                    "Failed to update queue status. HTTP "
                            + response.statusCode()
                            + ": "
                            + response.body());
        }

        return GSON.fromJson(
                response.body(),
                QueueResponse.class);
    }

    public static QueueResponse cancelQueueEntry(
            long queueEntryId)
            throws Exception {

        var response = ApiClient.delete(
                "/queue/" + queueEntryId);

        if (response.statusCode() != 200) {
            throw new IllegalStateException(
                    "Failed to cancel queue entry. HTTP "
                            + response.statusCode()
                            + ": "
                            + response.body());
        }

        return GSON.fromJson(
                response.body(),
                QueueResponse.class);
    }

    public static class QueueResponse {

        private Long id;
        private Long tripId;
        private Long commuterId;
        private String commuterName;
        private String commuterEmail;
        private Integer queueNumber;
        private String status;
        private LocalDateTime joinedAt;
        private LocalDateTime calledAt;
        private LocalDateTime boardedAt;
        private LocalDateTime updatedAt;

        public Long getId() {
            return id;
        }

        public Long getTripId() {
            return tripId;
        }

        public Long getCommuterId() {
            return commuterId;
        }

        public String getCommuterName() {
            return commuterName;
        }

        public String getCommuterEmail() {
            return commuterEmail;
        }

        public Integer getQueueNumber() {
            return queueNumber;
        }

        public String getStatus() {
            return status;
        }

        public LocalDateTime getJoinedAt() {
            return joinedAt;
        }

        public LocalDateTime getCalledAt() {
            return calledAt;
        }

        public LocalDateTime getBoardedAt() {
            return boardedAt;
        }

        public LocalDateTime getUpdatedAt() {
            return updatedAt;
        }
    }
}