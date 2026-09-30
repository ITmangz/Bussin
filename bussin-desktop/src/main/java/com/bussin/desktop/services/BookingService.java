package com.bussin.desktop.services;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bussin.desktop.data.TripStore;
import com.bussin.desktop.ui.flow.BookingStore;
import com.bussin.desktop.ui.flow.BookingStore.UserBooking;
import com.bussin.desktop.ui.flow.CommuterTrip;

/**
 * Authoritative booking transaction.
 *
 * Only identifiers travel in the request (user, trip id, seat, passenger
 * details). Trip data and fare are always re-read from {@link TripStore}, and
 * the seat claim plus booking insert happen atomically under one lock.
 *
 * The request key makes a retried request idempotent: the same key always
 * returns the booking created the first time instead of creating another.
 */
public final class BookingService {

    private static final Logger LOG = Logger.getLogger(BookingService.class.getName());

    private static final Object LOCK = new Object();

    private static final Map<String, UserBooking> BY_REQUEST_KEY = new HashMap<>();

    private BookingService() {
    }

    public record CreateBookingRequest(
            String requestKey,
            String userEmail,
            String tripId,
            String seat,
            String passengerName,
            String passengerPhone,
            String passengerEmail) {
    }

    public static UserBooking createBooking(CreateBookingRequest request)
            throws BookingException {

        if (request == null) {
            throw new BookingException(BookingException.Kind.INVALID_REQUEST,
                    "Booking request is missing.");
        }

        if (isBlank(request.userEmail())
                || !MockAuthService.userExists(request.userEmail().trim())) {
            throw new BookingException(BookingException.Kind.UNAUTHENTICATED,
                    "You must be signed in to book a trip.");
        }

        if (isBlank(request.tripId()) || isBlank(request.seat())
                || isBlank(request.passengerName())
                || isBlank(request.passengerPhone())
                || isBlank(request.passengerEmail())
                || !request.passengerEmail().contains("@")) {
            throw new BookingException(BookingException.Kind.INVALID_REQUEST,
                    "Some booking information is incomplete or invalid.");
        }

        synchronized (LOCK) {

            String key = request.requestKey();

            if (!isBlank(key) && BY_REQUEST_KEY.containsKey(key)) {
                return BY_REQUEST_KEY.get(key);
            }

            CommuterTrip trip = TripStore.findById(request.tripId().trim());

            if (trip == null) {
                throw new BookingException(BookingException.Kind.NOT_FOUND,
                        "That trip could not be found.");
            }

            String seat = request.seat().trim().toUpperCase();

            if (!trip.isValidSeat(seat)) {
                throw new BookingException(BookingException.Kind.INVALID_REQUEST,
                        "That seat does not exist on this trip.");
            }

            // Atomic claim: returns false if another booking already holds it.
            if (!trip.reserveSeat(seat)) {
                throw new BookingException(BookingException.Kind.SEAT_UNAVAILABLE,
                        "That seat is no longer available. Please select another seat.");
            }

            try {
                UserBooking booking = new UserBooking(
                        BookingStore.generateBookingId(),
                        request.userEmail().trim(),
                        request.passengerName().trim(),
                        request.passengerPhone().trim(),
                        request.passengerEmail().trim(),
                        trip.getTripId(),
                        trip.getRoute(),
                        trip.getDeparture(),
                        trip.getArrival(),
                        trip.getBusNumber(),
                        seat,
                        trip.getFare(),
                        "Pending",
                        "Confirmed",
                        LocalDateTime.now());

                BookingStore.addBooking(booking);

                if (!isBlank(key)) {
                    BY_REQUEST_KEY.put(key, booking);
                }

                return booking;

            } catch (RuntimeException ex) {
                trip.releaseSeat(seat);
                LOG.log(Level.SEVERE, "Booking persistence failed", ex);
                throw new BookingException(BookingException.Kind.INTERNAL,
                        "Something went wrong while creating your booking.", ex);
            }
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
