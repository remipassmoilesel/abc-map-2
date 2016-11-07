package abcmap.cancel;

import java.util.ArrayList;

import abcmap.exceptions.LayoutPaperException;
import abcmap.managers.ProjectManager;
import abcmap.managers.stub.MainManager;
import abcmap.project.layouts.LayoutPaper;

public class LayoutPaperCancelOp implements CancelOperation {

	private ProjectManager projectm;
	private ArrayList<LayoutPaper> papers;
	private boolean sheetHaveBeenAdded;
	private boolean sheetHaveBeenDeleted;
	private ArrayList<Integer> indexes;

	public LayoutPaperCancelOp(ArrayList<LayoutPaper> shts) {

		this.projectm = MainManager.getProjectManager();

		if (projectm.isInitialized() == false)
			throw new IllegalStateException("Non initialized project");

		this.papers = new ArrayList<LayoutPaper>(shts);
		this.indexes = new ArrayList<Integer>();

		for (LayoutPaper s : shts) {
			Integer index = projectm.getLayouts().indexOf(s);
			indexes.add(index);

			if (index == -1)
				throw new IllegalStateException("Unknown page");
		}

		this.sheetHaveBeenAdded = false;
		this.sheetHaveBeenDeleted = false;

	}

	@Override
	public void cancel() {

		if (projectm.isInitialized() == false)
			return;

		// le calque viens juste d'etre ajoute : retrait
		if (sheetHaveBeenAdded) {

			for (LayoutPaper s : papers) {
				if (projectm.getLayouts().contains(s) == false){
					throw new IllegalStateException("Unknown page");
				}

				projectm.removeLayout(s);
			}

		}

		// le calque viens juste d'être retiré: ajout
		else if (sheetHaveBeenDeleted) {
			for (LayoutPaper s : papers) {

				try {
					Integer index = indexes.get(papers.indexOf(s));
					projectm.addLayout(s, index);
				} catch (LayoutPaperException e) {
					projectm.addLayout(s);
				}
				s.refreshSheet();
			}
		}

		// changement d'état
		else {

			for (LayoutPaper s : papers) {

				if (projectm.getLayouts().contains(s) == false){
					throw new IllegalStateException("Unknown page");
				}

				s.getMementoManager().restore();
				s.refreshSheet();
			}
		}

		projectm.fireLayoutListChanged();
	}

	@Override
	public void redo() {

		if (projectm.isInitialized() == false)
			return;

		// le calque viens juste d'etre ajoute : retrait
		if (sheetHaveBeenAdded) {

			for (LayoutPaper s : papers) {
				try {
					Integer index = indexes.get(papers.indexOf(s));
					projectm.addLayout(s, index);
				} catch (LayoutPaperException e) {
					projectm.addLayout(s);
				}
				s.refreshSheet();
			}
		}

		// le calque viens juste d'être retiré: ajout
		else if (sheetHaveBeenDeleted) {
			for (LayoutPaper s : papers) {
				if (projectm.getLayouts().contains(s) == false)
					throw new IllegalStateException("Unknown page");

				projectm.removeLayout(s);
			}
		}

		// changement d'état
		else {

			for (LayoutPaper s : papers) {

				if (projectm.getLayouts().contains(s) == false)
					throw new IllegalStateException("Unknown page");

				s.getMementoManager().redo();
				s.refreshSheet();
			}
		}

		projectm.fireLayoutListChanged();

	}

	public void sheetHaveBeenAdded(boolean layerHaveBeenAdded) {
		this.sheetHaveBeenAdded = layerHaveBeenAdded;
	}

	public void sheetHaveBeenDeleted(boolean layerHaveBeenDeleted) {
		this.sheetHaveBeenDeleted = layerHaveBeenDeleted;
	}

}
