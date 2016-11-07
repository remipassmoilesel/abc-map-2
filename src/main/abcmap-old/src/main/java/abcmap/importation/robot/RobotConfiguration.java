package abcmap.importation.robot;

import abcmap.configuration.Configuration;
import abcmap.gui.comps.progressbar.ProgressbarTask;
import abcmap.utils.Utils;
import abcmap.utils.gui.Lng;

/**
 * Classe de configuration de robot d'importation. Regroupe plusieurs
 * paramètres.
 * 
 * @author remipassmoilesel
 *
 */
public class RobotConfiguration {

	/** Paramètres d'import par défaut */
	public static final RobotConfiguration NORMAL_IMPORT = new RobotConfiguration(
			"Configuration par défaut");

	/** Paramètres appliqués lorsque la capture peut être rapide */
	public static final RobotConfiguration RAPID_IMPORT = new RobotConfiguration("Import rapide",
			0.05f, 500, 500, 500, RobotCaptureMode.START_FROM_ULC);

	/** Paramètres appliqués lorsque la capture doit être lente */
	public static final RobotConfiguration SLOW_IMPORT = new RobotConfiguration("Import lent",
			0.15f, 1500, 1500, 100, RobotCaptureMode.START_FROM_ULC);

	/** Paramètres appliqués lorsque la capture doit être lente */
	public static final RobotConfiguration CUSTOM_IMPORT = new RobotConfiguration(
			"Paramètres personnalisés");

	/**
	 * Listes des configurations prédéfinies. CUSTOM doit être placé en dernier
	 */
	private static final RobotConfiguration[] predefinedConfigurations = new RobotConfiguration[] {
			NORMAL_IMPORT, RAPID_IMPORT, SLOW_IMPORT, CUSTOM_IMPORT };

	public static final int CUSTOM_SETTINGS_INDEX = predefinedConfigurations.length - 1;

	/**
	 * Nom de la configuration. N'est pas pris en compte dans la méthode equals
	 */
	private String name;

	private float covering;
	private int movingDelay;
	private int captureDelay;
	private int hiddingDelay;

	private RobotCaptureMode mode;

	/**
	 * Créer un objet de configuration et l'initialiser aux valeurs par défaut.
	 */
	public RobotConfiguration(String name) {

		this.name = name;

		Configuration conf = new Configuration();
		this.covering = conf.ROBOT_IMPORT_COVERING;
		this.movingDelay = conf.ROBOT_IMPORT_MOVING_DELAY;
		this.captureDelay = conf.ROBOT_IMPORT_CAPTURE_DELAY;
		this.hiddingDelay = conf.WINDOW_HIDDING_DELAY;
		this.mode = RobotCaptureMode.safeValueOf(conf.ROBOT_IMPORT_MODE);
	}

	public RobotConfiguration(String name, float covering, int draggingDelay, int captureDelay,
			int hiddingDelay, RobotCaptureMode mode) {

		this.name = name;

		this.covering = covering;
		this.movingDelay = draggingDelay;
		this.captureDelay = captureDelay;
		this.hiddingDelay = hiddingDelay;
		this.mode = mode;
	}

	/**
	 * Ne prend pas en compte le nom du set de configuration
	 */
	@Override
	public boolean equals(Object obj) {

		Object[] fields1 = new Object[] { this.movingDelay, this.captureDelay, this.hiddingDelay,
				this.mode, this.covering };

		Object[] fields2 = null;
		if (obj instanceof RobotConfiguration) {
			RobotConfiguration obj2 = (RobotConfiguration) obj;
			fields2 = new Object[] { obj2.movingDelay, obj2.captureDelay, obj2.hiddingDelay,
					obj2.mode, obj2.covering };
		}

		return Utils.equalsUtil(this, obj, fields1, fields2);

	}

	public RobotCaptureMode getMode() {
		return mode;
	}

	public void setMode(RobotCaptureMode mode) {
		this.mode = mode;
	}

	public Float getCovering() {
		return covering;
	}

	public Integer getCaptureDelay() {
		return captureDelay;
	}

	public Integer getMovingDelay() {
		return movingDelay;
	}

	public Integer getHiddingDelay() {
		return hiddingDelay;
	}

	public void setCaptureDelay(Integer captureDelay) {
		this.captureDelay = captureDelay;
	}

	public void setCovering(Float covering) {
		this.covering = covering;
	}

	public void setMovingDelay(Integer draggingDelay) {
		this.movingDelay = draggingDelay;
	}

	public void setHiddingDelay(Integer hiddingDelay) {
		this.hiddingDelay = hiddingDelay;
	}

	public String getName() {
		return name;
	}

	public static RobotConfiguration[] getPredefinedConfigurations() {
		return predefinedConfigurations;
	}

	public static String[] getPredefinedConfigurationNames() {
		String[] rslt = new String[predefinedConfigurations.length];
		for (int i = 0; i < predefinedConfigurations.length; i++) {
			// TODO: remplacer lorsque ce sera necessaire
			// rslt[i] = Lng.get(predefinedConfigurations[i].getId());
			rslt[i] = predefinedConfigurations[i].getName();
		}
		return rslt;
	}

	/**
	 * Retourne la configuration prédéfini de nom name ou null si pas de
	 * résultat.
	 * 
	 * @param name
	 * @return
	 */
	public static RobotConfiguration getConfigurationByName(String name) {
		for (RobotConfiguration rc : predefinedConfigurations) {
			if (rc.getName().equals(name)) {
				return rc;
			}
		}

		return null;
	}

	@Override
	public String toString() {

		Object[] keys = new Object[] { "id", "covering", "movingDelay", "captureDelay",
				"hiddingDelay", "mode", };
		Object[] values = new Object[] { name, covering, movingDelay, captureDelay, hiddingDelay,
				mode, };

		return Utils.toString(this, keys, values);

	}

	/**
	 * Met à jour tous les champs à partir de l'objet passé en paramètre, sauf
	 * le nom.
	 * 
	 * @param rconf
	 */
	public void update(RobotConfiguration rconf) {

		this.captureDelay = rconf.captureDelay;
		this.covering = rconf.covering;
		this.hiddingDelay = rconf.hiddingDelay;
		this.movingDelay = rconf.movingDelay;
		this.mode = rconf.mode;

	}

}
