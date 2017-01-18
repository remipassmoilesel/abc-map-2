package org.abcmap.core.wms;

import java.util.Objects;

/**
 * Predefined WMS server, which can be loaded from locale server list or distant.
 */
public class PredefinedWmsServer {


    /**
     * Name of WMS server, which can be used to display this server
     *
     * @return
     */
    private String name;

    /**
     * URL of WMS server, which can be used to create a WMS layer
     *
     * @return
     */
    private String url;

    public PredefinedWmsServer(String name, String url) {
        this.name = name;
        this.url = url;
    }

    /**
     * Name of WMS server, which can be used to display this server
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Name of WMS server, which can be used to display this server
     *
     * @return
     */
    public void setName(String name) {
        this.name = name;
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
        return "PredefinedWMSServer{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PredefinedWmsServer that = (PredefinedWmsServer) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, url);
    }
}
