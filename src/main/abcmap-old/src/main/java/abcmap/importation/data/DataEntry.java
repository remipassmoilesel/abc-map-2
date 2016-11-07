package abcmap.importation.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import abcmap.geo.Coordinate;
import abcmap.utils.PrintUtils;
import abcmap.utils.Utils;

/**
 * Element d'une liste de données
 * 
 * @author remipassmoilesel
 *
 */
public class DataEntry {

	private Coordinate coords;

	/** Champs additionnels */
	private HashMap<String, String> fields;

	/** Index pour noms de champs générés automatiquement */
	private int fieldNameIndex;

	private String DEFAULT_FIELD_PREFIX = "field_";

	public DataEntry() {

		this.fieldNameIndex = 1;

		this.coords = new Coordinate();
		this.fields = new HashMap<String, String>();
	}

	/**
	 * Retourne la liste des noms des champs HORS latitude et longitude.
	 * 
	 * @return
	 */
	public Set<String> getFieldNames() {
		return fields.keySet();
	}

	/**
	 * Ajoute un champs à l'entrée. Si le nom est nul, un nom est généré.
	 * 
	 * @param name
	 * @param value
	 */
	public void addField(String name, String value) {

		// enlever les espaces inutiles
		name = name.trim();
		value = value.trim();

		// si le nom est vide, générer un nom
		if (name.isEmpty()) {
			name = DEFAULT_FIELD_PREFIX + fieldNameIndex;
			fieldNameIndex++;
		}

		fields.put(name, value);
	}

	public String getField(String name) {
		return fields.get(name);
	}

	public Coordinate getCoords() {
		return coords;
	}

	public void setCoords(Coordinate coords) {
		this.coords = coords;
	}

	public void setCoords(double latDeg, double lngDeg) {
		this.coords = new Coordinate(latDeg, lngDeg, 0, 0);
	}

	@Override
	public String toString() {

		ArrayList<Object> keys = new ArrayList<Object>();
		keys.add("coords.latitudeSec");
		keys.add("coords.longitudeSec");
		keys.addAll(fields.keySet());

		ArrayList<Object> values = new ArrayList<Object>();
		values.add(coords.latitudeSec);
		values.add(coords.longitudeSec);
		values.addAll(fields.values());

		return Utils.toString(this, keys.toArray(new Object[keys.size()]),
				values.toArray(new Object[values.size()]));

	}

}
