import {
  ArrowRight,
  CalendarDays,
  CheckCircle2,
  Clock3,
  MapPin,
  Ticket,
  TrendingUp,
} from "lucide-react";

import AppCard from "../components/ui/AppCard";

import "./Dashboard.css";

const upcomingTrip = {
  bookingCode: "BK-8F42A19C",
  origin: "Bacoor",
  destination: "Cebu",
  date: "October 3, 2026",
  time: "8:00 AM",
  busNumber: "ABC-1234",
  seat: "9C",
  status: "CONFIRMED",
};

const recentBookings = [
  {
    id: 1,
    route: "Bacoor → Cebu",
    date: "October 3, 2026",
    seat: "9C",
    status: "CONFIRMED",
    amount: "₱850.00",
  },
  {
    id: 2,
    route: "Manila → Baguio",
    date: "September 20, 2026",
    seat: "12A",
    status: "COMPLETED",
    amount: "₱520.00",
  },
  {
    id: 3,
    route: "Bacoor → Manila",
    date: "September 12, 2026",
    seat: "7B",
    status: "COMPLETED",
    amount: "₱180.00",
  },
];

function Dashboard() {
  return (
    <div className="dashboard-page">
      {/* Page Header */}
      <div className="dashboard-header">
        <div>
          <h1>Good morning, Antonio</h1>

          <p>Here's your commuter overview.</p>
        </div>

        <div className="dashboard-date">
          <CalendarDays size={18} />

          <span>October 2026</span>
        </div>
      </div>

      {/* Statistics */}
      <div className="dashboard-stats">
        <AppCard className="dashboard-stat-card">
          <div className="stat-icon stat-icon-blue">
            <CalendarDays size={21} />
          </div>

          <div className="stat-content">
            <span className="stat-label">Upcoming Trips</span>

            <strong>2</strong>

            <span className="stat-description">Scheduled trips</span>
          </div>
        </AppCard>

        <AppCard className="dashboard-stat-card">
          <div className="stat-icon stat-icon-red">
            <Ticket size={21} />
          </div>

          <div className="stat-content">
            <span className="stat-label">Active Booking</span>

            <strong>1</strong>

            <span className="stat-description">Currently confirmed</span>
          </div>
        </AppCard>

        <AppCard className="dashboard-stat-card">
          <div className="stat-icon stat-icon-green">
            <CheckCircle2 size={21} />
          </div>

          <div className="stat-content">
            <span className="stat-label">Completed Trips</span>

            <strong>12</strong>

            <span className="stat-description">Total completed</span>
          </div>
        </AppCard>

        <AppCard className="dashboard-stat-card">
          <div className="stat-icon stat-icon-purple">
            <TrendingUp size={21} />
          </div>

          <div className="stat-content">
            <span className="stat-label">Total Bookings</span>

            <strong>15</strong>

            <span className="stat-description">All-time bookings</span>
          </div>
        </AppCard>
      </div>

      {/* Main Dashboard Grid */}
      <div className="dashboard-main-grid">
        {/* Upcoming Trip */}
        <AppCard className="upcoming-trip-card">
          <div className="section-header">
            <div>
              <h2>Upcoming Trip</h2>

              <p>Your next scheduled journey</p>
            </div>

            <span className="status-badge status-confirmed">
              {upcomingTrip.status}
            </span>
          </div>

          <div className="trip-route">
            <div className="route-location">
              <span className="route-label">FROM</span>

              <strong>{upcomingTrip.origin}</strong>
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

              <strong>{upcomingTrip.destination}</strong>
            </div>
          </div>

          <div className="trip-details">
            <div className="trip-detail">
              <CalendarDays size={18} />

              <div>
                <span>Date</span>
                <strong>{upcomingTrip.date}</strong>
              </div>
            </div>

            <div className="trip-detail">
              <Clock3 size={18} />

              <div>
                <span>Departure</span>
                <strong>{upcomingTrip.time}</strong>
              </div>
            </div>

            <div className="trip-detail">
              <Ticket size={18} />

              <div>
                <span>Seat</span>
                <strong>{upcomingTrip.seat}</strong>
              </div>
            </div>

            <div className="trip-detail">
              <MapPin size={18} />

              <div>
                <span>Bus</span>
                <strong>{upcomingTrip.busNumber}</strong>
              </div>
            </div>
          </div>

          <div className="booking-code">
            <span>Booking Reference</span>

            <strong>{upcomingTrip.bookingCode}</strong>
          </div>

          <button type="button" className="trip-action-button">
            View Booking
            <ArrowRight size={17} />
          </button>
        </AppCard>

        {/* Quick Actions */}
        <AppCard className="quick-actions-card">
          <div className="section-header">
            <div>
              <h2>Quick Actions</h2>

              <p>Common commuter actions</p>
            </div>
          </div>

          <div className="quick-actions">
            <button type="button" className="quick-action quick-action-primary">
              <div className="quick-action-icon">
                <Ticket size={21} />
              </div>

              <div>
                <strong>Book a Trip</strong>

                <span>Find available trips</span>
              </div>

              <ArrowRight size={18} />
            </button>

            <button type="button" className="quick-action">
              <div className="quick-action-icon">
                <CalendarDays size={21} />
              </div>

              <div>
                <strong>My Bookings</strong>

                <span>View your reservations</span>
              </div>

              <ArrowRight size={18} />
            </button>

            <button type="button" className="quick-action">
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

      {/* Recent Bookings */}
      <AppCard className="recent-bookings-card">
        <div className="section-header">
          <div>
            <h2>Recent Bookings</h2>

            <p>Your latest booking activity</p>
          </div>

          <button type="button" className="view-all-button">
            View All
            <ArrowRight size={16} />
          </button>
        </div>

        <div className="recent-bookings">
          {recentBookings.map((booking) => (
            <div key={booking.id} className="booking-row">
              <div className="booking-route">
                <div className="booking-route-icon">
                  <MapPin size={18} />
                </div>

                <div>
                  <strong>{booking.route}</strong>

                  <span>{booking.date}</span>
                </div>
              </div>

              <div className="booking-seat">
                <span>Seat</span>
                <strong>{booking.seat}</strong>
              </div>

              <div className="booking-amount">
                <span>Fare</span>
                <strong>{booking.amount}</strong>
              </div>

              <span
                className={
                  booking.status === "CONFIRMED"
                    ? "status-badge status-confirmed"
                    : "status-badge status-completed"
                }
              >
                {booking.status}
              </span>
            </div>
          ))}
        </div>
      </AppCard>
    </div>
  );
}

export default Dashboard;
