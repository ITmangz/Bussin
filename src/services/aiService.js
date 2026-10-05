import apiClient from "./apiClient";

export async function sendAIMessage(message) {
  const response = await apiClient.post(
    "/ai/chat",
    {
      message,
    }
  );

  return response.data;
}