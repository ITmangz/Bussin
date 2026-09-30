package com.bussin.desktop.ui.screens;

import com.bussin.desktop.ui.components.*;
import com.bussin.desktop.ui.components.AppBadge.Status;
import com.bussin.desktop.ui.theme.BussinTheme;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import javax.swing.*;

/**
 * Operational overview: key figures, today's trips, queue flow and the latest
 * bookings. All data is static mock data for now.
 */
public class DashboardScreen extends JPanel {

        // ================================================================
        // MOCK DATA
        // ================================================================

        private record Trip(
                        String departure,
                        String origin,
                        String destination,
                        String bus,
                        String status,
                        int seatsLeft,
                        int capacity) {

                boolean isActive() {
                        return status.equals("Boarding") || status.equals("On Route");
                }
        }

        private record Booking(
                        String passenger,
                        String reference,
                        String route,
                        String status,
                        String time) {
        }

        private record QueueEntry(
                        String number,
                        String passenger,
                        String destination) {
        }

        private static final List<Trip> TRIPS = List.of(
                        new Trip("05:30 AM", "Manila", "Batangas City", "BUS 101", "On Route", 9, 50),
                        new Trip("06:15 AM", "Batangas City", "Manila", "BUS 108", "On Route", 11, 50),
                        new Trip("07:00 AM", "Manila", "Lucena", "BUS 114", "On Route", 4, 45),
                        new Trip("08:30 AM", "Manila", "Naga", "BUS 120", "Boarding", 17, 50),
                        new Trip("09:30 AM", "Manila", "Batangas City", "BUS 102", "Boarding", 14, 50),
                        new Trip("10:00 AM", "Manila", "Lucena", "BUS 115", "Boarding", 22, 45),
                        new Trip("11:15 AM", "Lucena", "Manila", "BUS 109", "Scheduled", 45, 45),
                        new Trip("12:30 PM", "Manila", "Legazpi", "BUS 127", "Delayed", 38, 50),
                        new Trip("01:00 PM", "Manila", "Baguio", "BUS 131", "Scheduled", 42, 50));

        private static final List<Booking> BOOKINGS = List.of(
                        new Booking("Maria Santos", "BSN-2026-0148", "Manila → Batangas City", "Confirmed", "09:42 AM"),
                        new Booking("Jose Ramirez", "BSN-2026-0147", "Manila → Lucena", "Pending", "09:38 AM"),
                        new Booking("Ana Reyes", "BSN-2026-0146", "Manila → Naga", "Confirmed", "09:21 AM"),
                        new Booking("Carlo Mendoza", "BSN-2026-0145", "Lucena → Manila", "Cancelled", "09:05 AM"),
                        new Booking("Liza Villanueva", "BSN-2026-0144", "Manila → Baguio", "Confirmed", "08:47 AM"),
                        new Booking("Ramon Bautista", "BSN-2026-0143", "Batangas City → Manila", "Pending", "08:30 AM"));

        private static final List<QueueEntry> NEXT_IN_LINE = List.of(
                        new QueueEntry("Q-025", "Juan Dela Cruz", "Batangas City"),
                        new QueueEntry("Q-026", "Grace Aquino", "Lucena"),
                        new QueueEntry("Q-027", "Paolo Garcia", "Naga"),
                        new QueueEntry("Q-028", "Nina Castillo", "Baguio"));

        private static final int QUEUE_WAITING = 24;
        private static final int QUEUE_BOARDING = 18;
        private static final int QUEUE_COMPLETED = 96;

        private static final int BOOKINGS_TODAY = 148;
        private static final int BOOKINGS_PENDING = 26;
        private static final int BUSES_AVAILABLE = 12;
        private static final int BUSES_TOTAL = 18;

        // ================================================================
        // CONSTRUCTOR
        // ================================================================

        public DashboardScreen() {

                setBackground(BussinTheme.BACKGROUND);

                setLayout(new BorderLayout());

                PageContent page = new PageContent();

                page.addBlock(createHeader(), 0);
                page.addBlock(createStatistics(), 24);
                page.addBlock(createTripsAndQueue(), 18);
                page.addBlock(createRecentBookings(), 18);

                add(page.inScrollPane(), BorderLayout.CENTER);
        }

        // ================================================================
        // HEADER
        // ================================================================

        private JPanel createHeader() {

                JPanel header = new JPanel(new BorderLayout(24, 0));
                header.setOpaque(false);

                JPanel text = new JPanel();
                text.setOpaque(false);
                text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

                JLabel title = AppLabel.title("Dashboard");
                JLabel subtitle = AppLabel.secondary(
                                "Today's departures, passenger queue, and booking activity across all routes.");

                text.add(title);
                text.add(Box.createVerticalStrut(4));
                text.add(subtitle);

                JPanel context = new JPanel();
                context.setOpaque(false);
                context.setLayout(new BoxLayout(context, BoxLayout.Y_AXIS));

                String today = LocalDate.now().format(
                                DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.ENGLISH));

                JLabel date = AppLabel.secondary(today);
                date.setAlignmentX(Component.RIGHT_ALIGNMENT);

                JLabel role = AppLabel.muted("Administrator view · All routes");
                role.setAlignmentX(Component.RIGHT_ALIGNMENT);

                context.add(date);
                context.add(Box.createVerticalStrut(2));
                context.add(role);

                JPanel contextHolder = new JPanel(new BorderLayout());
                contextHolder.setOpaque(false);
                contextHolder.add(context, BorderLayout.SOUTH);

                header.add(text, BorderLayout.CENTER);
                header.add(contextHolder, BorderLayout.EAST);

                return header;
        }

        // ================================================================
        // STATISTICS
        // ================================================================

        private JPanel createStatistics() {

                long activeTrips = TRIPS.stream().filter(Trip::isActive).count();

                JPanel stats = new JPanel(new ResponsiveLayouts.Grid(4, 200, 14));
                stats.setOpaque(false);

                stats.add(new StatCard(
                                "TODAY'S BOOKINGS",
                                String.valueOf(BOOKINGS_TODAY),
                                BOOKINGS_PENDING + " pending confirmation",
                                "booking"));

                stats.add(new StatCard(
                                "ACTIVE TRIPS",
                                String.valueOf(activeTrips),
                                "of " + TRIPS.size() + " scheduled today",
                                "trip"));

                stats.add(new StatCard(
                                "PENDING QUEUE",
                                String.valueOf(QUEUE_WAITING),
                                QUEUE_BOARDING + " boarding now",
                                "queue"));

                stats.add(new StatCard(
                                "AVAILABLE BUSES",
                                String.valueOf(BUSES_AVAILABLE),
                                "of " + BUSES_TOTAL + " in fleet",
                                "bus"));

                return stats;
        }

        // ================================================================
        // TODAY'S TRIPS + QUEUE OVERVIEW
        // ================================================================

        private JPanel createTripsAndQueue() {

                JPanel row = new JPanel(new ResponsiveLayouts.Split(18, 1000, 2, 1));
                row.setOpaque(false);

                row.add(createTodaysTrips());
                row.add(createQueueOverview());

                return row;
        }

        private AppCard createTodaysTrips() {

                AppCard card = new AppCard();
                card.setLayout(new BorderLayout(0, 12));

                card.add(
                                new SectionHeader(
                                                "Today's Trips",
                                                "Scheduled departures and seat availability",
                                                AppLabel.muted(TRIPS.size() + " trips")),
                                BorderLayout.NORTH);

                DataGrid grid = new DataGrid(
                                new String[] { "Departure", "Origin", "Destination", "Bus", "Status", "Available Seats" },
                                new double[] { 1.0, 1.4, 1.6, 1.0, 1.3, 1.3 });

                for (Trip trip : TRIPS) {

                        grid.addRow(
                                        DataGrid.strong(trip.departure()),
                                        DataGrid.text(trip.origin()),
                                        DataGrid.text(trip.destination()),
                                        DataGrid.text(trip.bus()),
                                        new AppBadge(trip.status(), tripStatus(trip.status())),
                                        seatsLabel(trip));
                }

                card.add(grid, BorderLayout.CENTER);

                return card;
        }

        private JLabel seatsLabel(Trip trip) {

                if (trip.seatsLeft() == 0) {
                        return DataGrid.colored("Full", BussinTheme.DANGER);
                }

                String value = trip.seatsLeft() + " of " + trip.capacity();

                return trip.seatsLeft() <= 5
                                ? DataGrid.colored(value, BussinTheme.DANGER)
                                : DataGrid.text(value);
        }

        private AppCard createQueueOverview() {

                int total = QUEUE_WAITING + QUEUE_BOARDING + QUEUE_COMPLETED;

                AppCard card = new AppCard();
                card.setLayout(new BorderLayout(0, 14));

                card.add(
                                new SectionHeader(
                                                "Queue Overview",
                                                "Passenger flow today",
                                                AppLabel.muted(total + " total")),
                                BorderLayout.NORTH);

                JPanel body = new JPanel();
                body.setOpaque(false);
                body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

                DistributionBar bar = new DistributionBar(
                                new int[] { QUEUE_WAITING, QUEUE_BOARDING, QUEUE_COMPLETED },
                                new Color[] { BussinTheme.WARNING, BussinTheme.PRIMARY, BussinTheme.SUCCESS });
                bar.setAlignmentX(Component.LEFT_ALIGNMENT);

                body.add(bar);
                body.add(Box.createVerticalStrut(8));

                body.add(queueRow("Waiting", "Not yet called", QUEUE_WAITING, BussinTheme.WARNING));
                body.add(queueRow("Boarding", "Called to the gate", QUEUE_BOARDING, BussinTheme.PRIMARY));
                body.add(queueRow("Completed", "Boarded and departed", QUEUE_COMPLETED, BussinTheme.SUCCESS));

                body.add(Box.createVerticalStrut(18));

                JLabel next = new JLabel("NEXT IN LINE");
                next.setFont(BussinTheme.SMALL_BOLD);
                next.setForeground(BussinTheme.TEXT_MUTED);
                next.setAlignmentX(Component.LEFT_ALIGNMENT);

                body.add(next);
                body.add(Box.createVerticalStrut(2));

                DataGrid nextGrid = new DataGrid(
                                new String[] { "Queue No.", "Passenger", "Destination" },
                                new double[] { 1.0, 1.7, 1.4 });
                nextGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

                for (QueueEntry entry : NEXT_IN_LINE) {

                        nextGrid.addRow(
                                        DataGrid.strong(entry.number()),
                                        DataGrid.text(entry.passenger()),
                                        DataGrid.text(entry.destination()));
                }

                body.add(nextGrid);

                card.add(body, BorderLayout.CENTER);

                return card;
        }

        private JPanel queueRow(String label, String description, int count, Color color) {

                JPanel row = new JPanel(new BorderLayout(10, 0));
                row.setOpaque(false);
                row.setAlignmentX(Component.LEFT_ALIGNMENT);
                row.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

                JPanel swatch = new JPanel();
                swatch.setBackground(color);
                swatch.setPreferredSize(new Dimension(10, 10));

                JPanel swatchHolder = new JPanel(new GridBagLayout());
                swatchHolder.setOpaque(false);
                swatchHolder.add(swatch);

                JPanel text = new JPanel();
                text.setOpaque(false);
                text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
                text.add(DataGrid.strong(label));
                text.add(AppLabel.muted(description));

                JLabel value = new JLabel(String.valueOf(count));
                value.setFont(BussinTheme.SECTION_TITLE);
                value.setForeground(BussinTheme.TEXT_PRIMARY);

                row.add(swatchHolder, BorderLayout.WEST);
                row.add(text, BorderLayout.CENTER);
                row.add(value, BorderLayout.EAST);

                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, row.getPreferredSize().height));

                return row;
        }

        // ================================================================
        // RECENT BOOKINGS
        // ================================================================

        private AppCard createRecentBookings() {

                AppCard card = new AppCard();
                card.setLayout(new BorderLayout(0, 12));

                card.add(
                                new SectionHeader(
                                                "Recent Bookings",
                                                "Latest reservations across all routes",
                                                AppLabel.muted("Latest " + BOOKINGS.size() + " of " + BOOKINGS_TODAY)),
                                BorderLayout.NORTH);

                DataGrid grid = new DataGrid(
                                new String[] { "Passenger", "Booking No.", "Route", "Status", "Time" },
                                new double[] { 1.6, 1.6, 2.2, 1.2, 1.0 });

                for (Booking booking : BOOKINGS) {

                        grid.addRow(
                                        DataGrid.strong(booking.passenger()),
                                        DataGrid.text(booking.reference()),
                                        DataGrid.text(booking.route()),
                                        new AppBadge(booking.status(), bookingStatus(booking.status())),
                                        DataGrid.text(booking.time()));
                }

                card.add(grid, BorderLayout.CENTER);

                return card;
        }

        // ================================================================
        // STATUS MAPPING
        // ================================================================

        private static Status tripStatus(String status) {

                return switch (status) {
                        case "On Route" -> Status.SUCCESS;
                        case "Boarding" -> Status.WARNING;
                        case "Delayed" -> Status.DANGER;
                        default -> Status.INFO;
                };
        }

        private static Status bookingStatus(String status) {

                return switch (status) {
                        case "Confirmed" -> Status.SUCCESS;
                        case "Pending" -> Status.WARNING;
                        case "Cancelled" -> Status.DANGER;
                        default -> Status.INFO;
                };
        }

        // ================================================================
        // DISTRIBUTION BAR
        // ================================================================

        /** Flat proportional bar; segments are drawn in the given order. */
        private static final class DistributionBar extends JComponent {

                private final int[] values;
                private final Color[] colors;

                DistributionBar(int[] values, Color[] colors) {
                        this.values = values;
                        this.colors = colors;
                        setPreferredSize(new Dimension(100, 8));
                        setMaximumSize(new Dimension(Integer.MAX_VALUE, 8));
                }

                @Override
                protected void paintComponent(Graphics g) {

                        int total = 0;

                        for (int v : values) {
                                total += v;
                        }

                        if (total == 0) {
                                return;
                        }

                        int x = 0;

                        for (int i = 0; i < values.length; i++) {

                                int end = (i == values.length - 1)
                                                ? getWidth()
                                                : x + Math.round((float) getWidth() * values[i] / total);

                                g.setColor(colors[i]);
                                g.fillRect(x, 0, Math.max(0, end - x - (i < values.length - 1 ? 2 : 0)), getHeight());

                                x = end;
                        }
                }
        }
}
