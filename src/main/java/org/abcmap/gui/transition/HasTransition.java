package org.abcmap.gui.transition;

/**
 * Created by remipassmoilesel on 09/01/17.
 */
public interface HasTransition {

    public void startTransition(String t, Runnable whenFinished);

}
