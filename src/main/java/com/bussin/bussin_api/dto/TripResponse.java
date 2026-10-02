package com.bussin.bussin_api.dto;

import java.time.LocalDateTime;

public class TripResponse {

    private Long id;
    private Long busId;
    private String busPlateNumber;
    private Long routeId;
    private String routeIdentifier;
    private LocalDateTime scheduledDeparture;
    private LocalDateTime scheduledArrival;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TripResponse(
            Long id,
            Long busId,
            String busPlateNumber,
            Long routeId,
            String routeIdentifier,
            LocalDateTime scheduledDeparture,
            LocalDateTime scheduledArrival,
            String status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.id = id;
        this.busId = busId;
        this.busPlateNumber = busPlateNumber;
        this.routeId = routeId;
        this.routeIdentifier = routeIdentifier;
        this.scheduledDeparture = scheduledDeparture;
        this.scheduledArrival = scheduledArrival;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getBusId() {
        return busId;
    }

    public String getBusPlateNumber() {
        return busPlateNumber;
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