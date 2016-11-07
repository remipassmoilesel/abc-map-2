package abcmap.gui.comps.layout;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

import abcmap.draw.basicshapes.Drawable;
import abcmap.gui.GuiCursor;
import abcmap.gui.comps.display.DynamicDisplayPanel;
import abcmap.project.layouts.LayoutPaper;
import abcmap.project.utils.ProjectRenderer;

/**
 * Affichage d'une feuille de mise en page. Ce composant doit être intégré à un
 * LayoutSupport.
 * 
 * @author remipassmoilesel
 *
 */
public class LayoutDisplayPanel extends DynamicDisplayPanel {

	/** La feuille associée au panneau */
	private LayoutPaper paper;

	/**
	 * Le facteur de dimensionnement du composant graphique par rapport à la
	 * taille relle
	 */
	private float componentSizeFactor;

	private Border border;

	public LayoutDisplayPanel(LayoutPaper paper) {

		this.componentSizeFactor = 0.5f;

		// le papier à afficher
		this.paper = paper;

		// echelle d'affichage
		this.setDisplayScale(paper.getMapScale());
		this.setDrawableElementOrigin(paper.getPositionOnMap());

		// bordure
		border = BorderFactory.createLineBorder(Color.gray);
		this.setBorder(border);

		// valeurs min et max de zoom, adapté à la qualité de rendue médiocre
		// des images
		setMinScaleValue(0.5f);
		setMaxScaleValue(3f);

		// affecter le gestionnaire de projet comme element a dessiner
		setDrawableElement(new ProjectRenderer(Drawable.RENDER_FOR_PRINTING));

		setMoveMouseButton(DynamicDisplayPanel.LEFT_BUTTON);

	}

	public void setComponentSizeFactor(float d) {
		this.componentSizeFactor = d;
	}

	/**
	 * Retourne la taille
	 */
	@Override
	public Dimension getPreferredSize() {
		Dimension dim = paper.getPixelDimensions(Math
				.round(72 * componentSizeFactor));
		return dim;
	}
}
