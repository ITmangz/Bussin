import { Menu } from "lucide-react";

import { useAuth } from "../../../contexts/AuthContext";

function AdminTopbar({ onMenuClick }) {
  const { user, profile } = useAuth();

  const displayName =
    profile?.firstName ||
    profile?.displayName ||
    user?.displayName ||
    "Administrator";

  const email = profile?.email || user?.email || "";

  const initials = displayName
    .split(" ")
    .filter(Boolean)
    .slice(0, 2)
    .map((part) => part.charAt(0))
    .join("")
    .toUpperCase();

  return (
    <header className="admin-topbar">
      <button type="button" className="admin-menu-button" onClick={onMenuClick}>
        <Menu size={22} />
      </button>

      <div className="admin-topbar-spacer" />

      <div className="admin-user">
        <div className="admin-user-info">
          <span className="admin-user-name">{displayName}</span>

          <span className="admin-user-role">Administrator</span>
        </div>

        <div className="admin-user-avatar">{initials || "AD"}</div>
      </div>
    </header>
  );
}

export default AdminTopbar;
