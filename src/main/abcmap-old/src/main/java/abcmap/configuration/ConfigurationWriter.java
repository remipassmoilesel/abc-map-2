package abcmap.configuration;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import abcmap.managers.Log;
import abcmap.utils.PrintUtils;
import abcmap.utils.Utils;

public class ConfigurationWriter {

	public static void write(File path, Configuration conf, boolean overwrite) throws IOException {

		if (path.isFile() == true && overwrite == false)
			throw new IOException(path.getAbsolutePath());

		if (path.isFile() == false) {
			path.createNewFile();
		}

		Element root = new Element(ConfigurationConstants.XML_ROOT_NAME);
		Document xmlDoc = new Document(root);

		// Iterer les membres de la classe
		Field[] fields = conf.getClass().getFields();

		int skippedElements = 0;

		parsing: for (Field f : fields) {

			String name = f.getName();
			String val = null;

			try {

				// Couleur
				if (Color.class.equals(f.getType())) {
					val = Utils.colorToString((Color) f.get(conf));
				}

				// Tous les autres types
				else {
					val = f.get(conf) != null ? f.get(conf).toString() : "null";
				}

			}

			catch (IllegalArgumentException | IllegalAccessException e) {
				Log.error(e);
				skippedElements++;
				continue parsing;
			}

			// creation de l'element
			Element e = new Element(ConfigurationConstants.XML_PARAMETER_TAG);
			Attribute a = new Attribute("name", name);
			e.setAttribute(a);
			e.setText(val);

			root.addContent(e);

		}

		// signaler les erreurs
		if (skippedElements > 0) {
			PrintUtils.p("Ignored elements while saving configuration: " + skippedElements);
		}

		// ecrire le fichier
		try {
			XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
			out.output(xmlDoc, new FileOutputStream(path));
		} catch (Exception e) {
			Log.error(e);
			throw new IOException("Unable to write the configuration profile");
		}

	}

}
