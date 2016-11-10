package org.abcmap.core.utils;

import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Created by remipassmoilesel on 10/11/16.
 */
public class CRSUtils {

    public static final CustomLogger logger = LogManager.getLogger(CRSUtils.class);

    public static final CoordinateReferenceSystem WGS_84 = DefaultGeographicCRS.WGS84;

    /**
     * Generic 2D coordinate reference system. Prefer use of this instead of
     * DefaultEngineeringSystem.GENERIC2D because it can be a lot slower.
     */
    public static CoordinateReferenceSystem GENERIC_2D;

    static {
        try {
            GENERIC_2D = CRS.decode("EPSG:404000");
        } catch (FactoryException e) {
            logger.error(e);
        }
    }

}
