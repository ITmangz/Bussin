import {
  CalendarDays,
  CheckCircle2,
  Clock3,
  Eye,
  MapPin,
  Search,
  Ticket,
  XCircle,
} from "lucide-react";

import { useEffect, useMemo, useState } from "react";

import AppButton from "../components/ui/AppButton";
import AppCard from "../components/ui/AppCard";
import ETicket from "../components/booking/ETicket";

import { getMyBookings, cancelMyBooking } from "../services/bookingService";

import "./MyBookings.css";

function MyBookings() {
  const [search, setSearch] = useState("");
  const [statusFilter, setStatusFilter] = useState("ALL");
  const [selectedBooking, setSelectedBooking] = useState(null);

  const [bookings, setBookings] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [cancelling, setCancelling] = useState(false);

  useEffect(() => {
    loadBookings();
  }, []);

  async function loadBookings() {
    try {
      setLoading(true);
      setError("");

      const response = await getMyBookings();

      console.log("MY BOOKINGS API RESPONSE:", response);

      const bookingList = Array.isArray(response) ? response : [];

      setBookings(bookingList.map(mapBooking));
    } catch (err) {
      console.error("Failed to load bookings:", err);

      setError(
        err.response?.data?.message ||
          "Unable to load your bookings from the server.",
      );
    } finally {
      setLoading(false);
    }
  }

  function mapBooking(booking) {
    return {
      id: booking.id,

      reference: booking.bookingReference,

      origin: booking.origin,

      destination: booking.destination,

      date: formatDate(booking.scheduledDeparture),

      time: formatTime(booking.scheduledDeparture),

      busNumber: booking.busPlateNumber || "N/A",

      seats:
        Array.isArray(booking.seatNumbers) && booking.seatNumbers.length > 0
          ? booking.seatNumbers
          : [booking.seatNumber].filter(Boolean),

      seat: booking.seatNumber || "N/A",

      queueNumber: booking.queueNumber,
      queueStatus: booking.queueStatus || "",

      fare: Number(booking.fare || 0),

      bookingStatus: booking.status,

      paymentStatus: booking.paymentStatus,

      passenger: booking.passengerName || "N/A",

      contact: booking.passengerPhone || "N/A",

      passengerEmail: booking.passengerEmail || "",

      scheduledDeparture: booking.scheduledDeparture,

      scheduledArrival: booking.scheduledArrival,

      tripId: booking.tripId,

      routeIdentifier: booking.routeIdentifier,
    };
  }

  function formatDate(dateTime) {
    if (!dateTime) {
      return "N/A";
    }

    const dateObject = new Date(dateTime);

    return dateObject.toLocaleDateString("en-US", {
      year: "numeric",
      month: "long",
      day: "numeric",
    });
  }

  function formatTime(dateTime) {
    if (!dateTime) {
      return "N/A";
    }

    const dateObject = new Date(dateTime);

    return dateObject.toLocaleTimeString("en-US", {
      hour: "numeric",
      minute: "2-digit",
    });
  }

  const statistics = {
    upcoming: bookings.filter(
      (booking) => booking.bookingStatus === "CONFIRMED",
    ).length,

    confirmed: bookings.filter(
      (booking) => booking.bookingStatus === "CONFIRMED",
    ).length,

    completed: bookings.filter(
      (booking) => booking.bookingStatus === "COMPLETED",
    ).length,

    total: bookings.length,
  };

  const filteredBookings = useMemo(() => {
    const normalizedSearch = search.trim().toLowerCase();

    return bookings.filter((booking) => {
      const matchesSearch =
        !normalizedSearch ||
        booking.reference.toLowerCase().includes(normalizedSearch) ||
        booking.origin.toLowerCase().includes(normalizedSearch) ||
        booking.destination.toLowerCase().includes(normalizedSearch);

      const matchesStatus =
        statusFilter === "ALL" || booking.bookingStatus === statusFilter;

      return matchesSearch && matchesStatus;
    });
  }, [bookings, search, statusFilter]);

  function formatFare(fare) {
    return Number(fare || 0).toLocaleString("en-PH", {
      minimumFractionDigits: 2,
    });
  }

  function getStatusClass(status) {
    return status.toLowerCase();
  }

  async function handleCancelBooking() {
    if (!selectedBooking) {
      return;
    }

    const confirmed = window.confirm(
      `Are you sure you want to cancel booking ${selectedBooking.reference}?`,
    );

    if (!confirmed) {
      return;
    }

    try {
      setCancelling(true);
      setError("");

      const updatedBooking = await cancelMyBooking(selectedBooking.id);

      const mappedBooking = mapBooking(updatedBooking);

      setBookings((currentBookings) =>
        currentBookings.map((booking) =>
          booking.id === mappedBooking.id ? mappedBooking : booking,
        ),
      );

      setSelectedBooking(mappedBooking);
    } catch (err) {
      console.error("Failed to cancel booking:", err);

      setError(err.response?.data?.message || "Unable to cancel this booking.");
    } finally {
      setCancelling(false);
    }
  }

  if (loading) {
    return (
      <section className="my-bookings-page">
        <header className="my-bookings-header">
          <div>
            <h1>My Bookings</h1>

            <p>View and manage your bus trip bookings.</p>
          </div>

          <div className="my-bookings-header-icon">
            <Ticket size={21} />
          </div>
        </header>

        <AppCard className="bookings-empty">
          <div className="bookings-empty-icon">
            <Ticket size={23} />
          </div>

          <h3>Loading bookings</h3>

          <p>We're getting your latest bookings from the BUSSIN server.</p>
        </AppCard>
      </section>
    );
  }

  if (error && bookings.length === 0) {
    return (
      <section className="my-bookings-page">
        <header className="my-bookings-header">
          <div>
            <h1>My Bookings</h1>

            <p>View and manage your bus trip bookings.</p>
          </div>

          <div className="my-bookings-header-icon">
            <Ticket size={21} />
          </div>
        </header>

        <AppCard className="bookings-empty">
          <div className="bookings-empty-icon">
            <XCircle size={23} />
          </div>

          <h3>Unable to load bookings</h3>

          <p>{error}</p>

          <AppButton variant="secondary" onClick={loadBookings}>
            Try Again
          </AppButton>
        </AppCard>
      </section>
    );
  }

  return (
    <section className="my-bookings-page">
      <header className="my-bookings-header">
        <div>
          <h1>My Bookings</h1>

          <p>View and manage your bus trip bookings.</p>
        </div>

        <div className="my-bookings-header-icon">
          <Ticket size={21} />
        </div>
      </header>

      {error && (
        <AppCard className="bookings-empty">
          <h3>Something went wrong</h3>

          <p>{error}</p>

          <AppButton variant="secondary" onClick={loadBookings}>
            Try Again
          </AppButton>
        </AppCard>
      )}

      <div className="booking-statistics">
        <AppCard className="booking-stat-card">
          <div className="booking-stat-icon upcoming">
            <Clock3 size={18} />
          </div>

          <div>
            <span>Upcoming</span>
            <strong>{statistics.upcoming}</strong>
          </div>
        </AppCard>

        <AppCard className="booking-stat-card">
          <div className="booking-stat-icon confirmed">
            <CheckCircle2 size={18} />
          </div>

          <div>
            <span>Confirmed</span>
            <strong>{statistics.confirmed}</strong>
          </div>
        </AppCard>

        <AppCard className="booking-stat-card">
          <div className="booking-stat-icon completed">
            <CheckCircle2 size={18} />
          </div>

          <div>
            <span>Completed</span>
            <strong>{statistics.completed}</strong>
          </div>
        </AppCard>

        <AppCard className="booking-stat-card">
          <div className="booking-stat-icon total">
            <Ticket size={18} />
          </div>

          <div>
            <span>Total Bookings</span>
            <strong>{statistics.total}</strong>
          </div>
        </AppCard>
      </div>

      <AppCard className="booking-filter-card">
        <div className="booking-search">
          <Search size={16} />

          <input
            type="search"
            placeholder="Search booking reference or route..."
            value={search}
            onChange={(event) => setSearch(event.target.value)}
          />
        </div>

        <select
          value={statusFilter}
          onChange={(event) => setStatusFilter(event.target.value)}
          aria-label="Filter by booking status"
        >
          <option value="ALL">All Statuses</option>

          <option value="CONFIRMED">Confirmed</option>

          <option value="COMPLETED">Completed</option>

          <option value="CANCELLED">Cancelled</option>
        </select>
      </AppCard>

      <div className="bookings-section-header">
        <div>
          <h2>Booking History</h2>

          <span>
            {filteredBookings.length} booking
            {filteredBookings.length !== 1 ? "s" : ""}
          </span>
        </div>
      </div>

      {filteredBookings.length > 0 ? (
        <div className="booking-list">
          {filteredBookings.map((booking) => (
            <AppCard key={booking.id} className="booking-card">
              <div className="booking-main">
                <div className="booking-reference">
                  <span>Booking Reference</span>

                  <strong>{booking.reference}</strong>
                </div>

                <div className="booking-route">
                  <div>
                    <span>From</span>

                    <strong>{booking.origin}</strong>
                  </div>

                  <div className="booking-route-arrow">→</div>

                  <div>
                    <span>To</span>

                    <strong>{booking.destination}</strong>
                  </div>
                </div>

                <div className="booking-info">
                  <div>
                    <CalendarDays size={14} />

                    <span>{booking.date}</span>
                  </div>

                  <div>
                    <Clock3 size={14} />

                    <span>{booking.time}</span>
                  </div>

                  <div>
                    <Ticket size={14} />

                    <span>Seats {booking.seats.join(", ") || "N/A"}</span>
                  </div>
                </div>
              </div>

              <div className="booking-side">
                <div className="booking-statuses">
                  <span
                    className={`booking-status ${getStatusClass(
                      booking.bookingStatus,
                    )}`}
                  >
                    {booking.bookingStatus}
                  </span>

                  <span
                    className={`payment-status ${booking.paymentStatus.toLowerCase()}`}
                  >
                    {booking.paymentStatus}
                  </span>
                </div>

                <div className="booking-fare">
                  <span>Fare</span>

                  <strong>₱{formatFare(booking.fare)}</strong>
                </div>

                {booking.queueNumber && (
                  <div className="booking-queue">
                    <span>Queue</span>
                    <strong>#{booking.queueNumber} · {booking.queueStatus || "WAITING"}</strong>
                  </div>
                )}

                <AppButton
                  variant="secondary"
                  onClick={() => setSelectedBooking(booking)}
                >
                  <Eye size={14} />
                  Details
                </AppButton>
              </div>
            </AppCard>
          ))}
        </div>
      ) : (
        <AppCard className="bookings-empty">
          <div className="bookings-empty-icon">
            <Ticket size={23} />
          </div>

          <h3>No bookings found</h3>

          <p>No bookings match your current search or filter.</p>
        </AppCard>
      )}

      {selectedBooking && (
        <div
          className="booking-modal-backdrop"
          onMouseDown={(event) => {
            if (event.target === event.currentTarget) {
              setSelectedBooking(null);
            }
          }}
        >
          <div
            className="booking-modal"
            role="dialog"
            aria-modal="true"
            aria-label="Booking details"
          >
            <div className="booking-modal-header">
              <div>
                <span>Booking Details</span>

                <strong>{selectedBooking.reference}</strong>
              </div>

              <button
                type="button"
                className="booking-modal-close"
                onClick={() => setSelectedBooking(null)}
                aria-label="Close booking details"
              >
                <XCircle size={21} />
              </button>
            </div>

            <div className="booking-modal-body">
              <div className="modal-route">
                <div>
                  <span>From</span>

                  <strong>{selectedBooking.origin}</strong>
                </div>

                <div className="modal-route-line">
                  <div />
                  <MapPin size={15} />
                  <div />
                </div>

                <div>
                  <span>To</span>

                  <strong>{selectedBooking.destination}</strong>
                </div>
              </div>

              <div className="modal-details-grid">
                <div>
                  <span>Date</span>

                  <strong>{selectedBooking.date}</strong>
                </div>

                <div>
                  <span>Departure</span>

                  <strong>{selectedBooking.time}</strong>
                </div>

                <div>
                  <span>Bus</span>

                  <strong>{selectedBooking.busNumber}</strong>
                </div>

                <div>
                  <span>Seats</span>

                  <strong>{selectedBooking.seats.join(", ") || "N/A"}</strong>
                </div>

                <div>
                  <span>Passenger</span>

                  <strong>{selectedBooking.passenger}</strong>
                </div>

                <div>
                  <span>Contact</span>

                  <strong>{selectedBooking.contact}</strong>
                </div>
              </div>

              <div className="modal-queue">
                <div>
                  <span>Queue</span>
                  <strong>
                    {selectedBooking.queueNumber
                      ? `#${selectedBooking.queueNumber}`
                      : "Not assigned"}
                  </strong>
                </div>

                <div>
                  <span>Queue Status</span>
                  <strong>{selectedBooking.queueStatus || "N/A"}</strong>
                </div>
              </div>

              <div className="modal-payment">
                <div>
                  <span>Payment</span>

                  <strong>{selectedBooking.paymentStatus}</strong>
                </div>

                <div>
                  <span>Total Fare</span>

                  <strong>₱{formatFare(selectedBooking.fare)}</strong>
                </div>
              </div>

              <ETicket
                booking={{
                  bookingReference: selectedBooking.reference,
                  passengerName: selectedBooking.passenger,
                  passengerPhone: selectedBooking.contact,
                  origin: selectedBooking.origin,
                  destination: selectedBooking.destination,
                  scheduledDeparture: selectedBooking.scheduledDeparture,
                  scheduledArrival: selectedBooking.scheduledArrival,
                  busPlateNumber: selectedBooking.busNumber,
                  seatNumbers: selectedBooking.seats,
                  queueNumber: selectedBooking.queueNumber,
                  queueStatus: selectedBooking.queueStatus,
                  paymentStatus: selectedBooking.paymentStatus,
                  status: selectedBooking.bookingStatus,
                  fare: selectedBooking.fare,
                }}
                showPreview={false}
              />
            </div>

            <div className="booking-modal-footer">
              <span
                className={`booking-status ${getStatusClass(
                  selectedBooking.bookingStatus,
                )}`}
              >
                {selectedBooking.bookingStatus}
              </span>

              <div>
                {selectedBooking.bookingStatus === "CONFIRMED" && (
                  <AppButton
                    variant="secondary"
                    onClick={handleCancelBooking}
                    disabled={cancelling}
                  >
                    {cancelling ? "Cancelling..." : "Cancel Booking"}
                  </AppButton>
                )}

                <AppButton
                  variant="secondary"
                  onClick={() => setSelectedBooking(null)}
                >
                  Close
                </AppButton>
              </div>
            </div>
          </div>
        </div>
      )}
    </section>
  );
}

export default MyBookings;
