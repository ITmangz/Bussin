import { Bell, Menu, Search } from "lucide-react";
import { useAuth } from "../../contexts/AuthContext";

import "./Topbar.css";

function Topbar({ onMenuClick }) {
  const { user } = useAuth();

  const displayName =
    user?.displayName?.trim() ||
    user?.email?.split("@")[0] ||
    "Guest";

  const initials = displayName
    .split(/\s+/)
    .filter(Boolean)
    .slice(0, 2)
    .map((part) => part.charAt(0).toUpperCase())
    .join("");

  return (
    <header className="topbar">
      <button
        type="button"
        className="mobile-menu-button"
        onClick={onMenuClick}
        aria-label="Open navigation"
      >
        <Menu size={21} />
      </button>

      <div className="topbar-search">
        <Search size={18} />
        <input type="search" placeholder="Search..." aria-label="Search" />
      </div>

      <div className="topbar-actions">
        <button
          type="button"
          className="topbar-icon-button"
          aria-label="Notifications"
        >
          <Bell size={19} />
        </button>

        <div className="topbar-divider" />

        <div className="topbar-user">
          <div className="topbar-avatar">{initials || "U"}</div>

          <div className="topbar-user-info">
            <strong>{displayName}</strong>
            <span>{user ? "Commuter" : "Guest booking"}</span>
          </div>
        </div>
      </div>
    </header>
  );
}

export default Topbar;
