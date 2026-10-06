package com.bussin.bussin_api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.bussin.bussin_api.dto.BookingResponse;
import com.bussin.bussin_api.dto.CreateBookingRequest;
import com.bussin.bussin_api.dto.UpdatePaymentStatusRequest;
import com.bussin.bussin_api.dto.UpdateBookingStatusRequest;
import com.bussin.bussin_api.service.BookingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/admin")
    public List<BookingResponse> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @GetMapping("/employee")
    public List<BookingResponse> getEmployeeBookings() {
        return bookingService.getEmployeeBookings();
    }

    @GetMapping("/admin/{bookingId}")
    public BookingResponse getBookingForStaff(@PathVariable Long bookingId) {
        return bookingService.getBookingForStaff(bookingId);
    }

    @org.springframework.web.bind.annotation.PatchMapping("/admin/{bookingId}/status")
    public BookingResponse updateBookingStatus(
            @PathVariable Long bookingId,
            @Valid @RequestBody UpdateBookingStatusRequest request) {
        return bookingService.updateBookingStatus(bookingId, request);
    }

    @org.springframework.web.bind.annotation.PatchMapping("/admin/{bookingId}/payment-status")
    public BookingResponse updateAdminPaymentStatus(
            @PathVariable Long bookingId,
            @Valid @RequestBody UpdatePaymentStatusRequest request) {
        return bookingService.updateAdminPaymentStatus(bookingId, request);
    }

    @org.springframework.web.bind.annotation.PatchMapping("/employee/{bookingId}/payment-status")
    public BookingResponse updateEmployeePaymentStatus(
            @PathVariable Long bookingId,
            @Valid @RequestBody UpdatePaymentStatusRequest request) {
        return bookingService.updateEmployeePaymentStatus(bookingId, request);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse createBooking(
            @Valid @RequestBody CreateBookingRequest request) {

        return bookingService.createBooking(request);
    }

    @PostMapping("/guest")
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse createGuestBooking(
            @Valid @RequestBody CreateBookingRequest request) {

        return bookingService.createGuestBooking(request);
    }

    @GetMapping("/trip/{tripId}/seats")
    public List<String> getAvailableSeats(
            @PathVariable Long tripId) {

        return bookingService.getAvailableSeats(tripId);
    }

    @GetMapping("/me")
    public List<BookingResponse> getMyBookings() {
        return bookingService.getMyBookings();
    }

    @GetMapping("/me/{bookingId}")
    public BookingResponse getMyBooking(
            @PathVariable Long bookingId) {

        return bookingService.getMyBooking(bookingId);
    }

    @DeleteMapping("/me/{bookingId}")
    public BookingResponse cancelMyBooking(
            @PathVariable Long bookingId) {

        return bookingService.cancelMyBooking(bookingId);
    }
}
