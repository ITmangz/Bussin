package com.bussin.desktop;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.bussin.desktop.ui.MainFrame;
import com.bussin.desktop.ui.auth.ForgotPasswordScreen;
import com.bussin.desktop.ui.auth.LoginScreen;
import com.bussin.desktop.ui.auth.OpeningScreen;
import com.bussin.desktop.ui.auth.RegistrationData;
import com.bussin.desktop.ui.auth.RegistrationStep1Screen;
import com.bussin.desktop.ui.auth.RegistrationStep2Screen;
import com.bussin.desktop.ui.theme.BussinTheme;

public class App {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            BussinTheme.initialize();

            showOpening();
        });
    }

    // ================================================================
    // OPENING
    // ================================================================

    private static void showOpening() {

        JFrame frame = createAuthFrame();

        OpeningScreen screen = new OpeningScreen(
                () -> {

                    frame.dispose();

                    showLogin();
                });

        frame.setContentPane(screen);

        frame.setVisible(true);
    }

    // ================================================================
    // LOGIN
    // ================================================================

    private static void showLogin() {

        JFrame frame = createAuthFrame();

        LoginScreen screen = new LoginScreen(
                route -> {

                    if (route == null) {
                        return;
                    }

                    switch (route) {

                        case "register" -> {

                            frame.dispose();

                            showRegistration();
                        }

                        case "forgot-password" -> {

                            frame.dispose();

                            showForgotPassword();
                        }

                        default -> {

                            /*
                             * Successful login now sends:
                             *
                             * authenticated:ADMIN
                             * authenticated:EMPLOYEE
                             * authenticated:USER
                             */

                            if (route.startsWith(
                                    "authenticated:")) {

                                String role = route.substring(
                                        "authenticated:"
                                                .length());

                                frame.dispose();

                                MainFrame mainFrame = new MainFrame(role);

                                mainFrame.setVisible(true);
                            }
                        }
                    }
                });

        frame.setContentPane(screen);

        frame.setVisible(true);
    }

    // ================================================================
    // REGISTRATION — STEP 1
    // ================================================================

    private static void showRegistration() {

        JFrame frame = createAuthFrame();

        final RegistrationStep1Screen[] step1Holder = new RegistrationStep1Screen[1];

        step1Holder[0] = new RegistrationStep1Screen(
                route -> {

                    switch (route) {

                        case "login" -> {

                            frame.dispose();

                            showLogin();
                        }

                        case "register-step-2" -> {

                            RegistrationData data = step1Holder[0]
                                    .getData();

                            frame.dispose();

                            showRegistrationStep2(
                                    data);
                        }

                        default -> {
                            // No action.
                        }
                    }
                });

        frame.setContentPane(
                step1Holder[0]);

        frame.setVisible(true);
    }

    // ================================================================
    // REGISTRATION — STEP 2
    // ================================================================

    private static void showRegistrationStep2(
            RegistrationData data) {

        JFrame frame = createAuthFrame();

        RegistrationStep2Screen step2 = new RegistrationStep2Screen(
                route -> {

                    switch (route) {

                        case "register" -> {

                            frame.dispose();

                            showRegistration();
                        }

                        case "login" -> {

                            frame.dispose();

                            showLogin();
                        }

                        default -> {
                            // No action.
                        }
                    }
                },
                data);

        frame.setContentPane(step2);

        frame.setVisible(true);
    }

    // ================================================================
    // FORGOT PASSWORD
    // ================================================================

    private static void showForgotPassword() {

        JFrame frame = createAuthFrame();

        ForgotPasswordScreen screen = new ForgotPasswordScreen(
                route -> {

                    if ("login".equals(route)) {

                        frame.dispose();

                        showLogin();
                    }
                });

        frame.setContentPane(screen);

        frame.setVisible(true);
    }

    // ================================================================
    // AUTH WINDOW
    // ================================================================

    private static JFrame createAuthFrame() {

        JFrame frame = new JFrame("BUSSIN");

        frame.setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE);

        frame.setMinimumSize(
                new Dimension(
                        900,
                        650));

        frame.setSize(
                1280,
                800);

        frame.setLocationRelativeTo(null);

        return frame;
    }
}