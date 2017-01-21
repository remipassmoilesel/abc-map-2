package org.abcmap.iegroup.menubar;

import org.abcmap.ielements.GroupOfInteractionElements;
import org.abcmap.ielements.profiles.CreateNewProfile;
import org.abcmap.ielements.profiles.OpenProfile;
import org.abcmap.ielements.profiles.SaveAsProfile;
import org.abcmap.ielements.profiles.SaveProfile;

public class ProfileMenu extends GroupOfInteractionElements {

    public ProfileMenu() {

        label = "Profils";

        addInteractionElement(new CreateNewProfile());
        addInteractionElement(new OpenProfile());
        addInteractionElement(new SaveProfile());
        addInteractionElement(new SaveAsProfile());

    }

}
