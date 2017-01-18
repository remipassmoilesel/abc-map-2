package org.abcmap.core.wms;

/**
 * Created by remipassmoilesel on 24/11/16.
 */
public class WmsException extends Exception{
    public WmsException() {
    }

    public WmsException(String message) {
        super(message);
    }

    public WmsException(String message, Throwable cause) {
        super(message, cause);
    }

    public WmsException(Throwable cause) {
        super(cause);
    }

    public WmsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
