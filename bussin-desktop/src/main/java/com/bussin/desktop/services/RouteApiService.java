package com.bussin.desktop.services;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class RouteApiService {

        private static final Gson GSON = new GsonBuilder()
                        .create();

        private RouteApiService() {
        }

        public static List<RouteResponse> getAllRoutes()
                        throws Exception {

                var response = ApiClient.get("/routes");

                if (response.statusCode() != 200) {
                        throw new IllegalStateException(
                                        "Failed to retrieve routes. HTTP "
                                                        + response.statusCode()
                                                        + ": "
                                                        + response.body());
                }

                RouteResponse[] routes = GSON.fromJson(
                                response.body(),
                                RouteResponse[].class);

                return Arrays.asList(routes);
        }

        public static RouteResponse createRoute(
                        String routeIdentifier,
                        String origin,
                        String destination,
                        BigDecimal distanceKm,
                        Integer durationMinutes,
                        BigDecimal baseFare,
                        String description)
                        throws Exception {

                String json = GSON.toJson(
                                new CreateRouteRequest(
                                                routeIdentifier,
                                                origin,
                                                destination,
                                                distanceKm,
                                                durationMinutes,
                                                baseFare,
                                                description));

                var response = ApiClient.post(
                                "/routes",
                                json);

                if (response.statusCode() != 201) {
                        throw new IllegalStateException(
                                        "Failed to create route. HTTP "
                                                        + response.statusCode()
                                                        + ": "
                                                        + response.body());
                }

                return GSON.fromJson(
                                response.body(),
                                RouteResponse.class);
        }

        public static RouteResponse updateRoute(
                        long id,
                        String routeIdentifier,
                        String origin,
                        String destination,
                        BigDecimal distanceKm,
                        Integer durationMinutes,
                        BigDecimal baseFare,
                        String description,
                        boolean active)
                        throws Exception {

                String json = GSON.toJson(
                                new UpdateRouteRequest(
                                                routeIdentifier,
                                                origin,
                                                destination,
                                                distanceKm,
                                                durationMinutes,
                                                baseFare,
                                                description,
                                                active));

                var response = ApiClient.put(
                                "/routes/" + id,
                                json);

                if (response.statusCode() != 200) {
                        throw new IllegalStateException(
                                        "Failed to update route. HTTP "
                                                        + response.statusCode()
                                                        + ": "
                                                        + response.body());
                }

                return GSON.fromJson(
                                response.body(),
                                RouteResponse.class);
        }

        public static void deleteRoute(
                        long id)
                        throws Exception {

                var response = ApiClient.delete(
                                "/routes/" + id);

                if (response.statusCode() != 204) {
                        throw new IllegalStateException(
                                        "Failed to delete route. HTTP "
                                                        + response.statusCode()
                                                        + ": "
                                                        + response.body());
                }
        }

        public static class RouteResponse {

                private Long id;
                private String routeIdentifier;
                private String origin;
                private String destination;
                private BigDecimal distanceKm;
                private Integer durationMinutes;
                private BigDecimal baseFare;
                private String description;
                private boolean active;

                public Long getId() {
                        return id;
                }

                public String getRouteIdentifier() {
                        return routeIdentifier;
                }

                public String getOrigin() {
                        return origin;
                }

                public String getDestination() {
                        return destination;
                }

                public BigDecimal getDistanceKm() {
                        return distanceKm;
                }

                public Integer getDurationMinutes() {
                        return durationMinutes;
                }

                public BigDecimal getBaseFare() {
                        return baseFare;
                }

                public String getDescription() {
                        return description;
                }

                public boolean isActive() {
                        return active;
                }
        }

        private record CreateRouteRequest(
                        String routeIdentifier,
                        String origin,
                        String destination,
                        BigDecimal distanceKm,
                        Integer durationMinutes,
                        BigDecimal baseFare,
                        String description) {
        }

        private record UpdateRouteRequest(
                        String routeIdentifier,
                        String origin,
                        String destination,
                        BigDecimal distanceKm,
                        Integer durationMinutes,
                        BigDecimal baseFare,
                        String description,
                        boolean active) {
        }
}