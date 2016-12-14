package org.abcmap.core.managers;

import org.abcmap.core.draw.*;
import org.abcmap.core.events.manager.EventNotificationManager;
import org.abcmap.core.events.manager.HasEventNotificationManager;
import org.abcmap.core.project.layers.AbstractLayer;
import org.abcmap.core.project.layers.FeatureLayer;
import org.abcmap.core.styles.StyleContainer;
import org.abcmap.gui.tools.ToolContainer;

import java.awt.*;
import java.util.ArrayList;

/**
 * Here are managed all operations around drawing draw on map
 */
public class DrawManager implements HasEventNotificationManager {

    private final ProjectManager projectMan;

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
    private EventNotificationManager notifm;
    private ToolContainer currentToolContainer;

    public DrawManager() {

        this.projectMan = MainManager.getProjectManager();
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
    public LineBuilder getLineBuilder() throws DrawManagerException {
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

    public StyleContainer getActiveStyle() {
        return projectMan.getProject().getStyle(activeForeground, activeBackground, activeThick);
    }

    public Color getActiveForeground() {
        return activeForeground;
    }

    public void setActiveForeground(Color activeForeground) {
        this.activeForeground = activeForeground;
    }

    public Color getActiveBackground() {
        return activeBackground;
    }

    public void setActiveBackground(Color activeBackground) {
        this.activeBackground = activeBackground;
    }

    /**
     * Return the current active layer if it is a feature layer, or throw an exception
     *
     * @return
     * @throws DrawManagerException
     */
    public FeatureLayer getActiveFeatureLayerOrThrow() throws DrawManagerException {

        AbstractLayer layer = projectMan.getProject().getActiveLayer();
        if (layer instanceof FeatureLayer == false) {
            throw new DrawManagerException("Active layer is not a feature layer");
        }

        return (FeatureLayer) layer;

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

    public Object getCurrentTool() {
        return new Object();
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

    public void setCurrentTool(ToolContainer currentTool) {

    }

    public ToolContainer getCurrentToolContainer() {
        return currentToolContainer;
    }
}
