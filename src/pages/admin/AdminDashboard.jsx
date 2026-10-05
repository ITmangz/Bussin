import { useAuth } from "../../contexts/AuthContext";

function AdminDashboard() {
  const { profile } = useAuth();

  return (
    <div>
      <h1>Admin Dashboard</h1>

      <p>Welcome, {profile?.firstName || profile?.email || "Administrator"}.</p>

      <p>This is the BUSSIN administration portal.</p>
    </div>
  );
}

export default AdminDashboard;
