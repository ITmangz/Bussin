package com.bussin.desktop.ui.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import com.bussin.desktop.ui.components.AppBadge;
import com.bussin.desktop.ui.components.AppButton;
import com.bussin.desktop.ui.components.AppCard;
import com.bussin.desktop.ui.components.AppLabel;
import com.bussin.desktop.ui.components.IconFactory;
import com.bussin.desktop.ui.theme.BussinTheme;

public class EmployeeScreen extends JPanel {

    private final List<Employee> employees = new ArrayList<>();

    private final List<Employee> filteredEmployees = new ArrayList<>();

    private final EmployeeTableModel tableModel = new EmployeeTableModel();

    private final JTable employeeTable = new JTable(tableModel);

    private final JTextField searchField = new JTextField();

    private final JComboBox<String> statusFilter = new JComboBox<>(
            new String[] {
                    "All Status",
                    "Active",
                    "Inactive"
            });

    private final JLabel totalValue = new JLabel("0");

    private final JLabel activeValue = new JLabel("0");

    private final JLabel inactiveValue = new JLabel("0");

    private final JLabel detailEmployeeId = new JLabel("-");

    private final JLabel detailName = new JLabel("-");

    private final JLabel detailPosition = new JLabel("-");

    private final JLabel detailContact = new JLabel("-");

    private final JLabel detailEmail = new JLabel("-");

    private final JLabel detailDateJoined = new JLabel("-");

    private final AppBadge detailStatus = new AppBadge(
            "NO STATUS",
            AppBadge.Status.NEUTRAL);

    private final AppButton editButton = new AppButton(
            "Edit",
            AppButton.Variant.SECONDARY);

    private final AppButton toggleStatusButton = new AppButton(
            "Activate",
            AppButton.Variant.SECONDARY);

    private final AppButton deleteButton = new AppButton(
            "Delete",
            AppButton.Variant.DANGER);

    public EmployeeScreen() {

        initializeData();
        initializeUI();
        refreshEmployees();
    }

    // ================================================================
    // MOCK DATA
    // ================================================================

    private void initializeData() {

        employees.add(
                new Employee(
                        "EMP-001",
                        "Juan Dela Cruz",
                        "Bus Driver",
                        "09171234567",
                        "juan.delacruz@bussin.com",
                        "2025-01-15",
                        "Active"));

        employees.add(
                new Employee(
                        "EMP-002",
                        "Maria Santos",
                        "Dispatcher",
                        "09181234567",
                        "maria.santos@bussin.com",
                        "2025-02-10",
                        "Active"));

        employees.add(
                new Employee(
                        "EMP-003",
                        "Pedro Reyes",
                        "Bus Driver",
                        "09191234567",
                        "pedro.reyes@bussin.com",
                        "2025-03-05",
                        "Active"));

        employees.add(
                new Employee(
                        "EMP-004",
                        "Ana Garcia",
                        "Ticketing Staff",
                        "09201234567",
                        "ana.garcia@bussin.com",
                        "2025-04-20",
                        "Inactive"));

        employees.add(
                new Employee(
                        "EMP-005",
                        "Carlos Mendoza",
                        "Fleet Supervisor",
                        "09211234567",
                        "carlos.mendoza@bussin.com",
                        "2025-05-12",
                        "Active"));
    }

    // ================================================================
    // UI INITIALIZATION
    // ================================================================

    private void initializeUI() {

        setOpaque(true);

        setBackground(
                BussinTheme.BACKGROUND);

        setLayout(
                new BorderLayout());

        add(
                createContent(),
                BorderLayout.CENTER);
    }

    private JComponent createContent() {

        JPanel content = new JPanel();

        content.setOpaque(false);

        content.setBorder(
                BorderFactory.createEmptyBorder(
                        BussinTheme.PAGE_PADDING,
                        BussinTheme.PAGE_PADDING,
                        BussinTheme.PAGE_PADDING,
                        BussinTheme.PAGE_PADDING));

        content.setLayout(
                new BoxLayout(
                        content,
                        BoxLayout.Y_AXIS));

        content.add(
                createHeader());

        content.add(
                Box.createVerticalStrut(
                        BussinTheme.SPACE_XL));

        content.add(
                createStatistics());

        content.add(
                Box.createVerticalStrut(
                        BussinTheme.SPACE_XL));

        content.add(
                createMainSection());

        JScrollPane scrollPane = new JScrollPane(content);

        scrollPane.setBorder(null);

        scrollPane.setOpaque(false);

        scrollPane.getViewport()
                .setOpaque(false);

        scrollPane.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        scrollPane.getVerticalScrollBar()
                .setUnitIncrement(16);

        return scrollPane;
    }

    // ================================================================
    // HEADER
    // ================================================================

    private JPanel createHeader() {

        JPanel header = new JPanel(
                new BorderLayout());

        header.setOpaque(false);

        JPanel titlePanel = new JPanel();

        titlePanel.setOpaque(false);

        titlePanel.setLayout(
                new BoxLayout(
                        titlePanel,
                        BoxLayout.Y_AXIS));

        titlePanel.add(
                AppLabel.title(
                        "Employee Management"));

        titlePanel.add(
                Box.createVerticalStrut(5));

        titlePanel.add(
                AppLabel.secondary(
                        "Manage employee information, positions, and account status."));

        AppButton createButton = new AppButton(
                "Create Employee");

        createButton.setIcon(
                IconFactory.create(
                        "plus",
                        16,
                        Color.WHITE));

        createButton.addActionListener(
                event -> showCreateEmployeeDialog());

        JPanel actionPanel = new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT,
                        0,
                        0));

        actionPanel.setOpaque(false);

        actionPanel.add(createButton);

        header.add(
                titlePanel,
                BorderLayout.WEST);

        header.add(
                actionPanel,
                BorderLayout.EAST);

        return header;
    }

    // ================================================================
    // STATISTICS
    // ================================================================

    private JPanel createStatistics() {

        JPanel statistics = new JPanel(
                new GridLayout(
                        1,
                        3,
                        14,
                        0));

        statistics.setOpaque(false);

        statistics.add(
                createStatCard(
                        "TOTAL EMPLOYEES",
                        totalValue,
                        BussinTheme.CHARCOAL));

        statistics.add(
                createStatCard(
                        "ACTIVE",
                        activeValue,
                        BussinTheme.SUCCESS));

        statistics.add(
                createStatCard(
                        "INACTIVE",
                        inactiveValue,
                        BussinTheme.RED));

        return statistics;
    }

    private AppCard createStatCard(
            String title,
            JLabel value,
            Color accent) {

        AppCard card = new AppCard();

        card.setLayout(
                new BorderLayout(
                        14,
                        0));

        JPanel indicator = new JPanel();

        indicator.setBackground(
                accent);

        indicator.setPreferredSize(
                new Dimension(
                        4,
                        54));

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
                indicator,
                BorderLayout.WEST);

        card.add(
                content,
                BorderLayout.CENTER);

        return card;
    }

    // ================================================================
    // MAIN SECTION
    // ================================================================

    private JPanel createMainSection() {

        JPanel main = new JPanel(
                new GridLayout(
                        1,
                        2,
                        18,
                        0));

        main.setOpaque(false);

        main.add(
                createEmployeeListCard());

        main.add(
                createEmployeeDetailsCard());

        return main;
    }

    // ================================================================
    // EMPLOYEE LIST
    // ================================================================

    private AppCard createEmployeeListCard() {

        AppCard card = new AppCard();

        card.setLayout(
                new BorderLayout(
                        0,
                        14));

        JPanel heading = new JPanel(
                new BorderLayout());

        heading.setOpaque(false);

        heading.add(
                AppLabel.section(
                        "Employee Directory"),
                BorderLayout.WEST);

        JLabel hint = new JLabel(
                "Select an employee to manage");

        hint.setFont(
                BussinTheme.SMALL);

        hint.setForeground(
                BussinTheme.TEXT_MUTED);

        heading.add(
                hint,
                BorderLayout.EAST);

        card.add(
                heading,
                BorderLayout.NORTH);

        JPanel center = new JPanel(
                new BorderLayout(
                        0,
                        10));

        center.setOpaque(false);

        center.add(
                createFilterBar(),
                BorderLayout.NORTH);

        configureTable();

        JScrollPane tableScroll = new JScrollPane(
                employeeTable);

        tableScroll.setBorder(
                BorderFactory.createLineBorder(
                        BussinTheme.BORDER));

        tableScroll.getVerticalScrollBar()
                .setUnitIncrement(12);

        center.add(
                tableScroll,
                BorderLayout.CENTER);

        card.add(
                center,
                BorderLayout.CENTER);

        return card;
    }

    private JPanel createFilterBar() {

        JPanel filters = new JPanel(
                new BorderLayout(
                        10,
                        0));

        filters.setOpaque(false);

        searchField.setPreferredSize(
                new Dimension(
                        220,
                        40));

        searchField.setToolTipText(
                "Search employee ID, name, position, contact, or email");

        statusFilter.setPreferredSize(
                new Dimension(
                        140,
                        40));

        filters.add(
                searchField,
                BorderLayout.CENTER);

        filters.add(
                statusFilter,
                BorderLayout.EAST);

        searchField
                .getDocument()
                .addDocumentListener(
                        new DocumentListener() {

                            @Override
                            public void insertUpdate(
                                    DocumentEvent e) {
                                refreshEmployees();
                            }

                            @Override
                            public void removeUpdate(
                                    DocumentEvent e) {
                                refreshEmployees();
                            }

                            @Override
                            public void changedUpdate(
                                    DocumentEvent e) {
                                refreshEmployees();
                            }
                        });

        statusFilter.addActionListener(
                event -> refreshEmployees());

        return filters;
    }

    private void configureTable() {

        employeeTable.setRowHeight(42);

        employeeTable.setFont(
                BussinTheme.SMALL);

        employeeTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);

        employeeTable.setAutoCreateRowSorter(true);

        employeeTable.setShowVerticalLines(false);

        employeeTable.setShowHorizontalLines(true);

        employeeTable.setGridColor(
                BussinTheme.BORDER);

        employeeTable.setSelectionBackground(
                BussinTheme.PRIMARY_LIGHT);

        employeeTable.setSelectionForeground(
                BussinTheme.TEXT_PRIMARY);

        employeeTable.setFillsViewportHeight(true);

        employeeTable.getTableHeader()
                .setFont(
                        BussinTheme.SMALL_BOLD);

        employeeTable.getTableHeader()
                .setBackground(
                        BussinTheme.SURFACE_ALT);

        employeeTable.getTableHeader()
                .setForeground(
                        BussinTheme.TEXT_SECONDARY);

        for (int i = 0; i < 7; i++) {

            employeeTable.getColumnModel()
                    .getColumn(i)
                    .setPreferredWidth(
                            switch (i) {

                                case 0 -> 80;

                                case 1 -> 135;

                                case 2 -> 115;

                                case 3 -> 105;

                                case 4 -> 180;

                                case 5 -> 105;

                                case 6 -> 90;

                                default -> 100;
                            });
        }

        employeeTable.getColumnModel()
                .getColumn(6)
                .setCellRenderer(
                        new StatusCellRenderer());

        employeeTable.getSelectionModel()
                .addListSelectionListener(
                        event -> {

                            if (!event.getValueIsAdjusting()) {
                                updateSelectedEmployee();
                            }
                        });
    }

    // ================================================================
    // EMPLOYEE DETAILS
    // ================================================================

    private AppCard createEmployeeDetailsCard() {

        AppCard card = new AppCard();

        card.setLayout(
                new BorderLayout(
                        0,
                        18));

        JPanel header = new JPanel(
                new BorderLayout());

        header.setOpaque(false);

        header.add(
                AppLabel.section(
                        "Employee Details"),
                BorderLayout.WEST);

        header.add(
                detailStatus,
                BorderLayout.EAST);

        card.add(
                header,
                BorderLayout.NORTH);

        JPanel details = new JPanel();

        details.setOpaque(false);

        details.setLayout(
                new BoxLayout(
                        details,
                        BoxLayout.Y_AXIS));

        JLabel idTitle = new JLabel(
                "EMPLOYEE ID");

        idTitle.setFont(
                BussinTheme.SMALL_BOLD);

        idTitle.setForeground(
                BussinTheme.TEXT_SECONDARY);

        detailEmployeeId.setFont(
                BussinTheme.SECTION_TITLE);

        detailEmployeeId.setForeground(
                BussinTheme.TEXT_PRIMARY);

        details.add(idTitle);

        details.add(
                Box.createVerticalStrut(4));

        details.add(
                detailEmployeeId);

        details.add(
                Box.createVerticalStrut(14));

        details.add(
                createSeparator());

        details.add(
                Box.createVerticalStrut(14));

        addDetail(
                details,
                "FULL NAME",
                detailName);

        addDetail(
                details,
                "POSITION",
                detailPosition);

        addDetail(
                details,
                "CONTACT NUMBER",
                detailContact);

        addDetail(
                details,
                "EMAIL",
                detailEmail);

        addDetail(
                details,
                "DATE JOINED",
                detailDateJoined);

        details.add(
                Box.createVerticalStrut(8));

        JPanel actions = new JPanel(
                new FlowLayout(
                        FlowLayout.LEFT,
                        8,
                        0));

        actions.setOpaque(false);

        editButton.setEnabled(false);

        toggleStatusButton.setEnabled(false);

        deleteButton.setEnabled(false);

        editButton.addActionListener(
                event -> showEditEmployeeDialog());

        toggleStatusButton.addActionListener(
                event -> toggleEmployeeStatus());

        deleteButton.addActionListener(
                event -> deleteSelectedEmployee());

        actions.add(editButton);

        actions.add(toggleStatusButton);

        actions.add(deleteButton);

        details.add(actions);

        JScrollPane scroll = new JScrollPane(details);

        scroll.setBorder(null);

        scroll.setOpaque(false);

        scroll.getViewport()
                .setOpaque(false);

        scroll.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        card.add(
                scroll,
                BorderLayout.CENTER);

        return card;
    }

    private void addDetail(
            JPanel panel,
            String title,
            JLabel value) {

        JLabel titleLabel = new JLabel(title);

        titleLabel.setFont(
                BussinTheme.SMALL_BOLD);

        titleLabel.setForeground(
                BussinTheme.TEXT_SECONDARY);

        value.setFont(
                BussinTheme.BODY);

        value.setForeground(
                BussinTheme.TEXT_PRIMARY);

        panel.add(titleLabel);

        panel.add(
                Box.createVerticalStrut(3));

        panel.add(value);

        panel.add(
                Box.createVerticalStrut(12));
    }

    private JSeparator createSeparator() {

        JSeparator separator = new JSeparator();

        separator.setForeground(
                BussinTheme.BORDER);

        return separator;
    }

    // ================================================================
    // REFRESH
    // ================================================================

    private void refreshEmployees() {

        String query = searchField.getText()
                .trim()
                .toLowerCase();

        String selectedStatus = String.valueOf(
                statusFilter.getSelectedItem());

        Employee previouslySelected = getSelectedEmployee();

        filteredEmployees.clear();

        for (Employee employee : employees) {

            boolean matchesSearch = query.isEmpty()
                    || employee.employeeId
                            .toLowerCase()
                            .contains(query)
                    || employee.name
                            .toLowerCase()
                            .contains(query)
                    || employee.position
                            .toLowerCase()
                            .contains(query)
                    || employee.contact
                            .toLowerCase()
                            .contains(query)
                    || employee.email
                            .toLowerCase()
                            .contains(query)
                    || employee.dateJoined
                            .toLowerCase()
                            .contains(query);

            boolean matchesStatus = selectedStatus.equals(
                    "All Status")
                    || employee.status.equals(
                            selectedStatus);

            if (matchesSearch
                    && matchesStatus) {

                filteredEmployees.add(
                        employee);
            }
        }

        tableModel.fireTableDataChanged();

        updateStatistics();

        if (previouslySelected != null
                && filteredEmployees.contains(
                        previouslySelected)) {

            int modelIndex = filteredEmployees.indexOf(
                    previouslySelected);

            int viewIndex = employeeTable
                    .convertRowIndexToView(
                            modelIndex);

            if (viewIndex >= 0
                    && viewIndex < employeeTable.getRowCount()) {

                employeeTable.setRowSelectionInterval(
                        viewIndex,
                        viewIndex);

                return;
            }
        }

        if (!filteredEmployees.isEmpty()) {

            employeeTable.setRowSelectionInterval(
                    0,
                    0);

        } else {

            clearDetails();
        }
    }

    private void updateStatistics() {

        int total = employees.size();

        int active = 0;

        int inactive = 0;

        for (Employee employee : employees) {

            if (employee.status.equals(
                    "Active")) {

                active++;
            }

            if (employee.status.equals(
                    "Inactive")) {

                inactive++;
            }
        }

        totalValue.setText(
                String.valueOf(total));

        activeValue.setText(
                String.valueOf(active));

        inactiveValue.setText(
                String.valueOf(inactive));
    }

    // ================================================================
    // SELECTED EMPLOYEE
    // ================================================================

    private void updateSelectedEmployee() {

        Employee employee = getSelectedEmployee();

        if (employee == null) {

            clearDetails();

            return;
        }

        detailEmployeeId.setText(
                employee.employeeId);

        detailName.setText(
                employee.name);

        detailPosition.setText(
                employee.position);

        detailContact.setText(
                employee.contact);

        detailEmail.setText(
                employee.email);

        detailDateJoined.setText(
                employee.dateJoined);

        updateStatusBadge(
                employee.status);

        editButton.setEnabled(true);

        boolean active = employee.status.equals(
                "Active");

        toggleStatusButton.setText(
                active
                        ? "Deactivate"
                        : "Activate");

        toggleStatusButton.setEnabled(true);

        deleteButton.setEnabled(true);
    }

    private void clearDetails() {

        detailEmployeeId.setText("-");

        detailName.setText("-");

        detailPosition.setText("-");

        detailContact.setText("-");

        detailEmail.setText("-");

        detailDateJoined.setText("-");

        updateStatusBadge(
                "No Status");

        editButton.setEnabled(false);

        toggleStatusButton.setText(
                "Activate");

        toggleStatusButton.setEnabled(false);

        deleteButton.setEnabled(false);
    }

    private Employee getSelectedEmployee() {

        int selectedRow = employeeTable.getSelectedRow();

        if (selectedRow < 0) {
            return null;
        }

        int modelRow = employeeTable
                .convertRowIndexToModel(
                        selectedRow);

        if (modelRow < 0
                || modelRow >= filteredEmployees.size()) {

            return null;
        }

        return filteredEmployees.get(
                modelRow);
    }

    // ================================================================
    // STATUS BADGE
    // ================================================================

    private void updateStatusBadge(
            String status) {

        AppBadge.Status badgeStatus;

        switch (status) {

            case "Active" ->
                badgeStatus = AppBadge.Status.SUCCESS;

            case "Inactive" ->
                badgeStatus = AppBadge.Status.DANGER;

            default ->
                badgeStatus = AppBadge.Status.NEUTRAL;
        }

        detailStatus.setText(
                status.toUpperCase());

        switch (badgeStatus) {

            case SUCCESS -> {

                detailStatus.setForeground(
                        BussinTheme.SUCCESS);

                detailStatus.setBackground(
                        BussinTheme.SUCCESS_LIGHT);
            }

            case DANGER -> {

                detailStatus.setForeground(
                        BussinTheme.DANGER);

                detailStatus.setBackground(
                        BussinTheme.DANGER_LIGHT);
            }

            default -> {

                detailStatus.setForeground(
                        BussinTheme.TEXT_SECONDARY);

                detailStatus.setBackground(
                        BussinTheme.SURFACE_ALT);
            }
        }
    }

    // ================================================================
    // CREATE EMPLOYEE
    // ================================================================

    private void showCreateEmployeeDialog() {

        JDialog dialog = new JDialog(
                SwingUtilities
                        .getWindowAncestor(this),
                "Create Employee",
                Dialog.ModalityType.APPLICATION_MODAL);

        dialog.setDefaultCloseOperation(
                JDialog.DISPOSE_ON_CLOSE);

        dialog.setSize(
                540,
                500);

        dialog.setMinimumSize(
                new Dimension(
                        540,
                        500));

        dialog.setLocationRelativeTo(this);

        JPanel root = new JPanel(
                new BorderLayout());

        root.setBackground(
                BussinTheme.BACKGROUND);

        JLabel title = new JLabel(
                "Create New Employee");

        title.setFont(
                BussinTheme.SECTION_TITLE);

        title.setForeground(
                BussinTheme.TEXT_PRIMARY);

        JPanel titlePanel = new JPanel(
                new BorderLayout());

        titlePanel.setOpaque(false);

        titlePanel.setBorder(
                BorderFactory.createEmptyBorder(
                        22,
                        24,
                        10,
                        24));

        titlePanel.add(
                title,
                BorderLayout.WEST);

        JPanel form = new JPanel(
                new GridLayout(
                        6,
                        2,
                        12,
                        10));

        form.setOpaque(false);

        form.setBorder(
                BorderFactory.createEmptyBorder(
                        12,
                        24,
                        12,
                        24));

        JTextField nameField = new JTextField();

        JTextField positionField = new JTextField();

        JTextField contactField = new JTextField();

        JTextField emailField = new JTextField();

        JTextField dateJoinedField = new JTextField();

        dateJoinedField.setToolTipText(
                "Format: YYYY-MM-DD");

        JComboBox<String> statusField = new JComboBox<>(
                new String[] {
                        "Active",
                        "Inactive"
                });

        addFormField(
                form,
                "Full Name",
                nameField);

        addFormField(
                form,
                "Position",
                positionField);

        addFormField(
                form,
                "Contact Number",
                contactField);

        addFormField(
                form,
                "Email",
                emailField);

        addFormField(
                form,
                "Date Joined",
                dateJoinedField);

        addFormField(
                form,
                "Status",
                statusField);

        JPanel buttons = new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT,
                        8,
                        12));

        buttons.setOpaque(false);

        AppButton cancelButton = new AppButton(
                "Cancel",
                AppButton.Variant.SECONDARY);

        AppButton saveButton = new AppButton(
                "Create Employee");

        cancelButton.addActionListener(
                event -> dialog.dispose());

        saveButton.addActionListener(
                event -> {

                    String name = nameField.getText()
                            .trim();

                    String position = positionField.getText()
                            .trim();

                    String contact = contactField.getText()
                            .trim();

                    String email = emailField.getText()
                            .trim();

                    String dateJoined = dateJoinedField
                            .getText()
                            .trim();

                    String status = String.valueOf(
                            statusField
                                    .getSelectedItem());

                    if (!validateEmployeeFields(
                            dialog,
                            name,
                            position,
                            contact,
                            email,
                            dateJoined)) {

                        return;
                    }

                    employees.add(
                            new Employee(
                                    generateEmployeeId(),
                                    name,
                                    position,
                                    contact,
                                    email,
                                    dateJoined,
                                    status));

                    refreshEmployees();

                    dialog.dispose();
                });

        buttons.add(cancelButton);

        buttons.add(saveButton);

        root.add(
                titlePanel,
                BorderLayout.NORTH);

        root.add(
                form,
                BorderLayout.CENTER);

        root.add(
                buttons,
                BorderLayout.SOUTH);

        dialog.setContentPane(root);

        dialog.setVisible(true);
    }

    // ================================================================
    // EDIT EMPLOYEE
    // ================================================================

    private void showEditEmployeeDialog() {

        Employee employee = getSelectedEmployee();

        if (employee == null) {
            return;
        }

        JDialog dialog = new JDialog(
                SwingUtilities
                        .getWindowAncestor(this),
                "Edit Employee",
                Dialog.ModalityType.APPLICATION_MODAL);

        dialog.setDefaultCloseOperation(
                JDialog.DISPOSE_ON_CLOSE);

        dialog.setSize(
                540,
                500);

        dialog.setMinimumSize(
                new Dimension(
                        540,
                        500));

        dialog.setLocationRelativeTo(this);

        JPanel root = new JPanel(
                new BorderLayout());

        root.setBackground(
                BussinTheme.BACKGROUND);

        JLabel title = new JLabel(
                "Edit Employee");

        title.setFont(
                BussinTheme.SECTION_TITLE);

        title.setForeground(
                BussinTheme.TEXT_PRIMARY);

        JPanel titlePanel = new JPanel(
                new BorderLayout());

        titlePanel.setOpaque(false);

        titlePanel.setBorder(
                BorderFactory.createEmptyBorder(
                        22,
                        24,
                        10,
                        24));

        titlePanel.add(
                title,
                BorderLayout.WEST);

        JPanel form = new JPanel(
                new GridLayout(
                        7,
                        2,
                        12,
                        10));

        form.setOpaque(false);

        form.setBorder(
                BorderFactory.createEmptyBorder(
                        12,
                        24,
                        12,
                        24));

        JTextField employeeIdField = new JTextField(
                employee.employeeId);

        JTextField nameField = new JTextField(
                employee.name);

        JTextField positionField = new JTextField(
                employee.position);

        JTextField contactField = new JTextField(
                employee.contact);

        JTextField emailField = new JTextField(
                employee.email);

        JTextField dateJoinedField = new JTextField(
                employee.dateJoined);

        JComboBox<String> statusField = new JComboBox<>(
                new String[] {
                        "Active",
                        "Inactive"
                });

        statusField.setSelectedItem(
                employee.status);

        addFormField(
                form,
                "Employee ID",
                employeeIdField);

        addFormField(
                form,
                "Full Name",
                nameField);

        addFormField(
                form,
                "Position",
                positionField);

        addFormField(
                form,
                "Contact Number",
                contactField);

        addFormField(
                form,
                "Email",
                emailField);

        addFormField(
                form,
                "Date Joined",
                dateJoinedField);

        addFormField(
                form,
                "Status",
                statusField);

        JPanel buttons = new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT,
                        8,
                        12));

        buttons.setOpaque(false);

        AppButton cancelButton = new AppButton(
                "Cancel",
                AppButton.Variant.SECONDARY);

        AppButton saveButton = new AppButton(
                "Save Changes");

        cancelButton.addActionListener(
                event -> dialog.dispose());

        saveButton.addActionListener(
                event -> {

                    String employeeId = employeeIdField
                            .getText()
                            .trim();

                    String name = nameField.getText()
                            .trim();

                    String position = positionField.getText()
                            .trim();

                    String contact = contactField.getText()
                            .trim();

                    String email = emailField.getText()
                            .trim();

                    String dateJoined = dateJoinedField
                            .getText()
                            .trim();

                    String status = String.valueOf(
                            statusField
                                    .getSelectedItem());

                    if (employeeId.isEmpty()) {

                        showWarning(
                                dialog,
                                "Employee ID cannot be empty.",
                                "Invalid Employee");

                        return;
                    }

                    if (isDuplicateEmployeeId(
                            employeeId,
                            employee)) {

                        showWarning(
                                dialog,
                                "Another employee already uses this Employee ID.",
                                "Duplicate Employee ID");

                        return;
                    }

                    if (!validateEmployeeFields(
                            dialog,
                            name,
                            position,
                            contact,
                            email,
                            dateJoined)) {

                        return;
                    }

                    employee.employeeId = employeeId;

                    employee.name = name;

                    employee.position = position;

                    employee.contact = contact;

                    employee.email = email;

                    employee.dateJoined = dateJoined;

                    employee.status = status;

                    refreshEmployees();

                    dialog.dispose();
                });

        buttons.add(cancelButton);

        buttons.add(saveButton);

        root.add(
                titlePanel,
                BorderLayout.NORTH);

        root.add(
                form,
                BorderLayout.CENTER);

        root.add(
                buttons,
                BorderLayout.SOUTH);

        dialog.setContentPane(root);

        dialog.setVisible(true);
    }

    // ================================================================
    // VALIDATION
    // ================================================================

    private boolean validateEmployeeFields(
            Component parent,
            String name,
            String position,
            String contact,
            String email,
            String dateJoined) {

        if (name.isEmpty()
                || position.isEmpty()
                || contact.isEmpty()
                || email.isEmpty()
                || dateJoined.isEmpty()) {

            showWarning(
                    parent,
                    "Please complete all required fields.",
                    "Invalid Employee");

            return false;
        }

        if (!contact.matches(
                "\\d{11}")) {

            showWarning(
                    parent,
                    "Contact number must contain exactly 11 digits.",
                    "Invalid Contact Number");

            return false;
        }

        if (!email.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {

            showWarning(
                    parent,
                    "Please enter a valid email address.",
                    "Invalid Email");

            return false;
        }

        if (!dateJoined.matches(
                "\\d{4}-\\d{2}-\\d{2}")) {

            showWarning(
                    parent,
                    "Date Joined must use YYYY-MM-DD format.",
                    "Invalid Date");

            return false;
        }

        return true;
    }

    private boolean isDuplicateEmployeeId(
            String employeeId,
            Employee ignoredEmployee) {

        for (Employee employee : employees) {

            if (employee == ignoredEmployee) {
                continue;
            }

            if (employee.employeeId.equalsIgnoreCase(
                    employeeId)) {

                return true;
            }
        }

        return false;
    }

    private void showWarning(
            Component parent,
            String message,
            String title) {

        JOptionPane.showMessageDialog(
                parent,
                message,
                title,
                JOptionPane.WARNING_MESSAGE);
    }

    // ================================================================
    // EMPLOYEE ID GENERATION
    // ================================================================

    private String generateEmployeeId() {

        int highest = 0;

        for (Employee employee : employees) {

            try {

                int number = Integer.parseInt(
                        employee.employeeId
                                .substring(4));

                highest = Math.max(
                        highest,
                        number);

            } catch (NumberFormatException ignored) {
                // Ignore unexpected IDs.
            }
        }

        return String.format(
                "EMP-%03d",
                highest + 1);
    }

    // ================================================================
    // STATUS
    // ================================================================

    private void toggleEmployeeStatus() {

        Employee employee = getSelectedEmployee();

        if (employee == null) {
            return;
        }

        boolean deactivate = employee.status.equals(
                "Active");

        if (deactivate) {

            int result = JOptionPane.showConfirmDialog(
                    this,
                    "Deactivate "
                            + employee.employeeId
                            + " - "
                            + employee.name
                            + "?\n\n"
                            + "This employee will be marked as inactive.",
                    "Deactivate Employee",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (result != JOptionPane.YES_OPTION) {

                return;
            }

            employee.status = "Inactive";

        } else {

            employee.status = "Active";
        }

        refreshEmployees();
    }

    // ================================================================
    // DELETE
    // ================================================================

    private void deleteSelectedEmployee() {

        Employee employee = getSelectedEmployee();

        if (employee == null) {
            return;
        }

        int result = JOptionPane.showConfirmDialog(
                this,
                "Delete "
                        + employee.employeeId
                        + " - "
                        + employee.name
                        + "?\n\n"
                        + "This action cannot be undone.",
                "Delete Employee",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {

            employees.remove(employee);

            refreshEmployees();
        }
    }

    // ================================================================
    // FORM FIELD
    // ================================================================

    private void addFormField(
            JPanel panel,
            String labelText,
            JComponent field) {

        JLabel label = new JLabel(
                labelText);

        label.setFont(
                BussinTheme.SMALL_BOLD);

        label.setForeground(
                BussinTheme.TEXT_SECONDARY);

        panel.add(label);

        field.setPreferredSize(
                new Dimension(
                        0,
                        36));

        panel.add(field);
    }

    // ================================================================
    // EMPLOYEE MODEL
    // ================================================================

    private static class Employee {

        private String employeeId;

        private String name;

        private String position;

        private String contact;

        private String email;

        private String dateJoined;

        private String status;

        private Employee(
                String employeeId,
                String name,
                String position,
                String contact,
                String email,
                String dateJoined,
                String status) {

            this.employeeId = employeeId;

            this.name = name;

            this.position = position;

            this.contact = contact;

            this.email = email;

            this.dateJoined = dateJoined;

            this.status = status;
        }
    }

    // ================================================================
    // TABLE MODEL
    // ================================================================

    private class EmployeeTableModel
            extends AbstractTableModel {

        private final String[] columns = {
                "Employee ID",
                "Name",
                "Position",
                "Contact",
                "Email",
                "Date Joined",
                "Status"
        };

        @Override
        public int getRowCount() {

            return filteredEmployees.size();
        }

        @Override
        public int getColumnCount() {

            return columns.length;
        }

        @Override
        public String getColumnName(
                int column) {

            return columns[column];
        }

        @Override
        public Object getValueAt(
                int row,
                int column) {

            Employee employee = filteredEmployees.get(
                    row);

            return switch (column) {

                case 0 ->
                    employee.employeeId;

                case 1 ->
                    employee.name;

                case 2 ->
                    employee.position;

                case 3 ->
                    employee.contact;

                case 4 ->
                    employee.email;

                case 5 ->
                    employee.dateJoined;

                case 6 ->
                    employee.status;

                default ->
                    "";
            };
        }

        @Override
        public boolean isCellEditable(
                int row,
                int column) {

            return false;
        }
    }

    // ================================================================
    // STATUS TABLE RENDERER
    // ================================================================

    private static class StatusCellRenderer
            extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean selected,
                boolean focus,
                int row,
                int column) {

            String status = String.valueOf(value);

            AppBadge.Status badgeStatus;

            switch (status) {

                case "Active" ->
                    badgeStatus = AppBadge.Status.SUCCESS;

                case "Inactive" ->
                    badgeStatus = AppBadge.Status.DANGER;

                default ->
                    badgeStatus = AppBadge.Status.NEUTRAL;
            }

            AppBadge badge = new AppBadge(
                    status,
                    badgeStatus);

            JPanel wrapper = new JPanel(
                    new FlowLayout(
                            FlowLayout.LEFT,
                            0,
                            7));

            wrapper.setBackground(
                    selected
                            ? table.getSelectionBackground()
                            : table.getBackground());

            wrapper.add(badge);

            return wrapper;
        }
    }
}