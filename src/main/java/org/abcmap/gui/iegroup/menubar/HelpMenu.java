package org.abcmap.gui.iegroup.menubar;

import org.abcmap.ielements.GroupOfInteractionElements;
import org.abcmap.ielements.program.ShowAboutDialog;
import org.abcmap.ielements.ressources.*;

public class HelpMenu extends GroupOfInteractionElements {

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
