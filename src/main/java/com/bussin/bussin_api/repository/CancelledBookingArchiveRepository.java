package com.bussin.bussin_api.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bussin.bussin_api.entity.CancelledBookingArchive;

public interface CancelledBookingArchiveRepository
        extends JpaRepository<CancelledBookingArchive, Long> {

    List<CancelledBookingArchive> findByCommuterIdOrderByCreatedAtDesc(Long commuterId);

    List<CancelledBookingArchive> findByTripIdInOrderByCreatedAtDesc(List<Long> tripIds);

    Optional<CancelledBookingArchive> findByBookingIdAndCommuterId(Long bookingId, Long commuterId);

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
