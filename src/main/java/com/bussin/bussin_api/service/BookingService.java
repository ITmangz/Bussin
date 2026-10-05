package com.bussin.bussin_api.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bussin.bussin_api.dto.BookingResponse;
import com.bussin.bussin_api.dto.CreateBookingRequest;
import com.bussin.bussin_api.entity.Booking;
import com.bussin.bussin_api.entity.BookingStatus;
import com.bussin.bussin_api.entity.Bus;
import com.bussin.bussin_api.entity.PaymentStatus;
import com.bussin.bussin_api.entity.Role;
import com.bussin.bussin_api.entity.Trip;
import com.bussin.bussin_api.entity.TripStatus;
import com.bussin.bussin_api.entity.User;
import com.bussin.bussin_api.exception.ConflictException;
import com.bussin.bussin_api.exception.ResourceNotFoundException;
import com.bussin.bussin_api.repository.BookingRepository;
import com.bussin.bussin_api.repository.TripRepository;
import com.bussin.bussin_api.repository.UserRepository;
import com.google.firebase.auth.FirebaseToken;

@Service
public class BookingService {

        private static final int SEATS_PER_ROW = 6;

        private final BookingRepository bookingRepository;
        private final TripRepository tripRepository;
        private final UserRepository userRepository;

        public BookingService(
                        BookingRepository bookingRepository,
                        TripRepository tripRepository,
                        UserRepository userRepository) {

                this.bookingRepository = bookingRepository;
                this.tripRepository = tripRepository;
                this.userRepository = userRepository;
        }

        @Transactional
        public BookingResponse createBooking(CreateBookingRequest request) {

                String firebaseUid = getAuthenticatedFirebaseUid();

                User commuter = userRepository.findByFirebaseUid(firebaseUid)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "BUSSIN user profile not found."));

                if (commuter.getRole() != Role.COMMUTER) {
                        throw new ConflictException(
                                        "Only commuter accounts can create bookings.");
                }

                Trip trip = tripRepository.findById(request.getTripId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Trip not found: " + request.getTripId()));

                validateTrip(trip);

                String seatNumber = normalizeSeatNumber(request.getSeatNumber());

                validateSeatNumber(seatNumber, trip.getBus());

                List<BookingStatus> activeStatuses = List.of(
                                BookingStatus.PENDING,
                                BookingStatus.CONFIRMED);

                boolean alreadyBooked = bookingRepository
                                .existsByCommuterIdAndTripIdAndStatusIn(
                                                commuter.getId(),
                                                trip.getId(),
                                                activeStatuses);

                if (alreadyBooked) {
                        throw new ConflictException(
                                        "You already have an active booking for this trip.");
                }

                boolean seatAlreadyBooked = bookingRepository
                                .existsByTripIdAndSeatNumberAndStatusIn(
                                                trip.getId(),
                                                seatNumber,
                                                activeStatuses);

                if (seatAlreadyBooked) {
                        throw new ConflictException(
                                        "Seat " + seatNumber + " is already booked.");
                }

                if (trip.getRoute().getBaseFare() == null) {
                        throw new ConflictException(
                                        "The selected trip does not have a configured fare.");
                }

                LocalDateTime now = LocalDateTime.now();

                Booking booking = new Booking();

                booking.setBookingReference(generateBookingReference());
                booking.setCommuter(commuter);
                booking.setTrip(trip);

                booking.setPassengerName(request.getPassengerName().trim());
                booking.setPassengerPhone(request.getPassengerPhone().trim());
                booking.setPassengerEmail(request.getPassengerEmail().trim());

                booking.setSeatNumber(seatNumber);
                booking.setFare(trip.getRoute().getBaseFare());

                booking.setStatus(BookingStatus.CONFIRMED);
                booking.setPaymentStatus(PaymentStatus.UNPAID);

                booking.setCreatedAt(now);
                booking.setUpdatedAt(now);

                Booking savedBooking = bookingRepository.save(booking);

                return BookingResponse.from(savedBooking);
        }

        @Transactional(readOnly = true)
        public List<String> getAvailableSeats(Long tripId) {

                Trip trip = tripRepository.findById(tripId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Trip not found: " + tripId));

                validateTrip(trip);

                int capacity = trip.getBus().getCapacity();

                List<BookingStatus> activeStatuses = List.of(
                                BookingStatus.PENDING,
                                BookingStatus.CONFIRMED);

                List<String> availableSeats = new ArrayList<>();

                for (int position = 1; position <= capacity; position++) {

                        int row = ((position - 1) / SEATS_PER_ROW) + 1;

                        int seatIndex = (position - 1) % SEATS_PER_ROW;

                        String seatNumber = row
                                        + String.valueOf((char) ('A' + seatIndex));

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

        @Transactional(readOnly = true)
        public List<BookingResponse> getMyBookings() {

                String firebaseUid = getAuthenticatedFirebaseUid();

                User commuter = userRepository.findByFirebaseUid(firebaseUid)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "BUSSIN user profile not found."));

                System.out.println("========================================");
                System.out.println("GET MY BOOKINGS");
                System.out.println("Firebase UID: " + firebaseUid);
                System.out.println("Commuter ID: " + commuter.getId());
                System.out.println("Commuter Email: " + commuter.getEmail());

                List<Booking> bookings = bookingRepository.findByCommuterIdOrderByCreatedAtDesc(
                                commuter.getId());

                System.out.println("Bookings found: " + bookings.size());

                for (Booking booking : bookings) {
                        System.out.println(
                                        "Booking ID: " + booking.getId()
                                                        + " | Reference: " + booking.getBookingReference()
                                                        + " | Commuter ID: " + booking.getCommuter().getId());
                }

                System.out.println("========================================");

                return bookings.stream()
                                .map(BookingResponse::from)
                                .toList();
        }

        @Transactional(readOnly = true)
        public BookingResponse getMyBooking(Long bookingId) {

                String firebaseUid = getAuthenticatedFirebaseUid();

                User commuter = userRepository.findByFirebaseUid(firebaseUid)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "BUSSIN user profile not found."));

                Booking booking = bookingRepository
                                .findByIdAndCommuterId(bookingId, commuter.getId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Booking not found: " + bookingId));

                return BookingResponse.from(booking);
        }

        @Transactional
        public BookingResponse cancelMyBooking(Long bookingId) {

                String firebaseUid = getAuthenticatedFirebaseUid();

                User commuter = userRepository.findByFirebaseUid(firebaseUid)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "BUSSIN user profile not found."));

                Booking booking = bookingRepository
                                .findByIdAndCommuterId(bookingId, commuter.getId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Booking not found: " + bookingId));

                if (booking.getStatus() == BookingStatus.CANCELLED) {
                        throw new ConflictException(
                                        "Booking is already cancelled.");
                }

                if (booking.getStatus() == BookingStatus.COMPLETED) {
                        throw new ConflictException(
                                        "Completed bookings cannot be cancelled.");
                }

                booking.setStatus(BookingStatus.CANCELLED);
                booking.setUpdatedAt(LocalDateTime.now());

                Booking savedBooking = bookingRepository.save(booking);

                return BookingResponse.from(savedBooking);
        }

        private void validateTrip(Trip trip) {

                if (trip.getStatus() != TripStatus.SCHEDULED
                                && trip.getStatus() != TripStatus.BOARDING) {

                        throw new ConflictException(
                                        "This trip is not available for booking.");
                }

                if (!trip.getRoute().isActive()) {
                        throw new ConflictException(
                                        "The route for this trip is inactive.");
                }

                if (trip.getBus().getStatus().name().equals("OUT_OF_SERVICE")) {
                        throw new ConflictException(
                                        "The bus assigned to this trip is out of service.");
                }
        }

        private void validateSeatNumber(String seatNumber, Bus bus) {

                int letterIndex = getSeatLetterIndex(seatNumber);
                int rowNumber = getSeatRow(seatNumber);

                if (rowNumber <= 0) {
                        throw new ConflictException(
                                        "Invalid seat number: " + seatNumber);
                }

                if (letterIndex < 0 || letterIndex >= SEATS_PER_ROW) {
                        throw new ConflictException(
                                        "Invalid seat number: " + seatNumber);
                }

                int seatIndex = ((rowNumber - 1) * SEATS_PER_ROW)
                                + letterIndex
                                + 1;

                if (seatIndex > bus.getCapacity()) {
                        throw new ConflictException(
                                        "Seat " + seatNumber
                                                        + " does not exist on this bus.");
                }
        }

        private int getSeatRow(String seatNumber) {

                String numericPart = seatNumber.substring(
                                0,
                                seatNumber.length() - 1);

                try {
                        return Integer.parseInt(numericPart);
                } catch (NumberFormatException ex) {
                        throw new ConflictException(
                                        "Invalid seat number: " + seatNumber);
                }
        }

        private int getSeatLetterIndex(String seatNumber) {

                char letter = seatNumber.charAt(
                                seatNumber.length() - 1);

                return switch (letter) {
                        case 'A' -> 0;
                        case 'B' -> 1;
                        case 'C' -> 2;
                        case 'D' -> 3;
                        case 'E' -> 4;
                        case 'F' -> 5;
                        default -> -1;
                };
        }

        private String normalizeSeatNumber(String seatNumber) {

                if (seatNumber == null || seatNumber.isBlank()) {
                        throw new ConflictException(
                                        "Seat number is required.");
                }

                return seatNumber.trim().toUpperCase();
        }

        private String generateBookingReference() {

                String reference;

                do {
                        reference = "BK-" + UUID.randomUUID()
                                        .toString()
                                        .replace("-", "")
                                        .substring(0, 12)
                                        .toUpperCase();

                } while (bookingRepository
                                .findByBookingReference(reference)
                                .isPresent());

                return reference;
        }

        private String getAuthenticatedFirebaseUid() {

                Authentication authentication = SecurityContextHolder
                                .getContext()
                                .getAuthentication();

                if (authentication == null
                                || !authentication.isAuthenticated()) {

                        throw new ConflictException(
                                        "Authenticated Firebase user is required.");
                }

                Object principal = authentication.getPrincipal();

                if (!(principal instanceof FirebaseToken firebaseToken)) {

                        throw new ConflictException(
                                        "Invalid Firebase authentication.");
                }

                return firebaseToken.getUid();
        }
}