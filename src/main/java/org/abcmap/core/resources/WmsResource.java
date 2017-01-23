package org.abcmap.core.resources;

import java.util.Objects;

/**
 * Predefined WMS server, which can be loaded from locale server list or distant.
 */
public class WmsResource extends DistantResource {

    private String url;

    public WmsResource(String name, String url) {
        super(name, "");
        this.name = name;
        this.url = url;
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
