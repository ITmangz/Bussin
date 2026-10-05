import { useEffect, useMemo, useState } from "react";
import {
  BusFront,
  CheckCircle2,
  Clock3,
  LoaderCircle,
  Ticket,
  XCircle,
} from "lucide-react";

import AppCard from "../components/ui/AppCard";
import AppButton from "../components/ui/AppButton";
import { getMyBookings } from "../services/bookingService";

import "./Queue.css";

const ACTIVE_QUEUE_STATUSES = ["WAITING", "CALLED", "BOARDED"];

function Queue() {
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  async function loadQueue() {
    try {
      setLoading(true);
      setError("");

      const data = await getMyBookings();
      setBookings(Array.isArray(data) ? data : []);
    } catch (err) {
      console.error("Failed to load commuter queue:", err);
      setError(
        err.response?.data?.message ||
          "Unable to load your queue information.",
      );
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadQueue();
  }, []);

  const queueBookings = useMemo(
    () =>
      bookings.filter(
        (booking) =>
          booking.queueStatus &&
          ACTIVE_QUEUE_STATUSES.includes(booking.queueStatus),
      ),
    [bookings],
  );

  function formatDateTime(value) {
    if (!value) return "N/A";

    const date = new Date(value);

    return date.toLocaleString("en-PH", {
      dateStyle: "medium",
      timeStyle: "short",
    });
  }

  function getStatusIcon(status) {
    if (status === "BOARDED") return <CheckCircle2 size={20} />;
    if (status === "CALLED") return <LoaderCircle size={20} />;
    return <Clock3 size={20} />;
  }

  if (loading) {
    return (
      <section className="queue-page">
        <header className="queue-header">
          <div>
            <h1>Queue</h1>
            <p>Track your boarding queue and status.</p>
          </div>
        </header>

        <AppCard className="queue-instructions">
          <div className="queue-instructions-icon">
            <Clock3 size={19} />
          </div>

          <div>
            <h3>Loading your queue</h3>
            <p>Getting your latest bookings and queue positions.</p>
          </div>
        </AppCard>
      </section>
    );
  }

  if (error) {
    return (
      <section className="queue-page">
        <header className="queue-header">
          <div>
            <h1>Queue</h1>
            <p>Track your boarding queue and status.</p>
          </div>
        </header>

        <AppCard className="queue-instructions">
          <div className="queue-instructions-icon">
            <XCircle size={19} />
          </div>

          <div>
            <h3>Unable to load queue</h3>
            <p>{error}</p>
            <AppButton variant="secondary" onClick={loadQueue}>
              Try Again
            </AppButton>
          </div>
        </AppCard>
      </section>
    );
  }

  return (
    <section className="queue-page">
      <header className="queue-header">
        <div>
          <h1>Queue</h1>
          <p>Track your boarding queue and status.</p>
        </div>
      </header>

      {queueBookings.length === 0 ? (
        <AppCard className="queue-instructions">
          <div className="queue-instructions-icon">
            <BusFront size={19} />
          </div>

          <div>
            <h3>No active queue entries</h3>
            <p>
              Confirm a trip booking and BUSSIN will automatically place you in
              the boarding queue.
            </p>
          </div>
        </AppCard>
      ) : (
        <div className="queue-list">
          {queueBookings.map((booking) => {
            const seats =
              Array.isArray(booking.seatNumbers) &&
              booking.seatNumbers.length > 0
                ? booking.seatNumbers
                : [booking.seatNumber].filter(Boolean);

            return (
              <AppCard className="queue-card" key={booking.id}>
                <div className="queue-card-main">
                  <div className="queue-card-icon">
                    {getStatusIcon(booking.queueStatus)}
                  </div>

                  <div className="queue-card-content">
                    <span>{booking.routeIdentifier || "Trip Queue"}</span>

                    <h2>
                      {booking.origin} → {booking.destination}
                    </h2>

                    <div className="queue-card-meta">
                      <span>
                        <Ticket size={13} />
                        {booking.bookingReference}
                      </span>

                      <span>
                        <BusFront size={13} />
                        Seats {seats.join(", ")}
                      </span>

                      <span>
                        <Clock3 size={13} />
                        {formatDateTime(booking.scheduledDeparture)}
                      </span>
                    </div>
                  </div>
                </div>

                <div className="queue-card-position">
                  <span>Queue Number</span>
                  <strong>
                    {booking.queueNumber ? `#${booking.queueNumber}` : "—"}
                  </strong>
                  <em className={String(booking.queueStatus).toLowerCase()}>
                    {booking.queueStatus}
                  </em>
                </div>
              </AppCard>
            );
          })}
        </div>
      )}
    </section>
  );
}

export default Queue;
