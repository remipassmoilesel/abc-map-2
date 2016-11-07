package abcmap.geo;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

public class CoordinatesTest {

	@Test
	public void coordinatesTest() {

		// test d'égalité
		Coordinate c1 = new Coordinate(52.0, 55.5, 1000, 800);
		Coordinate c2 = new Coordinate(50.0, 58.5, 100, 700);

		Assert.assertEquals(c1, c1);
		Assert.assertNotEquals(c1, c2);

		Assert.assertEquals(c1, new Coordinate(c1));

		// tests de conversion DD -> DMS
		Coordinate c3 = new Coordinate(50.59028, 0, 0, 0);
		double[] expected = new double[] { 50, 35, 25, 0, 0, 0 };

		Assert.assertTrue(Arrays.equals(expected, c3.getDMSCoords()));

		// tests de parsage

		Assert.assertNull(Coordinate.valueOf("abcd", "efgh"));

		// D.d 55.555 == DM.m 55 33.3 == DMS 55 33 18

		String[] strs = new String[] {
				//
				"55.555",
				//
				"55.555°",
				//
				"55° 33.3\'",
				//
				"55° 33' 18\"" };

		Coordinate expcCoords = new Coordinate(55.555, 55.555, 0, 0);

		for (String s : strs) {
			Coordinate c = Coordinate.valueOf(s, s);
			Assert.assertEquals("Parsing: " + s, expcCoords, c);
		}

	}
}
