package com.bussin.bussin_api.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.bussin.bussin_api.dto.CreateRouteRequest;
import com.bussin.bussin_api.dto.RouteResponse;
import com.bussin.bussin_api.dto.UpdateRouteRequest;
import com.bussin.bussin_api.entity.Route;
import com.bussin.bussin_api.exception.ConflictException;
import com.bussin.bussin_api.exception.ResourceNotFoundException;
import com.bussin.bussin_api.repository.RouteRepository;

@Service
public class RouteService {

    private final RouteRepository routeRepository;

    public RouteService(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    public List<RouteResponse> getAllRoutes(Boolean activeOnly) {

        List<Route> routes;

        if (Boolean.TRUE.equals(activeOnly)) {
            routes = routeRepository.findByActiveTrueOrderByRouteIdentifierAsc();
        } else {
            routes = routeRepository.findAllByOrderByRouteIdentifierAsc();
        }

        return routes.stream()
                .map(this::toResponse)
                .toList();
    }

    public RouteResponse getRoute(Long routeId) {

        return toResponse(findRoute(routeId));
    }

    public RouteResponse createRoute(CreateRouteRequest request) {

        String routeIdentifier = normalizeIdentifier(request.getRouteIdentifier());

        if (routeRepository.existsByRouteIdentifier(routeIdentifier)) {
            throw new ConflictException(
                    "A route with this identifier already exists");
        }

        Route route = new Route();

        route.setRouteIdentifier(routeIdentifier);
        route.setOrigin(request.getOrigin().trim());
        route.setDestination(request.getDestination().trim());
        route.setDescription(
                request.getDescription() != null
                        ? request.getDescription().trim()
                        : null);
        route.setActive(true);

        LocalDateTime now = LocalDateTime.now();

        route.setCreatedAt(now);
        route.setUpdatedAt(now);

        return toResponse(saveOrConflict(route));
    }

    public RouteResponse updateRoute(
            Long routeId,
            UpdateRouteRequest request) {

        Route route = findRoute(routeId);

        String routeIdentifier = normalizeIdentifier(request.getRouteIdentifier());

        if (routeRepository.existsByRouteIdentifierAndIdNot(
                routeIdentifier, routeId)) {

            throw new ConflictException(
                    "A route with this identifier already exists");
        }

        route.setRouteIdentifier(routeIdentifier);
        route.setOrigin(request.getOrigin().trim());
        route.setDestination(request.getDestination().trim());
        route.setDescription(
                request.getDescription() != null
                        ? request.getDescription().trim()
                        : null);

        route.setActive(
                request.isActive());

        route.setUpdatedAt(
                LocalDateTime.now());

        return toResponse(saveOrConflict(route));
    }

    public void deleteRoute(Long routeId) {

        Route route = findRoute(routeId);

        try {
            routeRepository.delete(route);
            routeRepository.flush();
        } catch (DataIntegrityViolationException exception) {
            throw new ConflictException(
                    "Route cannot be deleted because it is referenced by other records");
        }
    }

    private Route findRoute(Long routeId) {

        return routeRepository
                .findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Route not found"));
    }

    private Route saveOrConflict(Route route) {

        try {
            return routeRepository.saveAndFlush(route);
        } catch (DataIntegrityViolationException exception) {
            throw new ConflictException(
                    "A route with this identifier already exists");
        }
    }

    private String normalizeIdentifier(String identifier) {

        return identifier.trim().replaceAll("\\s+", " ")
                .toUpperCase();
    }

    private RouteResponse toResponse(Route route) {

        return new RouteResponse(
                route.getId(),
                route.getRouteIdentifier(),
                route.getOrigin(),
                route.getDestination(),
                route.getDescription(),
                route.isActive(),
                route.getCreatedAt(),
                route.getUpdatedAt());
    }
}