package com.bussin.desktop.ui.components;

import com.bussin.desktop.ui.theme.BussinTheme;
import java.awt.*;
import javax.swing.*;

public class AppCard extends JPanel {

        private final int radius = BussinTheme.LARGE_RADIUS;

        public AppCard() {

                setOpaque(false);

                setBorder(
                                BorderFactory.createEmptyBorder(
                                                18,
                                                18,
                                                18,
                                                18));
        }

        @Override
        protected void paintComponent(
                        Graphics g) {

                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                                RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);

                // --------------------------------------------------------
                // Shadow
                // --------------------------------------------------------

                g2.setColor(
                                new Color(
                                                0,
                                                0,
                                                0,
                                                10));

                g2.fillRoundRect(
                                2,
                                3,
                                getWidth() - 4,
                                getHeight() - 3,
                                radius,
                                radius);

                // --------------------------------------------------------
                // Surface
                // --------------------------------------------------------

                g2.setColor(
                                BussinTheme.SURFACE);

                g2.fillRoundRect(
                                0,
                                0,
                                getWidth() - 4,
                                getHeight() - 4,
                                radius,
                                radius);

                // --------------------------------------------------------
                // Border
                // --------------------------------------------------------

                g2.setColor(
                                BussinTheme.BORDER);

                g2.drawRoundRect(
                                0,
                                0,
                                getWidth() - 5,
                                getHeight() - 5,
                                radius,
                                radius);

                g2.dispose();

                super.paintComponent(g);
        }
}