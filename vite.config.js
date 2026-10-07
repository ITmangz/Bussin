import { defineConfig } from "vitest/config";
import react from "@vitejs/plugin-react";

export default defineConfig({
    plugins: [react()],
    server: {
        port: 5173,
        strictPort: true,
        headers: {
            "Cross-Origin-Opener-Policy": "unsafe-none"
        }
    },
    optimizeDeps: {
        exclude: ["maplibre-gl"]
    },
    build: {
        outDir: "dist"
    },
    test: {
        environment: "jsdom",
        clearMocks: true
    }
});
