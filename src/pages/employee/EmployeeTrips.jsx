import { useEffect, useMemo, useState } from "react";
import { CalendarClock, RefreshCw, Route } from "lucide-react";

import AppCard from "../../components/ui/AppCard";
import { getEmployeeTrips } from "../../services/tripService";
import "./EmployeeTrips.css";

function formatDate(value) {
  if (!value) return "—";
  const date = new Date(value);
  return Number.isNaN(date.getTime())
    ? "—"
    : date.toLocaleString("en-PH", {
        year: "numeric",
        month: "short",
        day: "numeric",
        hour: "numeric",
        minute: "2-digit",
      });
}

function EmployeeTrips() {
  const [trips, setTrips] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [status, setStatus] = useState("");

  async function loadTrips() {
    try {
      setError("");
      const response = await getEmployeeTrips();
      setTrips(Array.isArray(response) ? response : []);
    } catch (loadError) {
      console.error("Failed to load assigned trips:", loadError);
      setError(loadError.response?.data?.message || "Unable to load your assigned trips.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    void Promise.resolve().then(loadTrips);
  }, []);

  const filteredTrips = useMemo(() => trips.filter((trip) => !status || trip.status === status), [trips, status]);

  function refreshTrips() {
    setLoading(true);
    loadTrips();
  }

  return (
    <section className="employee-trips-page">
      <header className="employee-module-header">
        <div>
          <h1>My Trips</h1>
          <p>Schedules assigned to your BUSSIN employee account.</p>
        </div>
        <button type="button" className="employee-module-refresh" onClick={refreshTrips} disabled={loading}>
          <RefreshCw size={15} /> Refresh
        </button>
      </header>

      {error && <div className="employee-module-error" role="alert">{error}</div>}

      <div className="employee-module-toolbar">
        <label htmlFor="employee-trip-status">Trip status</label>
        <select id="employee-trip-status" value={status} onChange={(event) => setStatus(event.target.value)}>
          <option value="">All statuses</option>
          <option value="SCHEDULED">Scheduled</option>
          <option value="BOARDING">Boarding</option>
          <option value="DEPARTED">Departed</option>
          <option value="COMPLETED">Completed</option>
          <option value="CANCELLED">Cancelled</option>
        </select>
      </div>

      <AppCard className="employee-module-card">
        {loading ? (
          <div className="employee-module-state"><CalendarClock size={26} /><strong>Loading trips</strong><span>Getting your assigned schedules.</span></div>
        ) : filteredTrips.length === 0 ? (
          <div className="employee-module-state"><Route size={26} /><strong>{trips.length ? "No trips match this status" : "No trips assigned yet"}</strong><span>{trips.length ? "Choose another trip status." : "Your administrator can assign you to trips from the Trips page."}</span></div>
        ) : (
          <div className="employee-module-table-wrap">
            <table className="employee-module-table">
              <thead><tr><th>Trip</th><th>Route</th><th>Bus</th><th>Departure</th><th>Arrival</th><th>Status</th></tr></thead>
              <tbody>
                {filteredTrips.map((trip) => (
                  <tr key={trip.id}>
                    <td><strong>#{trip.id}</strong></td>
                    <td><strong>{trip.routeIdentifier || `Route #${trip.routeId}`}</strong></td>
                    <td>{trip.busPlateNumber || `Bus #${trip.busId}`}<small>{trip.busCapacity ? `${trip.busCapacity} seats` : ""}</small></td>
                    <td>{formatDate(trip.scheduledDeparture)}</td>
                    <td>{formatDate(trip.scheduledArrival)}</td>
                    <td><span className={`employee-module-status ${String(trip.status || "").toLowerCase()}`}>{trip.status || "Unknown"}</span></td>
                  </tr>
                ))}
              </tbody>
            </table>
            <div className="employee-module-count">Showing {filteredTrips.length} of {trips.length} assigned trips</div>
          </div>
        )}
      </AppCard>
    </section>
  );
}

export default EmployeeTrips;
