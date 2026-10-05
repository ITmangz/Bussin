import { Navigate } from "react-router-dom";

import { useAuth } from "../../contexts/AuthContext";

function RoleRoute({ allowedRoles, children }) {
  const { isAuthenticated, role, loading } = useAuth();

  if (loading) {
    return <div>Loading...</div>;
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (!role) {
    return <div>Loading user profile...</div>;
  }

  if (!allowedRoles.includes(role)) {
    return <Navigate to="/dashboard" replace />;
  }

  return children;
}

export default RoleRoute;
