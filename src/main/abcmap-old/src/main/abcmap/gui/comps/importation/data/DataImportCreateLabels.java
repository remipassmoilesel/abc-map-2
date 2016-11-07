package abcmap.gui.comps.importation.data;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import net.miginfocom.swing.MigLayout;
import abcmap.draw.shapes.Label;
import abcmap.gui.comps.buttons.HtmlCheckbox;
import abcmap.gui.comps.draw.SelectedObjectPanel;
import abcmap.utils.gui.GuiUtils;

public class DataImportCreateLabels extends JPanel {

	private DataImportOptionsPanel parent;
	private String gapleft;
	private String wrap15;

	public DataImportCreateLabels(DataImportOptionsPanel parent) {
		super(new MigLayout("insets 0"));

		this.parent = parent;
		this.wrap15 = "wrap 15, ";
		this.gapleft = "gapleft 15px, ";

		// afficher la forme sélectionnée à dupliquer
		GuiUtils.addLabel("Sélectionnez l'étiquette à dupliquer: ", this,
				"wrap");

		SelectedObjectPanel sop = new SelectedObjectPanel();

		// filtrer les formes
		sop.addFilter(Label.class);

		add(sop, wrap15 + gapleft);

		// options supplémentaires
		GuiUtils.addLabel("Options: ", this, "wrap");

		// afficher les coordonnées de l'etiquette
		JRadioButton rdDisplayCoords = new JRadioButton(
				"<html>Afficher les coordonnées du point</html>");
		rdDisplayCoords.setSelected(true);
		add(rdDisplayCoords, "wrap");

		// afficher un texte en fonction d'un champs
		JRadioButton rdChangeText = new JRadioButton(
				"<html>Modifier le texte en fonction du champs:</html>");
		add(rdChangeText, "wrap");

		ButtonGroup bg1 = new ButtonGroup();
		bg1.add(rdDisplayCoords);
		bg1.add(rdChangeText);

		DataHeadersCombo cbChangeText = new DataHeadersCombo();
		add(cbChangeText, gapleft + wrap15);

		// changer la couleur des etiquettes en fonction du champs
		HtmlCheckbox chkChangeColor = new HtmlCheckbox(
				"<html>Changer la couleur des etiquettes en fonction du champs:</html>");
		add(chkChangeColor, "wrap");

		DataHeadersCombo cbChangeColor = new DataHeadersCombo();
		add(cbChangeColor, gapleft + wrap15);

	}
}
