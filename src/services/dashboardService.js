import apiClient from "./apiClient";

export async function getDashboardMetrics() {
  const response = await apiClient.get("/dashboard");
  return response.data;
}
