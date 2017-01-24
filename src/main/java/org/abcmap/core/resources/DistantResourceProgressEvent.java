package org.abcmap.core.resources;

/**
 * Created by remipassmoilesel on 24/01/17.
 */
public class DistantResourceProgressEvent {

    /**
     * Resource is initialized when the event has just been instancied
     */
    public static final String INITIALIZED = "INITIALIZED";

    /**
     * Resource is downloading when files are currently in download
     */
    public static final String DOWNLOADING = "DOWNLOADING";

    /**
     * Sometimes resources need to be uncompressed
     */
    public static final String UNCOMPRESSING = "UNCOMPRESSING";

    /**
     * Resource is "preparing" when it is on local disk, and currently imported
     * <p>
     * This status should be the last before "imported"
     */
    public static final String PREPARING = "PREPARING";


    private Object[] values;
    private String status;
    private final DistantResource resource;
    private double downloadedSize;
    private double finalSize;

    DistantResourceProgressEvent(DistantResource res) {
        this.resource = res;
        this.status = INITIALIZED;
        this.values = null;
        this.downloadedSize = -1;
    }

    public void setValues(Object[] values) {
        this.values = values;
    }

    public Object[] getValues() {
        return values;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public DistantResource getResource() {
        return resource;
    }

    public String getStatus() {
        return status;
    }

    public void setDownloadedSize(double downloadedSize) {
        this.downloadedSize = downloadedSize;
    }

    public double getDownloadedSize() {
        return downloadedSize;
    }

    public double getFinalSize() {
        return finalSize;
    }

    public void setFinalSize(double finalSize) {
        this.finalSize = finalSize;
    }
}
