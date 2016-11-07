package abcmap.importation.data.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import abcmap.exceptions.DataImportException;
import abcmap.gpxparser.GPXParser;
import abcmap.gpxparser.beans.GPX;
import abcmap.gpxparser.beans.Route;
import abcmap.gpxparser.beans.Track;
import abcmap.gpxparser.beans.Waypoint;
import abcmap.importation.data.DataEntry;
import abcmap.importation.data.DataEntryList;
import abcmap.managers.Log;

public class GpxDataParser extends AbstractDataParser {

	private static String[] supportedExtensions = new String[] { "gpx" };
	private static List<String> headers = Arrays.asList(LABEL_LATITUDE, LABEL_LONGITUDE, LABEL_TYPE,
			LABEL_TIME, LABEL_ELEVATION, LABEL_NAME, LABEL_COMMENT, LABEL_DESCRIPTION);

	@Override
	public String[] getSupportedExtensions() {
		return supportedExtensions;
	}

	@Override
	public boolean isSupportedExtension(String extension) {
		return Arrays.asList(supportedExtensions).contains(extension);
	}

	@Override
	public ArrayList<String> getHeaders(File file) throws IOException, DataImportException {
		return new ArrayList<String>(headers);
	}

	@Override
	public DataEntryList parseFile(File file) throws IOException, DataImportException {

		// parser le fichier
		GPXParser gpx = new GPXParser();
		GPX g;
		try {
			g = gpx.parseGPX(new FileInputStream(file));
		} catch (ParserConfigurationException | SAXException e) {
			Log.error(e);
			throw new DataImportException(DataImportException.INVALID_GPX_FORMAT);
		}

		// liste résultat
		DataEntryList rslt = new DataEntryList();

		// beaucoup de verifications sont faites pour eviter les nullpointex car
		// les accesseurs
		// retournent nulle si pas de données

		// compter les import
		int imported = 0;

		// extraire les routes
		HashSet<Route> routes = g.getRoutes();
		if (routes != null) {
			for (Route r : routes) {
				ArrayList<Waypoint> points = r.getRoutePoints();
				if (points != null) {

					if (r.getComment() != null) {
						rslt.addComment(r.getComment());
					}

					if (r.getDescription() != null) {
						rslt.addComment(r.getDescription());
					}

					for (Waypoint p : points) {
						rslt.add(getEntryFrom(p, GPX_TYPE_ROUTE));

						if (imported > MAX_DATA_PARSING) {
							throw new DataImportException(DataImportException.DATAS_TOO_HEAVY);
						}

						imported++;
					}
				}
			}
		}

		// extraire les tracks
		HashSet<Track> tracks = g.getTracks();
		if (tracks != null) {
			for (Track t : tracks) {

				ArrayList<Waypoint> points = t.getTrackPoints();
				if (points != null) {

					if (t.getComment() != null) {
						rslt.addComment(t.getComment());
					}

					if (t.getDescription() != null) {
						rslt.addComment(t.getDescription());
					}

					for (Waypoint p : points) {
						rslt.add(getEntryFrom(p, GPX_TYPE_ROUTE));

						if (imported > MAX_DATA_PARSING) {
							throw new DataImportException(DataImportException.DATAS_TOO_HEAVY);
						}

						imported++;
					}
				}
			}
		}

		// extraire les waypoint
		HashSet<Waypoint> ways = g.getWaypoints();
		if (ways != null) {
			for (Iterator<Waypoint> iterator = ways.iterator(); iterator.hasNext();) {
				Waypoint waypoint = iterator.next();

				rslt.add(getEntryFrom(waypoint, GPX_TYPE_WAYPOINT));

				if (imported > MAX_DATA_PARSING) {
					throw new DataImportException(DataImportException.DATAS_TOO_HEAVY);
				}

				imported++;
			}
		}

		return rslt;
	}

	private DataEntry getEntryFrom(Waypoint p, String type) {

		DataEntry de = new DataEntry();

		// latitude et longitude
		de.setCoords(p.getLatitude(), p.getLongitude());

		// le reste des données, inséré dans l'ordre des headers
		// LABEL_LATITUDE, LABEL_LONGITUDE, LABEL_TYPE, LABEL_TIME,
		// LABEL_ELEVATION, LABEL_NAME, LABEL_COMMENT, LABEL_DESCRIPTION);
		Object[] values = new Object[] { null, null, type, p.getTime(), p.getElevation(),
				p.getName(), p.getComment(), p.getDescription(), };

		for (int i = 2; i < headers.size(); i++) {
			Object v = values[i];
			if (v != null) {
				de.addField(headers.get(i), v.toString());
			}
		}

		return de;
	}

}
