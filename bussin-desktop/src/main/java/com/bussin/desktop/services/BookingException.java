package com.bussin.desktop.services;

/**
 * Failure raised by {@link BookingService}. The {@link Kind} mirrors the HTTP
 * status a REST backend would return, so the UI can be reused unchanged if the
 * service is later replaced by an HTTP client.
 */
public class BookingException extends Exception {

    public enum Kind {
        INVALID_REQUEST(400),
        UNAUTHENTICATED(401),
        FORBIDDEN(403),
        NOT_FOUND(404),
        SEAT_UNAVAILABLE(409),
        INTERNAL(500);

        private final int httpStatus;

        Kind(int httpStatus) {
            this.httpStatus = httpStatus;
        }

        public int httpStatus() {
            return httpStatus;
        }
    }

    private final Kind kind;

    public BookingException(Kind kind, String message) {
        super(message);
        this.kind = kind;
    }

    public BookingException(Kind kind, String message, Throwable cause) {
        super(message, cause);
        this.kind = kind;
    }

    public Kind getKind() {
        return kind;
    }
}
