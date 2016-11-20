package org.abcmap.core.project.layer;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.Polygon;
import org.abcmap.core.project.tiles.TileStorage;
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
import org.geotools.data.store.ContentFeatureSource;
import org.geotools.gce.imagemosaic.jdbc.ImageMosaicJDBCFormat;
import org.geotools.gce.imagemosaic.jdbc.ImageMosaicJDBCReader;
import org.geotools.gce.imagemosaic.jdbc.SpatialExtension;
import org.geotools.geometry.GeneralEnvelope;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.geopkg.FeatureEntry;
import org.geotools.geopkg.GeoPackage;
import org.geotools.jdbc.JDBCDataStore;
import org.geotools.map.*;
import org.geotools.map.FeatureLayer;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterValue;
import org.geotools.gce.imagemosaic.jdbc.Config;
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
    private final ContentFeatureSource featureSource;
    private final SimpleFeatureStore featureStore;
    private final TileFeatureBuilder featureBuilder;
    private final String coverageName;
    private final FeatureLayer outlineLayer;

    public TileLayer(String layerId, String title, boolean visible, int zindex, GeoPackage geopkg, boolean create) throws IOException {
        this(new LayerIndexEntry(layerId, title, visible, zindex, LayerType.TILES), geopkg, create);
    }

    public TileLayer(LayerIndexEntry entry, GeoPackage geopkg, boolean create) throws IOException {

        super(entry);

        tileStorage = pman.getProject().getTileStorage();
        coverageName = entry.getLayerId();

        // if true, create database entries
        if (create) {

            // create a tile storage entry
            tileStorage.addCoverage(entry.getLayerId());

            // create an outline feature entry
            SimpleFeatureType type = TileFeatureBuilder.getTileFeatureType(entry.getLayerId(), this.crs);
            FeatureEntry fe = new FeatureEntry();
            fe.setBounds(new ReferencedEnvelope());

            // create a geopackage entry
            geopkg.create(fe, type);

        }

        JDBCDataStore datastore = SQLUtils.getDatastoreFromGeopackage(geopkg.getFile().toPath());
        this.featureSource = datastore.getFeatureSource(entry.getLayerId());
        this.featureStore = (SimpleFeatureStore) featureSource;

        // create a feature builder to create outlines
        this.featureBuilder = FeatureUtils.getTileFeatureBuilder(entry.getLayerId(), crs);

        // outline layer, with an empty style
        this.outlineLayer = new org.geotools.map.FeatureLayer(featureSource, sf.createStyle());

        //createCoverageLayer(geopkg.getFile().toPath(), coverageName, crsCode);
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

            System.out.println(filter);

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
        config.setInterpolation(1);
        config.setIgnoreAxisOrder(false);
        config.setVerifyCardinality(false);

        // Database config
        config.setDstype("DBCP");
        config.setUsername("");
        config.setPassword("");
        config.setJdbcUrl("jdbc:sqlite:" + databasePath.toString());
        config.setDriverClassName("org.sqlite.JDBC");
        config.setMaxActive(5);
        config.setMaxIdle(0);
        config.setSpatialExtension(SpatialExtension.fromString("universal"));

        // master table
        config.setMasterTable(TileStorage.MASTER_TABLE_NAME);
        config.setCoverageNameAttribute(TileStorage.COVERAGE_NAME_FIELD_NAME);
        config.setTileMinXAttribute(TileStorage.MIN_X_FIELD_NAME);
        config.setTileMinYAttribute(TileStorage.MIN_Y_FIELD_NAME);
        config.setTileMaxXAttribute(TileStorage.MAX_X_FIELD_NAME);
        config.setTileMaxYAttribute(TileStorage.MAX_Y_FIELD_NAME);
        config.setTileTableNameAtribute(TileStorage.TILE_TABLE_NAME_FIELD_NAME);
        config.setSpatialTableNameAtribute(TileStorage.SPATIAL_TABLE_NAME_FIELD_NAME);

        // tile table
        config.setBlobAttributeNameInTileTable(TileStorage.TILE_DATA_FIELD_NAME);
        config.setKeyAttributeNameInTileTable(TileStorage.TILE_ID_FIELD_NAME);

        // spatial table
        config.setKeyAttributeNameInSpatialTable(TileStorage.TILE_ID_FIELD_NAME);
        config.setMinXAttribute(TileStorage.MIN_X_FIELD_NAME);
        config.setMinYAttribute(TileStorage.MIN_Y_FIELD_NAME);
        config.setMaxXAttribute(TileStorage.MAX_X_FIELD_NAME);
        config.setMaxYAttribute(TileStorage.MAX_Y_FIELD_NAME);
        config.setResXAttribute(TileStorage.RES_X_FIELD_NAME);
        config.setResYAttribute(TileStorage.RES_Y_FIELD_NAME);

        config.validateConfig();

        return config;
    }

    /**
     * @param databasePath
     * @param coverageName
     * @param crsCode
     * @throws IOException
     */
    private Layer createCoverageLayer(Path databasePath, String coverageName, String crsCode) throws IOException {

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

        internalLayer = new GridCoverageLayer(coverage, GeoUtils.getDefaultRGBRasterStyle(reader, new GeneralParameterValue[]{gg, outTransp}));

        return internalLayer;
    }


    @Override
    public ReferencedEnvelope getBounds() {
        throw new IllegalStateException("Not implemented for now");
    }
}
