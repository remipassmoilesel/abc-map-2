package abcmap.gui.ie.importation.data;

import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import abcmap.gui.comps.geo.CrsSelectionPanel;
import abcmap.gui.comps.importation.data.DataImportOptionsPanel;
import abcmap.gui.dock.comps.blockitems.SimpleBlockItem;
import abcmap.gui.ie.InteractionElement;
import net.miginfocom.swing.MigLayout;

public class SelectDataImportCRS extends InteractionElement {

	public SelectDataImportCRS() {

		this.label = "Système de coordonnées de l'import";
		this.help = "....";

	}

	@Override
	protected Component createPrimaryGUI() {
		CrsSelectionPanel panel = new CrsSelectionPanel();
		return panel;
	}

}
