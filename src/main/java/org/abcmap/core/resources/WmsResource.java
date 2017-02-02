package org.abcmap.core.resources;

import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.project.Project;
import org.abcmap.core.project.layers.AbmWMSLayer;
import org.abcmap.gui.utils.GuiUtils;
import org.abcmap.ielements.importation.AddWMSLayer;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Predefined WMS server, which can be loaded from locale server list or distant.
 */
public class WmsResource extends DistantResource {

    private static final CustomLogger logger = LogManager.getLogger(WmsResource.class);

    private String url;

    public WmsResource(String name, String url) {
        super(name, "");
        this.name = name;
        this.url = url;
    }

    @Override
    public void importIn(Project p, Consumer<DistantResourceProgressEvent> progressListener) throws IOException {

        GuiUtils.throwIfOnEDT();

        // inform of progress
        DistantResourceProgressEvent updateRes = new DistantResourceProgressEvent(this);
        updateRes.setStatus(DistantResourceProgressEvent.PREPARING);
        try {
            progressListener.accept(updateRes);
        } catch (Exception e) {
            logger.error(e);
        }

        // try to open wms layer
        AddWMSLayer awmslayer = new AddWMSLayer();
        awmslayer.openLayer(url, null);

    }

    /**
     * URL of WMS server, which can be used to create a WMS layer
     *
     * @return
     */
    public String getUrl() {
        return url;
    }

    /**
     * URL of WMS server, which can be used to create a WMS layer
     *
     * @param url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "WmsResource{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        WmsResource that = (WmsResource) o;
        return Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), url);
    }
}
