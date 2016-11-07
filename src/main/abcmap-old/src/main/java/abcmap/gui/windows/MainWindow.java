package abcmap.gui.windows;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;

import abcmap.gui.comps.geo.MapPanel;
import abcmap.gui.comps.share.StatusBar;
import abcmap.gui.dock.comps.Dock;
import abcmap.gui.ie.program.QuitProgram;
import abcmap.managers.GuiManager;
import abcmap.managers.stub.MainManager;
import abcmap.utils.Utils;
import abcmap.utils.gui.GuiUtils;
import abcmap.utils.threads.ThreadManager;

/**
 * Fenêtre principale du programme. 3 modes d'affichage différents: Carte,
 * Tuiles refusées et Mise en page.
 * 
 * @author remipassmoilesel
 *
 */
public class MainWindow extends AbstractCustomWindow {

	/** Le mode d'affichage actuel de la fenêtre */
	private abcmap.gui.windows.MainWindowMode windowMode;

	/** La barre de statut en bas de fenetre */
	private StatusBar statusBar;

	/** Le panneau principal */
	private JPanel contentPane;

	/** Le panneau d'affichage de la carte */
	private MapPanel mapPanel;

	/** Panneau d'affichage des tuiles refusées */
	private JPanel refusedTilesPanel;

	/** Panneau d'affichage de mise en page */
	private JPanel layoutPanel;

	private GuiManager guim;

	private HashMap<MainWindowMode, Component> modesAndComps;

	private Dock dockE;

	private Dock dockW;

	public MainWindow() {

		guim = MainManager.getGuiManager();

		// femeture de la fenetre
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new MainWindowListener());

		// taille par défaut 70% moins grande que l'écran
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		dim.width = dim.width - dim.width / 70;
		dim.height = dim.height - dim.height / 70;
		this.setSize(dim);

		this.setLocationRelativeTo(null);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);

		// panneau principal de contenu
		contentPane = new JPanel(new BorderLayout());
		this.setContentPane(contentPane);

		// barre de statut
		statusBar = new StatusBar();
		contentPane.add(statusBar, BorderLayout.SOUTH);

	}

	/**
	 * Ecouter les changements de la fenêtre
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class MainWindowListener extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {
			ThreadManager.runLater(new QuitProgram());
		}
	}

	/**
	 * Affecter le panneau central
	 * 
	 * @param comp
	 */
	private void setCenterComponent(Component comp) {

		if (comp == null) {
			throw new NullPointerException("Composant cannot be null");
		}

		// retirer le précédent composant si nécéssaire
		Component centerComp = getCenterComponent();
		if (centerComp != null) {
			contentPane.remove(centerComp);
		}

		// ajouter le composant
		getContentPane().add(comp, BorderLayout.CENTER);

	}

	private Component getCenterComponent() {
		BorderLayout layout = (BorderLayout) contentPane.getLayout();
		return layout.getLayoutComponent(BorderLayout.CENTER);
	}

	/**
	 * Changer le mode d'affichage de la fenêtre. Utiliser en priorité la
	 * méthode du gestionnaire d'interface.
	 * 
	 * @param mode
	 */
	@Deprecated
	public void setWindowMode(MainWindowMode mode) {

		GuiUtils.throwIfNotOnEDT();

		// le composant à afficher
		if (modesAndComps == null) {
			mapModesAndComponents();
		}
		Component comp = modesAndComps.get(mode);

		// vérifier l'argument
		if (comp == null) {
			throw new NullPointerException("Invalid mode: " + mode);
		}

		// pas de changement nécéssaire, arret
		if (Utils.safeEquals(getCenterComponent(), comp)) {
			return;
		}

		this.windowMode = mode;

		if (MainWindowMode.SHOW_MAP.equals(mode)) {
			setCenterComponent(mapPanel);
		}

		else if (MainWindowMode.SHOW_REFUSED_TILES.equals(mode)) {
			setCenterComponent(refusedTilesPanel);
		}

		else if (MainWindowMode.SHOW_LAYOUTS.equals(mode)) {
			setCenterComponent(layoutPanel);
		}

		this.revalidate();
		this.repaint();

	}

	/**
	 * Initialisation tardive de la collection mode de fenetre / composant
	 */
	private void mapModesAndComponents() {

		modesAndComps = new HashMap<MainWindowMode, Component>();
		modesAndComps.put(MainWindowMode.SHOW_MAP, mapPanel);
		modesAndComps.put(MainWindowMode.SHOW_REFUSED_TILES, refusedTilesPanel);
		modesAndComps.put(MainWindowMode.SHOW_LAYOUTS, layoutPanel);

	}

	public abcmap.gui.windows.MainWindowMode getWindowMode() {
		return windowMode;
	}

	public StatusBar getStatusBar() {
		return statusBar;
	}

	public void setMapPanel(MapPanel map) {
		this.mapPanel = map;
	}

	public void setRefusedTilesPanel(JPanel refusedTilesPanel) {
		this.refusedTilesPanel = refusedTilesPanel;
	}

	public void setLayoutPanel(JPanel layoutPanel) {
		this.layoutPanel = layoutPanel;
	}

	/**
	 * Retourne le dock Est de la fenetre
	 * 
	 * @return
	 */
	public Dock getEastDock() {
		return dockE;
	}

	/**
	 * Retourne le dock Ouest de la fenetre
	 * 
	 * @return
	 */
	public Dock getWestDock() {
		return dockW;
	}

	public void setWestDock(Dock dockW) {
		this.dockW = dockW;
		getContentPane().add(dockW, BorderLayout.WEST);
	}

	public void setEastDock(Dock dockE) {
		this.dockE = dockE;
		getContentPane().add(dockE, BorderLayout.EAST);
	}

}
