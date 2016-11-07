package abcmap.managers;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;

import abcmap.configuration.Configuration;
import abcmap.configuration.ConfigurationConstants;
import abcmap.configuration.ConfigurationLoader;
import abcmap.configuration.ConfigurationWriter;
import abcmap.events.ConfigurationEvent;
import abcmap.importation.robot.RobotCaptureMode;
import abcmap.importation.robot.RobotConfiguration;
import abcmap.managers.stub.MainManager;
import abcmap.surf.Params;
import abcmap.utils.notifications.HasNotificationManager;
import abcmap.utils.notifications.NotificationManager;
import abcmap.utils.threads.ThreadManager;

/**
 * Gestionnaire de profils de configuration. La configuration est enregistrée
 * dans deux profils au format XML identiques: <br>
 * Le profil utilisateur que l'utilisateur peut déplacer, copier, ... <br>
 * Le profil du système qui est une copie du profil que l'utilisateur souhaite
 * utiliser.
 * 
 * @author remipassmoilesel
 *
 */
public class ConfigurationManager implements HasNotificationManager {

	private static final String CURRENT_ROBOT_CONFIGURATION = "CURRENT_ROBOT_CONFIGURATION";

	private Integer printResolution;
	private Double coeffMillitoPix;
	private Configuration conf;
	private boolean isProfileLoaded;
	private File profileRoot;
	private File profile;
	private NotificationManager observer;

	public ConfigurationManager() {

		// objet de configuration
		conf = new Configuration();

		// tester si le dossier racine des profil est present ou le creer
		profileRoot = new File(ConfigurationConstants.PROFILE_ROOT_PATH);
		if (profileRoot.isDirectory() == false) {
			profileRoot.mkdirs();
		}

		this.observer = new NotificationManager(this);
		isProfileLoaded = false;

		// initialisation des parametres d'impression
		setPrintResolution(ConfigurationConstants.DEFAULT_PRINT_RESOLUTION);

	}

	public Params getSurfParameters() {

		// vérifier le parametre
		if (conf.SURF_MODE < 0
				|| conf.SURF_MODE > ConfigurationConstants.SURF_PARAMS.length - 1) {
			conf.SURF_MODE = 0;
		}

		return ConfigurationConstants.SURF_PARAMS[conf.SURF_MODE];
	}

	public void setSurfMode(int param) {

		// verifier le parametre
		if (param < 0 || param > ConfigurationConstants.SURF_PARAMS.length - 1) {
			param = 0;
		}

		// modifier la configuration
		conf.SURF_MODE = param;

		fireParametersUpdated();
	}

	/**
	 * Retourne la configuration en cours
	 * 
	 * @return
	 */
	public Configuration getConfiguration() {
		return conf;
	}

	/**
	 * Retablis les parametres par defaut
	 */
	public void resetConfiguration() {

		// sav de la langue
		String lang = null;
		if (conf != null)
			lang = new String(conf.LANGUAGE);

		// reset puis reaffect langue
		conf = new Configuration();

		if (lang != null)
			conf.LANGUAGE = lang;

		observer.fireEvent(new ConfigurationEvent(
				ConfigurationEvent.CONFIGURATION_RESETED, conf));
	}

	public boolean isProfileLoaded() {
		return isProfileLoaded;
	}

	/**
	 * Charge le profil systeme.
	 * 
	 * @param path
	 * @throws IOException
	 */
	public void loadSystemProfile() throws IOException {
		loadProfile(ConfigurationConstants.SYSTEM_PROFILE_PATH);
	}

	public void loadProfile(String path) throws IOException {
		loadProfile(new File(path));
	}

	public void loadProfile(File profile) throws IOException {

		// raz configuration sauf langue
		resetConfiguration();

		// chargement
		ConfigurationLoader.load(conf, profile);

		isProfileLoaded = true;

		// notifications
		observer.fireEvent(new ConfigurationEvent(
				ConfigurationEvent.NEW_CONFIGURATION_LOADED, conf));

	}

	/**
	 * Enregistre le profil en cours
	 * 
	 * @throws IOException
	 */
	public void saveProfile() throws IOException {
		saveProfile(new File(ConfigurationConstants.SYSTEM_PROFILE_PATH), conf,
				true);
		saveProfile(new File(conf.PROFILE_PATH), conf, true);
	}

	/**
	 * Enregistre le profile en cours l'emplacement demand, en mettant jour le
	 * profil systme
	 * 
	 * @param path
	 * @throws IOException
	 */

	public void saveProfile(String path, boolean overwrite) throws IOException {
		// conserver le chemin du profil
		conf.PROFILE_PATH = path;

		// enregistrer le profil utilisateur
		saveProfile(new File(conf.PROFILE_PATH), conf, overwrite);

		// enregistrer le profil systeme
		saveProfile(new File(ConfigurationConstants.SYSTEM_PROFILE_PATH), conf,
				true);
	}

	/**
	 * Enregistre le profil par defaut
	 * 
	 * @throws IOException
	 */
	public void createAndSaveDefaultProfile() throws IOException {
		Configuration newConf = new Configuration();
		saveProfile(new File(ConfigurationConstants.SYSTEM_PROFILE_PATH),
				newConf, true);
		saveProfile(new File(ConfigurationConstants.DEFAULT_PROFILE_PATH),
				newConf, true);
		conf = newConf;
	}

	private void saveProfile(File file, Configuration conf, boolean overwrite)
			throws IOException {

		try {
			ConfigurationWriter.write(file, conf, overwrite);
		} catch (IOException e) {
			throw new IOException("Unable to write configuration file: "
					+ file.getAbsolutePath(), e);
		}

		observer.fireEvent(new ConfigurationEvent(
				ConfigurationEvent.CONFIGURATION_SAVED, conf));

	}

	public File getProfile() {
		return profile;
	}

	@Override
	public NotificationManager getNotificationManager() {
		return observer;
	}

	/**
	 * Fonction trigger lorsque changement de parametre
	 */
	public void fireParametersUpdated() {
		observer.fireEvent(new ConfigurationEvent(
				ConfigurationEvent.CONFIGURATION_UPDATED, conf));
	}

	/**
	 * 
	 * @param dpi
	 */
	public void setPrintResolution(Integer dpi) {
		printResolution = dpi;
		coeffMillitoPix = (double) printResolution / 25.40d;
	}

	/**
	 * 
	 * @param dpi
	 */
	public Integer getPrintResolution() {
		return printResolution;
	}

	/**
	 * Coefficient soumis au reglage dpi
	 * 
	 * @return
	 */
	public Double getPrintresCoeffMilliToPix() {
		return coeffMillitoPix;
	}

	/**
	 * Coefficient soumis au reglage dpi
	 * 
	 * @return
	 */
	public Double get72dpiCoeffMilliToPix() {
		return (double) ConfigurationConstants.JAVA_RESOLUTION / 25.40d;
	}

	/**
	 * Retourne les dimensions de recadrage enregistres dans la configuration
	 * 
	 * @return
	 */
	public Rectangle getCropRectangle() {

		Rectangle r = new Rectangle();
		r.x = conf.CROP_AREA_SELECTION_X;
		r.y = conf.CROP_AREA_SELECTION_Y;
		r.width = conf.CROP_AREA_SELECTION_W;
		r.height = conf.CROP_AREA_SELECTION_H;

		return r;
	}

	/**
	 * Affecte de nouvelles dimensions de recadrage.
	 * 
	 * @param r
	 */
	public void setCropRectangle(Rectangle r) {
		setCropRectangle(r.x, r.y, r.width, r.height);
	}

	/**
	 * Affecte de nouvelles dimensions de recadrage.
	 * 
	 * @param r
	 */
	public void setCropRectangle(int x, int y, int w, int h) {

		conf.CROP_AREA_SELECTION_X = x;
		conf.CROP_AREA_SELECTION_Y = y;
		conf.CROP_AREA_SELECTION_W = w;
		conf.CROP_AREA_SELECTION_H = h;

		fireParametersUpdated();
	}

	public void setCroppingEnabled(boolean val) {
		conf.ENABLE_CROPPING = val;
		fireParametersUpdated();
	}

	public boolean isCroppingEnabled() {
		return conf.ENABLE_CROPPING;
	}

	public int getWindowHidingDelay() {
		return conf.WINDOW_HIDDING_DELAY;
	}

	public void setWindowHidingDelay(int val) {
		conf.WINDOW_HIDDING_DELAY = val;
		fireParametersUpdated();
	}

	public int getSurfMode() {
		return conf.SURF_MODE;
	}

	/**
	 * Spécifier le chemin du repertoire d'import.
	 * 
	 * @param path
	 */
	public void setDirectoryImportPath(String path) {
		conf.DIRECTORY_IMPORT_PATH = path;
		fireParametersUpdated();
	}

	public String getDirectoryImportPath() {
		return conf.DIRECTORY_IMPORT_PATH;
	}

	public float getDocumentImportFactor() {
		return conf.DOCUMENT_IMPORT_FACTOR;
	}

	public String getDocumentImportPath() {
		return conf.DOCUMENT_IMPORT_PATH;
	}

	public void setDocumentImportPath(String path) {
		conf.DOCUMENT_IMPORT_PATH = path;
		fireParametersUpdated();
	}

	public void setDocumentImportFactor(float factor) {
		conf.DOCUMENT_IMPORT_FACTOR = factor;
		fireParametersUpdated();
	}

	public String getDocumentImportType() {
		return conf.DOCUMENT_IMPORT_TYPE;
	}

	public void setDocumentImportType(String type) {
		conf.DOCUMENT_IMPORT_TYPE = type;
		fireParametersUpdated();
	}

	/**
	 * Liste des pages par import. Le compte des pages commence à un. Si un zéro
	 * est dans la liste, toutes les pages seront importées.
	 * 
	 * @return
	 */
	public void setDocumentImportPages(String pages) {
		conf.DOCUMENT_IMPORT_PAGES = pages;
		fireParametersUpdated();
	}

	/**
	 * Liste des pages par import. Le compte des pages commence à un. Si un zéro
	 * est dans la liste, toutes les pages seront importées.
	 * 
	 * @return
	 */
	public String getDocumentImportPages() {
		return conf.DOCUMENT_IMPORT_PAGES;
	}

	public Dimension getRobotImportCaptureArea() {
		return new Dimension(conf.ROBOT_IMPORT_WIDTH, conf.ROBOT_IMPORT_HEIGHT);
	}

	public void setRobotImportCaptureArea(Dimension dim) {
		conf.ROBOT_IMPORT_WIDTH = dim.width;
		conf.ROBOT_IMPORT_HEIGHT = dim.height;

		fireParametersUpdated();
	}

	public Float getRobotImportCovering() {
		return conf.ROBOT_IMPORT_COVERING;
	}

	public void setRobotConfiguration(RobotConfiguration values) {

		conf.ROBOT_IMPORT_CAPTURE_DELAY = values.getCaptureDelay();
		conf.WINDOW_HIDDING_DELAY = values.getHiddingDelay();
		conf.ROBOT_IMPORT_MOVING_DELAY = values.getMovingDelay();
		conf.ROBOT_IMPORT_COVERING = values.getCovering();
		conf.ROBOT_IMPORT_MODE = values.getMode().toString();

		fireParametersUpdated();
	}

	public RobotConfiguration getRobotConfiguration() {

		RobotConfiguration rconfig = new RobotConfiguration(
				CURRENT_ROBOT_CONFIGURATION);

		rconfig.setCaptureDelay(conf.ROBOT_IMPORT_CAPTURE_DELAY);
		rconfig.setHiddingDelay(conf.WINDOW_HIDDING_DELAY);
		rconfig.setMovingDelay(conf.ROBOT_IMPORT_MOVING_DELAY);
		rconfig.setCovering(conf.ROBOT_IMPORT_COVERING);
		rconfig.setMode(RobotCaptureMode.safeValueOf(conf.ROBOT_IMPORT_MODE));

		return rconfig;
	}

	public String getDataImportPath() {
		return conf.DATA_IMPORT_PATH;
	}

	public void setDataImportPath(String path) {
		conf.DATA_IMPORT_PATH = path;

		// mettre à jour les headers dans le gestionnaire d'import
		ThreadManager.runLater(new Runnable() {
			@Override
			public void run() {
				MainManager.getImportManager().updateCurrentsDataImportHeaders();
			}
		});

		fireParametersUpdated();
	}

	public boolean isSaveProfileWhenQuit() {
		return conf.SAVE_PROFILE_WHEN_QUIT;
	}

}
