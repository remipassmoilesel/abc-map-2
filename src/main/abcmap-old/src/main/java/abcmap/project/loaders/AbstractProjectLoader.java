package abcmap.project.loaders;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import abcmap.project.Project;
import abcmap.project.loaders.abm.AbmProjectLoader;

public abstract class AbstractProjectLoader {

	@SuppressWarnings("unchecked")
	public static Class<? extends AbstractProjectLoader>[] getAvailablesLoaders() {
		return new Class[] { AbmProjectLoader.class, };
	}

	private String name;
	private String extension;
	private ArrayList<Exception> minorExceptions;

	public AbstractProjectLoader() {
		this.name = "no name";
		this.extension = "xxx";
		this.minorExceptions = new ArrayList<Exception>();
	}

	public abstract void load(File file, Project project) throws IOException;

	public abstract void verify(File toImport) throws IOException;

	public ArrayList<Exception> getMinorExceptions() {
		return minorExceptions;
	}

	public String getName() {
		return name;
	}

	public String getExtension() {
		return extension;
	}

	protected void addMinorException(Exception e) {
		minorExceptions.add(e);
	}

}
