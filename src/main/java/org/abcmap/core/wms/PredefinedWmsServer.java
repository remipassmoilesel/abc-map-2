package org.abcmap.core.wms;

import java.util.Objects;

/**
 * Created by remipassmoilesel on 18/01/17.
 */
public class PredefinedWmsServer {

    private String name;
    private String url;

    public PredefinedWmsServer(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

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
