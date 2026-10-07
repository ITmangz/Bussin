-- Store the passenger's pinned drop-off and fare category with each booking.
ALTER TABLE bookings
    ADD COLUMN IF NOT EXISTS dropoff_latitude NUMERIC(10, 7),
    ADD COLUMN IF NOT EXISTS dropoff_longitude NUMERIC(10, 7),
    ADD COLUMN IF NOT EXISTS passenger_type VARCHAR(20);