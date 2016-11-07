package abcmap.cancel;

import abcmap.managers.ProjectManager;
import abcmap.managers.stub.MainManager;
import abcmap.project.Project;

/**
 * Uniquement pour les changements d'ordre de calque. Sinon utiliser
 * MapLayerCancelOp
 * 
 * @author remipassmoilesel
 *
 */
public class ProjectListsCancelOp implements CancelOperation {

	private ProjectManager projectc;
	private Project originalProject;

	public ProjectListsCancelOp(Project project) {

		this.projectc = MainManager.getProjectManager();

		if (projectc.isInitialized() == false)
			throw new IllegalStateException("Non-initialized project");

		this.originalProject = project;

	}

	@Override
	public void cancel() {

		// verifier si projet non initialisé
		if (projectc.isInitialized() == false)
			return;

		if (originalProject.equals(projectc.getProject()))
			return;

		// changements
		projectc.getMementoManager().restore();

		// notififications
		projectc.fireLayerListChanged();
		projectc.fireLayoutListChanged();

	}

	@Override
	public void redo() {

		if (projectc.isInitialized() == false)
			return;

		if (originalProject.equals(projectc.getProject()))
			return;

		projectc.getMementoManager().redo();

		// notififications
		projectc.fireLayerListChanged();
		projectc.fireLayoutListChanged();

	}

}
