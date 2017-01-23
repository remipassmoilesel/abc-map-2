package org.abcmap.core.resources;

import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.project.Project;
import org.abcmap.core.project.layers.AbmWMSLayer;
import org.abcmap.gui.utils.GuiUtils;

import java.io.IOException;
import java.nio.file.Path;
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
    public void importIn(Project p, Consumer<Object[]> update) throws IOException {

        GuiUtils.throwIfOnEDT();

        // try to open wms layer
        AbmWMSLayer layer = p.addNewWMSLayer(url, null);
        p.addLayer(layer);

        p.fireLayerListChanged();

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
                "url='" + url + '\'' +
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
