package org.abcmap.core.importation.data.writer;

import org.abcmap.core.importation.data.DataEntryList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 *
 */
public abstract class AbstractDataWriter {

    /**
     * Return a list of supported extensions
     * @return
     */
    public abstract List<String> getSupportedExtensions();

    /**
     * Return true if extension is supported
     * @param str
     * @return
     */
    public boolean isSupportedExtension(String str){
        return getSupportedExtensions().contains(str.toLowerCase().trim());
    }

    /**
     *
     * @param list
     * @param destination
     * @throws IOException
     */
    public abstract void write(DataEntryList list, Path destination) throws IOException;

    /**
     * Retourne une instance de chaque obet d'criture disponible.
     *
     * @return
     */
    public static AbstractDataWriter[] getAvailablesWriters() {
        return new AbstractDataWriter[]{new CsvDataWriter()};
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
