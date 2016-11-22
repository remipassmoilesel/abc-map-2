package org.abcmap.core.imageanalyzer;

import com.labun.surf.InterestPoint;
import ij.ImagePlus;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Created by remipassmoilesel on 22/11/16.
 */
public class MatchablePointFactory {

    /**
     * Return an image that ImageJ can process
     *
     * @param bimg
     * @return
     */
    private ImagePlus createImagePlus(BufferedImage bimg) {

        ImagePlus imp = new ImagePlus("", bimg);

        // TODO: Try with black and white image to save memory ?
        if (imp.getProcessor().getBitDepth() != ImagePlus.COLOR_RGB) {
            imp.getProcessor().convertToRGB();
            imp.updateAndDraw();
        }

        return imp;

    }

    public List<MatchablePoint> surfAnalyse(ImagePlus imp)  {

        // maj parametre surf utilisés pour analyse
        surfMode = new Integer(confm.getConfiguration().SURF_MODE);

        // analyse et extraire les poijnts d'interet
        IntegralImage intImg = new IntegralImage(img.getProcessor(), true);
        ipts = IJFacade.detectAndDescribeInterestPoints(intImg, importm.getSurfParameters());

        // serialisation des points
        serializePointsSafeMode();

        return ipts;
    }
}
