package com.bussin.desktop.ui.components;

import com.bussin.desktop.ui.theme.BussinTheme;
import java.awt.*;
import javax.swing.*;

public class QueueCard extends AppCard {

        public QueueCard(
                        String queueNumber,
                        String passenger,
                        String destination,
                        String status,
                        int waiting,
                        int serving,
                        int completed) {

                setLayout(new BorderLayout(0, 16));

                // --------------------------------------------------------
                // Header
                // --------------------------------------------------------

                JPanel header = new JPanel(
                                new BorderLayout());

                header.setOpaque(false);

                JLabel queueLabel = new JLabel(queueNumber);

                queueLabel.setFont(
                                BussinTheme.CARD_TITLE);

                queueLabel.setForeground(
                                BussinTheme.TEXT_PRIMARY);

                AppBadge.Status badgeStatus = switch (status.toLowerCase()) {

                        case "serving" ->
                                AppBadge.Status.INFO;

                        case "completed" ->
                                AppBadge.Status.SUCCESS;

                        case "skipped" ->
                                AppBadge.Status.DANGER;

                        default ->
                                AppBadge.Status.WARNING;
                };

                AppBadge badge = new AppBadge(
                                status,
                                badgeStatus);

                header.add(
                                queueLabel,
                                BorderLayout.WEST);

                header.add(
                                badge,
                                BorderLayout.EAST);

                add(header, BorderLayout.NORTH);

                // --------------------------------------------------------
                // Passenger
                // --------------------------------------------------------

                JPanel passengerPanel = new JPanel();

                passengerPanel.setOpaque(false);

                passengerPanel.setLayout(
                                new BoxLayout(
                                                passengerPanel,
                                                BoxLayout.Y_AXIS));

                JLabel passengerLabel = new JLabel(passenger);

                passengerLabel.setFont(
                                BussinTheme.BODY_MEDIUM);

                passengerLabel.setForeground(
                                BussinTheme.TEXT_PRIMARY);

                JLabel destinationLabel = new JLabel(destination);

                destinationLabel.setFont(
                                BussinTheme.SMALL);

                destinationLabel.setForeground(
                                BussinTheme.TEXT_SECONDARY);

                passengerPanel.add(passengerLabel);
                passengerPanel.add(
                                Box.createVerticalStrut(3));
                passengerPanel.add(destinationLabel);

                add(
                                passengerPanel,
                                BorderLayout.CENTER);

                // --------------------------------------------------------
                // Queue statistics
                // --------------------------------------------------------

                JPanel stats = new JPanel(
                                new GridLayout(1, 3, 8, 0));

                stats.setOpaque(false);

                stats.add(
                                createStat(
                                                "Waiting",
                                                String.valueOf(waiting)));

                stats.add(
                                createStat(
                                                "Serving",
                                                String.valueOf(serving)));

                stats.add(
                                createStat(
                                                "Completed",
                                                String.valueOf(completed)));

                add(stats, BorderLayout.SOUTH);
        }

        private JPanel createStat(
                        String title,
                        String value) {

                JPanel panel = new JPanel();
                panel.setOpaque(false);

                panel.setLayout(
                                new BoxLayout(
                                                panel,
                                                BoxLayout.Y_AXIS));

                JLabel valueLabel = new JLabel(value);

                valueLabel.setFont(
                                BussinTheme.CARD_TITLE);

                valueLabel.setForeground(
                                BussinTheme.TEXT_PRIMARY);

                JLabel titleLabel = new JLabel(title);

                titleLabel.setFont(
                                BussinTheme.SMALL);

                titleLabel.setForeground(
                                BussinTheme.TEXT_MUTED);

                panel.add(valueLabel);
                panel.add(titleLabel);

                return panel;
        }
}