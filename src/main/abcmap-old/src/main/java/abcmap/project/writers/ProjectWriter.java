package abcmap.project.writers;

import java.io.IOException;
import java.util.ArrayList;

import abcmap.exceptions.ProjectException;
import abcmap.project.Project;

public abstract class ProjectWriter {

	public static Class<? extends ProjectWriter>[] getAvailablesWriters() {
		return new Class[] { AbmProjectWriter.class };
	}

	protected String name;
	protected String finalExtension;
	protected boolean overwriting;
	protected ArrayList<Exception> minorExceptions;

	public ProjectWriter() {
		this.name = "no_name";
		this.finalExtension = "xxx";
		this.overwriting = false;
		this.minorExceptions = new ArrayList<Exception>();
	}

	public abstract void write(Project project) throws ProjectException, IOException;

	public abstract void verify(Project project) throws ProjectException, IOException;

	public boolean isOverwriting() {
		return overwriting;
	}

	public void setOverwriting(boolean overwriting) {
		this.overwriting = overwriting;
	}

	public ArrayList<Exception> getMinorExceptions() {
		return minorExceptions;
	}

	protected void addMinorException(Exception e) {
		minorExceptions.add(e);
	}
}
