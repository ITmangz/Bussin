import { useCallback, useEffect, useMemo, useState } from "react";
import {
  BriefcaseBusiness,
  Edit3,
  Search,
  ShieldCheck,
  UserPlus,
  Users,
  X,
} from "lucide-react";

import AppCard from "../../components/ui/AppCard";
import { useAuth } from "../../contexts/AuthContext";
import {
  getAllUsers,
  getEmployees,
  updateManagedUser,
  updateUserRole,
} from "../../services/userService";
import "./UserManagement.css";

const EMPTY_FORM = {
  firstName: "",
  middleName: "",
  lastName: "",
  contactNumber: "",
  address: "",
};

function getName(user) {
  return [user.firstName, user.middleName, user.lastName]
    .filter(Boolean)
    .join(" ") || "Name not provided";
}

function getErrorMessage(error, fallback) {
  return (
    error.response?.data?.message ||
    error.response?.data?.error ||
    fallback
  );
}

function UserManagement({ mode }) {
  const isEmployeesPage = mode === "employees";
  const { profile } = useAuth();
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [search, setSearch] = useState("");
  const [roleFilter, setRoleFilter] = useState("");
  const [editingUser, setEditingUser] = useState(null);
  const [form, setForm] = useState(EMPTY_FORM);
  const [formError, setFormError] = useState("");
  const [savingProfile, setSavingProfile] = useState(false);
  const [updatingRoleId, setUpdatingRoleId] = useState(null);
  const [employeeModalOpen, setEmployeeModalOpen] = useState(false);
  const [candidates, setCandidates] = useState([]);
  const [candidateSearch, setCandidateSearch] = useState("");
  const [loadingCandidates, setLoadingCandidates] = useState(false);

  const loadUsers = useCallback(async () => {
    try {
      const response = isEmployeesPage ? await getEmployees() : await getAllUsers();
      setError("");
      setUsers(Array.isArray(response) ? response : []);
    } catch (loadError) {
      console.error("Failed to load account directory:", loadError);
      setError(getErrorMessage(loadError, "Unable to load accounts from BUSSIN."));
    } finally {
      setLoading(false);
    }
  }, [isEmployeesPage]);

  useEffect(() => {
    void Promise.resolve().then(loadUsers);
  }, [loadUsers]);

  const filteredUsers = useMemo(() => {
    const query = search.trim().toLowerCase();

    return users
      .filter((user) => !roleFilter || user.role === roleFilter)
      .filter((user) => {
        if (!query) return true;
        return [getName(user), user.email, user.contactNumber, user.address]
          .some((value) => String(value || "").toLowerCase().includes(query));
      })
      .sort((left, right) => getName(left).localeCompare(getName(right)));
  }, [users, search, roleFilter]);

  const counts = useMemo(() => ({
    commuters: users.filter((user) => user.role === "COMMUTER").length,
    employees: users.filter((user) => user.role === "EMPLOYEE").length,
    admins: users.filter((user) => user.role === "ADMIN").length,
  }), [users]);

  function refreshUsers() {
    setLoading(true);
    setError("");
    loadUsers();
  }

  function openProfileEditor(user) {
    setEditingUser(user);
    setForm({
      firstName: user.firstName || "",
      middleName: user.middleName || "",
      lastName: user.lastName || "",
      contactNumber: user.contactNumber || "",
      address: user.address || "",
    });
    setFormError("");
  }

  function closeProfileEditor() {
    if (savingProfile) return;
    setEditingUser(null);
    setForm(EMPTY_FORM);
    setFormError("");
  }

  function handleFormChange(event) {
    const { name, value } = event.target;
    setForm((current) => ({ ...current, [name]: value }));
    setFormError("");
  }

  async function handleProfileSave(event) {
    event.preventDefault();
    if (!editingUser) return;

    const payload = {
      firstName: form.firstName.trim(),
      middleName: form.middleName.trim() || null,
      lastName: form.lastName.trim(),
      contactNumber: form.contactNumber.trim() || null,
      address: form.address.trim() || null,
    };

    if (payload.firstName.length < 2 || payload.lastName.length < 2) {
      setFormError("First and last name must each contain at least 2 characters.");
      return;
    }

    try {
      setSavingProfile(true);
      setFormError("");
      const updated = await updateManagedUser(editingUser.id, payload);
      setUsers((current) => current.map((user) => user.id === updated.id ? updated : user));
      setEditingUser(null);
    } catch (saveError) {
      console.error("Failed to update account:", saveError);
      setFormError(getErrorMessage(saveError, "Unable to save account details."));
    } finally {
      setSavingProfile(false);
    }
  }

  async function changeRole(user, nextRole, { confirm = true } = {}) {
    if (nextRole === user.role || updatingRoleId !== null) return false;

    if (profile?.id === user.id) {
      setError("You cannot change the role of the account you are currently using.");
      return false;
    }

    const action = nextRole === "EMPLOYEE" ? "grant employee access to" :
      nextRole === "COMMUTER" && user.role === "EMPLOYEE" ? "revoke employee access for" :
        `change the role for`;
    if (confirm && !window.confirm(`Are you sure you want to ${action} ${getName(user)}?`)) return;

    try {
      setUpdatingRoleId(user.id);
      setError("");
      const updated = await updateUserRole(user.id, nextRole);
      setUsers((current) => {
        if (!isEmployeesPage) {
          return current.map((item) => item.id === updated.id ? updated : item);
        }
        if (updated.role !== "EMPLOYEE") {
          return current.filter((item) => item.id !== updated.id);
        }
        return current.some((item) => item.id === updated.id)
          ? current.map((item) => item.id === updated.id ? updated : item)
          : [...current, updated];
      });
      setCandidates((current) => current.filter((item) => item.id !== updated.id));
      return true;
    } catch (roleError) {
      console.error("Failed to update account role:", roleError);
      setError(getErrorMessage(roleError, "Unable to update account access."));
      return false;
    } finally {
      setUpdatingRoleId(null);
    }
  }

  async function openEmployeeModal() {
    setEmployeeModalOpen(true);
    setCandidateSearch("");
    setLoadingCandidates(true);
    try {
      const response = await getAllUsers();
      setCandidates(Array.isArray(response)
        ? response.filter((user) => user.role === "COMMUTER")
        : []);
    } catch (candidateError) {
      console.error("Failed to load employee candidates:", candidateError);
      setError(getErrorMessage(candidateError, "Unable to load commuter accounts."));
      setEmployeeModalOpen(false);
    } finally {
      setLoadingCandidates(false);
    }
  }

  const filteredCandidates = candidates.filter((user) => {
    const query = candidateSearch.trim().toLowerCase();
    return !query || [getName(user), user.email, user.contactNumber]
      .some((value) => String(value || "").toLowerCase().includes(query));
  });

  async function addEmployee(user) {
    const updated = await changeRole(user, "EMPLOYEE", { confirm: false });
    if (updated) {
      setEmployeeModalOpen(false);
    }
  }

  return (
    <section className="user-management-page">
      <header className="user-management-header">
        <div>
          <h1>{isEmployeesPage ? "Employees" : "Users"}</h1>
          <p>
            {isEmployeesPage
              ? "Manage employee access for registered BUSSIN accounts."
              : "Manage BUSSIN account details and access levels."}
          </p>
        </div>
        {isEmployeesPage && (
          <button type="button" className="user-management-primary" onClick={openEmployeeModal}>
            <UserPlus size={17} />
            Add employee
          </button>
        )}
      </header>

      {error && (
        <div className="user-management-alert" role="alert">
          <span>{error}</span>
          <button type="button" aria-label="Dismiss message" onClick={() => setError("")}><X size={16} /></button>
        </div>
      )}

      <div className="user-management-stats">
        {isEmployeesPage ? (
          <StatCard icon={BriefcaseBusiness} label="Employees" value={counts.employees} />
        ) : (
          <>
            <StatCard icon={Users} label="Total accounts" value={users.length} />
            <StatCard icon={UserPlus} label="Commuters" value={counts.commuters} />
            <StatCard icon={BriefcaseBusiness} label="Employees" value={counts.employees} />
            <StatCard icon={ShieldCheck} label="Administrators" value={counts.admins} />
          </>
        )}
      </div>

      <div className="user-management-toolbar">
        <label className="user-management-search">
          <Search size={17} />
          <input
            type="search"
            value={search}
            onChange={(event) => setSearch(event.target.value)}
            placeholder="Search by name, email, or contact"
            aria-label="Search accounts"
          />
        </label>
        {!isEmployeesPage && (
          <select
            className="user-management-role-filter"
            value={roleFilter}
            onChange={(event) => setRoleFilter(event.target.value)}
            aria-label="Filter by account role"
          >
            <option value="">All roles</option>
            <option value="COMMUTER">Commuter</option>
            <option value="EMPLOYEE">Employee</option>
            <option value="ADMIN">Administrator</option>
          </select>
        )}
        <button type="button" className="user-management-refresh" onClick={refreshUsers} disabled={loading}>
          Refresh
        </button>
      </div>

      <AppCard className="user-management-table-card">
        {loading ? (
          <div className="user-management-state">Loading {isEmployeesPage ? "employees" : "accounts"}…</div>
        ) : filteredUsers.length === 0 ? (
          <div className="user-management-state">
            <div className="user-management-empty-icon"><Users size={22} /></div>
            <strong>{search || roleFilter ? "No matching accounts" : isEmployeesPage ? "No employees yet" : "No accounts found"}</strong>
            <span>{isEmployeesPage
              ? "Add employee access to a registered commuter account to get started."
              : "Accounts appear here after users register for BUSSIN."}</span>
          </div>
        ) : (
          <div className="user-management-table-wrap">
            <table className="user-management-table">
              <thead>
                <tr>
                  <th>Account</th>
                  <th>Contact</th>
                  {!isEmployeesPage && <th>Access level</th>}
                  <th>Joined</th>
                  <th><span className="sr-only">Actions</span></th>
                </tr>
              </thead>
              <tbody>
                {filteredUsers.map((user) => (
                  <tr key={user.id}>
                    <td>
                      <div className="user-management-person">
                        <div className="user-management-avatar">{getName(user).charAt(0).toUpperCase()}</div>
                        <div className="user-management-person-copy">
                          <strong>{getName(user)}</strong>
                          <span>{user.email || "No email"}</span>
                        </div>
                      </div>
                    </td>
                    <td>{user.contactNumber || <span className="user-management-muted">Not provided</span>}</td>
                    {!isEmployeesPage && (
                      <td>
                        <select
                          className={`user-management-role ${String(user.role || "").toLowerCase()}`}
                          value={user.role}
                          disabled={updatingRoleId !== null || profile?.id === user.id}
                          onChange={(event) => changeRole(user, event.target.value)}
                          aria-label={`Change access level for ${getName(user)}`}
                        >
                          <option value="COMMUTER">Commuter</option>
                          <option value="EMPLOYEE">Employee</option>
                          <option value="ADMIN">Administrator</option>
                        </select>
                      </td>
                    )}
                    <td>{formatDate(user.createdAt)}</td>
                    <td>
                      <div className="user-management-actions">
                        <button
                          type="button"
                          className="user-management-icon-button"
                          onClick={() => openProfileEditor(user)}
                          aria-label={`Edit ${getName(user)}`}
                          title="Edit account details"
                        >
                          <Edit3 size={16} />
                        </button>
                        {isEmployeesPage && (
                          <button
                            type="button"
                            className="user-management-text-button"
                            disabled={updatingRoleId !== null}
                            onClick={() => changeRole(user, "COMMUTER")}
                          >
                            Revoke access
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
        {!loading && filteredUsers.length > 0 && (
          <div className="user-management-table-footer">
            Showing {filteredUsers.length} of {users.length} {isEmployeesPage ? "employees" : "accounts"}
          </div>
        )}
      </AppCard>

      {editingUser && (
        <div className="user-management-overlay" onMouseDown={(event) => event.target === event.currentTarget && closeProfileEditor()}>
          <form className="user-management-modal" onSubmit={handleProfileSave} role="dialog" aria-modal="true" aria-labelledby="user-edit-title">
            <div className="user-management-modal-header">
              <div>
                <h2 id="user-edit-title">Edit account</h2>
                <p>{editingUser.email || "Email not provided"}</p>
              </div>
              <button type="button" className="user-management-icon-button" onClick={closeProfileEditor} aria-label="Close dialog"><X size={18} /></button>
            </div>
            <div className="user-management-form-grid">
              <label>First name<input name="firstName" value={form.firstName} onChange={handleFormChange} required maxLength={50} /></label>
              <label>Middle name<input name="middleName" value={form.middleName} onChange={handleFormChange} maxLength={50} /></label>
              <label>Last name<input name="lastName" value={form.lastName} onChange={handleFormChange} required maxLength={50} /></label>
              <label>Contact number<input name="contactNumber" value={form.contactNumber} onChange={handleFormChange} maxLength={40} /></label>
              <label className="full-width">Address<input name="address" value={form.address} onChange={handleFormChange} maxLength={255} /></label>
            </div>
            {formError && <div className="user-management-form-error" role="alert">{formError}</div>}
            <div className="user-management-modal-actions">
              <button type="button" className="user-management-secondary" onClick={closeProfileEditor} disabled={savingProfile}>Cancel</button>
              <button type="submit" className="user-management-primary" disabled={savingProfile}>{savingProfile ? "Saving…" : "Save changes"}</button>
            </div>
          </form>
        </div>
      )}

      {employeeModalOpen && (
        <div className="user-management-overlay" onMouseDown={(event) => event.target === event.currentTarget && setEmployeeModalOpen(false)}>
          <div className="user-management-modal employee-picker" role="dialog" aria-modal="true" aria-labelledby="employee-picker-title">
            <div className="user-management-modal-header">
              <div>
                <h2 id="employee-picker-title">Add an employee</h2>
                <p>Choose a registered commuter account to grant employee access.</p>
              </div>
              <button type="button" className="user-management-icon-button" onClick={() => setEmployeeModalOpen(false)} aria-label="Close dialog"><X size={18} /></button>
            </div>
            <label className="user-management-search employee-picker-search">
              <Search size={17} />
              <input type="search" value={candidateSearch} onChange={(event) => setCandidateSearch(event.target.value)} placeholder="Search commuter accounts" aria-label="Search commuter accounts" />
            </label>
            <div className="employee-picker-list">
              {loadingCandidates ? (
                <div className="user-management-state">Loading commuter accounts…</div>
              ) : filteredCandidates.length === 0 ? (
                <div className="user-management-state">No commuter accounts available.</div>
              ) : filteredCandidates.map((user) => (
                <div className="employee-picker-row" key={user.id}>
                  <div className="user-management-person">
                    <div className="user-management-avatar">{getName(user).charAt(0).toUpperCase()}</div>
                    <div className="user-management-person-copy">
                      <strong>{getName(user)}</strong>
                      <span>{user.email || "No email"}</span>
                    </div>
                  </div>
                  <button
                    type="button"
                    className="user-management-secondary compact"
                    disabled={updatingRoleId !== null}
                    onClick={() => addEmployee(user)}
                  >
                    {updatingRoleId === user.id ? "Adding…" : "Add"}
                  </button>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}
    </section>
  );
}

function StatCard({ icon: Icon, label, value }) {
  return (
    <AppCard className="user-management-stat-card">
      <div className="user-management-stat-icon"><Icon size={18} /></div>
      <div><span>{label}</span><strong>{value}</strong></div>
    </AppCard>
  );
}

function formatDate(value) {
  if (!value) return "—";
  const date = new Date(value);
  return Number.isNaN(date.getTime())
    ? "—"
    : date.toLocaleDateString(undefined, { year: "numeric", month: "short", day: "numeric" });
}

export default UserManagement;
