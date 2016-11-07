package abcmap.gui.comps.layout;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import abcmap.configuration.ConfigurationConstants;
import abcmap.events.ProjectEvent;
import abcmap.managers.ConfigurationManager;
import abcmap.managers.ProjectManager;
import abcmap.managers.stub.MainManager;
import abcmap.project.layouts.LayoutPaper;
import abcmap.utils.Refreshable;
import abcmap.utils.gui.GuiUtils;
import abcmap.utils.notifications.Notification;
import abcmap.utils.notifications.HasNotificationManager;
import abcmap.utils.notifications.NotificationManager;
import net.miginfocom.swing.MigLayout;

/**
 * Panneau d'affichage de toutes les feuilles d'impression disponibles. Destiné
 * à être intégré directement tel quel dans une fenêtre.
 * 
 * @author remipassmoilesel
 *
 */
public class LayoutScrollPanel extends JPanel implements Refreshable,
		HasNotificationManager {

	private JPanel view;
	private ProjectManager projectm;
	private NotificationManager notifm;
	private ConfigurationManager configm;

	public LayoutScrollPanel() {

		projectm = MainManager.getProjectManager();
		configm = MainManager.getConfigurationManager();

		// écouter le projet et reconstruire la vue lors d'un changement dans la
		// liste de feuilles
		notifm = new LayoutViewNotifM();
		projectm.getNotificationManager().addObserver(this);

		// caractéristiques
		setBackground(Color.DARK_GRAY);
		setLayout(new BorderLayout());

		// la vue centrale
		view = new JPanel(new MigLayout("fillx"));
		JScrollPane scrollpane = new JScrollPane(view);

		// defilement vertical
		scrollpane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		JScrollBar vsb = scrollpane.getVerticalScrollBar();
		vsb.setUnitIncrement(ConfigurationConstants.SCROLLBAR_UNIT_INCREMENT);

		add(scrollpane, BorderLayout.CENTER);
	}

	@Override
	public void refresh() {
		view.revalidate();
		view.repaint();
	}

	@Override
	public void reconstruct() {

		GuiUtils.throwIfNotOnEDT();

		// retirer tous les panneaux
		view.removeAll();

		// lister les feuilles de mise en page
		for (LayoutPaper paper : projectm.getLayouts()) {

			LayoutSupportPanel pn = new LayoutSupportPanel(paper);
			view.add(pn, "wrap 15px, align center");

		}
		
		refresh();

	}

	@Override
	public NotificationManager getNotificationManager() {
		return notifm;
	}

	/**
	 * Gestion des notifications. Si la liste des feuilles change le composant
	 * graphique reconstruit ses feuilles.
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class LayoutViewNotifM extends NotificationManager {

		public LayoutViewNotifM() {
			super(LayoutScrollPanel.this);
		}

		@Override
		public void notificationReceived(Notification arg) {
			if (arg instanceof ProjectEvent
					&& arg.getName().equals(ProjectEvent.LAYOUTS_LIST_CHANGED)) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						reconstruct();
					}
				});
			}
		}

	}

}
