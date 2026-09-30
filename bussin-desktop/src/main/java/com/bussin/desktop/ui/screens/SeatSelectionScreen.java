package com.bussin.desktop.ui.screens;

import java.awt.BorderLayout;
import java.awt.Color;
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
import com.bussin.desktop.ui.flow.BookingFlowState;
import com.bussin.desktop.ui.flow.CommuterTrip;
import com.bussin.desktop.ui.theme.BussinTheme;

public class SeatSelectionScreen extends JPanel {

        private final Consumer<String> navigationHandler;
        private final BookingFlowState flowState;

        private final JPanel seatPanel = new JPanel();

        private String selectedSeat;

        private AppButton continueButton;

        public SeatSelectionScreen(
                        Consumer<String> navigationHandler,
                        BookingFlowState flowState) {

                this.navigationHandler = navigationHandler;
                this.flowState = flowState;

                initializeUI();
        }

        private void initializeUI() {

                setBackground(
                                BussinTheme.BACKGROUND);

                setLayout(
                                new BorderLayout());

                PageContent page = new PageContent();

                page.addBlock(
                                createHeader(),
                                0);

                page.addBlock(
                                createTripSummary(),
                                20);

                page.addBlock(
                                createSeatCard(),
                                18);

                page.addBlock(
                                createActions(),
                                18);

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
                                AppLabel.title(
                                                "Select Your Seat"));

                panel.add(
                                Box.createVerticalStrut(5));

                panel.add(
                                AppLabel.secondary(
                                                "Choose an available seat for your trip."));

                return panel;
        }

        private JPanel createTripSummary() {

                AppCard card = new AppCard();

                card.setLayout(
                                new BorderLayout());

                CommuterTrip trip = flowState.getSelectedTrip();

                if (trip == null) {

                        card.add(
                                        new JLabel(
                                                        "No trip selected."),
                                        BorderLayout.CENTER);

                        return card;
                }

                JPanel content = new JPanel();

                content.setOpaque(false);

                content.setLayout(
                                new BoxLayout(
                                                content,
                                                BoxLayout.Y_AXIS));

                content.add(
                                AppLabel.section(
                                                trip.getRoute()));

                content.add(
                                Box.createVerticalStrut(6));

                JLabel details = new JLabel(
                                trip.getDeparture()
                                                + " → "
                                                + trip.getArrival()
                                                + "   •   "
                                                + trip.getBusNumber()
                                                + "   •   ₱"
                                                + String.format(
                                                                "%,.2f",
                                                                trip.getFare()));

                details.setFont(
                                BussinTheme.BODY);

                details.setForeground(
                                BussinTheme.TEXT_SECONDARY);

                content.add(details);

                JLabel availability = new JLabel(
                                trip.getAvailableSeats()
                                                + " of "
                                                + trip.getTotalSeats()
                                                + " seats available");

                availability.setFont(
                                BussinTheme.SMALL_BOLD);

                availability.setForeground(
                                trip.getAvailableSeats() > 0
                                                ? BussinTheme.SUCCESS
                                                : BussinTheme.DANGER);

                content.add(
                                Box.createVerticalStrut(8));

                content.add(availability);

                card.add(
                                content,
                                BorderLayout.CENTER);

                return card;
        }

        private JPanel createSeatCard() {

                AppCard card = new AppCard();

                card.setLayout(
                                new BorderLayout(
                                                0,
                                                16));

                JPanel heading = new JPanel(
                                new BorderLayout());

                heading.setOpaque(false);

                heading.add(
                                AppLabel.section(
                                                "Bus Seats"),
                                BorderLayout.WEST);

                JLabel legend = new JLabel(
                                "Available • Selected • Occupied");

                legend.setFont(
                                BussinTheme.SMALL);

                legend.setForeground(
                                BussinTheme.TEXT_MUTED);

                heading.add(
                                legend,
                                BorderLayout.EAST);

                seatPanel.removeAll();

                seatPanel.setOpaque(false);

                seatPanel.setLayout(
                                new GridLayout(
                                                0,
                                                5,
                                                8,
                                                8));

                buildSeats();

                card.add(
                                heading,
                                BorderLayout.NORTH);

                card.add(
                                seatPanel,
                                BorderLayout.CENTER);

                return card;
        }

        private void buildSeats() {

                seatPanel.removeAll();

                CommuterTrip trip = flowState.getSelectedTrip();

                if (trip == null) {
                        return;
                }

                int rows = (int) Math.ceil(
                                trip.getTotalSeats() / 4.0);

                int number = 1;

                for (int row = 0; row < rows; row++) {

                        for (int column = 0; column < 5; column++) {

                                /*
                                 * Column 3 represents the aisle.
                                 */
                                if (column == 2) {

                                        seatPanel.add(
                                                        new JLabel(""));

                                        continue;
                                }

                                if (number > trip.getTotalSeats()) {

                                        seatPanel.add(
                                                        new JLabel(""));

                                        continue;
                                }

                                String seat = String.format(
                                                "%02d%c",
                                                ((number - 1) / 4) + 1,
                                                "ABCD".charAt(
                                                                (number - 1) % 4));

                                JButton button = createSeatButton(
                                                trip,
                                                seat);

                                seatPanel.add(button);

                                number++;
                        }
                }

                seatPanel.revalidate();
                seatPanel.repaint();
        }

        private JButton createSeatButton(
                        CommuterTrip trip,
                        String seat) {

                JButton button = new JButton(seat);

                button.setFocusPainted(false);

                button.setFont(
                                BussinTheme.SMALL_BOLD);

                button.setPreferredSize(
                                new java.awt.Dimension(
                                                70,
                                                42));

                button.setBorder(
                                BorderFactory.createLineBorder(
                                                BussinTheme.BORDER));

                /*
                 * Occupied seats are read from the shared
                 * CommuterTrip instance.
                 */
                if (!trip.isSeatAvailable(seat)) {

                        button.setEnabled(false);

                        button.setText(
                                        seat + " • Taken");

                        button.setBackground(
                                        BussinTheme.COOL_GRAY);

                        button.setForeground(
                                        BussinTheme.TEXT_MUTED);

                        return button;
                }

                button.setBackground(
                                BussinTheme.SURFACE);

                button.setForeground(
                                BussinTheme.TEXT_PRIMARY);

                button.addActionListener(
                                event -> selectSeat(
                                                button,
                                                seat));

                return button;
        }

        private void selectSeat(
                        JButton button,
                        String seat) {

                selectedSeat = seat;

                /*
                 * Store the temporary selection in the
                 * booking flow state.
                 *
                 * The seat is NOT reserved yet.
                 */
                flowState.setSelectedSeat(
                                selectedSeat);

                /*
                 * Reset all available seats to their
                 * normal appearance.
                 */
                for (java.awt.Component component : seatPanel.getComponents()) {

                        if (component instanceof JButton other
                                        && other.isEnabled()) {

                                other.setBackground(
                                                BussinTheme.SURFACE);

                                other.setForeground(
                                                BussinTheme.TEXT_PRIMARY);
                        }
                }

                /*
                 * Highlight the currently selected seat.
                 */
                button.setBackground(
                                BussinTheme.PRIMARY);

                button.setForeground(
                                Color.WHITE);

                /*
                 * IMPORTANT:
                 * Enable Continue immediately after a
                 * seat has been selected.
                 */
                if (continueButton != null) {
                        continueButton.setEnabled(true);
                }
        }

        private JPanel createActions() {

                JPanel panel = new JPanel(
                                new FlowLayout(
                                                FlowLayout.RIGHT,
                                                10,
                                                0));

                panel.setOpaque(false);

                AppButton back = new AppButton(
                                "Back",
                                AppButton.Variant.SECONDARY);

                back.addActionListener(
                                event -> {

                                        flowState.setSelectedSeat(
                                                        null);

                                        selectedSeat = null;

                                        navigationHandler.accept(
                                                        "trip-search");
                                });

                continueButton = new AppButton(
                                "Continue");

                /*
                 * Continue starts disabled unless a seat
                 * already exists in the flow state.
                 */
                continueButton.setEnabled(
                                flowState.hasSeat());

                continueButton.addActionListener(
                                event -> {

                                        if (!flowState.hasSeat()) {
                                                return;
                                        }

                                        navigationHandler.accept(
                                                        "passenger-information");
                                });

                panel.add(back);
                panel.add(continueButton);

                return panel;
        }
}