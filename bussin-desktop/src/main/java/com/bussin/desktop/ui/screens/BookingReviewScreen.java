package com.bussin.desktop.ui.screens;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.bussin.desktop.ui.components.AppButton;
import com.bussin.desktop.ui.components.AppCard;
import com.bussin.desktop.ui.components.AppLabel;
import com.bussin.desktop.ui.components.PageContent;
import com.bussin.desktop.ui.flow.BookingFlowState;
import com.bussin.desktop.ui.flow.CommuterTrip;
import com.bussin.desktop.ui.theme.BussinTheme;

public class BookingReviewScreen extends JPanel {

        private final Consumer<String> navigationHandler;
        private final BookingFlowState flowState;

        public BookingReviewScreen(
                        Consumer<String> navigationHandler,
                        BookingFlowState flowState) {

                this.navigationHandler = navigationHandler;
                this.flowState = flowState;

                initializeUI();
        }

        private void initializeUI() {

                setBackground(BussinTheme.BACKGROUND);
                setLayout(new BorderLayout());

                PageContent page = new PageContent();

                page.addBlock(createHeader(), 0);
                page.addBlock(createSummary(), 20);
                page.addBlock(createPassengerCard(), 18);
                page.addBlock(createActions(), 18);

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
                                                "Review Booking"));

                panel.add(
                                Box.createVerticalStrut(5));

                panel.add(
                                AppLabel.secondary(
                                                "Review your trip and passenger information before confirming."));

                return panel;
        }

        private JPanel createSummary() {

                AppCard card = new AppCard();

                card.setLayout(
                                new BorderLayout());

                JPanel content = new JPanel();
                content.setOpaque(false);
                content.setLayout(
                                new BoxLayout(
                                                content,
                                                BoxLayout.Y_AXIS));

                CommuterTrip trip = flowState.getSelectedTrip();

                if (trip == null) {
                        content.add(
                                        new JLabel(
                                                        "No trip selected."));
                        card.add(content);
                        return card;
                }

                content.add(
                                AppLabel.section(
                                                trip.getRoute()));

                content.add(
                                Box.createVerticalStrut(12));

                addLine(
                                content,
                                "Trip",
                                trip.getTripId());

                addLine(
                                content,
                                "Departure",
                                trip.getDeparture());

                addLine(
                                content,
                                "Arrival",
                                trip.getArrival());

                addLine(
                                content,
                                "Bus",
                                trip.getBusNumber());

                addLine(
                                content,
                                "Seat",
                                flowState.getSelectedSeat());

                addLine(
                                content,
                                "Fare",
                                String.format(
                                                "₱%,.2f",
                                                trip.getFare()));

                card.add(
                                content,
                                BorderLayout.CENTER);

                return card;
        }

        private JPanel createPassengerCard() {

                AppCard card = new AppCard();

                card.setLayout(
                                new BorderLayout());

                JPanel content = new JPanel();
                content.setOpaque(false);
                content.setLayout(
                                new BoxLayout(
                                                content,
                                                BoxLayout.Y_AXIS));

                content.add(
                                AppLabel.section(
                                                "Passenger"));

                content.add(
                                Box.createVerticalStrut(12));

                addLine(
                                content,
                                "Name",
                                flowState.getPassengerName());

                addLine(
                                content,
                                "Phone",
                                flowState.getPassengerPhone());

                addLine(
                                content,
                                "Email",
                                flowState.getPassengerEmail());

                card.add(
                                content,
                                BorderLayout.CENTER);

                return card;
        }

        private void addLine(
                        JPanel parent,
                        String label,
                        String value) {

                JPanel row = new JPanel(
                                new BorderLayout(
                                                20,
                                                0));

                row.setOpaque(false);

                JLabel left = new JLabel(label);

                left.setFont(
                                BussinTheme.SMALL_BOLD);

                left.setForeground(
                                BussinTheme.TEXT_SECONDARY);

                JLabel right = new JLabel(
                                value == null
                                                ? "-"
                                                : value);

                right.setFont(
                                BussinTheme.BODY);

                right.setForeground(
                                BussinTheme.TEXT_PRIMARY);

                row.add(
                                left,
                                BorderLayout.WEST);

                row.add(
                                right,
                                BorderLayout.EAST);

                parent.add(row);
                parent.add(
                                Box.createVerticalStrut(10));
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
                                event -> navigationHandler.accept(
                                                "passenger-information"));

                AppButton confirm = new AppButton(
                                "Confirm Booking");

                confirm.addActionListener(
                                event -> confirmBooking());

                panel.add(back);
                panel.add(confirm);

                return panel;
        }

        private void confirmBooking() {

                if (!flowState.hasTrip()
                                || !flowState.hasSeat()
                                || !flowState.hasPassengerInformation()) {

                        javax.swing.JOptionPane.showMessageDialog(
                                        this,
                                        "Some booking information is incomplete.",
                                        "Incomplete Booking",
                                        javax.swing.JOptionPane.WARNING_MESSAGE);

                        return;
                }

                CommuterTrip trip = flowState.getSelectedTrip();

                if (!trip.isSeatAvailable(
                                flowState.getSelectedSeat())) {

                        javax.swing.JOptionPane.showMessageDialog(
                                        this,
                                        "That seat is no longer available.",
                                        "Seat Unavailable",
                                        javax.swing.JOptionPane.WARNING_MESSAGE);

                        navigationHandler.accept(
                                        "seat-selection");

                        return;
                }

                int result = javax.swing.JOptionPane.showConfirmDialog(
                                this,
                                "Confirm this booking for "
                                                + trip.getRoute()
                                                + "?"
                                                + "\nSeat: "
                                                + flowState.getSelectedSeat()
                                                + "\nFare: ₱"
                                                + String.format(
                                                                "%,.2f",
                                                                trip.getFare()),
                                "Confirm Booking",
                                javax.swing.JOptionPane.YES_NO_OPTION,
                                javax.swing.JOptionPane.QUESTION_MESSAGE);

                if (result != javax.swing.JOptionPane.YES_OPTION) {

                        return;
                }

                String bookingId = com.bussin.desktop.ui.flow.BookingStore
                                .generateBookingId();

                trip.reserveSeat(
                                flowState.getSelectedSeat());

                com.bussin.desktop.ui.flow.BookingStore
                                .addBooking(
                                                new com.bussin.desktop.ui.flow.BookingStore.UserBooking(
                                                                bookingId,
                                                                flowState.getPassengerEmail(),
                                                                flowState.getPassengerName(),
                                                                flowState.getPassengerPhone(),
                                                                flowState.getPassengerEmail(),
                                                                trip.getTripId(),
                                                                trip.getRoute(),
                                                                trip.getDeparture(),
                                                                trip.getArrival(),
                                                                trip.getBusNumber(),
                                                                flowState.getSelectedSeat(),
                                                                trip.getFare(),
                                                                "Unpaid",
                                                                "Pending",
                                                                java.time.LocalDateTime.now()));

                flowState.setBookingId(
                                bookingId);

                navigationHandler.accept(
                                "booking-confirmation");
        }
}