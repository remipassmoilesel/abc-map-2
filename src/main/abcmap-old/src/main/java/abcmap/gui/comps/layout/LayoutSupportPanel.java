package abcmap.gui.comps.layout;

import java.awt.Dimension;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import abcmap.managers.stub.MainManager;
import net.miginfocom.swing.MigLayout;
import abcmap.managers.ProjectManager;
import abcmap.project.layouts.LayoutFormat.Orientation;
import abcmap.project.layouts.LayoutPaper;

/**
 * Composant d'affichage d'une feuille de mise en page. Destiné à être intégré à
 * un LayoutScrollPanel.
 * 
 * @author remipassmoilesel
 *
 */
public class LayoutSupportPanel extends JPanel {

	/** La feuille associée au panneau */
	private LayoutPaper paper;

	/** Le panneau ou sera affiché la feuille */
	private LayoutDisplayPanel displayPanel;

	/** Si cochée, la feuille réagira au modifications */
	private JCheckBox chkActive;

	/** Zone d'affichage d'informations sur la feuille */
	private JLabel labelInfo;

	private ProjectManager projectm;

	public LayoutSupportPanel(LayoutPaper paper) {
		super(new MigLayout("insets 5, fillx"));

		this.projectm = MainManager.getProjectManager();

		this.paper = paper;

		// afficher la feuille
		this.displayPanel = new LayoutDisplayPanel(paper);
		add(displayPanel, "align center, wrap 10, spanx");

		// checkbox actif/passif
		this.chkActive = new JCheckBox();
		add(chkActive);

		// Affichage du format et d'indications diverses
		this.labelInfo = new JLabel();
		add(labelInfo);

		// mise a jour des informations
		refeshPaperInformations();

	}

	/**
	 * Mettre à jour les informations sur la feuille affichée en fonction de la
	 * feuille
	 */
	public void refeshPaperInformations() {

		// active / inactive
		boolean active = paper.isActive();
		if (active != chkActive.isSelected()) {

			chkActive.setSelected(active);

			chkActive.revalidate();
			chkActive.repaint();
		}

		// index et format
		int index = projectm.getLayouts().indexOf(paper) + 1;
		Dimension dim = paper.getDimensionsMm();
		Orientation or = paper.getOrientation();

		String infos = "Feuille n°" + index + ", format: " + dim.width + " x "
				+ dim.height;

		if (labelInfo.getText().equals(infos) == false) {
			labelInfo.setText(infos);

			labelInfo.revalidate();
			labelInfo.repaint();
		}

	}

	/**
	 * Active la feuille et
	 * 
	 * @param val
	 */
	public void setPaperActive(boolean val) {
		if (val != paper.isActive()) {
			paper.setActive(val);
			refeshPaperInformations();
		}
	}

	public void setDisplayFactor(float d) {
		displayPanel.setComponentSizeFactor(d);

		displayPanel.revalidate();
		displayPanel.repaint();

		this.revalidate();
		this.repaint();
	}

	@Override
	public String toString() {
		return "LayoutSupportPanel [paper=" + paper + "]";
	}

}
