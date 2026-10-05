import {
  ArrowRight,
  BusFront,
  CalendarDays,
  Clock3,
  MapPin,
  Search,
  Users,
} from "lucide-react";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import AppButton from "../components/ui/AppButton";
import AppCard from "../components/ui/AppCard";

import { getAllTrips, getRouteById } from "../services/tripService";

import "./Trips.css";

function Trips() {
  const navigate = useNavigate();

  const [origin, setOrigin] = useState("");
  const [destination, setDestination] = useState("");
  const [date, setDate] = useState("");

  const [trips, setTrips] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [searched, setSearched] = useState(false);

  useEffect(() => {
    loadTrips();
  }, []);

  async function loadTrips() {
    try {
      setLoading(true);
      setError("");

      const tripResponse = await getAllTrips();

      const tripList = Array.isArray(tripResponse) ? tripResponse : [];

      /*
       * TripResponse contains bus + route IDs,
       * while route information contains origin,
       * destination, duration and fare.
       *
       * Fetch the corresponding route for each trip.
       */
      const enrichedTrips = await Promise.all(
        tripList.map(async (trip) => {
          try {
            const route = await getRouteById(trip.routeId);

            return {
              id: trip.id,

              origin: route.origin,
              destination: route.destination,

              date: formatDate(trip.scheduledDeparture),

              time: formatTime(trip.scheduledDeparture),

              busNumber: trip.busPlateNumber || "N/A",

              busType: "Bus",

              duration: formatDuration(route.durationMinutes),

              fare: Number(route.baseFare || 0),

              /*
               * Backend currently provides bus capacity,
               * but not the current available-seat count.
               */
              availableSeats: trip.busCapacity ?? 0,

              capacity: trip.busCapacity ?? 0,

              routeId: trip.routeId,

              routeIdentifier: trip.routeIdentifier,

              scheduledDeparture: trip.scheduledDeparture,

              scheduledArrival: trip.scheduledArrival,

              status: trip.status,
            };
          } catch (routeError) {
            console.error(`Failed to load route ${trip.routeId}:`, routeError);

            return {
              id: trip.id,

              origin: "Unknown",
              destination: "Unknown",

              date: formatDate(trip.scheduledDeparture),

              time: formatTime(trip.scheduledDeparture),

              busNumber: trip.busPlateNumber || "N/A",

              busType: "Bus",

              duration: "N/A",

              fare: 0,

              availableSeats: trip.busCapacity ?? 0,

              capacity: trip.busCapacity ?? 0,

              routeId: trip.routeId,

              routeIdentifier: trip.routeIdentifier,

              scheduledDeparture: trip.scheduledDeparture,

              scheduledArrival: trip.scheduledArrival,

              status: trip.status,
            };
          }
        }),
      );

      setTrips(enrichedTrips);
    } catch (err) {
      console.error("Failed to load trips:", err);

      setError(
        err.response?.data?.message || "Unable to load trips from the server.",
      );
    } finally {
      setLoading(false);
    }
  }

  function formatDate(dateTime) {
    if (!dateTime) {
      return "N/A";
    }

    const dateObject = new Date(dateTime);

    return dateObject.toLocaleDateString("en-US", {
      year: "numeric",
      month: "long",
      day: "numeric",
    });
  }

  function formatTime(dateTime) {
    if (!dateTime) {
      return "N/A";
    }

    const dateObject = new Date(dateTime);

    return dateObject.toLocaleTimeString("en-US", {
      hour: "numeric",
      minute: "2-digit",
    });
  }

  function formatDuration(minutes) {
    if (minutes === null || minutes === undefined) {
      return "N/A";
    }

    const hours = Math.floor(minutes / 60);

    const remainingMinutes = minutes % 60;

    if (hours === 0) {
      return `${remainingMinutes}m`;
    }

    if (remainingMinutes === 0) {
      return `${hours}h`;
    }

    return `${hours}h ${remainingMinutes}m`;
  }

  const displayedTrips = searched
    ? trips.filter((trip) => {
        const matchesOrigin =
          !origin || trip.origin.toLowerCase().includes(origin.toLowerCase());

        const matchesDestination =
          !destination ||
          trip.destination.toLowerCase().includes(destination.toLowerCase());

        const matchesDate = !date || trip.scheduledDeparture?.startsWith(date);

        return matchesOrigin && matchesDestination && matchesDate;
      })
    : trips;

  function handleSearch(event) {
    event.preventDefault();

    setSearched(true);
  }

  function clearSearch() {
    setOrigin("");
    setDestination("");
    setDate("");
    setSearched(false);
  }

  function handleBookTrip(trip) {
    navigate("/ai-booking", {
      state: {
        trip,
      },
    });
  }

  if (loading) {
    return (
      <section className="trips-page">
        <header className="trips-header">
          <div>
            <h1>Trips</h1>

            <p>Find and select the bus trip that works for you.</p>
          </div>

          <div className="trips-header-icon">
            <BusFront size={22} />
          </div>
        </header>

        <AppCard className="trips-empty">
          <div className="trips-empty-icon">
            <BusFront size={24} />
          </div>

          <h3>Loading trips</h3>

          <p>We're getting the latest trips from the BUSSIN server.</p>
        </AppCard>
      </section>
    );
  }

  if (error) {
    return (
      <section className="trips-page">
        <header className="trips-header">
          <div>
            <h1>Trips</h1>

            <p>Find and select the bus trip that works for you.</p>
          </div>

          <div className="trips-header-icon">
            <BusFront size={22} />
          </div>
        </header>

        <AppCard className="trips-empty">
          <div className="trips-empty-icon">
            <Search size={24} />
          </div>

          <h3>Unable to load trips</h3>

          <p>{error}</p>

          <AppButton variant="secondary" onClick={loadTrips}>
            Try Again
          </AppButton>
        </AppCard>
      </section>
    );
  }

  return (
    <section className="trips-page">
      <header className="trips-header">
        <div>
          <h1>Trips</h1>

          <p>Find and select the bus trip that works for you.</p>
        </div>

        <div className="trips-header-icon">
          <BusFront size={22} />
        </div>
      </header>

      <AppCard className="trip-search-card">
        <form onSubmit={handleSearch}>
          <div className="trip-search-grid">
            <div className="trip-field">
              <label htmlFor="trip-origin">From</label>

              <div className="trip-input">
                <MapPin size={16} />

                <input
                  id="trip-origin"
                  type="text"
                  placeholder="Departure location"
                  value={origin}
                  onChange={(event) => setOrigin(event.target.value)}
                />
              </div>
            </div>

            <div className="trip-field">
              <label htmlFor="trip-destination">To</label>

              <div className="trip-input">
                <MapPin size={16} />

                <input
                  id="trip-destination"
                  type="text"
                  placeholder="Destination"
                  value={destination}
                  onChange={(event) => setDestination(event.target.value)}
                />
              </div>
            </div>

            <div className="trip-field">
              <label htmlFor="trip-date">Travel Date</label>

              <div className="trip-input">
                <CalendarDays size={16} />

                <input
                  id="trip-date"
                  type="date"
                  value={date}
                  onChange={(event) => setDate(event.target.value)}
                />
              </div>
            </div>

            <div className="trip-search-actions">
              <AppButton type="submit" variant="primary">
                <Search size={16} />
                Search Trips
              </AppButton>

              {searched && (
                <button
                  type="button"
                  className="trip-clear-button"
                  onClick={clearSearch}
                >
                  Clear
                </button>
              )}
            </div>
          </div>
        </form>
      </AppCard>

      <div className="trips-results-header">
        <div>
          <h2>{searched ? "Search Results" : "Available Trips"}</h2>

          <span>{displayedTrips.length} trips found</span>
        </div>
      </div>

      {displayedTrips.length > 0 ? (
        <div className="trip-list">
          {displayedTrips.map((trip) => (
            <AppCard key={trip.id} className="trip-card">
              <div className="trip-card-main">
                <div className="trip-route">
                  <div className="trip-location">
                    <span className="trip-time">{trip.time}</span>

                    <strong>{trip.origin}</strong>
                  </div>

                  <div className="trip-route-line">
                    <span>{trip.duration}</span>

                    <div className="route-line">
                      <span />
                      <div />
                      <span />
                    </div>
                  </div>

                  <div className="trip-location destination">
                    <span className="trip-time">Arrival</span>

                    <strong>{trip.destination}</strong>
                  </div>
                </div>

                <div className="trip-details">
                  <div className="trip-detail">
                    <BusFront size={15} />

                    <span>{trip.busNumber}</span>
                  </div>

                  <div className="trip-detail">
                    <Clock3 size={15} />

                    <span>{trip.busType}</span>
                  </div>

                  <div className="trip-detail">
                    <Users size={15} />

                    <span>{trip.capacity} seats</span>
                  </div>
                </div>
              </div>

              <div className="trip-card-side">
                <span className="trip-date">{trip.date}</span>

                <div className="trip-fare">
                  <small>Fare</small>

                  <strong>
                    ₱
                    {trip.fare.toLocaleString("en-PH", {
                      minimumFractionDigits: 2,
                    })}
                  </strong>
                </div>

                <AppButton
                  variant="primary"
                  onClick={() => handleBookTrip(trip)}
                >
                  Select Trip
                  <ArrowRight size={15} />
                </AppButton>
              </div>
            </AppCard>
          ))}
        </div>
      ) : (
        <AppCard className="trips-empty">
          <div className="trips-empty-icon">
            <Search size={24} />
          </div>

          <h3>No trips found</h3>

          <p>
            Try changing your departure location, destination, or search
            criteria.
          </p>

          <AppButton variant="secondary" onClick={clearSearch}>
            Clear Search
          </AppButton>
        </AppCard>
      )}
    </section>
  );
}

export default Trips;
