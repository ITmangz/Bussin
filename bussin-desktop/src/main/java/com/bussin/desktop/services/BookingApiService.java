package com.bussin.desktop.services;

import java.io.IOException;
import java.net.URLEncoder;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

public final class BookingApiService {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(
                    LocalDateTime.class,
                    (JsonDeserializer<LocalDateTime>) (json, type, context) -> LocalDateTime.parse(json.getAsString()))
            .create();

    private BookingApiService() {
    }

    public static BookingResponse createBooking(
            long tripId,
            String seatNumber,
            String passengerName,
            String passengerPhone,
            String passengerEmail)
            throws IOException, InterruptedException {

        CreateBookingRequest request = new CreateBookingRequest(
                tripId,
                seatNumber,
                passengerName,
                passengerPhone,
                passengerEmail);

        HttpResponse<String> response = ApiClient.post(
                "/bookings",
                GSON.toJson(request));

        ensureSuccess(response);

        return GSON.fromJson(
                response.body(),
                BookingResponse.class);
    }

    public static List<BookingResponse> getMyBookings()
            throws IOException, InterruptedException {

        HttpResponse<String> response = ApiClient.get("/bookings/me");

        ensureSuccess(response);

        BookingResponse[] bookings = GSON.fromJson(
                response.body(),
                BookingResponse[].class);

        return Arrays.asList(bookings);
    }

    public static BookingResponse getMyBooking(long bookingId)
            throws IOException, InterruptedException {

        String endpoint = "/bookings/me/"
                + URLEncoder.encode(
                        String.valueOf(bookingId),
                        StandardCharsets.UTF_8);

        HttpResponse<String> response = ApiClient.get(endpoint);

        ensureSuccess(response);

        return GSON.fromJson(
                response.body(),
                BookingResponse.class);
    }

    public static BookingResponse cancelBooking(long bookingId)
            throws IOException, InterruptedException {

        String endpoint = "/bookings/me/"
                + URLEncoder.encode(
                        String.valueOf(bookingId),
                        StandardCharsets.UTF_8);

        HttpResponse<String> response = ApiClient.delete(endpoint);

        ensureSuccess(response);

        return GSON.fromJson(
                response.body(),
                BookingResponse.class);
    }

    private static void ensureSuccess(
            HttpResponse<String> response) {

        if (response.statusCode() < 200
                || response.statusCode() >= 300) {

            throw new IllegalStateException(
                    "Booking API request failed. HTTP "
                            + response.statusCode()
                            + ": "
                            + response.body());
        }
    }

    public static class CreateBookingRequest {

        private final long tripId;
        private final String seatNumber;
        private final String passengerName;
        private final String passengerPhone;
        private final String passengerEmail;

        public CreateBookingRequest(
                long tripId,
                String seatNumber,
                String passengerName,
                String passengerPhone,
                String passengerEmail) {

            this.tripId = tripId;
            this.seatNumber = seatNumber;
            this.passengerName = passengerName;
            this.passengerPhone = passengerPhone;
            this.passengerEmail = passengerEmail;
        }
    }

    public static class BookingResponse {

        private long id;
        private String bookingReference;

        private long commuterId;
        private String commuterName;
        private String commuterEmail;

        private long tripId;
        private String routeIdentifier;
        private String origin;
        private String destination;

        private long busId;
        private String busPlateNumber;

        private LocalDateTime scheduledDeparture;
        private LocalDateTime scheduledArrival;

        private String passengerName;
        private String passengerPhone;
        private String passengerEmail;

        private String seatNumber;
        private double fare;

        private String status;
        private String paymentStatus;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public long getId() {
            return id;
        }

        public String getBookingReference() {
            return bookingReference;
        }

        public long getCommuterId() {
            return commuterId;
        }

        public String getCommuterName() {
            return commuterName;
        }

        public String getCommuterEmail() {
            return commuterEmail;
        }

        public long getTripId() {
            return tripId;
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

        public long getBusId() {
            return busId;
        }

        public String getBusPlateNumber() {
            return busPlateNumber;
        }

        public LocalDateTime getScheduledDeparture() {
            return scheduledDeparture;
        }

        public LocalDateTime getScheduledArrival() {
            return scheduledArrival;
        }

        public String getPassengerName() {
            return passengerName;
        }

        public String getPassengerPhone() {
            return passengerPhone;
        }

        public String getPassengerEmail() {
            return passengerEmail;
        }

        public String getSeatNumber() {
            return seatNumber;
        }

        public double getFare() {
            return fare;
        }

        public String getStatus() {
            return status;
        }

        public String getPaymentStatus() {
            return paymentStatus;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public LocalDateTime getUpdatedAt() {
            return updatedAt;
        }
    }
}