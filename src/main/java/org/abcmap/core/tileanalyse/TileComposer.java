package org.abcmap.core.tileanalyse;

import com.labun.surf.InterestPoint;
import com.labun.surf.Matcher;
import com.labun.surf.Params;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.tiles.TileContainer;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.renderer.lite.RendererUtilities;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Return a list of IDs/Coordinates allowing to assemble images
 * <p>
 * All coordinates correspond to position of upper left corner of images, compared to the first image provided.
 */
public class TileComposer {

    private static final CustomLogger logger = LogManager.getLogger(TileComposer.class);

    /**
     * Object used to store and read interest points
     */
    private final InterestPointStorage iptsDao;

    /**
     * Number of common points between to images to reach,
     * <p>
     * to consider images are matching
     */
    private int pointThreshold;

    /**
     * SURF algorithm configuration
     */
    private Params surfConfig;

    /**
     * Unique ID of SURF configuration
     * <p>
     * SURF configurations need to match
     */
    private int surfConfigId;

    /**
     * Object used to create a factory
     */
    private InterestPointFactory iptsFactory;

    /**
     * Distance tolerance between two matching points. For example, one point is at 0.5 pixel,
     * other one is at 0.6 pixel, if tolerance is about 0.2, it is accepted as a matching point.
     */
    private double distanceTolerance;

    public TileComposer(Path interestPointDatabase, Params surfConfig, int pointThreshold, int surfModeId) throws IOException {
        this.pointThreshold = pointThreshold;
        this.surfConfig = surfConfig;
        this.surfConfigId = surfModeId;

        this.iptsFactory = new InterestPointFactory(surfConfig);
        this.iptsDao = new InterestPointStorage(interestPointDatabase);


        System.out.println("iptsDao");
        System.out.println(iptsDao);
        System.out.println(interestPointDatabase);

        this.distanceTolerance = 0.3;
    }

    /**
     * Return the position of specified image comapred to other image found in specified image source, in order to assemble them.
     *
     * @param tileToMove
     * @param source
     * @return
     * @throws IOException
     * @throws TileAnalyseException
     */
    public synchronized ReferencedEnvelope process(TileContainer tileToMove, TileSource source) throws IOException, TileAnalyseException {

        // reset source to iterate last tiles before
        source.reset();

        TileContainer currentCtr = source.next();

        // no image to analyse, throw exception
        if (currentCtr == null) {
            throw new TileAnalyseException("No tiles found in source");
        }

        // reference to interest point of tile to move
        InterestPoint tileToMoveIP = null;
        // reference to interest point of reference tile, the tile around
        InterestPoint referenceTileIP = null;

        // reference tile
        TileContainer referenceTile = null;

        double minDist = Double.MAX_VALUE;

        // else search image with enough common points
        searchReferenceTile:
        while (currentCtr != null) {

            // search common point between images
            Map<InterestPoint, InterestPoint> commonPoints = getCommonPoints(tileToMove, currentCtr);

            // not enough points, continue
            if (commonPoints.size() < pointThreshold) {
                continue;
            }

            // iterate common points to get matching positions
            for (InterestPoint ip1 : commonPoints.keySet()) {
                InterestPoint ip2 = commonPoints.get(ip1);

                int matching = 0;
                for (InterestPoint ipTest1 : commonPoints.keySet()) {
                    InterestPoint ipTest2 = commonPoints.get(ipTest1);

                    // compute distance between points
                    double dX1 = Math.abs(ipTest1.x - ip1.x);
                    double dY1 = Math.abs(ipTest1.y - ip1.y);
                    double dX2 = Math.abs(ipTest2.x - ip2.x);
                    double dY2 = Math.abs(ipTest2.y - ip2.y);

                    // if distance between points is < than 0.5, count as matching
                    double ttDist = Math.abs((dX1 + dY1) - (dX2 + dY2));

                    if (ttDist < minDist) {
                        minDist = ttDist;
                    }

                    // if distance between points is < than threshold, count as matching
                    if (ttDist < distanceTolerance) {
                        matching++;
                        if (matching >= pointThreshold) {

                            //System.out.println("commonPoints.size()");
                            //System.out.println(commonPoints.size());
                            //System.out.println("index: " + i);

                            referenceTile = currentCtr;
                            tileToMoveIP = ip1;
                            referenceTileIP = ip2;

                            break searchReferenceTile;
                        }
                    }

                }


            }

            currentCtr = source.next();

        }

        // no reference image have be found
        if (referenceTile == null) {
            throw new TileAnalyseException("Image cannot be assembled, no corresponding tiles have been found. Minimum distance: " + minDist);
        }

        Rectangle referenceTileImageRect = new Rectangle(0, 0, referenceTile.getImage().getWidth(), referenceTile.getImage().getHeight());
        Rectangle tileToMoveImageRect = new Rectangle(0, 0, tileToMove.getImage().getWidth(), tileToMove.getImage().getHeight());

        System.out.println();
        System.out.println("referenceTileImageRect");
        System.out.println(referenceTileImageRect);
        System.out.println("tileToMoveImageRect");
        System.out.println(tileToMoveImageRect);

        //
        // compute positions (1) of tile to move in source tile coordinate system (ULC and pixel units)
        //
        Point2D tileToMoveULC = new Point2D.Double(
                referenceTileIP.x - tileToMoveIP.x,
                referenceTileIP.y - tileToMoveIP.y);
        Point2D tileToMoveBRC = new Point2D.Double(
                tileToMoveULC.getX() + tileToMoveImageRect.getWidth(),
                tileToMoveULC.getY() + tileToMoveImageRect.getHeight());

        System.out.println("tileToMoveULC");
        System.out.println(tileToMoveULC);
        System.out.println("tileToMoveBRC");
        System.out.println(tileToMoveBRC);

        // create a screen to world transform (2) to transform points from source tile coordinate system to world coordinate system
        AffineTransform referenceTileTransform = RendererUtilities.worldToScreenTransform(referenceTile.getArea(), referenceTileImageRect);

        System.out.println("referenceTileTransform");
        System.out.println(referenceTileTransform);

        try {
            referenceTileTransform.invert();
        } catch (NoninvertibleTransformException e) {
            throw new TileAnalyseException("Unable to transform point from image coordinate space to world coordinate space");
        }

        System.out.println("referenceTileTransform");
        System.out.println(referenceTileTransform);

        // Transform positions (1) with transform (2)
        Point2D tileToMoveBLC = referenceTileTransform.transform(tileToMoveULC, null);
        Point2D tileToMoveURC = referenceTileTransform.transform(tileToMoveBRC, null);

        System.out.println("tileToMoveBLC");
        System.out.println(tileToMoveBLC);
        System.out.println("tileToMoveURC");
        System.out.println(tileToMoveURC);

        // create a new envelope and return it
        ReferencedEnvelope result = new ReferencedEnvelope(
                tileToMoveBLC.getX(), tileToMoveURC.getX(),
                tileToMoveBLC.getY(), tileToMoveURC.getY(),
                referenceTile.getArea().getCoordinateReferenceSystem());

        System.out.println("result");
        System.out.println(result);
        System.out.println(result.getWidth());
        System.out.println(result.getHeight());

        return result;
    }

    /**
     * Return common points between two images
     *
     * @param ctr1
     * @param ctr2
     * @return
     * @throws IOException
     * @throws TileAnalyseException
     */
    private Map<InterestPoint, InterestPoint> getCommonPoints(TileContainer ctr1, TileContainer ctr2) throws IOException, TileAnalyseException {

        List<InterestPoint> ipts1 = getPointsFromContainer(ctr1);
        List<InterestPoint> ipts2 = getPointsFromContainer(ctr2);

        // interest points from container 1
        if (ipts1 == null || ipts1.size() <= 0) {
            throw new TileAnalyseException("No points found on: " + ctr1.getTileId());
        }
        if (ipts2 == null || ipts2.size() <= 0) {
            throw new TileAnalyseException("No points found on: " + ctr2.getTileId());
        }

        return Matcher.findMatches(ipts1, ipts2, true);

    }

    /**
     * Return interest points from storage or update them if they are invalid
     *
     * @param ctr
     * @return
     * @throws IOException
     */
    private List<InterestPoint> getPointsFromContainer(TileContainer ctr) throws IOException {

        // try to get points from database
        InterestPointContainer pointsCtr = (InterestPointContainer) iptsDao.readById(ctr.getTileId());

        // container does not exist, create it
        if (pointsCtr == null) {
            pointsCtr = new InterestPointContainer();
            pointsCtr.setTileId(ctr.getTileId());
            pointsCtr.setSurfConfigId(surfConfigId);

            ArrayList<InterestPoint> list = iptsFactory.getPointsList(ctr.getImage());
            pointsCtr.setPoints(list);

            iptsDao.create(pointsCtr);

            return list;
        }

        // points invalid, refresh it
        else if (pointsCtr.getPoints() == null || pointsCtr.getPoints().size() < 1
                || pointsCtr.getSurfConfigId() != surfConfigId) {

            ArrayList<InterestPoint> list = iptsFactory.getPointsList(ctr.getImage());
            pointsCtr.setPoints(list);

            iptsDao.create(pointsCtr);

            return list;

        }

        // list is valid, return it
        else {
            return pointsCtr.getPoints();
        }

    }

}
