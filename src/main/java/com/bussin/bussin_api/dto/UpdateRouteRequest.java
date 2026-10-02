package com.bussin.bussin_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateRouteRequest {

    @NotBlank(message = "Route identifier is required")
    @Size(max = 100, message = "Route identifier must be at most 100 characters")
    private String routeIdentifier;

    @NotBlank(message = "Origin is required")
    @Size(max = 100, message = "Origin must be at most 100 characters")
    private String origin;

    @NotBlank(message = "Destination is required")
    @Size(max = 100, message = "Destination must be at most 100 characters")
    private String destination;

    @Size(max = 255, message = "Description must be at most 255 characters")
    private String description;

    private boolean active;

    public UpdateRouteRequest() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}