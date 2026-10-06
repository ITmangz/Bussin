package com.bussin.bussin_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bussin.bussin_api.entity.QueueEntry;
import com.bussin.bussin_api.entity.QueueStatus;
import com.bussin.bussin_api.entity.Trip;
import com.bussin.bussin_api.entity.TripStatus;
import com.bussin.bussin_api.entity.User;
import com.bussin.bussin_api.repository.QueueEntryRepository;
import com.bussin.bussin_api.repository.TripRepository;
import com.bussin.bussin_api.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class QueueServiceTest {

    @Mock
    private QueueEntryRepository queueEntryRepository;

    @Mock
    private TripRepository tripRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private QueueService queueService;

    @Test
    void cancellationCompactsRemainingQueueNumbers() {
        Trip trip = mock(Trip.class);
        when(trip.getId()).thenReturn(9L);
        when(tripRepository.findByIdForUpdate(9L)).thenReturn(Optional.of(trip));

        User commuter = mock(User.class);
        when(commuter.getId()).thenReturn(18L);

        QueueEntry first = queueEntry(trip, commuter, 1, QueueStatus.WAITING);
        QueueEntry cancelled = queueEntry(trip, commuter, 2, QueueStatus.WAITING);
        QueueEntry third = queueEntry(trip, commuter, 3, QueueStatus.WAITING);

        when(queueEntryRepository.findByTripIdAndCommuterId(9L, 18L))
                .thenReturn(Optional.of(cancelled));
        when(queueEntryRepository.findByTripIdOrderByQueueNumberAsc(9L))
                .thenReturn(List.of(first, cancelled, third));

        queueService.cancelQueueEntryForBooking(trip, commuter);

        assertEquals(QueueStatus.CANCELLED, cancelled.getStatus());
        assertEquals(1, first.getQueueNumber());
        assertEquals(2, third.getQueueNumber());
        assertEquals(2, third.getPosition());
        assertNotNull(third.getUpdatedAt());
        verify(queueEntryRepository).saveAll(List.of(third));
    }

    @Test
    void rejoiningAfterCancellationUsesNextActiveQueueNumber() {
        Trip trip = mock(Trip.class);
        when(trip.getId()).thenReturn(9L);
        when(trip.getStatus()).thenReturn(TripStatus.SCHEDULED);
        when(tripRepository.findByIdForUpdate(9L)).thenReturn(Optional.of(trip));

        User commuter = mock(User.class);
        when(commuter.getId()).thenReturn(18L);

        QueueEntry first = queueEntry(trip, commuter, 1, QueueStatus.WAITING);
        QueueEntry cancelled = queueEntry(trip, commuter, 2, QueueStatus.CANCELLED);
        QueueEntry second = queueEntry(trip, mock(User.class), 2, QueueStatus.WAITING);

        when(queueEntryRepository.findByTripIdAndCommuterId(9L, 18L))
                .thenReturn(Optional.of(cancelled));
        when(queueEntryRepository.findByTripIdOrderByQueueNumberAsc(9L))
                .thenReturn(List.of(first, cancelled, second));
        when(queueEntryRepository.save(cancelled)).thenReturn(cancelled);

        QueueEntry rejoined = queueService.ensureQueueEntryForBooking(trip, commuter);

        assertEquals(3, rejoined.getQueueNumber());
        assertEquals(3, rejoined.getPosition());
        assertEquals(QueueStatus.WAITING, rejoined.getStatus());
    }

    private QueueEntry queueEntry(
            Trip trip,
            User commuter,
            int queueNumber,
            QueueStatus status) {
        QueueEntry entry = new QueueEntry();
        entry.setTrip(trip);
        entry.setCommuter(commuter);
        entry.setQueueNumber(queueNumber);
        entry.setPosition(queueNumber);
        entry.setStatus(status);
        return entry;
    }
}
