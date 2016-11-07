package abcmap.gui.dock.comps;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

import abcmap.gui.HasDisplayableSpace;
import abcmap.gui.iegroup.InteractionElementGroup;
import abcmap.managers.GuiManager;
import abcmap.managers.stub.MainManager;
import abcmap.utils.gui.GuiUtils;
import abcmap.utils.gui.HasExpandableHelp;
import net.miginfocom.swing.MigLayout;

public class Dock extends JPanel implements HasDisplayableSpace {

	public enum DockOrientation {
		WEST, EST
	}

	public static final int DOCK_WIDTH = 60;
	public static final int MENU_ITEM_WIDTH = 260;
	public static final int VERTICAL_SCROLLBAR_UNIT_INCREMENT = 50;
	public static final Color DOCK_BACKGROUND = new Color(240, 240, 240);

	/**
	 * Panneau ou sont affichés les widget, par exemple les boutons permettant
	 * l'ouverture d'un menu.
	 */
	private JPanel widgetIconsPanel;

	/**
	 * L'espace ou sont affichés les menus
	 */
	private DockWidgetSpacePanel widgetSpacePanel;

	/**
	 * Orientation du dock: E / W
	 */
	private DockOrientation orientation;
	private GuiManager guim;

	public Dock(DockOrientation orientation) {

		guim = MainManager.getGuiManager();

		setLayout(new MigLayout("insets 0, gap 3, fill"));

		this.orientation = orientation;

		// barre laterale ou sont placés les widgets
		widgetIconsPanel = new JPanel(new MigLayout("insets 20 5 5 5, gapy 20"));
		widgetIconsPanel.setOpaque(false);
		widgetIconsPanel.setBorder(BorderFactory.createLineBorder(
				Color.LIGHT_GRAY, 1));
		String place = orientation.equals(DockOrientation.WEST) ? "dock west"
				: "dock east";
		add(widgetIconsPanel, "grow, width " + DOCK_WIDTH + ", " + place);

		// espace masquable ou sont affichés les menus
		widgetSpacePanel = new DockWidgetSpacePanel(orientation);

	}

	public DockWidgetSpacePanel getWidgetSpacePanel() {
		return widgetSpacePanel;
	}

	public void displayNextWidgetspaceAvailable() {
		widgetSpacePanel.displayNext();
	}

	public void displayPreviousWidgetspaceAvailable() {
		widgetSpacePanel.displayPrevious();
	}

	/**
	 * Afficher le panneau latéral de menu avec au centre le composant passé en
	 * parametre. Ce doit être la seule méthode utilisée pour afficher un
	 * panneau dans un doc.
	 * 
	 * @param widgetsp
	 */
	public void showWidgetspace(Component widgetsp) {

		// mise en place du widget space
		widgetSpacePanel.displayNew(widgetsp);

		// afficher le panneau masquable si nécéssaire
		boolean alreadyHere = Arrays.asList(getComponents()).contains(
				widgetSpacePanel);
		if (alreadyHere == false) {
			add(widgetSpacePanel, "dock center");
		}

		// actualisation
		widgetsp.revalidate();
		widgetsp.repaint();

		widgetSpacePanel.refresh();

		refresh();

	}

	@Override
	public void displayComponent(JComponent comp) {
		showWidgetspace(comp);
	}

	public void hideWidgetspace() {

		GuiUtils.throwIfNotOnEDT();

		remove(widgetSpacePanel);

		setAllMenuSelected(false);

		refresh();
	}

	public void addWidget(JComponent widget) {
		widgetIconsPanel.add(widget, "grow, wrap");
	}

	@Override
	public void reconstruct() {
		refresh();
	}

	@Override
	public void refresh() {
		revalidate();
		repaint();
	}

	public DockOrientation getOrientation() {
		return orientation;
	}

	@Deprecated
	@Override
	public Component add(Component comp) {
		return super.add(comp);
	}

	@Deprecated
	@Override
	public Component add(String name, Component comp) {
		return super.add(name, comp);
	}

	@Deprecated
	@Override
	public Component add(Component comp, int index) {
		return super.add(comp, index);
	}

	@Deprecated
	@Override
	public void add(Component comp, Object constraints) {
		super.add(comp, constraints);
	}

	@Deprecated
	@Override
	public void add(Component comp, Object constraints, int index) {
		super.add(comp, constraints, index);
	}

	public static void expandHelpRecursively(Container cont, boolean val) {

		// Util.pr(cont.getClass().getName() + " " + cont.hashCode());

		if (cont instanceof HasExpandableHelp) {
			HasExpandableHelp he = (HasExpandableHelp) cont;
			if (he.isHelpExpanded() != val) {
				he.expandHelp(val);
				cont.revalidate();
				cont.repaint();
			}
		}

		for (Component c : cont.getComponents()) {

			if (c instanceof Container)
				expandHelpRecursively((Container) c, val);

			else if (c instanceof HasExpandableHelp) {
				HasExpandableHelp he = (HasExpandableHelp) c;
				if (he.isHelpExpanded() != val) {
					he.expandHelp(val);
					c.revalidate();
					c.repaint();
				}
			}

		}
	}

	/**
	 * Retrouve et afficher le premier menu de dock correspondant à la classe du
	 * groupe d'interaction passé en paramètre, ou retourne faux si aucun menu
	 * n'est trouvé.
	 * <p>
	 * Le mode de la fenêtre principale sera modifié en conséquence.
	 * 
	 * @param iegClass
	 * @return
	 */
	public boolean showFirstMenuPanelCorrespondingThis(
			Class<? extends InteractionElementGroup> iegClass) {
		return showFirstMenuPanelCorrespondingThis(iegClass.getSimpleName());
	}

	/**
	 * Retrouve et afficher le premier menu de dock correspondant à la classe du
	 * groupe d'interaction passé en paramètre, ou retourne faux si aucun menu
	 * n'est trouvé.
	 * <p>
	 * Le mode de la fenêtre principale sera modifié en conséquence.
	 * 
	 * @param iegClass
	 * @return
	 */
	public boolean showFirstMenuPanelCorrespondingThis(String className) {

		// Itérer les widgets disponibles
		for (Component c : widgetIconsPanel.getComponents()) {

			// le widget est un menu
			if (c instanceof DockMenuWidget) {

				// comparer les noms de classes
				DockMenuWidget mw = (DockMenuWidget) c;
				if (className.equals(mw.getInteractionElementGroup().getClass()
						.getSimpleName())) {

					// changer le mode de fenetre si cenessaire
					guim.setMainWindowMode(mw.getInteractionElementGroup()
							.getWindowMode());

					// afficher l'espace
					showWidgetspace(mw.getMenuPanel());

					return true;
				}
			}

		}

		return false;

	}

	/**
	 * Retourne le composant de menu qui contient comp, ou null si aucun
	 * résultat ne correspond.
	 * 
	 * @param comp
	 * @return
	 */
	public DockMenuWidget getMenuWidgetParentOf(Component comp) {

		return (DockMenuWidget) GuiUtils.searchParentOf(comp,
				DockMenuWidget.class);
	}

	public void setMenuSelected(DockMenuWidget menu) {

		GuiUtils.throwIfNotOnEDT();

		setAllMenuSelected(false);

		menu.setSelected(true);

	}

	private void setAllMenuSelected(boolean val) {
		for (Component c : widgetIconsPanel.getComponents()) {
			if (c instanceof DockMenuWidget) {

				DockMenuWidget dwm = (DockMenuWidget) c;
				dwm.setSelected(val);

				dwm.revalidate();
				dwm.repaint();
			}
		}
	}

	/**
	 * Retourne le dock parent du composant ou null si aucun parent ne
	 * correspond à la class Dock
	 * 
	 * @param comp
	 * @return
	 */
	public static Dock getDockParentForComponent(Component comp) {
		return (Dock) GuiUtils.searchParentOf(comp, Dock.class);
	}

}
