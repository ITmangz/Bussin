import apiClient from "./apiClient";

export async function getTripQueue(tripId) {
  const response = await apiClient.get(`/queue/trip/${tripId}`);
  return response.data;
}

export async function updateQueueStatus(queueEntryId, status) {
  const response = await apiClient.put(`/queue/${queueEntryId}/status`, { status });
  return response.data;
}
