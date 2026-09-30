package com.bussin.desktop.data;

public final class QueueStore {

    private QueueStore() {
    }

    public static int getQueuePosition(String bookingId) {

        if (bookingId == null || bookingId.isBlank()) {
            return 0;
        }

        /*
         * Mock queue behavior for Phase 11.
         *
         * The real queue will eventually come from
         * the Spring Boot API.
         */

        int number;

        try {
            number = Integer.parseInt(
                    bookingId.replace("BK-", ""));
        } catch (NumberFormatException ex) {
            return 3;
        }

        return Math.max(
                1,
                ((number - 1011) % 5) + 1);
    }

    public static String getStatus(String bookingId) {

        int position = getQueuePosition(bookingId);

        if (position == 1) {
            return "Boarding";
        }

        return "Waiting";
    }

    public static String getEstimatedBoarding(
            String bookingId) {

        int position = getQueuePosition(bookingId);

        if (position <= 1) {
            return "Now";
        }

        return "Approximately "
                + ((position - 1) * 5)
                + " minutes";
    }
}