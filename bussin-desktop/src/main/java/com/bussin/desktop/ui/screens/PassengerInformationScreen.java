package com.bussin.desktop.ui.screens;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

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

        private AppButton continueButton;

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
                attachValidationListeners();
                updateContinueState();
        }

        // ---------------------------------------------------------------------
        // Defaults
        // ---------------------------------------------------------------------

        private void initializeDefaults() {

                nameField.setText(
                                getPassengerName());

                phoneField.setText(
                                flowState.getPassengerPhone() == null
                                                ? ""
                                                : flowState.getPassengerPhone());

                /*
                 * The passenger email always belongs to the authenticated
                 * commuter. Do not allow the booking flow to change ownership.
                 */
                emailField.setText(currentUserEmail);
                emailField.setEditable(false);
                emailField.setBackground(
                                BussinTheme.SURFACE_ALT);
        }

        // ---------------------------------------------------------------------
        // UI
        // ---------------------------------------------------------------------

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
                                createForm(),
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

                        JLabel label = new JLabel(
                                        "No trip selected.");

                        label.setFont(
                                        BussinTheme.BODY_MEDIUM);

                        label.setForeground(
                                        BussinTheme.DANGER);

                        card.add(
                                        label,
                                        BorderLayout.CENTER);

                        return card;
                }

                String seat = flowState.getSelectedSeat();

                String summary = trip.getRoute()
                                + "  •  "
                                + trip.getDeparture()
                                + "  •  Seat "
                                + (seat == null || seat.isBlank()
                                                ? "Not selected"
                                                : seat);

                JLabel label = new JLabel(summary);

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

                panel.add(
                                Box.createVerticalStrut(6));

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
                                event -> goBack());

                continueButton = new AppButton(
                                "Continue");

                continueButton.addActionListener(
                                event -> continueToReview());

                panel.add(back);
                panel.add(continueButton);

                return panel;
        }

        // ---------------------------------------------------------------------
        // Validation
        // ---------------------------------------------------------------------

        private void attachValidationListeners() {

                DocumentListener listener = new DocumentListener() {

                        @Override
                        public void insertUpdate(
                                        DocumentEvent event) {

                                updateContinueState();
                        }

                        @Override
                        public void removeUpdate(
                                        DocumentEvent event) {

                                updateContinueState();
                        }

                        @Override
                        public void changedUpdate(
                                        DocumentEvent event) {

                                updateContinueState();
                        }
                };

                nameField
                                .getDocument()
                                .addDocumentListener(listener);

                phoneField
                                .getDocument()
                                .addDocumentListener(listener);
        }

        private void updateContinueState() {

                if (continueButton == null) {
                        return;
                }

                boolean valid = hasRequiredFields()
                                && hasValidPhone();

                continueButton.setEnabled(valid);
        }

        private boolean hasRequiredFields() {

                return !nameField
                                .getText()
                                .trim()
                                .isBlank()

                                && !phoneField
                                                .getText()
                                                .trim()
                                                .isBlank()

                                && !currentUserEmail.isBlank();
        }

        private boolean hasValidPhone() {

                String phone = phoneField
                                .getText()
                                .trim();

                if (phone.isBlank()) {
                        return false;
                }

                String digits = phone.replaceAll(
                                "[^0-9]",
                                "");

                /*
                 * Accept common Philippine formats such as:
                 *
                 * 09171234567
                 * +639171234567
                 * 639171234567
                 *
                 * Also allow other international/local numbers with
                 * at least 7 digits.
                 */
                return digits.length() >= 7
                                && digits.length() <= 15;
        }

        // ---------------------------------------------------------------------
        // Navigation
        // ---------------------------------------------------------------------

        private void goBack() {

                navigationHandler.accept(
                                "seat-selection");
        }

        private void continueToReview() {

                CommuterTrip trip = flowState.getSelectedTrip();

                if (trip == null) {

                        JOptionPane.showMessageDialog(
                                        this,
                                        "Please select a trip before entering passenger information.",
                                        "No Trip Selected",
                                        JOptionPane.WARNING_MESSAGE);

                        navigationHandler.accept(
                                        "trip-search");

                        return;
                }

                String selectedSeat = flowState.getSelectedSeat();

                if (selectedSeat == null
                                || selectedSeat.isBlank()) {

                        JOptionPane.showMessageDialog(
                                        this,
                                        "Please select a bus seat before continuing.",
                                        "No Seat Selected",
                                        JOptionPane.WARNING_MESSAGE);

                        navigationHandler.accept(
                                        "seat-selection");

                        return;
                }

                String name = nameField
                                .getText()
                                .trim();

                String phone = phoneField
                                .getText()
                                .trim();

                if (name.isBlank()
                                || phone.isBlank()) {

                        JOptionPane.showMessageDialog(
                                        this,
                                        "Please complete your name and phone number.",
                                        "Incomplete Information",
                                        JOptionPane.WARNING_MESSAGE);

                        updateContinueState();

                        return;
                }

                if (!hasValidPhone()) {

                        JOptionPane.showMessageDialog(
                                        this,
                                        "Please enter a valid phone number.",
                                        "Invalid Phone Number",
                                        JOptionPane.WARNING_MESSAGE);

                        phoneField.requestFocusInWindow();

                        return;
                }

                /*
                 * Store the passenger information in the shared booking flow.
                 */
                flowState.setPassengerName(
                                name);

                flowState.setPassengerPhone(
                                phone);

                /*
                 * Always use the authenticated user's email.
                 * This keeps booking ownership tied to the logged-in account.
                 */
                flowState.setPassengerEmail(
                                currentUserEmail);

                navigationHandler.accept(
                                "booking-review");
        }

        // ---------------------------------------------------------------------
        // Passenger Defaults
        // ---------------------------------------------------------------------

        private String getPassengerName() {

                if (flowState.getPassengerName() != null
                                && !flowState
                                                .getPassengerName()
                                                .isBlank()) {

                        return flowState
                                        .getPassengerName();
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

                        String[] parts = local.split(
                                        "[._-]+");

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