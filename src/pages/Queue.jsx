import {
  ArrowRight,
  BusFront,
  CheckCircle2,
  Clock3,
  Hash,
  MapPin,
  RefreshCw,
  Ticket,
  Users,
} from "lucide-react";
import { useState } from "react";

import AppButton from "../components/ui/AppButton";
import AppCard from "../components/ui/AppCard";

import "./Queue.css";

const queueSteps = [
  {
    number: 1,
    title: "Booking Confirmed",
    description: "Your trip has been confirmed.",
    completed: true,
  },
  {
    number: 2,
    title: "Waiting for Boarding",
    description: "Stay near the boarding area.",
    completed: true,
  },
  {
    number: 3,
    title: "Queue",
    description: "Wait for your queue number to be called.",
    completed: false,
    current: true,
  },
  {
    number: 4,
    title: "Boarding",
    description: "Proceed to your assigned bus.",
    completed: false,
  },
];

function Queue() {
  const [lastUpdated, setLastUpdated] = useState("Just now");

  const [refreshing, setRefreshing] = useState(false);

  function refreshQueue() {
    setRefreshing(true);

    window.setTimeout(() => {
      setRefreshing(false);
      setLastUpdated("Just now");
    }, 800);
  }

  return (
    <section className="queue-page">
      <header className="queue-header">
        <div>
          <h1>Queue</h1>

          <p>Track your boarding queue and estimated waiting time.</p>
        </div>

        <AppButton
          variant="secondary"
          onClick={refreshQueue}
          disabled={refreshing}
          className="queue-refresh-button"
        >
          <RefreshCw
            size={15}
            className={refreshing ? "queue-refreshing" : ""}
          />
          Refresh
        </AppButton>
      </header>

      <div className="queue-update">
        <span>Last updated: {lastUpdated}</span>

        <span className="queue-live">
          <span />
          Live Queue
        </span>
      </div>

      <div className="queue-overview">
        <AppCard className="queue-position-card">
          <div className="queue-position-label">
            <Hash size={15} />
            Your Queue Number
          </div>

          <strong className="queue-number">24</strong>

          <span className="queue-position-description">
            Please remain near the boarding area.
          </span>
        </AppCard>

        <AppCard className="queue-wait-card">
          <div className="queue-wait-icon">
            <Clock3 size={21} />
          </div>

          <div>
            <span>Estimated Wait</span>

            <strong>~18 min</strong>

            <small>Based on current queue</small>
          </div>
        </AppCard>

        <AppCard className="queue-current-card">
          <div className="queue-current-icon">
            <Users size={20} />
          </div>

          <div>
            <span>Now Boarding</span>

            <strong>#18</strong>

            <small>6 people ahead</small>
          </div>
        </AppCard>
      </div>

      <div className="queue-content-grid">
        <AppCard className="queue-trip-card">
          <div className="queue-section-header">
            <div>
              <span>Current Trip</span>

              <h2>Trip #4</h2>
            </div>

            <span className="queue-status">SCHEDULED</span>
          </div>

          <div className="queue-route">
            <div className="queue-location">
              <span>Departure</span>

              <strong>Bacoor</strong>

              <small>8:00 AM</small>
            </div>

            <div className="queue-route-line">
              <div />

              <ArrowRight size={17} />

              <div />
            </div>

            <div className="queue-location destination">
              <span>Destination</span>

              <strong>Cebu</strong>

              <small>October 3, 2026</small>
            </div>
          </div>

          <div className="queue-trip-details">
            <div>
              <BusFront size={15} />

              <span>ABC-1234</span>
            </div>

            <div>
              <Ticket size={15} />

              <span>Seat 9C</span>
            </div>

            <div>
              <Hash size={15} />

              <span>BK-8F42A19C</span>
            </div>
          </div>
        </AppCard>

        <AppCard className="queue-progress-card">
          <div className="queue-section-header">
            <div>
              <span>Boarding Progress</span>

              <h2>Your Status</h2>
            </div>

            <CheckCircle2 size={19} className="queue-progress-icon" />
          </div>

          <div className="queue-progress">
            {queueSteps.map((step, index) => (
              <div
                key={step.number}
                className={`queue-step ${step.completed ? "completed" : ""} ${
                  step.current ? "current" : ""
                }`}
              >
                <div className="queue-step-indicator">
                  {step.completed ? (
                    <CheckCircle2 size={16} />
                  ) : (
                    <span>{step.number}</span>
                  )}
                </div>

                <div className="queue-step-content">
                  <strong>{step.title}</strong>

                  <span>{step.description}</span>
                </div>

                {index < queueSteps.length - 1 && (
                  <div className="queue-step-line" />
                )}
              </div>
            ))}
          </div>
        </AppCard>
      </div>

      <AppCard className="queue-instructions">
        <div className="queue-instructions-icon">
          <MapPin size={19} />
        </div>

        <div>
          <h3>Boarding Instructions</h3>

          <p>
            Please arrive at the boarding area at least 15 minutes before
            departure. Keep your booking reference and valid identification
            ready.
          </p>
        </div>
      </AppCard>
    </section>
  );
}

export default Queue;
