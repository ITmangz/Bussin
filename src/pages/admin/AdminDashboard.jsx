import { Bus, Map, Route, Ticket } from "lucide-react";

import { useAuth } from "../../contexts/AuthContext";

function AdminDashboard() {
  const { profile } = useAuth();

  const displayName =
    profile?.firstName || profile?.displayName || "Administrator";

  return (
    <div>
      <div className="admin-page-header">
        <h1 className="admin-page-title">Admin Dashboard</h1>

        <p className="admin-page-description">
          Welcome back, {displayName}. Manage and monitor the BUSSIN
          transportation system.
        </p>
      </div>

      <div className="admin-dashboard-grid">
        <div className="admin-stat-card">
          <Bus size={22} />
          <div className="admin-stat-label">Total Buses</div>
          <div className="admin-stat-value">—</div>
        </div>

        <div className="admin-stat-card">
          <Map size={22} />
          <div className="admin-stat-label">Active Routes</div>
          <div className="admin-stat-value">—</div>
        </div>

        <div className="admin-stat-card">
          <Route size={22} />
          <div className="admin-stat-label">Today's Trips</div>
          <div className="admin-stat-value">—</div>
        </div>

        <div className="admin-stat-card">
          <Ticket size={22} />
          <div className="admin-stat-label">Today's Bookings</div>
          <div className="admin-stat-value">—</div>
        </div>
      </div>

      <div className="admin-dashboard-panel">
        <h2 className="admin-panel-title">System Overview</h2>

        <p className="admin-panel-description">
          Dashboard statistics and operational information will appear here once
          the management APIs are connected.
        </p>
      </div>
    </div>
  );
}

export default AdminDashboard;
