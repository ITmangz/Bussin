import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { ArrowLeft, ArrowRight } from "lucide-react";

import AuthLayout from "../../components/auth/AuthLayout";
import { registerWithEmail } from "../../services/authService";
import { createUserProfile } from "../../services/userService";

import "./Register.css";

function Register() {
  const navigate = useNavigate();

  const [step, setStep] = useState(1);

  const [formData, setFormData] = useState({
    email: "",
    password: "",
    confirmPassword: "",

    firstName: "",
    middleName: "",
    lastName: "",
    contactNumber: "",
    address: "",
  });

  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  function updateField(field, value) {
    setFormData((previous) => ({
      ...previous,
      [field]: value,
    }));
  }

  function handleStepOne(event) {
    event.preventDefault();

    setError("");

    if (!formData.email.trim()) {
      setError("Please enter your email address.");
      return;
    }

    if (!formData.password) {
      setError("Please enter a password.");
      return;
    }

    if (formData.password.length < 6) {
      setError("Password must be at least 6 characters.");
      return;
    }

    if (formData.password !== formData.confirmPassword) {
      setError("Passwords do not match.");
      return;
    }

    setStep(2);
  }

  async function handleRegistration(event) {
    event.preventDefault();

    setError("");

    if (!formData.firstName.trim()) {
      setError("Please enter your first name.");
      return;
    }

    if (!formData.lastName.trim()) {
      setError("Please enter your last name.");
      return;
    }

    if (!formData.contactNumber.trim()) {
      setError("Please enter your contact number.");
      return;
    }

    if (!formData.address.trim()) {
      setError("Please enter your address.");
      return;
    }

    setLoading(true);

    try {
      // 1. Create Firebase account
      const user = await registerWithEmail(formData.email, formData.password);

      // 2. Get Firebase ID token
      const token = await user.getIdToken();

      console.log("Firebase UID:", user.uid);
      console.log("Firebase ID Token:", token);

      // 3. Create BUSSIN user profile through Spring Boot
      const profile = await createUserProfile({
        firstName: formData.firstName.trim(),
        middleName: formData.middleName.trim(),
        lastName: formData.lastName.trim(),
        contactNumber: formData.contactNumber.trim(),
        address: formData.address.trim(),
      });

      console.log("BUSSIN profile created:", profile);

      // 4. Registration completely finished
      navigate("/dashboard");
    } catch (error) {
      console.error("Registration failed:", error);

      console.error("ERROR MESSAGE:", error.message);
      console.error("ERROR CODE:", error.code);
      console.error("ERROR RESPONSE:", error.response);
      console.error("ERROR RESPONSE DATA:", error.response?.data);
      console.error("ERROR STATUS:", error.response?.status);

      // Firebase errors
      switch (error.code) {
        case "auth/email-already-in-use":
          setError("An account with this email already exists.");
          break;

        case "auth/invalid-email":
          setError("Please enter a valid email address.");
          break;

        case "auth/weak-password":
          setError("Password is too weak.");
          break;

        default:
          // Axios / Spring Boot error
          if (error.response) {
            console.error("Backend response:", error.response.data);

            setError(
              error.response.data?.message ||
                "Account was created, but your BUSSIN profile could not be saved.",
            );
          } else {
            setError("Unable to create account. Please try again.");
          }
      }
    } finally {
      setLoading(false);
    }
  }

  return (
    <AuthLayout>
      <div className="register-page">
        <div className="registration-progress">
          <div className={step >= 1 ? "progress-step active" : "progress-step"}>
            <span>1</span>
            <label>Account</label>
          </div>

          <div className="progress-line" />

          <div className={step >= 2 ? "progress-step active" : "progress-step"}>
            <span>2</span>
            <label>Personal</label>
          </div>
        </div>

        {step === 1 && (
          <>
            <div className="auth-header">
              <h2>Create your account</h2>

              <p>Set up your BUSSIN login credentials</p>
            </div>

            {error && <div className="auth-error">{error}</div>}

            <form onSubmit={handleStepOne}>
              <div className="form-group">
                <label htmlFor="register-email">Email Address</label>

                <input
                  id="register-email"
                  type="email"
                  value={formData.email}
                  onChange={(event) => updateField("email", event.target.value)}
                  placeholder="Enter your email"
                  autoComplete="email"
                  required
                />
              </div>

              <div className="form-group">
                <label htmlFor="register-password">Password</label>

                <input
                  id="register-password"
                  type="password"
                  value={formData.password}
                  onChange={(event) =>
                    updateField("password", event.target.value)
                  }
                  placeholder="Create a password"
                  autoComplete="new-password"
                  required
                />
              </div>

              <div className="form-group">
                <label htmlFor="confirm-password">Confirm Password</label>

                <input
                  id="confirm-password"
                  type="password"
                  value={formData.confirmPassword}
                  onChange={(event) =>
                    updateField("confirmPassword", event.target.value)
                  }
                  placeholder="Confirm your password"
                  autoComplete="new-password"
                  required
                />
              </div>

              <button type="submit" className="auth-submit">
                Continue
                <ArrowRight size={18} />
              </button>
            </form>
          </>
        )}

        {step === 2 && (
          <>
            <div className="auth-header">
              <h2>Personal information</h2>

              <p>Tell us a little about yourself</p>
            </div>

            {error && <div className="auth-error">{error}</div>}

            <form onSubmit={handleRegistration}>
              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="first-name">First Name</label>

                  <input
                    id="first-name"
                    type="text"
                    value={formData.firstName}
                    onChange={(event) =>
                      updateField("firstName", event.target.value)
                    }
                    placeholder="First name"
                    autoComplete="given-name"
                    required
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="middle-name">Middle Name</label>

                  <input
                    id="middle-name"
                    type="text"
                    value={formData.middleName}
                    onChange={(event) =>
                      updateField("middleName", event.target.value)
                    }
                    placeholder="Middle name"
                    autoComplete="additional-name"
                  />
                </div>
              </div>

              <div className="form-group">
                <label htmlFor="last-name">Last Name</label>

                <input
                  id="last-name"
                  type="text"
                  value={formData.lastName}
                  onChange={(event) =>
                    updateField("lastName", event.target.value)
                  }
                  placeholder="Last name"
                  autoComplete="family-name"
                  required
                />
              </div>

              <div className="form-group">
                <label htmlFor="contact-number">Contact Number</label>

                <input
                  id="contact-number"
                  type="tel"
                  value={formData.contactNumber}
                  onChange={(event) =>
                    updateField("contactNumber", event.target.value)
                  }
                  placeholder="09XXXXXXXXX"
                  autoComplete="tel"
                  required
                />
              </div>

              <div className="form-group">
                <label htmlFor="address">Address</label>

                <input
                  id="address"
                  type="text"
                  value={formData.address}
                  onChange={(event) =>
                    updateField("address", event.target.value)
                  }
                  placeholder="Enter your address"
                  autoComplete="street-address"
                  required
                />
              </div>

              <div className="registration-actions">
                <button
                  type="button"
                  className="auth-back"
                  onClick={() => {
                    setError("");
                    setStep(1);
                  }}
                  disabled={loading}
                >
                  <ArrowLeft size={18} />
                  Back
                </button>

                <button
                  type="submit"
                  className="auth-submit"
                  disabled={loading}
                >
                  {loading ? "Creating..." : "Create Account"}
                </button>
              </div>
            </form>
          </>
        )}

        <div className="auth-footer">
          <span>Already have an account?</span>

          <button type="button" onClick={() => navigate("/login")}>
            Sign in
          </button>
        </div>
      </div>
    </AuthLayout>
  );
}

export default Register;
