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