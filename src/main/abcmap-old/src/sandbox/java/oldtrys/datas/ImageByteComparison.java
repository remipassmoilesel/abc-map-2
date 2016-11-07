package oldtrys.datas;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import abcmap.utils.Utils;
import abcmap.utils.sandbox.ExecutionTimeComputing;

public class ImageByteComparison {

	private static BufferedImage imageToCompare;
	private static byte[] digestToCompare;
	private static byte[] byteArrayToCompare;
	private static String imagePath;

	// Comparaison des tableaux de byte
	// 50 essais.
	// Moyenne: 0.209 s.
	// Minimum: 0.204 s.
	// Maximum: 0.225 s.
	//
	// Comparaison des hash
	// 50 essais.
	// Moyenne: 0.215 s.
	// Minimum: 0.211 s.
	// Maximum: 0.236 s.

	public static void launch() {

		imagePath = "./examples/map_3.jpg";

		try {
			imageToCompare = ImageIO.read(new File(imagePath));
			digestToCompare = Utils.getHashFromImage(imageToCompare);
			byteArrayToCompare = Utils.imageToByte(imageToCompare);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		TimeComputingArea1 tca1 = new TimeComputingArea1();
		TimeComputingArea2 tca2 = new TimeComputingArea2();

		tca2.launchAndPrintResume();
		tca1.launchAndPrintResume();
	}

	private static class TimeComputingArea1 extends ExecutionTimeComputing {

		@Override
		protected void actionToMeasure() {
			try {
				byte[] digest2 = Utils.getHashFromImage(ImageIO.read(new File(imagePath)));
				Arrays.equals(digest2, digestToCompare);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private static class TimeComputingArea2 extends ExecutionTimeComputing {

		@Override
		protected void actionToMeasure() {
			try {
				byte[] byteArray = Utils.imageToByte(ImageIO.read(new File(imagePath)));
				Arrays.equals(byteArray, byteArrayToCompare);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}