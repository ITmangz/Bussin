-- BUSSIN guest booking support
-- Stop the API and run once against the PostgreSQL database before starting
-- a build that accepts guest bookings. This intentionally keeps guest
-- bookings attached to trips and queue entries without creating user rows.

ALTER TABLE IF EXISTS bookings
    ALTER COLUMN commuter_id DROP NOT NULL;

ALTER TABLE IF EXISTS queue_entries
    ALTER COLUMN commuter_id DROP NOT NULL;

ALTER TABLE IF EXISTS cancelled_bookings
    ALTER COLUMN commuter_id DROP NOT NULL;

ALTER TABLE IF EXISTS queue_entries
    ADD COLUMN IF NOT EXISTS booking_id BIGINT,
    ADD COLUMN IF NOT EXISTS passenger_name VARCHAR(100),
    ADD COLUMN IF NOT EXISTS passenger_email VARCHAR(150);

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = current_schema()
          AND table_name = 'queue_entries'
          AND column_name = 'user_id'
    ) THEN
        ALTER TABLE queue_entries
            ALTER COLUMN user_id DROP NOT NULL;
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'uk_queue_booking'
          AND conrelid = to_regclass(current_schema() || '.queue_entries')
    ) THEN
        ALTER TABLE queue_entries
            ADD CONSTRAINT uk_queue_booking UNIQUE (booking_id);
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_queue_entries_booking'
          AND conrelid = to_regclass(current_schema() || '.queue_entries')
    ) THEN
        ALTER TABLE queue_entries
            ADD CONSTRAINT fk_queue_entries_booking
            FOREIGN KEY (booking_id)
            REFERENCES bookings (id)
            ON DELETE SET NULL;
    END IF;
END $$;
