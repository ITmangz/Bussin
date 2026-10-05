package com.bussin.bussin_api.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AiChatService {

  @Value("${OPENROUTER_API_KEY}")
  private String apiKey;

  @Value("${OPENROUTER_BASE_URL:https://openrouter.ai/api/v1}")
  private String baseUrl;

  @Value("${OPENROUTER_MODEL}")
  private String model;

  private final HttpClient httpClient = HttpClient.newBuilder()
      .connectTimeout(Duration.ofSeconds(30))
      .build();

  public String chat(
      String message,
      String bookingContext)
      throws IOException, InterruptedException {

    LocalDate currentDate = LocalDate.now();

    String requestBody = """
        {
          "model": "%s",
          "messages": [
            {
              "role": "system",
              "content": "You are the BUSSIN AI Booking Assistant. Today's date is %s. This date is authoritative. Never use a date from your training data. You help commuters search for trips and prepare bookings. Always respond using valid JSON only. Use this structure: {\\\"intent\\\":\\\"CHAT|SEARCH_TRIPS|SELECT_TRIP|SELECT_SEAT|PASSENGER_INFO|CONFIRM_BOOKING|CANCEL_BOOKING\\\",\\\"message\\\":\\\"your response\\\",\\\"origin\\\":null,\\\"destination\\\":null,\\\"date\\\":null,\\\"time\\\":null,\\\"tripId\\\":null,\\\"seatNumber\\\":null,\\\"passengerName\\\":null,\\\"passengerPhone\\\":null,\\\"passengerEmail\\\":null,\\\"confirmed\\\":false}. Extract information from the user's message. Today means %s. Tomorrow means the next calendar date. Dates must use YYYY-MM-DD. Times must use HH:mm. Seat numbers use formats such as 1A or 9C. Never invent trip IDs, seats, fares, routes, availability, or passenger information. If the user confirms a booking, set confirmed to true. If the user wants to cancel the current booking conversation, use CANCEL_BOOKING."
            },
            {
              "role": "system",
              "content": "Current booking context: %s"
            },
            {
              "role": "user",
              "content": "%s"
            }
          ]
        }
        """
        .formatted(
            model,
            currentDate,
            currentDate,
            escapeJson(bookingContext),
            escapeJson(message));

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(
            baseUrl
                + "/chat/completions"))
        .timeout(
            Duration.ofMinutes(2))
        .header(
            "Authorization",
            "Bearer " + apiKey)
        .header(
            "Content-Type",
            "application/json")
        .POST(
            HttpRequest.BodyPublishers
                .ofString(
                    requestBody))
        .build();

    HttpResponse<String> response = httpClient.send(
        request,
        HttpResponse.BodyHandlers
            .ofString());

    if (response.statusCode() < 200
        || response.statusCode() >= 300) {

      throw new IllegalStateException(
          "OpenRouter request failed. HTTP "
              + response.statusCode()
              + ": "
              + response.body());
    }

    return response.body();
  }

  private String escapeJson(String value) {

    if (value == null) {
      return "";
    }

    return value
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\r", "\\r")
        .replace("\n", "\\n");
  }
}