import { useCallback, useMemo, useState } from "react";
import RouteMap from "./RouteMap";
import "./PassengerFareMap.css";
import { quoteRouteFare } from "../../services/routeService";

const TYPES=[["REGULAR","Regular"],["STUDENT","Student"],["SENIOR","Senior Citizen"],["PWD","PWD"]];

export default function PassengerFareMap({route,onQuoteChange,onDropoffChange}){
 const [dropoff,setDropoff]=useState(null),[type,setType]=useState("REGULAR"),[quote,setQuote]=useState(null),[loading,setLoading]=useState(false),[error,setError]=useState("");
 const handlePoint=useCallback(async point=>{
  setDropoff(point);onDropoffChange?.(point);setQuote(null);onQuoteChange?.(null);setError("");setLoading(true);
  try{const next=await quoteRouteFare(route.id,{...point,passengerType:type});setQuote(next);onQuoteChange?.({...next,passengerType:type});}
  catch(e){setError(e.response?.data?.message||e.response?.data?.error||"Unable to calculate the fare.");}
  finally{setLoading(false);}
 },[route.id,type,onQuoteChange,onDropoffChange]);
 const origin=useMemo(()=>route.originLatitude&&route.originLongitude?{latitude:Number(route.originLatitude),longitude:Number(route.originLongitude)}:null,[route]);
 function changeType(e){const next=e.target.value;setType(next);setQuote(null);onQuoteChange?.(null);if(dropoff)handlePoint(dropoff);}
 return <div className="passenger-fare-map">
  <div className="route-map-help">Tap the map to pin your drop-off location. BUSSIN calculates the road distance from the route origin and applies the selected passenger discount.</div>
  <RouteMap origin={origin} destination={dropoff} geometry={route.routeGeometry} onPointSelect={handlePoint}/>
  <div className="route-map-points"><span className="route-map-point selected">Origin: {route.origin}</span>{dropoff&&<span className="route-map-point">Drop-off pinned</span>}</div>
  <label>Passenger type<select value={type} onChange={changeType}>{TYPES.map(([v,l])=><option key={v} value={v}>{l}</option>)}</select></label>
  {loading&&<p>Calculating road distance and fare…</p>}
  {error&&<p className="routes-error">{error}</p>}
  {quote&&<div className="fare-quote-card"><div>Distance <strong>{quote.distanceKm} km</strong></div><div>Regular fare <strong>₱{Number(quote.regularFare).toFixed(2)}</strong></div><div>Discount <strong>{quote.discountPercent}%</strong></div><div>Final fare <strong>₱{Number(quote.finalFare).toFixed(2)}</strong></div></div>}
 </div>;
}