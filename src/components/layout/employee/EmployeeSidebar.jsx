import { NavLink, useNavigate } from "react-router-dom";

import { logoutUser } from "../../../services/authService";
import {
  LayoutDashboard,
  Route,
  Ticket,
  ListOrdered,
  UserCheck,
  LogOut,
} from "lucide-react";

function EmployeeSidebar({ mobileOpen, onClose }) {
  const navigate = useNavigate();

  async function handleLogout() {
    try {
      await logoutUser();
      onClose();
      navigate("/login", { replace: true });
    } catch (error) {
      console.error("Logout failed:", error);
    }
  }

  const links = [
    {
      label: "Dashboard",
      path: "/employee/dashboard",
      icon: LayoutDashboard,
    },
    {
      label: "My Trips",
      path: "/employee/trips",
      icon: Route,
    },
    {
      label: "Bookings",
      path: "/employee/bookings",
      icon: Ticket,
    },
    {
      label: "Queue",
      path: "/employee/queue",
      icon: ListOrdered,
    },
    {
      label: "Boarding",
      path: "/employee/boarding",
      icon: UserCheck,
    },
  ];

  return (
    <>
      {mobileOpen && (
        <div className="employee-sidebar-overlay" onClick={onClose} />
      )}

      <aside className={`employee-sidebar ${mobileOpen ? "mobile-open" : ""}`}>
        <div className="employee-sidebar-header">
          <div className="employee-brand">
            <div className="employee-brand-mark">B</div>

            <div>
              <div className="employee-brand-name">BUSSIN</div>

              <div className="employee-brand-subtitle">Operations</div>
            </div>
          </div>
        </div>

        <nav className="employee-sidebar-navigation">
          {links.map((item) => {
            const Icon = item.icon;

            return (
              <NavLink
                key={item.path}
                to={item.path}
                onClick={onClose}
                className={({ isActive }) =>
                  `employee-sidebar-link ${isActive ? "active" : ""}`
                }
              >
                <Icon size={19} />
                <span>{item.label}</span>
              </NavLink>
            );
          })}
        </nav>

        <div className="employee-sidebar-footer">
          <button
            type="button"
            className="employee-sidebar-logout"
            onClick={handleLogout}
          >
            <LogOut size={18} />
            <span>Sign Out</span>
          </button>

          <div className="employee-sidebar-version">BUSSIN v1.0.0</div>
        </div>
      </aside>
    </>
  );
}

export default EmployeeSidebar;
