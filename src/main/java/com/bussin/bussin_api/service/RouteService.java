package com.bussin.bussin_api.service;

import com.bussin.bussin_api.dto.*;
import com.bussin.bussin_api.entity.Route;
import com.bussin.bussin_api.exception.*;
import com.bussin.bussin_api.repository.RouteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RouteService {
 private final RouteRepository routeRepository;
 public RouteService(RouteRepository routeRepository){this.routeRepository=routeRepository;}
 public List<RouteResponse> getAllRoutes(Boolean activeOnly){List<Route> routes=activeOnly!=null&&activeOnly?routeRepository.findByActiveTrue():routeRepository.findAll();return routes.stream().map(this::toResponse).toList();}
 public RouteResponse getRoute(Long id){return toResponse(findRoute(id));}
 @Transactional public RouteResponse createRoute(CreateRouteRequest r){Route route=new Route();apply(route,r);LocalDateTime now=LocalDateTime.now();route.setCreatedAt(now);route.setUpdatedAt(now);route.setActive(true);return toResponse(saveOrConflict(route));}
 @Transactional public RouteResponse updateRoute(Long id,UpdateRouteRequest r){Route route=findRoute(id);apply(route,r);route.setActive(r.isActive());route.setUpdatedAt(LocalDateTime.now());return toResponse(saveOrConflict(route));}
 @Transactional public void deleteRoute(Long id){routeRepository.delete(findRoute(id));}
 private void apply(Route route,CreateRouteRequest r){route.setRouteIdentifier(normalizeIdentifier(r.getRouteIdentifier()));route.setOrigin(r.getOrigin().trim());route.setDestination(r.getDestination().trim());route.setDistanceKm(r.getDistanceKm());route.setDurationMinutes(r.getDurationMinutes());route.setBaseFare(r.getBaseFare());route.setFarePerKm(r.getFarePerKm());route.setOriginLatitude(r.getOriginLatitude());route.setOriginLongitude(r.getOriginLongitude());route.setDestinationLatitude(r.getDestinationLatitude());route.setDestinationLongitude(r.getDestinationLongitude());route.setRouteGeometry(r.getRouteGeometry());route.setDescription(normalizeDescription(r.getDescription()));}
 private void apply(Route route,UpdateRouteRequest r){route.setRouteIdentifier(normalizeIdentifier(r.getRouteIdentifier()));route.setOrigin(r.getOrigin().trim());route.setDestination(r.getDestination().trim());route.setDistanceKm(r.getDistanceKm());route.setDurationMinutes(r.getDurationMinutes());route.setBaseFare(r.getBaseFare());route.setFarePerKm(r.getFarePerKm());route.setOriginLatitude(r.getOriginLatitude());route.setOriginLongitude(r.getOriginLongitude());route.setDestinationLatitude(r.getDestinationLatitude());route.setDestinationLongitude(r.getDestinationLongitude());route.setRouteGeometry(r.getRouteGeometry());route.setDescription(normalizeDescription(r.getDescription()));}
 private Route findRoute(Long id){return routeRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Route not found: "+id));}
 private Route saveOrConflict(Route r){try{return routeRepository.save(r);}catch(org.springframework.dao.DataIntegrityViolationException e){throw new ConflictException("Route identifier already exists: "+r.getRouteIdentifier());}}
 private String normalizeIdentifier(String s){return s.trim().toUpperCase();} private String normalizeDescription(String s){return s==null||s.isBlank()?null:s.trim();}
 private RouteResponse toResponse(Route r){return new RouteResponse(r.getId(),r.getRouteIdentifier(),r.getOrigin(),r.getDestination(),r.getDistanceKm(),r.getDurationMinutes(),r.getBaseFare(),r.getFarePerKm(),r.getOriginLatitude(),r.getOriginLongitude(),r.getDestinationLatitude(),r.getDestinationLongitude(),r.getRouteGeometry(),r.getDescription(),r.isActive(),r.getCreatedAt(),r.getUpdatedAt());}
}