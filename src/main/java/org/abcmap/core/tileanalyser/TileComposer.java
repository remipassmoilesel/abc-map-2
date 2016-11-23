package org.abcmap.core.tileanalyser;

import com.labun.surf.InterestPoint;
import com.labun.surf.Matcher;
import com.labun.surf.Params;
import com.vividsolutions.jts.geom.Coordinate;
import org.abcmap.core.project.tiles.TileContainer;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.List;

/**
 * Return a list of IDs/Coordinates allowing to assemble images
 * <p>
 * All coordinates correspond to position of upper left corner of images, compared to the first image provided.
 */
public class TileComposer {

    private final InterestPointStorage iptsDao;
    private int pointThreshold;
    private Params surfConfig;
    private int surfConfigId;
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
    public synchronized Coordinate process(TileContainer tileToMove, TileSource source) throws IOException, TileAnalyseException {

        // reset source to iterate last tiles before
        source.reset();

        TileContainer currentCtr = source.next();

        // no image to analyse, throw exception
        if (currentCtr == null) {
            throw new TileAnalyseException("No tiles found in source");
        }

        // reference interest point of tile to insert
        InterestPoint insertedTileIP = null;
        // reference interest point of reference tile
        InterestPoint referenceTileIP = null;

        // reference tile
        TileContainer referenceTile = null;

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
                int i = 0;
                for (InterestPoint ipTest1 : commonPoints.keySet()) {
                    InterestPoint ipTest2 = commonPoints.get(ipTest1);

                    double dX1 = Math.abs(ipTest1.x - ip1.x);
                    double dY1 = Math.abs(ipTest1.y - ip1.y);
                    double dX2 = Math.abs(ipTest2.x - ip2.x);
                    double dY2 = Math.abs(ipTest2.y - ip2.y);

                    // if distance between points is < than 0.5, count as matching
                    double ttDist = Math.abs((dX1 + dY1) - (dX2 + dY2));
                    if (ttDist < distanceTolerance) {
                        matching++;
                        if (matching >= pointThreshold) {

                            //System.out.println("commonPoints.size()");
                            //System.out.println(commonPoints.size());
                            //System.out.println("index: " + i);

                            referenceTile = currentCtr;
                            insertedTileIP = ip1;
                            referenceTileIP = ip2;

                            break searchReferenceTile;
                        }
                    }

                    i++;
                }


            }

            currentCtr = source.next();

        }

        // no reference image have be found
        if (referenceTile == null) {
            throw new TileAnalyseException("Image cannot be assembled");
        }

        // position BLC on map of tile
        Coordinate refTilePosition = referenceTile.getPosition();

        // height of reference tile
        int referenceTileHeight = referenceTile.getImage().getHeight();

        // position BLC of reference interest point on tile
        Coordinate relativeRefIP = new Coordinate(referenceTileIP.x, referenceTileHeight - referenceTileIP.y);

        // absolute position of reference point
        Coordinate absoluteRefPoint = new Coordinate(refTilePosition.x + relativeRefIP.x, refTilePosition.y + relativeRefIP.y);

        // position BLC of interest point of tile to insert
        int insertedTileHeight = tileToMove.getImage().getHeight();
        Coordinate blcInsertedTileInterestPoint = new Coordinate(insertedTileIP.x, insertedTileHeight - insertedTileIP.y);

        Coordinate result = new Coordinate(absoluteRefPoint.x - blcInsertedTileInterestPoint.x,
                absoluteRefPoint.y - blcInsertedTileInterestPoint.y);

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
