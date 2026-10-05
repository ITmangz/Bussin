import { useAuth } from "../../contexts/AuthContext";

function EmployeeDashboard() {
  const { profile } = useAuth();

  const displayName = profile?.firstName || profile?.displayName || "Employee";

  return (
    <div>
      <div className="admin-page-header">
        <h1 className="admin-page-title">Employee Dashboard</h1>

        <p className="admin-page-description">
          Welcome back, {displayName}. Manage your assigned trips and passenger
          operations.
        </p>
      </div>

      <div className="admin-dashboard-panel">
        <h2 className="admin-panel-title">Operations Overview</h2>

        <p className="admin-panel-description">
          Your assigned trips, bookings, queue, and boarding information will
          appear here.
        </p>
      </div>
    </div>
  );
}

export default EmployeeDashboard;
