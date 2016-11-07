package abcmap.managers;

import java.io.File;
import java.util.ArrayList;

import abcmap.cancel.CancelOperation;
import abcmap.cancel.ElementsCancelOp;
import abcmap.cancel.LayoutPaperCancelOp;
import abcmap.cancel.MapLayerCancelOp;
import abcmap.cancel.ProjectListsCancelOp;
import abcmap.draw.basicshapes.LayerElement;
import abcmap.draw.shapes.Image;
import abcmap.draw.shapes.Tile;
import abcmap.events.CancelManagerEvent;
import abcmap.events.ProjectEvent;
import abcmap.managers.stub.MainManager;
import abcmap.project.layers.MapLayer;
import abcmap.project.layouts.LayoutPaper;
import abcmap.utils.PrintUtils;
import abcmap.utils.gui.GuiUtils;
import abcmap.utils.notifications.Notification;
import abcmap.utils.notifications.HasNotificationManager;
import abcmap.utils.notifications.NotificationManager;
import abcmap.utils.notifications.UpdatableByNotificationManager;
import abcmap.utils.threads.ThreadManager;

public class CancelManager implements HasNotificationManager {

	public boolean debugMode = false;

	/** Liste des opérations d'annulation */
	private ArrayList<CancelOperation> toCancel;

	/** Liste des operations à refaire */
	private ArrayList<CancelOperation> toRedo;

	private ProjectManager projectm;
	private DrawManager drawCtrl;

	private NotificationManager observer;

	public CancelManager() {

		this.projectm = MainManager.getProjectManager();
		this.drawCtrl = MainManager.getDrawManager();

		this.toCancel = new ArrayList<CancelOperation>(50);
		this.toRedo = new ArrayList<CancelOperation>(50);

		// vider les listes aux changements de projets
		this.observer = new NotificationManager(this);
		observer.setDefaultUpdatableObject(new UpdatableByNotificationManager() {
			@Override
			public void notificationReceived(Notification arg) {
				if (ProjectEvent.isNewProjectLoadedEvent(arg)
						|| ProjectEvent.isCloseProjectEvent(arg)) {
					CancelManager.this.clearLists();
				}
			}
		});

		// ecouter le projet
		projectm.getNotificationManager().addObserver(this);
	}

	public void clearLists() {
		toCancel.clear();
		toRedo.clear();

		fireListsUpdated();
	}

	public void addOperation(CancelOperation op) {

		toCancel.add(0, op);
		toRedo.clear();

		fireListsUpdated();

		if (debugMode) {
			talk();
		}
	}

	public void undo() {

		GuiUtils.throwIfOnEDT();

		// rcuprer le dernier lement puis le deplacer dans redo
		if (toCancel.size() < 1)
			return;

		final CancelOperation op = toCancel.remove(0);
		toRedo.add(0, op);

		// annuler dans un thread pour ne pas bloquer l'appli
		ThreadManager.runLater(new Runnable() {
			@Override
			public void run() {

				// desactiver eventuellement l'outil (label)
				drawCtrl.getCurrentTool().stopWorking();

				try {
					op.cancel();
				} catch (Exception e) {
					Log.error(e);
				}
			}
		});

		fireListsUpdated();

		if (debugMode) {
			CancelManager.this.talk();
		}
	}

	public void redo() {

		GuiUtils.throwIfOnEDT();

		// rcuprer le dernier lement puis le dplacer dans cancel
		if (toRedo.size() < 1)
			return;

		final CancelOperation op = toRedo.remove(0);
		toCancel.add(0, op);

		// redo dans un thread pour ne pas bloquer l'appli
		ThreadManager.runLater(new Runnable() {
			@Override
			public void run() {

				// desactiver eventuellement l'outil (label)
				drawCtrl.getCurrentTool().stopWorking();

				try {
					op.redo();
				} catch (Exception e) {
					Log.error(e);
				}
			}
		});

		fireListsUpdated();

		if (debugMode) {
			CancelManager.this.talk();
		}
	}

	/*
	 * 
	 * 
	 */

	public boolean isPossibleToCancel() {
		return toCancel.size() > 0;
	}

	public boolean isPossibleToRedo() {
		return toRedo.size() > 0;
	}

	public void fireListsUpdated() {
		observer.fireEvent(new CancelManagerEvent(
				CancelManagerEvent.LISTS_UPDATED, null));
	}

	public ElementsCancelOp addDrawOperation(MapLayer owner,
			LayerElement toCancel) {
		ArrayList<LayerElement> list = new ArrayList<LayerElement>();
		list.add(toCancel);
		return addDrawOperation(owner, list);
	}

	public ElementsCancelOp addDrawOperation(MapLayer owner,
			ArrayList<LayerElement> toCancel) {
		ElementsCancelOp op = new ElementsCancelOp(owner, toCancel);
		this.addOperation(op);
		return op;
	}

	/**
	 * Vérfifier si un element fait parti d'une operation d'annulation
	 * 
	 * @param elmt
	 */
	public boolean isElementInCancelOperation(LayerElement elmt) {
		ArrayList<CancelOperation> operations = new ArrayList<CancelOperation>(
				toCancel);
		operations.addAll(toRedo);
		for (CancelOperation op : operations) {
			if (op instanceof ElementsCancelOp) {
				if (((ElementsCancelOp) op).getElements().contains(elmt))
					return true;
			}
		}

		return false;
	}

	/**
	 * Vérfifier si un element fait parti d'une operation d'annulation
	 * 
	 * @param elmt
	 */
	public ArrayList<File> getTileAndImageFiles() {

		ArrayList<CancelOperation> operations = new ArrayList<CancelOperation>(
				toCancel);
		operations.addAll(toRedo);

		ArrayList<File> files = new ArrayList<File>();

		for (CancelOperation op : operations) {
			if (op instanceof ElementsCancelOp) {
				ArrayList<LayerElement> elmts = ((ElementsCancelOp) op)
						.getElements();
				for (LayerElement elmt : elmts) {
					if (elmt instanceof Tile) {
						files.add(new File(((Tile) elmt).getSourceFile()
								.getAbsolutePath()));
					} else if (elmt instanceof Image) {
						files.add(new File(((Image) elmt).getSourceFile()
								.getAbsolutePath()));
					}
				}
			}
		}

		return files;

	}

	public MapLayerCancelOp addMapLayerOperation(MapLayer layer) {
		MapLayerCancelOp op = new MapLayerCancelOp(layer);
		this.addOperation(op);
		return op;
	}

	public ProjectListsCancelOp addProjectListsOperation() {
		ProjectListsCancelOp op = new ProjectListsCancelOp(
				projectm.getProject());
		this.addOperation(op);
		return op;
	}

	public LayoutPaperCancelOp addLayoutSheetOperation(LayoutPaper sheet) {
		ArrayList<LayoutPaper> list = new ArrayList<LayoutPaper>();
		list.add(sheet);
		LayoutPaperCancelOp op = new LayoutPaperCancelOp(list);
		this.addOperation(op);
		return op;
	}

	public LayoutPaperCancelOp addLayoutSheetOperation(
			ArrayList<LayoutPaper> sheets) {
		LayoutPaperCancelOp op = new LayoutPaperCancelOp(sheets);
		this.addOperation(op);
		return op;
	}

	@Override
	public NotificationManager getNotificationManager() {
		return observer;
	}

	public void talk() {
		int i = 0;
		PrintUtils.p("### toCancel: ");
		for (CancelOperation op : toCancel) {
			PrintUtils.p("--" + i + " : " + op.getClass().getSimpleName());
			PrintUtils.p(op);
		}

		i = 0;
		PrintUtils.p("### toRedo: ");
		for (CancelOperation op : toRedo) {
			PrintUtils.p("--" + i + " : " + op.getClass().getSimpleName());
			PrintUtils.p(op);
		}
	}

	public ArrayList<CancelOperation> getOperationsToCancel() {
		return toCancel;
	}

	public ArrayList<CancelOperation> getOperationsToRedo() {
		return toRedo;
	}

}
