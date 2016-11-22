package org.abcmap.core.imageanalyzer;

import com.labun.surf.IntegralImage;
import com.labun.surf.InterestPoint;
import com.labun.surf.Params;
import com.labun.surf.plugin.IJFacade;
import ij.ImagePlus;
import org.abcmap.core.managers.ConfigurationManager;
import org.abcmap.core.managers.MainManager;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by remipassmoilesel on 22/11/16.
 */
public class MatchablePointFactory {

    private final ConfigurationManager configm;
    private final Params surfConfig;
    private final Integer surfMode;

    public MatchablePointFactory() {
        this.configm = MainManager.getConfigurationManager();
        this.surfConfig = configm.getSurfConfiguration();
        this.surfMode = configm.getConfiguration().IMPORT_SURF_MODE;
    }

    public MatchablePointContainer getPointsContainer(BufferedImage img, String id) {

        MatchablePointContainer mpc = new MatchablePointContainer();
        mpc.setSurfMode(surfMode);
        mpc.setImageId(id);
        mpc.setPoints(getPointsList(img));

        return mpc;
    }

    /**
     * Process an image and return a list of matchable points
     *
     * @param img
     * @return
     */
    public ArrayList<MatchablePoint> getPointsList(BufferedImage img) {
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
    private ArrayList<MatchablePoint> surfAnalyse(IntegralImage img) {

        // maj parametre surf utilisés pour analyse
        Integer surfMode = configm.getConfiguration().IMPORT_SURF_MODE;

        // analyse et extraire les poijnts d'interet

        List<InterestPoint> ipts = IJFacade.detectAndDescribeInterestPoints(img, configm.getSurfConfiguration());
        ArrayList<MatchablePoint> mtpt = new ArrayList<>(ipts.size());

        for (InterestPoint p : ipts) {
            mtpt.add(new MatchablePoint((double) p.x, (double) p.y));
        }

        return mtpt;
    }


}
