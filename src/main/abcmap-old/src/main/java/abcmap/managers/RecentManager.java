package abcmap.managers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import abcmap.configuration.ConfigurationConstants;
import abcmap.events.RecentHistoryEvent;
import abcmap.utils.gui.GuiUtils;
import abcmap.utils.notifications.HasNotificationManager;
import abcmap.utils.notifications.NotificationManager;

/**
 * Gère les fichiers et les profils de configuration récents.
 * 
 * @author remipassmoilesel
 *
 */
public class RecentManager implements HasNotificationManager {

	// capacité maximum de l'historique des profils et des projets
	private static final int HISTORY_MAX_CAPACITY = 40;

	// nom des balises du fichier de sauvegarde des récents
	private static String ROOT_TAG_NAME = "history";
	private static String PROJECT_TAG_NAME = "project";
	private static String PROFILE_TAG_NAME = "profile";

	// le fichier ou est sauvegardé l'historique
	private File historyFile = new File(ConfigurationConstants.PROFILE_ROOT_PATH + "history");

	/**
	 * le projet le plus recent est à la position 0
	 */
	private ArrayList<File> projectHistory;
	/**
	 * le profil le plus recent est à la position 0
	 */
	private ArrayList<File> profileHistory;
	private NotificationManager observer;

	public RecentManager() {
		this.projectHistory = new ArrayList<File>(HISTORY_MAX_CAPACITY);
		this.profileHistory = new ArrayList<File>(HISTORY_MAX_CAPACITY);
		this.observer = new NotificationManager(RecentManager.this);
	}

	/**
	 * Charger l'historique à partir du fichier de stockage. Envoi une
	 * notification.
	 * 
	 * @throws IOException
	 */
	public void loadHistory() throws IOException {

		GuiUtils.throwIfOnEDT();

		// verifier existence du fichier
		if (historyFile.isFile() == false) {

			// le fichier n'existe pas, création puis arret
			historyFile.createNewFile();
			return;
		}

		// effacer les listes
		profileHistory.clear();
		projectHistory.clear();

		// parser le fichier
		SAXBuilder sxb = new SAXBuilder();
		Document doc = null;
		try {
			doc = sxb.build(historyFile);
		} catch (Exception e) {
			Log.error(e);
			throw new IOException("Unable to read file " + historyFile.getAbsolutePath(), e);
		}

		// iterer les enfant de la balise racine
		ArrayList<Element> elements = new ArrayList<Element>(doc.getRootElement().getChildren());
		Collections.reverse(elements);

		for (Element elmt : elements) {
			File path = new File(elmt.getText());

			// l'enfant est une balise de projet
			if (PROJECT_TAG_NAME.equalsIgnoreCase(elmt.getName())) {
				addFile(projectHistory, path);
			}

			// l'enfant est une balise de profil de configuration
			else if (PROFILE_TAG_NAME.equalsIgnoreCase(elmt.getName())) {
				addFile(profileHistory, path);
			}

		}

		// notification
		observer.fireEvent(new RecentHistoryEvent(RecentHistoryEvent.HISTORY_LOADED, null));

	}

	/**
	 * Vide les listes d'historique. Envoi une notification.
	 */
	public void clearHistory() {
		clearProjectHistory();
		clearProfileHistory();
		observer.fireEvent(new RecentHistoryEvent(RecentHistoryEvent.HISTORY_CLEARED, null));
	}

	public void clearProjectHistory() {
		projectHistory.clear();
		observer.fireEvent(new RecentHistoryEvent(RecentHistoryEvent.HISTORY_CLEARED, null));
	}

	public void clearProfileHistory() {
		profileHistory.clear();
		observer.fireEvent(new RecentHistoryEvent(RecentHistoryEvent.HISTORY_CLEARED, null));
	}

	/**
	 * Sauvegarde les listes d'historique. Envoi une notification.
	 * 
	 * @throws IOException
	 */
	public void saveHistory() throws IOException {

		Element root = new Element(ROOT_TAG_NAME);
		Document xmlDoc = new Document(root);

		// parcours des projets pour ajout xml
		for (File f : projectHistory) {
			Element e = new Element(PROJECT_TAG_NAME);
			e.setText(f.getAbsolutePath());
			root.addContent(e);
		}

		// parcours des profils de configuration pour ajout xml
		for (File f : profileHistory) {
			Element e = new Element(PROFILE_TAG_NAME);
			e.setText(f.getAbsolutePath());
			root.addContent(e);
		}

		// ecrire le fichier
		try {
			XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
			out.output(xmlDoc, new FileOutputStream(historyFile));
		} catch (Exception e) {
			Log.error(e);
			throw new IOException("Unable to write history file ", e);
		}
	}

	/**
	 * Ajoute à la liste passee en parametre l'element passé en parametre.
	 * Retire les éventuels doublon si l'élément est déjà présent. Retire les
	 * elements depassant la capacité max de l'historique.
	 * 
	 * @param list
	 * @param toAdd
	 */
	private void addFile(ArrayList<File> list, File toAdd) {

		for (File inList : new ArrayList<File>(list)) {

			if (inList.getAbsolutePath().equals(toAdd.getAbsolutePath()))
				list.remove(inList);

			if (list.indexOf(inList) >= HISTORY_MAX_CAPACITY) {
				list.remove(inList);
			}
		}

		list.add(0, toAdd);

	}

	public void addProject(File f) {
		addFile(projectHistory, f);
		observer.fireEvent(new RecentHistoryEvent(RecentHistoryEvent.PROJECT_ADDED, f));
	}

	public void removeProject(File f) {
		projectHistory.remove(f);
		observer.fireEvent(new RecentHistoryEvent(RecentHistoryEvent.PROJECT_REMOVED, f));
	}

	public void addProfile(File f) {
		addFile(profileHistory, f);
		observer.fireEvent(new RecentHistoryEvent(RecentHistoryEvent.PROFILE_ADDED, f));
	}

	public void removeProfile(File f) {
		profileHistory.remove(f);
		observer.fireEvent(new RecentHistoryEvent(RecentHistoryEvent.PROFILE_REMOVED, f));
	}

	/**
	 * Retourne la liste des projets recents <br>
	 * Le plus recent est à la position zero
	 * 
	 * @return
	 */
	public ArrayList<File> getProjectHistory() {
		return new ArrayList<File>(projectHistory);
	}

	/**
	 * Retourne la liste des profils de configuration recents <br>
	 * Le plus recent est à la position zero
	 * 
	 * @return
	 */
	public ArrayList<File> getProfileHistory() {
		return new ArrayList<File>(profileHistory);
	}

	@Override
	public NotificationManager getNotificationManager() {
		return observer;
	}

}
