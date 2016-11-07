package abcmap.gui.comps.help;

import javax.swing.ImageIcon;

import abcmap.gui.GuiIcons;

/**
 * Interactions possibles imagées. Exemple: clic, double clic, touche MAJ, ...
 * 
 * @author remipassmoilesel
 *
 */
public class Interaction {

	public static Interaction SIMPLE_CLICK;
	public static Interaction DOUBLE_CLICK;
	public static Interaction DRAG;

	public static Interaction PRESS_CONTROL;
	public static Interaction PRESS_SHIFT;

	private ImageIcon icon;
	private String toolTipText;

	public static void init() {
		SIMPLE_CLICK = new Interaction(GuiIcons.INTERACTION_SIMPLECLICK, "Clic simple");
		DOUBLE_CLICK = new Interaction(GuiIcons.INTERACTION_DOUBLECLICK, "Double clic");
		PRESS_CONTROL = new Interaction(GuiIcons.INTERACTION_PRESSCTRL, "Presser la touche 'Control'");
		PRESS_SHIFT = new Interaction(GuiIcons.INTERACTION_PRESSMAJ, "Presser la touche 'Majuscule'");
		DRAG = new Interaction(GuiIcons.INTERACTION_DRAG,
				"Cliquer puis maintenir le bouton gauche de la souris lors d'un déplacement");
	}

	private Interaction(ImageIcon icon, String toolTipText) {
		this.icon = icon;
		this.toolTipText = toolTipText;
	}

	public ImageIcon getIcon() {
		return icon;
	}

	public String getToolTipText() {
		return toolTipText;
	}

}
