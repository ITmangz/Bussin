-- Adds an optional employee assignment to each trip.
-- Run once against the BUSSIN database before deploying the employee module.

ALTER TABLE trips
    ADD COLUMN IF NOT EXISTS employee_id BIGINT;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_trips_employee'
    ) THEN
        ALTER TABLE trips
            ADD CONSTRAINT fk_trips_employee
            FOREIGN KEY (employee_id)
            REFERENCES users(id)
            ON DELETE SET NULL;
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_trips_employee_id
    ON trips(employee_id);
