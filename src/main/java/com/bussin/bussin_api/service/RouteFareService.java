package com.bussin.bussin_api.service;

import java.math.*;
import java.util.Locale;
import org.springframework.stereotype.Service;
import com.bussin.bussin_api.dto.*;
import com.bussin.bussin_api.entity.Route;
import com.bussin.bussin_api.exception.ResourceNotFoundException;
import com.bussin.bussin_api.repository.RouteRepository;

@Service
public class RouteFareService {
    private static final BigDecimal MINIMUM_DISTANCE_KM=BigDecimal.valueOf(5);
    private static final BigDecimal STANDARD_DISCOUNT=BigDecimal.valueOf(20);
    private final RouteRepository routeRepository;
    private final RouteRoutingService routingService;

    public RouteFareService(RouteRepository routeRepository,RouteRoutingService routingService){
        this.routeRepository=routeRepository;this.routingService=routingService;
    }

    public FareQuoteResponse quote(Long routeId,FareQuoteRequest request){
        Route route=routeRepository.findById(routeId)
                .orElseThrow(()->new ResourceNotFoundException("Route not found: "+routeId));
        if(route.getOriginLatitude()==null || route.getOriginLongitude()==null)
            throw new IllegalStateException("This route does not have map coordinates configured yet.");
        RoutePreviewResponse preview=routingService.preview(new RoutePreviewRequest(
                route.getOriginLatitude(),route.getOriginLongitude(),request.getLatitude(),request.getLongitude()));
        BigDecimal distance=preview.distanceKm();
        BigDecimal extra=distance.subtract(MINIMUM_DISTANCE_KM).max(BigDecimal.ZERO);
        BigDecimal rate=route.getFarePerKm()==null?BigDecimal.ZERO:route.getFarePerKm();
        BigDecimal regular=route.getBaseFare().add(extra.multiply(rate)).setScale(2,RoundingMode.HALF_UP);
        String type=request.getPassengerType()==null?"REGULAR":request.getPassengerType().trim().toUpperCase(Locale.ROOT);
        BigDecimal discount=switch(type){case "STUDENT","SENIOR","PWD" -> STANDARD_DISCOUNT;default -> BigDecimal.ZERO;};
        BigDecimal amount=regular.multiply(discount).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP);
        return new FareQuoteResponse(distance,regular,discount,amount,regular.subtract(amount).setScale(2,RoundingMode.HALF_UP),type);
    }
}