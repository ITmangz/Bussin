package com.bussin.desktop.ui.screens;

import com.bussin.desktop.ui.components.AppBadge;
import com.bussin.desktop.ui.components.AppButton;
import com.bussin.desktop.ui.components.AppCard;
import com.bussin.desktop.ui.components.AppLabel;
import com.bussin.desktop.ui.components.IconFactory;
import com.bussin.desktop.ui.theme.BussinTheme;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TripScreen extends JPanel {

    private final List<Trip> trips = new ArrayList<>();
    private final List<Trip> filteredTrips = new ArrayList<>();

    private final TripTableModel tableModel = new TripTableModel();

    private final JTable tripTable = new JTable(tableModel);

    private final JTextField searchField = new JTextField();

    private final JComboBox<String> statusFilter = new JComboBox<>(
            new String[] {
                    "All Status",
                    "Scheduled",
                    "Boarding",
                    "On Route",
                    "Completed",
                    "Cancelled"
            });

    private final JLabel totalValue = new JLabel("0");
    private final JLabel scheduledValue = new JLabel("0");
    private final JLabel boardingValue = new JLabel("0");
    private final JLabel completedValue = new JLabel("0");

    private final JLabel detailTripId = new JLabel("-");
    private final JLabel detailRoute = new JLabel("-");
    private final JLabel detailBus = new JLabel("-");
    private final JLabel detailDeparture = new JLabel("-");
    private final JLabel detailCapacity = new JLabel("-");
    private final JLabel detailBooked = new JLabel("-");
    private final JLabel detailAvailable = new JLabel("-");

    private final JProgressBar seatProgress = new JProgressBar();

    private final AppBadge detailStatus = new AppBadge(
            "NO STATUS",
            AppBadge.Status.NEUTRAL);

    private final AppButton startBoardingButton = new AppButton("Start Boarding");

    private final AppButton completeTripButton = new AppButton(
            "Complete",
            AppButton.Variant.SECONDARY);

    private final AppButton cancelTripButton = new AppButton(
            "Cancel Trip",
            AppButton.Variant.DANGER);

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern(
            "MMM dd, yyyy • hh:mm a");

    public TripScreen() {

        initializeData();
        initializeUI();
        refreshTrips();
    }

    private void initializeData() {

        trips.add(
                new Trip(
                        "TR-001",
                        "Manila → Batangas",
                        "BUS 102",
                        "09:30 AM",
                        50,
                        36,
                        "Boarding"));

        trips.add(
                new Trip(
                        "TR-002",
                        "Manila → Lucena",
                        "BUS 114",
                        "10:00 AM",
                        50,
                        42,
                        "On Route"));

        trips.add(
                new Trip(
                        "TR-003",
                        "Manila → Bicol",
                        "BUS 121",
                        "11:30 AM",
                        60,
                        18,
                        "Scheduled"));

        trips.add(
                new Trip(
                        "TR-004",
                        "Manila → Batangas",
                        "BUS 108",
                        "01:00 PM",
                        50,
                        0,
                        "Scheduled"));

        trips.add(
                new Trip(
                        "TR-005",
                        "Manila → Lucena",
                        "BUS 119",
                        "03:00 PM",
                        50,
                        44,
                        "Completed"));

        trips.add(
                new Trip(
                        "TR-006",
                        "Manila → Bicol",
                        "BUS 125",
                        "05:30 PM",
                        60,
                        0,
                        "Cancelled"));
    }

    private void initializeUI() {

        setOpaque(true);
        setBackground(
                BussinTheme.BACKGROUND);

        setLayout(
                new BorderLayout());

        add(
                createContent(),
                BorderLayout.CENTER);
    }

    private JComponent createContent() {

        JPanel content = new JPanel();

        content.setOpaque(false);

        content.setBorder(
                BorderFactory.createEmptyBorder(
                        BussinTheme.PAGE_PADDING,
                        BussinTheme.PAGE_PADDING,
                        BussinTheme.PAGE_PADDING,
                        BussinTheme.PAGE_PADDING));

        content.setLayout(
                new BoxLayout(
                        content,
                        BoxLayout.Y_AXIS));

        content.add(createHeader());

        content.add(
                Box.createVerticalStrut(
                        BussinTheme.SPACE_XL));

        content.add(createStatistics());

        content.add(
                Box.createVerticalStrut(
                        BussinTheme.SPACE_XL));

        content.add(createMainSection());

        JScrollPane scrollPane = new JScrollPane(content);

        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        scrollPane.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        scrollPane.getVerticalScrollBar()
                .setUnitIncrement(16);

        return scrollPane;
    }

    private JPanel createHeader() {

        JPanel header = new JPanel(
                new BorderLayout());

        header.setOpaque(false);

        JPanel titlePanel = new JPanel();

        titlePanel.setOpaque(false);

        titlePanel.setLayout(
                new BoxLayout(
                        titlePanel,
                        BoxLayout.Y_AXIS));

        titlePanel.add(
                AppLabel.title(
                        "Trip Management"));

        titlePanel.add(
                Box.createVerticalStrut(5));

        titlePanel.add(
                AppLabel.secondary(
                        "Manage scheduled departures, assigned buses, and passenger capacity."));

        AppButton createButton = new AppButton(
                "Create Trip");

        createButton.setIcon(
                IconFactory.create(
                        "plus",
                        16,
                        Color.WHITE));

        createButton.addActionListener(
                event -> showCreateTripDialog());

        JPanel actionPanel = new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT,
                        0,
                        0));

        actionPanel.setOpaque(false);
        actionPanel.add(createButton);

        header.add(
                titlePanel,
                BorderLayout.WEST);

        header.add(
                actionPanel,
                BorderLayout.EAST);

        return header;
    }

    private JPanel createStatistics() {

        JPanel statistics = new JPanel(
                new GridLayout(
                        1,
                        4,
                        14,
                        0));

        statistics.setOpaque(false);

        statistics.add(
                createStatCard(
                        "TOTAL TRIPS",
                        totalValue,
                        BussinTheme.CHARCOAL));

        statistics.add(
                createStatCard(
                        "SCHEDULED",
                        scheduledValue,
                        BussinTheme.INFO));

        statistics.add(
                createStatCard(
                        "BOARDING",
                        boardingValue,
                        BussinTheme.RED));

        statistics.add(
                createStatCard(
                        "COMPLETED",
                        completedValue,
                        BussinTheme.SUCCESS));

        return statistics;
    }

    private AppCard createStatCard(
            String title,
            JLabel value,
            Color accent) {

        AppCard card = new AppCard();

        card.setLayout(
                new BorderLayout(
                        14,
                        0));

        JPanel indicator = new JPanel();

        indicator.setBackground(accent);

        indicator.setPreferredSize(
                new Dimension(
                        4,
                        54));

        JPanel content = new JPanel();

        content.setOpaque(false);

        content.setLayout(
                new BoxLayout(
                        content,
                        BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);

        titleLabel.setFont(
                BussinTheme.SMALL_BOLD);

        titleLabel.setForeground(
                BussinTheme.TEXT_SECONDARY);

        value.setFont(
                BussinTheme.STAT_VALUE);

        value.setForeground(
                BussinTheme.TEXT_PRIMARY);

        content.add(titleLabel);

        content.add(
                Box.createVerticalStrut(5));

        content.add(value);

        card.add(
                indicator,
                BorderLayout.WEST);

        card.add(
                content,
                BorderLayout.CENTER);

        return card;
    }

    private JPanel createMainSection() {

        JPanel main = new JPanel(
                new GridLayout(
                        1,
                        2,
                        18,
                        0));

        main.setOpaque(false);

        main.add(
                createTripListCard());

        main.add(
                createTripDetailsCard());

        return main;
    }

    private AppCard createTripListCard() {

        AppCard card = new AppCard();

        card.setLayout(
                new BorderLayout(
                        0,
                        14));

        JPanel heading = new JPanel(
                new BorderLayout());

        heading.setOpaque(false);

        heading.add(
                AppLabel.section("Trip Schedule"),
                BorderLayout.WEST);

        JLabel hint = new JLabel(
                "Select a trip to manage operations");

        hint.setFont(
                BussinTheme.SMALL);

        hint.setForeground(
                BussinTheme.TEXT_MUTED);

        heading.add(
                hint,
                BorderLayout.EAST);

        card.add(
                heading,
                BorderLayout.NORTH);

        JPanel center = new JPanel(
                new BorderLayout(
                        0,
                        10));

        center.setOpaque(false);

        center.add(
                createFilterBar(),
                BorderLayout.NORTH);

        configureTable();

        JScrollPane tableScroll = new JScrollPane(
                tripTable);

        tableScroll.setBorder(
                BorderFactory.createLineBorder(
                        BussinTheme.BORDER));

        tableScroll.getVerticalScrollBar()
                .setUnitIncrement(12);

        center.add(
                tableScroll,
                BorderLayout.CENTER);

        card.add(
                center,
                BorderLayout.CENTER);

        return card;
    }

    private JPanel createFilterBar() {

        JPanel filters = new JPanel(
                new BorderLayout(
                        10,
                        0));

        filters.setOpaque(false);

        searchField.setPreferredSize(
                new Dimension(
                        220,
                        40));

        searchField.setToolTipText(
                "Search trip ID, route, or bus");

        statusFilter.setPreferredSize(
                new Dimension(
                        140,
                        40));

        filters.add(
                searchField,
                BorderLayout.CENTER);

        filters.add(
                statusFilter,
                BorderLayout.EAST);

        searchField
                .getDocument()
                .addDocumentListener(
                        new DocumentListener() {

                            @Override
                            public void insertUpdate(
                                    DocumentEvent e) {
                                refreshTrips();
                            }

                            @Override
                            public void removeUpdate(
                                    DocumentEvent e) {
                                refreshTrips();
                            }

                            @Override
                            public void changedUpdate(
                                    DocumentEvent e) {
                                refreshTrips();
                            }
                        });

        statusFilter.addActionListener(
                event -> refreshTrips());

        return filters;
    }

    private void configureTable() {

        tripTable.setRowHeight(42);

        tripTable.setFont(
                BussinTheme.SMALL);

        tripTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);

        tripTable.setAutoCreateRowSorter(true);

        tripTable.setShowVerticalLines(false);
        tripTable.setShowHorizontalLines(true);

        tripTable.setGridColor(
                BussinTheme.BORDER);

        tripTable.setSelectionBackground(
                BussinTheme.PRIMARY_LIGHT);

        tripTable.setSelectionForeground(
                BussinTheme.TEXT_PRIMARY);

        tripTable.setFillsViewportHeight(true);

        tripTable.getTableHeader()
                .setFont(
                        BussinTheme.SMALL_BOLD);

        tripTable.getTableHeader()
                .setBackground(
                        BussinTheme.SURFACE_ALT);

        tripTable.getTableHeader()
                .setForeground(
                        BussinTheme.TEXT_SECONDARY);

        for (int i = 0; i < 7; i++) {
            tripTable.getColumnModel()
                    .getColumn(i)
                    .setPreferredWidth(
                            switch (i) {
                                case 0 -> 80;
                                case 1 -> 150;
                                case 2 -> 80;
                                case 3 -> 85;
                                case 4, 5 -> 65;
                                case 6 -> 100;
                                default -> 80;
                            });
        }

        tripTable.getColumnModel()
                .getColumn(6)
                .setCellRenderer(
                        new StatusCellRenderer());

        tripTable.getSelectionModel()
                .addListSelectionListener(
                        event -> {

                            if (!event.getValueIsAdjusting()) {
                                updateSelectedTrip();
                            }
                        });
    }

    private AppCard createTripDetailsCard() {

        AppCard card = new AppCard();

        card.setLayout(
                new BorderLayout(
                        0,
                        18));

        JPanel header = new JPanel(
                new BorderLayout());

        header.setOpaque(false);

        header.add(
                AppLabel.section(
                        "Trip Details"),
                BorderLayout.WEST);

        header.add(
                detailStatus,
                BorderLayout.EAST);

        card.add(
                header,
                BorderLayout.NORTH);

        JPanel details = new JPanel();

        details.setOpaque(false);

        details.setLayout(
                new BoxLayout(
                        details,
                        BoxLayout.Y_AXIS));

        JLabel idTitle = new JLabel("TRIP ID");

        idTitle.setFont(
                BussinTheme.SMALL_BOLD);

        idTitle.setForeground(
                BussinTheme.TEXT_SECONDARY);

        detailTripId.setFont(
                BussinTheme.SECTION_TITLE);

        detailTripId.setForeground(
                BussinTheme.TEXT_PRIMARY);

        details.add(idTitle);

        details.add(
                Box.createVerticalStrut(4));

        details.add(detailTripId);

        details.add(
                Box.createVerticalStrut(14));

        details.add(createSeparator());

        details.add(
                Box.createVerticalStrut(14));

        addDetail(
                details,
                "ROUTE",
                detailRoute);

        addDetail(
                details,
                "ASSIGNED BUS",
                detailBus);

        addDetail(
                details,
                "DEPARTURE",
                detailDeparture);

        addDetail(
                details,
                "CAPACITY",
                detailCapacity);

        addDetail(
                details,
                "BOOKED",
                detailBooked);

        addDetail(
                details,
                "AVAILABLE",
                detailAvailable);

        JLabel occupancyTitle = new JLabel("SEAT OCCUPANCY");

        occupancyTitle.setFont(
                BussinTheme.SMALL_BOLD);

        occupancyTitle.setForeground(
                BussinTheme.TEXT_SECONDARY);

        details.add(occupancyTitle);

        details.add(
                Box.createVerticalStrut(7));

        seatProgress.setMinimum(0);
        seatProgress.setMaximum(100);
        seatProgress.setValue(0);
        seatProgress.setStringPainted(true);
        seatProgress.setFont(
                BussinTheme.SMALL_BOLD);
        seatProgress.setForeground(
                BussinTheme.RED);

        details.add(seatProgress);

        details.add(
                Box.createVerticalStrut(20));

        JPanel actions = new JPanel(
                new FlowLayout(
                        FlowLayout.LEFT,
                        8,
                        0));

        actions.setOpaque(false);

        startBoardingButton.addActionListener(
                event -> startBoarding());

        completeTripButton.addActionListener(
                event -> completeTrip());

        cancelTripButton.addActionListener(
                event -> cancelTrip());

        startBoardingButton.setEnabled(false);
        completeTripButton.setEnabled(false);
        cancelTripButton.setEnabled(false);

        actions.add(startBoardingButton);
        actions.add(completeTripButton);
        actions.add(cancelTripButton);

        details.add(actions);

        JScrollPane scroll = new JScrollPane(details);

        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        scroll.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        card.add(
                scroll,
                BorderLayout.CENTER);

        return card;
    }

    private void addDetail(
            JPanel panel,
            String title,
            JLabel value) {

        JLabel titleLabel = new JLabel(title);

        titleLabel.setFont(
                BussinTheme.SMALL_BOLD);

        titleLabel.setForeground(
                BussinTheme.TEXT_SECONDARY);

        value.setFont(
                BussinTheme.BODY);

        value.setForeground(
                BussinTheme.TEXT_PRIMARY);

        panel.add(titleLabel);

        panel.add(
                Box.createVerticalStrut(3));

        panel.add(value);

        panel.add(
                Box.createVerticalStrut(12));
    }

    private JSeparator createSeparator() {

        JSeparator separator = new JSeparator();

        separator.setForeground(
                BussinTheme.BORDER);

        return separator;
    }

    private void refreshTrips() {

        String query = searchField
                .getText()
                .trim()
                .toLowerCase();

        String selectedStatus = String.valueOf(
                statusFilter.getSelectedItem());

        filteredTrips.clear();

        for (Trip trip : trips) {

            boolean matchesSearch = query.isEmpty()
                    || trip.tripId
                            .toLowerCase()
                            .contains(query)
                    || trip.route
                            .toLowerCase()
                            .contains(query)
                    || trip.bus
                            .toLowerCase()
                            .contains(query);

            boolean matchesStatus = selectedStatus.equals("All Status")
                    || trip.status.equals(
                            selectedStatus);

            if (matchesSearch && matchesStatus) {
                filteredTrips.add(trip);
            }
        }

        tableModel.fireTableDataChanged();

        updateStatistics();

        if (filteredTrips.isEmpty()) {
            clearDetails();
        } else {
            tripTable.setRowSelectionInterval(0, 0);
        }
    }

    private void updateStatistics() {

        int total = trips.size();
        int scheduled = 0;
        int boarding = 0;
        int completed = 0;

        for (Trip trip : trips) {

            switch (trip.status) {

                case "Scheduled" -> scheduled++;

                case "Boarding" -> boarding++;

                case "Completed" -> completed++;
            }
        }

        totalValue.setText(
                String.valueOf(total));

        scheduledValue.setText(
                String.valueOf(scheduled));

        boardingValue.setText(
                String.valueOf(boarding));

        completedValue.setText(
                String.valueOf(completed));
    }

    private void updateSelectedTrip() {

        int selectedRow = tripTable.getSelectedRow();

        if (selectedRow < 0) {
            clearDetails();
            return;
        }

        int modelRow = tripTable.convertRowIndexToModel(
                selectedRow);

        if (modelRow < 0
                || modelRow >= filteredTrips.size()) {

            clearDetails();
            return;
        }

        Trip trip = filteredTrips.get(modelRow);

        detailTripId.setText(
                trip.tripId);

        detailRoute.setText(
                trip.route);

        detailBus.setText(
                trip.bus);

        detailDeparture.setText(
                trip.departure);

        detailCapacity.setText(
                String.valueOf(
                        trip.capacity));

        detailBooked.setText(
                String.valueOf(
                        trip.booked));

        detailAvailable.setText(
                String.valueOf(
                        trip.capacity
                                - trip.booked));

        int occupancy = trip.capacity == 0
                ? 0
                : (int) (trip.booked
                        * 100.0
                        / trip.capacity);

        seatProgress.setValue(
                occupancy);

        seatProgress.setString(
                occupancy + "% occupied");

        updateStatusBadge(
                trip.status);

        startBoardingButton.setEnabled(
                trip.status.equals("Scheduled"));

        completeTripButton.setEnabled(
                trip.status.equals("Boarding")
                        || trip.status.equals("On Route"));

        cancelTripButton.setEnabled(
                !trip.status.equals("Completed")
                        && !trip.status.equals("Cancelled"));
    }

    private void clearDetails() {

        detailTripId.setText("-");
        detailRoute.setText("-");
        detailBus.setText("-");
        detailDeparture.setText("-");
        detailCapacity.setText("-");
        detailBooked.setText("-");
        detailAvailable.setText("-");

        seatProgress.setValue(0);
        seatProgress.setString("0% occupied");

        updateStatusBadge("No Status");

        startBoardingButton.setEnabled(false);
        completeTripButton.setEnabled(false);
        cancelTripButton.setEnabled(false);
    }

    private void updateStatusBadge(
            String status) {

        AppBadge.Status badgeStatus;

        switch (status) {

            case "Scheduled" ->
                badgeStatus = AppBadge.Status.NEUTRAL;

            case "Boarding", "On Route" ->
                badgeStatus = AppBadge.Status.INFO;

            case "Completed" ->
                badgeStatus = AppBadge.Status.SUCCESS;

            case "Cancelled" ->
                badgeStatus = AppBadge.Status.DANGER;

            default ->
                badgeStatus = AppBadge.Status.NEUTRAL;
        }

        detailStatus.setText(
                status.toUpperCase());

        switch (badgeStatus) {

            case SUCCESS -> {
                detailStatus.setForeground(
                        BussinTheme.SUCCESS);
                detailStatus.setBackground(
                        BussinTheme.SUCCESS_LIGHT);
            }

            case INFO -> {
                detailStatus.setForeground(
                        BussinTheme.TEXT_PRIMARY);
                detailStatus.setBackground(
                        BussinTheme.INFO_LIGHT);
            }

            case DANGER -> {
                detailStatus.setForeground(
                        BussinTheme.DANGER);
                detailStatus.setBackground(
                        BussinTheme.DANGER_LIGHT);
            }

            case WARNING -> {
                detailStatus.setForeground(
                        BussinTheme.WARNING);
                detailStatus.setBackground(
                        BussinTheme.WARNING_LIGHT);
            }

            default -> {
                detailStatus.setForeground(
                        BussinTheme.TEXT_SECONDARY);
                detailStatus.setBackground(
                        BussinTheme.SURFACE_ALT);
            }
        }
    }

    private Trip getSelectedTrip() {

        int selectedRow = tripTable.getSelectedRow();

        if (selectedRow < 0) {
            return null;
        }

        int modelRow = tripTable.convertRowIndexToModel(
                selectedRow);

        if (modelRow < 0
                || modelRow >= filteredTrips.size()) {

            return null;
        }

        return filteredTrips.get(modelRow);
    }

    private void startBoarding() {

        Trip trip = getSelectedTrip();

        if (trip == null
                || !trip.status.equals("Scheduled")) {
            return;
        }

        trip.status = "Boarding";

        refreshTrips();
    }

    private void completeTrip() {

        Trip trip = getSelectedTrip();

        if (trip == null) {
            return;
        }

        if (!trip.status.equals("Boarding")
                && !trip.status.equals("On Route")) {
            return;
        }

        trip.status = "Completed";

        refreshTrips();
    }

    private void cancelTrip() {

        Trip trip = getSelectedTrip();

        if (trip == null) {
            return;
        }

        if (trip.status.equals("Completed")
                || trip.status.equals("Cancelled")) {
            return;
        }

        int result = JOptionPane.showConfirmDialog(
                this,
                "Cancel "
                        + trip.tripId
                        + " for "
                        + trip.route
                        + "?",
                "Cancel Trip",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {

            trip.status = "Cancelled";

            refreshTrips();
        }
    }

    private void showCreateTripDialog() {

        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Create Trip",
                Dialog.ModalityType.APPLICATION_MODAL);

        dialog.setDefaultCloseOperation(
                JDialog.DISPOSE_ON_CLOSE);

        dialog.setSize(
                520,
                430);

        dialog.setMinimumSize(
                new Dimension(
                        520,
                        430));

        dialog.setLocationRelativeTo(this);

        JPanel root = new JPanel(
                new BorderLayout());

        root.setBackground(
                BussinTheme.BACKGROUND);

        JLabel title = new JLabel(
                "Create New Trip");

        title.setFont(
                BussinTheme.SECTION_TITLE);

        title.setForeground(
                BussinTheme.TEXT_PRIMARY);

        JPanel titlePanel = new JPanel(
                new BorderLayout());

        titlePanel.setOpaque(false);

        titlePanel.setBorder(
                BorderFactory.createEmptyBorder(
                        22,
                        24,
                        10,
                        24));

        titlePanel.add(
                title,
                BorderLayout.WEST);

        JPanel form = new JPanel(
                new GridLayout(
                        6,
                        2,
                        12,
                        10));

        form.setOpaque(false);

        form.setBorder(
                BorderFactory.createEmptyBorder(
                        12,
                        24,
                        12,
                        24));

        JTextField routeField = new JTextField();

        JTextField busField = new JTextField();

        JTextField departureField = new JTextField();

        JTextField capacityField = new JTextField();

        JTextField bookedField = new JTextField("0");

        addFormField(
                form,
                "Route",
                routeField);

        addFormField(
                form,
                "Bus",
                busField);

        addFormField(
                form,
                "Departure",
                departureField);

        addFormField(
                form,
                "Capacity",
                capacityField);

        addFormField(
                form,
                "Booked",
                bookedField);

        JLabel note = new JLabel(
                "New trips start with Scheduled status.");

        note.setFont(
                BussinTheme.SMALL);

        note.setForeground(
                BussinTheme.TEXT_MUTED);

        form.add(note);
        form.add(new JLabel());

        JPanel buttons = new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT,
                        8,
                        12));

        buttons.setOpaque(false);

        AppButton closeButton = new AppButton(
                "Cancel",
                AppButton.Variant.SECONDARY);

        AppButton saveButton = new AppButton(
                "Create Trip");

        closeButton.addActionListener(
                event -> dialog.dispose());

        saveButton.addActionListener(
                event -> {

                    String route = routeField.getText().trim();

                    String bus = busField.getText().trim();

                    String departure = departureField
                            .getText()
                            .trim();

                    String capacityText = capacityField
                            .getText()
                            .trim();

                    String bookedText = bookedField
                            .getText()
                            .trim();

                    if (route.isEmpty()
                            || bus.isEmpty()
                            || departure.isEmpty()
                            || capacityText.isEmpty()) {

                        JOptionPane.showMessageDialog(
                                dialog,
                                "Please complete all required fields.",
                                "Invalid Trip",
                                JOptionPane.WARNING_MESSAGE);

                        return;
                    }

                    try {

                        int capacity = Integer.parseInt(
                                capacityText);

                        int booked = bookedText.isEmpty()
                                ? 0
                                : Integer.parseInt(
                                        bookedText);

                        if (capacity <= 0
                                || booked < 0
                                || booked > capacity) {

                            throw new NumberFormatException();
                        }

                        trips.add(
                                new Trip(
                                        generateTripId(),
                                        route,
                                        bus,
                                        departure,
                                        capacity,
                                        booked,
                                        "Scheduled"));

                        refreshTrips();

                        dialog.dispose();

                    } catch (NumberFormatException exception) {

                        JOptionPane.showMessageDialog(
                                dialog,
                                "Capacity and booked seats must be valid numbers, and booked seats cannot exceed capacity.",
                                "Invalid Capacity",
                                JOptionPane.WARNING_MESSAGE);
                    }
                });

        buttons.add(closeButton);
        buttons.add(saveButton);

        root.add(
                titlePanel,
                BorderLayout.NORTH);

        root.add(
                form,
                BorderLayout.CENTER);

        root.add(
                buttons,
                BorderLayout.SOUTH);

        dialog.setContentPane(root);
        dialog.setVisible(true);
    }

    private void addFormField(
            JPanel panel,
            String labelText,
            JComponent field) {

        JLabel label = new JLabel(labelText);

        label.setFont(
                BussinTheme.SMALL_BOLD);

        label.setForeground(
                BussinTheme.TEXT_SECONDARY);

        panel.add(label);

        field.setPreferredSize(
                new Dimension(
                        0,
                        36));

        panel.add(field);
    }

    private String generateTripId() {

        int highest = 0;

        for (Trip trip : trips) {

            try {

                int number = Integer.parseInt(
                        trip.tripId.substring(3));

                highest = Math.max(
                        highest,
                        number);

            } catch (NumberFormatException ignored) {
                // Ignore unexpected mock IDs.
            }
        }

        return String.format(
                "TR-%03d",
                highest + 1);
    }

    private static class Trip {

        private final String tripId;
        private final String route;
        private final String bus;
        private final String departure;
        private final int capacity;
        private final int booked;

        private String status;

        private Trip(
                String tripId,
                String route,
                String bus,
                String departure,
                int capacity,
                int booked,
                String status) {

            this.tripId = tripId;
            this.route = route;
            this.bus = bus;
            this.departure = departure;
            this.capacity = capacity;
            this.booked = booked;
            this.status = status;
        }
    }

    private class TripTableModel
            extends AbstractTableModel {

        private final String[] columns = {
                "Trip ID",
                "Route",
                "Bus",
                "Departure",
                "Capacity",
                "Booked",
                "Status"
        };

        @Override
        public int getRowCount() {
            return filteredTrips.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(
                int column) {
            return columns[column];
        }

        @Override
        public Object getValueAt(
                int row,
                int column) {

            Trip trip = filteredTrips.get(row);

            return switch (column) {

                case 0 -> trip.tripId;
                case 1 -> trip.route;
                case 2 -> trip.bus;
                case 3 -> trip.departure;
                case 4 -> trip.capacity;
                case 5 -> trip.booked;
                case 6 -> trip.status;

                default -> "";
            };
        }

        @Override
        public boolean isCellEditable(
                int row,
                int column) {
            return false;
        }
    }

    private static class StatusCellRenderer
            extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean selected,
                boolean focus,
                int row,
                int column) {

            String status = String.valueOf(value);

            AppBadge.Status badgeStatus;

            switch (status) {

                case "Boarding", "On Route" ->
                    badgeStatus = AppBadge.Status.INFO;

                case "Completed" ->
                    badgeStatus = AppBadge.Status.SUCCESS;

                case "Cancelled" ->
                    badgeStatus = AppBadge.Status.DANGER;

                case "Scheduled" ->
                    badgeStatus = AppBadge.Status.NEUTRAL;

                default ->
                    badgeStatus = AppBadge.Status.NEUTRAL;
            }

            AppBadge badge = new AppBadge(
                    status,
                    badgeStatus);

            JPanel wrapper = new JPanel(
                    new FlowLayout(
                            FlowLayout.LEFT,
                            0,
                            7));

            wrapper.setBackground(
                    selected
                            ? table.getSelectionBackground()
                            : table.getBackground());

            wrapper.add(badge);

            return wrapper;
        }
    }
}