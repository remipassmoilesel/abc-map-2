package abcmap.importation.directory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import abcmap.exceptions.MapImportException;
import abcmap.importation.tile.TileMaker;

public class DirectoryImporter extends TileMaker {

	/**
	 * Liste toutes les images d'un repertoire et lance l'analyse.
	 * 
	 * @param directory
	 * @throws MapImportException
	 */
	public void addAllFileFrom(File directory) throws MapImportException {

		// lister les fichiers du dossier
		ArrayList<File> files;
		try {
			files = importm.getAllValidPicturesFrom(directory);
		}

		// dossier invalide: erreur
		catch (IOException e) {
			throw new MapImportException(MapImportException.INVALID_DIRECTORY);
		}

		// aucun fichiers, erreur
		if (files.size() <= 0) {
			throw new MapImportException(MapImportException.NO_FILES_TO_IMPORT);
		}

		// tri par ordre alphabetique
		Collections.sort(files);

		// ajout des fichiers
		addFiles(files);

	}
	
	

}
