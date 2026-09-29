package com.bussin.desktop.ui.components;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import com.bussin.desktop.ui.theme.BussinTheme;

public class BusCard extends AppCard {

        public BusCard(
                        String busNumber,
                        String route,
                        String departure,
                        int occupiedSeats,
                        int totalSeats,
                        String status) {

                setLayout(new BorderLayout(0, 14));

                // --------------------------------------------------------
                // Normalize occupancy values
                // --------------------------------------------------------

                int safeTotalSeats = Math.max(totalSeats, 1);
                int safeOccupiedSeats = Math.max(
                                0,
                                Math.min(occupiedSeats, safeTotalSeats));

                // --------------------------------------------------------
                // Header
                // --------------------------------------------------------

                JPanel header = new JPanel(new BorderLayout());
                header.setOpaque(false);

                JPanel busInfo = new JPanel();
                busInfo.setOpaque(false);

                busInfo.setLayout(new BoxLayout(
                                busInfo,
                                BoxLayout.Y_AXIS));

                JLabel busLabel = new JLabel(busNumber);
                busLabel.setFont(BussinTheme.CARD_TITLE);
                busLabel.setForeground(
                                BussinTheme.TEXT_PRIMARY);

                JLabel routeLabel = new JLabel(route);
                routeLabel.setFont(BussinTheme.SMALL);
                routeLabel.setForeground(
                                BussinTheme.TEXT_SECONDARY);

                busInfo.add(busLabel);
                busInfo.add(Box.createVerticalStrut(3));
                busInfo.add(routeLabel);

                // --------------------------------------------------------
                // Status
                // --------------------------------------------------------

                AppBadge.Status badgeStatus = switch (status.toLowerCase()) {

                        case "boarding", "on route" ->
                                AppBadge.Status.INFO;

                        case "completed" ->
                                AppBadge.Status.SUCCESS;

                        case "delayed" ->
                                AppBadge.Status.WARNING;

                        case "cancelled" ->
                                AppBadge.Status.DANGER;

                        default ->
                                AppBadge.Status.NEUTRAL;
                };

                AppBadge badge = new AppBadge(
                                status,
                                badgeStatus);

                header.add(
                                busInfo,
                                BorderLayout.WEST);

                header.add(
                                badge,
                                BorderLayout.EAST);

                add(
                                header,
                                BorderLayout.NORTH);

                // --------------------------------------------------------
                // Departure
                // --------------------------------------------------------

                JPanel departurePanel = new JPanel(
                                new BorderLayout());

                departurePanel.setOpaque(false);

                JLabel departureTitle = new JLabel(
                                "Departure");

                departureTitle.setFont(
                                BussinTheme.SMALL);

                departureTitle.setForeground(
                                BussinTheme.TEXT_MUTED);

                JLabel departureValue = new JLabel(
                                departure);

                departureValue.setFont(
                                BussinTheme.BODY_MEDIUM);

                departureValue.setForeground(
                                BussinTheme.TEXT_PRIMARY);

                departurePanel.add(
                                departureTitle,
                                BorderLayout.WEST);

                departurePanel.add(
                                departureValue,
                                BorderLayout.EAST);

                add(
                                departurePanel,
                                BorderLayout.CENTER);

                // --------------------------------------------------------
                // Occupancy
                // --------------------------------------------------------

                JPanel occupancy = new JPanel();

                occupancy.setOpaque(false);

                occupancy.setLayout(
                                new BoxLayout(
                                                occupancy,
                                                BoxLayout.Y_AXIS));

                JPanel occupancyText = new JPanel(
                                new BorderLayout());

                occupancyText.setOpaque(false);

                JLabel seatsLabel = new JLabel(
                                "Seat occupancy");

                seatsLabel.setFont(
                                BussinTheme.SMALL);

                seatsLabel.setForeground(
                                BussinTheme.TEXT_SECONDARY);

                JLabel seatsValue = new JLabel(
                                safeOccupiedSeats
                                                + " / "
                                                + totalSeats);

                seatsValue.setFont(
                                BussinTheme.SMALL_BOLD);

                seatsValue.setForeground(
                                BussinTheme.TEXT_PRIMARY);

                occupancyText.add(
                                seatsLabel,
                                BorderLayout.WEST);

                occupancyText.add(
                                seatsValue,
                                BorderLayout.EAST);

                // --------------------------------------------------------
                // Progress bar
                // --------------------------------------------------------

                JProgressBar progress = new JProgressBar(
                                0,
                                safeTotalSeats);

                progress.setValue(
                                safeOccupiedSeats);

                progress.setStringPainted(false);
                progress.setBorderPainted(false);

                progress.setPreferredSize(
                                new Dimension(100, 7));

                progress.setMaximumSize(
                                new Dimension(
                                                Integer.MAX_VALUE,
                                                7));

                occupancy.add(
                                occupancyText);

                occupancy.add(
                                Box.createVerticalStrut(6));

                occupancy.add(
                                progress);

                add(
                                occupancy,
                                BorderLayout.SOUTH);
        }
}