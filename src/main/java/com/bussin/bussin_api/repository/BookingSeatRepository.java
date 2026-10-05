package com.bussin.bussin_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bussin.bussin_api.entity.BookingSeat;
import com.bussin.bussin_api.entity.BookingStatus;

public interface BookingSeatRepository extends JpaRepository<BookingSeat, Long> {

    List<BookingSeat> findByBookingIdOrderBySeatNumberAsc(Long bookingId);

    @Query("""
            select case when count(bs) > 0 then true else false end
            from BookingSeat bs
            where bs.trip.id = :tripId
              and bs.seatNumber = :seatNumber
              and bs.booking.status in :statuses
            """)
    boolean existsActiveBookingSeat(
            @Param("tripId") Long tripId,
            @Param("seatNumber") String seatNumber,
            @Param("statuses") List<BookingStatus> statuses);
}
