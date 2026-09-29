package com.bussin.desktop.ui.components;

import com.bussin.desktop.ui.theme.BussinTheme;
import java.awt.*;
import javax.swing.*;

public class AppButton extends JButton {

        public enum Variant {
                PRIMARY,
                SECONDARY,
                GHOST,
                DANGER
        }

        private final Variant variant;

        public AppButton(String text) {
                this(text, Variant.PRIMARY);
        }

        public AppButton(
                        String text,
                        Variant variant) {

                super(text);

                this.variant = variant;

                setFont(
                                BussinTheme.BUTTON);

                setFocusPainted(false);

                setBorderPainted(false);

                setContentAreaFilled(false);

                setOpaque(false);

                setCursor(
                                Cursor.getPredefinedCursor(
                                                Cursor.HAND_CURSOR));

                setBorder(
                                BorderFactory.createEmptyBorder(
                                                10,
                                                18,
                                                10,
                                                18));

                applyForeground();
        }

        @Override
        public void setEnabled(boolean enabled) {

                super.setEnabled(enabled);

                if (enabled) {
                        applyForeground();
                } else {
                        setForeground(BussinTheme.TEXT_MUTED);
                }
        }

        private void applyForeground() {

                switch (variant) {

                        case PRIMARY,
                                        DANGER ->
                                setForeground(
                                                Color.WHITE);

                        case SECONDARY,
                                        GHOST ->
                                setForeground(
                                                BussinTheme.TEXT_PRIMARY);
                }
        }

        @Override
        protected void paintComponent(
                        Graphics g) {

                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                                RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);

                Color background;

                if (!isEnabled()) {

                        background = BussinTheme.SURFACE_ALT;

                } else if (getModel().isPressed()) {

                        background = switch (variant) {

                                case PRIMARY ->
                                        BussinTheme.PRIMARY_PRESSED;

                                case DANGER ->
                                        BussinTheme.CRIMSON;

                                case SECONDARY,
                                                GHOST ->
                                        BussinTheme.BORDER;
                        };

                } else if (getModel().isRollover()) {

                        background = switch (variant) {

                                case PRIMARY ->
                                        BussinTheme.PRIMARY_HOVER;

                                case DANGER ->
                                        BussinTheme.CRIMSON;

                                case SECONDARY ->
                                        BussinTheme.SURFACE_ALT;

                                case GHOST ->
                                        BussinTheme.PRIMARY_LIGHT;
                        };

                } else {

                        background = switch (variant) {

                                case PRIMARY ->
                                        BussinTheme.PRIMARY;

                                case SECONDARY,
                                                GHOST ->
                                        BussinTheme.SURFACE;

                                case DANGER ->
                                        BussinTheme.DANGER;
                        };
                }

                g2.setColor(background);

                g2.fillRoundRect(
                                0,
                                0,
                                getWidth(),
                                getHeight(),
                                BussinTheme.SMALL_RADIUS,
                                BussinTheme.SMALL_RADIUS);

                // --------------------------------------------------------
                // SECONDARY BORDER
                // --------------------------------------------------------

                if (variant == Variant.SECONDARY) {

                        g2.setColor(
                                        BussinTheme.BORDER_STRONG);

                        g2.drawRoundRect(
                                        0,
                                        0,
                                        getWidth() - 1,
                                        getHeight() - 1,
                                        BussinTheme.SMALL_RADIUS,
                                        BussinTheme.SMALL_RADIUS);
                }

                // --------------------------------------------------------
                // GHOST BORDER
                // --------------------------------------------------------

                if (variant == Variant.GHOST) {

                        g2.setColor(
                                        BussinTheme.BORDER);

                        g2.drawRoundRect(
                                        0,
                                        0,
                                        getWidth() - 1,
                                        getHeight() - 1,
                                        BussinTheme.SMALL_RADIUS,
                                        BussinTheme.SMALL_RADIUS);
                }

                g2.dispose();

                super.paintComponent(g);
        }
}