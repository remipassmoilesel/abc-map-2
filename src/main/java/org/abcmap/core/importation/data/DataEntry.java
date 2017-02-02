package org.abcmap.core.importation.data;

import com.vividsolutions.jts.geom.Coordinate;

import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

/**
 * Generic element of a data list
 */
public class DataEntry {

    /**
     * Default prefix used to geneate field name, if no name was provided
     */
    private static final String DEFAULT_FIELD_PREFIX = "field_";

    /**
     * Coordinates of entry, mandatory
     */
    private Coordinate coordinates;

    /**
     * Additional fields of entry
     */
    private HashMap<String, String> fields;

    /**
     * Index used to generate name for fields, if non name was provided
     */
    private int fieldNameIndex;

    public DataEntry(Coordinate coords) {
        this.fieldNameIndex = 1;
        this.coordinates = coords;
        this.fields = new HashMap<>();
    }

    /**
     * Return all field names, excluding latitude and longitude
     *
     * @return
     */
    public Set<String> getFieldNames() {
        return fields.keySet();
    }

    /**
     * Add a custom field. If name is null, a new name will be generated.
     *
     * /!\ Name and value will be trimmed
     *
     * @param name
     * @param value
     */
    public void addField(String name, String value) {

        // remove uneeded spaces
        name = name.trim();
        value = value.trim();

        if (fields.get(name) != null) {
            throw new IllegalArgumentException("Name already exist: " + name);
        }

        // if name is empty, generate a name
        if (name.isEmpty()) {
            name = DEFAULT_FIELD_PREFIX + fieldNameIndex;
            fieldNameIndex++;
        }

        fields.put(name, value);
    }

    /**
     * Update value associated with field
     *
     * /!\ Name and value will be trimmed
     *
     * @param name
     * @param value
     */
    public void updateField(String name, String value) {

        // remove uneeded spaces
        name = name.trim();
        value = value.trim();

        if (fields.get(name) == null) {
            throw new IllegalArgumentException("Unknown name: " + name);
        }

        // remove uneeded spaces
        name = name.trim();
        value = value.trim();

        fields.put(name, value);
    }

    /**
     * Return value associated with specified name
     *
     * @param name
     * @return
     */
    public String getField(String name) {
        return fields.get(name);
    }

    /**
     * Get coordinates of element
     *
     * @return
     */
    public Coordinate getCoordinates() {
        return coordinates;
    }

    /**
     * Set coordinates of element
     *
     * @param coordinates
     */
    public void setCoordinates(Coordinate coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    public String toString() {
        return "DataEntry{" +
                "coordinates=" + coordinates +
                ", fields=" + fields +
                ", fieldNameIndex=" + fieldNameIndex +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataEntry dataEntry = (DataEntry) o;
        return fieldNameIndex == dataEntry.fieldNameIndex &&
                Objects.equals(coordinates, dataEntry.coordinates) &&
                Objects.equals(fields, dataEntry.fields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coordinates, fields, fieldNameIndex);
    }
}
