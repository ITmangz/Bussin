package com.bussin.bussin_api.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bussin.bussin_api.dto.CreateTripRequest;
import com.bussin.bussin_api.dto.TripResponse;
import com.bussin.bussin_api.dto.UpdateTripRequest;
import com.bussin.bussin_api.entity.Bus;
import com.bussin.bussin_api.entity.Route;
import com.bussin.bussin_api.entity.Role;
import com.bussin.bussin_api.entity.Trip;
import com.bussin.bussin_api.entity.TripStatus;
import com.bussin.bussin_api.entity.User;
import com.bussin.bussin_api.exception.ConflictException;
import com.bussin.bussin_api.exception.ResourceNotFoundException;
import com.bussin.bussin_api.repository.BusRepository;
import com.bussin.bussin_api.repository.RouteRepository;
import com.bussin.bussin_api.repository.TripRepository;
import com.bussin.bussin_api.repository.UserRepository;
import com.google.firebase.auth.FirebaseToken;

@Service
public class TripService {

    private final TripRepository tripRepository;
    private final BusRepository busRepository;
    private final RouteRepository routeRepository;
    private final UserRepository userRepository;

    public TripService(
            TripRepository tripRepository,
            BusRepository busRepository,
            RouteRepository routeRepository,
            UserRepository userRepository) {

        this.tripRepository = tripRepository;
        this.busRepository = busRepository;
        this.routeRepository = routeRepository;
        this.userRepository = userRepository;
    }

    // ============================================================
    // GET ALL TRIPS (optionally filtered)
    // ============================================================

    public List<TripResponse> getAllTrips(
            TripStatus status,
            Long routeId,
            LocalDateTime from,
            LocalDateTime to) {

        User actor = getAuthenticatedUser();
        List<Trip> trips;

        if (actor.getRole() == Role.EMPLOYEE) {
            trips = filterEmployeeTrips(
                    tripRepository.findAssignedToEmployee(actor.getId()),
                    status,
                    routeId,
                    from,
                    to);
        } else if (status != null && routeId != null) {
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
                .map(trip -> toResponse(trip, actor))
                .toList();
    }

    // ============================================================
    // GET TRIP BY ID
    // ============================================================

    public TripResponse getTrip(Long tripId) {

        User actor = getAuthenticatedUser();
        return toResponse(requireVisibleTrip(findTrip(tripId), actor), actor);
    }

    // ============================================================
    // CREATE TRIP
    // ============================================================

    @Transactional
    public TripResponse createTrip(CreateTripRequest request) {

        Bus bus = findBus(request.getBusId());
        Route route = findRoute(request.getRouteId());
        User actor = getAuthenticatedUser();
        User employee = resolveEmployee(request.getEmployeeId(), actor);

        validateSchedule(request);

        Trip trip = new Trip();

        trip.setBus(bus);
        trip.setRoute(route);
        trip.setEmployee(employee);
        trip.setScheduledDeparture(request.getScheduledDeparture());
        trip.setScheduledArrival(request.getScheduledArrival());
        trip.setStatus(
                request.getStatus() != null
                        ? request.getStatus()
                        : TripStatus.SCHEDULED);

        LocalDateTime now = LocalDateTime.now();

        trip.setCreatedAt(now);
        trip.setUpdatedAt(now);

        return toResponse(saveOrConflict(trip), actor);
    }

    // ============================================================
    // UPDATE TRIP
    // ============================================================

    @Transactional
    public TripResponse updateTrip(
            Long tripId,
            UpdateTripRequest request) {

        Trip trip = findTrip(tripId);
        User actor = getAuthenticatedUser();
        requireEmployeeOwnsTrip(actor, trip);

        Bus bus = findBus(request.getBusId());
        Route route = findRoute(request.getRouteId());
        User employee = resolveEmployee(request.getEmployeeId(), actor);

        validateSchedule(request);

        trip.setBus(bus);
        trip.setRoute(route);
        trip.setEmployee(employee);
        trip.setScheduledDeparture(request.getScheduledDeparture());
        trip.setScheduledArrival(request.getScheduledArrival());
        trip.setStatus(request.getStatus());
        trip.setUpdatedAt(LocalDateTime.now());

        return toResponse(saveOrConflict(trip), actor);
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

    private TripResponse toResponse(Trip trip, User actor) {
        boolean canViewAssignments = actor.getRole() == Role.ADMIN
                || actor.getRole() == Role.EMPLOYEE;
        User employee = canViewAssignments ? trip.getEmployee() : null;

        return new TripResponse(
                trip.getId(),
                trip.getBus().getId(),
                trip.getBus().getPlateNumber(),
                trip.getBus().getCapacity(),
                trip.getRoute().getId(),
                trip.getRoute().getRouteIdentifier(),
                employee == null ? null : employee.getId(),
                employee == null ? null : buildEmployeeName(employee),
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

        User actor = getAuthenticatedUser();
        return visibleTrips(trips, actor).stream()
                .map(trip -> toResponse(trip, actor))
                .toList();
    }

    private List<Trip> visibleTrips(List<Trip> trips, User actor) {
        if (actor.getRole() != Role.EMPLOYEE) {
            return trips;
        }
        return trips.stream()
                .filter(trip -> trip.getEmployee() != null
                        && trip.getEmployee().getId().equals(actor.getId()))
                .toList();
    }

    private List<Trip> filterEmployeeTrips(
            List<Trip> trips,
            TripStatus status,
            Long routeId,
            LocalDateTime from,
            LocalDateTime to) {
        if (status != null && routeId != null) {
            return trips.stream()
                    .filter(trip -> trip.getStatus() == status
                            && trip.getRoute().getId().equals(routeId))
                    .toList();
        }
        if (status != null) {
            return trips.stream().filter(trip -> trip.getStatus() == status).toList();
        }
        if (routeId != null) {
            return trips.stream().filter(trip -> trip.getRoute().getId().equals(routeId)).toList();
        }
        if (from != null && to != null) {
            return trips.stream()
                    .filter(trip -> !trip.getScheduledDeparture().isBefore(from)
                            && !trip.getScheduledDeparture().isAfter(to))
                    .toList();
        }
        if (from != null) {
            return trips.stream().filter(trip -> trip.getScheduledDeparture().isAfter(from)).toList();
        }
        return trips;
    }

    private Trip requireVisibleTrip(Trip trip, User actor) {
        if (actor.getRole() == Role.EMPLOYEE
                && (trip.getEmployee() == null
                        || !trip.getEmployee().getId().equals(actor.getId()))) {
            throw new ResourceNotFoundException("Trip not found");
        }
        return trip;
    }

    private void requireEmployeeOwnsTrip(User actor, Trip trip) {
        if (actor.getRole() == Role.EMPLOYEE) {
            requireVisibleTrip(trip, actor);
        }
    }

    private User resolveEmployee(Long employeeId, User actor) {
        if (actor.getRole() == Role.EMPLOYEE) {
            if (employeeId != null && !employeeId.equals(actor.getId())) {
                throw new ConflictException("Employees can only assign trips to themselves.");
            }
            return actor;
        }

        if (employeeId == null) {
            return null;
        }

        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        if (employee.getRole() != Role.EMPLOYEE) {
            throw new ConflictException("Trips can only be assigned to an employee account.");
        }
        return employee;
    }

    private String buildEmployeeName(User employee) {
        return java.util.stream.Stream.of(
                        employee.getFirstName(),
                        employee.getMiddleName(),
                        employee.getLastName())
                .filter(value -> value != null && !value.isBlank())
                .collect(java.util.stream.Collectors.joining(" "));
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof FirebaseToken firebaseToken)) {
            throw new ConflictException("Authenticated Firebase user is required.");
        }
        return userRepository.findByFirebaseUid(firebaseToken.getUid())
                .orElseThrow(() -> new ResourceNotFoundException("BUSSIN user profile not found."));
    }
}
