package com.bussin.bussin_api.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "cancelled_bookings", indexes = {
        @Index(name = "idx_cancelled_bookings_commuter_created", columnList = "commuter_id, created_at")
})
public class CancelledBookingArchive {

    @Id
    @Column(name = "booking_id", nullable = false)
    private Long bookingId;

    @Column(name = "booking_reference", nullable = false, length = 20)
    private String bookingReference;

    @Column(name = "commuter_id", nullable = false)
    private Long commuterId;

    @Column(name = "commuter_name", nullable = false, length = 600)
    private String commuterName;

    @Column(name = "commuter_email", length = 255)
    private String commuterEmail;

    @Column(name = "trip_id", nullable = false)
    private Long tripId;

    @Column(name = "route_identifier", length = 100)
    private String routeIdentifier;

    @Column(length = 100)
    private String origin;

    @Column(length = 100)
    private String destination;

    @Column(name = "bus_id")
    private Long busId;

    @Column(name = "bus_plate_number", length = 20)
    private String busPlateNumber;

    @Column(name = "scheduled_departure")
    private LocalDateTime scheduledDeparture;

    @Column(name = "scheduled_arrival")
    private LocalDateTime scheduledArrival;

    @Column(name = "passenger_name", nullable = false, length = 100)
    private String passengerName;

    @Column(name = "passenger_phone", nullable = false, length = 30)
    private String passengerPhone;

    @Column(name = "passenger_email", nullable = false, length = 150)
    private String passengerEmail;

    @Column(name = "seat_number", nullable = false, length = 10)
    private String seatNumber;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "cancelled_booking_seats",
            joinColumns = @JoinColumn(name = "booking_id"),
            uniqueConstraints = @UniqueConstraint(
                    name = "uk_cancelled_booking_seat",
                    columnNames = {"booking_id", "seat_number"}))
    @OrderColumn(name = "seat_index")
    @Column(name = "seat_number", nullable = false, length = 10)
    private List<String> seatNumbers = new ArrayList<>();

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal fare;

    @Column(name = "queue_number")
    private Integer queueNumber;

    @Column(name = "queue_status", length = 20)
    private String queueStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    private PaymentStatus paymentStatus;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected CancelledBookingArchive() {
    }

    public static CancelledBookingArchive from(
            Booking booking,
            QueueEntry queueEntry,
            LocalDateTime cancelledAt) {
        CancelledBookingArchive archive = new CancelledBookingArchive();
        User commuter = booking.getCommuter();
        Trip trip = booking.getTrip();
        Route route = trip.getRoute();
        Bus bus = trip.getBus();

        archive.bookingId = booking.getId();
        archive.bookingReference = booking.getBookingReference();
        archive.commuterId = commuter.getId();
        archive.commuterName = Stream.of(
                        commuter.getFirstName(),
                        commuter.getMiddleName(),
                        commuter.getLastName())
                .filter(value -> value != null && !value.isBlank())
                .collect(Collectors.joining(" "));
        archive.commuterEmail = commuter.getEmail();
        archive.tripId = trip.getId();
        archive.routeIdentifier = route.getRouteIdentifier();
        archive.origin = route.getOrigin();
        archive.destination = route.getDestination();
        archive.busId = bus.getId();
        archive.busPlateNumber = bus.getPlateNumber();
        archive.scheduledDeparture = trip.getScheduledDeparture();
        archive.scheduledArrival = trip.getScheduledArrival();
        archive.passengerName = booking.getPassengerName();
        archive.passengerPhone = booking.getPassengerPhone();
        archive.passengerEmail = booking.getPassengerEmail();
        archive.seatNumber = booking.getSeatNumber();
        archive.seatNumbers = booking.getBookingSeats().stream()
                .map(BookingSeat::getSeatNumber)
                .sorted()
                .collect(Collectors.toCollection(ArrayList::new));
        if (archive.seatNumbers.isEmpty() && booking.getSeatNumber() != null) {
            archive.seatNumbers.add(booking.getSeatNumber());
        }
        archive.fare = booking.getFare();
        if (queueEntry != null) {
            archive.queueNumber = queueEntry.getQueueNumber();
            archive.queueStatus = queueEntry.getStatus().name();
        }
        archive.paymentStatus = booking.getPaymentStatus();
        archive.createdAt = booking.getCreatedAt();
        archive.updatedAt = cancelledAt;
        return archive;
    }

    public Long getBookingId() {
        return bookingId;
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

    public BigDecimal getFare() {
        return fare;
    }

    public Integer getQueueNumber() {
        return queueNumber;
    }

    public String getQueueStatus() {
        return queueStatus;
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
