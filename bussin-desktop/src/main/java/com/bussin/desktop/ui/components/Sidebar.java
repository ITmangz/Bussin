package com.bussin.desktop.ui.components;

import com.bussin.desktop.ui.theme.BussinTheme;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Sidebar extends JPanel {

        private final Consumer<String> navigationHandler;

        private final Map<String, JButton> navigationButtons = new HashMap<>();

        private String activeRoute;

        public Sidebar(
                        Consumer<String> navigationHandler) {

                this.navigationHandler = navigationHandler;

                setPreferredSize(
                                new Dimension(
                                                BussinTheme.SIDEBAR_WIDTH,
                                                0));

                setMinimumSize(
                                new Dimension(
                                                BussinTheme.SIDEBAR_WIDTH,
                                                0));

                setBackground(
                                BussinTheme.CHARCOAL);

                setLayout(
                                new BorderLayout());

                add(
                                createContent(),
                                BorderLayout.CENTER);

                add(
                                createFooter(),
                                BorderLayout.SOUTH);
        }

        // ================================================================
        // CONTENT
        // ================================================================

        private JPanel createContent() {

                JPanel content = new JPanel();

                content.setOpaque(false);

                content.setBorder(
                                BorderFactory.createEmptyBorder(
                                                24,
                                                12,
                                                16,
                                                12));

                content.setLayout(
                                new BoxLayout(
                                                content,
                                                BoxLayout.Y_AXIS));

                // ============================================================
                // BRAND
                // ============================================================

                JPanel brand = new JPanel();

                brand.setOpaque(false);

                brand.setLayout(
                                new BoxLayout(
                                                brand,
                                                BoxLayout.Y_AXIS));

                JLabel logo = new JLabel("BUSSIN");

                logo.setFont(
                                BussinTheme.BRAND);

                logo.setForeground(
                                Color.WHITE);

                JLabel subtitle = new JLabel(
                                "BUS OPERATIONS");

                subtitle.setFont(
                                BussinTheme.SMALL_BOLD);

                subtitle.setForeground(
                                BussinTheme.MUTED_SILVER);

                brand.add(logo);

                brand.add(
                                Box.createVerticalStrut(3));

                brand.add(subtitle);

                content.add(brand);

                content.add(
                                Box.createVerticalStrut(34));

                // ============================================================
                // OPERATIONS
                // ============================================================

                content.add(
                                createSectionLabel(
                                                "OPERATIONS"));

                content.add(
                                createNavButton(
                                                "Dashboard",
                                                "dashboard",
                                                "dashboard"));

                content.add(
                                createNavButton(
                                                "Queue",
                                                "queue",
                                                "queue"));

                content.add(
                                createNavButton(
                                                "Bookings",
                                                "bookings",
                                                "booking"));

                content.add(
                                createNavButton(
                                                "Trips",
                                                "trips",
                                                "trip"));

                content.add(
                                createNavButton(
                                                "Buses",
                                                "buses",
                                                "bus"));

                content.add(
                                createNavButton(
                                                "Routes",
                                                "routes",
                                                "route"));

                content.add(
                                Box.createVerticalStrut(26));

                // ============================================================
                // MANAGEMENT
                // ============================================================

                content.add(
                                createSectionLabel(
                                                "MANAGEMENT"));

                content.add(
                                createNavButton(
                                                "Employees",
                                                "employees",
                                                "employee"));

                content.add(
                                createNavButton(
                                                "Reports",
                                                "reports",
                                                "report"));

                return content;
        }

        // ================================================================
        // SECTION LABEL
        // ================================================================

        private JLabel createSectionLabel(
                        String text) {

                JLabel label = new JLabel(text);

                label.setFont(
                                BussinTheme.SMALL_BOLD);

                label.setForeground(
                                BussinTheme.MUTED_SILVER);

                label.setBorder(
                                BorderFactory.createEmptyBorder(
                                                0,
                                                12,
                                                9,
                                                0));

                label.setAlignmentX(
                                Component.LEFT_ALIGNMENT);

                return label;
        }

        // ================================================================
        // NAVIGATION BUTTON
        // ================================================================

        private JButton createNavButton(
                        String text,
                        String route,
                        String iconName) {

                JButton button = new JButton(text) {

                        @Override
                        protected void paintComponent(
                                        Graphics g) {

                                Graphics2D g2 = (Graphics2D) g.create();

                                g2.setRenderingHint(
                                                RenderingHints.KEY_ANTIALIASING,
                                                RenderingHints.VALUE_ANTIALIAS_ON);

                                boolean active = route.equals(activeRoute);

                                boolean hovered = getModel().isRollover();

                                if (active) {

                                        g2.setColor(
                                                        BussinTheme.RED);

                                        g2.fillRoundRect(
                                                        0,
                                                        0,
                                                        getWidth(),
                                                        getHeight(),
                                                        BussinTheme.MEDIUM_RADIUS,
                                                        BussinTheme.MEDIUM_RADIUS);

                                } else if (hovered) {

                                        g2.setColor(
                                                        new Color(
                                                                        255,
                                                                        0,
                                                                        0,
                                                                        35));

                                        g2.fillRoundRect(
                                                        0,
                                                        0,
                                                        getWidth(),
                                                        getHeight(),
                                                        BussinTheme.MEDIUM_RADIUS,
                                                        BussinTheme.MEDIUM_RADIUS);
                                }

                                g2.dispose();

                                super.paintComponent(g);
                        }
                };

                button.setHorizontalAlignment(
                                SwingConstants.LEFT);

                button.setFont(
                                BussinTheme.BODY_MEDIUM);

                button.setFocusPainted(false);

                button.setBorderPainted(false);

                button.setContentAreaFilled(false);

                button.setOpaque(false);

                button.setIconTextGap(14);

                button.putClientProperty(
                                "iconName",
                                iconName);

                button.setCursor(
                                Cursor.getPredefinedCursor(
                                                Cursor.HAND_CURSOR));

                button.setBorder(
                                BorderFactory.createEmptyBorder(
                                                11,
                                                14,
                                                11,
                                                14));

                button.setAlignmentX(
                                Component.LEFT_ALIGNMENT);

                button.setMaximumSize(
                                new Dimension(
                                                Integer.MAX_VALUE,
                                                46));

                navigationButtons.put(
                                route,
                                button);

                updateButtonAppearance(
                                button,
                                false);

                button.addActionListener(
                                event -> {

                                        setActiveRoute(route);

                                        navigationHandler.accept(
                                                        route);
                                });

                return button;
        }

        // ================================================================
        // ACTIVE ROUTE
        // ================================================================

        public void setActiveRoute(
                        String route) {

                activeRoute = route;

                for (Map.Entry<String, JButton> entry : navigationButtons.entrySet()) {

                        boolean active = entry.getKey()
                                        .equals(route);

                        updateButtonAppearance(
                                        entry.getValue(),
                                        active);
                }

                repaint();
        }

        // ================================================================
        // BUTTON APPEARANCE
        // ================================================================

        private void updateButtonAppearance(
                        JButton button,
                        boolean active) {

                String iconName = (String) button.getClientProperty(
                                "iconName");

                button.setForeground(
                                active
                                                ? Color.WHITE
                                                : Color.decode("#D1D5DB"));

                button.setIcon(
                                IconFactory.create(
                                                iconName,
                                                19,
                                                active
                                                                ? Color.WHITE
                                                                : BussinTheme.MUTED_SILVER));

                button.repaint();
        }

        // ================================================================
        // FOOTER
        // ================================================================

        private JPanel createFooter() {

                JPanel footer = new JPanel(
                                new BorderLayout());

                footer.setOpaque(false);

                footer.setBorder(
                                BorderFactory.createCompoundBorder(
                                                BorderFactory.createMatteBorder(
                                                                1,
                                                                0,
                                                                0,
                                                                0,
                                                                BussinTheme.BORDER_DARK),

                                                BorderFactory.createEmptyBorder(
                                                                14,
                                                                18,
                                                                16,
                                                                18)));

                // ------------------------------------------------------------
                // USER ICON
                // ------------------------------------------------------------

                JLabel icon = new JLabel();

                icon.setIcon(
                                IconFactory.create(
                                                "employee",
                                                22,
                                                BussinTheme.RED));

                icon.setBorder(
                                BorderFactory.createEmptyBorder(
                                                0,
                                                0,
                                                0,
                                                10));

                // ------------------------------------------------------------
                // USER DETAILS
                // ------------------------------------------------------------

                JPanel userInfo = new JPanel();

                userInfo.setOpaque(false);

                userInfo.setLayout(
                                new BoxLayout(
                                                userInfo,
                                                BoxLayout.Y_AXIS));

                JLabel name = new JLabel(
                                "Administrator");

                name.setFont(
                                BussinTheme.SMALL_BOLD);

                name.setForeground(
                                Color.WHITE);

                JLabel role = new JLabel(
                                "BUSSIN Staff");

                role.setFont(
                                BussinTheme.SMALL);

                role.setForeground(
                                BussinTheme.MUTED_SILVER);

                userInfo.add(name);

                userInfo.add(
                                Box.createVerticalStrut(2));

                userInfo.add(role);

                footer.add(
                                icon,
                                BorderLayout.WEST);

                footer.add(
                                userInfo,
                                BorderLayout.CENTER);

                return footer;
        }
}
