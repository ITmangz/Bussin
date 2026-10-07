-- BUSSIN realtime map and distance-based fare support.
-- Run once against the BUSSIN PostgreSQL/Supabase database.

ALTER TABLE routes
    ADD COLUMN IF NOT EXISTS origin_latitude NUMERIC(10, 7),
    ADD COLUMN IF NOT EXISTS origin_longitude NUMERIC(10, 7),
    ADD COLUMN IF NOT EXISTS destination_latitude NUMERIC(10, 7),
    ADD COLUMN IF NOT EXISTS destination_longitude NUMERIC(10, 7),
    ADD COLUMN IF NOT EXISTS route_geometry TEXT,
    ADD COLUMN IF NOT EXISTS fare_per_km NUMERIC(10, 2);

UPDATE routes SET fare_per_km = 0 WHERE fare_per_km IS NULL;
ALTER TABLE routes ALTER COLUMN fare_per_km SET DEFAULT 0;