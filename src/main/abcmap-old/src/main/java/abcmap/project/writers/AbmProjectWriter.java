package abcmap.project.writers;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import abcmap.configuration.ConfigurationConstants;
import abcmap.exceptions.ProjectException;
import abcmap.project.Project;
import abcmap.utils.ZipDirectory;

/**
 * Ecrire un projet Abc-Map.
 * <p>
 * Le chemin final est spécifié dans le projet lui-même
 * 
 * @author remipassmoilesel
 *
 */
public class AbmProjectWriter extends ProjectWriter {

	private boolean saveOnlyDescriptor;

	public AbmProjectWriter() {
		saveOnlyDescriptor = false;
	}

	public void saveOnlyDescriptor(boolean val) {
		this.saveOnlyDescriptor = val;
	}

	@Override
	public void verify(Project project) throws ProjectException, IOException {

		// verifier si le projet n'est pas nul
		if (project == null)
			throw new ProjectException(ProjectException.PROJECT_NOT_INITIALIZED);

		// verifier le chemin du projet SI on n'écrit pas seulement le
		// descripteur
		if (saveOnlyDescriptor == false) {

			if (project.getRealPath() == null)
				throw new ProjectException(ProjectException.PROJECT_WITHOUT_FINAL_PATH);

			if (project.getRealPath().isFile() && isOverwriting() == false)
				throw new FileAlreadyExistsException("File already exist: " + project.getRealPath().getAbsolutePath());

		}

	}

	@Override
	public void write(Project project) throws ProjectException, IOException {

		// verifications
		verify(project);

		// ecrire descripteur
		AbmDescriptorWriter descWriter = new AbmDescriptorWriter();
		descWriter.saveProjectDescriptor(project);

		// récupérer eventuelles erreurs mineures
		if (descWriter.getMinorExceptions() != null && descWriter.getMinorExceptions().size() > 0) {
			for (Exception e : descWriter.getMinorExceptions())
				addMinorException(e);
		}

		if (saveOnlyDescriptor == false) {

			// chemin temporaire
			File tmp = new File(
					ConfigurationConstants.TEMP_PGRM_DIRECTORY + File.separator + System.currentTimeMillis() + ".tmp");

			// zipper le projet
			ZipDirectory zd = new ZipDirectory();
			zd.zipDirectory(project.getTempDirectoryFile(), tmp, false);

			// copie
			StandardCopyOption option = StandardCopyOption.COPY_ATTRIBUTES;
			if (isOverwriting())
				option = StandardCopyOption.REPLACE_EXISTING;
			Files.copy(tmp.toPath(), project.getRealPath().toPath(), option);

			// effacer l'ancien
			tmp.delete();

		}

	}

}
