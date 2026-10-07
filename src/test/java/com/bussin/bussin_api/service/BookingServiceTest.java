package com.bussin.bussin_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import com.bussin.bussin_api.dto.CreateBookingRequest;
import com.bussin.bussin_api.dto.BookingResponse;
import com.bussin.bussin_api.dto.UpdatePaymentStatusRequest;
import com.bussin.bussin_api.entity.Booking;
import com.bussin.bussin_api.entity.BookingSeat;
import com.bussin.bussin_api.entity.BookingStatus;
import com.bussin.bussin_api.entity.Bus;
import com.bussin.bussin_api.entity.BusStatus;
import com.bussin.bussin_api.entity.CancelledBookingArchive;
import com.bussin.bussin_api.entity.PaymentStatus;
import com.bussin.bussin_api.entity.QueueEntry;
import com.bussin.bussin_api.entity.QueueStatus;
import com.bussin.bussin_api.entity.Role;
import com.bussin.bussin_api.entity.Route;
import com.bussin.bussin_api.entity.Trip;
import com.bussin.bussin_api.entity.TripStatus;
import com.bussin.bussin_api.entity.User;
import com.bussin.bussin_api.exception.ConflictException;
import com.bussin.bussin_api.repository.BookingRepository;
import com.bussin.bussin_api.repository.BookingSeatRepository;
import com.bussin.bussin_api.repository.CancelledBookingArchiveRepository;
import com.bussin.bussin_api.repository.TripRepository;
import com.bussin.bussin_api.repository.UserRepository;
import com.google.firebase.auth.FirebaseToken;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingSeatRepository bookingSeatRepository;

    @Mock
    private CancelledBookingArchiveRepository cancelledBookingArchiveRepository;

    @Mock
    private TripRepository tripRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private QueueService queueService;

    @InjectMocks
    private BookingService bookingService;

    @BeforeEach
    void authenticateCommuter() {
        FirebaseToken firebaseToken = mock(FirebaseToken.class);
        lenient().when(firebaseToken.getUid()).thenReturn("firebase-uid");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        firebaseToken,
                        null,
                        List.of()));
    }

    @AfterEach
    void clearAuthentication() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createBookingTranslatesDatabaseConstraintViolationToConflict() {
        User commuter = mock(User.class);
        when(commuter.getRole()).thenReturn(Role.COMMUTER);
        when(commuter.getId()).thenReturn(18L);
        when(userRepository.findByFirebaseUid("firebase-uid"))
                .thenReturn(Optional.of(commuter));

        Bus bus = mock(Bus.class);
        when(bus.getCapacity()).thenReturn(40);
        when(bus.getStatus()).thenReturn(BusStatus.ACTIVE);

        Route route = mock(Route.class);
        when(route.isActive()).thenReturn(true);
        when(route.getBaseFare()).thenReturn(new BigDecimal("100.00"));

        Trip trip = mock(Trip.class);
        when(trip.getId()).thenReturn(9L);
        when(trip.getStatus()).thenReturn(TripStatus.SCHEDULED);
        when(trip.getBus()).thenReturn(bus);
        when(trip.getRoute()).thenReturn(route);
        when(tripRepository.findByIdForUpdate(9L))
                .thenReturn(Optional.of(trip));

        when(bookingRepository.existsByCommuterIdAndTripIdAndStatusIn(
                18L,
                9L,
                List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED)))
                .thenReturn(false);
        when(bookingSeatRepository.existsActiveBookingSeat(
                9L,
                "1A",
                List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED)))
                .thenReturn(false);
        when(bookingRepository.findByBookingReference(org.mockito.ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());
        when(bookingRepository.saveAndFlush(org.mockito.ArgumentMatchers.any(Booking.class)))
                .thenThrow(new DataIntegrityViolationException(
                        "duplicate key violates uk_booking_commuter_trip",
                        new SQLException("duplicate key", "23505")));

        CreateBookingRequest request = new CreateBookingRequest();
        request.setTripId(9L);
        request.setSeatNumbers(List.of("1A"));
        request.setPassengerName("Commuter");
        request.setPassengerPhone("09123456789");
        request.setPassengerEmail("commuter@example.com");

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> bookingService.createBooking(request));

        assertEquals(
                "A booking conflicts with an existing booking or seat allocation. "
                        + "Refresh availability and try again.",
                exception.getMessage());
        verify(queueService, never()).ensureQueueEntryForBooking(trip, commuter);
    }

    @Test
    void createBookingRethrowsNonUniqueDatabaseConstraintViolation() {
        User commuter = mock(User.class);
        when(commuter.getRole()).thenReturn(Role.COMMUTER);
        when(commuter.getId()).thenReturn(18L);
        when(userRepository.findByFirebaseUid("firebase-uid"))
                .thenReturn(Optional.of(commuter));

        Bus bus = mock(Bus.class);
        when(bus.getCapacity()).thenReturn(40);
        when(bus.getStatus()).thenReturn(BusStatus.ACTIVE);

        Route route = mock(Route.class);
        when(route.isActive()).thenReturn(true);
        when(route.getBaseFare()).thenReturn(new BigDecimal("100.00"));

        Trip trip = mock(Trip.class);
        when(trip.getId()).thenReturn(9L);
        when(trip.getStatus()).thenReturn(TripStatus.SCHEDULED);
        when(trip.getBus()).thenReturn(bus);
        when(trip.getRoute()).thenReturn(route);
        when(tripRepository.findByIdForUpdate(9L))
                .thenReturn(Optional.of(trip));

        when(bookingRepository.existsByCommuterIdAndTripIdAndStatusIn(
                18L,
                9L,
                List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED)))
                .thenReturn(false);
        when(bookingSeatRepository.existsActiveBookingSeat(
                9L,
                "1A",
                List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED)))
                .thenReturn(false);
        when(bookingRepository.findByBookingReference(org.mockito.ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());
        DataIntegrityViolationException foreignKeyViolation = new DataIntegrityViolationException(
                "foreign key violation",
                new SQLException("foreign key violation", "23503"));
        when(bookingRepository.saveAndFlush(org.mockito.ArgumentMatchers.any(Booking.class)))
                .thenThrow(foreignKeyViolation);

        CreateBookingRequest request = new CreateBookingRequest();
        request.setTripId(9L);
        request.setSeatNumbers(List.of("1A"));
        request.setPassengerName("Commuter");
        request.setPassengerPhone("09123456789");
        request.setPassengerEmail("commuter@example.com");

        DataIntegrityViolationException exception = assertThrows(
                DataIntegrityViolationException.class,
                () -> bookingService.createBooking(request));

        assertEquals(foreignKeyViolation, exception);
        verify(queueService, never()).ensureQueueEntryForBooking(trip, commuter);
    }

    @Test
    void guestBookingCanBeCreatedWithoutACommuterProfile() {
        SecurityContextHolder.clearContext();

        Bus bus = mock(Bus.class);
        when(bus.getCapacity()).thenReturn(40);
        when(bus.getStatus()).thenReturn(BusStatus.ACTIVE);
        when(bus.getId()).thenReturn(7L);
        when(bus.getPlateNumber()).thenReturn("ABC-1234");

        Route route = mock(Route.class);
        when(route.isActive()).thenReturn(true);
        when(route.getBaseFare()).thenReturn(new BigDecimal("100.00"));

        Trip trip = mock(Trip.class);
        when(trip.getId()).thenReturn(9L);
        when(trip.getStatus()).thenReturn(TripStatus.SCHEDULED);
        when(trip.getBus()).thenReturn(bus);
        when(trip.getRoute()).thenReturn(route);
        when(tripRepository.findByIdForUpdate(9L)).thenReturn(Optional.of(trip));
        when(bookingSeatRepository.existsActiveBookingSeat(
                9L,
                "1A",
                List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED)))
                .thenReturn(false);
        when(bookingRepository.findByBookingReference(org.mockito.ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());
        when(bookingRepository.saveAndFlush(org.mockito.ArgumentMatchers.any(Booking.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        QueueEntry queueEntry = mock(QueueEntry.class);
        when(queueEntry.getQueueNumber()).thenReturn(1);
        when(queueEntry.getStatus()).thenReturn(QueueStatus.WAITING);
        when(queueService.ensureQueueEntryForBooking(
                org.mockito.ArgumentMatchers.eq(trip),
                org.mockito.ArgumentMatchers.any(Booking.class)))
                .thenReturn(queueEntry);

        CreateBookingRequest request = new CreateBookingRequest();
        request.setTripId(9L);
        request.setSeatNumbers(List.of("1A"));
        request.setPassengerName("Guest Passenger");
        request.setPassengerPhone("09123456789");
        request.setPassengerEmail("guest@example.com");

        BookingResponse response = bookingService.createGuestBooking(request);

        assertTrue(response.isGuestBooking());
        assertEquals("Guest", response.getCommuterName());
        assertEquals("Guest Passenger", response.getPassengerName());
        assertEquals(1, response.getQueueNumber());
        verify(userRepository, never()).findByFirebaseUid(org.mockito.ArgumentMatchers.anyString());
        verify(queueService).ensureQueueEntryForBooking(
                org.mockito.ArgumentMatchers.eq(trip),
                org.mockito.ArgumentMatchers.any(Booking.class));
    }

    @Test
    void authenticatedUserCannotUseGuestBookingEndpoint() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> bookingService.createGuestBooking(new CreateBookingRequest()));

        assertEquals(org.springframework.http.HttpStatus.FORBIDDEN, exception.getStatusCode());
    }

    @Test
    void cancelMyBookingArchivesHistoryBeforeDeletingActiveRecord() {
        User commuter = mock(User.class);
        when(commuter.getId()).thenReturn(18L);
        when(commuter.getEmail()).thenReturn("commuter@example.com");
        when(commuter.getFirstName()).thenReturn("Commuter");
        when(commuter.getLastName()).thenReturn("One");
        when(userRepository.findByFirebaseUid("firebase-uid"))
                .thenReturn(Optional.of(commuter));

        Route route = mock(Route.class);
        when(route.getRouteIdentifier()).thenReturn("BAC-MARS");
        when(route.getOrigin()).thenReturn("Bacolod");
        when(route.getDestination()).thenReturn("Manila");

        Bus bus = mock(Bus.class);
        when(bus.getId()).thenReturn(7L);
        when(bus.getPlateNumber()).thenReturn("ABC-1234");

        Trip trip = mock(Trip.class);
        when(trip.getId()).thenReturn(9L);
        when(trip.getRoute()).thenReturn(route);
        when(trip.getBus()).thenReturn(bus);

        BookingSeat bookingSeat = mock(BookingSeat.class);
        when(bookingSeat.getSeatNumber()).thenReturn("1A");

        Booking booking = mock(Booking.class);
        when(booking.getId()).thenReturn(41L);
        when(booking.getStatus()).thenReturn(BookingStatus.CONFIRMED);
        when(booking.getBookingReference()).thenReturn("BUS-REF-41");
        when(booking.getCommuter()).thenReturn(commuter);
        when(booking.getTrip()).thenReturn(trip);
        when(booking.getPassengerName()).thenReturn("Commuter One");
        when(booking.getPassengerPhone()).thenReturn("09123456789");
        when(booking.getPassengerEmail()).thenReturn("commuter@example.com");
        when(booking.getSeatNumber()).thenReturn("1A");
        when(booking.getBookingSeats()).thenReturn(List.of(bookingSeat));
        when(booking.getFare()).thenReturn(new BigDecimal("100.00"));
        when(booking.getPaymentStatus()).thenReturn(PaymentStatus.PAID);
        when(booking.getCreatedAt()).thenReturn(java.time.LocalDateTime.parse("2026-10-06T10:00:00"));
        when(bookingRepository.findByIdAndCommuterId(41L, 18L))
                .thenReturn(Optional.of(booking));

        QueueEntry queueEntry = mock(QueueEntry.class);
        when(queueEntry.getQueueNumber()).thenReturn(2);
        when(queueEntry.getStatus()).thenReturn(QueueStatus.CANCELLED);
        when(queueService.findQueueEntryForBooking(booking)).thenReturn(queueEntry);
        when(cancelledBookingArchiveRepository.saveAndFlush(
                org.mockito.ArgumentMatchers.any(CancelledBookingArchive.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        ArgumentCaptor<CancelledBookingArchive> archivedBooking =
                ArgumentCaptor.forClass(CancelledBookingArchive.class);

        BookingResponse response = bookingService.cancelMyBooking(41L);

        assertEquals(41L, response.getId());
        assertEquals("BUS-REF-41", response.getBookingReference());
        assertEquals(BookingStatus.CANCELLED, response.getStatus());
        assertEquals(List.of("1A"), response.getSeatNumbers());
        assertEquals(2, response.getQueueNumber());

        InOrder cancellationOrder = inOrder(
                queueService,
                cancelledBookingArchiveRepository,
                bookingRepository);
        cancellationOrder.verify(queueService).cancelQueueEntryForBooking(booking);
        cancellationOrder.verify(queueService).findQueueEntryForBooking(booking);
        cancellationOrder.verify(cancelledBookingArchiveRepository).saveAndFlush(
                archivedBooking.capture());
        cancellationOrder.verify(queueService).detachBookingFromQueueEntry(booking);
        cancellationOrder.verify(bookingRepository).delete(booking);
        cancellationOrder.verify(bookingRepository).flush();

        when(bookingRepository.findByCommuterIdOrderByCreatedAtDesc(18L))
                .thenReturn(List.of());
        when(cancelledBookingArchiveRepository.findByCommuterIdOrderByCreatedAtDesc(18L))
                .thenReturn(List.of(archivedBooking.getValue()));
        List<BookingResponse> bookingHistory = bookingService.getMyBookings();

        assertEquals(1, bookingHistory.size());
        assertEquals(BookingStatus.CANCELLED, bookingHistory.get(0).getStatus());
        assertEquals(41L, bookingHistory.get(0).getId());
    }

    @Test
    void adminCanMarkAnActiveBookingPaid() {
        User admin = mock(User.class);
        when(admin.getRole()).thenReturn(Role.ADMIN);
        when(userRepository.findByFirebaseUid("firebase-uid"))
                .thenReturn(Optional.of(admin));

        Booking booking = mockBookingForPaymentStatus();
        when(bookingRepository.findById(17L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);
        prepareBookingResponse(booking);
        when(booking.getPaymentStatus()).thenReturn(PaymentStatus.UNPAID);

        BookingResponse response = bookingService.updateAdminPaymentStatus(
                17L,
                paymentStatusRequest(PaymentStatus.PAID));

        assertEquals(17L, response.getId());
        verify(booking).setPaymentStatus(PaymentStatus.PAID);
        verify(bookingRepository).save(booking);
    }

    @Test
    void employeeCanOnlyUpdatePaymentForAnAssignedTrip() {
        User employee = mock(User.class);
        when(employee.getId()).thenReturn(24L);
        when(employee.getRole()).thenReturn(Role.EMPLOYEE);
        when(userRepository.findByFirebaseUid("firebase-uid"))
                .thenReturn(Optional.of(employee));

        Booking booking = mockBookingForPaymentStatus();
        when(bookingRepository.findById(17L)).thenReturn(Optional.of(booking));
        when(booking.getTrip().getId()).thenReturn(9L);
        when(tripRepository.existsByIdAndEmployeeId(9L, 24L)).thenReturn(false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> bookingService.updateEmployeePaymentStatus(
                        17L,
                        paymentStatusRequest(PaymentStatus.PAID)));

        assertEquals(org.springframework.http.HttpStatus.FORBIDDEN, exception.getStatusCode());
        verify(booking, never()).setPaymentStatus(PaymentStatus.PAID);
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void employeeCanUpdatePaymentForAnAssignedTrip() {
        User employee = mock(User.class);
        when(employee.getId()).thenReturn(24L);
        when(employee.getRole()).thenReturn(Role.EMPLOYEE);
        when(userRepository.findByFirebaseUid("firebase-uid"))
                .thenReturn(Optional.of(employee));

        Booking booking = mockBookingForPaymentStatus();
        when(bookingRepository.findById(17L)).thenReturn(Optional.of(booking));
        when(tripRepository.existsByIdAndEmployeeId(9L, 24L)).thenReturn(true);
        when(bookingRepository.save(booking)).thenReturn(booking);
        prepareBookingResponse(booking);
        when(booking.getPaymentStatus()).thenReturn(PaymentStatus.UNPAID);

        BookingResponse response = bookingService.updateEmployeePaymentStatus(
                17L,
                paymentStatusRequest(PaymentStatus.PAID));

        assertEquals(17L, response.getId());
        verify(booking).setPaymentStatus(PaymentStatus.PAID);
        verify(bookingRepository).save(booking);
    }

    @Test
    void unpaidBookingCannotBeMarkedRefunded() {
        User admin = mock(User.class);
        when(admin.getRole()).thenReturn(Role.ADMIN);
        when(userRepository.findByFirebaseUid("firebase-uid"))
                .thenReturn(Optional.of(admin));

        Booking booking = mockBookingForPaymentStatus();
        when(bookingRepository.findById(17L)).thenReturn(Optional.of(booking));
        when(booking.getPaymentStatus()).thenReturn(PaymentStatus.UNPAID);

        assertThrows(
                ConflictException.class,
                () -> bookingService.updateAdminPaymentStatus(
                        17L,
                        paymentStatusRequest(PaymentStatus.REFUNDED)));

        verify(booking, never()).setPaymentStatus(PaymentStatus.REFUNDED);
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void adminCanMarkPaidArchivedBookingRefunded() {
        User admin = mock(User.class);
        when(admin.getRole()).thenReturn(Role.ADMIN);
        when(userRepository.findByFirebaseUid("firebase-uid"))
                .thenReturn(Optional.of(admin));

        CancelledBookingArchive archive = mock(CancelledBookingArchive.class);
        when(archive.getBookingId()).thenReturn(17L);
        when(archive.getPaymentStatus()).thenReturn(PaymentStatus.PAID);
        when(archive.getSeatNumbers()).thenReturn(List.of());
        when(bookingRepository.findById(17L)).thenReturn(Optional.empty());
        when(cancelledBookingArchiveRepository.findById(17L))
                .thenReturn(Optional.of(archive));
        when(cancelledBookingArchiveRepository.save(archive)).thenReturn(archive);

        BookingResponse response = bookingService.updateAdminPaymentStatus(
                17L,
                paymentStatusRequest(PaymentStatus.REFUNDED));

        assertEquals(17L, response.getId());
        assertEquals(BookingStatus.CANCELLED, response.getStatus());
        verify(archive).setPaymentStatus(PaymentStatus.REFUNDED);
        verify(cancelledBookingArchiveRepository).save(archive);
    }

    private Booking mockBookingForPaymentStatus() {
        Trip trip = mock(Trip.class);
        Booking booking = mock(Booking.class);

        when(booking.getTrip()).thenReturn(trip);
        return booking;
    }

    private void prepareBookingResponse(Booking booking) {
        Trip trip = booking.getTrip();
        when(booking.getId()).thenReturn(17L);
        when(booking.getBookingSeats()).thenReturn(List.of());
        when(trip.getId()).thenReturn(9L);
        when(trip.getRoute()).thenReturn(mock(Route.class));
        when(trip.getBus()).thenReturn(mock(Bus.class));
        when(queueService.findQueueEntryForBooking(booking)).thenReturn(null);
    }

    private UpdatePaymentStatusRequest paymentStatusRequest(PaymentStatus paymentStatus) {
        UpdatePaymentStatusRequest request = new UpdatePaymentStatusRequest();
        request.setPaymentStatus(paymentStatus);
        return request;
    }
}
