package com.bussin.desktop.ui.flow;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class CommuterTrip {

    private final String tripId;
    private final String departure;
    private final String arrival;
    private final String origin;
    private final String destination;
    private final String busNumber;
    private final double fare;
    private final int totalSeats;

    private final Set<String> bookedSeats = new LinkedHashSet<>();

    /*
     * ------------------------------------------------------------------
     * Existing constructor
     * ------------------------------------------------------------------
     *
     * Keeps compatibility with the existing TripSearchScreen.
     */
    public CommuterTrip(
            String tripId,
            String departure,
            String arrival,
            String origin,
            String destination,
            String busNumber,
            double fare,
            int totalSeats) {

        this(
                tripId,
                departure,
                arrival,
                origin,
                destination,
                busNumber,
                fare,
                totalSeats,
                0);
    }

    /*
     * ------------------------------------------------------------------
     * Extended constructor
     * ------------------------------------------------------------------
     *
     * The final parameter is retained for compatibility with the
     * shared TripStore. Actual occupied seats are tracked by
     * bookedSeats rather than by a simple counter.
     */
    public CommuterTrip(
            String tripId,
            String departure,
            String arrival,
            String origin,
            String destination,
            String busNumber,
            double fare,
            int totalSeats,
            int ignoredInitialBookedSeats) {

        this.tripId = tripId;
        this.departure = departure;
        this.arrival = arrival;
        this.origin = origin;
        this.destination = destination;
        this.busNumber = busNumber;
        this.fare = fare;
        this.totalSeats = totalSeats;
    }

    /*
     * ------------------------------------------------------------------
     * Seat Management
     * ------------------------------------------------------------------
     */

    public boolean reserveSeat(String seat) {

        if (seat == null || seat.isBlank()) {
            return false;
        }

        String normalized = seat.trim().toUpperCase();

        if (!isValidSeat(normalized)) {
            return false;
        }

        if (bookedSeats.contains(normalized)) {
            return false;
        }

        bookedSeats.add(normalized);

        return true;
    }

    public boolean isSeatAvailable(String seat) {

        if (seat == null || seat.isBlank()) {
            return false;
        }

        return !bookedSeats.contains(
                seat.trim().toUpperCase());
    }

    public Set<String> getBookedSeats() {

        return Collections.unmodifiableSet(
                bookedSeats);
    }

    public int getAvailableSeats() {

        return Math.max(
                0,
                totalSeats - bookedSeats.size());
    }

    private boolean isValidSeat(String seat) {

        if (seat.length() < 3) {
            return false;
        }

        try {

            int row = Integer.parseInt(
                    seat.substring(
                            0,
                            seat.length() - 1));

            char column = seat.charAt(seat.length() - 1);

            return row >= 1
                    && row <= 99
                    && column >= 'A'
                    && column <= 'D';

        } catch (NumberFormatException ex) {

            return false;
        }
    }

    /*
     * ------------------------------------------------------------------
     * Display Helpers
     * ------------------------------------------------------------------
     */

    /**
     * Returns the route in the format expected by the
     * existing Phase 11 screens.
     *
     * Example:
     * Manila → Batangas
     */
    public String getRoute() {

        return origin
                + " → "
                + destination;
    }

    /*
     * ------------------------------------------------------------------
     * Getters
     * ------------------------------------------------------------------
     */

    public String getTripId() {
        return tripId;
    }

    public String getDeparture() {
        return departure;
    }

    public String getArrival() {
        return arrival;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public double getFare() {
        return fare;
    }

    public int getTotalSeats() {
        return totalSeats;
    }
}