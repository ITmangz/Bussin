package com.bussin.bussin_api.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name="bookings")
public class Booking {
 @OneToMany(mappedBy="booking",cascade=CascadeType.ALL,orphanRemoval=true) private java.util.List<BookingSeat> bookingSeats=new java.util.ArrayList<>();
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true,length=20) private String bookingReference;
 @ManyToOne(optional=true) @JoinColumn(name="commuter_id") private User commuter;
 @ManyToOne(optional=false) @JoinColumn(name="trip_id",nullable=false) private Trip trip;
 @Column(nullable=false,length=100) private String passengerName;
 @Column(nullable=false,length=30) private String passengerPhone;
 @Column(nullable=false,length=150) private String passengerEmail;
 @Column(name="seat_number",nullable=false,length=10) private String seatNumber;
 @Column(nullable=false,precision=10,scale=2) private BigDecimal fare;
 @Column(name="dropoff_latitude",precision=10,scale=7) private BigDecimal dropoffLatitude;
 @Column(name="dropoff_longitude",precision=10,scale=7) private BigDecimal dropoffLongitude;
 @Column(name="passenger_type",length=20) private String passengerType;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private BookingStatus status;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private PaymentStatus paymentStatus;
 @Column(nullable=false) private LocalDateTime createdAt;
 @Column(nullable=false) private LocalDateTime updatedAt;
 public Booking(){}
 public java.util.List<BookingSeat> getBookingSeats(){return bookingSeats;}
 public void addBookingSeat(BookingSeat b){bookingSeats.add(b);b.setBooking(this);}
 public Long getId(){return id;} public String getBookingReference(){return bookingReference;} public void setBookingReference(String v){bookingReference=v;}
 public User getCommuter(){return commuter;} public void setCommuter(User v){commuter=v;} public Trip getTrip(){return trip;} public void setTrip(Trip v){trip=v;}
 public String getPassengerName(){return passengerName;} public void setPassengerName(String v){passengerName=v;} public String getPassengerPhone(){return passengerPhone;} public void setPassengerPhone(String v){passengerPhone=v;}
 public String getPassengerEmail(){return passengerEmail;} public void setPassengerEmail(String v){passengerEmail=v;} public String getSeatNumber(){return seatNumber;} public void setSeatNumber(String v){seatNumber=v;}
 public BigDecimal getFare(){return fare;} public void setFare(BigDecimal v){fare=v;} public BigDecimal getDropoffLatitude(){return dropoffLatitude;} public void setDropoffLatitude(BigDecimal v){dropoffLatitude=v;}
 public BigDecimal getDropoffLongitude(){return dropoffLongitude;} public void setDropoffLongitude(BigDecimal v){dropoffLongitude=v;} public String getPassengerType(){return passengerType;} public void setPassengerType(String v){passengerType=v;}
 public BookingStatus getStatus(){return status;} public void setStatus(BookingStatus v){status=v;} public PaymentStatus getPaymentStatus(){return paymentStatus;} public void setPaymentStatus(PaymentStatus v){paymentStatus=v;}
 public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime v){createdAt=v;} public LocalDateTime getUpdatedAt(){return updatedAt;} public void setUpdatedAt(LocalDateTime v){updatedAt=v;}
}