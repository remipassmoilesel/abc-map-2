package abcmap.importation.data.writer;

import java.io.File;
import java.io.IOException;

import abcmap.importation.data.DataEntryList;

public abstract class AbstractDataWriter {

	/**
	 * Retourne la liste de extensions supportées par l'objet d'ecriture
	 * 
	 * @return
	 */
	public abstract String[] getSupportedExtensions();

	/**
	 * Retourne vrai si l'extension est supportée par l'objet d'ecriture
	 * 
	 * @param str
	 * @return
	 */
	public abstract boolean isSupportedExtension(String str);

	/**
	 * Ecrit la liste dans le fichier de destination
	 * 
	 * @param list
	 * @param destination
	 * @throws IOException
	 */
	public abstract void write(DataEntryList list, File destination) throws IOException;

	/**
	 * Retourne une instance de chaque obet d'criture disponible.
	 * 
	 * @return
	 */
	public static AbstractDataWriter[] getAvailablesWriters() {
		return new AbstractDataWriter[] { new CsvDataWriter() };
	}

	/**
	 * Retourne un objet d'écriture compatible avec l'extension passée en
	 * paramètre ou null.
	 * 
	 * @param extension
	 * @return
	 */
	public static AbstractDataWriter getWriterFor(String extension) {

		if (extension == null) {
			throw new NullPointerException("Extension is null");
		}

		extension = extension.trim().toLowerCase();

		for (AbstractDataWriter writer : getAvailablesWriters()) {
			if (writer.isSupportedExtension(extension)) {
				return writer;
			}
		}

		return null;
	}

}
