import { useEffect, useMemo, useState } from "react";
import {
  BusFront,
  Edit3,
  Plus,
  Search,
  Trash2,
  X,
} from "lucide-react";

import { createBus, deleteBus, getAllBuses, updateBus } from "../../services/busService";
import "./Buses.css";

const STATUS_OPTIONS = [
  { value: "ACTIVE", label: "Active" },
  { value: "MAINTENANCE", label: "Maintenance" },
  { value: "OUT_OF_SERVICE", label: "Out of Service" },
];

const EMPTY_FORM = {
  plateNumber: "",
  model: "",
  capacity: "",
  status: "ACTIVE",
};

function getStatusLabel(status) {
  const option = STATUS_OPTIONS.find((item) => item.value === status);

  return option?.label || status || "Unknown";
}

function getStatusClass(status) {
  return String(status || "")
    .toLowerCase()
    .replaceAll("_", "-");
}

function Buses() {
  const [buses, setBuses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [search, setSearch] = useState("");
  const [statusFilter, setStatusFilter] = useState("");

  const [modalOpen, setModalOpen] = useState(false);
  const [editingBus, setEditingBus] = useState(null);
  const [form, setForm] = useState(EMPTY_FORM);
  const [formError, setFormError] = useState("");
  const [saving, setSaving] = useState(false);
  const [deletingId, setDeletingId] = useState(null);

  useEffect(() => {
    loadBuses();
  }, []);

  async function loadBuses() {
    try {
      setLoading(true);
      setError("");

      const response = await getAllBuses();
      setBuses(Array.isArray(response) ? response : []);
    } catch (err) {
      console.error("Failed to load buses:", err);

      setError(
        err.response?.data?.message ||
          "Unable to load buses from the BUSSIN server.",
      );
    } finally {
      setLoading(false);
    }
  }

  const filteredBuses = useMemo(() => {
    const normalizedSearch = search.trim().toLowerCase();

    return buses.filter((bus) => {
      const matchesSearch =
        !normalizedSearch ||
        bus.plateNumber?.toLowerCase().includes(normalizedSearch) ||
        bus.model?.toLowerCase().includes(normalizedSearch);

      const matchesStatus =
        !statusFilter || bus.status === statusFilter;

      return matchesSearch && matchesStatus;
    });
  }, [buses, search, statusFilter]);

  function openCreateModal() {
    setEditingBus(null);
    setForm(EMPTY_FORM);
    setFormError("");
    setModalOpen(true);
  }

  function openEditModal(bus) {
    setEditingBus(bus);

    setForm({
      plateNumber: bus.plateNumber || "",
      model: bus.model || "",
      capacity: String(bus.capacity ?? ""),
      status: bus.status || "ACTIVE",
    });

    setFormError("");
    setModalOpen(true);
  }

  function closeModal() {
    if (saving) {
      return;
    }

    setModalOpen(false);
    setEditingBus(null);
    setForm(EMPTY_FORM);
    setFormError("");
  }

  function handleFormChange(event) {
    const { name, value } = event.target;

    setForm((current) => ({
      ...current,
      [name]: value,
    }));

    setFormError("");
  }

  async function handleSubmit(event) {
    event.preventDefault();

    const plateNumber = form.plateNumber.trim();
    const model = form.model.trim();
    const capacity = Number(form.capacity);

    if (!plateNumber) {
      setFormError("Plate number is required.");
      return;
    }

    if (!Number.isInteger(capacity) || capacity < 1 || capacity > 200) {
      setFormError("Capacity must be a whole number between 1 and 200.");
      return;
    }

    try {
      setSaving(true);
      setFormError("");

      const payload = {
        plateNumber,
        model: model || null,
        capacity,
        status: form.status,
      };

      const savedBus = editingBus
        ? await updateBus(editingBus.id, payload)
        : await createBus(payload);

      setBuses((current) => {
        if (!editingBus) {
          return [...current, savedBus].sort((a, b) =>
            String(a.plateNumber).localeCompare(String(b.plateNumber)),
          );
        }

        return current
          .map((bus) => (bus.id === savedBus.id ? savedBus : bus))
          .sort((a, b) =>
            String(a.plateNumber).localeCompare(String(b.plateNumber)),
          );
      });

      closeModal();
    } catch (err) {
      console.error("Failed to save bus:", err);

      setFormError(
        err.response?.data?.message ||
          err.response?.data?.error ||
          "Unable to save the bus. Please try again.",
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(bus) {
    const confirmed = window.confirm(
      `Delete bus ${bus.plateNumber}? This action cannot be undone.`,
    );

    if (!confirmed) {
      return;
    }

    try {
      setDeletingId(bus.id);
      setError("");

      await deleteBus(bus.id);

      setBuses((current) => current.filter((item) => item.id !== bus.id));
    } catch (err) {
      console.error("Failed to delete bus:", err);

      setError(
        err.response?.data?.message ||
          err.response?.data?.error ||
          "Unable to delete the bus.",
      );
    } finally {
      setDeletingId(null);
    }
  }

  return (
    <section className="buses-page">
      <header className="buses-header">
        <div className="buses-header-copy">
          <h1>Buses</h1>

          <p>
            Manage the buses available to the BUSSIN transportation system.
          </p>
        </div>

        <button
          type="button"
          className="bus-primary-button"
          onClick={openCreateModal}
        >
          <Plus size={15} />
          Add Bus
        </button>
      </header>

      {error && <div className="buses-error">{error}</div>}

      <div className="buses-toolbar">
        <div className="buses-search">
          <Search size={16} />

          <input
            type="search"
            value={search}
            onChange={(event) => setSearch(event.target.value)}
            placeholder="Search plate number or model..."
            aria-label="Search buses"
          />
        </div>

        <select
          className="buses-filter"
          value={statusFilter}
          onChange={(event) => setStatusFilter(event.target.value)}
          aria-label="Filter buses by status"
        >
          <option value="">All statuses</option>

          {STATUS_OPTIONS.map((option) => (
            <option key={option.value} value={option.value}>
              {option.label}
            </option>
          ))}
        </select>
      </div>

      <div className="admin-dashboard-panel buses-table-card">
        {loading ? (
          <div className="buses-empty">
            <BusFront size={25} />

            <h3>Loading buses</h3>

            <p>We're getting the latest bus records from the server.</p>
          </div>
        ) : filteredBuses.length === 0 ? (
          <div className="buses-empty">
            <BusFront size={25} />

            <h3>
              {buses.length === 0 ? "No buses yet" : "No buses found"}
            </h3>

            <p>
              {buses.length === 0
                ? "Add the first bus to begin configuring BUSSIN operations."
                : "Try changing your search or status filter."}
            </p>
          </div>
        ) : (
          <>
            <div className="buses-table-wrap">
              <table className="buses-table">
                <thead>
                  <tr>
                    <th>Plate Number</th>
                    <th>Model</th>
                    <th>Capacity</th>
                    <th>Status</th>
                    <th>Updated</th>
                    <th>Actions</th>
                  </tr>
                </thead>

                <tbody>
                  {filteredBuses.map((bus) => (
                    <tr key={bus.id}>
                      <td>
                        <span className="bus-plate">
                          {bus.plateNumber}
                        </span>
                      </td>

                      <td>
                        <span className="bus-model">
                          {bus.model || "—"}
                        </span>
                      </td>

                      <td>{bus.capacity} seats</td>

                      <td>
                        <span
                          className={`bus-status ${getStatusClass(
                            bus.status,
                          )}`}
                        >
                          {getStatusLabel(bus.status)}
                        </span>
                      </td>

                      <td>
                        {bus.updatedAt
                          ? new Date(bus.updatedAt).toLocaleDateString(
                              "en-PH",
                              {
                                year: "numeric",
                                month: "short",
                                day: "numeric",
                              },
                            )
                          : "—"}
                      </td>

                      <td>
                        <div className="bus-actions">
                          <button
                            type="button"
                            className="bus-action"
                            title="Edit bus"
                            aria-label={`Edit ${bus.plateNumber}`}
                            onClick={() => openEditModal(bus)}
                          >
                            <Edit3 size={15} />
                          </button>

                          <button
                            type="button"
                            className="bus-action danger"
                            title="Delete bus"
                            aria-label={`Delete ${bus.plateNumber}`}
                            disabled={deletingId === bus.id}
                            onClick={() => handleDelete(bus)}
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

            <div className="buses-count">
              Showing {filteredBuses.length} of {buses.length} buses
            </div>
          </>
        )}
      </div>

      {modalOpen && (
        <div
          className="bus-modal-overlay"
          role="presentation"
          onMouseDown={(event) => {
            if (event.target === event.currentTarget) {
              closeModal();
            }
          }}
        >
          <div
            className="bus-modal"
            role="dialog"
            aria-modal="true"
            aria-labelledby="bus-modal-title"
          >
            <div className="bus-modal-header">
              <div>
                <h2 id="bus-modal-title">
                  {editingBus ? "Edit Bus" : "Add Bus"}
                </h2>

                <p>
                  {editingBus
                    ? "Update the bus information and operational status."
                    : "Register a new bus in the BUSSIN system."}
                </p>
              </div>

              <button
                type="button"
                className="bus-modal-close"
                onClick={closeModal}
                disabled={saving}
                aria-label="Close"
              >
                <X size={18} />
              </button>
            </div>

            <form className="bus-form" onSubmit={handleSubmit}>
              <div className="bus-form-grid">
                <div className="bus-form-field full">
                  <label htmlFor="bus-plate-number">
                    Plate Number
                  </label>

                  <input
                    id="bus-plate-number"
                    name="plateNumber"
                    value={form.plateNumber}
                    onChange={handleFormChange}
                    maxLength={20}
                    placeholder="e.g. ABC 1234"
                    autoComplete="off"
                    required
                  />
                </div>

                <div className="bus-form-field">
                  <label htmlFor="bus-model">Model</label>

                  <input
                    id="bus-model"
                    name="model"
                    value={form.model}
                    onChange={handleFormChange}
                    maxLength={100}
                    placeholder="e.g. Toyota Coaster"
                  />
                </div>

                <div className="bus-form-field">
                  <label htmlFor="bus-capacity">Capacity</label>

                  <input
                    id="bus-capacity"
                    name="capacity"
                    type="number"
                    min="1"
                    max="200"
                    step="1"
                    value={form.capacity}
                    onChange={handleFormChange}
                    placeholder="e.g. 36"
                    required
                  />
                </div>

                <div className="bus-form-field full">
                  <label htmlFor="bus-status">Status</label>

                  <select
                    id="bus-status"
                    name="status"
                    value={form.status}
                    onChange={handleFormChange}
                  >
                    {STATUS_OPTIONS.map((option) => (
                      <option key={option.value} value={option.value}>
                        {option.label}
                      </option>
                    ))}
                  </select>
                </div>
              </div>

              <p className="bus-form-hint">
                Bus capacity follows the server limit of 1–200 seats.
              </p>

              {formError && (
                <div className="bus-form-error">{formError}</div>
              )}

              <div className="bus-modal-actions">
                <button
                  type="button"
                  className="bus-secondary-button"
                  onClick={closeModal}
                  disabled={saving}
                >
                  Cancel
                </button>

                <button
                  type="submit"
                  className="bus-primary-button"
                  disabled={saving}
                >
                  {saving
                    ? "Saving..."
                    : editingBus
                      ? "Save Changes"
                      : "Create Bus"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </section>
  );
}

export default Buses;
