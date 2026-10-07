package com.bussin.bussin_api.controller;

import java.util.List;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import com.bussin.bussin_api.dto.*;
import com.bussin.bussin_api.service.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/routes")
public class RouteController {
 private final RouteService routeService; private final RouteRoutingService routingService; private final RouteFareService fareService;
 public RouteController(RouteService routeService,RouteRoutingService routingService,RouteFareService fareService){this.routeService=routeService;this.routingService=routingService;this.fareService=fareService;}
 @GetMapping public List<RouteResponse> getAllRoutes(@RequestParam(required=false) Boolean activeOnly){return routeService.getAllRoutes(activeOnly);}
 @GetMapping("/{routeId}") public RouteResponse getRoute(@PathVariable Long routeId){return routeService.getRoute(routeId);}
 @PostMapping("/preview") public RoutePreviewResponse preview(@Valid @RequestBody RoutePreviewRequest request){return routingService.preview(request);}
 @PostMapping("/{routeId}/fare-quote") public FareQuoteResponse fareQuote(@PathVariable Long routeId,@Valid @RequestBody FareQuoteRequest request){return fareService.quote(routeId,request);}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public RouteResponse createRoute(@Valid @RequestBody CreateRouteRequest request){return routeService.createRoute(request);}
 @PutMapping("/{routeId}") public RouteResponse updateRoute(@PathVariable Long routeId,@Valid @RequestBody UpdateRouteRequest request){return routeService.updateRoute(routeId,request);}
 @DeleteMapping("/{routeId}") public ResponseEntity<Void> deleteRoute(@PathVariable Long routeId){routeService.deleteRoute(routeId);return ResponseEntity.noContent().build();}
}