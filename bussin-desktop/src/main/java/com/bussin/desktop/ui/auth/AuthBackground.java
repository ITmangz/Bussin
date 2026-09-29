package com.bussin.desktop.ui.auth;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.bussin.desktop.ui.theme.BussinTheme;

public class AuthBackground extends JPanel {

        protected final JPanel authContentPanel;

        public AuthBackground() {

                setLayout(new BorderLayout());
                setBackground(BussinTheme.BACKGROUND);

                JPanel leftPanel = createBrandPanel();

                authContentPanel = new JPanel(new BorderLayout());
                authContentPanel.setBackground(Color.WHITE);
                authContentPanel.setBorder(
                                new EmptyBorder(
                                                40,
                                                55,
                                                40,
                                                55));

                add(leftPanel, BorderLayout.WEST);
                add(authContentPanel, BorderLayout.CENTER);
        }

        private JPanel createBrandPanel() {

                JPanel panel = new JPanel(new BorderLayout()) {

                        @Override
                        protected void paintComponent(Graphics g) {

                                super.paintComponent(g);

                                Graphics2D g2 = (Graphics2D) g.create();

                                g2.setRenderingHint(
                                                RenderingHints.KEY_ANTIALIASING,
                                                RenderingHints.VALUE_ANTIALIAS_ON);

                                int width = getWidth();
                                int height = getHeight();

                                /*
                                 * Main BUSSIN brand background.
                                 */
                                g2.setColor(
                                                BussinTheme.CHARCOAL);

                                g2.fillRect(
                                                0,
                                                0,
                                                width,
                                                height);

                                /*
                                 * Subtle red vertical accent.
                                 */
                                g2.setColor(
                                                BussinTheme.RED);

                                g2.fillRect(
                                                0,
                                                0,
                                                5,
                                                height);

                                /*
                                 * Very subtle decorative route lines.
                                 */
                                g2.setStroke(
                                                new BasicStroke(
                                                                1.2f));

                                g2.setColor(
                                                new Color(
                                                                255,
                                                                255,
                                                                255,
                                                                18));

                                int startX = width - 180;
                                int startY = height - 230;

                                g2.drawLine(
                                                startX,
                                                startY,
                                                width - 50,
                                                height - 80);

                                g2.drawLine(
                                                startX + 40,
                                                startY - 40,
                                                width - 20,
                                                height - 150);

                                g2.drawLine(
                                                startX - 20,
                                                startY + 90,
                                                width - 90,
                                                height - 20);

                                /*
                                 * Route nodes.
                                 */
                                g2.setColor(
                                                new Color(
                                                                255,
                                                                0,
                                                                0,
                                                                90));

                                drawNode(
                                                g2,
                                                width - 180,
                                                height - 230);

                                drawNode(
                                                g2,
                                                width - 50,
                                                height - 80);

                                drawNode(
                                                g2,
                                                width - 20,
                                                height - 150);

                                g2.dispose();
                        }
                };

                panel.setPreferredSize(
                                new Dimension(
                                                470,
                                                0));

                panel.setMinimumSize(
                                new Dimension(
                                                360,
                                                0));

                panel.setBorder(
                                new EmptyBorder(
                                                42,
                                                48,
                                                42,
                                                48));

                JPanel content = new JPanel();

                content.setOpaque(false);

                content.setLayout(
                                new BoxLayout(
                                                content,
                                                BoxLayout.Y_AXIS));

                /*
                 * ---------------------------------------------------------
                 * BUSSIN LOGO
                 * ---------------------------------------------------------
                 */

                JLabel logo = createImageLabel(
                                "/images/bussinlogokiosk.png",
                                135,
                                62);

                logo.setAlignmentX(
                                Component.LEFT_ALIGNMENT);

                content.add(logo);

                content.add(
                                Box.createVerticalGlue());

                /*
                 * Small section label.
                 */

                JLabel sectionLabel = new JLabel(
                                "BUS OPERATIONS SYSTEM");

                sectionLabel.setFont(
                                new Font(
                                                "Segoe UI",
                                                Font.BOLD,
                                                11));

                sectionLabel.setForeground(
                                new Color(
                                                255,
                                                255,
                                                255,
                                                150));

                sectionLabel.setAlignmentX(
                                Component.LEFT_ALIGNMENT);

                content.add(sectionLabel);

                content.add(
                                Box.createVerticalStrut(12));

                /*
                 * Main heading.
                 */

                JLabel heading = new JLabel(
                                "<html>"
                                                + "Smart Queueing<br>"
                                                + "<font color='#FF0000'>"
                                                + "for Every Journey"
                                                + "</font>"
                                                + "</html>");

                heading.setFont(
                                new Font(
                                                "Segoe UI",
                                                Font.BOLD,
                                                38));

                heading.setForeground(
                                Color.WHITE);

                heading.setAlignmentX(
                                Component.LEFT_ALIGNMENT);

                content.add(heading);

                content.add(
                                Box.createVerticalStrut(18));

                /*
                 * Description.
                 */

                JLabel description = new JLabel(
                                "<html>"
                                                + "Manage bookings, queues, trips, "
                                                + "and terminal operations<br>"
                                                + "through one centralized BUSSIN system."
                                                + "</html>");

                description.setFont(
                                new Font(
                                                "Segoe UI",
                                                Font.PLAIN,
                                                14));

                description.setForeground(
                                new Color(
                                                225,
                                                225,
                                                225));

                description.setAlignmentX(
                                Component.LEFT_ALIGNMENT);

                content.add(description);

                content.add(
                                Box.createVerticalStrut(28));

                /*
                 * Feature list.
                 */

                content.add(
                                createFeature(
                                                "Queue Management"));

                content.add(
                                Box.createVerticalStrut(9));

                content.add(
                                createFeature(
                                                "Trip & Route Operations"));

                content.add(
                                Box.createVerticalStrut(9));

                content.add(
                                createFeature(
                                                "Booking & Passenger Management"));

                content.add(
                                Box.createVerticalGlue());

                /*
                 * Bus image.
                 */

                JLabel busImage = createImageLabel(
                                "/images/homeimage.png",
                                235,
                                210);

                busImage.setAlignmentX(
                                Component.LEFT_ALIGNMENT);

                content.add(busImage);

                content.add(
                                Box.createVerticalStrut(20));

                /*
                 * Footer.
                 */

                JLabel footer = new JLabel(
                                "BUSSIN • Terminal Operations");

                footer.setFont(
                                new Font(
                                                "Segoe UI",
                                                Font.PLAIN,
                                                11));

                footer.setForeground(
                                new Color(
                                                255,
                                                255,
                                                255,
                                                100));

                footer.setAlignmentX(
                                Component.LEFT_ALIGNMENT);

                content.add(footer);

                panel.add(
                                content,
                                BorderLayout.CENTER);

                return panel;
        }

        private JPanel createFeature(
                        String text) {

                JPanel feature = new JPanel(
                                new FlowLayout(
                                                FlowLayout.LEFT,
                                                0,
                                                0));

                feature.setOpaque(false);

                feature.setAlignmentX(
                                Component.LEFT_ALIGNMENT);

                JLabel indicator = new JLabel(
                                "■");

                indicator.setFont(
                                new Font(
                                                "Segoe UI",
                                                Font.BOLD,
                                                9));

                indicator.setForeground(
                                BussinTheme.RED);

                JLabel label = new JLabel(
                                "  " + text);

                label.setFont(
                                new Font(
                                                "Segoe UI",
                                                Font.PLAIN,
                                                12));

                label.setForeground(
                                new Color(
                                                235,
                                                235,
                                                235));

                feature.add(indicator);
                feature.add(label);

                return feature;
        }

        private void drawNode(
                        Graphics2D g2,
                        int x,
                        int y) {

                g2.fillOval(
                                x - 4,
                                y - 4,
                                8,
                                8);
        }

        private JLabel createImageLabel(
                        String resourcePath,
                        int width,
                        int height) {

                JLabel label = new JLabel();

                java.net.URL resource = getClass().getResource(
                                resourcePath);

                if (resource == null) {
                        return label;
                }

                ImageIcon icon = new ImageIcon(resource);

                Image image = icon.getImage()
                                .getScaledInstance(
                                                width,
                                                height,
                                                Image.SCALE_SMOOTH);

                label.setIcon(
                                new ImageIcon(image));

                return label;
        }

        public JPanel getRightPanel() {
                return authContentPanel;
        }

        public void setAuthContent(
                        JComponent component) {

                authContentPanel.removeAll();

                authContentPanel.add(
                                component,
                                BorderLayout.CENTER);

                authContentPanel.revalidate();
                authContentPanel.repaint();
        }
}