package org.abcmap.ielements;

import org.abcmap.gui.components.dock.blockitems.SubMenuItem;
import org.abcmap.gui.windows.MainWindowMode;

import java.awt.*;
import java.util.ArrayList;

/**
 * Interaction element group. A group can be used to create menus, docks, etc ...
 */
public class InteractionElementGroup extends InteractionElement {

    /**
     * Return true if specified element is a separator
     *
     * @param ie
     * @return
     */
    public static boolean isSeparator(InteractionElement ie) {
        return ie instanceof IEGSeparator;
    }

    /**
     * Elements of group
     */
    protected ArrayList<InteractionElement> interactionElements;

    /**
     * Associated window mode
     */
    protected MainWindowMode windowMode;

    public InteractionElementGroup() {

        this.label = "no name";

        this.help = null;

        this.interactionElements = new ArrayList<InteractionElement>();

        this.windowMode = MainWindowMode.SHOW_MAP;
    }

    public ArrayList<InteractionElement> getElements() {
        return new ArrayList<InteractionElement>(interactionElements);
    }

    public void addInteractionElement(InteractionElement ie) {
        this.interactionElements.add(ie);
    }

    public void addSeparator() {
        this.interactionElements.add(new IEGSeparator());
    }

    public MainWindowMode getWindowMode() {
        return windowMode;
    }

    @Override
    protected Component createBlockGUI() {
        return new SubMenuItem(this);
    }

}
