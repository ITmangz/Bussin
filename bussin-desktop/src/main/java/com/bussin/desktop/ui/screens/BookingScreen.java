package com.bussin.desktop.ui.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
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
import javax.swing.JViewport;
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
import com.bussin.desktop.ui.components.PageContent;
import com.bussin.desktop.ui.components.ResponsiveLayouts;
import com.bussin.desktop.ui.theme.BussinTheme;

public class BookingScreen extends JPanel {

        // ================================================================
        // CURRENT USER
        // ================================================================

        private final String userRole;
        private final String currentUserEmail;

        // ================================================================
        // DATA
        // ================================================================

        private final List<Booking> bookings = new ArrayList<>();

        private final List<Booking> filteredBookings = new ArrayList<>();

        private final BookingTableModel tableModel = new BookingTableModel();

        // ================================================================
        // TABLE
        // ================================================================

        private final JTable bookingTable = new JTable(tableModel) {

                @Override
                public boolean getScrollableTracksViewportWidth() {

                        return getParent() instanceof JViewport viewport
                                        && viewport.getWidth() >= 780;
                }
        };

        // ================================================================
        // FILTERS
        // ================================================================

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

        // ================================================================
        // STATISTICS
        // ================================================================

        private final JLabel totalValue = new JLabel("0");

        private final JLabel completedValue = new JLabel("0");

        private final JLabel confirmedValue = new JLabel("0");

        private final JLabel pendingValue = new JLabel("0");

        private final JLabel cancelledValue = new JLabel("0");

        // ================================================================
        // DETAILS
        // ================================================================

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

        // ================================================================
        // ACTION BUTTONS
        // ================================================================

        private final AppButton cancelButton = new AppButton(
                        "Cancel",
                        AppButton.Variant.DANGER);

        private final AppButton confirmButton = new AppButton(
                        "Confirm");

        private final AppButton payButton = new AppButton(
                        "Mark Paid",
                        AppButton.Variant.SECONDARY);

        private final AppButton completeButton = new AppButton(
                        "Complete",
                        AppButton.Variant.SECONDARY);

        // ================================================================
        // FORMATS
        // ================================================================

        private static final DateTimeFormatter TABLE_TIME_FORMAT = DateTimeFormatter.ofPattern(
                        "MMM d, h:mm a");

        private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern(
                        "MMM dd, yyyy • hh:mm a");

        // ================================================================
        // CONSTRUCTOR
        // ================================================================

        public BookingScreen(
                        String userRole,
                        String currentUserEmail) {

                this.userRole = normalizeRole(userRole);

                this.currentUserEmail = currentUserEmail == null
                                ? ""
                                : currentUserEmail.trim();

                initializeData();

                initializeUI();

                refreshBookings();
        }

        // ================================================================
        // ROLE HELPERS
        // ================================================================

        private boolean isUser() {

                return "USER".equals(userRole);
        }

        private boolean isStaff() {

                return "ADMIN".equals(userRole)
                                || "EMPLOYEE".equals(userRole);
        }

        // ================================================================
        // INITIAL DATA
        // ================================================================

        private void initializeData() {

                LocalDateTime now = LocalDateTime.now()
                                .withSecond(0)
                                .withNano(0);

                /*
                 * Existing bookings are assigned to different owners.
                 *
                 * ADMIN and EMPLOYEE can see all bookings.
                 *
                 * USER can only see bookings whose ownerEmail
                 * matches the currently logged-in account.
                 */

                add(
                                "BK-1001",
                                "admin@bussin.com",
                                "Juan Dela Cruz",
                                "Trip 102 • 10:00 AM",
                                "Manila → Batangas",
                                "12A",
                                450,
                                "Paid",
                                "Completed",
                                now.minusDays(1)
                                                .withHour(16)
                                                .withMinute(20));

                add(
                                "BK-1002",
                                "employee@bussin.com",
                                "Maria Santos",
                                "Trip 102 • 10:00 AM",
                                "Manila → Batangas",
                                "12B",
                                360,
                                "Paid",
                                "Confirmed",
                                now.minusHours(20));

                add(
                                "BK-1003",
                                "user@bussin.com",
                                "User",
                                "Trip 114 • 10:30 AM",
                                "Manila → Lucena",
                                "08A",
                                520,
                                "Unpaid",
                                "Pending",
                                now.minusHours(3)
                                                .minusMinutes(12));

                add(
                                "BK-1004",
                                "user@bussin.com",
                                "User",
                                "Trip 114 • 10:30 AM",
                                "Manila → Lucena",
                                "08B",
                                520,
                                "Paid",
                                "Confirmed",
                                now.minusHours(5));

                add(
                                "BK-1005",
                                "admin@bussin.com",
                                "Pedro Garcia",
                                "Trip 121 • 11:00 AM",
                                "Manila → Nasugbu",
                                "22A",
                                380,
                                "Refunded",
                                "Cancelled",
                                now.minusDays(1)
                                                .withHour(9)
                                                .withMinute(45));

                add(
                                "BK-1006",
                                "employee@bussin.com",
                                "Sofia Ramos",
                                "Trip 121 • 11:00 AM",
                                "Manila → Nasugbu",
                                "22B",
                                380,
                                "Paid",
                                "Confirmed",
                                now.minusHours(2)
                                                .minusMinutes(30));

                add(
                                "BK-1007",
                                "admin@bussin.com",
                                "Mark Villanueva",
                                "Trip 102 • 10:00 AM",
                                "Manila → Batangas",
                                "05C",
                                450,
                                "Unpaid",
                                "Pending",
                                now.minusMinutes(50));

                add(
                                "BK-1008",
                                "employee@bussin.com",
                                "Liza Mendoza",
                                "Trip 114 • 10:30 AM",
                                "Manila → Lucena",
                                "10A",
                                416,
                                "Paid",
                                "Completed",
                                now.minusDays(2)
                                                .withHour(14)
                                                .withMinute(5));

                add(
                                "BK-1009",
                                "admin@bussin.com",
                                "Rico Bautista",
                                "Trip 121 • 11:00 AM",
                                "Manila → Nasugbu",
                                "14D",
                                380,
                                "Unpaid",
                                "Cancelled",
                                now.minusDays(2)
                                                .withHour(11)
                                                .withMinute(30));

                add(
                                "BK-1010",
                                "employee@bussin.com",
                                "Nina Aquino",
                                "Trip 102 • 10:00 AM",
                                "Manila → Batangas",
                                "03A",
                                360,
                                "Paid",
                                "Confirmed",
                                now.minusHours(1)
                                                .minusMinutes(5));
        }

        private void add(
                        String id,
                        String ownerEmail,
                        String passenger,
                        String trip,
                        String route,
                        String seat,
                        double fare,
                        String payment,
                        String status,
                        LocalDateTime createdAt) {

                bookings.add(
                                new Booking(
                                                id,
                                                ownerEmail,
                                                passenger,
                                                trip,
                                                route,
                                                seat,
                                                fare,
                                                payment,
                                                status,
                                                createdAt));
        }

        // ================================================================
        // INITIALIZE UI
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

        // ================================================================
        // CONTENT
        // ================================================================

        private JComponent createContent() {

                PageContent page = new PageContent();

                page.addBlock(
                                createHeader(),
                                0);

                page.addBlock(
                                createStatistics(),
                                24);

                page.addBlock(
                                createMainSection(),
                                18);

                return page.inScrollPane();
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
                                                isUser()
                                                                ? "My Bookings"
                                                                : "Booking Management"));

                titlePanel.add(
                                Box.createVerticalStrut(5));

                titlePanel.add(
                                AppLabel.secondary(
                                                isUser()
                                                                ? "View and create your own passenger bookings."
                                                                : "Manage passenger bookings, trip assignments, seats, and payment status."));

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

                actionPanel.add(
                                createButton);

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
                                new ResponsiveLayouts.Grid(
                                                4,
                                                200,
                                                14));

                statistics.setOpaque(false);

                statistics.add(
                                createStatCard(
                                                "PENDING",
                                                pendingValue,
                                                BussinTheme.WARNING));

                statistics.add(
                                createStatCard(
                                                "CONFIRMED",
                                                confirmedValue,
                                                BussinTheme.SUCCESS));

                statistics.add(
                                createStatCard(
                                                "COMPLETED",
                                                completedValue,
                                                BussinTheme.CHARCOAL));

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

                content.add(
                                titleLabel);

                content.add(
                                Box.createVerticalStrut(5));

                content.add(
                                value);

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
                                new ResponsiveLayouts.Split(
                                                18,
                                                1000,
                                                3,
                                                1));

                main.setOpaque(false);

                main.add(
                                createBookingListCard());

                main.add(
                                createBookingDetailsCard());

                return main;
        }

        // ================================================================
        // BOOKING LIST
        // ================================================================

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
                                                isUser()
                                                                ? "My Bookings"
                                                                : "Bookings"),
                                BorderLayout.WEST);

                JLabel hint = new JLabel(
                                isUser()
                                                ? "Select a booking to view details"
                                                : "Select a booking to view details");

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

                card.add(
                                center,
                                BorderLayout.CENTER);

                return card;
        }

        // ================================================================
        // FILTER BAR
        // ================================================================

        private JPanel createFilterBar() {

                JPanel filters = new JPanel(
                                new ResponsiveLayouts.Grid(
                                                3,
                                                150,
                                                10));

                filters.setOpaque(false);

                searchField.setToolTipText(
                                "Search booking ID, passenger, route, trip, or seat");

                searchField.putClientProperty(
                                "JTextField.placeholderText",
                                "Search ID, passenger, route, seat");

                filters.add(
                                createSearchField());

                filters.add(
                                statusFilter);

                filters.add(
                                paymentFilter);

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

        // ================================================================
        // TABLE
        // ================================================================

        private void configureTable() {

                bookingTable.setRowHeight(40);

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

                for (int i = 0; i < 9; i++) {

                        bookingTable
                                        .getColumnModel()
                                        .getColumn(i)
                                        .setPreferredWidth(
                                                        switch (i) {
                                                                case 0 -> 70;
                                                                case 1 -> 110;
                                                                case 2 -> 108;
                                                                case 3 -> 125;
                                                                case 4 -> 42;
                                                                case 5 -> 68;
                                                                case 6 -> 92;
                                                                case 7 -> 92;
                                                                case 8 -> 118;
                                                                default -> 90;
                                                        });
                }

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

                bookingTable.setAutoResizeMode(
                                JTable.AUTO_RESIZE_ALL_COLUMNS);

                bookingTable
                                .getSelectionModel()
                                .addListSelectionListener(
                                                event -> {

                                                        if (!event.getValueIsAdjusting()) {

                                                                updateSelectedBooking();
                                                        }
                                                });
        }

        // ================================================================
        // DETAILS
        // ================================================================

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
                                new BoxLayout(
                                                details,
                                                BoxLayout.Y_AXIS));

                JLabel bookingIdTitle = createDetailTitle(
                                "BOOKING ID");

                styleDetailValue(
                                detailBookingId,
                                true);

                details.add(
                                bookingIdTitle);

                details.add(
                                Box.createVerticalStrut(3));

                details.add(
                                detailBookingId);

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

                JScrollPane scroll = new JScrollPane(
                                details);

                scroll.setBorder(null);

                scroll.setOpaque(false);

                scroll.getViewport()
                                .setOpaque(false);

                scroll.setHorizontalScrollBarPolicy(
                                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

                card.add(
                                scroll,
                                BorderLayout.CENTER);

                JPanel actions = new JPanel(
                                new ResponsiveLayouts.Grid(
                                                2,
                                                120,
                                                8));

                actions.setOpaque(false);

                /*
                 * USER cannot perform operational booking actions.
                 *
                 * Staff buttons remain available and are additionally
                 * protected by updateSelected().
                 */

                for (AppButton button : new AppButton[] {
                                confirmButton,
                                payButton,
                                completeButton,
                                cancelButton }) {

                        button.setEnabled(false);
                }

                confirmButton.addActionListener(
                                event -> updateSelected(
                                                "Confirmed",
                                                null,
                                                "Confirm this booking?"));

                payButton.addActionListener(
                                event -> updateSelected(
                                                null,
                                                "Paid",
                                                null));

                completeButton.addActionListener(
                                event -> updateSelected(
                                                "Completed",
                                                null,
                                                null));

                cancelButton.addActionListener(
                                event -> cancelSelectedBooking());

                /*
                 * USER sees no management buttons.
                 *
                 * Keep Cancel only if the business rule allows commuters
                 * to cancel their own active bookings.
                 */

                if (isUser()) {

                        actions.add(
                                        cancelButton);

                } else {

                        actions.add(
                                        confirmButton);

                        actions.add(
                                        payButton);

                        actions.add(
                                        completeButton);

                        actions.add(
                                        cancelButton);
                }

                card.add(
                                actions,
                                BorderLayout.SOUTH);

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

                JLabel titleLabel = createDetailTitle(title);

                styleDetailValue(
                                value,
                                false);

                panel.add(
                                titleLabel);

                panel.add(
                                Box.createVerticalStrut(3));

                panel.add(
                                value);

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

        // ================================================================
        // REFRESH
        // ================================================================

        private void refreshBookings() {

                String query = searchField
                                .getText()
                                .trim()
                                .toLowerCase();

                String selectedStatus = String.valueOf(
                                statusFilter.getSelectedItem());

                String selectedPayment = String.valueOf(
                                paymentFilter.getSelectedItem());

                String previousId = selectedBookingId();

                filteredBookings.clear();

                for (Booking booking : bookings) {

                        /*
                         * --------------------------------------------------------
                         * OWNERSHIP FILTER
                         * --------------------------------------------------------
                         *
                         * USER can ONLY see their own bookings.
                         *
                         * ADMIN and EMPLOYEE can see everything.
                         */

                        if (isUser()
                                        && !booking.ownerEmail
                                                        .equalsIgnoreCase(
                                                                        currentUserEmail)) {

                                continue;
                        }

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

                        boolean matchesPayment = selectedPayment.equals(
                                        "All Payments")
                                        || booking.paymentStatus.equals(
                                                        selectedPayment);

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

                } else {

                        int row = 0;

                        for (int i = 0; i < filteredBookings.size(); i++) {

                                if (filteredBookings
                                                .get(i).bookingId
                                                .equals(previousId)) {

                                        row = i;

                                        break;
                                }
                        }

                        int viewRow = bookingTable
                                        .convertRowIndexToView(row);

                        if (viewRow >= 0
                                        && viewRow < bookingTable.getRowCount()) {

                                bookingTable.setRowSelectionInterval(
                                                viewRow,
                                                viewRow);

                                updateSelectedBooking();
                        }
                }
        }

        // ================================================================
        // SELECTED BOOKING
        // ================================================================

        private String selectedBookingId() {

                Booking booking = selectedBooking();

                return booking == null
                                ? null
                                : booking.bookingId;
        }

        private Booking selectedBooking() {

                int row = bookingTable.getSelectedRow();

                if (row < 0) {

                        return null;
                }

                int modelRow = bookingTable
                                .convertRowIndexToModel(row);

                return modelRow >= 0
                                && modelRow < filteredBookings.size()
                                                ? filteredBookings.get(modelRow)
                                                : null;
        }

        // ================================================================
        // UPDATE SELECTED BOOKING
        // ================================================================

        private void updateSelected(
                        String status,
                        String payment,
                        String confirmMessage) {

                Booking booking = selectedBooking();

                if (booking == null) {

                        return;
                }

                /*
                 * USER PROTECTION
                 *
                 * A USER must never be able to execute management
                 * operations, even if a button becomes enabled accidentally.
                 */

                if (isUser()) {

                        showAccessDenied();

                        return;
                }

                /*
                 * Ownership protection.
                 *
                 * This is mainly defensive because ADMIN/EMPLOYEE see all
                 * bookings. USER is already filtered from other bookings.
                 */

                if (!canManageBooking(booking)) {

                        showAccessDenied();

                        return;
                }

                /*
                 * COMPLETED/CANCELLED BOOKINGS ARE IMMUTABLE.
                 */

                if (isLocked(booking)) {

                        showLockedMessage();

                        return;
                }

                if (confirmMessage != null
                                && JOptionPane.showConfirmDialog(
                                                this,
                                                confirmMessage
                                                                + "\n"
                                                                + booking.bookingId
                                                                + " · "
                                                                + booking.passenger,
                                                "Confirm Booking",
                                                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {

                        return;
                }

                if (status != null) {

                        booking.status = status;
                }

                if (payment != null) {

                        booking.paymentStatus = payment;

                        if (booking.status.equals("Pending")
                                        && payment.equals("Paid")) {

                                booking.status = "Confirmed";
                        }
                }

                refreshBookings();
        }

        // ================================================================
        // STATISTICS
        // ================================================================

        private void updateStatistics() {

                int total = filteredBookings.size();

                int completed = 0;

                int confirmed = 0;

                int pending = 0;

                int cancelled = 0;

                for (Booking booking : filteredBookings) {

                        switch (booking.status) {

                                case "Confirmed" ->
                                        confirmed++;

                                case "Completed" ->
                                        completed++;

                                case "Pending" ->
                                        pending++;

                                case "Cancelled" ->
                                        cancelled++;
                        }
                }

                totalValue.setText(
                                String.valueOf(total));

                completedValue.setText(
                                String.valueOf(completed));

                confirmedValue.setText(
                                String.valueOf(confirmed));

                pendingValue.setText(
                                String.valueOf(pending));

                cancelledValue.setText(
                                String.valueOf(cancelled));
        }

        // ================================================================
        // UPDATE DETAILS
        // ================================================================

        private void updateSelectedBooking() {

                int selectedRow = bookingTable.getSelectedRow();

                if (selectedRow < 0) {

                        clearDetails();

                        return;
                }

                int modelRow = bookingTable
                                .convertRowIndexToModel(
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

                updateActionStates(
                                booking);
        }

        // ================================================================
        // ACTION STATES
        // ================================================================

        private void updateActionStates(
                        Booking booking) {

                boolean open = !isLocked(booking);

                /*
                 * USER:
                 *
                 * - Cannot confirm
                 * - Cannot mark paid
                 * - Cannot complete
                 * - Can only cancel their own open booking
                 */

                if (isUser()) {

                        confirmButton.setEnabled(false);

                        payButton.setEnabled(false);

                        completeButton.setEnabled(false);

                        cancelButton.setEnabled(
                                        isOwnedByCurrentUser(booking)
                                                        && open);

                        return;
                }

                /*
                 * ADMIN / EMPLOYEE
                 */

                confirmButton.setEnabled(
                                open
                                                && booking.status.equals(
                                                                "Pending"));

                payButton.setEnabled(
                                open
                                                && booking.paymentStatus.equals(
                                                                "Unpaid"));

                completeButton.setEnabled(
                                open
                                                && booking.status.equals(
                                                                "Confirmed"));

                cancelButton.setEnabled(
                                open);
        }

        // ================================================================
        // CLEAR DETAILS
        // ================================================================

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

                confirmButton.setEnabled(false);

                payButton.setEnabled(false);

                completeButton.setEnabled(false);

                cancelButton.setEnabled(false);
        }

        // ================================================================
        // STATUS BADGE
        // ================================================================

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

                        case "Completed" ->
                                badgeStatus = AppBadge.Status.INFO;

                        case "Paid" ->
                                badgeStatus = AppBadge.Status.SUCCESS;

                        case "Unpaid" ->
                                badgeStatus = AppBadge.Status.WARNING;

                        case "Refunded" ->
                                badgeStatus = AppBadge.Status.NEUTRAL;

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

        // ================================================================
        // CANCEL BOOKING
        // ================================================================

        private void cancelSelectedBooking() {

                Booking booking = selectedBooking();

                if (booking == null) {

                        return;
                }

                /*
                 * USER can only cancel their own booking.
                 */

                if (isUser()
                                && !isOwnedByCurrentUser(
                                                booking)) {

                        showAccessDenied();

                        return;
                }

                /*
                 * ADMIN / EMPLOYEE can manage all bookings.
                 */

                if (!isUser()
                                && !canManageBooking(
                                                booking)) {

                        showAccessDenied();

                        return;
                }

                /*
                 * Completed and cancelled bookings cannot
                 * be changed.
                 */

                if (isLocked(booking)) {

                        showLockedMessage();

                        return;
                }

                int result = JOptionPane.showConfirmDialog(
                                this,
                                "Cancel booking "
                                                + booking.bookingId
                                                + " for "
                                                + booking.passenger
                                                + "?"
                                                + (booking.paymentStatus.equals(
                                                                "Paid")
                                                                                ? "\nThe payment will be marked as refunded."
                                                                                : ""),
                                "Cancel Booking",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE);

                if (result == JOptionPane.YES_OPTION) {

                        booking.status = "Cancelled";

                        if (booking.paymentStatus.equals(
                                        "Paid")) {

                                booking.paymentStatus = "Refunded";
                        }

                        refreshBookings();
                }
        }

        // ================================================================
        // CREATE BOOKING
        // ================================================================

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
                                                480,
                                                620));

                dialog.setSize(
                                520,
                                640);

                dialog.setLocationRelativeTo(
                                this);

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

                // ------------------------------------------------------------
                // PASSENGER
                // ------------------------------------------------------------

                JTextField passengerField = new JTextField();

                if (isUser()) {

                        /*
                         * USER cannot type another passenger's name.
                         *
                         * The booking belongs to the authenticated account.
                         */

                        passengerField.setText(
                                        getCurrentPassengerName());

                        passengerField.setEditable(false);

                        passengerField.setBackground(
                                        BussinTheme.SURFACE_ALT);

                }

                // ------------------------------------------------------------
                // TRIP
                // ------------------------------------------------------------

                JComboBox<String> tripCombo = new JComboBox<>(
                                new String[] {
                                                "Trip 102 • 10:00 AM",
                                                "Trip 114 • 10:30 AM",
                                                "Trip 121 • 11:00 AM"
                                });

                // ------------------------------------------------------------
                // ROUTE
                // ------------------------------------------------------------

                JComboBox<String> routeCombo = new JComboBox<>(
                                new String[] {
                                                "Manila → Batangas",
                                                "Manila → Lucena",
                                                "Manila → Nasugbu"
                                });

                routeCombo.setEnabled(false);

                tripCombo.addActionListener(
                                event -> routeCombo.setSelectedIndex(
                                                tripCombo.getSelectedIndex()));

                // ------------------------------------------------------------
                // SEAT
                // ------------------------------------------------------------

                JTextField seatField = new JTextField();

                // ------------------------------------------------------------
                // FARE
                // ------------------------------------------------------------

                JTextField fareField = new JTextField();

                // ------------------------------------------------------------
                // PAYMENT
                // ------------------------------------------------------------

                JComboBox<String> paymentCombo = new JComboBox<>(
                                new String[] {
                                                "Unpaid",
                                                "Paid"
                                });

                /*
                 * USER should not be allowed to mark their own booking
                 * as paid manually.
                 *
                 * Payment processing belongs to BUSSIN staff.
                 */

                if (isUser()) {

                        paymentCombo.setSelectedItem(
                                        "Unpaid");

                        paymentCombo.setEnabled(false);
                }

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
                                "Fare (₱)",
                                fareField);

                addFormRow(
                                content,
                                gbc,
                                row,
                                "Payment",
                                paymentCombo);

                // ------------------------------------------------------------
                // TITLE
                // ------------------------------------------------------------

                JLabel title = new JLabel(
                                isUser()
                                                ? "Create My Booking"
                                                : "Create New Booking");

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

                // ------------------------------------------------------------
                // BUTTONS
                // ------------------------------------------------------------

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

                                        // ------------------------------------------------
                                        // USER OWNERSHIP
                                        // ------------------------------------------------

                                        if (isUser()) {

                                                /*
                                                 * Force the passenger name to the
                                                 * authenticated user.
                                                 */

                                                passenger = getCurrentPassengerName();

                                                if (passenger.isBlank()) {

                                                        JOptionPane.showMessageDialog(
                                                                        dialog,
                                                                        "Your account name could not be determined.",
                                                                        "Invalid Account",
                                                                        JOptionPane.WARNING_MESSAGE);

                                                        return;
                                                }
                                        }

                                        // ------------------------------------------------
                                        // VALIDATION
                                        // ------------------------------------------------

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

                                        /*
                                         * USER bookings are ALWAYS owned by the
                                         * authenticated user.
                                         */

                                        String ownerEmail = currentUserEmail;

                                        String payment = isUser()
                                                        ? "Unpaid"
                                                        : String.valueOf(
                                                                        paymentCombo
                                                                                        .getSelectedItem());

                                        String status = payment.equals("Paid")
                                                        ? "Confirmed"
                                                        : "Pending";

                                        String id = generateBookingId();

                                        bookings.add(
                                                        new Booking(
                                                                        id,
                                                                        ownerEmail,
                                                                        passenger,
                                                                        String.valueOf(
                                                                                        tripCombo
                                                                                                        .getSelectedItem()),
                                                                        String.valueOf(
                                                                                        routeCombo
                                                                                                        .getSelectedItem()),
                                                                        seat,
                                                                        fare,
                                                                        payment,
                                                                        status,
                                                                        LocalDateTime.now()));

                                        refreshBookings();

                                        dialog.dispose();

                                        JOptionPane.showMessageDialog(
                                                        this,
                                                        "Booking "
                                                                        + id
                                                                        + " was created successfully.",
                                                        "Booking Created",
                                                        JOptionPane.INFORMATION_MESSAGE);
                                });

                buttons.add(
                                cancel);

                buttons.add(
                                save);

                // ------------------------------------------------------------
                // ROOT
                // ------------------------------------------------------------

                root.add(
                                titlePanel,
                                BorderLayout.NORTH);

                root.add(
                                content,
                                BorderLayout.CENTER);

                root.add(
                                buttons,
                                BorderLayout.SOUTH);

                dialog.setContentPane(
                                root);

                dialog.setVisible(true);
        }

        // ================================================================
        // FORM ROW
        // ================================================================

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

        // ================================================================
        // BOOKING ID
        // ================================================================

        private String generateBookingId() {

                int highest = 1000;

                for (Booking booking : bookings) {

                        try {

                                int number = Integer.parseInt(
                                                booking.bookingId
                                                                .substring(3));

                                highest = Math.max(
                                                highest,
                                                number);

                        } catch (NumberFormatException ignored) {

                                // Ignore unexpected mock IDs.
                        }
                }

                return "BK-"
                                + (highest + 1);
        }

        // ================================================================
        // OWNERSHIP
        // ================================================================

        private boolean isOwnedByCurrentUser(
                        Booking booking) {

                return booking != null
                                && booking.ownerEmail
                                                .equalsIgnoreCase(
                                                                currentUserEmail);
        }

        private boolean canManageBooking(
                        Booking booking) {

                if (booking == null) {

                        return false;
                }

                /*
                 * ADMIN and EMPLOYEE are operational staff.
                 *
                 * USER never reaches this as a management operation.
                 */

                return isStaff();
        }

        // ================================================================
        // LOCKED BOOKINGS
        // ================================================================

        private boolean isLocked(
                        Booking booking) {

                if (booking == null) {

                        return true;
                }

                return "Completed".equals(
                                booking.status)
                                || "Cancelled".equals(
                                                booking.status);
        }

        private void showLockedMessage() {

                JOptionPane.showMessageDialog(
                                this,
                                "Completed or cancelled bookings cannot be modified.",
                                "Booking Locked",
                                JOptionPane.WARNING_MESSAGE);
        }

        private void showAccessDenied() {

                JOptionPane.showMessageDialog(
                                this,
                                "You do not have permission to modify this booking.",
                                "Access Denied",
                                JOptionPane.WARNING_MESSAGE);
        }

        // ================================================================
        // CURRENT PASSENGER NAME
        // ================================================================

        private String getCurrentPassengerName() {

                /*
                 * For now this is mock frontend data.
                 *
                 * Later, the backend/Firebase profile will provide the
                 * actual authenticated user's display name.
                 */

                if (currentUserEmail == null
                                || currentUserEmail.isBlank()) {

                        return "";
                }

                if (currentUserEmail.equalsIgnoreCase(
                                "admin@bussin.com")) {

                        return "Administrator";
                }

                if (currentUserEmail.equalsIgnoreCase(
                                "employee@bussin.com")) {

                        return "Employee";
                }

                if (currentUserEmail.equalsIgnoreCase(
                                "user@bussin.com")) {

                        return "User";
                }

                /*
                 * Registered USER accounts do not yet have a connected
                 * profile source in this mock frontend.
                 *
                 * Use the email as a temporary display name rather than
                 * allowing the user to enter another passenger.
                 */

                int at = currentUserEmail.indexOf('@');

                if (at > 0) {

                        String localPart = currentUserEmail.substring(
                                        0,
                                        at);

                        return formatName(
                                        localPart);
                }

                return currentUserEmail;
        }

        private String formatName(
                        String value) {

                if (value == null
                                || value.isBlank()) {

                        return "";
                }

                String[] parts = value.split("[._-]+");

                StringBuilder result = new StringBuilder();

                for (String part : parts) {

                        if (part.isBlank()) {

                                continue;
                        }

                        if (result.length() > 0) {

                                result.append(" ");
                        }

                        result.append(
                                        Character.toUpperCase(
                                                        part.charAt(0)));

                        if (part.length() > 1) {

                                result.append(
                                                part.substring(1)
                                                                .toLowerCase());
                        }
                }

                return result.toString();
        }

        // ================================================================
        // ROLE NORMALIZATION
        // ================================================================

        private String normalizeRole(
                        String role) {

                if (role == null
                                || role.isBlank()) {

                        return "USER";
                }

                return role
                                .trim()
                                .toUpperCase();
        }

        // ================================================================
        // BOOKING MODEL
        // ================================================================

        private static class Booking {

                private final String bookingId;

                /*
                 * The account that owns the booking.
                 */
                private final String ownerEmail;

                private final String passenger;

                private final String trip;

                private final String route;

                private final String seat;

                private final double fare;

                private String paymentStatus;

                private String status;

                private final LocalDateTime createdAt;

                private Booking(
                                String bookingId,
                                String ownerEmail,
                                String passenger,
                                String trip,
                                String route,
                                String seat,
                                double fare,
                                String paymentStatus,
                                String status,
                                LocalDateTime createdAt) {

                        this.bookingId = bookingId;

                        this.ownerEmail = ownerEmail;

                        this.passenger = passenger;

                        this.trip = trip;

                        this.route = route;

                        this.seat = seat;

                        this.fare = fare;

                        this.paymentStatus = paymentStatus;

                        this.status = status;

                        this.createdAt = createdAt;
                }
        }

        // ================================================================
        // TABLE MODEL
        // ================================================================

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
                                int rowIndex,
                                int columnIndex) {

                        Booking booking = filteredBookings.get(
                                        rowIndex);

                        return switch (columnIndex) {

                                case 0 ->
                                        booking.bookingId;

                                case 1 ->
                                        booking.passenger;

                                case 2 ->
                                        booking.trip.replace(
                                                        "Trip ",
                                                        "");

                                case 3 ->
                                        booking.route;

                                case 4 ->
                                        booking.seat;

                                case 5 ->
                                        String.format(
                                                        "₱%,.2f",
                                                        booking.fare);

                                case 6 ->
                                        booking.status;

                                case 7 ->
                                        booking.paymentStatus;

                                case 8 ->
                                        booking.createdAt.format(
                                                        TABLE_TIME_FORMAT);

                                default ->
                                        "";
                        };
                }

                @Override
                public boolean isCellEditable(
                                int rowIndex,
                                int columnIndex) {

                        return false;
                }
        }

        // ================================================================
        // STATUS CELL RENDERER
        // ================================================================

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

                                case "Completed" ->
                                        badgeStatus = AppBadge.Status.INFO;

                                case "Paid" ->
                                        badgeStatus = AppBadge.Status.SUCCESS;

                                case "Unpaid" ->
                                        badgeStatus = AppBadge.Status.WARNING;

                                case "Refunded" ->
                                        badgeStatus = AppBadge.Status.NEUTRAL;

                                default ->
                                        badgeStatus = AppBadge.Status.NEUTRAL;
                        }

                        AppBadge badge = new AppBadge(
                                        status,
                                        badgeStatus);

                        JPanel wrapper = new JPanel(
                                        new BorderLayout());

                        wrapper.setOpaque(true);

                        wrapper.setBackground(
                                        isSelected
                                                        ? table.getSelectionBackground()
                                                        : table.getBackground());

                        wrapper.setBorder(
                                        BorderFactory.createEmptyBorder(
                                                        7,
                                                        0,
                                                        7,
                                                        0));

                        JPanel left = new JPanel(
                                        new FlowLayout(
                                                        FlowLayout.LEFT,
                                                        0,
                                                        0));

                        left.setOpaque(false);

                        left.add(
                                        badge);

                        wrapper.add(
                                        left,
                                        BorderLayout.WEST);

                        return wrapper;
                }
        }
}