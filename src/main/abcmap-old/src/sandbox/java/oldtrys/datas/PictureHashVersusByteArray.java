package oldtrys.datas;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import abcmap.utils.Utils;

public class PictureHashVersusByteArray {

	public static void main(String[] args) {

		try {
			BufferedImage image = ImageIO.read(new File("examples/map.jpg"));

			System.out.println("Taille du tableau de bytes: " + Utils.imageToByte(image).length);
			System.out.println("Taille du hash: " + Utils.getHashFromImage(image).length);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
