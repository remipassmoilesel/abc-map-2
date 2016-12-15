package org.abcmap.gui.layouts;

import net.miginfocom.swing.MigLayout;
import org.abcmap.core.project.Project;
import org.abcmap.core.project.layouts.LayoutSheet;
import org.abcmap.gui.GuiIcons;
import org.abcmap.gui.components.buttons.HtmlCheckbox;
import org.abcmap.gui.components.map.CachedMapPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * Created by remipassmoilesel on 14/12/16.
 */
public class LayoutPanel extends JPanel {

    private final CachedMapPane mapPane;
    private final int controlPaneWidthPx;
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

        // add map pane in center
        mapPane = new CachedMapPane(project);
        mapPane.setMouseManagementEnabled(true);
        mapPane.setBorder(BorderFactory.createLineBorder(Color.gray, 2));
        add(mapPane, BorderLayout.CENTER);

        // add control pane on right
        controlPaneWidthPx = 50;
        JPanel controlPane = new JPanel(new MigLayout("fillx, width " + controlPaneWidthPx + "!"));
        HtmlCheckbox chkActive = new HtmlCheckbox("");
        JButton btnReset = new JButton(GuiIcons.MAP_MOVECENTER);
        btnReset.addActionListener((ev) -> {
            mapPane.resetDisplay();
            mapPane.refreshMap();
            updateLayoutFromMap();
        });
        controlPane.add(chkActive, "alignx center, height 25px!, wrap 15");
        controlPane.add(btnReset, "alignx center, height 25px!, width " + (GuiIcons.MAP_MOVECENTER.getImage().getWidth(null) + 5) + "px!, wrap 15");
        add(controlPane, BorderLayout.EAST);

        // update layout when user use map
        MouseAdapter layoutUpdater = new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                updateLayoutFromMap();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                updateLayoutFromMap();
            }
        };
        mapPane.addMouseListener(layoutUpdater);
        mapPane.addMouseWheelListener(layoutUpdater);
    }

    private void refresh() {
        computeMaxDimensions();
    }

    public CachedMapPane getMapPane() {
        return mapPane;
    }

    /**
     * Compute max dimensions of component relative to maximum height and layout millimeter dimensions
     */
    private void computeMaxDimensions() {
        maxDimensions.height = maxHeight;
        maxDimensions.width = ((int) (maxHeight * layout.getWidthMm() / layout.getHeightMm())) + controlPaneWidthPx;
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

    private void updateLayoutFromMap() {

        System.out.println(mapPane.getWorldEnvelope());
        System.out.println(mapPane.getActualWorldEnvelope());

        layout.setEnvelope(mapPane.getActualWorldEnvelope());
    }

}
