package com.bussin.desktop.ui.screens;

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
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.Scrollable;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.bussin.desktop.services.BusApiService;
import com.bussin.desktop.services.BusApiService.BusResponse;
import com.bussin.desktop.services.RouteApiService;
import com.bussin.desktop.services.RouteApiService.RouteResponse;
import com.bussin.desktop.services.TripApiService;
import com.bussin.desktop.services.TripApiService.TripResponse;
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

public class TripScreen extends JPanel {

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

    private static final String API_SCHEDULED = "SCHEDULED";
    private static final String API_BOARDING = "BOARDING";
    private static final String API_DEPARTED = "DEPARTED";
    private static final String API_COMPLETED = "COMPLETED";
    private static final String API_CANCELLED = "CANCELLED";

    private static final int TABLE_MIN_WIDTH = 900;
    private static final int SCROLLBAR_ALLOWANCE = 14;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern(
            "MMM dd, yyyy",
            Locale.ENGLISH);

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern(
            "hh:mm a",
            Locale.ENGLISH);

    private final List<TripResponse> trips = new ArrayList<>();

    private final List<RouteResponse> routes = new ArrayList<>();

    private final List<BusResponse> buses = new ArrayList<>();

    private final List<TripResponse> filteredTrips = new ArrayList<>();

    private final JTextField searchField = new JTextField();

    private final JComboBox<String> statusFilter = new JComboBox<>(
            new String[] {
                    ALL_STATUS,
                    SCHEDULED,
                    BOARDING,
                    IN_PROGRESS,
                    COMPLETED,
                    CANCELLED
            });

    private final JComboBox<String> dateFilter = new JComboBox<>(
            new String[] {
                    ALL_DATES,
                    DATE_TODAY,
                    DATE_TOMORROW,
                    DATE_PAST
            });

    private final JPanel statsHolder = transparent();

    private final JPanel listHolder = transparent();

    public TripScreen() {

        setBackground(
                BussinTheme.BACKGROUND);

        setLayout(
                new BorderLayout());

        PageContent page = new PageContent();

        page.addBlock(
                createHeader(),
                0);

        page.addBlock(
                statsHolder,
                24);

        page.addBlock(
                createFilterBar(),
                18);

        page.addBlock(
                listHolder,
                18);

        add(
                page.inScrollPane(),
                BorderLayout.CENTER);

        renderLoadingState();

        loadData();
    }

    private JPanel createHeader() {

        JPanel header = new JPanel(
                new BorderLayout(
                        24,
                        0));

        header.setOpaque(false);

        JPanel text = new JPanel();

        text.setOpaque(false);

        text.setLayout(
                new BoxLayout(
                        text,
                        BoxLayout.Y_AXIS));

        text.add(
                AppLabel.title(
                        "Trip Management"));

        text.add(
                Box.createVerticalStrut(4));

        text.add(
                AppLabel.secondary(
                        "Manage scheduled trips, assigned buses, routes, and trip status."));

        AppButton refreshButton = new AppButton(
                "Refresh",
                AppButton.Variant.SECONDARY);

        refreshButton.setIcon(
                IconFactory.create(
                        "arrow-right",
                        16,
                        BussinTheme.TEXT_PRIMARY));

        refreshButton.addActionListener(
                e -> loadData());

        AppButton addButton = new AppButton(
                "Add Trip");

        addButton.setIcon(
                IconFactory.create(
                        "plus",
                        16,
                        java.awt.Color.WHITE));

        addButton.addActionListener(
                e -> {

                    if (!hasActiveRoute()
                            || getAssignableBuses(null).isEmpty()) {

                        JOptionPane.showMessageDialog(
                                this,
                                "An active route and active bus are required before creating a trip.",
                                "Unable to Add Trip",
                                JOptionPane.WARNING_MESSAGE);

                        return;
                    }

                    showTripForm(null);
                });

        JPanel actions = new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT,
                        10,
                        0));

        actions.setOpaque(false);

        actions.add(
                refreshButton);

        actions.add(
                addButton);

        JPanel actionHolder = new JPanel(
                new BorderLayout());

        actionHolder.setOpaque(false);

        actionHolder.add(
                actions,
                BorderLayout.SOUTH);

        header.add(
                text,
                BorderLayout.CENTER);

        header.add(
                actionHolder,
                BorderLayout.EAST);

        return header;
    }

    private boolean hasActiveRoute() {

        for (RouteResponse route : routes) {

            if (route.isActive()) {
                return true;
            }
        }

        return false;
    }

    private JPanel createFilterBar() {

        AppCard card = new AppCard();

        card.setLayout(
                new ResponsiveLayouts.Grid(
                        4,
                        180,
                        12));

        searchField.setToolTipText(
                "Search by trip ID, route, or bus");

        searchField.putClientProperty(
                "JTextField.placeholderText",
                "Search trip, route, bus");

        searchField.getDocument()
                .addDocumentListener(
                        new DocumentListener() {

                            @Override
                            public void insertUpdate(
                                    DocumentEvent e) {
                                refresh();
                            }

                            @Override
                            public void removeUpdate(
                                    DocumentEvent e) {
                                refresh();
                            }

                            @Override
                            public void changedUpdate(
                                    DocumentEvent e) {
                                refresh();
                            }
                        });

        statusFilter.addActionListener(
                e -> refresh());

        dateFilter.addActionListener(
                e -> refresh());

        AppButton clear = new AppButton(
                "Clear Filters",
                AppButton.Variant.SECONDARY);

        clear.addActionListener(
                e -> clearFilters());

        card.add(
                searchField);

        card.add(
                statusFilter);

        card.add(
                dateFilter);

        card.add(
                clear);

        return card;
    }

    private void clearFilters() {

        searchField.setText("");

        statusFilter.setSelectedIndex(0);

        dateFilter.setSelectedIndex(0);
    }

    private void loadData() {

        renderLoadingState();

        new SwingWorker<DataSet, Void>() {

            @Override
            protected DataSet doInBackground()
                    throws Exception {

                List<RouteResponse> loadedRoutes = RouteApiService.getAllRoutes();

                List<BusResponse> loadedBuses = BusApiService.getAllBuses();

                List<TripResponse> loadedTrips = TripApiService.getAllTrips();

                return new DataSet(
                        loadedRoutes,
                        loadedBuses,
                        loadedTrips);
            }

            @Override
            protected void done() {

                try {

                    DataSet data = get();

                    routes.clear();

                    routes.addAll(
                            data.routes());

                    buses.clear();

                    buses.addAll(
                            data.buses());

                    trips.clear();

                    trips.addAll(
                            data.trips());

                    refresh();

                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();

                    showLoadError(
                            "Loading trips was interrupted.");

                } catch (ExecutionException e) {

                    showLoadError(
                            getErrorMessage(e));
                }
            }
        }.execute();
    }

    private void refresh() {

        filteredTrips.clear();

        for (TripResponse trip : trips) {

            if (matches(trip)) {
                filteredTrips.add(trip);
            }
        }

        filteredTrips.sort(
                Comparator.comparing(
                        TripResponse::getScheduledDeparture));

        renderStats();

        renderList();

        revalidate();

        repaint();
    }

    private boolean matches(
            TripResponse trip) {

        String query = searchField
                .getText()
                .trim()
                .toLowerCase(
                        Locale.ROOT);

        String tripId = tripId(trip)
                .toLowerCase(
                        Locale.ROOT);

        String route = safe(
                trip.getRouteIdentifier())
                .toLowerCase(
                        Locale.ROOT);

        String bus = safe(
                trip.getBusPlateNumber())
                .toLowerCase(
                        Locale.ROOT);

        boolean matchesSearch = query.isEmpty()
                || tripId.contains(query)
                || route.contains(query)
                || bus.contains(query);

        String selectedStatus = String.valueOf(
                statusFilter
                        .getSelectedItem());

        boolean matchesStatus = selectedStatus.equals(
                ALL_STATUS)
                || displayStatus(
                        trip.getStatus())
                        .equals(
                                selectedStatus);

        LocalDate today = LocalDate.now();

        LocalDate day = trip.getScheduledDeparture()
                .toLocalDate();

        boolean matchesDate = switch (String.valueOf(
                dateFilter.getSelectedItem())) {

            case DATE_TODAY ->
                day.equals(today);

            case DATE_TOMORROW ->
                day.equals(
                        today.plusDays(1));

            case DATE_PAST ->
                day.isBefore(today);

            default ->
                true;
        };

        return matchesSearch
                && matchesStatus
                && matchesDate;
    }

    private void renderStats() {

        LocalDate today = LocalDate.now();

        int todayCount = 0;
        int scheduled = 0;
        int boarding = 0;
        int inProgress = 0;
        int completed = 0;

        for (TripResponse trip : filteredTrips) {

            if (trip.getScheduledDeparture()
                    .toLocalDate()
                    .equals(today)) {

                todayCount++;
            }

            String status = normalizeStatus(
                    trip.getStatus());

            if (status.equals(
                    API_SCHEDULED)) {

                scheduled++;

            } else if (status.equals(
                    API_BOARDING)) {

                boarding++;

            } else if (status.equals(
                    API_DEPARTED)) {

                inProgress++;

            } else if (status.equals(
                    API_COMPLETED)) {

                completed++;
            }
        }

        statsHolder.removeAll();

        statsHolder.setLayout(
                new ResponsiveLayouts.Grid(
                        4,
                        200,
                        14));

        statsHolder.add(
                new StatCard(
                        "TODAY'S TRIPS",
                        String.valueOf(
                                todayCount),
                        "departing today",
                        "trip"));

        statsHolder.add(
                new StatCard(
                        "SCHEDULED",
                        String.valueOf(
                                scheduled),
                        "awaiting boarding",
                        "queue"));

        statsHolder.add(
                new StatCard(
                        "IN PROGRESS",
                        String.valueOf(
                                inProgress),
                        boarding
                                + " boarding now",
                        "bus"));

        statsHolder.add(
                new StatCard(
                        "COMPLETED",
                        String.valueOf(
                                completed),
                        "finished trips",
                        "seat"));
    }

    private void renderList() {

        listHolder.removeAll();

        listHolder.setLayout(
                new BorderLayout());

        AppCard card = new AppCard();

        card.setLayout(
                new BorderLayout(
                        0,
                        10));

        card.add(
                new SectionHeader(
                        "Trip Schedule",
                        filteredTrips.size()
                                + " of "
                                + trips.size()
                                + " trips shown"),
                BorderLayout.NORTH);

        if (filteredTrips.isEmpty()) {

            card.add(
                    emptyState(
                            trips.isEmpty()
                                    ? "No trips have been scheduled yet."
                                    : "No trips match the current filters."),
                    BorderLayout.CENTER);

        } else {

            DataGrid grid = new DataGrid(
                    new String[] {
                            "Trip",
                            "Route",
                            "Departure",
                            "Arrival",
                            "Bus",
                            "Status",
                            "Actions"
                    },
                    new double[] {
                            0.8,
                            1.7,
                            1.2,
                            1.2,
                            1.0,
                            1.1,
                            2.8
                    });

            for (TripResponse trip : filteredTrips) {

                grid.addRow(
                        DataGrid.strong(
                                tripId(trip)),

                        DataGrid.text(
                                safe(
                                        trip.getRouteIdentifier())),

                        twoLine(
                                trip.getScheduledDeparture()
                                        .format(
                                                TIME_FORMAT),
                                trip.getScheduledDeparture()
                                        .format(
                                                DATE_FORMAT)),

                        twoLine(
                                trip.getScheduledArrival()
                                        .format(
                                                TIME_FORMAT),
                                arrivalNote(trip)),

                        DataGrid.text(
                                safe(
                                        trip.getBusPlateNumber())),

                        new AppBadge(
                                displayStatus(
                                        trip.getStatus()),
                                badgeFor(
                                        trip.getStatus())),

                        createActionCell(trip));
            }

            card.add(
                    new HorizontalScrollHolder(
                            grid),
                    BorderLayout.CENTER);
        }

        listHolder.add(
                card,
                BorderLayout.CENTER);
    }

    private JPanel createActionCell(
            TripResponse trip) {

        JPanel actions = new JPanel(
                new FlowLayout(
                        FlowLayout.LEFT,
                        6,
                        0));

        actions.setOpaque(false);

        AppButton view = small(
                "View",
                AppButton.Variant.SECONDARY);

        view.addActionListener(
                e -> showDetails(trip));

        actions.add(view);

        if (canEdit(trip)) {

            AppButton edit = small(
                    "Edit",
                    AppButton.Variant.SECONDARY);

            edit.addActionListener(
                    e -> showTripForm(trip));

            actions.add(edit);
        }

        String next = nextStepShort(trip);

        if (next != null) {

            AppButton step = small(
                    next,
                    AppButton.Variant.PRIMARY);

            step.setToolTipText(
                    nextStepLabel(trip));

            step.addActionListener(
                    e -> advance(trip));

            actions.add(step);
        }

        if (canCancel(trip)) {

            AppButton cancel = small(
                    "Cancel",
                    AppButton.Variant.GHOST);

            cancel.addActionListener(
                    e -> cancelTrip(
                            trip,
                            true));

            actions.add(cancel);
        }

        if (canDelete(trip)) {

            AppButton delete = small(
                    "Delete",
                    AppButton.Variant.DANGER);

            delete.addActionListener(
                    e -> deleteTrip(trip));

            actions.add(delete);
        }

        return actions;
    }

    private String nextStepLabel(
            TripResponse trip) {

        String status = normalizeStatus(
                trip.getStatus());

        if (status.equals(
                API_SCHEDULED)) {

            return canStartBoarding(trip)
                    ? "Start Boarding"
                    : null;
        }

        if (status.equals(
                API_BOARDING)) {

            return "Start Trip";
        }

        if (status.equals(
                API_DEPARTED)) {

            return "Complete Trip";
        }

        return null;
    }

    private String nextStepShort(
            TripResponse trip) {

        String status = normalizeStatus(
                trip.getStatus());

        if (status.equals(
                API_SCHEDULED)) {

            return canStartBoarding(trip)
                    ? "Board"
                    : null;
        }

        if (status.equals(
                API_BOARDING)) {

            return "Depart";
        }

        if (status.equals(
                API_DEPARTED)) {

            return "Complete";
        }

        return null;
    }

    boolean advance(
            TripResponse trip) {

        String current = normalizeStatus(
                trip.getStatus());

        String next;

        if (current.equals(
                API_SCHEDULED)) {

            if (!canStartBoarding(trip)) {
                return false;
            }

            next = API_BOARDING;

        } else if (current.equals(
                API_BOARDING)) {

            next = API_DEPARTED;

        } else if (current.equals(
                API_DEPARTED)) {

            next = API_COMPLETED;

        } else {

            return false;
        }

        updateStatus(
                trip,
                next);

        return true;
    }

    boolean cancelTrip(
            TripResponse trip,
            boolean confirm) {

        if (!canCancel(trip)) {
            return false;
        }

        if (confirm) {

            int result = JOptionPane.showConfirmDialog(
                    this,
                    "Cancel "
                            + tripId(trip)
                            + " ("
                            + safe(
                                    trip.getRouteIdentifier())
                            + ", "
                            + trip.getScheduledDeparture()
                                    .format(
                                            TIME_FORMAT)
                            + ")?",
                    "Cancel Trip",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (result != JOptionPane.YES_OPTION) {

                return false;
            }
        }

        updateStatus(
                trip,
                API_CANCELLED);

        return true;
    }

    private void updateStatus(
            TripResponse trip,
            String status) {

        new SwingWorker<TripResponse, Void>() {

            @Override
            protected TripResponse doInBackground()
                    throws Exception {

                return TripApiService.updateTrip(
                        trip.getId(),
                        trip.getBusId(),
                        trip.getRouteId(),
                        trip.getScheduledDeparture(),
                        trip.getScheduledArrival(),
                        status);
            }

            @Override
            protected void done() {

                try {

                    TripResponse updated = get();

                    replaceTrip(
                            updated);

                    refresh();

                } catch (InterruptedException e) {

                    Thread.currentThread()
                            .interrupt();

                    showError(
                            "Trip update was interrupted.");

                } catch (ExecutionException e) {

                    showError(
                            getErrorMessage(e));
                }
            }
        }.execute();
    }

    private void deleteTrip(
            TripResponse trip) {

        int result = JOptionPane.showConfirmDialog(
                this,
                "Delete "
                        + tripId(trip)
                        + " permanently?",
                "Delete Trip",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (result != JOptionPane.YES_OPTION) {

            return;
        }

        new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground()
                    throws Exception {

                TripApiService.deleteTrip(
                        trip.getId());

                return null;
            }

            @Override
            protected void done() {

                try {

                    get();

                    trips.removeIf(
                            existing -> existing.getId()
                                    .equals(
                                            trip.getId()));

                    refresh();

                } catch (InterruptedException e) {

                    Thread.currentThread()
                            .interrupt();

                    showError(
                            "Trip deletion was interrupted.");

                } catch (ExecutionException e) {

                    showError(
                            getErrorMessage(e));
                }
            }
        }.execute();
    }

    private void showDetails(
            TripResponse trip) {

        JDialog dialog = createDialog(
                "Trip Details");

        JPanel root = new JPanel(
                new BorderLayout(
                        0,
                        14));

        root.setBackground(
                BussinTheme.BACKGROUND);

        root.setBorder(
                BorderFactory.createEmptyBorder(
                        22,
                        24,
                        18,
                        24));

        JPanel titleText = new JPanel();

        titleText.setOpaque(false);

        titleText.setLayout(
                new BoxLayout(
                        titleText,
                        BoxLayout.Y_AXIS));

        JLabel id = new JLabel(
                tripId(trip));

        id.setFont(
                BussinTheme.SECTION_TITLE);

        id.setForeground(
                BussinTheme.TEXT_PRIMARY);

        titleText.add(id);

        JLabel route = new JLabel(
                safe(
                        trip.getRouteIdentifier()));

        route.setFont(
                BussinTheme.BODY);

        route.setForeground(
                BussinTheme.TEXT_SECONDARY);

        titleText.add(route);

        JPanel badgeHolder = new JPanel(
                new BorderLayout());

        badgeHolder.setOpaque(false);

        badgeHolder.add(
                new AppBadge(
                        displayStatus(
                                trip.getStatus()),
                        badgeFor(
                                trip.getStatus())),
                BorderLayout.NORTH);

        JPanel title = new JPanel(
                new BorderLayout(
                        12,
                        0));

        title.setOpaque(false);

        title.add(
                titleText,
                BorderLayout.CENTER);

        title.add(
                badgeHolder,
                BorderLayout.EAST);

        Duration travel = Duration.between(
                trip.getScheduledDeparture(),
                trip.getScheduledArrival());

        AppCard facts = new AppCard();

        facts.setLayout(
                new GridBagLayout());

        int row = 0;

        row = addFact(
                facts,
                row,
                "DATE",
                trip.getScheduledDeparture()
                        .format(
                                DATE_FORMAT),
                "DURATION",
                formatDuration(
                        travel));

        row = addFact(
                facts,
                row,
                "DEPARTURE",
                trip.getScheduledDeparture()
                        .format(
                                TIME_FORMAT),
                "EST. ARRIVAL",
                trip.getScheduledArrival()
                        .format(
                                TIME_FORMAT)
                        + (arrivalNote(trip)
                                .contains("+1")
                                        ? "  (+1 day)"
                                        : ""));

        row = addFact(
                facts,
                row,
                "BUS",
                safe(
                        trip.getBusPlateNumber()),
                "ROUTE",
                safe(
                        trip.getRouteIdentifier()));

        addFact(
                facts,
                row,
                "TRIP ID",
                tripId(trip),
                "STATUS",
                displayStatus(
                        trip.getStatus()));

        JPanel body = new JPanel(
                new BorderLayout(
                        0,
                        10));

        body.setOpaque(false);

        body.add(
                facts,
                BorderLayout.CENTER);

        JLabel note = new JLabel(
                "<html>Passenger bookings and queue information "
                        + "are managed separately and are not part of Trip Management.</html>");

        note.setFont(
                BussinTheme.SMALL);

        note.setForeground(
                BussinTheme.TEXT_MUTED);

        body.add(
                note,
                BorderLayout.SOUTH);

        JPanel buttons = new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT,
                        8,
                        0));

        buttons.setOpaque(false);

        AppButton close = new AppButton(
                "Close",
                AppButton.Variant.SECONDARY);

        close.addActionListener(
                e -> dialog.dispose());

        buttons.add(close);

        if (canEdit(trip)) {

            AppButton edit = new AppButton(
                    "Edit",
                    AppButton.Variant.SECONDARY);

            edit.addActionListener(
                    e -> {

                        dialog.dispose();

                        showTripForm(trip);
                    });

            buttons.add(edit);
        }

        if (canCancel(trip)) {

            AppButton cancel = new AppButton(
                    "Cancel Trip",
                    AppButton.Variant.DANGER);

            cancel.addActionListener(
                    e -> {

                        if (cancelTrip(
                                trip,
                                true)) {

                            dialog.dispose();
                        }
                    });

            buttons.add(cancel);
        }

        String next = nextStepLabel(trip);

        if (next != null) {

            AppButton step = new AppButton(next);

            step.addActionListener(
                    e -> {

                        advance(trip);

                        dialog.dispose();
                    });

            buttons.add(step);
        }

        root.add(
                title,
                BorderLayout.NORTH);

        root.add(
                body,
                BorderLayout.CENTER);

        root.add(
                buttons,
                BorderLayout.SOUTH);

        showDialog(
                dialog,
                root,
                520);
    }

    private void showTripForm(
            TripResponse editing) {

        boolean isNew = editing == null;

        JDialog dialog = createDialog(
                isNew
                        ? "Add Trip"
                        : "Edit Trip");

        List<RouteResponse> availableRoutes = new ArrayList<>();

        for (RouteResponse route : routes) {

            if (route.isActive()
                    || (!isNew
                            && route.getId()
                                    .equals(
                                            editing.getRouteId()))) {

                availableRoutes.add(route);
            }
        }

        List<BusResponse> availableBuses = getAssignableBuses(
                isNew
                        ? null
                        : editing.getBusId());

        JComboBox<RouteResponse> routeBox = new JComboBox<>(
                availableRoutes.toArray(
                        new RouteResponse[0]));

        JComboBox<BusResponse> busBox = new JComboBox<>(
                availableBuses.toArray(
                        new BusResponse[0]));

        configureRouteRenderer(
                routeBox);

        configureBusRenderer(
                busBox);

        JComboBox<String> statusBox = new JComboBox<>(
                new String[] {
                        SCHEDULED,
                        BOARDING,
                        IN_PROGRESS,
                        COMPLETED,
                        CANCELLED
                });

        LocalDate startDate = isNew
                ? LocalDate.now()
                : editing
                        .getScheduledDeparture()
                        .toLocalDate();

        LocalTime startTime = isNew
                ? LocalTime.of(
                        8,
                        0)
                : editing
                        .getScheduledDeparture()
                        .toLocalTime();

        JSpinner dateSpinner = dateSpinner(
                startDate);

        JSpinner departSpinner = timeSpinner(
                startTime);

        LocalTime initialArrival = isNew
                ? startTime.plusMinutes(
                        selectedRouteDuration(
                                availableRoutes))
                : editing
                        .getScheduledArrival()
                        .toLocalTime();

        JSpinner arriveSpinner = timeSpinner(
                initialArrival);

        JLabel arrivalHint = new JLabel(" ");

        arrivalHint.setFont(
                BussinTheme.SMALL);

        arrivalHint.setForeground(
                BussinTheme.TEXT_MUTED);

        JLabel errorLabel = new JLabel(" ");

        errorLabel.setFont(
                BussinTheme.SMALL_BOLD);

        errorLabel.setForeground(
                BussinTheme.DANGER);

        if (!isNew) {

            selectRoute(
                    routeBox,
                    editing.getRouteId());

            selectBus(
                    busBox,
                    editing.getBusId());

            statusBox.setSelectedItem(
                    displayStatus(
                            editing.getStatus()));
        }

        Runnable estimateArrival = () -> {

            RouteResponse route = (RouteResponse) routeBox
                    .getSelectedItem();

            if (route == null) {
                return;
            }

            LocalTime departure = timeOf(
                    departSpinner);

            int duration = route.getDurationMinutes() == null
                    ? 0
                    : route
                            .getDurationMinutes();

            arriveSpinner.setValue(
                    toDate(
                            departure.plusMinutes(
                                    duration)));

            arrivalHint.setText(
                    "Estimated from route travel time ("
                            + formatDuration(
                                    Duration.ofMinutes(
                                            duration))
                            + "). Adjust if needed.");
        };

        if (isNew) {
            estimateArrival.run();
        } else {
            arrivalHint.setText(
                    "Adjust the arrival time if the schedule changes.");
        }

        routeBox.addActionListener(
                e -> estimateArrival.run());

        departSpinner.addChangeListener(
                e -> estimateArrival.run());

        JPanel form = new JPanel(
                new GridBagLayout());

        form.setOpaque(false);

        int row = 0;

        addFormRow(
                form,
                row++,
                "Route *",
                routeBox,
                null,
                null);

        addFormRow(
                form,
                row++,
                "Date *",
                dateSpinner,
                "Departure time *",
                departSpinner);

        addFormRow(
                form,
                row++,
                "Est. arrival *",
                arriveSpinner,
                "Status",
                statusBox);

        addFormRow(
                form,
                row++,
                "Bus *",
                busBox,
                null,
                null);

        GridBagConstraints hintConstraints = new GridBagConstraints();

        hintConstraints.gridx = 0;
        hintConstraints.gridy = row;
        hintConstraints.gridwidth = 2;
        hintConstraints.weightx = 1;
        hintConstraints.anchor = GridBagConstraints.WEST;
        hintConstraints.fill = GridBagConstraints.HORIZONTAL;
        hintConstraints.insets = new Insets(
                0,
                0,
                6,
                0);

        form.add(
                arrivalHint,
                hintConstraints);

        JPanel buttons = new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT,
                        8,
                        0));

        buttons.setOpaque(false);

        AppButton cancelButton = new AppButton(
                "Cancel",
                AppButton.Variant.SECONDARY);

        cancelButton.addActionListener(
                e -> dialog.dispose());

        AppButton saveButton = new AppButton(
                isNew
                        ? "Add Trip"
                        : "Save Changes");

        saveButton.addActionListener(
                e -> {

                    RouteResponse route = (RouteResponse) routeBox
                            .getSelectedItem();

                    BusResponse bus = (BusResponse) busBox
                            .getSelectedItem();

                    LocalDate date = dateOf(
                            dateSpinner);

                    LocalTime departureTime = timeOf(
                            departSpinner);

                    LocalTime arrivalTime = timeOf(
                            arriveSpinner);

                    String status = apiStatus(
                            String.valueOf(
                                    statusBox
                                            .getSelectedItem()));

                    String error = validateTrip(
                            editing,
                            route,
                            date,
                            departureTime,
                            arrivalTime,
                            bus);

                    if (error != null) {

                        errorLabel.setText(
                                error);

                        dialog.pack();

                        return;
                    }

                    LocalDateTime departure = date.atTime(
                            departureTime);

                    LocalDateTime arrival = arrivalFor(
                            departure,
                            arrivalTime);

                    saveTrip(
                            dialog,
                            editing,
                            route,
                            bus,
                            departure,
                            arrival,
                            status,
                            errorLabel);
                });

        buttons.add(
                cancelButton);

        buttons.add(
                saveButton);

        JPanel south = new JPanel(
                new BorderLayout(
                        0,
                        8));

        south.setOpaque(false);

        south.add(
                errorLabel,
                BorderLayout.NORTH);

        south.add(
                buttons,
                BorderLayout.SOUTH);

        JLabel heading = new JLabel(
                isNew
                        ? "Add New Trip"
                        : "Edit "
                                + tripId(
                                        editing));

        heading.setFont(
                BussinTheme.SECTION_TITLE);

        heading.setForeground(
                BussinTheme.TEXT_PRIMARY);

        JPanel root = new JPanel(
                new BorderLayout(
                        0,
                        14));

        root.setBackground(
                BussinTheme.BACKGROUND);

        root.setBorder(
                BorderFactory.createEmptyBorder(
                        22,
                        24,
                        18,
                        24));

        root.add(
                heading,
                BorderLayout.NORTH);

        root.add(
                form,
                BorderLayout.CENTER);

        root.add(
                south,
                BorderLayout.SOUTH);

        dialog.getRootPane()
                .setDefaultButton(
                        saveButton);

        showDialog(
                dialog,
                root,
                560);
    }

    private void saveTrip(
            JDialog dialog,
            TripResponse editing,
            RouteResponse route,
            BusResponse bus,
            LocalDateTime departure,
            LocalDateTime arrival,
            String status,
            JLabel errorLabel) {

        new SwingWorker<TripResponse, Void>() {

            @Override
            protected TripResponse doInBackground()
                    throws Exception {

                if (editing == null) {

                    return TripApiService.createTrip(
                            bus.getId(),
                            route.getId(),
                            departure,
                            arrival,
                            status);
                }

                return TripApiService.updateTrip(
                        editing.getId(),
                        bus.getId(),
                        route.getId(),
                        departure,
                        arrival,
                        status);
            }

            @Override
            protected void done() {

                try {

                    TripResponse saved = get();

                    replaceTrip(
                            saved);

                    refresh();

                    dialog.dispose();

                } catch (InterruptedException e) {

                    Thread.currentThread()
                            .interrupt();

                    errorLabel.setText(
                            "Trip operation was interrupted.");

                    dialog.pack();

                } catch (ExecutionException e) {

                    errorLabel.setText(
                            getErrorMessage(e));

                    dialog.pack();
                }
            }
        }.execute();
    }

    String validateTrip(
            TripResponse editing,
            RouteResponse route,
            LocalDate date,
            LocalTime departure,
            LocalTime arrival,
            BusResponse bus) {

        if (route == null) {
            return "Select a route.";
        }

        if (bus == null) {
            return "Select a bus.";
        }

        if (date == null
                || departure == null
                || arrival == null) {

            return "Enter the date, departure time, and arrival time.";
        }

        LocalDateTime departureDateTime = date.atTime(
                departure);

        LocalDateTime arrivalDateTime = arrivalFor(
                departureDateTime,
                arrival);

        if (departureDateTime.isBefore(
                LocalDateTime.now())) {

            return "The departure date and time cannot be in the past.";
        }

        if (!arrivalDateTime.isAfter(
                departureDateTime)) {

            return "Arrival must be after departure.";
        }

        if (Duration.between(
                departureDateTime,
                arrivalDateTime)
                .toHours() > 16) {

            return "Trip duration is over 16 hours. Check the arrival time.";
        }

        for (TripResponse other : trips) {

            if (editing != null
                    && other.getId()
                            .equals(
                                    editing.getId())) {

                continue;
            }

            if (normalizeStatus(
                    other.getStatus())
                    .equals(
                            API_CANCELLED)) {

                continue;
            }

            LocalDateTime otherDeparture = other.getScheduledDeparture();

            LocalDateTime otherArrival = other.getScheduledArrival();

            boolean overlaps = departureDateTime.isBefore(
                    otherArrival)
                    && otherDeparture.isBefore(
                            arrivalDateTime);

            if (!overlaps) {
                continue;
            }

            if (other.getBusId()
                    .equals(
                            bus.getId())) {

                return safe(
                        bus.getPlateNumber())
                        + " is already assigned to "
                        + tripId(other)
                        + " during this time.";
            }
        }

        return null;
    }

    private List<BusResponse> getAssignableBuses(
            Long currentBusId) {

        List<BusResponse> result = new ArrayList<>();

        for (BusResponse bus : buses) {

            boolean active = "ACTIVE".equalsIgnoreCase(
                    safe(
                            bus.getStatus()));

            boolean current = currentBusId != null
                    && bus.getId()
                            .equals(
                                    currentBusId);

            if (active || current) {
                result.add(bus);
            }
        }

        return result;
    }

    private static int selectedRouteDuration(
            List<RouteResponse> routes) {

        if (routes.isEmpty()) {
            return 0;
        }

        RouteResponse route = routes.get(0);

        return route.getDurationMinutes() == null
                ? 0
                : route.getDurationMinutes();
    }

    private static void selectRoute(
            JComboBox<RouteResponse> box,
            Long routeId) {

        for (int i = 0; i < box.getItemCount(); i++) {

            RouteResponse route = box.getItemAt(i);

            if (route.getId()
                    .equals(
                            routeId)) {

                box.setSelectedIndex(i);

                return;
            }
        }
    }

    private static void selectBus(
            JComboBox<BusResponse> box,
            Long busId) {

        for (int i = 0; i < box.getItemCount(); i++) {

            BusResponse bus = box.getItemAt(i);

            if (bus.getId()
                    .equals(
                            busId)) {

                box.setSelectedIndex(i);

                return;
            }
        }
    }

    private static void configureRouteRenderer(
            JComboBox<RouteResponse> box) {

        box.setRenderer(
                new DefaultListCellRenderer() {

                    @Override
                    public Component getListCellRendererComponent(
                            JList<?> list,
                            Object value,
                            int index,
                            boolean selected,
                            boolean focused) {

                        super.getListCellRendererComponent(
                                list,
                                value,
                                index,
                                selected,
                                focused);

                        if (value instanceof RouteResponse route) {

                            setText(
                                    safe(
                                            route.getRouteIdentifier())
                                            + "  ·  "
                                            + safe(
                                                    route.getOrigin())
                                            + " → "
                                            + safe(
                                                    route.getDestination()));
                        }

                        return this;
                    }
                });
    }

    private static void configureBusRenderer(
            JComboBox<BusResponse> box) {

        box.setRenderer(
                new DefaultListCellRenderer() {

                    @Override
                    public Component getListCellRendererComponent(
                            JList<?> list,
                            Object value,
                            int index,
                            boolean selected,
                            boolean focused) {

                        super.getListCellRendererComponent(
                                list,
                                value,
                                index,
                                selected,
                                focused);

                        if (value instanceof BusResponse bus) {

                            setText(
                                    safe(
                                            bus.getPlateNumber())
                                            + "  ·  "
                                            + safe(
                                                    bus.getModel())
                                            + "  ·  "
                                            + bus.getCapacity()
                                            + " seats");
                        }

                        return this;
                    }
                });
    }

    private static LocalDateTime arrivalFor(
            LocalDateTime departure,
            LocalTime arrivalTime) {

        LocalDateTime arrival = departure
                .toLocalDate()
                .atTime(
                        arrivalTime);

        return arrival.isAfter(
                departure)
                        ? arrival
                        : arrival.plusDays(1);
    }

    private void replaceTrip(
            TripResponse updated) {

        for (int i = 0; i < trips.size(); i++) {

            if (trips.get(i)
                    .getId()
                    .equals(
                            updated.getId())) {

                trips.set(
                        i,
                        updated);

                return;
            }
        }

        trips.add(
                updated);
    }

    private static String tripId(
            TripResponse trip) {

        return "TR-" + trip.getId();
    }

    private static String displayStatus(
            String status) {

        String normalized = normalizeStatus(
                status);

        if (normalized.equals(
                API_SCHEDULED)) {

            return SCHEDULED;
        }

        if (normalized.equals(
                API_BOARDING)) {

            return BOARDING;
        }

        if (normalized.equals(
                API_DEPARTED)) {

            return IN_PROGRESS;
        }

        if (normalized.equals(
                API_COMPLETED)) {

            return COMPLETED;
        }

        if (normalized.equals(
                API_CANCELLED)) {

            return CANCELLED;
        }

        return status == null
                ? ""
                : status;
    }

    private static String apiStatus(
            String displayStatus) {

        if (displayStatus.equals(
                SCHEDULED)) {

            return API_SCHEDULED;
        }

        if (displayStatus.equals(
                BOARDING)) {

            return API_BOARDING;
        }

        if (displayStatus.equals(
                IN_PROGRESS)) {

            return API_DEPARTED;
        }

        if (displayStatus.equals(
                COMPLETED)) {

            return API_COMPLETED;
        }

        if (displayStatus.equals(
                CANCELLED)) {

            return API_CANCELLED;
        }

        return API_SCHEDULED;
    }

    private static String normalizeStatus(
            String status) {

        return status == null
                ? ""
                : status.trim()
                        .toUpperCase(
                                Locale.ROOT);
    }

    private static AppBadge.Status badgeFor(
            String status) {

        String normalized = normalizeStatus(
                status);

        if (normalized.equals(
                API_BOARDING)) {

            return AppBadge.Status.WARNING;
        }

        if (normalized.equals(
                API_DEPARTED)) {

            return AppBadge.Status.INFO;
        }

        if (normalized.equals(
                API_COMPLETED)) {

            return AppBadge.Status.SUCCESS;
        }

        if (normalized.equals(
                API_CANCELLED)) {

            return AppBadge.Status.DANGER;
        }

        return AppBadge.Status.NEUTRAL;
    }

    private static boolean canEdit(
            TripResponse trip) {

        return normalizeStatus(
                trip.getStatus())
                .equals(
                        API_SCHEDULED);
    }

    private static boolean canCancel(
            TripResponse trip) {

        String status = normalizeStatus(
                trip.getStatus());

        return status.equals(
                API_SCHEDULED)
                || status.equals(
                        API_BOARDING);
    }

    private static boolean canDelete(
            TripResponse trip) {

        String status = normalizeStatus(
                trip.getStatus());

        return status.equals(
                API_SCHEDULED)
                || status.equals(
                        API_CANCELLED);
    }

    private static boolean canStartBoarding(
            TripResponse trip) {

        return !trip
                .getScheduledDeparture()
                .toLocalDate()
                .isAfter(
                        LocalDate.now());
    }

    private static String arrivalNote(
            TripResponse trip) {

        return trip
                .getScheduledArrival()
                .toLocalDate()
                .equals(
                        trip
                                .getScheduledDeparture()
                                .toLocalDate())
                                        ? "same day"
                                        : "+1 day";
    }

    private static String formatDuration(
            Duration duration) {

        long hours = duration.toHours();

        long minutes = duration.toMinutesPart();

        if (minutes == 0) {
            return hours + "h";
        }

        return hours
                + "h "
                + minutes
                + "m";
    }

    private int addFact(
            JPanel panel,
            int row,
            String leftLabel,
            String leftValue,
            String rightLabel,
            String rightValue) {

        addFactCell(
                panel,
                0,
                row,
                leftLabel,
                leftValue);

        addFactCell(
                panel,
                1,
                row,
                rightLabel,
                rightValue);

        return row + 1;
    }

    private void addFactCell(
            JPanel panel,
            int column,
            int row,
            String label,
            String value) {

        JLabel title = new JLabel(label);

        title.setFont(
                BussinTheme.SMALL_BOLD);

        title.setForeground(
                BussinTheme.TEXT_MUTED);

        JLabel text = new JLabel(value);

        text.setFont(
                BussinTheme.BODY);

        text.setForeground(
                BussinTheme.TEXT_PRIMARY);

        JPanel cell = new JPanel();

        cell.setOpaque(false);

        cell.setLayout(
                new BoxLayout(
                        cell,
                        BoxLayout.Y_AXIS));

        cell.setBorder(
                BorderFactory.createEmptyBorder(
                        0,
                        0,
                        12,
                        12));

        cell.add(title);

        cell.add(
                Box.createVerticalStrut(3));

        cell.add(text);

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = column;
        gbc.gridy = row;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        panel.add(
                cell,
                gbc);
    }

    private void addFormRow(
            JPanel form,
            int row,
            String leftLabel,
            JComponent leftField,
            String rightLabel,
            JComponent rightField) {

        addFormCell(
                form,
                0,
                row,
                leftLabel,
                leftField,
                rightField == null
                        ? 2
                        : 1);

        if (rightField != null) {

            addFormCell(
                    form,
                    1,
                    row,
                    rightLabel,
                    rightField,
                    1);
        }
    }

    private void addFormCell(
            JPanel form,
            int column,
            int row,
            String label,
            JComponent field,
            int span) {

        JLabel title = new JLabel(label);

        title.setFont(
                BussinTheme.SMALL_BOLD);

        title.setForeground(
                BussinTheme.TEXT_SECONDARY);

        field.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        36));

        JPanel cell = new JPanel(
                new BorderLayout(
                        0,
                        4));

        cell.setOpaque(false);

        cell.setBorder(
                BorderFactory.createEmptyBorder(
                        0,
                        column == 0
                                ? 0
                                : 6,
                        12,
                        column == 0
                                && span == 1
                                        ? 6
                                        : 0));

        cell.add(
                title,
                BorderLayout.NORTH);

        cell.add(
                field,
                BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = column;
        gbc.gridy = row;
        gbc.gridwidth = span;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        form.add(
                cell,
                gbc);
    }

    private JDialog createDialog(
            String title) {

        Window owner = SwingUtilities
                .getWindowAncestor(
                        this);

        JDialog dialog = new JDialog(
                owner,
                title,
                Dialog.ModalityType.APPLICATION_MODAL);

        dialog.setDefaultCloseOperation(
                JDialog.DISPOSE_ON_CLOSE);

        return dialog;
    }

    private void showDialog(
            JDialog dialog,
            JPanel root,
            int minWidth) {

        JScrollPane scroll = new JScrollPane(
                root,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        scroll.setBorder(null);

        scroll.getViewport()
                .setBackground(
                        BussinTheme.BACKGROUND);

        dialog.setContentPane(
                scroll);

        dialog.pack();

        Rectangle screen = dialog.getGraphicsConfiguration() == null
                ? new Rectangle(
                        0,
                        0,
                        1280,
                        720)
                : dialog
                        .getGraphicsConfiguration()
                        .getBounds();

        Dimension size = dialog.getSize();

        size.width = Math.min(
                Math.max(
                        size.width,
                        minWidth),
                screen.width - 80);

        size.height = Math.min(
                size.height + 4,
                screen.height - 120);

        dialog.setSize(
                size);

        dialog.setMinimumSize(
                new Dimension(
                        Math.min(
                                minWidth,
                                size.width),
                        Math.min(
                                320,
                                size.height)));

        dialog.setLocationRelativeTo(
                this);

        dialog.setVisible(true);
    }

    private static JSpinner dateSpinner(
            LocalDate date) {

        SpinnerDateModel model = new SpinnerDateModel(
                Date.from(
                        date.atStartOfDay(
                                ZoneId.systemDefault())
                                .toInstant()),
                null,
                null,
                Calendar.DAY_OF_MONTH);

        JSpinner spinner = new JSpinner(model);

        spinner.setEditor(
                new JSpinner.DateEditor(
                        spinner,
                        "MMM dd, yyyy"));

        return spinner;
    }

    private static JSpinner timeSpinner(
            LocalTime time) {

        SpinnerDateModel model = new SpinnerDateModel(
                toDate(time),
                null,
                null,
                Calendar.MINUTE);

        JSpinner spinner = new JSpinner(model);

        spinner.setEditor(
                new JSpinner.DateEditor(
                        spinner,
                        "hh:mm a"));

        return spinner;
    }

    private static Date toDate(
            LocalTime time) {

        return Date.from(
                LocalDate.now()
                        .atTime(time)
                        .atZone(
                                ZoneId.systemDefault())
                        .toInstant());
    }

    private static LocalTime timeOf(
            JSpinner spinner) {

        return ((Date) spinner.getValue())
                .toInstant()
                .atZone(
                        ZoneId.systemDefault())
                .toLocalTime()
                .withSecond(0)
                .withNano(0);
    }

    private static LocalDate dateOf(
            JSpinner spinner) {

        return ((Date) spinner.getValue())
                .toInstant()
                .atZone(
                        ZoneId.systemDefault())
                .toLocalDate();
    }

    private static JPanel transparent() {

        JPanel panel = new JPanel();

        panel.setOpaque(false);

        return panel;
    }

    private static AppButton small(
            String text,
            AppButton.Variant variant) {

        AppButton button = new AppButton(
                text,
                variant);

        button.setBorder(
                BorderFactory.createEmptyBorder(
                        6,
                        12,
                        6,
                        12));

        return button;
    }

    private static JPanel emptyState(
            String message) {

        JPanel panel = new JPanel(
                new GridBagLayout());

        panel.setOpaque(false);

        panel.setBorder(
                BorderFactory.createEmptyBorder(
                        24,
                        0,
                        24,
                        0));

        JLabel label = new JLabel(message);

        label.setFont(
                BussinTheme.BODY);

        label.setForeground(
                BussinTheme.TEXT_MUTED);

        panel.add(label);

        return panel;
    }

    private void renderLoadingState() {

        statsHolder.removeAll();

        statsHolder.setLayout(
                new ResponsiveLayouts.Grid(
                        4,
                        200,
                        14));

        statsHolder.add(
                new StatCard(
                        "TODAY'S TRIPS",
                        "...",
                        "loading",
                        "trip"));

        statsHolder.add(
                new StatCard(
                        "SCHEDULED",
                        "...",
                        "loading",
                        "queue"));

        statsHolder.add(
                new StatCard(
                        "IN PROGRESS",
                        "...",
                        "loading",
                        "bus"));

        statsHolder.add(
                new StatCard(
                        "COMPLETED",
                        "...",
                        "loading",
                        "seat"));

        listHolder.removeAll();

        listHolder.setLayout(
                new BorderLayout());

        listHolder.add(
                emptyState(
                        "Loading trips..."),
                BorderLayout.CENTER);

        revalidate();

        repaint();
    }

    private void showLoadError(
            String message) {

        statsHolder.removeAll();

        listHolder.removeAll();

        listHolder.setLayout(
                new BorderLayout());

        listHolder.add(
                emptyState(
                        "Failed to load trips: "
                                + safe(
                                        message)),
                BorderLayout.CENTER);

        revalidate();

        repaint();
    }

    private void showError(
            String message) {

        JOptionPane.showMessageDialog(
                this,
                safe(message),
                "Trip Management",
                JOptionPane.ERROR_MESSAGE);
    }

    private static String getErrorMessage(
            ExecutionException exception) {

        Throwable cause = exception.getCause();

        if (cause == null) {
            return "The trip operation failed.";
        }

        if (cause.getMessage() == null) {
            return cause.toString();
        }

        return cause.getMessage();
    }

    private static String safe(
            String value) {

        return value == null
                ? ""
                : value;
    }

    private static JPanel twoLine(
            String top,
            String bottom) {

        JLabel primary = DataGrid.strong(top);

        JLabel secondary = new JLabel(bottom);

        secondary.setFont(
                BussinTheme.SMALL);

        secondary.setForeground(
                BussinTheme.TEXT_MUTED);

        JPanel panel = new JPanel();

        panel.setOpaque(false);

        panel.setLayout(
                new BoxLayout(
                        panel,
                        BoxLayout.Y_AXIS));

        panel.add(primary);

        panel.add(secondary);

        return panel;
    }

    private static final class HorizontalScrollHolder
            extends JScrollPane {

        HorizontalScrollHolder(
                JComponent table) {

            super(
                    new Body(table),
                    ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

            setBorder(null);

            setOpaque(false);

            getViewport()
                    .setOpaque(false);

            setWheelScrollingEnabled(false);

            getHorizontalScrollBar()
                    .setUnitIncrement(24);
        }

        private static final class Body
                extends JPanel
                implements Scrollable {

            private final JComponent table;

            Body(
                    JComponent table) {

                super(
                        new BorderLayout());

                this.table = table;

                setOpaque(false);

                setBorder(
                        BorderFactory.createEmptyBorder(
                                0,
                                0,
                                SCROLLBAR_ALLOWANCE,
                                0));

                add(
                        table,
                        BorderLayout.NORTH);
            }

            @Override
            public Dimension getPreferredSize() {

                Dimension natural = table.getPreferredSize();

                return new Dimension(
                        Math.max(
                                TABLE_MIN_WIDTH,
                                natural.width),
                        natural.height
                                + SCROLLBAR_ALLOWANCE);
            }

            @Override
            public Dimension getPreferredScrollableViewportSize() {
                return getPreferredSize();
            }

            @Override
            public int getScrollableUnitIncrement(
                    Rectangle visible,
                    int orientation,
                    int direction) {

                return 24;
            }

            @Override
            public int getScrollableBlockIncrement(
                    Rectangle visible,
                    int orientation,
                    int direction) {

                return Math.max(
                        24,
                        visible.width - 48);
            }

            @Override
            public boolean getScrollableTracksViewportWidth() {

                return getParent() != null
                        && getParent().getWidth() >= getPreferredSize().width;
            }

            @Override
            public boolean getScrollableTracksViewportHeight() {
                return false;
            }
        }
    }

    private record DataSet(
            List<RouteResponse> routes,
            List<BusResponse> buses,
            List<TripResponse> trips) {
    }
}