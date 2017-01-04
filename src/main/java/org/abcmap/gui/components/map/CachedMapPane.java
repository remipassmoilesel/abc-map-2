package org.abcmap.gui.components.map;

import com.vividsolutions.jts.awt.ShapeWriter;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import net.miginfocom.swing.MigLayout;
import org.abcmap.core.draw.builder.AffinePointTransformation;
import org.abcmap.core.events.CacheRenderingEvent;
import org.abcmap.core.events.ProjectEvent;
import org.abcmap.core.events.manager.EventNotificationManager;
import org.abcmap.core.events.manager.HasEventNotificationManager;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.DrawManager;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.managers.Main;
import org.abcmap.core.project.Project;
import org.abcmap.core.project.layers.AbmAbstractLayer;
import org.abcmap.core.rendering.CachedRenderingEngine;
import org.abcmap.core.utils.FeatureUtils;
import org.abcmap.core.utils.GeoUtils;
import org.abcmap.core.utils.Utils;
import org.abcmap.gui.components.geo.MapNavigationBar;
import org.abcmap.gui.tools.MapTool;
import org.abcmap.gui.utils.GuiUtils;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapContent;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.opengis.filter.FilterFactory2;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Display a map by using a partial cache system
 * <p>
 * Cache is managed by a RenderedPartialFactory. This partial factory produce portions of map and store it in database.
 * <p>
 */
public class CachedMapPane extends JPanel implements HasEventNotificationManager {

    private static final CustomLogger logger = LogManager.getLogger(CachedMapPane.class);

    private final static StyleFactory sf = FeatureUtils.getStyleFactory();
    private final static FilterFactory2 ff = FeatureUtils.getFilterFactory();
    private final static GeometryFactory geom = GeoUtils.getGeometryFactory();

    /**
     * Project associated with this panel
     */
    private final Project project;

    /**
     * Rendering engine associated with pane
     */
    private final CachedRenderingEngine renderingEngine;

    /**
     * Map content used to display features above all other layers
     * <p>
     * This content is in memory, features displayed here are more modifiable faster than in database.
     */
    private MapContent inMemoryMapContent;

    /**
     * Renderer used with mask content
     */
    private StreamingRenderer inMemoryLayerRenderer;


    /**
     * If set to true, panel will ask to current tool to paint if needed
     */
    private boolean acceptPaintFromTool;

    /**
     * If true, it is the first time panel is rendering
     */
    private boolean firstTimeRender;

    /**
     * World envelope of map rendered on panel
     */
    // TODO: try to delete this field ?
    private ReferencedEnvelope currentWorldEnvelope;

    /**
     * Various mouse listeners which allow user to control map with mouse
     */
    private CachedMapPaneMouseController mouseControl;

    /**
     * Optional navigation bar in bottom of map
     */
    private MapNavigationBar navigationBar;

    /**
     * If set to true, more information are displayed
     */
    private boolean debugMode;

    /**
     * If set tot true, layer bounds will be drawn (for debug purposes)
     */
    private boolean drawLayerBounds;

    /**
     * Maximal zoom factor relative to project width
     */
    private double maxZoomFactor;

    /**
     * List of layer and project bounds with properties, to draw in debug mode
     */
    private ArrayList<Object[]> debugBoundsList;

    private final EventNotificationManager notifm;
    private final DrawManager drawm;

    public CachedMapPane(Project p) {
        super(new MigLayout("fill"));

        drawm = Main.getDrawManager();
        this.acceptPaintFromTool = false;

        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        addComponentListener(new RefreshMapComponentListener());

        maxZoomFactor = 3;

        this.project = p;
        this.renderingEngine = new CachedRenderingEngine(project);

        // first time render whole project
        firstTimeRender = true;

        // repaint when new partials are ready
        notifm = new EventNotificationManager(this);
        notifm.setDefaultListener((ev) -> {

            // new partials are ready, only repaint
            if (CacheRenderingEvent.isNewPartialsEvent(ev) || CacheRenderingEvent.isPartialsUpdatedEvent(ev)) {
                CachedMapPane.this.repaint();
            }

            // map changed, prepare new and repaint
            else if (CacheRenderingEvent.isPartialsDeletedEvent(ev) || ev instanceof ProjectEvent) {
                refreshMap();
            }

        });

        // listen rendering new partials
        renderingEngine.getNotificationManager().addObserver(this);

        // listen map modifications
        Main.getProjectManager().getNotificationManager().addObserver(this);

        this.inMemoryLayerRenderer = GeoUtils.buildRenderer();

        debugBoundsList = new ArrayList<>();
        setDebugMode(false);

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // don't paint before all is ready
        if (firstTimeRender) {
            if (debugMode) {
                logger.warning(this.getClass().getSimpleName() + ".paintComponent(): Call rejected, before first rendering operation");
            }
            return;
        }

        // improve quality of painting
        Graphics2D g2d = (Graphics2D) g;
        GuiUtils.applyQualityRenderingHints(g2d);

        // paint map
        renderingEngine.paint(g2d);

        // paint mask
        if (inMemoryMapContent != null) {
            // do not use currentWorldEnvelope to prevent bad offsets
            //maskRenderer.paint(g2d, new Rectangle(getSize()), currentWorldEnvelope);

            inMemoryLayerRenderer.paint(g2d, new Rectangle(getSize()), renderingEngine.getWorldEnvelope());
        }

        // if debug mode enabled, paint world envelope asked
        if (debugMode) {
            paintGrid(g2d);
            paintBounds(g2d);
        }

        // let tool paint if needed
        if (acceptPaintFromTool && drawm.getCurrentTool() != null) {
            drawm.getCurrentTool().drawOnMainMap(g2d);
        }

    }

    /**
     * Paint grid for debug purposes
     *
     * @param g2d
     */
    private void paintGrid(Graphics2D g2d) {

        AffineTransform worldToScreenTransform = getWorldToScreenTransform();
        if (worldToScreenTransform != null) {

            Point2D blc = new Point2D.Double(currentWorldEnvelope.getMinX(), currentWorldEnvelope.getMinY());
            Point2D urc = new Point2D.Double(currentWorldEnvelope.getMaxX(), currentWorldEnvelope.getMaxY());
            blc = worldToScreenTransform.transform(blc, null);
            urc = worldToScreenTransform.transform(urc, null);

            int x = (int) blc.getX();
            int y = (int) urc.getY();
            int w = (int) Math.abs(urc.getX() - blc.getX());
            int h = (int) Math.abs(urc.getY() - blc.getY());

            g2d.setColor(Color.blue);
            int st = 2;
            g2d.setStroke(new BasicStroke(st));
            g2d.drawRect(x + st, y + st, w - st * 2, h - st * 2);

        }
    }


    /**
     * Refresh list of partials to display in component
     */
    public void refreshMap() {

        Dimension panelDimensions = getSize();

        // panel is not displayed yet, do not render
        if (panelDimensions.getWidth() < 1 || panelDimensions.getHeight() < 1) {
            if (debugMode) {
                logger.warning(this.getClass().getSimpleName() + ".refreshMap(): Call rejected, component too small " + panelDimensions);
            }
            return;
        }

        if (this.isVisible() == false) {
            if (debugMode) {
                logger.warning(this.getClass().getSimpleName() + ".refreshMap(): Call rejected, component not visible");
            }
            return;
        }

        // first time we have to render map,
        // render whole project
        if (firstTimeRender) {
            resetDisplay();
            firstTimeRender = false;
        }

        // ensure that envelope is valid and proportional to component
        checkWorldEnvelope();

        // prepare map to render
        try {
            renderingEngine.prepareMap(currentWorldEnvelope, panelDimensions);
        } catch (Exception e) {
            logger.error(e);
        }

        // prepare bounds
        if (debugMode) {
            prepareBounds();
        }

        // repaint component
        repaint();

    }

    /**
     * Ensure that envelope is valid and proportional to component
     */
    private void checkWorldEnvelope() {

        // TODO: prevent move out of bounds ?

        Dimension panelDimensions = getSize();

        double coeffPx = panelDimensions.getWidth() / panelDimensions.getHeight();
        double coeffWu = currentWorldEnvelope.getWidth() / currentWorldEnvelope.getHeight();

        if (Math.abs(coeffPx - coeffWu) > 0.001) {

            double widthWu = currentWorldEnvelope.getWidth();
            double heightWu = widthWu / coeffPx;

            double minx = currentWorldEnvelope.getMinX();
            double miny = currentWorldEnvelope.getMinY();
            double maxx = currentWorldEnvelope.getMaxX();
            double maxy = miny + heightWu;

            currentWorldEnvelope = new ReferencedEnvelope(minx, maxx, miny, maxy, currentWorldEnvelope.getCoordinateReferenceSystem());
        }

    }

    /**
     * Clear the special layer in memory
     */
    public void clearInMemoryLayer() {
        inMemoryMapContent.dispose();
        inMemoryMapContent = null;
    }

    /**
     * Set content of a special layer, drawn over all others, where all features are stored in memory.
     *
     * @param featureCollection
     * @param style
     */
    public void setInMemoryLayerContent(DefaultFeatureCollection featureCollection, Style style) {

        FeatureLayer inMemoryLayer = new FeatureLayer(featureCollection, style);
        this.inMemoryMapContent = new MapContent();
        inMemoryMapContent.addLayer(inMemoryLayer);

        inMemoryLayerRenderer.setMapContent(inMemoryMapContent);
    }

    /**
     * Pixel scale can be used to translate summary pixels to world unit
     *
     * @return
     */
    public double getScale() {
        return renderingEngine.getScale();
    }

    /**
     * @param direction
     */
    private void zoomEnvelope(int direction) {

        double projectWorldWidth = project.getMaximumBounds().getWidth();

        double minx;
        double maxx;
        double miny;
        double maxy;

        ReferencedEnvelope newEnv;

        // zoom in
        if (direction > 0) {

            double zoomStepW = currentWorldEnvelope.getWidth() / 10;
            double zoomStepH = currentWorldEnvelope.getHeight() * zoomStepW / currentWorldEnvelope.getWidth();

            minx = currentWorldEnvelope.getMinX() + zoomStepW;
            maxx = currentWorldEnvelope.getMaxX() - zoomStepW;

            miny = currentWorldEnvelope.getMinY() + zoomStepH;
            maxy = currentWorldEnvelope.getMaxY() - zoomStepH;

            newEnv = new ReferencedEnvelope(minx, maxx, miny, maxy, currentWorldEnvelope.getCoordinateReferenceSystem());
        }

        // zoom out
        else if (direction < 0) {

            // when zooming out, we need to have a 'zoom out step' greater than a 'zoom in step',
            // in order to restore previous envelope before zoom in, and reuse views in cache
            double zoomStepW = currentWorldEnvelope.getWidth() / 8;
            double zoomStepH = currentWorldEnvelope.getHeight() * zoomStepW / currentWorldEnvelope.getWidth();

            minx = currentWorldEnvelope.getMinX() - zoomStepW;
            maxx = currentWorldEnvelope.getMaxX() + zoomStepW;

            miny = currentWorldEnvelope.getMinY() - zoomStepH;
            maxy = currentWorldEnvelope.getMaxY() + zoomStepH;

            newEnv = new ReferencedEnvelope(minx, maxx, miny, maxy, currentWorldEnvelope.getCoordinateReferenceSystem());

        }

        // invalid argument
        else {
            throw new IllegalArgumentException("Invalid zoom direction: " + direction);
        }

        if (newEnv.getWidth() < projectWorldWidth * maxZoomFactor) {
            currentWorldEnvelope = newEnv;
        }

        // check if envelope is proportional
        checkWorldEnvelope();

    }

    /**
     * Zoom in one increment
     */
    public void zoomIn() {
        zoomEnvelope(1);
    }

    /**
     * Zoom out one increment
     */
    public void zoomOut() {
        zoomEnvelope(-1);
    }

    /**
     * Reset display to show whole width of map, from upper left corner corner
     */
    public void resetDisplay() {

        // TODO center map at a different scale ?
        // this could avoid "blank screen" when layers are large but empty

        // get world x and width
        ReferencedEnvelope projectBounds = project.getMaximumBounds();

        double worldWidthWu = projectBounds.getMaxX() - projectBounds.getMinX();
        double heightWu = getHeight() * worldWidthWu / getWidth();

        double minx = projectBounds.getMinX();
        double maxx = projectBounds.getMaxX();
        double miny = projectBounds.getMaxY() - heightWu;
        double maxy = projectBounds.getMaxY();

        currentWorldEnvelope = new ReferencedEnvelope(minx, maxx, miny, maxy, project.getCrs());

    }

    /**
     * Enable navigation bar with zoom in/out and center button
     *
     * @param val
     */
    public void setNavigationBarEnabled(boolean val) {

        // add navigation bar
        if (val) {
            navigationBar = new MapNavigationBar(this);
            add(navigationBar, "alignx right, aligny bottom");
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
        this.debugMode = debugMode;
        this.drawLayerBounds = debugMode;
        renderingEngine.setDebugMode(debugMode);
    }

    /**
     * Return a utility to transform coordinates
     *
     * @return
     */
    public AffineTransform getWorldToScreenTransform() {
        return renderingEngine.getWorldToScreenTransform();
    }

    /**
     * Return a utility to transform coordinates
     *
     * @return
     */
    public AffineTransform getScreenToWorldTransform() {
        return renderingEngine.getScreenToWorldTransform();
    }

    /**
     * Remove a tool from listener list
     *
     * @param tool
     */
    public void removeToolFromListeners(MapTool tool) {
        removeMouseListener(tool);
        removeMouseMotionListener(tool);
        removeMouseWheelListener(tool);
    }

    /**
     * Remove a tool from listener list
     *
     * @param tool
     */
    public void addToolToListeners(MapTool tool) {
        addMouseListener(tool);
        addMouseMotionListener(tool);
        addMouseWheelListener(tool);
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

        // enable management
        if (enabled == true) {

            if (this.mouseControl != null) {
                return;
            }

            this.mouseControl = new CachedMapPaneMouseController(this);

            this.addMouseListener(mouseControl);
            this.addMouseMotionListener(mouseControl);
            this.addMouseWheelListener(mouseControl);

        }

        // disable management
        else {

            if (this.mouseControl == null) {
                return;
            }

            this.removeMouseListener(mouseControl);
            this.removeMouseMotionListener(mouseControl);
            this.removeMouseWheelListener(mouseControl);

            mouseControl = null;
        }
    }

    /**
     * Get project associated with rendering engine
     *
     * @return
     */
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

        this.currentWorldEnvelope = worldEnvelope;

        // check if envelope is proportional
        checkWorldEnvelope();
    }

    /**
     * Paint layers and project bounds
     *
     * @param g2d
     */
    private void paintBounds(Graphics2D g2d) {

        if (drawLayerBounds == true) {
            AffineTransform worldToScreenTransform = getWorldToScreenTransform();
            ShapeWriter shapeWriter = new ShapeWriter(new AffinePointTransformation(worldToScreenTransform));

            ArrayList<Integer> yPositions = new ArrayList<>();

            int fontSize = 11;

            for (Object[] o : debugBoundsList) {
                g2d.setColor((Color) o[2]);
                g2d.setStroke(new BasicStroke(2));
                Shape shape = shapeWriter.toShape(JTS.toGeometry((Envelope) o[1]));
                g2d.draw(shape);

                int increment = (int) (fontSize * 2);
                int yPos = (int) shape.getBounds().getMinY() - increment;
                for (int i = 0; i < yPositions.size(); i++) {
                    Integer cPos = yPositions.get(i);
                    if (Math.abs(cPos - yPos) < increment / 2) {
                        yPos -= increment;
                    }
                }
                yPositions.add(yPos);

                g2d.setColor(Color.white);
                g2d.fillRect((int) shape.getBounds().getMinX() - 5, yPos - fontSize - 5, 400, fontSize * 2);

                g2d.setColor(Color.gray);
                g2d.drawRect((int) shape.getBounds().getMinX() - 5, yPos - fontSize - 5, 400, fontSize * 2);

                g2d.setColor((Color) o[2]);
                g2d.setFont(new Font(Font.DIALOG, Font.BOLD, 11));
                g2d.drawString(o[0].toString(), (int) shape.getBounds().getMinX(), yPos);
            }
        }

    }

    /**
     * Prepare bounds to draw in debug mode
     */
    public void prepareBounds() {

        debugBoundsList.clear();

        if (drawLayerBounds == true) {

            // add layers
            for (AbmAbstractLayer layer : project.getLayersList()) {
                try {
                    debugBoundsList.add(new Object[]{
                            "Layer: " + layer.getId(),
                            layer.getBounds().transform(project.getCrs(), true),
                            Utils.randColor()
                    });
                } catch (TransformException | FactoryException e) {
                    logger.error(e);
                }

            }

            // add project bounds
            debugBoundsList.add(new Object[]{
                    "Project bounds",
                    project.getMaximumBounds(),
                    Color.BLACK
            });

        }

    }


    /**
     * If set to true, panel will ask to current tool to paint if needed
     */
    public void setAcceptPaintFromTool(boolean acceptPaintFromTool) {
        this.acceptPaintFromTool = acceptPaintFromTool;
    }

    /**
     * If set to true, panel will ask to current tool to paint if needed
     */
    public boolean isAcceptPaintFromTool() {
        return acceptPaintFromTool;
    }

    /**
     * Get specified world envelope to show
     *
     * @return
     */
    public ReferencedEnvelope getWorldEnvelope() {

        checkWorldEnvelope();

        return new ReferencedEnvelope(currentWorldEnvelope);
    }

    @Override
    public EventNotificationManager getNotificationManager() {
        return notifm;
    }

}