package org.abcmap.gui.iegroup.menubar;

import org.abcmap.core.managers.MainManager;
import org.abcmap.gui.ie.InteractionElement;
import org.abcmap.gui.ie.InteractionElementGroup;

import javax.swing.*;
import java.util.ArrayList;

public class GuiMenuBar extends JMenuBar {

    public GuiMenuBar() {

        ArrayList<InteractionElementGroup> groups = new ArrayList<InteractionElementGroup>();
        groups.add(new FileMenu());
        groups.add(new EditionMenu());
        groups.add(new DrawingToolsMenu());
        groups.add(new ImportMenu());
        groups.add(new ExportMenu());
        groups.add(new ProfileMenu());
        groups.add(new HelpMenu());

        if (MainManager.isDebugMode()) {
            groups.add(new DebugMenu());
        }

        for (InteractionElementGroup ieg : groups) {

            JMenu smenu = new JMenu(ieg.getLabel());

            if (ieg.getMenuIcon() != null) {
                smenu.setIcon(ieg.getMenuIcon());
            }

            for (InteractionElement ie : ieg.getElements()) {

                if (InteractionElementGroup.isSeparator(ie)) {
                    smenu.addSeparator();
                } else {
                    smenu.add(ie.getMenuGUI());
                }

            }

            add(smenu);

        }
    }

}
