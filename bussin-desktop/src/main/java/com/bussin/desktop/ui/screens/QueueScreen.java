package com.bussin.desktop.ui.screens;

import com.bussin.desktop.services.QueueApiService;
import com.bussin.desktop.services.QueueApiService.QueueResponse;
import com.bussin.desktop.services.TripApiService;
import com.bussin.desktop.services.TripApiService.TripResponse;
import com.bussin.desktop.ui.components.AppBadge;
import com.bussin.desktop.ui.components.AppButton;
import com.bussin.desktop.ui.components.AppCard;
import com.bussin.desktop.ui.components.AppLabel;
import com.bussin.desktop.ui.components.DataGrid;
import com.bussin.desktop.ui.components.IconFactory;
import com.bussin.desktop.ui.components.PageContent;
import com.bussin.desktop.ui.components.QueueCard;
import com.bussin.desktop.ui.components.ResponsiveLayouts;
import com.bussin.desktop.ui.components.SectionHeader;
import com.bussin.desktop.ui.components.StatCard;
import com.bussin.desktop.ui.theme.BussinTheme;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class QueueScreen extends JPanel {

    private static final String ALL_TRIPS = "All trips";
    private static final String ALL_STATUS = "All statuses";

    private static final String WAITING = "WAITING";
    private static final String CALLED = "CALLED";
    private static final String BOARDED = "BOARDED";
    private static final String CANCELLED = "CANCELLED";

    private static final DateTimeFormatter DISPLAY_TIME = DateTimeFormatter.ofPattern("hh:mm a");

    private final List<TripResponse> trips = new ArrayList<>();
    private final List<QueueResponse> entries = new ArrayList<>();
    private final Map<Long, TripResponse> tripMap = new HashMap<>();

    private final JTextField searchField = new JTextField();
    private final JComboBox<String> tripFilter = new JComboBox<>();
    private final JComboBox<String> statusFilter = new JComboBox<>(
            new String[] {
                    ALL_STATUS,
                    WAITING,
                    CALLED,
                    BOARDED,
                    CANCELLED
            });

    private final JPanel statsHolder = transparent();
    private final JPanel boardingHolder = transparent();
    private final JPanel waitingHolder = transparent();
    private final JPanel historyHolder = transparent();

    private final AppButton callNextButton = new AppButton("Call Next");

    public QueueScreen() {

        setBackground(BussinTheme.BACKGROUND);
        setLayout(new BorderLayout());

        PageContent page = new PageContent();

        page.addBlock(createHeader(), 0);
        page.addBlock(statsHolder, 24);
        page.addBlock(createFilterBar(), 18);
        page.addBlock(boardingHolder, 18);
        page.addBlock(waitingHolder, 18);
        page.addBlock(historyHolder, 18);

        add(
                page.inScrollPane(),
                BorderLayout.CENTER);

        loadData();
    }

    private JPanel createHeader() {

        JPanel header = new JPanel(new BorderLayout(24, 0));

        header.setOpaque(false);

        JPanel text = new JPanel();

        text.setOpaque(false);
        text.setLayout(
                new BoxLayout(
                        text,
                        BoxLayout.Y_AXIS));

        text.add(
                AppLabel.title(
                        "Queue Management"));

        text.add(
                Box.createVerticalStrut(4));

        text.add(
                AppLabel.secondary(
                        "Manage passenger queues, call passengers to board, and track boarding status."));

        AppButton refresh = new AppButton(
                "Refresh",
                AppButton.Variant.SECONDARY);

        refresh.setIcon(
                IconFactory.create(
                        "arrow-right",
                        16,
                        BussinTheme.TEXT_PRIMARY));

        refresh.addActionListener(
                e -> loadData());

        callNextButton.addActionListener(
                e -> callNext());

        JPanel actions = new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT,
                        10,
                        0));

        actions.setOpaque(false);

        actions.add(refresh);
        actions.add(callNextButton);

        JPanel actionHolder = new JPanel(
                new BorderLayout());

        actionHolder.setOpaque(false);

        actionHolder.add(
                actions,
                BorderLayout.SOUTH);

        header.add(
                text,
                BorderLayout.CENTER);

        header.add(
                actionHolder,
                BorderLayout.EAST);

        return header;
    }

    private JPanel createFilterBar() {

        AppCard card = new AppCard();

        card.setLayout(
                new ResponsiveLayouts.Grid(
                        4,
                        180,
                        12));

        searchField.setToolTipText(
                "Search by passenger or queue number");

        searchField.putClientProperty(
                "JTextField.placeholderText",
                "Search passenger or queue no.");

        searchField
                .getDocument()
                .addDocumentListener(
                        new DocumentListener() {

                            @Override
                            public void insertUpdate(
                                    DocumentEvent e) {
                                refreshView();
                            }

                            @Override
                            public void removeUpdate(
                                    DocumentEvent e) {
                                refreshView();
                            }

                            @Override
                            public void changedUpdate(
                                    DocumentEvent e) {
                                refreshView();
                            }
                        });

        tripFilter.addActionListener(
                e -> refreshView());

        statusFilter.addActionListener(
                e -> refreshView());

        AppButton clear = new AppButton(
                "Clear Filters",
                AppButton.Variant.SECONDARY);

        clear.addActionListener(
                e -> {
                    searchField.setText("");
                    tripFilter.setSelectedIndex(0);
                    statusFilter.setSelectedIndex(0);
                });

        card.add(searchField);
        card.add(tripFilter);
        card.add(statusFilter);
        card.add(clear);

        return card;
    }

    private void loadData() {

        new Thread(() -> {

            try {

                List<TripResponse> loadedTrips = TripApiService.getAllTrips();

                List<QueueResponse> loadedEntries = new ArrayList<>();

                for (TripResponse trip : loadedTrips) {

                    try {

                        loadedEntries.addAll(
                                QueueApiService.getTripQueue(
                                        trip.getId()));

                    } catch (Exception ignored) {
                    }
                }

                javax.swing.SwingUtilities.invokeLater(
                        () -> {

                            trips.clear();
                            trips.addAll(
                                    loadedTrips);

                            entries.clear();
                            entries.addAll(
                                    loadedEntries);

                            rebuildTripFilter();
                            rebuildTripMap();
                            refreshView();
                        });

            } catch (Exception ex) {

                javax.swing.SwingUtilities.invokeLater(
                        () -> showError(
                                "Failed to load queue data",
                                ex));
            }

        }).start();
    }

    private void rebuildTripMap() {

        tripMap.clear();

        for (TripResponse trip : trips) {

            if (trip.getId() != null) {
                tripMap.put(
                        trip.getId(),
                        trip);
            }
        }
    }

    private void rebuildTripFilter() {

        Object previous = tripFilter.getSelectedItem();

        tripFilter.removeAllItems();

        tripFilter.addItem(
                ALL_TRIPS);

        for (TripResponse trip : trips) {

            tripFilter.addItem(
                    tripLabel(trip));
        }

        if (previous != null) {

            for (int i = 0; i < tripFilter.getItemCount(); i++) {

                if (previous.equals(
                        tripFilter.getItemAt(i))) {

                    tripFilter.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void refreshView() {

        int waiting = 0;
        int called = 0;
        int boarded = 0;
        int cancelled = 0;

        TripResponse selected = selectedTrip();

        for (QueueResponse entry : entries) {

            if (selected != null
                    && !selected.getId()
                            .equals(entry.getTripId())) {
                continue;
            }

            switch (entry.getStatus()) {

                case WAITING ->
                    waiting++;

                case CALLED ->
                    called++;

                case BOARDED ->
                    boarded++;

                case CANCELLED ->
                    cancelled++;

                default -> {
                }
            }
        }

        statsHolder.removeAll();

        statsHolder.setLayout(
                new ResponsiveLayouts.Grid(
                        4,
                        200,
                        14));

        statsHolder.add(
                new StatCard(
                        "WAITING",
                        String.valueOf(waiting),
                        "in line",
                        "queue"));

        statsHolder.add(
                new StatCard(
                        "CALLED",
                        String.valueOf(called),
                        "called to bus",
                        "bus"));

        statsHolder.add(
                new StatCard(
                        "BOARDED",
                        String.valueOf(boarded),
                        "boarded",
                        "seat"));

        statsHolder.add(
                new StatCard(
                        "CANCELLED",
                        String.valueOf(cancelled),
                        "cancelled",
                        "users"));

        List<QueueResponse> visible = new ArrayList<>();

        for (QueueResponse entry : entries) {

            if (matches(entry)) {
                visible.add(entry);
            }
        }

        renderBoarding(visible);
        renderWaiting(visible);
        renderHistory(visible);

        callNextButton.setEnabled(
                nextWaiting() != null);

        revalidate();
        repaint();
    }

    private void renderBoarding(
            List<QueueResponse> visible) {

        List<QueueResponse> boarding = filterByStatus(
                visible,
                CALLED);

        boardingHolder.removeAll();

        boardingHolder.setLayout(
                new BorderLayout());

        AppCard card = new AppCard();

        card.setLayout(
                new BorderLayout(
                        0,
                        14));

        card.add(
                new SectionHeader(
                        "Now Boarding",
                        boarding.size()
                                + " passenger(s) called to the bus"),
                BorderLayout.NORTH);

        if (boarding.isEmpty()) {

            card.add(
                    emptyState(
                            "No passengers are currently called to board."),
                    BorderLayout.CENTER);

        } else {

            JPanel grid = new JPanel(
                    new ResponsiveLayouts.Grid(
                            3,
                            280,
                            14));

            grid.setOpaque(false);

            for (QueueResponse entry : boarding) {

                JPanel holder = new JPanel(
                        new BorderLayout(
                                0,
                                10));

                holder.setOpaque(false);

                TripResponse trip = tripMap.get(
                        entry.getTripId());

                holder.add(
                        new QueueCard(
                                queueNumber(entry),
                                safe(entry.getCommuterName()),
                                tripLabel(trip),
                                displayStatus(entry.getStatus()),
                                countFor(
                                        entry.getTripId(),
                                        WAITING),
                                countFor(
                                        entry.getTripId(),
                                        CALLED),
                                countFor(
                                        entry.getTripId(),
                                        BOARDED)),
                        BorderLayout.CENTER);

                AppButton complete = small(
                        "Complete",
                        AppButton.Variant.PRIMARY);

                complete.addActionListener(
                        ev -> updateStatus(
                                entry,
                                BOARDED));

                AppButton cancel = small(
                        "Cancel",
                        AppButton.Variant.SECONDARY);

                cancel.addActionListener(
                        ev -> cancelEntry(entry));

                JPanel actions = new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                8,
                                0));

                actions.setOpaque(false);

                actions.add(complete);
                actions.add(cancel);

                holder.add(
                        actions,
                        BorderLayout.SOUTH);

                grid.add(holder);
            }

            card.add(
                    grid,
                    BorderLayout.CENTER);
        }

        boardingHolder.add(
                card,
                BorderLayout.CENTER);
    }

    private void renderWaiting(
            List<QueueResponse> visible) {

        List<QueueResponse> waiting = filterByStatus(
                visible,
                WAITING);

        waiting.sort(
                (a, b) -> Integer.compare(
                        safeQueueNumber(a),
                        safeQueueNumber(b)));

        waitingHolder.removeAll();

        waitingHolder.setLayout(
                new BorderLayout());

        AppCard card = new AppCard();

        card.setLayout(
                new BorderLayout(
                        0,
                        10));

        card.add(
                new SectionHeader(
                        "Waiting Queue",
                        waiting.size()
                                + " passenger(s) waiting, ordered by queue position"),
                BorderLayout.NORTH);

        if (waiting.isEmpty()) {

            card.add(
                    emptyState(
                            "No waiting passengers match the current filters."),
                    BorderLayout.CENTER);

        } else {

            DataGrid grid = new DataGrid(
                    new String[] {
                            "Pos.",
                            "Queue No.",
                            "Passenger",
                            "Trip",
                            "Joined",
                            "Actions"
                    },
                    new double[] {
                            0.4,
                            0.8,
                            1.6,
                            2.0,
                            1.0,
                            1.7
                    });

            for (QueueResponse entry : waiting) {

                AppButton call = small(
                        "Call",
                        AppButton.Variant.PRIMARY);

                call.addActionListener(
                        ev -> updateStatus(
                                entry,
                                CALLED));

                AppButton cancel = small(
                        "Cancel",
                        AppButton.Variant.SECONDARY);

                cancel.addActionListener(
                        ev -> cancelEntry(entry));

                JPanel actions = new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                6,
                                0));

                actions.setOpaque(false);

                actions.add(call);
                actions.add(cancel);

                TripResponse trip = tripMap.get(
                        entry.getTripId());

                grid.addRow(
                        DataGrid.colored(
                                String.valueOf(
                                        positionOf(entry)),
                                BussinTheme.RED),

                        DataGrid.strong(
                                queueNumber(entry)),

                        passengerCell(entry),

                        DataGrid.text(
                                tripLabel(trip)),

                        DataGrid.text(
                                formatTime(
                                        entry.getJoinedAt())),

                        actions);
            }

            card.add(
                    grid,
                    BorderLayout.CENTER);
        }

        waitingHolder.add(
                card,
                BorderLayout.CENTER);
    }

    private void renderHistory(
            List<QueueResponse> visible) {

        List<QueueResponse> history = new ArrayList<>();

        for (QueueResponse entry : visible) {

            if (BOARDED.equals(
                    entry.getStatus())
                    || CANCELLED.equals(
                            entry.getStatus())) {

                history.add(entry);
            }
        }

        historyHolder.removeAll();

        historyHolder.setLayout(
                new BorderLayout());

        AppCard card = new AppCard();

        card.setLayout(
                new BorderLayout(
                        0,
                        10));

        card.add(
                new SectionHeader(
                        "Completed & Cancelled",
                        history.size()
                                + " entr"
                                + (history.size() == 1
                                        ? "y"
                                        : "ies")),
                BorderLayout.NORTH);

        if (history.isEmpty()) {

            card.add(
                    emptyState(
                            "No completed or cancelled passengers match the current filters."),
                    BorderLayout.CENTER);

        } else {

            DataGrid grid = new DataGrid(
                    new String[] {
                            "Queue No.",
                            "Passenger",
                            "Trip",
                            "Joined",
                            "Status"
                    },
                    new double[] {
                            0.8,
                            1.6,
                            2.0,
                            1.0,
                            1.2
                    });

            for (QueueResponse entry : history) {

                TripResponse trip = tripMap.get(
                        entry.getTripId());

                AppBadge badge = new AppBadge(
                        displayStatus(
                                entry.getStatus()),
                        BOARDED.equals(
                                entry.getStatus())
                                        ? AppBadge.Status.SUCCESS
                                        : AppBadge.Status.DANGER);

                grid.addRow(
                        DataGrid.strong(
                                queueNumber(entry)),

                        passengerCell(entry),

                        DataGrid.text(
                                tripLabel(trip)),

                        DataGrid.text(
                                formatTime(
                                        entry.getJoinedAt())),

                        badge);
            }

            card.add(
                    grid,
                    BorderLayout.CENTER);
        }

        historyHolder.add(
                card,
                BorderLayout.CENTER);
    }

    private void callNext() {

        QueueResponse next = nextWaiting();

        if (next != null) {
            updateStatus(
                    next,
                    CALLED);
        }
    }

    private QueueResponse nextWaiting() {

        TripResponse selected = selectedTrip();

        QueueResponse result = null;

        for (QueueResponse entry : entries) {

            if (!WAITING.equals(
                    entry.getStatus())) {
                continue;
            }

            if (selected != null
                    && !selected.getId()
                            .equals(entry.getTripId())) {
                continue;
            }

            if (result == null
                    || safeQueueNumber(entry) < safeQueueNumber(result)) {

                result = entry;
            }
        }

        return result;
    }

    private void updateStatus(
            QueueResponse entry,
            String status) {

        new Thread(() -> {

            try {

                QueueResponse updated = QueueApiService.updateQueueStatus(
                        entry.getId(),
                        status);

                javax.swing.SwingUtilities.invokeLater(
                        () -> {

                            replaceEntry(
                                    updated);

                            refreshView();
                        });

            } catch (Exception ex) {

                javax.swing.SwingUtilities.invokeLater(
                        () -> showError(
                                "Failed to update queue status",
                                ex));
            }

        }).start();
    }

    private void cancelEntry(
            QueueResponse entry) {

        new Thread(() -> {

            try {

                QueueResponse updated = QueueApiService.cancelQueueEntry(
                        entry.getId());

                javax.swing.SwingUtilities.invokeLater(
                        () -> {

                            replaceEntry(
                                    updated);

                            refreshView();
                        });

            } catch (Exception ex) {

                javax.swing.SwingUtilities.invokeLater(
                        () -> showError(
                                "Failed to cancel queue entry",
                                ex));
            }

        }).start();
    }

    private void replaceEntry(
            QueueResponse updated) {

        for (int i = 0; i < entries.size(); i++) {

            QueueResponse current = entries.get(i);

            if (current.getId()
                    .equals(updated.getId())) {

                entries.set(
                        i,
                        updated);

                return;
            }
        }

        entries.add(updated);
    }

    private boolean matches(
            QueueResponse entry) {

        TripResponse selected = selectedTrip();

        if (selected != null
                && !selected.getId()
                        .equals(entry.getTripId())) {

            return false;
        }

        Object status = statusFilter.getSelectedItem();

        if (status != null
                && !ALL_STATUS.equals(status)
                && !status.equals(
                        entry.getStatus())) {

            return false;
        }

        String q = searchField
                .getText()
                .trim()
                .toLowerCase();

        if (q.isEmpty()) {
            return true;
        }

        String name = safe(entry.getCommuterName())
                .toLowerCase();

        String number = queueNumber(entry)
                .toLowerCase();

        return name.contains(q)
                || number.contains(q);
    }

    private TripResponse selectedTrip() {

        int index = tripFilter.getSelectedIndex();

        if (index <= 0) {
            return null;
        }

        if (index - 1 >= trips.size()) {
            return null;
        }

        return trips.get(
                index - 1);
    }

    private List<QueueResponse> filterByStatus(
            List<QueueResponse> source,
            String status) {

        List<QueueResponse> result = new ArrayList<>();

        for (QueueResponse entry : source) {

            if (status.equals(
                    entry.getStatus())) {

                result.add(entry);
            }
        }

        return result;
    }

    private int countFor(
            Long tripId,
            String status) {

        int count = 0;

        for (QueueResponse entry : entries) {

            if (tripId.equals(
                    entry.getTripId())
                    && status.equals(
                            entry.getStatus())) {

                count++;
            }
        }

        return count;
    }

    private int positionOf(
            QueueResponse target) {

        int position = 1;

        for (QueueResponse entry : entries) {

            if (!target.getTripId()
                    .equals(entry.getTripId())) {

                continue;
            }

            if (!WAITING.equals(
                    entry.getStatus())) {

                continue;
            }

            if (safeQueueNumber(entry) < safeQueueNumber(target)) {

                position++;
            }
        }

        return position;
    }

    private Component passengerCell(
            QueueResponse entry) {

        JPanel cell = transparent();

        cell.setLayout(
                new BoxLayout(
                        cell,
                        BoxLayout.Y_AXIS));

        JLabel name = DataGrid.strong(
                safe(entry.getCommuterName()));

        String email = safe(entry.getCommuterEmail());

        JLabel contact = new JLabel(email);

        contact.setFont(
                BussinTheme.SMALL);

        contact.setForeground(
                BussinTheme.TEXT_MUTED);

        cell.add(name);
        cell.add(contact);

        return cell;
    }

    private AppButton small(
            String text,
            AppButton.Variant variant) {

        AppButton button = new AppButton(
                text,
                variant);

        button.setBorder(
                BorderFactory.createEmptyBorder(
                        6,
                        12,
                        6,
                        12));

        return button;
    }

    private JPanel emptyState(
            String message) {

        JPanel panel = new JPanel(
                new GridBagLayout());

        panel.setOpaque(false);

        panel.setBorder(
                BorderFactory.createEmptyBorder(
                        24,
                        0,
                        24,
                        0));

        JLabel label = new JLabel(message);

        label.setFont(
                BussinTheme.BODY);

        label.setForeground(
                BussinTheme.TEXT_MUTED);

        panel.add(label);

        return panel;
    }

    private void showError(
            String title,
            Exception ex) {

        javax.swing.JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                title,
                javax.swing.JOptionPane.ERROR_MESSAGE);
    }

    private String tripLabel(
            TripResponse trip) {

        if (trip == null) {
            return "Unknown Trip";
        }

        String route = safe(
                trip.getRouteIdentifier());

        String bus = safe(
                trip.getBusPlateNumber());

        String departure = formatTime(
                trip.getScheduledDeparture());

        return departure
                + " · "
                + route
                + " · "
                + bus;
    }

    private String formatTime(
            LocalDateTime value) {

        if (value == null) {
            return "—";
        }

        return value.format(
                DISPLAY_TIME);
    }

    private String queueNumber(
            QueueResponse entry) {

        if (entry.getQueueNumber() == null) {
            return "—";
        }

        return "#" + entry.getQueueNumber();
    }

    private int safeQueueNumber(
            QueueResponse entry) {

        return entry.getQueueNumber() == null
                ? Integer.MAX_VALUE
                : entry.getQueueNumber();
    }

    private String displayStatus(
            String status) {

        return switch (status) {

            case WAITING ->
                "Waiting";

            case CALLED ->
                "Boarding";

            case BOARDED ->
                "Completed";

            case CANCELLED ->
                "Cancelled";

            default ->
                status;
        };
    }

    private static String safe(
            String value) {

        return value == null
                ? ""
                : value;
    }

    private static JPanel transparent() {

        JPanel panel = new JPanel();

        panel.setOpaque(false);

        return panel;
    }
}