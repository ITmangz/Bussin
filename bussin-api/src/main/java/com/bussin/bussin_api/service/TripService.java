package com.bussin.bussin_api.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bussin.bussin_api.dto.CreateTripRequest;
import com.bussin.bussin_api.dto.TripResponse;
import com.bussin.bussin_api.dto.UpdateTripRequest;
import com.bussin.bussin_api.entity.Bus;
import com.bussin.bussin_api.entity.Route;
import com.bussin.bussin_api.entity.Trip;
import com.bussin.bussin_api.entity.TripStatus;
import com.bussin.bussin_api.exception.ConflictException;
import com.bussin.bussin_api.exception.ResourceNotFoundException;
import com.bussin.bussin_api.repository.BusRepository;
import com.bussin.bussin_api.repository.RouteRepository;
import com.bussin.bussin_api.repository.TripRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TripService {

    private final TripRepository tripRepository;
    private final BusRepository busRepository;
    private final RouteRepository routeRepository;

    public TripService(
            TripRepository tripRepository,
            BusRepository busRepository,
            RouteRepository routeRepository) {

        this.tripRepository = tripRepository;
        this.busRepository = busRepository;
        this.routeRepository = routeRepository;
    }

    // ============================================================
    // GET ALL TRIPS (optionally filtered)
    // ============================================================

    public List<TripResponse> getAllTrips(
            TripStatus status,
            Long routeId,
            LocalDateTime from,
            LocalDateTime to) {

        List<Trip> trips;

        if (status != null && routeId != null) {
            trips = tripRepository
                    .findByRouteIdAndStatusInOrderByScheduledDepartureDesc(
                            routeId, List.of(status));
        } else if (status != null) {
            trips = tripRepository
                    .findByStatusOrderByScheduledDepartureDesc(status);
        } else if (routeId != null) {
            trips = tripRepository
                    .findByRouteIdOrderByScheduledDepartureDesc(
                            routeId);
        } else if (from != null && to != null) {
            trips = tripRepository
                    .findByScheduledDepartureBetweenOrderByScheduledDepartureDesc(
                            from, to);
        } else if (from != null) {
            trips = tripRepository
                    .findByScheduledDepartureAfterOrderByScheduledDepartureDesc(
                            from);
        } else {
            trips = tripRepository
                    .findAllByOrderByScheduledDepartureDesc();
        }

        return trips.stream()
                .map(this::toResponse)
                .toList();
    }

    // ============================================================
    // GET TRIP BY ID
    // ============================================================

    public TripResponse getTrip(Long tripId) {

        return toResponse(findTrip(tripId));
    }

    // ============================================================
    // CREATE TRIP
    // ============================================================

    @Transactional
    public TripResponse createTrip(CreateTripRequest request) {

        Bus bus = findBus(request.getBusId());
        Route route = findRoute(request.getRouteId());

        validateSchedule(request);

        Trip trip = new Trip();

        trip.setBus(bus);
        trip.setRoute(route);
        trip.setScheduledDeparture(request.getScheduledDeparture());
        trip.setScheduledArrival(request.getScheduledArrival());
        trip.setStatus(
                request.getStatus() != null
                        ? request.getStatus()
                        : TripStatus.SCHEDULED);

        LocalDateTime now = LocalDateTime.now();

        trip.setCreatedAt(now);
        trip.setUpdatedAt(now);

        return toResponse(saveOrConflict(trip));
    }

    // ============================================================
    // UPDATE TRIP
    // ============================================================

    @Transactional
    public TripResponse updateTrip(
            Long tripId,
            UpdateTripRequest request) {

        Trip trip = findTrip(tripId);

        Bus bus = findBus(request.getBusId());
        Route route = findRoute(request.getRouteId());

        validateSchedule(request);

        trip.setBus(bus);
        trip.setRoute(route);
        trip.setScheduledDeparture(request.getScheduledDeparture());
        trip.setScheduledArrival(request.getScheduledArrival());
        trip.setStatus(request.getStatus());
        trip.setUpdatedAt(LocalDateTime.now());

        return toResponse(saveOrConflict(trip));
    }

    // ============================================================
    // DELETE TRIP
    // ============================================================

    @Transactional
    public void deleteTrip(Long tripId) {

        Trip trip = findTrip(tripId);

        try {
            tripRepository.delete(trip);
            tripRepository.flush();
        } catch (DataIntegrityViolationException exception) {
            throw new ConflictException(
                    "Trip cannot be deleted because it is referenced by other records");
        }
    }

    // ============================================================
    // HELPERS
    // ============================================================

    private Trip findTrip(Long tripId) {

        return tripRepository
                .findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Trip not found"));
    }

    private Bus findBus(Long busId) {

        return busRepository
                .findById(busId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Bus not found"));
    }

    private Route findRoute(Long routeId) {

        return routeRepository
                .findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Route not found"));
    }

    private void validateSchedule(CreateTripRequest request) {

        if (request.getScheduledArrival()
                .isBefore(request.getScheduledDeparture())) {

            throw new IllegalArgumentException(
                    "Scheduled arrival must not be before scheduled departure");
        }
    }

    private void validateSchedule(UpdateTripRequest request) {

        if (request.getScheduledArrival()
                .isBefore(request.getScheduledDeparture())) {

            throw new IllegalArgumentException(
                    "Scheduled arrival must not be before scheduled departure");
        }
    }

    private Trip saveOrConflict(Trip trip) {

        try {
            return tripRepository.saveAndFlush(trip);
        } catch (DataIntegrityViolationException exception) {
            throw new ConflictException(
                    "Trip could not be saved due to a data integrity violation");
        }
    }

    private TripResponse toResponse(Trip trip) {

        return new TripResponse(
                trip.getId(),
                trip.getBus().getId(),
                trip.getBus().getPlateNumber(),
                trip.getBus().getCapacity(),
                trip.getRoute().getId(),
                trip.getRoute().getRouteIdentifier(),
                trip.getScheduledDeparture(),
                trip.getScheduledArrival(),
                trip.getStatus(),
                trip.getCreatedAt(),
                trip.getUpdatedAt());

    }

    public List<TripResponse> searchTrips(
            String origin,
            String destination,
            LocalDateTime from,
            LocalDateTime to) {

        List<Route> routes = routeRepository
                .findByOriginIgnoreCaseAndDestinationIgnoreCaseAndActiveTrue(
                        origin,
                        destination);

        if (routes.isEmpty()) {
            return List.of();
        }

        List<Long> routeIds = routes.stream()
                .map(Route::getId)
                .toList();

        List<Trip> trips = tripRepository
                .findByRouteIdInAndStatusInAndScheduledDepartureBetweenOrderByScheduledDepartureAsc(
                        routeIds,
                        List.of(
                                TripStatus.SCHEDULED,
                                TripStatus.BOARDING),
                        from,
                        to);

        return trips.stream()
                .map(this::toResponse)
                .toList();
    }
}