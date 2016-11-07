package abcmap.gui.wizards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;

import abcmap.gui.ie.draw.SelectLayer;
import abcmap.gui.ie.importation.SelectCropAreaForScreen;
import abcmap.gui.ie.importation.SelectPictureAnalyseMode;
import abcmap.gui.ie.importation.robot.LaunchRobotImport;
import abcmap.gui.ie.importation.robot.SelectCaptureArea;
import abcmap.gui.ie.importation.robot.SelectRobotImportOptions;
import abcmap.gui.ie.program.ShowAboutDialog;
import abcmap.gui.ie.program.ShowSupportProjectDialog;
import abcmap.gui.ie.project.NewProject;
import abcmap.gui.ie.project.OpenProject;
import abcmap.gui.ie.ressources.GoToOpenStreetMap;
import abcmap.gui.iegroup.docks.GroupImportation;

public class AutoScreenCaptureWizard extends Wizard {

	public AutoScreenCaptureWizard() {
		super();

		setTitle("Capturer une carte à partir d'un site de cartographie en ligne");
		setDescription("Cet assistant vous permet de créer une carte simple, étape par étape,"
				+ " à partir du site de cartographie OpenStreetMap");

		ArrayList<String> titles = new ArrayList<String>();
		ArrayList<List> elements = new ArrayList<List>();

		/*
		 * 
		 */
		titles.add("Introduction");
		elements.add(Arrays
				.asList(

				"Cet assistant permet d'importer une carte à partir d'un site de cartographie en ligne.",

						"Une fois configuré, Abc-Map utilisera votre poste de travail pour capturer"
								+ " la carte en ligne sous forme d'images, puis pour les recadrer et "
								+ "les assembler automatiquement."

				));
		/*
		 * 
		 */
		titles.add("Préparation du projet");
		elements.add(Arrays
				.asList("Ouvrez un projet existant ou créez un nouveau projet.",

						"Un projet est un conteneur d'informations dans lequel"
								+ " vous pouvez enregistrer votre travail dans un format modifiable par Abc-Map.",

						new NewProject(),

						new OpenProject()

				));

		/*
		 * 
		 */
		titles.add("Préparation de l'import");
		elements.add(Arrays
				.asList("Ouvrez votre navigateur web et rendez vous sur http://OpenStreetMap.org",

						new GoToOpenStreetMap(),

						"Rendez-vous à l'emplacement de votre choix (par exemple sur la ville de Grenoble),"
								+ " puis changez l'échelle (zoomez/dézoomez) puis agrandissez la carte interactive"
								+ " au maximum (mode plein écran).",

						"Ouvrez ensuite cet assistant dans une nouvelle fenêtre en cliquant sur"
								+ "le bouton 'Ouvrir dans une nouvelle fenêtre' situé juste au-dessus.",

						"Sélectionnez les outils d'import puis choisissez l'élément 'Capture automatique d'écran'.",

						createShowGroupString(GroupImportation.class,
								"Outils d'import")));

		/*
		 * 
		 */
		titles.add("Configuration de la taille d'import");
		elements.add(Arrays
				.asList(

				"Configurez la taille de l'import automatique en nombre de déplacement. Par exemple,"
						+ " indiquez une taille de 5 x 4 pour qu'Abc-Map déplace l'écran 5 fois en largeur"
						+ " et 4 fois en hauteur.",

				new SelectCaptureArea().getBlockGUI()

				));

		/*
		 * 
		 */
		titles.add("Configuration des options d'import");
		elements.add(Arrays
				.asList(

				"Configurez les options d'import automatique. Choisissez de commencer par le coin haut"
						+ " gauche de la zone à capturer ou choisissez de commencer à partir du"
						+ " centre de la zone.",

						"Configurez ensuite les options de vitesse et de couverture de la capture. Les paramètres"
								+ " par défaut conviennent dans la plupart des situations. Sachez cependant que"
								+ " plus les délais seront long et plus la capture sera lente, mais plus"
								+ " le site de cartographie aura de temps pour charger.",

						new SelectRobotImportOptions().getBlockGUI()

				));

		/*
		 * 
		 */
		titles.add("Choix du type d'analyse d'image");
		elements.add(Arrays.asList(

		"Choisissez le type d'analyse d'image. Plus l'analyse sera complète et plus"
				+ " l'assemblage sera lent, mais plus il sera précis.",

		new SelectPictureAnalyseMode().getBlockGUI()

		));

		/*
		 * 
		 */
		titles.add("Configuration du recadrage");
		elements.add(Arrays
				.asList(

				"Configurez les options de recadrage. Pour assembler la carte finale, Abc-Map utilisera"
						+ " votre navigateur web et capturera l'écran. Afin d'éliminer tout élement inutile"
						+ " sélectionnez la zone de l'écran à recadrer.",

				new SelectCropAreaForScreen().getBlockGUI()

				));

		/*
		 * 
		 */
		titles.add("Choix du calque de destination");
		elements.add(Arrays
				.asList(

				"Choisissez le calque de destination d'import. Il peut être judicieux de consacrer un calque"
						+ " par type d'information: photographie aériennes, étiquettes de texte, légende, ... "
						+ " Cela permet de structurer un projet et de faciliter les modifications.",

				new SelectLayer().getBlockGUI()

				));

		/*
		 * 
		 */
		titles.add("Lancement de l'import");
		elements.add(Arrays
				.asList(

				"Il ne reste plus qu'à lancer l'import automatique. Vérifiez que la fenêtre du logiciel"
						+ " Abc-Map soit bien juste au-dessus de la fenêtre du navigateur puis cliquez sur le bouton"
						+ " 'Lancer l'import'.",

				"Vous pouvez interrompre l'import à tout moment en cliquant sur le bouton"
						+ " 'Arrêter l'import'.",

				new LaunchRobotImport().getBlockGUI()

				));

		addNewSteps(titles, elements);

	}
}
