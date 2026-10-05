import { NavLink } from "react-router-dom";
import {
  LayoutDashboard,
  Bus,
  Map,
  Route,
  Ticket,
  ListOrdered,
  Users,
  UserCog,
  BarChart3,
  Settings,
  X,
} from "lucide-react";

function AdminSidebar({ mobileOpen, onClose }) {
  const navigation = [
    {
      label: "Dashboard",
      path: "/admin/dashboard",
      icon: LayoutDashboard,
    },
  ];

  const operations = [
    {
      label: "Buses",
      path: "/admin/buses",
      icon: Bus,
    },
    {
      label: "Routes",
      path: "/admin/routes",
      icon: Map,
    },
    {
      label: "Trips",
      path: "/admin/trips",
      icon: Route,
    },
    {
      label: "Bookings",
      path: "/admin/bookings",
      icon: Ticket,
    },
    {
      label: "Queue",
      path: "/admin/queue",
      icon: ListOrdered,
    },
  ];

  const management = [
    {
      label: "Users",
      path: "/admin/users",
      icon: Users,
    },
    {
      label: "Employees",
      path: "/admin/employees",
      icon: UserCog,
    },
  ];

  const system = [
    {
      label: "Reports",
      path: "/admin/reports",
      icon: BarChart3,
    },
    {
      label: "Settings",
      path: "/admin/settings",
      icon: Settings,
    },
  ];

  const renderLinks = (items) =>
    items.map((item) => {
      const Icon = item.icon;

      return (
        <NavLink
          key={item.path}
          to={item.path}
          onClick={onClose}
          className={({ isActive }) =>
            `admin-sidebar-link ${isActive ? "active" : ""}`
          }
        >
          <Icon size={19} strokeWidth={2} />
          <span>{item.label}</span>
        </NavLink>
      );
    });

  return (
    <>
      {mobileOpen && (
        <div className="admin-sidebar-overlay" onClick={onClose} />
      )}

      <aside className={`admin-sidebar ${mobileOpen ? "mobile-open" : ""}`}>
        <div className="admin-sidebar-header">
          <div className="admin-brand">
            <div className="admin-brand-mark">B</div>

            <div>
              <div className="admin-brand-name">BUSSIN</div>
              <div className="admin-brand-subtitle">Administration</div>
            </div>
          </div>

          <button
            type="button"
            className="admin-sidebar-close"
            onClick={onClose}
          >
            <X size={20} />
          </button>
        </div>

        <nav className="admin-sidebar-navigation">
          <div className="admin-sidebar-section">{renderLinks(navigation)}</div>

          <div className="admin-sidebar-section">
            <div className="admin-sidebar-section-title">Operations</div>

            {renderLinks(operations)}
          </div>

          <div className="admin-sidebar-section">
            <div className="admin-sidebar-section-title">Management</div>

            {renderLinks(management)}
          </div>

          <div className="admin-sidebar-section">
            <div className="admin-sidebar-section-title">System</div>

            {renderLinks(system)}
          </div>
        </nav>
      </aside>
    </>
  );
}

export default AdminSidebar;
