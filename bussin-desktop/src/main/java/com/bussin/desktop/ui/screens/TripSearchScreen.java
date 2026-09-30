package com.bussin.desktop.ui.screens;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.bussin.desktop.data.TripStore;
import com.bussin.desktop.ui.components.AppButton;
import com.bussin.desktop.ui.components.AppCard;
import com.bussin.desktop.ui.components.AppLabel;
import com.bussin.desktop.ui.components.PageContent;
import com.bussin.desktop.ui.components.ResponsiveLayouts;
import com.bussin.desktop.ui.flow.BookingFlowState;
import com.bussin.desktop.ui.flow.CommuterTrip;
import com.bussin.desktop.ui.theme.BussinTheme;

public class TripSearchScreen extends JPanel {

        private final Consumer<String> navigationHandler;
        private final BookingFlowState flowState;

        private final JPanel resultsPanel = new JPanel();

        private final JComboBox<String> originCombo = new JComboBox<>(new String[] {
                        "Manila"
        });

        private final JComboBox<String> destinationCombo = new JComboBox<>(new String[] {
                        "All destinations",
                        "Batangas",
                        "Lucena",
                        "Nasugbu",
                        "Naga"
        });

        public TripSearchScreen(
                        Consumer<String> navigationHandler,
                        BookingFlowState flowState) {

                this.navigationHandler = navigationHandler;
                this.flowState = flowState;

                initializeUI();
                renderResults();
        }

        private void initializeUI() {

                setBackground(BussinTheme.BACKGROUND);
                setLayout(new BorderLayout());

                PageContent page = new PageContent();

                page.addBlock(createHeader(), 0);
                page.addBlock(createSearchCard(), 24);
                page.addBlock(resultsPanel, 18);

                add(
                                page.inScrollPane(),
                                BorderLayout.CENTER);
        }

        private JPanel createHeader() {

                JPanel panel = new JPanel();
                panel.setOpaque(false);
                panel.setLayout(
                                new BoxLayout(
                                                panel,
                                                BoxLayout.Y_AXIS));

                panel.add(
                                AppLabel.title("Find a Trip"));

                panel.add(
                                Box.createVerticalStrut(5));

                panel.add(
                                AppLabel.secondary(
                                                "Search available BUSSIN trips and choose your preferred schedule."));

                return panel;
        }

        private JPanel createSearchCard() {

                AppCard card = new AppCard();

                card.setLayout(
                                new BorderLayout(
                                                16,
                                                12));

                JPanel fields = new JPanel(
                                new ResponsiveLayouts.Grid(
                                                2,
                                                220,
                                                14));

                fields.setOpaque(false);

                JPanel from = fieldPanel(
                                "FROM",
                                originCombo);

                JPanel to = fieldPanel(
                                "TO",
                                destinationCombo);

                fields.add(from);
                fields.add(to);

                AppButton searchButton = new AppButton(
                                "Search Trips");

                searchButton.addActionListener(
                                event -> renderResults());

                JPanel actions = new JPanel(
                                new FlowLayout(
                                                FlowLayout.RIGHT,
                                                0,
                                                0));

                actions.setOpaque(false);

                actions.add(searchButton);

                card.add(
                                fields,
                                BorderLayout.CENTER);

                card.add(
                                actions,
                                BorderLayout.SOUTH);

                return card;
        }

        private JPanel fieldPanel(
                        String title,
                        JComboBox<String> combo) {

                JPanel panel = new JPanel();

                panel.setOpaque(false);

                panel.setLayout(
                                new BoxLayout(
                                                panel,
                                                BoxLayout.Y_AXIS));

                JLabel label = new JLabel(title);

                label.setFont(
                                BussinTheme.SMALL_BOLD);

                label.setForeground(
                                BussinTheme.TEXT_SECONDARY);

                combo.setPreferredSize(
                                new java.awt.Dimension(
                                                0,
                                                38));

                panel.add(label);

                panel.add(
                                Box.createVerticalStrut(6));

                panel.add(combo);

                return panel;
        }

        private void renderResults() {

                resultsPanel.removeAll();

                resultsPanel.setOpaque(false);

                resultsPanel.setLayout(
                                new BorderLayout());

                JPanel wrapper = new JPanel();

                wrapper.setOpaque(false);

                wrapper.setLayout(
                                new BoxLayout(
                                                wrapper,
                                                BoxLayout.Y_AXIS));

                JLabel title = AppLabel.section(
                                "Available Trips");

                wrapper.add(title);

                wrapper.add(
                                Box.createVerticalStrut(12));

                String origin = String.valueOf(
                                originCombo.getSelectedItem());

                String destination = String.valueOf(
                                destinationCombo.getSelectedItem());

                /*
                 * ------------------------------------------------------------
                 * Shared trip state
                 * ------------------------------------------------------------
                 *
                 * TripSearchScreen no longer creates its own trips.
                 *
                 * TripStore is now the single source of truth for:
                 *
                 * - Trip information
                 * - Bus information
                 * - Seat availability
                 * - Seat reservations
                 *
                 * This means the trip selected here is the exact same
                 * CommuterTrip instance used by the rest of the booking flow.
                 */
                List<CommuterTrip> matches = new ArrayList<>(
                                TripStore.search(
                                                origin,
                                                "All destinations".equalsIgnoreCase(destination)
                                                                ? ""
                                                                : destination));

                if (matches.isEmpty()) {

                        wrapper.add(
                                        createEmptyState(
                                                        "No trips match your selected destination."));

                } else {

                        JPanel grid = new JPanel(
                                        new GridLayout(
                                                        0,
                                                        2,
                                                        14,
                                                        14));

                        grid.setOpaque(false);

                        for (CommuterTrip trip : matches) {

                                grid.add(
                                                createTripCard(trip));
                        }

                        wrapper.add(grid);
                }

                resultsPanel.add(
                                wrapper,
                                BorderLayout.CENTER);

                resultsPanel.revalidate();
                resultsPanel.repaint();
        }

        private JPanel createTripCard(
                        CommuterTrip trip) {

                AppCard card = new AppCard();

                card.setLayout(
                                new BorderLayout(
                                                0,
                                                14));

                JPanel top = new JPanel();

                top.setOpaque(false);

                top.setLayout(
                                new BoxLayout(
                                                top,
                                                BoxLayout.Y_AXIS));

                JLabel route = AppLabel.section(
                                trip.getRoute());

                JLabel tripId = new JLabel(
                                trip.getTripId());

                tripId.setFont(
                                BussinTheme.SMALL_BOLD);

                tripId.setForeground(
                                BussinTheme.TEXT_MUTED);

                top.add(route);

                top.add(
                                Box.createVerticalStrut(4));

                top.add(tripId);

                JPanel details = new JPanel(
                                new GridLayout(
                                                2,
                                                2,
                                                12,
                                                8));

                details.setOpaque(false);

                details.add(
                                info(
                                                "DEPARTURE",
                                                trip.getDeparture()));

                details.add(
                                info(
                                                "ARRIVAL",
                                                trip.getArrival()));

                details.add(
                                info(
                                                "BUS",
                                                trip.getBusNumber()));

                details.add(
                                info(
                                                "FARE",
                                                String.format(
                                                                "₱%,.2f",
                                                                trip.getFare())));

                JLabel availability = new JLabel(
                                trip.getAvailableSeats()
                                                + " seats available");

                availability.setFont(
                                BussinTheme.SMALL_BOLD);

                availability.setForeground(
                                trip.getAvailableSeats() > 0
                                                ? BussinTheme.SUCCESS
                                                : BussinTheme.DANGER);

                AppButton select = new AppButton(
                                trip.getAvailableSeats() > 0
                                                ? "Select Trip"
                                                : "Fully Booked");

                select.setEnabled(
                                trip.getAvailableSeats() > 0);

                select.addActionListener(
                                event -> {

                                        flowState.reset();

                                        flowState.setSelectedTrip(
                                                        trip);

                                        navigationHandler.accept(
                                                        "seat-selection");
                                });

                card.add(
                                top,
                                BorderLayout.NORTH);

                card.add(
                                details,
                                BorderLayout.CENTER);

                JPanel bottom = new JPanel(
                                new BorderLayout());

                bottom.setOpaque(false);

                bottom.add(
                                availability,
                                BorderLayout.WEST);

                bottom.add(
                                select,
                                BorderLayout.EAST);

                card.add(
                                bottom,
                                BorderLayout.SOUTH);

                return card;
        }

        private JPanel info(
                        String title,
                        String value) {

                JPanel panel = new JPanel();

                panel.setOpaque(false);

                panel.setLayout(
                                new BoxLayout(
                                                panel,
                                                BoxLayout.Y_AXIS));

                JLabel titleLabel = new JLabel(title);

                titleLabel.setFont(
                                BussinTheme.SMALL_BOLD);

                titleLabel.setForeground(
                                BussinTheme.TEXT_MUTED);

                JLabel valueLabel = new JLabel(value);

                valueLabel.setFont(
                                BussinTheme.BODY_MEDIUM);

                valueLabel.setForeground(
                                BussinTheme.TEXT_PRIMARY);

                panel.add(titleLabel);

                panel.add(
                                Box.createVerticalStrut(3));

                panel.add(valueLabel);

                return panel;
        }

        private JPanel createEmptyState(
                        String message) {

                JPanel panel = new JPanel(
                                new FlowLayout(
                                                FlowLayout.LEFT));

                panel.setOpaque(false);

                panel.setBorder(
                                BorderFactory.createEmptyBorder(
                                                20,
                                                0,
                                                20,
                                                0));

                JLabel label = new JLabel(message);

                label.setFont(
                                BussinTheme.BODY);

                label.setForeground(
                                BussinTheme.TEXT_MUTED);

                panel.add(label);

                return panel;
        }
}
