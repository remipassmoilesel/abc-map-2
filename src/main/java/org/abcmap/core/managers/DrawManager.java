package org.abcmap.core.managers;

import org.abcmap.core.shapes.LineBuilder;
import org.abcmap.core.shapes.PointBuilder;
import org.abcmap.core.shapes.PolygonBuilder;
import org.abcmap.core.styles.StyleContainer;
import org.abcmap.core.styles.StyleLibrary;

import java.awt.*;

/**
 * Here are managed all operations around drawing shapes on map
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
    public LineBuilder getLineBuilder() {
        LineBuilder builder = new LineBuilder();
        builder.setStyle(getActiveStyle());
        return builder;

    }

    /**
     * Get a polygon builder, to draw polygons on current project
     *
     * @return
     */
    public PolygonBuilder getPolygonBuilder() {
        PolygonBuilder builder = new PolygonBuilder();
        builder.setStyle(getActiveStyle());
        return builder;
    }

    /**
     * Get a point builder, to draw points on current project
     *
     * @return
     */
    public PointBuilder getPointBuilder() {
        PointBuilder builder = new PointBuilder();
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

}
