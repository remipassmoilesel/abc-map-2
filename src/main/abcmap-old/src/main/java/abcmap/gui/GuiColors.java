package abcmap.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

import javax.swing.UIManager;

/**
 * Conteneurs de couleurs utilisées dans le GUI. Utiliser autant que possible
 * des couleurs prises à partir de l'UIManager pour garantir une harmonie avec
 * l'environnement utiliateur.
 *
 */
public class GuiColors {

	/** Couleur des titres de menus classe 1 */
	public static final Color MENU_TITLE_1 = new Color(13, 0, 178);

	/** Couleurs des titres de menus classe 2 */
	public static final Color MENU_TITLE_2 = new Color(80, 80, 80);

	/** Couleur à peindre par transparence lorsque un element est sous focus */
	public static Color FOCUS_COLOR_BACKGROUND;

	/** Bordure à peindre lorsque qu'un élement à le focus */
	public static int FOCUS_STROKE_THICKNESS;

	public static Stroke FOCUS_STROKE;

	/** Couleur de fond par défaut d'un jmenuitem */
	public static Color MENU_ITEM_BACKGROUND;

	/** Couleur de fond par defaut d'un jpanel */
	public static Color PANEL_BACKGROUND;

	/** Couleur de fond d'une boite d'info */
	public static Color INFO_BOX_BACKGROUND;

	/** Couleur de fond d'une boite d'erreur */
	public static Color ERROR_BOX_BACKGROUND;

	/**
	 * Initiliser les variables. Doit être appelée après le changement
	 * d'UIManager.
	 */
	public static void init() {

		FOCUS_COLOR_BACKGROUND = UIManager.getColor("MenuItem.selectionBackground");

		FOCUS_STROKE_THICKNESS = 1;

		FOCUS_STROKE = new BasicStroke(FOCUS_STROKE_THICKNESS);

		MENU_ITEM_BACKGROUND = UIManager.getColor("MenuItem.background");

		PANEL_BACKGROUND = UIManager.getColor("Panel.background");

		INFO_BOX_BACKGROUND = new Color(0, 50, 10);

		ERROR_BOX_BACKGROUND = new Color(50, 10, 0);

	}

}
