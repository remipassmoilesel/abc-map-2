package abcmap.gui.ie.importation.data;

import abcmap.gui.dock.comps.blockitems.DockMenuPanel;

public class SubMenuDataImport extends DockMenuPanel {

	public SubMenuDataImport() {
		super();

		// Chemin de la liste à importer
		addMenuElement(new SelectDataToImport());

		// Ouvrir une liste d'exemple
		addMenuElement(new CreateDataFile());

		// options
		addMenuElement(new SelectDataImportOptions());

		// systeme de coordonnées
		addMenuElement(new SelectDataImportCRS());

		// lancement
		addMenuElement(new LaunchDataImport());

		reconstruct();
	}
}
