package org.abcmap.core.project.layers;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Polygon;
import org.abcmap.core.project.Project;
import org.abcmap.core.tiles.TileContainer;
import org.abcmap.core.tiles.TileFeatureBuilder;
import org.abcmap.core.tiles.TileStorage;
import org.abcmap.core.tiles.TileStorageQueries;
import org.abcmap.core.utils.FeatureUtils;
import org.abcmap.core.utils.GeoUtils;
import org.abcmap.core.utils.SQLUtils;
import org.geotools.coverage.grid.GeneralGridEnvelope;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridGeometry2D;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridFormatFinder;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.gce.imagemosaic.jdbc.Config;
import org.geotools.gce.imagemosaic.jdbc.ImageMosaicJDBCFormat;
import org.geotools.gce.imagemosaic.jdbc.ImageMosaicJDBCReader;
import org.geotools.gce.imagemosaic.jdbc.SpatialExtension;
import org.geotools.geometry.GeneralEnvelope;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.jdbc.JDBCDataStore;
import org.geotools.map.FeatureLayer;
import org.geotools.map.GridCoverageLayer;
import org.geotools.map.Layer;
import org.geotools.referencing.CRS;
import org.geotools.styling.*;
import org.geotools.styling.Stroke;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterValue;
import org.opengis.referencing.FactoryException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Layer where we can store tiles with absolute positioning.
 * <p>
 * This object wrap two layers:
 * - The effective coverage layer, with tiles
 * - A feature layer with draw to do geometric researches
 */
public class TileLayer extends AbstractLayer {

    /**
     * Tile store, where are actually stored tiles
     */
    private final TileStorage tileStorage;
    private final SimpleFeatureStore featureStore;
    private final TileFeatureBuilder featureBuilder;

    /**
     * Name of current coverage, uppercase mandatory
     */
    private final String coverageName;
    private final FeatureLayer outlineLayer;
    private final Style outlineStyle;
    private FeatureTypeStyle outlineFeatureType;

    public TileLayer(String layerId, String title, boolean visible, int zindex, Project owner, boolean create) throws IOException {
        this(new LayerIndexEntry(layerId, title, visible, zindex, LayerType.TILES), owner, create);
    }

    public TileLayer(LayerIndexEntry entry, Project owner, boolean create) throws IOException {

        super(owner, entry);

        JDBCDataStore datastore = SQLUtils.getGeotoolsDatastoreFromH2(project.getDatabasePath());

        this.tileStorage = project.getTileStorage();

        // uppercase mandatory
        coverageName = entry.getLayerId().toUpperCase();

        // if true, create database entries
        if (create) {

            // create a tile storage entry
            tileStorage.createCoverageStorage(entry.getLayerId());

            // create an outline feature entry
            SimpleFeatureType type = TileFeatureBuilder.getTileFeatureType(entry.getLayerId(), owner.getCrs());

            datastore.createSchema(type);

        }

        this.featureStore = (SimpleFeatureStore) datastore.getFeatureSource(entry.getLayerId());

        // create a feature builder to create outlines
        this.featureBuilder = FeatureUtils.getTileFeatureBuilder(entry.getLayerId(), owner.getCrs());

        // outline layer, with an empty style
        this.outlineStyle = sf.createStyle();
        outlineStyle.featureTypeStyles().add(getOutlineFeatureTypeStyle());

        this.outlineLayer = new org.geotools.map.FeatureLayer(featureStore, outlineStyle);

        refreshCoverage();
    }

    /**
     * Refresh present coverage by building a new one.
     * <p>
     * This method should be called after very modification: add, move, ...
     *
     * @throws IOException
     */
    public void refreshCoverage() throws IOException {
        buildCoverageLayer(coverageName, GeoUtils.crsToString(project.getCrs()));
    }

    /**
     * Add a tile to this layer, and return its id.
     * <p>
     * Return null if an error occur
     * <p>
     * To see changes, call refreshCoverage()
     *
     * @param imagePath
     * @param position
     */
    public String addTile(Path imagePath, Coordinate position) {

        BufferedImage img = null;
        try {
            img = ImageIO.read(imagePath.toFile());
        } catch (IOException e) {
            logger.error(e);
            return null;
        }

        return addTile(img, position);
    }

    /**
     * Add a tile to this layer, and return its id.
     * <p>
     * Return null if an error occur
     * <p>
     * To see changes, call refreshCoverage()
     *
     * @param container
     */
    public String addTile(TileContainer container) {
        return addTile(container.getImage(), container.getPosition());
    }

    /**
     * Add a tile to this layer, and return its id.
     * <p>
     * Return null if an error occur
     * <p>
     * To see changes, call refreshCoverage()
     *
     * @param image
     * @param position
     */
    public String addTile(BufferedImage image, Coordinate position) {

        try {

            // add tile to tile storage
            String tileId = tileStorage.addTile(coverageName, image, position);

            // create outline shape
            int width = image.getWidth();
            int height = image.getHeight();
            Polygon outline = geom.createPolygon(new Coordinate[]{
                    position,
                    new Coordinate(position.x + width, position.y),
                    new Coordinate(position.x + width, position.y + height),
                    new Coordinate(position.x, position.y + height),
                    position
            });

            // add outline to feature layer
            featureStore.addFeatures(FeatureUtils.asList(featureBuilder.build(outline, tileId)));

            return tileId;
        } catch (IOException e) {
            logger.error(e);
        }
        return null;
    }

    /**
     * Remove tiles from layer
     * <p>
     * To see changes, call refreshCoverage()
     *
     * @param ids
     */
    public boolean removeTiles(List<String> ids) {

        try {

            // remove tile from storage
            tileStorage.deleteTiles(coverageName, ids);

            // remove outline from feature layer
            Filter filter = ff.equal(ff.property(TileFeatureBuilder.TILE_ID_ATTRIBUTE_NAME), ff.literal(ids.get(0)), true);
            for (int i = 1; i < ids.size(); i++) {
                String id = ids.get(i);
                filter = ff.or(filter, ff.equal(ff.property(TileFeatureBuilder.TILE_ID_ATTRIBUTE_NAME), ff.literal(id), true));
            }

            featureStore.removeFeatures(filter);

            return true;

        } catch (IOException e) {
            logger.error(e);
        }

        return false;

    }

    /**
     * Generate a JDBC mosaic configuration
     *
     * @return
     */
    private Config getConfiguration(Path databasePath, String coverageName, String crsCode) {

        // instantiate configuration
        Config config = new Config("org.abcmap." + coverageName + "_" + System.nanoTime());

        // General config
        config.setCoverageName(coverageName);
        config.setCoordsys(crsCode);
        // interpolation 1 = nearest neighbour, 2 = bipolar, 3 = bicubic
        config.setInterpolation(3);
        config.setIgnoreAxisOrder(false);
        config.setVerifyCardinality(false);

        // Database config
        config.setDstype("DBCP");
        config.setUsername("");
        config.setPassword("");
        config.setJdbcUrl(SQLUtils.getJdbcUrlForH2(databasePath));
        config.setDriverClassName("org.h2.Driver");
        config.setMaxActive(5);
        config.setMaxIdle(0);
        config.setSpatialExtension(SpatialExtension.fromString("universal"));

        // master table
        config.setMasterTable(TileStorageQueries.MASTER_TABLE_NAME);
        config.setCoverageNameAttribute(TileStorageQueries.COVERAGE_NAME_FIELD_NAME);
        config.setTileMinXAttribute(TileStorageQueries.MIN_X_FIELD_NAME);
        config.setTileMinYAttribute(TileStorageQueries.MIN_Y_FIELD_NAME);
        config.setTileMaxXAttribute(TileStorageQueries.MAX_X_FIELD_NAME);
        config.setTileMaxYAttribute(TileStorageQueries.MAX_Y_FIELD_NAME);
        config.setTileTableNameAtribute(TileStorageQueries.TILE_TABLE_NAME_FIELD_NAME);
        config.setSpatialTableNameAtribute(TileStorageQueries.SPATIAL_TABLE_NAME_FIELD_NAME);

        // tile table
        config.setBlobAttributeNameInTileTable(TileStorageQueries.TILE_DATA_FIELD_NAME);
        config.setKeyAttributeNameInTileTable(TileStorageQueries.TILE_ID_FIELD_NAME);

        // spatial table
        config.setKeyAttributeNameInSpatialTable(TileStorageQueries.TILE_ID_FIELD_NAME);
        config.setMinXAttribute(TileStorageQueries.MIN_X_FIELD_NAME);
        config.setMinYAttribute(TileStorageQueries.MIN_Y_FIELD_NAME);
        config.setMaxXAttribute(TileStorageQueries.MAX_X_FIELD_NAME);
        config.setMaxYAttribute(TileStorageQueries.MAX_Y_FIELD_NAME);
        config.setResXAttribute(TileStorageQueries.RES_X_FIELD_NAME);
        config.setResYAttribute(TileStorageQueries.RES_Y_FIELD_NAME);

        config.validateConfig();

        return config;
    }

    /**
     * @param coverageName
     * @param crsCode
     * @throws IOException
     */
    private Layer buildCoverageLayer(String coverageName, String crsCode) throws IOException {

        // retrieve appropriate configuation for jdbc plugin
        Config config = getConfiguration(project.getDatabasePath(), coverageName, crsCode);

        // get a coverage reader
        AbstractGridFormat format = GridFormatFinder.findFormat(config);
        ImageMosaicJDBCReader reader = (ImageMosaicJDBCReader) format.getReader(config, null);

        // setup coverage dimensions
        ParameterValue<GridGeometry2D> gg = AbstractGridFormat.READ_GRIDGEOMETRY2D.createValue();

        ReferencedEnvelope bounds = getBounds();

        double x1 = bounds.getMinX();
        double y1 = bounds.getMinY();

        double x2 = bounds.getMaxX();
        double y2 = bounds.getMaxY();

        double width = bounds.getWidth();
        double height = bounds.getHeight();

        // this object must be set with the lower left corner position and upper tight corner position, x > y order
        GeneralEnvelope envelope = new GeneralEnvelope(new double[]{x1, y1}, new double[]{x2, y2});

        try {
            envelope.setCoordinateReferenceSystem(CRS.decode(crsCode));
        } catch (FactoryException e) {
            throw new IOException("Invalid CRS code: " + crsCode, e);
        }

        // this object must be set with the dimensions of coverage and previous bounds in envelope
        gg.setValue(new GridGeometry2D(new GeneralGridEnvelope(new Rectangle(0, 0, (int) width, (int) height)), envelope));

        // transparent background
        final ParameterValue outTransp = ImageMosaicJDBCFormat.OUTPUT_TRANSPARENT_COLOR.createValue();
        outTransp.setValue(Color.WHITE);

        // params of readers cannot be null
        GeneralParameterValue[] params = new GeneralParameterValue[]{gg, outTransp};
        GridCoverage2D coverage = reader.read(params);

        // create a Geotools coverage layer and store it
        this.internalLayer = new GridCoverageLayer(coverage, GeoUtils.getDefaultRGBRasterStyle(coverage));

        return internalLayer;
    }

    /**
     * Return a feature layer containing draw that are outlines of real tiles.
     *
     * @return
     */
    public FeatureLayer getOutlineLayer() {
        return outlineLayer;
    }

    @Override
    public ReferencedEnvelope getBounds() {

        try {
            return tileStorage.computeCoverageBounds(coverageName);
        } catch (Exception e) {
            logger.error(e);
        }

        return null;
    }

    /**
     * Return the outline feature store
     *
     * @return
     */
    public SimpleFeatureStore getOutlineFeatureStore() {
        return featureStore;
    }

    public String getCoverageName() {
        return coverageName;
    }

    /**
     * Return default feature type style for outline layer
     *
     * @return
     */
    private static FeatureTypeStyle getOutlineFeatureTypeStyle() {

        Color foreground = Color.green;
        Color background = Color.yellow;

        // create point symbolizer
        Stroke stroke = sf.stroke(ff.literal(foreground), null, null, null, null, null, null);
        Fill fill = sf.fill(null, ff.literal(background), ff.literal(0.2));

        // create line symbolizer
        LineSymbolizer lineSym = sf.createLineSymbolizer(stroke, null);

        // create polygon symbolizer
        PolygonSymbolizer polygonSym = sf.createPolygonSymbolizer(stroke, fill, null);

        // create rule
        Rule r = sf.createRule();
        r.symbolizers().add(lineSym);
        r.symbolizers().add(polygonSym);
        r.setIsElseFilter(true);

        return sf.createFeatureTypeStyle(new Rule[]{r});
    }
}
