package com.bussin.desktop.ui.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import com.bussin.desktop.services.UserApiService;
import com.bussin.desktop.services.UserApiService.UserResponse;
import com.bussin.desktop.ui.components.AppBadge;
import com.bussin.desktop.ui.components.AppButton;
import com.bussin.desktop.ui.components.AppCard;
import com.bussin.desktop.ui.components.AppLabel;
import com.bussin.desktop.ui.components.IconFactory;
import com.bussin.desktop.ui.theme.BussinTheme;

public class EmployeeScreen extends JPanel {

        private final List<UserResponse> users = new ArrayList<>();

        private final List<UserResponse> filteredUsers = new ArrayList<>();

        private final UserTableModel tableModel = new UserTableModel();

        private final JTable userTable = new JTable(tableModel);

        private final JTextField searchField = new JTextField();

        private final JComboBox<String> roleFilter = new JComboBox<>(
                        new String[] {
                                        "All Roles",
                                        "ADMIN",
                                        "EMPLOYEE",
                                        "COMMUTER"
                        });

        private final JLabel totalValue = new JLabel("0");

        private final JLabel adminValue = new JLabel("0");

        private final JLabel employeeValue = new JLabel("0");

        private final JLabel commuterValue = new JLabel("0");

        private final JLabel detailUserId = new JLabel("-");

        private final JLabel detailName = new JLabel("-");

        private final JLabel detailEmail = new JLabel("-");

        private final JLabel detailContact = new JLabel("-");

        private final JLabel detailGender = new JLabel("-");

        private final JLabel detailAge = new JLabel("-");

        private final JLabel detailDateOfBirth = new JLabel("-");

        private final AppBadge detailRole = new AppBadge(
                        "NO ROLE",
                        AppBadge.Status.NEUTRAL);

        private final AppButton changeRoleButton = new AppButton(
                        "Change Role",
                        AppButton.Variant.SECONDARY);

        public EmployeeScreen() {

                initializeUI();

                refreshUsers();
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

                JScrollPane scrollPane = new JScrollPane(
                                content);

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
                                                "User & Employee Management"));

                titlePanel.add(
                                Box.createVerticalStrut(5));

                titlePanel.add(
                                AppLabel.secondary(
                                                "Manage BUSSIN users and their system roles."));

                AppButton refreshButton = new AppButton(
                                "Refresh");

                refreshButton.setIcon(
                                IconFactory.create(
                                                "refresh-cw",
                                                16,
                                                Color.WHITE));

                refreshButton.addActionListener(
                                event -> refreshUsers());

                JPanel actionPanel = new JPanel(
                                new FlowLayout(
                                                FlowLayout.RIGHT,
                                                0,
                                                0));

                actionPanel.setOpaque(false);

                actionPanel.add(refreshButton);

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
                                                4,
                                                14,
                                                0));

                statistics.setOpaque(false);

                statistics.add(
                                createStatCard(
                                                "TOTAL USERS",
                                                totalValue,
                                                BussinTheme.CHARCOAL));

                statistics.add(
                                createStatCard(
                                                "ADMINISTRATORS",
                                                adminValue,
                                                BussinTheme.RED));

                statistics.add(
                                createStatCard(
                                                "EMPLOYEES",
                                                employeeValue,
                                                BussinTheme.SUCCESS));

                statistics.add(
                                createStatCard(
                                                "COMMUTERS",
                                                commuterValue,
                                                BussinTheme.PRIMARY));

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
                                createUserListCard());

                main.add(
                                createUserDetailsCard());

                return main;
        }

        // ================================================================
        // USER LIST
        // ================================================================

        private AppCard createUserListCard() {

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
                                                "User Directory"),
                                BorderLayout.WEST);

                JLabel hint = new JLabel(
                                "Select a user to manage");

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
                                userTable);

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
                                "Search by ID, name, email, or contact number");

                roleFilter.setPreferredSize(
                                new Dimension(
                                                140,
                                                40));

                filters.add(
                                searchField,
                                BorderLayout.CENTER);

                filters.add(
                                roleFilter,
                                BorderLayout.EAST);

                searchField
                                .getDocument()
                                .addDocumentListener(
                                                new DocumentListener() {

                                                        @Override
                                                        public void insertUpdate(
                                                                        DocumentEvent event) {
                                                                refreshUsers();
                                                        }

                                                        @Override
                                                        public void removeUpdate(
                                                                        DocumentEvent event) {
                                                                refreshUsers();
                                                        }

                                                        @Override
                                                        public void changedUpdate(
                                                                        DocumentEvent event) {
                                                                refreshUsers();
                                                        }
                                                });

                roleFilter.addActionListener(
                                event -> refreshUsers());

                return filters;
        }

        private void configureTable() {

                userTable.setRowHeight(42);

                userTable.setFont(
                                BussinTheme.SMALL);

                userTable.setSelectionMode(
                                ListSelectionModel.SINGLE_SELECTION);

                userTable.setAutoCreateRowSorter(true);

                userTable.setShowVerticalLines(false);

                userTable.setShowHorizontalLines(true);

                userTable.setGridColor(
                                BussinTheme.BORDER);

                userTable.setSelectionBackground(
                                BussinTheme.PRIMARY_LIGHT);

                userTable.setSelectionForeground(
                                BussinTheme.TEXT_PRIMARY);

                userTable.setFillsViewportHeight(true);

                userTable.getTableHeader()
                                .setFont(
                                                BussinTheme.SMALL_BOLD);

                userTable.getTableHeader()
                                .setBackground(
                                                BussinTheme.SURFACE_ALT);

                userTable.getTableHeader()
                                .setForeground(
                                                BussinTheme.TEXT_SECONDARY);

                for (int i = 0; i < 6; i++) {

                        userTable.getColumnModel()
                                        .getColumn(i)
                                        .setPreferredWidth(
                                                        switch (i) {

                                                                case 0 -> 70;

                                                                case 1 -> 145;

                                                                case 2 -> 180;

                                                                case 3 -> 115;

                                                                case 4 -> 100;

                                                                case 5 -> 100;

                                                                default -> 100;
                                                        });
                }

                userTable.getColumnModel()
                                .getColumn(5)
                                .setCellRenderer(
                                                new RoleCellRenderer());

                userTable.getSelectionModel()
                                .addListSelectionListener(
                                                event -> {

                                                        if (!event.getValueIsAdjusting()) {
                                                                updateSelectedUser();
                                                        }
                                                });
        }

        // ================================================================
        // USER DETAILS
        // ================================================================

        private AppCard createUserDetailsCard() {

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
                                                "User Details"),
                                BorderLayout.WEST);

                header.add(
                                detailRole,
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
                                "USER ID");

                idTitle.setFont(
                                BussinTheme.SMALL_BOLD);

                idTitle.setForeground(
                                BussinTheme.TEXT_SECONDARY);

                detailUserId.setFont(
                                BussinTheme.SECTION_TITLE);

                detailUserId.setForeground(
                                BussinTheme.TEXT_PRIMARY);

                details.add(idTitle);

                details.add(
                                Box.createVerticalStrut(4));

                details.add(
                                detailUserId);

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
                                "EMAIL",
                                detailEmail);

                addDetail(
                                details,
                                "CONTACT NUMBER",
                                detailContact);

                addDetail(
                                details,
                                "GENDER",
                                detailGender);

                addDetail(
                                details,
                                "AGE",
                                detailAge);

                addDetail(
                                details,
                                "DATE OF BIRTH",
                                detailDateOfBirth);

                details.add(
                                Box.createVerticalStrut(8));

                JPanel actions = new JPanel(
                                new FlowLayout(
                                                FlowLayout.LEFT,
                                                8,
                                                0));

                actions.setOpaque(false);

                changeRoleButton.setEnabled(false);

                changeRoleButton.addActionListener(
                                event -> showChangeRoleDialog());

                actions.add(
                                changeRoleButton);

                details.add(actions);

                JScrollPane scroll = new JScrollPane(
                                details);

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
        // API REFRESH
        // ================================================================

        private void refreshUsers() {

                try {

                        List<UserResponse> fetchedUsers = UserApiService.getAllUsers();

                        users.clear();

                        users.addAll(
                                        fetchedUsers);

                        applyFilters();

                } catch (Exception exception) {

                        users.clear();

                        filteredUsers.clear();

                        tableModel.fireTableDataChanged();

                        updateStatistics();

                        clearDetails();

                        JOptionPane.showMessageDialog(
                                        this,
                                        exception.getMessage(),
                                        "Failed to Load Users",
                                        JOptionPane.ERROR_MESSAGE);
                }
        }

        private void applyFilters() {

                String query = searchField.getText()
                                .trim()
                                .toLowerCase();

                String selectedRole = String.valueOf(
                                roleFilter.getSelectedItem());

                UserResponse previouslySelected = getSelectedUser();

                filteredUsers.clear();

                for (UserResponse user : users) {

                        String fullName = buildFullName(user);

                        String email = safe(user.getEmail());

                        String contact = safe(user.getContactNumber());

                        String firebaseUid = safe(user.getFirebaseUid());

                        String userId = user.getId() == null
                                        ? ""
                                        : String.valueOf(
                                                        user.getId());

                        String role = safe(user.getRole())
                                        .toUpperCase();

                        boolean matchesSearch = query.isEmpty()
                                        || userId.toLowerCase()
                                                        .contains(query)
                                        || firebaseUid.toLowerCase()
                                                        .contains(query)
                                        || fullName.toLowerCase()
                                                        .contains(query)
                                        || email.toLowerCase()
                                                        .contains(query)
                                        || contact.toLowerCase()
                                                        .contains(query);

                        boolean matchesRole = "All Roles".equals(
                                        selectedRole)
                                        || role.equals(
                                                        selectedRole);

                        if (matchesSearch
                                        && matchesRole) {

                                filteredUsers.add(user);
                        }
                }

                tableModel.fireTableDataChanged();

                updateStatistics();

                if (previouslySelected != null
                                && filteredUsers.contains(
                                                previouslySelected)) {

                        int modelIndex = filteredUsers.indexOf(
                                        previouslySelected);

                        int viewIndex = userTable.convertRowIndexToView(
                                        modelIndex);

                        if (viewIndex >= 0
                                        && viewIndex < userTable.getRowCount()) {

                                userTable.setRowSelectionInterval(
                                                viewIndex,
                                                viewIndex);

                                return;
                        }
                }

                if (!filteredUsers.isEmpty()) {

                        userTable.setRowSelectionInterval(
                                        0,
                                        0);

                } else {

                        clearDetails();
                }
        }

        private void updateStatistics() {

                int total = users.size();

                int admins = 0;

                int employees = 0;

                int commuters = 0;

                for (UserResponse user : users) {

                        String role = safe(
                                        user.getRole())
                                        .toUpperCase();

                        switch (role) {

                                case "ADMIN" ->
                                        admins++;

                                case "EMPLOYEE" ->
                                        employees++;

                                case "COMMUTER" ->
                                        commuters++;

                                default -> {
                                }
                        }
                }

                totalValue.setText(
                                String.valueOf(total));

                adminValue.setText(
                                String.valueOf(admins));

                employeeValue.setText(
                                String.valueOf(employees));

                commuterValue.setText(
                                String.valueOf(commuters));
        }

        // ================================================================
        // SELECTED USER
        // ================================================================

        private void updateSelectedUser() {

                UserResponse user = getSelectedUser();

                if (user == null) {

                        clearDetails();

                        return;
                }

                detailUserId.setText(
                                user.getId() == null
                                                ? "-"
                                                : String.valueOf(
                                                                user.getId()));

                detailName.setText(
                                buildFullName(user));

                detailEmail.setText(
                                displayValue(
                                                user.getEmail()));

                detailContact.setText(
                                displayValue(
                                                user.getContactNumber()));

                detailGender.setText(
                                displayValue(
                                                user.getGender()));

                detailAge.setText(
                                user.getAge() == null
                                                ? "-"
                                                : String.valueOf(
                                                                user.getAge()));

                detailDateOfBirth.setText(
                                user.getDateOfBirth() == null
                                                ? "-"
                                                : user.getDateOfBirth()
                                                                .toString());

                updateRoleBadge(
                                user.getRole());

                changeRoleButton.setEnabled(true);
        }

        private void clearDetails() {

                detailUserId.setText("-");

                detailName.setText("-");

                detailEmail.setText("-");

                detailContact.setText("-");

                detailGender.setText("-");

                detailAge.setText("-");

                detailDateOfBirth.setText("-");

                updateRoleBadge(null);

                changeRoleButton.setEnabled(false);
        }

        private UserResponse getSelectedUser() {

                int selectedRow = userTable.getSelectedRow();

                if (selectedRow < 0) {
                        return null;
                }

                int modelRow = userTable.convertRowIndexToModel(
                                selectedRow);

                if (modelRow < 0
                                || modelRow >= filteredUsers.size()) {

                        return null;
                }

                return filteredUsers.get(
                                modelRow);
        }

        // ================================================================
        // ROLE BADGE
        // ================================================================

        private void updateRoleBadge(
                        String role) {

                String normalizedRole = safe(role)
                                .toUpperCase();

                AppBadge.Status badgeStatus;

                switch (normalizedRole) {

                        case "ADMIN" ->
                                badgeStatus = AppBadge.Status.DANGER;

                        case "EMPLOYEE" ->
                                badgeStatus = AppBadge.Status.SUCCESS;

                        case "COMMUTER" ->
                                badgeStatus = AppBadge.Status.NEUTRAL;

                        default ->
                                badgeStatus = AppBadge.Status.NEUTRAL;
                }

                detailRole.setText(
                                normalizedRole.isBlank()
                                                ? "NO ROLE"
                                                : normalizedRole);

                switch (badgeStatus) {

                        case SUCCESS -> {

                                detailRole.setForeground(
                                                BussinTheme.SUCCESS);

                                detailRole.setBackground(
                                                BussinTheme.SUCCESS_LIGHT);
                        }

                        case DANGER -> {

                                detailRole.setForeground(
                                                BussinTheme.DANGER);

                                detailRole.setBackground(
                                                BussinTheme.DANGER_LIGHT);
                        }

                        default -> {

                                detailRole.setForeground(
                                                BussinTheme.TEXT_SECONDARY);

                                detailRole.setBackground(
                                                BussinTheme.SURFACE_ALT);
                        }
                }
        }

        // ================================================================
        // CHANGE ROLE
        // ================================================================

        private void showChangeRoleDialog() {

                UserResponse user = getSelectedUser();

                if (user == null
                                || user.getId() == null) {

                        return;
                }

                String currentRole = safe(user.getRole())
                                .toUpperCase();

                JComboBox<String> roleField = new JComboBox<>(
                                new String[] {
                                                "ADMIN",
                                                "EMPLOYEE",
                                                "COMMUTER"
                                });

                roleField.setSelectedItem(
                                currentRole);

                JPanel panel = new JPanel(
                                new BorderLayout(
                                                0,
                                                10));

                JLabel label = new JLabel(
                                "Select the new role for this user:");

                panel.add(
                                label,
                                BorderLayout.NORTH);

                panel.add(
                                roleField,
                                BorderLayout.CENTER);

                int result = JOptionPane.showConfirmDialog(
                                this,
                                panel,
                                "Change User Role",
                                JOptionPane.OK_CANCEL_OPTION,
                                JOptionPane.PLAIN_MESSAGE);

                if (result != JOptionPane.OK_OPTION) {
                        return;
                }

                String newRole = String.valueOf(
                                roleField.getSelectedItem());

                if (newRole.equals(
                                currentRole)) {

                        return;
                }

                int confirmation = JOptionPane.showConfirmDialog(
                                this,
                                "Change "
                                                + buildFullName(user)
                                                + "'s role from "
                                                + currentRole
                                                + " to "
                                                + newRole
                                                + "?",
                                "Confirm Role Change",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE);

                if (confirmation != JOptionPane.YES_OPTION) {
                        return;
                }

                try {

                        UserApiService.updateUserRole(
                                        user.getId(),
                                        newRole);

                        refreshUsers();

                        JOptionPane.showMessageDialog(
                                        this,
                                        "User role updated successfully.",
                                        "Role Updated",
                                        JOptionPane.INFORMATION_MESSAGE);

                } catch (Exception exception) {

                        JOptionPane.showMessageDialog(
                                        this,
                                        exception.getMessage(),
                                        "Failed to Update Role",
                                        JOptionPane.ERROR_MESSAGE);
                }
        }

        // ================================================================
        // HELPERS
        // ================================================================

        private String buildFullName(
                        UserResponse user) {

                StringBuilder name = new StringBuilder();

                appendNamePart(
                                name,
                                user.getFirstName());

                appendNamePart(
                                name,
                                user.getMiddleName());

                appendNamePart(
                                name,
                                user.getLastName());

                if (name.isEmpty()) {
                        return "-";
                }

                return name.toString();
        }

        private void appendNamePart(
                        StringBuilder builder,
                        String value) {

                if (value == null
                                || value.isBlank()) {

                        return;
                }

                if (!builder.isEmpty()) {
                        builder.append(" ");
                }

                builder.append(
                                value.trim());
        }

        private String safe(
                        String value) {

                return value == null
                                ? ""
                                : value.trim();
        }

        private String displayValue(
                        String value) {

                String normalized = safe(value);

                return normalized.isBlank()
                                ? "-"
                                : normalized;
        }

        // ================================================================
        // TABLE MODEL
        // ================================================================

        private class UserTableModel
                        extends AbstractTableModel {

                private final String[] columns = {
                                "User ID",
                                "Name",
                                "Email",
                                "Contact",
                                "Role",
                                "Firebase UID"
                };

                @Override
                public int getRowCount() {

                        return filteredUsers.size();
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

                        UserResponse user = filteredUsers.get(row);

                        return switch (column) {

                                case 0 ->
                                        user.getId() == null
                                                        ? "-"
                                                        : user.getId();

                                case 1 ->
                                        buildFullName(user);

                                case 2 ->
                                        displayValue(
                                                        user.getEmail());

                                case 3 ->
                                        displayValue(
                                                        user.getContactNumber());

                                case 4 ->
                                        safe(user.getRole())
                                                        .toUpperCase();

                                case 5 ->
                                        displayValue(
                                                        user.getFirebaseUid());

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
        // ROLE TABLE RENDERER
        // ================================================================

        private static class RoleCellRenderer
                        extends DefaultTableCellRenderer {

                @Override
                public Component getTableCellRendererComponent(
                                JTable table,
                                Object value,
                                boolean selected,
                                boolean focus,
                                int row,
                                int column) {

                        String role = String.valueOf(value);

                        AppBadge.Status badgeStatus;

                        switch (role) {

                                case "ADMIN" ->
                                        badgeStatus = AppBadge.Status.DANGER;

                                case "EMPLOYEE" ->
                                        badgeStatus = AppBadge.Status.SUCCESS;

                                case "COMMUTER" ->
                                        badgeStatus = AppBadge.Status.NEUTRAL;

                                default ->
                                        badgeStatus = AppBadge.Status.NEUTRAL;
                        }

                        AppBadge badge = new AppBadge(
                                        role,
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