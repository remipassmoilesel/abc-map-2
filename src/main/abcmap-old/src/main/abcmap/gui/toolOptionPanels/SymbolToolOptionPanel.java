package abcmap.gui.toolOptionPanels;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import abcmap.draw.DrawConstants;
import abcmap.draw.shapes.Symbol;
import abcmap.events.DrawManagerEvent;
import abcmap.events.ProjectEvent;
import abcmap.gui.comps.draw.symbols.SymbolSelector;
import abcmap.managers.Log;
import abcmap.project.properties.ShapeProperties;
import abcmap.project.utils.ShapeUpdater;
import abcmap.utils.gui.GuiUtils;

public class SymbolToolOptionPanel extends ToolOptionPanel {

	private static final Integer[] PREDEFINED_SIZES = new Integer[] { 50, 75,
			100, 150, 200 };

	private JComboBox cbSymbolSize;
	private SymbolSelector symbolSelector;

	/**
	 * Create the panel.
	 */
	public SymbolToolOptionPanel() {

		GuiUtils.throwIfNotOnEDT();

		// selection de la taille des symboles
		GuiUtils.addLabel("Taille: ", this, "wrap");

		cbSymbolSize = new JComboBox(PREDEFINED_SIZES);
		cbSymbolSize.setEditable(true);
		cbSymbolSize
				.addActionListener(new Performer(DrawConstants.MODIFY_SIZE));

		add(cbSymbolSize, gapLeft + largeWrap);

		// selection de symbole
		GuiUtils.addLabel("Symbole: ", this, "wrap");

		symbolSelector = new SymbolSelector();
		symbolSelector.getListenerHandler().add(
				new Performer(DrawConstants.MODIFY_SYMBOL_CODE));

		add(symbolSelector, gapLeft);

		// ecouter le projet et le gestionnaire de dessin
		FormUpdater formUpdater = new FormUpdater();
		formUpdater.addEventFilter(ProjectEvent.class);
		formUpdater.addEventFilter(DrawManagerEvent.class);
		observer.addObserver(drawm);
		observer.addObserver(projectm);
		observer.setDefaultUpdatableObject(formUpdater);

		formUpdater.run();
	}

	private class FormUpdater extends abcmap.utils.gui.FormUpdater {

		@Override
		protected void updateFields() {
			super.updateFields();

			// recuperer la premiere forme selectionnée
			Symbol shp = (Symbol) getFirstSelectedElement(Symbol.class);

			// pas de selection, mise à jour à partir de la forme temoin
			if (shp == null) {
				shp = drawm.getWitnessSymbol();
			}

			updateComponentWithoutFire(cbSymbolSize, shp.getSize());
			symbolSelector.updateValues(shp.getSymbolSetName(),
					shp.getSymbolCode(), false);

		}

	}

	private class Performer extends ShapeUpdater {

		public Performer(DrawConstants mode) {
			setMode(mode);
			addShapeFilter(Symbol.class);
		}

		@Override
		protected void beforeBeginUpdate() {
			super.beforeBeginUpdate();

			// rassemblement des valeurs a affecter
			ShapeProperties pp = new ShapeProperties();

			pp.symbolSetName = getSelectedSet();
			pp.symbolCode = getSelectedCode();
			pp.size = getSelectedSize();

			setProperties(pp);

			// enregistrement des preferences pour creation
			drawm.updateWitness(Symbol.class, mode, pp);

		}

	}

	public Integer getSelectedSize() {
		try {
			return (Integer) cbSymbolSize.getSelectedItem();
		} catch (Exception e) {
			Log.error(e);
			return null;
		}
	}

	public int getSelectedCode() {
		return symbolSelector.getSelectedSymbolCode();
	}

	public String getSelectedSet() {
		return symbolSelector.getSelectedSetName();
	}

}
