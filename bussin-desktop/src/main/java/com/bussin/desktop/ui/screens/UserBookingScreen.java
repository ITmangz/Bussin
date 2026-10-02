package com.bussin.desktop.ui.screens;

import com.bussin.desktop.ui.components.AppButton;
import com.bussin.desktop.ui.components.AppCard;
import com.bussin.desktop.ui.components.AppLabel;
import com.bussin.desktop.ui.components.PageContent;
import com.bussin.desktop.ui.flow.BookingFlowState;
import com.bussin.desktop.ui.theme.BussinTheme;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class UserBookingScreen extends JPanel {

        private final Consumer<String> navigationHandler;
        private final BookingFlowState flowState;

        public UserBookingScreen(
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
                                createBookingOptions(),
                                24);

                add(
                                page.inScrollPane(),
                                BorderLayout.CENTER);
        }

        private JPanel createHeader() {

                JPanel header = new JPanel();

                header.setOpaque(false);

                header.setLayout(
                                new BoxLayout(
                                                header,
                                                BoxLayout.Y_AXIS));

                header.add(
                                AppLabel.title(
                                                "Book a Trip"));

                header.add(
                                Box.createVerticalStrut(6));

                header.add(
                                AppLabel.secondary(
                                                "Choose how you want to book your next BUSSIN trip."));

                return header;
        }

        private JPanel createBookingOptions() {

                JPanel container = new JPanel(
                                new GridLayout(
                                                1,
                                                2,
                                                16,
                                                0));

                container.setOpaque(false);

                container.add(
                                createNormalBookingCard());

                container.add(
                                createAiBookingCard());

                return container;
        }

        private JPanel createNormalBookingCard() {

                AppCard card = new AppCard();

                card.setLayout(
                                new BoxLayout(
                                                card,
                                                BoxLayout.Y_AXIS));

                card.setPreferredSize(
                                new Dimension(
                                                0,
                                                220));

                card.add(
                                AppLabel.section(
                                                "Find & Book a Trip"));

                card.add(
                                Box.createVerticalStrut(8));

                card.add(
                                AppLabel.secondary(
                                                "Search available trips, select your seat, provide passenger information, and confirm your booking."));

                card.add(
                                Box.createVerticalGlue());

                AppButton button = new AppButton(
                                "Find a Trip");

                button.setAlignmentX(
                                CENTER_ALIGNMENT);

                button.addActionListener(
                                event -> {

                                        flowState.reset();

                                        navigationHandler.accept(
                                                        "trip-search");
                                });

                card.add(button);

                return card;
        }

        private JPanel createAiBookingCard() {

                AppCard card = new AppCard();

                card.setLayout(
                                new BoxLayout(
                                                card,
                                                BoxLayout.Y_AXIS));

                card.setPreferredSize(
                                new Dimension(
                                                0,
                                                220));

                card.add(
                                AppLabel.section(
                                                "AI Chat Booking"));

                card.add(
                                Box.createVerticalStrut(8));

                card.add(
                                AppLabel.secondary(
                                                "Book a trip through a conversational AI assistant."));

                card.add(
                                Box.createVerticalGlue());

                AppButton button = new AppButton(
                                "Book with AI");

                button.setAlignmentX(
                                CENTER_ALIGNMENT);

                button.addActionListener(
                                event -> {

                                        flowState.reset();

                                        navigationHandler.accept(
                                                        "ai-booking");
                                });

                card.add(button);

                return card;
        }
}