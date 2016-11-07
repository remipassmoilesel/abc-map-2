package abcmap.utils;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class PrintUtils {

	private static final String PREFIX = "---- ";
	private static Integer displayed = 0;

	public static void p(Object o) {

		// numéro de la ligne
		String displayedStr = displayed.toString();

		// Affichage
		System.out.println("#" + displayedStr + ": " + o);

		displayed++;
	}

	public static void p() {
		p("");
	}

	public static void pObjectAndValues(Object o, Object[] values) {
		pObjectAndValues(o, null, values);
	}

	public static void pObjectAndValues(Object o, Object[] keys, Object[] values) {

		p(PREFIX + "[" + values.length + "]: " + o + " / " + o.getClass().getName());
		for (int i = 0; i < values.length; i++) {
			if (keys == null) {
				p("\t " + i + ": " + values[i]);
			}

			else {
				p("\t " + i + ": " + keys[i] + " : " + values[i]);
			}
		}
	}

	public static void printClassPath() {
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		URL[] urls = ((URLClassLoader) cl).getURLs();

		pObjectAndValues(cl, urls);
	}

	public static void p(Object[] array) {

		if (array == null) {
			p("Array is null");
			return;
		}

		if (array.length == 0) {
			p("Array is empty: " + array.getClass().getName());
			return;
		}

		pObjectAndValues(array, array);
	}

	public static void p(Map o) {
		if (o == null) {
			p("Null object.");
			pStackTrace(5);
			return;
		}
		pObjectAndValues(o, o.keySet().toArray(), o.values().toArray());
	}

	public static void p(Collection lst) {
		pObjectAndValues(lst, lst.toArray());
	}

	public static void pClass(Object c) {

		// lister les interfaces
		Class<?>[] interfaces = c.getClass().getInterfaces();
		Object[] keys = new Object[interfaces.length + 1];
		Object[] values = new Object[interfaces.length + 1];

		for (int i = 0; i < interfaces.length; i++) {
			keys[i + 1] = "Interface";
			values[i + 1] = interfaces[i];
		}

		// ajouter la superclasse
		keys[0] = "Superclass";
		values[0] = c.getClass().getSuperclass();

		// affichage
		pObjectAndValues(c, keys, values);

	}

	public static void p(CoordinateReferenceSystem crs) {

		Object[] keys = new Object[] { "crs.hashCode()", "crs.getAlias()", "crs.getIdentifiers()",
				"crs.getName()", "crs.getScope()", "crs.getRemarks()", };
		Object[] values = new Object[] { crs.hashCode(), crs.getAlias(), crs.getIdentifiers(),
				crs.getName(), crs.getScope(), crs.getRemarks(), };

		pObjectAndValues(crs, keys, values);

	}

	public static void pStackTrace(Integer nbr) {

		if (nbr == null) {
			nbr = 5;
		}

		// recuperer la stack
		List<StackTraceElement> stack = Arrays.asList(new Exception().getStackTrace());

		if (stack.size() < 2) {
			p("Stack is empty");
			return;
		}

		// enlever les elements non significatifs
		int maxSize = stack.size() > nbr + 1 ? nbr + 1 : stack.size();
		stack = stack.subList(1, maxSize);

		pObjectAndValues("printStackTrace()", stack.toArray());

	}

}
