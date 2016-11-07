package abcmap.importation.data;

public class DataImportConfiguration {

	public static final String RENDER_AS_IS = "RENDER_AS_IS";
	public static final String CREATE_SYMBOLS = "CREATE_SYMBOLS";
	public static final String CREATE_LABELS = "CREATE_LABELS";
	public static final String CREATE_POLYLINES = "CREATE_POLYLINES";
	public static final String CREATE_POLYGONS = "CREATE_POLYGONS";

	private String mode;

	public DataImportConfiguration() {

		this.mode = RENDER_AS_IS;

	}
	
	public String getMode() {
		return mode;
	}

}
