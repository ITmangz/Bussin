package com.bussin.bussin_api.dto;

import com.bussin.bussin_api.entity.BookingStatus;

import jakarta.validation.constraints.NotNull;

public class UpdateBookingStatusRequest {

    @NotNull
    private BookingStatus status;

    public UpdateBookingStatusRequest() {
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }
}
