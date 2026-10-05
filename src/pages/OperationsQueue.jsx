import { useEffect, useMemo, useState } from "react";
import { ListOrdered, RefreshCw } from "lucide-react";
import { getAllTrips } from "../services/tripService";
import { getTripQueue, updateQueueStatus } from "../services/queueService";
import "./OperationsQueue.css";

const STATUSES = ["WAITING", "CALLED", "BOARDED", "CANCELLED"];

function formatDate(value) {
  if (!value) return "—";
  const d = new Date(value);
  return Number.isNaN(d.getTime()) ? "—" : d.toLocaleString("en-PH", {
    month: "short", day: "numeric", year: "numeric",
    hour: "numeric", minute: "2-digit",
  });
}

function OperationsQueue({ title = "Queue", description = "Monitor passenger queues and manage boarding operations." }) {
  const [trips, setTrips] = useState([]);
  const [tripId, setTripId] = useState("");
  const [queue, setQueue] = useState([]);
  const [loadingTrips, setLoadingTrips] = useState(true);
  const [loadingQueue, setLoadingQueue] = useState(false);
  const [savingId, setSavingId] = useState(null);
  const [error, setError] = useState("");

  async function loadTrips() {
    try {
      setLoadingTrips(true);
      setError("");
      const data = await getAllTrips();
      const list = Array.isArray(data) ? data : [];
      setTrips(list);
      if (!tripId && list.length) setTripId(String(list[0].id));
    } catch (err) {
      setError(err.response?.data?.message || "Unable to load trips.");
    } finally {
      setLoadingTrips(false);
    }
  }

  async function loadQueue(selectedTripId = tripId) {
    if (!selectedTripId) {
      setQueue([]);
      return;
    }
    try {
      setLoadingQueue(true);
      setError("");
      const data = await getTripQueue(selectedTripId);
      setQueue(Array.isArray(data) ? data : []);
    } catch (err) {
      setError(err.response?.data?.message || "Unable to load the trip queue.");
      setQueue([]);
    } finally {
      setLoadingQueue(false);
    }
  }

  useEffect(() => { loadTrips(); }, []);
  useEffect(() => { if (tripId) loadQueue(tripId); }, [tripId]);

  const selectedTrip = trips.find((trip) => String(trip.id) === String(tripId));
  const counts = useMemo(() => ({
    waiting: queue.filter((e) => e.status === "WAITING").length,
    called: queue.filter((e) => e.status === "CALLED").length,
    boarded: queue.filter((e) => e.status === "BOARDED").length,
    total: queue.length,
  }), [queue]);

  function nextStatuses(status) {
    if (status === "WAITING") return ["CALLED", "CANCELLED"];
    if (status === "CALLED") return ["BOARDED", "CANCELLED"];
    return [];
  }

  async function changeStatus(entry, status) {
    try {
      setSavingId(entry.id);
      setError("");
      const updated = await updateQueueStatus(entry.id, status);
      setQueue((current) => current.map((item) => item.id === updated.id ? updated : item));
    } catch (err) {
      setError(err.response?.data?.message || "Unable to update queue status.");
    } finally {
      setSavingId(null);
    }
  }

  return (
    <section className="operations-queue-page">
      <header className="operations-queue-header">
        <div>
          <h1>{title}</h1>
          <p>{description}</p>
        </div>
        <button type="button" className="queue-refresh-button" onClick={() => loadQueue()} disabled={loadingQueue || !tripId}>
          <RefreshCw size={15} className={loadingQueue ? "queue-spin" : ""} /> Refresh
        </button>
      </header>

      {error && <div className="operations-queue-error">{error}</div>}

      <div className="operations-queue-selector">
        <div>
          <label htmlFor="queue-trip">Trip</label>
          <select id="queue-trip" value={tripId} onChange={(e) => setTripId(e.target.value)} disabled={loadingTrips}>
            {!trips.length && <option value="">No trips available</option>}
            {trips.map((trip) => (
              <option key={trip.id} value={trip.id}>
                #{trip.id} — {trip.routeIdentifier || "Route"} — {trip.busPlateNumber || "Bus"} — {formatDate(trip.scheduledDeparture)}
              </option>
            ))}
          </select>
        </div>
        {selectedTrip && (
          <div className="queue-trip-summary">
            <strong>{selectedTrip.origin || selectedTrip.routeIdentifier} → {selectedTrip.destination || ""}</strong>
            <span>{selectedTrip.status} · Bus {selectedTrip.busPlateNumber || "—"}</span>
          </div>
        )}
      </div>

      <div className="queue-statistics">
        <div><span>Waiting</span><strong>{counts.waiting}</strong></div>
        <div><span>Called</span><strong>{counts.called}</strong></div>
        <div><span>Boarded</span><strong>{counts.boarded}</strong></div>
        <div><span>Total</span><strong>{counts.total}</strong></div>
      </div>

      <div className="operations-queue-card">
        {loadingQueue ? <div className="operations-queue-empty"><ListOrdered size={26}/><h3>Loading queue</h3><p>Getting live queue entries from the server.</p></div> :
        queue.length === 0 ? <div className="operations-queue-empty"><ListOrdered size={26}/><h3>No queue entries</h3><p>No passengers have joined this trip's queue yet.</p></div> :
        <div className="operations-queue-table-wrap"><table className="operations-queue-table"><thead><tr><th>#</th><th>Passenger</th><th>Email</th><th>Status</th><th>Joined</th><th>Action</th></tr></thead><tbody>
          {queue.map((entry) => <tr key={entry.id}>
            <td><strong className="queue-number">Q{entry.queueNumber}</strong></td>
            <td><strong>{entry.commuterName || "Unknown passenger"}</strong><small>ID #{entry.commuterId}</small></td>
            <td>{entry.commuterEmail || "—"}</td>
            <td><span className={"queue-status "+String(entry.status).toLowerCase()}>{entry.status}</span></td>
            <td>{formatDate(entry.joinedAt)}</td>
            <td>
              {nextStatuses(entry.status).length ? <select value="" disabled={savingId === entry.id} onChange={(e) => e.target.value && changeStatus(entry, e.target.value)} aria-label={`Update queue entry Q${entry.queueNumber}`}>
                <option value="">{savingId === entry.id ? "Updating..." : "Update status"}</option>
                {nextStatuses(entry.status).map((s) => <option key={s} value={s}>{s}</option>)}
              </select> : <span className="queue-no-action">No action</span>}
            </td>
          </tr>)}
        </tbody></table></div>}
      </div>
    </section>
  );
}

export default OperationsQueue;
