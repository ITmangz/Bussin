package com.bussin.bussin_api.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateRouteRequest {

    @NotBlank(message = "Route identifier is required")
    @Size(max = 100, message = "Route identifier must be at most 100 characters")
    private String routeIdentifier;

    @NotBlank(message = "Origin is required")
    @Size(max = 100, message = "Origin must be at most 100 characters")
    private String origin;

    @NotBlank(message = "Destination is required")
    @Size(max = 100, message = "Destination must be at most 100 characters")
    private String destination;

    @NotNull(message = "Distance is required")
    @DecimalMin(value = "0.01", message = "Distance must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Distance must have at most 2 decimal places")
    private BigDecimal distanceKm;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    private Integer durationMinutes;

    @NotNull(message = "Base fare is required")
    @DecimalMin(value = "0.01", message = "Base fare must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Base fare must have at most 2 decimal places")
    private BigDecimal baseFare;

    @Size(max = 255, message = "Description must be at most 255 characters")
    private String description;

    public CreateRouteRequest() {
    }

    public String getRouteIdentifier() {
        return routeIdentifier;
    }

    public void setRouteIdentifier(String routeIdentifier) {
        this.routeIdentifier = routeIdentifier;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public BigDecimal getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(BigDecimal distanceKm) {
        this.distanceKm = distanceKm;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public BigDecimal getBaseFare() {
        return baseFare;
    }

    public void setBaseFare(BigDecimal baseFare) {
        this.baseFare = baseFare;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}