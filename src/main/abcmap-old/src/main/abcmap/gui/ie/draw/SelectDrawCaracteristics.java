package abcmap.gui.ie.draw;

import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import abcmap.draw.DrawConstants;
import abcmap.draw.basicshapes.DrawProperties;
import abcmap.draw.basicshapes.LayerElement;
import abcmap.draw.styles.LineStyle;
import abcmap.draw.styles.Texture;
import abcmap.gui.comps.draw.ComboLineStyle;
import abcmap.gui.comps.draw.ComboTexture;
import abcmap.gui.dock.comps.blockitems.SimpleBlockItem;
import abcmap.gui.ie.InteractionElement;
import abcmap.managers.Log;
import abcmap.project.properties.DrawPropertiesContainer;
import abcmap.project.utils.ShapeUpdater;
import abcmap.utils.gui.FormUpdater;
import abcmap.utils.gui.GuiUtils;
import net.miginfocom.swing.MigLayout;

public class SelectDrawCaracteristics extends InteractionElement {

	private static final Integer[] PREDEFINED_SIZE = new Integer[] { 5, 10, 15,
			20, 25 };

	private ComboLineStyle cbStroke;
	private JComboBox<Integer> cbThickness;
	private JComboBox<Texture> cbTexture;

	public SelectDrawCaracteristics() {

		// caracteristiques
		this.label = "Caractéristiques de tracé";
		this.help = "Sélectionnez ici les caractéristiques de tracépour vos formes. Vous pouvez utiliser des "
				+ "traits pleins ou pointillés, ...";

		// affichage complexe dans la recherche
		this.displaySimplyInSearch = false;

	}

	@Override
	protected Component createPrimaryGUI() {

		JPanel panel = new JPanel(new MigLayout("insets 5"));

		// largeur des contrôles
		String compWidth = "width 150, ";

		// texture
		GuiUtils.addLabel("Texture:", panel, "wrap");

		cbTexture = new ComboTexture();
		cbTexture
				.addActionListener(new Performer(DrawConstants.MODIFY_TEXTURE));
		panel.add(cbTexture, compWidth + wrap15);

		// type de trait
		GuiUtils.addLabel("Type de trait:", panel, "wrap");

		cbStroke = new ComboLineStyle();
		cbStroke.addActionListener(new Performer(DrawConstants.MODIFY_LINESTYLE));
		panel.add(cbStroke, compWidth + wrap15);

		// epaisseur du trait
		GuiUtils.addLabel("Epaisseur de trait:", panel, "wrap");

		cbThickness = new JComboBox<Integer>(PREDEFINED_SIZE);
		cbThickness.setEditable(true);
		cbThickness.addActionListener(new Performer(
				DrawConstants.MODIFY_LINESTYLE));
		panel.add(cbThickness, compWidth + wrap15);

		// ecouter les changements en provenance du gestionnaire d'evenement
		notifm.setDefaultUpdatableObject(new CustomFormUpdater());
		drawm.getNotificationManager().addObserver(this);
		projectm.getNotificationManager().addObserver(this);

		return panel;
	}

	private class Performer extends ShapeUpdater {

		public Performer(DrawConstants mode) {
			this.mode = mode;
		}

		@Override
		protected void beforeBeginUpdate() {
			super.beforeBeginUpdate();

			// recuperer les caracteristiques du trait
			DrawProperties st = drawm.getNewStroke();
			DrawPropertiesContainer pp = (DrawPropertiesContainer) st
					.getProperties();

			pp.linestyle = getSelectedLinestyle().toString();
			pp.thickness = getSelectedSize();
			pp.texture = getSelectedTexture().toString();

			setProperties(pp);

			// mettre à jour le manager de dessin
			drawm.setStroke(st);

		}
	}

	private class CustomFormUpdater extends FormUpdater {

		@Override
		protected void updateFields() {
			super.updateFields();

			// recuperer la premiere forme selectionnée
			LayerElement shp = (LayerElement) getFirstSelectedElement();
			DrawProperties str = shp != null ? shp.getStroke() : drawm
					.getNewStroke();

			// mise à jour des composants
			updateComponentWithoutFire(cbTexture, str.getTexture());
			updateComponentWithoutFire(cbStroke, str.getLinestyle());
			updateComponentWithoutFire(cbThickness, str.getThickness());

		}

	}

	private LineStyle getSelectedLinestyle() {

		try {
			return (LineStyle) cbStroke.getSelectedItem();
		} catch (Exception e) {
			Log.error(e);
			return null;
		}

	}

	private Texture getSelectedTexture() {

		try {
			return (Texture) cbTexture.getSelectedItem();
		} catch (Exception e) {
			Log.error(e);
			return null;
		}

	}

	private Integer getSelectedSize() {

		try {
			return (Integer) cbThickness.getSelectedItem();
		} catch (Exception e) {
			Log.error(e);
			return null;
		}

	}
}
