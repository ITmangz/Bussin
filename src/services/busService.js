import apiClient from "./apiClient";

export async function getAllBuses(status) {
  const response = await apiClient.get("/buses", {
    params: status ? { status } : {},
  });

  return response.data;
}

export async function getBusById(busId) {
  const response = await apiClient.get(`/buses/${busId}`);
  return response.data;
}

export async function createBus(busData) {
  const response = await apiClient.post("/buses", busData);
  return response.data;
}

export async function updateBus(busId, busData) {
  const response = await apiClient.put(`/buses/${busId}`, busData);
  return response.data;
}

export async function deleteBus(busId) {
  await apiClient.delete(`/buses/${busId}`);
}
