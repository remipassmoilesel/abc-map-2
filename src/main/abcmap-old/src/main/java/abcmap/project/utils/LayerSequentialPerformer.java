package abcmap.project.utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import abcmap.draw.basicshapes.LayerElement;
import abcmap.exceptions.MapLayerException;
import abcmap.gui.comps.color.ColorEvent;
import abcmap.gui.comps.color.ColorEventListener;
import abcmap.managers.CancelManager;
import abcmap.managers.DrawManager;
import abcmap.managers.Log;
import abcmap.managers.MapManager;
import abcmap.managers.ProjectManager;
import abcmap.managers.stub.MainManager;
import abcmap.project.layers.MapLayer;
import abcmap.utils.gui.GuiUtils;
import abcmap.utils.notifications.Notification;
import abcmap.utils.notifications.UpdatableByNotificationManager;
import abcmap.utils.threads.ThreadManager;

/**
 * Classe utilitaire permettant d'appliquer des modifications simples aux
 * elements d'un claque.<br>
 * 
 * @author remipassmoilesel
 *
 */
public class LayerSequentialPerformer implements Runnable, ActionListener,
		UpdatableByNotificationManager, ColorEventListener {

	protected MapManager mapm;
	protected ProjectManager projectm;
	protected DrawManager drawm;
	protected MapLayer activeLayer;
	protected CancelManager cancelm;
	protected boolean onlySelectedElements;

	protected ArrayList<Class> shapeFilters;
	protected ArrayList<Class> eventFilters;

	public LayerSequentialPerformer() {

		projectm = MainManager.getProjectManager();
		cancelm = MainManager.getCancelManager();
		drawm = MainManager.getDrawManager();
		mapm = MainManager.getMapManager();

		// classe de filtre
		shapeFilters = new ArrayList<Class>();

		// filtre de selection
		onlySelectedElements = false;

		// filtre d'evenements
		eventFilters = new ArrayList<Class>();
	}

	@Override
	public void notificationReceived(Notification arg) {
		// filter les arguments
		if (eventFilters.contains(arg.getClass())) {
			performLater();
		}
	}

	@Override
	public void colorChanged(ColorEvent c) {
		performLater();
	}

	/**
	 * A overrider pour executer du code avant qu'un evenement ne lance des
	 * moficiations. Si retourne false, pas de modifs.
	 * 
	 * @param e
	 * @return
	 */
	protected boolean beforeActionPerformed(ActionEvent e) {
		return true;
	}

	@Override
	public void run() {

		// pas d'actions dans l'EDT
		GuiUtils.throwIfOnEDT();

		if (projectm.isInitialized() == false)
			return;

		activeLayer = null;

		// récupérer le calque actif
		try {
			activeLayer = projectm.getActiveLayer();
		} catch (MapLayerException e) {
			Log.error(e);
			return;
		}

		// avant les modifications
		beforeBeginUpdate();

		// itérer les elements
		for (LayerElement elmt : activeLayer.getAllElements()) {

			// filter les elements selectionnés
			if (onlySelectedElements && elmt.isSelected() == false){
				continue;
			}

			// pas de filtre, modification systèmatique
			if (shapeFilters.size() <= 0) {
				updateLayerElement(elmt);
			}

			// un filtre, ne modifier que les objets dela bonne de la classe
			else if (shapeFilters.size() > 0) {
				if (shapeFilters.contains(elmt.getClass())) {
					updateLayerElement(elmt);
				}
			}

		}

		// fin des modifications
		updatesAreDone();

	}

	/**
	 * Méthode à Overrider
	 * <p>
	 * Exécutées avant les modifications
	 * 
	 * @param elmt
	 */
	protected void beforeBeginUpdate() {

	}

	/**
	 * Méthode à Overrider
	 * <p>
	 * Action à overrider pour modifier les elements de calque.
	 * 
	 * @param elmt
	 */
	protected void updateLayerElement(LayerElement elmt) {

	}

	/**
	 * Méthode à Overrider
	 * <p>
	 * Fin des modifications.
	 * 
	 * @param elmt
	 */
	protected void updatesAreDone() {

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (beforeActionPerformed(e)) {
			performLater();
		}
	}

	/**
	 * Lancer les modifications
	 */
	public void performLater() {
		ThreadManager.runLater(this);
	}

	public void setOnlySelectedElements(boolean onlySelectedElements) {
		this.onlySelectedElements = onlySelectedElements;
	}

	public boolean isOnlySelectedElements() {
		return onlySelectedElements;
	}

	public void addShapeFilter(Class filter) {
		shapeFilters.add(filter);
	}

	public void addEventFilter(Class filter) {
		eventFilters.add(filter);
	}

}
