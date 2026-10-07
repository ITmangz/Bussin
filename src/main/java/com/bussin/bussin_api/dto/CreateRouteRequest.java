package com.bussin.bussin_api.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.*;

public class CreateRouteRequest {
 @NotBlank @Size(max=100) private String routeIdentifier;
 @NotBlank @Size(max=100) private String origin;
 @NotBlank @Size(max=100) private String destination;
 @NotNull @DecimalMin("0.01") @Digits(integer=8,fraction=2) private BigDecimal distanceKm;
 @NotNull @Min(1) private Integer durationMinutes;
 @NotNull @DecimalMin("0.01") @Digits(integer=8,fraction=2) private BigDecimal baseFare;
 @NotNull @DecimalMin("0.00") @Digits(integer=8,fraction=2) private BigDecimal farePerKm;
 @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") private BigDecimal originLatitude;
 @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") private BigDecimal originLongitude;
 @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") private BigDecimal destinationLatitude;
 @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") private BigDecimal destinationLongitude;
 private String routeGeometry;
 @Size(max=255) private String description;
 public CreateRouteRequest(){}
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
}