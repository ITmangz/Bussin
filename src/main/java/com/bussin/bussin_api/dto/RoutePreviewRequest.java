package com.bussin.bussin_api.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.*;

public class RoutePreviewRequest {
    @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") private BigDecimal originLatitude;
    @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") private BigDecimal originLongitude;
    @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") private BigDecimal destinationLatitude;
    @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") private BigDecimal destinationLongitude;
    public RoutePreviewRequest() {}
    public RoutePreviewRequest(BigDecimal a, BigDecimal b, BigDecimal c, BigDecimal d) { originLatitude=a; originLongitude=b; destinationLatitude=c; destinationLongitude=d; }
    public BigDecimal getOriginLatitude(){return originLatitude;} public void setOriginLatitude(BigDecimal v){originLatitude=v;}
    public BigDecimal getOriginLongitude(){return originLongitude;} public void setOriginLongitude(BigDecimal v){originLongitude=v;}
    public BigDecimal getDestinationLatitude(){return destinationLatitude;} public void setDestinationLatitude(BigDecimal v){destinationLatitude=v;}
    public BigDecimal getDestinationLongitude(){return destinationLongitude;} public void setDestinationLongitude(BigDecimal v){destinationLongitude=v;}
}