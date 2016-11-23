package org.abcmap.core.tileanalyser;

import com.labun.surf.IntegralImage;
import com.labun.surf.InterestPoint;
import com.labun.surf.Params;
import com.labun.surf.plugin.IJFacade;
import ij.ImagePlus;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Created by remipassmoilesel on 22/11/16.
 */
public class InterestPointFactory {

    private Params surfConfig;

    public InterestPointFactory(Params surfConfig) {
        this.surfConfig = surfConfig;
    }

    /**
     * Process an image and return a list of matchable points
     *
     * @param img
     * @return
     */
    public ArrayList<InterestPoint> getPointsList(BufferedImage img) {
        IntegralImage itimg = createIntegralImage(img);
        return surfAnalyse(itimg);
    }

    /**
     * Return an image that ImageJ can process
     *
     * @param bimg
     * @return
     */
    private IntegralImage createIntegralImage(BufferedImage bimg) {

        ImagePlus imp = new ImagePlus("", bimg);

        // TODO: Try with black and white image to save memory ?
        if (imp.getProcessor().getBitDepth() != ImagePlus.COLOR_RGB) {
            imp.getProcessor().convertToRGB();
            imp.updateAndDraw();
        }

        return new IntegralImage(imp.getProcessor(), true);
    }

    /**
     * Search interest points and return as a list
     *
     * @param img
     * @return
     */
    private ArrayList<InterestPoint> surfAnalyse(IntegralImage img) {
        ArrayList<InterestPoint> list = new ArrayList<InterestPoint>();
        list.addAll(IJFacade.detectAndDescribeInterestPoints(img, surfConfig));
        return list;
    }


}
