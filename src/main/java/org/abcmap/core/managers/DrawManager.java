package org.abcmap.core.managers;

import org.abcmap.core.draw.DrawManagerException;
import org.abcmap.core.draw.LayerElement;
import org.abcmap.core.draw.builder.LineBuilder;
import org.abcmap.core.draw.builder.PointBuilder;
import org.abcmap.core.draw.builder.PolygonBuilder;
import org.abcmap.core.events.DrawManagerEvent;
import org.abcmap.core.events.manager.EventNotificationManager;
import org.abcmap.core.events.manager.HasEventNotificationManager;
import org.abcmap.core.project.layers.AbstractLayer;
import org.abcmap.core.project.layers.FeatureLayer;
import org.abcmap.core.styles.StyleContainer;
import org.abcmap.gui.components.map.CachedMapPane;
import org.abcmap.gui.tools.MapTool;
import org.abcmap.gui.tools.containers.ToolContainer;
import org.abcmap.gui.tools.containers.ToolLibrary;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

/**
 * Here are managed all operations around drawing draw on map
 */
public class DrawManager implements HasEventNotificationManager {

    private final ProjectManager projectm;

    /**
     * Present thick of lines, used if a new builder is returned
     */
    private final int activeThick;

    /**
     * Active color used to draw, if a new builder is returned
     */
    private Color activeForeground;

    /**
     * Active color used to draw, if a new builder is returned
     */
    private Color activeBackground;

    /**
     * Current tool activated
     */
    private ToolContainer currentToolContainer;

    private EventNotificationManager notifm;

    public DrawManager() {

        this.projectm = MainManager.getProjectManager();

        this.activeForeground = Color.blue;
        this.activeBackground = Color.green;
        this.activeThick = 5;

        notifm = new EventNotificationManager(DrawManager.this);

    }


    /**
     * Get a line builder, to draw lines on current project
     *
     * @return
     */
    public LineBuilder getLineBuilder(AffineTransform transform) throws DrawManagerException {

        FeatureLayer layer = getActiveFeatureLayerOrThrow();

        LineBuilder builder = new LineBuilder(layer);
        builder.setStyle(getActiveStyle());
        return builder;

    }

    /**
     * Get a polygon builder, to draw polygons on current project
     *
     * @return
     */
    public PolygonBuilder getPolygonBuilder() throws DrawManagerException {
        FeatureLayer layer = getActiveFeatureLayerOrThrow();

        PolygonBuilder builder = new PolygonBuilder(layer);
        builder.setStyle(getActiveStyle());
        return builder;
    }

    /**
     * Get a point builder, to draw points on current project
     *
     * @return
     */
    public PointBuilder getPointBuilder() throws DrawManagerException {
        FeatureLayer layer = getActiveFeatureLayerOrThrow();

        PointBuilder builder = new PointBuilder(layer);
        builder.setStyle(getActiveStyle());
        return builder;
    }

    /**
     * Return active style on map
     *
     * @return
     */
    public StyleContainer getActiveStyle() {
        return projectm.getProject().getStyle(activeForeground, activeBackground, activeThick);
    }

    /**
     * Return active foreground color used for drawing
     *
     * @return
     */
    public Color getActiveForeground() {
        return activeForeground;
    }

    /**
     * Set active foreground color used for drawing
     *
     * @param activeForeground
     */
    public void setActiveForeground(Color activeForeground) {
        this.activeForeground = activeForeground;
    }

    /**
     * Get active background color used for drawing
     *
     * @return
     */
    public Color getActiveBackground() {
        return activeBackground;
    }

    /**
     * Set active background color used for drawing
     *
     * @return
     */
    public void setActiveBackground(Color activeBackground) {
        this.activeBackground = activeBackground;
    }

    /**
     * Return the current active layer if it is a feature layer, or throw an exception
     *
     * @return
     * @throws DrawManagerException
     */
    private FeatureLayer getActiveFeatureLayerOrThrow() throws DrawManagerException {

        AbstractLayer layer = projectm.getProject().getActiveLayer();
        if (layer instanceof FeatureLayer == false) {
            throw new DrawManagerException("Active layer is not a feature layer");
        }

        return (FeatureLayer) layer;

    }

    /**
     * Set current tool
     * <p>
     * If new tool is set, return true. If not (because there is no map), return false.
     *
     * @param toolId
     * @return
     */
    public boolean setCurrentTool(String toolId) {

        ToolContainer toolCtr = null;
        for (ToolContainer ctr : ToolLibrary.getAvailableTools()) {
            if (ctr.getId().equals(toolId)) {
                toolCtr = ctr;
                break;
            }
        }

        if (toolCtr == null) {
            throw new IllegalArgumentException("Unknown id: " + toolId);
        }

        return setCurrentTool(toolCtr);
    }

    /**
     * Set current tool
     * <p>
     * If new tool is set, return true. If not (because there is no map), return false.
     *
     * @param toolContainer
     * @return
     */
    public boolean setCurrentTool(ToolContainer toolContainer) {

        CachedMapPane map = MainManager.getMapManager().getMainMap();

        // stop previous tool if needed
        if (currentToolContainer != null) {
            MapTool previousTool = currentToolContainer.getCurrentInstance();
            if (previousTool != null) {
                previousTool.stopWorking();

                if (map != null) {
                    map.removeToolFromListeners(previousTool);
                }
            }
        }

        // if map is not present, return false
        if (map == null) {
            return false;
        }

        // else, register tool as listener
        this.currentToolContainer = toolContainer;
        MapTool instance = toolContainer.getNewInstance();
        map.addToolToListeners(instance);

        // throw event
        notifm.fireEvent(new DrawManagerEvent(DrawManagerEvent.TOOL_CHANGED));

        return true;
    }

    /**
     * Return current tool container
     *
     * @return
     */
    public ToolContainer getCurrentToolContainer() {
        return currentToolContainer;
    }

    /**
     * Return current tool or null if no tool is set
     *
     * @return
     */
    public MapTool getCurrentTool() {

        // no tool is set
        if (getCurrentToolContainer() == null) {
            return null;
        }
        // a tool is set, return instance
        else {
            return getCurrentToolContainer().getCurrentInstance();
        }
    }

    public ArrayList<String> getAvailableSymbolSets() {
        return new ArrayList<>();
    }

    public Font getSymbolSetFont(String setName) throws DrawManagerException {
        return new Font("Dialog", Font.PLAIN, 20);
    }

    public ArrayList<Integer> getAvailableSymbolCodesFor(String setName) {
        return new ArrayList<>();
    }


    public LayerElement getFirstSelectedElement() {
        return getFirstSelectedElement((Class) null);
    }

    public LayerElement getFirstSelectedElement(Class filter) {
        return new LayerElement();
    }

    public LayerElement getFirstSelectedElement(ArrayList<? extends Class> filter) {
        return new LayerElement();
    }

    public String getReadableNameFor(Class<? extends LayerElement> aClass) {
        return "Readable name";
    }

    public int getInteractionAreaMargin() {
        return 5;
    }

    @Override
    public EventNotificationManager getNotificationManager() {
        return notifm;
    }


}
