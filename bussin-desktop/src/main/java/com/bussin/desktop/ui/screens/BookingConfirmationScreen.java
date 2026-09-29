package com.bussin.desktop.ui.screens;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
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

public class BookingConfirmationScreen extends JPanel {

        private final Consumer<String> navigationHandler;
        private final BookingFlowState flowState;

        public BookingConfirmationScreen(
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

                page.addBlock(
                                createConfirmation(),
                                0);

                add(
                                page.inScrollPane(),
                                BorderLayout.CENTER);
        }

        private JPanel createConfirmation() {

                JPanel wrapper = new JPanel(
                                new GridBagLayout());

                wrapper.setOpaque(false);

                AppCard card = new AppCard();

                card.setLayout(
                                new BoxLayout(
                                                card,
                                                BoxLayout.Y_AXIS));

                JLabel check = new JLabel("✓");

                check.setFont(
                                new java.awt.Font(
                                                "Segoe UI",
                                                java.awt.Font.BOLD,
                                                48));

                check.setForeground(
                                BussinTheme.SUCCESS);

                check.setAlignmentX(
                                CENTER_ALIGNMENT);

                JLabel title = AppLabel.title(
                                "Booking Confirmed");

                title.setAlignmentX(
                                CENTER_ALIGNMENT);

                JLabel subtitle = AppLabel.secondary(
                                "Your BUSSIN booking has been created successfully.");

                subtitle.setAlignmentX(
                                CENTER_ALIGNMENT);

                card.add(check);
                card.add(Box.createVerticalStrut(8));
                card.add(title);
                card.add(Box.createVerticalStrut(5));
                card.add(subtitle);
                card.add(Box.createVerticalStrut(24));

                JLabel bookingId = new JLabel(
                                flowState.getBookingId());

                bookingId.setFont(
                                new java.awt.Font(
                                                "Segoe UI",
                                                java.awt.Font.BOLD,
                                                30));

                bookingId.setForeground(
                                BussinTheme.PRIMARY);

                bookingId.setAlignmentX(
                                CENTER_ALIGNMENT);

                card.add(bookingId);

                card.add(
                                Box.createVerticalStrut(20));

                CommuterTrip trip = flowState.getSelectedTrip();

                if (trip != null) {

                        JLabel route = new JLabel(
                                        trip.getRoute());

                        route.setFont(
                                        BussinTheme.BODY_MEDIUM);

                        route.setForeground(
                                        BussinTheme.TEXT_PRIMARY);

                        route.setAlignmentX(
                                        CENTER_ALIGNMENT);

                        card.add(route);

                        card.add(
                                        Box.createVerticalStrut(5));

                        JLabel details = new JLabel(
                                        trip.getDeparture()
                                                        + "  •  Seat "
                                                        + flowState.getSelectedSeat()
                                                        + "  •  ₱"
                                                        + String.format(
                                                                        "%,.2f",
                                                                        trip.getFare()));

                        details.setFont(
                                        BussinTheme.BODY);

                        details.setForeground(
                                        BussinTheme.TEXT_SECONDARY);

                        details.setAlignmentX(
                                        CENTER_ALIGNMENT);

                        card.add(details);
                }

                card.add(
                                Box.createVerticalStrut(24));

                JLabel status = new JLabel(
                                "Status: Pending");

                status.setFont(
                                BussinTheme.SMALL_BOLD);

                status.setForeground(
                                BussinTheme.WARNING);

                status.setAlignmentX(
                                CENTER_ALIGNMENT);

                card.add(status);

                card.add(
                                Box.createVerticalStrut(24));

                JPanel actions = new JPanel(
                                new FlowLayout(
                                                FlowLayout.CENTER,
                                                8,
                                                0));

                actions.setOpaque(false);

                AppButton bookings = new AppButton(
                                "My Bookings");

                bookings.addActionListener(
                                event -> navigationHandler.accept(
                                                "bookings"));

                AppButton home = new AppButton(
                                "Back to Home",
                                AppButton.Variant.SECONDARY);

                home.addActionListener(
                                event -> {
                                        flowState.reset();
                                        navigationHandler.accept(
                                                        "dashboard");
                                });

                actions.add(bookings);
                actions.add(home);

                card.add(actions);

                wrapper.add(card);

                return wrapper;
        }
}