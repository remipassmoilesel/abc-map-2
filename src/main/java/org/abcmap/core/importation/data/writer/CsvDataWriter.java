package org.abcmap.core.importation.data.writer;

import com.vividsolutions.jts.geom.Coordinate;
import org.abcmap.core.configuration.ConfigurationConstants;
import org.abcmap.core.importation.data.DataEntry;
import org.abcmap.core.importation.data.DataEntryList;
import org.abcmap.core.importation.data.reader.AbstractDataReader;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CsvDataWriter extends AbstractDataWriter {

    private static final List<String> supportedExtensions = Arrays.asList("csv");

    @Override
    public List<String> getSupportedExtensions() {
        return supportedExtensions;
    }

    @Override
    public void write(DataEntryList list, Path destination) throws IOException {

        // create file first
        Files.createFile(destination);

        // initialize FileWriter object
        try (FileWriter fileWriter = new FileWriter(destination.toFile());
             CSVPrinter csvFilePrinter = new CSVPrinter(fileWriter, ConfigurationConstants.DEFAULT_CSV_FORMAT)) {

            // get all headers from list
            ArrayList<String> headers = new ArrayList<>(list.getAllHeaders());
            headers.add(0, AbstractDataReader.LABEL_LATITUDE);
            headers.add(1, AbstractDataReader.LABEL_LONGITUDE);

            // print headers and comment
            csvFilePrinter.printRecord(list.getComments());
            csvFilePrinter.printRecord(headers);

            // print data entries
            for (DataEntry entry : list) {

                // add position
                Coordinate degrees = entry.getCoordinates();
                csvFilePrinter.print(degrees.y);
                csvFilePrinter.print(degrees.x);

                // iterate optional fields
                for (int i = 2; i < headers.size(); i++) {
                    String field = entry.getField(headers.get(i));
                    csvFilePrinter.print(field != null ? field : "");
                }

                // end line
                csvFilePrinter.println();

            }

        }
    }

    /**
     * Create an example file and save it
     *
     * @param destination
     * @throws IOException
     */
    public static void saveExampleAt(Path destination) throws IOException {

        DataEntryList list = new DataEntryList();
        list.addComment("Titre et commentaires. Les coordonnées être représentées de plusieurs manières: 0.00, 0° 0.00', 0° 0' 0.00\".");

        int entries = 5;

        for (int i = 0; i < entries; i++) {

            DataEntry de = new DataEntry(new Coordinate(90, 180));
            de.addField("category", "square");
            de.addField("color", "red");
            de.addField("...", "...");

            list.add(de);
        }

        for (int i = 0; i < entries; i++) {

            DataEntry de = new DataEntry(new Coordinate(90, 180));
            de.addField("category", "ellipse");
            de.addField("color", "#123456");
            de.addField("...", "...");

            list.add(de);
        }

        CsvDataWriter writer = new CsvDataWriter();
        writer.write(list, destination);

    }

}
