import apiClient from "./apiClient";

export async function getAllBookings() {
  const response = await apiClient.get("/bookings/admin");
  return response.data;
}

export async function getEmployeeBookings() {
  const response = await apiClient.get("/bookings/employee");
  return response.data;
}

export async function getBookingById(bookingId) {
  const response = await apiClient.get(`/bookings/admin/${bookingId}`);
  return response.data;
}

export async function updateBookingStatus(bookingId, status) {
  const response = await apiClient.patch(
    `/bookings/admin/${bookingId}/status`,
    { status },
  );
  return response.data;
}
