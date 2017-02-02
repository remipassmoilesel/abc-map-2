package org.abcmap.core.importation;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by remipassmoilesel on 02/02/17.
 */
public class DataImportConfiguration {

    /**
     * Path of data document to import
     */
    private Path dataPath;

    /**
     * Available headers of data to import
     */
    private ArrayList<String> headers;

    public DataImportConfiguration() {
    }

    /**
     * Return the path of data to import
     *
     * @return
     */
    public Path getDataPath() {
        return dataPath;
    }

    /**
     * Set the path of data to import
     *
     * @param dataPath
     */
    public void setDataPath(Path dataPath) {
        this.dataPath = dataPath;
    }

    /**
     * Return a list of available headers in data
     *
     * @return
     */
    public ArrayList<String> getHeaders() {
        return headers;
    }

    /**
     * Set the list of headers
     *
     * @param headers
     */
    public void setHeaders(ArrayList<String> headers) {
        this.headers = headers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataImportConfiguration that = (DataImportConfiguration) o;
        return Objects.equals(dataPath, that.dataPath) &&
                Objects.equals(headers, that.headers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataPath, headers);
    }
}
