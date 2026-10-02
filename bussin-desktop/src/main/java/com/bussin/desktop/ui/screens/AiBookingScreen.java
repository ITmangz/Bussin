package com.bussin.desktop.ui.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import com.bussin.desktop.services.AiChatApiService;
import com.bussin.desktop.services.AiChatApiService.AiChatResponse;
import com.bussin.desktop.services.AiChatApiService.BookingResult;
import com.bussin.desktop.services.AiChatApiService.TripOption;
import com.bussin.desktop.ui.components.AppButton;
import com.bussin.desktop.ui.components.AppLabel;
import com.bussin.desktop.ui.components.PageContent;
import com.bussin.desktop.ui.flow.BookingFlowState;
import com.bussin.desktop.ui.theme.BussinTheme;

public class AiBookingScreen extends JPanel {

    private final Consumer<String> navigationHandler;
    private final String currentUserEmail;
    private final BookingFlowState flowState;

    private final JPanel messagesPanel = new JPanel();

    private final JScrollPane scrollPane;

    private final JTextField messageField = new JTextField();

    private final AppButton sendButton = new AppButton("Send");

    public AiBookingScreen(
            Consumer<String> navigationHandler,
            String currentUserEmail,
            BookingFlowState flowState) {

        this.navigationHandler = navigationHandler;
        this.currentUserEmail = currentUserEmail;
        this.flowState = flowState;

        messagesPanel.setLayout(
                new BoxLayout(
                        messagesPanel,
                        BoxLayout.Y_AXIS));

        messagesPanel.setBackground(
                BussinTheme.BACKGROUND);

        messagesPanel.setBorder(
                new EmptyBorder(
                        20,
                        20,
                        20,
                        20));

        scrollPane = new JScrollPane(
                messagesPanel);

        scrollPane.setBorder(
                BorderFactory.createEmptyBorder());

        scrollPane.getVerticalScrollBar()
                .setUnitIncrement(16);

        initializeUI();

        addAssistantMessage(
                "Hi! I'm the BUSSIN AI Booking Assistant. "
                        + "Tell me where and when you want to travel, "
                        + "and I'll help you book your trip.");
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
                createChatCard(),
                20);

        add(
                page.inScrollPane(),
                BorderLayout.CENTER);
    }

    private JPanel createHeader() {

        JPanel header = new JPanel();

        header.setOpaque(false);

        header.setLayout(
                new BoxLayout(
                        header,
                        BoxLayout.Y_AXIS));

        header.add(
                AppLabel.title(
                        "AI Booking Assistant"));

        header.add(
                Box.createVerticalStrut(6));

        header.add(
                AppLabel.secondary(
                        "Book your BUSSIN trip through conversation."));

        return header;
    }

    private JPanel createChatCard() {

        JPanel card = new JPanel(
                new BorderLayout());

        card.setBackground(
                BussinTheme.SURFACE);

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BussinTheme.BORDER_DARK),
                        BorderFactory.createEmptyBorder(
                                10,
                                10,
                                10,
                                10)));

        card.setPreferredSize(
                new Dimension(
                        0,
                        560));

        card.add(
                scrollPane,
                BorderLayout.CENTER);

        card.add(
                createInputPanel(),
                BorderLayout.SOUTH);

        return card;
    }

    private JPanel createInputPanel() {

        JPanel panel = new JPanel(
                new BorderLayout(10, 0));

        panel.setOpaque(false);

        panel.setBorder(
                BorderFactory.createEmptyBorder(
                        10,
                        10,
                        10,
                        10));

        messageField.setPreferredSize(
                new Dimension(
                        0,
                        42));

        messageField.setFont(
                BussinTheme.BODY);

        messageField.addActionListener(
                event -> sendMessage());

        sendButton.setPreferredSize(
                new Dimension(
                        100,
                        42));

        sendButton.addActionListener(
                event -> sendMessage());

        panel.add(
                messageField,
                BorderLayout.CENTER);

        panel.add(
                sendButton,
                BorderLayout.EAST);

        return panel;
    }

    private void sendMessage() {

        String message = messageField.getText().trim();

        if (message.isBlank()) {
            return;
        }

        addUserMessage(message);

        messageField.setText("");

        setInputEnabled(false);

        new SwingWorker<AiChatResponse, Void>() {

            @Override
            protected AiChatResponse doInBackground()
                    throws Exception {

                return AiChatApiService.sendMessage(
                        message);
            }

            @Override
            protected void done() {

                try {

                    AiChatResponse response = get();

                    handleResponse(response);

                } catch (Exception exception) {

                    Throwable cause = exception.getCause();

                    String errorMessage = cause != null
                            ? cause.getMessage()
                            : exception.getMessage();

                    addAssistantMessage(
                            "Sorry, I couldn't process that request."
                                    + "\n\n"
                                    + errorMessage);

                } finally {

                    setInputEnabled(true);

                    messageField.requestFocusInWindow();
                }
            }

        }.execute();
    }

    private void handleResponse(
            AiChatResponse response) {

        if (response == null) {

            addAssistantMessage(
                    "The AI returned an empty response.");

            return;
        }

        String message = response.message();

        if (message != null
                && !message.isBlank()) {

            addAssistantMessage(message);
        }

        if (response.trips() != null
                && !response.trips().isEmpty()) {

            addTripOptions(
                    response.trips());
        }

        if (response.availableSeats() != null
                && !response.availableSeats().isEmpty()) {

            addSeatOptions(
                    response.availableSeats());
        }

        if (response.booking() != null) {

            showBookingCreated(
                    response.booking());
        }
    }

    private void addTripOptions(
            List<TripOption> trips) {

        for (TripOption trip : trips) {

            JPanel card = new JPanel();

            card.setLayout(
                    new BoxLayout(
                            card,
                            BoxLayout.Y_AXIS));

            card.setBackground(
                    BussinTheme.SURFACE);

            card.setBorder(
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(
                                    BussinTheme.BORDER_DARK),
                            BorderFactory.createEmptyBorder(
                                    12,
                                    14,
                                    12,
                                    14)));

            JLabel route = new JLabel(
                    trip.origin()
                            + " → "
                            + trip.destination());

            route.setFont(
                    BussinTheme.BODY);

            route.setForeground(
                    BussinTheme.TEXT_PRIMARY);

            JLabel details = new JLabel(
                    "Trip "
                            + trip.id()
                            + "  •  "
                            + trip.routeIdentifier()
                            + "  •  Bus "
                            + trip.busPlateNumber());

            details.setFont(
                    BussinTheme.BODY);

            details.setForeground(
                    BussinTheme.TEXT_SECONDARY);

            JLabel departure = new JLabel(
                    "Departure: "
                            + formatDateTime(
                                    trip.scheduledDeparture())
                            + "  •  Fare: ₱"
                            + String.format(
                                    "%.2f",
                                    trip.fare()));

            departure.setFont(
                    BussinTheme.BODY);

            departure.setForeground(
                    BussinTheme.TEXT_SECONDARY);

            AppButton selectButton = new AppButton(
                    "Book Trip "
                            + trip.id());

            selectButton.setAlignmentX(
                    LEFT_ALIGNMENT);

            selectButton.addActionListener(
                    event -> sendMessage(
                            "I want to book trip "
                                    + trip.id()));

            card.add(route);

            card.add(
                    Box.createVerticalStrut(4));

            card.add(details);

            card.add(
                    Box.createVerticalStrut(4));

            card.add(departure);

            card.add(
                    Box.createVerticalStrut(10));

            card.add(selectButton);

            messagesPanel.add(
                    Box.createVerticalStrut(10));

            messagesPanel.add(card);
        }

        refreshChat();
    }

    private void addSeatOptions(
            List<String> seats) {

        StringBuilder builder = new StringBuilder();

        builder.append(
                "Available seats: ");

        for (int i = 0; i < seats.size(); i++) {

            if (i > 0) {
                builder.append(", ");
            }

            builder.append(
                    seats.get(i));
        }

        addAssistantMessage(
                builder.toString());
    }

    private void showBookingCreated(
            BookingResult booking) {

        JPanel card = new JPanel();

        card.setLayout(
                new BoxLayout(
                        card,
                        BoxLayout.Y_AXIS));

        card.setBackground(
                BussinTheme.SURFACE);

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BussinTheme.BORDER_DARK),
                        BorderFactory.createEmptyBorder(
                                20,
                                20,
                                20,
                                20)));

        JLabel title = new JLabel(
                "Booking Confirmed");

        title.setFont(
                BussinTheme.BODY);

        title.setForeground(
                BussinTheme.TEXT_PRIMARY);

        JLabel reference = new JLabel(
                "Booking Reference: "
                        + booking.bookingReference());

        reference.setFont(
                BussinTheme.BODY);

        reference.setForeground(
                BussinTheme.TEXT_PRIMARY);

        JLabel trip = new JLabel(
                booking.origin()
                        + " → "
                        + booking.destination());

        trip.setFont(
                BussinTheme.BODY);

        trip.setForeground(
                BussinTheme.TEXT_SECONDARY);

        JLabel seat = new JLabel(
                "Seat: "
                        + booking.seatNumber()
                        + "  •  Fare: ₱"
                        + String.format(
                                "%.2f",
                                booking.fare()));

        seat.setFont(
                BussinTheme.BODY);

        seat.setForeground(
                BussinTheme.TEXT_SECONDARY);

        JLabel status = new JLabel(
                "Status: "
                        + booking.status()
                        + "  •  Payment: "
                        + booking.paymentStatus());

        status.setFont(
                BussinTheme.BODY);

        status.setForeground(
                BussinTheme.TEXT_SECONDARY);

        AppButton bookingsButton = new AppButton(
                "View My Bookings");

        bookingsButton.setAlignmentX(
                LEFT_ALIGNMENT);

        bookingsButton.addActionListener(
                event -> navigationHandler.accept(
                        "bookings-list"));

        card.add(title);

        card.add(
                Box.createVerticalStrut(10));

        card.add(reference);

        card.add(
                Box.createVerticalStrut(8));

        card.add(trip);

        card.add(
                Box.createVerticalStrut(4));

        card.add(seat);

        card.add(
                Box.createVerticalStrut(4));

        card.add(status);

        card.add(
                Box.createVerticalStrut(14));

        card.add(bookingsButton);

        messagesPanel.add(
                Box.createVerticalStrut(12));

        messagesPanel.add(card);

        refreshChat();
    }

    private void addUserMessage(
            String message) {

        addMessageBubble(
                message,
                true);
    }

    private void addAssistantMessage(
            String message) {

        addMessageBubble(
                message,
                false);
    }

    private void addMessageBubble(
            String message,
            boolean user) {

        JPanel wrapper = new JPanel(
                new FlowLayout(
                        user
                                ? FlowLayout.RIGHT
                                : FlowLayout.LEFT));

        wrapper.setOpaque(false);

        JLabel label = new JLabel(
                "<html>"
                        + escapeHtml(
                                message)
                                .replace(
                                        "\n",
                                        "<br>")
                        + "</html>");

        label.setFont(
                BussinTheme.BODY);

        label.setForeground(
                user
                        ? Color.WHITE
                        : BussinTheme.TEXT_PRIMARY);

        label.setBorder(
                BorderFactory.createEmptyBorder(
                        10,
                        14,
                        10,
                        14));

        label.setOpaque(true);

        label.setBackground(
                user
                        ? BussinTheme.PRIMARY
                        : BussinTheme.BACKGROUND);

        wrapper.add(label);

        messagesPanel.add(wrapper);

        refreshChat();
    }

    private void sendMessage(
            String message) {

        addUserMessage(message);

        setInputEnabled(false);

        new SwingWorker<AiChatResponse, Void>() {

            @Override
            protected AiChatResponse doInBackground()
                    throws Exception {

                return AiChatApiService.sendMessage(
                        message);
            }

            @Override
            protected void done() {

                try {

                    handleResponse(get());

                } catch (Exception exception) {

                    Throwable cause = exception.getCause();

                    addAssistantMessage(
                            cause != null
                                    ? cause.getMessage()
                                    : exception.getMessage());

                } finally {

                    setInputEnabled(true);
                }
            }

        }.execute();
    }

    private void setInputEnabled(
            boolean enabled) {

        messageField.setEnabled(
                enabled);

        sendButton.setEnabled(
                enabled);
    }

    private void refreshChat() {

        messagesPanel.revalidate();

        messagesPanel.repaint();

        SwingUtilities.invokeLater(() -> JScrollBarHelper.scrollToBottom(
                scrollPane));
    }

    private String formatDateTime(
            String value) {

        if (value == null
                || value.isBlank()) {

            return "Unknown";
        }

        return value.replace(
                "T",
                " ");
    }

    private String escapeHtml(
            String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private static final class JScrollBarHelper {

        private JScrollBarHelper() {
        }

        private static void scrollToBottom(
                JScrollPane scrollPane) {

            scrollPane.getVerticalScrollBar()
                    .setValue(
                            scrollPane
                                    .getVerticalScrollBar()
                                    .getMaximum());
        }
    }
}