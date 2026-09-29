package com.bussin.desktop.ui.components;

import com.bussin.desktop.ui.theme.BussinTheme;
import java.awt.*;
import javax.swing.*;

public class StatCard extends AppCard {

        private final JLabel valueLabel;
        private final JLabel titleLabel;
        private final JLabel detailLabel;

        public StatCard(
                        String title,
                        String value,
                        String detail) {

                setLayout(new BorderLayout(0, 8));

                titleLabel = new JLabel(title);
                titleLabel.setFont(BussinTheme.SMALL_BOLD);
                titleLabel.setForeground(BussinTheme.TEXT_SECONDARY);

                valueLabel = new JLabel(value);

                valueLabel.setFont(
                                BussinTheme.STAT_VALUE);

                valueLabel.setForeground(
                                BussinTheme.TEXT_PRIMARY);

                detailLabel = new JLabel(detail);
                detailLabel.setFont(BussinTheme.SMALL);
                detailLabel.setForeground(BussinTheme.TEXT_MUTED);

                JPanel content = new JPanel();
                content.setOpaque(false);

                content.setLayout(new BoxLayout(
                                content,
                                BoxLayout.Y_AXIS));

                content.add(titleLabel);
                content.add(Box.createVerticalStrut(4));
                content.add(valueLabel);
                content.add(Box.createVerticalStrut(2));
                content.add(detailLabel);

                add(content, BorderLayout.CENTER);
        }
}