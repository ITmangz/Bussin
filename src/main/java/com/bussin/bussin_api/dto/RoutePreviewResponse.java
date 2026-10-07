package com.bussin.bussin_api.dto;

import java.math.BigDecimal;
public record RoutePreviewResponse(BigDecimal distanceKm, Integer durationMinutes, String geometryJson) {}