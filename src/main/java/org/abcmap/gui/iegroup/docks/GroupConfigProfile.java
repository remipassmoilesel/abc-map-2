package org.abcmap.gui.iegroup.docks;

import org.abcmap.gui.GuiIcons;
import org.abcmap.ielements.GroupOfInteractionElements;
import org.abcmap.ielements.profiles.*;
import org.abcmap.ielements.recents.OpenRecentProfile;

public class GroupConfigProfile extends GroupOfInteractionElements {

    public GroupConfigProfile() {
        label = "Profils de configuration";
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
