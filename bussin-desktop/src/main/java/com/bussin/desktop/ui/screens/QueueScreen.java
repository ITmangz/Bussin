package com.bussin.desktop.ui.screens;

import com.bussin.desktop.ui.components.AppBadge;
import com.bussin.desktop.ui.components.AppButton;
import com.bussin.desktop.ui.components.AppCard;
import com.bussin.desktop.ui.components.AppLabel;
import com.bussin.desktop.ui.components.IconFactory;
import com.bussin.desktop.ui.theme.BussinTheme;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class QueueScreen extends JPanel {

    private final List<QueueEntry> queueEntries = new ArrayList<>();

    private final JLabel waitingValue = new JLabel("0");
    private final JLabel servingValue = new JLabel("0");
    private final JLabel completedValue = new JLabel("0");
    private final JLabel skippedValue = new JLabel("0");

    private final JPanel waitingListPanel = new JPanel();
    private final JPanel currentServingPanel = new JPanel();

    public QueueScreen() {
        initializeData();
        initializeUI();
        refreshQueue();
    }

    private void initializeData() {

        queueEntries.add(
                new QueueEntry(
                        "A-024",
                        "Juan Dela Cruz",
                        "Manila → Batangas",
                        "09:15 AM",
                        "Waiting"));

        queueEntries.add(
                new QueueEntry(
                        "A-025",
                        "Maria Santos",
                        "Manila → Batangas",
                        "09:18 AM",
                        "Waiting"));

        queueEntries.add(
                new QueueEntry(
                        "A-026",
                        "Carlo Reyes",
                        "Manila → Batangas",
                        "09:21 AM",
                        "Waiting"));

        queueEntries.add(
                new QueueEntry(
                        "A-027",
                        "Angela Cruz",
                        "Manila → Batangas",
                        "09:24 AM",
                        "Waiting"));

        queueEntries.add(
                new QueueEntry(
                        "A-023",
                        "Pedro Garcia",
                        "Manila → Batangas",
                        "09:10 AM",
                        "Serving"));

        queueEntries.add(
                new QueueEntry(
                        "A-022",
                        "Sofia Ramos",
                        "Manila → Batangas",
                        "09:05 AM",
                        "Completed"));

        queueEntries.add(
                new QueueEntry(
                        "A-021",
                        "Mark Villanueva",
                        "Manila → Batangas",
                        "08:58 AM",
                        "Completed"));

        queueEntries.add(
                new QueueEntry(
                        "A-020",
                        "Daniel Flores",
                        "Manila → Batangas",
                        "08:50 AM",
                        "Skipped"));
    }

    private void initializeUI() {

        setOpaque(true);
        setBackground(BussinTheme.BACKGROUND);

        setLayout(new BorderLayout());

        add(createContent(), BorderLayout.CENTER);
    }

    private JComponent createContent() {

        JPanel content = new JPanel();
        content.setOpaque(false);

        content.setBorder(
                new EmptyBorder(
                        BussinTheme.PAGE_PADDING,
                        BussinTheme.PAGE_PADDING,
                        BussinTheme.PAGE_PADDING,
                        BussinTheme.PAGE_PADDING));

        content.setLayout(
                new BoxLayout(
                        content,
                        BoxLayout.Y_AXIS));

        content.add(createHeader());

        content.add(
                Box.createVerticalStrut(
                        BussinTheme.SPACE_XL));

        content.add(createStatistics());

        content.add(
                Box.createVerticalStrut(
                        BussinTheme.SPACE_XL));

        content.add(createMainSection());

        content.add(
                Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(content);

        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        scrollPane.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        return scrollPane;
    }

    private JPanel createHeader() {

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);

        titlePanel.setLayout(
                new BoxLayout(
                        titlePanel,
                        BoxLayout.Y_AXIS));

        AppLabel title = AppLabel.title("Queue Management");

        AppLabel subtitle = AppLabel.secondary(
                "Monitor passenger queues and manage the current boarding flow.");

        titlePanel.add(title);
        titlePanel.add(
                Box.createVerticalStrut(5));
        titlePanel.add(subtitle);

        JPanel actions = new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT,
                        10,
                        0));

        actions.setOpaque(false);

        AppButton refreshButton = new AppButton(
                "Refresh",
                AppButton.Variant.SECONDARY);

        refreshButton.setIcon(
                IconFactory.create(
                        "arrow-right",
                        16,
                        BussinTheme.TEXT_PRIMARY));

        refreshButton.addActionListener(
                event -> refreshQueue());

        actions.add(refreshButton);

        header.add(
                titlePanel,
                BorderLayout.WEST);

        header.add(
                actions,
                BorderLayout.EAST);

        return header;
    }

    private JPanel createStatistics() {

        JPanel statistics = new JPanel(
                new GridLayout(
                        1,
                        4,
                        14,
                        0));

        statistics.setOpaque(false);

        statistics.add(
                createQueueStat(
                        "WAITING",
                        waitingValue,
                        BussinTheme.WARNING));

        statistics.add(
                createQueueStat(
                        "SERVING",
                        servingValue,
                        BussinTheme.RED));

        statistics.add(
                createQueueStat(
                        "COMPLETED",
                        completedValue,
                        BussinTheme.SUCCESS));

        statistics.add(
                createQueueStat(
                        "SKIPPED",
                        skippedValue,
                        BussinTheme.DANGER));

        return statistics;
    }

    private AppCard createQueueStat(
            String title,
            JLabel value,
            Color accent) {

        AppCard card = new AppCard();

        card.setLayout(
                new BorderLayout(
                        14,
                        0));

        JPanel indicator = new JPanel();
        indicator.setPreferredSize(
                new Dimension(
                        4,
                        55));

        indicator.setBackground(accent);

        card.add(
                indicator,
                BorderLayout.WEST);

        JPanel content = new JPanel();
        content.setOpaque(false);

        content.setLayout(
                new BoxLayout(
                        content,
                        BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);

        titleLabel.setFont(
                BussinTheme.SMALL_BOLD);

        titleLabel.setForeground(
                BussinTheme.TEXT_SECONDARY);

        value.setFont(
                BussinTheme.STAT_VALUE);

        value.setForeground(
                BussinTheme.TEXT_PRIMARY);

        content.add(titleLabel);

        content.add(
                Box.createVerticalStrut(5));

        content.add(value);

        card.add(
                content,
                BorderLayout.CENTER);

        return card;
    }

    private JPanel createMainSection() {

        JPanel main = new JPanel(
                new GridLayout(
                        1,
                        2,
                        18,
                        0));

        main.setOpaque(false);

        main.add(
                createCurrentServingCard());

        main.add(
                createWaitingQueueCard());

        return main;
    }

    private AppCard createCurrentServingCard() {

        AppCard card = new AppCard();

        card.setLayout(
                new BorderLayout(
                        0,
                        18));

        JPanel header = new JPanel(
                new BorderLayout());

        header.setOpaque(false);

        AppLabel title = AppLabel.section(
                "Current Serving");

        AppBadge badge = new AppBadge(
                "SERVING",
                AppBadge.Status.INFO);

        header.add(
                title,
                BorderLayout.WEST);

        header.add(
                badge,
                BorderLayout.EAST);

        currentServingPanel.setOpaque(false);

        currentServingPanel.setLayout(
                new BorderLayout());

        card.add(
                header,
                BorderLayout.NORTH);

        card.add(
                currentServingPanel,
                BorderLayout.CENTER);

        return card;
    }

    private AppCard createWaitingQueueCard() {

        AppCard card = new AppCard();

        card.setLayout(
                new BorderLayout(
                        0,
                        14));

        JPanel header = new JPanel(
                new BorderLayout());

        header.setOpaque(false);

        AppLabel title = AppLabel.section(
                "Waiting Queue");

        JLabel count = new JLabel();

        count.setFont(
                BussinTheme.SMALL_BOLD);

        count.setForeground(
                BussinTheme.TEXT_MUTED);

        header.add(
                title,
                BorderLayout.WEST);

        header.add(
                count,
                BorderLayout.EAST);

        waitingListPanel.setOpaque(false);

        waitingListPanel.setLayout(
                new BoxLayout(
                        waitingListPanel,
                        BoxLayout.Y_AXIS));

        JScrollPane scroll = new JScrollPane(
                waitingListPanel);

        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        scroll.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        scroll.getVerticalScrollBar().setUnitIncrement(12);

        card.add(
                header,
                BorderLayout.NORTH);

        card.add(
                scroll,
                BorderLayout.CENTER);

        return card;
    }

    private JPanel createServingContent(
            QueueEntry entry) {

        JPanel content = new JPanel();

        content.setOpaque(false);

        content.setLayout(
                new BoxLayout(
                        content,
                        BoxLayout.Y_AXIS));

        JLabel queueNumber = new JLabel(
                entry.queueNumber);

        queueNumber.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        32));

        queueNumber.setForeground(
                BussinTheme.RED);

        JLabel passenger = new JLabel(
                entry.passenger);

        passenger.setFont(
                BussinTheme.SECTION_TITLE);

        passenger.setForeground(
                BussinTheme.TEXT_PRIMARY);

        JLabel destination = new JLabel(
                entry.destination);

        destination.setFont(
                BussinTheme.BODY);

        destination.setForeground(
                BussinTheme.TEXT_SECONDARY);

        JLabel time = new JLabel(
                "Queued at " + entry.time);

        time.setFont(
                BussinTheme.SMALL);

        time.setForeground(
                BussinTheme.TEXT_MUTED);

        content.add(queueNumber);

        content.add(
                Box.createVerticalStrut(4));

        content.add(passenger);

        content.add(
                Box.createVerticalStrut(4));

        content.add(destination);

        content.add(
                Box.createVerticalStrut(8));

        content.add(time);

        content.add(
                Box.createVerticalStrut(18));

        JPanel actions = new JPanel(
                new FlowLayout(
                        FlowLayout.LEFT,
                        8,
                        0));

        actions.setOpaque(false);

        AppButton complete = new AppButton(
                "Complete");

        AppButton skip = new AppButton(
                "Skip",
                AppButton.Variant.SECONDARY);

        complete.addActionListener(
                event -> completeEntry(entry));

        skip.addActionListener(
                event -> skipEntry(entry));

        actions.add(complete);
        actions.add(skip);

        content.add(actions);

        return content;
    }

    private JPanel createWaitingEntry(
            QueueEntry entry,
            int position) {

        JPanel item = new JPanel(
                new BorderLayout(
                        12,
                        0));

        item.setOpaque(false);

        item.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                0,
                                0,
                                1,
                                0,
                                BussinTheme.BORDER),
                        BorderFactory.createEmptyBorder(
                                12,
                                4,
                                12,
                                4)));

        JLabel positionLabel = new JLabel(
                String.format(
                        ("%02d"),
                        position));

        positionLabel.setFont(
                BussinTheme.SMALL_BOLD);

        positionLabel.setForeground(
                BussinTheme.MUTED_SILVER);

        positionLabel.setPreferredSize(
                new Dimension(
                        30,
                        30));

        JPanel information = new JPanel();

        information.setOpaque(false);

        information.setLayout(
                new BoxLayout(
                        information,
                        BoxLayout.Y_AXIS));

        JLabel queue = new JLabel(
                entry.queueNumber
                        + "  •  "
                        + entry.passenger);

        queue.setFont(
                BussinTheme.SMALL_BOLD);

        queue.setForeground(
                BussinTheme.TEXT_PRIMARY);

        JLabel destination = new JLabel(
                entry.destination
                        + "  •  "
                        + entry.time);

        destination.setFont(
                BussinTheme.SMALL);

        destination.setForeground(
                BussinTheme.TEXT_MUTED);

        information.add(queue);

        information.add(
                Box.createVerticalStrut(3));

        information.add(destination);

        JButton callButton = new JButton();

        callButton.setIcon(
                IconFactory.create(
                        "arrow-right",
                        15,
                        BussinTheme.RED));

        callButton.setToolTipText(
                "Call passenger");

        callButton.setFocusPainted(false);
        callButton.setBorderPainted(false);
        callButton.setContentAreaFilled(false);
        callButton.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR));

        callButton.addActionListener(
                event -> callEntry(entry));

        item.add(
                positionLabel,
                BorderLayout.WEST);

        item.add(
                information,
                BorderLayout.CENTER);

        item.add(
                callButton,
                BorderLayout.EAST);

        return item;
    }

    private void refreshQueue() {

        waitingListPanel.removeAll();
        currentServingPanel.removeAll();

        int waiting = 0;
        int serving = 0;
        int completed = 0;
        int skipped = 0;

        QueueEntry currentServing = null;

        for (QueueEntry entry : queueEntries) {

            switch (entry.status) {

                case "Waiting" -> waiting++;

                case "Serving" -> {
                    serving++;
                    currentServing = entry;
                }

                case "Completed" -> completed++;

                case "Skipped" -> skipped++;
            }
        }

        waitingValue.setText(
                String.valueOf(waiting));

        servingValue.setText(
                String.valueOf(serving));

        completedValue.setText(
                String.valueOf(completed));

        skippedValue.setText(
                String.valueOf(skipped));

        if (currentServing != null) {

            currentServingPanel.add(
                    createServingContent(
                            currentServing),
                    BorderLayout.CENTER);

        } else {

            JPanel empty = createEmptyState(
                    "No passenger is currently serving.");

            currentServingPanel.add(
                    empty,
                    BorderLayout.CENTER);
        }

        int position = 1;

        for (QueueEntry entry : queueEntries) {

            if (!entry.status.equals("Waiting")) {
                continue;
            }

            waitingListPanel.add(
                    createWaitingEntry(
                            entry,
                            position++));
        }

        if (position == 1) {

            waitingListPanel.add(
                    createEmptyState(
                            "No passengers are waiting."));
        }

        waitingListPanel.revalidate();
        waitingListPanel.repaint();

        currentServingPanel.revalidate();
        currentServingPanel.repaint();
    }

    private JPanel createEmptyState(
            String message) {

        JPanel panel = new JPanel(
                new GridBagLayout());

        panel.setOpaque(false);

        JLabel label = new JLabel(
                message);

        label.setFont(
                BussinTheme.BODY);

        label.setForeground(
                BussinTheme.TEXT_MUTED);

        panel.add(label);

        return panel;
    }

    private void callEntry(
            QueueEntry entry) {

        QueueEntry current = findServingEntry();

        if (current != null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please complete or skip the current passenger before calling the next queue.",
                    "Queue In Progress",
                    JOptionPane.INFORMATION_MESSAGE);

            return;
        }

        entry.status = "Serving";

        refreshQueue();
    }

    private void completeEntry(
            QueueEntry entry) {

        entry.status = "Completed";

        refreshQueue();
    }

    private void skipEntry(
            QueueEntry entry) {

        entry.status = "Skipped";

        refreshQueue();
    }

    private QueueEntry findServingEntry() {

        for (QueueEntry entry : queueEntries) {

            if (entry.status.equals("Serving")) {
                return entry;
            }
        }

        return null;
    }

    private static class QueueEntry {

        private final String queueNumber;
        private final String passenger;
        private final String destination;
        private final String time;

        private String status;

        private QueueEntry(
                String queueNumber,
                String passenger,
                String destination,
                String time,
                String status) {

            this.queueNumber = queueNumber;
            this.passenger = passenger;
            this.destination = destination;
            this.time = time;
            this.status = status;
        }
    }
}