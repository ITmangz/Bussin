-- BUSSIN multi-seat booking migration
--
-- Run this once against the existing Supabase PostgreSQL database before
-- creating a new multi-seat booking. The application now protects active
-- seat allocation transactionally by locking the trip row.
--
-- Existing cancelled/completed bookings must not permanently reserve a seat.

ALTER TABLE bookings
    DROP CONSTRAINT IF EXISTS uk_booking_trip_seat;

ALTER TABLE bookings
    DROP CONSTRAINT IF EXISTS uk_booking_commuter_trip;
