package com.bussin.desktop.ui.auth;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.bussin.desktop.services.MockAuthService;
import com.bussin.desktop.ui.components.AppButton;
import com.bussin.desktop.ui.theme.BussinTheme;

public class ForgotPasswordScreen extends JPanel {

        private final Consumer<String> navigationHandler;
        private JTextField emailField;

        public ForgotPasswordScreen(
                        Consumer<String> navigationHandler) {

                this.navigationHandler = navigationHandler;

                setLayout(new BorderLayout());

                AuthBackground background = new AuthBackground();

                JPanel authPanel = createForgotPasswordPanel();

                background.setAuthContent(authPanel);

                add(
                                background,
                                BorderLayout.CENTER);
        }

        private JPanel createForgotPasswordPanel() {

                JPanel panel = new JPanel();

                panel.setBackground(
                                Color.WHITE);

                panel.setBorder(
                                new EmptyBorder(
                                                55,
                                                60,
                                                45,
                                                60));

                panel.setLayout(
                                new BoxLayout(
                                                panel,
                                                BoxLayout.Y_AXIS));

                JLabel title = new JLabel(
                                "Reset your password");

                title.setFont(
                                new Font(
                                                "Segoe UI",
                                                Font.BOLD,
                                                30));

                title.setForeground(
                                BussinTheme.TEXT_PRIMARY);

                title.setAlignmentX(
                                Component.LEFT_ALIGNMENT);

                panel.add(title);

                panel.add(
                                Box.createVerticalStrut(8));

                JLabel subtitle = new JLabel(
                                "<html>Enter your email and we'll send instructions to reset your password.</html>");

                subtitle.setFont(
                                new Font(
                                                "Segoe UI",
                                                Font.PLAIN,
                                                14));

                subtitle.setForeground(
                                BussinTheme.TEXT_SECONDARY);

                subtitle.setAlignmentX(
                                Component.LEFT_ALIGNMENT);

                panel.add(subtitle);

                panel.add(
                                Box.createVerticalStrut(35));

                JLabel emailLabel = new JLabel(
                                "Email address");

                emailLabel.setFont(
                                new Font(
                                                "Segoe UI",
                                                Font.BOLD,
                                                13));

                emailLabel.setForeground(
                                BussinTheme.TEXT_PRIMARY);

                emailLabel.setAlignmentX(
                                Component.LEFT_ALIGNMENT);

                panel.add(emailLabel);

                panel.add(
                                Box.createVerticalStrut(8));

                emailField = new JTextField();

                emailField.setFont(
                                new Font(
                                                "Segoe UI",
                                                Font.PLAIN,
                                                14));

                emailField.setForeground(
                                BussinTheme.TEXT_PRIMARY);

                emailField.setBackground(
                                Color.WHITE);

                emailField.setBorder(
                                BorderFactory.createCompoundBorder(
                                                BorderFactory.createLineBorder(
                                                                BussinTheme.BORDER_STRONG),
                                                BorderFactory.createEmptyBorder(
                                                                0,
                                                                12,
                                                                0,
                                                                12)));

                emailField.setMaximumSize(
                                new Dimension(
                                                Integer.MAX_VALUE,
                                                46));

                emailField.setPreferredSize(
                                new Dimension(
                                                0,
                                                46));

                panel.add(emailField);

                panel.add(
                                Box.createVerticalStrut(26));

                AppButton resetButton = new AppButton(
                                "Send Reset Instructions");

                resetButton.setAlignmentX(
                                Component.LEFT_ALIGNMENT);

                resetButton.setMaximumSize(
                                new Dimension(
                                                Integer.MAX_VALUE,
                                                48));

                resetButton.setPreferredSize(
                                new Dimension(
                                                0,
                                                48));

                resetButton.setBackground(
                                BussinTheme.RED);

                resetButton.setForeground(
                                Color.WHITE);

                resetButton.setFont(
                                new Font(
                                                "Segoe UI",
                                                Font.BOLD,
                                                14));

                resetButton.addActionListener(
                                e -> handleReset());

                panel.add(resetButton);

                panel.add(
                                Box.createVerticalStrut(20));

                JButton backButton = new JButton(
                                "Back to Sign In");

                backButton.setFont(
                                new Font(
                                                "Segoe UI",
                                                Font.BOLD,
                                                13));

                backButton.setForeground(
                                BussinTheme.RED);

                backButton.setBorderPainted(false);
                backButton.setContentAreaFilled(false);
                backButton.setFocusPainted(false);
                backButton.setCursor(
                                new Cursor(
                                                Cursor.HAND_CURSOR));

                backButton.setAlignmentX(
                                Component.LEFT_ALIGNMENT);

                backButton.addActionListener(
                                e -> navigationHandler.accept(
                                                "login"));

                panel.add(backButton);

                panel.add(
                                Box.createVerticalGlue());

                return panel;
        }

        private void handleReset() {

                String value = emailField.getText().trim();

                if (value.isEmpty()) {

                        JOptionPane.showMessageDialog(
                                        this,
                                        "Please enter your email.",
                                        "Password Reset",
                                        JOptionPane.WARNING_MESSAGE);

                        return;
                }

                if (MockAuthService.sendPasswordReset(
                                value)) {

                        JOptionPane.showMessageDialog(
                                        this,
                                        "Password reset instructions would be sent to "
                                                        + value
                                                        + ".\n\nFirebase will handle this once authentication is connected.",
                                        "Password Reset",
                                        JOptionPane.INFORMATION_MESSAGE);

                        navigationHandler.accept(
                                        "login");

                } else {

                        JOptionPane.showMessageDialog(
                                        this,
                                        "No BUSSIN account was found with that email.",
                                        "Password Reset",
                                        JOptionPane.WARNING_MESSAGE);
                }
        }
}