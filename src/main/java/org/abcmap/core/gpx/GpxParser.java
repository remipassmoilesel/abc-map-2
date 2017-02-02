package org.abcmap.core.gpx;

import org.abcmap.core.cancel.UndoableOperationWrapper;
import org.abcmap.core.gpx.schema.gpxv1.Gpx;
import org.abcmap.core.gpx.schema.gpxv1_1.*;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Parse a GPX file and return a container, or a a list of points
 */
public class GpxParser {

    private static final String GPX_V1 = "org.abcmap.core.gpx.schema.gpxv1";
    private static final String GPX_V1_1 = "org.abcmap.core.gpx.schema.gpxv1_1";
    private static final String[] availableFormats = new String[]{
            GPX_V1, GPX_V1_1
    };

    private static final CustomLogger logger = LogManager.getLogger(UndoableOperationWrapper.class);

    /**
     * Objects used to marshall GPX documents in XML
     */
    private final HashMap<String, Marshaller> marshallers;

    /**
     * Objects used to unmarshall XML in GPX documents
     */
    private final HashMap<String, Unmarshaller> unmarshallers;

    /**
     * Source from where GPX data is parsed
     * <p>
     * If this source is null, gpxSourcePath will be used
     */
    private InputStream gpxSource;

    /**
     * Path of a gpx file used as a source
     * <p>
     * This path is used only if gpxSource is null
     */
    private Path gpxSourcePath;

    /**
     * Produced raw document after parsing
     */
    private Object document;


    public GpxParser() {

        marshallers = new HashMap<>();
        unmarshallers = new HashMap<>();

        // create all marshallers needed
        try {

            for (String pkg : availableFormats) {
                JAXBContext jaxbContextGpxV0 = JAXBContext.newInstance(pkg);
                marshallers.put(pkg, jaxbContextGpxV0.createMarshaller());
                unmarshallers.put(pkg, jaxbContextGpxV0.createUnmarshaller());
            }

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

    }

    /**
     * Return a generic container of GPX points
     *
     * @return
     */
    public ArrayList<GpxPointsList> getPointsLists() {

        ArrayList<GpxPointsList> result = new ArrayList<>();

        if (document == null) {
            throw new NullPointerException("Please set a source and call parse() before get points");
        }

        // document version is GPX 1.0
        if (document instanceof Gpx) {
            Gpx gpxv1 = (Gpx) document;

            // parse routes
            for (Gpx.Rte route : gpxv1.getRte()) {
                GpxPointsList ctr = new GpxPointsList(GpxPointsList.Type.ROUTE);
                for (Gpx.Rte.Rtept pt : route.getRtept()) {
                    String time = pt.getTime() != null ? pt.getTime().toString() : null;
                    ctr.getPoints().add(new GpxPoint(pt.getLat(), pt.getLon(), pt.getEle(), time));
                }
                result.add(ctr);
            }

            // parse tracks
            for (Gpx.Trk track : gpxv1.getTrk()) {
                GpxPointsList ctr = new GpxPointsList(GpxPointsList.Type.TRACK);
                for (Gpx.Trk.Trkseg seg : track.getTrkseg()) {
                    for (Gpx.Trk.Trkseg.Trkpt pt : seg.getTrkpt()) {
                        String time = pt.getTime() != null ? pt.getTime().toString() : null;
                        ctr.getPoints().add(new GpxPoint(pt.getLat(), pt.getLon(), pt.getEle(), time));
                    }
                }
                result.add(ctr);
            }

        }

        // document version is GPX 1.1
        else if (document instanceof GpxType) {
            GpxType gpxv1_1 = (GpxType) document;

            // parse routes
            for (RteType route : gpxv1_1.getRte()) {
                GpxPointsList ctr = new GpxPointsList(GpxPointsList.Type.ROUTE);
                for (WptType pt : route.getRtept()) {
                    String time = pt.getTime() != null ? pt.getTime().toString() : null;
                    ctr.getPoints().add(new GpxPoint(pt.getLat(), pt.getLon(), pt.getEle(), time));
                }
                result.add(ctr);
            }

            // parse tracks
            for (TrkType track : gpxv1_1.getTrk()) {
                GpxPointsList ctr = new GpxPointsList(GpxPointsList.Type.TRACK);
                for (TrksegType seg : track.getTrkseg()) {
                    for (WptType pt : seg.getTrkpt()) {
                        String time = pt.getTime() != null ? pt.getTime().toString() : null;
                        ctr.getPoints().add(new GpxPoint(pt.getLat(), pt.getLon(), pt.getEle(), time));
                    }
                }
                result.add(ctr);
            }

            // parse waypoints
            GpxPointsList ctr = new GpxPointsList(GpxPointsList.Type.WAY_POINT);
            for (WptType pt : gpxv1_1.getWpt()) {
                String time = pt.getTime() != null ? pt.getTime().toString() : null;
                ctr.getPoints().add(new GpxPoint(pt.getLat(), pt.getLon(), pt.getEle(), time));
            }
            if (ctr.getPoints().size() > 0) {
                result.add(ctr);
            }

        }

        // unknown document
        else {
            throw new IllegalStateException("Unknown document: " + document);
        }

        return result;
    }

    /**
     * Set the GPX source as a file. This file will be read to parse GPX data.
     *
     * @param p
     * @throws IOException
     */
    public void setGpxSource(Path p) throws IOException {
        gpxSourcePath = p;
        gpxSource = null;
    }

    /**
     * Set the GPX source as a string. This XML string will be read to parse GPX data.
     *
     * @param xml
     * @throws IOException
     */
    public void setGpxSource(String xml) throws IOException {
        setGpxSource(new ByteArrayInputStream(xml.getBytes()));
    }

    /**
     * Set the GPX source as a stream. This stream will be read to parse GPX data.
     * <p>
     * This stream must support marks. File streams do not support marks, so use setGpxSource(File) instead
     *
     * @param stream
     * @throws IOException
     */
    public void setGpxSource(InputStream stream) throws IOException {

        if (stream.markSupported() == false) {
            throw new IOException("Invalid stream");
        }

        this.gpxSource = stream;
        this.gpxSourcePath = null;

        // prepare future resets
        gpxSource.mark(1);
    }

    /**
     * Throw an exception if soruce is null
     *
     * @throws GpxParsingException
     */
    private void testSource() throws GpxParsingException {
        if (gpxSource == null && gpxSourcePath == null) {
            throw new GpxParsingException("Source is null");
        }
    }

    /**
     * Parse the specified source
     *
     * @throws GpxParsingException
     */
    public void parse() throws GpxParsingException {

        testSource();

        try {
            this.document = parseV1();
        } catch (Exception e) {
            logger.error(e);
            try {
                this.document = parseV1_1();
            } catch (Exception e2) {
                throw new GpxParsingException(e2);
            }
        }
    }

    /**
     * Parse a GPX V1 document. If something wrong, an exception is thrown.
     *
     * @return
     * @throws GpxParsingException
     */
    private synchronized Gpx parseV1() throws GpxParsingException {

        testSource();

        // parse document and return it
        try {

            Gpx gpxDocument;

            // source is a stream
            if (gpxSource != null) {
                gpxSource.reset();
                gpxDocument = (Gpx) unmarshallers.get(GPX_V1).unmarshal(gpxSource);
            }

            // source is a file
            else {
                gpxDocument = (Gpx) unmarshallers.get(GPX_V1).unmarshal(gpxSourcePath.toFile());
            }

            return gpxDocument;

        } catch (Exception e) {
            throw new GpxParsingException(e);
        }

    }

    /**
     * Parse a GPX V1.1 document. If something wrong, an exception is thrown.
     *
     * @return
     * @throws GpxParsingException
     */
    private synchronized GpxType parseV1_1() throws GpxParsingException {

        testSource();

        // parse document and return it
        try {

            JAXBElement element;
            // source is a stream
            if (gpxSource != null) {
                gpxSource.reset();
                element = (JAXBElement) unmarshallers.get(GPX_V1_1).unmarshal(gpxSource);
            }

            // source is a file
            else {
                element = (JAXBElement) unmarshallers.get(GPX_V1_1).unmarshal(gpxSourcePath.toFile());
            }

            return (GpxType) element.getValue();
        } catch (Exception e) {
            throw new GpxParsingException(e);
        }

    }


}
