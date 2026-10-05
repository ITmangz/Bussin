import "./AppButton.css";

function AppButton({
  children,
  variant = "primary",
  type = "button",
  className = "",
  onClick,
  disabled = false,
}) {
  return (
    <button
      type={type}
      className={`app-button app-button-${variant} ${className}`}
      onClick={onClick}
      disabled={disabled}
    >
      {children}
    </button>
  );
}

export default AppButton;
