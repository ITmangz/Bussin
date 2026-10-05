import apiClient from "./apiClient";

export async function getAllRoutes(activeOnly) {
  const response = await apiClient.get("/routes", {
    params: activeOnly === undefined ? {} : { activeOnly },
  });
  return response.data;
}

export async function getRouteById(routeId) {
  const response = await apiClient.get(`/routes/${routeId}`);
  return response.data;
}

export async function createRoute(routeData) {
  const response = await apiClient.post("/routes", routeData);
  return response.data;
}

export async function updateRoute(routeId, routeData) {
  const response = await apiClient.put(`/routes/${routeId}`, routeData);
  return response.data;
}

export async function deleteRoute(routeId) {
  await apiClient.delete(`/routes/${routeId}`);
}
