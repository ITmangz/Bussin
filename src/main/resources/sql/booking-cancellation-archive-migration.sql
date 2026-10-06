-- BUSSIN cancelled booking history archive
--
-- Stop the API, then run once against the existing PostgreSQL database before
-- deploying a build that physically removes cancelled bookings from the
-- active bookings table.
-- The archive intentionally stores commuter/trip details as snapshots, with
-- no foreign keys to users or trips, so history survives later record changes.

CREATE TABLE IF NOT EXISTS cancelled_bookings (
    booking_id BIGINT PRIMARY KEY,
    booking_reference VARCHAR(20) NOT NULL,
    commuter_id BIGINT NOT NULL,
    commuter_name VARCHAR(600) NOT NULL,
    commuter_email VARCHAR(255),
    trip_id BIGINT NOT NULL,
    route_identifier VARCHAR(100),
    origin VARCHAR(100),
    destination VARCHAR(100),
    bus_id BIGINT,
    bus_plate_number VARCHAR(20),
    scheduled_departure TIMESTAMP WITHOUT TIME ZONE,
    scheduled_arrival TIMESTAMP WITHOUT TIME ZONE,
    passenger_name VARCHAR(100) NOT NULL,
    passenger_phone VARCHAR(30) NOT NULL,
    passenger_email VARCHAR(150) NOT NULL,
    seat_number VARCHAR(10) NOT NULL,
    fare NUMERIC(10, 2) NOT NULL,
    queue_number INTEGER,
    queue_status VARCHAR(20),
    payment_status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_cancelled_bookings_commuter_created
    ON cancelled_bookings (commuter_id, created_at);

CREATE TABLE IF NOT EXISTS cancelled_booking_seats (
    booking_id BIGINT NOT NULL
        REFERENCES cancelled_bookings (booking_id) ON DELETE CASCADE,
    seat_index INTEGER NOT NULL,
    seat_number VARCHAR(10) NOT NULL,
    PRIMARY KEY (booking_id, seat_index),
    CONSTRAINT uk_cancelled_booking_seat UNIQUE (booking_id, seat_number)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_cancelled_booking_seat
    ON cancelled_booking_seats (booking_id, seat_number);

-- Preserve existing cancelled bookings in the archive before removing them
-- from the active bookings table.
INSERT INTO cancelled_bookings (
    booking_id,
    booking_reference,
    commuter_id,
    commuter_name,
    commuter_email,
    trip_id,
    route_identifier,
    origin,
    destination,
    bus_id,
    bus_plate_number,
    scheduled_departure,
    scheduled_arrival,
    passenger_name,
    passenger_phone,
    passenger_email,
    seat_number,
    fare,
    queue_number,
    queue_status,
    payment_status,
    created_at,
    updated_at
)
SELECT
    booking.id,
    booking.booking_reference,
    booking.commuter_id,
    CONCAT_WS(
        ' ',
        NULLIF(BTRIM(commuter.first_name), ''),
        NULLIF(BTRIM(commuter.middle_name), ''),
        NULLIF(BTRIM(commuter.last_name), '')
    ),
    commuter.email,
    booking.trip_id,
    route.route_identifier,
    route.origin,
    route.destination,
    bus.id,
    bus.plate_number,
    trip.scheduled_departure,
    trip.scheduled_arrival,
    booking.passenger_name,
    booking.passenger_phone,
    booking.passenger_email,
    booking.seat_number,
    booking.fare,
    queue_entry.queue_number,
    queue_entry.status,
    booking.payment_status,
    booking.created_at,
    booking.updated_at
FROM bookings booking
JOIN users commuter ON commuter.id = booking.commuter_id
JOIN trips trip ON trip.id = booking.trip_id
LEFT JOIN routes route ON route.id = trip.route_id
LEFT JOIN buses bus ON bus.id = trip.bus_id
LEFT JOIN queue_entries queue_entry
    ON queue_entry.trip_id = booking.trip_id
   AND queue_entry.commuter_id = booking.commuter_id
WHERE booking.status = 'CANCELLED'
ON CONFLICT (booking_id) DO NOTHING;

-- Copy every historical seat and provide a fallback for bookings created
-- before the booking_seats table stored seat allocations.
INSERT INTO cancelled_booking_seats (booking_id, seat_index, seat_number)
SELECT
    booking.id,
    (ROW_NUMBER() OVER (
        PARTITION BY booking.id
        ORDER BY booking_seat.seat_number
    ) - 1)::INTEGER,
    booking_seat.seat_number
FROM bookings booking
JOIN booking_seats booking_seat ON booking_seat.booking_id = booking.id
WHERE booking.status = 'CANCELLED'
ON CONFLICT DO NOTHING;

INSERT INTO cancelled_booking_seats (booking_id, seat_index, seat_number)
SELECT booking.id, 0, booking.seat_number
FROM bookings booking
WHERE booking.status = 'CANCELLED'
  AND NOT EXISTS (
      SELECT 1
      FROM booking_seats booking_seat
      WHERE booking_seat.booking_id = booking.id
  )
ON CONFLICT DO NOTHING;

DELETE FROM booking_seats booking_seat
USING bookings booking
WHERE booking_seat.booking_id = booking.id
  AND booking.status = 'CANCELLED';

DELETE FROM bookings
WHERE status = 'CANCELLED';

-- Compact the current queue once, preserving canceled entries as history.
WITH active_queue AS (
    SELECT
        id,
        ROW_NUMBER() OVER (
            PARTITION BY trip_id
            ORDER BY queue_number, joined_at, id
        )::INTEGER AS compacted_number
    FROM queue_entries
    WHERE status <> 'CANCELLED'
)
UPDATE queue_entries queue_entry
SET queue_number = active_queue.compacted_number,
    position = active_queue.compacted_number
FROM active_queue
WHERE queue_entry.id = active_queue.id
  AND (
      queue_entry.queue_number IS DISTINCT FROM active_queue.compacted_number
      OR queue_entry.position IS DISTINCT FROM active_queue.compacted_number
  );
