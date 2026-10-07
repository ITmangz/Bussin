import apiClient from "./apiClient";

export async function getAllTrips() {
    const response = await apiClient.get("/trips");
    return response.data;
}

export async function getEmployeeTrips(params = {}) {
    const response = await apiClient.get("/trips/employee", {
        params,
    });
    return response.data;
}

export async function getTripById(tripId) {
    const response = await apiClient.get(
        `/trips/${tripId}`
    );
    return response.data;
}

export async function searchTrips({
    origin,
    destination,
    from,
    to
}) {
    const response = await apiClient.get("/trips/search", {
        params: {
            origin,
            destination,
            from,
            to
        }
    });
    return response.data;
}

export async function getRouteById(routeId) {
    const response = await apiClient.get(
        `/routes/${routeId}`
    );
    return response.data;
}

export async function createTrip(tripData) {
    const response = await apiClient.post("/trips", tripData);
    return response.data;
}

export async function updateTrip(tripId, tripData) {
    const response = await apiClient.put(
        `/trips/${tripId}`,
        tripData
    );
    return response.data;
}

export async function deleteTrip(tripId) {
    await apiClient.delete(`/trips/${tripId}`);
}
