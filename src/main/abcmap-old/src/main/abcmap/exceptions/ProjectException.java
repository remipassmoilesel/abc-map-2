package abcmap.exceptions;

import java.io.IOException;

public class ProjectException extends IOException {

	public static final String PROJECT_NOT_INITIALIZED = "PROJECT_NOT_INITIALIZED";
	public static final String INVALID_PROJECT_PATH = "INVALID_PROJECT_PATH";
	public static final String PROJECT_WITHOUT_FINAL_PATH = "PROJECT_WITHOUT_FINAL_PATH";
	public static final String PROJECT_ALREADY_OPENED = "PROJECT_NON_CLOSED";

	public ProjectException(String msg) {
		super(msg);
	}

	public ProjectException() {
		super();
	}
}
