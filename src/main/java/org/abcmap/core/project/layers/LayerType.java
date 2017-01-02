package org.abcmap.core.project.layers;

import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;

/**
 * Types of layer
 */
public enum LayerType {


    /**
     * A features layer
     */
    FEATURES,

    /**
     * A tiles layer is a raster layer made of tiles created with Abc-Map
     */
    TILES,

    /**
     * A distant raster layer
     */
    WMS,

    /**
     * Layer displaying a shape file
     */
    SHAPE_FILE;

    private static final CustomLogger logger = LogManager.getLogger(LayerType.class);

    /**
     * Return the corresponding enum object or null.
     *
     * @param name
     * @return
     */
    public static LayerType safeValueOf(String name) {
        try {
            return LayerType.valueOf(name);
        } catch (Exception e) {
            logger.error(e);
            return null;
        }
    }
}
