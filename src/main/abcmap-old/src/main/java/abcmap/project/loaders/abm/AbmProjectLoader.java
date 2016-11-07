package abcmap.project.loaders.abm;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Iterator;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import abcmap.configuration.ConfigurationConstants;
import abcmap.draw.basicshapes.LayerElement;
import abcmap.exceptions.MapLayerException;
import abcmap.exceptions.ProjectException;
import abcmap.geo.Coordinate;
import abcmap.managers.Log;
import abcmap.project.Project;
import abcmap.project.ProjectMetadatas;
import abcmap.project.layers.MapLayer;
import abcmap.project.layouts.LayoutPaper;
import abcmap.project.loaders.AbmConstants;
import abcmap.project.loaders.AbstractProjectLoader;
import abcmap.project.properties.PropertiesContainer;
import abcmap.utils.Utils;
import abcmap.utils.ZipDirectory;

public class AbmProjectLoader extends AbstractProjectLoader {

	private Project project;

	@Override
	public void verify(File pathToImport) throws IOException {

		// verifier le chemin
		if (pathToImport == null) {
			throw new IOException("Path null");
		}

		if (pathToImport.isFile() == false) {
			throw new IOException("File does not exist: "
					+ pathToImport.getAbsolutePath());
		}

		// verifier l'extension
		if (Utils.checkExtension(pathToImport.getAbsolutePath(),
				ConfigurationConstants.PROJECT_EXTENSION) == false) {
			throw new ProjectException(ProjectException.INVALID_PROJECT_PATH);
		}
	}

	@Override
	public void load(File file, Project project) throws IOException {

		this.project = project;

		// verifications
		verify(file);

		// dezipper les fichiers
		ZipDirectory zp = new ZipDirectory();
		File tmp = project.getTempDirectoryFile();
		zp.unzipArchive(file, tmp);

		// charger le descripteur
		loadDescriptor();

		// chemin du projet
		project.setRealPath(file);

		// selectionner le calque actif
		try {
			project.setActiveLayer(0);
		} catch (MapLayerException e) {
			Log.debug(e);
		}

	}

	private void loadDescriptor() throws IOException {

		Document xmlDescriptor;
		SAXBuilder sxb = new SAXBuilder();

		// desactivation des notifications du projet
		project.setNotificationsEnabled(false);

		String path = project.getTempDirectoryFile() + File.separator
				+ ConfigurationConstants.DESCRIPTOR_NAME;

		// parser le fichier
		try {
			xmlDescriptor = sxb.build(path);
		} catch (Exception e) {
			throw new IOException("XML error while reading file: " + path);
		}

		// recover la balise racine
		Element root = (Element) xmlDescriptor.getRootElement();
		Iterator<Element> it = root.getChildren().iterator();

		// introspection des metadas
		Class<? extends ProjectMetadatas> metaClass = ProjectMetadatas.class;
		ProjectMetadatas mtd = new ProjectMetadatas();

		// iterer les enfants de racine
		while (it.hasNext()) {

			// recuperer l'element a iterer
			Element c = it.next();

			// Cas n°1: Balise de calque
			if (AbmConstants.PROJECT_LAYER_TAG_NAME.equalsIgnoreCase(c
					.getName())) {

				// creer un calque
				MapLayer layer = project.addNewLayer();
				PropertiesContainer pp = AbmShapesLoader
						.getLayerPropertiesFrom(c);

				// continuer quand meme si erreur
				if (pp != null)
					layer.setProperties(pp);

				// desactiver les notifications pour ajouter les objet de
				// manière plus "legère"
				layer.setNotificationsEnabled(false);

				// iterer les enfants de la balise de calque
				Iterator<Element> itC = c.getChildren().iterator();
				while (itC.hasNext()) {

					// recuperer l'element a anlyser
					Element e = itC.next();

					// mettre en forme le nom de la classe de l'element a creer
					// 'Nomdeclasse'
					String name = e.getName().substring(0, 1).toUpperCase()
							+ e.getName().substring(1).toLowerCase();

					LayerElement elmt = null;
					try {

						// deserialiser les proprietes de l'element
						PropertiesContainer ppE = AbmShapesLoader
								.getElementPropertiesFrom(e);

						// creation d'une instance de l'objet
						Class<?> cl = Class
								.forName(ConfigurationConstants.DRAW_PACKAGE
										+ "." + name);
						elmt = (LayerElement) cl.newInstance();

						// affetation des proprietes
						elmt.setProperties(ppE);
					}

					catch (Exception e2) {
						Log.debug(e2);
						addMinorException(e2);
					}

					// ajout de l'element si pas d'erreur
					if (elmt != null) {
						elmt.refreshShape();
						layer.addElement(elmt, false);
					}
				}

				// activer les notifications
				layer.setNotificationsEnabled(true);

			}

			/*
			 * cas 2 : feuilles de mise en page
			 */
			else if (AbmConstants.PROJECT_LAYOUT_TAG_NAME.equalsIgnoreCase(c
					.getName())) {
				LayoutPaper layout = project.addNewLayout();
				layout.setProperties(AbmLayoutsLoader
						.constructPropertiesForLayout(c));
			}

			/*
			 * Cas 3 : references de geolocalisation
			 */

			else if (Coordinate.class.getSimpleName().equalsIgnoreCase(
					c.getName())) {
				Coordinate m = new Coordinate();
				PropertiesContainer pp = AbmShapesLoader
						.getCoordinatePropertiesFrom(c);
				if (pp != null) {
					m.setProperties(pp);
					project.addGeoreference(m);
				}

			}

			/*
			 * Tous les autres elements sont stockes dans les metadonnees
			 * 
			 * ATTENTION: IllegalArgumentException si autre methode
			 * d'affectation
			 */
			else {
				Field f = null;
				try {
					f = metaClass.getDeclaredField(c.getName().toUpperCase());

					// test de type
					Object test = f.get(mtd);

					if (test instanceof Dimension) {
						f.set(mtd, Utils.stringToDimension(c.getText()));
					}

					else if (test instanceof Color) {
						f.set(mtd, Utils.stringToColor(c.getText()));
					}

					else if (test instanceof String) {
						f.set(mtd, new String(c.getText()));
					}

					else if (test instanceof Float) {
						f.set(mtd, new Float(c.getText()));
					}

					else if (test instanceof Double) {
						f.set(mtd, new Double(c.getText()));
					}

					else if (test instanceof Integer) {
						f.set(mtd, new Integer(c.getText()));
					}

					else if (test instanceof Boolean) {
						f.set(mtd, new Boolean(c.getText()));
					}

					else {
						throw new IllegalArgumentException("Illegal type: "
								+ f.getType().getName());
					}

				} catch (IllegalArgumentException | IllegalAccessException
						| NoSuchFieldException e) {
					Log.debug(e);
					addMinorException(e);
				}

			}
		}

		// affecter les metadonnées a la fin
		project.setMetadatas(mtd);

		// re activation des notifications du projet
		project.setNotificationsEnabled(true);

	}

}
