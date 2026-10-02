package com.bussin.desktop.ui.auth;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.bussin.desktop.services.FirebaseAuthService;
import com.bussin.desktop.services.UserApiService;
import com.bussin.desktop.ui.components.AppButton;
import com.bussin.desktop.ui.components.AppLabel;
import com.bussin.desktop.ui.theme.BussinTheme;

/**
 * BUSSIN Registration - Step 2
 *
 * Collects the user's personal information after the account
 * information has already been entered in Step 1.
 */
public class RegistrationStep2Screen extends JPanel {

        private final java.util.function.Consumer<String> navigationHandler;

        private final RegistrationData registrationData;

        private JTextField firstNameField;
        private JTextField middleNameField;
        private JTextField lastNameField;
        private JTextField ageField;
        private JTextField dateOfBirthField;
        private JTextField contactNumberField;

        private JComboBox<String> genderComboBox;

        public RegistrationStep2Screen(
                        java.util.function.Consumer<String> navigationHandler,
                        RegistrationData registrationData) {

                this.navigationHandler = navigationHandler;
                this.registrationData = registrationData;

                setLayout(new BorderLayout());
                setBackground(BussinTheme.BACKGROUND);

                buildUI();
        }

        private void buildUI() {

                AuthBackground authBackground = new AuthBackground();

                JPanel content = createRegistrationContent();

                authBackground.setAuthContent(content);

                setLayout(new BorderLayout());
                add(authBackground, BorderLayout.CENTER);
        }

        private JPanel createRegistrationContent() {

                JPanel wrapper = new JPanel(new GridBagLayout());

                wrapper.setBackground(
                                BussinTheme.BACKGROUND);

                wrapper.setBorder(
                                new EmptyBorder(
                                                28,
                                                36,
                                                28,
                                                36));

                JPanel card = createCard();

                GridBagConstraints gbc = new GridBagConstraints();

                gbc.gridx = 0;
                gbc.gridy = 0;

                gbc.weightx = 1.0;
                gbc.weighty = 1.0;

                gbc.fill = GridBagConstraints.BOTH;

                wrapper.add(card, gbc);

                return wrapper;
        }

        private JPanel createCard() {

                JPanel card = new JPanel(new BorderLayout());

                card.setBackground(
                                BussinTheme.SURFACE);

                card.setBorder(
                                BorderFactory.createCompoundBorder(
                                                BorderFactory.createLineBorder(
                                                                BussinTheme.BORDER,
                                                                1),
                                                new EmptyBorder(
                                                                30,
                                                                34,
                                                                26,
                                                                34)));

                JPanel header = createHeader();

                card.add(
                                header,
                                BorderLayout.NORTH);

                JPanel form = createForm();

                JScrollPane scrollPane = new JScrollPane(form);

                scrollPane.setBorder(null);

                scrollPane.setBackground(
                                BussinTheme.SURFACE);

                scrollPane.getViewport()
                                .setBackground(
                                                BussinTheme.SURFACE);

                scrollPane.setHorizontalScrollBarPolicy(
                                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

                scrollPane.getVerticalScrollBar()
                                .setUnitIncrement(14);

                card.add(
                                scrollPane,
                                BorderLayout.CENTER);

                JPanel footer = createFooter();

                card.add(
                                footer,
                                BorderLayout.SOUTH);

                return card;
        }

        private JPanel createHeader() {

                JPanel header = new JPanel();

                header.setLayout(
                                new BoxLayout(
                                                header,
                                                BoxLayout.Y_AXIS));

                header.setOpaque(false);

                AppLabel brand = new AppLabel(
                                "BUSSIN");

                brand.setFont(
                                new Font(
                                                "Segoe UI",
                                                Font.BOLD,
                                                13));

                brand.setForeground(
                                BussinTheme.PRIMARY);

                header.add(brand);

                header.add(
                                Box.createVerticalStrut(7));

                JLabel title = new JLabel(
                                "Tell us about yourself");

                title.setFont(
                                new Font(
                                                "Segoe UI",
                                                Font.BOLD,
                                                28));

                title.setForeground(
                                BussinTheme.TEXT_PRIMARY);

                header.add(title);

                header.add(
                                Box.createVerticalStrut(6));

                JLabel subtitle = new JLabel(
                                "Complete your personal information to finish creating your account.");

                subtitle.setFont(
                                new Font(
                                                "Segoe UI",
                                                Font.PLAIN,
                                                13));

                subtitle.setForeground(
                                BussinTheme.TEXT_SECONDARY);

                header.add(subtitle);

                header.add(
                                Box.createVerticalStrut(20));

                header.add(
                                createStepIndicator());

                header.add(
                                Box.createVerticalStrut(24));

                return header;
        }

        private JPanel createStepIndicator() {

                JPanel panel = new JPanel(
                                new GridLayout(
                                                1,
                                                2,
                                                10,
                                                0));

                panel.setOpaque(false);

                JPanel accountStep = createStep(
                                "01",
                                "Account",
                                false);

                JPanel personalStep = createStep(
                                "02",
                                "Personal",
                                true);

                panel.add(accountStep);
                panel.add(personalStep);

                return panel;
        }

        private JPanel createStep(
                        String number,
                        String label,
                        boolean active) {

                JPanel panel = new JPanel(
                                new BorderLayout(
                                                10,
                                                0));

                panel.setOpaque(false);

                JLabel numberLabel = new JLabel(number);

                numberLabel.setHorizontalAlignment(
                                SwingConstants.CENTER);

                numberLabel.setPreferredSize(
                                new Dimension(
                                                34,
                                                34));

                numberLabel.setFont(
                                new Font(
                                                "Segoe UI",
                                                Font.BOLD,
                                                12));

                numberLabel.setForeground(
                                active
                                                ? BussinTheme.TEXT_ON_PRIMARY
                                                : BussinTheme.TEXT_MUTED);

                numberLabel.setOpaque(true);

                numberLabel.setBackground(
                                active
                                                ? BussinTheme.PRIMARY
                                                : BussinTheme.COOL_GRAY);

                panel.add(
                                numberLabel,
                                BorderLayout.WEST);

                JLabel labelLabel = new JLabel(label);

                labelLabel.setFont(
                                new Font(
                                                "Segoe UI",
                                                active
                                                                ? Font.BOLD
                                                                : Font.PLAIN,
                                                12));

                labelLabel.setForeground(
                                active
                                                ? BussinTheme.TEXT_PRIMARY
                                                : BussinTheme.TEXT_MUTED);

                panel.add(
                                labelLabel,
                                BorderLayout.CENTER);

                return panel;
        }

        private JPanel createForm() {

                JPanel container = new JPanel(
                                new BorderLayout());

                container.setOpaque(false);

                JPanel form = new JPanel(
                                new GridBagLayout());

                form.setOpaque(false);

                GridBagConstraints gbc = new GridBagConstraints();

                gbc.gridx = 0;
                gbc.gridy = 0;

                gbc.weightx = 1.0;

                gbc.fill = GridBagConstraints.HORIZONTAL;

                gbc.insets = new Insets(
                                0,
                                0,
                                14,
                                0);

                firstNameField = new JTextField();

                addField(
                                form,
                                gbc,
                                "First name",
                                firstNameField,
                                true);

                middleNameField = new JTextField();

                addField(
                                form,
                                gbc,
                                "Middle name",
                                middleNameField,
                                false);

                lastNameField = new JTextField();

                addField(
                                form,
                                gbc,
                                "Last name",
                                lastNameField,
                                true);

                genderComboBox = new JComboBox<>(
                                new String[] {
                                                "Select gender",
                                                "Male",
                                                "Female",
                                                "Other",
                                                "Prefer not to say"
                                });

                genderComboBox.setFont(
                                new Font(
                                                "Segoe UI",
                                                Font.PLAIN,
                                                13));

                genderComboBox.setPreferredSize(
                                new Dimension(
                                                100,
                                                42));

                genderComboBox.setBackground(
                                BussinTheme.SURFACE);

                genderComboBox.setBorder(
                                BorderFactory.createLineBorder(
                                                BussinTheme.BORDER_STRONG));

                addField(
                                form,
                                gbc,
                                "Gender",
                                genderComboBox,
                                true);

                ageField = new JTextField();

                addField(
                                form,
                                gbc,
                                "Age",
                                ageField,
                                true);

                dateOfBirthField = new JTextField();

                dateOfBirthField.setToolTipText(
                                "Format: YYYY-MM-DD");

                addField(
                                form,
                                gbc,
                                "Date of birth",
                                dateOfBirthField,
                                true);

                contactNumberField = new JTextField();

                addField(
                                form,
                                gbc,
                                "Contact number",
                                contactNumberField,
                                true);

                gbc.gridy++;

                gbc.weighty = 1.0;

                gbc.fill = GridBagConstraints.BOTH;

                form.add(
                                Box.createVerticalGlue(),
                                gbc);

                container.add(
                                form,
                                BorderLayout.NORTH);

                return container;
        }

        private void addField(
                        JPanel form,
                        GridBagConstraints gbc,
                        String label,
                        javax.swing.JComponent field,
                        boolean required) {

                JPanel fieldPanel = new JPanel();

                fieldPanel.setLayout(
                                new BoxLayout(
                                                fieldPanel,
                                                BoxLayout.Y_AXIS));

                fieldPanel.setOpaque(false);

                JLabel fieldLabel = new JLabel(
                                required
                                                ? label + " *"
                                                : label);

                fieldLabel.setFont(
                                new Font(
                                                "Segoe UI",
                                                Font.BOLD,
                                                12));

                fieldLabel.setForeground(
                                BussinTheme.TEXT_PRIMARY);

                fieldPanel.add(fieldLabel);

                fieldPanel.add(
                                Box.createVerticalStrut(6));

                if (field instanceof JTextField textField) {

                        styleTextField(textField);
                }

                fieldPanel.add(field);

                form.add(
                                fieldPanel,
                                gbc);

                gbc.gridy++;
        }

        private void styleTextField(
                        JTextField field) {

                field.setFont(
                                new Font(
                                                "Segoe UI",
                                                Font.PLAIN,
                                                13));

                field.setForeground(
                                BussinTheme.TEXT_PRIMARY);

                field.setBackground(
                                BussinTheme.SURFACE);

                field.setCaretColor(
                                BussinTheme.PRIMARY);

                field.setBorder(
                                BorderFactory.createCompoundBorder(
                                                BorderFactory.createLineBorder(
                                                                BussinTheme.BORDER_STRONG),
                                                new EmptyBorder(
                                                                10,
                                                                12,
                                                                10,
                                                                12)));

                field.setPreferredSize(
                                new Dimension(
                                                100,
                                                42));

                field.setMaximumSize(
                                new Dimension(
                                                Integer.MAX_VALUE,
                                                42));

                field.addFocusListener(
                                new FocusAdapter() {

                                        @Override
                                        public void focusGained(
                                                        FocusEvent event) {

                                                field.setBorder(
                                                                BorderFactory.createCompoundBorder(
                                                                                BorderFactory.createLineBorder(
                                                                                                BussinTheme.PRIMARY,
                                                                                                1),
                                                                                new EmptyBorder(
                                                                                                10,
                                                                                                12,
                                                                                                10,
                                                                                                12)));
                                        }

                                        @Override
                                        public void focusLost(
                                                        FocusEvent event) {

                                                field.setBorder(
                                                                BorderFactory.createCompoundBorder(
                                                                                BorderFactory.createLineBorder(
                                                                                                BussinTheme.BORDER_STRONG,
                                                                                                1),
                                                                                new EmptyBorder(
                                                                                                10,
                                                                                                12,
                                                                                                10,
                                                                                                12)));
                                        }
                                });
        }

        private JPanel createFooter() {

                JPanel footer = new JPanel();

                footer.setLayout(
                                new BoxLayout(
                                                footer,
                                                BoxLayout.Y_AXIS));

                footer.setOpaque(false);

                footer.add(
                                Box.createVerticalStrut(18));

                AppButton createAccountButton = new AppButton(
                                "Create Account");

                createAccountButton.setAlignmentX(
                                CENTER_ALIGNMENT);

                createAccountButton.setPreferredSize(
                                new Dimension(
                                                200,
                                                44));

                createAccountButton.setMaximumSize(
                                new Dimension(
                                                Integer.MAX_VALUE,
                                                44));

                createAccountButton.setBackground(
                                BussinTheme.PRIMARY);

                createAccountButton.setForeground(
                                BussinTheme.TEXT_ON_PRIMARY);

                createAccountButton.setCursor(
                                Cursor.getPredefinedCursor(
                                                Cursor.HAND_CURSOR));

                createAccountButton.addActionListener(
                                event -> createAccount());

                footer.add(
                                createAccountButton);

                footer.add(
                                Box.createVerticalStrut(14));

                JPanel loginPanel = new JPanel(
                                new FlowLayout(
                                                FlowLayout.CENTER,
                                                4,
                                                0));

                loginPanel.setOpaque(false);

                JLabel text = new JLabel(
                                "Already have an account?");

                text.setFont(
                                new Font(
                                                "Segoe UI",
                                                Font.PLAIN,
                                                12));

                text.setForeground(
                                BussinTheme.TEXT_SECONDARY);

                loginPanel.add(text);

                JButton loginButton = new JButton("Sign in");

                loginButton.setFont(
                                new Font(
                                                "Segoe UI",
                                                Font.BOLD,
                                                12));

                loginButton.setForeground(
                                BussinTheme.PRIMARY);

                loginButton.setBorderPainted(false);
                loginButton.setContentAreaFilled(false);
                loginButton.setFocusPainted(false);

                loginButton.setCursor(
                                Cursor.getPredefinedCursor(
                                                Cursor.HAND_CURSOR));

                loginButton.addActionListener(
                                event -> navigationHandler.accept(
                                                "login"));

                loginPanel.add(loginButton);

                footer.add(loginPanel);

                return footer;
        }

        /**
         * Validates the form and creates the account.
         */
        private void createAccount() {

                String firstName = firstNameField
                                .getText()
                                .trim();

                String middleName = middleNameField
                                .getText()
                                .trim();

                String lastName = lastNameField
                                .getText()
                                .trim();

                String age = ageField
                                .getText()
                                .trim();

                String dateOfBirth = dateOfBirthField
                                .getText()
                                .trim();

                String contact = contactNumberField
                                .getText()
                                .trim();

                String gender = String.valueOf(
                                genderComboBox
                                                .getSelectedItem());

                // --------------------------------------------------------
                // Validate required fields
                // --------------------------------------------------------

                if (firstName.isBlank()
                                || lastName.isBlank()
                                || age.isBlank()
                                || dateOfBirth.isBlank()
                                || contact.isBlank()
                                || "Select gender".equals(gender)) {

                        JOptionPane.showMessageDialog(
                                        this,
                                        "Please complete all required fields.",
                                        "Registration",
                                        JOptionPane.WARNING_MESSAGE);

                        return;
                }

                // --------------------------------------------------------
                // Validate age
                // --------------------------------------------------------

                int parsedAge;

                try {

                        parsedAge = Integer.parseInt(age);

                        if (parsedAge <= 0
                                        || parsedAge > 120) {

                                JOptionPane.showMessageDialog(
                                                this,
                                                "Please enter a valid age.",
                                                "Registration",
                                                JOptionPane.WARNING_MESSAGE);

                                return;
                        }

                } catch (NumberFormatException exception) {

                        JOptionPane.showMessageDialog(
                                        this,
                                        "Age must be a valid number.",
                                        "Registration",
                                        JOptionPane.WARNING_MESSAGE);

                        return;
                }

                // --------------------------------------------------------
                // Validate date of birth
                // --------------------------------------------------------

                LocalDate parsedDateOfBirth;

                try {

                        parsedDateOfBirth = LocalDate.parse(dateOfBirth);

                } catch (DateTimeParseException exception) {

                        JOptionPane.showMessageDialog(
                                        this,
                                        "Date of birth must use the format YYYY-MM-DD.",
                                        "Registration",
                                        JOptionPane.WARNING_MESSAGE);

                        return;
                }

                // --------------------------------------------------------
                // Save personal information into RegistrationData
                // --------------------------------------------------------

                registrationData.setFirstName(
                                firstName);

                registrationData.setMiddleName(
                                middleName);

                registrationData.setLastName(
                                lastName);

                registrationData.setGender(
                                gender);

                registrationData.setAge(
                                age);

                registrationData.setDateOfBirth(
                                dateOfBirth);

                registrationData.setContactNumber(
                                contact);

                // --------------------------------------------------------
                // Create account
                // --------------------------------------------------------

                try {

                        // ------------------------------------------------
                        // 1. Create Firebase account
                        // ------------------------------------------------

                        boolean registered = FirebaseAuthService.register(
                                        registrationData.getEmail(),
                                        registrationData.getPassword());

                        if (!registered) {

                                JOptionPane.showMessageDialog(
                                                this,
                                                "Unable to create your Firebase account.",
                                                "Registration Failed",
                                                JOptionPane.ERROR_MESSAGE);

                                return;
                        }

                        // ------------------------------------------------
                        // 2. Create BUSSIN user profile
                        // ------------------------------------------------

                        UserApiService.createCurrentUser(
                                        registrationData.getFirstName(),
                                        registrationData.getMiddleName(),
                                        registrationData.getLastName(),
                                        registrationData.getGender(),
                                        parsedAge,
                                        parsedDateOfBirth,
                                        registrationData.getContactNumber());

                        // ------------------------------------------------
                        // 3. Clear authenticated session
                        // ------------------------------------------------

                        FirebaseAuthService.logout();

                        // ------------------------------------------------
                        // 4. Registration complete
                        // ------------------------------------------------

                        JOptionPane.showMessageDialog(
                                        this,
                                        "Account created successfully.",
                                        "Registration Complete",
                                        JOptionPane.INFORMATION_MESSAGE);

                        navigationHandler.accept(
                                        "login");

                } catch (InterruptedException exception) {

                        Thread.currentThread().interrupt();

                        FirebaseAuthService.logout();

                        JOptionPane.showMessageDialog(
                                        this,
                                        "The registration request was interrupted.",
                                        "Registration Failed",
                                        JOptionPane.ERROR_MESSAGE);

                } catch (Exception exception) {

                        FirebaseAuthService.logout();

                        JOptionPane.showMessageDialog(
                                        this,
                                        "Unable to complete registration.\n\n"
                                                        + exception.getMessage(),
                                        "Registration Failed",
                                        JOptionPane.ERROR_MESSAGE);
                }
        }
}