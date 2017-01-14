package org.abcmap.core.project.layers;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Distant raster layer
 */
public class AbmWMSLayer extends AbmAbstractLayer {

    private WMSEntry wmsEntry;
    private List<Layer> possibleLayers;
    private WebMapServer wmswebMapServer;

    /**
     * @param readableName
     * @param url
     * @param wmsLayerName
     * @param visible
     * @param zindex
     * @param owner
     * @throws IOException
     */
    public AbmWMSLayer(String readableName, String url, String wmsLayerName, boolean visible, int zindex, Project owner) throws IOException, WMSException {
        super(owner, new LayerIndexEntry(null, readableName, visible, zindex, AbmLayerType.WMS));

        // create a wms entry
        wmsEntry = new WMSEntry(indexEntry.getLayerId(), url, wmsLayerName);

        // create an entry in WMS index
        WMSdao dao = new WMSdao(project.getDatabasePath());
        dao.create(wmsEntry);
        dao.close();

        this.wmswebMapServer = null;
        try {
            wmswebMapServer = new WebMapServer(new URL(wmsEntry.getUrl()));
        } catch (Exception e) {
            throw new IOException("Unable to create WMS layer", e);
        }

        buildInternalLayer(url, wmsLayerName);
    }

    private void buildInternalLayer(String url, String wmsName) {

        if (internalLayer != null) {
            internalLayer.dispose();
        }

        WMSCapabilities capabilities = wmswebMapServer.getCapabilities();
        List<Layer> namedLayers = Arrays.asList(WMSUtils.getNamedLayers(capabilities));

        Layer wmsLayer = null;

        // display the first layer if no name is specified
        if (wmsEntry.getWmsLayerName() == null) {
            wmsLayer = namedLayers.get(0);
            wmsEntry.setWMSLayerName(wmsLayer.getName());
        }

        // or search specified name
        else {
            for (Layer lay : namedLayers) {

                String layName = lay.getName().trim();
                String entryName = wmsEntry.getWmsLayerName().trim();

                System.out.println(layName);
                System.out.println(entryName);

                // or display the layer with the same name
                // remove extra space and ignore case
                if (layName.equalsIgnoreCase(entryName)) {
                    wmsLayer = lay;
                    break;
                }
            }
        }

        // name not found, take the first layer available
        if (wmsLayer == null) {
            logger.error("Unknow name: " + wmsEntry.getWmsLayerName());
            logger.error("Availables: " + namedLayers);

            wmsLayer = namedLayers.get(0);
        }

        // create layer
        internalLayer = new org.geotools.map.WMSLayer(wmswebMapServer, wmsLayer);

        // update internal parameters
        wmsEntry.setUrl(url);
        wmsEntry.setWMSLayerName(wmsName);

    }

    /**
     * Change internal layer by his name
     * <p>
     * This name should be choosen from list of available names in Web Map Server
     * <p>
     * You can get this list with: getAvailableWMSNames();
     *
     * @param layerName
     */
    public void changeWmsName(String layerName) {
        buildInternalLayer(wmsEntry.getUrl(), layerName);
    }

    /**
     * Return the list of WMS layer names availables on the current web map server
     *
     * @return
     */
    public ArrayList<String> getAvailableWMSNames() {

        if (possibleLayers == null) {
            WMSCapabilities capabilities = wmswebMapServer.getCapabilities();
            possibleLayers = Arrays.asList(WMSUtils.getNamedLayers(capabilities));
        }

        ArrayList<String> names = new ArrayList<>();

        for (Layer lay : possibleLayers) {
            names.add(lay.getName());
        }

        return names;
    }

    @Override
    public ReferencedEnvelope getBounds() {
        // TODO check if it take too much time ?
        logger.debug("// TODO check if it take too much time ?");
        return internalLayer.getBounds();
    }

}
