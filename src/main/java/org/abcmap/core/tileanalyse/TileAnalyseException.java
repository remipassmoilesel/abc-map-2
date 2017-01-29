package org.abcmap.core.tileanalyse;

/**
 * Created by remipassmoilesel on 22/11/16.
 */
public class TileAnalyseException extends Exception {
    public TileAnalyseException() {
    }

    public TileAnalyseException(String s) {
        super(s);
    }

    public TileAnalyseException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public TileAnalyseException(Throwable throwable) {
        super(throwable);
    }

    public TileAnalyseException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
