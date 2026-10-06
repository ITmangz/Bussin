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

export async function getAllUsers() {
    const response = await apiClient.get("/users");
    return response.data;
}

export async function getEmployees() {
    const response = await apiClient.get("/users/employees");
    return response.data;
}

export async function updateManagedUser(userId, userData) {
    const response = await apiClient.put(`/users/${userId}`, userData);
    return response.data;
}

export async function updateUserRole(userId, role) {
    const response = await apiClient.put(`/users/${userId}/role`, { role });
    return response.data;
}
