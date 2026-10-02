package com.bussin.desktop.services;

import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class BusApiService {

    private static final Gson GSON = new GsonBuilder()
            .create();

    private BusApiService() {
    }

    public static List<BusResponse> getAllBuses()
            throws Exception {

        var response = ApiClient.get("/buses");

        if (response.statusCode() != 200) {

            throw new IllegalStateException(
                    "Failed to retrieve buses. HTTP "
                            + response.statusCode()
                            + ": "
                            + response.body());
        }

        BusResponse[] buses = GSON.fromJson(
                response.body(),
                BusResponse[].class);

        return Arrays.asList(buses);
    }

    public static BusResponse createBus(
            String plateNumber,
            String model,
            int capacity)
            throws Exception {

        String json = GSON.toJson(
                new CreateBusRequest(
                        plateNumber,
                        model,
                        capacity));

        var response = ApiClient.post(
                "/buses",
                json);

        if (response.statusCode() != 201) {

            throw new IllegalStateException(
                    "Failed to create bus. HTTP "
                            + response.statusCode()
                            + ": "
                            + response.body());
        }

        return GSON.fromJson(
                response.body(),
                BusResponse.class);
    }

    public static BusResponse updateBus(
            long id,
            String plateNumber,
            String model,
            int capacity,
            String status)
            throws Exception {

        String json = GSON.toJson(
                new UpdateBusRequest(
                        plateNumber,
                        model,
                        capacity,
                        status));

        var response = ApiClient.put(
                "/buses/" + id,
                json);

        if (response.statusCode() != 200) {

            throw new IllegalStateException(
                    "Failed to update bus. HTTP "
                            + response.statusCode()
                            + ": "
                            + response.body());
        }

        return GSON.fromJson(
                response.body(),
                BusResponse.class);
    }

    public static void deleteBus(
            long id)
            throws Exception {

        var response = ApiClient.delete(
                "/buses/" + id);

        if (response.statusCode() != 204) {

            throw new IllegalStateException(
                    "Failed to delete bus. HTTP "
                            + response.statusCode()
                            + ": "
                            + response.body());
        }
    }

    public static class BusResponse {

        private Long id;
        private String plateNumber;
        private String model;
        private Integer capacity;
        private String status;

        public Long getId() {
            return id;
        }

        public String getPlateNumber() {
            return plateNumber;
        }

        public String getModel() {
            return model;
        }

        public Integer getCapacity() {
            return capacity;
        }

        public String getStatus() {
            return status;
        }
    }

    private record CreateBusRequest(
            String plateNumber,
            String model,
            int capacity) {
    }

    private record UpdateBusRequest(
            String plateNumber,
            String model,
            int capacity,
            String status) {
    }
}