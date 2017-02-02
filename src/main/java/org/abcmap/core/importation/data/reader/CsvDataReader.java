package org.abcmap.core.importation.data.reader;

import com.vividsolutions.jts.geom.Coordinate;
import org.abcmap.core.configuration.ConfigurationConstants;
import org.abcmap.core.importation.data.DataEntry;
import org.abcmap.core.importation.data.DataEntryList;
import org.abcmap.core.importation.data.DataImportException;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.utils.GeoUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Read CSV files
 */
public class CsvDataReader extends AbstractDataReader {

    private static final CustomLogger logger = LogManager.getLogger(CsvDataReader.class);

    private static final List<String> supportedExtensions = Arrays.asList("csv");

    @Override
    public List<String> getSupportedExtensions() {
        return supportedExtensions;
    }

    @Override
    public ArrayList<String> getHeaders(Path file) throws IOException, DataImportException {

        // get headers
        CSVRecord headersLine = getLines(file, HEADERS_LABELS_INDEX).get(HEADERS_LABELS_INDEX);

        ArrayList<String> headers = new ArrayList<>();

        // check if headers have a name.
        // If not, create one
        int i = 1;
        for (Iterator<String> iterator = headersLine.iterator(); iterator.hasNext(); ) {

            String header = iterator.next().trim();
            headers.add(header.isEmpty() ? HEADER_DEFAULT_PREFIX + i : header);

            i++;
        }

        return headers;

    }

    @Override
    public DataEntryList parseFile(Path file) throws IOException, DataImportException {

        // get headers
        ArrayList<String> headers = getHeaders(file);

        // get all lines of csv file
        List<CSVRecord> lines = getLines(file, -1);

        // create a new data list
        DataEntryList dataList = new DataEntryList();

        // iterate lines from header line to end
        for (int i = HEADERS_LABELS_INDEX; i < lines.size(); i++) {

            CSVRecord cl = lines.get(i);
            if (cl.size() < MINIMUM_LINE_SIZE) {
                continue;
            }

            // parse coordinates
            String lat = cl.get(HEADER_INDEX_LATITUDE);
            String lon = cl.get(HEADER_INDEX_LONGITUDE);
            Coordinate coords = GeoUtils.stringToCoordinate(lat, lon);

            // coordinates are null, ignore
            if (coords == null) {
                // TODO: throw error ?
                logger.warning("Coordinates are invalid: " + lat + " / " + lon);
                continue;
            }

            // create an entry
            DataEntry data = new DataEntry(coords);

            // add all fields
            for (int j = HEADER_FIRST_FIELD_INDEX; j < cl.size(); j++) {
                // get header name or let data entry generate one
                String name = j < headers.size() ? headers.get(j) : "";
                data.addField(name, cl.get(j));
            }

            dataList.add(data);

        }

        return dataList;
    }


    /**
     * Return lines of CSV file. If specified count of line <= 0, all lines will be read.
     *
     * @param path
     * @param lines
     * @return
     * @throws DataImportException
     * @throws IOException
     */
    private List<CSVRecord> getLines(Path path, int lines) throws DataImportException, IOException {

        try (Reader in = new FileReader(path.toFile());
             CSVParser parser = ConfigurationConstants.DEFAULT_CSV_FORMAT.parse(in)) {

            // ask for records in order to get number of records later
            // if we don't ask records here, getRecordNumber() will return 0
            List<CSVRecord> originalRecords = parser.getRecords();

            // check file length
            if (originalRecords.size() < HEADERS_LABELS_INDEX + 1) {
                throw new DataImportException(DataImportException.DATAS_TOO_LIGHT);
            } else if (originalRecords.size() > MAX_DATA_PARSING) {
                throw new DataImportException(DataImportException.DATAS_TOO_HEAVY);
            }

            // return all lines
            if (lines <= 0) {
                return originalRecords;
            }

            // or just some
            else {

                ArrayList<CSVRecord> records = new ArrayList<>();
                Iterator<CSVRecord> iterator = originalRecords.iterator();
                for (int i = 0; i < lines; i++) {
                    records.add(iterator.next());
                }

                return originalRecords;
            }

        }
    }

}
