package com.bussin.desktop.ui.screens;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.bussin.desktop.services.UserApiService;
import com.bussin.desktop.ui.components.AppBadge;
import com.bussin.desktop.ui.components.AppButton;
import com.bussin.desktop.ui.components.AppCard;
import com.bussin.desktop.ui.components.AppLabel;
import com.bussin.desktop.ui.components.PageContent;
import com.bussin.desktop.ui.components.ResponsiveLayouts;
import com.bussin.desktop.ui.theme.BussinTheme;

/**
 * BUSSIN - Profile & Account Management
 *
 * Profile information is loaded from the BUSSIN API.
 *
 * Current backend-supported profile fields:
 * - User ID / Firebase UID
 * - Email
 * - First name
 * - Last name
 * - Role
 *
 * Phone number and address are currently displayed as
 * "Not provided" because they are not yet persisted
 * by the backend.
 */
public class ProfileScreen extends JPanel {

        // ================================================================
        // USER DATA
        // ================================================================

        private String userRole;

        private String fullName;
        private String email;
        private String phone = "";
        private String address = "";
        private String userId;

        private final String accountStatus = "Active";
        private final String memberSince = "September 2026";

        // ================================================================
        // DISPLAY COMPONENTS
        // ================================================================

        private JLabel avatarLabel;
        private JLabel nameHeader;
        private JLabel emailHeader;

        private AppBadge roleBadge;
        private AppBadge statusBadge;

        private JLabel nameValue;
        private JLabel emailValue;
        private JLabel phoneValue;
        private JLabel addressValue;

        private JLabel userIdValue;
        private JLabel roleValue;
        private JLabel statusValue;
        private JLabel memberSinceValue;

        // ================================================================
        // BUTTONS
        // ================================================================

        private AppButton editProfileButton;
        private AppButton changePasswordButton;
        private AppButton copyAccountButton;

        // ================================================================
        // CONSTRUCTOR
        // ================================================================

        public ProfileScreen(String userRole) {

                this.userRole = normalizeRole(userRole);

                initializeComponents();
                initializeUI();
                refreshProfile();
                loadCurrentUser();
        }

        // ================================================================
        // INITIALIZE COMPONENTS
        // ================================================================

        private void initializeComponents() {

                avatarLabel = new JLabel();

                nameHeader = new JLabel();
                emailHeader = new JLabel();

                roleBadge = new AppBadge(
                                "COMMUTER",
                                AppBadge.Status.INFO);

                statusBadge = new AppBadge(
                                "ACTIVE",
                                AppBadge.Status.SUCCESS);

                nameValue = new JLabel();

                emailValue = new JLabel();

                phoneValue = new JLabel();

                addressValue = new JLabel();

                userIdValue = new JLabel();

                roleValue = new JLabel();

                statusValue = new JLabel();

                memberSinceValue = new JLabel();

                editProfileButton = new AppButton(
                                "Edit Profile");

                changePasswordButton = new AppButton(
                                "Change Password");

                copyAccountButton = new AppButton(
                                "Copy Account Information",
                                AppButton.Variant.SECONDARY);
        }

        // ================================================================
        // UI
        // ================================================================

        private void initializeUI() {

                setOpaque(true);
                setBackground(BussinTheme.BACKGROUND);

                setLayout(
                                new BorderLayout());

                PageContent page = new PageContent();

                page.addBlock(
                                createHeader(),
                                0);

                page.addBlock(
                                createProfileHeader(),
                                20);

                page.addBlock(
                                createInformationSection(),
                                18);

                page.addBlock(
                                createAccountSection(),
                                18);

                page.addBlock(
                                createSecuritySection(),
                                18);

                add(
                                page.inScrollPane(),
                                BorderLayout.CENTER);
        }

        // ================================================================
        // HEADER
        // ================================================================

        private JPanel createHeader() {

                JPanel panel = new JPanel(
                                new BorderLayout());

                panel.setOpaque(false);

                JPanel titlePanel = new JPanel();

                titlePanel.setOpaque(false);

                titlePanel.setLayout(
                                new BoxLayout(
                                                titlePanel,
                                                BoxLayout.Y_AXIS));

                titlePanel.add(
                                AppLabel.title(
                                                "My Profile"));

                titlePanel.add(
                                Box.createVerticalStrut(5));

                titlePanel.add(
                                AppLabel.secondary(
                                                "Manage your personal information and account settings."));

                panel.add(
                                titlePanel,
                                BorderLayout.WEST);

                return panel;
        }

        // ================================================================
        // PROFILE HEADER
        // ================================================================

        private JPanel createProfileHeader() {

                AppCard card = new AppCard();

                card.setBorder(
                                BorderFactory.createEmptyBorder(
                                                24,
                                                24,
                                                24,
                                                24));

                card.setLayout(
                                new BorderLayout(
                                                20,
                                                0));

                // ------------------------------------------------------------
                // Avatar
                // ------------------------------------------------------------

                avatarLabel.setPreferredSize(
                                new Dimension(
                                                84,
                                                84));

                avatarLabel.setHorizontalAlignment(
                                SwingConstants.CENTER);

                avatarLabel.setVerticalAlignment(
                                SwingConstants.CENTER);

                avatarLabel.setOpaque(true);

                avatarLabel.setBackground(
                                BussinTheme.PRIMARY);

                avatarLabel.setForeground(
                                BussinTheme.TEXT_ON_PRIMARY);

                avatarLabel.setFont(
                                new Font(
                                                "Segoe UI",
                                                Font.BOLD,
                                                28));

                card.add(
                                avatarLabel,
                                BorderLayout.WEST);

                // ------------------------------------------------------------
                // Information
                // ------------------------------------------------------------

                JPanel information = new JPanel();

                information.setOpaque(false);

                information.setLayout(
                                new BoxLayout(
                                                information,
                                                BoxLayout.Y_AXIS));

                nameHeader.setFont(
                                BussinTheme.PAGE_TITLE);

                nameHeader.setForeground(
                                BussinTheme.TEXT_PRIMARY);

                emailHeader.setFont(
                                BussinTheme.BODY);

                emailHeader.setForeground(
                                BussinTheme.TEXT_SECONDARY);

                JPanel badges = new JPanel(
                                new FlowLayout(
                                                FlowLayout.LEFT,
                                                8,
                                                0));

                badges.setOpaque(false);

                badges.add(roleBadge);
                badges.add(statusBadge);

                information.add(nameHeader);

                information.add(
                                Box.createVerticalStrut(4));

                information.add(emailHeader);

                information.add(
                                Box.createVerticalStrut(12));

                information.add(badges);

                card.add(
                                information,
                                BorderLayout.CENTER);

                return card;
        }

        // ================================================================
        // PERSONAL INFORMATION
        // ================================================================

        private JPanel createInformationSection() {

                AppCard card = new AppCard();

                card.setBorder(
                                BorderFactory.createEmptyBorder(
                                                22,
                                                24,
                                                24,
                                                24));

                card.setLayout(
                                new BorderLayout(
                                                0,
                                                18));

                // ------------------------------------------------------------
                // Section header
                // ------------------------------------------------------------

                JPanel header = new JPanel(
                                new BorderLayout());

                header.setOpaque(false);

                header.add(
                                AppLabel.section(
                                                "Personal Information"),
                                BorderLayout.WEST);

                editProfileButton.addActionListener(
                                event -> showEditProfileDialog());

                header.add(
                                editProfileButton,
                                BorderLayout.EAST);

                card.add(
                                header,
                                BorderLayout.NORTH);

                // ------------------------------------------------------------
                // Information grid
                // ------------------------------------------------------------

                JPanel information = new JPanel(
                                new ResponsiveLayouts.Grid(
                                                2,
                                                280,
                                                20));

                information.setOpaque(false);

                information.add(
                                createInfoField(
                                                "FULL NAME",
                                                nameValue));

                information.add(
                                createInfoField(
                                                "EMAIL ADDRESS",
                                                emailValue));

                information.add(
                                createInfoField(
                                                "PHONE NUMBER",
                                                phoneValue));

                information.add(
                                createInfoField(
                                                "ADDRESS",
                                                addressValue));

                card.add(
                                information,
                                BorderLayout.CENTER);

                return card;
        }

        private JPanel createInfoField(
                        String title,
                        JLabel value) {

                JPanel panel = new JPanel();

                panel.setOpaque(false);

                panel.setLayout(
                                new BoxLayout(
                                                panel,
                                                BoxLayout.Y_AXIS));

                JLabel titleLabel = new JLabel(title);

                titleLabel.setFont(
                                BussinTheme.SMALL_BOLD);

                titleLabel.setForeground(
                                BussinTheme.TEXT_SECONDARY);

                value.setFont(
                                BussinTheme.BODY_MEDIUM);

                value.setForeground(
                                BussinTheme.TEXT_PRIMARY);

                panel.add(titleLabel);

                panel.add(
                                Box.createVerticalStrut(5));

                panel.add(value);

                return panel;
        }

        // ================================================================
        // ACCOUNT INFORMATION
        // ================================================================

        private JPanel createAccountSection() {

                AppCard card = new AppCard();

                card.setBorder(
                                BorderFactory.createEmptyBorder(
                                                22,
                                                24,
                                                24,
                                                24));

                card.setLayout(
                                new BorderLayout(
                                                0,
                                                18));

                card.add(
                                AppLabel.section(
                                                "Account Information"),
                                BorderLayout.NORTH);

                JPanel rows = new JPanel();

                rows.setOpaque(false);

                rows.setLayout(
                                new BoxLayout(
                                                rows,
                                                BoxLayout.Y_AXIS));

                rows.add(
                                createAccountRow(
                                                "User ID",
                                                userIdValue));

                rows.add(
                                createRowSeparator());

                rows.add(
                                createAccountRow(
                                                "Role",
                                                roleValue));

                rows.add(
                                createRowSeparator());

                rows.add(
                                createAccountRow(
                                                "Account Status",
                                                statusValue));

                rows.add(
                                createRowSeparator());

                rows.add(
                                createAccountRow(
                                                "Member Since",
                                                memberSinceValue));

                card.add(
                                rows,
                                BorderLayout.CENTER);

                // ------------------------------------------------------------
                // Copy button
                // ------------------------------------------------------------

                JPanel actions = new JPanel(
                                new FlowLayout(
                                                FlowLayout.LEFT,
                                                0,
                                                0));

                actions.setOpaque(false);

                copyAccountButton.addActionListener(
                                event -> copyAccountInformation());

                actions.add(copyAccountButton);

                card.add(
                                actions,
                                BorderLayout.SOUTH);

                return card;
        }

        private JPanel createAccountRow(
                        String title,
                        JLabel value) {

                JPanel row = new JPanel(
                                new BorderLayout());

                row.setOpaque(false);

                row.setBorder(
                                BorderFactory.createEmptyBorder(
                                                10,
                                                0,
                                                10,
                                                0));

                JLabel titleLabel = new JLabel(title);

                titleLabel.setFont(
                                BussinTheme.BODY);

                titleLabel.setForeground(
                                BussinTheme.TEXT_SECONDARY);

                value.setFont(
                                BussinTheme.BODY_MEDIUM);

                value.setForeground(
                                BussinTheme.TEXT_PRIMARY);

                value.setHorizontalAlignment(
                                SwingConstants.RIGHT);

                row.add(
                                titleLabel,
                                BorderLayout.WEST);

                row.add(
                                value,
                                BorderLayout.EAST);

                return row;
        }

        private JSeparator createRowSeparator() {

                JSeparator separator = new JSeparator();

                separator.setForeground(
                                BussinTheme.BORDER);

                return separator;
        }

        // ================================================================
        // SECURITY
        // ================================================================

        private JPanel createSecuritySection() {

                AppCard card = new AppCard();

                card.setBorder(
                                BorderFactory.createEmptyBorder(
                                                22,
                                                24,
                                                24,
                                                24));

                card.setLayout(
                                new BorderLayout(
                                                0,
                                                14));

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
                                AppLabel.section(
                                                "Security"));

                titlePanel.add(
                                Box.createVerticalStrut(4));

                titlePanel.add(
                                AppLabel.secondary(
                                                "Manage your account password."));

                header.add(
                                titlePanel,
                                BorderLayout.WEST);

                changePasswordButton.addActionListener(
                                event -> showChangePasswordDialog());

                header.add(
                                changePasswordButton,
                                BorderLayout.EAST);

                card.add(
                                header,
                                BorderLayout.CENTER);

                return card;
        }

        // ================================================================
        // LOAD CURRENT USER FROM API
        // ================================================================

        private void loadCurrentUser() {

                nameHeader.setText(
                                "Loading profile...");

                emailHeader.setText("");

                nameValue.setText(
                                "Loading...");

                emailValue.setText(
                                "Loading...");

                phoneValue.setText(
                                "Not provided");

                addressValue.setText(
                                "Not provided");

                userIdValue.setText(
                                "Loading...");

                roleValue.setText(
                                "Loading...");

                statusValue.setText(
                                accountStatus);

                memberSinceValue.setText(
                                memberSince);

                Thread profileThread = new Thread(
                                () -> {

                                        try {

                                                UserApiService.UserResponse user = UserApiService.getCurrentUser();

                                                String firstName = user.getFirstName();

                                                String lastName = user.getLastName();

                                                String combinedName = ((firstName == null)
                                                                ? ""
                                                                : firstName.trim())
                                                                + " "
                                                                + ((lastName == null)
                                                                                ? ""
                                                                                : lastName.trim());

                                                fullName = combinedName.trim();

                                                if (fullName.isBlank()) {

                                                        fullName = "BUSSIN User";
                                                }

                                                email = user.getEmail();

                                                if (email == null
                                                                || email.isBlank()) {

                                                        email = "Not provided";
                                                }

                                                userId = user.getFirebaseUid();

                                                if (userId == null
                                                                || userId.isBlank()) {

                                                        userId = String.valueOf(
                                                                        user.getId());
                                                }

                                                userRole = normalizeRole(
                                                                user.getRole());

                                                SwingUtilities.invokeLater(
                                                                this::refreshProfile);

                                        } catch (Exception exception) {

                                                SwingUtilities.invokeLater(
                                                                () -> {

                                                                        nameHeader.setText(
                                                                                        "Unable to load profile");

                                                                        emailHeader.setText(
                                                                                        "");

                                                                        nameValue.setText(
                                                                                        "Unavailable");

                                                                        emailValue.setText(
                                                                                        "Unavailable");

                                                                        userIdValue.setText(
                                                                                        "Unavailable");

                                                                        roleValue.setText(
                                                                                        "Unavailable");

                                                                        JOptionPane.showMessageDialog(
                                                                                        this,
                                                                                        "Unable to load your profile.\n\n"
                                                                                                        + exception.getMessage(),
                                                                                        "Profile Error",
                                                                                        JOptionPane.ERROR_MESSAGE);
                                                                });
                                        }
                                });

                profileThread.setDaemon(true);
                profileThread.start();
        }

        // ================================================================
        // REFRESH PROFILE
        // ================================================================

        private void refreshProfile() {

                // ------------------------------------------------------------
                // Header
                // ------------------------------------------------------------

                nameHeader.setText(
                                fullName == null || fullName.isBlank()
                                                ? "Loading profile..."
                                                : fullName);

                emailHeader.setText(
                                email == null
                                                ? ""
                                                : email);

                // ------------------------------------------------------------
                // Personal Information
                // ------------------------------------------------------------

                nameValue.setText(
                                fullName == null || fullName.isBlank()
                                                ? "Not provided"
                                                : fullName);

                emailValue.setText(
                                email == null || email.isBlank()
                                                ? "Not provided"
                                                : email);

                phoneValue.setText(
                                phone == null || phone.isBlank()
                                                ? "Not provided"
                                                : phone);

                addressValue.setText(
                                address == null || address.isBlank()
                                                ? "Not provided"
                                                : address);

                // ------------------------------------------------------------
                // Account Information
                // ------------------------------------------------------------

                userIdValue.setText(
                                userId == null || userId.isBlank()
                                                ? "Not provided"
                                                : userId);

                roleValue.setText(
                                getRoleDisplay());

                statusValue.setText(
                                accountStatus);

                memberSinceValue.setText(
                                memberSince);

                // ------------------------------------------------------------
                // Badges
                // ------------------------------------------------------------

                roleBadge.setText(
                                getRoleDisplay()
                                                .toUpperCase());

                statusBadge.setText(
                                accountStatus
                                                .toUpperCase());

                // ------------------------------------------------------------
                // Avatar
                // ------------------------------------------------------------

                avatarLabel.setText(
                                getInitials(fullName));

                // ------------------------------------------------------------
                // Force Swing UI update
                // ------------------------------------------------------------

                nameHeader.revalidate();
                nameHeader.repaint();

                emailHeader.revalidate();
                emailHeader.repaint();

                nameValue.revalidate();
                nameValue.repaint();

                emailValue.revalidate();
                emailValue.repaint();

                phoneValue.revalidate();
                phoneValue.repaint();

                addressValue.revalidate();
                addressValue.repaint();

                userIdValue.revalidate();
                userIdValue.repaint();

                roleValue.revalidate();
                roleValue.repaint();

                statusValue.revalidate();
                statusValue.repaint();

                memberSinceValue.revalidate();
                memberSinceValue.repaint();

                avatarLabel.revalidate();
                avatarLabel.repaint();

                revalidate();
                repaint();
        }

        // ================================================================
        // EDIT PROFILE DIALOG
        // ================================================================

        private void showEditProfileDialog() {

                JDialog dialog = new JDialog(
                                SwingUtilities.getWindowAncestor(this),
                                "Edit Profile",
                                Dialog.ModalityType.APPLICATION_MODAL);

                dialog.setDefaultCloseOperation(
                                JDialog.DISPOSE_ON_CLOSE);

                dialog.setSize(
                                520,
                                480);

                dialog.setMinimumSize(
                                new Dimension(
                                                480,
                                                440));

                dialog.setLocationRelativeTo(this);

                JPanel root = new JPanel(
                                new BorderLayout());

                root.setBackground(
                                BussinTheme.BACKGROUND);

                // ------------------------------------------------------------
                // Title
                // ------------------------------------------------------------

                JPanel titlePanel = new JPanel(
                                new BorderLayout());

                titlePanel.setOpaque(false);

                titlePanel.setBorder(
                                BorderFactory.createEmptyBorder(
                                                24,
                                                28,
                                                0,
                                                28));

                titlePanel.add(
                                AppLabel.section(
                                                "Edit Profile"),
                                BorderLayout.WEST);

                // ------------------------------------------------------------
                // Form
                // ------------------------------------------------------------

                JPanel content = new JPanel(
                                new GridBagLayout());

                content.setOpaque(false);

                content.setBorder(
                                BorderFactory.createEmptyBorder(
                                                20,
                                                28,
                                                10,
                                                28));

                GridBagConstraints gbc = new GridBagConstraints();

                gbc.gridx = 0;
                gbc.weightx = 1.0;
                gbc.fill = GridBagConstraints.HORIZONTAL;

                gbc.insets = new Insets(
                                6,
                                0,
                                6,
                                0);

                JTextField nameField = createDialogField(fullName);

                JTextField phoneField = createDialogField(phone);

                JTextField addressField = createDialogField(address);

                int row = 0;

                row = addDialogField(
                                content,
                                gbc,
                                row,
                                "Full Name",
                                nameField);

                row = addDialogField(
                                content,
                                gbc,
                                row,
                                "Phone Number",
                                phoneField);

                addDialogField(
                                content,
                                gbc,
                                row,
                                "Address",
                                addressField);

                // ------------------------------------------------------------
                // Buttons
                // ------------------------------------------------------------

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

                                        String newName = nameField.getText()
                                                        .trim();

                                        String newPhone = phoneField.getText()
                                                        .trim();

                                        String newAddress = addressField.getText()
                                                        .trim();

                                        // ------------------------------------------------
                                        // Validation
                                        // ------------------------------------------------

                                        if (newName.isEmpty()) {

                                                JOptionPane.showMessageDialog(
                                                                dialog,
                                                                "Full name is required.",
                                                                "Invalid Profile",
                                                                JOptionPane.WARNING_MESSAGE);

                                                nameField.requestFocus();

                                                return;
                                        }

                                        if (newPhone.isEmpty()) {

                                                JOptionPane.showMessageDialog(
                                                                dialog,
                                                                "Phone number is required.",
                                                                "Invalid Profile",
                                                                JOptionPane.WARNING_MESSAGE);

                                                phoneField.requestFocus();

                                                return;
                                        }

                                        if (newAddress.isEmpty()) {

                                                JOptionPane.showMessageDialog(
                                                                dialog,
                                                                "Address is required.",
                                                                "Invalid Profile",
                                                                JOptionPane.WARNING_MESSAGE);

                                                addressField.requestFocus();

                                                return;
                                        }

                                        // ------------------------------------------------
                                        // Split full name
                                        // ------------------------------------------------

                                        String[] nameParts = newName.split(
                                                        "\\s+",
                                                        2);

                                        String firstName = nameParts[0];

                                        String lastName = nameParts.length > 1
                                                        ? nameParts[1]
                                                        : "";

                                        if (lastName.isBlank()) {

                                                JOptionPane.showMessageDialog(
                                                                dialog,
                                                                "Please enter both your first and last name.",
                                                                "Invalid Profile",
                                                                JOptionPane.WARNING_MESSAGE);

                                                nameField.requestFocus();

                                                return;
                                        }

                                        // ------------------------------------------------
                                        // Update backend
                                        // ------------------------------------------------

                                        saveButton.setEnabled(false);

                                        Thread updateThread = new Thread(
                                                        () -> {

                                                                try {

                                                                        UserApiService.UserResponse updatedUser = UserApiService
                                                                                        .updateCurrentUser(
                                                                                                        firstName,
                                                                                                        lastName);

                                                                        fullName = ((updatedUser.getFirstName() == null)
                                                                                        ? ""
                                                                                        : updatedUser.getFirstName())
                                                                                        + " "
                                                                                        + ((updatedUser.getLastName() == null)
                                                                                                        ? ""
                                                                                                        : updatedUser.getLastName());

                                                                        fullName = fullName.trim();

                                                                        email = updatedUser.getEmail();

                                                                        userId = updatedUser.getFirebaseUid();

                                                                        userRole = normalizeRole(
                                                                                        updatedUser.getRole());

                                                                        /*
                                                                         * Phone and address are not yet
                                                                         * persisted by the backend.
                                                                         *
                                                                         * Keep them locally for this
                                                                         * application session only.
                                                                         */
                                                                        phone = newPhone;

                                                                        address = newAddress;

                                                                        SwingUtilities.invokeLater(
                                                                                        () -> {

                                                                                                refreshProfile();

                                                                                                dialog.dispose();

                                                                                                JOptionPane.showMessageDialog(
                                                                                                                this,
                                                                                                                "Your profile has been updated successfully.",
                                                                                                                "Profile Updated",
                                                                                                                JOptionPane.INFORMATION_MESSAGE);
                                                                                        });

                                                                } catch (Exception exception) {

                                                                        SwingUtilities.invokeLater(
                                                                                        () -> {

                                                                                                saveButton.setEnabled(
                                                                                                                true);

                                                                                                JOptionPane.showMessageDialog(
                                                                                                                dialog,
                                                                                                                "Unable to update your profile.\n\n"
                                                                                                                                + exception.getMessage(),
                                                                                                                "Profile Update Failed",
                                                                                                                JOptionPane.ERROR_MESSAGE);
                                                                                        });
                                                                }
                                                        });

                                        updateThread.setDaemon(true);
                                        updateThread.start();
                                });

                buttons.add(
                                cancelButton);

                buttons.add(
                                saveButton);

                // ------------------------------------------------------------
                // Dialog layout
                // ------------------------------------------------------------

                root.add(
                                titlePanel,
                                BorderLayout.NORTH);

                root.add(
                                content,
                                BorderLayout.CENTER);

                root.add(
                                buttons,
                                BorderLayout.SOUTH);

                dialog.setContentPane(root);

                dialog.setVisible(true);
        }

        // ================================================================
        // DIALOG FIELD
        // ================================================================

        private JTextField createDialogField(
                        String value) {

                JTextField field = new JTextField(
                                value == null
                                                ? ""
                                                : value);

                field.setPreferredSize(
                                new Dimension(
                                                0,
                                                38));

                return field;
        }

        private int addDialogField(
                        JPanel panel,
                        GridBagConstraints gbc,
                        int row,
                        String labelText,
                        JComponent field) {

                gbc.gridy = row++;

                JLabel label = new JLabel(
                                labelText);

                label.setFont(
                                BussinTheme.SMALL_BOLD);

                label.setForeground(
                                BussinTheme.TEXT_SECONDARY);

                panel.add(
                                label,
                                gbc);

                gbc.gridy = row++;

                panel.add(
                                field,
                                gbc);

                return row;
        }

        // ================================================================
        // CHANGE PASSWORD
        // ================================================================

        private void showChangePasswordDialog() {

                JDialog dialog = new JDialog(
                                SwingUtilities.getWindowAncestor(this),
                                "Change Password",
                                Dialog.ModalityType.APPLICATION_MODAL);

                dialog.setDefaultCloseOperation(
                                JDialog.DISPOSE_ON_CLOSE);

                dialog.setSize(
                                520,
                                500);

                dialog.setMinimumSize(
                                new Dimension(
                                                480,
                                                460));

                dialog.setLocationRelativeTo(this);

                JPanel root = new JPanel(
                                new BorderLayout());

                root.setBackground(
                                BussinTheme.BACKGROUND);

                // ------------------------------------------------------------
                // Title
                // ------------------------------------------------------------

                JPanel titlePanel = new JPanel(
                                new BorderLayout());

                titlePanel.setOpaque(false);

                titlePanel.setBorder(
                                BorderFactory.createEmptyBorder(
                                                24,
                                                28,
                                                0,
                                                28));

                titlePanel.add(
                                AppLabel.section(
                                                "Change Password"),
                                BorderLayout.WEST);

                // ------------------------------------------------------------
                // Form
                // ------------------------------------------------------------

                JPanel content = new JPanel(
                                new GridBagLayout());

                content.setOpaque(false);

                content.setBorder(
                                BorderFactory.createEmptyBorder(
                                                20,
                                                28,
                                                10,
                                                28));

                GridBagConstraints gbc = new GridBagConstraints();

                gbc.gridx = 0;
                gbc.weightx = 1.0;
                gbc.fill = GridBagConstraints.HORIZONTAL;

                gbc.insets = new Insets(
                                6,
                                0,
                                6,
                                0);

                JPasswordField currentPassword = createPasswordField();

                JPasswordField newPassword = createPasswordField();

                JPasswordField confirmPassword = createPasswordField();

                int row = 0;

                row = addDialogField(
                                content,
                                gbc,
                                row,
                                "Current Password",
                                currentPassword);

                row = addDialogField(
                                content,
                                gbc,
                                row,
                                "New Password",
                                newPassword);

                row = addDialogField(
                                content,
                                gbc,
                                row,
                                "Confirm New Password",
                                confirmPassword);

                JLabel hint = new JLabel(
                                "Password must contain at least 6 characters.");

                hint.setFont(
                                BussinTheme.SMALL);

                hint.setForeground(
                                BussinTheme.TEXT_MUTED);

                gbc.gridy = row;

                content.add(
                                hint,
                                gbc);

                // ------------------------------------------------------------
                // Buttons
                // ------------------------------------------------------------

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
                                "Update Password");

                cancelButton.addActionListener(
                                event -> dialog.dispose());

                saveButton.addActionListener(
                                event -> {

                                        String current = new String(
                                                        currentPassword
                                                                        .getPassword());

                                        String password = new String(
                                                        newPassword
                                                                        .getPassword());

                                        String confirmation = new String(
                                                        confirmPassword
                                                                        .getPassword());

                                        if (current.isBlank()) {

                                                showPasswordError(
                                                                dialog,
                                                                "Please enter your current password.");

                                                return;
                                        }

                                        if (password.length() < 6) {

                                                showPasswordError(
                                                                dialog,
                                                                "New password must contain at least 6 characters.");

                                                return;
                                        }

                                        if (!password.equals(
                                                        confirmation)) {

                                                showPasswordError(
                                                                dialog,
                                                                "New password and confirmation do not match.");

                                                return;
                                        }

                                        /*
                                         * Password persistence is intentionally
                                         * not implemented yet.
                                         *
                                         * Firebase password update will be
                                         * implemented in a later authentication
                                         * integration step.
                                         */

                                        JOptionPane.showMessageDialog(
                                                        dialog,
                                                        "Password update is not connected yet.",
                                                        "Coming Soon",
                                                        JOptionPane.INFORMATION_MESSAGE);
                                });

                buttons.add(
                                cancelButton);

                buttons.add(
                                saveButton);

                root.add(
                                titlePanel,
                                BorderLayout.NORTH);

                root.add(
                                content,
                                BorderLayout.CENTER);

                root.add(
                                buttons,
                                BorderLayout.SOUTH);

                dialog.setContentPane(root);

                dialog.setVisible(true);
        }

        private JPasswordField createPasswordField() {

                JPasswordField field = new JPasswordField();

                field.setPreferredSize(
                                new Dimension(
                                                0,
                                                38));

                return field;
        }

        private void showPasswordError(
                        Component parent,
                        String message) {

                JOptionPane.showMessageDialog(
                                parent,
                                message,
                                "Invalid Password",
                                JOptionPane.WARNING_MESSAGE);
        }

        // ================================================================
        // COPY ACCOUNT INFORMATION
        // ================================================================

        private void copyAccountInformation() {

                String information = "BUSSIN ACCOUNT INFORMATION\n"
                                + "--------------------------\n"
                                + "Name: "
                                + fullName
                                + "\n"
                                + "Email: "
                                + email
                                + "\n"
                                + "User ID: "
                                + userId
                                + "\n"
                                + "Role: "
                                + getRoleDisplay()
                                + "\n"
                                + "Status: "
                                + accountStatus
                                + "\n"
                                + "Member Since: "
                                + memberSince;

                Toolkit.getDefaultToolkit()
                                .getSystemClipboard()
                                .setContents(
                                                new StringSelection(
                                                                information),
                                                null);

                JOptionPane.showMessageDialog(
                                this,
                                "Account information copied to the clipboard.",
                                "Copied",
                                JOptionPane.INFORMATION_MESSAGE);
        }

        // ================================================================
        // ROLE DISPLAY
        // ================================================================

        private String getRoleDisplay() {

                return switch (userRole) {

                        case "ADMIN" ->
                                "Administrator";

                        case "EMPLOYEE" ->
                                "Employee";

                        case "COMMUTER",
                                        "USER" ->
                                "Commuter";

                        default ->
                                "Commuter";
                };
        }

        // ================================================================
        // AVATAR INITIALS
        // ================================================================

        private String getInitials(
                        String name) {

                if (name == null
                                || name.isBlank()) {

                        return "?";
                }

                String[] parts = name.trim()
                                .split("\\s+");

                if (parts.length == 1) {

                        return parts[0]
                                        .substring(
                                                        0,
                                                        Math.min(
                                                                        2,
                                                                        parts[0].length()))
                                        .toUpperCase();
                }

                return (parts[0].charAt(0)
                                + ""
                                + parts[parts.length - 1]
                                                .charAt(0))
                                .toUpperCase();
        }

        // ================================================================
        // ROLE NORMALIZATION
        // ================================================================

        private String normalizeRole(
                        String role) {

                if (role == null
                                || role.isBlank()) {

                        return "COMMUTER";
                }

                String normalized = role.trim()
                                .toUpperCase();

                return switch (normalized) {

                        case "ADMIN",
                                        "EMPLOYEE",
                                        "COMMUTER",
                                        "USER" ->
                                normalized;

                        default ->
                                "COMMUTER";
                };
        }
}