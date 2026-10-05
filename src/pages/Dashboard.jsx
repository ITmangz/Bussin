import {
  ArrowRight,
  CalendarDays,
  CheckCircle2,
  Clock3,
  MapPin,
  Ticket,
  TrendingUp,
} from "lucide-react";
import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";

import AppButton from "../components/ui/AppButton";
import AppCard from "../components/ui/AppCard";
import { useAuth } from "../contexts/AuthContext";
import { getMyBookings } from "../services/bookingService";

import "./Dashboard.css";

function Dashboard() {
  const navigate = useNavigate();
  const { user } = useAuth();

  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    loadBookings();
  }, []);

  async function loadBookings() {
    try {
      setLoading(true);
      setError("");

      const response = await getMyBookings();
      setBookings(Array.isArray(response) ? response : []);
    } catch (err) {
      console.error("Failed to load dashboard bookings:", err);
      setError(
        err.response?.data?.message ||
          "Unable to load your dashboard data from the server.",
      );
    } finally {
      setLoading(false);
    }
  }

  const normalizedBookings = useMemo(
    () =>
      bookings.map((booking) => ({
        ...booking,
        departureDate: booking.scheduledDeparture
          ? new Date(booking.scheduledDeparture)
          : null,
      })),
    [bookings],
  );

  const upcomingBookings = normalizedBookings
    .filter(
      (booking) =>
        booking.departureDate &&
        booking.departureDate.getTime() >= Date.now() &&
        booking.status !== "CANCELLED",
    )
    .sort((a, b) => a.departureDate - b.departureDate);

  const recentBookings = [...normalizedBookings]
    .sort((a, b) => {
      const aDate = new Date(a.scheduledDeparture || a.createdAt || 0);
      const bDate = new Date(b.scheduledDeparture || b.createdAt || 0);
      return bDate - aDate;
    })
    .slice(0, 5);

  const completedTrips = normalizedBookings.filter(
    (booking) => booking.status === "COMPLETED",
  ).length;

  const activeBookings = normalizedBookings.filter(
    (booking) =>
      booking.status === "CONFIRMED" || booking.status === "SCHEDULED",
  ).length;

  const firstName =
    user?.displayName?.trim()?.split(/\s+/)[0] ||
    user?.email?.split("@")[0] ||
    "Commuter";

  const now = new Date();
  const greeting =
    now.getHours() < 12
      ? "Good morning"
      : now.getHours() < 18
        ? "Good afternoon"
        : "Good evening";

  function formatDate(dateTime) {
    if (!dateTime) return "N/A";

    return new Date(dateTime).toLocaleDateString("en-US", {
      year: "numeric",
      month: "long",
      day: "numeric",
    });
  }

  function formatTime(dateTime) {
    if (!dateTime) return "N/A";

    return new Date(dateTime).toLocaleTimeString("en-US", {
      hour: "numeric",
      minute: "2-digit",
    });
  }

  function formatFare(fare) {
    return Number(fare || 0).toLocaleString("en-PH", {
      minimumFractionDigits: 2,
    });
  }

  function getStatusClass(status) {
    return status === "CONFIRMED"
      ? "status-confirmed"
      : status === "COMPLETED"
        ? "status-completed"
        : "";
  }

  const upcomingTrip = upcomingBookings[0];

  return (
    <div className="dashboard-page">
      <div className="dashboard-header">
        <div>
          <h1>
            {greeting}, {firstName}
          </h1>

          <p>Here's your commuter overview.</p>
        </div>

        <div className="dashboard-date">
          <CalendarDays size={18} />

          <span>
            {now.toLocaleDateString("en-US", {
              month: "long",
              year: "numeric",
            })}
          </span>
        </div>
      </div>

      {error && (
        <AppCard className="recent-bookings-card">
          <div className="section-header">
            <div>
              <h2>Unable to load dashboard data</h2>
              <p>{error}</p>
            </div>

            <AppButton variant="secondary" onClick={loadBookings}>
              Retry
            </AppButton>
          </div>
        </AppCard>
      )}

      <div className="dashboard-stats">
        <AppCard className="dashboard-stat-card">
          <div className="stat-icon stat-icon-blue">
            <CalendarDays size={21} />
          </div>

          <div className="stat-content">
            <span className="stat-label">Upcoming Trips</span>
            <strong>{loading ? "—" : upcomingBookings.length}</strong>
            <span className="stat-description">Scheduled trips</span>
          </div>
        </AppCard>

        <AppCard className="dashboard-stat-card">
          <div className="stat-icon stat-icon-red">
            <Ticket size={21} />
          </div>

          <div className="stat-content">
            <span className="stat-label">Active Booking</span>
            <strong>{loading ? "—" : activeBookings}</strong>
            <span className="stat-description">Current active bookings</span>
          </div>
        </AppCard>

        <AppCard className="dashboard-stat-card">
          <div className="stat-icon stat-icon-green">
            <CheckCircle2 size={21} />
          </div>

          <div className="stat-content">
            <span className="stat-label">Completed Trips</span>
            <strong>{loading ? "—" : completedTrips}</strong>
            <span className="stat-description">Completed bookings</span>
          </div>
        </AppCard>

        <AppCard className="dashboard-stat-card">
          <div className="stat-icon stat-icon-purple">
            <TrendingUp size={21} />
          </div>

          <div className="stat-content">
            <span className="stat-label">Total Bookings</span>
            <strong>{loading ? "—" : bookings.length}</strong>
            <span className="stat-description">All-time bookings</span>
          </div>
        </AppCard>
      </div>

      <div className="dashboard-main-grid">
        <AppCard className="upcoming-trip-card">
          <div className="section-header">
            <div>
              <h2>Upcoming Trip</h2>
              <p>Your next scheduled journey</p>
            </div>

            {upcomingTrip && (
              <span
                className={`status-badge ${getStatusClass(
                  upcomingTrip.status,
                )}`}
              >
                {upcomingTrip.status}
              </span>
            )}
          </div>

          {upcomingTrip ? (
            <>
              <div className="trip-route">
                <div className="route-location">
                  <span className="route-label">FROM</span>
                  <strong>{upcomingTrip.origin || "N/A"}</strong>
                </div>

                <div className="route-line">
                  <div className="route-dot" />
                  <div className="route-connector" />
                  <div className="route-arrow">
                    <ArrowRight size={18} />
                  </div>
                  <div className="route-connector" />
                  <div className="route-dot" />
                </div>

                <div className="route-location route-destination">
                  <span className="route-label">TO</span>
                  <strong>{upcomingTrip.destination || "N/A"}</strong>
                </div>
              </div>

              <div className="trip-details">
                <div className="trip-detail">
                  <CalendarDays size={18} />
                  <div>
                    <span>Date</span>
                    <strong>{formatDate(upcomingTrip.scheduledDeparture)}</strong>
                  </div>
                </div>

                <div className="trip-detail">
                  <Clock3 size={18} />
                  <div>
                    <span>Departure</span>
                    <strong>{formatTime(upcomingTrip.scheduledDeparture)}</strong>
                  </div>
                </div>

                <div className="trip-detail">
                  <Ticket size={18} />
                  <div>
                    <span>Seat</span>
                    <strong>{upcomingTrip.seatNumber || "N/A"}</strong>
                  </div>
                </div>

                <div className="trip-detail">
                  <MapPin size={18} />
                  <div>
                    <span>Bus</span>
                    <strong>{upcomingTrip.busPlateNumber || "N/A"}</strong>
                  </div>
                </div>
              </div>

              <div className="booking-code">
                <span>Booking Reference</span>
                <strong>{upcomingTrip.bookingReference || "N/A"}</strong>
              </div>

              <button
                type="button"
                className="trip-action-button"
                onClick={() => navigate("/bookings")}
              >
                View Booking
                <ArrowRight size={17} />
              </button>
            </>
          ) : (
            <div className="recent-bookings">
              <div className="booking-row">
                <div className="booking-route">
                  <div className="booking-route-icon">
                    <Ticket size={18} />
                  </div>

                  <div>
                    <strong>
                      {loading ? "Loading your trips..." : "No upcoming trips"}
                    </strong>
                    <span>
                      {loading
                        ? "Please wait while we load your bookings."
                        : "Book a trip to see your next journey here."}
                    </span>
                  </div>
                </div>

                {!loading && (
                  <button
                    type="button"
                    className="view-all-button"
                    onClick={() => navigate("/trips")}
                  >
                    Find Trips
                    <ArrowRight size={16} />
                  </button>
                )}
              </div>
            </div>
          )}
        </AppCard>

        <AppCard className="quick-actions-card">
          <div className="section-header">
            <div>
              <h2>Quick Actions</h2>
              <p>Common commuter actions</p>
            </div>
          </div>

          <div className="quick-actions">
            <button
              type="button"
              className="quick-action quick-action-primary"
              onClick={() => navigate("/trips")}
            >
              <div className="quick-action-icon">
                <Ticket size={21} />
              </div>

              <div>
                <strong>Book a Trip</strong>
                <span>Find available trips</span>
              </div>

              <ArrowRight size={18} />
            </button>

            <button
              type="button"
              className="quick-action"
              onClick={() => navigate("/bookings")}
            >
              <div className="quick-action-icon">
                <CalendarDays size={21} />
              </div>

              <div>
                <strong>My Bookings</strong>
                <span>View your reservations</span>
              </div>

              <ArrowRight size={18} />
            </button>

            <button
              type="button"
              className="quick-action"
              onClick={() => navigate("/queue")}
            >
              <div className="quick-action-icon">
                <Clock3 size={21} />
              </div>

              <div>
                <strong>Check Queue</strong>
                <span>View your queue status</span>
              </div>

              <ArrowRight size={18} />
            </button>
          </div>
        </AppCard>
      </div>

      <AppCard className="recent-bookings-card">
        <div className="section-header">
          <div>
            <h2>Recent Bookings</h2>
            <p>Your latest booking activity</p>
          </div>

          <button
            type="button"
            className="view-all-button"
            onClick={() => navigate("/bookings")}
          >
            View All
            <ArrowRight size={16} />
          </button>
        </div>

        {recentBookings.length > 0 ? (
          <div className="recent-bookings">
            {recentBookings.map((booking) => (
              <div key={booking.id} className="booking-row">
                <div className="booking-route">
                  <div className="booking-route-icon">
                    <MapPin size={18} />
                  </div>

                  <div>
                    <strong>
                      {booking.origin || "N/A"} → {booking.destination || "N/A"}
                    </strong>

                    <span>{formatDate(booking.scheduledDeparture)}</span>
                  </div>
                </div>

                <div className="booking-seat">
                  <span>Seat</span>
                  <strong>{booking.seatNumber || "N/A"}</strong>
                </div>

                <div className="booking-amount">
                  <span>Fare</span>
                  <strong>₱{formatFare(booking.fare)}</strong>
                </div>

                <span
                  className={`status-badge ${getStatusClass(booking.status)}`}
                >
                  {booking.status || "N/A"}
                </span>
              </div>
            ))}
          </div>
        ) : (
          <div className="recent-bookings">
            <div className="booking-row">
              <div className="booking-route">
                <div className="booking-route-icon">
                  <Ticket size={18} />
                </div>

                <div>
                  <strong>
                    {loading ? "Loading bookings..." : "No bookings yet"}
                  </strong>

                  <span>
                    {loading
                      ? "Please wait while we load your booking activity."
                      : "Your booking history will appear here."}
                  </span>
                </div>
              </div>

              {!loading && (
                <button
                  type="button"
                  className="view-all-button"
                  onClick={() => navigate("/trips")}
                >
                  Find Trips
                  <ArrowRight size={16} />
                </button>
              )}
            </div>
          </div>
        )}
      </AppCard>
    </div>
  );
}

export default Dashboard;
