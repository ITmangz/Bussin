package com.bussin.desktop.ui.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.File;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.bussin.desktop.reports.ReportExporter;
import com.bussin.desktop.ui.components.AppBadge;
import com.bussin.desktop.ui.components.AppButton;
import com.bussin.desktop.ui.components.AppCard;
import com.bussin.desktop.ui.theme.BussinTheme;

public class ReportScreen extends JPanel {

        private JComboBox<String> periodFilter;
        private JPanel contentPanel;
        private ReportData reportData;
        private boolean refreshing = false;

        public ReportScreen() {
                setLayout(new BorderLayout());
                setBackground(BussinTheme.BACKGROUND);

                loadAllTimeData();
                initializeUI();
        }

        // ============================================================
        // UI
        // ============================================================

        private void initializeUI() {

                removeAll();

                JPanel root = new JPanel(new BorderLayout());
                root.setBackground(BussinTheme.BACKGROUND);

                // --------------------------------------------------------
                // Header
                // --------------------------------------------------------

                JPanel header = new JPanel(new BorderLayout());
                header.setOpaque(false);
                header.setBorder(
                                new EmptyBorder(24, 28, 18, 28));

                JPanel titlePanel = new JPanel();
                titlePanel.setOpaque(false);
                titlePanel.setLayout(
                                new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

                JLabel title = new JLabel("Reports & Analytics");
                title.setFont(BussinTheme.BODY);
                title.setForeground(BussinTheme.TEXT_PRIMARY);

                JLabel subtitle = new JLabel(
                                "Monitor BUSSIN operations and performance.");
                subtitle.setFont(BussinTheme.SMALL);
                subtitle.setForeground(BussinTheme.TEXT_SECONDARY);

                titlePanel.add(title);
                titlePanel.add(Box.createVerticalStrut(4));
                titlePanel.add(subtitle);

                header.add(titlePanel, BorderLayout.WEST);

                // --------------------------------------------------------
                // Controls
                // --------------------------------------------------------

                JPanel controls = new JPanel(
                                new FlowLayout(
                                                FlowLayout.RIGHT,
                                                8,
                                                0));

                controls.setOpaque(false);

                JLabel periodLabel = new JLabel("Period");
                periodLabel.setFont(BussinTheme.SMALL_BOLD);
                periodLabel.setForeground(
                                BussinTheme.TEXT_SECONDARY);

                periodFilter = new JComboBox<>(
                                new String[] {
                                                "Today",
                                                "This Week",
                                                "This Month",
                                                "All Time"
                                });

                periodFilter.setFont(BussinTheme.SMALL);
                periodFilter.setPreferredSize(
                                new Dimension(135, 34));

                periodFilter.addActionListener(event -> {

                        if (refreshing) {
                                return;
                        }

                        String selected = (String) periodFilter.getSelectedItem();

                        updateReportForPeriod(selected);
                });

                AppButton copyButton = new AppButton(
                                "Copy Report",
                                AppButton.Variant.SECONDARY);

                copyButton.addActionListener(
                                event -> copyReportToClipboard());

                AppButton exportButton = new AppButton(
                                "Export Report",
                                AppButton.Variant.PRIMARY);

                exportButton.addActionListener(
                                event -> showExportDialog());

                controls.add(periodLabel);
                controls.add(periodFilter);
                controls.add(copyButton);
                controls.add(exportButton);

                header.add(
                                controls,
                                BorderLayout.EAST);

                root.add(
                                header,
                                BorderLayout.NORTH);

                // --------------------------------------------------------
                // Content
                // --------------------------------------------------------

                contentPanel = new JPanel();

                contentPanel.setLayout(
                                new BoxLayout(
                                                contentPanel,
                                                BoxLayout.Y_AXIS));

                contentPanel.setBackground(
                                BussinTheme.BACKGROUND);

                contentPanel.setBorder(
                                new EmptyBorder(
                                                0,
                                                28,
                                                28,
                                                28));

                JScrollPane scrollPane = new JScrollPane(contentPanel);

                scrollPane.setBorder(null);
                scrollPane.setBackground(
                                BussinTheme.BACKGROUND);

                scrollPane.getViewport().setBackground(
                                BussinTheme.BACKGROUND);

                scrollPane.setHorizontalScrollBarPolicy(
                                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

                scrollPane.setVerticalScrollBarPolicy(
                                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

                root.add(
                                scrollPane,
                                BorderLayout.CENTER);

                add(
                                root,
                                BorderLayout.CENTER);

                buildReportContent();

                revalidate();
                repaint();
        }

        // ============================================================
        // REPORT CONTENT
        // ============================================================

        private void buildReportContent() {

                contentPanel.removeAll();

                // --------------------------------------------------------
                // Operational Overview
                // --------------------------------------------------------

                contentPanel.add(
                                createSectionTitle(
                                                "Operational Overview"));

                contentPanel.add(
                                Box.createVerticalStrut(12));

                JPanel overviewGrid = createGridPanel(4);

                overviewGrid.add(
                                createMetricCard(
                                                "Total Bookings",
                                                String.valueOf(
                                                                reportData.totalBookings),
                                                AppBadge.Status.INFO));

                overviewGrid.add(
                                createMetricCard(
                                                "Completed Trips",
                                                String.valueOf(
                                                                reportData.completedTrips),
                                                AppBadge.Status.SUCCESS));

                overviewGrid.add(
                                createMetricCard(
                                                "Active Buses",
                                                String.valueOf(
                                                                reportData.activeBuses),
                                                AppBadge.Status.SUCCESS));

                overviewGrid.add(
                                createMetricCard(
                                                "Active Employees",
                                                String.valueOf(
                                                                reportData.activeEmployees),
                                                AppBadge.Status.INFO));

                contentPanel.add(overviewGrid);

                contentPanel.add(
                                Box.createVerticalStrut(24));

                // --------------------------------------------------------
                // Operational Analytics
                // --------------------------------------------------------

                contentPanel.add(
                                createSectionTitle(
                                                "Operational Analytics"));

                contentPanel.add(
                                Box.createVerticalStrut(12));

                JPanel analyticsGrid = createGridPanel(2);

                analyticsGrid.add(
                                createBookingDistributionCard());

                analyticsGrid.add(
                                createBusDistributionCard());

                contentPanel.add(analyticsGrid);

                contentPanel.add(
                                Box.createVerticalStrut(24));

                // --------------------------------------------------------
                // Detailed Summary
                // --------------------------------------------------------

                contentPanel.add(
                                createSectionTitle(
                                                "Detailed Summary"));

                contentPanel.add(
                                Box.createVerticalStrut(12));

                JPanel detailGrid = createGridPanel(3);

                detailGrid.add(
                                createBookingOverviewCard());

                detailGrid.add(
                                createRouteSummaryCard());

                detailGrid.add(
                                createEmployeeSummaryCard());

                contentPanel.add(detailGrid);

                contentPanel.add(
                                Box.createVerticalStrut(24));

                // --------------------------------------------------------
                // Current Report Period
                // --------------------------------------------------------

                JPanel periodCard = new AppCard();

                periodCard.setLayout(
                                new BorderLayout());

                periodCard.setBorder(
                                new EmptyBorder(
                                                16,
                                                18,
                                                16,
                                                18));

                JLabel periodTitle = new JLabel(
                                "Current Report Period");

                periodTitle.setFont(
                                BussinTheme.SMALL_BOLD);

                periodTitle.setForeground(
                                BussinTheme.TEXT_SECONDARY);

                JLabel periodValue = new JLabel(
                                getSelectedPeriod());

                periodValue.setFont(
                                BussinTheme.BODY);

                periodValue.setForeground(
                                BussinTheme.TEXT_PRIMARY);

                JPanel periodText = new JPanel();

                periodText.setOpaque(false);

                periodText.setLayout(
                                new BoxLayout(
                                                periodText,
                                                BoxLayout.Y_AXIS));

                periodText.add(periodTitle);
                periodText.add(
                                Box.createVerticalStrut(4));
                periodText.add(periodValue);

                periodCard.add(
                                periodText,
                                BorderLayout.WEST);

                contentPanel.add(periodCard);

                contentPanel.revalidate();
                contentPanel.repaint();
        }

        // ============================================================
        // METRIC CARD
        // ============================================================

        private JPanel createMetricCard(
                        String title,
                        String value,
                        AppBadge.Status status) {

                AppCard card = new AppCard();

                card.setLayout(
                                new BorderLayout(
                                                14,
                                                0));

                // No visible border.
                card.setBorder(
                                new EmptyBorder(
                                                18,
                                                18,
                                                18,
                                                18));

                JPanel indicator = new JPanel();

                indicator.setPreferredSize(
                                new Dimension(5, 45));

                indicator.setBackground(
                                getStatusColor(status));

                card.add(
                                indicator,
                                BorderLayout.WEST);

                JPanel textPanel = new JPanel();

                textPanel.setOpaque(false);

                textPanel.setLayout(
                                new BoxLayout(
                                                textPanel,
                                                BoxLayout.Y_AXIS));

                JLabel titleLabel = new JLabel(title);

                titleLabel.setFont(
                                BussinTheme.SMALL);

                titleLabel.setForeground(
                                BussinTheme.TEXT_SECONDARY);

                JLabel valueLabel = new JLabel(value);

                valueLabel.setFont(
                                BussinTheme.BODY);

                valueLabel.setForeground(
                                BussinTheme.TEXT_PRIMARY);

                textPanel.add(titleLabel);

                textPanel.add(
                                Box.createVerticalStrut(5));

                textPanel.add(valueLabel);

                card.add(
                                textPanel,
                                BorderLayout.CENTER);

                return card;
        }

        private Color getStatusColor(
                        AppBadge.Status status) {

                if (status == AppBadge.Status.SUCCESS) {
                        return new Color(46, 125, 50);
                }

                if (status == AppBadge.Status.WARNING) {
                        return new Color(245, 158, 11);
                }

                if (status == AppBadge.Status.DANGER) {
                        return BussinTheme.RED;
                }

                return new Color(59, 130, 246);
        }

        // ============================================================
        // BOOKING DISTRIBUTION
        // ============================================================

        private JPanel createBookingDistributionCard() {

                AppCard card = createAnalyticsCard(
                                "Booking Distribution",
                                "Booking status for the selected period.");

                JPanel body = new JPanel();

                body.setOpaque(false);

                body.setLayout(
                                new BoxLayout(
                                                body,
                                                BoxLayout.Y_AXIS));

                body.add(
                                createProgressRow(
                                                "Pending",
                                                reportData.pendingBookings,
                                                reportData.totalBookings));

                body.add(
                                Box.createVerticalStrut(10));

                body.add(
                                createProgressRow(
                                                "Confirmed",
                                                reportData.confirmedBookings,
                                                reportData.totalBookings));

                body.add(
                                Box.createVerticalStrut(10));

                body.add(
                                createProgressRow(
                                                "Completed",
                                                reportData.completedBookings,
                                                reportData.totalBookings));

                body.add(
                                Box.createVerticalStrut(10));

                body.add(
                                createProgressRow(
                                                "Cancelled",
                                                reportData.cancelledBookings,
                                                reportData.totalBookings));

                card.add(
                                body,
                                BorderLayout.CENTER);

                return card;
        }

        // ============================================================
        // BUS DISTRIBUTION
        // ============================================================

        private JPanel createBusDistributionCard() {

                AppCard card = createAnalyticsCard(
                                "Bus Distribution",
                                "Current fleet status.");

                JPanel body = new JPanel();

                body.setOpaque(false);

                body.setLayout(
                                new BoxLayout(
                                                body,
                                                BoxLayout.Y_AXIS));

                int totalBuses = reportData.availableBuses
                                + reportData.onTripBuses
                                + reportData.maintenanceBuses
                                + reportData.inactiveBuses;

                body.add(
                                createProgressRow(
                                                "Available",
                                                reportData.availableBuses,
                                                totalBuses));

                body.add(
                                Box.createVerticalStrut(10));

                body.add(
                                createProgressRow(
                                                "On Trip",
                                                reportData.onTripBuses,
                                                totalBuses));

                body.add(
                                Box.createVerticalStrut(10));

                body.add(
                                createProgressRow(
                                                "Maintenance",
                                                reportData.maintenanceBuses,
                                                totalBuses));

                body.add(
                                Box.createVerticalStrut(10));

                body.add(
                                createProgressRow(
                                                "Inactive",
                                                reportData.inactiveBuses,
                                                totalBuses));

                card.add(
                                body,
                                BorderLayout.CENTER);

                return card;
        }

        // ============================================================
        // PROGRESS ROW
        // ============================================================

        private JPanel createProgressRow(
                        String label,
                        int value,
                        int total) {

                JPanel row = new JPanel(
                                new BorderLayout(
                                                10,
                                                4));

                row.setOpaque(false);

                JLabel labelComponent = new JLabel(label);

                labelComponent.setFont(
                                BussinTheme.SMALL);

                labelComponent.setForeground(
                                BussinTheme.TEXT_PRIMARY);

                JLabel valueComponent = new JLabel(
                                value + " / " + total);

                valueComponent.setFont(
                                BussinTheme.SMALL_BOLD);

                valueComponent.setForeground(
                                BussinTheme.TEXT_SECONDARY);

                JProgressBar progressBar = new JProgressBar(
                                0,
                                Math.max(total, 1));

                progressBar.setValue(
                                Math.min(
                                                value,
                                                Math.max(total, 1)));

                progressBar.setStringPainted(false);

                progressBar.setPreferredSize(
                                new Dimension(100, 8));

                JPanel top = new JPanel(
                                new BorderLayout());

                top.setOpaque(false);

                top.add(
                                labelComponent,
                                BorderLayout.WEST);

                top.add(
                                valueComponent,
                                BorderLayout.EAST);

                JPanel container = new JPanel();

                container.setOpaque(false);

                container.setLayout(
                                new BoxLayout(
                                                container,
                                                BoxLayout.Y_AXIS));

                container.add(top);

                container.add(
                                Box.createVerticalStrut(5));

                container.add(progressBar);

                row.add(
                                container,
                                BorderLayout.CENTER);

                return row;
        }

        // ============================================================
        // BOOKING OVERVIEW
        // ============================================================

        private JPanel createBookingOverviewCard() {

                AppCard card = createAnalyticsCard(
                                "Booking Overview",
                                "Summary of booking activity.");

                JPanel body = createKeyValuePanel();

                addKeyValue(
                                body,
                                "Total Bookings",
                                reportData.totalBookings);

                addKeyValue(
                                body,
                                "Pending",
                                reportData.pendingBookings);

                addKeyValue(
                                body,
                                "Confirmed",
                                reportData.confirmedBookings);

                addKeyValue(
                                body,
                                "Completed",
                                reportData.completedBookings);

                addKeyValue(
                                body,
                                "Cancelled",
                                reportData.cancelledBookings);

                card.add(
                                body,
                                BorderLayout.CENTER);

                return card;
        }

        // ============================================================
        // ROUTE SUMMARY
        // ============================================================

        private JPanel createRouteSummaryCard() {

                AppCard card = createAnalyticsCard(
                                "Route Summary",
                                "Current route information.");

                JPanel body = createKeyValuePanel();

                addKeyValue(
                                body,
                                "Active Routes",
                                reportData.activeRoutes);

                addKeyValue(
                                body,
                                "Inactive Routes",
                                reportData.inactiveRoutes);

                addKeyValue(
                                body,
                                "Total Distance",
                                reportData.totalDistance + " km");

                addKeyValue(
                                body,
                                "Average Fare",
                                "₱" + reportData.averageFare);

                card.add(
                                body,
                                BorderLayout.CENTER);

                return card;
        }

        // ============================================================
        // EMPLOYEE SUMMARY
        // ============================================================

        private JPanel createEmployeeSummaryCard() {

                AppCard card = createAnalyticsCard(
                                "Employee Summary",
                                "Current employee distribution.");

                JPanel body = createKeyValuePanel();

                addKeyValue(
                                body,
                                "Total Employees",
                                reportData.totalEmployees);

                addKeyValue(
                                body,
                                "Active Employees",
                                reportData.activeEmployees);

                addKeyValue(
                                body,
                                "Inactive Employees",
                                reportData.inactiveEmployees);

                addKeyValue(
                                body,
                                "Bus Drivers",
                                reportData.busDrivers);

                addKeyValue(
                                body,
                                "Dispatchers",
                                reportData.dispatchers);

                addKeyValue(
                                body,
                                "Ticketing Staff",
                                reportData.ticketingStaff);

                addKeyValue(
                                body,
                                "Fleet Supervisors",
                                reportData.fleetSupervisors);

                card.add(
                                body,
                                BorderLayout.CENTER);

                return card;
        }

        // ============================================================
        // ANALYTICS CARD
        // ============================================================

        private AppCard createAnalyticsCard(
                        String title,
                        String subtitle) {

                AppCard card = new AppCard();

                card.setLayout(
                                new BorderLayout(
                                                0,
                                                14));

                // No visible border.
                card.setBorder(
                                new EmptyBorder(
                                                18,
                                                18,
                                                18,
                                                18));

                JPanel header = new JPanel();

                header.setOpaque(false);

                header.setLayout(
                                new BoxLayout(
                                                header,
                                                BoxLayout.Y_AXIS));

                JLabel titleLabel = new JLabel(title);

                titleLabel.setFont(
                                BussinTheme.BODY);

                titleLabel.setForeground(
                                BussinTheme.TEXT_PRIMARY);

                JLabel subtitleLabel = new JLabel(subtitle);

                subtitleLabel.setFont(
                                BussinTheme.SMALL);

                subtitleLabel.setForeground(
                                BussinTheme.TEXT_SECONDARY);

                header.add(titleLabel);

                header.add(
                                Box.createVerticalStrut(3));

                header.add(subtitleLabel);

                card.add(
                                header,
                                BorderLayout.NORTH);

                return card;
        }

        // ============================================================
        // KEY VALUE
        // ============================================================

        private JPanel createKeyValuePanel() {

                JPanel panel = new JPanel();

                panel.setOpaque(false);

                panel.setLayout(
                                new BoxLayout(
                                                panel,
                                                BoxLayout.Y_AXIS));

                return panel;
        }

        private void addKeyValue(
                        JPanel panel,
                        String key,
                        Object value) {

                JPanel row = new JPanel(
                                new BorderLayout());

                row.setOpaque(false);

                JLabel keyLabel = new JLabel(key);

                keyLabel.setFont(
                                BussinTheme.SMALL);

                keyLabel.setForeground(
                                BussinTheme.TEXT_SECONDARY);

                JLabel valueLabel = new JLabel(
                                String.valueOf(value));

                valueLabel.setFont(
                                BussinTheme.SMALL_BOLD);

                valueLabel.setForeground(
                                BussinTheme.TEXT_PRIMARY);

                row.add(
                                keyLabel,
                                BorderLayout.WEST);

                row.add(
                                valueLabel,
                                BorderLayout.EAST);

                panel.add(row);

                panel.add(
                                Box.createVerticalStrut(9));
        }

        // ============================================================
        // SECTION TITLE
        // ============================================================

        private JPanel createSectionTitle(
                        String text) {

                JPanel panel = new JPanel(
                                new BorderLayout());

                panel.setOpaque(false);

                JLabel label = new JLabel(text);

                label.setFont(
                                BussinTheme.BODY);

                label.setForeground(
                                BussinTheme.TEXT_PRIMARY);

                panel.add(
                                label,
                                BorderLayout.WEST);

                return panel;
        }

        // ============================================================
        // GRID
        // ============================================================

        private JPanel createGridPanel(
                        int columns) {

                JPanel grid = new JPanel(
                                new GridLayout(
                                                1,
                                                columns,
                                                14,
                                                14));

                grid.setOpaque(false);

                return grid;
        }

        // ============================================================
        // PERIOD
        // ============================================================

        private void updateReportForPeriod(
                        String period) {

                if (period == null) {
                        return;
                }

                switch (period) {

                        case "Today":
                                loadTodayData();
                                break;

                        case "This Week":
                                loadWeekData();
                                break;

                        case "This Month":
                                loadMonthData();
                                break;

                        case "All Time":
                        default:
                                loadAllTimeData();
                                break;
                }

                refreshReport(period);
        }

        private void refreshReport(
                        String selectedPeriod) {

                refreshing = true;

                initializeUI();

                if (periodFilter != null) {
                        periodFilter.setSelectedItem(
                                        selectedPeriod);
                }

                refreshing = false;

                revalidate();
                repaint();
        }

        private String getSelectedPeriod() {

                if (periodFilter == null) {
                        return "All Time";
                }

                Object selected = periodFilter.getSelectedItem();

                if (selected == null) {
                        return "All Time";
                }

                return selected.toString();
        }

        // ============================================================
        // MOCK DATA
        // ============================================================

        private void loadTodayData() {

                reportData = new ReportData();

                reportData.totalBookings = 18;
                reportData.completedTrips = 11;
                reportData.activeBuses = 6;
                reportData.activeEmployees = 4;

                reportData.pendingBookings = 3;
                reportData.confirmedBookings = 4;
                reportData.completedBookings = 11;
                reportData.cancelledBookings = 0;

                reportData.availableBuses = 6;
                reportData.onTripBuses = 3;
                reportData.maintenanceBuses = 2;
                reportData.inactiveBuses = 1;

                reportData.activeRoutes = 4;
                reportData.inactiveRoutes = 1;
                reportData.totalDistance = 245;
                reportData.averageFare = 485;

                reportData.totalEmployees = 5;
                reportData.activeEmployees = 4;
                reportData.inactiveEmployees = 1;

                reportData.busDrivers = 2;
                reportData.dispatchers = 1;
                reportData.ticketingStaff = 1;
                reportData.fleetSupervisors = 1;
        }

        private void loadWeekData() {

                reportData = new ReportData();

                reportData.totalBookings = 67;
                reportData.completedTrips = 45;
                reportData.activeBuses = 6;
                reportData.activeEmployees = 4;

                reportData.pendingBookings = 7;
                reportData.confirmedBookings = 15;
                reportData.completedBookings = 45;
                reportData.cancelledBookings = 5;

                reportData.availableBuses = 6;
                reportData.onTripBuses = 3;
                reportData.maintenanceBuses = 2;
                reportData.inactiveBuses = 1;

                reportData.activeRoutes = 4;
                reportData.inactiveRoutes = 1;
                reportData.totalDistance = 785;
                reportData.averageFare = 495;

                reportData.totalEmployees = 5;
                reportData.activeEmployees = 4;
                reportData.inactiveEmployees = 1;

                reportData.busDrivers = 2;
                reportData.dispatchers = 1;
                reportData.ticketingStaff = 1;
                reportData.fleetSupervisors = 1;
        }

        private void loadMonthData() {

                reportData = new ReportData();

                reportData.totalBookings = 128;
                reportData.completedTrips = 84;
                reportData.activeBuses = 6;
                reportData.activeEmployees = 4;

                reportData.pendingBookings = 12;
                reportData.confirmedBookings = 24;
                reportData.completedBookings = 84;
                reportData.cancelledBookings = 8;

                reportData.availableBuses = 6;
                reportData.onTripBuses = 3;
                reportData.maintenanceBuses = 2;
                reportData.inactiveBuses = 1;

                reportData.activeRoutes = 4;
                reportData.inactiveRoutes = 1;
                reportData.totalDistance = 1235;
                reportData.averageFare = 508;

                reportData.totalEmployees = 5;
                reportData.activeEmployees = 4;
                reportData.inactiveEmployees = 1;

                reportData.busDrivers = 2;
                reportData.dispatchers = 1;
                reportData.ticketingStaff = 1;
                reportData.fleetSupervisors = 1;
        }

        private void loadAllTimeData() {

                reportData = new ReportData();

                reportData.totalBookings = 128;
                reportData.completedTrips = 84;
                reportData.activeBuses = 6;
                reportData.activeEmployees = 4;

                reportData.pendingBookings = 12;
                reportData.confirmedBookings = 24;
                reportData.completedBookings = 84;
                reportData.cancelledBookings = 8;

                reportData.availableBuses = 6;
                reportData.onTripBuses = 3;
                reportData.maintenanceBuses = 2;
                reportData.inactiveBuses = 1;

                reportData.activeRoutes = 4;
                reportData.inactiveRoutes = 1;
                reportData.totalDistance = 1235;
                reportData.averageFare = 508;

                reportData.totalEmployees = 5;
                reportData.activeEmployees = 4;
                reportData.inactiveEmployees = 1;

                reportData.busDrivers = 2;
                reportData.dispatchers = 1;
                reportData.ticketingStaff = 1;
                reportData.fleetSupervisors = 1;
        }

        // ============================================================
        // EXPORT DIALOG
        // ============================================================

        private void showExportDialog() {

                JDialog dialog = new JDialog(
                                SwingUtilities.getWindowAncestor(this),
                                "Export Report",
                                Dialog.ModalityType.APPLICATION_MODAL);

                dialog.setLayout(
                                new BorderLayout());

                dialog.setSize(
                                430,
                                270);

                dialog.setLocationRelativeTo(this);

                JPanel content = new JPanel();

                content.setBackground(
                                BussinTheme.BACKGROUND);

                content.setBorder(
                                new EmptyBorder(
                                                24,
                                                24,
                                                24,
                                                24));

                content.setLayout(
                                new BoxLayout(
                                                content,
                                                BoxLayout.Y_AXIS));

                JLabel title = new JLabel("Export Report");

                title.setFont(
                                BussinTheme.BODY);

                title.setForeground(
                                BussinTheme.TEXT_PRIMARY);

                JLabel description = new JLabel(
                                "Choose a format for the selected report.");

                description.setFont(
                                BussinTheme.SMALL);

                description.setForeground(
                                BussinTheme.TEXT_SECONDARY);

                content.add(title);

                content.add(
                                Box.createVerticalStrut(5));

                content.add(description);

                content.add(
                                Box.createVerticalStrut(20));

                JLabel formatLabel = new JLabel("Export Format");

                formatLabel.setFont(
                                BussinTheme.SMALL_BOLD);

                formatLabel.setForeground(
                                BussinTheme.TEXT_PRIMARY);

                content.add(formatLabel);

                content.add(
                                Box.createVerticalStrut(6));

                JComboBox<String> formatCombo = new JComboBox<>(
                                new String[] {
                                                "CSV (.csv)",
                                                "Word Document (.docx)",
                                                "PDF (.pdf)",
                                                "Text File (.txt)",
                                                "JSON (.json)"
                                });

                formatCombo.setFont(
                                BussinTheme.SMALL);

                formatCombo.setMaximumSize(
                                new Dimension(
                                                Integer.MAX_VALUE,
                                                36));

                content.add(formatCombo);

                content.add(
                                Box.createVerticalStrut(20));

                JPanel buttons = new JPanel(
                                new FlowLayout(
                                                FlowLayout.RIGHT,
                                                8,
                                                0));

                buttons.setOpaque(false);

                AppButton cancelButton = new AppButton(
                                "Cancel",
                                AppButton.Variant.SECONDARY);

                AppButton exportButton = new AppButton(
                                "Export",
                                AppButton.Variant.PRIMARY);

                cancelButton.addActionListener(
                                event -> dialog.dispose());

                exportButton.addActionListener(
                                event -> {

                                        String format = (String) formatCombo
                                                        .getSelectedItem();

                                        dialog.dispose();

                                        exportReport(format);
                                });

                buttons.add(cancelButton);
                buttons.add(exportButton);

                content.add(buttons);

                dialog.add(
                                content,
                                BorderLayout.CENTER);

                dialog.setVisible(true);
        }

        // ============================================================
        // EXPORT
        // ============================================================

        private void exportReport(
                        String format) {

                if (format == null) {
                        return;
                }

                String period = getSelectedPeriod();

                String safePeriod = period.replace(
                                " ",
                                "_");

                JFileChooser chooser = new JFileChooser();

                chooser.setDialogTitle(
                                "Export BUSSIN Report");

                String extension;

                if (format.startsWith("CSV")) {

                        extension = ".csv";

                } else if (format.startsWith("Word")) {

                        extension = ".docx";

                } else if (format.startsWith("PDF")) {

                        extension = ".pdf";

                } else if (format.startsWith("Text")) {

                        extension = ".txt";

                } else {

                        extension = ".json";
                }

                chooser.setSelectedFile(
                                new File(
                                                "BUSSIN_Report_"
                                                                + safePeriod
                                                                + extension));

                int result = chooser.showSaveDialog(this);

                if (result != JFileChooser.APPROVE_OPTION) {
                        return;
                }

                File file = chooser.getSelectedFile();

                try {

                        if (!file.getName()
                                        .toLowerCase()
                                        .endsWith(extension)) {

                                file = new File(
                                                file.getAbsolutePath()
                                                                + extension);
                        }

                        if (format.startsWith("CSV")) {

                                ReportExporter.exportCSV(
                                                file,
                                                period,
                                                reportData);

                        } else if (format.startsWith("Word")) {

                                ReportExporter.exportDOCX(
                                                file,
                                                period,
                                                reportData);

                        } else if (format.startsWith("PDF")) {

                                ReportExporter.exportPDF(
                                                file,
                                                period,
                                                reportData);

                        } else if (format.startsWith("Text")) {

                                ReportExporter.exportTXT(
                                                file,
                                                period,
                                                reportData);

                        } else {

                                ReportExporter.exportJSON(
                                                file,
                                                period,
                                                reportData);
                        }

                        JOptionPane.showMessageDialog(
                                        this,
                                        "Report exported successfully.\n\n"
                                                        + file.getAbsolutePath(),
                                        "Export Successful",
                                        JOptionPane.INFORMATION_MESSAGE);

                } catch (Exception exception) {

                        exception.printStackTrace();

                        JOptionPane.showMessageDialog(
                                        this,
                                        "Failed to export the report.\n\n"
                                                        + exception.getMessage(),
                                        "Export Error",
                                        JOptionPane.ERROR_MESSAGE);
                }
        }

        // ============================================================
        // COPY
        // ============================================================

        private void copyReportToClipboard() {

                String report = ReportExporter.buildTextReport(
                                getSelectedPeriod(),
                                reportData);

                StringSelection selection = new StringSelection(report);

                Toolkit.getDefaultToolkit()
                                .getSystemClipboard()
                                .setContents(
                                                selection,
                                                null);

                JOptionPane.showMessageDialog(
                                this,
                                "Report copied to clipboard.",
                                "Copied",
                                JOptionPane.INFORMATION_MESSAGE);
        }

        // ============================================================
        // REPORT DATA
        // ============================================================

        public static class ReportData {

                public int totalBookings;
                public int completedTrips;
                public int activeBuses;
                public int activeEmployees;

                public int pendingBookings;
                public int confirmedBookings;
                public int completedBookings;
                public int cancelledBookings;

                public int availableBuses;
                public int onTripBuses;
                public int maintenanceBuses;
                public int inactiveBuses;

                public int activeRoutes;
                public int inactiveRoutes;
                public int totalDistance;
                public int averageFare;

                public int totalEmployees;
                public int inactiveEmployees;
                public int busDrivers;
                public int dispatchers;
                public int ticketingStaff;
                public int fleetSupervisors;
        }
}