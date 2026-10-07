package com.bussin.bussin_api.dto;

import java.math.BigDecimal;
public record FareQuoteResponse(BigDecimal distanceKm, BigDecimal regularFare, BigDecimal discountPercent, BigDecimal discountAmount, BigDecimal finalFare, String passengerType) {}