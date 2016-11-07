package abcmap.utils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataFormatImpl;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.Icon;
import javax.swing.KeyStroke;

import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import abcmap.managers.Log;
import abcmap.utils.gui.GuiUtils;

public class Utils {

	/** Objet de mesure de mémoire */
	private static Instrumentation instrumentation;

	/** Séparateur pour conversion de données en chaines */
	private static final String SEPARATOR = ",";

	/** Objet de valeurs aléatoire */
	private static Random rand = new Random();

	/** Algorithme de hashage par defaut */
	private static final String DEFAULT_HASH_ALGO = "SHA-256";

	/** Objet de hashage */
	private static MessageDigest hasher = getHasherInstance();

	public static final String WINDOWS = "windows";
	public static final String MAC = "mac";
	public static final String LINUX = "linux";

	/**
	 * Retourne nom de l'OS ou null.
	 * 
	 * @return
	 */
	public static String getOsName() {
		String[] osArray = new String[] { WINDOWS, MAC, LINUX };
		String os = System.getProperty("os.name").toLowerCase();
		for (String o : osArray) {
			if (os.indexOf(o) >= 0) {
				return o;
			}
		}
		return null;
	}

	/**
	 * Méthode utilitaire permettant d'organiser les surcharges de méthodes
	 * toString. Retourne une chaine contenant la classe et les champs passés en
	 * argument.
	 * 
	 * @param obj
	 * @param class1
	 * @param keys
	 * @param values
	 * @return
	 */
	public static String toString(Object obj, Object[] keys, Object[] values) {

		if (keys.length != values.length) {
			throw new IllegalArgumentException("Keys and values table must"
					+ "have same length: key: " + keys.length + ", values:"
					+ values.length);
		}

		StringBuilder builder = new StringBuilder(100);
		builder.append(obj.getClass().getSimpleName() + " " + obj.hashCode()
				+ "[ ");

		for (int i = 0; i < keys.length; i++) {
			Object k = keys[i];
			Object v = values[i];
			builder.append(k + ": '" + v + "', ");
		}

		builder.append("]");

		return builder.toString();
	}

	public static Map<String, Integer> sortByValue(
			Map<String, Integer> unsortMap) {

		// Convert Map to List
		List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(
				unsortMap.entrySet());

		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1,
					Map.Entry<String, Integer> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		// Convert sorted map back to a Map
		Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it
				.hasNext();) {
			Map.Entry<String, Integer> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}

	public static String jdomToString(Element element) {
		XMLOutputter xml = new XMLOutputter(Format.getPrettyFormat());
		return xml.outputString(element);
	}

	/**
	 * Compare sans NullPointerException possible.
	 * <p>
	 * Si les deux références pointent vers le même objets, renvoie vrai,
	 * <p>
	 * Si les deux références sont nulles, renvoi vrai,
	 * <p>
	 * Si les deux objets sont égaux, renvoi vrai.
	 * 
	 * @param obj1
	 * @param obj2
	 * @return
	 */
	public static Boolean safeEquals(Object obj1, Object obj2) {

		if (obj1 == obj2) {
			return true;
		}

		if (obj1 == null || obj2 == null) {
			return false;
		}

		return obj1.equals(obj2);

	}

	public static boolean safeEqualsIgnoreCase(String obj1, String obj2) {

		if (obj1 == obj2) {
			return true;
		}

		if (obj1 == null || obj2 == null)
			return false;

		return obj1.equalsIgnoreCase(obj2);

	}

	/**
	 * Compare sans NullPointerException possible.
	 * <p>
	 * Si les deux références pointent vers le même objets, renvoie vrai,
	 * <p>
	 * Si les deux références sont nulles, renvoi vrai,
	 * <p>
	 * Si les deux objets sont égaux, renvoi vrai.
	 * 
	 * @param obj1
	 * @param obj2
	 * @return
	 */
	public static boolean safeEquals(Collection<Object> c1,
			Collection<Object> c2) {

		if (c1 == c2) {
			return true;
		}

		if (c1 == null || c2 == null) {
			return false;
		}

		// vérifier la taille des collections
		if (c1.size() != c2.size())
			return false;

		// verifier l'égalité des points
		Iterator<Object> it1 = c1.iterator();
		Iterator<Object> it2 = c2.iterator();

		while (it1.hasNext()) {

			Object o1 = it1.next();
			Object o2 = it2.next();

			if (safeEquals(o1, o2) == false) {
				return false;
			}
		}

		return true;

	}

	public static String join(String sep, Object[] list) {
		return join(sep, Arrays.asList(list));
	}

	public static String join(String sep, List list) {

		String result = new String();

		// iterer la liste d'objets
		Iterator iterator = list.iterator();
		while (iterator.hasNext()) {

			// recuperer l'objet
			Object object = iterator.next();

			// ajouter au resultat final
			result += object.toString();

			// ajouter le separateur si necessaire
			if (iterator.hasNext()) {
				result += sep;
			}

		}

		return result;

	}

	public static String colorToString(Color c) {

		if (c == null) {
			return "null";
		}

		return join(SEPARATOR,
				new Integer[] { c.getRed(), c.getGreen(), c.getBlue() });

	}

	public static Color stringToColor(String s) {

		if (s.equalsIgnoreCase("null")) {
			return null;
		}

		// parser la chaine
		Integer[] vals = stringToIntArray(s);

		// verifier la longueur
		if (vals.length != 3) {
			throw new IllegalArgumentException("Chaine incorrecte: " + s);
		}

		return new Color(Integer.valueOf(vals[0]), Integer.valueOf(vals[1]),
				Integer.valueOf(vals[2]));
	}

	public static String dimensionToString(Dimension dim) {

		if (dim == null) {
			return "null";
		}

		return join(SEPARATOR, new Integer[] { dim.width, dim.height });
	}

	public static Dimension stringToDimension(String s) {

		if (s.equalsIgnoreCase("null")) {
			return null;
		}

		// parser la chaine
		Integer[] vals = stringToIntArray(s);

		// verifier la longueur
		if (vals.length != 2) {
			throw new IllegalArgumentException("Chaine incorrecte: " + s);
		}

		return new Dimension(Integer.valueOf(vals[0]), Integer.valueOf(vals[1]));
	}

	public static String pointToString(Point pt) {
		if (pt == null) {
			return "null";
		}

		return join(SEPARATOR, new Integer[] { pt.x, pt.y });
	}

	public static Point stringToPoint(String s) {

		if (s.equalsIgnoreCase("null")) {
			return null;
		}

		// parser la chaine
		Integer[] vals = stringToIntArray(s);

		// verifier la longueur
		if (vals.length != 2) {
			throw new IllegalArgumentException("Chaine incorrecte: " + s);
		}

		return new Point(Integer.valueOf(vals[0]), Integer.valueOf(vals[1]));
	}

	public static double round(double a, int n) {
		double p = Math.pow(10.0d, n);
		return Math.floor((a * p) + 0.5d) / p;
	}

	public static float round(float a, int n) {
		double p = Math.pow(10.0f, n);
		return (float) (Math.floor((a * p) + 0.5f) / p);
	}

	public static boolean checkExtension(String name, String extension) {
		String ext = getExtension(name);
		return ext.equalsIgnoreCase(extension);
	}

	/**
	 * Retourne l'extension d'un fichier ou une chaine vide
	 * 
	 * @param name
	 * @return
	 */
	public static String getExtension(String name) {

		int pt = name.lastIndexOf('.');

		if (pt == -1) {
			return "";
		} else {
			return name.substring(pt + 1, name.length()).toLowerCase().trim();
		}
	}

	public static String getExtension(File file) {
		return getExtension(file.getName());
	}

	public static int randInt(int min, int max) {
		int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}

	/**
	 * Supprime un dossier recursivement
	 * 
	 * @param file
	 * @throws IOException
	 */
	public static void deleteRecursively(File file) throws IOException {
		if (file.isDirectory()) {
			if (file.list().length == 0) {
				file.delete();
			} else {
				String files[] = file.list();
				for (String temp : files) {
					File fileDelete = new File(file, temp);
					deleteRecursively(fileDelete);
				}
				if (file.list().length == 0) {
					file.delete();
				}
			}
		} else {
			file.delete();
		}
	}

	/**
	 * Ecrire une image en utilisant un writer "jpeg" avec une qualité maximale.
	 * 
	 * @param image
	 * @param file
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void writeImage(BufferedImage image, File file)
			throws FileNotFoundException, IOException {

		// création du writer
		Iterator iter = ImageIO.getImageWritersByFormatName("jpeg");
		ImageWriter writer = (ImageWriter) iter.next();
		ImageWriteParam iwp = writer.getDefaultWriteParam();
		iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);

		// qualité de l'image au max
		iwp.setCompressionQuality(1f);

		// ouverture du flux
		FileImageOutputStream output = null;

		// ecriture
		try {
			output = new FileImageOutputStream(file);
			writer.setOutput(output);
			IIOImage iioi = new IIOImage(image, null, null);
			writer.write(null, iioi, iwp);
		}

		finally {
			if (writer != null) {
				writer.dispose();
			}
			if (output != null) {
				output.close();
			}
		}

	}

	/**
	 * Conversion d'image vers Byte
	 * 
	 * @param img
	 * @return
	 */
	public static byte[] imageToByte(BufferedImage img) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] bytes = null;
		try {
			ImageIO.write(img, "jpg", baos);
			baos.flush();
			bytes = baos.toByteArray();
		}

		catch (IOException e) {
			Log.error(e);
		}

		finally {
			try {
				baos.close();
			} catch (IOException e) {
				Log.error(e);
			}
		}
		return bytes;
	}

	public static BufferedImage byteToImage(byte[] bytes) {

		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);

		BufferedImage img = null;
		try {
			img = ImageIO.read(bais);
		}

		catch (IOException e) {
			Log.error(e);
		}

		finally {
			try {
				bais.close();
			} catch (IOException e) {
				Log.error(e);
			}
		}

		return img;
	}

	public static void premain(String args, Instrumentation inst) {
		instrumentation = inst;
	}

	public static long getObjectSize(Object o) {
		return instrumentation.getObjectSize(o);
	}

	public static IIOMetadataNode getMetadatasForResolution(double resolutionDPI) {

		String sizePix = Double.toString(resolutionDPI / 25.4d);

		IIOMetadataNode vertical = new IIOMetadataNode("VerticalPixelSize");
		vertical.setAttribute("value", sizePix);

		IIOMetadataNode horizontal = new IIOMetadataNode("HorizontalPixelSize");
		horizontal.setAttribute("value", sizePix);

		IIOMetadataNode dimension = new IIOMetadataNode("Dimension");
		dimension.appendChild(vertical);
		dimension.appendChild(horizontal);

		IIOMetadataNode rslt = new IIOMetadataNode(
				IIOMetadataFormatImpl.standardMetadataFormatName);
		rslt.appendChild(dimension);

		return rslt;
	}

	public static void writePngImage(File output, BufferedImage img,
			IIOMetadataNode metas) throws IOException {

		ImageTypeSpecifier imageType = ImageTypeSpecifier
				.createFromBufferedImageType(img.getType());

		ImageOutputStream stream = null;
		try {
			Iterator<ImageWriter> writers = ImageIO
					.getImageWritersBySuffix("png");

			while (writers.hasNext()) {
				ImageWriter writer = writers.next();
				ImageWriteParam writeParam = writer.getDefaultWriteParam();
				IIOMetadata defaultDatas = writer.getDefaultImageMetadata(
						imageType, writeParam);
				if (!defaultDatas.isStandardMetadataFormatSupported()) {
					continue;
				}
				if (defaultDatas.isReadOnly()) {
					continue;
				}

				defaultDatas
						.mergeTree(
								IIOMetadataFormatImpl.standardMetadataFormatName,
								metas);

				IIOImage imageWithMetadata = new IIOImage(img, null,
						defaultDatas);

				stream = ImageIO.createImageOutputStream(output);
				writer.setOutput(stream);
				writer.write(null, imageWithMetadata, writeParam);
			}
		}

		finally {
			if (stream != null) {
				stream.close();
			}
		}
	}

	/**
	 * 
	 * @param unsortMap
	 * @return
	 */
	public static Map<String, Integer> sortByComparator(
			Map<String, Integer> unsortMap) {

		return sortByComparator(unsortMap, true);
	}

	/**
	 * Trier une map string / integer
	 * 
	 * @param unsortMap
	 * @param croissant
	 * @return
	 */
	public static Map<String, Integer> sortByComparator(
			Map<String, Integer> unsortMap, final boolean croissant) {

		// Convert Map to List
		List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(
				unsortMap.entrySet());

		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1,
					Map.Entry<String, Integer> o2) {
				if (croissant) {
					return (o1.getValue()).compareTo(o2.getValue());
				} else {
					return -(o1.getValue()).compareTo(o2.getValue());
				}
			}
		});

		// Convert sorted map back to a Map
		Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it
				.hasNext();) {
			Map.Entry<String, Integer> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}

	/**
	 * Obtenir une représentation "lisible" d'une keystroke.
	 * 
	 * @param ks
	 * @return
	 */
	public static String keystrokeToString(KeyStroke ks) {

		StringBuilder buf = new StringBuilder();

		// modificateur
		int modifiers = ks.getModifiers();
		if ((modifiers & InputEvent.SHIFT_DOWN_MASK) != 0) {
			buf.append("Shift");
		}
		if ((modifiers & InputEvent.CTRL_DOWN_MASK) != 0) {
			buf.append("Ctrl");
		}
		if ((modifiers & InputEvent.META_DOWN_MASK) != 0) {
			buf.append("Meta");
		}
		if ((modifiers & InputEvent.ALT_DOWN_MASK) != 0) {
			buf.append("Alt");
		}
		if ((modifiers & InputEvent.ALT_GRAPH_DOWN_MASK) != 0) {
			buf.append("Alt Gr");
		}
		if ((modifiers & InputEvent.BUTTON1_DOWN_MASK) != 0) {
			buf.append("B1");
		}
		if ((modifiers & InputEvent.BUTTON2_DOWN_MASK) != 0) {
			buf.append("B2");
		}
		if ((modifiers & InputEvent.BUTTON3_DOWN_MASK) != 0) {
			buf.append("B3");
		}

		// touche
		buf.append(" + ");
		buf.append(String.valueOf((char) ks.getKeyCode()));

		return buf.toString();
	}

	/**
	 * Obtenir la date courante sous forme de chaine.<br>
	 * Format: yyyy-MM-dd_HH-mm-ss_S<br>
	 * 
	 */
	public static String getDate() {
		return getDate("yyyy-MM-dd_HH-mm-ss_S");
	}

	/**
	 * Obtenir la date courante sous forme de chaine.<br>
	 * Code courants:<br>
	 * yyyy/MM/dd<br>
	 * HH:mm:ss<br>
	 * S (millisecondes)
	 * 
	 * @param date
	 */
	public static String getDate(String format) {
		DateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(new Date());
	}

	/**
	 * Enlever les bordure transparentes d'une image
	 * 
	 * @param image
	 * @return
	 */
	public static BufferedImage removeTransparentBorders(BufferedImage image) {

		// taille actuelle de l'image
		int width = image.getWidth();
		int height = image.getHeight();

		if (width == 0 || height == 0)
			throw new IllegalArgumentException("Dimensions too shorts: w: "
					+ width + " h: " + height);

		// nouvelles dimensions
		Rectangle newBounds = new Rectangle();
		newBounds.width = width;
		newBounds.height = height;

		// chercher les pixels significatifs à partir du haut
		for (int y = 0; y < height; y++) {
			int x = 0;
			for (; x < width; x++) {
				int pixel = image.getRGB(x, y);
				if (pixel != 0) {
					break;
				}
			}

			// la ligne est transparente
			if (x >= width) {
				newBounds.y++;
			}

			// la ligne n'est pas transparente
			else {
				break;
			}
		}

		// chercher les pixels significatifs à partir de la droite
		for (int x = width - 1; x >= 0; x--) {
			int y = 0;
			for (; y < height - 1; y++) {
				int pixel = image.getRGB(x, y);
				if (pixel != 0) {
					break;
				}
			}

			// la ligne est transparente
			if (y >= height - 1) {
				newBounds.width--;
			}

			// la ligne n'est pas transparente
			else {
				break;
			}
		}

		// chercher les pixels significatifs à partir du bas
		for (int y = height - 1; y >= 0; y--) {
			int x = 0;
			for (; x < width; x++) {
				int pixel = image.getRGB(x, y);
				if (pixel != 0) {
					break;
				}
			}

			// la ligne est transparente
			if (x >= width) {
				newBounds.height--;
			}

			// la ligne n'est pas transparente
			else {
				break;
			}
		}

		// chercher les pixels significatifs à partir de la gauche
		for (int x = 0; x < width; x++) {
			int y = 0;
			for (; y < height; y++) {
				int pixel = image.getRGB(x, y);
				if (pixel != 0) {
					break;
				}
			}

			// la ligne est transparente
			if (y >= height) {
				newBounds.x++;
			}

			// la ligne n'est pas transparente
			else {
				break;
			}
		}

		// corriger les dimensions
		newBounds.width -= newBounds.x;
		newBounds.height -= newBounds.y;

		// verifier les dimensions
		int diffx = width - newBounds.x + newBounds.width;
		int diffy = height - newBounds.y + newBounds.height;

		// les dimensions sont incorrecte ou le recadrage est null
		if (diffx <= 0 || diffy <= 0) {
			return image;
		}

		// recadrer et retourner
		else {
			return image.getSubimage(newBounds.x, newBounds.y, newBounds.width,
					newBounds.height);
		}

	}

	/**
	 * Retourne la couleur opposée d'une couleur
	 * 
	 * @param color
	 * @return
	 */
	public static Color getOppositeColor(Color color) {
		return new Color(255 - color.getRed(), 255 - color.getGreen(),
				255 - color.getBlue());
	}

	/**
	 * Utilitaire pour surchage d'equals. Permet n'éviter les erreurs dans les
	 * tableaux dechamps à comparer.
	 * <p>
	 * Retourne faux si 'objOrig' et 'objToCompare' ne sont pas de la même
	 * classe,
	 * <p>
	 * Retourne vrai ou faux si les tableaux de proprietes sont égaux,
	 * <p>
	 * Lance une exception si les tableaux ne sont pas de même taille.
	 * 
	 * @param classToCompare
	 * @param toCompare
	 * @param fields1
	 * @param fields2
	 * @return
	 */
	public static boolean equalsUtil(Object objOrig, Object objToCompare,
			Object[] fields1, Object[] fields2) {

		// verifier la classe de l'objet
		if (objOrig.getClass().isInstance(objToCompare) == false)
			return false;

		// verifier si les tableaux sont nuls
		if (fields1 == null || fields2 == null) {
			throw new NullPointerException("Properties are null. P1: "
					+ fields1 + ", P2: " + fields2);
		}

		// verifier les dimensionsdes tableaux
		else if (fields1.length != fields2.length) {
			throw new IllegalArgumentException(
					"Properties have not same lenght. P1: " + fields1.length
							+ ", P2: " + fields2.length);
		}

		// comparer les classes
		for (int i = 0; i < fields2.length; i++) {
			Object o1 = fields1[i];
			Object o2 = fields2[i];

			if (o1.getClass().isInstance(o2) == false) {
				throw new IllegalArgumentException(
						"Properties do not match. Index: " + i + ", object 1: "
								+ o1.getClass() + ", object 2: "
								+ o2.getClass());
			}

		}

		return Arrays.deepEquals(fields1, fields2);
	}

	/**
	 * Retourne les dimensions d'une image sans la lire ou null si echec.
	 * 
	 * @param source
	 * @return
	 * @throws IOException
	 */
	public static Dimension getImageDimensions(File source) throws IOException {

		ImageInputStream in = null;
		ImageReader reader = null;
		try {

			// ouvrir un flux entrant
			in = ImageIO.createImageInputStream(source);

			// chercher un reader compatible
			Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
			if (readers.hasNext()) {
				reader = readers.next();

				// lire et retourner la taille de l'image
				reader.setInput(in);
				return new Dimension(reader.getWidth(0), reader.getHeight(0));

			}

			// pas de reader disponible
			else {
				throw new IOException("No readers availables");
			}

		}

		// erreur: lancer une exception
		catch (Exception e) {
			Log.error(e);
			throw new IOException(e);
		}

		// fermer les ressources
		finally {
			if (reader != null) {
				reader.dispose();
			}
			if (in != null) {
				in.close();
			}
		}
	}

	/**
	 * Retourne tous les formats d'images ayant un ImageReader associé, en
	 * minuscules.
	 * 
	 * @return
	 */
	public static String[] getAllImageSupportedFormats() {
		String[] formats = ImageIO.getReaderFormatNames();
		for (int i = 0; i < formats.length; i++) {
			formats[i] = formats[i].toLowerCase();
		}

		return formats;
	}

	/**
	 * Convertir une chaine en tableau d'entiers
	 * 
	 * @param str
	 * @return
	 */
	public static Integer[] stringToIntArray(String str) {

		String[] parts = str.toLowerCase().trim().split(SEPARATOR);
		ArrayList<Integer> result = new ArrayList<Integer>();

		for (int i = 0; i < parts.length; i++) {
			try {
				result.add(Integer.valueOf(parts[i].trim()));
			} catch (Exception e) {
				Log.error(e);
			}
		}

		return result.toArray(new Integer[result.size()]);
	}

	/**
	 * Convertir un tableau d'entiers en chaine
	 * 
	 * @param array
	 * @return
	 */
	public static String intArrayToString(Integer[] array) {
		return Utils.join(SEPARATOR, array);
	}

	public static boolean isStringIntArray(String string) {
		return string.matches("^ *[0-9]+( *, *[0-9]+)* *$");
	}

	/**
	 * Retourne une image à l'echelle avec pour hauteur maximum maxHeight et
	 * pour largeur maximum maxWidth.
	 * <p>
	 * Méthode déstinée à réduire des images.
	 * <p>
	 * La plus petite des deux dimensions fournie sera retenue pour calculer le
	 * facteur de retrecissement.
	 * 
	 * @param img
	 * @param maxWidth
	 * @param maxHeight
	 * @return
	 */
	public static BufferedImage scaleImage(BufferedImage img, Integer maxWidth,
			Integer maxHeight) {

		// dimensions de l'image originale
		float originWidth = img.getWidth();
		float originHeight = img.getHeight();

		// ne retenir que la plus petite valeur
		int maxSide = maxWidth <= maxHeight ? maxWidth : maxHeight;

		// calculer le coeff de rétrécissement
		float coeff = maxWidth <= maxHeight ? maxWidth / originWidth
				: maxHeight / originHeight;

		return scaleImage(img, coeff);

	}

	/**
	 * Retourne une image agrandie ou rétrécie au coefficient passé en argument.
	 * 
	 * @param img
	 * @param coeff
	 * @return
	 */
	public static BufferedImage scaleImage(BufferedImage img, float coeff) {

		int newWidth = Math.round(img.getWidth() * coeff);
		int newHeight = Math.round(img.getHeight() * coeff);

		// creer une nouvelle image
		BufferedImage resizedImage = new BufferedImage(newWidth, newHeight,
				img.getType());
		Graphics2D g = resizedImage.createGraphics();

		// améliorer la qualité du rendu
		GuiUtils.applyQualityRenderingHints(g);

		// dessiner l'image
		g.drawImage(img, 0, 0, newWidth, newHeight, null);
		g.dispose();

		return resizedImage;
	}

	/**
	 * Renvoi vrai si les deux points sont égaux, avec une précision de p
	 * 
	 * @param x
	 * @param y
	 * @param p
	 * @return
	 */
	public static boolean approximateEquals(int x, int y, int p) {

		// vérifier que la précision est bien positive
		if (p < 0) {
			throw new IllegalArgumentException(
					"Precision must be positive. p: " + p);
		}

		return Math.abs(x - y) < p;
	}

	/**
	 * Stop le thread courant timems ms
	 * 
	 * @param timems
	 */
	public static void sleep(int timems) {

		if (timems <= 0) {
			return;
		}

		try {
			Thread.sleep(timems);
		} catch (InterruptedException e) {
			Log.error(e);
		}

	}

	public static MessageDigest getHasherInstance() {
		return getHasherInstance(DEFAULT_HASH_ALGO);
	}

	public static MessageDigest getHasherInstance(String name) {
		try {
			return MessageDigest.getInstance(name);
		} catch (NoSuchAlgorithmException e) {
			Log.error(e);
		}

		return null;
	}

	public static byte[] getHashFromImage(BufferedImage image) {
		hasher.update(Utils.imageToByte(image));
		return hasher.digest();
	}

	/**
	 * Retourne une chaine unique caractérisant un Thread composées de son nom
	 * et d'un identifiant.
	 * 
	 * @return
	 */
	public static String getThreadSimpleID() {
		return getThreadSimpleID(Thread.currentThread());
	}

	/**
	 * Retourne une chaine unique caractérisant un Thread composées de son nom
	 * et d'un identifiant.
	 * 
	 * @return
	 */
	public static String getThreadSimpleID(Thread t) {
		return t.getName() + "_" + t.getId();
	}

	private static final String LOREM_IPSUM = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. "
			+ "Donec a diam lectus. Sed sit amet ipsum mauris. Maecenas congue ligula ac "
			+ "quam viverra nec consectetur ante hendrerit. Donec et mollis dolor. "
			+ "Praesent et diam eget libero egestas mattis sit amet vitae augue. Nam "
			+ "tincidunt congue enim, ut porta lorem lacinia consectetur. Donec ut libero "
			+ "sed arcu vehicula ultricies a non tortor. Lorem ipsum dolor sit amet, "
			+ "consectetur adipiscing elit. Aenean ut gravida lorem. Ut turpis felis, "
			+ "pulvinar a semper sed, adipiscing id dolor. Pellentesque auctor nisi id "
			+ "magna consequat sagittis. Curabitur dapibus enim sit amet elit pharetra "
			+ "tincidunt feugiat nisl imperdiet. Ut convallis libero in urna ultrices "
			+ "accumsan. Donec sed odio eros. Donec viverra mi quis quam pulvinar at "
			+ "malesuada arcu rhoncus. Cum sociis natoque penatibus et magnis dis parturient "
			+ "montes, nascetur ridiculus mus. In rutrum accumsan ultricies. Mauris "
			+ "vitae nisi at sem facilisis semper ac in est.";

	public static String generateLoremIpsum(int length) {

		String rslt = LOREM_IPSUM;

		while (rslt.length() < length) {
			rslt += LOREM_IPSUM;
		}

		return rslt.substring(0, length);
	}

	/**
	 * Converti une valeur en pixel en millimètres, en fonction de la résolution
	 * précisées.
	 * 
	 * @param pixel
	 * @param dpiRes
	 * @return
	 */
	public static double pixelToMillimeter(double pixel, double dpiRes) {
		return pixel / dpiRes * 25.40d;
	}

	/**
	 * Converti une valeur en millimètres en pixels, en fonction de la
	 * résolution précisées.
	 * 
	 * @param pixel
	 * @param dpiRes
	 * @return
	 */
	public static double millimeterToPixel(double millimeter, double dpiRes) {
		return millimeter / 25.40d * dpiRes;
	}

}
