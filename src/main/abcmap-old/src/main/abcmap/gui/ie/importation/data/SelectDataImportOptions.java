package abcmap.gui.ie.importation.data;

import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import abcmap.gui.comps.importation.data.DataImportOptionsPanel;
import abcmap.gui.dock.comps.blockitems.SimpleBlockItem;
import abcmap.gui.ie.InteractionElement;
import net.miginfocom.swing.MigLayout;

public class SelectDataImportOptions extends InteractionElement {

	public SelectDataImportOptions() {

		this.label = "Options d'import de donnnées";
		this.help = "Sélectionnez ici les options d'import dedonnées.";

	}

	@Override
	protected Component createPrimaryGUI() {
		DataImportOptionsPanel panel = new DataImportOptionsPanel();
		return panel;
	}

}
