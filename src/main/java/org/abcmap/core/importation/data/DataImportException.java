package org.abcmap.core.importation.data;

public class DataImportException extends Exception {

	public static final String DATAS_TOO_HEAVY = "DATAS_TOO_HEAVY";
	public static final String DATAS_TOO_LIGHT = "DATAS_TOO_LIGHT";
	public static final String INVALID_GPX_FORMAT = "INVALID_GPX_FORMAT";
	public static final String UNKNOWN_FORMAT = "UNKNOWN_FORMAT";

	public DataImportException(String msg) {
		super(msg);
	}
}