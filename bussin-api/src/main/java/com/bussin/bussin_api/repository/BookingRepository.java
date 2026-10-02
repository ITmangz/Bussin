package com.bussin.bussin_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bussin.bussin_api.entity.Booking;
import com.bussin.bussin_api.entity.BookingStatus;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findByBookingReference(String bookingReference);

    List<Booking> findByCommuterIdOrderByCreatedAtDesc(Long commuterId);

    List<Booking> findByTripIdOrderByCreatedAtDesc(Long tripId);

    Optional<Booking> findByCommuterIdAndTripId(Long commuterId, Long tripId);

    Optional<Booking> findByIdAndCommuterId(Long bookingId, Long commuterId);

    boolean existsByTripIdAndSeatNumber(Long tripId, String seatNumber);

    boolean existsByCommuterIdAndTripIdAndStatusIn(
            Long commuterId,
            Long tripId,
            List<BookingStatus> statuses);

    boolean existsByTripIdAndSeatNumberAndStatusIn(
            Long tripId,
            String seatNumber,
            List<BookingStatus> statuses);
}