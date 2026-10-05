import { useEffect, useMemo, useState } from "react";
import { CalendarClock, Edit3, Plus, Search, Trash2, X } from "lucide-react";
import { getAllBuses } from "../../services/busService";
import { createTrip, deleteTrip, getAllTrips, updateTrip } from "../../services/tripService";
import "./Trips.css";

const STATUS_OPTIONS = [
  { value: "SCHEDULED", label: "Scheduled" },
  { value: "BOARDING", label: "Boarding" },
  { value: "DEPARTED", label: "Departed" },
  { value: "COMPLETED", label: "Completed" },
  { value: "CANCELLED", label: "Cancelled" },
];
const EMPTY_FORM = { busId: "", routeId: "", scheduledDeparture: "", scheduledArrival: "", status: "SCHEDULED" };

function formatDateTime(value) {
  if (!value) return "—";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "—";
  return date.toLocaleString("en-PH", { year:"numeric", month:"short", day:"numeric", hour:"numeric", minute:"2-digit" });
}
function toInputDateTime(value) {
  if (!value) return "";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "";
  const pad = (n) => String(n).padStart(2, "0");
  return date.getFullYear()+"-"+pad(date.getMonth()+1)+"-"+pad(date.getDate())+"T"+pad(date.getHours())+":"+pad(date.getMinutes());
}

function Trips() {
  const [trips,setTrips]=useState([]), [buses,setBuses]=useState([]), [loading,setLoading]=useState(true);
  const [error,setError]=useState(""), [search,setSearch]=useState(""), [statusFilter,setStatusFilter]=useState("");
  const [modalOpen,setModalOpen]=useState(false), [editingTrip,setEditingTrip]=useState(null);
  const [form,setForm]=useState(EMPTY_FORM), [formError,setFormError]=useState(""), [saving,setSaving]=useState(false), [deletingId,setDeletingId]=useState(null);

  useEffect(() => {
    async function loadData() {
      try {
        setLoading(true); setError("");
        const [tripData,busData]=await Promise.all([getAllTrips(),getAllBuses()]);
        setTrips(Array.isArray(tripData)?tripData:[]); setBuses(Array.isArray(busData)?busData:[]);
      } catch(err) {
        console.error("Failed to load trips:",err);
        setError(err.response?.data?.message||"Unable to load trips from the BUSSIN server.");
      } finally { setLoading(false); }
    }
    loadData();
  },[]);

  const filteredTrips=useMemo(()=>{
    const query=search.trim().toLowerCase();
    return trips.filter(trip=>{
      const matches=!query || String(trip.id??"").includes(query) || trip.routeIdentifier?.toLowerCase().includes(query) || trip.busPlateNumber?.toLowerCase().includes(query);
      return matches && (!statusFilter || trip.status===statusFilter);
    });
  },[trips,search,statusFilter]);

  function openCreateModal(){ setEditingTrip(null); setForm({...EMPTY_FORM,busId:buses[0]?.id?String(buses[0].id):""}); setFormError(""); setModalOpen(true); }
  function openEditModal(trip){ setEditingTrip(trip); setForm({busId:String(trip.busId??""),routeId:String(trip.routeId??""),scheduledDeparture:toInputDateTime(trip.scheduledDeparture),scheduledArrival:toInputDateTime(trip.scheduledArrival),status:trip.status||"SCHEDULED"}); setFormError(""); setModalOpen(true); }
  function closeModal(){ if(saving)return; setModalOpen(false); setEditingTrip(null); setForm(EMPTY_FORM); setFormError(""); }
  function handleChange(e){ setForm(c=>({...c,[e.target.name]:e.target.value})); setFormError(""); }

  async function handleSubmit(e){
    e.preventDefault();
    const busId=Number(form.busId), routeId=Number(form.routeId), departure=new Date(form.scheduledDeparture), arrival=new Date(form.scheduledArrival);
    if(!Number.isInteger(busId)||busId<1)return setFormError("Please select a bus.");
    if(!Number.isInteger(routeId)||routeId<1)return setFormError("Route ID is required. Enter an existing route ID.");
    if(!form.scheduledDeparture||Number.isNaN(departure.getTime()))return setFormError("Scheduled departure is required.");
    if(!form.scheduledArrival||Number.isNaN(arrival.getTime()))return setFormError("Scheduled arrival is required.");
    if(arrival<departure)return setFormError("Scheduled arrival must not be before departure.");
    try{
      setSaving(true); setFormError("");
      const payload={busId,routeId,scheduledDeparture:form.scheduledDeparture,scheduledArrival:form.scheduledArrival,status:form.status};
      const saved=editingTrip?await updateTrip(editingTrip.id,payload):await createTrip(payload);
      setTrips(current=>{
        const next=editingTrip?current.map(item=>item.id===saved.id?saved:item):[...current,saved];
        return next.sort((a,b)=>new Date(a.scheduledDeparture)-new Date(b.scheduledDeparture));
      });
      closeModal();
    }catch(err){ console.error("Failed to save trip:",err); setFormError(err.response?.data?.message||err.response?.data?.error||"Unable to save the trip."); }
    finally{setSaving(false);}
  }

  async function handleDelete(trip){
    if(!window.confirm("Delete Trip #"+trip.id+"? This action cannot be undone."))return;
    try{setDeletingId(trip.id);setError("");await deleteTrip(trip.id);setTrips(c=>c.filter(i=>i.id!==trip.id));}
    catch(err){console.error("Failed to delete trip:",err);setError(err.response?.data?.message||err.response?.data?.error||"Unable to delete the trip.");}
    finally{setDeletingId(null);}
  }

  return <section className="trips-admin-page">
    <header className="trips-admin-header"><div><h1>Trips</h1><p>Schedule and manage bus trips using the existing BUSSIN trip API.</p></div><button type="button" className="trip-primary-button" onClick={openCreateModal}><Plus size={15}/> Add Trip</button></header>
    {error&&<div className="trips-error">{error}</div>}
    <div className="trips-toolbar">
      <div className="trips-search"><Search size={16}/><input type="search" value={search} onChange={e=>setSearch(e.target.value)} placeholder="Search trip, route, or plate..." aria-label="Search trips"/></div>
      <select value={statusFilter} onChange={e=>setStatusFilter(e.target.value)} aria-label="Filter trips by status"><option value="">All statuses</option>{STATUS_OPTIONS.map(o=><option key={o.value} value={o.value}>{o.label}</option>)}</select>
    </div>
    <div className="admin-dashboard-panel trips-table-card">
      {loading?<div className="trips-empty"><CalendarClock size={25}/><h3>Loading trips</h3><p>Getting the latest schedules from the server.</p></div>:
      filteredTrips.length===0?<div className="trips-empty"><CalendarClock size={25}/><h3>{trips.length?"No trips found":"No trips yet"}</h3><p>{trips.length?"Try changing your search or status filter.":"Create a trip once an existing route and bus are available."}</p></div>:
      <><div className="trips-table-wrap"><table className="trips-table"><thead><tr><th>Trip</th><th>Route</th><th>Bus</th><th>Departure</th><th>Arrival</th><th>Status</th><th>Actions</th></tr></thead><tbody>
      {filteredTrips.map(trip=><tr key={trip.id}><td><strong>#{trip.id}</strong></td><td><span className="trip-route">{trip.routeIdentifier||"Route #"+trip.routeId}</span></td><td><span className="trip-bus">{trip.busPlateNumber||"Bus #"+trip.busId}</span><small>{trip.busCapacity?trip.busCapacity+" seats":"—"}</small></td><td>{formatDateTime(trip.scheduledDeparture)}</td><td>{formatDateTime(trip.scheduledArrival)}</td><td><span className={"trip-status "+String(trip.status||"").toLowerCase()}>{STATUS_OPTIONS.find(s=>s.value===trip.status)?.label||trip.status||"Unknown"}</span></td><td><div className="trip-actions"><button type="button" className="trip-action" onClick={()=>openEditModal(trip)} aria-label={"Edit trip "+trip.id} title="Edit trip"><Edit3 size={15}/></button><button type="button" className="trip-action danger" onClick={()=>handleDelete(trip)} disabled={deletingId===trip.id} aria-label={"Delete trip "+trip.id} title="Delete trip"><Trash2 size={15}/></button></div></td></tr>)}
      </tbody></table></div><div className="trips-count">Showing {filteredTrips.length} of {trips.length} trips</div></>}
    </div>
    {modalOpen&&<div className="trip-modal-overlay" role="presentation" onMouseDown={e=>e.target===e.currentTarget&&closeModal()}><div className="trip-modal" role="dialog" aria-modal="true" aria-labelledby="trip-modal-title">
      <div className="trip-modal-header"><div><h2 id="trip-modal-title">{editingTrip?"Edit Trip":"Add Trip"}</h2><p>Configure the bus, route, schedule, and status.</p></div><button type="button" className="trip-modal-close" onClick={closeModal} disabled={saving} aria-label="Close"><X size={18}/></button></div>
      <form className="trip-form" onSubmit={handleSubmit}><div className="trip-form-grid">
        <div className="trip-form-field"><label htmlFor="trip-bus">Bus</label><select id="trip-bus" name="busId" value={form.busId} onChange={handleChange} required><option value="">Select bus</option>{buses.map(bus=><option key={bus.id} value={bus.id}>{bus.plateNumber} — {bus.model||"Bus"} ({bus.capacity} seats)</option>)}</select></div>
        <div className="trip-form-field"><label htmlFor="trip-route-id">Route ID</label><input id="trip-route-id" name="routeId" type="number" min="1" step="1" value={form.routeId} onChange={handleChange} placeholder="e.g. 1" required/><small>The current backend has no Route CRUD endpoint, so an existing route ID is entered directly.</small></div>
        <div className="trip-form-field"><label htmlFor="trip-departure">Scheduled departure</label><input id="trip-departure" name="scheduledDeparture" type="datetime-local" value={form.scheduledDeparture} onChange={handleChange} required/></div>
        <div className="trip-form-field"><label htmlFor="trip-arrival">Scheduled arrival</label><input id="trip-arrival" name="scheduledArrival" type="datetime-local" value={form.scheduledArrival} onChange={handleChange} required/></div>
        <div className="trip-form-field full"><label htmlFor="trip-status">Status</label><select id="trip-status" name="status" value={form.status} onChange={handleChange}>{STATUS_OPTIONS.map(o=><option key={o.value} value={o.value}>{o.label}</option>)}</select></div>
      </div>{formError&&<div className="trip-form-error">{formError}</div>}<div className="trip-modal-actions"><button type="button" className="trip-secondary-button" onClick={closeModal} disabled={saving}>Cancel</button><button type="submit" className="trip-primary-button" disabled={saving}>{saving?"Saving...":editingTrip?"Save Changes":"Create Trip"}</button></div></form>
    </div></div>}
  </section>;
}
export default Trips;
