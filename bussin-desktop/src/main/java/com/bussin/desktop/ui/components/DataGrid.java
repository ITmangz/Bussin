package com.bussin.desktop.ui.components;

import com.bussin.desktop.ui.theme.BussinTheme;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 * Lightweight read-only table for summary lists (dashboard, reports).
 * Columns share the available width by weight and shrink with ellipsis
 * instead of overflowing. Cells may be any component (labels, badges).
 * Use JTable-based screens for sortable, selectable management tables.
 */
public class DataGrid extends JPanel {

    private static final Font BODY_BOLD = BussinTheme.BODY.deriveFont(Font.BOLD);

    private final double[] weights;
    private int nextRow = 1;
    private List<JComponent> lastRow = new ArrayList<>();

    public DataGrid(String[] headers, double[] weights) {

        this.weights = weights;

        setOpaque(false);
        setLayout(new GridBagLayout());

        for (int i = 0; i < headers.length; i++) {

            JLabel label = new JLabel(headers[i]);
            label.setFont(BussinTheme.SMALL_BOLD);
            label.setForeground(BussinTheme.TEXT_MUTED);

            add(wrap(label, border(6, 8, BussinTheme.BORDER_STRONG)), constraints(i, 0));
        }
    }

    /** Appends a row; pass one component per column. */
    public void addRow(Component... cells) {

        // The previous last row gets its divider back; the new last row has none.
        for (JComponent cell : lastRow) {
            cell.setBorder(border(10, 10, BussinTheme.BORDER));
        }

        List<JComponent> row = new ArrayList<>();

        for (int i = 0; i < cells.length; i++) {

            JComponent cell = wrap(cells[i], border(10, 10, null));

            row.add(cell);

            add(cell, constraints(i, nextRow));
        }

        lastRow = row;

        nextRow++;
    }

    // ================================================================
    // CELL HELPERS
    // ================================================================

    public static JLabel text(String value) {

        JLabel label = new JLabel(value);
        label.setFont(BussinTheme.BODY);
        label.setForeground(BussinTheme.TEXT_SECONDARY);

        return label;
    }

    public static JLabel strong(String value) {

        JLabel label = new JLabel(value);
        label.setFont(BODY_BOLD);
        label.setForeground(BussinTheme.TEXT_PRIMARY);

        return label;
    }

    public static JLabel colored(String value, Color color) {

        JLabel label = new JLabel(value);
        label.setFont(BODY_BOLD);
        label.setForeground(color);

        return label;
    }

    // ================================================================
    // INTERNALS
    // ================================================================

    private GridBagConstraints constraints(int column, int row) {

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = column;
        gbc.gridy = row;
        gbc.weightx = column < weights.length ? weights[column] : 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;

        return gbc;
    }

    private static Border border(int top, int bottom, Color divider) {

        Border padding = BorderFactory.createEmptyBorder(top, 0, bottom, 12);

        if (divider == null) {
            return padding;
        }

        return BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, divider),
                padding);
    }

    private static JComponent wrap(Component content, Border border) {

        JPanel cell = new JPanel(new BorderLayout());

        cell.setOpaque(false);
        cell.setBorder(border);

        // Allow columns to shrink below the content's natural width.
        cell.setMinimumSize(new Dimension(0, 0));

        if (content instanceof AppBadge) {

            JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            left.setOpaque(false);
            left.add(content);

            cell.add(left, BorderLayout.CENTER);

        } else {

            cell.add(content, BorderLayout.CENTER);
        }

        return cell;
    }
}
