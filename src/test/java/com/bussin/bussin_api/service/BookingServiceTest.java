package com.bussin.bussin_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.inOrder;
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

import com.bussin.bussin_api.dto.CreateBookingRequest;
import com.bussin.bussin_api.dto.BookingResponse;
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
        when(firebaseToken.getUid()).thenReturn("firebase-uid");
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
        when(queueService.findQueueEntry(9L, 18L)).thenReturn(queueEntry);
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
        cancellationOrder.verify(queueService).cancelQueueEntryForBooking(trip, commuter);
        cancellationOrder.verify(queueService).findQueueEntry(9L, 18L);
        cancellationOrder.verify(cancelledBookingArchiveRepository).saveAndFlush(
                archivedBooking.capture());
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
}
