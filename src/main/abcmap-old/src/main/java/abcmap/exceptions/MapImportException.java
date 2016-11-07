package abcmap.exceptions;

public class MapImportException extends Exception {

	public static final String NO_FILES_TO_IMPORT = "NO_FILES_TO_IMPORT";
	public static final String ALREADY_IMPORTING = "ALREADY_IMPORTING";
	public static final String INVALID_DIRECTORY = "INVALID_DIRECTORY";
	public static final String NO_RENDERER_AVAILABLE = "NO_RENDERER_AVAILABLE";
	public static final String INVALID_FILE = "INVALID_FILE";
	public static final String ROBOT_IMPORT_MOUSE_CANCELED = "ROBOT_IMPORT_MOUSE_CANCELED";
	public static final String ROBOT_INSTATIATION_EXCEPTION = "ROBOT_INSTATIATION_EXCEPTION";

	public MapImportException(String string) {
		super(string);
	}

	public MapImportException(Exception e) {
		super(e);
	}

}
