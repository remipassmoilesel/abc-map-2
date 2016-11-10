package org.abcmap.core.utils;

import com.vividsolutions.jts.geom.GeometryFactory;
import org.geotools.geometry.jts.JTSFactoryFinder;

/**
 * Created by remipassmoilesel on 10/11/16.
 */
public class GeomUtils {

    public GeometryFactory getGeometryFactory(){
        return JTSFactoryFinder.getGeometryFactory();
    }

}
