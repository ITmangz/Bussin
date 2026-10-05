-- BUSSIN multi-seat booking migration
--
-- Run this once against the existing Supabase PostgreSQL database before
-- creating a new multi-seat booking.
--
-- The legacy bookings table stored one seat in bookings.seat_number and
-- older deployments could have a UNIQUE constraint on (trip_id, seat_number).
-- That constraint is incompatible with cancellation/rebooking and multi-seat
-- bookings because the authoritative seat allocation now lives in
-- booking_seats. Remove both possible legacy trip/seat unique constraints.
--
-- The application now protects active seat allocation transactionally by
-- locking the trip row and checking active booking_seats.

DO $$
DECLARE
    constraint_record RECORD;
BEGIN
    FOR constraint_record IN
        SELECT
            ns.nspname AS schema_name,
            cls.relname AS table_name,
            con.conname AS constraint_name
        FROM pg_constraint con
        JOIN pg_class cls
            ON cls.oid = con.conrelid
        JOIN pg_namespace ns
            ON ns.oid = cls.relnamespace
        WHERE con.contype = 'u'
          AND ns.nspname = current_schema()
          AND cls.relname IN ('bookings', 'booking_seats')
          AND (
              (
                  cls.relname = 'bookings'
                  AND (
                      SELECT array_agg(att.attname ORDER BY ord.ordinality)
                      FROM unnest(con.conkey) WITH ORDINALITY AS ord(attnum, ordinality)
                      JOIN pg_attribute att
                        ON att.attrelid = cls.oid
                       AND att.attnum = ord.attnum
                  ) = ARRAY['seat_number', 'trip_id']
              )
              OR
              (
                  cls.relname = 'booking_seats'
                  AND (
                      SELECT array_agg(att.attname ORDER BY ord.ordinality)
                      FROM unnest(con.conkey) WITH ORDINALITY AS ord(attnum, ordinality)
                      JOIN pg_attribute att
                        ON att.attrelid = cls.oid
                       AND att.attnum = ord.attnum
                  ) = ARRAY['seat_number', 'trip_id']
              )
          )
    LOOP
        EXECUTE format(
            'ALTER TABLE %I.%I DROP CONSTRAINT %I',
            constraint_record.schema_name,
            constraint_record.table_name,
            constraint_record.constraint_name
        );
    END LOOP;
END $$;

-- These were the expected names in older BUSSIN deployments. IF EXISTS keeps
-- this safe when the database generated a different constraint name.
ALTER TABLE bookings
    DROP CONSTRAINT IF EXISTS uk_booking_trip_seat;

ALTER TABLE bookings
    DROP CONSTRAINT IF EXISTS uk_booking_commuter_trip;
