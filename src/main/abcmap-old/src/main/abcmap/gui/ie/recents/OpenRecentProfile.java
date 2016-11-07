package abcmap.gui.ie.recents;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JPanel;

import abcmap.gui.comps.fileselection.FileSelectionPanel;
import abcmap.gui.ie.InteractionElement;
import abcmap.gui.ie.profiles.OpenProfile;
import abcmap.gui.ie.recents.RecentHistoryReseter.Mode;
import abcmap.utils.gui.FormUpdater;
import abcmap.utils.gui.Lng;
import abcmap.utils.threads.ThreadManager;
import net.miginfocom.swing.MigLayout;

public class OpenRecentProfile extends InteractionElement {

	private FileSelectionPanel fileSelectionPanel;
	private RecentManagerListener recentManagerListener;

	public OpenRecentProfile() {

		label = Lng.get("open recent configuration profile");
		help = Lng.get("open recent configuration profile help");

		displayInHideableElement = true;

	}

	@Override
	protected Component createPrimaryGUI() {

		// conteneur principal
		JPanel panel = new JPanel(new MigLayout("insets 0"));

		// vue des fichiers
		this.fileSelectionPanel = new FileSelectionPanel();
		fileSelectionPanel.addActionButtonListener(new ProfileOpener());
		fileSelectionPanel.addResetButtonListener(new RecentHistoryReseter(Mode.PROFILES));

		// mise à jour de la vue
		this.recentManagerListener = new RecentManagerListener();

		// ecouter les changements en provenance du manager
		notifm.setDefaultUpdatableObject(recentManagerListener);
		recentsm.getNotificationManager().addObserver(this);

		// panneau scroll avec liste de fichiers
		panel.add(fileSelectionPanel, "width 98%!");

		recentManagerListener.run();

		return panel;

	}

	/**
	 * Ouvrir un profil lors de l'appui sur le bouton correspondant
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class ProfileOpener implements ActionListener {

		private OpenProfile opener;

		public ProfileOpener() {
			// objet d'ouverture de projet unique
			this.opener = new OpenProfile();
		}

		@Override
		public void actionPerformed(ActionEvent e) {

			File activFile = fileSelectionPanel.getActiveFile();
			if (activFile != null && activFile.getAbsolutePath() != null) {
				opener.openProfile(activFile);
				ThreadManager.runLater(opener);
			}

		}

	}

	/**
	 * Objet de mise à jour de la vue des fichiers
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class RecentManagerListener extends FormUpdater {

		protected void updateFields() {
			// recuperer la liste des fichiers
			ArrayList<File> files = recentsm.getProfileHistory();

			// effacer les entrées précédentes
			fileSelectionPanel.clearFileList();

			// ajouter les entrées actuelles
			fileSelectionPanel.addFiles(files);

		};

	}

}
