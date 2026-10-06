import { useEffect, useMemo, useState } from "react";
import { RefreshCw, Ticket } from "lucide-react";

import AppCard from "../../components/ui/AppCard";
import { getEmployeeBookings } from "../../services/adminBookingService";
import "./EmployeeTrips.css";
import "./EmployeeBookings.css";

function formatDate(value) {
  if (!value) return "—";
  const date = new Date(value);
  return Number.isNaN(date.getTime())
    ? "—"
    : date.toLocaleString("en-PH", { month: "short", day: "numeric", year: "numeric", hour: "numeric", minute: "2-digit" });
}

function formatSeats(booking) {
  const seats = Array.isArray(booking.seatNumbers) && booking.seatNumbers.length
    ? booking.seatNumbers
    : booking.seatNumber ? [booking.seatNumber] : [];
  return seats.length ? seats.join(", ") : "—";
}

function EmployeeBookings() {
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [search, setSearch] = useState("");
  const [status, setStatus] = useState("");

  async function loadBookings() {
    try {
      setError("");
      const response = await getEmployeeBookings();
      setBookings(Array.isArray(response) ? response : []);
    } catch (loadError) {
      console.error("Failed to load assigned-trip bookings:", loadError);
      setError(loadError.response?.data?.message || "Unable to load bookings for your assigned trips.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    void Promise.resolve().then(loadBookings);
  }, []);

  const filteredBookings = useMemo(() => {
    const query = search.trim().toLowerCase();
    return bookings.filter((booking) => {
      const matchesStatus = !status || booking.status === status;
      const matchesSearch = !query || [
        booking.bookingReference,
        booking.commuterName,
        booking.passengerName,
        booking.passengerPhone,
        booking.routeIdentifier,
        booking.busPlateNumber,
        booking.tripId,
      ].some((value) => String(value || "").toLowerCase().includes(query));
      return matchesStatus && matchesSearch;
    });
  }, [bookings, search, status]);

  function refreshBookings() {
    setLoading(true);
    loadBookings();
  }

  return (
    <section className="employee-bookings-page">
      <header className="employee-module-header">
        <div>
          <h1>Bookings</h1>
          <p>Passenger manifests for trips assigned to you.</p>
        </div>
        <button type="button" className="employee-module-refresh" onClick={refreshBookings} disabled={loading}>
          <RefreshCw size={15} /> Refresh
        </button>
      </header>

      {error && <div className="employee-module-error" role="alert">{error}</div>}

      <div className="employee-bookings-toolbar">
        <label className="employee-bookings-search">
          <Ticket size={16} />
          <input type="search" value={search} onChange={(event) => setSearch(event.target.value)} placeholder="Search passenger, booking, or trip" aria-label="Search bookings" />
        </label>
        <select value={status} onChange={(event) => setStatus(event.target.value)} aria-label="Filter bookings by status">
          <option value="">All statuses</option>
          <option value="CONFIRMED">Confirmed</option>
          <option value="PENDING">Pending</option>
          <option value="COMPLETED">Completed</option>
          <option value="CANCELLED">Cancelled</option>
        </select>
      </div>

      <AppCard className="employee-module-card">
        {loading ? (
          <div className="employee-module-state"><Ticket size={26} /><strong>Loading bookings</strong><span>Getting passenger manifests for your trips.</span></div>
        ) : filteredBookings.length === 0 ? (
          <div className="employee-module-state"><Ticket size={26} /><strong>{bookings.length ? "No matching bookings" : "No bookings for your trips"}</strong><span>{bookings.length ? "Change your search or status filter." : "Passenger bookings will appear here after a trip is assigned to you."}</span></div>
        ) : (
          <div className="employee-module-table-wrap">
            <table className="employee-module-table employee-bookings-table">
              <thead><tr><th>Booking</th><th>Passenger</th><th>Trip</th><th>Seats</th><th>Queue</th><th>Departure</th><th>Status</th></tr></thead>
              <tbody>
                {filteredBookings.map((booking) => (
                  <tr key={booking.id}>
                    <td><strong>{booking.bookingReference || `#${booking.id}`}</strong><small>{booking.seatCount || 1} passenger seat{(booking.seatCount || 1) === 1 ? "" : "s"}</small></td>
                    <td><strong>{booking.passengerName || booking.commuterName || "Passenger"}</strong><small>{booking.guestBooking ? "Guest booking · " : ""}{booking.passengerPhone || booking.passengerEmail || booking.commuterEmail || "Contact not provided"}</small></td>
                    <td><strong>#{booking.tripId}</strong><small>{booking.routeIdentifier || "Route not available"} · {booking.busPlateNumber || "Bus not available"}</small></td>
                    <td>{formatSeats(booking)}</td>
                    <td>{booking.queueNumber ? `Q${booking.queueNumber} · ${booking.queueStatus || "WAITING"}` : "—"}</td>
                    <td>{formatDate(booking.scheduledDeparture)}</td>
                    <td><span className={`employee-module-status ${String(booking.status || "").toLowerCase()}`}>{booking.status || "Unknown"}</span></td>
                  </tr>
                ))}
              </tbody>
            </table>
            <div className="employee-module-count">Showing {filteredBookings.length} of {bookings.length} bookings</div>
          </div>
        )}
      </AppCard>
    </section>
  );
}

export default EmployeeBookings;
