package oldtrys.datas;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import abcmap.gpxparser.GPXParser;
import abcmap.gpxparser.beans.GPX;
import abcmap.gpxparser.beans.Route;
import abcmap.gpxparser.beans.Track;
import abcmap.gpxparser.beans.Waypoint;

public class GpxTest {

	public static void main(String[] args) {

		File[] files = new File[] { new File("examples/gpx/export.gpx"),
				new File("examples/gpx/export_2.gpx"), };

		GPXParser gpx = new GPXParser();

		for (int i = 0; i < files.length; i++) {

			File file = files[i];

			System.out.println();
			System.out.println(file.getAbsolutePath());

			try {
				GPX g = gpx.parseGPX(new FileInputStream(file));

				HashSet<Route> routes = g.getRoutes();
				if (routes != null)
					for (Route route : routes) {
						System.out.println(route);
						ArrayList<Waypoint> points = route.getRoutePoints();
						for (int j = 0; j < points.size(); j++) {
							System.out.println(points.get(i));
						}

					}

				HashSet<Waypoint> ways = g.getWaypoints();
				if (ways != null)
					for (Waypoint way : ways) {
						System.out.println(way);
					}

				HashSet<Track> tracks = g.getTracks();
				if (tracks != null)
					for (Track track : tracks) {
						System.out.println(track);
					}

			} catch (ParserConfigurationException | SAXException | IOException e) {
				e.printStackTrace();
			}

		}
	}
}
