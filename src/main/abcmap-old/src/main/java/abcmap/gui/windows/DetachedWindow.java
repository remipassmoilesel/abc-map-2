package abcmap.gui.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import abcmap.managers.stub.MainManager;
import net.miginfocom.swing.MigLayout;
import abcmap.configuration.ConfigurationConstants;
import abcmap.gui.HasDisplayableSpace;
import abcmap.gui.comps.buttons.HtmlButton;
import abcmap.gui.comps.buttons.HtmlCheckbox;
import abcmap.gui.comps.progressbar.HasProgressbarManager;
import abcmap.gui.comps.progressbar.ProgressbarManager;
import abcmap.gui.ie.InteractionElement;
import abcmap.gui.ie.display.window.ShowMainWindow;
import abcmap.gui.iegroup.InteractionElementGroup;
import abcmap.managers.GuiManager;
import abcmap.utils.Refreshable;
import abcmap.utils.notifications.HasNotificationManager;
import abcmap.utils.notifications.NotificationManager;

/**
 * Fenetre de taille réduite destinée détachée de la fenêtre principale.
 * 
 * @author remipassmoilesel
 *
 */
public class DetachedWindow extends AbstractCustomWindow implements Refreshable,
		HasNotificationManager, HasDisplayableSpace, HasProgressbarManager {

	private static final Dimension WINDOW_PREF_SIZE = new Dimension(300, 500);

	/** L'objet à afficher dans la fenêtre */
	private Object displayable;

	/** Le panneau en tête de la fenêtre */
	private JPanel headerPane;

	/** Le panneau de contenu de la fenetre */
	private JPanel contentPane;

	/** La vue du scrollpane au centre de la fenêtre */
	private JPanel viewportView;

	/**
	 * Si vrai le bouton de retour à la fenetre principale sera visible.
	 */
	private boolean buttonToMainWindowEnabled;

	private GuiManager guim;
	private ProgressbarManager progressbarManager;
	private NotificationManager notifm;

	public DetachedWindow() {
		super();

		this.guim = MainManager.getGuiManager();
		this.notifm = new NotificationManager(this);

		// taille par défaut
		this.setSize(WINDOW_PREF_SIZE);
		this.setResizable(false);

		// action à mener lors de la fermeture
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new CustomWindowListener());

		// panneau principal
		contentPane = new JPanel(new MigLayout("insets 0, gap 0, fill"));
		this.setContentPane(contentPane);

		// afficher le bouton retour par défaut
		buttonToMainWindowEnabled = true;

		// par defaut toujours au dessu
		setAlwaysOnTop(true);

		reconstruct();
	}

	@Override
	public void displayComponent(JComponent displayable) {
		this.displayable = displayable;
	}

	public void displayComponent(InteractionElementGroup group) {
		this.displayable = group;
	}

	@Override
	public void reconstruct() {

		contentPane.removeAll();

		// panneau d'en tete
		headerPane = new JPanel(new MigLayout("insets 5, gap 5"));
		headerPane.setBorder(BorderFactory.createLineBorder(Color.lightGray));
		contentPane.add(headerPane, "north");

		// Bouton d'activation du mode 'toujours au dessus'
		HtmlCheckbox chkAlwaysOnTop = new HtmlCheckbox(
				"Afficher cette fenêtre toujours au dessus des autres.");
		chkAlwaysOnTop.setSelected(true);
		chkAlwaysOnTop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				DetachedWindow.this.setAlwaysOnTop(((AbstractButton) arg0
						.getSource()).isSelected());
			}
		});
		headerPane.add(chkAlwaysOnTop, "wrap");

		// bouton de retour à la fenêtre principale
		if (buttonToMainWindowEnabled) {
			HtmlButton btnMainWindow = new HtmlButton(
					"Retour à la fenêtre principale");
			btnMainWindow.addActionListener(new ShowMainWindow());
			headerPane.add(btnMainWindow, "align right, wrap");
		}

		// barre de progression
		progressbarManager = new ProgressbarManager();
		progressbarManager.setComponentsVisible(false);

		// contenu du scrollpane au centre de la fenetre
		viewportView = new JPanel(new MigLayout("insets 5, gap 5"));

		// scrollpane au centre de la fenêtre
		JScrollPane scrollPane = new JScrollPane(viewportView);
		scrollPane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.getVerticalScrollBar().setUnitIncrement(
				ConfigurationConstants.SCROLLBAR_UNIT_INCREMENT);
		contentPane.add(scrollPane, "center, grow, push");

		// rien a afficher, retour
		if (displayable == null) {
			return;
		}

		// le composant a afficher est un groupe
		if (displayable instanceof InteractionElementGroup) {

			// ajouter les composants
			for (InteractionElement e : ((InteractionElementGroup) displayable)
					.getElements()) {

				if (e.isHiddenInDetachedWindow()) {
					continue;
				}

				addViewportItem(e.getBlockGUI());
			}

		}

		// le composant a afficher est un composant graphique
		else if (displayable instanceof Component) {
			viewportView.add((Component) displayable);
		}

		// le composant n'est pas pris en charge
		else {
			throw new IllegalArgumentException("Unknown displayable object: "
					+ displayable);
		}

		// rafraichir
		refresh();
	}

	/**
	 * Ajout d'un element avec contraintes adaptées
	 * 
	 * @param c
	 */
	private void addViewportItem(Component c) {
		viewportView.add(c, "width "
				+ (DetachedWindow.WINDOW_PREF_SIZE.width - 40) + "!, wrap");
	}

	/**
	 * Déplacer la fenêtre jusqu'a sa position par defaut
	 */
	public void moveToDefaultPosition() {
		setLocation(new Point(20, 20));
	}

	@Override
	public void refresh() {

		headerPane.revalidate();
		headerPane.repaint();

		contentPane.revalidate();
		contentPane.repaint();

		revalidate();
		repaint();
	}

	@Override
	public NotificationManager getNotificationManager() {
		return notifm;
	}

	@Deprecated
	@Override
	public void dispose() {
		super.dispose();
	}

	public ProgressbarManager getProgressbarManager() {
		return progressbarManager;
	}

	/**
	 * Masque la fenêtre lors de la fermeture.
	 * 
	 * @author remipassmoilesel
	 *
	 */
	public class CustomWindowListener extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {
			setVisible(false);
			new ShowMainWindow().run();
		}
	}

	public void setMainWindowButtonVisible(boolean b) {
		this.buttonToMainWindowEnabled = b;
	}

}
