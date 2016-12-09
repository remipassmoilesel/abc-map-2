package org.abcmap.gui.wizards;

import org.abcmap.gui.toProcess.gui.ie.program.ShowAboutDialog;
import org.abcmap.gui.toProcess.gui.ie.program.ShowSupportProjectDialog;
import org.abcmap.gui.toProcess.gui.iegroup.docks.GroupDrawingTools;
import org.abcmap.gui.toProcess.gui.iegroup.docks.GroupGeoreferencement;
import org.abcmap.gui.toProcess.gui.iegroup.docks.GroupImportation;
import org.abcmap.gui.toProcess.gui.iegroup.docks.GroupLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PresentationWizard extends Wizard {

	public PresentationWizard() {
		super();

		setTitle("Présentation en 5 étapes");
		setDescription("Découvrez les possibilités du logiciel et ses principaux concepts.");

		ArrayList<String> titles = new ArrayList<String>();
		ArrayList<List> elements = new ArrayList<List>();

		// etape
		titles.add("Créez des cartes <i>pratiques</i>");
		elements.add(Arrays
				.asList("Alors que d'excellents logiciels comme QGis ou GvSIG proposent des fonctionnalités"
						+ " avancées destinées à des projets complexes, Abc-Map propose une méthode de création de carte"
						+ " rapide et simple."
						+ "<br/><br/>"
						+ "Abc-Map est un projet bénévole, soutenez ce projet en diffusant son esprit "
						+ "autour de vous !",

				new ShowSupportProjectDialog(),

				new ShowAboutDialog()

				));

		// etape
		titles.add("Importez des cartes à partir de sources variées");
		elements.add(Arrays
				.asList(

				"Utilisez des cartes existantes et ajoutez des informations. Importez à partir "
						+ "de sites de cartographie en ligne ou à partir d'images locales.",

						createShowGroupString(GroupImportation.class,
								"Outils d'import de carte")

				));

		// etape
		titles.add("Tracez des formes et ajoutez des informations");
		elements.add(Arrays.asList(

		"Utilisez les outils de dessin vectoriel pour ajouter des formes simples et "
				+ "des étiquettes de texte pour compléter vos cartes.",

		createShowGroupString(GroupDrawingTools.class, "Outils de traçage")

		));

		// etape
		titles.add("Géoréférencez et importez des données");
		elements.add(Arrays
				.asList(

				"Utilisez deux points sur votre carte pour la géoréférencer et appliquer une grille de"
						+ " coordonnées géographiques."
						+ "<br/><br/>"
						+ "Une fois votre carte géoréférencée, importez des informations géographiques:"
						+ " formats GPX, CSV, Excel, ...",

						createShowGroupString(GroupGeoreferencement.class,
								"Outils de géo-référencement"),

						createShowGroupString(GroupImportation.class,
								"Outils d'import")

				));

		// etape
		titles.add("Mettez en page puis imprimez ou exportez");
		elements.add(Arrays.asList(

		"Mettez en page votre projet sur plusieurs feuilles,"
				+ " de formats standards ou personnalisés. " + "<br/><br/>"
				+ "Exportez votre travail au format PNG, HTML, GPX, ...",

		createShowGroupString(GroupLayout.class, "Outils de mise en page")

		));

		// ajout des étapes
		addNewSteps(titles, elements);

	}
}
