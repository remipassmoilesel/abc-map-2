package org.abcmap.gui.layouts;

import org.abcmap.core.project.Project;
import org.abcmap.core.project.layouts.LayoutSheet;
import org.abcmap.gui.components.map.CachedMapPane;

import javax.swing.*;
import java.awt.*;

/**
 * Created by remipassmoilesel on 14/12/16.
 */
public class LayoutPanel extends JPanel {

    private final CachedMapPane mapPane;
    private Project project;
    private int maxHeight;
    private Dimension maxDimensions;
    private LayoutSheet layout;

    public LayoutPanel(Project project, LayoutSheet lay, int maxHeight) {
        super(new BorderLayout());

        this.project = project;
        this.layout = lay;
        this.maxHeight = maxHeight;

        maxDimensions = new Dimension();
        computeMaxDimensions();

        setBorder(BorderFactory.createLineBorder(Color.gray, 2));

        // add map pane in center
        mapPane = new CachedMapPane(project);
        mapPane.setMouseManagementEnabled(true);
        add(mapPane, BorderLayout.CENTER);
    }

    private void refresh() {
        computeMaxDimensions();
    }

    /**
     * Compute max dimensions of component relative to maximum height and layout millimeter dimensions
     */
    private void computeMaxDimensions() {
        maxDimensions.height = maxHeight;
        maxDimensions.width = (int) (maxHeight * layout.getWidthMm() / layout.getHeightMm());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    /**
     * Get layout sheet associated with this panel
     *
     * @return
     */
    public LayoutSheet getLayoutSheet() {
        return layout;
    }

    /**
     * Override preferred size to limit size of sheet
     *
     * @return
     */
    @Override
    public Dimension getPreferredSize() {
        return maxDimensions;
    }

    /**
     * Override preferred size to limit size of sheet
     *
     * @return
     */
    @Override
    public Dimension getMinimumSize() {
        return maxDimensions;
    }

    /**
     * Override preferred size to limit size of sheet
     *
     * @return
     */
    @Override
    public Dimension getMaximumSize() {
        return maxDimensions;
    }

    /**
     * Get maximum height allowed
     *
     * @return
     */
    public int getMaxHeight() {
        return maxHeight;
    }

    /**
     * Set maximum height allowed
     *
     * @return
     */
    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
        refresh();
    }


}
