package abcmap.gui.toolOptionPanels;

import java.awt.event.ActionEvent;

import javax.swing.JLabel;

import abcmap.draw.DrawConstants;
import abcmap.draw.basicshapes.RectangleShape;
import abcmap.events.DrawManagerEvent;
import abcmap.events.MapEvent;
import abcmap.events.ProjectEvent;
import abcmap.geo.GeoConstants;
import abcmap.geo.GeoInfoMode;
import abcmap.gui.comps.geo.GeoInfoForm;
import abcmap.gui.comps.help.AttentionPanel;
import abcmap.managers.Log;
import abcmap.project.properties.ShapeProperties;
import abcmap.project.utils.ShapeUpdater;
import abcmap.utils.gui.GuiUtils;

public class RectangleShapeToolOptionPanel extends ToolOptionPanel {

	private Performer performer;
	private Class<? extends RectangleShape> shapeClass;
	private GeoInfoForm geoInfoForm;

	public RectangleShapeToolOptionPanel(
			Class<? extends RectangleShape> shapeClass) {

		GuiUtils.throwIfNotOnEDT();

		this.shapeClass = shapeClass;

		// avertissement de georeferencement
		add(new AttentionPanel(), "span, " + largeWrap);

		// informations géo
		GuiUtils.addLabel("Informations géographiques: ", this, "wrap");

		GeoInfoMode formMode = new GeoInfoMode("111001");
		geoInfoForm = new GeoInfoForm(formMode);
		geoInfoForm.getListenerHandler().add(new Performer());
		add(geoInfoForm);

		// mettre à jour les formuaires en fonction du projet, du gestionnaire
		// de dessin et de carte
		FormUpdater formUpdater = new FormUpdater();
		formUpdater.addEventFilter(DrawManagerEvent.class);
		formUpdater.addEventFilter(ProjectEvent.class);
		formUpdater.addEventFilter(MapEvent.class);

		// ecouter le projet et le gest de dessin
		observer.setDefaultUpdatableObject(formUpdater);
		projectm.getNotificationManager().addObserver(this);
		drawm.getNotificationManager().addObserver(this);
		mapm.getNotificationManager().addObserver(this);

		// première mise à jour
		formUpdater.run();

	}

	private class Performer extends ShapeUpdater {

		public Performer() {
			addShapeFilter(shapeClass);
		}

		@Override
		protected boolean beforeActionPerformed(ActionEvent e) {

			GeoConstants ac = null;
			try {
				ac = GeoConstants.valueOf(e.getActionCommand());
			} catch (Exception e2) {
				Log.debug(e2);
				return false;
			}

			if (GeoConstants.DISPLAY_GEO_TEXT_SIZE.equals(ac)) {
				setMode(DrawConstants.MODIFY_TEXT_SIZE);
			}

			else {
				setMode(DrawConstants.MODIFY_GEOTEXTMODE);
			}

			return true;
		}

		@Override
		protected void beforeBeginUpdate() {
			super.beforeBeginUpdate();

			// rassemblement des valeurs a affecter
			ShapeProperties pp = new ShapeProperties();

			pp.geoInfoMode = geoInfoForm.getSelectedInfoMode().toString();
			pp.geoInfoSize = geoInfoForm.getSelectedTextsize();

			// proprietes pour application aux elemnts selectionnes
			setProperties(pp);

		}

	}

	/**
	 * Mettre à jour les formulaires
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class FormUpdater extends abcmap.utils.gui.FormUpdater {

		@Override
		protected void updateFields() {
			super.updateFields();

			// recuperer la premiere forme selectionnée
			RectangleShape shp = (RectangleShape) getFirstSelectedElement(shapeClass);

			// pas de selection, retour
			if (shp == null) {
				geoInfoForm.resetForm();
				return;
			}

			// mise à jour des composants
			geoInfoForm.changeInfomodeWithoutFire(shp.getGeoInfoMode());
			geoInfoForm.changeTextSizeWithoutFire(shp.getGeoTextSize());

		}

	}

}
