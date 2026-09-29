package com.bussin.desktop.ui.components;

import com.bussin.desktop.ui.theme.BussinTheme;

import javax.swing.*;
import java.awt.*;

public class TopBar extends JPanel {

        private final JLabel pageTitle;

        public TopBar() {

                setPreferredSize(
                                new Dimension(
                                                0,
                                                BussinTheme.TOP_BAR_HEIGHT));

                setMinimumSize(
                                new Dimension(
                                                0,
                                                BussinTheme.TOP_BAR_HEIGHT));

                setBackground(
                                BussinTheme.SURFACE);

                setBorder(
                                BorderFactory.createMatteBorder(
                                                0,
                                                0,
                                                1,
                                                0,
                                                BussinTheme.BORDER));

                setLayout(
                                new BorderLayout());

                // ============================================================
                // PAGE TITLE
                // ============================================================

                pageTitle = new JLabel(
                                "Dashboard");

                pageTitle.setFont(
                                BussinTheme.CARD_TITLE);

                pageTitle.setForeground(
                                BussinTheme.TEXT_PRIMARY);

                pageTitle.setBorder(
                                BorderFactory.createEmptyBorder(
                                                0,
                                                28,
                                                0,
                                                0));

                add(
                                pageTitle,
                                BorderLayout.WEST);

                // ============================================================
                // RIGHT CONTENT
                // ============================================================

                add(
                                createRightContent(),
                                BorderLayout.EAST);
        }

        // ================================================================
        // RIGHT CONTENT
        // ================================================================

        private JPanel createRightContent() {

                JPanel panel = new JPanel(
                                new FlowLayout(
                                                FlowLayout.RIGHT,
                                                14,
                                                13));

                panel.setOpaque(false);

                // ------------------------------------------------------------
                // NOTIFICATIONS
                // ------------------------------------------------------------

                JButton notification = new JButton();

                notification.setIcon(
                                IconFactory.create(
                                                "notification",
                                                19,
                                                BussinTheme.TEXT_SECONDARY));

                notification.setToolTipText(
                                "Notifications");

                notification.setFocusPainted(false);

                notification.setBorderPainted(false);

                notification.setContentAreaFilled(false);

                notification.setCursor(
                                Cursor.getPredefinedCursor(
                                                Cursor.HAND_CURSOR));

                panel.add(notification);

                // ------------------------------------------------------------
                // SEPARATOR
                // ------------------------------------------------------------

                JSeparator separator = new JSeparator(
                                SwingConstants.VERTICAL);

                separator.setPreferredSize(
                                new Dimension(
                                                1,
                                                26));

                separator.setForeground(
                                BussinTheme.BORDER);

                panel.add(separator);

                // ------------------------------------------------------------
                // USER AVATAR
                // ------------------------------------------------------------

                JLabel avatar = new JLabel("A");

                avatar.setHorizontalAlignment(
                                SwingConstants.CENTER);

                avatar.setVerticalAlignment(
                                SwingConstants.CENTER);

                avatar.setFont(
                                new Font(
                                                "Segoe UI",
                                                Font.BOLD,
                                                12));

                avatar.setForeground(
                                Color.WHITE);

                avatar.setOpaque(true);

                avatar.setBackground(
                                BussinTheme.PRIMARY);

                avatar.setPreferredSize(
                                new Dimension(
                                                32,
                                                32));

                panel.add(avatar);

                // ------------------------------------------------------------
                // USER DETAILS
                // ------------------------------------------------------------

                JPanel userInfo = new JPanel();

                userInfo.setOpaque(false);

                userInfo.setLayout(
                                new BoxLayout(
                                                userInfo,
                                                BoxLayout.Y_AXIS));

                JLabel name = new JLabel(
                                "Administrator");

                name.setFont(
                                BussinTheme.SMALL_BOLD);

                name.setForeground(
                                BussinTheme.TEXT_PRIMARY);

                JLabel role = new JLabel(
                                "BUSSIN Staff");

                role.setFont(
                                BussinTheme.SMALL);

                role.setForeground(
                                BussinTheme.TEXT_SECONDARY);

                userInfo.add(name);

                userInfo.add(
                                Box.createVerticalStrut(1));

                userInfo.add(role);

                panel.add(userInfo);

                return panel;
        }

        // ================================================================
        // PAGE TITLE
        // ================================================================

        public void setPageTitle(
                        String title) {

                pageTitle.setText(title);
        }
}
