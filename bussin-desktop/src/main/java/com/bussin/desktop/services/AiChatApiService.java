package com.bussin.desktop.services;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public final class AiChatApiService {

    private static final Gson GSON = new Gson();

    private AiChatApiService() {
    }

    public static AiChatResponse sendMessage(
            String message)
            throws IOException, InterruptedException {

        ChatRequest request = new ChatRequest(message);

        HttpResponse<String> response = ApiClient.post(
                "/ai/chat",
                GSON.toJson(request));

        ensureSuccess(response);

        return parseResponse(response.body());
    }

    private static AiChatResponse parseResponse(
            String json) {

        JsonObject root = JsonParser
                .parseString(json)
                .getAsJsonObject();

        String intent = getString(
                root,
                "intent",
                "CHAT");

        String message = getString(
                root,
                "message",
                "");

        List<TripOption> trips = parseTrips(
                root.get("trips"));

        List<String> availableSeats = parseSeats(
                root.get("availableSeats"));

        BookingResult booking = parseBooking(
                root.get("booking"));

        return new AiChatResponse(
                intent,
                message,
                trips,
                availableSeats,
                booking);
    }

    private static List<TripOption> parseTrips(
            JsonElement element) {

        List<TripOption> trips = new ArrayList<>();

        if (element == null
                || !element.isJsonArray()) {

            return trips;
        }

        for (JsonElement item : element.getAsJsonArray()) {

            if (!item.isJsonObject()) {
                continue;
            }

            JsonObject trip = item.getAsJsonObject();

            trips.add(
                    new TripOption(
                            getLong(trip, "id"),
                            getString(trip, "routeIdentifier", ""),
                            getString(trip, "origin", ""),
                            getString(trip, "destination", ""),
                            getString(trip, "busPlateNumber", ""),
                            getString(trip, "scheduledDeparture", ""),
                            getString(trip, "scheduledArrival", ""),
                            getDouble(trip, "fare")));
        }

        return trips;
    }

    private static List<String> parseSeats(
            JsonElement element) {

        List<String> seats = new ArrayList<>();

        if (element == null
                || !element.isJsonArray()) {

            return seats;
        }

        for (JsonElement item : element.getAsJsonArray()) {

            if (item.isJsonPrimitive()) {
                seats.add(item.getAsString());
            }
        }

        return seats;
    }

    private static BookingResult parseBooking(
            JsonElement element) {

        if (element == null
                || element.isJsonNull()
                || !element.isJsonObject()) {

            return null;
        }

        JsonObject booking = element.getAsJsonObject();

        return new BookingResult(
                getLong(booking, "id"),
                getString(
                        booking,
                        "bookingReference",
                        ""),
                getLong(
                        booking,
                        "tripId"),
                getString(
                        booking,
                        "routeIdentifier",
                        ""),
                getString(
                        booking,
                        "origin",
                        ""),
                getString(
                        booking,
                        "destination",
                        ""),
                getString(
                        booking,
                        "seatNumber",
                        ""),
                getDouble(
                        booking,
                        "fare"),
                getString(
                        booking,
                        "status",
                        ""),
                getString(
                        booking,
                        "paymentStatus",
                        ""));
    }

    private static String getString(
            JsonObject object,
            String property,
            String fallback) {

        if (!object.has(property)
                || object.get(property).isJsonNull()) {

            return fallback;
        }

        return object.get(property).getAsString();
    }

    private static long getLong(
            JsonObject object,
            String property) {

        if (!object.has(property)
                || object.get(property).isJsonNull()) {

            return 0L;
        }

        return object.get(property).getAsLong();
    }

    private static double getDouble(
            JsonObject object,
            String property) {

        if (!object.has(property)
                || object.get(property).isJsonNull()) {

            return 0.0;
        }

        return object.get(property).getAsDouble();
    }

    private static void ensureSuccess(
            HttpResponse<String> response) {

        if (response.statusCode() < 200
                || response.statusCode() >= 300) {

            throw new IllegalStateException(
                    "AI Booking API request failed. HTTP "
                            + response.statusCode()
                            + ": "
                            + response.body());
        }
    }

    private record ChatRequest(
            String message) {
    }

    public record AiChatResponse(
            String intent,
            String message,
            List<TripOption> trips,
            List<String> availableSeats,
            BookingResult booking) {
    }

    public record TripOption(
            long id,
            String routeIdentifier,
            String origin,
            String destination,
            String busPlateNumber,
            String scheduledDeparture,
            String scheduledArrival,
            double fare) {
    }

    public record BookingResult(
            long id,
            String bookingReference,
            long tripId,
            String routeIdentifier,
            String origin,
            String destination,
            String seatNumber,
            double fare,
            String status,
            String paymentStatus) {
    }
}