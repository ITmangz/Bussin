import { Printer } from "lucide-react";

import AppButton from "../ui/AppButton";
import { printBookingDocument } from "./printBookingDocument";
import "./ETicket.css";

function formatDateTime(value) {
  if (!value) return "—";

  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "—";

  return date.toLocaleString("en-PH", {
    dateStyle: "medium",
    timeStyle: "short",
  });
}

function formatFare(value) {
  return new Intl.NumberFormat("en-PH", {
    style: "currency",
    currency: "PHP",
    minimumFractionDigits: 2,
  }).format(Number(value) || 0);
}

function ETicket({ booking, showPreview = true }) {
  const seats =
    Array.isArray(booking.seatNumbers) && booking.seatNumbers.length > 0
      ? booking.seatNumbers
      : [booking.seatNumber].filter(Boolean);
  const status = booking.status || booking.bookingStatus || "CONFIRMED";
  const className = showPreview
    ? "e-ticket-card e-ticket-print-target"
    : "e-ticket-card e-ticket-print-target e-ticket-print-only";

  return (
    <div
      className={`e-ticket-widget ${showPreview ? "" : "e-ticket-compact"}`}
      data-print-document
    >
      <article className={className} data-print-target aria-label="BUSSIN electronic ticket">
        <header className="e-ticket-header">
          <div className="e-ticket-brand">
            <span className="e-ticket-logo">B</span>
            <div>
              <strong>BUSSIN</strong>
              <span>BUS TRAVEL</span>
            </div>
          </div>
          <div className="e-ticket-type">
            <strong>E-TICKET</strong>
            <span>PASSENGER COPY</span>
          </div>
        </header>

        <div className="e-ticket-reference-row">
          <div>
            <span>BOOKING REFERENCE</span>
            <strong>{booking.bookingReference || booking.reference || "—"}</strong>
          </div>
          <span className={`e-ticket-status ${status.toLowerCase()}`}>{status}</span>
        </div>

        <div className="e-ticket-route">
          <div>
            <span>FROM</span>
            <strong>{booking.origin || "—"}</strong>
          </div>
          <span className="e-ticket-route-arrow" aria-hidden="true">→</span>
          <div>
            <span>TO</span>
            <strong>{booking.destination || "—"}</strong>
          </div>
        </div>

        <div className="e-ticket-details">
          <div>
            <span>PASSENGER</span>
            <strong>{booking.passengerName || booking.passenger || "—"}</strong>
          </div>
          <div>
            <span>PHONE</span>
            <strong>{booking.passengerPhone || booking.contact || "—"}</strong>
          </div>
          <div>
            <span>DEPARTURE</span>
            <strong>{formatDateTime(booking.scheduledDeparture)}</strong>
          </div>
          <div>
            <span>ARRIVAL</span>
            <strong>{formatDateTime(booking.scheduledArrival)}</strong>
          </div>
          <div>
            <span>BUS</span>
            <strong>{booking.busPlateNumber || booking.busNumber || "—"}</strong>
          </div>
          <div>
            <span>SEAT{seats.length === 1 ? "" : "S"}</span>
            <strong>{seats.join(", ") || "—"}</strong>
          </div>
          <div>
            <span>QUEUE</span>
            <strong>
              {booking.queueNumber
                ? `#${booking.queueNumber} · ${booking.queueStatus || "WAITING"}`
                : "—"}
            </strong>
          </div>
          <div>
            <span>PAYMENT</span>
            <strong>{booking.paymentStatus || "UNPAID"}</strong>
          </div>
        </div>

        <footer className="e-ticket-footer">
          <div>
            <span>TOTAL FARE</span>
            <strong>{formatFare(booking.fare)}</strong>
          </div>
          <p>Keep this ticket and present the booking reference when boarding.</p>
        </footer>
      </article>

      <div className="e-ticket-actions">
        <AppButton
          variant="secondary"
          onClick={(event) => printBookingDocument(event, "printing-e-ticket")}
        >
          <Printer size={15} />
          Print / Save PDF
        </AppButton>
      </div>
    </div>
  );
}

export default ETicket;
