package com.bussin.desktop.ui.components;

import com.bussin.desktop.ui.theme.BussinTheme;
import javax.swing.*;

public class AppBadge extends JLabel {

    public enum Status {
        SUCCESS,
        WARNING,
        DANGER,
        INFO,
        NEUTRAL
    }

    public AppBadge(
            String text,
            Status status) {

        super(text);

        setFont(
                BussinTheme.SMALL_BOLD);

        setHorizontalAlignment(
                SwingConstants.CENTER);

        setBorder(
                BorderFactory.createEmptyBorder(
                        5,
                        10,
                        5,
                        10));

        setOpaque(true);

        applyStatus(status);
    }

    private void applyStatus(
            Status status) {

        switch (status) {

            case SUCCESS -> {

                setForeground(
                        BussinTheme.SUCCESS);

                setBackground(
                        BussinTheme.SUCCESS_LIGHT);
            }

            case WARNING -> {

                setForeground(
                        BussinTheme.WARNING);

                setBackground(
                        BussinTheme.WARNING_LIGHT);
            }

            case DANGER -> {

                setForeground(
                        BussinTheme.DANGER);

                setBackground(
                        BussinTheme.DANGER_LIGHT);
            }

            case INFO -> {

                setForeground(
                        BussinTheme.TEXT_PRIMARY);

                setBackground(
                        BussinTheme.INFO_LIGHT);
            }

            case NEUTRAL -> {

                setForeground(
                        BussinTheme.TEXT_SECONDARY);

                setBackground(
                        BussinTheme.SURFACE_ALT);
            }
        }
    }
}