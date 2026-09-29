package com.bussin.desktop.ui.screens;

import com.bussin.desktop.ui.components.*;
import com.bussin.desktop.ui.theme.BussinTheme;
import java.awt.*;
import javax.swing.*;

public class DashboardScreen extends JPanel {

        public DashboardScreen() {

                setBackground(
                                BussinTheme.BACKGROUND);

                setLayout(
                                new BorderLayout());

                add(
                                createScrollContent(),
                                BorderLayout.CENTER);
        }

        private JScrollPane createScrollContent() {

                JPanel content = new JPanel();

                content.setBackground(
                                BussinTheme.BACKGROUND);

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

                // ========================================================
                // PAGE HEADER
                // ========================================================

                JLabel title = AppLabel.title("Dashboard");

                JLabel subtitle = AppLabel.secondary(
                                "Monitor today's bus operations, departures, and passenger queues.");

                content.add(title);

                content.add(
                                Box.createVerticalStrut(4));

                content.add(subtitle);

                content.add(
                                Box.createVerticalStrut(24));

                // ========================================================
                // STATISTICS
                // ========================================================

                JPanel stats = new JPanel(
                                new GridLayout(
                                                1,
                                                4,
                                                14,
                                                0));

                stats.setOpaque(false);

                stats.add(
                                new StatCard(
                                                "ACTIVE BUSES",
                                                "12",
                                                "3 currently boarding"));

                stats.add(
                                new StatCard(
                                                "DEPARTURES",
                                                "8",
                                                "Today's scheduled trips"));

                stats.add(
                                new StatCard(
                                                "WAITING PASSENGERS",
                                                "24",
                                                "Across active queues"));

                stats.add(
                                new StatCard(
                                                "SEAT OCCUPANCY",
                                                "68%",
                                                "Current active trips"));

                content.add(stats);

                content.add(
                                Box.createVerticalStrut(24));

                // ========================================================
                // MAIN CONTENT
                // ========================================================

                JPanel mainContent = new JPanel(
                                new GridLayout(
                                                1,
                                                2,
                                                18,
                                                0));

                mainContent.setOpaque(false);

                // --------------------------------------------------------
                // Active departure
                // --------------------------------------------------------

                AppCard departureCard = new AppCard();

                departureCard.setLayout(
                                new BorderLayout(
                                                0,
                                                14));

                departureCard.add(
                                createSectionHeader(
                                                "Active Departures",
                                                "View today's active trips"),
                                BorderLayout.NORTH);

                JPanel buses = new JPanel();

                buses.setOpaque(false);

                buses.setLayout(
                                new BoxLayout(
                                                buses,
                                                BoxLayout.Y_AXIS));

                buses.add(
                                new BusCard(
                                                "BUS 102",
                                                "Manila → Batangas",
                                                "09:30 AM",
                                                36,
                                                50,
                                                "Boarding"));

                buses.add(
                                Box.createVerticalStrut(12));

                buses.add(
                                new BusCard(
                                                "BUS 114",
                                                "Manila → Lucena",
                                                "10:00 AM",
                                                42,
                                                50,
                                                "On Route"));

                departureCard.add(
                                buses,
                                BorderLayout.CENTER);

                // --------------------------------------------------------
                // Queue
                // --------------------------------------------------------

                AppCard queueCard = new AppCard();

                queueCard.setLayout(
                                new BorderLayout(
                                                0,
                                                14));

                queueCard.add(
                                createSectionHeader(
                                                "Queue Status",
                                                "Current passenger activity"),
                                BorderLayout.NORTH);

                JPanel queues = new JPanel();

                queues.setOpaque(false);

                queues.setLayout(
                                new BoxLayout(
                                                queues,
                                                BoxLayout.Y_AXIS));

                queues.add(
                                new QueueCard(
                                                "Queue A-023",
                                                "Juan Dela Cruz",
                                                "Manila → Batangas",
                                                "Serving",
                                                12,
                                                3,
                                                18));

                queues.add(
                                Box.createVerticalStrut(12));

                queues.add(
                                new QueueCard(
                                                "Queue B-018",
                                                "Maria Santos",
                                                "Manila → Lucena",
                                                "Waiting",
                                                8,
                                                2,
                                                15));

                queueCard.add(
                                queues,
                                BorderLayout.CENTER);

                mainContent.add(
                                departureCard);

                mainContent.add(
                                queueCard);

                content.add(mainContent);

                content.add(
                                Box.createVerticalStrut(24));

                // ========================================================
                // QUICK ACTIONS
                // ========================================================

                AppCard quickActions = new AppCard();

                quickActions.setLayout(
                                new BorderLayout(
                                                0,
                                                16));

                quickActions.add(
                                createSectionHeader(
                                                "Quick Actions",
                                                "Common BUSSIN operations"),
                                BorderLayout.NORTH);

                JPanel buttons = new JPanel(
                                new FlowLayout(
                                                FlowLayout.LEFT,
                                                10,
                                                0));

                buttons.setOpaque(false);

                buttons.add(
                                new AppButton(
                                                "Manage Queue"));

                buttons.add(
                                new AppButton(
                                                "Create Booking"));

                buttons.add(
                                new AppButton(
                                                "Manage Trips",
                                                AppButton.Variant.SECONDARY));

                buttons.add(
                                new AppButton(
                                                "View Reports",
                                                AppButton.Variant.SECONDARY));

                quickActions.add(
                                buttons,
                                BorderLayout.CENTER);

                content.add(
                                quickActions);

                // ========================================================
                // SCROLL PANE
                // ========================================================

                JScrollPane scrollPane = new JScrollPane(content);

                scrollPane.setBorder(
                                BorderFactory.createEmptyBorder());

                scrollPane.setHorizontalScrollBarPolicy(
                                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

                scrollPane.getVerticalScrollBar()
                                .setUnitIncrement(16);

                return scrollPane;
        }

        private JPanel createSectionHeader(
                        String title,
                        String subtitle) {

                JPanel header = new JPanel(
                                new BorderLayout());

                header.setOpaque(false);

                JPanel text = new JPanel();

                text.setOpaque(false);

                text.setLayout(
                                new BoxLayout(
                                                text,
                                                BoxLayout.Y_AXIS));

                JLabel titleLabel = new JLabel(title);

                titleLabel.setFont(
                                BussinTheme.CARD_TITLE);

                titleLabel.setForeground(
                                BussinTheme.TEXT_PRIMARY);

                JLabel subtitleLabel = new JLabel(subtitle);

                subtitleLabel.setFont(
                                BussinTheme.SMALL);

                subtitleLabel.setForeground(
                                BussinTheme.TEXT_MUTED);

                text.add(titleLabel);

                text.add(
                                Box.createVerticalStrut(3));

                text.add(subtitleLabel);

                header.add(
                                text,
                                BorderLayout.WEST);

                return header;
        }
}