package org.abcmap.gui.transition;

/**
 * Created by remipassmoilesel on 09/01/17.
 */
public abstract class Transition {

    public static String FADE_IN = "FADE_IN";
    public static String FADE_OUT = "FADE_OUT";

    public abstract void start(String type, Runnable whenFinished);

}
