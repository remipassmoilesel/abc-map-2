package org.abcmap.ielements.project;

import org.abcmap.ielements.InteractionElement;
import org.abcmap.gui.utils.GuiUtils;

/**
 * Created by remipassmoilesel on 16/12/16.
 */
public class PrintLayouts extends InteractionElement {

    public PrintLayouts() {
        this.label= "Imprimer la mise en page";
        this.help = "...";
    }

    @Override
    public void run() {

        GuiUtils.throwIfOnEDT();

        if (getOperationLock() == false) {
            return;
        }

        try {
//          laym.prepareLayouts();
//          laym.printLayouts();


        } finally {
            releaseOperationLock();
        }

    }
}
