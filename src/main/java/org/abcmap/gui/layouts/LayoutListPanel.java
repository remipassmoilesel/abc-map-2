package org.abcmap.gui.layouts;

import net.miginfocom.swing.MigLayout;
import org.abcmap.core.configuration.ConfigurationConstants;
import org.abcmap.core.events.ProjectEvent;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.GuiManager;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.managers.ProjectManager;
import org.abcmap.core.project.Project;
import org.abcmap.core.project.layouts.LayoutSheet;
import org.abcmap.gui.GuiIcons;
import org.abcmap.gui.components.buttons.HtmlCheckbox;
import org.abcmap.gui.utils.GuiUtils;
import org.abcmap.gui.windows.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;

/**
 * Created by remipassmoilesel on 14/12/16.
 */
public class LayoutListPanel extends JPanel {

    private static final CustomLogger logger = LogManager.getLogger(MainWindow.class);

    /**
     * List of last rendered layouts, to avoid too many changes when reconstruct panel
     */
    private final ArrayList<LayoutPanel> layoutPanels;

    /**
     * Scroll pane in center of panel where are sicplayed layout sheet
     */
    private final JScrollPane scrollPane;

    /**
     * Viewport inside main scroll pane
     */
    private final JPanel viewPort;

    /**
     * Max height in pixel of sheets
     */
    private int maxHeight;

    /**
     * Max height in percent relative to the total height of panel, which a sheet can occupy
     */
    private double maxHeightPercentOfSheets = 0.7;

    private final Project project;
    private final ProjectManager projectm;
    private final GuiManager guim;

    public LayoutListPanel(Project project) {
        super(new BorderLayout());

        this.project = project;
        projectm = MainManager.getProjectManager();
        guim = MainManager.getGuiManager();

        // keep created panels to avoid too much work
        layoutPanels = new ArrayList<>();

        // show all in scroll pane
        viewPort = new JPanel(new MigLayout("insets 5, gap 5, fillx")); // fillx needed to center components
        scrollPane = new JScrollPane(viewPort);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(ConfigurationConstants.SCROLLBAR_UNIT_INCREMENT);
        scrollPane.getVerticalScrollBar().setUnitIncrement(ConfigurationConstants.SCROLLBAR_UNIT_INCREMENT);
        add(scrollPane, BorderLayout.CENTER);

        // listen project manager for changes
        projectm.getNotificationManager().addSimpleListener(this, (ev) -> {
            if (ev instanceof ProjectEvent && ev.getName().equals(ProjectEvent.LAYOUTS_LIST_CHANGED)) {
                SwingUtilities.invokeLater(() -> {
                    reconstruct();
                });
            }
        });

        addComponentListener(new LayoutsUpdater());

        // compute max size of panels relative to main window
        maxHeight = computeMaxHeight();

        reconstruct();
    }

    /**
     * Reconstruct whole panel
     */
    private void reconstruct() {

        GuiUtils.throwIfNotOnEDT();

        // clean panel
        viewPort.removeAll();

        ArrayList<LayoutSheet> layouts = project.getLayouts();

        // remove all panels out of layout list bounds
        while (layoutPanels.size() > layouts.size()) {
            layoutPanels.remove(layoutPanels.size() - 1);
        }

        // iterate layouts
        for (int i = 0; i < layouts.size(); i++) {
            LayoutSheet layout = layouts.get(i);
            LayoutPanel panel = null;

            // try to get corresponding panel (may not exist yet)
            try {
                panel = layoutPanels.get(i);
            } catch (Exception e) {
                logger.debug(e);
            }

            // update panel if necessary
            if (panel == null
                    || panel.getLayoutSheet().equals(layout) == false
                    || panel.getMaxHeight() != maxHeight) {

                panel = new LayoutPanel(project, layout, maxHeight);
                layoutPanels.add(panel);
            }

            panel.setMaxHeight(maxHeight);

            // add it to viewport
            viewPort.add(panel, "align center, wrap 15");
        }

        refreshLayoutDimensions();
    }

    /**
     * Refresh dimensions of all components
     */
    public void refreshLayoutDimensions() {

        GuiUtils.throwIfNotOnEDT();

        // compute max size of panels relative to main window
        maxHeight = computeMaxHeight();

        for (LayoutPanel panel : layoutPanels) {
            panel.setMaxHeight(maxHeight);
        }

        viewPort.revalidate();
        viewPort.repaint();

        this.revalidate();
        this.repaint();

    }

    private int computeMaxHeight() {
        Dimension mainWin = guim.getMainWindow().getSize();
        return (int) (mainWin.height * maxHeightPercentOfSheets);
    }

    /**
     * Refresh layouts panel when needed
     */
    private class LayoutsUpdater implements ComponentListener {
        @Override
        public void componentResized(ComponentEvent e) {
            refreshLayoutDimensions();
        }

        @Override
        public void componentMoved(ComponentEvent e) {
            refreshLayoutDimensions();
        }

        @Override
        public void componentShown(ComponentEvent e) {
            refreshLayoutDimensions();
        }

        @Override
        public void componentHidden(ComponentEvent e) {

        }
    }
}
