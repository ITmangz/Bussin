package com.bussin.desktop.ui.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
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
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import com.bussin.desktop.services.BookingApiService;
import com.bussin.desktop.services.BookingApiService.BookingResponse;
import com.bussin.desktop.services.TripApiService;
import com.bussin.desktop.services.TripApiService.TripResponse;
import com.bussin.desktop.services.UserApiService;
import com.bussin.desktop.services.UserApiService.UserResponse;
import com.bussin.desktop.ui.components.AppBadge;
import com.bussin.desktop.ui.components.AppButton;
import com.bussin.desktop.ui.components.AppCard;
import com.bussin.desktop.ui.components.AppLabel;
import com.bussin.desktop.ui.components.PageContent;
import com.bussin.desktop.ui.theme.BussinTheme;

public class BookingScreen extends JPanel {

        private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("MMM dd, yyyy • hh:mm a");

        private static final DateTimeFormatter SHORT_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("MMM d, h:mm a");

        private final String userRole;
        private final String currentUserEmail;

        private final List<BookingResponse> bookings = new ArrayList<>();
        private final List<BookingResponse> filteredBookings = new ArrayList<>();
        private final List<TripResponse> trips = new ArrayList<>();

        private final BookingTableModel tableModel = new BookingTableModel();

        private final JTable bookingTable = new JTable(tableModel);

        private final JTextField searchField = new JTextField();

        private final JComboBox<String> statusFilter = new JComboBox<>(
                        new String[] {
                                        "All Status",
                                        "Pending",
                                        "Confirmed",
                                        "Completed",
                                        "Cancelled"
                        });

        private final JComboBox<String> paymentFilter = new JComboBox<>(
                        new String[] {
                                        "All Payments",
                                        "Paid",
                                        "Unpaid",
                                        "Refunded"
                        });

        private final JLabel totalValue = new JLabel("0");

        private final JLabel pendingValue = new JLabel("0");

        private final JLabel confirmedValue = new JLabel("0");

        private final JLabel completedValue = new JLabel("0");

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
                        "Cancel",
                        AppButton.Variant.DANGER);

        public BookingScreen(
                        String userRole,
                        String currentUserEmail) {

                this.userRole = normalizeRole(userRole);

                this.currentUserEmail = currentUserEmail == null
                                ? ""
                                : currentUserEmail.trim();

                initializeUI();

                loadBookings();
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

                PageContent page = new PageContent();

                page.addBlock(
                                createHeader(),
                                0);

                page.addBlock(
                                createStatistics(),
                                20);

                page.addBlock(
                                createMainSection(),
                                18);

                return page.inScrollPane();
        }

        private JPanel createHeader() {

                JPanel panel = new JPanel(
                                new BorderLayout());

                panel.setOpaque(false);

                JPanel titlePanel = new JPanel();

                titlePanel.setOpaque(false);

                titlePanel.setLayout(
                                new javax.swing.BoxLayout(
                                                titlePanel,
                                                javax.swing.BoxLayout.Y_AXIS));

                titlePanel.add(
                                AppLabel.title(
                                                "My Bookings"));

                titlePanel.add(
                                javax.swing.Box.createVerticalStrut(5));

                titlePanel.add(
                                AppLabel.secondary(
                                                "View and manage your bookings."));

                AppButton createButton = new AppButton(
                                "Create Booking");

                createButton.addActionListener(
                                event -> showCreateBookingDialog());

                JPanel actionPanel = new JPanel(
                                new FlowLayout(
                                                FlowLayout.RIGHT,
                                                0,
                                                0));

                actionPanel.setOpaque(false);

                actionPanel.add(
                                createButton);

                panel.add(
                                titlePanel,
                                BorderLayout.WEST);

                panel.add(
                                actionPanel,
                                BorderLayout.EAST);

                return panel;
        }

        private JPanel createStatistics() {

                JPanel panel = new JPanel(
                                new java.awt.GridLayout(
                                                1,
                                                4,
                                                12,
                                                0));

                panel.setOpaque(false);

                panel.add(
                                createStatCard(
                                                "PENDING",
                                                pendingValue,
                                                BussinTheme.WARNING));

                panel.add(
                                createStatCard(
                                                "CONFIRMED",
                                                confirmedValue,
                                                BussinTheme.SUCCESS));

                panel.add(
                                createStatCard(
                                                "COMPLETED",
                                                completedValue,
                                                BussinTheme.CHARCOAL));

                panel.add(
                                createStatCard(
                                                "CANCELLED",
                                                cancelledValue,
                                                BussinTheme.RED));

                return panel;
        }

        private AppCard createStatCard(
                        String title,
                        JLabel value,
                        Color accent) {

                AppCard card = new AppCard();

                card.setLayout(
                                new BorderLayout(
                                                12,
                                                0));

                JPanel indicator = new JPanel();

                indicator.setBackground(
                                accent);

                indicator.setPreferredSize(
                                new Dimension(
                                                4,
                                                50));

                JPanel content = new JPanel();

                content.setOpaque(false);

                content.setLayout(
                                new javax.swing.BoxLayout(
                                                content,
                                                javax.swing.BoxLayout.Y_AXIS));

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
                                javax.swing.Box.createVerticalStrut(5));

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

                JPanel panel = new JPanel(
                                new BorderLayout(
                                                18,
                                                0));

                panel.setOpaque(false);

                panel.add(
                                createBookingListCard(),
                                BorderLayout.CENTER);

                panel.add(
                                createDetailsCard(),
                                BorderLayout.EAST);

                return panel;
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

                JPanel center = new JPanel(
                                new BorderLayout(
                                                0,
                                                10));

                center.setOpaque(false);

                center.add(
                                createFilters(),
                                BorderLayout.NORTH);

                configureTable();

                JScrollPane scroll = new JScrollPane(
                                bookingTable);

                scroll.setBorder(
                                BorderFactory.createLineBorder(
                                                BussinTheme.BORDER));

                center.add(
                                scroll,
                                BorderLayout.CENTER);

                card.add(
                                center,
                                BorderLayout.CENTER);

                return card;
        }

        private JPanel createFilters() {

                JPanel panel = new JPanel(
                                new java.awt.GridLayout(
                                                1,
                                                3,
                                                10,
                                                0));

                panel.setOpaque(false);

                searchField.putClientProperty(
                                "JTextField.placeholderText",
                                "Search booking, passenger, route, seat");

                panel.add(searchField);
                panel.add(statusFilter);
                panel.add(paymentFilter);

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

                paymentFilter.addActionListener(
                                event -> refreshBookings());

                return panel;
        }

        private void configureTable() {

                bookingTable.setRowHeight(40);

                bookingTable.setFont(
                                BussinTheme.SMALL);

                bookingTable.setSelectionMode(
                                ListSelectionModel.SINGLE_SELECTION);

                bookingTable.setAutoCreateRowSorter(true);

                bookingTable.setShowVerticalLines(false);

                bookingTable.setGridColor(
                                BussinTheme.BORDER);

                bookingTable.setFillsViewportHeight(true);

                bookingTable
                                .getSelectionModel()
                                .addListSelectionListener(
                                                event -> {

                                                        if (!event.getValueIsAdjusting()) {
                                                                updateSelectedBooking();
                                                        }
                                                });

                bookingTable
                                .getColumnModel()
                                .getColumn(6)
                                .setCellRenderer(
                                                new StatusCellRenderer());

                bookingTable
                                .getColumnModel()
                                .getColumn(7)
                                .setCellRenderer(
                                                new StatusCellRenderer());
        }

        private AppCard createDetailsCard() {

                AppCard card = new AppCard();

                card.setPreferredSize(
                                new Dimension(
                                                310,
                                                0));

                card.setLayout(
                                new BorderLayout(
                                                0,
                                                18));

                JPanel header = new JPanel(
                                new BorderLayout());

                header.setOpaque(false);

                header.add(
                                AppLabel.section(
                                                "Details"),
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
                                new javax.swing.BoxLayout(
                                                details,
                                                javax.swing.BoxLayout.Y_AXIS));

                addDetail(
                                details,
                                "BOOKING REFERENCE",
                                detailBookingId,
                                true);

                addDetail(
                                details,
                                "PASSENGER",
                                detailPassenger,
                                false);

                addDetail(
                                details,
                                "TRIP",
                                detailTrip,
                                false);

                addDetail(
                                details,
                                "ROUTE",
                                detailRoute,
                                false);

                addDetail(
                                details,
                                "SEAT",
                                detailSeat,
                                false);

                addDetail(
                                details,
                                "FARE",
                                detailFare,
                                false);

                addDetail(
                                details,
                                "PAYMENT",
                                detailPayment,
                                false);

                addDetail(
                                details,
                                "CREATED",
                                detailCreated,
                                false);

                card.add(
                                new JScrollPane(details),
                                BorderLayout.CENTER);

                cancelButton.setEnabled(false);

                cancelButton.addActionListener(
                                event -> cancelSelectedBooking());

                JPanel actions = new JPanel(
                                new BorderLayout());

                actions.setOpaque(false);

                actions.add(
                                cancelButton,
                                BorderLayout.CENTER);

                card.add(
                                actions,
                                BorderLayout.SOUTH);

                return card;
        }

        private void addDetail(
                        JPanel panel,
                        String title,
                        JLabel value,
                        boolean emphasized) {

                JLabel titleLabel = new JLabel(title);

                titleLabel.setFont(
                                BussinTheme.SMALL_BOLD);

                titleLabel.setForeground(
                                BussinTheme.TEXT_SECONDARY);

                value.setFont(
                                emphasized
                                                ? BussinTheme.SECTION_TITLE
                                                : BussinTheme.BODY);

                value.setForeground(
                                BussinTheme.TEXT_PRIMARY);

                panel.add(titleLabel);

                panel.add(
                                javax.swing.Box.createVerticalStrut(3));

                panel.add(value);

                panel.add(
                                javax.swing.Box.createVerticalStrut(13));
        }

        private void loadBookings() {

                SwingUtilities.invokeLater(
                                () -> {

                                        try {

                                                bookings.clear();

                                                bookings.addAll(
                                                                BookingApiService
                                                                                .getMyBookings());

                                                trips.clear();

                                                trips.addAll(
                                                                TripApiService
                                                                                .getAllTrips());

                                                refreshBookings();

                                        } catch (Exception exception) {

                                                JOptionPane.showMessageDialog(
                                                                this,
                                                                getErrorMessage(
                                                                                exception),
                                                                "Unable to Load Bookings",
                                                                JOptionPane.ERROR_MESSAGE);
                                        }
                                });
        }

        private void refreshBookings() {

                String query = searchField
                                .getText()
                                .trim()
                                .toLowerCase();

                String selectedStatus = String.valueOf(
                                statusFilter
                                                .getSelectedItem());

                String selectedPayment = String.valueOf(
                                paymentFilter
                                                .getSelectedItem());

                String selectedReference = getSelectedReference();

                filteredBookings.clear();

                for (BookingResponse booking : bookings) {

                        boolean matchesSearch = query.isBlank()
                                        || contains(
                                                        booking.getBookingReference(),
                                                        query)
                                        || contains(
                                                        booking.getPassengerName(),
                                                        query)
                                        || contains(
                                                        booking.getPassengerEmail(),
                                                        query)
                                        || contains(
                                                        booking.getRouteIdentifier(),
                                                        query)
                                        || contains(
                                                        booking.getOrigin(),
                                                        query)
                                        || contains(
                                                        booking.getDestination(),
                                                        query)
                                        || contains(
                                                        booking.getSeatNumber(),
                                                        query);

                        boolean matchesStatus = "All Status".equals(
                                        selectedStatus)
                                        || selectedStatus.equalsIgnoreCase(
                                                        displayStatus(
                                                                        booking.getStatus()));

                        boolean matchesPayment = "All Payments".equals(
                                        selectedPayment)
                                        || selectedPayment.equalsIgnoreCase(
                                                        displayPayment(
                                                                        booking.getPaymentStatus()));

                        if (matchesSearch
                                        && matchesStatus
                                        && matchesPayment) {

                                filteredBookings.add(
                                                booking);
                        }
                }

                tableModel.fireTableDataChanged();

                updateStatistics();

                if (filteredBookings.isEmpty()) {

                        clearDetails();

                        return;
                }

                int row = 0;

                if (selectedReference != null) {

                        for (int i = 0; i < filteredBookings.size(); i++) {

                                if (selectedReference.equals(
                                                filteredBookings
                                                                .get(i)
                                                                .getBookingReference())) {

                                        row = i;

                                        break;
                                }
                        }
                }

                bookingTable.setRowSelectionInterval(
                                row,
                                row);

                updateSelectedBooking();
        }

        private void updateStatistics() {

                int pending = 0;
                int confirmed = 0;
                int completed = 0;
                int cancelled = 0;

                for (BookingResponse booking : filteredBookings) {

                        switch (safe(
                                        booking.getStatus())
                                        .toUpperCase()) {

                                case "PENDING" ->
                                        pending++;

                                case "CONFIRMED" ->
                                        confirmed++;

                                case "COMPLETED" ->
                                        completed++;

                                case "CANCELLED" ->
                                        cancelled++;

                                default -> {
                                }
                        }
                }

                totalValue.setText(
                                String.valueOf(
                                                filteredBookings.size()));

                pendingValue.setText(
                                String.valueOf(pending));

                confirmedValue.setText(
                                String.valueOf(confirmed));

                completedValue.setText(
                                String.valueOf(completed));

                cancelledValue.setText(
                                String.valueOf(cancelled));
        }

        private void updateSelectedBooking() {

                BookingResponse booking = getSelectedBooking();

                if (booking == null) {

                        clearDetails();

                        return;
                }

                detailBookingId.setText(
                                safe(
                                                booking.getBookingReference()));

                detailPassenger.setText(
                                safe(
                                                booking.getPassengerName()));

                detailTrip.setText(
                                "Trip "
                                                + booking.getTripId()
                                                + " • "
                                                + formatDateTime(
                                                                booking.getScheduledDeparture()));

                detailRoute.setText(
                                safe(
                                                booking.getOrigin())
                                                + " → "
                                                + safe(
                                                                booking.getDestination()));

                detailSeat.setText(
                                safe(
                                                booking.getSeatNumber()));

                detailFare.setText(
                                String.format(
                                                "₱%,.2f",
                                                booking.getFare()));

                detailPayment.setText(
                                displayPayment(
                                                booking.getPaymentStatus()));

                detailCreated.setText(
                                booking.getCreatedAt() == null
                                                ? "-"
                                                : booking.getCreatedAt()
                                                                .format(
                                                                                DATE_TIME_FORMAT));

                updateStatusBadge(
                                displayStatus(
                                                booking.getStatus()));

                updateCancelButton(
                                booking);
        }

        private void updateCancelButton(
                        BookingResponse booking) {

                boolean cancellable = booking != null
                                && !"CANCELLED".equalsIgnoreCase(
                                                booking.getStatus())
                                && !"COMPLETED".equalsIgnoreCase(
                                                booking.getStatus());

                cancelButton.setEnabled(
                                cancellable);
        }

        private BookingResponse getSelectedBooking() {

                int viewRow = bookingTable.getSelectedRow();

                if (viewRow < 0) {
                        return null;
                }

                int modelRow = bookingTable
                                .convertRowIndexToModel(
                                                viewRow);

                if (modelRow < 0
                                || modelRow >= filteredBookings.size()) {

                        return null;
                }

                return filteredBookings.get(
                                modelRow);
        }

        private String getSelectedReference() {

                BookingResponse booking = getSelectedBooking();

                return booking == null
                                ? null
                                : booking.getBookingReference();
        }

        private void cancelSelectedBooking() {

                BookingResponse booking = getSelectedBooking();

                if (booking == null) {
                        return;
                }

                if ("CANCELLED".equalsIgnoreCase(
                                booking.getStatus())) {

                        return;
                }

                if ("COMPLETED".equalsIgnoreCase(
                                booking.getStatus())) {

                        JOptionPane.showMessageDialog(
                                        this,
                                        "Completed bookings cannot be cancelled.",
                                        "Booking Locked",
                                        JOptionPane.WARNING_MESSAGE);

                        return;
                }

                int result = JOptionPane.showConfirmDialog(
                                this,
                                "Cancel booking "
                                                + booking.getBookingReference()
                                                + "?",
                                "Cancel Booking",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE);

                if (result != JOptionPane.YES_OPTION) {

                        return;
                }

                try {

                        BookingApiService.cancelBooking(
                                        booking.getId());

                        loadBookings();

                        JOptionPane.showMessageDialog(
                                        this,
                                        "Booking cancelled successfully.",
                                        "Booking Cancelled",
                                        JOptionPane.INFORMATION_MESSAGE);

                } catch (Exception exception) {

                        JOptionPane.showMessageDialog(
                                        this,
                                        getErrorMessage(
                                                        exception),
                                        "Cancellation Failed",
                                        JOptionPane.ERROR_MESSAGE);
                }
        }

        private void showCreateBookingDialog() {

                JDialog dialog = new JDialog(
                                SwingUtilities.getWindowAncestor(
                                                this),
                                "Create Booking",
                                java.awt.Dialog.ModalityType.APPLICATION_MODAL);

                dialog.setDefaultCloseOperation(
                                JDialog.DISPOSE_ON_CLOSE);

                dialog.setSize(
                                520,
                                600);

                dialog.setLocationRelativeTo(
                                this);

                JPanel root = new JPanel(
                                new BorderLayout());

                root.setBackground(
                                BussinTheme.BACKGROUND);

                JPanel content = new JPanel(
                                new GridBagLayout());

                content.setOpaque(false);

                content.setBorder(
                                BorderFactory.createEmptyBorder(
                                                20,
                                                24,
                                                10,
                                                24));

                GridBagConstraints gbc = new GridBagConstraints();

                gbc.gridx = 0;
                gbc.weightx = 1;
                gbc.fill = GridBagConstraints.HORIZONTAL;

                gbc.insets = new Insets(
                                6,
                                0,
                                6,
                                0);

                JTextField passengerField = new JTextField();

                JTextField phoneField = new JTextField();

                JTextField emailField = new JTextField();

                JComboBox<TripResponse> tripCombo = new JComboBox<>();

                JTextField seatField = new JTextField();

                JTextField fareField = new JTextField();

                fareField.setEditable(false);

                fareField.setBackground(
                                BussinTheme.SURFACE_ALT);

                loadCurrentUserProfile(
                                passengerField,
                                phoneField,
                                emailField);

                List<TripResponse> availableTrips = getAvailableTrips();

                tripCombo.setModel(
                                new DefaultComboBoxModel<>(
                                                availableTrips.toArray(
                                                                new TripResponse[0])));

                tripCombo.setRenderer(
                                new DefaultListCellRenderer() {

                                        @Override
                                        public Component getListCellRendererComponent(
                                                        javax.swing.JList<?> list,
                                                        Object value,
                                                        int index,
                                                        boolean isSelected,
                                                        boolean cellHasFocus) {

                                                super.getListCellRendererComponent(
                                                                list,
                                                                value,
                                                                index,
                                                                isSelected,
                                                                cellHasFocus);

                                                if (value instanceof TripResponse trip) {

                                                        setText(
                                                                        "Trip "
                                                                                        + trip.getId()
                                                                                        + " • "
                                                                                        + formatDateTime(
                                                                                                        trip.getScheduledDeparture())
                                                                                        + " • "
                                                                                        + safe(
                                                                                                        trip.getRouteIdentifier()));
                                                }

                                                return this;
                                        }
                                });

                tripCombo.addActionListener(
                                event -> {

                                        TripResponse trip = (TripResponse) tripCombo.getSelectedItem();

                                        if (trip == null) {

                                                fareField.setText("");

                                                return;
                                        }

                                        fareField.setText(
                                                        "Calculated by server");
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
                                "Phone",
                                phoneField);

                row = addFormRow(
                                content,
                                gbc,
                                row,
                                "Email",
                                emailField);

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
                                "Seat Number",
                                seatField);

                addFormRow(
                                content,
                                gbc,
                                row,
                                "Fare",
                                fareField);

                JLabel title = AppLabel.section(
                                "Create New Booking");

                JPanel titlePanel = new JPanel(
                                new BorderLayout());

                titlePanel.setOpaque(false);

                titlePanel.setBorder(
                                BorderFactory.createEmptyBorder(
                                                20,
                                                24,
                                                0,
                                                24));

                titlePanel.add(
                                title,
                                BorderLayout.WEST);

                JPanel actions = new JPanel(
                                new FlowLayout(
                                                FlowLayout.RIGHT,
                                                8,
                                                10));

                actions.setOpaque(false);

                AppButton closeButton = new AppButton(
                                "Cancel",
                                AppButton.Variant.SECONDARY);

                AppButton createButton = new AppButton(
                                "Create Booking");

                closeButton.addActionListener(
                                event -> dialog.dispose());

                createButton.addActionListener(
                                event -> {

                                        TripResponse trip = (TripResponse) tripCombo.getSelectedItem();

                                        String passengerName = passengerField
                                                        .getText()
                                                        .trim();

                                        String passengerPhone = phoneField
                                                        .getText()
                                                        .trim();

                                        String passengerEmail = emailField
                                                        .getText()
                                                        .trim();

                                        String seatNumber = seatField
                                                        .getText()
                                                        .trim();

                                        if (trip == null) {

                                                JOptionPane.showMessageDialog(
                                                                dialog,
                                                                "Please select a trip.",
                                                                "Invalid Booking",
                                                                JOptionPane.WARNING_MESSAGE);

                                                return;
                                        }

                                        if (passengerName.isBlank()
                                                        || passengerPhone.isBlank()
                                                        || passengerEmail.isBlank()
                                                        || seatNumber.isBlank()) {

                                                JOptionPane.showMessageDialog(
                                                                dialog,
                                                                "Please complete all required fields.",
                                                                "Invalid Booking",
                                                                JOptionPane.WARNING_MESSAGE);

                                                return;
                                        }

                                        try {

                                                BookingApiService.createBooking(
                                                                trip.getId(),
                                                                seatNumber,
                                                                passengerName,
                                                                passengerPhone,
                                                                passengerEmail);

                                                dialog.dispose();

                                                loadBookings();

                                                JOptionPane.showMessageDialog(
                                                                this,
                                                                "Booking created successfully.",
                                                                "Booking Created",
                                                                JOptionPane.INFORMATION_MESSAGE);

                                        } catch (Exception exception) {

                                                JOptionPane.showMessageDialog(
                                                                dialog,
                                                                getErrorMessage(
                                                                                exception),
                                                                "Booking Failed",
                                                                JOptionPane.ERROR_MESSAGE);
                                        }
                                });

                actions.add(
                                closeButton);

                actions.add(
                                createButton);

                root.add(
                                titlePanel,
                                BorderLayout.NORTH);

                root.add(
                                new JScrollPane(
                                                content),
                                BorderLayout.CENTER);

                root.add(
                                actions,
                                BorderLayout.SOUTH);

                dialog.setContentPane(
                                root);

                if (tripCombo.getItemCount() > 0) {
                        tripCombo.setSelectedIndex(0);
                        fareField.setText(
                                        "Calculated by server");
                }

                dialog.setVisible(true);
        }

        private List<TripResponse> getAvailableTrips() {

                List<TripResponse> result = new ArrayList<>();

                System.out.println("========== BOOKING TRIP DEBUG ==========");
                System.out.println("Total trips received: " + trips.size());

                for (TripResponse trip : trips) {

                        if (trip == null) {
                                System.out.println("Trip: NULL");
                                continue;
                        }

                        String status = safe(trip.getStatus()).toUpperCase();

                        System.out.println(
                                        "Trip ID: " + trip.getId()
                                                        + " | Route: " + trip.getRouteIdentifier()
                                                        + " | Bus: " + trip.getBusPlateNumber()
                                                        + " | Status: " + status);

                        if ("SCHEDULED".equals(status)
                                        || "BOARDING".equals(status)) {

                                result.add(trip);
                        }
                }

                System.out.println("Available trips: " + result.size());
                System.out.println("========================================");

                return result;
        }

        private void loadCurrentUserProfile(
                        JTextField passengerField,
                        JTextField phoneField,
                        JTextField emailField) {

                try {

                        UserResponse user = UserApiService
                                        .getCurrentUser();

                        if (user != null) {

                                passengerField.setText(
                                                buildFullName(
                                                                user.getFirstName(),
                                                                user.getMiddleName(),
                                                                user.getLastName()));

                                phoneField.setText(
                                                safe(
                                                                user.getContactNumber()));

                                emailField.setText(
                                                safe(
                                                                user.getEmail()));
                        }

                } catch (Exception exception) {

                        emailField.setText(
                                        currentUserEmail);
                }

                passengerField.setEditable(false);
                phoneField.setEditable(false);
                emailField.setEditable(false);

                passengerField.setBackground(
                                BussinTheme.SURFACE_ALT);

                phoneField.setBackground(
                                BussinTheme.SURFACE_ALT);

                emailField.setBackground(
                                BussinTheme.SURFACE_ALT);
        }

        private String buildFullName(
                        String firstName,
                        String middleName,
                        String lastName) {

                StringBuilder name = new StringBuilder();

                appendName(
                                name,
                                firstName);

                appendName(
                                name,
                                middleName);

                appendName(
                                name,
                                lastName);

                if (name.isEmpty()) {
                        return currentUserEmail;
                }

                return name.toString();
        }

        private void appendName(
                        StringBuilder builder,
                        String value) {

                if (value == null
                                || value.isBlank()) {
                        return;
                }

                if (!builder.isEmpty()) {
                        builder.append(" ");
                }

                builder.append(
                                value.trim());
        }

        private int addFormRow(
                        JPanel panel,
                        GridBagConstraints gbc,
                        int row,
                        String labelText,
                        JComponent field) {

                gbc.gridy = row;

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

        private void clearDetails() {

                detailBookingId.setText("-");
                detailPassenger.setText("-");
                detailTrip.setText("-");
                detailRoute.setText("-");
                detailSeat.setText("-");
                detailFare.setText("-");
                detailPayment.setText("-");
                detailCreated.setText("-");

                detailStatus.setText(
                                "NO STATUS");

                cancelButton.setEnabled(
                                false);
        }

        private void updateStatusBadge(
                        String status) {

                AppBadge.Status badgeStatus;

                switch (status) {

                        case "Confirmed" ->
                                badgeStatus = AppBadge.Status.SUCCESS;

                        case "Pending" ->
                                badgeStatus = AppBadge.Status.WARNING;

                        case "Completed" ->
                                badgeStatus = AppBadge.Status.INFO;

                        case "Cancelled" ->
                                badgeStatus = AppBadge.Status.DANGER;

                        default ->
                                badgeStatus = AppBadge.Status.NEUTRAL;
                }

                detailStatus.setText(
                                status.toUpperCase());
        }

        private String displayStatus(
                        String status) {

                if (status == null
                                || status.isBlank()) {

                        return "Unknown";
                }

                return switch (status.toUpperCase()) {

                        case "PENDING" ->
                                "Pending";

                        case "CONFIRMED" ->
                                "Confirmed";

                        case "COMPLETED" ->
                                "Completed";

                        case "CANCELLED" ->
                                "Cancelled";

                        default ->
                                status;
                };
        }

        private String displayPayment(
                        String payment) {

                if (payment == null
                                || payment.isBlank()) {

                        return "Unknown";
                }

                return switch (payment.toUpperCase()) {

                        case "PAID" ->
                                "Paid";

                        case "UNPAID" ->
                                "Unpaid";

                        case "REFUNDED" ->
                                "Refunded";

                        default ->
                                payment;
                };
        }

        private String formatDateTime(
                        LocalDateTime dateTime) {

                if (dateTime == null) {
                        return "-";
                }

                return dateTime.format(
                                SHORT_DATE_TIME_FORMAT);
        }

        private String normalizeRole(
                        String role) {

                if (role == null
                                || role.isBlank()) {

                        return "COMMUTER";
                }

                String value = role.trim()
                                .toUpperCase();

                if ("USER".equals(value)) {
                        return "COMMUTER";
                }

                return value;
        }

        private boolean contains(
                        String value,
                        String query) {

                return value != null
                                && value.toLowerCase()
                                                .contains(query);
        }

        private String safe(
                        String value) {

                return value == null
                                ? "-"
                                : value;
        }

        private String getErrorMessage(
                        Exception exception) {

                if (exception == null) {
                        return "Unknown error.";
                }

                if (exception.getMessage() == null
                                || exception.getMessage().isBlank()) {

                        return exception
                                        .getClass()
                                        .getSimpleName();
                }

                return exception.getMessage();
        }

        private class BookingTableModel
                        extends AbstractTableModel {

                private final String[] columns = {
                                "Ref. No.",
                                "Passenger",
                                "Trip",
                                "Route",
                                "Seat",
                                "Fare",
                                "Status",
                                "Payment",
                                "Booked"
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
                                int row,
                                int column) {

                        BookingResponse booking = filteredBookings.get(row);

                        return switch (column) {

                                case 0 ->
                                        booking.getBookingReference();

                                case 1 ->
                                        booking.getPassengerName();

                                case 2 ->
                                        "Trip "
                                                        + booking.getTripId();

                                case 3 ->
                                        safe(
                                                        booking.getRouteIdentifier());

                                case 4 ->
                                        booking.getSeatNumber();

                                case 5 ->
                                        String.format(
                                                        "₱%,.2f",
                                                        booking.getFare());

                                case 6 ->
                                        displayStatus(
                                                        booking.getStatus());

                                case 7 ->
                                        displayPayment(
                                                        booking.getPaymentStatus());

                                case 8 ->
                                        booking.getCreatedAt() == null
                                                        ? "-"
                                                        : booking.getCreatedAt()
                                                                        .format(
                                                                                        SHORT_DATE_TIME_FORMAT);

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
                                boolean focused,
                                int row,
                                int column) {

                        JLabel label = (JLabel) super.getTableCellRendererComponent(
                                        table,
                                        value,
                                        selected,
                                        focused,
                                        row,
                                        column);

                        String status = String.valueOf(value);

                        label.setOpaque(true);

                        if ("Confirmed".equals(status)
                                        || "Paid".equals(status)) {

                                label.setForeground(
                                                BussinTheme.SUCCESS);

                        } else if ("Pending".equals(status)
                                        || "Unpaid".equals(status)) {

                                label.setForeground(
                                                BussinTheme.WARNING);

                        } else if ("Cancelled".equals(status)) {

                                label.setForeground(
                                                BussinTheme.DANGER);

                        } else {

                                label.setForeground(
                                                BussinTheme.TEXT_PRIMARY);
                        }

                        return label;
                }
        }
}