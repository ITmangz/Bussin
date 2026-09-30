package com.bussin.bussin_api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bussin.bussin_api.dto.HealthResponse;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping
    public HealthResponse healthCheck() {
        return new HealthResponse(
            "ok",
            "BUSSIN API"
        );
    }
}