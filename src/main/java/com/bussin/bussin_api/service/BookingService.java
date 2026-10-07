package com.bussin.bussin_api.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.bussin.bussin_api.dto.BookingResponse;\nimport com.bussin.bussin_api.dto.FareQuoteRequest;\nimport com.bussin.bussin_api.dto.FareQuoteResponse;
import com.bussin.bussin_api.dto.CreateBookingRequest;
import com.bussin.bussin_api.dto.UpdatePaymentStatusRequest;
import com.bussin.bussin_api.dto.UpdateBookingStatusRequest;
import com.bussin.bussin_api.entity.Booking;
import com.bussin.bussin_api.entity.BookingSeat;
import com.bussin.bussin_api.entity.BookingStatus;
import com.bussin.bussin_api.entity.Bus;
import com.bussin.bussin_api.entity.CancelledBookingArchive;
import com.bussin.bussin_api.entity.PaymentStatus;
import com.bussin.bussin_api.entity.QueueEntry;
import com.bussin.bussin_api.entity.Role;
import com.bussin.bussin_api.entity.Trip;
import com.bussin.bussin_api.entity.TripStatus;
import com.bussin.bussin_api.entity.User;
import com.bussin.bussin_api.exception.ConflictException;
import com.bussin.bussin_api.exception.ResourceNotFoundException;
import com.bussin.bussin_api.repository.BookingRepository;
import com.bussin.bussin_api.repository.BookingSeatRepository;
import com.bussin.bussin_api.repository.CancelledBookingArchiveRepository;
import com.bussin.bussin_api.repository.TripRepository;
import com.bussin.bussin_api.repository.UserRepository;
import com.google.firebase.auth.FirebaseToken;

@Service
public class BookingService {

    private static final int SEATS_PER_ROW = 6;

    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final CancelledBookingArchiveRepository cancelledBookingArchiveRepository;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final QueueService queueService;\n    private final RouteFareService routeFareService;

    public BookingService(
            BookingRepository bookingRepository,
            BookingSeatRepository bookingSeatRepository,
            CancelledBookingArchiveRepository cancelledBookingArchiveRepository,
            TripRepository tripRepository,
            UserRepository userRepository,
            QueueService queueService,\n            RouteFareService routeFareService) {

        this.bookingRepository = bookingRepository;
        this.bookingSeatRepository = bookingSeatRepository;
        this.cancelledBookingArchiveRepository = cancelledBookingArchiveRepository;
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
        this.queueService = queueService;\n        this.routeFareService = routeFareService;
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

        return createBookingFor(request, commuter);
    }

    @Transactional
    public BookingResponse createGuestBooking(CreateBookingRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof FirebaseToken) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Sign out before creating a guest booking.");
        }
        return createBookingFor(request, null);
    }

    private BookingResponse createBookingFor(
            CreateBookingRequest request,
            User commuter) {

        Trip trip = tripRepository.findByIdForUpdate(request.getTripId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Trip not found: " + request.getTripId()));

        validateTrip(trip);

        List<String> seatNumbers = normalizeSeatNumbers(request.getSeatNumbers());

        if (seatNumbers.isEmpty()) {
            throw new ConflictException("At least one seat must be selected.");
        }

        if (seatNumbers.size() > trip.getBus().getCapacity()) {
            throw new ConflictException("The selected seat count exceeds bus capacity.");
        }

        List<BookingStatus> activeStatuses = List.of(
                BookingStatus.PENDING,
                BookingStatus.CONFIRMED);

        if (commuter != null) {
            boolean alreadyBooked = bookingRepository
                    .existsByCommuterIdAndTripIdAndStatusIn(
                            commuter.getId(),
                            trip.getId(),
                            activeStatuses);

            if (alreadyBooked) {
                throw new ConflictException(
                        "You already have an active booking for this trip.");
            }
        }

        for (String seatNumber : seatNumbers) {
            validateSeatNumber(seatNumber, trip.getBus());

            boolean childSeatBooked = bookingSeatRepository
                    .existsActiveBookingSeat(
                            trip.getId(),
                            seatNumber,
                            activeStatuses);

            if (childSeatBooked) {
                throw new ConflictException(
                        "Seat " + seatNumber + " is already booked.");
            }
        }

        if (trip.getRoute().getBaseFare() == null) {
            throw new ConflictException(
                    "The selected trip does not have a configured fare.");
        }

        FareQuoteResponse fareQuote = routeFareService.quote(
                trip.getRoute().getId(),
                new FareQuoteRequest(
                        request.getDropoffLatitude(),
                        request.getDropoffLongitude(),
                        request.getPassengerType()));

        BigDecimal farePerSeat = fareQuote.finalFare();
        BigDecimal totalFare = farePerSeat.multiply(BigDecimal.valueOf(seatNumbers.size()));

        LocalDateTime now = LocalDateTime.now();

        Booking booking = new Booking();

        booking.setBookingReference(generateBookingReference());
        booking.setCommuter(commuter);
        booking.setTrip(trip);

        booking.setPassengerName(request.getPassengerName().trim());
        booking.setPassengerPhone(request.getPassengerPhone().trim());
        booking.setPassengerEmail(request.getPassengerEmail().trim());

        // Kept for backward compatibility with the existing schema and API.
        booking.setSeatNumber(seatNumbers.get(0));
        booking.setFare(totalFare);\n        booking.setDropoffLatitude(request.getDropoffLatitude());\n        booking.setDropoffLongitude(request.getDropoffLongitude());\n        booking.setPassengerType(request.getPassengerType().trim().toUpperCase());

        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setPaymentStatus(PaymentStatus.UNPAID);

        booking.setCreatedAt(now);
        booking.setUpdatedAt(now);

        for (String seatNumber : seatNumbers) {
            BookingSeat bookingSeat = new BookingSeat();
            bookingSeat.setTrip(trip);
            bookingSeat.setSeatNumber(seatNumber);
            bookingSeat.setFare(farePerSeat);
            booking.addBookingSeat(bookingSeat);
        }

        Booking savedBooking;
        try {
            savedBooking = bookingRepository.saveAndFlush(booking);
        } catch (DataIntegrityViolationException exception) {
            if (isUniqueConstraintViolation(exception)) {
                throw new ConflictException(
                        "A booking conflicts with an existing booking or seat allocation. "
                                + "Refresh availability and try again.");
            }
            throw exception;
        }

        QueueEntry queueEntry = queueService.ensureQueueEntryForBooking(trip, savedBooking);

        return BookingResponse.from(savedBooking, queueEntry);
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

            boolean childOccupied = bookingSeatRepository
                    .existsActiveBookingSeat(
                            tripId,
                            seatNumber,
                            activeStatuses);

            if (!childOccupied) {
                availableSeats.add(seatNumber);
            }
        }

        return availableSeats;
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getAllBookings() {
        requireAdminAccess();
        List<BookingResponse> bookings = new ArrayList<>(bookingRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList());
        bookings.addAll(cancelledBookingArchiveRepository.findAll()
                .stream()
                .map(BookingResponse::from)
                .toList());
        return sortBookings(bookings);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getEmployeeBookings() {
        User employee = getAuthenticatedUser();
        if (employee.getRole() != Role.EMPLOYEE) {
            throw new ConflictException("Employee access is required.");
        }
        List<Long> tripIds = tripRepository.findAssignedToEmployee(employee.getId())
                .stream()
                .map(Trip::getId)
                .toList();
        List<BookingResponse> bookings = new ArrayList<>(bookingRepository
                .findAssignedToEmployee(employee.getId())
                .stream()
                .map(this::toResponse)
                .toList());
        if (!tripIds.isEmpty()) {
            bookings.addAll(cancelledBookingArchiveRepository
                    .findByTripIdInOrderByCreatedAtDesc(tripIds)
                    .stream()
                    .map(BookingResponse::from)
                    .toList());
        }
        return sortBookings(bookings);
    }

    private List<BookingResponse> sortBookings(List<BookingResponse> bookings) {
        bookings.sort(Comparator.comparing(BookingResponse::getCreatedAt).reversed());
        return bookings;
    }

    @Transactional(readOnly = true)
    public BookingResponse getBookingForStaff(Long bookingId) {
        requireAdminAccess();
        BookingResponse response = bookingRepository.findById(bookingId)
                .map(this::toResponse)
                .orElseGet(() -> cancelledBookingArchiveRepository.findById(bookingId)
                        .map(BookingResponse::from)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Booking not found: " + bookingId)));
        return response;
    }

    @Transactional
    public BookingResponse updateBookingStatus(
            Long bookingId,
            UpdateBookingStatusRequest request) {

        requireAdminAccess();

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Booking not found: " + bookingId));

        BookingStatus newStatus = request.getStatus();

        if (booking.getStatus() == BookingStatus.COMPLETED
                && newStatus != BookingStatus.COMPLETED) {
            throw new ConflictException(
                    "Completed bookings cannot be reopened.");
        }

        if (newStatus == BookingStatus.CANCELLED) {
            return archiveAndDeleteBooking(booking);
        }

        booking.setStatus(newStatus);
        booking.setUpdatedAt(LocalDateTime.now());

        Booking savedBooking = bookingRepository.save(booking);

        syncQueueWithBookingStatus(savedBooking);

        return toResponse(savedBooking);
    }

    @Transactional
    public BookingResponse updateAdminPaymentStatus(
            Long bookingId,
            UpdatePaymentStatusRequest request) {
        requireAdminAccess();
        return updatePaymentStatus(bookingId, request.getPaymentStatus(), null);
    }

    @Transactional
    public BookingResponse updateEmployeePaymentStatus(
            Long bookingId,
            UpdatePaymentStatusRequest request) {
        User employee = getAuthenticatedUser();
        if (employee.getRole() != Role.EMPLOYEE) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Employee access is required.");
        }
        return updatePaymentStatus(bookingId, request.getPaymentStatus(), employee);
    }

    private BookingResponse updatePaymentStatus(
            Long bookingId,
            PaymentStatus newStatus,
            User employee) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking != null) {
            requireAssignedTripAccess(booking.getTrip().getId(), employee);
            validatePaymentStatusChange(booking.getPaymentStatus(), newStatus);
            booking.setPaymentStatus(newStatus);
            booking.setUpdatedAt(LocalDateTime.now());
            return toResponse(bookingRepository.save(booking));
        }

        CancelledBookingArchive archive = cancelledBookingArchiveRepository
                .findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Booking not found: " + bookingId));
        requireAssignedTripAccess(archive.getTripId(), employee);
        validatePaymentStatusChange(archive.getPaymentStatus(), newStatus);
        archive.setPaymentStatus(newStatus);
        archive.setUpdatedAt(LocalDateTime.now());
        return BookingResponse.from(cancelledBookingArchiveRepository.save(archive));
    }

    private void requireAssignedTripAccess(Long tripId, User employee) {
        if (employee != null
                && !tripRepository.existsByIdAndEmployeeId(tripId, employee.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can only update payment for bookings on trips assigned to you.");
        }
    }

    private void validatePaymentStatusChange(
            PaymentStatus currentStatus,
            PaymentStatus newStatus) {
        if (newStatus == PaymentStatus.REFUNDED
                && currentStatus != PaymentStatus.PAID
                && currentStatus != PaymentStatus.REFUNDED) {
            throw new ConflictException(
                    "Only a paid booking can be marked as refunded.");
        }
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getMyBookings() {

        String firebaseUid = getAuthenticatedFirebaseUid();

        User commuter = userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "BUSSIN user profile not found."));

        List<BookingResponse> bookings = new ArrayList<>(bookingRepository
                .findByCommuterIdOrderByCreatedAtDesc(commuter.getId())
                .stream()
                .map(this::toResponse)
                .toList());
        bookings.addAll(cancelledBookingArchiveRepository
                .findByCommuterIdOrderByCreatedAtDesc(commuter.getId())
                .stream()
                .map(BookingResponse::from)
                .toList());
        bookings.sort(Comparator.comparing(BookingResponse::getCreatedAt).reversed());
        return bookings;
    }

    @Transactional(readOnly = true)
    public BookingResponse getMyBooking(Long bookingId) {

        String firebaseUid = getAuthenticatedFirebaseUid();

        User commuter = userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "BUSSIN user profile not found."));

        return bookingRepository.findByIdAndCommuterId(bookingId, commuter.getId())
                .map(this::toResponse)
                .orElseGet(() -> cancelledBookingArchiveRepository
                        .findByBookingIdAndCommuterId(bookingId, commuter.getId())
                        .map(BookingResponse::from)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Booking not found: " + bookingId)));
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
            return archiveAndDeleteBooking(booking);
        }

        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new ConflictException(
                    "Completed bookings cannot be cancelled.");
        }

        return archiveAndDeleteBooking(booking);
    }

    private BookingResponse archiveAndDeleteBooking(Booking booking) {
        LocalDateTime cancelledAt = LocalDateTime.now();
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setUpdatedAt(cancelledAt);

        queueService.cancelQueueEntryForBooking(booking);
        QueueEntry queueEntry = queueService.findQueueEntryForBooking(booking);

        CancelledBookingArchive archive = CancelledBookingArchive.from(
                booking,
                queueEntry,
                cancelledAt);
        CancelledBookingArchive savedArchive = cancelledBookingArchiveRepository.saveAndFlush(archive);

        queueService.detachBookingFromQueueEntry(booking);
        bookingRepository.delete(booking);
        bookingRepository.flush();

        return BookingResponse.from(savedArchive);
    }

    private BookingResponse toResponse(Booking booking) {
        QueueEntry queueEntry = queueService.findQueueEntryForBooking(booking);

        return BookingResponse.from(booking, queueEntry);
    }

    private void syncQueueWithBookingStatus(Booking booking) {
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            queueService.cancelQueueEntryForBooking(booking);
            return;
        }

        if (booking.getStatus() == BookingStatus.COMPLETED) {
            queueService.completeQueueEntryForBooking(booking);
            return;
        }

        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            queueService.ensureQueueEntryForBooking(booking.getTrip(), booking);
        }
    }

    private void requireAdminAccess() {
        User user = getAuthenticatedUser();

        if (user.getRole() != Role.ADMIN) {
            throw new ConflictException(
                    "Administrator access is required.");
        }
    }

    private User getAuthenticatedUser() {
        String firebaseUid = getAuthenticatedFirebaseUid();

        return userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "BUSSIN user profile not found."));
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

    private void validateSeatNumber(
            String seatNumber,
            Bus bus) {

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

    private List<String> normalizeSeatNumbers(List<String> seatNumbers) {

        if (seatNumbers == null) {
            return List.of();
        }

        LinkedHashSet<String> normalized = new LinkedHashSet<>();

        for (String seatNumber : seatNumbers) {
            if (seatNumber == null || seatNumber.isBlank()) {
                throw new ConflictException(
                        "Seat numbers cannot be blank.");
            }

            normalized.add(seatNumber.trim().toUpperCase());
        }

        if (normalized.size() != seatNumbers.size()) {
            throw new ConflictException(
                    "A seat cannot be selected more than once.");
        }

        return normalized.stream().sorted().toList();
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

    private boolean isUniqueConstraintViolation(DataIntegrityViolationException exception) {
        Throwable cause = exception.getMostSpecificCause();
        return cause instanceof SQLException sqlException
                && "23505".equals(sqlException.getSQLState());
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
