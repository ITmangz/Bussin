package com.bussin.bussin_api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RouteResponse {

    private Long id;
    private String routeIdentifier;
    private String origin;
    private String destination;
    private BigDecimal distanceKm;
    private Integer durationMinutes;
    private BigDecimal baseFare;
    private String description;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RouteResponse(
            Long id,
            String routeIdentifier,
            String origin,
            String destination,
            BigDecimal distanceKm,
            Integer durationMinutes,
            BigDecimal baseFare,
            String description,
            boolean active,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.id = id;
        this.routeIdentifier = routeIdentifier;
        this.origin = origin;
        this.destination = destination;
        this.distanceKm = distanceKm;
        this.durationMinutes = durationMinutes;
        this.baseFare = baseFare;
        this.description = description;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getRouteIdentifier() {
        return routeIdentifier;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public BigDecimal getDistanceKm() {
        return distanceKm;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public BigDecimal getBaseFare() {
        return baseFare;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}