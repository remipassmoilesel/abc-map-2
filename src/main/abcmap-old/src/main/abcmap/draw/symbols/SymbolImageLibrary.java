package abcmap.draw.symbols;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import abcmap.configuration.ConfigurationConstants;
import abcmap.managers.Log;

public class SymbolImageLibrary {

	private static SymbolImageLibrary library;

	/**
	 * Initilisation de la bilbiothèque de symboles
	 * 
	 * @throws IOException
	 */
	public static void init() throws IOException {
		if (library != null)
			throw new IllegalStateException("Initialisation error");

		library = new SymbolImageLibrary();
		library.loadSymbolSets();
	}

	public static ArrayList<Integer> getAvailablesCodesFor(String set) {
		testAndThrowInitException();

		int index = library.availablesSetNames.indexOf(set);
		return library.availablesSymbolCodes.get(index);
	}

	public static ArrayList<String> getAvailablesSets() {
		testAndThrowInitException();

		return new ArrayList<>(library.availablesSetNames);
	}

	public static Font getSetFont(String set) {
		testAndThrowInitException();

		int index = library.availablesSetNames.indexOf(set);
		return library.availablesSetFonts.get(index);
	}

	public static SymbolImage getImage(String set, int code, int size, Color color) {
		testAndThrowInitException();

		return library.searchAndReturnSymbol(set, code, size, color);
	}

	public static boolean isCodeValid(String set, int code) {
		return library.testSymbol(set, code);
	}

	/*
	 * 
	 * 
	 * 
	 */

	private ArrayList<String> availablesSetNames;
	private ArrayList<Font> availablesSetFonts;
	private ArrayList<ArrayList<Integer>> availablesSymbolCodes;

	private ArrayList<SymbolImage> availablesSymbolsImages;

	private SymbolImageLibrary() {
		availablesSetNames = new ArrayList<>(50);
		availablesSymbolCodes = new ArrayList<>(50);
		availablesSetFonts = new ArrayList<>(50);

		availablesSymbolsImages = new ArrayList<SymbolImage>(50);
	}

	private SymbolImage searchAndReturnSymbol(String set, int code, int size, Color color) {

		// vérifier si le symbole existe déjà
		// creer un symbol pour recherche
		SymbolImage si = new SymbolImage(set, code, size, color);

		// rechercher dans la liste
		int index = library.availablesSymbolsImages.indexOf(si);

		// le symbol existe, retour de l'image
		if (index != -1) {
			return library.availablesSymbolsImages.get(index);
		}

		// le symbole n'existe pas, creation puis retour
		else {

			try {
				// creation de l'image
				si.createImage();

				// enregistrement
				library.availablesSymbolsImages.add(si);

			} catch (Exception e) {
				Log.error(e);
			}

			// retour
			return si;

		}

	}

	private boolean testSymbol(String set, int code) {
		int index = availablesSetNames.indexOf(set);
		if (index == -1)
			return false;
		else
			return availablesSymbolCodes.get(index).contains(code);
	}

	/**
	 * Liste et charge les jeux de symbols disponibles
	 * 
	 * @return
	 * @throws IOException
	 */
	private void loadSymbolSets() throws IOException {

		// liste des set disponibles
		availablesSetFonts = new ArrayList<>();

		// la listedes codes disponibles par set
		availablesSymbolCodes = new ArrayList<>();

		// lister les fichiers
		File dir = new File(ConfigurationConstants.SYMBOLS_DIR_PATH);

		// le repertoire n'existe pas, arret
		if (dir.isDirectory() == false)
			throw new IOException();

		// iterer les fichiers
		String[] files = dir.list();
		for (String fileName : files) {

			// nom du set en fonction du nom du fichier
			int pt = fileName.lastIndexOf('.');

			// pas de point, ignorer l'element
			if (pt == -1)
				continue;

			String setName = fileName.substring(0, pt);
			String setPath = ConfigurationConstants.SYMBOLS_DIR_PATH + fileName;

			// charger la font et ajouter le set
			Font font;
			try {
				font = Font.createFont(Font.TRUETYPE_FONT, new File(setPath));
				availablesSetNames.add(setName);
				availablesSetFonts.add(font);
			} catch (FontFormatException | IOException e) {
				continue;
			}

			// tester les codes disponibles entre 23 et 255 pour éviter les
			// symboles non affichables
			ArrayList<Integer> codes = new ArrayList<Integer>(200);
			for (int i = 33; i < 255; i++) {
				if (font.canDisplay(i)) {
					String character = Character.toString((char) i);
					if (character.matches("^[\\p{ASCII}|\\p{Graph}]$") == true) {
						codes.add(i);
					}
				}
			}

			// ajouter la liste de symboles
			availablesSymbolCodes.add(codes);
		}

		// rien a recuperer dans le dossier
		if (availablesSetNames.size() < 1) {
			throw new IOException("No sets availables");
		}

	}

	private static void testAndThrowInitException() {
		if (library == null)
			throw new IllegalStateException("Initilisation error");
	}

}
