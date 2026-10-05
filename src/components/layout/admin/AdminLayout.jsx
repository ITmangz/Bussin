import { useState } from "react";

import AdminSidebar from "./AdminSidebar";
import AdminTopbar from "./AdminTopbar";

import "./admin.css";

function AdminLayout({ children }) {
  const [sidebarOpen, setSidebarOpen] = useState(false);

  return (
    <div className="admin-layout">
      <AdminSidebar
        mobileOpen={sidebarOpen}
        onClose={() => setSidebarOpen(false)}
      />

      <div className="admin-main">
        <AdminTopbar onMenuClick={() => setSidebarOpen(true)} />

        <main className="admin-content">{children}</main>
      </div>
    </div>
  );
}

export default AdminLayout;
