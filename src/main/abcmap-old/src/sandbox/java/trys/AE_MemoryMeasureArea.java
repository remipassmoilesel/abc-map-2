package trys;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import abcmap.utils.sandbox.MemoryChargeComputing;

public class AE_MemoryMeasureArea extends MemoryChargeComputing {

	public static void launch() {
		AE_MemoryMeasureArea tca = new AE_MemoryMeasureArea();
		tca.launchAndPrintAll();
	}

	@Override
	protected void actionToMeasure() {
		try {
			BufferedImage image = ImageIO.read(new File("examples/map.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
