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

import com.bussin.bussin_api.dto.BusResponse;
import com.bussin.bussin_api.dto.CreateBusRequest;
import com.bussin.bussin_api.dto.UpdateBusRequest;
import com.bussin.bussin_api.entity.BusStatus;
import com.bussin.bussin_api.service.BusService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/buses")
public class BusController {

    private final BusService busService;

    public BusController(BusService busService) {
        this.busService = busService;
    }

    // ADMIN, EMPLOYEE
    @GetMapping
    public List<BusResponse> getAllBuses(
            @RequestParam(required = false) BusStatus status) {

        return busService.getAllBuses(status);
    }

    // ADMIN, EMPLOYEE
    @GetMapping("/{busId}")
    public BusResponse getBus(@PathVariable Long busId) {

        return busService.getBus(busId);
    }

    // ADMIN, EMPLOYEE
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BusResponse createBus(
            @Valid @RequestBody CreateBusRequest request) {

        return busService.createBus(request);
    }

    // ADMIN, EMPLOYEE
    @PutMapping("/{busId}")
    public BusResponse updateBus(
            @PathVariable Long busId,
            @Valid @RequestBody UpdateBusRequest request) {

        return busService.updateBus(busId, request);
    }

    // ADMIN only
    @DeleteMapping("/{busId}")
    public ResponseEntity<Void> deleteBus(@PathVariable Long busId) {

        busService.deleteBus(busId);

        return ResponseEntity.noContent().build();
    }
}
