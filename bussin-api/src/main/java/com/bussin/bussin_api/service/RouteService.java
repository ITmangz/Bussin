package com.bussin.bussin_api.service;

import com.bussin.bussin_api.dto.CreateRouteRequest;
import com.bussin.bussin_api.dto.RouteResponse;
import com.bussin.bussin_api.dto.UpdateRouteRequest;
import com.bussin.bussin_api.entity.Route;
import com.bussin.bussin_api.exception.ConflictException;
import com.bussin.bussin_api.exception.ResourceNotFoundException;
import com.bussin.bussin_api.repository.RouteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RouteService {

    private final RouteRepository routeRepository;

    public RouteService(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    public List<RouteResponse> getAllRoutes(Boolean activeOnly) {
        List<Route> routes = activeOnly != null && activeOnly
                ? routeRepository.findByActiveTrue()
                : routeRepository.findAll();

        return routes.stream()
                .map(this::toResponse)
                .toList();
    }

    public RouteResponse getRoute(Long routeId) {
        return toResponse(findRoute(routeId));
    }

    @Transactional
    public RouteResponse createRoute(CreateRouteRequest request) {
        Route route = new Route();

        route.setRouteIdentifier(normalizeIdentifier(request.getRouteIdentifier()));
        route.setOrigin(request.getOrigin().trim());
        route.setDestination(request.getDestination().trim());
        route.setDistanceKm(request.getDistanceKm());
        route.setDurationMinutes(request.getDurationMinutes());
        route.setBaseFare(request.getBaseFare());
        route.setDescription(normalizeDescription(request.getDescription()));
        route.setActive(true);

        LocalDateTime now = LocalDateTime.now();
        route.setCreatedAt(now);
        route.setUpdatedAt(now);

        return toResponse(saveOrConflict(route));
    }

    @Transactional
    public RouteResponse updateRoute(Long routeId, UpdateRouteRequest request) {
        Route route = findRoute(routeId);

        route.setRouteIdentifier(normalizeIdentifier(request.getRouteIdentifier()));
        route.setOrigin(request.getOrigin().trim());
        route.setDestination(request.getDestination().trim());
        route.setDistanceKm(request.getDistanceKm());
        route.setDurationMinutes(request.getDurationMinutes());
        route.setBaseFare(request.getBaseFare());
        route.setDescription(normalizeDescription(request.getDescription()));
        route.setActive(request.isActive());
        route.setUpdatedAt(LocalDateTime.now());

        return toResponse(saveOrConflict(route));
    }

    @Transactional
    public void deleteRoute(Long routeId) {
        Route route = findRoute(routeId);
        routeRepository.delete(route);
    }

    private Route findRoute(Long routeId) {
        return routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found: " + routeId));
    }

    private Route saveOrConflict(Route route) {
        try {
            return routeRepository.save(route);
        } catch (org.springframework.dao.DataIntegrityViolationException exception) {
            throw new ConflictException(
                    "Route identifier already exists: " + route.getRouteIdentifier());
        }
    }

    private String normalizeIdentifier(String identifier) {
        return identifier.trim().toUpperCase();
    }

    private String normalizeDescription(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }

        return description.trim();
    }

    private RouteResponse toResponse(Route route) {
        return new RouteResponse(
                route.getId(),
                route.getRouteIdentifier(),
                route.getOrigin(),
                route.getDestination(),
                route.getDistanceKm(),
                route.getDurationMinutes(),
                route.getBaseFare(),
                route.getDescription(),
                route.isActive(),
                route.getCreatedAt(),
                route.getUpdatedAt());
    }
}