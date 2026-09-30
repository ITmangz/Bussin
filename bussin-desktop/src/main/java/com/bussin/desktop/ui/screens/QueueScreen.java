package com.bussin.desktop.ui.screens;

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
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Queue Management (frontend only, mock data).
 *
 * Passengers queue per trip. Staff call the next waiting passenger to board,
 * complete boarding, or skip a no-show. Statuses: Waiting, Boarding,
 * Completed, Skipped.
 */
public class QueueScreen extends JPanel {

    private static final String ALL_TRIPS = "All trips";
    private static final String ALL_STATUS = "All statuses";

    private static final String WAITING = "Waiting";
    private static final String BOARDING = "Boarding";
    private static final String COMPLETED = "Completed";
    private static final String SKIPPED = "Skipped";

    private final List<Trip> trips = new ArrayList<>();
    private final List<QueueEntry> entries = new ArrayList<>();

    private final JTextField searchField = new JTextField();
    private final JComboBox<String> tripFilter = new JComboBox<>();
    private final JComboBox<String> statusFilter = new JComboBox<>(new String[] {
            ALL_STATUS, WAITING, BOARDING, COMPLETED, SKIPPED });

    private final JPanel statsHolder = transparent();
    private final JPanel boardingHolder = transparent();
    private final JPanel waitingHolder = transparent();
    private final JPanel historyHolder = transparent();

    private final AppButton callNextButton = new AppButton("Call Next");

    public QueueScreen() {

        initializeData();

        setBackground(BussinTheme.BACKGROUND);
        setLayout(new BorderLayout());

        PageContent page = new PageContent();

        page.addBlock(createHeader(), 0);
        page.addBlock(statsHolder, 24);
        page.addBlock(createFilterBar(), 18);
        page.addBlock(boardingHolder, 18);
        page.addBlock(waitingHolder, 18);
        page.addBlock(historyHolder, 18);

        add(page.inScrollPane(), BorderLayout.CENTER);

        refresh();
    }

    // ================================================================
    // MOCK DATA
    // ================================================================

    private void initializeData() {

        Trip t1 = new Trip("10:00 AM", "Manila → Batangas", "BUS-101", "A");
        Trip t2 = new Trip("10:30 AM", "Manila → Lucena", "BUS-104", "B");
        Trip t3 = new Trip("11:00 AM", "Manila → Nasugbu", "BUS-107", "C");

        trips.add(t1);
        trips.add(t2);
        trips.add(t3);

        tripFilter.addItem(ALL_TRIPS);
        for (Trip t : trips) {
            tripFilter.addItem(t.label());
        }

        add(t1, "A-021", "Mark Villanueva", "0917 555 0121", "Regular", "08:58 AM", COMPLETED);
        add(t1, "A-022", "Sofia Ramos", "0918 555 0122", "Student", "09:05 AM", COMPLETED);
        add(t1, "A-023", "Pedro Garcia", "0920 555 0123", "Regular", "09:10 AM", BOARDING);
        add(t1, "A-020", "Daniel Flores", "0917 555 0120", "Senior", "08:50 AM", SKIPPED);
        add(t1, "A-024", "Juan Dela Cruz", "0919 555 0124", "Regular", "09:15 AM", WAITING);
        add(t1, "A-025", "Maria Santos", "0927 555 0125", "PWD", "09:18 AM", WAITING);
        add(t1, "A-026", "Carlo Reyes", "0917 555 0126", "Regular", "09:21 AM", WAITING);

        add(t2, "B-011", "Liza Mendoza", "0921 555 0211", "Regular", "09:30 AM", BOARDING);
        add(t2, "B-012", "Rico Bautista", "0917 555 0212", "Student", "09:36 AM", WAITING);
        add(t2, "B-013", "Angela Cruz", "0935 555 0213", "Regular", "09:41 AM", WAITING);

        add(t3, "C-004", "Nina Aquino", "0917 555 0304", "Senior", "09:44 AM", COMPLETED);
        add(t3, "C-005", "Paolo Navarro", "0908 555 0305", "Regular", "09:50 AM", WAITING);
    }

    private void add(Trip trip, String number, String passenger, String contact,
            String fareType, String joined, String status) {

        entries.add(new QueueEntry(trip, number, passenger, contact, fareType, joined, status));
    }

    // ================================================================
    // HEADER + FILTERS
    // ================================================================

    private JPanel createHeader() {

        JPanel header = new JPanel(new BorderLayout(24, 0));
        header.setOpaque(false);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(AppLabel.title("Queue Management"));
        text.add(Box.createVerticalStrut(4));
        text.add(AppLabel.secondary(
                "Call passengers to board, track queue position per trip, and clear no-shows."));

        AppButton refresh = new AppButton("Refresh", AppButton.Variant.SECONDARY);
        refresh.setIcon(IconFactory.create("arrow-right", 16, BussinTheme.TEXT_PRIMARY));
        refresh.addActionListener(e -> refresh());

        callNextButton.addActionListener(e -> callNext());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        actions.add(refresh);
        actions.add(callNextButton);

        JPanel actionHolder = new JPanel(new BorderLayout());
        actionHolder.setOpaque(false);
        actionHolder.add(actions, BorderLayout.SOUTH);

        header.add(text, BorderLayout.CENTER);
        header.add(actionHolder, BorderLayout.EAST);

        return header;
    }

    private JPanel createFilterBar() {

        AppCard card = new AppCard();
        card.setLayout(new ResponsiveLayouts.Grid(4, 180, 12));

        searchField.setToolTipText("Search by passenger or queue number");
        searchField.putClientProperty("JTextField.placeholderText", "Search passenger or queue no.");
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { refresh(); }
            @Override public void removeUpdate(DocumentEvent e) { refresh(); }
            @Override public void changedUpdate(DocumentEvent e) { refresh(); }
        });

        tripFilter.addActionListener(e -> refresh());
        statusFilter.addActionListener(e -> refresh());

        AppButton clear = new AppButton("Clear Filters", AppButton.Variant.SECONDARY);
        clear.addActionListener(e -> {
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

    // ================================================================
    // REFRESH / RENDER
    // ================================================================

    private void refresh() {

        // Statistics reflect the trip filter only, so the numbers stay meaningful
        // while the user narrows the lists by status or search.
        Trip selectedTrip = selectedTrip();

        int waiting = 0, boarding = 0, completed = 0, skipped = 0;

        for (QueueEntry e : entries) {
            if (selectedTrip != null && e.trip != selectedTrip) {
                continue;
            }
            switch (e.status) {
                case WAITING -> waiting++;
                case BOARDING -> boarding++;
                case COMPLETED -> completed++;
                case SKIPPED -> skipped++;
                default -> { }
            }
        }

        statsHolder.removeAll();
        statsHolder.setLayout(new ResponsiveLayouts.Grid(4, 200, 14));
        statsHolder.add(new StatCard("WAITING", String.valueOf(waiting),
                "in line", "queue"));
        statsHolder.add(new StatCard("BOARDING", String.valueOf(boarding),
                "called to bus", "bus"));
        statsHolder.add(new StatCard("COMPLETED", String.valueOf(completed),
                "boarded today", "seat"));
        statsHolder.add(new StatCard("SKIPPED", String.valueOf(skipped),
                "no-shows", "users"));

        List<QueueEntry> visible = new ArrayList<>();
        for (QueueEntry e : entries) {
            if (matches(e)) {
                visible.add(e);
            }
        }

        renderBoarding(visible);
        renderWaiting(visible);
        renderHistory(visible);

        callNextButton.setEnabled(nextWaiting() != null);

        revalidate();
        repaint();
    }

    private void renderBoarding(List<QueueEntry> visible) {

        List<QueueEntry> boarding = filterByStatus(visible, BOARDING);

        boardingHolder.removeAll();
        boardingHolder.setLayout(new BorderLayout());

        AppCard card = new AppCard();
        card.setLayout(new BorderLayout(0, 14));
        card.add(new SectionHeader("Now Boarding",
                boarding.size() + " passenger(s) called to the bus"), BorderLayout.NORTH);

        if (boarding.isEmpty()) {

            card.add(emptyState("No passengers are boarding. Use Call Next to start boarding."),
                    BorderLayout.CENTER);

        } else {

            JPanel grid = new JPanel(new ResponsiveLayouts.Grid(3, 280, 14));
            grid.setOpaque(false);

            for (QueueEntry e : boarding) {

                JPanel holder = new JPanel(new BorderLayout(0, 10));
                holder.setOpaque(false);

                holder.add(new QueueCard(
                        e.number,
                        e.passenger,
                        e.trip.label(),
                        e.status,
                        countFor(e.trip, WAITING),
                        countFor(e.trip, BOARDING),
                        countFor(e.trip, COMPLETED)), BorderLayout.CENTER);

                AppButton complete = small("Complete", AppButton.Variant.PRIMARY);
                complete.addActionListener(ev -> setStatus(e, COMPLETED));

                AppButton skip = small("Skip", AppButton.Variant.SECONDARY);
                skip.addActionListener(ev -> setStatus(e, SKIPPED));

                JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
                actions.setOpaque(false);
                actions.add(complete);
                actions.add(skip);

                holder.add(actions, BorderLayout.SOUTH);

                grid.add(holder);
            }

            card.add(grid, BorderLayout.CENTER);
        }

        boardingHolder.add(card, BorderLayout.CENTER);
    }

    private void renderWaiting(List<QueueEntry> visible) {

        List<QueueEntry> waiting = filterByStatus(visible, WAITING);

        waitingHolder.removeAll();
        waitingHolder.setLayout(new BorderLayout());

        AppCard card = new AppCard();
        card.setLayout(new BorderLayout(0, 10));
        card.add(new SectionHeader("Waiting Queue",
                waiting.size() + " passenger(s) waiting, ordered by queue position"),
                BorderLayout.NORTH);

        if (waiting.isEmpty()) {

            card.add(emptyState("No waiting passengers match the current filters."),
                    BorderLayout.CENTER);

        } else {

            DataGrid grid = new DataGrid(
                    new String[] { "Pos.", "Queue No.", "Passenger", "Trip", "Fare", "Joined", "Actions" },
                    new double[] { 0.4, 0.8, 1.6, 2.0, 0.8, 0.9, 1.7 });

            for (QueueEntry e : waiting) {

                AppButton call = small("Call", AppButton.Variant.PRIMARY);
                call.addActionListener(ev -> callEntry(e));

                AppButton skip = small("Skip", AppButton.Variant.SECONDARY);
                skip.addActionListener(ev -> setStatus(e, SKIPPED));

                JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
                actions.setOpaque(false);
                actions.add(call);
                actions.add(skip);

                grid.addRow(
                        DataGrid.colored(String.valueOf(positionOf(e)), BussinTheme.RED),
                        DataGrid.strong(e.number),
                        passengerCell(e),
                        DataGrid.text(e.trip.label()),
                        DataGrid.text(e.fareType),
                        DataGrid.text(e.joined),
                        actions);
            }

            card.add(grid, BorderLayout.CENTER);
        }

        waitingHolder.add(card, BorderLayout.CENTER);
    }

    private void renderHistory(List<QueueEntry> visible) {

        List<QueueEntry> history = new ArrayList<>();
        for (QueueEntry e : visible) {
            if (e.status.equals(COMPLETED) || e.status.equals(SKIPPED)) {
                history.add(e);
            }
        }

        historyHolder.removeAll();
        historyHolder.setLayout(new BorderLayout());

        AppCard card = new AppCard();
        card.setLayout(new BorderLayout(0, 10));
        card.add(new SectionHeader("Completed & Skipped",
                history.size() + " entr" + (history.size() == 1 ? "y" : "ies")),
                BorderLayout.NORTH);

        if (history.isEmpty()) {

            card.add(emptyState("No completed or skipped passengers match the current filters."),
                    BorderLayout.CENTER);

        } else {

            DataGrid grid = new DataGrid(
                    new String[] { "Queue No.", "Passenger", "Trip", "Joined", "Status", "Actions" },
                    new double[] { 0.8, 1.6, 2.0, 0.9, 1.0, 1.2 });

            for (QueueEntry e : history) {

                JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
                actions.setOpaque(false);

                if (e.status.equals(SKIPPED)) {
                    AppButton requeue = small("Requeue", AppButton.Variant.SECONDARY);
                    requeue.addActionListener(ev -> setStatus(e, WAITING));
                    actions.add(requeue);
                } else {
                    actions.add(DataGrid.text("—"));
                }

                grid.addRow(
                        DataGrid.strong(e.number),
                        passengerCell(e),
                        DataGrid.text(e.trip.label()),
                        DataGrid.text(e.joined),
                        new AppBadge(e.status, e.status.equals(COMPLETED)
                                ? AppBadge.Status.SUCCESS : AppBadge.Status.DANGER),
                        actions);
            }

            card.add(grid, BorderLayout.CENTER);
        }

        historyHolder.add(card, BorderLayout.CENTER);
    }

    // ================================================================
    // ACTIONS (mock state changes)
    // ================================================================

    private void callNext() {

        QueueEntry next = nextWaiting();

        if (next != null) {
            callEntry(next);
        }
    }

    /** First waiting passenger for the selected trip (or overall, by trip departure). */
    private QueueEntry nextWaiting() {

        Trip selected = selectedTrip();

        for (Trip t : trips) {
            if (selected != null && t != selected) {
                continue;
            }
            for (QueueEntry e : entries) {
                if (e.trip == t && e.status.equals(WAITING)) {
                    return e;
                }
            }
        }

        return null;
    }

    private void callEntry(QueueEntry entry) {

        setStatus(entry, BOARDING);
    }

    private void setStatus(QueueEntry entry, String status) {

        entry.status = status;

        refresh();
    }

    // ================================================================
    // HELPERS
    // ================================================================

    private boolean matches(QueueEntry e) {

        Trip selected = selectedTrip();

        if (selected != null && e.trip != selected) {
            return false;
        }

        Object status = statusFilter.getSelectedItem();

        if (status != null && !status.equals(ALL_STATUS) && !status.equals(e.status)) {
            return false;
        }

        String q = searchField.getText().trim().toLowerCase();

        return q.isEmpty()
                || e.passenger.toLowerCase().contains(q)
                || e.number.toLowerCase().contains(q);
    }

    private Trip selectedTrip() {

        int index = tripFilter.getSelectedIndex();

        return index <= 0 ? null : trips.get(index - 1);
    }

    private List<QueueEntry> filterByStatus(List<QueueEntry> source, String status) {

        List<QueueEntry> result = new ArrayList<>();

        for (QueueEntry e : source) {
            if (e.status.equals(status)) {
                result.add(e);
            }
        }

        return result;
    }

    private int countFor(Trip trip, String status) {

        int count = 0;

        for (QueueEntry e : entries) {
            if (e.trip == trip && e.status.equals(status)) {
                count++;
            }
        }

        return count;
    }

    /** Position within the trip's waiting line (1 = next to be called). */
    private int positionOf(QueueEntry target) {

        int position = 1;

        for (QueueEntry e : entries) {
            if (e == target) {
                return position;
            }
            if (e.trip == target.trip && e.status.equals(WAITING)) {
                position++;
            }
        }

        return position;
    }

    private Component passengerCell(QueueEntry e) {

        JPanel cell = transparent();
        cell.setLayout(new BoxLayout(cell, BoxLayout.Y_AXIS));

        JLabel name = DataGrid.strong(e.passenger);
        JLabel contact = new JLabel(e.contact);
        contact.setFont(BussinTheme.SMALL);
        contact.setForeground(BussinTheme.TEXT_MUTED);

        cell.add(name);
        cell.add(contact);

        return cell;
    }

    private AppButton small(String text, AppButton.Variant variant) {

        AppButton button = new AppButton(text, variant);
        button.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        return button;
    }

    private JPanel emptyState(String message) {

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 0, 24, 0));

        JLabel label = new JLabel(message);
        label.setFont(BussinTheme.BODY);
        label.setForeground(BussinTheme.TEXT_MUTED);
        panel.add(label);

        return panel;
    }

    private static JPanel transparent() {

        JPanel panel = new JPanel();
        panel.setOpaque(false);

        return panel;
    }

    // ================================================================
    // MODEL
    // ================================================================

    private record Trip(String departure, String route, String bus, String prefix) {

        String label() {
            return departure + " · " + route;
        }
    }

    private static class QueueEntry {

        private final Trip trip;
        private final String number;
        private final String passenger;
        private final String contact;
        private final String fareType;
        private final String joined;

        private String status;

        private QueueEntry(Trip trip, String number, String passenger, String contact,
                String fareType, String joined, String status) {

            this.trip = trip;
            this.number = number;
            this.passenger = passenger;
            this.contact = contact;
            this.fareType = fareType;
            this.joined = joined;
            this.status = status;
        }
    }
}
