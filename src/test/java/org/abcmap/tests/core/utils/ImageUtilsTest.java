package org.abcmap.tests.core.utils;

import org.abcmap.core.utils.Utils;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.IOException;

import static junit.framework.TestCase.assertTrue;

/**
 * Created by remipassmoilesel on 23/11/16.
 */
public class ImageUtilsTest {

    @Test
    public void tests() throws IOException, InterruptedException {

        String root = "/pictures/belledonne_";

        for (int i = 0; i < 4; i++) {


            String imgPath = root + (i + 1) + ".jpg";
            BufferedImage img = ImageIO.read(ImageUtilsTest.class.getResourceAsStream(imgPath));

            assertTrue("Image reading: " + imgPath, img != null);

            byte[] bytes = Utils.imageToByte(img);

            assertTrue("Image to byte util: " + imgPath, bytes != null);

            BufferedImage img2 = Utils.bytesToImage(bytes);

            assertTrue("Bytes to image util: " + imgPath, img2 != null);

            //GuiUtils.showImage(img);
            //GuiUtils.showImage(img2);
            //Thread.sleep(50000);

            Raster r1 = img.getData();
            Raster r2 = img2.getData();

            // compare random pixels
            for (int j = 0; j < 20; j += 8) {
                assertTrue("Image comparison test", r1.getPixel(j, j, (int[]) null)[0] == r2.getPixel(j, j, (int[]) null)[0]);
                assertTrue("Image comparison test", r1.getPixel(j, j, (int[]) null)[1] == r2.getPixel(j, j, (int[]) null)[1]);
                assertTrue("Image comparison test", r1.getPixel(j, j, (int[]) null)[2] == r2.getPixel(j, j, (int[]) null)[2]);
            }

            //ImageIO.write(img, "png", new File("test1.png"));
            //ImageIO.write(img2, "png", new File("test2.png"));
        }


    }

}
