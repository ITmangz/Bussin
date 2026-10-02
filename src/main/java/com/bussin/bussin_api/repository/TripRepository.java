package com.bussin.bussin_api.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bussin.bussin_api.entity.Bus;
import com.bussin.bussin_api.entity.Route;
import com.bussin.bussin_api.entity.Trip;
import com.bussin.bussin_api.entity.TripStatus;

public interface TripRepository extends JpaRepository<Trip, Long> {

    List<Trip> findByBusOrderByScheduledDepartureDesc(Bus bus);

    List<Trip> findByRouteOrderByScheduledDepartureDesc(Route route);

    List<Trip> findByStatusOrderByScheduledDepartureDesc(TripStatus status);

    boolean existsByBusAndIdNot(Bus bus, Long id);

    boolean existsByRouteAndIdNot(Route route, Long id);

    List<Trip> findByRouteIdAndStatusInOrderByScheduledDepartureDesc(
            Long routeId,
            List<TripStatus> statuses);

    List<Trip> findByScheduledDepartureBetweenOrderByScheduledDepartureDesc(
            LocalDateTime start,
            LocalDateTime end);

    List<Trip> findByScheduledDepartureAfterOrderByScheduledDepartureDesc(
            LocalDateTime after);
}