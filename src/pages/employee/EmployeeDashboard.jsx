import { useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import { ArrowRight, CalendarClock, Route, Ticket, UserCheck } from "lucide-react";

import AppCard from "../../components/ui/AppCard";
import { useAuth } from "../../contexts/AuthContext";
import { getEmployeeBookings } from "../../services/adminBookingService";
import { getEmployeeQueue } from "../../services/queueService";
import { getAllTrips } from "../../services/tripService";
import "./EmployeeDashboard.css";

function formatDate(value) {
  if (!value) return "—";
  const date = new Date(value);
  return Number.isNaN(date.getTime())
    ? "—"
    : date.toLocaleString("en-PH", { month: "short", day: "numeric", hour: "numeric", minute: "2-digit" });
}

function EmployeeDashboard() {
  const { profile } = useAuth();
  const [trips, setTrips] = useState([]);
  const [bookings, setBookings] = useState([]);
  const [queue, setQueue] = useState([]);
  const [currentTime, setCurrentTime] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    async function loadDashboard() {
      try {
        const [tripData, bookingData, queueData] = await Promise.all([
          getAllTrips(),
          getEmployeeBookings(),
          getEmployeeQueue(),
        ]);
        setTrips(Array.isArray(tripData) ? tripData : []);
        setBookings(Array.isArray(bookingData) ? bookingData : []);
        setQueue(Array.isArray(queueData) ? queueData : []);
        setCurrentTime(Date.now());
      } catch (loadError) {
        console.error("Failed to load employee dashboard:", loadError);
        setError(loadError.response?.data?.message || "Unable to load your operations overview.");
      } finally {
        setLoading(false);
      }
    }

    void Promise.resolve().then(loadDashboard);
  }, []);

  const upcomingTrips = useMemo(() => trips
    .filter((trip) => new Date(trip.scheduledDeparture).getTime() >= currentTime
      && !["COMPLETED", "CANCELLED"].includes(trip.status))
    .sort((left, right) => new Date(left.scheduledDeparture) - new Date(right.scheduledDeparture)), [trips, currentTime]);

  const activeBookings = bookings.filter((booking) => ["PENDING", "CONFIRMED"].includes(booking.status));
  const calledPassengers = queue.filter((entry) => entry.status === "CALLED").length;
  const displayName = profile?.firstName || "Employee";

  return (
    <section className="employee-dashboard-page">
      <header className="employee-dashboard-header">
        <div>
          <h1>Welcome back, {displayName}</h1>
          <p>Your schedule and passenger operations for trips assigned to you.</p>
        </div>
        <Link className="employee-dashboard-link" to="/employee/trips">View my trips <ArrowRight size={15} /></Link>
      </header>

      {error && <div className="employee-module-error" role="alert">{error}</div>}

      <div className="employee-dashboard-stats">
        <StatCard icon={Route} label="Assigned trips" value={loading ? "—" : trips.length} />
        <StatCard icon={CalendarClock} label="Upcoming trips" value={loading ? "—" : upcomingTrips.length} />
        <StatCard icon={Ticket} label="Active bookings" value={loading ? "—" : activeBookings.length} />
        <StatCard icon={UserCheck} label="Called to board" value={loading ? "—" : calledPassengers} />
      </div>

      <AppCard className="employee-dashboard-panel">
        <div className="employee-dashboard-panel-header">
          <div><h2>Next trips</h2><p>Your next scheduled assignments.</p></div>
          <Link to="/employee/trips">All trips <ArrowRight size={14} /></Link>
        </div>
        {loading ? (
          <div className="employee-dashboard-empty">Loading your schedule…</div>
        ) : upcomingTrips.length === 0 ? (
          <div className="employee-dashboard-empty">No upcoming trips are assigned to you.</div>
        ) : (
          <div className="employee-dashboard-trip-list">
            {upcomingTrips.slice(0, 5).map((trip) => (
              <div className="employee-dashboard-trip-row" key={trip.id}>
                <div className="employee-dashboard-trip-icon"><Route size={17} /></div>
                <div className="employee-dashboard-trip-details">
                  <strong>Trip #{trip.id} · {trip.routeIdentifier || `Route #${trip.routeId}`}</strong>
                  <span>{trip.busPlateNumber || `Bus #${trip.busId}`} · Departs {formatDate(trip.scheduledDeparture)}</span>
                </div>
                <span className={`employee-module-status ${String(trip.status || "").toLowerCase()}`}>{trip.status}</span>
              </div>
            ))}
          </div>
        )}
      </AppCard>

      <div className="employee-dashboard-quick-links">
        <Link to="/employee/bookings"><Ticket size={18} /><span><strong>Passenger bookings</strong><small>Review manifests for your assigned trips.</small></span><ArrowRight size={15} /></Link>
        <Link to="/employee/queue"><UserCheck size={18} /><span><strong>Queue and boarding</strong><small>Call passengers and record boarding.</small></span><ArrowRight size={15} /></Link>
      </div>
    </section>
  );
}

function StatCard({ icon: Icon, label, value }) {
  return (
    <AppCard className="employee-dashboard-stat">
      <div className="employee-dashboard-stat-icon"><Icon size={18} /></div>
      <div><span>{label}</span><strong>{value}</strong></div>
    </AppCard>
  );
}

export default EmployeeDashboard;
