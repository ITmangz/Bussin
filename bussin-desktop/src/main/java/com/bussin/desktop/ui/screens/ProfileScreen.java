package com.bussin.desktop.ui.screens;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.time.LocalDate;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.bussin.desktop.services.AuthSession;
import com.bussin.desktop.services.FirebaseAuthService;
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
 * Backend-supported profile fields:
 * - User ID / Firebase UID
 * - Email
 * - First name
 * - Middle name
 * - Last name
 * - Gender
 * - Age
 * - Date of birth
 * - Contact number
 * - Role
 *
 * Address is currently not persisted by the backend.
 */
public class ProfileScreen extends JPanel {

        // ================================================================
        // USER DATA
        // ================================================================

        private String userRole;

        private String firstName = "";
        private String middleName = "";
        private String lastName = "";

        private String fullName;
        private String email;

        private String gender = "";
        private Integer age;
        private LocalDate dateOfBirth;

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

        private JLabel firstNameValue;
        private JLabel middleNameValue;
        private JLabel lastNameValue;
        private JLabel genderValue;
        private JLabel ageValue;
        private JLabel dateOfBirthValue;
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

                firstNameValue = new JLabel();
                middleNameValue = new JLabel();
                lastNameValue = new JLabel();

                genderValue = new JLabel();
                ageValue = new JLabel();
                dateOfBirthValue = new JLabel();

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
                                                "FIRST NAME",
                                                firstNameValue));

                information.add(
                                createInfoField(
                                                "MIDDLE NAME",
                                                middleNameValue));

                information.add(
                                createInfoField(
                                                "LAST NAME",
                                                lastNameValue));

                information.add(
                                createInfoField(
                                                "GENDER",
                                                genderValue));

                information.add(
                                createInfoField(
                                                "AGE",
                                                ageValue));

                information.add(
                                createInfoField(
                                                "DATE OF BIRTH",
                                                dateOfBirthValue));

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

                firstNameValue.setText(
                                "Loading...");

                middleNameValue.setText(
                                "Loading...");

                lastNameValue.setText(
                                "Loading...");

                genderValue.setText(
                                "Loading...");

                ageValue.setText(
                                "Loading...");

                dateOfBirthValue.setText(
                                "Loading...");

                emailValue.setText(
                                "Loading...");

                phoneValue.setText(
                                "Loading...");

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

                                                UserApiService.UserResponse user = UserApiService
                                                                .getCurrentUser();

                                                // ------------------------------------------------
                                                // Basic information
                                                // ------------------------------------------------

                                                firstName = safeValue(
                                                                user.getFirstName());

                                                middleName = safeValue(
                                                                user.getMiddleName());

                                                lastName = safeValue(
                                                                user.getLastName());

                                                fullName = buildFullName();

                                                if (fullName.isBlank()) {

                                                        fullName = "BUSSIN User";
                                                }

                                                email = safeValue(
                                                                user.getEmail());

                                                if (email.isBlank()) {

                                                        email = "Not provided";
                                                }

                                                // ------------------------------------------------
                                                // Additional profile information
                                                // ------------------------------------------------

                                                gender = safeValue(
                                                                user.getGender());

                                                age = user.getAge();

                                                dateOfBirth = user.getDateOfBirth();

                                                phone = safeValue(
                                                                user.getContactNumber());

                                                // ------------------------------------------------
                                                // Account information
                                                // ------------------------------------------------

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

                                                                        firstNameValue.setText(
                                                                                        "Unavailable");

                                                                        middleNameValue.setText(
                                                                                        "Unavailable");

                                                                        lastNameValue.setText(
                                                                                        "Unavailable");

                                                                        genderValue.setText(
                                                                                        "Unavailable");

                                                                        ageValue.setText(
                                                                                        "Unavailable");

                                                                        dateOfBirthValue.setText(
                                                                                        "Unavailable");

                                                                        emailValue.setText(
                                                                                        "Unavailable");

                                                                        phoneValue.setText(
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

                firstNameValue.setText(
                                displayValue(firstName));

                middleNameValue.setText(
                                displayValue(middleName));

                lastNameValue.setText(
                                displayValue(lastName));

                genderValue.setText(
                                displayValue(gender));

                ageValue.setText(
                                age == null
                                                ? "Not provided"
                                                : String.valueOf(age));

                dateOfBirthValue.setText(
                                dateOfBirth == null
                                                ? "Not provided"
                                                : dateOfBirth.toString());

                emailValue.setText(
                                displayValue(email));

                phoneValue.setText(
                                displayValue(phone));

                addressValue.setText(
                                displayValue(address));

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

                firstNameValue.revalidate();
                firstNameValue.repaint();

                middleNameValue.revalidate();
                middleNameValue.repaint();

                lastNameValue.revalidate();
                lastNameValue.repaint();

                genderValue.revalidate();
                genderValue.repaint();

                ageValue.revalidate();
                ageValue.repaint();

                dateOfBirthValue.revalidate();
                dateOfBirthValue.repaint();

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

                                        String newFirstName = nameParts[0];

                                        String newLastName = nameParts.length > 1
                                                        ? nameParts[1]
                                                        : "";

                                        if (newLastName.isBlank()) {

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
                                                                                                        newFirstName,
                                                                                                        newLastName);

                                                                        firstName = safeValue(
                                                                                        updatedUser.getFirstName());

                                                                        middleName = safeValue(
                                                                                        updatedUser.getMiddleName());

                                                                        lastName = safeValue(
                                                                                        updatedUser.getLastName());

                                                                        fullName = buildFullName();

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
                                360);

                dialog.setMinimumSize(
                                new Dimension(
                                                480,
                                                340));

                dialog.setLocationRelativeTo(this);

                JPanel root = new JPanel(
                                new BorderLayout());

                root.setBackground(
                                BussinTheme.BACKGROUND);

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
                                8,
                                0,
                                8,
                                0);

                String email = AuthSession.getEmail();

                if (email == null || email.isBlank()) {

                        JLabel errorLabel = new JLabel(
                                        "Unable to determine your account email.");

                        errorLabel.setFont(
                                        BussinTheme.BODY);

                        errorLabel.setForeground(
                                        BussinTheme.TEXT_MUTED);

                        gbc.gridy = 0;

                        content.add(
                                        errorLabel,
                                        gbc);

                } else {

                        JLabel messageLabel = new JLabel(
                                        "<html>"
                                                        + "A password reset link will be sent to your registered email address.<br>"
                                                        + "Use the link in the email to create your new password."
                                                        + "</html>");

                        messageLabel.setFont(
                                        BussinTheme.BODY);

                        messageLabel.setForeground(
                                        BussinTheme.TEXT_PRIMARY);

                        gbc.gridy = 0;

                        content.add(
                                        messageLabel,
                                        gbc);

                        JLabel emailLabel = new JLabel(
                                        "<html><b>Email:</b> "
                                                        + email
                                                        + "</html>");

                        emailLabel.setFont(
                                        BussinTheme.BODY);

                        emailLabel.setForeground(
                                        BussinTheme.TEXT_MUTED);

                        gbc.gridy = 1;

                        content.add(
                                        emailLabel,
                                        gbc);
                }

                JPanel buttons = new JPanel(
                                new FlowLayout(
                                                FlowLayout.RIGHT,
                                                8,
                                                12));

                buttons.setOpaque(false);

                AppButton cancelButton = new AppButton(
                                "Cancel",
                                AppButton.Variant.SECONDARY);

                AppButton sendButton = new AppButton(
                                "Send Reset Instructions");

                cancelButton.addActionListener(
                                event -> dialog.dispose());

                sendButton.addActionListener(
                                event -> {

                                        String emailAddress = AuthSession.getEmail();

                                        if (emailAddress == null
                                                        || emailAddress.isBlank()) {

                                                JOptionPane.showMessageDialog(
                                                                dialog,
                                                                "Your account email could not be determined.",
                                                                "Unable to Continue",
                                                                JOptionPane.ERROR_MESSAGE);

                                                return;
                                        }

                                        try {

                                                sendButton.setEnabled(false);

                                                boolean sent = FirebaseAuthService
                                                                .sendPasswordReset(
                                                                                emailAddress);

                                                if (sent) {

                                                        JOptionPane.showMessageDialog(
                                                                        dialog,
                                                                        "Password reset instructions have been sent to:\n\n"
                                                                                        + emailAddress
                                                                                        + "\n\n"
                                                                                        + "Please check your email and follow the reset link.",
                                                                        "Reset Email Sent",
                                                                        JOptionPane.INFORMATION_MESSAGE);

                                                        dialog.dispose();

                                                } else {

                                                        JOptionPane.showMessageDialog(
                                                                        dialog,
                                                                        "Firebase could not send the password reset email.\n\n"
                                                                                        + "Please verify the email address and try again.",
                                                                        "Password Reset Failed",
                                                                        JOptionPane.ERROR_MESSAGE);

                                                        sendButton.setEnabled(true);
                                                }

                                        } catch (Exception exception) {

                                                exception.printStackTrace();

                                                JOptionPane.showMessageDialog(
                                                                dialog,
                                                                "An error occurred while sending the password reset email.\n\n"
                                                                                + exception.getMessage(),
                                                                "Password Reset Error",
                                                                JOptionPane.ERROR_MESSAGE);

                                                sendButton.setEnabled(true);
                                        }
                                });

                buttons.add(cancelButton);
                buttons.add(sendButton);

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
        // COPY ACCOUNT INFORMATION
        // ================================================================

        private void copyAccountInformation() {

                String information = "BUSSIN ACCOUNT INFORMATION\n"
                                + "--------------------------\n"
                                + "First Name: "
                                + firstName
                                + "\n"
                                + "Middle Name: "
                                + middleName
                                + "\n"
                                + "Last Name: "
                                + lastName
                                + "\n"
                                + "Gender: "
                                + gender
                                + "\n"
                                + "Age: "
                                + (age == null ? "Not provided" : age)
                                + "\n"
                                + "Date of Birth: "
                                + (dateOfBirth == null
                                                ? "Not provided"
                                                : dateOfBirth)
                                + "\n"
                                + "Email: "
                                + email
                                + "\n"
                                + "Phone Number: "
                                + phone
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

        // ================================================================
        // VALUE HELPERS
        // ================================================================

        private String safeValue(
                        String value) {

                return value == null
                                ? ""
                                : value.trim();
        }

        private String displayValue(
                        String value) {

                return value == null || value.isBlank()
                                ? "Not provided"
                                : value;
        }

        private String buildFullName() {

                StringBuilder builder = new StringBuilder();

                if (firstName != null
                                && !firstName.isBlank()) {

                        builder.append(firstName.trim());
                }

                if (middleName != null
                                && !middleName.isBlank()) {

                        if (builder.length() > 0) {
                                builder.append(" ");
                        }

                        builder.append(middleName.trim());
                }

                if (lastName != null
                                && !lastName.isBlank()) {

                        if (builder.length() > 0) {
                                builder.append(" ");
                        }

                        builder.append(lastName.trim());
                }

                return builder.toString().trim();
        }
}