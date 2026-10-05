import "./AppCard.css";

function AppCard({ children, className = "" }) {
  return <div className={`app-card ${className}`}>{children}</div>;
}

export default AppCard;
