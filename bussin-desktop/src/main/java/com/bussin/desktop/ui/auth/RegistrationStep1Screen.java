package com.bussin.desktop.ui.auth;

import com.bussin.desktop.ui.theme.BussinTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.Path2D;

public class RegistrationStep1Screen extends JPanel {

        private static final String FONT = "Segoe UI";

        private final java.util.function.Consumer<String> navigationHandler;
        private final RegistrationData registrationData;

        private JTextField emailField;
        private JPasswordField passwordField;
        private JPasswordField confirmPasswordField;

        public RegistrationStep1Screen(
                        java.util.function.Consumer<String> navigationHandler) {

                this.navigationHandler = navigationHandler;
                this.registrationData = new RegistrationData();

                setLayout(new BorderLayout());
                setBackground(Color.WHITE);

                AuthBackground background = new AuthBackground();

                background.setAuthContent(
                                createRegistrationContent());

                add(
                                background,
                                BorderLayout.CENTER);
        }

        // =============================================================
        // MAIN CONTENT
        // =============================================================

        private JPanel createRegistrationContent() {

                JPanel page = new JPanel(
                                new GridBagLayout());

                page.setBackground(
                                BussinTheme.COOL_GRAY);

                RoundedAuthCard card = new RoundedAuthCard();

                card.setLayout(
                                new BoxLayout(
                                                card,
                                                BoxLayout.Y_AXIS));

                card.setBorder(
                                new EmptyBorder(
                                                30,
                                                38,
                                                28,
                                                38));

                card.setPreferredSize(
                                new Dimension(
                                                430,
                                                610));

                // ---------------------------------------------------------
                // Brand
                // ---------------------------------------------------------

                JLabel brand = new JLabel("BUSSIN");

                brand.setFont(
                                new Font(
                                                FONT,
                                                Font.BOLD,
                                                18));

                brand.setForeground(
                                BussinTheme.RED);

                brand.setAlignmentX(
                                Component.LEFT_ALIGNMENT);

                card.add(brand);

                card.add(
                                Box.createVerticalStrut(20));

                // ---------------------------------------------------------
                // Title
                // ---------------------------------------------------------

                JLabel title = new JLabel(
                                "Create your account");

                title.setFont(
                                new Font(
                                                FONT,
                                                Font.BOLD,
                                                27));

                title.setForeground(
                                BussinTheme.TEXT_PRIMARY);

                title.setAlignmentX(
                                Component.LEFT_ALIGNMENT);

                card.add(title);

                card.add(
                                Box.createVerticalStrut(6));

                JLabel subtitle = new JLabel(
                                "Set up your login credentials.");

                subtitle.setFont(
                                new Font(
                                                FONT,
                                                Font.PLAIN,
                                                13));

                subtitle.setForeground(
                                BussinTheme.TEXT_SECONDARY);

                subtitle.setAlignmentX(
                                Component.LEFT_ALIGNMENT);

                card.add(subtitle);

                card.add(
                                Box.createVerticalStrut(20));

                // ---------------------------------------------------------
                // Step indicator
                // ---------------------------------------------------------

                card.add(
                                createStepIndicator());

                card.add(
                                Box.createVerticalStrut(22));

                // ---------------------------------------------------------
                // Email
                // ---------------------------------------------------------

                card.add(
                                createLabel("Email address"));

                card.add(
                                Box.createVerticalStrut(6));

                emailField = new JTextField();

                card.add(
                                createTextField(
                                                emailField));

                card.add(
                                Box.createVerticalStrut(15));

                // ---------------------------------------------------------
                // Password
                // ---------------------------------------------------------

                card.add(
                                createLabel("Password"));

                card.add(
                                Box.createVerticalStrut(6));

                card.add(
                                createPasswordField(
                                                false));

                card.add(
                                Box.createVerticalStrut(15));

                // ---------------------------------------------------------
                // Confirm Password
                // ---------------------------------------------------------

                card.add(
                                createLabel(
                                                "Confirm password"));

                card.add(
                                Box.createVerticalStrut(6));

                card.add(
                                createPasswordField(
                                                true));

                card.add(
                                Box.createVerticalStrut(22));

                // ---------------------------------------------------------
                // Continue button
                // ---------------------------------------------------------

                RoundedActionButton continueButton = new RoundedActionButton(
                                "Continue");

                continueButton.setAlignmentX(
                                Component.LEFT_ALIGNMENT);

                continueButton.setPreferredSize(
                                new Dimension(
                                                354,
                                                48));

                continueButton.setMaximumSize(
                                new Dimension(
                                                Integer.MAX_VALUE,
                                                48));

                continueButton.setBackground(
                                BussinTheme.RED);

                continueButton.setForeground(
                                Color.WHITE);

                continueButton.setFont(
                                new Font(
                                                FONT,
                                                Font.BOLD,
                                                14));

                continueButton.addActionListener(
                                e -> continueRegistration());

                card.add(
                                continueButton);

                card.add(
                                Box.createVerticalStrut(19));

                // ---------------------------------------------------------
                // Login footer
                // ---------------------------------------------------------

                card.add(
                                createLoginFooter());

                // ---------------------------------------------------------
                // Center card
                // ---------------------------------------------------------

                GridBagConstraints gbc = new GridBagConstraints();

                gbc.gridx = 0;
                gbc.gridy = 0;

                gbc.weightx = 1;
                gbc.weighty = 1;

                gbc.anchor = GridBagConstraints.CENTER;

                page.add(
                                card,
                                gbc);

                return page;
        }

        // =============================================================
        // STEP INDICATOR
        // =============================================================

        private JPanel createStepIndicator() {

                JPanel panel = new JPanel(
                                new GridBagLayout());

                panel.setOpaque(false);

                panel.setAlignmentX(
                                Component.LEFT_ALIGNMENT);

                panel.setPreferredSize(
                                new Dimension(
                                                354,
                                                32));

                GridBagConstraints gbc = new GridBagConstraints();

                gbc.gridy = 0;

                JLabel stepOne = createStep(
                                "01",
                                "Account",
                                true);

                JLabel stepTwo = createStep(
                                "02",
                                "Personal",
                                false);

                JPanel line = new JPanel();

                line.setPreferredSize(
                                new Dimension(
                                                30,
                                                1));

                line.setBackground(
                                BussinTheme.BORDER);

                gbc.gridx = 0;

                panel.add(
                                stepOne,
                                gbc);

                gbc.gridx = 1;

                gbc.weightx = 1;

                gbc.fill = GridBagConstraints.HORIZONTAL;

                panel.add(
                                line,
                                gbc);

                gbc.gridx = 2;

                gbc.weightx = 0;

                gbc.fill = GridBagConstraints.NONE;

                panel.add(
                                stepTwo,
                                gbc);

                return panel;
        }

        private JLabel createStep(
                        String number,
                        String text,
                        boolean active) {

                JLabel label = new JLabel(
                                number + "  " + text);

                label.setFont(
                                new Font(
                                                FONT,
                                                Font.BOLD,
                                                11));

                label.setForeground(
                                active
                                                ? BussinTheme.RED
                                                : BussinTheme.TEXT_MUTED);

                return label;
        }

        // =============================================================
        // TEXT FIELD
        // =============================================================

        private RoundedFieldPanel createTextField(
                        JTextField field) {

                field.setFont(
                                new Font(
                                                FONT,
                                                Font.PLAIN,
                                                14));

                field.setForeground(
                                BussinTheme.TEXT_PRIMARY);

                field.setCaretColor(
                                BussinTheme.RED);

                field.setOpaque(false);

                field.setBorder(
                                new EmptyBorder(
                                                0,
                                                14,
                                                0,
                                                14));

                RoundedFieldPanel container = new RoundedFieldPanel();

                container.setLayout(
                                new BorderLayout());

                configureField(
                                container);

                container.add(
                                field,
                                BorderLayout.CENTER);

                addFocusBehavior(
                                field,
                                container);

                return container;
        }

        // =============================================================
        // PASSWORD FIELD
        // =============================================================

        private RoundedFieldPanel createPasswordField(
                        boolean confirm) {

                JPasswordField field = new JPasswordField();

                field.setFont(
                                new Font(
                                                FONT,
                                                Font.PLAIN,
                                                14));

                field.setForeground(
                                BussinTheme.TEXT_PRIMARY);

                field.setCaretColor(
                                BussinTheme.RED);

                field.setEchoChar(
                                '\u2022');

                field.setOpaque(false);

                field.setBorder(
                                new EmptyBorder(
                                                0,
                                                14,
                                                0,
                                                4));

                RoundedFieldPanel container = new RoundedFieldPanel();

                container.setLayout(
                                new BorderLayout());

                configureField(
                                container);

                container.add(
                                field,
                                BorderLayout.CENTER);

                JButton eye = new JButton();

                eye.setIcon(
                                new PasswordEyeIcon(false));

                eye.setPreferredSize(
                                new Dimension(
                                                46,
                                                46));

                eye.setBorder(
                                new EmptyBorder(
                                                0,
                                                0,
                                                0,
                                                0));

                eye.setContentAreaFilled(false);
                eye.setBorderPainted(false);
                eye.setFocusPainted(false);
                eye.setFocusable(false);
                eye.setOpaque(false);

                eye.setCursor(
                                Cursor.getPredefinedCursor(
                                                Cursor.HAND_CURSOR));

                eye.setToolTipText(
                                "Show password");

                eye.addActionListener(
                                e -> {

                                        boolean visible = field.getEchoChar() == 0;

                                        if (visible) {

                                                field.setEchoChar(
                                                                '\u2022');

                                                eye.setIcon(
                                                                new PasswordEyeIcon(false));

                                                eye.setToolTipText(
                                                                "Show password");

                                        } else {

                                                field.setEchoChar(
                                                                (char) 0);

                                                eye.setIcon(
                                                                new PasswordEyeIcon(true));

                                                eye.setToolTipText(
                                                                "Hide password");
                                        }

                                        field.requestFocusInWindow();
                                });

                container.add(
                                eye,
                                BorderLayout.EAST);

                addFocusBehavior(
                                field,
                                container);

                if (confirm) {
                        confirmPasswordField = field;
                } else {
                        passwordField = field;
                }

                return container;
        }

        private void configureField(
                        RoundedFieldPanel container) {

                container.setAlignmentX(
                                Component.LEFT_ALIGNMENT);

                container.setPreferredSize(
                                new Dimension(
                                                354,
                                                46));

                container.setMinimumSize(
                                new Dimension(
                                                0,
                                                46));

                container.setMaximumSize(
                                new Dimension(
                                                Integer.MAX_VALUE,
                                                46));
        }

        private void addFocusBehavior(
                        JComponent field,
                        RoundedFieldPanel container) {

                field.addFocusListener(
                                new FocusAdapter() {

                                        @Override
                                        public void focusGained(
                                                        FocusEvent e) {

                                                container.setFocused(true);
                                                container.repaint();
                                        }

                                        @Override
                                        public void focusLost(
                                                        FocusEvent e) {

                                                container.setFocused(false);
                                                container.repaint();
                                        }
                                });
        }

        // =============================================================
        // CONTINUE
        // =============================================================

        private void continueRegistration() {

                String email = emailField
                                .getText()
                                .trim();

                String password = new String(
                                passwordField.getPassword());

                String confirmPassword = new String(
                                confirmPasswordField.getPassword());

                if (email.isBlank()
                                || password.isBlank()
                                || confirmPassword.isBlank()) {

                        JOptionPane.showMessageDialog(
                                        this,
                                        "Please complete all fields.",
                                        "Registration",
                                        JOptionPane.WARNING_MESSAGE);

                        return;
                }

                if (!email.contains("@")
                                || !email.contains(".")) {

                        JOptionPane.showMessageDialog(
                                        this,
                                        "Please enter a valid email address.",
                                        "Registration",
                                        JOptionPane.WARNING_MESSAGE);

                        return;
                }

                if (password.length() < 6) {

                        JOptionPane.showMessageDialog(
                                        this,
                                        "Password must contain at least 6 characters.",
                                        "Registration",
                                        JOptionPane.WARNING_MESSAGE);

                        return;
                }

                if (!password.equals(
                                confirmPassword)) {

                        JOptionPane.showMessageDialog(
                                        this,
                                        "Passwords do not match.",
                                        "Registration",
                                        JOptionPane.WARNING_MESSAGE);

                        return;
                }

                registrationData.setEmail(
                                email);

                registrationData.setPassword(
                                password);

                navigationHandler.accept(
                                "register-step-2");
        }

        // =============================================================
        // DATA
        // =============================================================

        public RegistrationData getData() {

                return registrationData;
        }

        // =============================================================
        // LOGIN FOOTER
        // =============================================================

        private JPanel createLoginFooter() {

                JPanel panel = new JPanel(
                                new FlowLayout(
                                                FlowLayout.CENTER,
                                                3,
                                                0));

                panel.setOpaque(false);

                panel.setAlignmentX(
                                Component.LEFT_ALIGNMENT);

                JLabel text = new JLabel(
                                "Already have an account?");

                text.setFont(
                                new Font(
                                                FONT,
                                                Font.PLAIN,
                                                12));

                text.setForeground(
                                BussinTheme.TEXT_SECONDARY);

                JButton login = new JButton(
                                "Sign in");

                login.setFont(
                                new Font(
                                                FONT,
                                                Font.BOLD,
                                                12));

                login.setForeground(
                                BussinTheme.RED);

                login.setBorderPainted(false);
                login.setContentAreaFilled(false);
                login.setFocusPainted(false);
                login.setOpaque(false);

                login.setCursor(
                                Cursor.getPredefinedCursor(
                                                Cursor.HAND_CURSOR));

                login.addActionListener(
                                e -> navigationHandler.accept(
                                                "login"));

                panel.add(text);
                panel.add(login);

                return panel;
        }

        // =============================================================
        // LABEL
        // =============================================================

        private JLabel createLabel(
                        String text) {

                JLabel label = new JLabel(text);

                label.setFont(
                                new Font(
                                                FONT,
                                                Font.BOLD,
                                                13));

                label.setForeground(
                                BussinTheme.TEXT_PRIMARY);

                label.setAlignmentX(
                                Component.LEFT_ALIGNMENT);

                return label;
        }

        // =============================================================
        // CARD
        // =============================================================

        private static class RoundedAuthCard
                        extends JPanel {

                RoundedAuthCard() {
                        setOpaque(false);
                }

                @Override
                protected void paintComponent(
                                Graphics graphics) {

                        Graphics2D g = (Graphics2D) graphics.create();

                        g.setRenderingHint(
                                        RenderingHints.KEY_ANTIALIASING,
                                        RenderingHints.VALUE_ANTIALIAS_ON);

                        int width = getWidth();
                        int height = getHeight();

                        g.setColor(
                                        new Color(
                                                        0,
                                                        0,
                                                        0,
                                                        14));

                        g.fillRoundRect(
                                        3,
                                        4,
                                        width - 4,
                                        height - 3,
                                        20,
                                        20);

                        g.setColor(
                                        Color.WHITE);

                        g.fillRoundRect(
                                        0,
                                        0,
                                        width - 5,
                                        height - 5,
                                        20,
                                        20);

                        g.setColor(
                                        BussinTheme.BORDER);

                        g.drawRoundRect(
                                        0,
                                        0,
                                        width - 5,
                                        height - 5,
                                        20,
                                        20);

                        g.dispose();

                        super.paintComponent(
                                        graphics);
                }
        }

        // =============================================================
        // FIELD PANEL
        // =============================================================

        private static class RoundedFieldPanel
                        extends JPanel {

                private boolean focused;

                void setFocused(
                                boolean value) {

                        focused = value;
                }

                @Override
                protected void paintComponent(
                                Graphics graphics) {

                        Graphics2D g = (Graphics2D) graphics.create();

                        g.setRenderingHint(
                                        RenderingHints.KEY_ANTIALIASING,
                                        RenderingHints.VALUE_ANTIALIAS_ON);

                        int width = getWidth();
                        int height = getHeight();

                        g.setColor(
                                        Color.decode("#F9FAFB"));

                        g.fillRoundRect(
                                        0,
                                        0,
                                        width - 1,
                                        height - 1,
                                        11,
                                        11);

                        g.setColor(
                                        focused
                                                        ? BussinTheme.RED
                                                        : BussinTheme.BORDER_STRONG);

                        g.setStroke(
                                        new BasicStroke(
                                                        focused
                                                                        ? 1.5f
                                                                        : 1f));

                        g.drawRoundRect(
                                        0,
                                        0,
                                        width - 1,
                                        height - 1,
                                        11,
                                        11);

                        g.dispose();

                        super.paintComponent(
                                        graphics);
                }
        }

        // =============================================================
        // BUTTON
        // =============================================================

        private static class RoundedActionButton
                        extends JButton {

                RoundedActionButton(
                                String text) {

                        super(text);

                        setOpaque(false);
                        setContentAreaFilled(false);
                        setBorderPainted(false);
                        setFocusPainted(false);

                        setCursor(
                                        Cursor.getPredefinedCursor(
                                                        Cursor.HAND_CURSOR));
                }

                @Override
                protected void paintComponent(
                                Graphics graphics) {

                        Graphics2D g = (Graphics2D) graphics.create();

                        g.setRenderingHint(
                                        RenderingHints.KEY_ANTIALIASING,
                                        RenderingHints.VALUE_ANTIALIAS_ON);

                        Color background = getBackground();

                        if (getModel().isPressed()) {

                                background = BussinTheme.CRIMSON;

                        } else if (getModel().isRollover()
                                        && background.equals(
                                                        BussinTheme.RED)) {

                                background = BussinTheme.PRIMARY_HOVER;
                        }

                        g.setColor(background);

                        g.fillRoundRect(
                                        0,
                                        0,
                                        getWidth() - 1,
                                        getHeight() - 1,
                                        11,
                                        11);

                        g.dispose();

                        super.paintComponent(
                                        graphics);
                }
        }

        // =============================================================
        // PASSWORD EYE
        // =============================================================

        private static class PasswordEyeIcon
                        implements Icon {

                private final boolean visible;

                PasswordEyeIcon(
                                boolean visible) {

                        this.visible = visible;
                }

                @Override
                public int getIconWidth() {
                        return 20;
                }

                @Override
                public int getIconHeight() {
                        return 20;
                }

                @Override
                public void paintIcon(
                                Component component,
                                Graphics graphics,
                                int x,
                                int y) {

                        Graphics2D g = (Graphics2D) graphics.create();

                        g.setRenderingHint(
                                        RenderingHints.KEY_ANTIALIASING,
                                        RenderingHints.VALUE_ANTIALIAS_ON);

                        g.setColor(
                                        BussinTheme.TEXT_SECONDARY);

                        g.setStroke(
                                        new BasicStroke(
                                                        1.5f,
                                                        BasicStroke.CAP_ROUND,
                                                        BasicStroke.JOIN_ROUND));

                        Path2D eye = new Path2D.Double();

                        eye.moveTo(
                                        x + 2,
                                        y + 10);

                        eye.quadTo(
                                        x + 10,
                                        y + 2,
                                        x + 18,
                                        y + 10);

                        eye.quadTo(
                                        x + 10,
                                        y + 18,
                                        x + 2,
                                        y + 10);

                        g.draw(eye);

                        g.drawOval(
                                        x + 7,
                                        y + 7,
                                        6,
                                        6);

                        g.fillOval(
                                        x + 9,
                                        y + 9,
                                        2,
                                        2);

                        if (!visible) {

                                g.drawLine(
                                                x + 3,
                                                y + 17,
                                                x + 17,
                                                y + 3);
                        }

                        g.dispose();
                }
        }
}