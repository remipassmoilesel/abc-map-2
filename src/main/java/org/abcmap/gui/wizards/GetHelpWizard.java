package org.abcmap.gui.wizards;

import org.abcmap.ielements.ressources.GoToHelpPage;
import org.abcmap.gui.iegroup.docks.GroupWizard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GetHelpWizard extends Wizard {

	public GetHelpWizard() {
		super();

		setTitle("Ou trouver de l'aide ?");
		setDescription("Découvrez tous les moyens à votre disposition pour apprendre simplement"
				+ " à vous servir de ce logiciel.");

		ArrayList<String> titles = new ArrayList<String>();
		ArrayList<List> elements = new ArrayList<List>();

		// etape
		titles.add("Utilisez les assistants dynamiques");
		elements.add(Arrays
				.asList("Les assistants dynamiques permettent de travailler avec Abc-Map sur des exemples concrets,"
						+ "étape par étape, et vous montrent les principales fonctionnalités du logiciel.",

						createShowGroupString(GroupWizard.class,
								"Assistants dynamiques")

				));

		// etape
		titles.add("Vidéos tutoriels");
		elements.add(Arrays.asList(
				"Les vidéos disponibles en lignes vous montrent étape par étape "
						+ " les différentes fonctionnalités du logiciel.",

				new GoToHelpPage()

		));

		// etape
		titles.add("Aide contextuelle");
		elements.add(Arrays
				.asList("A proximité de chaque commande se trouve une aide succincte. Par exemple, dans les docks latéraux,"
						+ " chaque commande est détaillé lorsque vous cliquez sur le point "
						+ "d'interrogation en haut à droite de la commande."

				));

		// ajout des étapes
		addNewSteps(titles, elements);

	}
}
