package abcmap.gui.dialogs.simple;

import java.io.File;

import javax.swing.JFileChooser;

import abcmap.utils.Utils;

/**
 * Conteneur de resultat de fenetre parcourir.
 * 
 * @author remipassmoilesel
 *
 */
public class BrowseDialogResult {

	public static final Integer APPROVE = JFileChooser.APPROVE_OPTION;
	public static final Integer CANCEL = JFileChooser.CANCEL_OPTION;

	private Integer returnVal;
	private File file;

	public BrowseDialogResult() {
		returnVal = null;
		file = null;
	}

	/**
	 * Mettre à jour le résultat à partir d'un autre résultat.
	 * 
	 * @param result
	 */
	public void update(BrowseDialogResult result) {
		this.returnVal = result.returnVal;
		this.file = result.file != null ? new File(result.file.getAbsolutePath()) : null;
	}

	public Integer getReturnVal() {
		return returnVal;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public void setReturnVal(Integer returnVal) {
		this.returnVal = returnVal;
	}

	@Override
	public String toString() {

		Object[] values = new Object[] { returnVal, file, };
		Object[] keys = new Object[] { "returnVal", "file", };

		return Utils.toString(this, keys, values);

	}

	public boolean isActionCanceled() {
		return Utils.safeEquals(returnVal, CANCEL);
	}

	public boolean isActionApproved() {
		return Utils.safeEquals(returnVal, APPROVE);
	}

}
