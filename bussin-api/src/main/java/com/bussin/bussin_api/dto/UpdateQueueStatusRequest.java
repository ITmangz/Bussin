package com.bussin.bussin_api.dto;

import com.bussin.bussin_api.entity.QueueStatus;

import jakarta.validation.constraints.NotNull;

public class UpdateQueueStatusRequest {

    @NotNull(message = "Queue status is required")
    private QueueStatus status;

    public UpdateQueueStatusRequest() {
    }

    public QueueStatus getStatus() {
        return status;
    }

    public void setStatus(QueueStatus status) {
        this.status = status;
    }
}