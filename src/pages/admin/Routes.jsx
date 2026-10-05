import { useEffect, useMemo, useState } from "react";
import { Edit3, MapPinned, Plus, Search, Trash2, X } from "lucide-react";
import {
  createRoute,
  deleteRoute,
  getAllRoutes,
  updateRoute,
} from "../../services/routeService";
import "./Routes.css";

const EMPTY_FORM = {
  routeIdentifier: "",
  origin: "",
  destination: "",
  distanceKm: "",
  durationMinutes: "",
  baseFare: "",
  description: "",
  active: true,
};

function formatMoney(value) {
  const amount = Number(value);
  return Number.isFinite(amount)
    ? new Intl.NumberFormat("en-PH", {
        style: "currency",
        currency: "PHP",
      }).format(amount)
    : "—";
}

function Routes() {
  const [routes, setRoutes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [search, setSearch] = useState("");
  const [statusFilter, setStatusFilter] = useState("");
  const [modalOpen, setModalOpen] = useState(false);
  const [editingRoute, setEditingRoute] = useState(null);
  const [form, setForm] = useState(EMPTY_FORM);
  const [formError, setFormError] = useState("");
  const [saving, setSaving] = useState(false);
  const [deletingId, setDeletingId] = useState(null);

  useEffect(() => {
    loadRoutes();
  }, []);

  async function loadRoutes() {
    try {
      setLoading(true);
      setError("");
      const data = await getAllRoutes();
      setRoutes(Array.isArray(data) ? data : []);
    } catch (err) {
      console.error("Failed to load routes:", err);
      setError(
        err.response?.data?.message ||
          err.response?.data?.error ||
          "Unable to load routes from the BUSSIN server.",
      );
    } finally {
      setLoading(false);
    }
  }

  const filteredRoutes = useMemo(() => {
    const term = search.trim().toLowerCase();

    return routes.filter((route) => {
      const matchesSearch =
        !term ||
        String(route.id).includes(term) ||
        route.routeIdentifier?.toLowerCase().includes(term) ||
        route.origin?.toLowerCase().includes(term) ||
        route.destination?.toLowerCase().includes(term);

      const matchesStatus =
        !statusFilter ||
        (statusFilter === "ACTIVE" ? route.active : !route.active);

      return matchesSearch && matchesStatus;
    });
  }, [routes, search, statusFilter]);

  function openCreateModal() {
    setEditingRoute(null);
    setForm(EMPTY_FORM);
    setFormError("");
    setModalOpen(true);
  }

  function openEditModal(route) {
    setEditingRoute(route);
    setForm({
      routeIdentifier: route.routeIdentifier || "",
      origin: route.origin || "",
      destination: route.destination || "",
      distanceKm: String(route.distanceKm ?? ""),
      durationMinutes: String(route.durationMinutes ?? ""),
      baseFare: String(route.baseFare ?? ""),
      description: route.description || "",
      active: Boolean(route.active),
    });
    setFormError("");
    setModalOpen(true);
  }

  function closeModal() {
    if (saving) return;
    setModalOpen(false);
    setEditingRoute(null);
    setForm(EMPTY_FORM);
    setFormError("");
  }

  function handleChange(event) {
    const { name, value, type, checked } = event.target;
    setForm((current) => ({
      ...current,
      [name]: type === "checkbox" ? checked : value,
    }));
    setFormError("");
  }

  async function handleSubmit(event) {
    event.preventDefault();

    const routeIdentifier = form.routeIdentifier.trim();
    const origin = form.origin.trim();
    const destination = form.destination.trim();
    const distanceKm = Number(form.distanceKm);
    const durationMinutes = Number(form.durationMinutes);
    const baseFare = Number(form.baseFare);
    const description = form.description.trim();

    if (!routeIdentifier || !origin || !destination) {
      setFormError("Route identifier, origin, and destination are required.");
      return;
    }
    if (!Number.isFinite(distanceKm) || distanceKm <= 0) {
      setFormError("Distance must be greater than 0 km.");
      return;
    }
    if (!Number.isInteger(durationMinutes) || durationMinutes < 1) {
      setFormError("Duration must be at least 1 minute.");
      return;
    }
    if (!Number.isFinite(baseFare) || baseFare <= 0) {
      setFormError("Base fare must be greater than PHP 0.");
      return;
    }

    const payload = {
      routeIdentifier,
      origin,
      destination,
      distanceKm,
      durationMinutes,
      baseFare,
      description: description || null,
      ...(editingRoute ? { active: form.active } : {}),
    };

    try {
      setSaving(true);
      setFormError("");

      const saved = editingRoute
        ? await updateRoute(editingRoute.id, payload)
        : await createRoute(payload);

      setRoutes((current) => {
        const next = editingRoute
          ? current.map((item) => (item.id === saved.id ? saved : item))
          : [...current, saved];

        return next.sort((a, b) =>
          String(a.routeIdentifier).localeCompare(String(b.routeIdentifier)),
        );
      });

      closeModal();
    } catch (err) {
      console.error("Failed to save route:", err);
      setFormError(
        err.response?.data?.message ||
          err.response?.data?.error ||
          "Unable to save the route. Please try again.",
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(route) {
    if (
      !window.confirm(
        `Delete route ${route.routeIdentifier}? Trips using this route may prevent deletion.`,
      )
    ) {
      return;
    }

    try {
      setDeletingId(route.id);
      setError("");
      await deleteRoute(route.id);
      setRoutes((current) => current.filter((item) => item.id !== route.id));
    } catch (err) {
      console.error("Failed to delete route:", err);
      setError(
        err.response?.data?.message ||
          err.response?.data?.error ||
          "Unable to delete the route.",
      );
    } finally {
      setDeletingId(null);
    }
  }

  return (
    <section className="routes-admin-page">
      <header className="routes-admin-header">
        <div>
          <h1>Routes</h1>
          <p>Manage bus routes, distance, duration, and base fares.</p>
        </div>
        <button type="button" className="route-primary-button" onClick={openCreateModal}>
          <Plus size={15} /> Add Route
        </button>
      </header>

      {error && <div className="routes-error">{error}</div>}

      <div className="routes-toolbar">
        <div className="routes-search">
          <Search size={16} />
          <input
            type="search"
            value={search}
            onChange={(event) => setSearch(event.target.value)}
            placeholder="Search route, origin, destination..."
            aria-label="Search routes"
          />
        </div>
        <select
          value={statusFilter}
          onChange={(event) => setStatusFilter(event.target.value)}
          aria-label="Filter routes by status"
        >
          <option value="">All statuses</option>
          <option value="ACTIVE">Active</option>
          <option value="INACTIVE">Inactive</option>
        </select>
      </div>

      <div className="admin-dashboard-panel routes-table-card">
        {loading ? (
          <div className="routes-empty">
            <MapPinned size={25} />
            <h3>Loading routes</h3>
            <p>We're getting the latest route records from the server.</p>
          </div>
        ) : filteredRoutes.length === 0 ? (
          <div className="routes-empty">
            <MapPinned size={25} />
            <h3>{routes.length === 0 ? "No routes yet" : "No routes found"}</h3>
            <p>
              {routes.length === 0
                ? "Add the first route to begin configuring BUSSIN operations."
                : "Try changing your search or status filter."}
            </p>
          </div>
        ) : (
          <>
            <div className="routes-table-wrap">
              <table className="routes-table">
                <thead>
                  <tr>
                    <th>Route</th>
                    <th>Origin</th>
                    <th>Destination</th>
                    <th>Distance</th>
                    <th>Duration</th>
                    <th>Base Fare</th>
                    <th>Status</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredRoutes.map((route) => (
                    <tr key={route.id}>
                      <td>
                        <strong>{route.routeIdentifier}</strong>
                        <small>#{route.id}</small>
                      </td>
                      <td>{route.origin}</td>
                      <td>{route.destination}</td>
                      <td>{route.distanceKm} km</td>
                      <td>{route.durationMinutes} min</td>
                      <td>{formatMoney(route.baseFare)}</td>
                      <td>
                        <span className={`route-status ${route.active ? "active" : "inactive"}`}>
                          {route.active ? "Active" : "Inactive"}
                        </span>
                      </td>
                      <td>
                        <div className="route-actions">
                          <button
                            type="button"
                            className="route-action"
                            onClick={() => openEditModal(route)}
                            aria-label={`Edit ${route.routeIdentifier}`}
                            title="Edit route"
                          >
                            <Edit3 size={15} />
                          </button>
                          <button
                            type="button"
                            className="route-action danger"
                            onClick={() => handleDelete(route)}
                            disabled={deletingId === route.id}
                            aria-label={`Delete ${route.routeIdentifier}`}
                            title="Delete route"
                          >
                            <Trash2 size={15} />
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
            <div className="routes-count">
              Showing {filteredRoutes.length} of {routes.length} routes
            </div>
          </>
        )}
      </div>

      {modalOpen && (
        <div
          className="route-modal-overlay"
          role="presentation"
          onMouseDown={(event) => {
            if (event.target === event.currentTarget) closeModal();
          }}
        >
          <div className="route-modal" role="dialog" aria-modal="true" aria-labelledby="route-modal-title">
            <div className="route-modal-header">
              <div>
                <h2 id="route-modal-title">{editingRoute ? "Edit Route" : "Add Route"}</h2>
                <p>Configure the route information used by BUSSIN trips.</p>
              </div>
              <button type="button" className="route-modal-close" onClick={closeModal} disabled={saving} aria-label="Close">
                <X size={18} />
              </button>
            </div>

            <form className="route-form" onSubmit={handleSubmit}>
              <div className="route-form-grid">
                <div className="route-form-field">
                  <label htmlFor="route-identifier">Route Identifier</label>
                  <input id="route-identifier" name="routeIdentifier" value={form.routeIdentifier} onChange={handleChange} maxLength={100} placeholder="e.g. BAC-MNL-01" required />
                </div>
                <div className="route-form-field">
                  <label htmlFor="route-origin">Origin</label>
                  <input id="route-origin" name="origin" value={form.origin} onChange={handleChange} maxLength={100} placeholder="e.g. Bacoor" required />
                </div>
                <div className="route-form-field">
                  <label htmlFor="route-destination">Destination</label>
                  <input id="route-destination" name="destination" value={form.destination} onChange={handleChange} maxLength={100} placeholder="e.g. Manila" required />
                </div>
                <div className="route-form-field">
                  <label htmlFor="route-distance">Distance (km)</label>
                  <input id="route-distance" name="distanceKm" type="number" min="0.01" step="0.01" value={form.distanceKm} onChange={handleChange} placeholder="e.g. 28.50" required />
                </div>
                <div className="route-form-field">
                  <label htmlFor="route-duration">Duration (minutes)</label>
                  <input id="route-duration" name="durationMinutes" type="number" min="1" step="1" value={form.durationMinutes} onChange={handleChange} placeholder="e.g. 75" required />
                </div>
                <div className="route-form-field">
                  <label htmlFor="route-fare">Base Fare (PHP)</label>
                  <input id="route-fare" name="baseFare" type="number" min="0.01" step="0.01" value={form.baseFare} onChange={handleChange} placeholder="e.g. 45.00" required />
                </div>
                <div className="route-form-field full">
                  <label htmlFor="route-description">Description</label>
                  <textarea id="route-description" name="description" value={form.description} onChange={handleChange} maxLength={255} rows={3} placeholder="Optional route notes..." />
                </div>
                {editingRoute && (
                  <label className="route-active-toggle">
                    <input type="checkbox" name="active" checked={form.active} onChange={handleChange} />
                    <span>Route is active</span>
                  </label>
                )}
              </div>

              {formError && <div className="route-form-error">{formError}</div>}

              <div className="route-modal-actions">
                <button type="button" className="route-secondary-button" onClick={closeModal} disabled={saving}>Cancel</button>
                <button type="submit" className="route-primary-button" disabled={saving}>
                  {saving ? "Saving..." : editingRoute ? "Save Changes" : "Create Route"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </section>
  );
}

export default Routes;
