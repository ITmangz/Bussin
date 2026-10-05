import apiClient from "./apiClient";

export async function createUserProfile(userData) {
    const response = await apiClient.post(
        "/users",
        userData
    );

    return response.data;
}

export async function getCurrentUserProfile() {
    const response = await apiClient.get(
        "/users/me"
    );

    return response.data;
}

export async function updateCurrentUserProfile(userData) {
    const response = await apiClient.put(
        "/users/me",
        userData
    );

    return response.data;
}