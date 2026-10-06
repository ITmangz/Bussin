package com.bussin.bussin_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.bussin.bussin_api.dto.CreateBookingRequest;
import com.bussin.bussin_api.entity.Booking;
import com.bussin.bussin_api.entity.BookingStatus;
import com.bussin.bussin_api.entity.Bus;
import com.bussin.bussin_api.entity.BusStatus;
import com.bussin.bussin_api.entity.Role;
import com.bussin.bussin_api.entity.Route;
import com.bussin.bussin_api.entity.Trip;
import com.bussin.bussin_api.entity.TripStatus;
import com.bussin.bussin_api.entity.User;
import com.bussin.bussin_api.exception.ConflictException;
import com.bussin.bussin_api.repository.BookingRepository;
import com.bussin.bussin_api.repository.BookingSeatRepository;
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
                        "duplicate key violates uk_booking_commuter_trip"));

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
}
