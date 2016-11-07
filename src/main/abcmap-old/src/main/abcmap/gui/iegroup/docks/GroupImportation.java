package abcmap.gui.iegroup.docks;

import abcmap.gui.GuiIcons;
import abcmap.gui.ie.importation.data.MenuDataImport;
import abcmap.gui.ie.importation.directory.MenuImportFromDirectory;
import abcmap.gui.ie.importation.document.MenuImportFromDocument;
import abcmap.gui.ie.importation.manual.MenuImportManualCapture;
import abcmap.gui.ie.importation.robot.MenuRobotImport;
import abcmap.gui.iegroup.InteractionElementGroup;
import abcmap.utils.gui.Lng;

public class GroupImportation extends InteractionElementGroup {

	public GroupImportation() {
		
		label = Lng.get("importation menu");
		blockIcon = GuiIcons.GROUP_IMPORT;

		addInteractionElement(new MenuImportFromDirectory());
		addInteractionElement(new MenuImportFromDocument());
		addInteractionElement(new MenuRobotImport());
		addInteractionElement(new MenuImportManualCapture());
		addInteractionElement(new MenuDataImport());

	}

}
