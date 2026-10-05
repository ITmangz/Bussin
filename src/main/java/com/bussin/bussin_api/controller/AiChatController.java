package com.bussin.bussin_api.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bussin.bussin_api.dto.CreateBookingRequest;
import com.bussin.bussin_api.dto.TripResponse;
import com.bussin.bussin_api.entity.Trip;
import com.bussin.bussin_api.service.AiBookingService;
import com.bussin.bussin_api.service.AiChatService;
import com.bussin.bussin_api.service.BookingService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.google.firebase.auth.FirebaseToken;

@RestController
@RequestMapping("/api/ai")
public class AiChatController {

        private final AiChatService aiChatService;
        private final AiBookingService aiBookingService;
        private final BookingService bookingService;
        private final ObjectMapper objectMapper;

        private final Map<String, BookingDraft> drafts = new ConcurrentHashMap<>();

        public AiChatController(
                        AiChatService aiChatService,
                        AiBookingService aiBookingService,
                        BookingService bookingService,
                        ObjectMapper objectMapper) {

                this.aiChatService = aiChatService;
                this.aiBookingService = aiBookingService;
                this.bookingService = bookingService;
                this.objectMapper = objectMapper;
        }

        @PostMapping("/chat")
        public ResponseEntity<?> chat(
                        @RequestBody ChatRequest request)
                        throws IOException, InterruptedException {

                // ------------------------------------------------------------
                // Validate request
                // ------------------------------------------------------------

                if (request == null
                                || request.message() == null
                                || request.message().isBlank()) {

                        return ResponseEntity.badRequest()
                                        .body("""
                                                        {"message":"Message is required."}
                                                        """);
                }

                // ------------------------------------------------------------
                // Get authenticated Firebase user
                // ------------------------------------------------------------

                String uid = getFirebaseUid();

                // ------------------------------------------------------------
                // Get or create booking draft
                // ------------------------------------------------------------

                BookingDraft draft = drafts.computeIfAbsent(
                                uid,
                                key -> new BookingDraft());

                // ------------------------------------------------------------
                // Send message + current booking context to AI
                // ------------------------------------------------------------

                String context = objectMapper.writeValueAsString(draft);

                String openRouterResponse = aiChatService.chat(
                                request.message().trim(),
                                context);

                // ------------------------------------------------------------
                // Parse OpenRouter response
                // ------------------------------------------------------------

                JsonNode root = objectMapper.readTree(
                                openRouterResponse);

                JsonNode choices = root.path("choices");

                if (!choices.isArray()
                                || choices.isEmpty()) {

                        return ResponseEntity.internalServerError()
                                        .body("""
                                                        {"message":"The AI returned an invalid response."}
                                                        """);
                }

                String content = choices
                                .get(0)
                                .path("message")
                                .path("content")
                                .asText();

                // ------------------------------------------------------------
                // Parse AI JSON response
                // ------------------------------------------------------------

                JsonNode aiResponse;

                try {

                        aiResponse = objectMapper.readTree(content);

                } catch (Exception ex) {

                        return ResponseEntity.ok(
                                        new AiResponse(
                                                        "CHAT",
                                                        content,
                                                        List.of(),
                                                        List.of(),
                                                        null));
                }

                // ------------------------------------------------------------
                // Update booking draft using AI response
                // ------------------------------------------------------------

                updateDraftFromAi(
                                draft,
                                aiResponse);

                String intent = aiResponse
                                .path("intent")
                                .asText("CHAT");

                String message = aiResponse
                                .path("message")
                                .asText("");

                // ============================================================
                // CANCEL BOOKING
                // ============================================================

                if ("CANCEL_BOOKING".equals(intent)) {

                        drafts.remove(uid);

                        return ResponseEntity.ok(
                                        new AiResponse(
                                                        "CANCEL_BOOKING",
                                                        "The booking conversation has been cancelled.",
                                                        List.of(),
                                                        List.of(),
                                                        null));
                }

                // ============================================================
                // SEARCH TRIPS
                // ============================================================

                if ("SEARCH_TRIPS".equals(intent)) {

                        if (!hasTripSearchData(draft)) {

                                return ResponseEntity.ok(
                                                buildResponse(
                                                                intent,
                                                                message,
                                                                List.of(),
                                                                draft));
                        }

                        List<TripResponse> trips = aiBookingService.searchTrips(
                                        draft.origin,
                                        draft.destination,
                                        draft.date,
                                        draft.time);

                        if (trips.isEmpty()) {

                                return ResponseEntity.ok(
                                                new AiResponse(
                                                                "SEARCH_TRIPS",
                                                                "I couldn't find available trips matching those details.",
                                                                List.of(),
                                                                List.of(),
                                                                null));
                        }

                        draft.trips = trips;

                        return ResponseEntity.ok(
                                        buildResponse(
                                                        "SEARCH_TRIPS",
                                                        message,
                                                        trips,
                                                        draft));
                }

                // ============================================================
                // SELECT TRIP
                // ============================================================

                if ("SELECT_TRIP".equals(intent)) {

                        if (draft.tripId == null) {

                                return ResponseEntity.ok(
                                                new AiResponse(
                                                                "SELECT_TRIP",
                                                                "Please provide the trip you want to book.",
                                                                draft.trips,
                                                                List.of(),
                                                                null));
                        }

                        Trip trip = aiBookingService.getTrip(
                                        draft.tripId);

                        draft.tripId = trip.getId();

                        draft.availableSeats = aiBookingService.getAvailableSeats(
                                        trip.getId());

                        return ResponseEntity.ok(
                                        buildResponse(
                                                        "SELECT_TRIP",
                                                        message,
                                                        draft.trips,
                                                        draft));
                }

                // ============================================================
                // SELECT SEAT
                // ============================================================

                if ("SELECT_SEAT".equals(intent)) {

                        if (draft.tripId == null) {

                                return ResponseEntity.ok(
                                                new AiResponse(
                                                                "SELECT_TRIP",
                                                                "Please select a trip first.",
                                                                draft.trips,
                                                                List.of(),
                                                                null));
                        }

                        if (draft.seatNumber == null
                                        || draft.seatNumber.isBlank()) {

                                return ResponseEntity.ok(
                                                buildResponse(
                                                                "SELECT_SEAT",
                                                                "Which available seat would you like?",
                                                                draft.trips,
                                                                draft));
                        }

                        boolean available = aiBookingService.isSeatAvailable(
                                        draft.tripId,
                                        draft.seatNumber);

                        if (!available) {

                                draft.seatNumber = null;

                                return ResponseEntity.ok(
                                                buildResponse(
                                                                "SELECT_SEAT",
                                                                "That seat is already occupied. Please choose another available seat.",
                                                                draft.trips,
                                                                draft));
                        }

                        return ResponseEntity.ok(
                                        buildResponse(
                                                        "SELECT_SEAT",
                                                        message,
                                                        draft.trips,
                                                        draft));
                }

                // ============================================================
                // PASSENGER INFORMATION
                // ============================================================

                if ("PASSENGER_INFO".equals(intent)) {

                        return ResponseEntity.ok(
                                        buildResponse(
                                                        "PASSENGER_INFO",
                                                        message,
                                                        draft.trips,
                                                        draft));
                }

                // ============================================================
                // CONFIRM BOOKING
                // ============================================================

                if ("CONFIRM_BOOKING".equals(intent)
                                && aiResponse
                                                .path("confirmed")
                                                .asBoolean(false)) {

                        // --------------------------------------------------------
                        // Make sure all required booking information exists
                        // --------------------------------------------------------

                        if (!draft.isComplete()) {

                                return ResponseEntity.ok(
                                                buildResponse(
                                                                "PASSENGER_INFO",
                                                                "I still need all booking information before I can create the booking.",
                                                                draft.trips,
                                                                draft));
                        }

                        // --------------------------------------------------------
                        // Build CreateBookingRequest
                        // --------------------------------------------------------

                        CreateBookingRequest bookingRequest = new CreateBookingRequest();

                        bookingRequest.setTripId(
                                        draft.tripId);

                        bookingRequest.setSeatNumbers(
                                        java.util.List.of(draft.seatNumber));

                        bookingRequest.setPassengerName(
                                        draft.passengerName);

                        bookingRequest.setPassengerPhone(
                                        draft.passengerPhone);

                        bookingRequest.setPassengerEmail(
                                        draft.passengerEmail);

                        // --------------------------------------------------------
                        // Create actual database booking
                        // --------------------------------------------------------

                        var booking = bookingService.createBooking(
                                        bookingRequest);

                        // --------------------------------------------------------
                        // Clear AI booking conversation
                        // --------------------------------------------------------

                        drafts.remove(uid);

                        // --------------------------------------------------------
                        // Return actual booking result
                        // --------------------------------------------------------

                        return ResponseEntity.ok(
                                        new AiResponse(
                                                        "BOOKING_CREATED",
                                                        "Your booking has been successfully created.",
                                                        List.of(),
                                                        List.of(),
                                                        booking));
                }

                // ============================================================
                // DEFAULT RESPONSE
                // ============================================================

                return ResponseEntity.ok(
                                buildResponse(
                                                intent,
                                                message,
                                                draft.trips,
                                                draft));
        }

        // =================================================================
        // UPDATE BOOKING DRAFT
        // =================================================================

        private void updateDraftFromAi(
                        BookingDraft draft,
                        JsonNode ai) {

                setIfPresent(
                                ai,
                                "origin",
                                value -> draft.origin = value);

                setIfPresent(
                                ai,
                                "destination",
                                value -> draft.destination = value);

                setIfPresent(
                                ai,
                                "date",
                                value -> draft.date = value);

                setIfPresent(
                                ai,
                                "time",
                                value -> draft.time = value);

                if (ai.hasNonNull("tripId")
                                && !ai.path("tripId").isNull()) {

                        if (ai.path("tripId").canConvertToLong()) {

                                draft.tripId = ai.path("tripId").asLong();
                        }
                }

                setIfPresent(
                                ai,
                                "seatNumber",
                                value -> draft.seatNumber = value.toUpperCase());

                setIfPresent(
                                ai,
                                "passengerName",
                                value -> draft.passengerName = value);

                setIfPresent(
                                ai,
                                "passengerPhone",
                                value -> draft.passengerPhone = value);

                setIfPresent(
                                ai,
                                "passengerEmail",
                                value -> draft.passengerEmail = value);
        }

        // =================================================================
        // SET VALUE IF PRESENT
        // =================================================================

        private void setIfPresent(
                        JsonNode node,
                        String field,
                        java.util.function.Consumer<String> consumer) {

                if (node.has(field)
                                && !node.path(field).isNull()
                                && !node.path(field)
                                                .asText()
                                                .isBlank()) {

                        consumer.accept(
                                        node.path(field)
                                                        .asText()
                                                        .trim());
                }
        }

        // =================================================================
        // CHECK SEARCH DATA
        // =================================================================

        private boolean hasTripSearchData(
                        BookingDraft draft) {

                return draft.origin != null
                                && draft.destination != null
                                && draft.date != null;
        }

        // =================================================================
        // BUILD NORMAL AI RESPONSE
        // =================================================================

        private AiResponse buildResponse(
                        String intent,
                        String message,
                        List<TripResponse> trips,
                        BookingDraft draft) {

                return new AiResponse(
                                intent,
                                message,
                                trips,
                                draft.availableSeats,
                                draft);
        }

        // =================================================================
        // FIREBASE UID
        // =================================================================

        private String getFirebaseUid() {

                Authentication authentication = SecurityContextHolder
                                .getContext()
                                .getAuthentication();

                Object principal = authentication.getPrincipal();

                if (!(principal instanceof FirebaseToken token)) {

                        throw new IllegalStateException(
                                        "Authenticated Firebase user is required.");
                }

                return token.getUid();
        }

        // =================================================================
        // CHAT REQUEST
        // =================================================================

        public record ChatRequest(
                        String message) {
        }

        // =================================================================
        // AI RESPONSE
        // =================================================================
        //
        // Object is intentional here because:
        //
        // Normal conversation:
        // booking = BookingDraft
        //
        // Successful booking:
        // booking = BookingResponse
        //
        // =================================================================

        public record AiResponse(
                        String intent,
                        String message,
                        List<TripResponse> trips,
                        List<String> availableSeats,
                        Object booking) {
        }

        // =================================================================
        // BOOKING DRAFT
        // =================================================================

        public static class BookingDraft {

                public String origin;
                public String destination;
                public String date;
                public String time;

                public Long tripId;
                public String seatNumber;

                public String passengerName;
                public String passengerPhone;
                public String passengerEmail;

                public List<TripResponse> trips = List.of();

                public List<String> availableSeats = List.of();

                public boolean isComplete() {

                        return tripId != null
                                        && seatNumber != null
                                        && !seatNumber.isBlank()
                                        && passengerName != null
                                        && !passengerName.isBlank()
                                        && passengerPhone != null
                                        && !passengerPhone.isBlank()
                                        && passengerEmail != null
                                        && !passengerEmail.isBlank();
                }
        }
}