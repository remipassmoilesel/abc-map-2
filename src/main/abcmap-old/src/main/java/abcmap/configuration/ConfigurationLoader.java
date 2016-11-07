package abcmap.configuration;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import abcmap.managers.stub.MainManager;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import abcmap.importation.robot.RobotCaptureMode;
import abcmap.managers.Log;
import abcmap.utils.PrintUtils;
import abcmap.utils.Utils;

/**
 * Chargeur de configuration
 * 
 * @author remipassmoilesel
 *
 */
public class ConfigurationLoader {

	/**
	 * Charge une configuration dans l'objet 'conf' à partir du fichier
	 * 'profile'.
	 * 
	 * @param conf
	 * @param profile
	 * @return
	 * @throws IOException
	 */
	public static Configuration load(Configuration conf, File profile) throws IOException {

		// creer une conf vierge
		if (conf == null)
			conf = new Configuration();

		// Ouvrir puis tester le fichier
		if (profile.isFile() == false) {
			throw new IOException("File not exist: " + profile.getAbsolutePath());
		}

		SAXBuilder sxb = new SAXBuilder();
		Document doc = null;

		// parser le document de profil
		try {
			doc = sxb.build(profile);
		} catch (Exception e) {
			Log.error(e);
			throw new IOException("Unable to parse file: " + profile.getAbsolutePath());
		}

		// ajouter tous les parametres du fichier
		List<Element> elements = doc.getRootElement()
				.getChildren(ConfigurationConstants.XML_PARAMETER_TAG);
		Iterator<Element> i = elements.iterator();

		String paramName;
		String value;

		ArrayList<String> skippedElmts = new ArrayList<String>(10);

		while (i.hasNext()) {

			// recuperer nom et valeur du parametre
			Element elmt = i.next();

			try {
				paramName = elmt.getAttribute(ConfigurationConstants.XML_PARAMETER_ATTRIBUTE_NAME)
						.getValue();
				value = elmt.getText();
			} catch (Exception e) {
				Log.error(e);
				continue;
			}

			// recuperer argument de l'objet config et affecter la valeur
			Class<? extends Configuration> configClass = conf.getClass();

			try {
				Field field = configClass.getField(paramName);
				Class<?> cl = field.getType();

				if (String.class.equals(cl)) {
					field.set(conf, value);
				}

				else if (Color.class.equals(cl)) {
					field.set(conf, Utils.stringToColor(value));
				}

				else if (Integer.class.equals(cl)) {
					field.set(conf, Integer.parseInt(value));
				}

				else if (Float.class.equals(cl)) {
					field.set(conf, Float.parseFloat(value));
				}

				else if (Double.class.equals(cl)) {
					field.set(conf, Double.parseDouble(value));
				}

				else if (Boolean.class.equals(cl)) {
					field.set(conf, Boolean.parseBoolean(value));
				}

				else {
					field.set(conf, value);
				}

			} catch (Exception e) {
				skippedElmts.add(paramName + " : " + value + " - ");
				Log.error(e);
			}
		}

		if (skippedElmts.size() > 0 && MainManager.isDebugMode()) {
			PrintUtils.p("Ignored elements while loading configuration: " + skippedElmts.size());
			PrintUtils.p(skippedElmts);
		}

		// mode surf
		if (conf.SURF_MODE < 0 || conf.SURF_MODE > ConfigurationConstants.SURF_PARAMS.length - 1) {
			conf.SURF_MODE = 0;
		}

		// langue
		if (Arrays.asList(ConfigurationConstants.LANGUAGES).contains(conf.LANGUAGE) == false) {
			conf.LANGUAGE = ConfigurationConstants.FRENCH;
		}

		// mode import
		if (conf.ROBOT_IMPORT_MODE.equals(RobotCaptureMode.START_FROM_ULC) == false
				&& conf.ROBOT_IMPORT_MODE.equals(RobotCaptureMode.START_FROM_MIDDLE) == false) {
			conf.ROBOT_IMPORT_MODE = RobotCaptureMode.safeValueOf(conf.ROBOT_IMPORT_MODE).toString();
		}

		return conf;
	}

}
