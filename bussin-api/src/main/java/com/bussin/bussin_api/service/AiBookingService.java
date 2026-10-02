package com.bussin.bussin_api.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bussin.bussin_api.entity.BookingStatus;
import com.bussin.bussin_api.entity.Trip;
import com.bussin.bussin_api.repository.BookingRepository;
import com.bussin.bussin_api.repository.TripRepository;

@Service
public class AiBookingService {

    private final TripService tripService;
    private final TripRepository tripRepository;
    private final BookingRepository bookingRepository;

    public AiBookingService(
            TripService tripService,
            TripRepository tripRepository,
            BookingRepository bookingRepository) {

        this.tripService = tripService;
        this.tripRepository = tripRepository;
        this.bookingRepository = bookingRepository;
    }

    public List<com.bussin.bussin_api.dto.TripResponse> searchTrips(
            String origin,
            String destination,
            String date,
            String time) {

        if (origin == null || origin.isBlank()) {
            throw new IllegalArgumentException("Origin is required.");
        }

        if (destination == null || destination.isBlank()) {
            throw new IllegalArgumentException("Destination is required.");
        }

        if (date == null || date.isBlank()) {
            throw new IllegalArgumentException("Travel date is required.");
        }

        LocalDate travelDate;

        try {
            travelDate = LocalDate.parse(date);
        } catch (Exception ex) {
            throw new IllegalArgumentException(
                    "Travel date must use YYYY-MM-DD format.");
        }

        LocalTime requestedTime = null;

        if (time != null && !time.isBlank()) {
            try {
                requestedTime = LocalTime.parse(time);
            } catch (Exception ex) {
                throw new IllegalArgumentException(
                        "Travel time must use HH:mm format.");
            }
        }

        LocalDateTime from;
        LocalDateTime to;

        if (requestedTime == null) {
            from = travelDate.atStartOfDay();
            to = travelDate.atTime(LocalTime.MAX);
        } else {
            from = travelDate.atTime(requestedTime).minusHours(2);
            to = travelDate.atTime(requestedTime).plusHours(2);
        }

        return tripService.searchTrips(
                origin.trim(),
                destination.trim(),
                from,
                to);
    }

    public boolean isSeatAvailable(
            Long tripId,
            String seatNumber) {

        Trip trip = getTrip(tripId);

        String normalizedSeat = normalizeSeat(seatNumber);

        validateSeatNumber(
                normalizedSeat,
                trip.getBus().getCapacity());

        return !bookingRepository
                .existsByTripIdAndSeatNumberAndStatusIn(
                        tripId,
                        normalizedSeat,
                        List.of(
                                BookingStatus.PENDING,
                                BookingStatus.CONFIRMED));
    }

    public List<String> getAvailableSeats(Long tripId) {

        Trip trip = getTrip(tripId);

        int capacity = trip.getBus().getCapacity();

        List<String> availableSeats = new ArrayList<>();

        List<BookingStatus> activeStatuses = List.of(
                BookingStatus.PENDING,
                BookingStatus.CONFIRMED);

        for (int position = 1; position <= capacity; position++) {

            int row = ((position - 1) / 4) + 1;

            int seatIndex = (position - 1) % 4;

            char letter = (char) ('A' + seatIndex);

            String seatNumber = row + String.valueOf(letter);

            boolean occupied = bookingRepository
                    .existsByTripIdAndSeatNumberAndStatusIn(
                            tripId,
                            seatNumber,
                            activeStatuses);

            if (!occupied) {
                availableSeats.add(seatNumber);
            }
        }

        return availableSeats;
    }

    public Trip getTrip(Long tripId) {

        if (tripId == null) {
            throw new IllegalArgumentException(
                    "Trip ID is required.");
        }

        return tripRepository
                .findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Trip not found."));
    }

    private String normalizeSeat(String seatNumber) {

        if (seatNumber == null
                || seatNumber.isBlank()) {

            throw new IllegalArgumentException(
                    "Seat number is required.");
        }

        return seatNumber
                .trim()
                .toUpperCase();
    }

    private void validateSeatNumber(
            String seatNumber,
            int capacity) {

        if (!seatNumber.matches("\\d+[A-D]")) {
            throw new IllegalArgumentException(
                    "Seat number must use a format such as 1A, 12B, or 20D.");
        }

        int row;

        try {
            row = Integer.parseInt(
                    seatNumber.substring(
                            0,
                            seatNumber.length() - 1));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(
                    "Invalid seat number.");
        }

        int seatIndex = seatNumber.charAt(
                seatNumber.length() - 1) - 'A';

        int seatPosition = ((row - 1) * 4)
                + seatIndex
                + 1;

        if (row < 1
                || seatPosition > capacity) {

            throw new IllegalArgumentException(
                    "Seat number is outside the bus capacity.");
        }
    }
}