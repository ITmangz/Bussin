package com.bussin.bussin_api.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name="routes")
public class Route {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true,length=100) private String routeIdentifier;
 @Column(nullable=false,length=100) private String origin;
 @Column(nullable=false,length=100) private String destination;
 @Column(precision=10,scale=2) private BigDecimal distanceKm;
 @Column private Integer durationMinutes;
 @Column(precision=10,scale=2) private BigDecimal baseFare;
 @Column(precision=10,scale=2) private BigDecimal farePerKm;
 @Column(precision=10,scale=7) private BigDecimal originLatitude;
 @Column(precision=10,scale=7) private BigDecimal originLongitude;
 @Column(precision=10,scale=7) private BigDecimal destinationLatitude;
 @Column(precision=10,scale=7) private BigDecimal destinationLongitude;
 @Column(columnDefinition="TEXT") private String routeGeometry;
 @Column(length=255) private String description;
 @Column(nullable=false) private boolean active;
 @Column(nullable=false) private LocalDateTime createdAt;
 @Column(nullable=false) private LocalDateTime updatedAt;
 public Route(){}
 public Long getId(){return id;}
 public String getRouteIdentifier(){return routeIdentifier;} public void setRouteIdentifier(String v){routeIdentifier=v;}
 public String getOrigin(){return origin;} public void setOrigin(String v){origin=v;}
 public String getDestination(){return destination;} public void setDestination(String v){destination=v;}
 public BigDecimal getDistanceKm(){return distanceKm;} public void setDistanceKm(BigDecimal v){distanceKm=v;}
 public Integer getDurationMinutes(){return durationMinutes;} public void setDurationMinutes(Integer v){durationMinutes=v;}
 public BigDecimal getBaseFare(){return baseFare;} public void setBaseFare(BigDecimal v){baseFare=v;}
 public BigDecimal getFarePerKm(){return farePerKm;} public void setFarePerKm(BigDecimal v){farePerKm=v;}
 public BigDecimal getOriginLatitude(){return originLatitude;} public void setOriginLatitude(BigDecimal v){originLatitude=v;}
 public BigDecimal getOriginLongitude(){return originLongitude;} public void setOriginLongitude(BigDecimal v){originLongitude=v;}
 public BigDecimal getDestinationLatitude(){return destinationLatitude;} public void setDestinationLatitude(BigDecimal v){destinationLatitude=v;}
 public BigDecimal getDestinationLongitude(){return destinationLongitude;} public void setDestinationLongitude(BigDecimal v){destinationLongitude=v;}
 public String getRouteGeometry(){return routeGeometry;} public void setRouteGeometry(String v){routeGeometry=v;}
 public String getDescription(){return description;} public void setDescription(String v){description=v;}
 public boolean isActive(){return active;} public void setActive(boolean v){active=v;}
 public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime v){createdAt=v;}
 public LocalDateTime getUpdatedAt(){return updatedAt;} public void setUpdatedAt(LocalDateTime v){updatedAt=v;}
}