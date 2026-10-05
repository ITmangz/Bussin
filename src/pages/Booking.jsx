import { useEffect, useMemo, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import {
  ArrowLeft,
  ArrowRight,
  BusFront,
  CheckCircle2,
  Clock3,
  Mail,
  Phone,
  User,
  Users,
} from "lucide-react";

import AppButton from "../components/ui/AppButton";
import AppCard from "../components/ui/AppCard";
import { useAuth } from "../contexts/AuthContext";
import { createBooking, getAvailableSeats } from "../services/bookingService";
import "./Booking.css";

const SEATS_PER_ROW = 6;

function formatDateTime(value) {
  if (!value) {
    return "N/A";
  }

  const date = new Date(value);

  return date.toLocaleString("en-PH", {
    dateStyle: "medium",
    timeStyle: "short",
  });
}

function generateSeatRows(capacity) {
  const seats = [];
  const totalSeats = Number(capacity || 0);

  for (let position = 1; position <= totalSeats; position += 1) {
    const row = Math.floor((position - 1) / SEATS_PER_ROW) + 1;
    const seatIndex = (position - 1) % SEATS_PER_ROW;

    seats.push({
      row,
      seat: `${row}${String.fromCharCode("A".charCodeAt(0) + seatIndex)}`,
      side: seatIndex < 3 ? "left" : "right",
    });
  }

  const rows = [];

  for (let index = 0; index < seats.length; index += SEATS_PER_ROW) {
    rows.push(seats.slice(index, index + SEATS_PER_ROW));
  }

  return rows;
}

function Booking() {
  const location = useLocation();
  const navigate = useNavigate();
  const { user } = useAuth();

  const trip = location.state?.trip;

  const [step, setStep] = useState(1);
  const [availableSeats, setAvailableSeats] = useState([]);
  const [selectedSeat, setSelectedSeat] = useState("");
  const [passengerName, setPassengerName] = useState(user?.displayName || "");
  const [passengerPhone, setPassengerPhone] = useState("");
  const [passengerEmail, setPassengerEmail] = useState(user?.email || "");
  const [loadingSeats, setLoadingSeats] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");
  const [booking, setBooking] = useState(null);

  useEffect(() => {
    if (!trip?.id) {
      return;
    }

    async function loadSeats() {
      try {
        setLoadingSeats(true);
        setError("");

        const seats = await getAvailableSeats(trip.id);

        setAvailableSeats(Array.isArray(seats) ? seats : []);
      } catch (err) {
        console.error("Failed to load seats:", err);

        setError(
          err.response?.data?.message || "Unable to load seat availability.",
        );
      } finally {
        setLoadingSeats(false);
      }
    }

    loadSeats();
  }, [trip?.id]);

  const seatRows = useMemo(() => {
    return generateSeatRows(trip?.capacity);
  }, [trip?.capacity]);

  if (!trip) {
    return (
      <section className="booking-page">
        <AppCard className="booking-empty">
          <h2>No trip selected</h2>
          <p>Select a trip before starting a booking.</p>

          <AppButton onClick={() => navigate("/trips")}>Browse Trips</AppButton>
        </AppCard>
      </section>
    );
  }

  function goBack() {
    if (step === 1) {
      navigate("/trips");
      return;
    }

    setError("");
    setStep((current) => current - 1);
  }

  function continueFromSeat() {
    if (!selectedSeat) {
      setError("Please select an available seat.");
      return;
    }

    setError("");
    setStep(2);
  }

  function continueFromPassenger() {
    if (
      !passengerName.trim() ||
      !passengerPhone.trim() ||
      !passengerEmail.trim()
    ) {
      setError("Please complete all passenger information.");
      return;
    }

    setError("");
    setStep(3);
  }

  async function submitBooking() {
    try {
      setSubmitting(true);
      setError("");

      const createdBooking = await createBooking({
        tripId: trip.id,
        seatNumber: selectedSeat,
        passengerName: passengerName.trim(),
        passengerPhone: passengerPhone.trim(),
        passengerEmail: passengerEmail.trim(),
      });

      setBooking(createdBooking);
    } catch (err) {
      console.error("Booking failed:", err);

      setError(
        err.response?.data?.message ||
          err.response?.data?.error ||
          "Unable to complete the booking. Please try again.",
      );
    } finally {
      setSubmitting(false);
    }
  }

  if (booking) {
    return (
      <section className="booking-page">
        <AppCard className="booking-success">
          <div className="booking-success-icon">
            <CheckCircle2 size={32} />
          </div>

          <span className="booking-eyebrow">BOOKING CONFIRMED</span>

          <h1>Your trip is booked.</h1>

          <p>
            Your booking has been created successfully. Payment remains unpaid
            until a payment is completed.
          </p>

          <div className="booking-reference">
            <span>Booking Reference</span>
            <strong>{booking.bookingReference}</strong>
          </div>

          <div className="booking-success-grid">
            <div>
              <span>Route</span>
              <strong>
                {booking.origin} → {booking.destination}
              </strong>
            </div>

            <div>
              <span>Seat</span>
              <strong>{booking.seatNumber}</strong>
            </div>

            <div>
              <span>Departure</span>
              <strong>{formatDateTime(booking.scheduledDeparture)}</strong>
            </div>

            <div>
              <span>Fare</span>
              <strong>
                ₱
                {Number(booking.fare || 0).toLocaleString("en-PH", {
                  minimumFractionDigits: 2,
                })}
              </strong>
            </div>
          </div>

          <div className="booking-success-actions">
            <AppButton variant="secondary" onClick={() => navigate("/trips")}>
              Browse More Trips
            </AppButton>

            <AppButton onClick={() => navigate("/bookings")}>
              View My Bookings
            </AppButton>
          </div>
        </AppCard>
      </section>
    );
  }

  return (
    <section className="booking-page">
      <header className="booking-header">
        <div>
          <button
            type="button"
            className="booking-back-button"
            onClick={goBack}
          >
            <ArrowLeft size={15} />
            Back
          </button>

          <h1>Book Your Trip</h1>
          <p>Complete the booking details for your selected trip.</p>
        </div>
      </header>

      <div className="booking-stepper">
        {[
          ["1", "Seat"],
          ["2", "Passenger"],
          ["3", "Review"],
        ].map(([number, label]) => (
          <div
            key={number}
            className={`booking-step ${step >= Number(number) ? "active" : ""}`}
          >
            <span>{number}</span>
            <strong>{label}</strong>
          </div>
        ))}
      </div>

      <div className="booking-layout">
        <div className="booking-main">
          <AppCard className="booking-trip-summary">
            <div className="booking-trip-icon">
              <BusFront size={21} />
            </div>

            <div>
              <span>{trip.routeIdentifier || "Selected Trip"}</span>

              <strong>
                {trip.origin} → {trip.destination}
              </strong>

              <div className="booking-trip-meta">
                <span>
                  <Clock3 size={13} />
                  {formatDateTime(trip.scheduledDeparture)}
                </span>

                <span>
                  <Users size={13} />
                  {trip.capacity} seats
                </span>
              </div>
            </div>
          </AppCard>

          {error && <div className="booking-error">{error}</div>}

          {step === 1 && (
            <AppCard className="booking-panel">
              <div className="booking-panel-header">
                <div>
                  <h2>Select a Seat</h2>
                  <p>Choose an available seat for this trip.</p>
                </div>

                <span>{availableSeats.length} available</span>
              </div>

              {loadingSeats ? (
                <div className="booking-loading">
                  Loading seat availability...
                </div>
              ) : (
                <div className="seat-layout">
                  <div className="seat-driver">DRIVER</div>

                  <div className="seat-column-labels">
                    <span>A</span>
                    <span>B</span>
                    <span>C</span>
                    <span className="seat-aisle-label" />
                    <span>D</span>
                    <span>E</span>
                    <span>F</span>
                  </div>

                  <div className="seat-rows">
                    {seatRows.map((rowSeats) => {
                      const rowNumber = rowSeats[0]?.row;

                      const leftSeats = rowSeats.slice(0, 3);
                      const rightSeats = rowSeats.slice(3, 6);

                      return (
                        <div className="seat-row" key={rowNumber}>
                          {leftSeats.map((seat) => {
                            const available = availableSeats.includes(
                              seat.seat,
                            );

                            return (
                              <button
                                key={seat.seat}
                                type="button"
                                className={`seat-button ${
                                  available ? "available" : "occupied"
                                } ${
                                  selectedSeat === seat.seat ? "selected" : ""
                                }`}
                                disabled={!available}
                                onClick={() => {
                                  setSelectedSeat(seat.seat);
                                  setError("");
                                }}
                              >
                                {seat.seat}
                              </button>
                            );
                          })}

                          <div className="seat-aisle" />

                          {rightSeats.map((seat) => {
                            const available = availableSeats.includes(
                              seat.seat,
                            );

                            return (
                              <button
                                key={seat.seat}
                                type="button"
                                className={`seat-button ${
                                  available ? "available" : "occupied"
                                } ${
                                  selectedSeat === seat.seat ? "selected" : ""
                                }`}
                                disabled={!available}
                                onClick={() => {
                                  setSelectedSeat(seat.seat);
                                  setError("");
                                }}
                              >
                                {seat.seat}
                              </button>
                            );
                          })}
                        </div>
                      );
                    })}
                  </div>

                  <div className="seat-legend">
                    <span>
                      <i className="available" />
                      Available
                    </span>

                    <span>
                      <i className="selected" />
                      Selected
                    </span>

                    <span>
                      <i className="occupied" />
                      Occupied
                    </span>
                  </div>
                </div>
              )}

              <div className="booking-panel-actions">
                <AppButton onClick={continueFromSeat}>
                  Continue
                  <ArrowRight size={15} />
                </AppButton>
              </div>
            </AppCard>
          )}

          {step === 2 && (
            <AppCard className="booking-panel">
              <div className="booking-panel-header">
                <div>
                  <h2>Passenger Information</h2>
                  <p>Enter the passenger details for this booking.</p>
                </div>
              </div>

              <div className="booking-form">
                <label>
                  <span>Passenger Name</span>

                  <div className="booking-input">
                    <User size={15} />

                    <input
                      value={passengerName}
                      onChange={(event) => setPassengerName(event.target.value)}
                      placeholder="Full name"
                    />
                  </div>
                </label>

                <label>
                  <span>Phone Number</span>

                  <div className="booking-input">
                    <Phone size={15} />

                    <input
                      value={passengerPhone}
                      onChange={(event) =>
                        setPassengerPhone(event.target.value)
                      }
                      placeholder="09XXXXXXXXX"
                    />
                  </div>
                </label>

                <label>
                  <span>Email Address</span>

                  <div className="booking-input">
                    <Mail size={15} />

                    <input
                      type="email"
                      value={passengerEmail}
                      onChange={(event) =>
                        setPassengerEmail(event.target.value)
                      }
                      placeholder="you@example.com"
                    />
                  </div>
                </label>
              </div>

              <div className="booking-panel-actions">
                <AppButton variant="secondary" onClick={goBack}>
                  Back
                </AppButton>

                <AppButton onClick={continueFromPassenger}>
                  Review Booking
                  <ArrowRight size={15} />
                </AppButton>
              </div>
            </AppCard>
          )}

          {step === 3 && (
            <AppCard className="booking-panel">
              <div className="booking-panel-header">
                <div>
                  <h2>Review Booking</h2>
                  <p>Confirm the details before creating your booking.</p>
                </div>
              </div>

              <div className="booking-review">
                <div>
                  <span>Route</span>

                  <strong>
                    {trip.origin} → {trip.destination}
                  </strong>
                </div>

                <div>
                  <span>Departure</span>

                  <strong>{formatDateTime(trip.scheduledDeparture)}</strong>
                </div>

                <div>
                  <span>Bus</span>

                  <strong>{trip.busNumber || "N/A"}</strong>
                </div>

                <div>
                  <span>Seat</span>

                  <strong>{selectedSeat}</strong>
                </div>

                <div>
                  <span>Passenger</span>

                  <strong>{passengerName}</strong>
                </div>

                <div>
                  <span>Contact</span>

                  <strong>{passengerPhone}</strong>
                </div>
              </div>

              <div className="booking-total">
                <span>Fare</span>

                <strong>
                  ₱
                  {Number(trip.fare || 0).toLocaleString("en-PH", {
                    minimumFractionDigits: 2,
                  })}
                </strong>
              </div>

              <p className="booking-payment-note">
                This step creates the booking with the server. Payment is
                currently recorded as unpaid.
              </p>

              <div className="booking-panel-actions">
                <AppButton variant="secondary" onClick={goBack}>
                  Back
                </AppButton>

                <AppButton onClick={submitBooking} disabled={submitting}>
                  {submitting ? "Creating Booking..." : "Confirm Booking"}

                  {!submitting && <CheckCircle2 size={15} />}
                </AppButton>
              </div>
            </AppCard>
          )}
        </div>

        <aside className="booking-side">
          <AppCard className="booking-fare-card">
            <span>Trip Fare</span>

            <strong>
              ₱
              {Number(trip.fare || 0).toLocaleString("en-PH", {
                minimumFractionDigits: 2,
              })}
            </strong>

            <small>Final fare is calculated by the BUSSIN server.</small>
          </AppCard>
        </aside>
      </div>
    </section>
  );
}

export default Booking;
