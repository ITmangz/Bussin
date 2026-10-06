package com.bussin.bussin_api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.bussin.bussin_api.entity.Booking;
import com.bussin.bussin_api.entity.BookingStatus;
import com.bussin.bussin_api.entity.CancelledBookingArchive;
import com.bussin.bussin_api.entity.PaymentStatus;
import com.bussin.bussin_api.entity.QueueEntry;

public class BookingResponse {

    private Long id;
    private String bookingReference;

    private Long commuterId;
    private String commuterName;
    private String commuterEmail;
    private boolean guestBooking;

    private Long tripId;
    private String routeIdentifier;
    private String origin;
    private String destination;

    private Long busId;
    private String busPlateNumber;

    private LocalDateTime scheduledDeparture;
    private LocalDateTime scheduledArrival;

    private String passengerName;
    private String passengerPhone;
    private String passengerEmail;

    private String seatNumber;
    private List<String> seatNumbers;
    private Integer seatCount;
    private BigDecimal fare;

    private Integer queueNumber;
    private String queueStatus;

    private BookingStatus status;
    private PaymentStatus paymentStatus;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BookingResponse() {
    }

    public static BookingResponse from(Booking booking) {
        return from(booking, null);
    }

    public static BookingResponse from(Booking booking, QueueEntry queueEntry) {
        BookingResponse response = new BookingResponse();

        response.id = booking.getId();
        response.bookingReference = booking.getBookingReference();

        if (booking.getCommuter() == null) {
            response.commuterName = "Guest";
            response.commuterEmail = booking.getPassengerEmail();
            response.guestBooking = true;
        } else {
            response.commuterId = booking.getCommuter().getId();
            response.commuterName = buildFullName(
                    booking.getCommuter().getFirstName(),
                    booking.getCommuter().getMiddleName(),
                    booking.getCommuter().getLastName());
            response.commuterEmail = booking.getCommuter().getEmail();
        }

        response.tripId = booking.getTrip().getId();
        response.routeIdentifier = booking.getTrip().getRoute().getRouteIdentifier();
        response.origin = booking.getTrip().getRoute().getOrigin();
        response.destination = booking.getTrip().getRoute().getDestination();

        response.busId = booking.getTrip().getBus().getId();
        response.busPlateNumber = booking.getTrip().getBus().getPlateNumber();

        response.scheduledDeparture = booking.getTrip().getScheduledDeparture();
        response.scheduledArrival = booking.getTrip().getScheduledArrival();

        response.passengerName = booking.getPassengerName();
        response.passengerPhone = booking.getPassengerPhone();
        response.passengerEmail = booking.getPassengerEmail();

        response.seatNumber = booking.getSeatNumber();
        response.seatNumbers = booking.getBookingSeats().stream()
                .map(com.bussin.bussin_api.entity.BookingSeat::getSeatNumber)
                .sorted()
                .toList();

        if (response.seatNumbers.isEmpty() && booking.getSeatNumber() != null) {
            response.seatNumbers = List.of(booking.getSeatNumber());
        }

        response.seatCount = response.seatNumbers.size();
        response.fare = booking.getFare();

        if (queueEntry != null) {
            response.queueNumber = queueEntry.getQueueNumber();
            response.queueStatus = queueEntry.getStatus().name();
        }

        response.status = booking.getStatus();
        response.paymentStatus = booking.getPaymentStatus();

        response.createdAt = booking.getCreatedAt();
        response.updatedAt = booking.getUpdatedAt();

        return response;
    }

    public static BookingResponse from(CancelledBookingArchive archive) {
        BookingResponse response = new BookingResponse();
        response.id = archive.getBookingId();
        response.bookingReference = archive.getBookingReference();
        response.commuterId = archive.getCommuterId();
        response.commuterName = archive.getCommuterId() == null
                ? "Guest"
                : archive.getCommuterName();
        response.commuterEmail = archive.getCommuterId() == null
                ? archive.getPassengerEmail()
                : archive.getCommuterEmail();
        response.guestBooking = archive.getCommuterId() == null;
        response.tripId = archive.getTripId();
        response.routeIdentifier = archive.getRouteIdentifier();
        response.origin = archive.getOrigin();
        response.destination = archive.getDestination();
        response.busId = archive.getBusId();
        response.busPlateNumber = archive.getBusPlateNumber();
        response.scheduledDeparture = archive.getScheduledDeparture();
        response.scheduledArrival = archive.getScheduledArrival();
        response.passengerName = archive.getPassengerName();
        response.passengerPhone = archive.getPassengerPhone();
        response.passengerEmail = archive.getPassengerEmail();
        response.seatNumber = archive.getSeatNumber();
        response.seatNumbers = new ArrayList<>(archive.getSeatNumbers());
        response.seatCount = response.seatNumbers.size();
        response.fare = archive.getFare();
        response.queueNumber = archive.getQueueNumber();
        response.queueStatus = archive.getQueueStatus();
        response.status = BookingStatus.CANCELLED;
        response.paymentStatus = archive.getPaymentStatus();
        response.createdAt = archive.getCreatedAt();
        response.updatedAt = archive.getUpdatedAt();
        return response;
    }

    private static String buildFullName(
            String firstName,
            String middleName,
            String lastName) {

        StringBuilder name = new StringBuilder();

        appendName(name, firstName);
        appendName(name, middleName);
        appendName(name, lastName);

        return name.toString();
    }

    private static void appendName(StringBuilder name, String value) {
        if (value != null && !value.isBlank()) {
            if (!name.isEmpty()) {
                name.append(" ");
            }

            name.append(value.trim());
        }
    }

    public Long getId() {
        return id;
    }

    public String getBookingReference() {
        return bookingReference;
    }

    public Long getCommuterId() {
        return commuterId;
    }

    public String getCommuterName() {
        return commuterName;
    }

    public String getCommuterEmail() {
        return commuterEmail;
    }

    public boolean isGuestBooking() {
        return guestBooking;
    }

    public Long getTripId() {
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

    public Long getBusId() {
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

    public List<String> getSeatNumbers() {
        return seatNumbers;
    }

    public Integer getSeatCount() {
        return seatCount;
    }

    public Integer getQueueNumber() {
        return queueNumber;
    }

    public String getQueueStatus() {
        return queueStatus;
    }

    public BigDecimal getFare() {
        return fare;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
