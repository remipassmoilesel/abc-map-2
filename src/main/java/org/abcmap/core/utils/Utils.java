package org.abcmap.core.utils;

import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.gui.utils.GuiUtils;
import org.apache.commons.io.FileUtils;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.imageio.*;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataFormatImpl;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.instrument.Instrumentation;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class Utils {

    private static final CustomLogger logger = LogManager.getLogger(Utils.class);

    /**
     * Memory check utility
     */
    private static Instrumentation instrumentation;

    private static final String STRING_SEPARATOR = ",";

    private static Random rand = new Random();

    private static final String DEFAULT_HASH_ALGO = "SHA-256";

    private static MessageDigest hasher = getHasherInstance();

    public static final String WINDOWS = "windows";
    public static final String MAC = "mac";
    public static final String LINUX = "linux";

    /**
     * Return OS name or null
     *
     * @return
     */
    public static String getOsName() {
        String[] osArray = new String[]{WINDOWS, MAC, LINUX};
        String os = System.getProperty("os.name").toLowerCase();
        for (String o : osArray) {
            if (os.indexOf(o) >= 0) {
                return o;
            }
        }
        return null;
    }

    /**
     * Utility to harmonize tostring methods
     *
     * @param obj
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
                .hasNext(); ) {
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
     * Compare without nullpointerexception
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

    /**
     * Compare without nullpointerexception
     *
     * @param obj1
     * @param obj2
     * @return
     */
    public static boolean safeEqualsIgnoreCase(String obj1, String obj2) {

        if (obj1 == obj2) {
            return true;
        }

        if (obj1 == null || obj2 == null)
            return false;

        return obj1.equalsIgnoreCase(obj2);

    }

    /**
     * Compare without nullpointerexception
     *
     * @param c1
     * @param c2
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

        if (c1.size() != c2.size())
            return false;

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

        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {

            Object object = iterator.next();

            result += object.toString();

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

        return join(STRING_SEPARATOR,
                new Integer[]{c.getRed(), c.getGreen(), c.getBlue()});

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

        return join(STRING_SEPARATOR, new Integer[]{dim.width, dim.height});
    }

    public static Dimension stringToDimension(String s) {

        if (s.equalsIgnoreCase("null")) {
            return null;
        }

        Integer[] vals = stringToIntArray(s);

        if (vals.length != 2) {
            throw new IllegalArgumentException("Chaine incorrecte: " + s);
        }

        return new Dimension(Integer.valueOf(vals[0]), Integer.valueOf(vals[1]));
    }

    public static String pointToString(Point pt) {
        if (pt == null) {
            return "null";
        }

        return join(STRING_SEPARATOR, new Integer[]{pt.x, pt.y});
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

    public static boolean checkExtension(Path file, String extension) {
        return checkExtension(file.toString(), extension);
    }

    /**
     * Return file extension or empty string
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
     * Delete a directory recursively
     *
     * @param file
     * @throws IOException
     */
    public static void deleteDirectories(File file) throws IOException {
        FileUtils.deleteDirectory(file);
    }


    /**
     * Write a jpeg picture with maximum quality
     *
     * @param image
     * @param file
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void writeImage(BufferedImage image, File file)
            throws FileNotFoundException, IOException {

        Iterator iter = ImageIO.getImageWritersByFormatName("jpeg");
        ImageWriter writer = (ImageWriter) iter.next();
        ImageWriteParam iwp = writer.getDefaultWriteParam();
        iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);

        iwp.setCompressionQuality(1f);

        FileImageOutputStream output = null;

        try {
            output = new FileImageOutputStream(file);
            writer.setOutput(output);
            IIOImage iioi = new IIOImage(image, null, null);
            writer.write(null, iioi, iwp);
        } finally {
            if (writer != null) {
                writer.dispose();
            }
            if (output != null) {
                output.close();
            }
        }

    }

    /**
     * Return a byte array or null if an error occur
     *
     * @param img
     * @return
     */
    public static byte[] imageToByte(BufferedImage img) {

        try {
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                ImageIO.write(img, "png", out);
                return out.toByteArray();
            }
        } catch (IOException e) {
            logger.error(e);
            return null;
        }

    }

    /**
     * Return a buffered image generated from byte array or null if an error occur
     *
     * @param bytes
     * @return
     */
    public static BufferedImage bytesToImage(byte[] bytes) {


        try {
            try (ByteArrayInputStream in = new ByteArrayInputStream(bytes)) {
                return ImageIO.read(in);
            }
        } catch (IOException e) {
            logger.error(e);
            return null;
        }

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
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    /**
     * @param unsortMap
     * @return
     */
    public static Map<String, Integer> sortByComparator(
            Map<String, Integer> unsortMap) {

        return sortByComparator(unsortMap, true);
    }

    /**
     * Sort a map
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
                .hasNext(); ) {
            Map.Entry<String, Integer> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }


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


    public static String getDate() {
        return getDate("yyyy-MM-dd_HH-mm-ss_S");
    }


    public static String getDate(String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(new Date());
    }

    /**
     * Check pixels of an picture searching transparent borders, to crop picture
     *
     * @param image
     * @return
     */
    public static BufferedImage removeTransparentBorders(BufferedImage image) {

        int width = image.getWidth();
        int height = image.getHeight();

        if (width == 0 || height == 0)
            throw new IllegalArgumentException("Dimensions too shorts: w: "
                    + width + " h: " + height);

        Rectangle newBounds = new Rectangle();
        newBounds.width = width;
        newBounds.height = height;

        for (int y = 0; y < height; y++) {
            int x = 0;
            for (; x < width; x++) {
                int pixel = image.getRGB(x, y);
                if (pixel != 0) {
                    break;
                }
            }

            if (x >= width) {
                newBounds.y++;
            } else {
                break;
            }
        }

        for (int x = width - 1; x >= 0; x--) {
            int y = 0;
            for (; y < height - 1; y++) {
                int pixel = image.getRGB(x, y);
                if (pixel != 0) {
                    break;
                }
            }

            if (y >= height - 1) {
                newBounds.width--;
            } else {
                break;
            }
        }

        for (int y = height - 1; y >= 0; y--) {
            int x = 0;
            for (; x < width; x++) {
                int pixel = image.getRGB(x, y);
                if (pixel != 0) {
                    break;
                }
            }

            if (x >= width) {
                newBounds.height--;
            } else {
                break;
            }
        }

        for (int x = 0; x < width; x++) {
            int y = 0;
            for (; y < height; y++) {
                int pixel = image.getRGB(x, y);
                if (pixel != 0) {
                    break;
                }
            }

            if (y >= height) {
                newBounds.x++;
            } else {
                break;
            }
        }

        newBounds.width -= newBounds.x;
        newBounds.height -= newBounds.y;

        int diffx = width - newBounds.x + newBounds.width;
        int diffy = height - newBounds.y + newBounds.height;

        if (diffx <= 0 || diffy <= 0) {
            return image;
        } else {
            return image.getSubimage(newBounds.x, newBounds.y, newBounds.width,
                    newBounds.height);
        }

    }

    public static Color getOppositeColor(Color color) {
        return new Color(255 - color.getRed(), 255 - color.getGreen(),
                255 - color.getBlue());
    }

    /**
     * Utility to harmonize equals methods
     *
     * @param objOrig
     * @param objToCompare
     * @param fields1
     * @param fields2
     * @return
     */
    public static boolean equalsUtil(Object objOrig, Object objToCompare,
                                     Object[] fields1, Object[] fields2) {

        if (objOrig.getClass().isInstance(objToCompare) == false)
            return false;

        if (fields1 == null || fields2 == null) {
            throw new NullPointerException("Properties are null. P1: "
                    + fields1 + ", P2: " + fields2);
        } else if (fields1.length != fields2.length) {
            throw new IllegalArgumentException(
                    "Properties have not same lenght. P1: " + fields1.length
                            + ", P2: " + fields2.length);
        }

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
     * Get dimensions of picture or null. Doesn't load image.
     *
     * @param source
     * @return
     * @throws IOException
     */
    public static Dimension getImageDimensions(File source) throws IOException {

        ImageInputStream in = null;
        ImageReader reader = null;
        try {

            in = ImageIO.createImageInputStream(source);

            Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
            if (readers.hasNext()) {
                reader = readers.next();

                reader.setInput(in);
                return new Dimension(reader.getWidth(0), reader.getHeight(0));

            } else {
                throw new IOException("No readers availables");
            }

        } catch (Exception e) {
            logger.error(e);
            throw new IOException(e);
        } finally {
            if (reader != null) {
                reader.dispose();
            }
            if (in != null) {
                in.close();
            }
        }
    }

    public static String[] getAllImageSupportedFormats() {
        String[] formats = ImageIO.getReaderFormatNames();
        for (int i = 0; i < formats.length; i++) {
            formats[i] = formats[i].toLowerCase();
        }

        return formats;
    }

    public static Integer[] stringToIntArray(String str) {

        String[] parts = str.toLowerCase().trim().split(STRING_SEPARATOR);
        ArrayList<Integer> result = new ArrayList<Integer>();

        for (int i = 0; i < parts.length; i++) {
            try {
                result.add(Integer.valueOf(parts[i].trim()));
            } catch (Exception e) {
                logger.error(e);
            }
        }

        return result.toArray(new Integer[result.size()]);
    }


    public static String intArrayToString(Integer[] array) {
        return Utils.join(STRING_SEPARATOR, array);
    }

    public static boolean isStringIntArray(String string) {
        return string.matches("^ *[0-9]+( *, *[0-9]+)* *$");
    }


    public static BufferedImage scaleImage(BufferedImage img, Integer maxWidth,
                                           Integer maxHeight) {

        float originWidth = img.getWidth();
        float originHeight = img.getHeight();

        int maxSide = maxWidth <= maxHeight ? maxWidth : maxHeight;

        float coeff = maxWidth <= maxHeight ? maxWidth / originWidth
                : maxHeight / originHeight;

        return scaleImage(img, coeff);

    }


    public static BufferedImage scaleImage(BufferedImage img, float coeff) {

        int newWidth = Math.round(img.getWidth() * coeff);
        int newHeight = Math.round(img.getHeight() * coeff);

        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight,
                img.getType());
        Graphics2D g = resizedImage.createGraphics();

        GuiUtils.applyQualityRenderingHints(g);

        g.drawImage(img, 0, 0, newWidth, newHeight, null);
        g.dispose();

        return resizedImage;
    }

    public static boolean approximateEquals(int x, int y, int p) {

        if (p < 0) {
            throw new IllegalArgumentException(
                    "Precision must be positive. p: " + p);
        }

        return Math.abs(x - y) < p;
    }

    public static void sleep(int timems) {

        if (timems <= 0) {
            return;
        }

        try {
            Thread.sleep(timems);
        } catch (InterruptedException e) {
            logger.error(e);
        }

    }

    public static MessageDigest getHasherInstance() {
        return getHasherInstance(DEFAULT_HASH_ALGO);
    }

    public static MessageDigest getHasherInstance(String name) {
        try {
            return MessageDigest.getInstance(name);
        } catch (NoSuchAlgorithmException e) {
            logger.error(e);
        }

        return null;
    }

    public static byte[] getHashFromImage(BufferedImage image) {
        hasher.update(Utils.imageToByte(image));
        return hasher.digest();
    }

    public static String getThreadSimpleID() {
        return getThreadSimpleID(Thread.currentThread());
    }

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

    public static double pixelToMillimeter(double pixel, double dpiRes) {
        return pixel / dpiRes * 25.40d;
    }

    public static double millimeterToPixel(double millimeter, double dpiRes) {
        return millimeter / 25.40d * dpiRes;
    }

}
