import { useState } from "react";
import { Outlet } from "react-router-dom";

import Sidebar from "./Sidebar";
import Topbar from "./Topbar";

import "./AppLayout.css";

function AppLayout({ children }) {
  const [sidebarOpen, setSidebarOpen] = useState(false);

  function closeSidebar() {
    setSidebarOpen(false);
  }

  return (
    <div className="app-layout">
      <Sidebar isOpen={sidebarOpen} onClose={closeSidebar} />

      {sidebarOpen && (
        <button
          type="button"
          className="sidebar-overlay"
          onClick={closeSidebar}
          aria-label="Close navigation"
        />
      )}

      <div className="app-content">
        <Topbar onMenuClick={() => setSidebarOpen(true)} />

        <main className="app-main">{children ?? <Outlet />}</main>
      </div>
    </div>
  );
}

export default AppLayout;
