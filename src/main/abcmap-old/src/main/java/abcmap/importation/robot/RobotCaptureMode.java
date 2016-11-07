package abcmap.importation.robot;

public enum RobotCaptureMode {
	
	START_FROM_ULC, START_FROM_MIDDLE;

	/**
	 * Retourne la valeur de la chaine passée en argument ou ROBOT_MODE_1 si une
	 * erreur survient
	 * 
	 * @return
	 */
	public static RobotCaptureMode safeValueOf(String s) {
		try {
			return RobotCaptureMode.valueOf(s);
		} catch (Exception e) {
			return START_FROM_ULC;
		}
	}
}
