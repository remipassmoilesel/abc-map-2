package abcmap.exceptions;

public class MapManagerException extends Exception {

	public static final String NOT_ENOUGHT_REFERENCES = "NOT_ENOUGHT_REFERENCES";
	public static final String REFERENCES_ARE_EQUALS = "REFERENCES_ARE_EQUALS";
	public static final String INVALID_CRS = "INVALID_CRS";
	public static final String INVALID_REFERENCE = "INVALID_REFERENCE";

	public MapManagerException(String string) {
		super(string);
	}

	public MapManagerException() {
		super();
	}

}
