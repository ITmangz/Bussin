import { useEffect, useRef } from "react";
import {
  LngLatBounds,
  Map,
  Marker,
  NavigationControl,
  Popup,
  setWorkerUrl,
} from "maplibre-gl";

import maplibreWorker from "maplibre-gl/dist/maplibre-gl-worker.mjs?worker&url";

import "maplibre-gl/dist/maplibre-gl.css";
import "./RouteMap.css";

setWorkerUrl(maplibreWorker);

const DEFAULT_CENTER = [120.9842, 14.5995];

const OSM_STYLE = {
  version: 8,
  sources: {
    osm: {
      type: "raster",
      tiles: ["https://tile.openstreetmap.org/{z}/{x}/{y}.png"],
      tileSize: 256,
      attribution: "© OpenStreetMap contributors",
    },
  },
  layers: [
    {
      id: "osm",
      type: "raster",
      source: "osm",
    },
  ],
};

function normalizeGeometry(value) {
  if (!value) {
    console.warn("BUSSIN MAP: No route geometry provided.");
    return null;
  }

  let parsed = value;

  if (typeof parsed === "string") {
    try {
      parsed = JSON.parse(parsed);
    } catch (error) {
      console.error("BUSSIN MAP: Failed to parse route geometry:", error);
      return null;
    }
  }

  if (!parsed || typeof parsed !== "object") {
    return null;
  }

  if (parsed.type === "Feature") {
    return parsed.geometry || null;
  }

  if (parsed.type === "FeatureCollection") {
    const feature = parsed.features?.find((item) => item?.geometry);

    return feature?.geometry || null;
  }

  if (parsed.type === "LineString") {
    return parsed;
  }

  console.error("BUSSIN MAP: Unsupported geometry:", parsed);

  return null;
}

function buildRouteFeature(geometry) {
  const normalized = normalizeGeometry(geometry);

  if (!normalized) {
    return null;
  }

  if (normalized.type !== "LineString") {
    console.error("BUSSIN MAP: Expected LineString:", normalized.type);
    return null;
  }

  if (
    !Array.isArray(normalized.coordinates) ||
    normalized.coordinates.length < 2
  ) {
    console.error(
      "BUSSIN MAP: Invalid route coordinates:",
      normalized.coordinates,
    );
    return null;
  }

  const coordinates = normalized.coordinates.map((coordinate) => [
    Number(coordinate[0]),
    Number(coordinate[1]),
  ]);

  const valid = coordinates.every(
    ([longitude, latitude]) =>
      Number.isFinite(longitude) && Number.isFinite(latitude),
  );

  if (!valid) {
    console.error("BUSSIN MAP: Invalid coordinate values:", coordinates);
    return null;
  }

  return {
    type: "Feature",
    properties: {},
    geometry: {
      type: "LineString",
      coordinates,
    },
  };
}

function addOrUpdateRouteLayer(map, geometry) {
  const routeFeature = buildRouteFeature(geometry);

  if (!routeFeature) {
    return false;
  }

  console.log("BUSSIN MAP: Route feature:", routeFeature);

  const source = map.getSource("bussin-route");

  if (source) {
    source.setData(routeFeature);
  } else {
    map.addSource("bussin-route", {
      type: "geojson",
      data: routeFeature,
    });
  }

  if (!map.getLayer("bussin-route-line")) {
    map.addLayer({
      id: "bussin-route-line",
      type: "line",
      source: "bussin-route",
      layout: {
        "line-cap": "round",
        "line-join": "round",
      },
      paint: {
        "line-color": "#FF0000",
        "line-width": 6,
        "line-opacity": 1,
      },
    });
  }

  return true;
}

function fitMapToRoute(map, geometry, origin, destination) {
  const coordinates = [];

  const routeFeature = buildRouteFeature(geometry);

  if (routeFeature?.geometry?.coordinates) {
    coordinates.push(...routeFeature.geometry.coordinates);
  }

  if (origin?.longitude != null && origin?.latitude != null) {
    coordinates.push([Number(origin.longitude), Number(origin.latitude)]);
  }

  if (destination?.longitude != null && destination?.latitude != null) {
    coordinates.push([
      Number(destination.longitude),
      Number(destination.latitude),
    ]);
  }

  if (!coordinates.length) {
    return;
  }

  const bounds = coordinates.reduce(
    (result, coordinate) => result.extend(coordinate),
    new LngLatBounds(coordinates[0], coordinates[0]),
  );

  map.fitBounds(bounds, {
    padding: 70,
    maxZoom: 15,
    duration: 800,
  });
}

function createPinElement(type) {
  const element = document.createElement("div");

  element.className =
    type === "origin"
      ? "bussin-map-pin bussin-map-pin-origin"
      : "bussin-map-pin bussin-map-pin-destination";

  element.innerHTML = `
    <div class="bussin-map-pin-shape">
      <div class="bussin-map-pin-dot"></div>
    </div>
  `;

  return element;
}

export default function RouteMap({
  origin,
  destination,
  geometry,
  onPointSelect,
  interactive = true,
}) {
  const containerRef = useRef(null);
  const mapRef = useRef(null);
  const markersRef = useRef([]);
  const mapLoadedRef = useRef(false);

  useEffect(() => {
    if (!containerRef.current) {
      return;
    }

    console.log("BUSSIN MAP: Creating map.");

    const map = new Map({
      container: containerRef.current,
      style: OSM_STYLE,
      center: DEFAULT_CENTER,
      zoom: 11,
      attributionControl: true,
    });

    mapRef.current = map;

    map.addControl(new NavigationControl(), "top-right");

    map.on("load", () => {
      console.log("BUSSIN MAP: Map loaded.");

      mapLoadedRef.current = true;

      if (geometry) {
        const rendered = addOrUpdateRouteLayer(map, geometry);

        if (rendered) {
          fitMapToRoute(map, geometry, origin, destination);
        }
      }
    });

    map.on("error", (event) => {
      console.error("BUSSIN MAP: MapLibre error:", event?.error || event);
    });

    if (interactive) {
      map.on("click", (event) => {
        const point = {
          latitude: event.lngLat.lat,
          longitude: event.lngLat.lng,
        };

        console.log("BUSSIN MAP: Selected point:", point);

        onPointSelect?.(point);
      });
    }

    return () => {
      mapLoadedRef.current = false;

      markersRef.current.forEach((marker) => marker.remove());

      markersRef.current = [];

      map.remove();

      mapRef.current = null;
    };
  }, [interactive, onPointSelect]);

  useEffect(() => {
    const map = mapRef.current;

    if (!map || !mapLoadedRef.current) {
      return;
    }

    if (!geometry) {
      return;
    }

    const rendered = addOrUpdateRouteLayer(map, geometry);

    if (rendered) {
      fitMapToRoute(map, geometry, origin, destination);
    }
  }, [geometry, origin, destination]);

  useEffect(() => {
    const map = mapRef.current;

    if (!map || !mapLoadedRef.current) {
      return;
    }

    markersRef.current.forEach((marker) => marker.remove());

    markersRef.current = [];

    const points = [
      {
        point: origin,
        type: "origin",
        label: "Origin",
      },
      {
        point: destination,
        type: "destination",
        label: "Destination",
      },
    ];

    points.forEach(({ point, type, label }) => {
      if (!point || point.latitude == null || point.longitude == null) {
        return;
      }

      const latitude = Number(point.latitude);

      const longitude = Number(point.longitude);

      if (!Number.isFinite(latitude) || !Number.isFinite(longitude)) {
        return;
      }

      const popup = new Popup({
        offset: 20,
      }).setText(label);

      const element = createPinElement(type);

      const marker = new Marker({
        element,
        anchor: "bottom",
      })
        .setLngLat([longitude, latitude])
        .setPopup(popup)
        .addTo(map);

      markersRef.current.push(marker);
    });

    if (geometry) {
      const rendered = addOrUpdateRouteLayer(map, geometry);

      if (rendered) {
        fitMapToRoute(map, geometry, origin, destination);
      }
    }
  }, [origin, destination, geometry]);

  return (
    <div
      ref={containerRef}
      className="bussin-map"
      aria-label="Interactive route map"
    />
  );
}
