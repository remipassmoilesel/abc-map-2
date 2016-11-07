package abcmap.gui.comps.textfields.search;

import java.awt.Component;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JPanel;

import abcmap.configuration.ConfigurationConstants;
import abcmap.gui.ie.InteractionElement;
import abcmap.managers.stub.MainManager;
import abcmap.utils.gui.GuiUtils;
import abcmap.utils.lists.CoeffComparator;
import abcmap.utils.lists.CoeffComparator.ComparableObject;
import abcmap.utils.lists.CoeffComparator.Order;
import abcmap.utils.threads.ThreadAccessControl;

/**
 * Champs texte de recherche d'elements d'interaction.
 * 
 * @author remipassmoilesel
 *
 */
public class CommandSearchTextField extends InteractiveTextField {

	public static final Integer POPUP_WIDTH_PX = 350;

	private ArrayList<InteractionElement> ielements;
	private ArrayList<Component> ieGUIs;

	private String lastSearch = "";
	private int maxResultDisplayed;

	public CommandSearchTextField() {
		super();

		// nombre max de resultats
		this.maxResultDisplayed = 30;

		// lister toutes les possibilités de recherche
		ielements = InteractionElement.getAllAvailablesInteractionElements();
		ielements.addAll(InteractionElement.getAllAvailablesPlugins());

		// créer toutes les instances graphiques de résultats de recherche
		// Les elements graphiques et les ie doivent avoir le même index
		this.ieGUIs = new ArrayList<Component>();
		for (InteractionElement ie : ielements) {
			ieGUIs.add(new IESearchResultPanel(getPopup(), ie));
		}

	}

	/**
	 * L'utilisateur vient de saisir une chaine de caractères
	 */
	@Override
	protected void userHaveTypedThis(String text) {

		// un seul thread à la fois
		if (ThreadAccessControl.get(1).askAccess() == false) {
			return;
		}

		// La recherche n'a pas changé, arret
		if (lastSearch.equals(text)) {
			showPopup(true);
			ThreadAccessControl.get(1).releaseAccess();
			return;
		}

		else {
			lastSearch = new String(text);
		}

		// creer une regex a partir des mots clefs de recherche
		String[] words = Pattern.compile("\\s+").split(text);
		Pattern[] patterns = new Pattern[words.length];

		for (int i = 0; i < words.length; i++) {

			// le mot a transformer
			String w = words[i];

			// remplacer tous les caracteres speciaux par une classe generique
			String regex = w.replaceAll("\\W", ".").toLowerCase();

			// remplacer les voyelles accentuables
			regex = regex.replaceAll("[eao]", ".").toLowerCase();

			// créer un pattern insensible à la case
			patterns[i] = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		}

		// Recherche des elements d'interaction
		// Chaque élément est associé à un coefficient, plus il est eleve plus
		// il correspond a la recherche

		ArrayList<ComparableObject> results = new ArrayList<ComparableObject>();

		// Liste d'importance des champs. A mettre en relation avec searchIn.
		// Ici une occurence dans le nom vaut deux points contre 1 seul dans
		// l'aide
		Integer[] points = new Integer[] { 2, 1 };

		for (InteractionElement ie : ielements) {

			// rechercher dans ces elements
			String label = ie.getLabel();
			String help = ie.getHelp();

			String[] searchIn = new String[] { label, help };

			// créer un objet comparable par coefficicent
			ComparableObject sr = CoeffComparator.getComparableFor(ie);
			for (int i = 0; i < searchIn.length; i++) {

				String currentSearch = searchIn[i];

				if (currentSearch == null) {
					continue;
				}

				for (int j = 0; j < patterns.length; j++) {

					Matcher m = patterns[j].matcher(currentSearch);

					while (m.find()) {
						sr.addToCoeff(points[i]);
					}
				}

			}

			results.add(sr);

		}

		// tri par ordre descendant
		CoeffComparator.sort(results, Order.DESCENDING);

		// recuperer le container
		JPanel ctr = getPopupContentPane();
		ctr.removeAll();

		// affichage des resultats les plus pertinents

		// compter les résultats affichés
		int displayedResults = 0;

		// additionner les hauteurs des elements pour affichage
		int height = 0;

		for (ComparableObject obj : results) {

			// verification du coeff
			if (obj.getCoeff() == 0) {
				break;
			}

			// recuperation des elements graphiques
			int index = ielements.indexOf(obj.getObject());
			Component comp = ieGUIs.get(index);

			// ajout du composant
			ctr.add(comp, "width " + (POPUP_WIDTH_PX - 10) + "px!, wrap");

			// prise en compte de la hauteur des composants
			height += comp.getPreferredSize().height;

			// quitter apres affichage max
			if (displayedResults > maxResultDisplayed)
				break;

			displayedResults++;
		}

		// adaptation de la hauteur du menu
		getPopup().proposePopupHeight(height);

		// aucun résultats
		if (displayedResults == 0) {
			GuiUtils.addLabel("Aucune commande ne correspond.", ctr, "wrap");

			// affichage du packet parent pour debogage
			if (MainManager.isDebugMode()) {
				GuiUtils.addLabel("Nom du package parent: "
						+ ConfigurationConstants.IE_PACKAGE_ROOT, ctr, "wrap");
			}
		}

		showPopup(true);

		// fin du travail
		ThreadAccessControl.get(1).releaseAccess();
	}

}
