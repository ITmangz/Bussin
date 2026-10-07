package com.bussin.bussin_api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RouteResponse {
 private Long id; private String routeIdentifier,origin,destination; private BigDecimal distanceKm,baseFare,farePerKm,originLatitude,originLongitude,destinationLatitude,destinationLongitude;
 private Integer durationMinutes; private String routeGeometry,description; private boolean active; private LocalDateTime createdAt,updatedAt;
 public RouteResponse(Long id,String routeIdentifier,String origin,String destination,BigDecimal distanceKm,Integer durationMinutes,BigDecimal baseFare,BigDecimal farePerKm,BigDecimal originLatitude,BigDecimal originLongitude,BigDecimal destinationLatitude,BigDecimal destinationLongitude,String routeGeometry,String description,boolean active,LocalDateTime createdAt,LocalDateTime updatedAt){
  this.id=id;this.routeIdentifier=routeIdentifier;this.origin=origin;this.destination=destination;this.distanceKm=distanceKm;this.durationMinutes=durationMinutes;this.baseFare=baseFare;this.farePerKm=farePerKm;this.originLatitude=originLatitude;this.originLongitude=originLongitude;this.destinationLatitude=destinationLatitude;this.destinationLongitude=destinationLongitude;this.routeGeometry=routeGeometry;this.description=description;this.active=active;this.createdAt=createdAt;this.updatedAt=updatedAt;
 }
 public Long getId(){return id;} public String getRouteIdentifier(){return routeIdentifier;} public String getOrigin(){return origin;} public String getDestination(){return destination;} public BigDecimal getDistanceKm(){return distanceKm;} public Integer getDurationMinutes(){return durationMinutes;} public BigDecimal getBaseFare(){return baseFare;} public BigDecimal getFarePerKm(){return farePerKm;} public BigDecimal getOriginLatitude(){return originLatitude;} public BigDecimal getOriginLongitude(){return originLongitude;} public BigDecimal getDestinationLatitude(){return destinationLatitude;} public BigDecimal getDestinationLongitude(){return destinationLongitude;} public String getRouteGeometry(){return routeGeometry;} public String getDescription(){return description;} public boolean isActive(){return active;} public LocalDateTime getCreatedAt(){return createdAt;} public LocalDateTime getUpdatedAt(){return updatedAt;}
}