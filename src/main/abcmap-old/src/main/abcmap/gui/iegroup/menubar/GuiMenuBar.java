package abcmap.gui.iegroup.menubar;

import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import abcmap.gui.ie.InteractionElement;
import abcmap.gui.iegroup.InteractionElementGroup;
import abcmap.managers.stub.MainManager;

public class GuiMenuBar extends JMenuBar {

	public GuiMenuBar() {

		// elements à ajouter
		ArrayList<InteractionElementGroup> groups = new ArrayList<InteractionElementGroup>();
		groups.add(new FileMenu());
		groups.add(new EditionMenu());
		groups.add(new DrawingToolsMenu());
		groups.add(new ImportMenu());
		groups.add(new ExportMenu());
		groups.add(new ProfileMenu());
		groups.add(new HelpMenu());

		// element de menu spécial debogage
		if (MainManager.isDebugMode()) {
			groups.add(new DebugMenu());
		}

		// parcourir les elements a ajouter
		for (InteractionElementGroup ieg : groups) {

			// creer un menu
			JMenu smenu = new JMenu(ieg.getLabel());

			// ajouter une icone si present
			if (ieg.getMenuIcon() != null)
				smenu.setIcon(ieg.getMenuIcon());

			// iterer les elements du menu
			for (InteractionElement ie : ieg.getElements()) {

				// insertion
				if (InteractionElementGroup.isSeparator(ie))
					smenu.addSeparator();
				else
					smenu.add(ie.getMenuGUI());
			}

			// ajout du sous menu
			add(smenu);

		}
	}

}
