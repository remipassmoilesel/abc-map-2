package abcmap.gui.ie.recents;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JPanel;

import abcmap.gui.comps.fileselection.FileSelectionPanel;
import abcmap.gui.ie.InteractionElement;
import abcmap.gui.ie.project.OpenProject;
import abcmap.gui.ie.recents.RecentHistoryReseter.Mode;
import abcmap.utils.gui.FormUpdater;
import abcmap.utils.gui.Lng;
import abcmap.utils.notifications.HasNotificationManager;
import abcmap.utils.threads.ThreadManager;
import net.miginfocom.swing.MigLayout;

/**
 * Ouvrir un projet récent
 * 
 * @author remipassmoilesel
 *
 */
public class OpenRecentProject extends InteractionElement implements HasNotificationManager {

	private FileSelectionPanel fileSelectionPanel;
	private FileViewUpdater fileViewUpdater;

	public OpenRecentProject() {

		this.label = Lng.get("open recent project");
		this.help = Lng.get("open recent project help");

	}

	@Override
	protected Component createPrimaryGUI() {

		// conteneur principal
		JPanel panel = new JPanel(new MigLayout("insets 5"));

		// vue des fichiers
		this.fileSelectionPanel = new FileSelectionPanel();
		fileSelectionPanel.addActionButtonListener(new ProjectOpener());
		fileSelectionPanel.addResetButtonListener(new RecentHistoryReseter(Mode.PROJECTS));

		
		// panneau scroll avec liste de fichiers
		panel.add(fileSelectionPanel, "width 98%!");

		// mise à jour de la vue
		this.fileViewUpdater = new FileViewUpdater();

		// ecouter les changements en provenance du manager
		notifm.setDefaultUpdatableObject(fileViewUpdater);
		recentsm.getNotificationManager().addObserver(this);

		fileViewUpdater.run();

		return panel;
	}

	private class ProjectOpener implements ActionListener {

		private OpenProject opener;

		public ProjectOpener() {
			// objet d'ouverture de projet unique
			this.opener = new OpenProject();

		}

		@Override
		public void actionPerformed(ActionEvent e) {

			File activFile = fileSelectionPanel.getActiveFile();
			if (activFile != null) {
				opener.openProject(activFile);
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
	private class FileViewUpdater extends FormUpdater {

		@Override
		protected void updateFields() {

			// recuperer la liste des fichiers
			ArrayList<File> files = recentsm.getProjectHistory();

			// effacer les entrées precedentes
			fileSelectionPanel.clearFileList();

			// ajouter les entrées actuelles
			fileSelectionPanel.addFiles(files);

		}
	}

}
