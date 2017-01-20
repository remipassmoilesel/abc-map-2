package org.abcmap.gui.components.dock;

/**
 * Visual components implementing this interface should have a space where a small help can be displayed
 */
public interface HasExpandableHelp {

    /**
     * Show or hide help on element
     *
     * @param showHelp
     */
    public void expandHelp(boolean showHelp);

    /**
     * Return true if help is expanded
     */
    public boolean isHelpExpanded();

}