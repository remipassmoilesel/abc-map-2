package abcmap.gui.dock.comps.blockitems;

import java.awt.Component;
import java.awt.PopupMenu;
import java.util.ArrayList;

import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;

import abcmap.gui.GuiStyle;
import abcmap.gui.dock.comps.Dock;
import abcmap.gui.ie.InteractionElement;
import abcmap.gui.iegroup.InteractionElementGroup;
import abcmap.utils.Refreshable;
import abcmap.utils.gui.GuiUtils;
import net.miginfocom.swing.MigLayout;

public class DockMenuPanel extends JPanel implements Refreshable {

	/** Liste des sous éléments */
	protected ArrayList<Object> elements;

	/** Titre du menu */
	protected String title;

	/** Description du menu */
	protected String description;

	public DockMenuPanel() {
		super(new MigLayout("fillx, insets 5 5 5 5"));

		this.title = "No title";
		this.description = null;
		this.elements = new ArrayList<Object>();

		reconstruct();
	}

	@Override
	public void reconstruct() {

		// tout enlever
		removeAll();

		// panneau de metadonnees: titre et description
		JPanel metadatas = new JPanel(new MigLayout("insets 5"));

		// titre du panneau
		GuiUtils.addLabel(title, metadatas, "width 95%!, wrap",
				GuiStyle.DOCK_MENU_TITLE_1);

		// ajout de la description au besoin
		if (description != null && description.isEmpty() == false) {

			// panneau d'affichage du texte
			JEditorPane desc = new JEditorPane("text/html", "<html>"
					+ description + "</html>");

			// mise en forme
			desc.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES,
					Boolean.TRUE);
			desc.setOpaque(false);
			desc.setEditable(false);
			GuiStyle.applyStyleTo(GuiStyle.DOCK_MENU_DESCRIPTION, desc);

			metadatas.add(desc, "width 95%!");
		}

		// ajout des metadonnees
		add(metadatas, "width " + Dock.MENU_ITEM_WIDTH + "!, wrap");

		// ajout des composants au panneau
		for (Object o : elements) {

			// l'objet est un composant, ajout direct
			if (o instanceof Component) {
				addItem((Component) o);
			}

			// l'ajout est un element d'interaction, ajout du gui
			else if (o instanceof InteractionElement) {
				addItem(((InteractionElement) o).getBlockGUI());
			}

		}

		// rafraichissement
		refresh();
	}

	/**
	 * Ajout d'un element de menu avec contraintes adaptées
	 * 
	 * @param c
	 */
	private void addItem(Component c) {
		super.add(c, "width " + Dock.MENU_ITEM_WIDTH + "!, wrap");
	}

	/**
	 * Ajout d'un composant au menu
	 * 
	 * @param o
	 */
	public void addMenuElement(InteractionElement o) {
		elements.add(o);
	}

	/**
	 * Ajout d'un composant au menu
	 * 
	 * @param o
	 */
	public void addMenuElement(Component o) {
		elements.add(o);
	}

	/**
	 * Retourne la liste des composants du menu. Les composants peuvent être de
	 * type Component ou InteractionElement
	 * 
	 * @return
	 */
	public ArrayList<Object> getMenuElements() {
		return new ArrayList<>(elements);
	}

	public void clearMenuElementsList() {
		elements.clear();
	}

	public void setMenuDescription(String description) {
		this.description = description;
	}

	public String getMenuDescription() {
		return description;
	}

	public String getMenuTitle() {
		return title;
	}

	public void setMenuTitle(String title) {
		this.title = title;
	}

	/**
	 * Utiliser addMenuComponent()
	 */
	@Deprecated
	@Override
	public Component add(Component comp) {
		return super.add(comp);
	}

	/**
	 * Utiliser addMenuComponent()
	 */
	@Deprecated
	@Override
	public void add(PopupMenu arg0) {
		super.add(arg0);
	}

	/**
	 * Utiliser addMenuComponent()
	 */
	@Deprecated
	@Override
	public Component add(String name, Component comp) {
		return super.add(name, comp);
	}

	/**
	 * Utiliser addMenuComponent()
	 */
	@Deprecated
	@Override
	public Component add(Component comp, int index) {
		return super.add(comp, index);
	}

	/**
	 * Utiliser addMenuComponent()
	 */
	@Deprecated
	@Override
	public void add(Component comp, Object constraints) {
		super.add(comp, constraints);
	}

	/**
	 * Utiliser addMenuComponent()
	 */
	@Deprecated
	@Override
	public void add(Component comp, Object constraints, int index) {
		super.add(comp, constraints, index);
	}

	@Override
	public void refresh() {
		revalidate();
		repaint();
	}

}
