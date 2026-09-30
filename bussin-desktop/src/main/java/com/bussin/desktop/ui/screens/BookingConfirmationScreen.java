package com.bussin.desktop.ui.screens;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import com.bussin.desktop.services.BookingException;
import com.bussin.desktop.services.BookingService;
import com.bussin.desktop.services.BookingService.CreateBookingRequest;
import com.bussin.desktop.ui.components.AppButton;
import com.bussin.desktop.ui.components.AppCard;
import com.bussin.desktop.ui.components.AppLabel;
import com.bussin.desktop.ui.components.PageContent;
import com.bussin.desktop.ui.flow.BookingFlowState;
import com.bussin.desktop.ui.flow.BookingStore;
import com.bussin.desktop.ui.flow.BookingStore.UserBooking;
import com.bussin.desktop.ui.theme.BussinTheme;

/**
 * Final step of the booking flow. Creating this screen performs the single
 * booking transaction (off the EDT) and then renders the real result.
 */
public class BookingConfirmationScreen extends JPanel {

        private static final Logger LOG = Logger.getLogger(BookingConfirmationScreen.class.getName());

        private final String currentUserEmail;
        private final Consumer<String> navigationHandler;
        private final BookingFlowState flowState;

        private final AppCard card = new AppCard();
        private final String requestKey = UUID.randomUUID().toString();

        private boolean submitting;

        public BookingConfirmationScreen(
                        String currentUserEmail,
                        Consumer<String> navigationHandler,
                        BookingFlowState flowState) {

                this.currentUserEmail = currentUserEmail;
                this.navigationHandler = navigationHandler;
                this.flowState = flowState;

                initializeUI();
                start();
        }

        private void initializeUI() {

                setBackground(BussinTheme.BACKGROUND);
                setLayout(new BorderLayout());

                card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

                JPanel wrapper = new JPanel(new GridBagLayout());
                wrapper.setOpaque(false);
                wrapper.add(card);

                PageContent page = new PageContent();
                page.addBlock(wrapper, 0);

                add(page.inScrollPane(), BorderLayout.CENTER);
        }

        // ---------------------------------------------------------------------
        // Booking transaction
        // ---------------------------------------------------------------------

        private void start() {

                // Re-opening this screen after success must never book again.
                if (flowState.getBookingId() != null) {
                        UserBooking existing = BookingStore.find(
                                        flowState.getBookingId(), currentUserEmail);
                        if (existing != null) {
                                showSuccess(existing);
                                return;
                        }
                }

                if (!flowState.hasTrip() || !flowState.hasSeat()
                                || !flowState.hasPassengerInformation()) {
                        showFailure("Some booking information is incomplete.", "booking-review");
                        return;
                }

                submit();
        }

        private void submit() {

                if (submitting) {
                        return;
                }

                submitting = true;
                showLoading();

                final CreateBookingRequest request = new CreateBookingRequest(
                                requestKey,
                                currentUserEmail,
                                flowState.getSelectedTrip().getTripId(),
                                flowState.getSelectedSeat(),
                                flowState.getPassengerName(),
                                flowState.getPassengerPhone(),
                                flowState.getPassengerEmail());

                new SwingWorker<UserBooking, Void>() {

                        @Override
                        protected UserBooking doInBackground() throws BookingException {
                                return BookingService.createBooking(request);
                        }

                        @Override
                        protected void done() {
                                submitting = false;
                                try {
                                        UserBooking booking = get();
                                        flowState.setBookingId(booking.getBookingId());
                                        showSuccess(booking);

                                } catch (java.util.concurrent.ExecutionException ex) {
                                        handleError(ex.getCause());

                                } catch (InterruptedException ex) {
                                        Thread.currentThread().interrupt();
                                        handleError(ex);
                                }
                        }
                }.execute();
        }

        private void handleError(Throwable error) {

                if (error instanceof BookingException be) {

                        LOG.log(Level.WARNING, "Booking rejected ({0}): {1}",
                                        new Object[] { be.getKind().httpStatus(), be.getMessage() });

                        switch (be.getKind()) {
                                case SEAT_UNAVAILABLE -> showFailure(
                                                "That seat is no longer available.\nPlease select another seat.",
                                                "seat-selection");
                                case UNAUTHENTICATED, FORBIDDEN -> showFailure(
                                                "Your session is not authorized to book. Please sign in again.",
                                                "booking-review");
                                case NOT_FOUND -> showFailure(
                                                "This trip is no longer available.",
                                                "trip-search");
                                case INVALID_REQUEST -> showFailure(
                                                be.getMessage(), "booking-review");
                                default -> showFailure(
                                                "Something went wrong. Please try again.",
                                                "booking-review");
                        }
                        return;
                }

                LOG.log(Level.SEVERE, "Unexpected booking failure", error);
                showFailure("Something went wrong. Please try again.", "booking-review");
        }

        // ---------------------------------------------------------------------
        // Views
        // ---------------------------------------------------------------------

        private void resetCard() {
                card.removeAll();
        }

        private void refreshCard() {
                card.revalidate();
                card.repaint();
        }

        private void showLoading() {

                resetCard();

                JLabel title = AppLabel.title("Creating booking...");
                title.setAlignmentX(CENTER_ALIGNMENT);

                JLabel subtitle = AppLabel.secondary("Please wait while we reserve your seat.");
                subtitle.setAlignmentX(CENTER_ALIGNMENT);

                card.add(title);
                card.add(Box.createVerticalStrut(5));
                card.add(subtitle);

                refreshCard();
        }

        private void showSuccess(UserBooking booking) {

                resetCard();

                JLabel check = new JLabel("✓");
                check.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 48));
                check.setForeground(BussinTheme.SUCCESS);
                check.setAlignmentX(CENTER_ALIGNMENT);

                JLabel title = AppLabel.title("Booking Confirmed");
                title.setAlignmentX(CENTER_ALIGNMENT);

                JLabel subtitle = AppLabel.secondary("Your BUSSIN booking has been created successfully.");
                subtitle.setAlignmentX(CENTER_ALIGNMENT);

                JLabel bookingId = new JLabel(booking.getBookingId());
                bookingId.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 30));
                bookingId.setForeground(BussinTheme.PRIMARY);
                bookingId.setAlignmentX(CENTER_ALIGNMENT);

                JLabel route = new JLabel(booking.getRoute());
                route.setFont(BussinTheme.BODY_MEDIUM);
                route.setForeground(BussinTheme.TEXT_PRIMARY);
                route.setAlignmentX(CENTER_ALIGNMENT);

                JLabel details = new JLabel(
                                booking.getDeparture()
                                                + "  •  Seat " + booking.getSeat()
                                                + "  •  ₱" + String.format("%,.2f", booking.getFare()));
                details.setFont(BussinTheme.BODY);
                details.setForeground(BussinTheme.TEXT_SECONDARY);
                details.setAlignmentX(CENTER_ALIGNMENT);

                JLabel status = new JLabel("Status: " + booking.getStatus());
                status.setFont(BussinTheme.SMALL_BOLD);
                status.setForeground(BussinTheme.SUCCESS);
                status.setAlignmentX(CENTER_ALIGNMENT);

                JPanel actions = actionsPanel();

                AppButton bookings = new AppButton("My Bookings");
                bookings.addActionListener(event -> navigationHandler.accept("bookings"));

                AppButton home = new AppButton("Back to Home", AppButton.Variant.SECONDARY);
                home.addActionListener(event -> {
                        flowState.reset();
                        navigationHandler.accept("dashboard");
                });

                actions.add(bookings);
                actions.add(home);

                card.add(check);
                card.add(Box.createVerticalStrut(8));
                card.add(title);
                card.add(Box.createVerticalStrut(5));
                card.add(subtitle);
                card.add(Box.createVerticalStrut(24));
                card.add(bookingId);
                card.add(Box.createVerticalStrut(20));
                card.add(route);
                card.add(Box.createVerticalStrut(5));
                card.add(details);
                card.add(Box.createVerticalStrut(24));
                card.add(status);
                card.add(Box.createVerticalStrut(24));
                card.add(actions);

                refreshCard();
        }

        private void showFailure(String message, String backRoute) {

                resetCard();

                JLabel mark = new JLabel("!");
                mark.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 48));
                mark.setForeground(BussinTheme.DANGER);
                mark.setAlignmentX(CENTER_ALIGNMENT);

                JLabel title = AppLabel.title("Booking could not be completed.");
                title.setAlignmentX(CENTER_ALIGNMENT);

                JPanel messagePanel = new JPanel();
                messagePanel.setOpaque(false);
                messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
                messagePanel.setAlignmentX(CENTER_ALIGNMENT);

                for (String line : message.split("\n")) {
                        JLabel label = AppLabel.secondary(line);
                        label.setAlignmentX(CENTER_ALIGNMENT);
                        messagePanel.add(label);
                }

                JPanel actions = actionsPanel();

                String backText = switch (backRoute) {
                        case "seat-selection" -> "Select Another Seat";
                        case "trip-search" -> "Back to Trips";
                        default -> "Back to Review";
                };

                AppButton back = new AppButton(backText);
                back.addActionListener(event -> navigationHandler.accept(backRoute));

                AppButton home = new AppButton("Back to Home", AppButton.Variant.SECONDARY);
                home.addActionListener(event -> {
                        flowState.reset();
                        navigationHandler.accept("dashboard");
                });

                actions.add(back);
                actions.add(home);

                card.add(mark);
                card.add(Box.createVerticalStrut(8));
                card.add(title);
                card.add(Box.createVerticalStrut(8));
                card.add(messagePanel);
                card.add(Box.createVerticalStrut(24));
                card.add(actions);

                refreshCard();
        }

        private JPanel actionsPanel() {
                JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
                actions.setOpaque(false);
                return actions;
        }
}
