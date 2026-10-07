import { useCallback, useMemo, useState } from "react";

import RouteMap from "./RouteMap";
import "./PassengerFareMap.css";

import { quoteRouteFare } from "../../services/routeService";

const TYPES = [
  ["REGULAR", "Regular"],
  ["STUDENT", "Student"],
  ["SENIOR", "Senior Citizen"],
  ["PWD", "PWD"],
];

export default function PassengerFareMap({
  route,
  onQuoteChange,
  onDropoffChange,
}) {
  const [dropoff, setDropoff] = useState(null);
  const [type, setType] = useState("REGULAR");
  const [quote, setQuote] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const origin = useMemo(() => {
    if (route?.originLatitude == null || route?.originLongitude == null) {
      return null;
    }

    return {
      latitude: Number(route.originLatitude),
      longitude: Number(route.originLongitude),
    };
  }, [route]);

  const handlePoint = useCallback(
    async (point) => {
      setDropoff(point);

      onDropoffChange?.(point);

      setQuote(null);
      onQuoteChange?.(null);

      setError("");
      setLoading(true);

      try {
        const nextQuote = await quoteRouteFare(route.id, {
          ...point,
          passengerType: type,
        });

        setQuote(nextQuote);

        onQuoteChange?.({
          ...nextQuote,
          passengerType: type,
        });
      } catch (error) {
        console.error("BUSSIN: Fare calculation failed:", error);

        setError(
          error.response?.data?.message ||
            error.response?.data?.error ||
            "Unable to calculate the fare.",
        );
      } finally {
        setLoading(false);
      }
    },
    [route.id, type, onQuoteChange, onDropoffChange],
  );

  async function changeType(event) {
    const nextType = event.target.value;

    setType(nextType);

    setQuote(null);
    onQuoteChange?.(null);

    if (!dropoff) {
      return;
    }

    setError("");
    setLoading(true);

    try {
      const nextQuote = await quoteRouteFare(route.id, {
        ...dropoff,
        passengerType: nextType,
      });

      setQuote(nextQuote);

      onQuoteChange?.({
        ...nextQuote,
        passengerType: nextType,
      });
    } catch (error) {
      console.error("BUSSIN: Fare recalculation failed:", error);

      setError(
        error.response?.data?.message ||
          error.response?.data?.error ||
          "Unable to calculate the fare.",
      );
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="passenger-fare-map">
      <div className="route-map-help">
        Tap the map to pin your drop-off location. BUSSIN calculates the road
        distance from the route origin and applies the selected passenger
        discount.
      </div>

      <RouteMap
        origin={origin}
        destination={dropoff}
        geometry={route.routeGeometry}
        onPointSelect={handlePoint}
      />

      <div className="route-map-points">
        <span className="route-map-point selected">Origin: {route.origin}</span>

        {dropoff && <span className="route-map-point">Drop-off pinned</span>}
      </div>

      <label>
        Passenger type
        <select value={type} onChange={changeType}>
          {TYPES.map(([value, label]) => (
            <option key={value} value={value}>
              {label}
            </option>
          ))}
        </select>
      </label>

      {loading && <p>Calculating road distance and fare…</p>}

      {error && <p className="routes-error">{error}</p>}

      {quote && (
        <div className="fare-quote-card">
          <div>
            Distance{" "}
            <strong>{Number(quote.distanceKm || 0).toFixed(2)} km</strong>
          </div>

          <div>
            Regular fare{" "}
            <strong>₱{Number(quote.regularFare || 0).toFixed(2)}</strong>
          </div>

          <div>
            Discount <strong>{quote.discountPercent}%</strong>
          </div>

          <div>
            Final fare{" "}
            <strong>₱{Number(quote.finalFare || 0).toFixed(2)}</strong>
          </div>
        </div>
      )}
    </div>
  );
}
