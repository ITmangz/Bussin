package com.bussin.bussin_api.dto;

import java.time.LocalDateTime;

import com.bussin.bussin_api.entity.TripStatus;

public record TripResponse(
        Long id,
        Long busId,
        String busPlateNumber,
        Integer busCapacity,
        Long routeId,
        String routeIdentifier,
        Long employeeId,
        String employeeName,
        LocalDateTime scheduledDeparture,
        LocalDateTime scheduledArrival,
        TripStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
