package abcmap.utils.gui;

import java.awt.Color;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlColors {

	private static final Object HEXA_ID = "#";
	private static HashMap<String, String> colors;

	public static Color parse(String str) {

		// enlever les caracterers inutiles
		str = str.replaceAll("\\s+", "");

		// cas n°2: la couleur est une couleur hexadecimale
		if (str.substring(0, 1).equals(HEXA_ID)) {
			return hexaToColor(str.substring(1));
		}

		// la couleur est une couleur html
		if (colors.get(str) != null) {
			return hexaToColor(colors.get(str));
		}

		// la couleur est rgb
		Pattern rgbPattern = Pattern.compile("^(\\d{1,3})[,\\.](\\d{1,3})[,\\.](\\d{1,3})$");
		Matcher m = rgbPattern.matcher(str);

		if (m.find()) {

			int r = new Integer(m.group(1));
			int g = new Integer(m.group(2));
			int b = new Integer(m.group(3));

			return new Color(r, g, b);

		}

		// chaine non parsable, retour null
		return null;

	}

	public static Color hexaToColor(String colorStr) {
		return new Color(Integer.valueOf(colorStr.substring(0, 2), 16),
				Integer.valueOf(colorStr.substring(2, 4), 16),
				Integer.valueOf(colorStr.substring(4, 6), 16));
	}

	static {

		colors = new HashMap<String, String>();
		colors.put("mediumorchid", "BA55D3");
		colors.put("darkseagreen", "8FBC8F");
		colors.put("sienna", "A0522D");
		colors.put("lightslategray", "778899");
		colors.put("black", "000000");
		colors.put("gainsboro", "DCDCDC");
		colors.put("lightcoral", "F08080");
		colors.put("orange", "FFA500");
		colors.put("dodgerblue", "1E90FF");
		colors.put("darkslategray", "2F4F4F");
		colors.put("lightseagreen", "20B2AA");
		colors.put("aquamarine", "7FFFD4");
		colors.put("beige", "F5F5DC");
		colors.put("royalblue", "041690");
		colors.put("darkviolet", "9400D3");
		colors.put("mediumslateblue", "7B68EE");
		colors.put("mediumaquamarine", "66CDAA");
		colors.put("olivedrab", "6B8E23");
		colors.put("midnightblue", "191970");
		colors.put("sandybrown", "F4A460");
		colors.put("violet", "EE82EE");
		colors.put("limegreen", "32CD32");
		colors.put("palegoldenrod", "EEE8AA");
		colors.put("magenta", "FF00FF");
		colors.put("powderblue", "B0E0E6");
		colors.put("darkslateblue", "483D8B");
		colors.put("deepskyblue", "00BFFF");
		colors.put("darkturquoise", "00CED1");
		colors.put("chartreuse", "7FFF00");
		colors.put("olive", "808000");
		colors.put("indianred", "CD5C5C");
		colors.put("peachpuff", "FFDAB9");
		colors.put("mediumpurple", "9370DB");
		colors.put("yellow", "FFFF00");
		colors.put("lightblue", "ADD8E6");
		colors.put("indigo", "4B0082");
		colors.put("springgreen", "00FF7F");
		colors.put("darkred", "8B0000");
		colors.put("wheat", "F5DEB3");
		colors.put("peru", "CD853F");
		colors.put("rosybrown", "BC8F8F");
		colors.put("darkcyan", "008B8B");
		colors.put("firebrick", "B22222");
		colors.put("lawngreen", "7CFC00");
		colors.put("orangered", "FF4500");
		colors.put("darkorange", "FF8C00");
		colors.put("teal", "008080");
		colors.put("turquoise", "40E0D0");
		colors.put("maroon", "800000");
		colors.put("cyan", "00FFFF");
		colors.put("blue", "0000FF");
		colors.put("moccasin", "FFE4B5");
		colors.put("chocolate", "D2691E");
		colors.put("whitesmoke", "F5F5F5");
		colors.put("thistle", "D8BFD8");
		colors.put("mediumseagreen", "3CB371");
		colors.put("seagreen", "2E8B57");
		colors.put("red", "FF0000");
		colors.put("lightcyan", "E0FFFF");
		colors.put("lavenderblush", "FFF0F5");
		colors.put("slategray", "708090");
		colors.put("mistyrose", "FFE4E1");
		colors.put("crimson", "DC143C");
		colors.put("navajowhite", "FFDEAD");
		colors.put("slateblue", "6A5ACD");
		colors.put("orchid", "DA70D6");
		colors.put("tan", "D2B48C");
		colors.put("lightsalmon", "FFA07A");
		colors.put("seashell", "FFF5EE");
		colors.put("darkgray", "A9A9A9");
		colors.put("snow", "FFFAFA");
		colors.put("fuchsia", "FF00FF");
		colors.put("darkblue", "00008B");
		colors.put("tomato", "FF6347");
		colors.put("amethyst", "9966CC");
		colors.put("plum", "DDA0DD");
		colors.put("cornsilk", "FFF8DC");
		colors.put("palegreen", "98FB98");
		colors.put("yellowgreen", "9ACD32");
		colors.put("mintcream", "F5FFFA");
		colors.put("palevioletred", "DB7093");
		colors.put("dimgray", "696969");
		colors.put("gold", "FFD700");
		colors.put("darkolivegreen", "556B2F");
		colors.put("azure", "F0FFFF");
		colors.put("gray", "808080");
		colors.put("salmon", "FA8072");
		colors.put("lemonchiffon", "FFFACD");
		colors.put("floralwhite", "FFFAF0");
		colors.put("blanchedalmond", "FFEBCD");
		colors.put("greenyellow", "ADFF2F");
		colors.put("silver", "C0C0C0");
		colors.put("pink", "FFC0CB");
		colors.put("khaki", "F0E68C");
		colors.put("lightskyblue", "87CEFA");
		colors.put("ivory", "FFFFF0");
		colors.put("aliceblue", "F0F8FF");
		colors.put("darkgreen", "006400");
		colors.put("darksalmon", "E9967A");
		colors.put("papayawhip", "FFEFD5");
		colors.put("linen", "FAF0E6");
		colors.put("lightgoldenrodyellow", "FAFAD2");
		colors.put("lightgreen", "90EE90");
		colors.put("mediumturquoise", "48D1CC");
		colors.put("honeydew", "F0FFF0");
		colors.put("lightgrey", "D3D3D3");
		colors.put("antiquewhite", "FAEBD7");
		colors.put("brown", "A52A2A");
		colors.put("lightpink", "FFB6C1");
		colors.put("oldlace", "FDF5E6");
		colors.put("aqua", "00FFFF");
		colors.put("bisque", "FFE4C4");
		colors.put("cadetblue", "5F9EA0");
		colors.put("burlywood", "DEB887");
		colors.put("mediumblue", "0000CD");
		colors.put("blueviolet", "8A2BE2");
		colors.put("lime", "00FF00");
		colors.put("lavender", "E6E6FA");
		colors.put("lightsteelblue", "B0C4DE");
		colors.put("cornflowerblue", "6495ED");
		colors.put("goldenrod", "DAA520");
		colors.put("steelblue", "4682B4");
		colors.put("ghostwhite", "F8F8FF");
		colors.put("mediumspringgreen", "00FA9A");
		colors.put("darkkhaki", "BDB76B");
		colors.put("paleturquoise", "AFEEEE");
		colors.put("darkgoldenrod", "B8860B");
		colors.put("forestgreen", "228B22");
		colors.put("deeppink", "FF1493");
		colors.put("darkorchid", "9932CC");
		colors.put("mediumvioletred", "C71585");
		colors.put("hotpink", "FF69B4");
		colors.put("lightyellow", "FFFFE0");
		colors.put("navy", "000080");
		colors.put("saddlebrown", "8B4513");
		colors.put("white", "FFFFFF");
		colors.put("coral", "FF7F50");
		colors.put("purple", "800080");
		colors.put("darkmagenta", "8B008B");
		colors.put("green", "008000");
		colors.put("skyblue", "87CEEB");

	}

}
