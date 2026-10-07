import { useCallback, useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import { ArrowRight, CalendarClock, RefreshCw, Route, Ticket, UserCheck } from "lucide-react";

import AppCard from "../../components/ui/AppCard";
import { useAuth } from "../../contexts/AuthContext";
import { getEmployeeBookings } from "../../services/adminBookingService";
import { getEmployeeQueue } from "../../services/queueService";
import { getEmployeeTrips } from "../../services/tripService";
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

  const loadDashboard = useCallback(async () => {
    setLoading(true);
    setError("");

    const results = await Promise.allSettled([
      getEmployeeTrips(),
      getEmployeeBookings(),
      getEmployeeQueue(),
    ]);
    const failures = [];
    const sections = [
      { label: "assigned trips", result: results[0], setData: setTrips },
      { label: "bookings", result: results[1], setData: setBookings },
      { label: "queue", result: results[2], setData: setQueue },
    ];

    sections.forEach(({ label, result, setData }) => {
      if (result.status === "fulfilled") {
        setData(Array.isArray(result.value) ? result.value : []);
      } else {
        console.error(`Failed to load employee ${label}:`, result.reason);
        failures.push(
          result.reason.response?.data?.message
            || `Unable to load your ${label}.`,
        );
      }
    });

    setCurrentTime(Date.now());
    setError(failures.join(" "));
    setLoading(false);
  }, []);

  useEffect(() => {
    void Promise.resolve().then(loadDashboard);
  }, [loadDashboard]);

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
        <div className="employee-dashboard-actions">
          <button
            type="button"
            className="employee-dashboard-refresh"
            onClick={loadDashboard}
            disabled={loading}
            aria-label="Refresh employee dashboard"
          >
            <RefreshCw size={15} className={loading ? "employee-dashboard-refreshing" : ""} />
            Refresh
          </button>
          <Link className="employee-dashboard-link" to="/employee/trips">View my trips <ArrowRight size={15} /></Link>
        </div>
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
