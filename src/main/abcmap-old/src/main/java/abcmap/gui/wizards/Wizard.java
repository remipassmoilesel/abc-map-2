package abcmap.gui.wizards;

import java.util.ArrayList;
import java.util.List;

import abcmap.gui.HasDisplayableSpace;
import abcmap.gui.iegroup.InteractionElementGroup;
import abcmap.gui.iegroup.docks.GroupWizard;
import abcmap.gui.windows.DetachedWindow;
import abcmap.managers.GuiManager;
import abcmap.managers.stub.MainManager;
import abcmap.utils.gui.GuiUtils;

/**
 * Un assistant du logiciel. Permet via une interface graphique étape par étape
 * la découverte des fonctionnalités du logiciel. Est composé de plusieurs
 * panneaux d'étape.
 * <p>
 * Un assitant devrait toujours avoir un titre, une description courte affichée
 * dans la liste des assistants disponibles ainsi qu'un premier panneau
 * d'introduction expliquant le but de l'assistant.
 * <p>
 * La méthode addNewStep(title, desc, elements) permet d'ajouter une nouvelle
 * étape simplement, ou elements et une liste d'objet à afficher: texte,
 * elements d'interaction, ou composants graphiques Swing.
 * 
 * @author remipassmoilesel
 *
 */
public class Wizard {

	/** Les différents panneaux d'étape */
	protected ArrayList<WizardStepPanel> steps;

	/** L'étape en cours */
	protected int currentStep;

	/** Le titre de l'assistant */
	protected String title;

	/** La description de l'assistant */
	protected String description;

	/** L'espace ou sera affiché l'assistant */
	private HasDisplayableSpace parent;

	protected GuiManager guim;

	/** Si vrai un bouton permet d'ouvrir l'assistant dans une nouvelle fenêtre */
	protected boolean newWindowButtonEnabled;

	public Wizard() {
		guim = MainManager.getGuiManager();

		this.steps = new ArrayList<WizardStepPanel>();
		this.currentStep = 0;
		this.title = "no name";
		this.description = "no description";

		this.newWindowButtonEnabled = true;
	}

	public void reconstructStepPanels() {
		for (WizardStepPanel wsp : steps) {
			wsp.reconstruct();
		}
	}

	public boolean isNewWindowButtonEnabled() {
		return newWindowButtonEnabled;
	}

	/**
	 * Activer un bouton d'ouverture de l'assistant dans une nouvelle fenêtre
	 * 
	 * @param newWindowButtonEnabled
	 */
	public void setNewWindowButtonEnabled(boolean newWindowButtonEnabled) {
		this.newWindowButtonEnabled = newWindowButtonEnabled;
	}

	/**
	 * Créé, enregistre et retourne un panneau d'étape.
	 * 
	 * @return
	 */
	protected WizardStepPanel addNewStep() {
		WizardStepPanel wsp = new WizardStepPanel(this);
		steps.add(wsp);
		return wsp;
	}

	/**
	 * Créé, enregistre et retourne un panneau d'étape.
	 * 
	 * @return
	 */
	protected WizardStepPanel addNewStep(String name, List elements) {

		WizardStepPanel wsp = new WizardStepPanel(this);
		steps.add(wsp);

		wsp.setName(name);
		wsp.addElements(elements);

		wsp.reconstruct();

		return wsp;
	}

	/**
	 * Ajoute une étape pour chaque élement passé en paramètre.
	 * 
	 * @param titles
	 * @param descriptions
	 * @param customComps
	 */
	protected void addNewSteps(ArrayList<String> titles,
			ArrayList<List> elements) {

		int ts = titles.size();
		int es = elements.size();

		if ((ts + es) / 2 != ts) {
			throw new IllegalStateException(
					"Invalids arguments, lists must have the same size: " + ts
							+ " " + es);
		}

		for (int i = 0; i < titles.size(); i++) {
			String t = titles.get(i);
			addNewStep(t, elements.get(i));
		}

	}

	/**
	 * Cloner cet assistant et l'afficher dans une fenêtre détachée.
	 */
	public void showInDetachedWindow() {

		GuiUtils.throwIfNotOnEDT();

		// instancier un nouvel assistant
		Wizard w2;
		try {
			w2 = this.getClass().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new IllegalStateException(e);
		}

		// l'associer a une fenetre
		DetachedWindow wizwin = guim.getWizardDetachedWindow();
		w2.setNewWindowButtonEnabled(false);
		w2.setDisplayableSpace(wizwin);
		// w2.reconstructStepPanels();

		w2.showStep(currentStep);

		// inutile car appelé par showStep
		// wizwin.reconstruct();

		// montrer à la position par defaut
		wizwin.moveToDefaultPosition();
		wizwin.setVisible(true);

	}

	/**
	 * Affecter le parent de l'assistant
	 * 
	 * @param dock
	 */
	public void setDisplayableSpace(HasDisplayableSpace parent) {
		this.parent = parent;
	}

	/**
	 * Affiche une étape de l'assistant. Si le paramètre est hors index, il sera
	 * corrigé.
	 * 
	 * @param i
	 */
	public void showStep(int i) {

		currentStep = i;

		if (steps.size() < 1) {
			throw new IllegalStateException("No step to show !");
		}

		if (parent == null) {
			throw new NullPointerException("No parent where show wizard !");
		}

		// verifiation de l'index
		if (currentStep > steps.size() - 1) {
			currentStep = steps.size() - 1;
		}

		if (currentStep < 0) {
			currentStep = 0;
		}

		// extraction du panneau ou retour
		WizardStepPanel wsp = steps.get(currentStep);

		// config des boutons suiv et prec
		boolean prevEnabled = currentStep > 0;
		boolean suivEnabled = currentStep < steps.size() - 1;

		wsp.setPreviousButtonEnabled(prevEnabled);
		wsp.setNextButtonEnabled(suivEnabled);

		wsp.reconstruct();

		parent.displayComponent(wsp);
		parent.reconstruct();

	}

	/**
	 * Affiche la prochaine étape disponible
	 */
	public void nextStep() {
		currentStep++;
		showStep(currentStep);
	}

	/**
	 * Affiche l'étape précédente si disponible
	 */
	public void previousStep() {
		currentStep--;
		showStep(currentStep);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Demande au dock affecté d'afficher le groupe d'interaction des
	 * assistants.
	 * 
	 * @return
	 */
	public void showWizardHome() {
		guim.showGroupInDock(GroupWizard.class);
	}

	/**
	 * Construire une chaine correspondant à un bouton d'affichage de groupe
	 * d'interaction. Utile pour utilisation de la méthode addElements()
	 * 
	 * @param class1
	 * @param desc
	 * @return
	 */
	protected Object createShowGroupString(
			Class<? extends InteractionElementGroup> class1, String desc) {
		return WizardStepPanel.IE_GROUP_MARK + class1.getSimpleName()
				+ WizardStepPanel.IE_GROUP_MARK + desc;
	}

}
