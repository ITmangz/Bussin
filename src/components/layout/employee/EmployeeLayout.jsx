import { useState } from "react";

import EmployeeSidebar from "./EmployeeSidebar";
import EmployeeTopbar from "./EmployeeTopbar";

import "./employee.css";

function EmployeeLayout({ children }) {
  const [sidebarOpen, setSidebarOpen] = useState(false);

  return (
    <div className="employee-layout">
      <EmployeeSidebar
        mobileOpen={sidebarOpen}
        onClose={() => setSidebarOpen(false)}
      />

      <div className="employee-main">
        <EmployeeTopbar onMenuClick={() => setSidebarOpen(true)} />

        <main className="employee-content">{children}</main>
      </div>
    </div>
  );
}

export default EmployeeLayout;
