import { Printer } from "lucide-react";

import AppButton from "../ui/AppButton";
import { printBookingDocument } from "./printBookingDocument";
import "./EReceipt.css";

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

function EReceipt({ booking, showPreview = true }) {
  const seats =
    Array.isArray(booking.seatNumbers) && booking.seatNumbers.length > 0
      ? booking.seatNumbers
      : [booking.seatNumber].filter(Boolean);
  const paymentStatus = booking.paymentStatus || "UNPAID";
  const className = showPreview
    ? "e-receipt-card e-receipt-print-target"
    : "e-receipt-card e-receipt-print-target e-receipt-print-only";

  return (
    <div
      className={`e-receipt-widget ${showPreview ? "" : "e-receipt-compact"}`}
      data-print-document
    >
      <article className={className} data-print-target aria-label="BUSSIN booking receipt">
        <header className="e-receipt-header">
          <div className="e-receipt-brand">
            <span className="e-receipt-logo">B</span>
            <div><strong>BUSSIN</strong><span>BUS TRAVEL</span></div>
          </div>
          <div className="e-receipt-type"><strong>BOOKING RECEIPT</strong><span>PASSENGER COPY</span></div>
        </header>

        <div className="e-receipt-reference">
          <span>BOOKING REFERENCE</span>
          <strong>{booking.bookingReference || booking.reference || "—"}</strong>
        </div>

        <dl className="e-receipt-details">
          <div><dt>Issued</dt><dd>{formatDateTime(booking.createdAt)}</dd></div>
          <div><dt>Passenger</dt><dd>{booking.passengerName || booking.passenger || "—"}</dd></div>
          <div><dt>Route</dt><dd>{booking.origin || "—"} → {booking.destination || "—"}</dd></div>
          <div><dt>Departure</dt><dd>{formatDateTime(booking.scheduledDeparture)}</dd></div>
          <div><dt>Seat{seats.length === 1 ? "" : "s"}</dt><dd>{seats.join(", ") || "—"}</dd></div>
          <div><dt>Payment status</dt><dd className={`e-receipt-payment ${paymentStatus.toLowerCase()}`}>{paymentStatus}</dd></div>
        </dl>

        <footer className="e-receipt-total">
          <span>{paymentStatus === "PAID" ? "AMOUNT PAID" : "BOOKING TOTAL"}</span>
          <strong>{formatFare(booking.fare)}</strong>
        </footer>
        {paymentStatus !== "PAID" && (
          <p className="e-receipt-note">This confirms the booking amount. Payment has not been recorded.</p>
        )}
      </article>

      <div className="e-receipt-actions">
        <AppButton
          variant="secondary"
          onClick={(event) => printBookingDocument(event, "printing-e-receipt")}
        >
          <Printer size={15} />
          Print / Save PDF
        </AppButton>
      </div>
    </div>
  );
}

export default EReceipt;
