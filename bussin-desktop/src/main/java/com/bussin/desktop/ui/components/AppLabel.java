package com.bussin.desktop.ui.components;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;

import com.bussin.desktop.ui.theme.BussinTheme;

public class AppLabel extends JLabel {

    public AppLabel(String text) {
        super(text);
        setFont(BussinTheme.BODY);
        setForeground(BussinTheme.TEXT_PRIMARY);
    }

    public AppLabel(String text, Font font, Color color) {
        super(text);
        setFont(font);
        setForeground(color);
    }

    public static AppLabel title(String text) {
        return new AppLabel(
                text,
                BussinTheme.PAGE_TITLE,
                BussinTheme.TEXT_PRIMARY);
    }

    public static AppLabel section(String text) {
        return new AppLabel(
                text,
                BussinTheme.SECTION_TITLE,
                BussinTheme.TEXT_PRIMARY);
    }

    public static AppLabel muted(String text) {
        return new AppLabel(
                text,
                BussinTheme.SMALL,
                BussinTheme.TEXT_MUTED);
    }

    public static AppLabel secondary(String text) {
        return new AppLabel(
                text,
                BussinTheme.BODY,
                BussinTheme.TEXT_SECONDARY);
    }
}