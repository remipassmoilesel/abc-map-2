package abcmap.gui.windows;

import java.awt.Color;

/**
 * Les modes d'affichage de la fenêtre principale. Chaque mode possède un label
 * et une couleur.
 * 
 * <p>
 * Pour internationaliser, remplacer les champs "label" par des identifiants de
 * type Lng.get("");
 * 
 * @author remipassmoilesel
 *
 */
public enum MainWindowMode {
	/**
	 * Afficher la carte
	 */
	SHOW_MAP(Color.green, "Carte"),

	/**
	 * Afficher la mise en page
	 */
	SHOW_LAYOUTS(Color.blue, "Feuilles de mise en page"),

	/**
	 * Afficher les tuiles refusées
	 */
	SHOW_REFUSED_TILES(Color.red, "Tuiles refusées");

	private String label;
	private Color fgColor;

	private MainWindowMode(Color fgColor, String label) {

		this.label = label;
		this.fgColor = fgColor;

	}

	public String getLabel() {
		return label;
	}

	public Color getFgColor() {
		return fgColor;
	}

}
