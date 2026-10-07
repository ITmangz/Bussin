import { useEffect, useRef } from "react";
import { Map, NavigationControl, Marker, Popup } from "maplibre-gl";
import "maplibre-gl/dist/maplibre-gl.css";
import "./RouteMap.css";

const DEFAULT_CENTER=[120.9842,14.5995];

const OSM_STYLE={
 version:8,
 sources:{osm:{type:"raster",tiles:["https://tile.openstreetmap.org/{z}/{x}/{y}.png"],tileSize:256,attribution:"© OpenStreetMap contributors"}},
 layers:[{id:"osm",type:"raster",source:"osm"}]
};

function addRouteLayer(map,geometry){
 if(!geometry)return;
 const data={type:"Feature",geometry:typeof geometry==="string"?JSON.parse(geometry):geometry};
 if(map.getSource("bussin-route")) map.getSource("bussin-route").setData(data);
 else {
  map.addSource("bussin-route",{type:"geojson",data});
  map.addLayer({id:"bussin-route-line",type:"line",source:"bussin-route",paint:{"line-color":"#990000","line-width":5,"line-opacity":0.9}});
 }
}

export default function RouteMap({origin,destination,geometry,onPointSelect,interactive=true}){
 const containerRef=useRef(null),mapRef=useRef(null),markersRef=useRef([]);
 useEffect(()=>{
  if(!containerRef.current)return;
  const map=new Map({container:containerRef.current,style:OSM_STYLE,center:DEFAULT_CENTER,zoom:11});
  map.addControl(new NavigationControl(),"top-right");
  map.on("load",()=>{if(geometry)addRouteLayer(map,geometry);});
  if(interactive) map.on("click",e=>onPointSelect?.({latitude:e.lngLat.lat,longitude:e.lngLat.lng}));
  mapRef.current=map;
  return()=>{map.remove();mapRef.current=null;};
 },[interactive,onPointSelect]);
 useEffect(()=>{
  const map=mapRef.current;if(!map)return;
  markersRef.current.forEach(m=>m.remove());markersRef.current=[];
  [[origin,"Origin"],[destination,"Destination"]].forEach(([point,label])=>{
   if(!point)return;
   const popup=new Popup({offset:18}).setText(label);
   const marker=new Marker({color:label==="Origin"?"#990000":"#121212"}).setLngLat([point.longitude,point.latitude]).setPopup(popup).addTo(map);
   markersRef.current.push(marker);
  });
  if(geometry&&map.isStyleLoaded())addRouteLayer(map,geometry);
 },[origin,destination,geometry]);
 return <div ref={containerRef} className="bussin-map" aria-label="Interactive route map"/>;
}