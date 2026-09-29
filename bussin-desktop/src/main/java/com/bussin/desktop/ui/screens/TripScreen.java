package com.bussin.desktop.ui.screens;

import com.bussin.desktop.ui.components.AppBadge;
import com.bussin.desktop.ui.components.AppButton;
import com.bussin.desktop.ui.components.AppCard;
import com.bussin.desktop.ui.components.AppLabel;
import com.bussin.desktop.ui.components.DataGrid;
import com.bussin.desktop.ui.components.IconFactory;
import com.bussin.desktop.ui.components.PageContent;
import com.bussin.desktop.ui.components.ResponsiveLayouts;
import com.bussin.desktop.ui.components.SectionHeader;
import com.bussin.desktop.ui.components.StatCard;
import com.bussin.desktop.ui.theme.BussinTheme;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Window;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.Scrollable;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Trip Management (frontend only, in-memory mock data).
 *
 * Staff schedule trips, assign a bus and driver, and move each trip through
 * its lifecycle: Scheduled, Boarding, In Progress, Completed. A trip can be
 * cancelled while it is Scheduled or Boarding. Dates are generated relative to
 * today so the mock schedule always has trips for "today" and "tomorrow".
 */
public class TripScreen extends JPanel {

    // ================================================================
    // CONSTANTS
    // ================================================================

    private static final String ALL_STATUS = "All statuses";
    private static final String ALL_DATES = "All dates";
    private static final String DATE_TODAY = "Today";
    private static final String DATE_TOMORROW = "Tomorrow";
    private static final String DATE_PAST = "Past trips";

    static final String SCHEDULED = "Scheduled";
    static final String BOARDING = "Boarding";
    static final String IN_PROGRESS = "In Progress";
    static final String COMPLETED = "Completed";
    static final String CANCELLED = "Cancelled";

    /** Below this width the trip table scrolls horizontally instead of clipping. */
    private static final int TABLE_MIN_WIDTH = 1120;

    /** Vertical space kept free under the table for the horizontal scrollbar. */
    private static final int SCROLLBAR_ALLOWANCE = 14;

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);

    /** Routes offered when scheduling (active routes from Route Management). */
    private static final List<RouteOption> ROUTES = List.of(
            new RouteOption("Manila → Batangas", 150),
            new RouteOption("Manila → Lucena", 195),
            new RouteOption("Manila → Bicol", 540),
            new RouteOption("Manila → Naga", 450));

    /** Fleet from Bus Management. Only assignable buses appear in the trip form. */
    private static final List<BusOption> BUSES = List.of(
            new BusOption("BUS 102", 50, "Pedro Garcia", true),
            new BusOption("BUS 108", 50, "Daniel Flores", true),
            new BusOption("BUS 114", 50, "Mark Villanueva", true),
            new BusOption("BUS 119", 50, "Sofia Ramos", true),
            new BusOption("BUS 121", 60, "Carlo Reyes", true),
            new BusOption("BUS 125", 60, "Unassigned", false));

    private static final List<String> DRIVERS = List.of(
            "Pedro Garcia", "Daniel Flores", "Mark Villanueva", "Sofia Ramos", "Carlo Reyes");

    // ================================================================
    // STATE
    // ================================================================

    private final List<Trip> trips = new ArrayList<>();
    private final List<Trip> filteredTrips = new ArrayList<>();

    private final JTextField searchField = new JTextField();

    private final JComboBox<String> statusFilter = new JComboBox<>(new String[] {
            ALL_STATUS, SCHEDULED, BOARDING, IN_PROGRESS, COMPLETED, CANCELLED });

    private final JComboBox<String> dateFilter = new JComboBox<>(new String[] {
            ALL_DATES, DATE_TODAY, DATE_TOMORROW, DATE_PAST });

    private final JPanel statsHolder = transparent();
    private final JPanel listHolder = transparent();

    public TripScreen() {

        initializeData();

        setBackground(BussinTheme.BACKGROUND);
        setLayout(new BorderLayout());

        PageContent page = new PageContent();

        page.addBlock(createHeader(), 0);
        page.addBlock(statsHolder, 24);
        page.addBlock(createFilterBar(), 18);
        page.addBlock(listHolder, 18);

        add(page.inScrollPane(), BorderLayout.CENTER);

        refresh();
    }

    // ================================================================
    // MOCK DATA
    // ================================================================

    private void initializeData() {

        // Buses, drivers and IDs match Bus Management so both screens tell one story.
        addTrip("TR-005", 1, 0, "05:00", "BUS 119", "Sofia Ramos", 44, COMPLETED);
        addTrip("TR-002", 1, 0, "07:00", "BUS 114", "Mark Villanueva", 42, IN_PROGRESS);
        addTrip("TR-001", 0, 0, "09:30", "BUS 102", "Pedro Garcia", 36, BOARDING);
        addTrip("TR-003", 2, 0, "11:30", "BUS 121", "Carlo Reyes", 18, SCHEDULED);
        addTrip("TR-004", 0, 0, "13:00", "BUS 108", "Daniel Flores", 0, SCHEDULED);
        addTrip("TR-006", 2, 0, "17:30", "BUS 125", "Unassigned", 0, CANCELLED);

        addTrip("TR-007", 3, 1, "06:00", "BUS 121", "Carlo Reyes", 12, SCHEDULED);
        addTrip("TR-008", 0, 1, "08:00", "BUS 108", "Daniel Flores", 5, SCHEDULED);
        addTrip("TR-009", 1, 1, "09:30", "BUS 119", "Sofia Ramos", 0, SCHEDULED);

        addTrip("TR-010", 0, -1, "16:00", "BUS 102", "Pedro Garcia", 47, COMPLETED);
    }

    private void addTrip(String id, int routeIndex, int dayOffset, String time,
            String busNumber, String driver, int booked, String status) {

        RouteOption route = ROUTES.get(routeIndex);
        LocalDateTime departure = LocalDate.now().plusDays(dayOffset).atTime(LocalTime.parse(time));

        trips.add(new Trip(id, route, departure, departure.plusMinutes(route.minutes()),
                busByNumber(busNumber), driver, booked, status));
    }

    private static BusOption busByNumber(String number) {

        for (BusOption bus : BUSES) {
            if (bus.number().equals(number)) {
                return bus;
            }
        }

        return BUSES.get(0);
    }

    // ================================================================
    // HEADER + FILTERS
    // ================================================================

    private JPanel createHeader() {

        JPanel header = new JPanel(new BorderLayout(24, 0));
        header.setOpaque(false);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(AppLabel.title("Trip Management"));
        text.add(Box.createVerticalStrut(4));
        text.add(AppLabel.secondary(
                "Manage scheduled trips, assigned buses, routes, and trip status."));

        AppButton refreshButton = new AppButton("Refresh", AppButton.Variant.SECONDARY);
        refreshButton.setIcon(IconFactory.create("arrow-right", 16, BussinTheme.TEXT_PRIMARY));
        refreshButton.addActionListener(e -> refresh());

        AppButton addButton = new AppButton("Add Trip");
        addButton.setIcon(IconFactory.create("plus", 16, java.awt.Color.WHITE));
        addButton.addActionListener(e -> showTripForm(null));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        actions.add(refreshButton);
        actions.add(addButton);

        JPanel actionHolder = new JPanel(new BorderLayout());
        actionHolder.setOpaque(false);
        actionHolder.add(actions, BorderLayout.SOUTH);

        header.add(text, BorderLayout.CENTER);
        header.add(actionHolder, BorderLayout.EAST);

        return header;
    }

    private JPanel createFilterBar() {

        AppCard card = new AppCard();
        card.setLayout(new ResponsiveLayouts.Grid(4, 180, 12));

        searchField.setToolTipText("Search by trip ID, route, bus, or driver");
        searchField.putClientProperty("JTextField.placeholderText", "Search trip, route, bus, driver");
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { refresh(); }
            @Override public void removeUpdate(DocumentEvent e) { refresh(); }
            @Override public void changedUpdate(DocumentEvent e) { refresh(); }
        });

        statusFilter.addActionListener(e -> refresh());
        dateFilter.addActionListener(e -> refresh());

        AppButton clear = new AppButton("Clear Filters", AppButton.Variant.SECONDARY);
        clear.addActionListener(e -> clearFilters());

        card.add(searchField);
        card.add(statusFilter);
        card.add(dateFilter);
        card.add(clear);

        return card;
    }

    private void clearFilters() {

        searchField.setText("");
        statusFilter.setSelectedIndex(0);
        dateFilter.setSelectedIndex(0);
    }

    // ================================================================
    // REFRESH / RENDER
    // ================================================================

    private void refresh() {

        filteredTrips.clear();

        for (Trip trip : trips) {
            if (matches(trip)) {
                filteredTrips.add(trip);
            }
        }

        filteredTrips.sort(Comparator.comparing((Trip t) -> t.departure));

        renderStats();
        renderList();

        revalidate();
        repaint();
    }

    private boolean matches(Trip trip) {

        String query = searchField.getText().trim().toLowerCase(Locale.ROOT);

        boolean matchesSearch = query.isEmpty()
                || trip.id.toLowerCase(Locale.ROOT).contains(query)
                || trip.route.name().toLowerCase(Locale.ROOT).contains(query)
                || trip.bus.number().toLowerCase(Locale.ROOT).contains(query)
                || trip.driver.toLowerCase(Locale.ROOT).contains(query);

        String status = String.valueOf(statusFilter.getSelectedItem());
        boolean matchesStatus = status.equals(ALL_STATUS) || trip.status.equals(status);

        LocalDate today = LocalDate.now();
        LocalDate day = trip.departure.toLocalDate();

        boolean matchesDate = switch (String.valueOf(dateFilter.getSelectedItem())) {
            case DATE_TODAY -> day.equals(today);
            case DATE_TOMORROW -> day.equals(today.plusDays(1));
            case DATE_PAST -> day.isBefore(today);
            default -> true;
        };

        return matchesSearch && matchesStatus && matchesDate;
    }

    /** Statistics are derived from the trips currently shown in the table. */
    private void renderStats() {

        LocalDate today = LocalDate.now();

        int todayCount = 0, scheduled = 0, boarding = 0, inProgress = 0, completed = 0;

        for (Trip t : filteredTrips) {

            if (t.departure.toLocalDate().equals(today)) {
                todayCount++;
            }

            switch (t.status) {
                case SCHEDULED -> scheduled++;
                case BOARDING -> boarding++;
                case IN_PROGRESS -> inProgress++;
                case COMPLETED -> completed++;
                default -> { }
            }
        }

        statsHolder.removeAll();
        statsHolder.setLayout(new ResponsiveLayouts.Grid(4, 200, 14));
        statsHolder.add(new StatCard("TODAY'S TRIPS", String.valueOf(todayCount),
                "departing today", "trip"));
        statsHolder.add(new StatCard("SCHEDULED", String.valueOf(scheduled),
                "awaiting boarding", "queue"));
        statsHolder.add(new StatCard("IN PROGRESS", String.valueOf(inProgress),
                boarding + " boarding now", "bus"));
        statsHolder.add(new StatCard("COMPLETED", String.valueOf(completed),
                "finished trips", "seat"));
    }

    private void renderList() {

        listHolder.removeAll();
        listHolder.setLayout(new BorderLayout());

        AppCard card = new AppCard();
        card.setLayout(new BorderLayout(0, 10));
        card.add(new SectionHeader("Trip Schedule",
                filteredTrips.size() + " of " + trips.size() + " trips shown"),
                BorderLayout.NORTH);

        if (filteredTrips.isEmpty()) {

            card.add(emptyState("No trips match the current filters."), BorderLayout.CENTER);

        } else {

            DataGrid grid = new DataGrid(
                    new String[] { "Trip", "Route", "Departure", "Arrival", "Bus", "Driver",
                            "Booked", "Seats Left", "Status", "Actions" },
                    new double[] { 0.8, 1.7, 1.1, 1.1, 0.8, 1.3, 0.8, 0.9, 1.1, 2.6 });

            for (Trip t : filteredTrips) {

                grid.addRow(
                        DataGrid.strong(t.id),
                        DataGrid.text(t.route.name()),
                        twoLine(t.departure.format(TIME_FORMAT), t.departure.format(DATE_FORMAT)),
                        twoLine(t.arrival.format(TIME_FORMAT), arrivalNote(t)),
                        DataGrid.text(t.bus.number()),
                        DataGrid.text(t.driver),
                        DataGrid.text(t.booked + " / " + t.capacity()),
                        availableCell(t),
                        new AppBadge(t.status, badgeFor(t.status)),
                        createActionCell(t));
            }

            card.add(new HorizontalScrollHolder(grid), BorderLayout.CENTER);
        }

        listHolder.add(card, BorderLayout.CENTER);
    }

    private JComponent availableCell(Trip t) {

        int free = t.capacity() - t.booked;

        if (free == 0 && t.isActive()) {
            return DataGrid.colored("Full", BussinTheme.WARNING);
        }

        return DataGrid.text(String.valueOf(free));
    }

    private static String arrivalNote(Trip t) {

        return t.arrival.toLocalDate().equals(t.departure.toLocalDate())
                ? "same day"
                : "+1 day";
    }

    private JPanel createActionCell(Trip t) {

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        actions.setOpaque(false);

        AppButton view = small("View", AppButton.Variant.SECONDARY);
        view.setToolTipText("View trip details");
        view.addActionListener(e -> showDetails(t));
        actions.add(view);

        if (t.canEdit()) {
            AppButton edit = small("Edit", AppButton.Variant.SECONDARY);
            edit.setToolTipText("Edit trip");
            edit.addActionListener(e -> showTripForm(t));
            actions.add(edit);
        }

        String next = nextStepShort(t);

        if (next != null) {
            AppButton step = small(next, AppButton.Variant.PRIMARY);
            step.setToolTipText(nextStepLabel(t));
            step.addActionListener(e -> advance(t));
            actions.add(step);
        }

        if (t.canCancel()) {
            AppButton cancel = small("Cancel", AppButton.Variant.GHOST);
            cancel.setToolTipText("Cancel trip");
            cancel.addActionListener(e -> cancelTrip(t, true));
            actions.add(cancel);
        }

        return actions;
    }

    // ================================================================
    // STATUS WORKFLOW
    // ================================================================

    /** Scheduled -> Boarding (today or earlier only) -> In Progress -> Completed. */
    private static String nextStepLabel(Trip t) {

        return switch (t.status) {
            case SCHEDULED -> t.canStartBoarding() ? "Start Boarding" : null;
            case BOARDING -> "Start Trip";
            case IN_PROGRESS -> "Complete Trip";
            default -> null;
        };
    }

    private static String nextStepShort(Trip t) {

        return switch (t.status) {
            case SCHEDULED -> t.canStartBoarding() ? "Board" : null;
            case BOARDING -> "Depart";
            case IN_PROGRESS -> "Complete";
            default -> null;
        };
    }

    /** Moves the trip to its next status. Returns false when no step applies. */
    boolean advance(Trip t) {

        if (nextStepLabel(t) == null) {
            return false;
        }

        switch (t.status) {
            case SCHEDULED -> t.status = BOARDING;
            case BOARDING -> t.status = IN_PROGRESS;
            case IN_PROGRESS -> t.status = COMPLETED;
            default -> {
                return false;
            }
        }

        refresh();

        return true;
    }

    /** Cancels a Scheduled or Boarding trip; asks for confirmation when requested. */
    boolean cancelTrip(Trip t, boolean confirm) {

        if (!t.canCancel()) {
            return false;
        }

        if (confirm) {

            int result = JOptionPane.showConfirmDialog(this,
                    "Cancel " + t.id + " (" + t.route.name() + ", "
                            + t.departure.format(TIME_FORMAT) + ")?\nBooked seats will need to be re-accommodated.",
                    "Cancel Trip",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (result != JOptionPane.YES_OPTION) {
                return false;
            }
        }

        t.status = CANCELLED;

        refresh();

        return true;
    }

    private static AppBadge.Status badgeFor(String status) {

        return switch (status) {
            case BOARDING -> AppBadge.Status.WARNING;
            case IN_PROGRESS -> AppBadge.Status.INFO;
            case COMPLETED -> AppBadge.Status.SUCCESS;
            case CANCELLED -> AppBadge.Status.DANGER;
            default -> AppBadge.Status.NEUTRAL;
        };
    }

    // ================================================================
    // TRIP DETAILS DIALOG
    // ================================================================

    private void showDetails(Trip t) {

        JDialog dialog = createDialog("Trip Details");

        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setBackground(BussinTheme.BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(22, 24, 18, 24));

        // Title row: trip ID, route, status.
        JPanel titleText = new JPanel();
        titleText.setOpaque(false);
        titleText.setLayout(new BoxLayout(titleText, BoxLayout.Y_AXIS));

        JLabel id = new JLabel(t.id);
        id.setFont(BussinTheme.SECTION_TITLE);
        id.setForeground(BussinTheme.TEXT_PRIMARY);
        titleText.add(id);

        JLabel route = new JLabel(t.route.name());
        route.setFont(BussinTheme.BODY);
        route.setForeground(BussinTheme.TEXT_SECONDARY);
        titleText.add(route);

        JPanel badgeHolder = new JPanel(new BorderLayout());
        badgeHolder.setOpaque(false);
        badgeHolder.add(new AppBadge(t.status, badgeFor(t.status)), BorderLayout.NORTH);

        JPanel title = new JPanel(new BorderLayout(12, 0));
        title.setOpaque(false);
        title.add(titleText, BorderLayout.CENTER);
        title.add(badgeHolder, BorderLayout.EAST);

        // Fact grid.
        int free = t.capacity() - t.booked;
        int occupancy = t.capacity() == 0 ? 0 : Math.round(t.booked * 100f / t.capacity());
        Duration travel = Duration.between(t.departure, t.arrival);

        AppCard facts = new AppCard();
        facts.setLayout(new GridBagLayout());

        int row = 0;
        row = addFact(facts, row, "DATE", t.departure.format(DATE_FORMAT), "DURATION", formatDuration(travel));
        row = addFact(facts, row, "DEPARTURE", t.departure.format(TIME_FORMAT),
                "EST. ARRIVAL", t.arrival.format(TIME_FORMAT) + (arrivalNote(t).contains("+1") ? "  (+1 day)" : ""));
        row = addFact(facts, row, "BUS", t.bus.number() + "  ·  " + t.bus.capacity() + " seats",
                "DRIVER", t.driver);
        row = addFact(facts, row, "BOOKED", t.booked + " of " + t.capacity(),
                "AVAILABLE", String.valueOf(free));

        JProgressBar occupancyBar = new JProgressBar(0, 100);
        occupancyBar.setValue(occupancy);
        occupancyBar.setStringPainted(true);
        occupancyBar.setString(occupancy + "% occupied");
        occupancyBar.setFont(BussinTheme.SMALL_BOLD);
        occupancyBar.setForeground(BussinTheme.RED);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 0, 0, 0);
        facts.add(occupancyBar, gbc);

        // Booking information (mock, no cross-screen sync).
        JLabel bookingNote = new JLabel("<html>" + bookingSummary(t, free)
                + " Passenger-level bookings are managed in Booking Management.</html>");
        bookingNote.setFont(BussinTheme.SMALL);
        bookingNote.setForeground(BussinTheme.TEXT_MUTED);

        JPanel body = new JPanel(new BorderLayout(0, 10));
        body.setOpaque(false);
        body.add(facts, BorderLayout.CENTER);
        body.add(bookingNote, BorderLayout.SOUTH);

        // Actions: only what makes sense for this status.
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);

        AppButton close = new AppButton("Close", AppButton.Variant.SECONDARY);
        close.addActionListener(e -> dialog.dispose());
        buttons.add(close);

        if (t.canEdit()) {
            AppButton edit = new AppButton("Edit", AppButton.Variant.SECONDARY);
            edit.addActionListener(e -> {
                dialog.dispose();
                showTripForm(t);
            });
            buttons.add(edit);
        }

        if (t.canCancel()) {
            AppButton cancel = new AppButton("Cancel Trip", AppButton.Variant.DANGER);
            cancel.addActionListener(e -> {
                if (cancelTrip(t, true)) {
                    dialog.dispose();
                }
            });
            buttons.add(cancel);
        }

        String next = nextStepLabel(t);

        if (next != null) {
            AppButton step = new AppButton(next);
            step.addActionListener(e -> {
                if (advance(t)) {
                    dialog.dispose();
                }
            });
            buttons.add(step);
        }

        root.add(title, BorderLayout.NORTH);
        root.add(body, BorderLayout.CENTER);
        root.add(buttons, BorderLayout.SOUTH);

        showDialog(dialog, root, 520);
    }

    private static String bookingSummary(Trip t, int free) {

        if (t.status.equals(CANCELLED)) {
            return t.booked == 0
                    ? "No seats were booked when this trip was cancelled."
                    : t.booked + " booked seat(s) need to be re-accommodated.";
        }

        if (free == 0) {
            return "This trip is fully booked.";
        }

        return t.booked + " seat(s) booked, " + free + " still available.";
    }

    private static String formatDuration(Duration d) {

        long hours = d.toHours();
        long minutes = d.toMinutesPart();

        return minutes == 0 ? hours + "h" : hours + "h " + minutes + "m";
    }

    private int addFact(JPanel panel, int row, String leftLabel, String leftValue,
            String rightLabel, String rightValue) {

        addFactCell(panel, 0, row, leftLabel, leftValue);
        addFactCell(panel, 1, row, rightLabel, rightValue);

        return row + 1;
    }

    private void addFactCell(JPanel panel, int column, int row, String label, String value) {

        JLabel title = new JLabel(label);
        title.setFont(BussinTheme.SMALL_BOLD);
        title.setForeground(BussinTheme.TEXT_MUTED);

        JLabel text = new JLabel(value);
        text.setFont(BussinTheme.BODY);
        text.setForeground(BussinTheme.TEXT_PRIMARY);

        JPanel cell = new JPanel();
        cell.setOpaque(false);
        cell.setLayout(new BoxLayout(cell, BoxLayout.Y_AXIS));
        cell.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 12));
        cell.add(title);
        cell.add(Box.createVerticalStrut(3));
        cell.add(text);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = column;
        gbc.gridy = row;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        panel.add(cell, gbc);
    }

    // ================================================================
    // ADD / EDIT TRIP DIALOG
    // ================================================================

    /** Opens the trip form. Pass null to add a new trip. */
    private void showTripForm(Trip editing) {

        boolean isNew = editing == null;

        JDialog dialog = createDialog(isNew ? "Add Trip" : "Edit Trip");

        JComboBox<RouteOption> routeBox = new JComboBox<>(ROUTES.toArray(new RouteOption[0]));

        List<BusOption> assignable = new ArrayList<>();
        for (BusOption bus : BUSES) {
            if (bus.assignable()) {
                assignable.add(bus);
            }
        }
        JComboBox<BusOption> busBox = new JComboBox<>(assignable.toArray(new BusOption[0]));
        JComboBox<String> driverBox = new JComboBox<>(DRIVERS.toArray(new String[0]));

        LocalDate startDate = isNew ? LocalDate.now() : editing.departure.toLocalDate();
        LocalTime startTime = isNew ? LocalTime.of(8, 0) : editing.departure.toLocalTime();

        JSpinner dateSpinner = dateSpinner(startDate);
        JSpinner departSpinner = timeSpinner(startTime);
        JSpinner arriveSpinner = timeSpinner(startTime.plusMinutes(ROUTES.get(0).minutes()));

        JLabel arrivalHint = new JLabel(" ");
        arrivalHint.setFont(BussinTheme.SMALL);
        arrivalHint.setForeground(BussinTheme.TEXT_MUTED);

        JLabel errorLabel = new JLabel(" ");
        errorLabel.setFont(BussinTheme.SMALL_BOLD);
        errorLabel.setForeground(BussinTheme.DANGER);

        if (isNew) {
            routeBox.setSelectedIndex(0);
            busBox.setSelectedIndex(0);
            driverBox.setSelectedItem(assignable.get(0).driver());
        } else {
            routeBox.setSelectedItem(editing.route);
            busBox.setSelectedItem(editing.bus);
            driverBox.setSelectedItem(editing.driver);
            arriveSpinner.setValue(toDate(editing.arrival.toLocalTime()));
        }

        Runnable estimateArrival = () -> {
            RouteOption route = (RouteOption) routeBox.getSelectedItem();
            LocalTime departs = timeOf(departSpinner);
            arriveSpinner.setValue(toDate(departs.plusMinutes(route.minutes())));
            arrivalHint.setText("Estimated from route travel time (" + formatDuration(
                    Duration.ofMinutes(route.minutes())) + "). Adjust if needed.");
        };

        if (isNew) {
            estimateArrival.run();
        } else {
            arrivalHint.setText("Adjust the arrival time if the schedule changes.");
        }

        routeBox.addActionListener(e -> estimateArrival.run());
        departSpinner.addChangeListener(e -> estimateArrival.run());
        busBox.addActionListener(e -> {
            BusOption bus = (BusOption) busBox.getSelectedItem();
            if (bus != null) {
                driverBox.setSelectedItem(bus.driver());
            }
        });

        // ---- form layout: labels above fields, two columns ----
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        int row = 0;
        addFormRow(form, row++, "Route *", routeBox, null, null);
        addFormRow(form, row++, "Date *", dateSpinner, "Departure time *", departSpinner);
        addFormRow(form, row++, "Est. arrival *", arriveSpinner, "Status", statusLabel(isNew ? SCHEDULED : editing.status));
        addFormRow(form, row++, "Bus *", busBox, "Driver *", driverBox);

        GridBagConstraints hintConstraints = new GridBagConstraints();
        hintConstraints.gridx = 0;
        hintConstraints.gridy = row;
        hintConstraints.gridwidth = 2;
        hintConstraints.weightx = 1;
        hintConstraints.anchor = GridBagConstraints.WEST;
        hintConstraints.fill = GridBagConstraints.HORIZONTAL;
        hintConstraints.insets = new Insets(0, 0, 6, 0);
        form.add(arrivalHint, hintConstraints);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);

        AppButton cancelButton = new AppButton("Cancel", AppButton.Variant.SECONDARY);
        cancelButton.addActionListener(e -> dialog.dispose());

        AppButton saveButton = new AppButton(isNew ? "Add Trip" : "Save Changes");
        saveButton.addActionListener(e -> {

            RouteOption route = (RouteOption) routeBox.getSelectedItem();
            BusOption bus = (BusOption) busBox.getSelectedItem();
            String driver = (String) driverBox.getSelectedItem();

            String error = validateTrip(editing, route, dateOf(dateSpinner), timeOf(departSpinner),
                    timeOf(arriveSpinner), bus, driver);

            if (error != null) {
                errorLabel.setText(error);
                dialog.pack();
                return;
            }

            LocalDateTime departure = dateOf(dateSpinner).atTime(timeOf(departSpinner));
            LocalDateTime arrival = arrivalFor(departure, timeOf(arriveSpinner));

            if (isNew) {
                trips.add(new Trip(generateTripId(), route, departure, arrival, bus, driver, 0, SCHEDULED));
            } else {
                editing.route = route;
                editing.departure = departure;
                editing.arrival = arrival;
                editing.bus = bus;
                editing.driver = driver;
            }

            refresh();
            dialog.dispose();
        });

        buttons.add(cancelButton);
        buttons.add(saveButton);

        JPanel south = new JPanel(new BorderLayout(0, 8));
        south.setOpaque(false);
        south.add(errorLabel, BorderLayout.NORTH);
        south.add(buttons, BorderLayout.SOUTH);

        JLabel heading = new JLabel(isNew ? "Add New Trip" : "Edit " + editing.id);
        heading.setFont(BussinTheme.SECTION_TITLE);
        heading.setForeground(BussinTheme.TEXT_PRIMARY);

        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setBackground(BussinTheme.BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(22, 24, 18, 24));
        root.add(heading, BorderLayout.NORTH);
        root.add(form, BorderLayout.CENTER);
        root.add(south, BorderLayout.SOUTH);

        dialog.getRootPane().setDefaultButton(saveButton);

        showDialog(dialog, root, 560);
    }

    /**
     * Returns the first problem with the entered trip, or null when it is valid.
     * Bus and driver may not be double-booked on an overlapping active trip.
     */
    String validateTrip(Trip editing, RouteOption route, LocalDate date, LocalTime depart,
            LocalTime arrive, BusOption bus, String driver) {

        if (route == null) {
            return "Select a route.";
        }

        if (bus == null) {
            return "Select a bus.";
        }

        if (driver == null || driver.isBlank()) {
            return "Select a driver.";
        }

        if (date == null || depart == null || arrive == null) {
            return "Enter the date, departure time, and arrival time.";
        }

        if (editing == null && date.isBefore(LocalDate.now())) {
            return "The departure date cannot be in the past.";
        }

        if (depart.equals(arrive)) {
            return "Arrival time must be different from the departure time.";
        }

        LocalDateTime departure = date.atTime(depart);
        LocalDateTime arrival = arrivalFor(departure, arrive);

        if (Duration.between(departure, arrival).toHours() > 16) {
            return "Trip duration is over 16 hours. Check the arrival time.";
        }

        int booked = editing == null ? 0 : editing.booked;

        if (booked > bus.capacity()) {
            return bus.number() + " seats only " + bus.capacity()
                    + ", but " + booked + " seats are already booked.";
        }

        for (Trip other : trips) {

            if (other == editing || !other.isActive()) {
                continue;
            }

            boolean overlaps = departure.isBefore(other.arrival) && other.departure.isBefore(arrival);

            if (!overlaps) {
                continue;
            }

            if (other.bus.number().equals(bus.number())) {
                return bus.number() + " is already assigned to " + other.id + " ("
                        + other.departure.format(TIME_FORMAT) + " - " + other.arrival.format(TIME_FORMAT) + ").";
            }

            if (other.driver.equals(driver)) {
                return driver + " is already driving " + other.id + " ("
                        + other.departure.format(TIME_FORMAT) + " - " + other.arrival.format(TIME_FORMAT) + ").";
            }
        }

        return null;
    }

    private static LocalDateTime arrivalFor(LocalDateTime departure, LocalTime arrive) {

        LocalDateTime arrival = departure.toLocalDate().atTime(arrive);

        // An arrival at or before the departure time means the bus arrives the next day.
        return arrival.isAfter(departure) ? arrival : arrival.plusDays(1);
    }

    private String generateTripId() {

        int highest = 0;

        for (Trip t : trips) {
            try {
                highest = Math.max(highest, Integer.parseInt(t.id.substring(3)));
            } catch (NumberFormatException ignored) {
                // Ignore unexpected mock IDs.
            }
        }

        return String.format("TR-%03d", highest + 1);
    }

    private void addFormRow(JPanel form, int row, String leftLabel, JComponent leftField,
            String rightLabel, JComponent rightField) {

        addFormCell(form, 0, row, leftLabel, leftField, rightField == null ? 2 : 1);

        if (rightField != null) {
            addFormCell(form, 1, row, rightLabel, rightField, 1);
        }
    }

    private void addFormCell(JPanel form, int column, int row, String label, JComponent field, int span) {

        JLabel title = new JLabel(label);
        title.setFont(BussinTheme.SMALL_BOLD);
        title.setForeground(BussinTheme.TEXT_SECONDARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        JPanel cell = new JPanel(new BorderLayout(0, 4));
        cell.setOpaque(false);
        cell.setBorder(BorderFactory.createEmptyBorder(0, column == 0 ? 0 : 6, 12, column == 0 && span == 1 ? 6 : 0));
        cell.add(title, BorderLayout.NORTH);
        cell.add(field, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = column;
        gbc.gridy = row;
        gbc.gridwidth = span;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        form.add(cell, gbc);
    }

    private JComponent statusLabel(String status) {

        JPanel holder = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 4));
        holder.setOpaque(false);
        holder.add(new AppBadge(status, badgeFor(status)));

        return holder;
    }

    // ================================================================
    // DIALOG + SPINNER HELPERS
    // ================================================================

    private JDialog createDialog(String title) {

        Window owner = SwingUtilities.getWindowAncestor(this);

        JDialog dialog = new JDialog(owner, title, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        return dialog;
    }

    /** Packs the dialog to its content, caps it to the screen, and shows it. */
    private void showDialog(JDialog dialog, JPanel root, int minWidth) {

        JScrollPane scroll = new JScrollPane(root,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BussinTheme.BACKGROUND);

        dialog.setContentPane(scroll);
        dialog.pack();

        Rectangle screen = dialog.getGraphicsConfiguration() == null
                ? new Rectangle(0, 0, 1280, 720)
                : dialog.getGraphicsConfiguration().getBounds();

        Dimension size = dialog.getSize();
        size.width = Math.min(Math.max(size.width, minWidth), screen.width - 80);
        size.height = Math.min(size.height + 4, screen.height - 120);

        dialog.setSize(size);
        dialog.setMinimumSize(new Dimension(Math.min(minWidth, size.width), Math.min(320, size.height)));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private static JSpinner dateSpinner(LocalDate date) {

        SpinnerDateModel model = new SpinnerDateModel(
                Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                null, null, Calendar.DAY_OF_MONTH);

        JSpinner spinner = new JSpinner(model);
        spinner.setEditor(new JSpinner.DateEditor(spinner, "MMM dd, yyyy"));

        return spinner;
    }

    private static JSpinner timeSpinner(LocalTime time) {

        SpinnerDateModel model = new SpinnerDateModel(toDate(time), null, null, Calendar.MINUTE);

        JSpinner spinner = new JSpinner(model);
        spinner.setEditor(new JSpinner.DateEditor(spinner, "hh:mm a"));

        return spinner;
    }

    private static Date toDate(LocalTime time) {

        return Date.from(LocalDate.now().atTime(time).atZone(ZoneId.systemDefault()).toInstant());
    }

    private static LocalTime timeOf(JSpinner spinner) {

        return ((Date) spinner.getValue()).toInstant().atZone(ZoneId.systemDefault())
                .toLocalTime().withSecond(0).withNano(0);
    }

    private static LocalDate dateOf(JSpinner spinner) {

        return ((Date) spinner.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    // ================================================================
    // SMALL UI HELPERS
    // ================================================================

    private static JPanel transparent() {

        JPanel panel = new JPanel();
        panel.setOpaque(false);

        return panel;
    }

    private static AppButton small(String text, AppButton.Variant variant) {

        AppButton button = new AppButton(text, variant);
        button.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        return button;
    }

    private static JPanel emptyState(String message) {

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 0, 24, 0));

        JLabel label = new JLabel(message);
        label.setFont(BussinTheme.BODY);
        label.setForeground(BussinTheme.TEXT_MUTED);
        panel.add(label);

        return panel;
    }

    /** Two-line table cell: primary value with a muted note underneath. */
    private static JPanel twoLine(String top, String bottom) {

        JLabel primary = DataGrid.strong(top);
        primary.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel secondary = new JLabel(bottom);
        secondary.setFont(BussinTheme.SMALL);
        secondary.setForeground(BussinTheme.TEXT_MUTED);
        secondary.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(primary);
        panel.add(secondary);

        return panel;
    }

    /**
     * Wraps the trip table so it fills the card when there is room and scrolls
     * horizontally (instead of clipping columns) when the window is narrow.
     *
     * DataGrid is a GridBagLayout: once it is given even slightly less than its
     * natural width, GridBag falls back to minimum sizes on both axes and the
     * rows collapse. The strip therefore never squeezes the grid below its
     * natural width; it scrolls instead.
     */
    private static final class HorizontalScrollHolder extends JScrollPane {

        HorizontalScrollHolder(JComponent table) {

            super(new Body(table),
                    ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

            setBorder(null);
            setOpaque(false);
            getViewport().setOpaque(false);

            // Let the mouse wheel scroll the page, not this strip.
            setWheelScrollingEnabled(false);
            getHorizontalScrollBar().setUnitIncrement(24);
        }

        private static final class Body extends JPanel implements Scrollable {

            private final JComponent table;

            Body(JComponent table) {
                super(new BorderLayout());
                this.table = table;
                setOpaque(false);

                // Room for the horizontal scrollbar, so it never overlaps the last row.
                setBorder(BorderFactory.createEmptyBorder(0, 0, SCROLLBAR_ALLOWANCE, 0));

                add(table, BorderLayout.NORTH);
            }

            @Override
            public Dimension getPreferredSize() {

                Dimension natural = table.getPreferredSize();

                return new Dimension(
                        Math.max(TABLE_MIN_WIDTH, natural.width),
                        natural.height + SCROLLBAR_ALLOWANCE);
            }

            @Override
            public Dimension getPreferredScrollableViewportSize() {
                return getPreferredSize();
            }

            @Override
            public int getScrollableUnitIncrement(Rectangle visible, int orientation, int direction) {
                return 24;
            }

            @Override
            public int getScrollableBlockIncrement(Rectangle visible, int orientation, int direction) {
                return Math.max(24, visible.width - 48);
            }

            @Override
            public boolean getScrollableTracksViewportWidth() {
                return getParent() != null && getParent().getWidth() >= getPreferredSize().width;
            }

            @Override
            public boolean getScrollableTracksViewportHeight() {
                return false;
            }
        }
    }

    // ================================================================
    // MODEL
    // ================================================================

    record RouteOption(String name, int minutes) {

        @Override
        public String toString() {
            return name + "  ·  " + formatDuration(Duration.ofMinutes(minutes));
        }
    }

    record BusOption(String number, int capacity, String driver, boolean assignable) {

        @Override
        public String toString() {
            return number + "  ·  " + capacity + " seats";
        }
    }

    static final class Trip {

        final String id;
        RouteOption route;
        LocalDateTime departure;
        LocalDateTime arrival;
        BusOption bus;
        String driver;
        final int booked;
        String status;

        Trip(String id, RouteOption route, LocalDateTime departure, LocalDateTime arrival,
                BusOption bus, String driver, int booked, String status) {

            this.id = id;
            this.route = route;
            this.departure = departure;
            this.arrival = arrival;
            this.bus = bus;
            this.driver = driver;
            this.booked = booked;
            this.status = status;
        }

        int capacity() {
            return bus.capacity();
        }

        /** Scheduled or underway: still occupies its bus and driver. */
        boolean isActive() {
            return status.equals(SCHEDULED) || status.equals(BOARDING) || status.equals(IN_PROGRESS);
        }

        boolean canEdit() {
            return status.equals(SCHEDULED);
        }

        boolean canCancel() {
            return status.equals(SCHEDULED) || status.equals(BOARDING);
        }

        /** Boarding opens on the departure day, not for future dates. */
        boolean canStartBoarding() {
            return !departure.toLocalDate().isAfter(LocalDate.now());
        }
    }
}
