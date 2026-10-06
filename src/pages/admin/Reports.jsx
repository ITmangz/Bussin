import { useCallback, useEffect, useMemo, useState } from "react";
import {
  BarChart3,
  Bus,
  CalendarDays,
  CircleDollarSign,
  Download,
  FileText,
  RefreshCw,
  Ticket,
  Users,
} from "lucide-react";

import { getAllBookings } from "../../services/adminBookingService";
import { getDashboardMetrics } from "../../services/dashboardService";
import "./Reports.css";

const PERIODS = [
  { value: "7", label: "Last 7 days" },
  { value: "30", label: "Last 30 days" },
  { value: "90", label: "Last 90 days" },
  { value: "all", label: "All time" },
];

const BOOKING_STATUSES = ["CONFIRMED", "PENDING", "COMPLETED", "CANCELLED"];

function formatDate(value, options = {}) {
  if (!value) return "—";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "—";
  return date.toLocaleString("en-PH", {
    year: "numeric",
    month: "short",
    day: "numeric",
    ...options,
  });
}

function formatMoney(value) {
  return new Intl.NumberFormat("en-PH", {
    style: "currency",
    currency: "PHP",
    maximumFractionDigits: 2,
  }).format(Number(value) || 0);
}

function dayKey(date) {
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, "0")}-${String(date.getDate()).padStart(2, "0")}`;
}

function downloadFile(filename, type, contents) {
  const blob = new Blob([contents], { type });
  const url = URL.createObjectURL(blob);
  const link = document.createElement("a");
  link.href = url;
  link.download = filename;
  document.body.appendChild(link);
  link.click();
  link.remove();
  window.setTimeout(() => URL.revokeObjectURL(url), 1000);
}

function csvCell(value) {
  return `"${String(value ?? "").replaceAll('"', '""')}"`;
}

function reportDateSuffix() {
  return new Date().toISOString().slice(0, 10);
}

function Reports() {
  const [bookings, setBookings] = useState([]);
  const [dashboard, setDashboard] = useState(null);
  const [period, setPeriod] = useState("30");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [metricsError, setMetricsError] = useState("");

  const loadReport = useCallback(async () => {
    setLoading(true);
    setError("");
    setMetricsError("");

    const [bookingsResult, dashboardResult] = await Promise.allSettled([
      getAllBookings(),
      getDashboardMetrics(),
    ]);

    if (bookingsResult.status === "fulfilled") {
      setBookings(Array.isArray(bookingsResult.value) ? bookingsResult.value : []);
    } else {
      console.error("Failed to load report bookings:", bookingsResult.reason);
      setError(
        bookingsResult.reason.response?.data?.message
          || bookingsResult.reason.response?.data?.error
          || "Unable to load booking data for this report.",
      );
    }

    if (dashboardResult.status === "fulfilled") {
      setDashboard(dashboardResult.value);
    } else {
      console.error("Failed to load report metrics:", dashboardResult.reason);
      setDashboard(null);
      setMetricsError("Operational summary metrics are unavailable right now.");
    }

    setLoading(false);
  }, []);

  useEffect(() => {
    void Promise.resolve().then(loadReport);
  }, [loadReport]);

  const filteredBookings = useMemo(() => {
    const now = new Date();
    let startDate = null;
    if (period !== "all") {
      startDate = new Date(now);
      startDate.setHours(0, 0, 0, 0);
      startDate.setDate(startDate.getDate() - Number(period) + 1);
    }

    return bookings
      .filter((booking) => {
        if (!startDate) return true;
        const createdAt = new Date(booking.createdAt);
        return !Number.isNaN(createdAt.getTime()) && createdAt >= startDate && createdAt <= now;
      })
      .sort((left, right) => new Date(right.createdAt) - new Date(left.createdAt));
  }, [bookings, period]);

  const summary = useMemo(() => {
    const byStatus = Object.fromEntries(
      BOOKING_STATUSES.map((status) => [
        status,
        filteredBookings.filter((booking) => booking.status === status).length,
      ]),
    );
    const paidFares = filteredBookings
      .filter((booking) => booking.paymentStatus === "PAID")
      .reduce((total, booking) => total + (Number(booking.fare) || 0), 0);

    return {
      totalBookings: filteredBookings.length,
      byStatus,
      paidFares,
    };
  }, [filteredBookings]);

  const bookingTrend = useMemo(() => {
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    return Array.from({ length: 7 }, (_, index) => {
      const date = new Date(today);
      date.setDate(today.getDate() - (6 - index));
      const key = dayKey(date);
      const count = bookings.filter((booking) => {
        const createdAt = new Date(booking.createdAt);
        return !Number.isNaN(createdAt.getTime()) && dayKey(createdAt) === key;
      }).length;
      return {
        key,
        count,
        label: date.toLocaleDateString("en-PH", { weekday: "short" }),
        dateLabel: date.toLocaleDateString("en-PH", { month: "short", day: "numeric" }),
      };
    });
  }, [bookings]);

  const topRoutes = useMemo(() => {
    const routeCounts = new Map();
    for (const booking of filteredBookings) {
      const route = booking.routeIdentifier
        || [booking.origin, booking.destination].filter(Boolean).join(" → ")
        || "Route not recorded";
      routeCounts.set(route, (routeCounts.get(route) || 0) + 1);
    }
    return [...routeCounts.entries()]
      .map(([route, count]) => ({ route, count }))
      .sort((left, right) => right.count - left.count || left.route.localeCompare(right.route))
      .slice(0, 5);
  }, [filteredBookings]);

  const trendMax = Math.max(...bookingTrend.map((day) => day.count), 1);
  const periodLabel = PERIODS.find((item) => item.value === period)?.label || "Selected period";

  function exportCsv() {
    const columns = [
      "Booking reference", "Passenger", "Guest booking", "Email", "Route", "Trip ID", "Bus plate",
      "Seats", "Fare (PHP)", "Booking status", "Payment status", "Queue number", "Booked at",
    ];
    const rows = filteredBookings.map((booking) => [
      booking.bookingReference || booking.id,
      booking.passengerName || booking.commuterName,
      booking.guestBooking ? "Yes" : "No",
      booking.passengerEmail || booking.commuterEmail,
      booking.routeIdentifier || `${booking.origin || ""} - ${booking.destination || ""}`,
      booking.tripId,
      booking.busPlateNumber,
      Array.isArray(booking.seatNumbers) ? booking.seatNumbers.join(", ") : booking.seatNumber,
      booking.fare,
      booking.status,
      booking.paymentStatus,
      booking.queueNumber,
      booking.createdAt,
    ]);
    const csv = [columns, ...rows].map((row) => row.map(csvCell).join(",")).join("\r\n");
    downloadFile(`bussin-report-${reportDateSuffix()}.csv`, "text/csv;charset=utf-8", `\ufeff${csv}`);
  }

  function exportJson() {
    const report = {
      generatedAt: new Date().toISOString(),
      period: periodLabel,
      summary: {
        ...summary,
        paidFares: Number(summary.paidFares.toFixed(2)),
      },
      operationalMetrics: dashboard,
      bookings: filteredBookings,
    };
    downloadFile(
      `bussin-report-${reportDateSuffix()}.json`,
      "application/json;charset=utf-8",
      JSON.stringify(report, null, 2),
    );
  }

  const reportGeneratedAt = formatDate(new Date(), { hour: "numeric", minute: "2-digit" });

  return (
    <section className="reports-page">
      <header className="reports-header">
        <div>
          <span className="reports-eyebrow">BUSSIN ANALYTICS</span>
          <h1>Reports</h1>
          <p>Booking and operations activity at a glance.</p>
        </div>
        <div className="reports-toolbar">
          <label className="reports-period-control">
            <CalendarDays size={16} />
            <select value={period} onChange={(event) => setPeriod(event.target.value)} aria-label="Report date range">
              {PERIODS.map((item) => <option value={item.value} key={item.value}>{item.label}</option>)}
            </select>
          </label>
          <button type="button" className="reports-icon-button" onClick={loadReport} disabled={loading} aria-label="Refresh report" title="Refresh report">
            <RefreshCw size={16} className={loading ? "reports-refreshing" : ""} />
          </button>
          <button type="button" className="reports-secondary-button" onClick={exportCsv} disabled={loading}>
            <Download size={15} /> CSV
          </button>
          <button type="button" className="reports-secondary-button" onClick={exportJson} disabled={loading}>
            <FileText size={15} /> JSON
          </button>
          <button type="button" className="reports-primary-button" onClick={() => window.print()} disabled={loading}>
            <FileText size={15} /> Print / Save PDF
          </button>
        </div>
      </header>

      <div className="reports-print-heading">
        <span>BUSSIN · ADMIN REPORT</span>
        <strong>{periodLabel}</strong>
        <small>Generated {reportGeneratedAt}</small>
      </div>

      {error && <div className="reports-error" role="alert">{error}</div>}
      {metricsError && !error && <div className="reports-notice" role="status">{metricsError} Booking reports are still available.</div>}

      <div className="reports-stat-grid">
        <ReportStat icon={Ticket} label={`Bookings · ${periodLabel.toLowerCase()}`} value={loading ? "—" : summary.totalBookings.toLocaleString("en-PH")} detail="All booking statuses" />
        <ReportStat icon={CircleDollarSign} label="Paid fares" value={loading ? "—" : formatMoney(summary.paidFares)} detail="Recorded as paid in this period" />
        <ReportStat icon={Bus} label="Trips today" value={loading ? "—" : dashboard ? Number(dashboard.todaysTrips || 0).toLocaleString("en-PH") : "—"} detail={`${Number(dashboard?.totalTrips || 0).toLocaleString("en-PH")} total trips`} />
        <ReportStat icon={Users} label="Waiting queue" value={loading ? "—" : dashboard ? Number(dashboard.waitingQueueEntries || 0).toLocaleString("en-PH") : "—"} detail={`${Number(dashboard?.totalQueueEntries || 0).toLocaleString("en-PH")} total queue entries`} />
      </div>

      <div className="reports-main-grid">
        <section className="reports-panel reports-trend-panel">
          <div className="reports-panel-heading">
            <div><h2>Booking activity</h2><p>New bookings over the last seven days.</p></div>
            <BarChart3 size={19} />
          </div>
          {loading ? <ReportEmptyState>Loading booking activity…</ReportEmptyState> : (
            <div className="reports-trend-chart" aria-label="Bookings created each day over the last seven days">
              {bookingTrend.map((day) => (
                <div className="reports-trend-day" key={day.key} title={`${day.dateLabel}: ${day.count} booking${day.count === 1 ? "" : "s"}`}>
                  <strong>{day.count}</strong>
                  <div className="reports-trend-track"><span style={{ height: `${day.count ? Math.max((day.count / trendMax) * 100, 5) : 0}%` }} /></div>
                  <span className="reports-trend-label">{day.label}</span>
                  <small>{day.dateLabel}</small>
                </div>
              ))}
            </div>
          )}
        </section>

        <section className="reports-panel reports-status-panel">
          <div className="reports-panel-heading">
            <div><h2>Booking status</h2><p>{periodLabel} breakdown.</p></div>
            <Ticket size={19} />
          </div>
          {loading ? <ReportEmptyState>Loading booking statuses…</ReportEmptyState> : (
            <div className="reports-status-list">
              {BOOKING_STATUSES.map((status) => {
                const count = summary.byStatus[status] || 0;
                const percentage = summary.totalBookings ? (count / summary.totalBookings) * 100 : 0;
                return (
                  <div className="reports-status-row" key={status}>
                    <div className="reports-status-label"><span className={`reports-status-dot ${status.toLowerCase()}`} /><span>{status}</span><strong>{count}</strong></div>
                    <div className="reports-status-track"><span className={status.toLowerCase()} style={{ width: `${percentage}%` }} /></div>
                  </div>
                );
              })}
            </div>
          )}
        </section>
      </div>

      <div className="reports-main-grid reports-lower-grid">
        <section className="reports-panel reports-routes-panel">
          <div className="reports-panel-heading"><div><h2>Popular routes</h2><p>Routes with the most bookings in this period.</p></div></div>
          {loading ? <ReportEmptyState>Loading route activity…</ReportEmptyState> : topRoutes.length === 0 ? (
            <ReportEmptyState>No route bookings in this period.</ReportEmptyState>
          ) : (
            <ol className="reports-route-list">
              {topRoutes.map((item, index) => (
                <li key={item.route}><span className="reports-route-rank">{index + 1}</span><span className="reports-route-name">{item.route}</span><strong>{item.count}</strong></li>
              ))}
            </ol>
          )}
        </section>

        <section className="reports-panel reports-bookings-panel">
          <div className="reports-panel-heading">
            <div><h2>Recent bookings</h2><p>Latest records for {periodLabel.toLowerCase()}.</p></div>
            <span className="reports-result-count">{filteredBookings.length} records</span>
          </div>
          {loading ? <ReportEmptyState>Loading recent bookings…</ReportEmptyState> : filteredBookings.length === 0 ? (
            <ReportEmptyState>No bookings in this period.</ReportEmptyState>
          ) : (
            <div className="reports-table-wrap">
              <table className="reports-table">
                <thead><tr><th>Booking</th><th>Passenger</th><th>Route</th><th>Fare</th><th>Status</th><th>Date</th></tr></thead>
                <tbody>
                  {filteredBookings.slice(0, 8).map((booking) => (
                    <tr key={`${booking.id}-${booking.bookingReference || "booking"}`}>
                      <td><strong>{booking.bookingReference || `#${booking.id}`}</strong></td>
                      <td>{booking.passengerName || booking.commuterName || "Passenger"}</td>
                      <td>{booking.routeIdentifier || [booking.origin, booking.destination].filter(Boolean).join(" → ") || "—"}</td>
                      <td>{formatMoney(booking.fare)}</td>
                      <td><span className={`reports-booking-status ${String(booking.status || "").toLowerCase()}`}>{booking.status || "Unknown"}</span></td>
                      <td>{formatDate(booking.createdAt, { year: undefined, hour: "numeric", minute: "2-digit" })}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
              {filteredBookings.length > 8 && <div className="reports-table-note">Showing the 8 latest bookings. CSV and JSON exports include all {filteredBookings.length} records.</div>}
            </div>
          )}
        </section>
      </div>

      <footer className="reports-footer">BUSSIN report · {periodLabel} · Generated {reportGeneratedAt}</footer>
    </section>
  );
}

function ReportStat({ icon: Icon, label, value, detail }) {
  return (
    <article className="reports-stat-card">
      <div className="reports-stat-icon"><Icon size={18} /></div>
      <div className="reports-stat-copy"><span>{label}</span><strong>{value}</strong><small>{detail}</small></div>
    </article>
  );
}

function ReportEmptyState({ children }) {
  return <div className="reports-empty-state">{children}</div>;
}

export default Reports;
