package abcmap.draw.tools;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import abcmap.draw.basicshapes.DrawProperties;
import abcmap.draw.basicshapes.LayerElement;
import abcmap.exceptions.MapLayerException;
import abcmap.managers.CancelManager;
import abcmap.managers.DrawManager;
import abcmap.managers.GuiManager;
import abcmap.managers.Log;
import abcmap.managers.MapManager;
import abcmap.managers.ProjectManager;
import abcmap.managers.stub.MainManager;
import abcmap.project.layers.MapLayer;
import abcmap.utils.PrintUtils;
import abcmap.utils.notifications.HasNotificationManager;
import abcmap.utils.notifications.NotificationManager;

public abstract class MapTool extends MouseAdapter implements HasNotificationManager {

	protected NotificationManager observer;
	protected DrawManager drawm;
	protected GuiManager guim;
	protected ProjectManager projectm;
	protected MapManager mapm;
	protected CancelManager cancelm;
	protected String mode;
	protected DrawProperties stroke;

	public MapTool() {
		this.drawm = MainManager.getDrawManager();
		this.guim = MainManager.getGuiManager();
		this.projectm = MainManager.getProjectManager();
		this.mapm = MainManager.getMapManager();
		this.cancelm = MainManager.getCancelManager();

		this.mode = null;

		// a l'ecoute de drawCtrl
		this.observer = new NotificationManager(MapTool.this);
		MainManager.getDrawManager().getNotificationManager().addObserver(this);

	}

	protected void printForDebug(LayerElement elmt, Point p1) {

		PrintUtils.p();

		PrintUtils.pStackTrace(2);

		if (elmt != null) {

			Object[] keys = new Object[] { "Element", "Selected", "InteractionArea bounds", };

			Object[] values = new Object[] { elmt.getClass().getSimpleName(), elmt.isSelected(),
					elmt.getInteractionArea().getBounds() };

			PrintUtils.pObjectAndValues(elmt, keys, values);

		}

		if (p1 != null) {

			// point appartient à forme
			String p1InIa = elmt.getInteractionArea() != null
					? Boolean.toString(elmt.getInteractionArea().contains(p1)) : "null IA";

			Object[] keys = new Object[] { "Point P1", "P1 in IA", "InteractionArea bounds", };

			Object[] values = new Object[] { p1.x + " : " + p1.y, p1InIa,
					elmt.getInteractionArea().getBounds() };

			PrintUtils.pObjectAndValues(p1, keys, values);

		}

	}

	/**
	 * Retourne le calque actif ou null si une erreur survient
	 * 
	 * @return
	 */
	protected MapLayer checkProjetAndReturnActiveLayer() {

		// verifier le projet
		if (MainManager.getProjectManager().isInitialized() == false)
			return null;

		// recuperer le calque actif
		try {
			return MainManager.getProjectManager().getActiveLayer();
		} catch (MapLayerException e) {
			Log.debug(e);
			return null;
		}

	}

	/**
	 * Retourn vrai si le projet est initialisé et si le click correspond
	 * 
	 * @param ev
	 * @return
	 */
	protected boolean checkProjectAndLeftClick(MouseEvent arg0) {

		// verifier le clic
		if (SwingUtilities.isLeftMouseButton(arg0) == false)
			return false;

		// verifier le projet
		if (MainManager.getProjectManager().isInitialized() == false)
			return false;

		return true;

	}

	/**
	 * Retourn vrai si le projet est initialisé et si le click correspond
	 * 
	 * @param ev
	 * @return
	 */
	protected boolean checkProjectAndRightClick(MouseEvent arg0) {

		// verifier le clic
		if (SwingUtilities.isRightMouseButton(arg0) == false)
			return false;

		// verifier le projet
		if (projectm.isInitialized() == false)
			return false;

		return true;

	}

	/**
	 * Deselectionne tout si control n'est pas préssé
	 * 
	 * @param arg0
	 */
	protected void unselectAllIfCtrlNotPressed(MouseEvent arg0) {
		if (arg0.isControlDown() == false) {
			projectm.setAllElementsSelected(false);
		}
	}

	public void setToolMode(String mode) {
		this.mode = mode;
	}

	public String getToolMode() {
		if (mode == null)
			return null;
		else
			return new String(mode);
	}

	@Override
	public NotificationManager getNotificationManager() {
		return observer;
	}

	/**
	 * Affichage eventuel par l'outil
	 * 
	 * @param g2d
	 */
	public void drawOnCanvas(Graphics2D g2d) {
	}

	/**
	 * L'outil sera bientôt retiré, effectuer les actions d'arrêt du travail.
	 */
	public void stopWorking() {
	}

}