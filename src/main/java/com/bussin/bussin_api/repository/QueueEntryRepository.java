package com.bussin.bussin_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bussin.bussin_api.entity.QueueEntry;
import com.bussin.bussin_api.entity.QueueStatus;

public interface QueueEntryRepository
        extends JpaRepository<QueueEntry, Long> {

    @Query("SELECT queueEntry.trip.id FROM QueueEntry queueEntry WHERE queueEntry.id = :queueEntryId")
    Optional<Long> findTripIdByQueueEntryId(@Param("queueEntryId") Long queueEntryId);

    @EntityGraph(attributePaths = {"commuter", "booking", "trip"})
    List<QueueEntry> findByTripIdOrderByQueueNumberAsc(
            Long tripId);

    @EntityGraph(attributePaths = {"commuter", "booking", "trip", "trip.route", "trip.bus"})
    @Query("SELECT q FROM QueueEntry q WHERE q.trip.employee.id = :employeeId ORDER BY q.trip.scheduledDeparture ASC, q.queueNumber ASC")
    List<QueueEntry> findAssignedToEmployee(@Param("employeeId") Long employeeId);

    @EntityGraph(attributePaths = {"commuter", "booking", "trip"})
    @Query("SELECT q FROM QueueEntry q WHERE q.id = :queueEntryId")
    Optional<QueueEntry> findByIdWithDetails(@Param("queueEntryId") Long queueEntryId);

    List<QueueEntry> findByTripIdAndStatusOrderByQueueNumberAsc(
            Long tripId,
            QueueStatus status);

    @EntityGraph(attributePaths = {"commuter", "booking", "trip"})
    Optional<QueueEntry> findByTripIdAndCommuterId(
            Long tripId,
            Long commuterId);

    @EntityGraph(attributePaths = {"commuter", "booking", "trip"})
    Optional<QueueEntry> findByBookingId(Long bookingId);

    boolean existsByTripIdAndCommuterId(
            Long tripId,
            Long commuterId);

    @EntityGraph(attributePaths = {"commuter", "trip"})
    long countByStatus(QueueStatus status);

}
