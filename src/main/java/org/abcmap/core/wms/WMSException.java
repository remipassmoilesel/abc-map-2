package org.abcmap.core.wms;

/**
 * Created by remipassmoilesel on 24/11/16.
 */
public class WMSException extends Exception{
    public WMSException() {
    }

    public WMSException(String message) {
        super(message);
    }

    public WMSException(String message, Throwable cause) {
        super(message, cause);
    }

    public WMSException(Throwable cause) {
        super(cause);
    }

    public WMSException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
