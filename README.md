# BUSSIN

BUSSIN is a bus trip booking and operations application. The repository contains a React/Vite commuter and staff interface, an Electron desktop shell, and a Spring Boot API backed by PostgreSQL with Firebase Authentication.

## Run locally

Use separate terminals for the API and web app. Install Node.js/npm and Java/Maven first.

1. Install frontend dependencies with `npm ci`.
2. Configure the environment values listed below without committing real credentials.
3. Start the API with `mvn spring-boot:run`.
4. Start the browser app with `npm run dev:web` (or run `npm run dev` to launch the Electron shell with Vite).

The API listens on port `8081` by default and the Vite app uses port `5173`. The frontend API base URL must point to the API origin.

### Environment values

Frontend build-time values:

- `VITE_API_BASE_URL`
- `VITE_FIREBASE_API_KEY`
- `VITE_FIREBASE_AUTH_DOMAIN`
- `VITE_FIREBASE_PROJECT_ID`
- `VITE_FIREBASE_STORAGE_BUCKET`
- `VITE_FIREBASE_MESSAGING_SENDER_ID`
- `VITE_FIREBASE_APP_ID`

Backend runtime values:

- `SUPABASE_DB_URL`, `SUPABASE_DB_USERNAME`, and `SUPABASE_DB_PASSWORD`
- `FIREBASE_SERVICE_ACCOUNT` containing the Firebase service account JSON, or a local `secrets/service-account-key.json` file
- `CORS_ALLOWED_ORIGINS` for the allowed browser origins
- `PORT` to override the API port

Keep backend credentials out of source control. Frontend Firebase values are client configuration; restrict the Firebase project’s API keys and authentication settings in Firebase Console. The local secrets directory and `.env` files are not project deliverables.

## Checks

- `npm test` runs the frontend component tests.
- `npm run build` creates the production web bundle.
- `npm run lint` runs ESLint across the frontend repository.
- `mvn -Dtest=BookingServiceTest,QueueServiceTest,TripServiceTest,SecurityConfigTest test` runs focused API service and security tests without starting the database-backed Spring application context.
- `mvn test` also runs the full suite; Spring context tests require valid database and Firebase runtime configuration.

## Database changes

The SQL scripts under `src/main/resources/sql` are manual migrations; Spring does not apply them automatically. Before applying a migration, back up the database and stop the API. Check the deployed schema first and run each needed script once:

1. `multi-seat-booking-migration.sql`
2. `booking-cancellation-archive-migration.sql`
3. `guest-booking-migration.sql`
4. `employee-trip-assignment-migration.sql` (independent of the first three)

These migrations support multi-seat bookings and rebooking, retained cancellation history, guest bookings, and employee trip assignments. Validate them against a non-production database before a production rollout.

## Release readiness

The app has automated coverage for core booking behavior and selected staff operations, plus the focused checks above. Live acceptance scenarios that need real Firebase roles, browser sessions, or a database are tracked in [docs/RELEASE_ACCEPTANCE.md](docs/RELEASE_ACCEPTANCE.md); unchecked items still need a human run-through before release.

Payment status is currently recorded manually by authorized staff as `UNPAID`, `PAID`, or `REFUNDED`; the code does not process payments through a payment provider. Desktop installer packaging and deployment-specific settings should be validated in the release environment.
