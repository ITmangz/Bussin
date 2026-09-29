package com.bussin.desktop.ui.flow;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class BookingStore {

    private static final List<UserBooking> BOOKINGS = new ArrayList<>();

    private static int nextBookingNumber = 1012;

    private BookingStore() {
    }

    static {
        UserBooking existing = new UserBooking(
                "BK-1011",
                "user@bussin.com",
                "User",
                "0917 000 0000",
                "user@bussin.com",
                "TR-102",
                "Manila → Batangas",
                "10:00 AM",
                "12:30 PM",
                "BUS-102",
                "03A",
                450,
                "Paid",
                "Confirmed",
                LocalDateTime.now());

        BOOKINGS.add(existing);
    }

    public static synchronized String generateBookingId() {

        String id = "BK-" + nextBookingNumber;

        nextBookingNumber++;

        return id;
    }

    public static synchronized void addBooking(UserBooking booking) {

        if (booking == null) {
            return;
        }

        BOOKINGS.add(booking);
    }

    public static synchronized List<UserBooking> getBookingsForUser(
            String email) {

        List<UserBooking> result = new ArrayList<>();

        if (email == null || email.isBlank()) {
            return result;
        }

        for (UserBooking booking : BOOKINGS) {

            if (booking.getOwnerEmail()
                    .equalsIgnoreCase(email.trim())) {

                result.add(booking);
            }
        }

        return result;
    }

    public static synchronized UserBooking find(
            String bookingId,
            String email) {

        if (bookingId == null || email == null) {
            return null;
        }

        for (UserBooking booking : BOOKINGS) {

            if (booking.getBookingId().equalsIgnoreCase(bookingId)
                    && booking.getOwnerEmail()
                            .equalsIgnoreCase(email)) {

                return booking;
            }
        }

        return null;
    }

    public static synchronized UserBooking getLatestForUser(
            String email) {

        List<UserBooking> bookings = getBookingsForUser(email);

        if (bookings.isEmpty()) {
            return null;
        }

        return bookings.get(bookings.size() - 1);
    }

    public static synchronized boolean cancelBooking(
            String bookingId,
            String email) {

        UserBooking booking = find(bookingId, email);

        if (booking == null) {
            return false;
        }

        if ("Completed".equals(booking.getStatus())
                || "Cancelled".equals(booking.getStatus())) {

            return false;
        }

        booking.setStatus("Cancelled");

        if ("Paid".equals(booking.getPaymentStatus())) {
            booking.setPaymentStatus("Refunded");
        }

        return true;
    }

    public static class UserBooking {

        private final String bookingId;
        private final String ownerEmail;
        private final String passengerName;
        private final String passengerPhone;
        private final String passengerEmail;

        private final String tripId;
        private final String route;
        private final String departure;
        private final String arrival;
        private final String busNumber;
        private final String seat;
        private final double fare;

        private String paymentStatus;
        private String status;

        private final LocalDateTime createdAt;

        public UserBooking(
                String bookingId,
                String ownerEmail,
                String passengerName,
                String passengerPhone,
                String passengerEmail,
                String tripId,
                String route,
                String departure,
                String arrival,
                String busNumber,
                String seat,
                double fare,
                String paymentStatus,
                String status,
                LocalDateTime createdAt) {

            this.bookingId = bookingId;
            this.ownerEmail = ownerEmail;
            this.passengerName = passengerName;
            this.passengerPhone = passengerPhone;
            this.passengerEmail = passengerEmail;
            this.tripId = tripId;
            this.route = route;
            this.departure = departure;
            this.arrival = arrival;
            this.busNumber = busNumber;
            this.seat = seat;
            this.fare = fare;
            this.paymentStatus = paymentStatus;
            this.status = status;
            this.createdAt = createdAt;
        }

        public String getBookingId() {
            return bookingId;
        }

        public String getOwnerEmail() {
            return ownerEmail;
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

        public String getTripId() {
            return tripId;
        }

        public String getRoute() {
            return route;
        }

        public String getDeparture() {
            return departure;
        }

        public String getArrival() {
            return arrival;
        }

        public String getBusNumber() {
            return busNumber;
        }

        public String getSeat() {
            return seat;
        }

        public double getFare() {
            return fare;
        }

        public String getPaymentStatus() {
            return paymentStatus;
        }

        public String getStatus() {
            return status;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setPaymentStatus(String paymentStatus) {
            this.paymentStatus = paymentStatus;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}