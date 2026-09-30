package com.bussin.desktop.ui.components;

import com.bussin.desktop.ui.theme.BussinTheme;
import java.awt.Dimension;
import java.awt.Rectangle;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;
import javax.swing.ScrollPaneConstants;

/**
 * Standard scrollable page body: light-gray background, page padding, blocks
 * stacked at the full available width. Tracks the viewport width so content
 * never needs a horizontal scrollbar.
 */
public class PageContent extends JPanel implements Scrollable {

    public PageContent() {

        setBackground(BussinTheme.BACKGROUND);

        setLayout(new ResponsiveLayouts.Vertical());

        setBorder(BorderFactory.createEmptyBorder(
                BussinTheme.PAGE_PADDING,
                BussinTheme.PAGE_PADDING,
                BussinTheme.PAGE_PADDING,
                BussinTheme.PAGE_PADDING));
    }

    /** Adds a full-width block with the given gap above it. */
    public <T extends JComponent> T addBlock(T block, int gapBefore) {

        block.putClientProperty(ResponsiveLayouts.GAP_BEFORE, gapBefore);

        add(block);

        return block;
    }

    /** Wraps this page in a vertically scrolling pane. */
    public JScrollPane inScrollPane() {

        JScrollPane scroll = new JScrollPane(
                this,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        scroll.setBorder(null);
        scroll.getViewport().setBackground(BussinTheme.BACKGROUND);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        return scroll;
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visible, int orientation, int direction) {
        return 16;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visible, int orientation, int direction) {
        return Math.max(16, visible.height - 32);
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
}
