package com.bussin.bussin_api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.bussin.bussin_api.entity.Booking;
import com.bussin.bussin_api.entity.BookingStatus;
import com.bussin.bussin_api.entity.PaymentStatus;
import com.bussin.bussin_api.entity.QueueEntry;

public class BookingResponse {

    private Long id;
    private String bookingReference;

    private Long commuterId;
    private String commuterName;
    private String commuterEmail;

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

        response.commuterId = booking.getCommuter().getId();
        response.commuterName = buildFullName(
                booking.getCommuter().getFirstName(),
                booking.getCommuter().getMiddleName(),
                booking.getCommuter().getLastName());
        response.commuterEmail = booking.getCommuter().getEmail();

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