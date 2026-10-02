package com.bussin.desktop.ui.screens;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import com.bussin.desktop.services.RouteApiService;
import com.bussin.desktop.services.RouteApiService.RouteResponse;
import com.bussin.desktop.services.TripApiService;
import com.bussin.desktop.services.TripApiService.TripResponse;
import com.bussin.desktop.ui.components.AppButton;
import com.bussin.desktop.ui.components.AppCard;
import com.bussin.desktop.ui.components.AppLabel;
import com.bussin.desktop.ui.components.PageContent;
import com.bussin.desktop.ui.components.ResponsiveLayouts;
import com.bussin.desktop.ui.flow.BookingFlowState;
import com.bussin.desktop.ui.flow.CommuterTrip;
import com.bussin.desktop.ui.theme.BussinTheme;

public class TripSearchScreen extends JPanel {

        private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
                        .ofPattern("MMM d, yyyy • h:mm a");

        private final Consumer<String> navigationHandler;
        private final BookingFlowState flowState;

        private final JPanel resultsPanel = new JPanel();

        private final JComboBox<String> originCombo = new JComboBox<>();

        private final JComboBox<String> destinationCombo = new JComboBox<>();

        private final Map<Long, RouteResponse> routesById = new LinkedHashMap<>();

        private List<TripResponse> trips = new ArrayList<>();

        public TripSearchScreen(
                        Consumer<String> navigationHandler,
                        BookingFlowState flowState) {

                this.navigationHandler = navigationHandler;
                this.flowState = flowState;

                initializeUI();
                loadData();
        }

        private void initializeUI() {

                setBackground(BussinTheme.BACKGROUND);
                setLayout(new BorderLayout());

                PageContent page = new PageContent();

                page.addBlock(createHeader(), 0);
                page.addBlock(createSearchCard(), 24);
                page.addBlock(resultsPanel, 18);

                add(page.inScrollPane(), BorderLayout.CENTER);

                showMessage("Loading available trips...");
        }

        private JPanel createHeader() {

                JPanel panel = new JPanel();
                panel.setOpaque(false);
                panel.setLayout(
                                new BoxLayout(
                                                panel,
                                                BoxLayout.Y_AXIS));

                panel.add(
                                AppLabel.title("Find a Trip"));

                panel.add(
                                Box.createVerticalStrut(5));

                panel.add(
                                AppLabel.secondary(
                                                "Search available BUSSIN trips and choose your preferred schedule."));

                return panel;
        }

        private JPanel createSearchCard() {

                AppCard card = new AppCard();

                card.setLayout(
                                new BorderLayout(
                                                16,
                                                12));

                JPanel fields = new JPanel(
                                new ResponsiveLayouts.Grid(
                                                2,
                                                220,
                                                14));

                fields.setOpaque(false);

                fields.add(
                                fieldPanel(
                                                "FROM",
                                                originCombo));

                fields.add(
                                fieldPanel(
                                                "TO",
                                                destinationCombo));

                AppButton searchButton = new AppButton("Search Trips");

                searchButton.addActionListener(
                                event -> renderResults());

                JPanel actions = new JPanel(
                                new FlowLayout(
                                                FlowLayout.RIGHT,
                                                0,
                                                0));

                actions.setOpaque(false);

                actions.add(searchButton);

                card.add(
                                fields,
                                BorderLayout.CENTER);

                card.add(
                                actions,
                                BorderLayout.SOUTH);

                return card;
        }

        private JPanel fieldPanel(
                        String title,
                        JComboBox<String> combo) {

                JPanel panel = new JPanel();

                panel.setOpaque(false);

                panel.setLayout(
                                new BoxLayout(
                                                panel,
                                                BoxLayout.Y_AXIS));

                JLabel label = new JLabel(title);

                label.setFont(
                                BussinTheme.SMALL_BOLD);

                label.setForeground(
                                BussinTheme.TEXT_SECONDARY);

                combo.setPreferredSize(
                                new java.awt.Dimension(
                                                0,
                                                38));

                panel.add(label);

                panel.add(
                                Box.createVerticalStrut(6));

                panel.add(combo);

                return panel;
        }

        private void loadData() {

                SwingWorker<LoadedData, Void> worker = new SwingWorker<>() {

                        @Override
                        protected LoadedData doInBackground()
                                        throws Exception {

                                List<RouteResponse> routes = RouteApiService.getAllRoutes();

                                List<TripResponse> loadedTrips = TripApiService.getAllTrips();

                                return new LoadedData(
                                                routes,
                                                loadedTrips);
                        }

                        @Override
                        protected void done() {

                                try {

                                        LoadedData data = get();

                                        routesById.clear();

                                        for (RouteResponse route : data.routes()) {

                                                if (route.getId() != null
                                                                && route.isActive()) {

                                                        routesById.put(
                                                                        route.getId(),
                                                                        route);
                                                }
                                        }

                                        trips = new ArrayList<>(
                                                        data.trips());

                                        populateFilters();
                                        renderResults();

                                } catch (Exception ex) {

                                        showMessage(
                                                        "Unable to load trips. "
                                                                        + getErrorMessage(ex));
                                }
                        }
                };

                worker.execute();
        }

        private void populateFilters() {

                String selectedOrigin = String.valueOf(
                                originCombo.getSelectedItem());

                String selectedDestination = String.valueOf(
                                destinationCombo.getSelectedItem());

                originCombo.removeAllItems();

                destinationCombo.removeAllItems();

                originCombo.addItem(
                                "All origins");

                destinationCombo.addItem(
                                "All destinations");

                List<String> origins = new ArrayList<>();

                List<String> destinations = new ArrayList<>();

                for (TripResponse trip : trips) {

                        if (trip.getRouteId() == null) {
                                continue;
                        }

                        RouteResponse route = routesById.get(
                                        trip.getRouteId());

                        if (route == null) {
                                continue;
                        }

                        if (route.getOrigin() != null
                                        && !route.getOrigin().isBlank()
                                        && !origins.contains(
                                                        route.getOrigin())) {

                                origins.add(
                                                route.getOrigin());
                        }

                        if (route.getDestination() != null
                                        && !route.getDestination().isBlank()
                                        && !destinations.contains(
                                                        route.getDestination())) {

                                destinations.add(
                                                route.getDestination());
                        }
                }

                origins.sort(String::compareToIgnoreCase);
                destinations.sort(String::compareToIgnoreCase);

                for (String origin : origins) {
                        originCombo.addItem(origin);
                }

                for (String destination : destinations) {
                        destinationCombo.addItem(destination);
                }

                restoreSelection(
                                originCombo,
                                selectedOrigin,
                                "All origins");

                restoreSelection(
                                destinationCombo,
                                selectedDestination,
                                "All destinations");
        }

        private void restoreSelection(
                        JComboBox<String> combo,
                        String value,
                        String fallback) {

                if (value != null
                                && !value.equals("null")) {

                        for (int i = 0; i < combo.getItemCount(); i++) {

                                if (value.equals(
                                                combo.getItemAt(i))) {

                                        combo.setSelectedIndex(i);
                                        return;
                                }
                        }
                }

                combo.setSelectedItem(fallback);
        }

        private void renderResults() {

                resultsPanel.removeAll();

                resultsPanel.setOpaque(false);

                resultsPanel.setLayout(
                                new BorderLayout());

                JPanel wrapper = new JPanel();

                wrapper.setOpaque(false);

                wrapper.setLayout(
                                new BoxLayout(
                                                wrapper,
                                                BoxLayout.Y_AXIS));

                wrapper.add(
                                AppLabel.section(
                                                "Available Trips"));

                wrapper.add(
                                Box.createVerticalStrut(12));

                String selectedOrigin = String.valueOf(
                                originCombo.getSelectedItem());

                String selectedDestination = String.valueOf(
                                destinationCombo.getSelectedItem());

                List<CommuterTrip> matches = new ArrayList<>();

                for (TripResponse trip : trips) {

                        RouteResponse route = routesById.get(
                                        trip.getRouteId());

                        if (route == null) {
                                continue;
                        }

                        if (!matchesFilter(
                                        selectedOrigin,
                                        route.getOrigin(),
                                        "All origins")) {

                                continue;
                        }

                        if (!matchesFilter(
                                        selectedDestination,
                                        route.getDestination(),
                                        "All destinations")) {

                                continue;
                        }

                        if (!isBookableStatus(
                                        trip.getStatus())) {

                                continue;
                        }

                        matches.add(
                                        toCommuterTrip(
                                                        trip,
                                                        route));
                }

                if (matches.isEmpty()) {

                        wrapper.add(
                                        createEmptyState(
                                                        "No available trips match your selected filters."));

                } else {

                        JPanel grid = new JPanel(
                                        new GridLayout(
                                                        0,
                                                        2,
                                                        14,
                                                        14));

                        grid.setOpaque(false);

                        for (CommuterTrip trip : matches) {

                                grid.add(
                                                createTripCard(trip));
                        }

                        wrapper.add(grid);
                }

                resultsPanel.add(
                                wrapper,
                                BorderLayout.CENTER);

                resultsPanel.revalidate();
                resultsPanel.repaint();
        }

        private boolean matchesFilter(
                        String selected,
                        String actual,
                        String allValue) {

                if (allValue.equalsIgnoreCase(selected)) {
                        return true;
                }

                if (actual == null) {
                        return false;
                }

                return actual.equalsIgnoreCase(selected);
        }

        private boolean isBookableStatus(
                        String status) {

                if (status == null) {
                        return false;
                }

                return "SCHEDULED".equalsIgnoreCase(status)
                                || "BOARDING".equalsIgnoreCase(status);
        }

        private CommuterTrip toCommuterTrip(
                        TripResponse trip,
                        RouteResponse route) {

                String departure = formatDateTime(
                                trip.getScheduledDeparture());

                String arrival = formatDateTime(
                                trip.getScheduledArrival());

                String busNumber = trip.getBusPlateNumber() == null
                                ? "Unassigned"
                                : trip.getBusPlateNumber();

                double fare = route.getBaseFare() == null
                                ? 0.0
                                : route.getBaseFare().doubleValue();

                int totalSeats = trip.getBusCapacity() == null
                                ? 0
                                : trip.getBusCapacity();

                return new CommuterTrip(
                                String.valueOf(
                                                trip.getId()),
                                departure,
                                arrival,
                                route.getOrigin(),
                                route.getDestination(),
                                busNumber,
                                fare,
                                totalSeats);
        }

        private JPanel createTripCard(
                        CommuterTrip trip) {

                AppCard card = new AppCard();

                card.setLayout(
                                new BorderLayout(
                                                0,
                                                14));

                JPanel top = new JPanel();

                top.setOpaque(false);

                top.setLayout(
                                new BoxLayout(
                                                top,
                                                BoxLayout.Y_AXIS));

                JLabel route = AppLabel.section(
                                trip.getRoute());

                JLabel tripId = new JLabel(
                                "Trip #" + trip.getTripId());

                tripId.setFont(
                                BussinTheme.SMALL_BOLD);

                tripId.setForeground(
                                BussinTheme.TEXT_MUTED);

                top.add(route);

                top.add(
                                Box.createVerticalStrut(4));

                top.add(tripId);

                JPanel details = new JPanel(
                                new GridLayout(
                                                2,
                                                2,
                                                12,
                                                8));

                details.setOpaque(false);

                details.add(
                                info(
                                                "DEPARTURE",
                                                trip.getDeparture()));

                details.add(
                                info(
                                                "ARRIVAL",
                                                trip.getArrival()));

                details.add(
                                info(
                                                "BUS",
                                                trip.getBusNumber()));

                details.add(
                                info(
                                                "FARE",
                                                String.format(
                                                                "₱%,.2f",
                                                                trip.getFare())));

                JLabel status = new JLabel(
                                "Available for booking");

                status.setFont(
                                BussinTheme.SMALL_BOLD);

                status.setForeground(
                                BussinTheme.SUCCESS);

                AppButton select = new AppButton(
                                "Select Trip");

                select.addActionListener(
                                event -> selectTrip(trip));

                card.add(
                                top,
                                BorderLayout.NORTH);

                card.add(
                                details,
                                BorderLayout.CENTER);

                JPanel bottom = new JPanel(
                                new BorderLayout());

                bottom.setOpaque(false);

                bottom.add(
                                status,
                                BorderLayout.WEST);

                bottom.add(
                                select,
                                BorderLayout.EAST);

                card.add(
                                bottom,
                                BorderLayout.SOUTH);

                return card;
        }

        private void selectTrip(
                        CommuterTrip trip) {

                flowState.reset();

                flowState.setSelectedTrip(
                                trip);

                navigationHandler.accept(
                                "seat-selection");
        }

        private JPanel info(
                        String title,
                        String value) {

                JPanel panel = new JPanel();

                panel.setOpaque(false);

                panel.setLayout(
                                new BoxLayout(
                                                panel,
                                                BoxLayout.Y_AXIS));

                JLabel titleLabel = new JLabel(title);

                titleLabel.setFont(
                                BussinTheme.SMALL_BOLD);

                titleLabel.setForeground(
                                BussinTheme.TEXT_MUTED);

                JLabel valueLabel = new JLabel(value);

                valueLabel.setFont(
                                BussinTheme.BODY_MEDIUM);

                valueLabel.setForeground(
                                BussinTheme.TEXT_PRIMARY);

                panel.add(titleLabel);

                panel.add(
                                Box.createVerticalStrut(3));

                panel.add(valueLabel);

                return panel;
        }

        private JPanel createEmptyState(
                        String message) {

                JPanel panel = new JPanel(
                                new FlowLayout(
                                                FlowLayout.LEFT));

                panel.setOpaque(false);

                panel.setBorder(
                                BorderFactory.createEmptyBorder(
                                                20,
                                                0,
                                                20,
                                                0));

                JLabel label = new JLabel(message);

                label.setFont(
                                BussinTheme.BODY);

                label.setForeground(
                                BussinTheme.TEXT_MUTED);

                panel.add(label);

                return panel;
        }

        private void showMessage(
                        String message) {

                resultsPanel.removeAll();

                resultsPanel.setOpaque(false);

                resultsPanel.setLayout(
                                new BorderLayout());

                resultsPanel.add(
                                createEmptyState(message),
                                BorderLayout.CENTER);

                resultsPanel.revalidate();
                resultsPanel.repaint();
        }

        private String formatDateTime(
                        LocalDateTime value) {

                if (value == null) {
                        return "Not scheduled";
                }

                return value.format(
                                DATE_TIME_FORMATTER);
        }

        private String getErrorMessage(
                        Exception exception) {

                Throwable cause = exception;

                while (cause.getCause() != null) {
                        cause = cause.getCause();
                }

                if (cause.getMessage() == null
                                || cause.getMessage().isBlank()) {

                        return "Please check that the BUSSIN API is running.";
                }

                return cause.getMessage();
        }

        private record LoadedData(
                        List<RouteResponse> routes,
                        List<TripResponse> trips) {
        }
}