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
import java.util.ArrayList;
import java.util.List;

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

    private void initializeData() {

        routes.add(
                new Route(
                        "RT-001",
                        "Manila",
                        "Batangas",
                        "110 km",
                        "2h 30m",
                        "₱250",
                        "Active"));

        routes.add(
                new Route(
                        "RT-002",
                        "Manila",
                        "Lucena",
                        "135 km",
                        "3h 15m",
                        "₱320",
                        "Active"));

        routes.add(
                new Route(
                        "RT-003",
                        "Manila",
                        "Bicol",
                        "450 km",
                        "9h 00m",
                        "₱850",
                        "Active"));

        routes.add(
                new Route(
                        "RT-004",
                        "Manila",
                        "Calapan",
                        "160 km",
                        "4h 00m",
                        "₱400",
                        "Inactive"));

        routes.add(
                new Route(
                        "RT-005",
                        "Manila",
                        "Naga",
                        "380 km",
                        "7h 30m",
                        "₱720",
                        "Active"));
    }

    private void initializeUI() {

        setOpaque(true);
        setBackground(BussinTheme.BACKGROUND);

        setLayout(new BorderLayout());

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
                createRouteListCard());

        main.add(
                createRouteDetailsCard());

        return main;
    }

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

        JScrollPane tableScroll = new JScrollPane(
                routeTable);

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
                "Search route ID, origin, or destination");

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
                                case 3 -> 85;
                                case 4 -> 85;
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

        JLabel idTitle = new JLabel("ROUTE ID");

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

        details.add(createSeparator());

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

        toggleStatusButton.setEnabled(false);
        deleteButton.setEnabled(false);

        toggleStatusButton.addActionListener(
                event -> toggleRouteStatus());

        deleteButton.addActionListener(
                event -> deleteSelectedRoute());

        actions.add(toggleStatusButton);
        actions.add(deleteButton);

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

    private void refreshRoutes() {

        String query = searchField
                .getText()
                .trim()
                .toLowerCase();

        String selectedStatus = String.valueOf(
                statusFilter.getSelectedItem());

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
                            .contains(query);

            boolean matchesStatus = selectedStatus.equals("All Status")
                    || route.status.equals(
                            selectedStatus);

            if (matchesSearch
                    && matchesStatus) {

                filteredRoutes.add(route);
            }
        }

        tableModel.fireTableDataChanged();

        updateStatistics();

        if (filteredRoutes.isEmpty()) {

            clearDetails();

        } else {

            routeTable.setRowSelectionInterval(
                    0,
                    0);
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

    private void updateSelectedRoute() {

        int selectedRow = routeTable.getSelectedRow();

        if (selectedRow < 0) {
            clearDetails();
            return;
        }

        int modelRow = routeTable.convertRowIndexToModel(
                selectedRow);

        if (modelRow < 0
                || modelRow >= filteredRoutes.size()) {

            clearDetails();
            return;
        }

        Route route = filteredRoutes.get(modelRow);

        detailRouteId.setText(
                route.routeId);

        detailOrigin.setText(
                route.origin);

        detailDestination.setText(
                route.destination);

        detailDistance.setText(
                route.distance);

        detailDuration.setText(
                route.duration);

        detailFare.setText(
                route.fare);

        updateStatusBadge(
                route.status);

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

        updateStatusBadge("No Status");

        toggleStatusButton.setText(
                "Activate");

        toggleStatusButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }

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

        return filteredRoutes.get(modelRow);
    }

    private void toggleRouteStatus() {

        Route route = getSelectedRoute();

        if (route == null) {
            return;
        }

        if (route.status.equals("Active")) {
            route.status = "Inactive";
        } else {
            route.status = "Active";
        }

        refreshRoutes();
    }

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
                        + ")?",
                "Delete Route",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {

            routes.remove(route);

            refreshRoutes();
        }
    }

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
                "Distance",
                distanceField);

        addFormField(
                form,
                "Travel Time",
                durationField);

        addFormField(
                form,
                "Base Fare",
                fareField);

        JLabel note = new JLabel(
                "New routes start as Active.");

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
                "Create Route");

        closeButton.addActionListener(
                event -> dialog.dispose());

        saveButton.addActionListener(
                event -> {

                    String origin = originField
                            .getText()
                            .trim();

                    String destination = destinationField
                            .getText()
                            .trim();

                    String distance = distanceField
                            .getText()
                            .trim();

                    String duration = durationField
                            .getText()
                            .trim();

                    String fare = fareField
                            .getText()
                            .trim();

                    if (origin.isEmpty()
                            || destination.isEmpty()
                            || distance.isEmpty()
                            || duration.isEmpty()
                            || fare.isEmpty()) {

                        JOptionPane.showMessageDialog(
                                dialog,
                                "Please complete all required fields.",
                                "Invalid Route",
                                JOptionPane.WARNING_MESSAGE);

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
                // Ignore unexpected mock IDs.
            }
        }

        return String.format(
                "RT-%03d",
                highest + 1);
    }

    private static class Route {

        private final String routeId;
        private final String origin;
        private final String destination;
        private final String distance;
        private final String duration;
        private final String fare;

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

            Route route = filteredRoutes.get(row);

            return switch (column) {

                case 0 -> route.routeId;
                case 1 -> route.origin;
                case 2 -> route.destination;
                case 3 -> route.distance;
                case 4 -> route.duration;
                case 5 -> route.fare;
                case 6 -> route.status;

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