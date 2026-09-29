package com.bussin.desktop.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.bussin.desktop.ui.components.Sidebar;
import com.bussin.desktop.ui.components.TopBar;
import com.bussin.desktop.ui.screens.BookingScreen;
import com.bussin.desktop.ui.screens.BusScreen;
import com.bussin.desktop.ui.screens.DashboardScreen;
import com.bussin.desktop.ui.screens.EmployeeScreen;
import com.bussin.desktop.ui.screens.QueueScreen;
import com.bussin.desktop.ui.screens.ReportScreen;
import com.bussin.desktop.ui.screens.RouteScreen;
import com.bussin.desktop.ui.screens.TripScreen;
import com.bussin.desktop.ui.theme.BussinTheme;

public class MainFrame extends JFrame {

        private final String userRole;

        private final JPanel contentPanel = new JPanel(new BorderLayout());

        private final Map<String, JPanel> screenCache = new HashMap<>();

        private Sidebar sidebar;
        private TopBar topBar;
        private String currentRoute;

        public MainFrame(String userRole) {

                this.userRole = normalizeRole(userRole);

                setTitle("BUSSIN");

                setDefaultCloseOperation(
                                JFrame.EXIT_ON_CLOSE);

                setMinimumSize(
                                new Dimension(
                                                1200,
                                                760));

                setSize(
                                1440,
                                900);

                setLocationRelativeTo(null);

                initializeUI();

                navigate("dashboard");
        }

        private void initializeUI() {

                sidebar = new Sidebar(
                                this::navigate,
                                userRole);

                topBar = new TopBar();

                contentPanel.setBackground(
                                BussinTheme.BACKGROUND);

                contentPanel.setBorder(
                                BorderFactory.createEmptyBorder(
                                                0,
                                                0,
                                                0,
                                                0));

                JPanel mainPanel = new JPanel(
                                new BorderLayout());

                mainPanel.setBackground(
                                BussinTheme.BACKGROUND);

                mainPanel.add(
                                sidebar,
                                BorderLayout.WEST);

                JPanel rightPanel = new JPanel(
                                new BorderLayout());

                rightPanel.setBackground(
                                BussinTheme.BACKGROUND);

                rightPanel.add(
                                topBar,
                                BorderLayout.NORTH);

                rightPanel.add(
                                contentPanel,
                                BorderLayout.CENTER);

                mainPanel.add(
                                rightPanel,
                                BorderLayout.CENTER);

                setContentPane(mainPanel);
        }

        private void navigate(
                        String route) {

                if (route == null || route.isBlank()) {
                        return;
                }

                if (!hasAccess(route)) {

                        JOptionPane.showMessageDialog(
                                        this,
                                        "You do not have permission to access this section.",
                                        "Access Denied",
                                        JOptionPane.WARNING_MESSAGE);

                        return;
                }

                currentRoute = route;

                JPanel screen = getOrCreateScreen(route);

                contentPanel.removeAll();

                contentPanel.add(
                                screen,
                                BorderLayout.CENTER);

                contentPanel.revalidate();
                contentPanel.repaint();

                sidebar.setActiveRoute(route);

                updateTitle(route);
        }

        private JPanel getOrCreateScreen(
                        String route) {

                if (screenCache.containsKey(route)) {

                        return screenCache.get(route);
                }

                JPanel screen = createScreen(route);

                screenCache.put(
                                route,
                                screen);

                return screen;
        }

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
                                new ReportScreen();

                        default ->
                                createPlaceholder(
                                                "Page Not Found",
                                                "The requested section does not exist.");
                };
        }

        private boolean hasAccess(
                        String route) {

                switch (userRole) {

                        case "ADMIN":

                                return true;

                        case "EMPLOYEE":

                                return switch (route) {

                                        case "dashboard",
                                                        "queue",
                                                        "bookings",
                                                        "trips",
                                                        "buses" ->
                                                true;

                                        default -> false;
                                };

                        case "USER":

                        default:

                                return switch (route) {

                                        case "dashboard",
                                                        "queue",
                                                        "bookings" ->
                                                true;

                                        default -> false;
                                };
                }
        }

        private void updateTitle(
                        String route) {

                String title = switch (route) {

                        case "dashboard" ->
                                "Dashboard";

                        case "queue" ->
                                "Queue";

                        case "bookings" ->
                                "Bookings";

                        case "trips" ->
                                "Trips";

                        case "buses" ->
                                "Bus Management";

                        case "routes" ->
                                "Route Management";

                        case "employees" ->
                                "Employee Management";

                        case "reports" ->
                                "Reports & Analytics";

                        default ->
                                "BUSSIN";
                };

                setTitle(
                                "BUSSIN - " + title);
        }

        private JPanel createPlaceholder(
                        String title,
                        String message) {

                JPanel panel = new JPanel(
                                new GridBagLayout());

                panel.setBackground(
                                BussinTheme.BACKGROUND);

                JPanel card = new JPanel();

                card.setBackground(
                                BussinTheme.SURFACE);

                card.setBorder(
                                BorderFactory.createCompoundBorder(
                                                BorderFactory.createLineBorder(
                                                                BussinTheme.BORDER_DARK),
                                                BorderFactory.createEmptyBorder(
                                                                32,
                                                                40,
                                                                32,
                                                                40)));

                card.setLayout(
                                new BoxLayout(
                                                card,
                                                BoxLayout.Y_AXIS));

                JLabel titleLabel = new JLabel(title);

                titleLabel.setFont(
                                BussinTheme.BODY);

                titleLabel.setForeground(
                                BussinTheme.TEXT_PRIMARY);

                titleLabel.setAlignmentX(
                                Component.CENTER_ALIGNMENT);

                JLabel messageLabel = new JLabel(message);

                messageLabel.setFont(
                                BussinTheme.BODY);

                messageLabel.setForeground(
                                BussinTheme.TEXT_SECONDARY);

                messageLabel.setAlignmentX(
                                Component.CENTER_ALIGNMENT);

                card.add(titleLabel);

                card.add(
                                Box.createVerticalStrut(10));

                card.add(messageLabel);

                panel.add(card);

                return panel;
        }

        private String normalizeRole(
                        String role) {

                if (role == null || role.isBlank()) {

                        return "USER";
                }

                return role
                                .trim()
                                .toUpperCase();
        }

        public String getUserRole() {

                return userRole;
        }

        public String getCurrentRoute() {

                return currentRoute;
        }
}