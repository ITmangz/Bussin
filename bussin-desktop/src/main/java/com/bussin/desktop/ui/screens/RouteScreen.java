package com.bussin.desktop.ui.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import com.bussin.desktop.ui.components.AppBadge;
import com.bussin.desktop.ui.components.AppButton;
import com.bussin.desktop.ui.components.AppCard;
import com.bussin.desktop.ui.components.AppLabel;
import com.bussin.desktop.ui.components.IconFactory;
import com.bussin.desktop.ui.theme.BussinTheme;

public class RouteScreen extends JPanel {

        private final List<Route> routes = new ArrayList<>();
        private final List<Route> filteredRoutes = new ArrayList<>();

        private final RouteTableModel tableModel = new RouteTableModel();

        private final JTable routeTable = new JTable(tableModel);

        private final JTextField searchField = new JTextField();

        private final JComboBox<String> statusFilter = new JComboBox<>(
                        new String[] {
                                        "All Status",
                                        "Active",
                                        "Inactive"
                        });

        private final JLabel totalValue = new JLabel("0");

        private final JLabel activeValue = new JLabel("0");

        private final JLabel inactiveValue = new JLabel("0");

        private final JLabel detailRouteId = new JLabel("-");

        private final JLabel detailOrigin = new JLabel("-");

        private final JLabel detailDestination = new JLabel("-");

        private final JLabel detailDistance = new JLabel("-");

        private final JLabel detailDuration = new JLabel("-");

        private final JLabel detailFare = new JLabel("-");

        private final AppBadge detailStatus = new AppBadge(
                        "NO STATUS",
                        AppBadge.Status.NEUTRAL);

        private final AppButton editButton = new AppButton(
                        "Edit",
                        AppButton.Variant.SECONDARY);

        private final AppButton toggleStatusButton = new AppButton(
                        "Activate",
                        AppButton.Variant.SECONDARY);

        private final AppButton deleteButton = new AppButton(
                        "Delete",
                        AppButton.Variant.DANGER);

        public RouteScreen() {

                initializeData();
                initializeUI();
                refreshRoutes();
        }

        // ================================================================
        // MOCK DATA
        // ================================================================

        private void initializeData() {

                routes.add(
                                new Route(
                                                "RT-001",
                                                "Manila",
                                                "Batangas",
                                                "110",
                                                "2h 30m",
                                                "250",
                                                "Active"));

                routes.add(
                                new Route(
                                                "RT-002",
                                                "Manila",
                                                "Lucena",
                                                "135",
                                                "3h 15m",
                                                "320",
                                                "Active"));

                routes.add(
                                new Route(
                                                "RT-003",
                                                "Manila",
                                                "Bicol",
                                                "450",
                                                "9h 00m",
                                                "850",
                                                "Active"));

                routes.add(
                                new Route(
                                                "RT-004",
                                                "Manila",
                                                "Calapan",
                                                "160",
                                                "4h 00m",
                                                "400",
                                                "Inactive"));

                routes.add(
                                new Route(
                                                "RT-005",
                                                "Manila",
                                                "Naga",
                                                "380",
                                                "7h 30m",
                                                "720",
                                                "Active"));
        }

        // ================================================================
        // UI INITIALIZATION
        // ================================================================

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

                content.add(
                                createHeader());

                content.add(
                                Box.createVerticalStrut(
                                                BussinTheme.SPACE_XL));

                content.add(
                                createStatistics());

                content.add(
                                Box.createVerticalStrut(
                                                BussinTheme.SPACE_XL));

                content.add(
                                createMainSection());

                JScrollPane scrollPane = new JScrollPane(content);

                scrollPane.setBorder(null);

                scrollPane.setOpaque(false);

                scrollPane.getViewport()
                                .setOpaque(false);

                scrollPane.setHorizontalScrollBarPolicy(
                                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

                scrollPane.getVerticalScrollBar()
                                .setUnitIncrement(16);

                return scrollPane;
        }

        // ================================================================
        // HEADER
        // ================================================================

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
                                                "Route Management"));

                titlePanel.add(
                                Box.createVerticalStrut(5));

                titlePanel.add(
                                AppLabel.secondary(
                                                "Manage bus routes, destinations, travel times, and fares."));

                AppButton createButton = new AppButton(
                                "Create Route");

                createButton.setIcon(
                                IconFactory.create(
                                                "plus",
                                                16,
                                                Color.WHITE));

                createButton.addActionListener(
                                event -> showCreateRouteDialog());

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

        // ================================================================
        // STATISTICS
        // ================================================================

        private JPanel createStatistics() {

                JPanel statistics = new JPanel(
                                new GridLayout(
                                                1,
                                                3,
                                                14,
                                                0));

                statistics.setOpaque(false);

                statistics.add(
                                createStatCard(
                                                "TOTAL ROUTES",
                                                totalValue,
                                                BussinTheme.CHARCOAL));

                statistics.add(
                                createStatCard(
                                                "ACTIVE",
                                                activeValue,
                                                BussinTheme.SUCCESS));

                statistics.add(
                                createStatCard(
                                                "INACTIVE",
                                                inactiveValue,
                                                BussinTheme.RED));

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

                indicator.setBackground(
                                accent);

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

        // ================================================================
        // MAIN SECTION
        // ================================================================

        private JPanel createMainSection() {

                JPanel main = new JPanel(
                                new GridLayout(
                                                1,
                                                2,
                                                18,
                                                0));

                main.setOpaque(false);

                main.add(
                                createRouteListCard());

                main.add(
                                createRouteDetailsCard());

                return main;
        }

        // ================================================================
        // ROUTE LIST
        // ================================================================

        private AppCard createRouteListCard() {

                AppCard card = new AppCard();

                card.setLayout(
                                new BorderLayout(
                                                0,
                                                14));

                JPanel heading = new JPanel(
                                new BorderLayout());

                heading.setOpaque(false);

                heading.add(
                                AppLabel.section(
                                                "Route Directory"),
                                BorderLayout.WEST);

                JLabel hint = new JLabel(
                                "Select a route to manage");

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

                JScrollPane tableScroll = new JScrollPane(routeTable);

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
                                "Search route ID, origin, destination, distance, or fare");

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
                                                                refreshRoutes();
                                                        }

                                                        @Override
                                                        public void removeUpdate(
                                                                        DocumentEvent e) {
                                                                refreshRoutes();
                                                        }

                                                        @Override
                                                        public void changedUpdate(
                                                                        DocumentEvent e) {
                                                                refreshRoutes();
                                                        }
                                                });

                statusFilter.addActionListener(
                                event -> refreshRoutes());

                return filters;
        }

        private void configureTable() {

                routeTable.setRowHeight(42);

                routeTable.setFont(
                                BussinTheme.SMALL);

                routeTable.setSelectionMode(
                                ListSelectionModel.SINGLE_SELECTION);

                routeTable.setAutoCreateRowSorter(true);

                routeTable.setShowVerticalLines(false);

                routeTable.setShowHorizontalLines(true);

                routeTable.setGridColor(
                                BussinTheme.BORDER);

                routeTable.setSelectionBackground(
                                BussinTheme.PRIMARY_LIGHT);

                routeTable.setSelectionForeground(
                                BussinTheme.TEXT_PRIMARY);

                routeTable.setFillsViewportHeight(true);

                routeTable.getTableHeader()
                                .setFont(
                                                BussinTheme.SMALL_BOLD);

                routeTable.getTableHeader()
                                .setBackground(
                                                BussinTheme.SURFACE_ALT);

                routeTable.getTableHeader()
                                .setForeground(
                                                BussinTheme.TEXT_SECONDARY);

                for (int i = 0; i < 7; i++) {

                        routeTable.getColumnModel()
                                        .getColumn(i)
                                        .setPreferredWidth(
                                                        switch (i) {

                                                                case 0 -> 75;

                                                                case 1, 2 -> 105;

                                                                case 3, 4 -> 85;

                                                                case 5 -> 75;

                                                                case 6 -> 90;

                                                                default -> 80;
                                                        });
                }

                routeTable.getColumnModel()
                                .getColumn(6)
                                .setCellRenderer(
                                                new StatusCellRenderer());

                routeTable.getSelectionModel()
                                .addListSelectionListener(
                                                event -> {

                                                        if (!event.getValueIsAdjusting()) {
                                                                updateSelectedRoute();
                                                        }
                                                });
        }

        // ================================================================
        // ROUTE DETAILS
        // ================================================================

        private AppCard createRouteDetailsCard() {

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
                                                "Route Details"),
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

                JLabel idTitle = new JLabel(
                                "ROUTE ID");

                idTitle.setFont(
                                BussinTheme.SMALL_BOLD);

                idTitle.setForeground(
                                BussinTheme.TEXT_SECONDARY);

                detailRouteId.setFont(
                                BussinTheme.SECTION_TITLE);

                detailRouteId.setForeground(
                                BussinTheme.TEXT_PRIMARY);

                details.add(idTitle);

                details.add(
                                Box.createVerticalStrut(4));

                details.add(detailRouteId);

                details.add(
                                Box.createVerticalStrut(14));

                details.add(
                                createSeparator());

                details.add(
                                Box.createVerticalStrut(14));

                addDetail(
                                details,
                                "ORIGIN",
                                detailOrigin);

                addDetail(
                                details,
                                "DESTINATION",
                                detailDestination);

                addDetail(
                                details,
                                "DISTANCE",
                                detailDistance);

                addDetail(
                                details,
                                "TRAVEL TIME",
                                detailDuration);

                addDetail(
                                details,
                                "BASE FARE",
                                detailFare);

                details.add(
                                Box.createVerticalStrut(8));

                JPanel actions = new JPanel(
                                new FlowLayout(
                                                FlowLayout.LEFT,
                                                8,
                                                0));

                actions.setOpaque(false);

                editButton.setEnabled(false);

                toggleStatusButton.setEnabled(false);

                deleteButton.setEnabled(false);

                editButton.addActionListener(
                                event -> showEditRouteDialog());

                toggleStatusButton.addActionListener(
                                event -> toggleRouteStatus());

                deleteButton.addActionListener(
                                event -> deleteSelectedRoute());

                actions.add(editButton);

                actions.add(toggleStatusButton);

                actions.add(deleteButton);

                details.add(actions);

                JScrollPane scroll = new JScrollPane(details);

                scroll.setBorder(null);

                scroll.setOpaque(false);

                scroll.getViewport()
                                .setOpaque(false);

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

        // ================================================================
        // REFRESH / FILTER
        // ================================================================

        private void refreshRoutes() {

                String query = searchField.getText()
                                .trim()
                                .toLowerCase();

                String selectedStatus = String.valueOf(
                                statusFilter.getSelectedItem());

                Route previouslySelected = getSelectedRoute();

                filteredRoutes.clear();

                for (Route route : routes) {

                        boolean matchesSearch = query.isEmpty()
                                        || route.routeId
                                                        .toLowerCase()
                                                        .contains(query)
                                        || route.origin
                                                        .toLowerCase()
                                                        .contains(query)
                                        || route.destination
                                                        .toLowerCase()
                                                        .contains(query)
                                        || route.distance
                                                        .toLowerCase()
                                                        .contains(query)
                                        || route.duration
                                                        .toLowerCase()
                                                        .contains(query)
                                        || route.fare
                                                        .toLowerCase()
                                                        .contains(query);

                        boolean matchesStatus = selectedStatus.equals(
                                        "All Status")
                                        || route.status.equals(
                                                        selectedStatus);

                        if (matchesSearch
                                        && matchesStatus) {

                                filteredRoutes.add(route);
                        }
                }

                tableModel.fireTableDataChanged();

                updateStatistics();

                if (previouslySelected != null
                                && filteredRoutes.contains(
                                                previouslySelected)) {

                        int modelIndex = filteredRoutes.indexOf(
                                        previouslySelected);

                        int viewIndex = routeTable.convertRowIndexToView(
                                        modelIndex);

                        if (viewIndex >= 0
                                        && viewIndex < routeTable.getRowCount()) {

                                routeTable.setRowSelectionInterval(
                                                viewIndex,
                                                viewIndex);

                                return;
                        }
                }

                if (!filteredRoutes.isEmpty()) {

                        routeTable.setRowSelectionInterval(
                                        0,
                                        0);

                } else {

                        clearDetails();
                }
        }

        private void updateStatistics() {

                int total = routes.size();

                int active = 0;

                int inactive = 0;

                for (Route route : routes) {

                        if (route.status.equals("Active")) {
                                active++;
                        }

                        if (route.status.equals("Inactive")) {
                                inactive++;
                        }
                }

                totalValue.setText(
                                String.valueOf(total));

                activeValue.setText(
                                String.valueOf(active));

                inactiveValue.setText(
                                String.valueOf(inactive));
        }

        // ================================================================
        // SELECTED ROUTE
        // ================================================================

        private void updateSelectedRoute() {

                Route route = getSelectedRoute();

                if (route == null) {

                        clearDetails();

                        return;
                }

                detailRouteId.setText(
                                route.routeId);

                detailOrigin.setText(
                                route.origin);

                detailDestination.setText(
                                route.destination);

                detailDistance.setText(
                                route.distance + " km");

                detailDuration.setText(
                                route.duration);

                detailFare.setText(
                                "₱" + route.fare);

                updateStatusBadge(
                                route.status);

                editButton.setEnabled(true);

                boolean active = route.status.equals("Active");

                toggleStatusButton.setText(
                                active
                                                ? "Deactivate"
                                                : "Activate");

                toggleStatusButton.setEnabled(true);

                deleteButton.setEnabled(true);
        }

        private void clearDetails() {

                detailRouteId.setText("-");

                detailOrigin.setText("-");

                detailDestination.setText("-");

                detailDistance.setText("-");

                detailDuration.setText("-");

                detailFare.setText("-");

                updateStatusBadge(
                                "No Status");

                editButton.setEnabled(false);

                toggleStatusButton.setText(
                                "Activate");

                toggleStatusButton.setEnabled(false);

                deleteButton.setEnabled(false);
        }

        private Route getSelectedRoute() {

                int selectedRow = routeTable.getSelectedRow();

                if (selectedRow < 0) {
                        return null;
                }

                int modelRow = routeTable.convertRowIndexToModel(
                                selectedRow);

                if (modelRow < 0
                                || modelRow >= filteredRoutes.size()) {

                        return null;
                }

                return filteredRoutes.get(
                                modelRow);
        }

        // ================================================================
        // STATUS BADGE
        // ================================================================

        private void updateStatusBadge(
                        String status) {

                AppBadge.Status badgeStatus;

                switch (status) {

                        case "Active" ->
                                badgeStatus = AppBadge.Status.SUCCESS;

                        case "Inactive" ->
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

                        case DANGER -> {

                                detailStatus.setForeground(
                                                BussinTheme.DANGER);

                                detailStatus.setBackground(
                                                BussinTheme.DANGER_LIGHT);
                        }

                        default -> {

                                detailStatus.setForeground(
                                                BussinTheme.TEXT_SECONDARY);

                                detailStatus.setBackground(
                                                BussinTheme.SURFACE_ALT);
                        }
                }
        }

        // ================================================================
        // STATUS ACTION
        // ================================================================

        private void toggleRouteStatus() {

                Route route = getSelectedRoute();

                if (route == null) {
                        return;
                }

                boolean deactivate = route.status.equals("Active");

                if (deactivate) {

                        int result = JOptionPane.showConfirmDialog(
                                        this,
                                        "Deactivate "
                                                        + route.routeId
                                                        + "?\n\n"
                                                        + "This route will no longer "
                                                        + "be available for normal operations.",
                                        "Deactivate Route",
                                        JOptionPane.YES_NO_OPTION,
                                        JOptionPane.WARNING_MESSAGE);

                        if (result != JOptionPane.YES_OPTION) {
                                return;
                        }

                        route.status = "Inactive";

                } else {

                        route.status = "Active";
                }

                refreshRoutes();
        }

        // ================================================================
        // DELETE
        // ================================================================

        private void deleteSelectedRoute() {

                Route route = getSelectedRoute();

                if (route == null) {
                        return;
                }

                int result = JOptionPane.showConfirmDialog(
                                this,
                                "Delete "
                                                + route.routeId
                                                + " ("
                                                + route.origin
                                                + " → "
                                                + route.destination
                                                + ")?\n\n"
                                                + "This action cannot be undone.",
                                "Delete Route",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE);

                if (result == JOptionPane.YES_OPTION) {

                        routes.remove(route);

                        refreshRoutes();
                }
        }

        // ================================================================
        // CREATE ROUTE
        // ================================================================

        private void showCreateRouteDialog() {

                JDialog dialog = new JDialog(
                                SwingUtilities
                                                .getWindowAncestor(this),
                                "Create Route",
                                Dialog.ModalityType.APPLICATION_MODAL);

                dialog.setDefaultCloseOperation(
                                JDialog.DISPOSE_ON_CLOSE);

                dialog.setSize(
                                520,
                                440);

                dialog.setMinimumSize(
                                new Dimension(
                                                520,
                                                440));

                dialog.setLocationRelativeTo(this);

                JPanel root = new JPanel(
                                new BorderLayout());

                root.setBackground(
                                BussinTheme.BACKGROUND);

                JLabel title = new JLabel(
                                "Create New Route");

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
                                                5,
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

                JTextField originField = new JTextField();

                JTextField destinationField = new JTextField();

                JTextField distanceField = new JTextField();

                JTextField durationField = new JTextField();

                JTextField fareField = new JTextField();

                addFormField(
                                form,
                                "Origin",
                                originField);

                addFormField(
                                form,
                                "Destination",
                                destinationField);

                addFormField(
                                form,
                                "Distance (km)",
                                distanceField);

                addFormField(
                                form,
                                "Travel Time",
                                durationField);

                addFormField(
                                form,
                                "Base Fare",
                                fareField);

                JPanel buttons = new JPanel(
                                new FlowLayout(
                                                FlowLayout.RIGHT,
                                                8,
                                                12));

                buttons.setOpaque(false);

                AppButton cancelButton = new AppButton(
                                "Cancel",
                                AppButton.Variant.SECONDARY);

                AppButton saveButton = new AppButton(
                                "Create Route");

                cancelButton.addActionListener(
                                event -> dialog.dispose());

                saveButton.addActionListener(
                                event -> {

                                        String origin = originField.getText()
                                                        .trim();

                                        String destination = destinationField.getText()
                                                        .trim();

                                        String distance = distanceField.getText()
                                                        .trim();

                                        String duration = durationField.getText()
                                                        .trim();

                                        String fare = fareField.getText()
                                                        .trim();

                                        if (!validateRouteFields(
                                                        dialog,
                                                        origin,
                                                        destination,
                                                        distance,
                                                        duration,
                                                        fare)) {

                                                return;
                                        }

                                        routes.add(
                                                        new Route(
                                                                        generateRouteId(),
                                                                        origin,
                                                                        destination,
                                                                        distance,
                                                                        duration,
                                                                        fare,
                                                                        "Active"));

                                        refreshRoutes();

                                        dialog.dispose();
                                });

                buttons.add(cancelButton);

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

        // ================================================================
        // EDIT ROUTE
        // ================================================================

        private void showEditRouteDialog() {

                Route route = getSelectedRoute();

                if (route == null) {
                        return;
                }

                JDialog dialog = new JDialog(
                                SwingUtilities
                                                .getWindowAncestor(this),
                                "Edit Route",
                                Dialog.ModalityType.APPLICATION_MODAL);

                dialog.setDefaultCloseOperation(
                                JDialog.DISPOSE_ON_CLOSE);

                dialog.setSize(
                                520,
                                440);

                dialog.setMinimumSize(
                                new Dimension(
                                                520,
                                                440));

                dialog.setLocationRelativeTo(this);

                JPanel root = new JPanel(
                                new BorderLayout());

                root.setBackground(
                                BussinTheme.BACKGROUND);

                JLabel title = new JLabel(
                                "Edit Route");

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

                JTextField routeIdField = new JTextField(
                                route.routeId);

                JTextField originField = new JTextField(
                                route.origin);

                JTextField destinationField = new JTextField(
                                route.destination);

                JTextField distanceField = new JTextField(
                                route.distance);

                JTextField durationField = new JTextField(
                                route.duration);

                JTextField fareField = new JTextField(
                                route.fare);

                addFormField(
                                form,
                                "Route ID",
                                routeIdField);

                addFormField(
                                form,
                                "Origin",
                                originField);

                addFormField(
                                form,
                                "Destination",
                                destinationField);

                addFormField(
                                form,
                                "Distance (km)",
                                distanceField);

                addFormField(
                                form,
                                "Travel Time",
                                durationField);

                addFormField(
                                form,
                                "Base Fare",
                                fareField);

                JPanel buttons = new JPanel(
                                new FlowLayout(
                                                FlowLayout.RIGHT,
                                                8,
                                                12));

                buttons.setOpaque(false);

                AppButton cancelButton = new AppButton(
                                "Cancel",
                                AppButton.Variant.SECONDARY);

                AppButton saveButton = new AppButton(
                                "Save Changes");

                cancelButton.addActionListener(
                                event -> dialog.dispose());

                saveButton.addActionListener(
                                event -> {

                                        String routeId = routeIdField.getText()
                                                        .trim();

                                        String origin = originField.getText()
                                                        .trim();

                                        String destination = destinationField.getText()
                                                        .trim();

                                        String distance = distanceField.getText()
                                                        .trim();

                                        String duration = durationField.getText()
                                                        .trim();

                                        String fare = fareField.getText()
                                                        .trim();

                                        if (routeId.isEmpty()) {

                                                showWarning(
                                                                dialog,
                                                                "Route ID cannot be empty.",
                                                                "Invalid Route");

                                                return;
                                        }

                                        if (isDuplicateRouteId(
                                                        routeId,
                                                        route)) {

                                                showWarning(
                                                                dialog,
                                                                "Another route already uses this Route ID.",
                                                                "Duplicate Route ID");

                                                return;
                                        }

                                        if (!validateRouteFields(
                                                        dialog,
                                                        origin,
                                                        destination,
                                                        distance,
                                                        duration,
                                                        fare)) {

                                                return;
                                        }

                                        route.routeId = routeId;

                                        route.origin = origin;

                                        route.destination = destination;

                                        route.distance = distance;

                                        route.duration = duration;

                                        route.fare = fare;

                                        refreshRoutes();

                                        dialog.dispose();
                                });

                buttons.add(cancelButton);

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

        // ================================================================
        // VALIDATION
        // ================================================================

        private boolean validateRouteFields(
                        Component parent,
                        String origin,
                        String destination,
                        String distance,
                        String duration,
                        String fare) {

                if (origin.isEmpty()
                                || destination.isEmpty()
                                || distance.isEmpty()
                                || duration.isEmpty()
                                || fare.isEmpty()) {

                        showWarning(
                                        parent,
                                        "Please complete all required fields.",
                                        "Invalid Route");

                        return false;
                }

                if (origin.equalsIgnoreCase(
                                destination)) {

                        showWarning(
                                        parent,
                                        "Origin and destination cannot be the same.",
                                        "Invalid Route");

                        return false;
                }

                try {

                        double distanceValue = Double.parseDouble(
                                        distance);

                        if (distanceValue <= 0) {

                                showWarning(
                                                parent,
                                                "Distance must be greater than zero.",
                                                "Invalid Distance");

                                return false;
                        }

                } catch (NumberFormatException exception) {

                        showWarning(
                                        parent,
                                        "Distance must be a valid number.",
                                        "Invalid Distance");

                        return false;
                }

                try {

                        double fareValue = Double.parseDouble(
                                        fare);

                        if (fareValue <= 0) {

                                showWarning(
                                                parent,
                                                "Fare must be greater than zero.",
                                                "Invalid Fare");

                                return false;
                        }

                } catch (NumberFormatException exception) {

                        showWarning(
                                        parent,
                                        "Fare must be a valid number.",
                                        "Invalid Fare");

                        return false;
                }

                return true;
        }

        private boolean isDuplicateRouteId(
                        String routeId,
                        Route ignoredRoute) {

                for (Route route : routes) {

                        if (route == ignoredRoute) {
                                continue;
                        }

                        if (route.routeId.equalsIgnoreCase(
                                        routeId)) {

                                return true;
                        }
                }

                return false;
        }

        private void showWarning(
                        Component parent,
                        String message,
                        String title) {

                JOptionPane.showMessageDialog(
                                parent,
                                message,
                                title,
                                JOptionPane.WARNING_MESSAGE);
        }

        // ================================================================
        // ROUTE ID GENERATION
        // ================================================================

        private String generateRouteId() {

                int highest = 0;

                for (Route route : routes) {

                        try {

                                int number = Integer.parseInt(
                                                route.routeId
                                                                .substring(3));

                                highest = Math.max(
                                                highest,
                                                number);

                        } catch (NumberFormatException ignored) {
                                // Ignore unexpected route IDs.
                        }
                }

                return String.format(
                                "RT-%03d",
                                highest + 1);
        }

        // ================================================================
        // FORM FIELD
        // ================================================================

        private void addFormField(
                        JPanel panel,
                        String labelText,
                        JComponent field) {

                JLabel label = new JLabel(
                                labelText);

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

        // ================================================================
        // ROUTE MODEL
        // ================================================================

        private static class Route {

                private String routeId;

                private String origin;

                private String destination;

                private String distance;

                private String duration;

                private String fare;

                private String status;

                private Route(
                                String routeId,
                                String origin,
                                String destination,
                                String distance,
                                String duration,
                                String fare,
                                String status) {

                        this.routeId = routeId;

                        this.origin = origin;

                        this.destination = destination;

                        this.distance = distance;

                        this.duration = duration;

                        this.fare = fare;

                        this.status = status;
                }
        }

        // ================================================================
        // TABLE MODEL
        // ================================================================

        private class RouteTableModel
                        extends AbstractTableModel {

                private final String[] columns = {
                                "Route ID",
                                "Origin",
                                "Destination",
                                "Distance",
                                "Duration",
                                "Fare",
                                "Status"
                };

                @Override
                public int getRowCount() {

                        return filteredRoutes.size();
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

                        Route route = filteredRoutes.get(
                                        row);

                        return switch (column) {

                                case 0 ->
                                        route.routeId;

                                case 1 ->
                                        route.origin;

                                case 2 ->
                                        route.destination;

                                case 3 ->
                                        route.distance + " km";

                                case 4 ->
                                        route.duration;

                                case 5 ->
                                        "₱" + route.fare;

                                case 6 ->
                                        route.status;

                                default ->
                                        "";
                        };
                }

                @Override
                public boolean isCellEditable(
                                int row,
                                int column) {

                        return false;
                }
        }

        // ================================================================
        // STATUS TABLE RENDERER
        // ================================================================

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

                                case "Active" ->
                                        badgeStatus = AppBadge.Status.SUCCESS;

                                case "Inactive" ->
                                        badgeStatus = AppBadge.Status.DANGER;

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