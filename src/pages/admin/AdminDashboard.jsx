import { useCallback, useEffect, useState } from "react";
import { Bus, Map, RefreshCw, Route, Ticket } from "lucide-react";

import { useAuth } from "../../contexts/AuthContext";
import { getDashboardMetrics } from "../../services/dashboardService";

const numberFormat = new Intl.NumberFormat("en-PH");

function AdminDashboard() {
  const { profile } = useAuth();
  const [metrics, setMetrics] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const loadDashboard = useCallback(async () => {
    setLoading(true);
    setError("");

    try {
      setMetrics(await getDashboardMetrics());
    } catch (loadError) {
      console.error("Failed to load admin dashboard metrics:", loadError);
      setError(
        loadError.response?.data?.message
          || "Unable to load dashboard statistics. Please refresh and try again.",
      );
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    void Promise.resolve().then(loadDashboard);
  }, [loadDashboard]);

  const displayName =
    profile?.firstName || profile?.displayName || "Administrator";
  const stats = [
    { icon: Bus, label: "Total Buses", value: metrics?.totalBuses },
    { icon: Map, label: "Active Routes", value: metrics?.activeRoutes },
    { icon: Route, label: "Today's Trips", value: metrics?.todaysTrips },
    { icon: Ticket, label: "Today's Bookings", value: metrics?.todaysBookings },
  ];
  const overview = [
    { label: "Commuters", value: metrics?.totalUsers },
    { label: "Employees", value: metrics?.totalEmployees },
    { label: "Total trips", value: metrics?.totalTrips },
    { label: "Total bookings", value: metrics?.totalBookings },
    { label: "Confirmed bookings", value: metrics?.confirmedBookings },
    { label: "Waiting passengers", value: metrics?.waitingQueueEntries },
    { label: "Cancelled bookings", value: metrics?.cancelledBookings },
  ];

  function formatValue(value) {
    return loading || value == null ? "—" : numberFormat.format(value);
  }

  return (
    <div>
      <div className="admin-page-header admin-dashboard-header">
        <div>
          <h1 className="admin-page-title">Admin Dashboard</h1>
          <p className="admin-page-description">
            Welcome back, {displayName}. Live overview of the BUSSIN transportation system.
          </p>
        </div>
        <button
          type="button"
          className="admin-dashboard-refresh"
          onClick={loadDashboard}
          disabled={loading}
          aria-label="Refresh dashboard statistics"
        >
          <RefreshCw size={15} className={loading ? "admin-dashboard-refreshing" : ""} />
          Refresh
        </button>
      </div>

      {error && <div className="admin-dashboard-error" role="alert">{error}</div>}

      <div className="admin-dashboard-grid" aria-busy={loading}>
        {stats.map(({ icon: Icon, label, value }) => (
          <div className="admin-stat-card" key={label}>
            <Icon size={22} />
            <div className="admin-stat-label">{label}</div>
            <div className="admin-stat-value">{formatValue(value)}</div>
          </div>
        ))}
      </div>

      <div className="admin-dashboard-panel">
        <h2 className="admin-panel-title">System Overview</h2>
        <p className="admin-panel-description">
          Current booking and operations totals.
        </p>
        <div className="admin-dashboard-overview-grid" aria-busy={loading}>
          {overview.map(({ label, value }) => (
            <div className="admin-dashboard-overview-item" key={label}>
              <span>{label}</span>
              <strong>{formatValue(value)}</strong>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

export default AdminDashboard;
