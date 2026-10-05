import { BusFront } from "lucide-react";
import "./AuthLayout.css";

function AuthLayout({ children }) {
  return (
    <div className="auth-page">
      <div className="auth-card">
        <div className="auth-brand">
          <div className="auth-logo">
            <BusFront size={28} />
          </div>

          <h1>BUSSIN</h1>

          <p>Bus Commuter Queue Management System</p>
        </div>

        {children}
      </div>
    </div>
  );
}

export default AuthLayout;
