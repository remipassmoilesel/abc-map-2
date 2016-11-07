package oldtrys.datas;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import abcmap.utils.Utils;

public class ResizeImage {

	public static void main(String[] args) {

		BufferedImage img = null;
		try {
			img = ImageIO.read(new File("./examples/map.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Dimensions originales: ");
		System.out.println("img.getWidth()");
		System.out.println(img.getWidth());
		System.out.println("img.getHeight()");
		System.out.println(img.getHeight());

		BufferedImage newImage = Utils.scaleImage(img, 0.5f);

		System.out.println("Dimensions à 0.5f: ");
		System.out.println("img.getWidth()");
		System.out.println(newImage.getWidth());
		System.out.println("img.getHeight()");
		System.out.println(newImage.getHeight());

		BufferedImage newImage2 = Utils.scaleImage(img, 2000, 1000);

		System.out.println("Dimensions avec max: ");
		System.out.println("img.getWidth()");
		System.out.println(newImage2.getWidth());
		System.out.println("img.getHeight()");
		System.out.println(newImage2.getHeight());
	}
}
