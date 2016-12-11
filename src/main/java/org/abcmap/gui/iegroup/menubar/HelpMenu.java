package org.abcmap.gui.iegroup.menubar;

import org.abcmap.gui.ie.InteractionElementGroup;
import org.abcmap.gui.toProcess.gui.ie.program.ShowAboutDialog;
import org.abcmap.gui.toProcess.gui.ie.ressources.*;

public class HelpMenu extends InteractionElementGroup {

    public HelpMenu() {

        label = "Aide";

        addInteractionElement(new GoToHelpPage());
        addInteractionElement(new GoToFaqPage());
        addInteractionElement(new GoToAskFormPage());

        addSeparator();
        addInteractionElement(new GoToBugReportPage());
        addInteractionElement(new ShowUserManual());

        addSeparator();
        addInteractionElement(new GoToSupportPage());
        addInteractionElement(new ShowAboutDialog());

    }

}
