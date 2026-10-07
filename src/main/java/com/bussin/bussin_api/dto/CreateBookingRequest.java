package com.bussin.bussin_api.dto;

import java.math.BigDecimal;
import java.util.List;
import jakarta.validation.constraints.*;

public class CreateBookingRequest {
 @NotNull private Long tripId;
 @NotEmpty @Size(min=1,max=60) private List<@NotBlank String> seatNumbers;
 @NotBlank private String passengerName;
 @NotBlank private String passengerPhone;
 @NotBlank @Email private String passengerEmail;
 @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") private BigDecimal dropoffLatitude;
 @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") private BigDecimal dropoffLongitude;
 @NotBlank private String passengerType;
 public CreateBookingRequest(){}
 public Long getTripId(){return tripId;} public void setTripId(Long v){tripId=v;}
 public List<String> getSeatNumbers(){return seatNumbers;} public void setSeatNumbers(List<String> v){seatNumbers=v;}
 public String getPassengerName(){return passengerName;} public void setPassengerName(String v){passengerName=v;}
 public String getPassengerPhone(){return passengerPhone;} public void setPassengerPhone(String v){passengerPhone=v;}
 public String getPassengerEmail(){return passengerEmail;} public void setPassengerEmail(String v){passengerEmail=v;}
 public BigDecimal getDropoffLatitude(){return dropoffLatitude;} public void setDropoffLatitude(BigDecimal v){dropoffLatitude=v;}
 public BigDecimal getDropoffLongitude(){return dropoffLongitude;} public void setDropoffLongitude(BigDecimal v){dropoffLongitude=v;}
 public String getPassengerType(){return passengerType;} public void setPassengerType(String v){passengerType=v;}
}