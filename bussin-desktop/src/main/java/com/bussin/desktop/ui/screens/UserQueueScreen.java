package com.bussin.desktop.ui.screens;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.bussin.desktop.ui.components.AppBadge;
import com.bussin.desktop.ui.components.AppButton;
import com.bussin.desktop.ui.components.AppCard;
import com.bussin.desktop.ui.components.AppLabel;
import com.bussin.desktop.ui.components.PageContent;
import com.bussin.desktop.ui.flow.BookingStore;
import com.bussin.desktop.ui.flow.BookingStore.UserBooking;
import com.bussin.desktop.ui.theme.BussinTheme;

public class UserQueueScreen extends JPanel {

    private final String currentUserEmail;
    private final Consumer<String> navigationHandler;

    public UserQueueScreen(
            String currentUserEmail,
            Consumer<String> navigationHandler) {

        this.currentUserEmail = currentUserEmail == null
                ? ""
                : currentUserEmail.trim();

        this.navigationHandler = navigationHandler;

        initializeUI();
    }

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
                createQueueCard(),
                24);

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
                        "My Queue"));

        panel.add(
                Box.createVerticalStrut(5));

        panel.add(
                AppLabel.secondary(
                        "Track your boarding status and queue position."));

        return panel;
    }

    private JPanel createQueueCard() {

        List<UserBooking> bookings = BookingStore.getBookingsForUser(
                currentUserEmail);

        UserBooking active = null;

        for (UserBooking booking : bookings) {

            if ("Confirmed".equals(
                    booking.getStatus())
                    || "Pending".equals(
                            booking.getStatus())) {

                active = booking;
            }
        }

        if (active == null) {

            return createEmptyState();
        }

        AppCard card = new AppCard();

        card.setLayout(
                new BoxLayout(
                        card,
                        BoxLayout.Y_AXIS));

        JPanel top = new JPanel(
                new BorderLayout());

        top.setOpaque(false);

        JLabel bookingId = new JLabel(
                active.getBookingId());

        bookingId.setFont(
                BussinTheme.CARD_TITLE);

        bookingId.setForeground(
                BussinTheme.TEXT_PRIMARY);

        AppBadge badge = new AppBadge(
                "Confirmed",
                AppBadge.Status.SUCCESS);

        top.add(
                bookingId,
                BorderLayout.WEST);

        top.add(
                badge,
                BorderLayout.EAST);

        card.add(top);

        card.add(
                Box.createVerticalStrut(18));

        JLabel route = AppLabel.section(
                active.getRoute());

        route.setAlignmentX(
                CENTER_ALIGNMENT);

        card.add(route);

        card.add(
                Box.createVerticalStrut(8));

        JLabel schedule = new JLabel(
                active.getDeparture()
                        + " → "
                        + active.getArrival());

        schedule.setFont(
                BussinTheme.BODY_MEDIUM);

        schedule.setForeground(
                BussinTheme.TEXT_SECONDARY);

        schedule.setAlignmentX(
                CENTER_ALIGNMENT);

        card.add(schedule);

        card.add(
                Box.createVerticalStrut(22));

        JPanel position = new JPanel(
                new GridBagLayout());

        position.setOpaque(false);

        JLabel number = new JLabel("#3");

        number.setFont(
                new java.awt.Font(
                        "Segoe UI",
                        java.awt.Font.BOLD,
                        42));

        number.setForeground(
                BussinTheme.PRIMARY);

        position.add(number);

        card.add(position);

        JLabel positionLabel = new JLabel(
                "Estimated queue position");

        positionLabel.setFont(
                BussinTheme.SMALL);

        positionLabel.setForeground(
                BussinTheme.TEXT_MUTED);

        positionLabel.setAlignmentX(
                CENTER_ALIGNMENT);

        card.add(positionLabel);

        card.add(
                Box.createVerticalStrut(22));

        JPanel information = new JPanel();

        information.setOpaque(false);

        information.setLayout(
                new BoxLayout(
                        information,
                        BoxLayout.Y_AXIS));

        addInfo(
                information,
                "QUEUE STATUS",
                "Waiting");

        addInfo(
                information,
                "BUS",
                active.getBusNumber());

        addInfo(
                information,
                "SEAT",
                active.getSeat());

        addInfo(
                information,
                "BOOKING",
                active.getBookingId());

        card.add(information);

        card.add(
                Box.createVerticalStrut(22));

        JLabel note = new JLabel(
                "Please stay near the boarding area until your queue number is called.");

        note.setFont(
                BussinTheme.BODY);

        note.setForeground(
                BussinTheme.TEXT_SECONDARY);

        note.setAlignmentX(
                CENTER_ALIGNMENT);

        card.add(note);

        card.add(
                Box.createVerticalStrut(20));

        AppButton bookingsButton = new AppButton(
                "My Bookings",
                AppButton.Variant.SECONDARY);

        bookingsButton.setAlignmentX(
                CENTER_ALIGNMENT);

        bookingsButton.addActionListener(
                event -> navigationHandler.accept(
                        "bookings"));

        card.add(bookingsButton);

        return card;
    }

    private void addInfo(
            JPanel parent,
            String label,
            String value) {

        JPanel row = new JPanel(
                new BorderLayout());

        row.setOpaque(false);

        JLabel left = new JLabel(label);

        left.setFont(
                BussinTheme.SMALL_BOLD);

        left.setForeground(
                BussinTheme.TEXT_SECONDARY);

        JLabel right = new JLabel(value);

        right.setFont(
                BussinTheme.BODY);

        right.setForeground(
                BussinTheme.TEXT_PRIMARY);

        row.add(
                left,
                BorderLayout.WEST);

        row.add(
                right,
                BorderLayout.EAST);

        parent.add(row);

        parent.add(
                Box.createVerticalStrut(10));
    }

    private JPanel createEmptyState() {

        AppCard card = new AppCard();

        card.setLayout(
                new BoxLayout(
                        card,
                        BoxLayout.Y_AXIS));

        JLabel title = AppLabel.section(
                "No Active Queue");

        title.setAlignmentX(
                CENTER_ALIGNMENT);

        JLabel message = AppLabel.secondary(
                "You don't currently have a pending or confirmed booking.");

        message.setAlignmentX(
                CENTER_ALIGNMENT);

        AppButton bookings = new AppButton(
                "My Bookings");

        bookings.setAlignmentX(
                CENTER_ALIGNMENT);

        bookings.addActionListener(
                event -> navigationHandler.accept(
                        "bookings"));

        card.add(title);
        card.add(Box.createVerticalStrut(8));
        card.add(message);
        card.add(Box.createVerticalStrut(16));
        card.add(bookings);

        return card;
    }
}