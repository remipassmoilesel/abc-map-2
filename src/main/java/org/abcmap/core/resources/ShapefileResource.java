package org.abcmap.core.resources;

import java.util.Objects;

/**
 * Predefined WMS server, which can be loaded from locale server list or distant.
 */
public class ShapefileResource extends DistantResource {

    /**
     * Base url of repo
     *
     * @return
     */
    protected String baseUrl;

    /**
     * Resource path relative to base url
     */
    protected String resourcePath;

    protected String size;

    public ShapefileResource(String name, String baseUrl, String resourcePath) {
        super(name, "");
        this.name = name;
        this.baseUrl = baseUrl;
        this.resourcePath = resourcePath;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public String getResourceUrl() {

        String url = baseUrl;
        if (baseUrl.substring(baseUrl.length() - 2).equals("/") == false) {
            url += "/";
        }
        url += resourcePath;

        return url;
    }

    @Override
    public String toString() {
        return "ShapefileResource{" +
                "baseUrl='" + baseUrl + '\'' +
                ", resourcePath='" + resourcePath + '\'' +
                ", size='" + size + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ShapefileResource that = (ShapefileResource) o;
        return Objects.equals(baseUrl, that.baseUrl) &&
                Objects.equals(resourcePath, that.resourcePath) &&
                Objects.equals(size, that.size);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), baseUrl, resourcePath, size);
    }
}
