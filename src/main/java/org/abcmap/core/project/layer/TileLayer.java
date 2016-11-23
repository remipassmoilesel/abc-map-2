package org.abcmap.core.project.layer;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Polygon;
import org.abcmap.core.project.tiles.TileContainer;
import org.abcmap.core.project.tiles.TileStorage;
import org.abcmap.core.project.tiles.TileStorageQueries;
import org.abcmap.core.shapes.feature.DefaultFeatureBuilder;
import org.abcmap.core.shapes.feature.TileFeatureBuilder;
import org.abcmap.core.utils.FeatureUtils;
import org.abcmap.core.utils.GeoUtils;
import org.abcmap.core.utils.SQLUtils;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridEnvelope2D;
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
 * - A feature layer with shapes to do geometric researches
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
    private final Path databasePath;
    private final Style outlineStyle;
    private FeatureTypeStyle outlineFeatureType;

    public TileLayer(String layerId, String title, boolean visible, int zindex, Path databasePath, boolean create) throws IOException {
        this(new LayerIndexEntry(layerId, title, visible, zindex, LayerType.TILES), databasePath, create);
    }

    public TileLayer(LayerIndexEntry entry, Path databasePath, boolean create) throws IOException {

        super(entry);

        JDBCDataStore datastore = SQLUtils.getDatastoreFromH2(databasePath);

        this.databasePath = databasePath;
        this.tileStorage = pman.getProject().getTileStorage();

        // uppercase mandatory
        coverageName = entry.getLayerId().toUpperCase();

        // if true, create database entries
        if (create) {

            // create a tile storage entry
            tileStorage.createCoverageStorage(entry.getLayerId());

            // create an outline feature entry
            SimpleFeatureType type = TileFeatureBuilder.getTileFeatureType(entry.getLayerId(), this.crs);

            datastore.createSchema(type);

        }

        this.featureStore = (SimpleFeatureStore) datastore.getFeatureSource(entry.getLayerId());

        // create a feature builder to create outlines
        this.featureBuilder = FeatureUtils.getTileFeatureBuilder(entry.getLayerId(), crs);

        // outline layer, with an empty style
        this.outlineStyle = sf.createStyle();
        outlineStyle.featureTypeStyles().add(getOutlineFeatureType());

        this.outlineLayer = new org.geotools.map.FeatureLayer(featureStore, outlineStyle);

        refreshCoverage();
    }

    public void refreshCoverage() throws IOException {
        buildCoverageLayer(databasePath, coverageName, crsCode);
    }

    /**
     * Add a tile to this layer, and return its id.
     * <p>
     * Return null if an error occur
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
        config.setJdbcUrl("jdbc:h2:file:" + databasePath.toString());
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
     * @param databasePath
     * @param coverageName
     * @param crsCode
     * @throws IOException
     */
    private Layer buildCoverageLayer(Path databasePath, String coverageName, String crsCode) throws IOException {

        Config config = getConfiguration(databasePath, coverageName, crsCode);

        AbstractGridFormat format = GridFormatFinder.findFormat(config);
        ImageMosaicJDBCReader reader = (ImageMosaicJDBCReader) format.getReader(config, null);

        // get a parameter object for a grid geometry
        ParameterValue<GridGeometry2D> gg = AbstractGridFormat.READ_GRIDGEOMETRY2D.createValue();

        // create an envelope, 2 Points, lower left and upper right, x,y order
        GeneralEnvelope envelope = new GeneralEnvelope(new double[]{0, 0}, new double[]{3000, 3000});

        // set a CRS for the envelope
        try {
            envelope.setCoordinateReferenceSystem(CRS.decode(crsCode));
        } catch (FactoryException e) {
            throw new IOException("Invalid CRS code: " + crsCode, e);
        }

        // Set the envelope into the parameter object
        int width = 3000;
        int heigth = 3000;

        // to check: GridEnvelope2D was GeneralGridRange (unavailable)
        gg.setValue(new GridGeometry2D(new GridEnvelope2D(new Rectangle(0, 0, width, heigth)), envelope));

        // transparent background
        final ParameterValue outTransp = ImageMosaicJDBCFormat.OUTPUT_TRANSPARENT_COLOR.createValue();
        outTransp.setValue(Color.WHITE);

        // params of readers cannot be null
        GeneralParameterValue[] params = new GeneralParameterValue[]{gg, outTransp};
        GridCoverage2D coverage = reader.read(params);

        this.internalLayer = new GridCoverageLayer(coverage, GeoUtils.getDefaultRGBRasterStyle(coverage));

        return internalLayer;
    }

    /**
     * Return a feature layer containing shapes that are outlines of real tiles.
     *
     * @return
     */
    public FeatureLayer getOutlineLayer() {
        return outlineLayer;
    }

    @Override
    public ReferencedEnvelope getBounds() {
        throw new IllegalStateException("Not implemented for now");
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

    private static FeatureTypeStyle getOutlineFeatureType() {

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
