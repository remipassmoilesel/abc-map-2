package org.abcmap.core.managers;

import org.abcmap.core.project.layer.AbstractLayer;
import org.abcmap.core.project.layer.FeatureLayer;
import org.abcmap.core.draw.DrawManagerException;
import org.abcmap.core.draw.LineBuilder;
import org.abcmap.core.draw.PointBuilder;
import org.abcmap.core.draw.PolygonBuilder;
import org.abcmap.core.styles.StyleContainer;

import java.awt.*;

/**
 * Here are managed all operations around drawing draw on map
 */
public class DrawManager {

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

    public DrawManager() {

        this.projectMan = MainManager.getProjectManager();
        this.activeForeground = Color.blue;
        this.activeBackground = Color.green;
        this.activeThick = 5;

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
}
