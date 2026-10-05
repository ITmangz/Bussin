import apiClient from "./apiClient";

export async function getAllTrips() {
    const response = await apiClient.get("/trips");

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