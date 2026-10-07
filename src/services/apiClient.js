import axios from "axios";
import { auth } from "../config/firebase";

const apiClient = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL,
    headers: {
        "Content-Type": "application/json"
    }
});

apiClient.interceptors.request.use(
    async (config) => {
        await auth.authStateReady();
        const user = auth.currentUser;

        if (user) {
            const token = await user.getIdToken(true);

            config.headers.Authorization = `Bearer ${token}`;
        }

        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

export default apiClient;
