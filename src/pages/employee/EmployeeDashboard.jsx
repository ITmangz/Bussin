import { useAuth } from "../../contexts/AuthContext";

function EmployeeDashboard() {
  const { profile } = useAuth();

  return (
    <div>
      <h1>Employee Dashboard</h1>

      <p>Welcome, {profile?.firstName || profile?.email || "Employee"}.</p>

      <p>This is the BUSSIN employee operations portal.</p>
    </div>
  );
}

export default EmployeeDashboard;
