package abcmap.gui.wizards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

public class GeolocWizard extends Wizard {

	public GeolocWizard() {
		super();

		title = "Géolocalisation";
		setDescription("Cet assistant vous permet de géolocaliser une carte pour utiliser un système"
				+ "de coordonnées.");

		ArrayList<String> titles = new ArrayList<String>();
		ArrayList<List> elements = new ArrayList<List>();

		/*
		 * 
		 */
		titles.add("Introduction");
		elements.add(Arrays
				.asList(

				"Cet assistant permet de géolocaliser une carte pour utiliser un système de coordonnées.",

						"Pour géolocaliser une carte, indiquez les coordonnées précises de deux point et le "
								+ "logiciel calculera les coordonnées pour le reste de ",

						"Une fois configuré, Abc-Map utilisera votre poste de travail pour capturer"
								+ " la carte en ligne sous forme d'images, puis pour les recadrer et "
								+ "les assembler automatiquement."

				));

	}
}
