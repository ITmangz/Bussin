package com.bussin.desktop.ui.components;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

/**
 * Small width-aware layout managers used to keep screens usable when the
 * window is resized. Heights always follow the children's preferred heights,
 * and the preferred width is intentionally small so a screen never forces a
 * horizontal scrollbar.
 */
public final class ResponsiveLayouts {

    /** Client property: extra vertical gap placed above a child in {@link Vertical}. */
    public static final String GAP_BEFORE = "bussin.gapBefore";

    private static final int MIN_PREFERRED_WIDTH = 320;

    private ResponsiveLayouts() {
    }

    // ================================================================
    // SHARED
    // ================================================================

    private abstract static class WidthAware implements LayoutManager {

        private int lastWidth = -1;

        @Override
        public void addLayoutComponent(String name, Component comp) {
        }

        @Override
        public void removeLayoutComponent(Component comp) {
        }

        /**
         * Child heights can depend on the width they were given, so when the
         * width changes we ask for one more layout pass with fresh sizes.
         */
        protected final void settle(Container parent) {

            int width = parent.getWidth();

            if (width != lastWidth) {

                lastWidth = width;

                SwingUtilities.invokeLater(() -> {
                    parent.invalidate();
                    parent.revalidate();
                });
            }
        }

        protected static int availableWidth(Container parent, int fallback) {

            Insets in = parent.getInsets();

            int width = parent.getWidth();

            if (width <= 0) {
                width = fallback + in.left + in.right;
            }

            return Math.max(0, width - in.left - in.right);
        }
    }

    // ================================================================
    // VERTICAL STACK (full-width blocks)
    // ================================================================

    public static final class Vertical extends WidthAware {

        private static int gapOf(Component c) {

            if (c instanceof JComponent jc) {

                Object value = jc.getClientProperty(GAP_BEFORE);

                if (value instanceof Integer gap) {
                    return gap;
                }
            }

            return 0;
        }

        @Override
        public Dimension preferredLayoutSize(Container parent) {

            Insets in = parent.getInsets();

            int height = in.top + in.bottom;

            for (Component c : parent.getComponents()) {

                if (c.isVisible()) {
                    height += gapOf(c) + c.getPreferredSize().height;
                }
            }

            return new Dimension(
                    MIN_PREFERRED_WIDTH + in.left + in.right,
                    height);
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            return preferredLayoutSize(parent);
        }

        @Override
        public void layoutContainer(Container parent) {

            Insets in = parent.getInsets();

            int width = availableWidth(parent, MIN_PREFERRED_WIDTH);

            int y = in.top;

            for (Component c : parent.getComponents()) {

                if (!c.isVisible()) {
                    continue;
                }

                y += gapOf(c);

                // Give the child its width first so its preferred height is accurate.
                c.setBounds(in.left, y, width, c.getHeight());

                int height = c.getPreferredSize().height;

                c.setBounds(in.left, y, width, height);

                y += height;
            }

            settle(parent);
        }
    }

    // ================================================================
    // EQUAL-WIDTH GRID (collapses to fewer columns when narrow)
    // ================================================================

    public static final class Grid extends WidthAware {

        private final int maxColumns;
        private final int minCellWidth;
        private final int gap;

        public Grid(int maxColumns, int minCellWidth, int gap) {
            this.maxColumns = maxColumns;
            this.minCellWidth = minCellWidth;
            this.gap = gap;
        }

        /** Largest column count that fits AND divides maxColumns evenly (4 -> 2 -> 1, never 3). */
        private int columns(int width) {

            int fit = Math.max(1, Math.min(maxColumns, (width + gap) / (minCellWidth + gap)));

            while (fit > 1 && maxColumns % fit != 0) {
                fit--;
            }

            return fit;
        }

        private int count(Container parent) {

            int n = 0;

            for (Component c : parent.getComponents()) {
                if (c.isVisible()) {
                    n++;
                }
            }

            return n;
        }

        private int cellHeight(Container parent) {

            int h = 0;

            for (Component c : parent.getComponents()) {
                if (c.isVisible()) {
                    h = Math.max(h, c.getPreferredSize().height);
                }
            }

            return h;
        }

        @Override
        public Dimension preferredLayoutSize(Container parent) {

            Insets in = parent.getInsets();

            int fallback = maxColumns * minCellWidth + (maxColumns - 1) * gap;

            int cols = columns(availableWidth(parent, fallback));

            int n = count(parent);

            int rows = n == 0 ? 0 : (n + cols - 1) / cols;

            int height = rows == 0 ? 0 : rows * cellHeight(parent) + (rows - 1) * gap;

            return new Dimension(
                    minCellWidth + in.left + in.right,
                    height + in.top + in.bottom);
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            return preferredLayoutSize(parent);
        }

        @Override
        public void layoutContainer(Container parent) {

            Insets in = parent.getInsets();

            int fallback = maxColumns * minCellWidth + (maxColumns - 1) * gap;

            int width = availableWidth(parent, fallback);

            int cols = columns(width);

            int cellW = (width - (cols - 1) * gap) / cols;

            int cellH = cellHeight(parent);

            int i = 0;

            for (Component c : parent.getComponents()) {

                if (!c.isVisible()) {
                    continue;
                }

                int col = i % cols;
                int row = i / cols;

                c.setBounds(
                        in.left + col * (cellW + gap),
                        in.top + row * (cellH + gap),
                        cellW,
                        cellH);

                i++;
            }

            settle(parent);
        }
    }

    // ================================================================
    // TWO-PANE SPLIT (stacks when narrow)
    // ================================================================

    public static final class Split extends WidthAware {

        private final int gap;
        private final int breakpoint;
        private final int leftWeight;
        private final int rightWeight;

        public Split(int gap, int breakpoint, int leftWeight, int rightWeight) {
            this.gap = gap;
            this.breakpoint = breakpoint;
            this.leftWeight = leftWeight;
            this.rightWeight = rightWeight;
        }

        @Override
        public Dimension preferredLayoutSize(Container parent) {

            Insets in = parent.getInsets();

            int width = availableWidth(parent, breakpoint);

            int height = 0;

            Component[] children = parent.getComponents();

            if (width >= breakpoint) {

                for (Component c : children) {
                    height = Math.max(height, c.getPreferredSize().height);
                }

            } else {

                for (int i = 0; i < children.length; i++) {
                    height += children[i].getPreferredSize().height + (i > 0 ? gap : 0);
                }
            }

            return new Dimension(
                    MIN_PREFERRED_WIDTH + in.left + in.right,
                    height + in.top + in.bottom);
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            return preferredLayoutSize(parent);
        }

        @Override
        public void layoutContainer(Container parent) {

            Insets in = parent.getInsets();

            int width = availableWidth(parent, breakpoint);

            Component[] c = parent.getComponents();

            if (c.length == 0) {
                return;
            }

            if (width >= breakpoint && c.length == 2) {

                int usable = width - gap;

                int leftW = usable * leftWeight / (leftWeight + rightWeight);

                int rightW = usable - leftW;

                int height = Math.max(
                        c[0].getPreferredSize().height,
                        c[1].getPreferredSize().height);

                c[0].setBounds(in.left, in.top, leftW, height);
                c[1].setBounds(in.left + leftW + gap, in.top, rightW, height);

            } else {

                int y = in.top;

                for (Component child : c) {

                    child.setBounds(in.left, y, width, child.getHeight());

                    int height = child.getPreferredSize().height;

                    child.setBounds(in.left, y, width, height);

                    y += height + gap;
                }
            }

            settle(parent);
        }
    }
}
