package org.abcmap.core.project;

import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.utils.CRSUtils;
import org.abcmap.core.utils.FeatureUtils;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.data.store.ContentFeatureSource;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.geopkg.FeatureEntry;
import org.geotools.geopkg.GeoPackage;
import org.geotools.jdbc.JDBCDataStore;
import org.geotools.map.MapContent;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Represent a serializable project. Projects are stored in Geopakages, some kind of sqlite
 * database. All layers are read directly on the database.
 */
public class Project {

    private static final CustomLogger logger = LogManager.getLogger(Project.class);

    private GeoPackage geopkg;
    private ArrayList<Layer> layers;
    private MapContent mainMapContent;
    private ProjectMetadata metadata;
    private Path tempDatabasePath;
    private CoordinateReferenceSystem crs;

    /**
     * Create a new project associated with the database at specifed location
     *
     * @param p
     * @throws IOException
     */
    public Project(Path p) throws IOException {

        // create database
        tempDatabasePath = p;
        geopkg = new GeoPackage(p.toFile());
        geopkg.init();

        metadata = new ProjectMetadata();
        layers = new ArrayList();
        mainMapContent = new MapContent();
        crs = CRSUtils.GENERIC_2D;

    }

    public MapContent getMainMapContent() {
        return mainMapContent;
    }

    /**
     * Get the path of the temporary database
     *
     * @return
     */
    public Path getTempDatabasePath() {
        return tempDatabasePath;
    }

    /**
     * Set the path of the temporary database
     *
     * @param tempDatabasePath
     */
    protected void setTempDatabasePath(Path tempDatabasePath) {
        this.tempDatabasePath = tempDatabasePath;
    }

    /**
     * Get metadata associated with project: title, comments, ...
     *
     * @return
     */
    public ProjectMetadata getMetadata() {
        return metadata;
    }

    /**
     * Set metadata associated with project: title, comments, ...
     *
     * @return
     */
    public void setMetadata(ProjectMetadata metadata) {
        this.metadata = metadata;
    }

    /**
     * Get all layers
     *
     * @return
     */
    public ArrayList<Layer> getLayers() {
        return layers;
    }

    /**
     * Method create and add a layer. This is the only way to create a layer.
     *
     * @param name
     * @param visible
     * @param zindex
     * @param type
     */
    public Layer addNewLayer(String name, boolean visible, int zindex, LayerType type) {

        // create a new feature source
        String layerid = LayerIndexEntry.generateId();
        FeatureSource source = null;
        try {
            source = createNewFeatureSoure(layerid);
        } catch (IOException e) {
            logger.error(e);
            return null;
        }

        // create a layer wrapper and store it
        Layer layer = new Layer(layerid, name, visible, zindex, type, source);
        addLayer(layer);

        return layer;
    }

    /**
     * Create a new table in associated geopackage, and return a feature source
     *
     * @param layerid
     * @return
     * @throws IOException
     */
    private ContentFeatureSource createNewFeatureSoure(String layerid) throws IOException {

        // create a simple feature type
        // /!\ If DefaultEngineeringCRS.GENERIC2D is used, a lot of time is waste.
        // Prefer CRS.decode("EPSG:40400");
        SimpleFeatureType type = FeatureUtils.getSimpleFeatureType(layerid, this.crs);
        FeatureEntry fe = new FeatureEntry();
        fe.setBounds(new ReferencedEnvelope());
        fe.setSrid(null);

        // create a geopackage entry
        geopkg.create(fe, type);

        // get feature source from geopackage
        Map<String, String> params = new HashMap();
        params.put("dbtype", "geopkg");
        params.put("database", tempDatabasePath.toString());

        JDBCDataStore datastore = (JDBCDataStore) DataStoreFinder.getDataStore(params);
        return datastore.getFeatureSource(layerid);

    }

    protected void addLayer(Layer layer) {
        layers.add(layer);
        mainMapContent.addLayer(layer.getGeotoolsLayer());
    }

    public void setCrs(CoordinateReferenceSystem crs) {
        this.crs = crs;
    }

    public CoordinateReferenceSystem getCrs() {
        return crs;
    }

    public void addNewLayout() {

    }

    public void getMetatdatas() {

    }

    public void saveMetadatas() {

    }

    public void getBackgroundColor() {

    }

    public void getLayouts() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Project project = (Project) o;

        if (layers != null ? !layers.equals(project.layers) : project.layers != null) return false;
        return metadata != null ? metadata.equals(project.metadata) : project.metadata == null;

    }

    @Override
    public int hashCode() {
        int result = layers != null ? layers.hashCode() : 0;
        result = 31 * result + (metadata != null ? metadata.hashCode() : 0);
        return result;
    }
}
