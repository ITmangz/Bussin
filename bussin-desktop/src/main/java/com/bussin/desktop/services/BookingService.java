package com.bussin.desktop.services;

import com.bussin.desktop.ui.flow.BookingStore.UserBooking;

public final class BookingService {

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

    public static UserBooking createBooking(
            CreateBookingRequest request)
            throws BookingException {

        if (request == null) {
            throw new BookingException(
                    BookingException.Kind.INVALID_REQUEST,
                    "Booking request is missing.");
        }

        if (request.tripId() == null
                || request.tripId().isBlank()
                || request.seat() == null
                || request.seat().isBlank()
                || request.passengerName() == null
                || request.passengerName().isBlank()
                || request.passengerPhone() == null
                || request.passengerPhone().isBlank()
                || request.passengerEmail() == null
                || request.passengerEmail().isBlank()) {

            throw new BookingException(
                    BookingException.Kind.INVALID_REQUEST,
                    "Some booking information is incomplete or invalid.");
        }

        final long tripId;

        try {
            tripId = Long.parseLong(request.tripId().trim());
        } catch (NumberFormatException ex) {
            throw new BookingException(
                    BookingException.Kind.INVALID_REQUEST,
                    "Invalid trip ID.",
                    ex);
        }

        try {
            BookingApiService.BookingResponse response = BookingApiService.createBooking(
                    tripId,
                    request.seat().trim().toUpperCase(),
                    request.passengerName().trim(),
                    request.passengerPhone().trim(),
                    request.passengerEmail().trim());

            return toUserBooking(response);

        } catch (java.io.IOException | InterruptedException ex) {

            if (ex instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }

            throw new BookingException(
                    BookingException.Kind.INTERNAL,
                    "Unable to communicate with the BUSSIN API.",
                    ex);

        } catch (IllegalStateException ex) {

            String message = ex.getMessage();

            if (message != null) {

                if (message.contains("HTTP 401")) {
                    throw new BookingException(
                            BookingException.Kind.UNAUTHENTICATED,
                            "Your session has expired. Please sign in again.",
                            ex);
                }

                if (message.contains("HTTP 403")) {
                    throw new BookingException(
                            BookingException.Kind.FORBIDDEN,
                            "Your account is not authorized to create bookings.",
                            ex);
                }

                if (message.contains("HTTP 404")) {
                    throw new BookingException(
                            BookingException.Kind.NOT_FOUND,
                            "This trip is no longer available.",
                            ex);
                }

                if (message.contains("HTTP 409")) {
                    throw new BookingException(
                            BookingException.Kind.SEAT_UNAVAILABLE,
                            extractApiMessage(message),
                            ex);
                }

                if (message.contains("HTTP 400")) {
                    throw new BookingException(
                            BookingException.Kind.INVALID_REQUEST,
                            extractApiMessage(message),
                            ex);
                }
            }

            throw new BookingException(
                    BookingException.Kind.INTERNAL,
                    "Something went wrong while creating your booking.",
                    ex);
        }
    }

    private static UserBooking toUserBooking(
            BookingApiService.BookingResponse response) {

        String bookingId = response.getBookingReference();

        String route = response.getOrigin()
                + " → "
                + response.getDestination();

        String departure = response.getScheduledDeparture() == null
                ? "Not scheduled"
                : response.getScheduledDeparture().toString();

        String arrival = response.getScheduledArrival() == null
                ? "Not scheduled"
                : response.getScheduledArrival().toString();

        return new UserBooking(
                bookingId,
                response.getCommuterEmail(),
                response.getPassengerName(),
                response.getPassengerPhone(),
                response.getPassengerEmail(),
                String.valueOf(response.getTripId()),
                route,
                departure,
                arrival,
                response.getBusPlateNumber(),
                response.getSeatNumber(),
                response.getFare(),
                response.getPaymentStatus(),
                response.getStatus(),
                response.getCreatedAt());
    }

    private static String extractApiMessage(
            String message) {

        int separator = message.indexOf(": ");

        if (separator < 0) {
            return message;
        }

        String body = message.substring(separator + 2);

        if (body.startsWith("{")
                && body.contains("\"message\"")) {

            int start = body.indexOf("\"message\"");
            int colon = body.indexOf(':', start);
            int firstQuote = body.indexOf('"', colon + 1);
            int secondQuote = body.indexOf('"', firstQuote + 1);

            if (firstQuote >= 0 && secondQuote > firstQuote) {
                return body.substring(
                        firstQuote + 1,
                        secondQuote);
            }
        }

        return body;
    }
}