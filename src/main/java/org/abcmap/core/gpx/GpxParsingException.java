package org.abcmap.core.gpx;

/**
 * Created by remipassmoilesel on 01/02/17.
 */
public class GpxParsingException extends Exception{
    public GpxParsingException() {
    }

    public GpxParsingException(String s) {
        super(s);
    }

    public GpxParsingException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public GpxParsingException(Throwable throwable) {
        super(throwable);
    }

    public GpxParsingException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
