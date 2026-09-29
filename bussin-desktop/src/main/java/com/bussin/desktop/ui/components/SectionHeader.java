package com.bussin.desktop.ui.components;

import com.bussin.desktop.ui.theme.BussinTheme;
import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/** Card section heading: title, optional subtitle, optional right-hand component. */
public class SectionHeader extends JPanel {

    public SectionHeader(String title, String subtitle) {
        this(title, subtitle, null);
    }

    public SectionHeader(String title, String subtitle, Component trailing) {

        setOpaque(false);

        setLayout(new BorderLayout(12, 0));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(BussinTheme.CARD_TITLE);
        titleLabel.setForeground(BussinTheme.TEXT_PRIMARY);
        text.add(titleLabel);

        if (subtitle != null && !subtitle.isBlank()) {

            JLabel subtitleLabel = new JLabel(subtitle);
            subtitleLabel.setFont(BussinTheme.SMALL);
            subtitleLabel.setForeground(BussinTheme.TEXT_MUTED);
            text.add(subtitleLabel);
        }

        add(text, BorderLayout.CENTER);

        if (trailing != null) {
            add(trailing, BorderLayout.EAST);
        }
    }
}
