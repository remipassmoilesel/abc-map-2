package oldtrys.geo;

import abcmap.geo.Coordinate;

public class CoordinateParsing {

	public static void main(String[] args) {

		String lat1 = "46.11°";
		String lng1 = "005.111°";

		System.out.println();
		System.out.println(Coordinate.valueOf(lat1, lng1));

		String lat2 = "46°11.0000'";
		String lng2 = "005°37.0000'";

		System.out.println();
		System.out.println(Coordinate.valueOf(lat2, lng2));

		String lat3 = "46°11'16.0000\"";
		String lng3 = "005°43'37.0000\"";

		System.out.println();
		System.out.println(Coordinate.valueOf(lat3, lng3));

	}
}
