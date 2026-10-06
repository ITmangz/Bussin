import apiClient from "./apiClient";

export async function getMyBookings() {
    const response = await apiClient.get("/bookings/me");

    return response.data;
}

export async function getMyBooking(bookingId) {
    const response = await apiClient.get(
        `/bookings/me/${bookingId}`
    );

    return response.data;
}

export async function cancelMyBooking(bookingId) {
    const response = await apiClient.delete(
        `/bookings/me/${bookingId}`
    );

    return response.data;
}

export async function getAvailableSeats(tripId) {
    const response = await apiClient.get(
        `/bookings/trip/${tripId}/seats`
    );

    return response.data;
}

export async function createBooking(bookingData) {
    const response = await apiClient.post(
        "/bookings",
        bookingData
    );

    return response.data;
}

export async function createGuestBooking(bookingData) {
    const response = await apiClient.post(
        "/bookings/guest",
        bookingData
    );

    return response.data;
}
