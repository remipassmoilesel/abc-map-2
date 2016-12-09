package org.abcmap.gui.components.data;

import net.miginfocom.swing.MigLayout;
import org.abcmap.gui.components.SelectedObjectPanel;
import org.abcmap.gui.utils.GuiUtils;

import javax.swing.*;

public class DataImportCreateLines extends JPanel {

	private DataImportOptionsPanel parent;
	private String gapleft;
	private String wrap15;

	public DataImportCreateLines(DataImportOptionsPanel parent) {
		super(new MigLayout("insets 0"));

		this.parent = parent;
		this.wrap15 = "wrap 15, ";
		this.gapleft = "gapleft 15px, ";

		// show shape to duplicate
		GuiUtils.addLabel("Sélectionnez l'objet à dupliquer: ", this, "wrap");
		SelectedObjectPanel sop = new SelectedObjectPanel();

		// filter forms
		//sop.addFilter(Polyline.class);
		//sop.addFilter(Polygon.class);

		add(sop, wrap15 + gapleft);

	}
}
