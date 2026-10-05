import { useEffect, useState } from "react";
import {
  User,
  Mail,
  Phone,
  MapPin,
  Shield,
  CalendarDays,
  Edit3,
  Save,
  X,
  LogOut,
} from "lucide-react";

import { useAuth } from "../contexts/AuthContext";
import { logoutUser } from "../services/authService";
import {
  getCurrentUserProfile,
  updateCurrentUserProfile,
} from "../services/userService";

import AppCard from "../components/ui/AppCard";
import AppButton from "../components/ui/AppButton";

import "./Profile.css";

function Profile() {
  const { user } = useAuth();

  const [profile, setProfile] = useState(null);

  const [formData, setFormData] = useState({
    firstName: "",
    middleName: "",
    lastName: "",
    contactNumber: "",
    address: "",
  });

  const [editing, setEditing] = useState(false);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  useEffect(() => {
    loadProfile();
  }, []);

  async function loadProfile() {
    try {
      setLoading(true);
      setError("");

      const data = await getCurrentUserProfile();

      setProfile(data);

      setFormData({
        firstName: data.firstName || "",
        middleName: data.middleName || "",
        lastName: data.lastName || "",
        contactNumber: data.contactNumber || "",
        address: data.address || "",
      });
    } catch (err) {
      console.error("Failed to load profile:", err);

      setError(err.response?.data?.message || "Unable to load your profile.");
    } finally {
      setLoading(false);
    }
  }

  function handleChange(event) {
    const { name, value } = event.target;

    setFormData((current) => ({
      ...current,
      [name]: value,
    }));
  }

  function handleEdit() {
    if (!profile) {
      return;
    }

    setSuccess("");
    setError("");

    setFormData({
      firstName: profile.firstName || "",
      middleName: profile.middleName || "",
      lastName: profile.lastName || "",
      contactNumber: profile.contactNumber || "",
      address: profile.address || "",
    });

    setEditing(true);
  }

  function handleCancel() {
    if (!profile) {
      return;
    }

    setFormData({
      firstName: profile.firstName || "",
      middleName: profile.middleName || "",
      lastName: profile.lastName || "",
      contactNumber: profile.contactNumber || "",
      address: profile.address || "",
    });

    setEditing(false);
    setError("");
    setSuccess("");
  }

  async function handleSave(event) {
    event.preventDefault();

    try {
      setSaving(true);
      setError("");
      setSuccess("");

      const updatedProfile = await updateCurrentUserProfile({
        firstName: formData.firstName.trim(),
        middleName: formData.middleName.trim(),
        lastName: formData.lastName.trim(),
        contactNumber: formData.contactNumber.trim(),
        address: formData.address.trim(),
      });

      setProfile(updatedProfile);

      setFormData({
        firstName: updatedProfile.firstName || "",
        middleName: updatedProfile.middleName || "",
        lastName: updatedProfile.lastName || "",
        contactNumber: updatedProfile.contactNumber || "",
        address: updatedProfile.address || "",
      });

      setEditing(false);
      setSuccess("Profile updated successfully.");
    } catch (err) {
      console.error("Failed to update profile:", err);

      setError(err.response?.data?.message || "Unable to update your profile.");
    } finally {
      setSaving(false);
    }
  }

  async function handleLogout() {
    try {
      await logoutUser();
    } catch (err) {
      console.error("Logout failed:", err);
    }
  }

  const email = user?.email || profile?.email || "No email available";

  const provider =
    user?.providerData?.[0]?.providerId === "google.com"
      ? "Google"
      : "Email & Password";

  const fullName = [formData.firstName, formData.middleName, formData.lastName]
    .filter(Boolean)
    .join(" ");

  const role = profile?.role || "COMMUTER";

  if (loading) {
    return (
      <div className="profile-page">
        <AppCard className="profile-card">
          <div className="profile-form">Loading your profile...</div>
        </AppCard>
      </div>
    );
  }

  if (error && !profile) {
    return (
      <div className="profile-page">
        <div className="profile-header">
          <div>
            <span className="profile-eyebrow">ACCOUNT</span>

            <h1>My Profile</h1>

            <p>Manage your personal information and account details.</p>
          </div>
        </div>

        <AppCard className="profile-card">
          <div className="profile-form">
            <p>{error}</p>

            <AppButton onClick={loadProfile}>Try Again</AppButton>
          </div>
        </AppCard>
      </div>
    );
  }

  return (
    <div className="profile-page">
      {/* Header */}

      <div className="profile-header">
        <div>
          <span className="profile-eyebrow">ACCOUNT</span>

          <h1>My Profile</h1>

          <p>Manage your personal information and account details.</p>
        </div>

        {!editing && (
          <AppButton variant="secondary" onClick={handleEdit}>
            <Edit3 size={15} />
            Edit Profile
          </AppButton>
        )}
      </div>

      {/* Messages */}

      {success && (
        <AppCard className="profile-card">
          <div className="account-row">
            <div className="account-row-icon">
              <Shield size={16} />
            </div>

            <div className="account-row-content">
              <span>Success</span>

              <strong>{success}</strong>
            </div>
          </div>
        </AppCard>
      )}

      {error && (
        <AppCard className="profile-card">
          <div className="account-row">
            <div className="account-row-icon">
              <Shield size={16} />
            </div>

            <div className="account-row-content">
              <span>Error</span>

              <strong>{error}</strong>
            </div>
          </div>
        </AppCard>
      )}

      {/* Main grid */}

      <div className="profile-grid">
        {/* Main column */}

        <div className="profile-main-column">
          <AppCard className="profile-card">
            <div className="profile-card-header">
              <div>
                <h2>Personal Information</h2>

                <p>
                  Your personal information associated with your BUSSIN account.
                </p>
              </div>

              <div className="profile-card-icon">
                <User size={18} />
              </div>
            </div>

            <form className="profile-form" onSubmit={handleSave}>
              <div className="profile-form-grid">
                {/* First name */}

                <div className="profile-field">
                  <label htmlFor="firstName">First Name</label>

                  <div className="profile-input-wrapper">
                    <User size={15} />

                    <input
                      id="firstName"
                      name="firstName"
                      type="text"
                      value={formData.firstName}
                      onChange={handleChange}
                      disabled={!editing}
                      required
                    />
                  </div>
                </div>

                {/* Middle name */}

                <div className="profile-field">
                  <label htmlFor="middleName">Middle Name</label>

                  <div className="profile-input-wrapper">
                    <User size={15} />

                    <input
                      id="middleName"
                      name="middleName"
                      type="text"
                      value={formData.middleName}
                      onChange={handleChange}
                      disabled={!editing}
                    />
                  </div>
                </div>

                {/* Last name */}

                <div className="profile-field">
                  <label htmlFor="lastName">Last Name</label>

                  <div className="profile-input-wrapper">
                    <User size={15} />

                    <input
                      id="lastName"
                      name="lastName"
                      type="text"
                      value={formData.lastName}
                      onChange={handleChange}
                      disabled={!editing}
                      required
                    />
                  </div>
                </div>

                {/* Email */}

                <div className="profile-field">
                  <label htmlFor="email">Email Address</label>

                  <div className="profile-input-wrapper">
                    <Mail size={15} />

                    <input id="email" type="email" value={email} disabled />
                  </div>
                </div>

                {/* Contact */}

                <div className="profile-field">
                  <label htmlFor="contactNumber">Contact Number</label>

                  <div className="profile-input-wrapper">
                    <Phone size={15} />

                    <input
                      id="contactNumber"
                      name="contactNumber"
                      type="text"
                      value={formData.contactNumber}
                      onChange={handleChange}
                      disabled={!editing}
                    />
                  </div>
                </div>

                {/* Address */}

                <div className="profile-field profile-field-full">
                  <label htmlFor="address">Address</label>

                  <div className="profile-input-wrapper">
                    <MapPin size={15} />

                    <input
                      id="address"
                      name="address"
                      type="text"
                      value={formData.address}
                      onChange={handleChange}
                      disabled={!editing}
                    />
                  </div>
                </div>
              </div>

              {/* Actions */}

              {editing && (
                <div className="profile-form-actions">
                  <AppButton
                    type="button"
                    variant="secondary"
                    onClick={handleCancel}
                    disabled={saving}
                  >
                    <X size={15} />
                    Cancel
                  </AppButton>

                  <AppButton type="submit" disabled={saving}>
                    <Save size={15} />

                    {saving ? "Saving..." : "Save Changes"}
                  </AppButton>
                </div>
              )}
            </form>
          </AppCard>

          {/* Account information */}

          <AppCard className="profile-card">
            <div className="profile-card-header">
              <div>
                <h2>Account Information</h2>

                <p>Information about your BUSSIN account.</p>
              </div>

              <div className="profile-card-icon">
                <Shield size={18} />
              </div>
            </div>

            <div className="account-information">
              <div className="account-row">
                <div className="account-row-icon">
                  <Mail size={15} />
                </div>

                <div className="account-row-content">
                  <span>Email Address</span>

                  <strong>{email}</strong>
                </div>
              </div>

              <div className="account-row">
                <div className="account-row-icon">
                  <Shield size={15} />
                </div>

                <div className="account-row-content">
                  <span>Authentication</span>

                  <strong>{provider}</strong>
                </div>
              </div>

              <div className="account-row">
                <div className="account-row-icon">
                  <Shield size={15} />
                </div>

                <div className="account-row-content">
                  <span>Account Role</span>

                  <strong>{role}</strong>
                </div>
              </div>

              <div className="account-row">
                <div className="account-row-icon">
                  <CalendarDays size={15} />
                </div>

                <div className="account-row-content">
                  <span>Member Since</span>

                  <strong>
                    {profile?.createdAt
                      ? new Date(profile.createdAt).toLocaleDateString(
                          "en-US",
                          {
                            year: "numeric",
                            month: "long",
                            day: "numeric",
                          },
                        )
                      : "N/A"}
                  </strong>
                </div>
              </div>
            </div>
          </AppCard>
        </div>

        {/* Side column */}

        <div className="profile-side-column">
          {/* Profile summary */}

          <AppCard className="profile-summary-card">
            <div className="profile-avatar-large">
              {[formData.firstName, formData.lastName]
                .filter(Boolean)
                .map((name) => name.charAt(0).toUpperCase())
                .join("") || "U"}
            </div>

            <h2>{fullName || "BUSSIN User"}</h2>

            <p>{email}</p>

            <div className="profile-summary-divider" />

            <div className="profile-summary-item">
              <Shield size={14} />

              <span>{role}</span>
            </div>

            <div className="profile-summary-item">
              <Mail size={14} />

              <span>{provider}</span>
            </div>

            <div className="profile-summary-item">
              <CalendarDays size={14} />

              <span>
                {profile?.createdAt
                  ? new Date(profile.createdAt).toLocaleDateString("en-US", {
                      year: "numeric",
                      month: "short",
                      day: "numeric",
                    })
                  : "N/A"}
              </span>
            </div>
          </AppCard>

          {/* Sign out */}

          <AppCard className="profile-danger-card">
            <div className="danger-header">
              <div className="danger-icon">
                <LogOut size={16} />
              </div>

              <div>
                <h3>Sign Out</h3>

                <p>Sign out of your BUSSIN account on this device.</p>
              </div>
            </div>

            <AppButton
              variant="danger"
              className="profile-logout-button"
              onClick={handleLogout}
            >
              <LogOut size={15} />
              Sign Out
            </AppButton>
          </AppCard>
        </div>
      </div>
    </div>
  );
}

export default Profile;
