import { useEffect, useRef } from "react";
import { Map, NavigationControl, Marker, Popup } from "maplibre-gl";
import "maplibre-gl/dist/maplibre-gl.css";
import "./RouteMap.css";

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

function normalizeGeometry(geometry) {
  if (!geometry) {
    return null;
  }

  let parsed = geometry;

  // routeGeometry is normally returned from Spring Boot as a JSON string.
  if (typeof parsed === "string") {
    try {
      parsed = JSON.parse(parsed);
    } catch (error) {
      console.error("Unable to parse route geometry:", error);
      return null;
    }
  }

  if (!parsed || typeof parsed !== "object") {
    return null;
  }

  /*
   * OSRM returns:
   *
   * {
   *   "type": "LineString",
   *   "coordinates": [...]
   * }
   *
   * But this also supports GeoJSON Feature / FeatureCollection
   * in case the backend changes later.
   */

  if (parsed.type === "Feature") {
    return parsed.geometry || null;
  }

  if (parsed.type === "FeatureCollection") {
    const firstFeature = parsed.features?.find((feature) => feature?.geometry);

    return firstFeature?.geometry || null;
  }

  if (parsed.type === "LineString") {
    return parsed;
  }

  return null;
}

function buildRouteFeature(geometry) {
  const normalized = normalizeGeometry(geometry);

  if (!normalized) {
    return null;
  }

  if (
    normalized.type !== "LineString" ||
    !Array.isArray(normalized.coordinates) ||
    normalized.coordinates.length < 2
  ) {
    console.error("Invalid route geometry:", normalized);
    return null;
  }

  return {
    type: "Feature",
    properties: {},
    geometry: normalized,
  };
}

function addOrUpdateRouteLayer(map, geometry) {
  const routeFeature = buildRouteFeature(geometry);

  if (!routeFeature) {
    return false;
  }

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
        "line-opacity": 0.95,
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

  if (coordinates.length === 0) {
    return;
  }

  const bounds = coordinates.reduce(
    (result, coordinate) => {
      return result.extend(coordinate);
    },
    new maplibregl.LngLatBounds(coordinates[0], coordinates[0]),
  );

  map.fitBounds(bounds, {
    padding: 70,
    maxZoom: 15,
    duration: 800,
  });
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

  /*
   * Create the map only once.
   */
  useEffect(() => {
    if (!containerRef.current) {
      return;
    }

    const map = new Map({
      container: containerRef.current,
      style: OSM_STYLE,
      center: DEFAULT_CENTER,
      zoom: 11,
    });

    mapRef.current = map;

    map.addControl(new NavigationControl(), "top-right");

    map.on("load", () => {
      mapLoadedRef.current = true;

      if (geometry) {
        addOrUpdateRouteLayer(map, geometry);
      }
    });

    if (interactive) {
      map.on("click", (event) => {
        onPointSelect?.({
          latitude: event.lngLat.lat,
          longitude: event.lngLat.lng,
        });
      });
    }

    return () => {
      mapLoadedRef.current = false;

      markersRef.current.forEach((marker) => {
        marker.remove();
      });

      markersRef.current = [];

      map.remove();
      mapRef.current = null;
    };
  }, [interactive, onPointSelect]);

  /*
   * Update route whenever geometry changes.
   */
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

  /*
   * Update markers whenever origin/destination changes.
   */
  useEffect(() => {
    const map = mapRef.current;

    if (!map || !mapLoadedRef.current) {
      return;
    }

    markersRef.current.forEach((marker) => {
      marker.remove();
    });

    markersRef.current = [];

    const points = [
      [origin, "Origin"],
      [destination, "Destination"],
    ];

    points.forEach(([point, label]) => {
      if (!point || point.latitude == null || point.longitude == null) {
        return;
      }

      const popup = new Popup({
        offset: 18,
      }).setText(label);

      const marker = new Marker({
        color: label === "Origin" ? "#990000" : "#121212",
      })
        .setLngLat([Number(point.longitude), Number(point.latitude)])
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
