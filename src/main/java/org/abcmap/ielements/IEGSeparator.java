package org.abcmap.ielements;

import java.awt.*;

/**
 * Special interaction element representing a separator
 */
public class IEGSeparator extends InteractionElement {
    public IEGSeparator() {
    }

    @Override
    public void run() {
        throw new IllegalStateException("Not a valid " + super.getClass().getSimpleName() + ": " + this.getClass().getSimpleName());
    }

    @Override
    public Component createPrimaryGUI() {
        throw new IllegalStateException("Not a valid " + super.getClass().getSimpleName() + ": " + this.getClass().getSimpleName());
    }
}
