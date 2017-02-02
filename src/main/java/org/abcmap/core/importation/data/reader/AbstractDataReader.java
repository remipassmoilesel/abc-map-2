package org.abcmap.core.importation.data.reader;

import org.abcmap.core.importation.data.DataEntryList;
import org.abcmap.core.importation.data.DataImportException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDataReader {

    /**
     * Default prefix used when non name are availables
     */
    public static final String HEADER_DEFAULT_PREFIX = "field_";

    /**
     * Maximum number of lines allowed
     */
    public static final int MAX_DATA_PARSING = 20000;

    /**
     * Minimal number of fields for a valid line
     */
    public static final int MINIMUM_LINE_SIZE = 2;

    /**
     * From zero, the index of the header line
     */
    public static final int HEADERS_LABELS_INDEX = 1;

    /**
     * From zero, index of latitude column
     */
    public static final int HEADER_INDEX_LATITUDE = 0;

    /**
     * From zero, index of longitude column
     */
    public static final int HEADER_INDEX_LONGITUDE = 1;

    /**
     * From zero, index of first custom column
     */
    public static final int HEADER_FIRST_FIELD_INDEX = 2;

    /**
     * Default label for latitude column
     */
    public static final String LABEL_LATITUDE = "latitude";

    /**
     * Default label for type column
     */
    public static final String LABEL_TYPE = "type";

    /**
     * Default label for time column
     */
    public static final String LABEL_TIME = "time";

    /**
     * Default label for elevation column
     */
    public static final String LABEL_ELEVATION = "elevation";

    /**
     * Default label for description column
     */
    public static final String LABEL_DESCRIPTION = "description";


    /**
     * Default label for longitude column
     */
    public static final String LABEL_LONGITUDE = "longitude";

    /**
     * Return a valid data parser for specified file extension or null
     *
     * @param extension
     * @return
     */
    public static AbstractDataReader getParserFor(String extension) {

        // throw if extension null
        if (extension == null) {
            throw new NullPointerException("Extension is null");
        }

        // normalize extension
        extension = extension.toLowerCase().trim();

        // search parser and return it
        for (AbstractDataReader p : AbstractDataReader.getAvailableParsers()) {
            if (p.isSupportedExtension(extension)) {
                return p;
            }
        }

        // nothing found
        return null;

    }

    /**
     * Return a list of available parser instances
     *
     * @return
     */
    public static AbstractDataReader[] getAvailableParsers() {
        return new AbstractDataReader[]{new CsvDataReader()};
    }

    /**
     * This method should return a list of supported file extensions in lower case
     *
     * @return
     */
    public abstract List<String> getSupportedExtensions();

    /**
     * Return true if parser support this extension
     *
     * @param extension
     * @return
     */
    public boolean isSupportedExtension(String extension) {
        return getSupportedExtensions().contains(extension);
    }

    /**
     * Parse the specified file and return a data entry list
     *
     * @param file
     * @return
     * @throws IOException
     * @throws DataImportException
     */
    public abstract DataEntryList parseFile(Path file) throws IOException, DataImportException;

    /**
     * Parse specified file and return list of headers
     *
     * @param file
     * @return
     * @throws IOException
     * @throws DataImportException
     */
    public abstract ArrayList<String> getHeaders(Path file) throws IOException, DataImportException;

}
