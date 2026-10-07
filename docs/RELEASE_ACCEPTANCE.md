# BUSSIN release acceptance

Run these scenarios with a non-production database and test accounts. Record the build, database migration state, role/account used, and result for each run. Do not mark a scenario complete based on code inspection alone.

## Accounts and access

- [ ] A commuter can sign in and view only their own bookings and cancellation history.
- [ ] A guest can book without signing in and receives a usable booking reference and e-ticket.
- [ ] A guest booking is visible to Admin and to the assigned Employee in bookings, queue, and boarding workflows, but does not appear in commuter account history.
- [ ] Admin, Employee, and Commuter routes and API operations enforce the expected roles; an Employee cannot act on an unassigned trip.

## Trips, seats, and bookings

- [ ] Admin-created trips appear in commuter trip search with correct route, bus, schedule, status, and fare.
- [ ] A commuter can book one seat and multiple seats; occupied seats cannot be selected.
- [ ] Two sessions attempting the same seat result in only one active booking.
- [ ] Booking confirmation shows the right passenger, trip, seat list, queue number, fare, and payment status.
- [ ] Cancelling a booking removes it from active availability, retains it in account history and Admin records, and compacts the remaining queue numbers.
- [ ] The passenger can book again after a cancellation without corrupting seat or queue availability.

## Staff operations and documents

- [ ] Employee trip assignment filters dashboard, manifest, queue, and boarding records to that Employee's trips.
- [ ] Staff can call and board eligible queue entries; refreshed views show the resulting status and sequence.
- [ ] Admin can update payment status; an Employee can do so only for an assigned trip. An unpaid cancellation cannot be marked refunded.
- [ ] Commuter, Admin, and Employee e-tickets/receipts print with the correct booking data and no clipped content.
- [ ] Admin report date/status filters, export formats, and print output contain the expected records and totals.
- [ ] Admin and Employee dashboards show counts that match their underlying trip/booking records, including empty states.

## Release environment

- [ ] Each manual database migration needed by the deployed build has been applied and verified against the target schema after a backup.
- [ ] API and frontend use the intended production database, Firebase project, API URL, and allowed origins; no test credentials are present.
- [ ] API health, browser login, role access, CORS, and end-to-end booking flow work on the release deployment.
- [ ] Electron/web release artifacts install or load correctly on the target devices; desktop distribution and update behavior are verified.
- [ ] Review and resolve or explicitly accept dependency audit findings before release. The current npm audit reports 14 findings: 8 moderate, 4 high, and 2 critical; its suggested forced fixes include package downgrades.
- [ ] Clear the existing repository-wide lint findings in Electron and legacy frontend modules, or record approved scoped exceptions. Focused lint on newly added component tests should pass.

## Known scope to decide before launch

- Payment status is a staff-entered record, not payment processing. If BUSSIN will collect money online, choose a payment provider and implement its secure checkout, webhook verification, reconciliation, and refund flow separately.
- Run the full backend suite with the intended integration database/Firebase test configuration in addition to focused unit tests.
