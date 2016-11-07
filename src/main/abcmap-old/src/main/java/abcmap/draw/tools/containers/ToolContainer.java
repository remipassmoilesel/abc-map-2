package abcmap.draw.tools.containers;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import abcmap.draw.tools.MapTool;
import abcmap.gui.comps.help.InteractionSequence;
import abcmap.gui.toolOptionPanels.ToolOptionPanel;
import abcmap.managers.Log;
import abcmap.managers.ShortcutManager;
import abcmap.managers.stub.MainManager;
import abcmap.utils.Utils;

/**
 * Conteneur d'outil. Peut retourner une instance d'outil et contient les
 * metadonnées associees (nom de l'outil, icone, ...)
 * 
 * @author remipassmoilesel
 *
 */
public class ToolContainer {

	/** L'instance courante de l'outil utilisé */
	protected MapTool currentInstance;

	/** L'identifiant de l'outil */
	protected String id;

	/** La classe de l'outil pour instanciation sur demande */
	protected Class<? extends MapTool> toolClass;

	/** Le nom de l'outil pour lecture */
	protected String readableName;

	/** L'icone de l'outil */
	protected ImageIcon icon;

	/** Le raccourci clavier de l'outil */
	protected KeyStroke accelerator;

	/** La classe du panneau d'options pour instanciation automatique */
	protected Class<? extends ToolOptionPanel> optionPanelClass;

	/** L'instance courante du panneau d'option */
	protected ToolOptionPanel optionPanel;

	/** Les sequences d'interactions possible de l'outil */
	protected InteractionSequence[] interactionsSequences;

	protected ShortcutManager shortcuts;

	public ToolContainer() {
		this.shortcuts = MainManager.getShortcutManager();
	}

	/**
	 * Retourne le panneau d'options associé à l'outil.
	 * 
	 * @return
	 */
	public ToolOptionPanel getOptionPanel() {
		if (optionPanel == null)
			return createToolOptionPanel();
		else
			return optionPanel;
	}

	/**
	 * Créer un panneau d'options de l'outil
	 * 
	 * @return
	 */
	protected ToolOptionPanel createToolOptionPanel() {
		// Création par defaut d'un panneau d'option
		if (optionPanelClass != null) {
			try {
				optionPanel = optionPanelClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				Log.error(e);
				return null;
			}
		}

		return optionPanel;
	}

	public String getReadableName() {
		return readableName;
	}

	public ImageIcon getIcon() {
		return icon;
	}

	public String getId() {
		return id;
	}

	/**
	 * Comparaison basée sur le nom de l'outil
	 */
	@Override
	public boolean equals(Object obj) {

		if (obj instanceof ToolContainer)
			return false;

		ToolContainer t = (ToolContainer) obj;
		return Utils.safeEqualsIgnoreCase(t.getId(), getId());

	}

	/**
	 * Retourne une instance de l'outil. Si l'outil est affécté via le champs
	 * toolClass et instatiable sans argument, alors il sera créé au premier
	 * appel.
	 * <p>
	 * Si l'outils nest pas instantiable sans arguments, overrider cette
	 * méthode.
	 * 
	 * @return
	 */
	public MapTool getNewInstance() {

		if (toolClass == null) {
			throw new IllegalStateException("No tool affected");
		}

		else {

			// retourner une instance simple, créé sans arguments
			try {
				currentInstance = toolClass.newInstance();
				return currentInstance;

			} catch (InstantiationException | IllegalAccessException e) {
				// erreur lors de l'instanciation
				Log.error(e);
				throw new IllegalStateException("Cannot instantiate tool");
			}

		}

	}

	public MapTool getCurrentInstance() {
		return currentInstance;
	}

	public KeyStroke getAccelerator() {
		return accelerator;
	}

	public InteractionSequence[] getInteractions() {
		return interactionsSequences;
	}
}
