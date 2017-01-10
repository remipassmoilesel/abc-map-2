package org.abcmap.gui.iegroup.menubar;

import org.abcmap.core.managers.Main;
import org.abcmap.ielements.InteractionElement;
import org.abcmap.ielements.GroupOfInteractionElements;

import javax.swing.*;
import java.util.ArrayList;

public class GuiMenuBar extends JMenuBar {

    public GuiMenuBar() {

        ArrayList<GroupOfInteractionElements> groups = new ArrayList<GroupOfInteractionElements>();
        groups.add(new FileMenu());
        groups.add(new EditionMenu());
        groups.add(new DrawingToolsMenu());
        groups.add(new ImportMenu());
        groups.add(new ExportMenu());
        groups.add(new ProfileMenu());
        groups.add(new HelpMenu());

        if (Main.isDebugMode()) {
            groups.add(new DebugMenu());
        }

        for (GroupOfInteractionElements ieg : groups) {

            JMenu smenu = new JMenu(ieg.getLabel());

            if (ieg.getMenuIcon() != null) {
                smenu.setIcon(ieg.getMenuIcon());
            }

            for (InteractionElement ie : ieg.getElements()) {

                if (GroupOfInteractionElements.isSeparator(ie)) {
                    smenu.addSeparator();
                } else {
                    smenu.add(ie.getMenuGUI());
                }

            }

            add(smenu);

        }
    }

}
