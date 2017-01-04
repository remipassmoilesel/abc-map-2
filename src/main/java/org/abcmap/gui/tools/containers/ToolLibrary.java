package org.abcmap.gui.tools.containers;

/**
 * Created by remipassmoilesel on 11/12/16.
 */
public class ToolLibrary {

    public static final String LINE_TOOL = "LINE_TOOL";
    public static final String POLYGON_TOOL = "POLYGON_TOOL";
    public static final String ZOOM_TOOL = "ZOOM_TOOL";
    public static final String SELECTION_TOOL = "SELECTION_TOOL";

    private static ToolContainer[] containers;

    /**
     * Return an array of available tools
     *
     * @return
     */
    public static ToolContainer[] getAvailableTools() {

        if (containers == null) {
            containers = new ToolContainer[]{
                    new SelectionToolContainer(),
                    new LineToolContainer(),
                    new PolygonToolContainer(),
                    new ZoomToolContainer()
            };
        }

        return containers;
    }
}
