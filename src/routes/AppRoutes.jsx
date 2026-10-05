import { Routes, Route, Navigate, useLocation } from "react-router-dom";

import AppLayout from "../components/layout/AppLayout";
import AdminLayout from "../components/layout/admin/AdminLayout";
import EmployeeLayout from "../components/layout/employee/EmployeeLayout";

import ProtectedRoute from "../components/auth/ProtectedRoute";
import RoleRoute from "../components/auth/RoleRoute";

import Login from "../pages/auth/Login";
import Register from "../pages/auth/Register";

import Dashboard from "../pages/Dashboard";
import AIBooking from "../pages/AiBooking";
import MyBookings from "../pages/MyBookings";
import Queue from "../pages/Queue";
import Trips from "../pages/Trips";
import Profile from "../pages/Profile";
import Booking from "../pages/Booking";

import AdminDashboard from "../pages/admin/AdminDashboard";
import Buses from "../pages/admin/Buses";
import AdminTrips from "../pages/admin/Trips";
import AdminRoutes from "../pages/admin/Routes";
import AdminBookings from "../pages/admin/Bookings";
import EmployeeDashboard from "../pages/employee/EmployeeDashboard";
import OperationsQueue from "../pages/OperationsQueue";

console.log("BUSSIN AppRoutes LOADED");

function AdminPlaceholder({ title, description }) {
  return (
    <div>
      <div className="admin-page-header">
        <h1 className="admin-page-title">{title}</h1>

        <p className="admin-page-description">{description}</p>
      </div>

      <div className="admin-dashboard-panel">
        <h2 className="admin-panel-title">Coming Next</h2>

        <p className="admin-panel-description">
          This management module is currently being developed.
        </p>
      </div>
    </div>
  );
}

function EmployeePlaceholder({ title, description }) {
  return (
    <div>
      <div className="admin-page-header">
        <h1 className="admin-page-title">{title}</h1>

        <p className="admin-page-description">{description}</p>
      </div>

      <div className="admin-dashboard-panel">
        <h2 className="admin-panel-title">Coming Next</h2>

        <p className="admin-panel-description">
          This employee module is currently being developed.
        </p>
      </div>
    </div>
  );
}

function AppRoutes() {
  const location = useLocation();

  console.log("BUSSIN CURRENT PATH:", location.pathname);

  return (
    <Routes>
      {/* =========================
          PUBLIC
      ========================= */}

      <Route path="/login" element={<Login />} />

      <Route path="/register" element={<Register />} />

      {/* =========================
          ADMIN
      ========================= */}

      <Route
        path="/admin"
        element={
          <ProtectedRoute>
            <RoleRoute allowedRoles={["ADMIN"]}>
              <AdminLayout>
                <AdminDashboard />
              </AdminLayout>
            </RoleRoute>
          </ProtectedRoute>
        }
      />

      <Route
        path="/admin/dashboard"
        element={
          <ProtectedRoute>
            <RoleRoute allowedRoles={["ADMIN"]}>
              <AdminLayout>
                <AdminDashboard />
              </AdminLayout>
            </RoleRoute>
          </ProtectedRoute>
        }
      />

      <Route
        path="/admin/buses"
        element={
          <ProtectedRoute>
            <RoleRoute allowedRoles={["ADMIN"]}>
              <AdminLayout>
                <Buses />
              </AdminLayout>
            </RoleRoute>
          </ProtectedRoute>
        }
      />

      <Route
        path="/admin/routes"
        element={
          <ProtectedRoute>
            <RoleRoute allowedRoles={["ADMIN"]}>
              <AdminLayout>
                <AdminRoutes />
              </AdminLayout>
            </RoleRoute>
          </ProtectedRoute>
        }
      />

      <Route
        path="/admin/trips"
        element={
          <ProtectedRoute>
            <RoleRoute allowedRoles={["ADMIN"]}>
              <AdminLayout>
                <AdminTrips />
              </AdminLayout>
            </RoleRoute>
          </ProtectedRoute>
        }
      />

      <Route
        path="/admin/bookings"
        element={
          <ProtectedRoute>
            <RoleRoute allowedRoles={["ADMIN"]}>
              <AdminLayout>
                <AdminBookings />
              </AdminLayout>
            </RoleRoute>
          </ProtectedRoute>
        }
      />

      <Route
        path="/admin/queue"
        element={
          <ProtectedRoute>
            <RoleRoute allowedRoles={["ADMIN"]}>
              <AdminLayout>
                <OperationsQueue title="Queue" description="Monitor passenger queues and manage boarding operations." />
              </AdminLayout>
            </RoleRoute>
          </ProtectedRoute>
        }
      />

      <Route
        path="/admin/users"
        element={
          <ProtectedRoute>
            <RoleRoute allowedRoles={["ADMIN"]}>
              <AdminLayout>
                <AdminPlaceholder
                  title="Users"
                  description="Manage BUSSIN commuter accounts and roles."
                />
              </AdminLayout>
            </RoleRoute>
          </ProtectedRoute>
        }
      />

      <Route
        path="/admin/employees"
        element={
          <ProtectedRoute>
            <RoleRoute allowedRoles={["ADMIN"]}>
              <AdminLayout>
                <AdminPlaceholder
                  title="Employees"
                  description="Manage employee accounts and assignments."
                />
              </AdminLayout>
            </RoleRoute>
          </ProtectedRoute>
        }
      />

      <Route
        path="/admin/reports"
        element={
          <ProtectedRoute>
            <RoleRoute allowedRoles={["ADMIN"]}>
              <AdminLayout>
                <AdminPlaceholder
                  title="Reports"
                  description="View BUSSIN operational and booking reports."
                />
              </AdminLayout>
            </RoleRoute>
          </ProtectedRoute>
        }
      />

      <Route
        path="/admin/settings"
        element={
          <ProtectedRoute>
            <RoleRoute allowedRoles={["ADMIN"]}>
              <AdminLayout>
                <AdminPlaceholder
                  title="Settings"
                  description="Configure BUSSIN system settings."
                />
              </AdminLayout>
            </RoleRoute>
          </ProtectedRoute>
        }
      />

      {/* =========================
          EMPLOYEE
      ========================= */}

      <Route
        path="/employee"
        element={
          <ProtectedRoute>
            <RoleRoute allowedRoles={["EMPLOYEE"]}>
              <EmployeeLayout>
                <EmployeeDashboard />
              </EmployeeLayout>
            </RoleRoute>
          </ProtectedRoute>
        }
      />

      <Route
        path="/employee/dashboard"
        element={
          <ProtectedRoute>
            <RoleRoute allowedRoles={["EMPLOYEE"]}>
              <EmployeeLayout>
                <EmployeeDashboard />
              </EmployeeLayout>
            </RoleRoute>
          </ProtectedRoute>
        }
      />

      <Route
        path="/employee/trips"
        element={
          <ProtectedRoute>
            <RoleRoute allowedRoles={["EMPLOYEE"]}>
              <EmployeeLayout>
                <EmployeePlaceholder
                  title="My Trips"
                  description="View trips assigned to you."
                />
              </EmployeeLayout>
            </RoleRoute>
          </ProtectedRoute>
        }
      />

      <Route
        path="/employee/bookings"
        element={
          <ProtectedRoute>
            <RoleRoute allowedRoles={["EMPLOYEE"]}>
              <EmployeeLayout>
                <EmployeePlaceholder
                  title="Bookings"
                  description="View and manage passenger bookings for your assigned trips."
                />
              </EmployeeLayout>
            </RoleRoute>
          </ProtectedRoute>
        }
      />

      <Route
        path="/employee/queue"
        element={
          <ProtectedRoute>
            <RoleRoute allowedRoles={["EMPLOYEE"]}>
              <EmployeeLayout>
                <OperationsQueue title="Queue" description="Monitor passenger queues for your assigned trips." />
              </EmployeeLayout>
            </RoleRoute>
          </ProtectedRoute>
        }
      />

      <Route
        path="/employee/boarding"
        element={
          <ProtectedRoute>
            <RoleRoute allowedRoles={["EMPLOYEE"]}>
              <EmployeeLayout>
                <EmployeePlaceholder
                  title="Boarding"
                  description="Manage passenger boarding and ticket verification."
                />
              </EmployeeLayout>
            </RoleRoute>
          </ProtectedRoute>
        }
      />

      {/* =========================
          COMMUTER
      ========================= */}

      <Route
        path="/dashboard"
        element={
          <ProtectedRoute>
            <AppLayout>
              <Dashboard />
            </AppLayout>
          </ProtectedRoute>
        }
      />

      <Route
        path="/ai-booking"
        element={
          <ProtectedRoute>
            <AppLayout>
              <AIBooking />
            </AppLayout>
          </ProtectedRoute>
        }
      />

      <Route
        path="/trips"
        element={
          <ProtectedRoute>
            <AppLayout>
              <Trips />
            </AppLayout>
          </ProtectedRoute>
        }
      />

      <Route
        path="/booking"
        element={
          <ProtectedRoute>
            <AppLayout>
              <Booking />
            </AppLayout>
          </ProtectedRoute>
        }
      />

      <Route
        path="/bookings"
        element={
          <ProtectedRoute>
            <AppLayout>
              <MyBookings />
            </AppLayout>
          </ProtectedRoute>
        }
      />

      <Route
        path="/queue"
        element={
          <ProtectedRoute>
            <AppLayout>
              <Queue />
            </AppLayout>
          </ProtectedRoute>
        }
      />

      <Route
        path="/profile"
        element={
          <ProtectedRoute>
            <AppLayout>
              <Profile />
            </AppLayout>
          </ProtectedRoute>
        }
      />

      {/* =========================
          FALLBACK
      ========================= */}

      <Route path="*" element={<Navigate to="/dashboard" replace />} />
    </Routes>
  );
}

export default AppRoutes;
