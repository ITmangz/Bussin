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

                this(title, value, detail, null);
        }

        /**
         * @param iconName optional {@link IconFactory} icon shown in the top-right corner
         */
        public StatCard(
                        String title,
                        String value,
                        String detail,
                        String iconName) {

                setLayout(new BorderLayout(12, 0));

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

                if (iconName != null) {

                        JLabel icon = new JLabel(
                                        IconFactory.create(
                                                        iconName,
                                                        20,
                                                        BussinTheme.PRIMARY));

                        icon.setHorizontalAlignment(SwingConstants.CENTER);
                        icon.setOpaque(true);
                        icon.setBackground(BussinTheme.PRIMARY_LIGHT);
                        icon.setPreferredSize(new Dimension(40, 40));

                        JPanel iconHolder = new JPanel(new BorderLayout());
                        iconHolder.setOpaque(false);
                        iconHolder.add(icon, BorderLayout.NORTH);

                        add(iconHolder, BorderLayout.EAST);
                }
        }
}
