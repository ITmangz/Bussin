package com.bussin.bussin_api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.bussin.bussin_api.dto.CreateRouteRequest;
import com.bussin.bussin_api.dto.RouteResponse;
import com.bussin.bussin_api.dto.UpdateRouteRequest;
import com.bussin.bussin_api.service.RouteService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    // ADMIN, EMPLOYEE, COMMUTER
    @GetMapping
    public List<RouteResponse> getAllRoutes(
            @RequestParam(required = false) Boolean activeOnly) {

        return routeService.getAllRoutes(activeOnly);
    }

    // ADMIN, EMPLOYEE, COMMUTER
    @GetMapping("/{routeId}")
    public RouteResponse getRoute(@PathVariable Long routeId) {

        return routeService.getRoute(routeId);
    }

    // ADMIN, EMPLOYEE
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RouteResponse createRoute(
            @Valid @RequestBody CreateRouteRequest request) {

        return routeService.createRoute(request);
    }

    // ADMIN, EMPLOYEE
    @PutMapping("/{routeId}")
    public RouteResponse updateRoute(
            @PathVariable Long routeId,
            @Valid @RequestBody UpdateRouteRequest request) {

        return routeService.updateRoute(routeId, request);
    }

    // ADMIN only
    @DeleteMapping("/{routeId}")
    public ResponseEntity<Void> deleteRoute(@PathVariable Long routeId) {

        routeService.deleteRoute(routeId);

        return ResponseEntity.noContent().build();
    }
}