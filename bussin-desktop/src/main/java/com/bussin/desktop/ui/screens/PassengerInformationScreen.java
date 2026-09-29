package com.bussin.desktop.ui.screens;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.bussin.desktop.ui.components.AppButton;
import com.bussin.desktop.ui.components.AppCard;
import com.bussin.desktop.ui.components.AppLabel;
import com.bussin.desktop.ui.components.PageContent;
import com.bussin.desktop.ui.flow.BookingFlowState;
import com.bussin.desktop.ui.flow.CommuterTrip;
import com.bussin.desktop.ui.theme.BussinTheme;

public class PassengerInformationScreen extends JPanel {

    private final Consumer<String> navigationHandler;
    private final BookingFlowState flowState;
    private final String currentUserEmail;

    private final JTextField nameField = new JTextField();
    private final JTextField phoneField = new JTextField();
    private final JTextField emailField = new JTextField();

    public PassengerInformationScreen(
            String currentUserEmail,
            Consumer<String> navigationHandler,
            BookingFlowState flowState) {

        this.currentUserEmail = currentUserEmail == null
                ? ""
                : currentUserEmail.trim();

        this.navigationHandler = navigationHandler;
        this.flowState = flowState;

        initializeDefaults();
        initializeUI();
    }

    private void initializeDefaults() {

        nameField.setText(
                getPassengerName());

        phoneField.setText(
                flowState.getPassengerPhone() == null
                        ? ""
                        : flowState.getPassengerPhone());

        emailField.setText(
                flowState.getPassengerEmail() == null
                        ? currentUserEmail
                        : flowState.getPassengerEmail());

        emailField.setEditable(false);
        emailField.setBackground(
                BussinTheme.SURFACE_ALT);
    }

    private void initializeUI() {

        setBackground(BussinTheme.BACKGROUND);
        setLayout(new BorderLayout());

        PageContent page = new PageContent();

        page.addBlock(createHeader(), 0);
        page.addBlock(createTripSummary(), 20);
        page.addBlock(createForm(), 18);
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
                        "Passenger Information"));

        panel.add(
                Box.createVerticalStrut(5));

        panel.add(
                AppLabel.secondary(
                        "Enter the information that will be attached to this booking."));

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

        JLabel label = new JLabel(
                trip.getRoute()
                        + "  •  "
                        + trip.getDeparture()
                        + "  •  Seat "
                        + flowState.getSelectedSeat());

        label.setFont(
                BussinTheme.BODY_MEDIUM);

        label.setForeground(
                BussinTheme.TEXT_PRIMARY);

        card.add(
                label,
                BorderLayout.CENTER);

        return card;
    }

    private JPanel createForm() {

        AppCard card = new AppCard();

        card.setLayout(
                new BoxLayout(
                        card,
                        BoxLayout.Y_AXIS));

        card.add(
                AppLabel.section(
                        "Passenger Details"));

        card.add(
                Box.createVerticalStrut(18));

        card.add(
                createField(
                        "FULL NAME",
                        nameField));

        card.add(
                Box.createVerticalStrut(16));

        card.add(
                createField(
                        "PHONE NUMBER",
                        phoneField));

        card.add(
                Box.createVerticalStrut(16));

        card.add(
                createField(
                        "EMAIL",
                        emailField));

        return card;
    }

    private JPanel createField(
            String labelText,
            JTextField field) {

        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(
                new BoxLayout(
                        panel,
                        BoxLayout.Y_AXIS));

        JLabel label = new JLabel(labelText);

        label.setFont(
                BussinTheme.SMALL_BOLD);

        label.setForeground(
                BussinTheme.TEXT_SECONDARY);

        field.setPreferredSize(
                new Dimension(
                        0,
                        40));

        field.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        40));

        field.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BussinTheme.BORDER),
                        BorderFactory.createEmptyBorder(
                                0,
                                10,
                                0,
                                10)));

        panel.add(label);
        panel.add(Box.createVerticalStrut(6));
        panel.add(field);

        return panel;
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
                        "seat-selection"));

        AppButton continueButton = new AppButton(
                "Continue");

        continueButton.addActionListener(
                event -> continueToReview());

        panel.add(back);
        panel.add(continueButton);

        return panel;
    }

    private void continueToReview() {

        String name = nameField.getText().trim();

        String phone = phoneField.getText().trim();

        if (name.isBlank()
                || phone.isBlank()) {

            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "Please complete your name and phone number.",
                    "Incomplete Information",
                    javax.swing.JOptionPane.WARNING_MESSAGE);

            return;
        }

        String digits = phone.replaceAll(
                "[^0-9+]",
                "");

        if (digits.length() < 7) {

            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid phone number.",
                    "Invalid Phone Number",
                    javax.swing.JOptionPane.WARNING_MESSAGE);

            return;
        }

        flowState.setPassengerName(name);
        flowState.setPassengerPhone(phone);
        flowState.setPassengerEmail(currentUserEmail);

        navigationHandler.accept(
                "booking-review");
    }

    private String getPassengerName() {

        if (flowState.getPassengerName() != null
                && !flowState.getPassengerName().isBlank()) {

            return flowState.getPassengerName();
        }

        if (currentUserEmail.equalsIgnoreCase(
                "user@bussin.com")) {

            return "User";
        }

        int at = currentUserEmail.indexOf('@');

        if (at > 0) {

            String local = currentUserEmail.substring(
                    0,
                    at);

            String[] parts = local.split("[._-]+");

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

            return result.toString();
        }

        return "";
    }
}