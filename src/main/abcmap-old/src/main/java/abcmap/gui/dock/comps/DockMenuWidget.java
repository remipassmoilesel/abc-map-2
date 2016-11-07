package abcmap.gui.dock.comps;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import net.miginfocom.swing.MigLayout;
import abcmap.gui.GuiIcons;
import abcmap.gui.comps.CustomComponent;
import abcmap.gui.comps.buttons.HtmlLabel;
import abcmap.gui.dock.comps.blockitems.DockMenuPanel;
import abcmap.gui.ie.InteractionElement;
import abcmap.gui.iegroup.InteractionElementGroup;
import abcmap.gui.windows.MainWindowMode;
import abcmap.managers.GuiManager;
import abcmap.managers.stub.MainManager;

/**
 * Composant de dock. Se présente sous la forme d'un bouton cliquable qui ouvre
 * un menu latéral. Chaque bouton est associé à une vue, par défaut la vue de la
 * carte.
 * 
 * @author remipassmoilesel
 *
 */
public class DockMenuWidget extends CustomComponent {

	/** Le panneau menu à afficher lors d'un clic */
	private DockMenuPanel menuPanel;

	/** Les elements du menu */
	private InteractionElementGroup interactionElementGroup;

	/** L'icone du widget */
	private JLabel lblIcon;

	/** Le dock auquel appartient l'element, lazy-init */
	private Dock dock;

	/** Le mode associé au dock */
	private MainWindowMode windowMode;

	private GuiManager guim;

	public DockMenuWidget() {

		this.guim = MainManager.getGuiManager();

		// style du composant graphique
		setLayout(new MigLayout("insets 5"));
		setOpaque(true);

		this.menuPanel = null;

		// mode par défaut
		this.windowMode = MainWindowMode.SHOW_MAP;

		// icone par défaut
		lblIcon = new JLabel(GuiIcons.DEFAULT_GROUP_ICON);
		add(lblIcon);

		// ecouter les clics
		addActionListener(new WidgetActionListener());

	}

	/**
	 * Ecoute un clic sur le bouton pour ouvrir le menu de dock
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class WidgetActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			// retrouver le dock parent
			if (dock == null) {
				dock = Dock.getDockParentForComponent(DockMenuWidget.this);
			}

			// affichage du menu
			dock.showWidgetspace(menuPanel);

			// sélection du menu dans le dock
			dock.setMenuSelected(DockMenuWidget.this);

			// affichage du mode de la fenetre principale adapté
			guim.setMainWindowMode(windowMode);
		}
	}

	/**
	 * Affecter le mode de fenetre principale à utiliser avec le groupe.
	 * 
	 * @param windowMode
	 */
	public void setWindowMode(MainWindowMode windowMode) {
		this.windowMode = windowMode;
	}

	/**
	 * Affecter l'icone du widget
	 * 
	 * @param icon
	 */
	public void setIcon(ImageIcon icon) {

		if (icon == null) {
			throw new NullPointerException("Icon cannot be null");
		}

		lblIcon.setIcon(icon);
		lblIcon.revalidate();
		lblIcon.repaint();
	}

	/**
	 * Affecter un groupe d'interaction
	 * 
	 * @param ieg
	 */
	public void setInteractionElementGroup(InteractionElementGroup ieg) {

		this.interactionElementGroup = ieg;

		setIcon(ieg.getBlockIcon());
		setToolTipText(ieg.getLabel());

		// créer le panneau de sous menu
		menuPanel = new DockMenuPanel();

		// ajouter le titre du panneau
		menuPanel.setMenuTitle(ieg.getLabel());

		// ajouter la description
		menuPanel.setMenuDescription(ieg.getHelp());

		// ajouter les composants
		for (InteractionElement e : ieg.getElements()) {

			// tester si l'element proposé est un sepearateur
			if (InteractionElementGroup.isSeparator(e)) {
				menuPanel.addMenuElement(new HtmlLabel(" "));
			}

			// ou ajout d'un element d'interaction
			else {
				menuPanel.addMenuElement(e);
			}

		}

		// rafraichir
		menuPanel.reconstruct();
	}

	/**
	 * Retourne le groupe d'interaction associé au widget
	 * 
	 * @return
	 */
	public InteractionElementGroup getInteractionElementGroup() {
		return interactionElementGroup;
	}

	/**
	 * Retourne un JPanel contenant tous les elements du menu
	 * 
	 * @return
	 */
	public DockMenuPanel getMenuPanel() {
		return menuPanel;
	}

}
