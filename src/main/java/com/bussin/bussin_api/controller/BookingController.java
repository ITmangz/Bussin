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
import com.bussin.bussin_api.service.BookingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse createBooking(
            @Valid @RequestBody CreateBookingRequest request) {

        return bookingService.createBooking(request);
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