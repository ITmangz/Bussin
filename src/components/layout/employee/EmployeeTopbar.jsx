import { Menu } from "lucide-react";

import { useAuth } from "../../../contexts/AuthContext";

function EmployeeTopbar({ onMenuClick }) {
  const { user, profile } = useAuth();

  const displayName =
    profile?.firstName ||
    profile?.displayName ||
    user?.displayName ||
    "Employee";

  const initials = displayName
    .split(" ")
    .filter(Boolean)
    .slice(0, 2)
    .map((part) => part.charAt(0))
    .join("")
    .toUpperCase();

  return (
    <header className="employee-topbar">
      <button
        type="button"
        className="employee-menu-button"
        onClick={onMenuClick}
      >
        <Menu size={22} />
      </button>

      <div className="employee-topbar-spacer" />

      <div className="employee-user">
        <div className="employee-user-info">
          <span className="employee-user-name">{displayName}</span>

          <span className="employee-user-role">Employee</span>
        </div>

        <div className="employee-user-avatar">{initials || "EM"}</div>
      </div>
    </header>
  );
}

export default EmployeeTopbar;
