package com.bussin.desktop.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.bussin.desktop.ui.components.Sidebar;
import com.bussin.desktop.ui.components.TopBar;
import com.bussin.desktop.ui.screens.BookingScreen;
import com.bussin.desktop.ui.screens.BusScreen;
import com.bussin.desktop.ui.screens.DashboardScreen;
import com.bussin.desktop.ui.screens.EmployeeScreen;
import com.bussin.desktop.ui.screens.QueueScreen;
import com.bussin.desktop.ui.screens.RouteScreen;
import com.bussin.desktop.ui.screens.TripScreen;
import com.bussin.desktop.ui.theme.BussinTheme;

public class MainFrame extends JFrame {

        private final Sidebar sidebar;
        private final TopBar topBar;
        private final JPanel screenContainer;

        private String currentRoute;

        public MainFrame() {

                setTitle(
                                "BUSSIN Desktop");

                setDefaultCloseOperation(
                                JFrame.EXIT_ON_CLOSE);

                setMinimumSize(
                                new Dimension(
                                                1100,
                                                700));

                setSize(
                                1440,
                                900);

                setLocationRelativeTo(null);

                // ============================================================
                // COMPONENTS
                // ============================================================

                sidebar = new Sidebar(
                                this::navigate);

                topBar = new TopBar();

                screenContainer = new JPanel(
                                new BorderLayout());

                screenContainer.setBackground(
                                BussinTheme.BACKGROUND);

                // ============================================================
                // MAIN AREA
                // ============================================================

                JPanel mainArea = new JPanel(
                                new BorderLayout());

                mainArea.setBackground(
                                BussinTheme.BACKGROUND);

                mainArea.add(
                                topBar,
                                BorderLayout.NORTH);

                mainArea.add(
                                screenContainer,
                                BorderLayout.CENTER);

                // ============================================================
                // FRAME
                // ============================================================

                setLayout(
                                new BorderLayout());

                add(
                                sidebar,
                                BorderLayout.WEST);

                add(
                                mainArea,
                                BorderLayout.CENTER);

                // ============================================================
                // INITIAL SCREEN
                // ============================================================

                navigate(
                                "dashboard");
        }

        // ================================================================
        // NAVIGATION
        // ================================================================

        private void navigate(
                        String route) {

                if (route == null
                                || route.isBlank()) {

                        return;
                }

                /*
                 * Prevent unnecessary screen recreation
                 * when the user clicks the currently active
                 * navigation item.
                 */
                if (route.equals(currentRoute)
                                && screenContainer
                                                .getComponentCount() > 0) {

                        return;
                }

                JPanel screen = createScreen(route);

                if (screen == null) {

                        screen = createPlaceholder(
                                        "Page Not Found",
                                        "The requested BUSSIN module could not be found.");
                }

                screenContainer.removeAll();

                screenContainer.add(
                                screen,
                                BorderLayout.CENTER);

                currentRoute = route;

                topBar.setPageTitle(
                                getPageTitle(route));

                sidebar.setActiveRoute(
                                route);

                screenContainer.revalidate();

                screenContainer.repaint();
        }

        // ================================================================
        // SCREEN FACTORY
        // ================================================================

        private JPanel createScreen(
                        String route) {

                return switch (route) {

                        case "dashboard" ->
                                new DashboardScreen();

                        case "queue" ->
                                new QueueScreen();

                        case "bookings" ->
                                new BookingScreen();

                        case "trips" ->
                                new TripScreen();

                        case "buses" ->
                                new BusScreen();

                        case "routes" ->
                                new RouteScreen();

                        case "employees" ->
                                new EmployeeScreen();

                        case "reports" ->
                                createPlaceholder(
                                                "Reports",
                                                "View operational, booking, queue, and revenue reports.");

                        default ->
                                createPlaceholder(
                                                "Page Not Found",
                                                "The requested BUSSIN module could not be found.");
                };
        }

        // ================================================================
        // PAGE TITLES
        // ================================================================

        private String getPageTitle(
                        String route) {

                return switch (route) {

                        case "dashboard" ->
                                "Dashboard";

                        case "queue" ->
                                "Queue Management";

                        case "bookings" ->
                                "Booking Management";

                        case "trips" ->
                                "Trip Management";

                        case "buses" ->
                                "Bus Management";

                        case "routes" ->
                                "Route Management";

                        case "employees" ->
                                "Employee Management";

                        case "reports" ->
                                "Reports";

                        default ->
                                "BUSSIN";
                };
        }

        // ================================================================
        // PLACEHOLDER
        // ================================================================

        private JPanel createPlaceholder(
                        String title,
                        String description) {

                JPanel panel = new JPanel(
                                new GridBagLayout());

                panel.setBackground(
                                BussinTheme.BACKGROUND);

                JPanel content = new JPanel();

                content.setOpaque(false);

                content.setLayout(
                                new BoxLayout(
                                                content,
                                                BoxLayout.Y_AXIS));

                JLabel titleLabel = new JLabel(title);

                titleLabel.setFont(
                                BussinTheme.PAGE_TITLE);

                titleLabel.setForeground(
                                BussinTheme.TEXT_PRIMARY);

                titleLabel.setAlignmentX(
                                Component.CENTER_ALIGNMENT);

                JLabel descriptionLabel = new JLabel(
                                description);

                descriptionLabel.setFont(
                                BussinTheme.BODY);

                descriptionLabel.setForeground(
                                BussinTheme.TEXT_SECONDARY);

                descriptionLabel.setAlignmentX(
                                Component.CENTER_ALIGNMENT);

                content.add(
                                titleLabel);

                content.add(
                                Box.createVerticalStrut(
                                                8));

                content.add(
                                descriptionLabel);

                panel.add(content);

                return panel;
        }
}