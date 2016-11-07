package abcmap.gui.iegroup.docks;

import abcmap.gui.GuiIcons;
import abcmap.gui.ie.profiles.CreateNewProfile;
import abcmap.gui.ie.profiles.OpenProfile;
import abcmap.gui.ie.profiles.SaveAsProfile;
import abcmap.gui.ie.profiles.SaveProfile;
import abcmap.gui.ie.profiles.SetProfileComment;
import abcmap.gui.ie.profiles.SetProfileTitle;
import abcmap.gui.ie.recents.OpenRecentProfile;
import abcmap.gui.iegroup.InteractionElementGroup;
import abcmap.utils.gui.Lng;

public class GroupConfigProfile extends InteractionElementGroup {

	public GroupConfigProfile() {
		label = Lng.get("configuration profiles");
		blockIcon = GuiIcons.GROUP_CONFIG_PROFILE;
		help = "Les profils de configuration permettent d'enregistrer vos paramètres sous différents "
				+ "fichiers pour ensuite les réutiliser facilement selon vos besoins.";

		addInteractionElement(new CreateNewProfile());
		addInteractionElement(new OpenProfile());
		addInteractionElement(new OpenRecentProfile());
		addInteractionElement(new SaveProfile());
		addInteractionElement(new SaveAsProfile());

		addSeparator();
		addInteractionElement(new SetProfileTitle());
		addInteractionElement(new SetProfileComment());
	}

}
