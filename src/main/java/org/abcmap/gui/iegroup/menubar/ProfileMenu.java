package org.abcmap.gui.iegroup.menubar;

import org.abcmap.gui.ie.InteractionElementGroup;
import org.abcmap.gui.ie.profiles.CreateNewProfile;
import org.abcmap.gui.ie.profiles.OpenProfile;
import org.abcmap.gui.ie.profiles.SaveAsProfile;
import org.abcmap.gui.ie.profiles.SaveProfile;

public class ProfileMenu extends InteractionElementGroup {

    public ProfileMenu() {

        label = "Profils";

        addInteractionElement(new CreateNewProfile());
        addInteractionElement(new OpenProfile());
        addInteractionElement(new SaveProfile());
        addInteractionElement(new SaveAsProfile());

    }

}
