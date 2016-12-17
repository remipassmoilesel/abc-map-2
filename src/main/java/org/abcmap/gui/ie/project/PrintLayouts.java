package org.abcmap.gui.ie.project;

import org.abcmap.core.configuration.ConfigurationConstants;
import org.abcmap.core.project.PMConstants;
import org.abcmap.core.project.Project;
import org.abcmap.gui.ie.InteractionElement;
import org.abcmap.gui.utils.GuiUtils;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.ResolutionSyntax;
import javax.print.attribute.standard.PrinterResolution;
import java.awt.print.Book;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

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
