package com.bussin.desktop;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.bussin.desktop.services.AuthSession;
import com.bussin.desktop.services.FirebaseAuthService;
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

    public static void showLogin() {

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

                        case "google-profile-completion" -> {

                            frame.dispose();

                            showGoogleProfileCompletion();
                        }

                        default -> {

                            if (route.startsWith(
                                    "authenticated:")) {

                                String authenticationData = route.substring(
                                        "authenticated:"
                                                .length());

                                String role = authenticationData;
                                String email = "";

                                int separator = authenticationData.indexOf(':');

                                if (separator >= 0) {

                                    role = authenticationData.substring(
                                            0,
                                            separator);

                                    email = authenticationData.substring(
                                            separator + 1);
                                }

                                frame.dispose();

                                MainFrame mainFrame = new MainFrame(
                                        role,
                                        email);

                                mainFrame.setVisible(true);
                            }
                        }
                    }
                });

        frame.setContentPane(screen);

        frame.setVisible(true);
    }

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
                                    data,
                                    false);
                        }

                        default -> {
                        }
                    }
                });

        frame.setContentPane(
                step1Holder[0]);

        frame.setVisible(true);
    }

    private static void showRegistrationStep2(
            RegistrationData data,
            boolean googleProfileCompletion) {

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
                        }
                    }
                },
                data,
                googleProfileCompletion);

        frame.setContentPane(step2);

        frame.setVisible(true);
    }

    private static void showGoogleProfileCompletion() {

        JFrame frame = createAuthFrame();

        RegistrationData data = new RegistrationData();

        RegistrationStep2Screen step2 = new RegistrationStep2Screen(
                route -> {

                    switch (route) {

                        case "google-profile-complete" -> {

                            frame.dispose();

                            String email = AuthSession.getEmail();

                            MainFrame mainFrame = new MainFrame(
                                    "COMMUTER",
                                    email);

                            mainFrame.setVisible(true);
                        }

                        case "login" -> {

                            FirebaseAuthService.logout();

                            frame.dispose();

                            showLogin();
                        }

                        default -> {
                        }
                    }
                },
                data,
                true);

        frame.setContentPane(step2);

        frame.setVisible(true);
    }

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