package com.bussin.desktop.ui.screens;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.bussin.desktop.ui.components.AppButton;
import com.bussin.desktop.ui.components.AppCard;
import com.bussin.desktop.ui.components.AppLabel;
import com.bussin.desktop.ui.components.PageContent;
import com.bussin.desktop.ui.theme.BussinTheme;

public class UserDashboardScreen extends JPanel {

        // ================================================================
        // CONSTANTS
        // ================================================================

        private static final int SECTION_GAP = 24;

        // ================================================================
        // CURRENT USER
        // ================================================================

        private final String currentUserEmail;
        private final Consumer<String> navigationHandler;

        // ================================================================
        // CONSTRUCTOR
        // ================================================================

        public UserDashboardScreen(
                        String currentUserEmail,
                        Consumer<String> navigationHandler) {

                this.currentUserEmail = currentUserEmail == null
                                ? ""
                                : currentUserEmail.trim();

                this.navigationHandler = navigationHandler;

                initializeUI();
        }

        // ================================================================
        // INITIALIZE UI
        // ================================================================

        private void initializeUI() {

                setLayout(
                                new BorderLayout());

                setBackground(
                                BussinTheme.BACKGROUND);

                PageContent page = new PageContent();

                // ------------------------------------------------------------
                // Header
                // ------------------------------------------------------------

                page.addBlock(
                                createHeader(),
                                0);

                // ------------------------------------------------------------
                // Find Trip
                // ------------------------------------------------------------

                page.addBlock(
                                createSearchCard(),
                                SECTION_GAP);

                // ------------------------------------------------------------
                // Upcoming Trip
                // ------------------------------------------------------------

                page.addBlock(
                                createUpcomingTripSection(),
                                SECTION_GAP);

                // ------------------------------------------------------------
                // Quick Actions
                // ------------------------------------------------------------

                page.addBlock(
                                createQuickActionsSection(),
                                SECTION_GAP);

                add(
                                page.inScrollPane(),
                                BorderLayout.CENTER);
        }

        // ================================================================
        // HEADER
        // ================================================================

        private JPanel createHeader() {

                JPanel panel = new JPanel();

                panel.setOpaque(false);

                panel.setLayout(
                                new BoxLayout(
                                                panel,
                                                BoxLayout.Y_AXIS));

                String name = getDisplayName();

                AppLabel title = new AppLabel(
                                "Good morning, "
                                                + name
                                                + " 👋");

                title.setFont(
                                BussinTheme.PAGE_TITLE);

                title.setForeground(
                                BussinTheme.TEXT_PRIMARY);

                AppLabel subtitle = new AppLabel(
                                "Plan your next trip with BUSSIN.");

                subtitle.setFont(
                                BussinTheme.PAGE_SUBTITLE);

                subtitle.setForeground(
                                BussinTheme.TEXT_SECONDARY);

                panel.add(title);

                panel.add(
                                Box.createVerticalStrut(6));

                panel.add(subtitle);

                return panel;
        }

        // ================================================================
        // SEARCH CARD
        // ================================================================

        private JPanel createSearchCard() {

                AppCard card = new AppCard();

                card.setLayout(
                                new BorderLayout(
                                                0,
                                                18));

                // ------------------------------------------------------------
                // Header
                // ------------------------------------------------------------

                JPanel header = new JPanel();

                header.setOpaque(false);

                header.setLayout(
                                new BoxLayout(
                                                header,
                                                BoxLayout.Y_AXIS));

                AppLabel title = new AppLabel(
                                "Where are you going?");

                title.setFont(
                                BussinTheme.SECTION_TITLE);

                title.setForeground(
                                BussinTheme.TEXT_PRIMARY);

                AppLabel subtitle = new AppLabel(
                                "Find an available bus trip for your destination.");

                subtitle.setFont(
                                BussinTheme.BODY);

                subtitle.setForeground(
                                BussinTheme.TEXT_SECONDARY);

                header.add(title);

                header.add(
                                Box.createVerticalStrut(5));

                header.add(subtitle);

                card.add(
                                header,
                                BorderLayout.NORTH);

                // ------------------------------------------------------------
                // Locations
                // ------------------------------------------------------------

                JPanel locations = new JPanel(
                                new GridLayout(
                                                1,
                                                2,
                                                16,
                                                0));

                locations.setOpaque(false);

                locations.add(
                                createLocationField(
                                                "FROM",
                                                "Manila"));

                locations.add(
                                createLocationField(
                                                "TO",
                                                "Batangas"));

                card.add(
                                locations,
                                BorderLayout.CENTER);

                // ------------------------------------------------------------
                // Search button
                // ------------------------------------------------------------

                AppButton searchButton = new AppButton(
                                "Find Available Trips");

                searchButton.setPreferredSize(
                                new Dimension(
                                                210,
                                                42));

                searchButton.addActionListener(
                                e -> navigate("trip-search"));

                JPanel buttonPanel = new JPanel(
                                new FlowLayout(
                                                FlowLayout.LEFT,
                                                0,
                                                0));

                buttonPanel.setOpaque(false);

                buttonPanel.add(searchButton);

                card.add(
                                buttonPanel,
                                BorderLayout.SOUTH);

                return card;
        }

        // ================================================================
        // LOCATION FIELD
        // ================================================================

        private JPanel createLocationField(
                        String label,
                        String value) {

                JPanel panel = new JPanel();

                panel.setOpaque(false);

                panel.setLayout(
                                new BoxLayout(
                                                panel,
                                                BoxLayout.Y_AXIS));

                JLabel labelComponent = new JLabel(label);

                labelComponent.setFont(
                                BussinTheme.SMALL_BOLD);

                labelComponent.setForeground(
                                BussinTheme.TEXT_MUTED);

                JLabel valueComponent = new JLabel(value);

                valueComponent.setFont(
                                BussinTheme.BODY_MEDIUM);

                valueComponent.setForeground(
                                BussinTheme.TEXT_PRIMARY);

                valueComponent.setBorder(
                                BorderFactory.createCompoundBorder(
                                                BorderFactory.createLineBorder(
                                                                BussinTheme.BORDER),
                                                BorderFactory.createEmptyBorder(
                                                                10,
                                                                12,
                                                                10,
                                                                12)));

                panel.add(labelComponent);

                panel.add(
                                Box.createVerticalStrut(6));

                panel.add(valueComponent);

                return panel;
        }

        // ================================================================
        // UPCOMING TRIP
        // ================================================================

        private JPanel createUpcomingTripSection() {

                JPanel section = new JPanel();

                section.setOpaque(false);

                section.setLayout(
                                new BoxLayout(
                                                section,
                                                BoxLayout.Y_AXIS));

                AppLabel title = new AppLabel(
                                "Upcoming Trip");

                title.setFont(
                                BussinTheme.SECTION_TITLE);

                title.setForeground(
                                BussinTheme.TEXT_PRIMARY);

                section.add(title);

                section.add(
                                Box.createVerticalStrut(12));

                AppCard card = new AppCard();

                card.setLayout(
                                new BorderLayout(
                                                20,
                                                0));

                // ------------------------------------------------------------
                // Booking information
                // ------------------------------------------------------------

                JPanel information = new JPanel();

                information.setOpaque(false);

                information.setLayout(
                                new BoxLayout(
                                                information,
                                                BoxLayout.Y_AXIS));

                JLabel bookingId = new JLabel(
                                "BK-1011");

                bookingId.setFont(
                                BussinTheme.SMALL_BOLD);

                bookingId.setForeground(
                                BussinTheme.TEXT_MUTED);

                JLabel route = new JLabel(
                                "Manila → Batangas");

                route.setFont(
                                BussinTheme.CARD_TITLE);

                route.setForeground(
                                BussinTheme.TEXT_PRIMARY);

                JLabel details = new JLabel(
                                "Today • 10:00 AM • Seat 03A");

                details.setFont(
                                BussinTheme.BODY);

                details.setForeground(
                                BussinTheme.TEXT_SECONDARY);

                information.add(bookingId);

                information.add(
                                Box.createVerticalStrut(5));

                information.add(route);

                information.add(
                                Box.createVerticalStrut(5));

                information.add(details);

                card.add(
                                information,
                                BorderLayout.CENTER);

                // ------------------------------------------------------------
                // Fare + status
                // ------------------------------------------------------------

                JPanel statusPanel = new JPanel();

                statusPanel.setOpaque(false);

                statusPanel.setLayout(
                                new BoxLayout(
                                                statusPanel,
                                                BoxLayout.Y_AXIS));

                JLabel fare = new JLabel("₱450");

                fare.setFont(
                                BussinTheme.CARD_TITLE);

                fare.setForeground(
                                BussinTheme.TEXT_PRIMARY);

                fare.setAlignmentX(
                                RIGHT_ALIGNMENT);

                JLabel status = new JLabel("Confirmed");

                status.setFont(
                                BussinTheme.SMALL_BOLD);

                status.setForeground(
                                BussinTheme.SUCCESS);

                status.setAlignmentX(
                                RIGHT_ALIGNMENT);

                statusPanel.add(fare);

                statusPanel.add(
                                Box.createVerticalStrut(5));

                statusPanel.add(status);

                card.add(
                                statusPanel,
                                BorderLayout.EAST);

                // ------------------------------------------------------------
                // View booking
                // ------------------------------------------------------------

                JButton viewButton = new JButton(
                                "View Booking");

                viewButton.setFont(
                                BussinTheme.BUTTON);

                viewButton.setForeground(
                                BussinTheme.PRIMARY);

                viewButton.setBorder(
                                BorderFactory.createEmptyBorder(
                                                8,
                                                0,
                                                8,
                                                0));

                viewButton.setContentAreaFilled(false);

                viewButton.setFocusPainted(false);

                viewButton.setCursor(
                                Cursor.getPredefinedCursor(
                                                Cursor.HAND_CURSOR));

                viewButton.addActionListener(
                                e -> navigate("bookings"));

                JPanel buttonPanel = new JPanel(
                                new FlowLayout(
                                                FlowLayout.RIGHT,
                                                0,
                                                0));

                buttonPanel.setOpaque(false);

                buttonPanel.add(viewButton);

                card.add(
                                buttonPanel,
                                BorderLayout.SOUTH);

                section.add(card);

                return section;
        }

        // ================================================================
        // QUICK ACTIONS
        // ================================================================

        private JPanel createQuickActionsSection() {

                JPanel section = new JPanel();

                section.setOpaque(false);

                section.setLayout(
                                new BoxLayout(
                                                section,
                                                BoxLayout.Y_AXIS));

                AppLabel title = new AppLabel(
                                "Quick Actions");

                title.setFont(
                                BussinTheme.SECTION_TITLE);

                title.setForeground(
                                BussinTheme.TEXT_PRIMARY);

                section.add(title);

                section.add(
                                Box.createVerticalStrut(12));

                JPanel actions = new JPanel(
                                new GridLayout(
                                                1,
                                                3,
                                                12,
                                                0));

                actions.setOpaque(false);

                actions.add(
                                createActionCard(
                                                "Find a Trip",
                                                "Search available bus trips.",
                                                "trip-search"));

                actions.add(
                                createActionCard(
                                                "My Bookings",
                                                "View your current bookings.",
                                                "bookings"));

                actions.add(
                                createActionCard(
                                                "Queue",
                                                "Check your boarding status.",
                                                "queue"));

                section.add(actions);

                return section;
        }

        // ================================================================
        // ACTION CARD
        // ================================================================

        private JPanel createActionCard(
                        String title,
                        String description,
                        String route) {

                AppCard card = new AppCard();

                card.setLayout(
                                new BorderLayout(
                                                0,
                                                8));

                JLabel titleLabel = new JLabel(title);

                titleLabel.setFont(
                                BussinTheme.CARD_TITLE);

                titleLabel.setForeground(
                                BussinTheme.TEXT_PRIMARY);

                JLabel descriptionLabel = new JLabel(
                                "<html>"
                                                + description
                                                + "</html>");

                descriptionLabel.setFont(
                                BussinTheme.SMALL);

                descriptionLabel.setForeground(
                                BussinTheme.TEXT_SECONDARY);

                card.add(
                                titleLabel,
                                BorderLayout.NORTH);

                card.add(
                                descriptionLabel,
                                BorderLayout.CENTER);

                JButton openButton = new JButton("Open");

                openButton.setFont(
                                BussinTheme.BUTTON);

                openButton.setForeground(
                                BussinTheme.PRIMARY);

                openButton.setBorder(
                                BorderFactory.createEmptyBorder(
                                                6,
                                                0,
                                                0,
                                                0));

                openButton.setContentAreaFilled(false);

                openButton.setFocusPainted(false);

                openButton.setCursor(
                                Cursor.getPredefinedCursor(
                                                Cursor.HAND_CURSOR));

                openButton.addActionListener(
                                e -> navigate(route));

                JPanel buttonPanel = new JPanel(
                                new FlowLayout(
                                                FlowLayout.LEFT,
                                                0,
                                                0));

                buttonPanel.setOpaque(false);

                buttonPanel.add(openButton);

                card.add(
                                buttonPanel,
                                BorderLayout.SOUTH);

                return card;
        }

        // ================================================================
        // NAVIGATION
        // ================================================================

        private void navigate(
                        String route) {

                if (navigationHandler != null) {

                        navigationHandler.accept(route);
                }
        }

        // ================================================================
        // DISPLAY NAME
        // ================================================================

        private String getDisplayName() {

                if (currentUserEmail.isBlank()) {

                        return "Commuter";
                }

                String localPart = currentUserEmail
                                .split("@")[0]
                                .trim();

                if (localPart.isBlank()) {

                        return "Commuter";
                }

                String[] parts = localPart
                                .replace(".", " ")
                                .replace("_", " ")
                                .replace("-", " ")
                                .split("\\s+");

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

                return result.length() > 0
                                ? result.toString()
                                : "Commuter";
        }

        // ================================================================
        // ALIGNMENT
        // ================================================================

        private static final float RIGHT = 1.0f;
}