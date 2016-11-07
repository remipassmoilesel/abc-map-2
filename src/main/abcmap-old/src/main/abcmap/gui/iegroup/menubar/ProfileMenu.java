package abcmap.gui.iegroup.menubar;

import abcmap.gui.ie.profiles.CreateNewProfile;
import abcmap.gui.ie.profiles.OpenProfile;
import abcmap.gui.ie.profiles.SaveAsProfile;
import abcmap.gui.ie.profiles.SaveProfile;
import abcmap.gui.iegroup.InteractionElementGroup;

public class ProfileMenu extends InteractionElementGroup {

	public ProfileMenu() {

		label = "Profils";

		addInteractionElement(new CreateNewProfile());
		addInteractionElement(new OpenProfile());
		addInteractionElement(new SaveProfile());
		addInteractionElement(new SaveAsProfile());

	}

}
