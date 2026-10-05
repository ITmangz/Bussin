import { Bell, Menu, Search } from "lucide-react";

import "./Topbar.css";

function Topbar({ onMenuClick }) {
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

          <span className="notification-dot" />
        </button>

        <div className="topbar-divider" />

        <div className="topbar-user">
          <div className="topbar-avatar">A</div>

          <div className="topbar-user-info">
            <strong>Antonio</strong>
            <span>Commuter</span>
          </div>
        </div>
      </div>
    </header>
  );
}

export default Topbar;
