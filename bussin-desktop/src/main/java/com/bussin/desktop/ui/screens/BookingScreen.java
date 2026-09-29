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

public class BookingScreen extends JPanel {

    private final List<Booking> bookings = new ArrayList<>();
    private final List<Booking> filteredBookings = new ArrayList<>();

    private final BookingTableModel tableModel = new BookingTableModel();

    private final JTable bookingTable = new JTable(tableModel);

    private final JTextField searchField = new JTextField();

    private final JComboBox<String> statusFilter = new JComboBox<>(
            new String[] {
                    "All Status",
                    "Confirmed",
                    "Pending",
                    "Cancelled"
            });

    private final JLabel totalValue = new JLabel("0");
    private final JLabel confirmedValue = new JLabel("0");
    private final JLabel pendingValue = new JLabel("0");
    private final JLabel cancelledValue = new JLabel("0");

    private final JLabel detailBookingId = new JLabel("-");
    private final JLabel detailPassenger = new JLabel("-");
    private final JLabel detailTrip = new JLabel("-");
    private final JLabel detailRoute = new JLabel("-");
    private final JLabel detailSeat = new JLabel("-");
    private final JLabel detailFare = new JLabel("-");
    private final JLabel detailPayment = new JLabel("-");
    private final JLabel detailCreated = new JLabel("-");

    private final AppBadge detailStatus = new AppBadge(
            "NO STATUS",
            AppBadge.Status.NEUTRAL);

    private final AppButton cancelButton = new AppButton(
            "Cancel Booking",
            AppButton.Variant.DANGER);

    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern(
            "MMM dd, yyyy • hh:mm a");

    public BookingScreen() {

        initializeData();
        initializeUI();
        refreshBookings();
    }

    private void initializeData() {

        bookings.add(
                new Booking(
                        "BK-1001",
                        "Juan Dela Cruz",
                        "Trip 102 • 09:30 AM",
                        "Manila → Batangas",
                        "12A",
                        450.00,
                        "Paid",
                        "Confirmed"));

        bookings.add(
                new Booking(
                        "BK-1002",
                        "Maria Santos",
                        "Trip 102 • 09:30 AM",
                        "Manila → Batangas",
                        "12B",
                        450.00,
                        "Paid",
                        "Confirmed"));

        bookings.add(
                new Booking(
                        "BK-1003",
                        "Carlo Reyes",
                        "Trip 114 • 10:00 AM",
                        "Manila → Lucena",
                        "08A",
                        520.00,
                        "Unpaid",
                        "Pending"));

        bookings.add(
                new Booking(
                        "BK-1004",
                        "Angela Cruz",
                        "Trip 114 • 10:00 AM",
                        "Manila → Lucena",
                        "08B",
                        520.00,
                        "Paid",
                        "Confirmed"));

        bookings.add(
                new Booking(
                        "BK-1005",
                        "Pedro Garcia",
                        "Trip 121 • 11:30 AM",
                        "Manila → Bicol",
                        "22A",
                        680.00,
                        "Paid",
                        "Cancelled"));

        bookings.add(
                new Booking(
                        "BK-1006",
                        "Sofia Ramos",
                        "Trip 121 • 11:30 AM",
                        "Manila → Bicol",
                        "22B",
                        680.00,
                        "Paid",
                        "Confirmed"));
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

        content.add(
                Box.createVerticalGlue());

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
                        "Booking Management"));

        titlePanel.add(
                Box.createVerticalStrut(5));

        titlePanel.add(
                AppLabel.secondary(
                        "Manage passenger bookings, trip assignments, seats, and payment status."));

        AppButton createButton = new AppButton(
                "Create Booking");

        createButton.setIcon(
                IconFactory.create(
                        "plus",
                        16,
                        Color.WHITE));

        createButton.addActionListener(
                event -> showCreateBookingDialog());

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
                        "TOTAL BOOKINGS",
                        totalValue,
                        BussinTheme.CHARCOAL));

        statistics.add(
                createStatCard(
                        "CONFIRMED",
                        confirmedValue,
                        BussinTheme.SUCCESS));

        statistics.add(
                createStatCard(
                        "PENDING",
                        pendingValue,
                        BussinTheme.WARNING));

        statistics.add(
                createStatCard(
                        "CANCELLED",
                        cancelledValue,
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
                createBookingListCard());

        main.add(
                createBookingDetailsCard());

        return main;
    }

    private AppCard createBookingListCard() {

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
                        "Bookings"),
                BorderLayout.WEST);

        JLabel hint = new JLabel(
                "Select a booking to view details");

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

        JPanel filters = createFilterBar();

        JPanel center = new JPanel(
                new BorderLayout(
                        0,
                        10));

        center.setOpaque(false);

        center.add(
                filters,
                BorderLayout.NORTH);

        configureTable();

        JScrollPane tableScroll = new JScrollPane(
                bookingTable);

        tableScroll.setBorder(
                BorderFactory.createLineBorder(
                        BussinTheme.BORDER));

        tableScroll.getVerticalScrollBar()
                .setUnitIncrement(12);

        center.add(
                tableScroll,
                BorderLayout.CENTER);

        JPanel actions = new JPanel(
                new FlowLayout(
                        FlowLayout.LEFT,
                        8,
                        0));

        actions.setOpaque(false);

        cancelButton.setEnabled(false);

        cancelButton.addActionListener(
                event -> cancelSelectedBooking());

        actions.add(cancelButton);

        center.add(
                actions,
                BorderLayout.SOUTH);

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
                        240,
                        40));

        searchField.setToolTipText(
                "Search booking ID, passenger, route, or seat");

        statusFilter.setPreferredSize(
                new Dimension(
                        140,
                        40));

        filters.add(
                createSearchField(),
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
                                refreshBookings();
                            }

                            @Override
                            public void removeUpdate(
                                    DocumentEvent event) {
                                refreshBookings();
                            }

                            @Override
                            public void changedUpdate(
                                    DocumentEvent event) {
                                refreshBookings();
                            }
                        });

        statusFilter.addActionListener(
                event -> refreshBookings());

        return filters;
    }

    private JPanel createSearchField() {

        JPanel panel = new JPanel(
                new BorderLayout());

        panel.setOpaque(false);

        JLabel icon = new JLabel(
                IconFactory.create(
                        "dashboard",
                        16,
                        BussinTheme.MUTED_SILVER));

        icon.setBorder(
                BorderFactory.createEmptyBorder(
                        0,
                        12,
                        0,
                        6));

        searchField.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BussinTheme.BORDER),
                        BorderFactory.createEmptyBorder(
                                0,
                                6,
                                0,
                                10)));

        panel.add(
                icon,
                BorderLayout.WEST);

        panel.add(
                searchField,
                BorderLayout.CENTER);

        return panel;
    }

    private void configureTable() {

        bookingTable.setRowHeight(42);

        bookingTable.setFont(
                BussinTheme.SMALL);

        bookingTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);

        bookingTable.setAutoCreateRowSorter(true);

        bookingTable.setShowVerticalLines(false);
        bookingTable.setShowHorizontalLines(true);

        bookingTable.setGridColor(
                BussinTheme.BORDER);

        bookingTable.setSelectionBackground(
                BussinTheme.PRIMARY_LIGHT);

        bookingTable.setSelectionForeground(
                BussinTheme.TEXT_PRIMARY);

        bookingTable.setFillsViewportHeight(true);

        bookingTable.getTableHeader()
                .setFont(
                        BussinTheme.SMALL_BOLD);

        bookingTable.getTableHeader()
                .setForeground(
                        BussinTheme.TEXT_SECONDARY);

        bookingTable.getTableHeader()
                .setBackground(
                        BussinTheme.SURFACE_ALT);

        bookingTable.getColumnModel()
                .getColumn(0)
                .setPreferredWidth(85);

        bookingTable.getColumnModel()
                .getColumn(1)
                .setPreferredWidth(130);

        bookingTable.getColumnModel()
                .getColumn(2)
                .setPreferredWidth(145);

        bookingTable.getColumnModel()
                .getColumn(3)
                .setPreferredWidth(160);

        bookingTable.getColumnModel()
                .getColumn(4)
                .setPreferredWidth(55);

        bookingTable.getColumnModel()
                .getColumn(5)
                .setPreferredWidth(80);

        bookingTable.getColumnModel()
                .getColumn(6)
                .setPreferredWidth(100);

        bookingTable.getColumnModel()
                .getColumn(6)
                .setCellRenderer(
                        new StatusCellRenderer());

        bookingTable
                .getSelectionModel()
                .addListSelectionListener(
                        event -> {

                            if (!event.getValueIsAdjusting()) {
                                updateSelectedBooking();
                            }
                        });
    }

    private AppCard createBookingDetailsCard() {

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
                        "Booking Details"),
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

        JLabel bookingIdTitle = createDetailTitle(
                "BOOKING ID");

        styleDetailValue(
                detailBookingId,
                true);

        details.add(bookingIdTitle);
        details.add(
                Box.createVerticalStrut(3));
        details.add(detailBookingId);

        details.add(
                Box.createVerticalStrut(16));

        details.add(
                createSeparator());

        details.add(
                Box.createVerticalStrut(14));

        addDetail(
                details,
                "PASSENGER",
                detailPassenger);

        addDetail(
                details,
                "TRIP",
                detailTrip);

        addDetail(
                details,
                "ROUTE",
                detailRoute);

        addDetail(
                details,
                "SEAT",
                detailSeat);

        addDetail(
                details,
                "FARE",
                detailFare);

        addDetail(
                details,
                "PAYMENT",
                detailPayment);

        addDetail(
                details,
                "CREATED",
                detailCreated);

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

    private JLabel createDetailTitle(
            String text) {

        JLabel label = new JLabel(text);

        label.setFont(
                BussinTheme.SMALL_BOLD);

        label.setForeground(
                BussinTheme.TEXT_SECONDARY);

        return label;
    }

    private void addDetail(
            JPanel panel,
            String title,
            JLabel value) {

        JLabel titleLabel = createDetailTitle(
                title);

        styleDetailValue(
                value,
                false);

        panel.add(titleLabel);

        panel.add(
                Box.createVerticalStrut(3));

        panel.add(value);

        panel.add(
                Box.createVerticalStrut(13));
    }

    private void styleDetailValue(
            JLabel label,
            boolean emphasized) {

        label.setFont(
                emphasized
                        ? BussinTheme.SECTION_TITLE
                        : BussinTheme.BODY);

        label.setForeground(
                BussinTheme.TEXT_PRIMARY);
    }

    private JSeparator createSeparator() {

        JSeparator separator = new JSeparator();

        separator.setForeground(
                BussinTheme.BORDER);

        return separator;
    }

    private void refreshBookings() {

        String query = searchField
                .getText()
                .trim()
                .toLowerCase();

        String selectedStatus = String.valueOf(
                statusFilter.getSelectedItem());

        filteredBookings.clear();

        for (Booking booking : bookings) {

            boolean matchesSearch = query.isEmpty()
                    || booking.bookingId
                            .toLowerCase()
                            .contains(query)
                    || booking.passenger
                            .toLowerCase()
                            .contains(query)
                    || booking.route
                            .toLowerCase()
                            .contains(query)
                    || booking.trip
                            .toLowerCase()
                            .contains(query)
                    || booking.seat
                            .toLowerCase()
                            .contains(query);

            boolean matchesStatus = selectedStatus.equals(
                    "All Status")
                    || booking.status.equals(
                            selectedStatus);

            if (matchesSearch && matchesStatus) {
                filteredBookings.add(
                        booking);
            }
        }

        tableModel.fireTableDataChanged();

        updateStatistics();

        if (filteredBookings.isEmpty()) {

            clearDetails();

        } else if (bookingTable.getSelectedRow() == -1) {

            bookingTable.setRowSelectionInterval(
                    0,
                    0);
        }
    }

    private void updateStatistics() {

        int total = bookings.size();
        int confirmed = 0;
        int pending = 0;
        int cancelled = 0;

        for (Booking booking : bookings) {

            switch (booking.status) {

                case "Confirmed" -> confirmed++;

                case "Pending" -> pending++;

                case "Cancelled" -> cancelled++;
            }
        }

        totalValue.setText(
                String.valueOf(total));

        confirmedValue.setText(
                String.valueOf(confirmed));

        pendingValue.setText(
                String.valueOf(pending));

        cancelledValue.setText(
                String.valueOf(cancelled));
    }

    private void updateSelectedBooking() {

        int selectedRow = bookingTable.getSelectedRow();

        if (selectedRow < 0) {
            clearDetails();
            return;
        }

        int modelRow = bookingTable.convertRowIndexToModel(
                selectedRow);

        if (modelRow < 0
                || modelRow >= filteredBookings.size()) {

            clearDetails();
            return;
        }

        Booking booking = filteredBookings.get(
                modelRow);

        detailBookingId.setText(
                booking.bookingId);

        detailPassenger.setText(
                booking.passenger);

        detailTrip.setText(
                booking.trip);

        detailRoute.setText(
                booking.route);

        detailSeat.setText(
                booking.seat);

        detailFare.setText(
                String.format(
                        "₱%,.2f",
                        booking.fare));

        detailPayment.setText(
                booking.paymentStatus);

        detailCreated.setText(
                booking.createdAt.format(
                        DATE_TIME_FORMAT));

        updateStatusBadge(
                booking.status);

        cancelButton.setEnabled(
                !booking.status.equals(
                        "Cancelled"));
    }

    private void clearDetails() {

        detailBookingId.setText("-");
        detailPassenger.setText("-");
        detailTrip.setText("-");
        detailRoute.setText("-");
        detailSeat.setText("-");
        detailFare.setText("-");
        detailPayment.setText("-");
        detailCreated.setText("-");

        updateStatusBadge(
                "No Status");

        cancelButton.setEnabled(false);
    }

    private void updateStatusBadge(
            String status) {

        AppBadge.Status badgeStatus;

        switch (status) {

            case "Confirmed" ->
                badgeStatus = AppBadge.Status.SUCCESS;

            case "Pending" ->
                badgeStatus = AppBadge.Status.WARNING;

            case "Cancelled" ->
                badgeStatus = AppBadge.Status.DANGER;

            default ->
                badgeStatus = AppBadge.Status.NEUTRAL;
        }

        detailStatus.setText(
                status.toUpperCase());

        detailStatus.setForeground(
                switch (badgeStatus) {

                    case SUCCESS ->
                        BussinTheme.SUCCESS;

                    case WARNING ->
                        BussinTheme.WARNING;

                    case DANGER ->
                        BussinTheme.DANGER;

                    case INFO ->
                        BussinTheme.TEXT_PRIMARY;

                    case NEUTRAL ->
                        BussinTheme.TEXT_SECONDARY;
                });

        detailStatus.setBackground(
                switch (badgeStatus) {

                    case SUCCESS ->
                        BussinTheme.SUCCESS_LIGHT;

                    case WARNING ->
                        BussinTheme.WARNING_LIGHT;

                    case DANGER ->
                        BussinTheme.DANGER_LIGHT;

                    case INFO ->
                        BussinTheme.INFO_LIGHT;

                    case NEUTRAL ->
                        BussinTheme.SURFACE_ALT;
                });
    }

    private void cancelSelectedBooking() {

        int selectedRow = bookingTable.getSelectedRow();

        if (selectedRow < 0) {
            return;
        }

        int modelRow = bookingTable.convertRowIndexToModel(
                selectedRow);

        Booking booking = filteredBookings.get(
                modelRow);

        if (booking.status.equals(
                "Cancelled")) {
            return;
        }

        int result = JOptionPane.showConfirmDialog(
                this,
                "Cancel booking "
                        + booking.bookingId
                        + " for "
                        + booking.passenger
                        + "?",
                "Cancel Booking",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {

            booking.status = "Cancelled";

            refreshBookings();
        }
    }

    private void showCreateBookingDialog() {

        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(
                        this),
                "Create Booking",
                Dialog.ModalityType.APPLICATION_MODAL);

        dialog.setDefaultCloseOperation(
                JDialog.DISPOSE_ON_CLOSE);

        dialog.setMinimumSize(
                new Dimension(
                        540,
                        520));

        dialog.setSize(
                540,
                520);

        dialog.setLocationRelativeTo(this);

        JPanel root = new JPanel(
                new BorderLayout());

        root.setBackground(
                BussinTheme.BACKGROUND);

        JPanel content = new JPanel();

        content.setOpaque(false);

        content.setBorder(
                BorderFactory.createEmptyBorder(
                        24,
                        24,
                        10,
                        24));

        content.setLayout(
                new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(
                7,
                0,
                7,
                0);

        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.weightx = 1.0;

        JTextField passengerField = new JTextField();

        JComboBox<String> tripCombo = new JComboBox<>(
                new String[] {
                        "Trip 102 • 09:30 AM",
                        "Trip 114 • 10:00 AM",
                        "Trip 121 • 11:30 AM"
                });

        JComboBox<String> routeCombo = new JComboBox<>(
                new String[] {
                        "Manila → Batangas",
                        "Manila → Lucena",
                        "Manila → Bicol"
                });

        JTextField seatField = new JTextField();

        JTextField fareField = new JTextField();

        JComboBox<String> paymentCombo = new JComboBox<>(
                new String[] {
                        "Paid",
                        "Unpaid"
                });

        int row = 0;

        row = addFormRow(
                content,
                gbc,
                row,
                "Passenger Name",
                passengerField);

        row = addFormRow(
                content,
                gbc,
                row,
                "Trip",
                tripCombo);

        row = addFormRow(
                content,
                gbc,
                row,
                "Route",
                routeCombo);

        row = addFormRow(
                content,
                gbc,
                row,
                "Seat",
                seatField);

        row = addFormRow(
                content,
                gbc,
                row,
                "Fare",
                fareField);

        addFormRow(
                content,
                gbc,
                row,
                "Payment",
                paymentCombo);

        JLabel title = new JLabel(
                "Create New Booking");

        title.setFont(
                BussinTheme.SECTION_TITLE);

        title.setForeground(
                BussinTheme.TEXT_PRIMARY);

        JPanel titlePanel = new JPanel(
                new BorderLayout());

        titlePanel.setOpaque(false);

        titlePanel.setBorder(
                BorderFactory.createEmptyBorder(
                        24,
                        24,
                        0,
                        24));

        titlePanel.add(
                title,
                BorderLayout.WEST);

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
                "Create Booking");

        cancel.addActionListener(
                event -> dialog.dispose());

        save.addActionListener(
                event -> {

                    String passenger = passengerField
                            .getText()
                            .trim();

                    String seat = seatField
                            .getText()
                            .trim();

                    String fareText = fareField
                            .getText()
                            .trim();

                    if (passenger.isEmpty()
                            || seat.isEmpty()
                            || fareText.isEmpty()) {

                        JOptionPane.showMessageDialog(
                                dialog,
                                "Please complete all required fields.",
                                "Invalid Booking",
                                JOptionPane.WARNING_MESSAGE);

                        return;
                    }

                    double fare;

                    try {

                        fare = Double.parseDouble(
                                fareText);

                        if (fare < 0) {
                            throw new NumberFormatException();
                        }

                    } catch (NumberFormatException exception) {

                        JOptionPane.showMessageDialog(
                                dialog,
                                "Fare must be a valid positive number.",
                                "Invalid Fare",
                                JOptionPane.WARNING_MESSAGE);

                        return;
                    }

                    String id = generateBookingId();

                    bookings.add(
                            new Booking(
                                    id,
                                    passenger,
                                    String.valueOf(
                                            tripCombo.getSelectedItem()),
                                    String.valueOf(
                                            routeCombo.getSelectedItem()),
                                    seat,
                                    fare,
                                    String.valueOf(
                                            paymentCombo.getSelectedItem()),
                                    paymentCombo.getSelectedItem()
                                            .equals("Paid")
                                                    ? "Confirmed"
                                                    : "Pending"));

                    refreshBookings();

                    dialog.dispose();
                });

        buttons.add(cancel);
        buttons.add(save);

        root.add(
                titlePanel,
                BorderLayout.NORTH);

        root.add(
                content,
                BorderLayout.CENTER);

        root.add(
                buttons,
                BorderLayout.SOUTH);

        dialog.setContentPane(root);
        dialog.setVisible(true);
    }

    private int addFormRow(
            JPanel panel,
            GridBagConstraints gbc,
            int row,
            String labelText,
            JComponent field) {

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;

        JLabel label = new JLabel(
                labelText);

        label.setFont(
                BussinTheme.SMALL_BOLD);

        label.setForeground(
                BussinTheme.TEXT_SECONDARY);

        panel.add(
                label,
                gbc);

        gbc.gridy = row + 1;

        field.setPreferredSize(
                new Dimension(
                        0,
                        38));

        panel.add(
                field,
                gbc);

        return row + 2;
    }

    private String generateBookingId() {

        int highest = 1000;

        for (Booking booking : bookings) {

            try {

                int number = Integer.parseInt(
                        booking.bookingId.substring(3));

                highest = Math.max(
                        highest,
                        number);

            } catch (NumberFormatException ignored) {
                // Ignore unexpected mock IDs.
            }
        }

        return "BK-" + (highest + 1);
    }

    private static class Booking {

        private final String bookingId;
        private final String passenger;
        private final String trip;
        private final String route;
        private final String seat;
        private final double fare;
        private final String paymentStatus;
        private final LocalDateTime createdAt;

        private String status;

        private Booking(
                String bookingId,
                String passenger,
                String trip,
                String route,
                String seat,
                double fare,
                String paymentStatus,
                String status) {

            this.bookingId = bookingId;
            this.passenger = passenger;
            this.trip = trip;
            this.route = route;
            this.seat = seat;
            this.fare = fare;
            this.paymentStatus = paymentStatus;
            this.status = status;
            this.createdAt = LocalDateTime.now();
        }
    }

    private class BookingTableModel
            extends AbstractTableModel {

        private final String[] columns = {
                "Booking ID",
                "Passenger",
                "Trip",
                "Route",
                "Seat",
                "Fare",
                "Status"
        };

        @Override
        public int getRowCount() {
            return filteredBookings.size();
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
                int rowIndex,
                int columnIndex) {

            Booking booking = filteredBookings.get(
                    rowIndex);

            return switch (columnIndex) {

                case 0 -> booking.bookingId;
                case 1 -> booking.passenger;
                case 2 -> booking.trip;
                case 3 -> booking.route;
                case 4 -> booking.seat;
                case 5 -> String.format(
                        "₱%,.2f",
                        booking.fare);
                case 6 -> booking.status;

                default -> "";
            };
        }

        @Override
        public boolean isCellEditable(
                int rowIndex,
                int columnIndex) {
            return false;
        }
    }

    private static class StatusCellRenderer
            extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {

            String status = String.valueOf(value);

            AppBadge.Status badgeStatus;

            switch (status) {

                case "Confirmed" ->
                    badgeStatus = AppBadge.Status.SUCCESS;

                case "Pending" ->
                    badgeStatus = AppBadge.Status.WARNING;

                case "Cancelled" ->
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

            wrapper.setOpaque(
                    !isSelected);

            if (isSelected) {
                wrapper.setBackground(
                        table.getSelectionBackground());
            } else {
                wrapper.setBackground(
                        table.getBackground());
            }

            wrapper.add(badge);

            return wrapper;
        }
    }
}