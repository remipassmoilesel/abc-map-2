package org.abcmap.core.project.layer;

import org.abcmap.core.project.Project;
import org.abcmap.core.wms.WMSEntry;
import org.abcmap.core.wms.WMSException;
import org.abcmap.core.wms.WMSdao;
import org.geotools.data.ows.Layer;
import org.geotools.data.ows.WMSCapabilities;
import org.geotools.data.wms.WMSUtils;
import org.geotools.data.wms.WebMapServer;
import org.geotools.geometry.jts.ReferencedEnvelope;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * Distant raster layer
 */
public class WMSLayer extends AbstractLayer {

    private WMSEntry wmsEntry;

    /**
     * @param name
     * @param url
     * @param wmsName
     * @param visible
     * @param zindex
     * @param owner
     * @throws IOException
     */
    public WMSLayer(String name, String url, String wmsName, boolean visible, int zindex, Project owner) throws IOException, WMSException {
        super(owner, new LayerIndexEntry(null, name, visible, zindex, LayerType.WMS));

        // create a wms entry
        wmsEntry = new WMSEntry(indexEntry.getLayerId(), url, wmsName);

        WMSdao dao = new WMSdao(project.getDatabasePath());
        dao.create(wmsEntry);
        dao.close();

        buildLayer();
    }

    public org.geotools.map.Layer buildLayer() throws IOException, WMSException {

        WebMapServer wms = null;
        try {
            wms = new WebMapServer(new URL(wmsEntry.getUrl()));
        } catch (Exception e) {
            throw new IOException("Unable to create WMS layer", e);
        }

        WMSCapabilities capabilities = wms.getCapabilities();
        List<Layer> namedLayers = Arrays.asList(WMSUtils.getNamedLayers(capabilities));

        Layer wmsLayer = null;
        for (Layer lay : namedLayers) {
            if (lay.getName().equals(wmsEntry.getWmsName())) {
                wmsLayer = lay;
                break;
            }
        }

        if (wmsLayer == null) {
            throw new WMSException("Unknow name: " + wmsEntry.getWmsName());
        }

        internalLayer = new org.geotools.map.WMSLayer(wms, wmsLayer);

        return internalLayer;
    }

    @Override
    public ReferencedEnvelope getBounds() {
        // TODO check if it take too much time ?
        logger.debug("// TODO check if it take too much time ?");
        return internalLayer.getBounds();
    }
}
