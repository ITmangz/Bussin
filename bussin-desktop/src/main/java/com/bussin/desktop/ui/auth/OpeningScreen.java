package com.bussin.desktop.ui.auth;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import com.bussin.desktop.ui.components.AppButton;

public class OpeningScreen extends JPanel {

        private final Runnable onStart;

        private final JLabel dateLabel;
        private final JLabel timeLabel;

        private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");

        private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

        public OpeningScreen(Runnable onStart) {

                this.onStart = onStart;

                setLayout(new BorderLayout());
                setOpaque(false);

                JPanel content = new BackgroundPanel();
                content.setLayout(new BorderLayout());

                // =========================================================
                // TOP BAR
                // =========================================================

                JPanel topBar = new JPanel(new BorderLayout());
                topBar.setOpaque(false);
                topBar.setBorder(
                                BorderFactory.createEmptyBorder(
                                                24,
                                                60,
                                                0,
                                                60));

                // BUSSIN LOGO
                JLabel logo = createImageLabel(
                                "/images/bussinlogokiosk.png",
                                120,
                                55);

                topBar.add(
                                logo,
                                BorderLayout.WEST);

                // DATE / TIME
                JPanel dateTimePanel = new JPanel();
                dateTimePanel.setOpaque(false);
                dateTimePanel.setLayout(
                                new BoxLayout(
                                                dateTimePanel,
                                                BoxLayout.Y_AXIS));

                dateLabel = new JLabel();
                dateLabel.setFont(
                                new Font(
                                                "Segoe UI",
                                                Font.BOLD,
                                                14));
                dateLabel.setForeground(Color.WHITE);
                dateLabel.setAlignmentX(
                                Component.CENTER_ALIGNMENT);

                timeLabel = new JLabel();
                timeLabel.setFont(
                                new Font(
                                                "Segoe UI",
                                                Font.BOLD,
                                                14));
                timeLabel.setForeground(Color.WHITE);
                timeLabel.setAlignmentX(
                                Component.CENTER_ALIGNMENT);

                dateTimePanel.add(dateLabel);
                dateTimePanel.add(timeLabel);

                topBar.add(
                                dateTimePanel,
                                BorderLayout.CENTER);

                // LANGUAGE
                JPanel languagePanel = new JPanel(
                                new FlowLayout(
                                                FlowLayout.RIGHT,
                                                6,
                                                0));

                languagePanel.setOpaque(false);

                JLabel languageIcon = createImageLabel(
                                "/images/Language.png",
                                40,
                                40);

                JLabel languageLabel = new JLabel("ENG");

                languageLabel.setFont(
                                new Font(
                                                "Segoe UI",
                                                Font.BOLD,
                                                15));

                languageLabel.setForeground(
                                Color.WHITE);

                languagePanel.add(languageIcon);
                languagePanel.add(languageLabel);

                topBar.add(
                                languagePanel,
                                BorderLayout.EAST);

                content.add(
                                topBar,
                                BorderLayout.NORTH);

                // =========================================================
                // MAIN CONTENT
                // =========================================================

                JPanel mainContent = new JPanel(
                                new GridBagLayout());

                mainContent.setOpaque(false);

                GridBagConstraints gbc = new GridBagConstraints();

                gbc.fill = GridBagConstraints.BOTH;

                // ---------------------------------------------------------
                // LEFT SIDE
                // ---------------------------------------------------------

                JPanel leftPanel = new JPanel();

                leftPanel.setOpaque(false);

                leftPanel.setLayout(
                                new BoxLayout(
                                                leftPanel,
                                                BoxLayout.Y_AXIS));

                JLabel title1 = createTitleLabel(
                                "Smart Queueing");

                title1.setForeground(
                                Color.decode("#FFD200"));

                JLabel title2 = createTitleLabel(
                                "for Every Journey");

                title2.setForeground(
                                Color.WHITE);

                JLabel title3 = createTitleLabel(
                                "with Bussin");

                title3.setForeground(
                                Color.WHITE);

                leftPanel.add(title1);
                leftPanel.add(title2);
                leftPanel.add(title3);

                leftPanel.add(
                                Box.createVerticalStrut(18));

                JLabel description = new JLabel(
                                "<html>"
                                                + "Designed for the Parañaque Integrated Terminal Exchange<br>"
                                                + "(PITX), Bussin streamlines boarding across all CALABARZON<br>"
                                                + "routes through a First-In, First-Out (FIFO) queue system,<br>"
                                                + "enabling faster, more organized, and efficient terminal<br>"
                                                + "operations."
                                                + "</html>");

                description.setFont(
                                new Font(
                                                "Segoe UI",
                                                Font.PLAIN,
                                                16));

                description.setForeground(
                                Color.WHITE);

                leftPanel.add(description);

                leftPanel.add(
                                Box.createVerticalStrut(20));

                AppButton startButton = new AppButton(
                                "Start Transaction");

                startButton.setPreferredSize(
                                new Dimension(
                                                435,
                                                55));

                startButton.setMaximumSize(
                                new Dimension(
                                                435,
                                                55));

                startButton.setAlignmentX(
                                Component.LEFT_ALIGNMENT);

                startButton.setFont(
                                new Font(
                                                "Segoe UI",
                                                Font.BOLD,
                                                16));

                startButton.setBackground(
                                Color.decode("#FFCC00"));

                startButton.setForeground(
                                Color.WHITE);

                startButton.addActionListener(
                                e -> onStart.run());

                leftPanel.add(startButton);

                // ---------------------------------------------------------
                // RIGHT SIDE
                // ---------------------------------------------------------

                JLabel homeImage = createImageLabel(
                                "/images/homeimage.png",
                                300,
                                270);

                // ---------------------------------------------------------
                // GRID
                // ---------------------------------------------------------

                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.weightx = 0.62;
                gbc.weighty = 1.0;
                gbc.insets = new Insets(
                                30,
                                80,
                                50,
                                20);

                mainContent.add(
                                leftPanel,
                                gbc);

                gbc.gridx = 1;
                gbc.weightx = 0.38;
                gbc.insets = new Insets(
                                30,
                                20,
                                50,
                                70);

                JPanel imageContainer = new JPanel(
                                new GridBagLayout());

                imageContainer.setOpaque(false);

                imageContainer.add(homeImage);

                mainContent.add(
                                imageContainer,
                                gbc);

                content.add(
                                mainContent,
                                BorderLayout.CENTER);

                add(content);

                // =========================================================
                // CLOCK
                // =========================================================

                updateDateTime();

                Timer timer = new Timer(
                                1000,
                                e -> updateDateTime());

                timer.start();
        }

        // =============================================================
        // HELPERS
        // =============================================================

        private JLabel createTitleLabel(
                        String text) {

                JLabel label = new JLabel(text);

                label.setFont(
                                new Font(
                                                "Segoe UI",
                                                Font.BOLD,
                                                50));

                label.setAlignmentX(
                                Component.LEFT_ALIGNMENT);

                return label;
        }

        private JLabel createImageLabel(
                        String resourcePath,
                        int width,
                        int height) {

                JLabel label = new JLabel();

                java.net.URL resource = getClass().getResource(
                                resourcePath);

                if (resource == null) {

                        label.setText(
                                        "");

                        label.setForeground(
                                        Color.WHITE);

                        return label;
                }

                ImageIcon original = new ImageIcon(resource);

                Image image = original
                                .getImage()
                                .getScaledInstance(
                                                width,
                                                height,
                                                Image.SCALE_SMOOTH);

                label.setIcon(
                                new ImageIcon(image));

                return label;
        }

        private void updateDateTime() {

                LocalDateTime now = LocalDateTime.now();

                dateLabel.setText(
                                now.format(
                                                dateFormatter));

                timeLabel.setText(
                                now.format(
                                                timeFormatter));
        }

        // =============================================================
        // BACKGROUND
        // =============================================================

        private static class BackgroundPanel
                        extends JPanel {

                private final Image backgroundImage;

                BackgroundPanel() {

                        setOpaque(false);

                        java.net.URL resource = getClass().getResource(
                                        "/images/KioskBg.png");

                        if (resource != null) {

                                backgroundImage = new ImageIcon(
                                                resource).getImage();

                        } else {

                                backgroundImage = null;
                        }
                }

                @Override
                protected void paintComponent(
                                Graphics g) {

                        super.paintComponent(g);

                        if (backgroundImage == null) {

                                g.setColor(
                                                new Color(
                                                                190,
                                                                0,
                                                                45));

                                g.fillRect(
                                                0,
                                                0,
                                                getWidth(),
                                                getHeight());

                                return;
                        }

                        Graphics2D g2 = (Graphics2D) g.create();

                        int width = getWidth();
                        int height = getHeight();

                        double scale = Math.max(
                                        (double) width
                                                        / backgroundImage.getWidth(null),
                                        (double) height
                                                        / backgroundImage.getHeight(null));

                        int imageWidth = (int) (backgroundImage.getWidth(null)
                                        * scale);

                        int imageHeight = (int) (backgroundImage.getHeight(null)
                                        * scale);

                        int x = (width - imageWidth) / 2;

                        int y = (height - imageHeight) / 2;

                        g2.drawImage(
                                        backgroundImage,
                                        x,
                                        y,
                                        imageWidth,
                                        imageHeight,
                                        this);

                        g2.dispose();
                }
        }
}