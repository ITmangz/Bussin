package com.bussin.bussin_api.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import com.bussin.bussin_api.entity.Bus;
import com.bussin.bussin_api.entity.Route;
import com.bussin.bussin_api.entity.Trip;
import com.bussin.bussin_api.entity.TripStatus;

public interface TripRepository extends JpaRepository<Trip, Long> {

        @Lock(LockModeType.PESSIMISTIC_WRITE)
        @Query("SELECT t FROM Trip t WHERE t.id = :id")
        java.util.Optional<Trip> findByIdForUpdate(@Param("id") Long id);

        List<Trip> findByBusOrderByScheduledDepartureDesc(Bus bus);

        List<Trip> findByRouteOrderByScheduledDepartureDesc(Route route);

        List<Trip> findByStatusOrderByScheduledDepartureDesc(TripStatus status);

        boolean existsByBusAndIdNot(Bus bus, Long id);

        boolean existsByRouteAndIdNot(Route route, Long id);

        List<Trip> findByRouteIdAndStatusInOrderByScheduledDepartureDesc(
                        Long routeId,
                        List<TripStatus> statuses);

        List<Trip> findByRouteIdOrderByScheduledDepartureDesc(
                        Long routeId);

        List<Trip> findByScheduledDepartureBetweenOrderByScheduledDepartureDesc(
                        LocalDateTime start,
                        LocalDateTime end);

        List<Trip> findByScheduledDepartureAfterOrderByScheduledDepartureDesc(
                        LocalDateTime after);

        List<Trip> findAllByOrderByScheduledDepartureDesc();\n\n        long countByScheduledDepartureBetween(LocalDateTime start, LocalDateTime end);

        List<Trip> findByRouteIdInAndScheduledDepartureBetweenOrderByScheduledDepartureAsc(
                        List<Long> routeIds,
                        LocalDateTime start,
                        LocalDateTime end);

        List<Trip> findByRouteIdInAndStatusInAndScheduledDepartureBetweenOrderByScheduledDepartureAsc(
                        List<Long> routeIds,
                        List<TripStatus> statuses,
                        LocalDateTime start,
                        LocalDateTime end);
}