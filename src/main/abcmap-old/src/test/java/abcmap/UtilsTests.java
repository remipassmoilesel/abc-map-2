package abcmap;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import abcmap.utils.Utils;

public class UtilsTests {

	@Test
	public void loremIpsumTest() {

		// generer du lorem ipsum
		assertEquals(Utils.generateLoremIpsum(50).length(), 50);

		// conversion pixel / mm: 1000px a 300dpi -> 84.66 mm
		assertEquals(Utils.pixelToMillimeter(1000, 300), 84.66d, 0.01d);

		// conversion mm / pixel: 100mm a 300dpi -> 1181.10 px
		assertEquals(Utils.millimeterToPixel(100, 300), 1181.10d, 0.01d);

	}

}
