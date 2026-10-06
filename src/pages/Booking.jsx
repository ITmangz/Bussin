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
import ETicket from "../components/booking/ETicket";
import EReceipt from "../components/booking/EReceipt";
import { useAuth } from "../contexts/AuthContext";
import {
  createBooking,
  createGuestBooking,
  getAvailableSeats,
} from "../services/bookingService";
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

function formatFare(value) {
  return Number(value || 0).toLocaleString("en-PH", {
    minimumFractionDigits: 2,
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
  const [seatCount, setSeatCount] = useState(1);
  const [selectedSeats, setSelectedSeats] = useState([]);
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
        const nextAvailableSeats = Array.isArray(seats) ? seats : [];

        setAvailableSeats(nextAvailableSeats);
        setSelectedSeats([]);
        setSeatCount(nextAvailableSeats.length > 0 ? 1 : 0);
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

  const maxSeatCount = availableSeats.length;
  const totalFare =
    Number(trip?.fare || 0) * Number(selectedSeats.length || seatCount || 0);

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

  function handleSeatCountChange(event) {
    const nextCount = Number(event.target.value);

    setSeatCount(nextCount);
    setSelectedSeats((current) => current.slice(0, nextCount));
    setError("");
  }

  function toggleSeat(seatNumber) {
    if (!availableSeats.includes(seatNumber)) {
      return;
    }

    setSelectedSeats((current) => {
      if (current.includes(seatNumber)) {
        setError("");
        return current.filter((seat) => seat !== seatNumber);
      }

      if (current.length >= seatCount) {
        setError(`You selected ${seatCount} seat${seatCount === 1 ? "" : "s"}. Deselect a seat before choosing another.`);
        return current;
      }

      setError("");
      return [...current, seatNumber].sort((a, b) =>
        a.localeCompare(b, undefined, { numeric: true }),
      );
    });
  }

  function continueFromSeat() {
    if (seatCount < 1) {
      setError("There are no available seats for this trip.");
      return;
    }

    if (selectedSeats.length !== seatCount) {
      setError(
        `Please select exactly ${seatCount} seat${seatCount === 1 ? "" : "s"}.`,
      );
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

      const bookingRequest = {
        tripId: trip.id,
        seatNumbers: selectedSeats,
        passengerName: passengerName.trim(),
        passengerPhone: passengerPhone.trim(),
        passengerEmail: passengerEmail.trim(),
      };
      const createdBooking = user
        ? await createBooking(bookingRequest)
        : await createGuestBooking(bookingRequest);

      setBooking(createdBooking);
    } catch (err) {
      console.error("Booking failed:", err);

      setError(
        err.response?.data?.message ||
          err.response?.data?.error ||
          "Unable to complete the booking. Please refresh the seat map and try again.",
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
            {booking.guestBooking
              ? "Your booking and queue entry were created. Keep your booking reference; guest bookings are available to BUSSIN staff but do not appear in commuter history."
              : "Your booking and queue entry were created successfully. Payment remains unpaid until a payment is completed."}
          </p>

          <ETicket booking={booking} />
          <EReceipt booking={booking} />

          <div className="booking-success-actions">
            <AppButton variant="secondary" onClick={() => navigate("/trips")}>
              Browse More Trips
            </AppButton>

            {booking.guestBooking ? (
              <AppButton variant="secondary" onClick={() => navigate("/login")}>
                Sign In
              </AppButton>
            ) : (
              <AppButton onClick={() => navigate("/bookings")}>
                View My Bookings
              </AppButton>
            )}
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
          <p>
            {user
              ? "Select your seats, enter passenger details, and confirm."
              : "Booking as a guest. Select seats, enter your contact details, and confirm manually."}
          </p>
        </div>
      </header>

      <div className="booking-stepper">
        {[
          ["1", "Seats"],
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
                  <h2>How many seats?</h2>
                  <p>Choose the number of seats you want to book, then select them below.</p>
                </div>

                <span>{availableSeats.length} available</span>
              </div>

              <div className="seat-count-control">
                <label htmlFor="seat-count">Seats to book</label>

                <select
                  id="seat-count"
                  value={seatCount}
                  onChange={handleSeatCountChange}
                  disabled={loadingSeats || maxSeatCount === 0}
                >
                  {Array.from(
                    { length: Math.max(maxSeatCount, 1) },
                    (_, index) => index + 1,
                  ).map((count) => (
                    <option key={count} value={count}>
                      {count} seat{count === 1 ? "" : "s"}
                    </option>
                  ))}
                </select>

                <strong>
                  {selectedSeats.length} / {seatCount || 0} selected
                </strong>
              </div>

              {loadingSeats ? (
                <div className="booking-loading">
                  Loading seat availability...
                </div>
              ) : maxSeatCount === 0 ? (
                <div className="booking-no-seats">
                  <h3>No seats available</h3>
                  <p>All seats on this trip have already been booked.</p>
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

                      const renderSeat = (seat) => {
                        const available = availableSeats.includes(seat.seat);
                        const selected = selectedSeats.includes(seat.seat);

                        return (
                          <button
                            key={seat.seat}
                            type="button"
                            className={`seat-button ${available ? "available" : "occupied"} ${selected ? "selected" : ""}`}
                            disabled={!available}
                            aria-label={`${seat.seat} ${available ? selected ? "selected" : "available" : "occupied"}`}
                            onClick={() => toggleSeat(seat.seat)}
                          >
                            {seat.seat}
                          </button>
                        );
                      };

                      return (
                        <div className="seat-row" key={rowNumber}>
                          {leftSeats.map(renderSeat)}
                          <div className="seat-aisle" />
                          {rightSeats.map(renderSeat)}
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
                <AppButton
                  onClick={continueFromSeat}
                  disabled={loadingSeats || maxSeatCount === 0}
                >
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
                  <span>Selected Seats</span>
                  <strong>{selectedSeats.join(", ")}</strong>
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
                <div>
                  <span>{selectedSeats.length} seat{selectedSeats.length === 1 ? "" : "s"}</span>
                  <small>₱{formatFare(trip.fare)} per seat</small>
                </div>

                <strong>₱{formatFare(totalFare)}</strong>
              </div>

              <p className="booking-payment-note">
                Confirming creates the booking and automatically assigns your
                queue number. Payment is currently recorded as unpaid.
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
            <span>Booking Total</span>

            <strong>
              ₱{formatFare(step === 3 ? totalFare : Number(trip.fare || 0) * seatCount)}
            </strong>

            <small>
              {step === 1
                ? `${seatCount || 0} seat${seatCount === 1 ? "" : "s"} selected for booking`
                : "Final fare is calculated by the BUSSIN server."}
            </small>
          </AppCard>
        </aside>
      </div>
    </section>
  );
}

export default Booking;
