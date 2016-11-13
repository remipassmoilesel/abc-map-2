package org.abcmap.core.project.layer;

/**
 * Created by remipassmoilesel on 10/11/16.
 */
public enum LayerType {

    /**
     * A features layer
     */
    FEATURES,
    /**
     * A tiles layer is a raster layer made of tiles created with Abc-Map
     */
    TILES;

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
            return null;
        }
    }
}
