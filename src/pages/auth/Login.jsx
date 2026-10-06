import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Eye, EyeOff } from "lucide-react";

import AuthLayout from "../../components/auth/AuthLayout";
import { loginWithEmail, loginWithGoogle, logoutUser } from "../../services/authService";

import { getCurrentUserProfile } from "../../services/userService";

import "./Login.css";

function Login() {
  const navigate = useNavigate();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const [showPassword, setShowPassword] = useState(false);

  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  function navigateByRole(profile) {
    console.log("BUSSIN LOGIN PROFILE:", profile);
    console.log("BUSSIN LOGIN ROLE:", profile?.role);

    switch (profile?.role) {
      case "ADMIN":
        navigate("/admin/dashboard");
        break;

      case "EMPLOYEE":
        navigate("/employee/dashboard");
        break;

      case "COMMUTER":
        navigate("/dashboard");
        break;

      default:
        console.error("Unknown BUSSIN user role:", profile?.role);

        setError("Your account does not have a valid BUSSIN role.");
    }
  }

  async function handleSubmit(event) {
    event.preventDefault();

    setError("");
    setLoading(true);

    try {
      await loginWithEmail(email, password);

      const profile = await getCurrentUserProfile();

      navigateByRole(profile);
    } catch (error) {
      console.error(error);

      switch (error.code) {
        case "auth/invalid-credential":
          setError("Invalid email or password.");
          break;

        case "auth/user-not-found":
          setError("No account found with this email.");
          break;

        case "auth/wrong-password":
          setError("Incorrect password.");
          break;

        case "auth/invalid-email":
          setError("Please enter a valid email address.");
          break;

        default:
          setError("Unable to sign in. Please try again.");
      }
    } finally {
      setLoading(false);
    }
  }

  async function handleGoogleLogin() {
    setError("");
    setLoading(true);

    try {
      await loginWithGoogle();

      const profile = await getCurrentUserProfile();

      navigateByRole(profile);
    } catch (error) {
      console.error(error);

      setError("Google sign-in failed. Please try again.");
    } finally {
      setLoading(false);
    }
  }

  async function handleGuestBooking() {
    setError("");
    setLoading(true);

    try {
      await logoutUser();
      navigate("/trips", { replace: true });
    } catch (error) {
      console.error("Unable to start a guest session:", error);
      setError("Unable to continue as a guest. Please sign out and try again.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <AuthLayout>
      <div className="login-page">
        <div className="auth-header">
          <h2>Welcome back</h2>

          <p>Sign in to your commuter account</p>
        </div>

        {error && <div className="auth-error">{error}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="login-email">Email Address</label>

            <input
              id="login-email"
              type="email"
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              placeholder="Enter your email"
              autoComplete="email"
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="login-password">Password</label>

            <div className="password-input">
              <input
                id="login-password"
                type={showPassword ? "text" : "password"}
                value={password}
                onChange={(event) => setPassword(event.target.value)}
                placeholder="Enter your password"
                autoComplete="current-password"
                required
              />

              <button
                type="button"
                className="password-toggle"
                onClick={() => setShowPassword((previous) => !previous)}
                aria-label={showPassword ? "Hide password" : "Show password"}
              >
                {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
              </button>
            </div>
          </div>

          <button type="submit" className="auth-submit" disabled={loading}>
            {loading ? "Signing in..." : "Sign In"}
          </button>
        </form>

        <div className="auth-divider">
          <span>OR</span>
        </div>

        <button
          type="button"
          className="google-button"
          onClick={handleGoogleLogin}
          disabled={loading}
        >
          Continue with Google
        </button>

        <button
          type="button"
          className="guest-booking-button"
          onClick={handleGuestBooking}
          disabled={loading}
        >
          Continue as guest
        </button>
        <p className="guest-booking-note">
          Book manually without an account. Guest bookings won’t appear in commuter history.
        </p>

        <div className="auth-footer">
          <span>Don't have an account?</span>

          <button type="button" onClick={() => navigate("/register")}>
            Create account
          </button>
        </div>
      </div>
    </AuthLayout>
  );
}

export default Login;
