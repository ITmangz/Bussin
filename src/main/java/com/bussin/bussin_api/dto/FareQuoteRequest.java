package com.bussin.bussin_api.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.*;

public class FareQuoteRequest {
    @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") private BigDecimal latitude;
    @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") private BigDecimal longitude;
    private String passengerType;
    public FareQuoteRequest(){}
    public BigDecimal getLatitude(){return latitude;} public void setLatitude(BigDecimal v){latitude=v;}
    public BigDecimal getLongitude(){return longitude;} public void setLongitude(BigDecimal v){longitude=v;}
    public String getPassengerType(){return passengerType;} public void setPassengerType(String v){passengerType=v;}
}