package com.bussin.bussin_api.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.bussin.bussin_api.dto.TripResponse;
import com.bussin.bussin_api.entity.BookingStatus;
import com.bussin.bussin_api.entity.Route;
import com.bussin.bussin_api.entity.Trip;
import com.bussin.bussin_api.entity.TripStatus;
import com.bussin.bussin_api.repository.BookingRepository;
import com.bussin.bussin_api.repository.RouteRepository;
import com.bussin.bussin_api.repository.TripRepository;

@Service
public class AiBookingService {

        private static final int SEARCH_DAYS_AHEAD = 30;

        private static final List<TripStatus> BOOKABLE_TRIP_STATUSES = List.of(
                        TripStatus.SCHEDULED,
                        TripStatus.BOARDING);

        private static final List<BookingStatus> ACTIVE_BOOKING_STATUSES = List.of(
                        BookingStatus.PENDING,
                        BookingStatus.CONFIRMED);

        private static final List<DateTimeFormatter> TIME_FORMATTERS = List.of(
                        DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH),
                        DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH),
                        DateTimeFormatter.ofPattern("H:mm", Locale.ENGLISH),
                        DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH),
                        DateTimeFormatter.ofPattern("h a", Locale.ENGLISH),
                        DateTimeFormatter.ofPattern("hh a", Locale.ENGLISH));

        private final TripRepository tripRepository;
        private final RouteRepository routeRepository;
        private final BookingRepository bookingRepository;

        public AiBookingService(
                        TripRepository tripRepository,
                        RouteRepository routeRepository,
                        BookingRepository bookingRepository) {

                this.tripRepository = tripRepository;
                this.routeRepository = routeRepository;
                this.bookingRepository = bookingRepository;
        }

        // ============================================================
        // SEARCH TRIPS
        // ============================================================

        /**
         * Searches bookable trips for the AI booking conversation.
         *
         * Supported combinations:
         *
         * origin + destination
         * origin + destination + date
         * origin + destination + date + time
         *
         * Date is optional.
         * Time is optional.
         *
         * If neither date nor time is supplied:
         * searches upcoming trips for the next 30 days.
         *
         * If only date is supplied:
         * searches the entire requested date.
         *
         * If date + time are supplied:
         * searches around the requested departure time.
         */
        public List<TripResponse> searchTrips(
                        String origin,
                        String destination,
                        String date,
                        String time) {

                validateRoute(origin, destination);

                String normalizedOrigin = normalizeText(origin);
                String normalizedDestination = normalizeText(destination);

                LocalDate travelDate = parseDate(date);
                LocalTime requestedTime = parseTime(time);

                LocalDateTime now = LocalDateTime.now();

                /*
                 * A date that has already passed cannot contain
                 * a currently bookable trip.
                 */
                if (travelDate != null
                                && travelDate.isBefore(now.toLocalDate())) {

                        return List.of();
                }

                List<Long> routeIds = findActiveRouteIds(
                                normalizedOrigin,
                                normalizedDestination);

                if (routeIds.isEmpty()) {
                        return List.of();
                }

                SearchWindow searchWindow = buildSearchWindow(
                                travelDate,
                                requestedTime,
                                now);

                List<Trip> trips = findTrips(
                                routeIds,
                                searchWindow.from(),
                                searchWindow.to());

                return trips.stream()
                                .filter(this::isBookableTrip)
                                .filter(this::hasAvailableSeat)
                                .map(this::toResponse)
                                .toList();
        }

        // ============================================================
        // SEARCH NEXT AVAILABLE
        // ============================================================

        /**
         * Searches upcoming trips when the commuter has not
         * specified a travel date yet.
         *
         * Example:
         *
         * "Show me any available trips."
         */
        public List<TripResponse> searchNextAvailableTrips(
                        String origin,
                        String destination) {

                validateRoute(origin, destination);

                String normalizedOrigin = normalizeText(origin);
                String normalizedDestination = normalizeText(destination);

                List<Long> routeIds = findActiveRouteIds(
                                normalizedOrigin,
                                normalizedDestination);

                if (routeIds.isEmpty()) {
                        return List.of();
                }

                LocalDateTime now = LocalDateTime.now();

                LocalDateTime searchEnd = now.plusDays(
                                SEARCH_DAYS_AHEAD);

                List<Trip> trips = findTrips(
                                routeIds,
                                now,
                                searchEnd);

                return trips.stream()
                                .filter(this::isBookableTrip)
                                .filter(this::hasAvailableSeat)
                                .map(this::toResponse)
                                .toList();
        }

        // ============================================================
        // SEARCH DEPARTURE TIMES
        // ============================================================

        /**
         * Returns available trips for a particular date.
         *
         * This is useful when the user asks:
         *
         * "What departure times are available?"
         *
         * The AI can call this without requiring the commuter
         * to specify a time first.
         */
        public List<TripResponse> searchDepartureTimes(
                        String origin,
                        String destination,
                        String date) {

                return searchTrips(
                                origin,
                                destination,
                                date,
                                null);
        }

        // ============================================================
        // FIND ACTIVE ROUTES
        // ============================================================

        private List<Long> findActiveRouteIds(
                        String origin,
                        String destination) {

                List<Route> routes = routeRepository
                                .findByOriginIgnoreCaseAndDestinationIgnoreCaseAndActiveTrue(
                                                origin,
                                                destination);

                return routes.stream()
                                .map(Route::getId)
                                .toList();
        }

        // ============================================================
        // FIND TRIPS
        // ============================================================

        private List<Trip> findTrips(
                        List<Long> routeIds,
                        LocalDateTime from,
                        LocalDateTime to) {

                return tripRepository
                                .findByRouteIdInAndStatusInAndScheduledDepartureBetweenOrderByScheduledDepartureAsc(
                                                routeIds,
                                                BOOKABLE_TRIP_STATUSES,
                                                from,
                                                to);
        }

        // ============================================================
        // BUILD SEARCH WINDOW
        // ============================================================

        private SearchWindow buildSearchWindow(
                        LocalDate travelDate,
                        LocalTime requestedTime,
                        LocalDateTime now) {

                /*
                 * No date and no time.
                 *
                 * Search all upcoming trips.
                 */
                if (travelDate == null
                                && requestedTime == null) {

                        return new SearchWindow(
                                        now,
                                        now.plusDays(SEARCH_DAYS_AHEAD));
                }

                /*
                 * Date supplied but no time.
                 *
                 * Search the entire day.
                 */
                if (travelDate != null
                                && requestedTime == null) {

                        LocalDateTime from = travelDate.atStartOfDay();

                        LocalDateTime to = travelDate.atTime(LocalTime.MAX);

                        /*
                         * If the requested date is today,
                         * don't return trips that already departed.
                         */
                        if (from.isBefore(now)) {
                                from = now;
                        }

                        return new SearchWindow(from, to);
                }

                /*
                 * Time supplied without a date.
                 *
                 * This means the commuter is interested in a
                 * departure time but has not specified a date.
                 *
                 * Search upcoming trips for the next 30 days
                 * around that time.
                 */
                if (travelDate == null) {

                        LocalDate today = now.toLocalDate();

                        LocalDateTime from = today.atTime(requestedTime)
                                        .minusMinutes(30);

                        LocalDateTime to = today.atTime(requestedTime)
                                        .plusMinutes(30);

                        /*
                         * If today's requested time has already passed,
                         * start looking from tomorrow.
                         */
                        if (to.isBefore(now)) {

                                LocalDate tomorrow = today.plusDays(1);

                                from = tomorrow.atTime(requestedTime)
                                                .minusMinutes(30);

                                to = tomorrow.atTime(requestedTime)
                                                .plusMinutes(30);

                                /*
                                 * Continue the search for the remaining
                                 * 30-day period.
                                 */
                                to = today
                                                .plusDays(SEARCH_DAYS_AHEAD)
                                                .atTime(requestedTime)
                                                .plusMinutes(30);
                        }

                        return new SearchWindow(
                                        from.isBefore(now) ? now : from,
                                        to);
                }

                /*
                 * Date + time.
                 *
                 * Search a reasonable window around the requested
                 * departure time rather than requiring an exact
                 * database timestamp.
                 *
                 * Example:
                 *
                 * User: 10:00 AM
                 *
                 * A trip at 9:45 AM or 10:15 AM can still be found.
                 */
                LocalDateTime requestedDateTime = travelDate.atTime(requestedTime);

                LocalDateTime from = requestedDateTime.minusMinutes(30);

                LocalDateTime to = requestedDateTime.plusMinutes(30);

                if (from.isBefore(now)) {
                        from = now;
                }

                return new SearchWindow(from, to);
        }

        // ============================================================
        // CHECK TRIP
        // ============================================================

        private boolean isBookableTrip(
                        Trip trip) {

                if (trip == null) {
                        return false;
                }

                if (trip.getStatus() != TripStatus.SCHEDULED
                                && trip.getStatus() != TripStatus.BOARDING) {

                        return false;
                }

                if (trip.getRoute() == null
                                || !trip.getRoute().isActive()) {

                        return false;
                }

                if (trip.getBus() == null) {
                        return false;
                }

                if (trip.getBus()
                                .getStatus()
                                .name()
                                .equals("OUT_OF_SERVICE")) {

                        return false;
                }

                if (trip.getScheduledDeparture() == null) {
                        return false;
                }

                return !trip.getScheduledDeparture()
                                .isBefore(LocalDateTime.now());
        }

        // ============================================================
        // CHECK AVAILABLE SEAT
        // ============================================================

        private boolean hasAvailableSeat(
                        Trip trip) {

                int capacity = trip.getBus().getCapacity();

                for (int position = 1; position <= capacity; position++) {

                        String seatNumber = generateSeatNumber(position);

                        boolean occupied = bookingRepository
                                        .existsByTripIdAndSeatNumberAndStatusIn(
                                                        trip.getId(),
                                                        seatNumber,
                                                        ACTIVE_BOOKING_STATUSES);

                        if (!occupied) {
                                return true;
                        }
                }

                return false;
        }

        // ============================================================
        // CHECK SPECIFIC SEAT
        // ============================================================

        public boolean isSeatAvailable(
                        Long tripId,
                        String seatNumber) {

                Trip trip = getTrip(tripId);

                validateTripAvailability(trip);

                String normalizedSeat = normalizeSeat(seatNumber);

                validateSeatNumber(
                                normalizedSeat,
                                trip.getBus().getCapacity());

                return !bookingRepository
                                .existsByTripIdAndSeatNumberAndStatusIn(
                                                tripId,
                                                normalizedSeat,
                                                ACTIVE_BOOKING_STATUSES);
        }

        // ============================================================
        // GET AVAILABLE SEATS
        // ============================================================

        public List<String> getAvailableSeats(
                        Long tripId) {

                Trip trip = getTrip(tripId);

                validateTripAvailability(trip);

                int capacity = trip.getBus().getCapacity();

                List<String> availableSeats = new ArrayList<>();

                for (int position = 1; position <= capacity; position++) {

                        String seatNumber = generateSeatNumber(position);

                        boolean occupied = bookingRepository
                                        .existsByTripIdAndSeatNumberAndStatusIn(
                                                        tripId,
                                                        seatNumber,
                                                        ACTIVE_BOOKING_STATUSES);

                        if (!occupied) {
                                availableSeats.add(seatNumber);
                        }
                }

                return availableSeats;
        }

        // ============================================================
        // GET TRIP
        // ============================================================

        public Trip getTrip(
                        Long tripId) {

                if (tripId == null) {
                        throw new IllegalArgumentException(
                                        "Trip ID is required.");
                }

                return tripRepository
                                .findById(tripId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Trip not found."));
        }

        // ============================================================
        // VALIDATE TRIP AVAILABILITY
        // ============================================================

        private void validateTripAvailability(
                        Trip trip) {

                if (trip.getStatus() != TripStatus.SCHEDULED
                                && trip.getStatus() != TripStatus.BOARDING) {

                        throw new IllegalArgumentException(
                                        "This trip is not currently available.");
                }

                if (trip.getRoute() == null
                                || !trip.getRoute().isActive()) {

                        throw new IllegalArgumentException(
                                        "The route for this trip is inactive.");
                }

                if (trip.getBus() == null) {

                        throw new IllegalArgumentException(
                                        "This trip does not have a valid bus.");
                }

                if (trip.getBus()
                                .getStatus()
                                .name()
                                .equals("OUT_OF_SERVICE")) {

                        throw new IllegalArgumentException(
                                        "The bus assigned to this trip is out of service.");
                }

                if (trip.getScheduledDeparture() == null) {

                        throw new IllegalArgumentException(
                                        "This trip does not have a scheduled departure.");
                }

                if (trip.getScheduledDeparture()
                                .isBefore(LocalDateTime.now())) {

                        throw new IllegalArgumentException(
                                        "This trip has already departed.");
                }
        }

        // ============================================================
        // VALIDATE ROUTE
        // ============================================================

        private void validateRoute(
                        String origin,
                        String destination) {

                if (origin == null
                                || origin.isBlank()) {

                        throw new IllegalArgumentException(
                                        "Origin is required.");
                }

                if (destination == null
                                || destination.isBlank()) {

                        throw new IllegalArgumentException(
                                        "Destination is required.");
                }
        }

        // ============================================================
        // NORMALIZE TEXT
        // ============================================================

        private String normalizeText(
                        String value) {

                return value
                                .trim()
                                .replaceAll("\\s+", " ");
        }

        // ============================================================
        // PARSE DATE
        // ============================================================

        private LocalDate parseDate(
                        String date) {

                if (date == null
                                || date.isBlank()) {

                        return null;
                }

                String normalized = date.trim();

                /*
                 * Primary format used internally by the AI:
                 *
                 * 2026-10-08
                 */
                try {

                        return LocalDate.parse(
                                        normalized);

                } catch (DateTimeParseException ignored) {
                        // Try additional human-readable formats below.
                }

                List<DateTimeFormatter> formatters = List.of(
                                DateTimeFormatter.ofPattern(
                                                "MMMM d, uuuu",
                                                Locale.ENGLISH),

                                DateTimeFormatter.ofPattern(
                                                "MMM d, uuuu",
                                                Locale.ENGLISH),

                                DateTimeFormatter.ofPattern(
                                                "MMMM d uuuu",
                                                Locale.ENGLISH),

                                DateTimeFormatter.ofPattern(
                                                "MMM d uuuu",
                                                Locale.ENGLISH));

                for (DateTimeFormatter formatter : formatters) {

                        try {

                                return LocalDate.parse(
                                                normalized,
                                                formatter);

                        } catch (DateTimeParseException ignored) {
                                // Try next format.
                        }
                }

                throw new IllegalArgumentException(
                                "Travel date could not be understood: "
                                                + date
                                                + ". Use a date such as 2026-10-08.");
        }

        // ============================================================
        // PARSE TIME
        // ============================================================

        private LocalTime parseTime(
                        String time) {

                if (time == null
                                || time.isBlank()) {

                        return null;
                }

                String normalized = time.trim()
                                .replaceAll(
                                                "\\s+",
                                                " ")
                                .toUpperCase(Locale.ENGLISH);

                /*
                 * Remove unnecessary spaces before AM/PM.
                 *
                 * Example:
                 *
                 * "10:00  AM"
                 * becomes
                 * "10:00 AM"
                 */
                normalized = normalized
                                .replaceAll(
                                                "\\s+(AM|PM)$",
                                                " $1");

                for (DateTimeFormatter formatter : TIME_FORMATTERS) {

                        try {

                                return LocalTime.parse(
                                                normalized,
                                                formatter);

                        } catch (DateTimeParseException ignored) {
                                // Try next format.
                        }
                }

                /*
                 * Also support:
                 *
                 * 10AM
                 * 10PM
                 */
                if (normalized.matches(
                                "\\d{1,2}(AM|PM)")) {

                        String hour = normalized.substring(
                                        0,
                                        normalized.length() - 2);

                        String period = normalized.substring(
                                        normalized.length() - 2);

                        normalized = hour + " " + period;

                        for (DateTimeFormatter formatter : TIME_FORMATTERS) {

                                try {

                                        return LocalTime.parse(
                                                        normalized,
                                                        formatter);

                                } catch (DateTimeParseException ignored) {
                                        // Try next format.
                                }
                        }
                }

                throw new IllegalArgumentException(
                                "Travel time could not be understood: "
                                                + time
                                                + ". Use a time such as 10:00 AM.");
        }

        // ============================================================
        // NORMALIZE SEAT
        // ============================================================

        private String normalizeSeat(
                        String seatNumber) {

                if (seatNumber == null
                                || seatNumber.isBlank()) {

                        throw new IllegalArgumentException(
                                        "Seat number is required.");
                }

                return seatNumber
                                .trim()
                                .toUpperCase();
        }

        // ============================================================
        // VALIDATE SEAT
        // ============================================================

        private void validateSeatNumber(
                        String seatNumber,
                        int capacity) {

                if (!seatNumber.matches(
                                "\\d+[A-D]")) {

                        throw new IllegalArgumentException(
                                        "Seat number must use a format such as 1A, 12B, or 20D.");
                }

                int row;

                try {

                        row = Integer.parseInt(
                                        seatNumber.substring(
                                                        0,
                                                        seatNumber.length() - 1));

                } catch (NumberFormatException exception) {

                        throw new IllegalArgumentException(
                                        "Invalid seat number.");
                }

                int seatIndex = seatNumber.charAt(
                                seatNumber.length() - 1)
                                - 'A';

                int seatPosition = ((row - 1) * 4)
                                + seatIndex
                                + 1;

                if (row < 1
                                || seatPosition > capacity) {

                        throw new IllegalArgumentException(
                                        "Seat number is outside the bus capacity.");
                }
        }

        // ============================================================
        // GENERATE SEAT NUMBER
        // ============================================================

        private String generateSeatNumber(
                        int position) {

                int row = ((position - 1) / 4) + 1;

                int seatIndex = (position - 1) % 4;

                char letter = (char) ('A' + seatIndex);

                return row + String.valueOf(letter);
        }

        // ============================================================
        // RESPONSE
        // ============================================================

        private TripResponse toResponse(
                        Trip trip) {

                return new TripResponse(
                                trip.getId(),
                                trip.getBus().getId(),
                                trip.getBus().getPlateNumber(),
                                trip.getBus().getCapacity(),
                                trip.getRoute().getId(),
                                trip.getRoute().getRouteIdentifier(),
                                null,
                                null,
                                trip.getScheduledDeparture(),
                                trip.getScheduledArrival(),
                                trip.getStatus(),
                                trip.getCreatedAt(),
                                trip.getUpdatedAt());
        }

        // ============================================================
        // SEARCH WINDOW
        // ============================================================

        private record SearchWindow(
                        LocalDateTime from,
                        LocalDateTime to) {
        }
}
