package org.abcmap.core.project.layers;

import org.abcmap.core.project.Project;
import org.abcmap.core.wms.WMSDao;
import org.abcmap.core.wms.WmsLayerEntry;
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

    /**
     * Entry containing metadata about WMS ids
     */
    private WmsLayerEntry wmsEntry;

    /**
     * List of WMS layers available on server
     */
    private List<Layer> possibleLayers;

    /**
     * WMS Server
     */
    private WebMapServer webMapServer;

    public AbmWMSLayer(String readableName, String url, String wmsLayerName, boolean visible, int zindex, Project owner) throws IOException {
        this(new LayerIndexEntry(null, readableName, visible, zindex, AbmLayerType.WMS), url, wmsLayerName, owner);
    }

    /**
     * Create a new WMS layer.
     *
     * @param layerEntry
     * @param url
     * @param wmsLayerName
     * @param owner
     * @throws IOException
     */
    public AbmWMSLayer(LayerIndexEntry layerEntry, String url, String wmsLayerName, Project owner) throws IOException {
        super(owner, layerEntry);

        WMSDao dao = new WMSDao(project.getDatabasePath());

        // url is null, this should be an existing layer, search for existing entries
        if (url == null) {
            wmsEntry = (WmsLayerEntry) dao.readById(layerEntry.getLayerId());
            if (wmsEntry == null) {
                throw new IOException("Specified URL is null, and no WMS entry can be found. Specify an URL or use another layer ID");
            }
        }

        // url is not null, this is a new layer, create entry and save it
        else {
            wmsEntry = new WmsLayerEntry(indexEntry.getLayerId(), url, wmsLayerName);
            dao.create(wmsEntry);
            dao.close();
        }

        this.webMapServer = null;
        try {
            webMapServer = new WebMapServer(new URL(wmsEntry.getUrl()));
        } catch (Exception e) {
            throw new IOException("Unable to create WMS layer", e);
        }

        buildInternalLayer();
    }

    @Override
    public org.geotools.map.Layer buildGeotoolsLayer() {

        WMSCapabilities capabilities = webMapServer.getCapabilities();
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
        return new org.geotools.map.WMSLayer(webMapServer, wmsLayer);

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
        wmsEntry.setWMSLayerName(layerName);
        buildInternalLayer();
    }

    /**
     * Return the list of WMS layer names availables on the current web map server
     *
     * @return
     */
    public ArrayList<String> getAvailableWMSNames() {

        if (possibleLayers == null) {
            WMSCapabilities capabilities = webMapServer.getCapabilities();
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
