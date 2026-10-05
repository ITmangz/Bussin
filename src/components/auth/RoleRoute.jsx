import { Navigate } from "react-router-dom";

import { useAuth } from "../../contexts/AuthContext";

function RoleRoute({ allowedRoles, children }) {
  const { isAuthenticated, role, loading } = useAuth();

  console.log("ROLE ROUTE:", {
    allowedRoles,
    role,
    isAuthenticated,
    loading,
  });

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
    console.log("ROLE ROUTE DENIED:", role, "allowed:", allowedRoles);

    return <Navigate to="/dashboard" replace />;
  }

  console.log("ROLE ROUTE ALLOWED:", role, "allowed:", allowedRoles);

  return children;
}

export default RoleRoute;
