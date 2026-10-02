package com.bussin.bussin_api.controller;

import java.time.LocalDateTime;
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

import com.bussin.bussin_api.dto.CreateTripRequest;
import com.bussin.bussin_api.dto.TripResponse;
import com.bussin.bussin_api.dto.UpdateTripRequest;
import com.bussin.bussin_api.entity.TripStatus;
import com.bussin.bussin_api.service.TripService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    // ADMIN, EMPLOYEE, COMMUTER
    @GetMapping
    public List<TripResponse> getAllTrips(
            @RequestParam(required = false) TripStatus status,
            @RequestParam(required = false) Long routeId,
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to) {

        return tripService.getAllTrips(status, routeId, from, to);
    }

    // ADMIN, EMPLOYEE, COMMUTER
    @GetMapping("/{tripId}")
    public TripResponse getTrip(@PathVariable Long tripId) {

        return tripService.getTrip(tripId);
    }

    // ADMIN, EMPLOYEE
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TripResponse createTrip(
            @Valid @RequestBody CreateTripRequest request) {

        return tripService.createTrip(request);
    }

    // ADMIN, EMPLOYEE
    @PutMapping("/{tripId}")
    public TripResponse updateTrip(
            @PathVariable Long tripId,
            @Valid @RequestBody UpdateTripRequest request) {

        return tripService.updateTrip(tripId, request);
    }

    // ADMIN only
    @DeleteMapping("/{tripId}")
    public ResponseEntity<Void> deleteTrip(@PathVariable Long tripId) {

        tripService.deleteTrip(tripId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public List<TripResponse> searchTrips(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam LocalDateTime from,
            @RequestParam LocalDateTime to) {

        return tripService.searchTrips(
                origin,
                destination,
                from,
                to);
    }
}