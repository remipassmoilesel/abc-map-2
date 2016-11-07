package abcmap.gui.iegroup.menubar;

import abcmap.gui.ie.program.ShowAboutDialog;
import abcmap.gui.ie.ressources.GoToAskFormPage;
import abcmap.gui.ie.ressources.GoToBugReportPage;
import abcmap.gui.ie.ressources.GoToFaqPage;
import abcmap.gui.ie.ressources.GoToHelpPage;
import abcmap.gui.ie.ressources.GoToSupportPage;
import abcmap.gui.ie.ressources.ShowUserManual;
import abcmap.gui.iegroup.InteractionElementGroup;

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
