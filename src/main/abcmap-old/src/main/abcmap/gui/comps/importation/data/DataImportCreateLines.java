package abcmap.gui.comps.importation.data;

import javax.swing.JLabel;
import javax.swing.JPanel;

import abcmap.draw.shapes.Polygon;
import abcmap.draw.shapes.Polyline;
import abcmap.gui.comps.buttons.HtmlLabel;
import abcmap.gui.comps.draw.SelectedObjectPanel;
import abcmap.utils.gui.GuiUtils;
import net.miginfocom.swing.MigLayout;

public class DataImportCreateLines extends JPanel {

	private DataImportOptionsPanel parent;
	private String gapleft;
	private String wrap15;

	public DataImportCreateLines(DataImportOptionsPanel parent) {
		super(new MigLayout("insets 0"));

		this.parent = parent;
		this.wrap15 = "wrap 15, ";
		this.gapleft = "gapleft 15px, ";

		// afficher la forme sélectionnée à dupliquer
		GuiUtils.addLabel("Sélectionnez l'objet à dupliquer: ", this, "wrap");

		SelectedObjectPanel sop = new SelectedObjectPanel();

		// filtrer les formes
		sop.addFilter(Polyline.class);
		sop.addFilter(Polygon.class);

		add(sop, wrap15 + gapleft);

	}
}
