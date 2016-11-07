package abcmap.gui.comps.draw;

import java.awt.Dimension;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import abcmap.exceptions.InvalidInputException;
import abcmap.gui.comps.buttons.HtmlCheckbox;
import abcmap.gui.comps.textfields.DecimalTextField;
import abcmap.utils.Utils;
import abcmap.utils.gui.GuiUtils;
import abcmap.utils.gui.TextFieldDelayedAction;

public class MapDimensionsPanel extends JPanel {

	private DecimalTextField txtWidth;
	private DecimalTextField txtHeight;
	private HtmlCheckbox chkDynamicDimensions;

	public MapDimensionsPanel() {
		super(new MigLayout("insets 5"));

		txtWidth = new DecimalTextField(8);
		txtHeight = new DecimalTextField(8);

		GuiUtils.addLabel("Largeur: ", this, "");
		add(txtWidth, "width 50px!, wrap");

		GuiUtils.addLabel("Hauteur: ", this, "");
		add(txtHeight, "width 50px!, wrap");

		add(new JPanel(), "wrap");

		chkDynamicDimensions = new HtmlCheckbox("Dimensions dynamiques");
		add(chkDynamicDimensions, "span");
	}

	public HtmlCheckbox getChkDynamicDimensions() {
		return chkDynamicDimensions;
	}

	public Dimension getValues() throws InvalidInputException {
		try {
			return new Dimension(Integer.valueOf(txtWidth.getText()),
					Integer.valueOf(txtHeight.getText()));
		} catch (Exception e) {
			throw new InvalidInputException();
		}
	}

	public void updateDynamicDimsCheckBoxWithoutFire(boolean val) {

		GuiUtils.throwIfNotOnEDT();

		if (chkDynamicDimensions.isSelected() != val) {
			chkDynamicDimensions.setSelected(val);
		}

	}

	public void updateValuesWithoutFire(Dimension dim) {

		GuiUtils.throwIfNotOnEDT();

		// mettre à jour la largeur si necesaire
		if (Utils.safeEquals(txtWidth.getText(), dim.width) == false) {
			if (txtWidth.isFocusOwner() == false) {
				GuiUtils.changeText(txtWidth, String.valueOf(dim.width));
			}
		}

		// mettre à jour la hauteur si necesaire
		if (Utils.safeEquals(txtHeight.getText(), dim.height) == false) {
			if (txtHeight.isFocusOwner() == false) {
				GuiUtils.changeText(txtHeight,
						String.valueOf(dim.height));
			}
		}
	}

	public TextFieldDelayedAction[] addDelayedAction(Runnable run,
			boolean runOnEdt) {
		TextFieldDelayedAction del1 = TextFieldDelayedAction.delayedActionFor(
				txtWidth, run, runOnEdt);
		TextFieldDelayedAction del2 = TextFieldDelayedAction.delayedActionFor(
				txtHeight, run, runOnEdt);

		return new TextFieldDelayedAction[] { del1, del2 };
	}

}
