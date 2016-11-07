package abcmap.gui.comps.importation.data;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import abcmap.draw.shapes.Ellipse;
import abcmap.draw.shapes.Image;
import abcmap.draw.shapes.Rectangle;
import abcmap.draw.shapes.Symbol;
import abcmap.gui.comps.buttons.HtmlCheckbox;
import abcmap.gui.comps.draw.SelectedObjectPanel;
import abcmap.utils.gui.GuiUtils;

public class DataImportCreateShapes extends JPanel {

	private DataImportOptionsPanel parent;
	private String gapleft;
	private String wrap15;

	public DataImportCreateShapes(DataImportOptionsPanel parent) {
		super(new MigLayout("insets 0"));

		this.parent = parent;
		this.wrap15 = "wrap 15, ";
		this.gapleft = "gapleft 15px, ";

		// afficher la forme sélectionnée à dupliquer
		GuiUtils.addLabel("Sélectionnez la forme à dupliquer: ", this, "wrap");

		SelectedObjectPanel sop = new SelectedObjectPanel();

		// filtrer les formes
		sop.addFilter(Rectangle.class);
		sop.addFilter(Ellipse.class);
		sop.addFilter(Image.class);
		sop.addFilter(Symbol.class);

		add(sop, wrap15 + gapleft);

		// options supplémentaires
		GuiUtils.addLabel("Options: ", this, "wrap");

		// changer la taille des objets en fonction du champs
		HtmlCheckbox chkChangeSize = new HtmlCheckbox(
				"<html>Changer la taille de l'objet en fonction du champs:</html>");
		add(chkChangeSize, "wrap");

		DataHeadersCombo cbChangeSize = new DataHeadersCombo();
		add(cbChangeSize, gapleft + wrap15);

		// changer la couleur des objets en fonction du champs
		HtmlCheckbox chkChangeColor = new HtmlCheckbox(
				"<html>Changer la couleur de l'objet en fonction du champs:</html>");
		add(chkChangeColor, "wrap");

		DataHeadersCombo cbChangeColor = new DataHeadersCombo();
		add(cbChangeColor, gapleft + wrap15);

	}

}
