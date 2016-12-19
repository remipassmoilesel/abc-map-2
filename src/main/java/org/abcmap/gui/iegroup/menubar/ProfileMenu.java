package org.abcmap.gui.iegroup.menubar;

import org.abcmap.ielements.InteractionElementGroup;
import org.abcmap.ielements.profiles.CreateNewProfile;
import org.abcmap.ielements.profiles.OpenProfile;
import org.abcmap.ielements.profiles.SaveAsProfile;
import org.abcmap.ielements.profiles.SaveProfile;

public class ProfileMenu extends InteractionElementGroup {

    public ProfileMenu() {

        label = "Profils";

        addInteractionElement(new CreateNewProfile());
        addInteractionElement(new OpenProfile());
        addInteractionElement(new SaveProfile());
        addInteractionElement(new SaveAsProfile());

    }

}
