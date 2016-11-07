package abcmap.utils.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import abcmap.draw.basicshapes.LayerElement;
import abcmap.gui.comps.buttons.HtmlCheckbox;
import abcmap.gui.comps.color.ColorButton;
import abcmap.managers.DrawManager;
import abcmap.managers.MapManager;
import abcmap.managers.ProjectManager;
import abcmap.managers.stub.MainManager;
import abcmap.utils.Utils;
import abcmap.utils.notifications.Notification;
import abcmap.utils.notifications.UpdatableByNotificationManager;

/**
 * Classe utilitaire permettant de mettre à jour un formulaire en fonction de
 * changements signalés à partir d'une action swing ou d'une notification de
 * gestionnaire.
 * 
 * @author remipassmoilesel
 *
 */
public class FormUpdater implements UpdatableByNotificationManager, Runnable,
		ActionListener {

	/** Filtrer les evenements qui déclenchent une mise à jour */
	protected ArrayList<Class> eventFilters;

	/** Filtrer les mises à jour en fonction de l'outil actif */
	protected ArrayList<Class> toolFilters;

	/**
	 * Si vrai, alors la mise à jour ne se fera que si le projet est initialisé
	 */
	protected boolean testProjectBeforeUpdate;

	protected ProjectManager projectm;
	protected DrawManager drawm;
	protected MapManager mapm;

	public FormUpdater() {

		projectm = MainManager.getProjectManager();
		drawm = MainManager.getDrawManager();
		mapm = MainManager.getMapManager();

		eventFilters = new ArrayList<Class>();
		toolFilters = new ArrayList<Class>();

		testProjectBeforeUpdate = false;

	}

	/**
	 * Méthode appelée sur l'EDT pour mettre à jour les formulaires.
	 * <p>
	 * <b>Attention:</b> Appeler cette méthode court circuite certains filtre.
	 * Appeler plutôt updateAllLater();
	 */
	@Override
	public void run() {

		// pas d'action hors de l'EDT
		GuiUtils.throwIfNotOnEDT();

		// tester si le projet est initialisé
		if (testProjectBeforeUpdate) {
			if (projectm.isInitialized() == false) {
				return;
			}
		}

		updateFields();
	}

	/**
	 * Utilitaire qui retourne le premier element selectionné passant le filtre
	 * ou null.
	 * 
	 * @param filter
	 * @return
	 */
	protected LayerElement getFirstSelectedElement(Class filter) {
		return drawm.getFirstSelectedElement(filter);
	}

	/**
	 * Utilitaire qui retourne le premier element selectionné passant le filtre
	 * ou null.
	 * 
	 * @param filter
	 * @return
	 */
	protected LayerElement getFirstSelectedElement() {
		return drawm.getFirstSelectedElement();
	}

	/**
	 * Utilitaire qui retourne le premier element selectionné passant le filtre
	 * ou null.
	 * 
	 * @param filter
	 * @return
	 */
	protected LayerElement getFirstSelectedElement(
			ArrayList<Class<? extends LayerElement>> filters) {
		return drawm.getFirstSelectedElement(filters);
	}

	/**
	 * Méthode à surcharger pour changer mettre à jour les champs de formulaire
	 */
	protected void updateFields() {

		// pas d'actions hors de l'EDT
		GuiUtils.throwIfNotOnEDT();

	}

	/**
	 * Reception d'un evenement de la part d'un gestionnaire de notification
	 */
	@Override
	public void notificationReceived(Notification arg) {

		// filter les arguments
		if (eventFilters.size() > 0) {
			if (eventFilters.contains(arg.getClass()) == false) {
				return;
			}
		}

		updateAllLater();
	}

	/**
	 * Mettre à jour le formulaire sur l'EDT
	 */
	public void updateAllLater() {

		// filtrer en fonction de l'outil actif
		if (toolFilters.size() > 0) {
			if (toolFilters.contains(drawm.getCurrentTool().getClass()) == false) {
				return;
			}
		}

		SwingUtilities.invokeLater(this);
	}

	/**
	 * Reception d'un evenement d'action
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		updateAllLater();
	}

	/**
	 * Ajoute un filtre qui empechera la mise à jour du formulaire en fonction
	 * du type d'evenement reçu
	 * 
	 * @param filter
	 */
	public void addEventFilter(Class filter) {
		eventFilters.add(filter);
	}

	/**
	 * Ajoute un filtre qui empechera la mise à jour du formulaire en fonction
	 * de l'outil actif
	 * 
	 * @param filter
	 */
	public void addDrawingToolFilter(Class filter) {
		toolFilters.add(filter);
	}

	/**
	 * Utilitaire de mise à jour de composant
	 * 
	 * @param comp
	 * @param value
	 */
	protected void updateComponentWithoutFire(ColorButton comp, Color value) {
		if (Utils.safeEquals(comp.getColor(), value) == false) {
			comp.setColor(value);
		}
	}

	/**
	 * Utilitaire de mise à jour de composant
	 * 
	 * @param comp
	 * @param value
	 */
	protected void updateComponentWithoutFire(JTextComponent comp, String value) {
		GuiUtils.changeText(comp, value);
	}

	/**
	 * Utilitaire de mise à jour de composant
	 * 
	 * @param comp
	 * @param value
	 */
	protected void updateComponentWithoutFire(HtmlCheckbox comp, boolean value) {
		if (Utils.safeEquals(comp.isSelected(), value) == false) {
			GuiUtils.setSelected(comp, value);
		}
	}

	/**
	 * Utilitaire de mise à jour de composant
	 * 
	 * @param comp
	 * @param value
	 */
	protected void updateComponentWithoutFire(AbstractButton comp, boolean value) {
		if (Utils.safeEquals(comp.isSelected(), value) == false) {
			GuiUtils.setSelected(comp, value);
		}
	}

	/**
	 * Utilitaire de mise à jour de composant
	 * 
	 * @param comp
	 * @param value
	 */
	protected void updateComponentWithoutFire(JComboBox comp, Object value) {
		if (Utils.safeEquals(comp.getSelectedItem(), value) == false) {
			GuiUtils.changeWithoutFire(comp, value);
		}
	}

}
