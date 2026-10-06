import {
  BusFront,
  LayoutDashboard,
  Bot,
  Ticket,
  Users,
  Map,
  UserCircle,
  LogIn,
  LogOut,
  X,
} from "lucide-react";

import { NavLink, useNavigate } from "react-router-dom";

import { logoutUser } from "../../services/authService";
import { useAuth } from "../../contexts/AuthContext";

import "./Sidebar.css";

function Sidebar({ isOpen, onClose }) {
  const navigate = useNavigate();
  const { user } = useAuth();

  async function handleLogout() {
    try {
      await logoutUser();

      onClose();

      navigate("/login", {
        replace: true,
      });
    } catch (error) {
      console.error("Logout failed:", error);
    }
  }

  function handleNavigation() {
    // Close the mobile sidebar after selecting a page.
    onClose();
  }

  return (
    <aside className={`sidebar ${isOpen ? "sidebar-open" : ""}`}>
      {/* Header */}

      <div className="sidebar-header">
        <div className="sidebar-brand">
          <div className="sidebar-logo">
            <BusFront size={21} />
          </div>

          <div>
            <strong>BUSSIN</strong>

            <span>{user ? "Commuter System" : "Guest booking"}</span>
          </div>
        </div>

        <button
          type="button"
          className="sidebar-close"
          onClick={onClose}
          aria-label="Close navigation"
        >
          <X size={20} />
        </button>
      </div>

      {/* Navigation */}

      <nav className="sidebar-navigation">
        <p className="sidebar-section-title">MAIN</p>

        {user && (
          <>
            <NavLink
              to="/dashboard"
              className={({ isActive }) =>
                `sidebar-link ${isActive ? "active" : ""}`
              }
              onClick={handleNavigation}
            >
              <LayoutDashboard size={18} />
              <span>Dashboard</span>
            </NavLink>

            <NavLink
              to="/ai-booking"
              className={({ isActive }) =>
                `sidebar-link ${isActive ? "active" : ""}`
              }
              onClick={handleNavigation}
            >
              <Bot size={18} />
              <span>AI Booking</span>
            </NavLink>
          </>
        )}

        <NavLink
          to="/trips"
          className={({ isActive }) =>
            `sidebar-link ${isActive ? "active" : ""}`
          }
          onClick={handleNavigation}
        >
          <Map size={18} />

          <span>Trips</span>
        </NavLink>

        {user && (
          <>
            <NavLink
              to="/bookings"
              className={({ isActive }) =>
                `sidebar-link ${isActive ? "active" : ""}`
              }
              onClick={handleNavigation}
            >
              <Ticket size={18} />
              <span>My Bookings</span>
            </NavLink>

            <NavLink
              to="/queue"
              className={({ isActive }) =>
                `sidebar-link ${isActive ? "active" : ""}`
              }
              onClick={handleNavigation}
            >
              <Users size={18} />
              <span>Queue</span>
            </NavLink>

            <p className="sidebar-section-title">ACCOUNT</p>

            <NavLink
              to="/profile"
              className={({ isActive }) =>
                `sidebar-link ${isActive ? "active" : ""}`
              }
              onClick={handleNavigation}
            >
              <UserCircle size={18} />
              <span>Profile</span>
            </NavLink>
          </>
        )}
      </nav>

      {/* Footer */}

      <div className="sidebar-footer">
        {user ? (
          <button type="button" className="sidebar-logout" onClick={handleLogout}>
            <LogOut size={18} />
            <span>Sign Out</span>
          </button>
        ) : (
          <button
            type="button"
            className="sidebar-logout"
            onClick={() => navigate("/login")}
          >
            <LogIn size={18} />
            <span>Sign In</span>
          </button>
        )}

        <div className="sidebar-version">BUSSIN v1.0.0</div>
      </div>
    </aside>
  );
}

export default Sidebar;
