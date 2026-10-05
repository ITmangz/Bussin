package com.bussin.bussin_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bussin.bussin_api.entity.QueueEntry;
import com.bussin.bussin_api.entity.QueueStatus;

public interface QueueEntryRepository
        extends JpaRepository<QueueEntry, Long> {

    List<QueueEntry> findByTripIdOrderByQueueNumberAsc(
            Long tripId);

    List<QueueEntry> findByTripIdAndStatusOrderByQueueNumberAsc(
            Long tripId,
            QueueStatus status);

    Optional<QueueEntry> findByTripIdAndCommuterId(
            Long tripId,
            Long commuterId);

    boolean existsByTripIdAndCommuterId(
            Long tripId,
            Long commuterId);

    Optional<QueueEntry> findTopByTripIdOrderByQueueNumberDesc(
            Long tripId);
}