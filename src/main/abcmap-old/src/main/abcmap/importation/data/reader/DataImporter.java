package abcmap.importation.data.reader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

import abcmap.exceptions.MapImportException;
import abcmap.managers.ConfigurationManager;
import abcmap.managers.ProjectManager;
import abcmap.managers.stub.MainManager;
import abcmap.utils.Utils;
import abcmap.utils.threads.ThreadManager;

public class DataImporter implements Runnable {

	private static final String TEMP_PREFIX = "waiting_for_datalist_import_";

	private ConfigurationManager configm;
	private ProjectManager projectm;
	private File fileToImport;

	public DataImporter() {
		projectm = MainManager.getProjectManager();
		configm = MainManager.getConfigurationManager();
	}

	public void startLater() throws MapImportException {

		// les premieres verification sont effectuées ici pour pouvoir lancées
		// des exceptions adaptées et permettre l'affichage de messages
		// d'erreurs plus explicites

		// verifier le fichier a importer
		fileToImport = new File(configm.getDataImportPath());

		if (fileToImport.isFile() == false) {
			throw new MapImportException(MapImportException.INVALID_FILE);
		}

		// determiner le bon lecteur à utiliser
		String ext = Utils.getExtension(fileToImport.getName());
		AbstractDataParser parser = AbstractDataParser.getParserFor(ext);

		if (parser == null) {
			throw new MapImportException(MapImportException.NO_RENDERER_AVAILABLE);
		}

		// lancer l'import
		ThreadManager.runLater(this);
	}

	@Override
	public void run() {

		// TODO

		try {
			copySourceFileInTempDirectory();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Copier le fichier à importer dans les fichiers temporaires.
	 * 
	 * @throws IOException
	 */
	protected void copySourceFileInTempDirectory() throws IOException {

		// creer un fichier temporaire
		File destination = projectm.createTemporaryFile(TEMP_PREFIX, null);

		if (destination == null) {
			throw new IOException("Unable to write temp files");
		}

		FileOutputStream stream = null;
		try {

			// copier le fichier à importer
			stream = new FileOutputStream(destination);
			Files.copy(fileToImport.toPath(), stream);

			// conserver la référence du fichier
			fileToImport = destination;
		}

		catch (IOException e) {
			throw new IOException(e);
		}

		finally {
			if (stream != null)
				stream.close();
		}

	}

}
