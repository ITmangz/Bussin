package com.bussin.desktop.ui.screens;

import com.bussin.desktop.ui.components.AppBadge;
import com.bussin.desktop.ui.components.AppButton;
import com.bussin.desktop.ui.components.AppCard;
import com.bussin.desktop.ui.components.AppLabel;
import com.bussin.desktop.ui.components.PageContent;
import com.bussin.desktop.ui.flow.BookingFlowState;
import com.bussin.desktop.ui.flow.BookingStore;
import com.bussin.desktop.ui.flow.BookingStore.UserBooking;
import com.bussin.desktop.ui.theme.BussinTheme;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class UserBookingsScreen extends JPanel {

    private final String currentUserEmail;
    private final Consumer<String> navigationHandler;
    private final BookingFlowState flowState;

    private final JPanel bookingsPanel = new JPanel();

    public UserBookingsScreen(
            String currentUserEmail,
            Consumer<String> navigationHandler,
            BookingFlowState flowState) {

        this.currentUserEmail = currentUserEmail == null
                ? ""
                : currentUserEmail.trim();

        this.navigationHandler = navigationHandler;

        this.flowState = flowState;

        initializeUI();
        refresh();
    }

    private void initializeUI() {

        setBackground(BussinTheme.BACKGROUND);
        setLayout(new BorderLayout());

        PageContent page = new PageContent();

        page.addBlock(
                createHeader(),
                0);

        page.addBlock(
                bookingsPanel,
                20);

        add(
                page.inScrollPane(),
                BorderLayout.CENTER);
    }

    private JPanel createHeader() {

        JPanel header = new JPanel(
                new BorderLayout());

        header.setOpaque(false);

        JPanel text = new JPanel();

        text.setOpaque(false);

        text.setLayout(
                new BoxLayout(
                        text,
                        BoxLayout.Y_AXIS));

        text.add(
                AppLabel.title(
                        "My Bookings"));

        text.add(
                Box.createVerticalStrut(5));

        text.add(
                AppLabel.secondary(
                        "View and manage your BUSSIN passenger bookings."));

        AppButton findTrip = new AppButton(
                "Find a Trip");

        findTrip.addActionListener(
                event -> navigationHandler.accept(
                        "trip-search"));

        header.add(
                text,
                BorderLayout.WEST);

        header.add(
                findTrip,
                BorderLayout.EAST);

        return header;
    }

    public void refresh() {

        bookingsPanel.removeAll();

        bookingsPanel.setOpaque(false);

        bookingsPanel.setLayout(
                new BoxLayout(
                        bookingsPanel,
                        BoxLayout.Y_AXIS));

        List<UserBooking> bookings = BookingStore.getBookingsForUser(
                currentUserEmail);

        if (bookings.isEmpty()) {

            bookingsPanel.add(
                    createEmptyState());

        } else {

            for (UserBooking booking : bookings) {

                bookingsPanel.add(
                        createBookingCard(
                                booking));

                bookingsPanel.add(
                        Box.createVerticalStrut(12));
            }
        }

        bookingsPanel.revalidate();
        bookingsPanel.repaint();
    }

    private JPanel createBookingCard(
            UserBooking booking) {

        AppCard card = new AppCard();

        card.setLayout(
                new BorderLayout(
                        16,
                        12));

        JPanel info = new JPanel();

        info.setOpaque(false);

        info.setLayout(
                new BoxLayout(
                        info,
                        BoxLayout.Y_AXIS));

        JPanel title = new JPanel(
                new BorderLayout());

        title.setOpaque(false);

        JLabel bookingId = new JLabel(
                booking.getBookingId());

        bookingId.setFont(
                BussinTheme.CARD_TITLE);

        bookingId.setForeground(
                BussinTheme.TEXT_PRIMARY);

        AppBadge badge = createBadge(
                booking.getStatus());

        title.add(
                bookingId,
                BorderLayout.WEST);

        title.add(
                badge,
                BorderLayout.EAST);

        info.add(title);

        info.add(
                Box.createVerticalStrut(8));

        JLabel route = new JLabel(
                booking.getRoute());

        route.setFont(
                BussinTheme.BODY_MEDIUM);

        route.setForeground(
                BussinTheme.TEXT_PRIMARY);

        info.add(route);

        info.add(
                Box.createVerticalStrut(4));

        JLabel details = new JLabel(
                booking.getDeparture()
                        + " → "
                        + booking.getArrival()
                        + "   •   "
                        + booking.getBusNumber()
                        + "   •   Seat "
                        + booking.getSeat());

        details.setFont(
                BussinTheme.SMALL);

        details.setForeground(
                BussinTheme.TEXT_SECONDARY);

        info.add(details);

        info.add(
                Box.createVerticalStrut(4));

        JLabel fare = new JLabel(
                String.format(
                        "₱%,.2f",
                        booking.getFare()));

        fare.setFont(
                BussinTheme.SMALL_BOLD);

        fare.setForeground(
                BussinTheme.TEXT_PRIMARY);

        info.add(fare);

        card.add(
                info,
                BorderLayout.CENTER);

        JPanel actions = new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT,
                        8,
                        0));

        actions.setOpaque(false);

        if ("Confirmed".equals(
                booking.getStatus())
                || "Pending".equals(
                        booking.getStatus())) {

            AppButton queue = new AppButton(
                    "View Queue",
                    AppButton.Variant.SECONDARY);

            queue.addActionListener(
                    event -> {

                        flowState.reset();

                        navigationHandler.accept(
                                "queue");
                    });

            actions.add(queue);

            AppButton cancel = new AppButton(
                    "Cancel",
                    AppButton.Variant.DANGER);

            cancel.addActionListener(
                    event -> cancelBooking(
                            booking));

            actions.add(cancel);
        }

        card.add(
                actions,
                BorderLayout.SOUTH);

        return card;
    }

    private AppBadge createBadge(
            String status) {

        AppBadge.Status badgeStatus = switch (status) {

            case "Confirmed" ->
                AppBadge.Status.SUCCESS;

            case "Pending" ->
                AppBadge.Status.WARNING;

            case "Cancelled" ->
                AppBadge.Status.DANGER;

            case "Completed" ->
                AppBadge.Status.INFO;

            default ->
                AppBadge.Status.NEUTRAL;
        };

        return new AppBadge(
                status,
                badgeStatus);
    }

    private void cancelBooking(
            UserBooking booking) {

        int result = JOptionPane.showConfirmDialog(
                this,
                "Cancel "
                        + booking.getBookingId()
                        + "?"
                        + "\n"
                        + booking.getRoute(),
                "Cancel Booking",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (result != JOptionPane.YES_OPTION) {

            return;
        }

        if (BookingStore.cancelBooking(
                booking.getBookingId(),
                currentUserEmail)) {

            refresh();

            JOptionPane.showMessageDialog(
                    this,
                    "Your booking was cancelled.",
                    "Booking Cancelled",
                    JOptionPane.INFORMATION_MESSAGE);

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "This booking cannot be cancelled.",
                    "Unable to Cancel",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private JPanel createEmptyState() {

        AppCard card = new AppCard();

        card.setLayout(
                new BoxLayout(
                        card,
                        BoxLayout.Y_AXIS));

        JLabel title = AppLabel.section(
                "No bookings yet");

        title.setAlignmentX(
                CENTER_ALIGNMENT);

        JLabel message = AppLabel.secondary(
                "Find a trip and create your first BUSSIN booking.");

        message.setAlignmentX(
                CENTER_ALIGNMENT);

        AppButton find = new AppButton(
                "Find a Trip");

        find.setAlignmentX(
                CENTER_ALIGNMENT);

        find.addActionListener(
                event -> navigationHandler.accept(
                        "trip-search"));

        card.add(title);
        card.add(Box.createVerticalStrut(6));
        card.add(message);
        card.add(Box.createVerticalStrut(16));
        card.add(find);

        return card;
    }
}