package com.bussin.desktop.ui.auth;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.Path2D;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.bussin.desktop.services.MockAuthService;
import com.bussin.desktop.ui.theme.BussinTheme;

public class LoginScreen extends JPanel {

        private static final String FONT = "Segoe UI";

        private final java.util.function.Consumer<String> navigationHandler;

        private JTextField emailField;
        private JPasswordField passwordField;
        private RoundedFieldPanel passwordContainer;
        private JButton passwordToggleButton;

        public LoginScreen(
                        java.util.function.Consumer<String> navigationHandler) {

                this.navigationHandler = navigationHandler;

                setLayout(new BorderLayout());
                setBackground(Color.WHITE);

                AuthBackground background = new AuthBackground();

                background.setAuthContent(
                                createLoginContent());

                add(
                                background,
                                BorderLayout.CENTER);
        }

        // =============================================================
        // LOGIN CONTENT
        // =============================================================

        private JPanel createLoginContent() {

                JPanel page = new JPanel(new GridBagLayout());

                page.setBackground(
                                Color.decode("#F3F4F6"));

                RoundedAuthCard card = new RoundedAuthCard();

                card.setLayout(
                                new BoxLayout(
                                                card,
                                                BoxLayout.Y_AXIS));

                card.setBorder(
                                new EmptyBorder(
                                                36,
                                                38,
                                                32,
                                                38));

                card.setPreferredSize(
                                new Dimension(
                                                430,
                                                575));

                card.setMinimumSize(
                                new Dimension(
                                                360,
                                                500));

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
                                Box.createVerticalStrut(25));

                // ---------------------------------------------------------
                // Heading
                // ---------------------------------------------------------

                JLabel title = new JLabel("Welcome back");

                title.setFont(
                                new Font(
                                                FONT,
                                                Font.BOLD,
                                                29));

                title.setForeground(
                                BussinTheme.TEXT_PRIMARY);

                title.setAlignmentX(
                                Component.LEFT_ALIGNMENT);

                card.add(title);

                card.add(
                                Box.createVerticalStrut(7));

                JLabel subtitle = new JLabel(
                                "Sign in to continue to your account.");

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
                                Box.createVerticalStrut(28));

                // ---------------------------------------------------------
                // Email
                // ---------------------------------------------------------

                card.add(
                                createLabel("Email address"));

                card.add(
                                Box.createVerticalStrut(7));

                emailField = new JTextField();

                card.add(
                                createTextField(emailField));

                card.add(
                                Box.createVerticalStrut(18));

                // ---------------------------------------------------------
                // Password header
                // ---------------------------------------------------------

                JPanel passwordHeader = new JPanel(
                                new BorderLayout());

                passwordHeader.setOpaque(false);
                passwordHeader.setAlignmentX(
                                Component.LEFT_ALIGNMENT);

                passwordHeader.setMaximumSize(
                                new Dimension(
                                                Integer.MAX_VALUE,
                                                20));

                passwordHeader.add(
                                createLabel("Password"),
                                BorderLayout.WEST);

                JButton forgot = createLinkButton(
                                "Forgot password?");

                forgot.addActionListener(
                                e -> navigationHandler.accept(
                                                "forgot-password"));

                passwordHeader.add(
                                forgot,
                                BorderLayout.EAST);

                card.add(passwordHeader);

                card.add(
                                Box.createVerticalStrut(7));

                // ---------------------------------------------------------
                // Password
                // ---------------------------------------------------------

                card.add(
                                createPasswordField());

                card.add(
                                Box.createVerticalStrut(22));

                // ---------------------------------------------------------
                // Sign in
                // ---------------------------------------------------------

                RoundedActionButton signIn = new RoundedActionButton(
                                "Sign In");

                signIn.setAlignmentX(
                                Component.LEFT_ALIGNMENT);

                signIn.setPreferredSize(
                                new Dimension(
                                                354,
                                                48));

                signIn.setMaximumSize(
                                new Dimension(
                                                Integer.MAX_VALUE,
                                                48));

                signIn.setBackground(
                                BussinTheme.RED);

                signIn.setForeground(
                                Color.WHITE);

                signIn.setFont(
                                new Font(
                                                FONT,
                                                Font.BOLD,
                                                14));

                signIn.addActionListener(
                                e -> handleLogin());

                card.add(signIn);

                card.add(
                                Box.createVerticalStrut(21));

                // ---------------------------------------------------------
                // Divider
                // ---------------------------------------------------------

                card.add(
                                createDivider());

                card.add(
                                Box.createVerticalStrut(18));

                // ---------------------------------------------------------
                // Google
                // ---------------------------------------------------------

                RoundedActionButton google = new RoundedActionButton(
                                "G     Continue with Google");

                google.setAlignmentX(
                                Component.LEFT_ALIGNMENT);

                google.setPreferredSize(
                                new Dimension(
                                                354,
                                                46));

                google.setMaximumSize(
                                new Dimension(
                                                Integer.MAX_VALUE,
                                                46));

                google.setBackground(
                                Color.WHITE);

                google.setForeground(
                                BussinTheme.TEXT_PRIMARY);

                google.setFont(
                                new Font(
                                                FONT,
                                                Font.BOLD,
                                                13));

                google.setBorderColor(
                                BussinTheme.BORDER_STRONG);

                google.addActionListener(
                                e -> handleGoogleLogin());

                card.add(google);

                card.add(
                                Box.createVerticalStrut(24));

                // ---------------------------------------------------------
                // Register
                // ---------------------------------------------------------

                JPanel register = new JPanel(
                                new FlowLayout(
                                                FlowLayout.CENTER,
                                                3,
                                                0));

                register.setOpaque(false);
                register.setAlignmentX(
                                Component.LEFT_ALIGNMENT);

                JLabel registerText = new JLabel(
                                "Don't have an account?");

                registerText.setFont(
                                new Font(
                                                FONT,
                                                Font.PLAIN,
                                                12));

                registerText.setForeground(
                                BussinTheme.TEXT_SECONDARY);

                JButton registerButton = createLinkButton(
                                "Create account");

                registerButton.setFont(
                                new Font(
                                                FONT,
                                                Font.BOLD,
                                                12));

                registerButton.addActionListener(
                                e -> navigationHandler.accept(
                                                "register"));

                register.add(registerText);
                register.add(registerButton);

                card.add(register);

                // ---------------------------------------------------------
                // Center card
                // ---------------------------------------------------------

                GridBagConstraints constraints = new GridBagConstraints();

                constraints.gridx = 0;
                constraints.gridy = 0;

                constraints.weightx = 1;
                constraints.weighty = 1;

                constraints.fill = GridBagConstraints.NONE;

                constraints.anchor = GridBagConstraints.CENTER;

                page.add(
                                card,
                                constraints);

                return page;
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

                configureFieldContainer(
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

        private RoundedFieldPanel createPasswordField() {

                passwordField = new JPasswordField();

                passwordField.setFont(
                                new Font(
                                                FONT,
                                                Font.PLAIN,
                                                14));

                passwordField.setForeground(
                                BussinTheme.TEXT_PRIMARY);

                passwordField.setCaretColor(
                                BussinTheme.RED);

                passwordField.setEchoChar(
                                '\u2022');

                passwordField.setOpaque(false);

                passwordField.setBorder(
                                new EmptyBorder(
                                                0,
                                                14,
                                                0,
                                                4));

                passwordContainer = new RoundedFieldPanel();

                passwordContainer.setLayout(
                                new BorderLayout());

                configureFieldContainer(
                                passwordContainer);

                passwordContainer.add(
                                passwordField,
                                BorderLayout.CENTER);

                // ---------------------------------------------------------
                // Eye button
                // ---------------------------------------------------------

                passwordToggleButton = new JButton();

                passwordToggleButton.setIcon(
                                new PasswordEyeIcon(false));

                passwordToggleButton.setPreferredSize(
                                new Dimension(
                                                46,
                                                46));

                passwordToggleButton.setMinimumSize(
                                new Dimension(
                                                46,
                                                46));

                passwordToggleButton.setMaximumSize(
                                new Dimension(
                                                46,
                                                46));

                passwordToggleButton.setBorder(
                                new EmptyBorder(
                                                0,
                                                0,
                                                0,
                                                0));

                passwordToggleButton.setContentAreaFilled(
                                false);

                passwordToggleButton.setBorderPainted(
                                false);

                passwordToggleButton.setFocusPainted(
                                false);

                passwordToggleButton.setFocusable(
                                false);

                passwordToggleButton.setOpaque(
                                false);

                passwordToggleButton.setCursor(
                                Cursor.getPredefinedCursor(
                                                Cursor.HAND_CURSOR));

                passwordToggleButton.setToolTipText(
                                "Show password");

                passwordToggleButton.addActionListener(
                                e -> togglePassword());

                passwordContainer.add(
                                passwordToggleButton,
                                BorderLayout.EAST);

                addFocusBehavior(
                                passwordField,
                                passwordContainer);

                return passwordContainer;
        }

        private void configureFieldContainer(
                        RoundedFieldPanel container) {

                container.setAlignmentX(
                                Component.LEFT_ALIGNMENT);

                container.setPreferredSize(
                                new Dimension(
                                                354,
                                                48));

                container.setMinimumSize(
                                new Dimension(
                                                0,
                                                48));

                container.setMaximumSize(
                                new Dimension(
                                                Integer.MAX_VALUE,
                                                48));
        }

        private void addFocusBehavior(
                        JComponent field,
                        RoundedFieldPanel container) {

                field.addFocusListener(
                                new FocusAdapter() {

                                        @Override
                                        public void focusGained(
                                                        FocusEvent event) {

                                                container.setFocused(
                                                                true);

                                                container.repaint();
                                        }

                                        @Override
                                        public void focusLost(
                                                        FocusEvent event) {

                                                container.setFocused(
                                                                false);

                                                container.repaint();
                                        }
                                });
        }

        // =============================================================
        // PASSWORD TOGGLE
        // =============================================================

        private void togglePassword() {

                boolean currentlyVisible = passwordField.getEchoChar() == 0;

                if (currentlyVisible) {

                        passwordField.setEchoChar(
                                        '\u2022');

                        passwordToggleButton.setIcon(
                                        new PasswordEyeIcon(false));

                        passwordToggleButton.setToolTipText(
                                        "Show password");

                } else {

                        passwordField.setEchoChar(
                                        (char) 0);

                        passwordToggleButton.setIcon(
                                        new PasswordEyeIcon(true));

                        passwordToggleButton.setToolTipText(
                                        "Hide password");
                }

                passwordField.requestFocusInWindow();
        }

        // =============================================================
        // LOGIN
        // =============================================================

        private void handleLogin() {

                String email = emailField
                                .getText()
                                .trim();

                String password = new String(
                                passwordField.getPassword());

                if (email.isBlank()
                                || password.isBlank()) {

                        JOptionPane.showMessageDialog(
                                        this,
                                        "Please enter your email and password.",
                                        "Sign In",
                                        JOptionPane.WARNING_MESSAGE);

                        return;
                }

                if (MockAuthService.login(
                                email,
                                password)) {

                        RegistrationData user = MockAuthService.getUser(
                                        email);

                        if (user != null) {

                                String role = user.getRole();

                                navigationHandler.accept(
                                                "authenticated:"
                                                                + user.getRole()
                                                                + ":"
                                                                + email);
                        }

                        return;
                }

                JOptionPane.showMessageDialog(
                                this,
                                "Invalid email or password.",
                                "Sign In",
                                JOptionPane.ERROR_MESSAGE);
        }

        // =============================================================
        // GOOGLE
        // =============================================================

        private void handleGoogleLogin() {

                JOptionPane.showMessageDialog(
                                this,
                                "Google authentication will be connected through Firebase.",
                                "Google Sign-In",
                                JOptionPane.INFORMATION_MESSAGE);
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
        // LINK
        // =============================================================

        private JButton createLinkButton(
                        String text) {

                JButton button = new JButton(text);

                button.setFont(
                                new Font(
                                                FONT,
                                                Font.PLAIN,
                                                12));

                button.setForeground(
                                BussinTheme.RED);

                button.setBorderPainted(false);
                button.setContentAreaFilled(false);
                button.setFocusPainted(false);
                button.setOpaque(false);

                button.setMargin(
                                new Insets(
                                                0,
                                                2,
                                                0,
                                                2));

                button.setCursor(
                                Cursor.getPredefinedCursor(
                                                Cursor.HAND_CURSOR));

                return button;
        }

        // =============================================================
        // DIVIDER
        // =============================================================

        private JPanel createDivider() {

                JPanel panel = new JPanel(
                                new BorderLayout(
                                                12,
                                                0));

                panel.setOpaque(false);

                panel.setAlignmentX(
                                Component.LEFT_ALIGNMENT);

                panel.setPreferredSize(
                                new Dimension(
                                                354,
                                                18));

                panel.setMaximumSize(
                                new Dimension(
                                                Integer.MAX_VALUE,
                                                18));

                JPanel lineLeft = new JPanel();

                lineLeft.setBackground(
                                BussinTheme.BORDER);

                JPanel lineRight = new JPanel();

                lineRight.setBackground(
                                BussinTheme.BORDER);

                JLabel or = new JLabel(
                                "OR",
                                SwingConstants.CENTER);

                or.setFont(
                                new Font(
                                                FONT,
                                                Font.BOLD,
                                                10));

                or.setForeground(
                                BussinTheme.TEXT_MUTED);

                panel.add(
                                lineLeft,
                                BorderLayout.WEST);

                panel.add(
                                or,
                                BorderLayout.CENTER);

                panel.add(
                                lineRight,
                                BorderLayout.EAST);

                return panel;
        }

        // =============================================================
        // AUTH CARD
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

                        // Subtle shadow
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

                        // Card
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
                                                                        : 1.0f));

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

                private Color borderColor = new Color(
                                0,
                                0,
                                0,
                                0);

                RoundedActionButton(
                                String text) {

                        super(text);

                        setOpaque(false);
                        setContentAreaFilled(false);
                        setBorderPainted(false);
                        setFocusPainted(false);

                        setMargin(
                                        new Insets(
                                                        0,
                                                        10,
                                                        0,
                                                        10));

                        setCursor(
                                        Cursor.getPredefinedCursor(
                                                        Cursor.HAND_CURSOR));
                }

                void setBorderColor(
                                Color color) {

                        borderColor = color;
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

                        Color background = getBackground();

                        if (getModel().isPressed()) {

                                if (background.equals(
                                                BussinTheme.RED)) {

                                        background = BussinTheme.CRIMSON;
                                }

                        } else if (getModel().isRollover()) {

                                if (background.equals(
                                                BussinTheme.RED)) {

                                        background = BussinTheme.PRIMARY_HOVER;
                                }
                        }

                        g.setColor(background);

                        g.fillRoundRect(
                                        0,
                                        0,
                                        width - 1,
                                        height - 1,
                                        11,
                                        11);

                        if (borderColor.getAlpha() > 0) {

                                g.setColor(
                                                borderColor);

                                g.drawRoundRect(
                                                0,
                                                0,
                                                width - 1,
                                                height - 1,
                                                11,
                                                11);
                        }

                        g.dispose();

                        super.paintComponent(
                                        graphics);
                }
        }

        // =============================================================
        // EYE ICON
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

                        // Eye shape
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

                        // Pupil
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

                        // Slash when password is hidden
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