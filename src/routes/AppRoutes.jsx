import { Routes, Route, Navigate } from "react-router-dom";

import AppLayout from "../components/layout/AppLayout";
import ProtectedRoute from "../components/auth/ProtectedRoute";

import Login from "../pages/auth/Login";
import Register from "../pages/auth/Register";

import Dashboard from "../pages/Dashboard";
import AIBooking from "../pages/AIBooking";
import MyBookings from "../pages/MyBookings";
import Queue from "../pages/Queue";
import Trips from "../pages/Trips";
import Profile from "../pages/Profile";
import Booking from "../pages/Booking";

function ProtectedLayout() {
  return (
    <ProtectedRoute>
      <AppLayout>
        <Routes>
          <Route path="/dashboard" element={<Dashboard />} />

          <Route path="/ai-booking" element={<AIBooking />} />

          <Route path="/bookings" element={<MyBookings />} />

          <Route path="/queue" element={<Queue />} />

          <Route path="/trips" element={<Trips />} />

          <Route path="/booking" element={<Booking />} />

          <Route path="/profile" element={<Profile />} />

          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </AppLayout>
    </ProtectedRoute>
  );
}

function AppRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />

      <Route path="/register" element={<Register />} />

      <Route path="/*" element={<ProtectedLayout />} />
    </Routes>
  );
}

export default AppRoutes;
