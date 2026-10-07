import { useEffect, useState } from "react";
import { Edit3, MapPinned, Plus, Search, Trash2, X } from "lucide-react";
import { createRoute, deleteRoute, getAllRoutes, previewRoute, updateRoute } from "../../services/routeService";
import RouteMap from "../../components/map/RouteMap";
import "./Routes.css";

const EMPTY={routeIdentifier:"",origin:"",destination:"",distanceKm:"",durationMinutes:"",baseFare:"",farePerKm:"",description:"",active:true,originLatitude:null,originLongitude:null,destinationLatitude:null,destinationLongitude:null,routeGeometry:null};

function money(v){return Number.isFinite(Number(v))?new Intl.NumberFormat("en-PH",{style:"currency",currency:"PHP"}).format(Number(v)):"—";}

export default function Routes(){
 const [routes,setRoutes]=useState([]),[loading,setLoading]=useState(true),[error,setError]=useState(""),[search,setSearch]=useState(""),[status,setStatus]=useState("");
 const [modal,setModal]=useState(false),[editing,setEditing]=useState(null),[form,setForm]=useState(EMPTY),[formError,setFormError]=useState(""),[saving,setSaving]=useState(false),[previewing,setPreviewing]=useState(false);

 async function load(){try{setLoading(true);setError("");setRoutes(await getAllRoutes());}catch(e){setError(e.response?.data?.message||e.response?.data?.error||"Unable to load routes.");}finally{setLoading(false);}}
 useEffect(()=>{load();},[]);

 const filtered=routes.filter(r=>{
  const t=search.trim().toLowerCase();
  return (!t||String(r.id).includes(t)||r.routeIdentifier?.toLowerCase().includes(t)||r.origin?.toLowerCase().includes(t)||r.destination?.toLowerCase().includes(t))&&(!status||(status==="ACTIVE"?r.active:!r.active));
 });

 function openCreate(){setEditing(null);setForm(EMPTY);setFormError("");setModal(true);}
 function openEdit(r){setEditing(r);setForm({...EMPTY,...r,distanceKm:String(r.distanceKm??""),durationMinutes:String(r.durationMinutes??""),baseFare:String(r.baseFare??""),farePerKm:String(r.farePerKm??"")});setFormError("");setModal(true);}
 function close(){if(!saving&&!previewing){setModal(false);setEditing(null);setForm(EMPTY);}}

 function change(e){const {name,value,type,checked}=e.target;setForm(f=>({...f,[name]:type==="checkbox"?checked:value}));setFormError("");}

 function mapPoint(point){
  if(!form.originLatitude)setForm(f=>({...f,originLatitude:point.latitude,originLongitude:point.longitude}));
  else if(!form.destinationLatitude)setForm(f=>({...f,destinationLatitude:point.latitude,destinationLongitude:point.longitude}));
  else setForm(f=>({...f,destinationLatitude:point.latitude,destinationLongitude:point.longitude}));
  setFormError("");
 }

 async function calculateRoute(){
  if(form.originLatitude==null||form.destinationLatitude==null){setFormError("Click the map once for the origin and once for the destination.");return;}
  try{
   setPreviewing(true);setFormError("");
   const p=await previewRoute({originLatitude:Number(form.originLatitude),originLongitude:Number(form.originLongitude),destinationLatitude:Number(form.destinationLatitude),destinationLongitude:Number(form.destinationLongitude)});
   setForm(f=>({...f,distanceKm:String(p.distanceKm),durationMinutes:String(p.durationMinutes),routeGeometry:p.geometryJson}));
  }catch(e){setFormError(e.response?.data?.message||e.response?.data?.error||"Unable to calculate a road route.");}
  finally{setPreviewing(false);}
 }

 async function submit(e){
  e.preventDefault();
  const payload={routeIdentifier:form.routeIdentifier.trim(),origin:form.origin.trim(),destination:form.destination.trim(),distanceKm:Number(form.distanceKm),durationMinutes:Number(form.durationMinutes),baseFare:Number(form.baseFare),farePerKm:Number(form.farePerKm),originLatitude:Number(form.originLatitude),originLongitude:Number(form.originLongitude),destinationLatitude:Number(form.destinationLatitude),destinationLongitude:Number(form.destinationLongitude),routeGeometry:form.routeGeometry||null,description:form.description.trim()||null,...(editing?{active:form.active}:{})};
  if(!payload.routeIdentifier||!payload.origin||!payload.destination){setFormError("Route identifier, origin, and destination are required.");return;}
  if(!Number.isFinite(payload.distanceKm)||payload.distanceKm<=0||!Number.isInteger(payload.durationMinutes)||payload.durationMinutes<1){setFormError("Calculate a valid road route before saving.");return;}
  if(!Number.isFinite(payload.baseFare)||payload.baseFare<=0){setFormError("Minimum fare must be greater than PHP 0.");return;}
  if(!Number.isFinite(payload.farePerKm)||payload.farePerKm<0){setFormError("Fare per succeeding kilometer cannot be negative.");return;}
  try{setSaving(true);const saved=editing?await updateRoute(editing.id,payload):await createRoute(payload);setRoutes(rs=>{const next=editing?rs.map(r=>r.id===saved.id?saved:r):[...rs,saved];return next.sort((a,b)=>String(a.routeIdentifier).localeCompare(String(b.routeIdentifier)));});close();}catch(e){setFormError(e.response?.data?.message||e.response?.data?.error||"Unable to save route.");}finally{setSaving(false);}
 }
 async function remove(r){if(!window.confirm("Delete route "+r.routeIdentifier+"?"))return;try{await deleteRoute(r.id);setRoutes(rs=>rs.filter(x=>x.id!==r.id));}catch(e){setError(e.response?.data?.message||e.response?.data?.error||"Unable to delete route.");}}

 const origin=form.originLatitude!=null?{latitude:Number(form.originLatitude),longitude:Number(form.originLongitude)}:null;
 const destination=form.destinationLatitude!=null?{latitude:Number(form.destinationLatitude),longitude:Number(form.destinationLongitude)}:null;

 return <section className="routes-admin-page">
  <header className="routes-admin-header"><div><h1>Routes</h1><p>Create road-based bus routes, map locations, and fare rules.</p></div><button className="route-primary-button" onClick={openCreate}><Plus size={15}/> Add Route</button></header>
  {error&&<div className="routes-error">{error}</div>}
  <div className="routes-toolbar"><div className="routes-search"><Search size={16}/><input value={search} onChange={e=>setSearch(e.target.value)} placeholder="Search route, origin, destination..."/></div><select value={status} onChange={e=>setStatus(e.target.value)}><option value="">All statuses</option><option value="ACTIVE">Active</option><option value="INACTIVE">Inactive</option></select></div>
  <div className="admin-dashboard-panel routes-table-card">{loading?<div className="routes-empty"><MapPinned size={25}/><h3>Loading routes</h3></div>:filtered.length===0?<div className="routes-empty"><MapPinned size={25}/><h3>No routes found</h3><p>Add a route and use the map to define its road path.</p></div>:<div className="routes-table-wrap"><table className="routes-table"><thead><tr><th>Route</th><th>Origin</th><th>Destination</th><th>Distance</th><th>Minimum Fare</th><th>Per KM</th><th>Status</th><th>Actions</th></tr></thead><tbody>{filtered.map(r=><tr key={r.id}><td><strong>{r.routeIdentifier}</strong><small>#{r.id}</small></td><td>{r.origin}</td><td>{r.destination}</td><td>{r.distanceKm} km</td><td>{money(r.baseFare)}</td><td>{money(r.farePerKm)}</td><td><span className={"route-status "+(r.active?"active":"inactive")}>{r.active?"Active":"Inactive"}</span></td><td><div className="route-actions"><button className="route-action" onClick={()=>openEdit(r)} title="Edit"><Edit3 size={15}/></button><button className="route-action danger" onClick={()=>remove(r)} title="Delete"><Trash2 size={15}/></button></div></td></tr>)}</tbody></table></div>}</div>
  {modal&&<div className="route-modal-overlay" onMouseDown={e=>e.target===e.currentTarget&&close()}><div className="route-modal"><div className="route-modal-header"><div><h2>{editing?"Edit Route":"Add Route"}</h2><p>Select two points on the map to build the road route.</p></div><button className="route-modal-close" onClick={close} disabled={saving||previewing}><X size={18}/></button></div>
   <form className="route-form" onSubmit={submit}>
    <div className="route-map-help">First click = origin. Second click = destination. Then calculate the road route. The server stores the resulting geometry and distance.</div>
    <RouteMap origin={origin} destination={destination} geometry={form.routeGeometry} onPointSelect={mapPoint}/>
    <div className="route-form-grid">
     <Field label="Route Identifier" name="routeIdentifier" value={form.routeIdentifier} onChange={change} placeholder="e.g. BAC-MNL-01"/>
     <Field label="Origin Name" name="origin" value={form.origin} onChange={change} placeholder="e.g. Bacoor"/>
     <Field label="Destination Name" name="destination" value={form.destination} onChange={change} placeholder="e.g. Manila"/>
     <div className="route-form-field"><label>Calculated Distance</label><input value={form.distanceKm?form.distanceKm+" km":"Not calculated"} readOnly/></div>
     <div className="route-form-field"><label>Estimated Duration</label><input value={form.durationMinutes?form.durationMinutes+" min":"Not calculated"} readOnly/></div>
     <Field label="Minimum Fare (first 5 km)" name="baseFare" value={form.baseFare} onChange={change} type="number" min="0.01" step="0.01" placeholder="e.g. 15.00"/>
     <Field label="Fare / succeeding km" name="farePerKm" value={form.farePerKm} onChange={change} type="number" min="0" step="0.01" placeholder="e.g. 2.49"/>
     <div className="route-form-field full"><label>Description</label><textarea name="description" value={form.description||""} onChange={change} rows="2"/></div>
     {editing&&<label className="route-active-toggle"><input type="checkbox" name="active" checked={Boolean(form.active)} onChange={change}/> Route is active</label>}
    </div>
    <button type="button" className="route-secondary-button route-calculate-button" onClick={calculateRoute} disabled={previewing}>{previewing?"Calculating road route…":"Calculate Road Route"}</button>
    {formError&&<div className="route-form-error">{formError}</div>}
    <div className="route-modal-actions"><button type="button" className="route-secondary-button" onClick={close} disabled={saving||previewing}>Cancel</button><button className="route-primary-button" disabled={saving||previewing}>{saving?"Saving…":editing?"Save Changes":"Create Route"}</button></div>
   </form>
  </div></div>}
 </section>;
}

function Field({label,name,value,onChange,placeholder,type="text",min,step}){return <div className="route-form-field"><label htmlFor={name}>{label}</label><input id={name} name={name} value={value??""} onChange={onChange} placeholder={placeholder} type={type} min={min} step={step} required/></div>;}
