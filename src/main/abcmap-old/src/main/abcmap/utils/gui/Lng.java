package abcmap.utils.gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe d'internationalisation.
 * <p>
 * Exemple: label = Lng.get("english keywords"); <br>
 * help = Lng.get("new configuration profile help");
 * 
 * @author remipassmoilesel
 *
 */
public class Lng {

	public static final String LANG_BASE_PATH = "./lang/lang_";
	public static final String ELEMENT_PATTERN = "^>>([^=]+)=$";

	public enum Langage {
		FRENCH("fr"), ENGLISH("en"), SPANNISH("es");
		private String prefix;

		private Langage(String p) {
			prefix = p;
		}

		public String getPrefix() {
			return prefix;
		}
	}

	public static HashMap<String, String> sentences;

	public static String get(String id) {
		String r = sentences.get(id.toLowerCase());

		if (r == null) {
			throw new NullPointerException("Cannot find: '" + id + "'");
		}

		return r;
	}

	public static void loadLanguage(Langage lang) throws IOException {

		// chargement du fichier
		File f = new File(LANG_BASE_PATH + lang.getPrefix() + ".txt");
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(f));
		} catch (FileNotFoundException e) {
			throw new IOException(e);
		}

		// lecture
		String l;
		Pattern begin = Pattern.compile(ELEMENT_PATTERN);
		Pattern empty = Pattern.compile("\\w+");
		HashMap<String, String> stncs = new HashMap<String, String>();
		String id = "";
		while ((l = br.readLine()) != null) {

			// ne pas traiter les lignes vides
			if (empty.matcher(l).find() == false)
				continue;

			// recherche du début de l'element
			Matcher m = begin.matcher(l);
			if (m.matches()) {
				id = m.group(1).trim().toLowerCase();
			}

			// ajout a l'element courant
			else {
				String existing = stncs.get(id);
				String newStr = existing == null ? l : existing + l;
				stncs.put(id, newStr);
			}
		}

		// fermeture ressource
		br.close();

		// remplacement phrases
		sentences = stncs;

	}

	public static HashMap<String, String> getSentences() {
		return sentences;
	}
}
