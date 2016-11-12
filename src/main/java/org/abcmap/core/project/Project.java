package org.abcmap.core.project;

import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.utils.CRSUtils;
import org.abcmap.core.utils.FeatureUtils;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.data.Transaction;
import org.geotools.data.store.ContentFeatureSource;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.geopkg.FeatureEntry;
import org.geotools.geopkg.GeoPackage;
import org.geotools.jdbc.JDBCDataStore;
import org.geotools.map.MapContent;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Represent a serializable project. Projects are stored in Geopakages, some kind of sqlite
 * database. All layers are read directly on the database.
 * <p>
 * Project are strongly dependant of database because layers read data directly in database
 */
public class Project {

    private static final CustomLogger logger = LogManager.getLogger(Project.class);

    /**
     * Temp directory, where is located database
     */
    private final Path tempDirectory;

    /**
     * Only one layer is alterable at a time, the active layer
     */
    private Layer activeLayer;

    /**
     * Final path of the project, where the user want to save it.
     * <p>
     * This location is used only to save project.
     */
    private Path finalPath;

    /**
     * Database associated with project
     */
    private GeoPackage geopkg;

    /**
     * The path of the database
     */
    private Path databasePath;

    /**
     * List of layers. Layers wrap Geotools layers.
     */
    private ArrayList<Layer> layers;

    /**
     * Geotools map content used to render and display map
     */
    private MapContent mainMapContent;

    /**
     * Metadata about project
     */
    private ProjectMetadata metadataContainer;

    /**
     * CRS of the whole project
     * <p>
     * TODO: to remove ?
     */
    private CoordinateReferenceSystem crs;

    /**
     * Create a new project associated with the database at specified location.
     * <p>
     * Project file must be a temporary project file.
     *
     * @param databasePath
     * @throws IOException
     */
    public Project(Path databasePath) throws IOException {

        this.databasePath = databasePath;

        this.tempDirectory = databasePath.getParent();
        this.metadataContainer = new ProjectMetadata();
        this.layers = new ArrayList();
        this.mainMapContent = new MapContent();
        this.crs = CRSUtils.GENERIC_2D;
        this.finalPath = null;

    }

    /**
     * Initialize geopackage object when database file is ready
     *
     * @throws IOException
     */
    protected void initializeGeopackage() throws IOException {
        this.geopkg = new GeoPackage(databasePath.toFile());
    }

    public MapContent getMainMapContent() {
        return mainMapContent;
    }

    /**
     * Get the path of the temporary database
     *
     * @return
     */
    public Path getDatabasePath() {
        return databasePath;
    }

    /**
     * Set the path of the temporary database
     *
     * @param databasePath
     */
    protected void setDatabasePath(Path databasePath) {
        this.databasePath = databasePath;
    }

    /**
     * Get metadataContainer associated with project: title, comments, ...
     *
     * @return
     */
    public ProjectMetadata getMetadataContainer() {
        return metadataContainer;
    }

    /**
     * Set metadataContainer associated with project: title, comments, ...
     *
     * @return
     */
    public void setMetadataContainer(ProjectMetadata metadata) {
        this.metadataContainer = metadata;
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
        params.put("database", databasePath.toString());

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

    /**
     * Return the background color of this project
     *
     * @return
     */
    public String getBackgroundColor() {
        return metadataContainer.getMetadata().get(PMConstants.BG_COLOR);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        if (geopkg != null) {
            logger.warning("Project geopackage have not been closed, closing " + this + " / " + geopkg + " / " + geopkg.getFile().toString());
            geopkg.close();
        }
    }

    /**
     * Close the database associated with this project.
     * <p>
     * Temporary files are not deleted
     */
    public void close() {
        geopkg.close();
        geopkg = null;
    }


    /**
     * Data used: layers, metadataContainer, finalpath, crs
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Project project = (Project) o;

        if (finalPath != null ? !finalPath.equals(project.finalPath) : project.finalPath != null) return false;
        if (layers != null ? !layers.equals(project.layers) : project.layers != null) return false;
        if (metadataContainer != null ? !metadataContainer.equals(project.metadataContainer) : project.metadataContainer != null)
            return false;
        return crs != null ? crs.equals(project.crs) : project.crs == null;

    }

    /**
     * Data used: layers, metadataContainer, finalpath, crs
     *
     * @return
     */
    @Override
    public int hashCode() {
        int result = finalPath != null ? finalPath.hashCode() : 0;
        result = 31 * result + (layers != null ? layers.hashCode() : 0);
        result = 31 * result + (metadataContainer != null ? metadataContainer.hashCode() : 0);
        result = 31 * result + (crs != null ? crs.hashCode() : 0);
        return result;
    }

    /**
     * Final path of the project, where the user want to save it.
     * <p>
     * This location is used only to save project.
     */
    public Path getFinalPath() {
        return finalPath;
    }

    /**
     * Final path of the project, where the user want to save it.
     * <p>
     * This location is used only to save project.
     */
    public void setFinalPath(Path finalPath) {
        this.finalPath = finalPath;
    }

    /**
     * Path of temporary directory with databases and misc files
     *
     * @return
     */
    public Path getTempDirectory() {
        return tempDirectory;
    }

    /**
     * Set the active layer, the only layer alterable
     *
     * @param index
     */
    public void setActiveLayer(int index) {
        this.activeLayer = layers.get(index);
    }

    /**
     * Set the active layer, the only layer alterable
     *
     * @param activeLayer
     */
    public void setActiveLayer(Layer activeLayer) {
        this.activeLayer = activeLayer;
    }

    /**
     * Get the active layer, the only layer alterable
     *
     * @return
     */
    public Layer getActiveLayer() {
        return activeLayer;
    }

    /**
     * Return a new database connection with project geopackage
     *
     * @return
     * @throws IOException
     */
    public Connection getDatabaseConnection() throws IOException {

        Map<String, String> params = new HashMap();
        params.put("dbtype", "geopkg");
        params.put("database", databasePath.toString());

        JDBCDataStore datastore = (JDBCDataStore) DataStoreFinder.getDataStore(params);

        return datastore.getConnection(Transaction.AUTO_COMMIT);

    }
}
