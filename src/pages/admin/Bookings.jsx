import { useEffect, useMemo, useState } from "react";
import { CalendarClock, Eye, Search, Ticket, X } from "lucide-react";
import { getAllBookings, updateBookingStatus } from "../../services/adminBookingService";
import "./Bookings.css";

const STATUSES = ["PENDING", "CONFIRMED", "COMPLETED", "CANCELLED"];

function formatDateTime(value) {
  if (!value) return "—";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "—";
  return date.toLocaleString("en-PH", {
    year: "numeric", month: "short", day: "numeric",
    hour: "numeric", minute: "2-digit",
  });
}

function money(value) {
  const n = Number(value);
  return Number.isFinite(n) ? new Intl.NumberFormat("en-PH", {
    style: "currency", currency: "PHP",
  }).format(n) : "—";
}

function Bookings() {
  const [bookings, setBookings] = useState([]);
  const [search, setSearch] = useState("");
  const [status, setStatus] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [selected, setSelected] = useState(null);
  const [saving, setSaving] = useState(false);

  async function loadBookings() {
    try {
      setLoading(true); setError("");
      const data = await getAllBookings();
      setBookings(Array.isArray(data) ? data : []);
    } catch (err) {
      console.error(err);
      setError(err.response?.data?.message || err.response?.data?.error || "Unable to load bookings.");
    } finally { setLoading(false); }
  }

  useEffect(() => { loadBookings(); }, []);

  const filtered = useMemo(() => {
    const q = search.trim().toLowerCase();
    return bookings.filter((b) => {
      const text = [
        b.bookingReference, b.passengerName, b.passengerEmail,
        b.commuterName, b.commuterEmail, b.routeIdentifier,
        b.origin, b.destination, b.busPlateNumber, b.seatNumber,
      ].filter(Boolean).join(" ").toLowerCase();
      return (!q || text.includes(q)) && (!status || b.status === status);
    });
  }, [bookings, search, status]);

  async function changeStatus(nextStatus) {
    if (!selected || nextStatus === selected.status) return;
    if (selected.status === "COMPLETED" && nextStatus !== "COMPLETED") {
      setError("Completed bookings cannot be reopened.");
      return;
    }
    try {
      setSaving(true); setError("");
      const updated = await updateBookingStatus(selected.id, nextStatus);
      setBookings((current) => current.map((b) => b.id === updated.id ? updated : b));
      setSelected(updated);
    } catch (err) {
      setError(err.response?.data?.message || err.response?.data?.error || "Unable to update booking status.");
    } finally { setSaving(false); }
  }

  return (
    <section className="bookings-admin-page">
      <header className="bookings-admin-header">
        <div><h1>Bookings</h1><p>View commuter bookings and manage booking status.</p></div>
      </header>
      {error && <div className="bookings-error">{error}</div>}
      <div className="bookings-toolbar">
        <div className="bookings-search"><Search size={16}/><input type="search" value={search} onChange={(e)=>setSearch(e.target.value)} placeholder="Search reference, passenger, route, bus..." aria-label="Search bookings"/></div>
        <select value={status} onChange={(e)=>setStatus(e.target.value)} aria-label="Filter booking status">
          <option value="">All statuses</option>{STATUSES.map((s)=><option key={s} value={s}>{s}</option>)}
        </select>
      </div>
      <div className="admin-dashboard-panel bookings-table-card">
        {loading ? <div className="bookings-empty"><CalendarClock size={25}/><h3>Loading bookings</h3><p>Getting booking records from the server.</p></div> :
        filtered.length === 0 ? <div className="bookings-empty"><Ticket size={25}/><h3>{bookings.length ? "No bookings found" : "No bookings yet"}</h3><p>{bookings.length ? "Try another search or status filter." : "Bookings created by commuters will appear here."}</p></div> :
        <div className="bookings-table-wrap"><table className="bookings-table"><thead><tr><th>Reference</th><th>Passenger</th><th>Route</th><th>Trip</th><th>Seat</th><th>Fare</th><th>Booking</th><th>Payment</th><th>Action</th></tr></thead><tbody>
          {filtered.map((b)=><tr key={b.id}><td><strong>{b.bookingReference}</strong><small>#{b.id}</small></td><td><strong>{b.passengerName}</strong><small>{b.passengerEmail}</small></td><td><span>{b.origin} → {b.destination}</span><small>{b.routeIdentifier || "Route"}</small></td><td><strong>#{b.tripId}</strong><small>{formatDateTime(b.scheduledDeparture)}</small></td><td>{b.seatNumber}</td><td>{money(b.fare)}</td><td><span className={"booking-status "+String(b.status).toLowerCase()}>{b.status}</span></td><td><span className={"payment-status "+String(b.paymentStatus).toLowerCase()}>{b.paymentStatus}</span></td><td><button type="button" className="booking-view-button" onClick={()=>setSelected(b)}><Eye size={15}/> Details</button></td></tr>)}
        </tbody></table></div>}
        {!loading && filtered.length > 0 && <div className="bookings-count">Showing {filtered.length} of {bookings.length} bookings</div>}
      </div>
      {selected && <div className="booking-admin-overlay" onMouseDown={(e)=>e.target===e.currentTarget&&setSelected(null)}>
        <div className="booking-admin-modal" role="dialog" aria-modal="true" aria-label="Booking details">
          <div className="booking-admin-modal-header"><div><span>Booking Details</span><h2>{selected.bookingReference}</h2></div><button type="button" onClick={()=>setSelected(null)} aria-label="Close"><X size={19}/></button></div>
          <div className="booking-admin-body">
            <div className="booking-detail-route"><strong>{selected.origin}</strong><span>→</span><strong>{selected.destination}</strong></div>
            <div className="booking-detail-grid">
              <div><span>Passenger</span><strong>{selected.passengerName}</strong></div><div><span>Phone</span><strong>{selected.passengerPhone}</strong></div>
              <div><span>Email</span><strong>{selected.passengerEmail}</strong></div><div><span>Commuter</span><strong>{selected.commuterName}</strong></div>
              <div><span>Trip</span><strong>#{selected.tripId}</strong></div><div><span>Bus</span><strong>{selected.busPlateNumber}</strong></div>
              <div><span>Departure</span><strong>{formatDateTime(selected.scheduledDeparture)}</strong></div><div><span>Arrival</span><strong>{formatDateTime(selected.scheduledArrival)}</strong></div>
              <div><span>Seat</span><strong>{selected.seatNumber}</strong></div><div><span>Fare</span><strong>{money(selected.fare)}</strong></div>
              <div><span>Payment</span><strong>{selected.paymentStatus}</strong></div><div><span>Created</span><strong>{formatDateTime(selected.createdAt)}</strong></div>
            </div>
            <label className="booking-status-control">Booking status<select value={selected.status} disabled={saving || selected.status === "COMPLETED"} onChange={(e)=>changeStatus(e.target.value)}>{STATUSES.map(s=><option key={s}>{s}</option>)}</select></label>
          </div>
          <div className="booking-admin-footer"><span className={"booking-status "+String(selected.status).toLowerCase()}>{selected.status}</span><button type="button" className="booking-close-button" onClick={()=>setSelected(null)}>Close</button></div>
        </div>
      </div>}
    </section>
  );
}
export default Bookings;
