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
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import com.bussin.desktop.services.BusApiService;
import com.bussin.desktop.ui.components.AppBadge;
import com.bussin.desktop.ui.components.AppButton;
import com.bussin.desktop.ui.components.AppCard;
import com.bussin.desktop.ui.components.AppLabel;
import com.bussin.desktop.ui.components.IconFactory;
import com.bussin.desktop.ui.theme.BussinTheme;

public class BusScreen extends JPanel {

        private final List<Bus> buses = new ArrayList<>();
        private final List<Bus> filteredBuses = new ArrayList<>();

        private final BusTableModel tableModel = new BusTableModel();

        private final JTable busTable = new JTable(tableModel);

        private final JTextField searchField = new JTextField();

        private final JComboBox<String> statusFilter = new JComboBox<>(
                        new String[] {
                                        "All Status",
                                        "Available",
                                        "Maintenance",
                                        "Inactive"
                        });

        private final JLabel totalValue = new JLabel("0");
        private final JLabel availableValue = new JLabel("0");
        private final JLabel assignedValue = new JLabel("0");
        private final JLabel maintenanceValue = new JLabel("0");

        private final JLabel detailBusNumber = new JLabel("-");
        private final JLabel detailPlateNumber = new JLabel("-");
        private final JLabel detailType = new JLabel("-");
        private final JLabel detailCapacity = new JLabel("-");
        private final JLabel detailDriver = new JLabel("-");
        private final JLabel detailTrip = new JLabel("-");

        private final AppBadge detailStatus = new AppBadge(
                        "NO STATUS",
                        AppBadge.Status.NEUTRAL);

        private final AppButton editButton = new AppButton(
                        "Edit",
                        AppButton.Variant.SECONDARY);

        private final AppButton availableButton = new AppButton(
                        "Set Available");

        private final AppButton maintenanceButton = new AppButton(
                        "Maintenance",
                        AppButton.Variant.SECONDARY);

        private final AppButton deactivateButton = new AppButton(
                        "Deactivate",
                        AppButton.Variant.DANGER);

        public BusScreen() {

                initializeUI();
                refreshBuses();
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
                                                "Bus Management"));

                titlePanel.add(
                                Box.createVerticalStrut(5));

                titlePanel.add(
                                AppLabel.secondary(
                                                "Manage BUSSIN vehicles, capacity, and operational status."));

                AppButton createButton = new AppButton(
                                "Add Bus");

                createButton.setIcon(
                                IconFactory.create(
                                                "plus",
                                                16,
                                                Color.WHITE));

                createButton.addActionListener(
                                event -> showCreateBusDialog());

                JPanel actions = new JPanel(
                                new FlowLayout(
                                                FlowLayout.RIGHT,
                                                0,
                                                0));

                actions.setOpaque(false);

                actions.add(createButton);

                header.add(
                                titlePanel,
                                BorderLayout.WEST);

                header.add(
                                actions,
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
                                                "TOTAL BUSES",
                                                totalValue,
                                                BussinTheme.CHARCOAL));

                statistics.add(
                                createStatCard(
                                                "AVAILABLE",
                                                availableValue,
                                                BussinTheme.SUCCESS));

                statistics.add(
                                createStatCard(
                                                "ASSIGNED",
                                                assignedValue,
                                                BussinTheme.RED));

                statistics.add(
                                createStatCard(
                                                "MAINTENANCE",
                                                maintenanceValue,
                                                BussinTheme.WARNING));

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

        private JPanel createMainSection() {

                JPanel main = new JPanel(
                                new GridLayout(
                                                1,
                                                2,
                                                18,
                                                0));

                main.setOpaque(false);

                main.add(
                                createBusListCard());

                main.add(
                                createBusDetailsCard());

                return main;
        }

        private AppCard createBusListCard() {

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
                                                "Bus Fleet"),
                                BorderLayout.WEST);

                JLabel hint = new JLabel(
                                "Select a bus to view its details");

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

                JScrollPane tableScroll = new JScrollPane(busTable);

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
                                "Search bus number, plate, or bus type");

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
                                                                        DocumentEvent event) {
                                                                applyFilters(
                                                                                getSelectedBus());
                                                        }

                                                        @Override
                                                        public void removeUpdate(
                                                                        DocumentEvent event) {
                                                                applyFilters(
                                                                                getSelectedBus());
                                                        }

                                                        @Override
                                                        public void changedUpdate(
                                                                        DocumentEvent event) {
                                                                applyFilters(
                                                                                getSelectedBus());
                                                        }
                                                });

                statusFilter.addActionListener(
                                event -> applyFilters(
                                                getSelectedBus()));

                return filters;
        }

        private void configureTable() {

                busTable.setRowHeight(42);

                busTable.setFont(
                                BussinTheme.SMALL);

                busTable.setSelectionMode(
                                ListSelectionModel.SINGLE_SELECTION);

                busTable.setAutoCreateRowSorter(true);

                busTable.setShowVerticalLines(false);

                busTable.setShowHorizontalLines(true);

                busTable.setGridColor(
                                BussinTheme.BORDER);

                busTable.setSelectionBackground(
                                BussinTheme.PRIMARY_LIGHT);

                busTable.setSelectionForeground(
                                BussinTheme.TEXT_PRIMARY);

                busTable.setFillsViewportHeight(true);

                busTable.getTableHeader()
                                .setFont(
                                                BussinTheme.SMALL_BOLD);

                busTable.getTableHeader()
                                .setBackground(
                                                BussinTheme.SURFACE_ALT);

                busTable.getTableHeader()
                                .setForeground(
                                                BussinTheme.TEXT_SECONDARY);

                for (int i = 0; i < 6; i++) {

                        busTable.getColumnModel()
                                        .getColumn(i)
                                        .setPreferredWidth(
                                                        switch (i) {

                                                                case 0 -> 85;
                                                                case 1 -> 110;
                                                                case 2 -> 140;
                                                                case 3 -> 70;
                                                                case 4 -> 120;
                                                                case 5 -> 100;

                                                                default -> 90;
                                                        });
                }

                busTable.getColumnModel()
                                .getColumn(5)
                                .setCellRenderer(
                                                new StatusCellRenderer());

                busTable.getSelectionModel()
                                .addListSelectionListener(
                                                event -> {

                                                        if (!event.getValueIsAdjusting()) {
                                                                updateSelectedBus();
                                                        }
                                                });
        }

        private AppCard createBusDetailsCard() {

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
                                                "Bus Details"),
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

                addDetail(
                                details,
                                "BUS NUMBER",
                                detailBusNumber);

                addDetail(
                                details,
                                "PLATE NUMBER",
                                detailPlateNumber);

                addDetail(
                                details,
                                "BUS TYPE",
                                detailType);

                addDetail(
                                details,
                                "CAPACITY",
                                detailCapacity);

                addDetail(
                                details,
                                "ASSIGNED DRIVER",
                                detailDriver);

                addDetail(
                                details,
                                "ASSIGNED TRIP",
                                detailTrip);

                details.add(
                                Box.createVerticalStrut(8));

                JPanel actions = new JPanel(
                                new FlowLayout(
                                                FlowLayout.LEFT,
                                                8,
                                                0));

                actions.setOpaque(false);

                editButton.addActionListener(
                                event -> showEditBusDialog());

                availableButton.addActionListener(
                                event -> changeBusStatus(
                                                "ACTIVE"));

                maintenanceButton.addActionListener(
                                event -> changeBusStatus(
                                                "MAINTENANCE"));

                deactivateButton.addActionListener(
                                event -> confirmDeactivateBus());

                editButton.setEnabled(false);
                availableButton.setEnabled(false);
                maintenanceButton.setEnabled(false);
                deactivateButton.setEnabled(false);

                actions.add(editButton);
                actions.add(availableButton);
                actions.add(maintenanceButton);
                actions.add(deactivateButton);

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

        private void refreshBuses() {

                Bus previouslySelected = getSelectedBus();

                try {

                        List<BusApiService.BusResponse> responses = BusApiService.getAllBuses();

                        buses.clear();

                        for (BusApiService.BusResponse response : responses) {

                                buses.add(
                                                new Bus(
                                                                response.getId(),
                                                                response.getPlateNumber(),
                                                                response.getModel() == null
                                                                                || response.getModel().isBlank()
                                                                                                ? "-"
                                                                                                : response.getModel(),
                                                                response.getCapacity() == null
                                                                                ? 0
                                                                                : response.getCapacity(),
                                                                "Unassigned",
                                                                "-",
                                                                mapStatus(
                                                                                response.getStatus())));
                        }

                        applyFilters(previouslySelected);

                } catch (Exception exception) {

                        JOptionPane.showMessageDialog(
                                        this,
                                        "Unable to load buses.\n\n"
                                                        + exception.getMessage(),
                                        "Bus Management",
                                        JOptionPane.ERROR_MESSAGE);
                }
        }

        private void applyFilters(
                        Bus previouslySelected) {

                String query = searchField.getText()
                                .trim()
                                .toLowerCase();

                String selectedStatus = String.valueOf(
                                statusFilter.getSelectedItem());

                filteredBuses.clear();

                for (Bus bus : buses) {

                        boolean matchesSearch = query.isEmpty()
                                        || String.valueOf(
                                                        bus.id)
                                                        .contains(query)
                                        || bus.plateNumber
                                                        .toLowerCase()
                                                        .contains(query)
                                        || bus.type
                                                        .toLowerCase()
                                                        .contains(query);

                        boolean matchesStatus = selectedStatus.equals(
                                        "All Status")
                                        || bus.status.equals(
                                                        selectedStatus);

                        if (matchesSearch
                                        && matchesStatus) {

                                filteredBuses.add(bus);
                        }
                }

                tableModel.fireTableDataChanged();

                updateStatistics();

                if (previouslySelected != null
                                && filteredBuses.contains(
                                                previouslySelected)) {

                        int index = filteredBuses.indexOf(
                                        previouslySelected);

                        if (index >= 0
                                        && index < busTable.getRowCount()) {

                                busTable.setRowSelectionInterval(
                                                index,
                                                index);

                                return;
                        }
                }

                if (!filteredBuses.isEmpty()) {

                        busTable.setRowSelectionInterval(
                                        0,
                                        0);

                } else {

                        clearDetails();
                }
        }

        private String mapStatus(
                        String status) {

                if (status == null) {
                        return "Inactive";
                }

                return switch (status.toUpperCase()) {

                        case "ACTIVE" ->
                                "Available";

                        case "MAINTENANCE" ->
                                "Maintenance";

                        case "OUT_OF_SERVICE" ->
                                "Inactive";

                        default ->
                                "Inactive";
                };
        }

        private String toApiStatus(
                        String status) {

                return switch (status) {

                        case "Available" ->
                                "ACTIVE";

                        case "Maintenance" ->
                                "MAINTENANCE";

                        case "Inactive" ->
                                "OUT_OF_SERVICE";

                        default ->
                                "ACTIVE";
                };
        }

        private void updateStatistics() {

                int available = 0;
                int assigned = 0;
                int maintenance = 0;

                for (Bus bus : buses) {

                        switch (bus.status) {

                                case "Available" ->
                                        available++;

                                case "Assigned",
                                                "Boarding" ->
                                        assigned++;

                                case "Maintenance" ->
                                        maintenance++;

                                default -> {
                                }
                        }
                }

                totalValue.setText(
                                String.valueOf(
                                                buses.size()));

                availableValue.setText(
                                String.valueOf(
                                                available));

                assignedValue.setText(
                                String.valueOf(
                                                assigned));

                maintenanceValue.setText(
                                String.valueOf(
                                                maintenance));
        }

        private void updateSelectedBus() {

                Bus bus = getSelectedBus();

                if (bus == null) {

                        clearDetails();

                        return;
                }

                detailBusNumber.setText(
                                "BUS-" + bus.id);

                detailPlateNumber.setText(
                                bus.plateNumber);

                detailType.setText(
                                bus.type);

                detailCapacity.setText(
                                String.valueOf(
                                                bus.capacity));

                detailDriver.setText(
                                bus.driver);

                detailTrip.setText(
                                bus.trip);

                updateStatusBadge(
                                bus.status);

                updateActionButtons(
                                bus);
        }

        private void updateActionButtons(
                        Bus bus) {

                boolean inactive = "Inactive".equals(
                                bus.status);

                boolean available = "Available".equals(
                                bus.status);

                boolean maintenance = "Maintenance".equals(
                                bus.status);

                editButton.setEnabled(
                                !inactive);

                availableButton.setEnabled(
                                !inactive
                                                && !available);

                maintenanceButton.setEnabled(
                                !inactive
                                                && !maintenance);

                deactivateButton.setEnabled(
                                !inactive);
        }

        private void clearDetails() {

                detailBusNumber.setText("-");
                detailPlateNumber.setText("-");
                detailType.setText("-");
                detailCapacity.setText("-");
                detailDriver.setText("-");
                detailTrip.setText("-");

                updateStatusBadge(
                                "No Status");

                editButton.setEnabled(false);
                availableButton.setEnabled(false);
                maintenanceButton.setEnabled(false);
                deactivateButton.setEnabled(false);
        }

        private void changeBusStatus(
                        String apiStatus) {

                Bus bus = getSelectedBus();

                if (bus == null) {
                        return;
                }

                try {

                        BusApiService.updateBus(
                                        bus.id,
                                        bus.plateNumber,
                                        "-".equals(bus.type)
                                                        ? null
                                                        : bus.type,
                                        bus.capacity,
                                        apiStatus);

                        refreshBuses();

                } catch (Exception exception) {

                        JOptionPane.showMessageDialog(
                                        this,
                                        "Unable to update bus status.\n\n"
                                                        + exception.getMessage(),
                                        "Bus Management",
                                        JOptionPane.ERROR_MESSAGE);
                }
        }

        private void confirmDeactivateBus() {

                Bus bus = getSelectedBus();

                if (bus == null) {
                        return;
                }

                int result = JOptionPane.showConfirmDialog(
                                this,
                                "Deactivate BUS-"
                                                + bus.id
                                                + "?\n\n"
                                                + "The bus will be marked as out of service "
                                                + "and will no longer be available for normal operations.",
                                "Deactivate Bus",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE);

                if (result != JOptionPane.YES_OPTION) {
                        return;
                }

                try {

                        BusApiService.updateBus(
                                        bus.id,
                                        bus.plateNumber,
                                        "-".equals(bus.type)
                                                        ? null
                                                        : bus.type,
                                        bus.capacity,
                                        "OUT_OF_SERVICE");

                        refreshBuses();

                } catch (Exception exception) {

                        JOptionPane.showMessageDialog(
                                        this,
                                        "Unable to deactivate bus.\n\n"
                                                        + exception.getMessage(),
                                        "Bus Management",
                                        JOptionPane.ERROR_MESSAGE);
                }
        }

        private Bus getSelectedBus() {

                int selectedRow = busTable.getSelectedRow();

                if (selectedRow < 0) {
                        return null;
                }

                int modelRow = busTable.convertRowIndexToModel(
                                selectedRow);

                if (modelRow < 0
                                || modelRow >= filteredBuses.size()) {

                        return null;
                }

                return filteredBuses.get(
                                modelRow);
        }

        private void updateStatusBadge(
                        String status) {

                AppBadge.Status badgeStatus;

                switch (status) {

                        case "Available" ->
                                badgeStatus = AppBadge.Status.SUCCESS;

                        case "Assigned",
                                        "Boarding" ->
                                badgeStatus = AppBadge.Status.INFO;

                        case "Maintenance" ->
                                badgeStatus = AppBadge.Status.WARNING;

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

                        case INFO -> {

                                detailStatus.setForeground(
                                                BussinTheme.TEXT_PRIMARY);

                                detailStatus.setBackground(
                                                BussinTheme.INFO_LIGHT);
                        }

                        case WARNING -> {

                                detailStatus.setForeground(
                                                BussinTheme.WARNING);

                                detailStatus.setBackground(
                                                BussinTheme.WARNING_LIGHT);
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

        private void showCreateBusDialog() {

                JDialog dialog = new JDialog(
                                SwingUtilities
                                                .getWindowAncestor(this),
                                "Add Bus",
                                Dialog.ModalityType.APPLICATION_MODAL);

                dialog.setDefaultCloseOperation(
                                JDialog.DISPOSE_ON_CLOSE);

                dialog.setSize(
                                500,
                                350);

                dialog.setLocationRelativeTo(
                                this);

                JPanel root = new JPanel(
                                new BorderLayout());

                root.setBackground(
                                BussinTheme.BACKGROUND);

                JLabel title = new JLabel(
                                "Add New Bus");

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
                                                3,
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

                JTextField plateField = new JTextField();
                JTextField typeField = new JTextField();
                JTextField capacityField = new JTextField();

                addFormField(
                                form,
                                "Plate Number",
                                plateField);

                addFormField(
                                form,
                                "Bus Type",
                                typeField);

                addFormField(
                                form,
                                "Capacity",
                                capacityField);

                JPanel buttons = new JPanel(
                                new FlowLayout(
                                                FlowLayout.RIGHT,
                                                8,
                                                12));

                buttons.setOpaque(false);

                AppButton cancel = new AppButton(
                                "Cancel",
                                AppButton.Variant.SECONDARY);

                AppButton save = new AppButton(
                                "Add Bus");

                cancel.addActionListener(
                                event -> dialog.dispose());

                save.addActionListener(
                                event -> {

                                        String plate = plateField.getText()
                                                        .trim();

                                        String type = typeField.getText()
                                                        .trim();

                                        String capacityText = capacityField.getText()
                                                        .trim();

                                        if (plate.isEmpty()
                                                        || capacityText.isEmpty()) {

                                                showWarning(
                                                                dialog,
                                                                "Plate number and capacity are required.",
                                                                "Invalid Bus");

                                                return;
                                        }

                                        int capacity;

                                        try {

                                                capacity = Integer.parseInt(
                                                                capacityText);

                                                if (capacity <= 0) {
                                                        throw new NumberFormatException();
                                                }

                                        } catch (NumberFormatException exception) {

                                                showWarning(
                                                                dialog,
                                                                "Capacity must be a valid positive number.",
                                                                "Invalid Capacity");

                                                return;
                                        }

                                        try {

                                                BusApiService.createBus(
                                                                plate,
                                                                type.isEmpty()
                                                                                ? null
                                                                                : type,
                                                                capacity);

                                                refreshBuses();

                                                dialog.dispose();

                                        } catch (Exception exception) {

                                                showWarning(
                                                                dialog,
                                                                "Unable to create bus.\n\n"
                                                                                + exception.getMessage(),
                                                                "Create Bus Failed");
                                        }
                                });

                buttons.add(cancel);
                buttons.add(save);

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

        private void showEditBusDialog() {

                Bus bus = getSelectedBus();

                if (bus == null) {
                        return;
                }

                if ("Inactive".equals(
                                bus.status)) {

                        showWarning(
                                        this,
                                        "Inactive buses cannot be edited.",
                                        "Bus Management");

                        return;
                }

                JDialog dialog = new JDialog(
                                SwingUtilities
                                                .getWindowAncestor(this),
                                "Edit Bus",
                                Dialog.ModalityType.APPLICATION_MODAL);

                dialog.setDefaultCloseOperation(
                                JDialog.DISPOSE_ON_CLOSE);

                dialog.setSize(
                                500,
                                350);

                dialog.setLocationRelativeTo(
                                this);

                JPanel root = new JPanel(
                                new BorderLayout());

                root.setBackground(
                                BussinTheme.BACKGROUND);

                JLabel title = new JLabel(
                                "Edit Bus");

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
                                                3,
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

                JTextField plateField = new JTextField(
                                bus.plateNumber);

                JTextField typeField = new JTextField(
                                "-".equals(bus.type)
                                                ? ""
                                                : bus.type);

                JTextField capacityField = new JTextField(
                                String.valueOf(
                                                bus.capacity));

                addFormField(
                                form,
                                "Plate Number",
                                plateField);

                addFormField(
                                form,
                                "Bus Type",
                                typeField);

                addFormField(
                                form,
                                "Capacity",
                                capacityField);

                JPanel buttons = new JPanel(
                                new FlowLayout(
                                                FlowLayout.RIGHT,
                                                8,
                                                12));

                buttons.setOpaque(false);

                AppButton cancel = new AppButton(
                                "Cancel",
                                AppButton.Variant.SECONDARY);

                AppButton save = new AppButton(
                                "Save Changes");

                cancel.addActionListener(
                                event -> dialog.dispose());

                save.addActionListener(
                                event -> {

                                        String plate = plateField.getText()
                                                        .trim();

                                        String type = typeField.getText()
                                                        .trim();

                                        String capacityText = capacityField.getText()
                                                        .trim();

                                        if (plate.isEmpty()
                                                        || capacityText.isEmpty()) {

                                                showWarning(
                                                                dialog,
                                                                "Plate number and capacity are required.",
                                                                "Invalid Bus");

                                                return;
                                        }

                                        int capacity;

                                        try {

                                                capacity = Integer.parseInt(
                                                                capacityText);

                                                if (capacity <= 0) {
                                                        throw new NumberFormatException();
                                                }

                                        } catch (NumberFormatException exception) {

                                                showWarning(
                                                                dialog,
                                                                "Capacity must be a valid positive number.",
                                                                "Invalid Capacity");

                                                return;
                                        }

                                        try {

                                                BusApiService.updateBus(
                                                                bus.id,
                                                                plate,
                                                                type.isEmpty()
                                                                                ? null
                                                                                : type,
                                                                capacity,
                                                                toApiStatus(
                                                                                bus.status));

                                                refreshBuses();

                                                dialog.dispose();

                                        } catch (Exception exception) {

                                                showWarning(
                                                                dialog,
                                                                "Unable to update bus.\n\n"
                                                                                + exception.getMessage(),
                                                                "Update Bus Failed");
                                        }
                                });

                buttons.add(cancel);
                buttons.add(save);

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

        private static class Bus {

                private final long id;

                private String plateNumber;
                private String type;
                private int capacity;
                private final String driver;
                private final String trip;
                private String status;

                private Bus(
                                long id,
                                String plateNumber,
                                String type,
                                int capacity,
                                String driver,
                                String trip,
                                String status) {

                        this.id = id;
                        this.plateNumber = plateNumber;
                        this.type = type;
                        this.capacity = capacity;
                        this.driver = driver;
                        this.trip = trip;
                        this.status = status;
                }
        }

        private class BusTableModel
                        extends AbstractTableModel {

                private final String[] columns = {
                                "Bus",
                                "Plate",
                                "Type",
                                "Capacity",
                                "Driver",
                                "Status"
                };

                @Override
                public int getRowCount() {

                        return filteredBuses.size();
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

                        Bus bus = filteredBuses.get(
                                        row);

                        return switch (column) {

                                case 0 ->
                                        "BUS-" + bus.id;

                                case 1 ->
                                        bus.plateNumber;

                                case 2 ->
                                        bus.type;

                                case 3 ->
                                        bus.capacity;

                                case 4 ->
                                        bus.driver;

                                case 5 ->
                                        bus.status;

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

                                case "Available" ->
                                        badgeStatus = AppBadge.Status.SUCCESS;

                                case "Assigned",
                                                "Boarding" ->
                                        badgeStatus = AppBadge.Status.INFO;

                                case "Maintenance" ->
                                        badgeStatus = AppBadge.Status.WARNING;

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