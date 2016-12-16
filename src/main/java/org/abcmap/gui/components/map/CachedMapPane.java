package org.abcmap.gui.components.map;

import org.abcmap.core.events.manager.EventNotificationManager;
import org.abcmap.core.events.manager.HasEventNotificationManager;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.project.Project;
import org.abcmap.core.rendering.CacheRenderingEngine;
import org.abcmap.core.rendering.RenderingException;
import org.abcmap.gui.components.geo.MapNavigationBar;
import org.geotools.geometry.jts.ReferencedEnvelope;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

/**
 * Display a map by using a partial cache system
 * <p>
 * Cache is managed by a RenderedPartialFactory. This partial factory produce portions of map and store it in database.
 * <p>
 */
public class CachedMapPane extends JPanel implements HasEventNotificationManager {

    private static final CustomLogger logger = LogManager.getLogger(CachedMapPane.class);

    /**
     * Project associated with this panel
     */
    private final Project project;

    /**
     * Rendering engine associated with pane
     */
    private final CacheRenderingEngine renderingEngine;

    /**
     * World envelope (positions) of map rendered on panel
     */
    private ReferencedEnvelope worldEnvelope;

    /**
     * Size of partials side to render
     */
    private double partialSideWu;

    /**
     * Various mouse listeners which allow user to control map with mouse
     */
    private CachedMapPaneMouseController mouseControl;

    /**
     * Optional navigation bar in bottom of map
     */
    private MapNavigationBar navigationBar;

    private final EventNotificationManager notifm;

    public CachedMapPane(Project p) {

        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        addComponentListener(new RefreshMapComponentListener());

        this.project = p;
        this.renderingEngine = new CacheRenderingEngine(project);

        this.partialSideWu = 500;

        // first time render whole project
        this.worldEnvelope = project.getMaximumBounds();
        renderingEngine.setParametersToRenderWholeMap();

        // repaint when new partials are ready
        notifm = new EventNotificationManager(this);
        notifm.setDefaultListener((ev) -> {
            CachedMapPane.this.repaint();
        });
        renderingEngine.getNotificationManager().addObserver(this);

    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // refresh navigation bar
        if (navigationBar != null) {
            navigationBar.refreshBoundsFrom(getSize());
        }

        renderingEngine.paint((Graphics2D) g);
    }


    /**
     * Refresh list of partials to display in component
     */
    public void refreshMap() {

        Dimension panelDimensions = getSize();

        // panel is not displayed yet
        if (panelDimensions.getWidth() < 1 || panelDimensions.getHeight() < 1) {
            return;
        }

        try {
            renderingEngine.prepareMap(worldEnvelope, panelDimensions, partialSideWu);
        } catch (RenderingException e) {
            logger.error(e);
        }

        // repaint component
        repaint();

    }

    /**
     * Get current scale in panel
     *
     * @return
     */
    public double getScale() {
        return renderingEngine.getPartialSideWu() / renderingEngine.getPartialSidePx();
    }

    /**
     * Set current scale in panel
     *
     * @return
     */
    public void setScale(double scale) {
        partialSideWu = renderingEngine.getPartialSidePx() * scale;
    }

    /**
     * Reset display to show whole width of map, from upper left corner corner
     */
    public void resetDisplay() {

        // TODO center map at a different scale ?
        // this could avoid "blank screen" when layers are large but empty

        worldEnvelope = project.getMaximumBounds();
        renderingEngine.setParametersToRenderWholeMap();
        partialSideWu = renderingEngine.getPartialSideWu();
    }

    /**
     * Enable navigation bar with zoom in/out and center button
     *
     * @param val
     */
    public void setNavigationBarEnabled(boolean val) {

        // add navigation bar
        if (val) {
            navigationBar = new MapNavigationBar();
            add(navigationBar);
            navigationBar.refreshBoundsFrom(getSize());
        }

        // remove bar if present
        else {
            if (navigationBar != null) {
                remove(navigationBar);
                navigationBar = null;
            }
        }
    }

    public void setDebugMode(boolean debugMode) {
        renderingEngine.setDebugMode(debugMode);
    }

    /**
     * Observe this component and refresh map when needed
     */
    private class RefreshMapComponentListener implements ComponentListener {

        @Override
        public void componentResized(ComponentEvent e) {
            // refresh map if needed
            refreshMap();
        }

        @Override
        public void componentMoved(ComponentEvent e) {
            // refresh map if needed
            refreshMap();
        }

        @Override
        public void componentShown(ComponentEvent e) {
            // refresh map if needed
            refreshMap();
        }

        @Override
        public void componentHidden(ComponentEvent e) {

        }
    }

    /**
     * Enable or disable mouse control of map, in order to allow user to move or zoom map
     */
    public void setMouseManagementEnabled(boolean enabled) {

        if (enabled == true) {

            if (this.mouseControl != null) {
                return;
            }

            this.mouseControl = new CachedMapPaneMouseController(this);

            this.addMouseListener(mouseControl);
            this.addMouseMotionListener(mouseControl);
            this.addMouseWheelListener(mouseControl);

        } else {

            if (this.mouseControl == null) {
                return;
            }

            this.removeMouseListener(mouseControl);
            this.removeMouseMotionListener(mouseControl);
            this.removeMouseWheelListener(mouseControl);

            mouseControl = null;
        }
    }

    public Project getProject() {
        return project;
    }

    /**
     * Set envelope shown by component
     *
     * @param worldEnvelope
     */
    public void setWorldEnvelope(ReferencedEnvelope worldEnvelope) {

        if (worldEnvelope.getCoordinateReferenceSystem().equals(project.getCrs()) == false) {
            throw new IllegalStateException("Coordinate Reference Systems are different: " + worldEnvelope.getCoordinateReferenceSystem() + " / " + project.getCrs());
        }

        this.worldEnvelope = worldEnvelope;
    }

    /**
     * Get specified world envelope shown. It can be larger or greater than actual map shown
     *
     * @return
     */
    public ReferencedEnvelope getWorldEnvelope() {
        return new ReferencedEnvelope(worldEnvelope);
    }

    /**
     * Get shown world envelope, corresponding to component size
     *
     * @return
     */
    public ReferencedEnvelope getActualWorldEnvelope() {

        Dimension panelDimensionsPx = getSize();

        // get width and height in world unit
        double partialSidePx = renderingEngine.getPartialSidePx();
        double wdg = partialSideWu * panelDimensionsPx.width / partialSidePx;
        double hdg = partialSideWu * panelDimensionsPx.height / partialSidePx;

        double x1 = worldEnvelope.getMinX();
        double x2 = worldEnvelope.getMinX() + wdg;
        double y1 = worldEnvelope.getMinY();
        double y2 = worldEnvelope.getMinY() + hdg;

        return new ReferencedEnvelope(x1, x2, y1, y2, project.getCrs());
    }


    @Override
    public void repaint(long tm, int x, int y, int width, int height) {
        super.repaint(tm, x, y, width, height);

        // refresh navigation bar
        if (navigationBar != null) {
            navigationBar.refreshBoundsFrom(getSize());
        }
    }

    @Override
    public void revalidate() {
        super.revalidate();

        // refresh navigation bar
        if (navigationBar != null) {
            navigationBar.refreshBoundsFrom(getSize());
        }
    }

    @Override
    public EventNotificationManager getNotificationManager() {
        return notifm;
    }

}