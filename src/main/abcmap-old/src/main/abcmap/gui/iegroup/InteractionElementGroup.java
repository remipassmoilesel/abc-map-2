package abcmap.gui.iegroup;

import java.awt.Component;
import java.util.ArrayList;

import abcmap.gui.dock.comps.blockitems.SubMenuItem;
import abcmap.gui.ie.InteractionElement;
import abcmap.gui.windows.MainWindowMode;

/**
 * Groupe d'éléments d'interaction. Un groupe d'éléments d'interaction peut être
 * utilisé pour créer un dock, une barre d'outil ou un menu haut.
 * <p>
 * Une instance de groupe doit être crée par composant graphique.
 * 
 * @author remipassmoilesel
 *
 */
public class InteractionElementGroup extends InteractionElement {

	/**
	 * Retourne vrai si l'élément passé en paramètre doit être considéré comme
	 * un séparateur et nom comme un élément d'interaction.
	 * 
	 * @param ie
	 * @return
	 */
	public static boolean isSeparator(InteractionElement ie) {
		return ie instanceof IEGSeparator;
	}

	/** Elements du groupe */
	protected ArrayList<InteractionElement> interactionElements;

	/** Mode de fenêtre à utiliser avec le groupe */
	protected MainWindowMode windowMode;

	public InteractionElementGroup() {

		this.label = "no name";

		// par defaut l'aide(la description) est nulle car optionnelle
		this.help = null;

		this.interactionElements = new ArrayList<InteractionElement>();

		// mode par défaut du groupe
		this.windowMode = abcmap.gui.windows.MainWindowMode.SHOW_MAP;
	}

	/**
	 * Retourne la liste des élements
	 * 
	 * @return
	 */
	public ArrayList<InteractionElement> getElements() {
		return new ArrayList<InteractionElement>(interactionElements);
	}

	/**
	 * Ajouter un element d'interaction
	 * 
	 * @param ie
	 */
	public void addInteractionElement(InteractionElement ie) {
		this.interactionElements.add(ie);
	}

	/**
	 * Ajouter un séparateur entre élements, pour augmenter la lisibilité du
	 * groupe
	 */
	public void addSeparator() {
		this.interactionElements.add(new IEGSeparator());
	}

	public MainWindowMode getWindowMode() {
		return windowMode;
	}

	@Override
	protected Component createBlockGUI() {
		return new SubMenuItem(this);
	}

}
