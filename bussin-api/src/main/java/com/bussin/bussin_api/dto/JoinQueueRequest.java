package com.bussin.bussin_api.dto;

import jakarta.validation.constraints.NotNull;

public class JoinQueueRequest {

    @NotNull(message = "Trip ID is required")
    private Long tripId;

    public JoinQueueRequest() {
    }

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }
}