package org.abcmap.core.importation.data.reader;

import com.vividsolutions.jts.geom.Coordinate;
import org.abcmap.core.importation.gpx.GpxParser;
import org.abcmap.core.importation.gpx.GpxPoint;
import org.abcmap.core.importation.gpx.GpxPointsList;
import org.abcmap.core.importation.data.DataEntry;
import org.abcmap.core.importation.data.DataEntryList;
import org.abcmap.core.importation.data.DataImportException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GpxDataParser extends AbstractDataReader {

    private static List<String> supportedExtensions = Arrays.asList("gpx");
    private static List<String> headers = Arrays.asList(LABEL_LATITUDE, LABEL_LONGITUDE, LABEL_TYPE,
            LABEL_TIME, LABEL_ELEVATION, LABEL_DESCRIPTION);

    @Override
    public List<String> getSupportedExtensions() {
        return supportedExtensions;
    }

    @Override
    public boolean isSupportedExtension(String extension) {
        return Arrays.asList(supportedExtensions).contains(extension);
    }

    @Override
    public ArrayList<String> getHeaders(Path p) throws IOException, DataImportException {
        // all headers are the same
        return new ArrayList<>(headers);
    }

    @Override
    public DataEntryList parseFile(Path p) throws IOException, DataImportException {

        // try to parse file
        GpxParser parser = new GpxParser();
        try {
            parser.setGpxSource(p);
            parser.parse();
        } catch (Exception e) {
            throw new IOException(e);
        }

        DataEntryList result = new DataEntryList();
        ArrayList<GpxPointsList> availableTracks = parser.getPointsLists();
        for (GpxPointsList list : availableTracks) {
            for (GpxPoint pt : list.getPoints()) {
                DataEntry de = new DataEntry(new Coordinate(pt.getLatitude(), pt.getLongitude()));
                de.addField(LABEL_TYPE, list.getType().toString());
                de.addField(LABEL_TIME, pt.getTime());
                de.addField(LABEL_ELEVATION, String.valueOf(pt.getElevation()));
                de.addField(LABEL_DESCRIPTION, pt.getDescription());

                result.add(de);
            }
        }

        return result;
    }

}
