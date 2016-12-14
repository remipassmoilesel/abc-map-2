package org.abcmap.core.project.layers;

import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;

import java.io.IOException;

/**
 * Created by remipassmoilesel on 13/11/16.
 */
public class LayerVisitExecutor {

    private final static CustomLogger logger = LogManager.getLogger(LayerVisitExecutor.class);
    private final FeatureLayer layer;

    public LayerVisitExecutor(FeatureLayer layer) {
        this.layer = layer;
    }

    /**
     * Execute a layer visit
     *
     * @param visitor
     * @param filter
     */
    public void execute(LayerVisitor visitor, Filter filter) {

        try {

            // get the feature source
            SimpleFeatureIterator iterator = null;
            if (filter == null) {
                iterator = layer.getFeatureSource().getFeatures().features();
            } else {
                iterator = layer.getFeatureSource().getFeatures(filter).features();
            }

            while (iterator.hasNext()) {

                boolean continueIteration;
                try {
                    SimpleFeature feat = iterator.next();
                    continueIteration = visitor.processFeature(feat);
                } catch (Throwable e) {
                    logger.error(e);
                    continueIteration = false;
                }

                if (continueIteration == false) {
                    break;
                }
            }

            iterator.close();

        } catch (IOException e) {
            logger.error(e);
        }
    }
}
