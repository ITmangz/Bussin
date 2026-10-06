package com.bussin.bussin_api.dto;

import java.time.LocalDateTime;

import com.bussin.bussin_api.entity.QueueEntry;

public class QueueResponse {

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

    public QueueResponse(
            Long id,
            Long tripId,
            Long commuterId,
            String commuterName,
            String commuterEmail,
            Integer queueNumber,
            String status,
            LocalDateTime joinedAt,
            LocalDateTime calledAt,
            LocalDateTime boardedAt,
            LocalDateTime updatedAt) {

        this.id = id;
        this.tripId = tripId;
        this.commuterId = commuterId;
        this.commuterName = commuterName;
        this.commuterEmail = commuterEmail;
        this.queueNumber = queueNumber;
        this.status = status;
        this.joinedAt = joinedAt;
        this.calledAt = calledAt;
        this.boardedAt = boardedAt;
        this.updatedAt = updatedAt;
    }

    public static QueueResponse from(
            QueueEntry entry) {

        String commuterName;
        String commuterEmail;
        Long commuterId = null;

        if (entry.getCommuter() == null) {
            commuterName = entry.getPassengerName() != null
                    ? entry.getPassengerName()
                    : entry.getBooking() == null
                            ? "Guest"
                            : entry.getBooking().getPassengerName();
            commuterEmail = entry.getPassengerEmail() != null
                    ? entry.getPassengerEmail()
                    : entry.getBooking() == null
                            ? null
                            : entry.getBooking().getPassengerEmail();
        } else {
            commuterId = entry.getCommuter().getId();
            commuterName = buildCommuterName(
                    entry.getCommuter().getFirstName(),
                    entry.getCommuter().getMiddleName(),
                    entry.getCommuter().getLastName());
            commuterEmail = entry.getCommuter().getEmail();
        }

        return new QueueResponse(
                entry.getId(),
                entry.getTrip().getId(),
                commuterId,
                commuterName,
                commuterEmail,
                entry.getQueueNumber(),
                entry.getStatus().name(),
                entry.getJoinedAt(),
                entry.getCalledAt(),
                entry.getBoardedAt(),
                entry.getUpdatedAt());
    }

    private static String buildCommuterName(
            String firstName,
            String middleName,
            String lastName) {

        StringBuilder name = new StringBuilder();

        appendNamePart(name, firstName);
        appendNamePart(name, middleName);
        appendNamePart(name, lastName);

        return name.toString();
    }

    private static void appendNamePart(
            StringBuilder name,
            String value) {

        if (value == null
                || value.isBlank()) {
            return;
        }

        if (name.length() > 0) {
            name.append(" ");
        }

        name.append(value);
    }

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
