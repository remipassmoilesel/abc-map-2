package abcmap.gui.comps.draw;

import javax.swing.JComboBox;

import abcmap.draw.styles.LineStyle;

public class ComboLineStyle extends JComboBox<LineStyle> {

	private LineStyleRenderer customRenderer;

	public ComboLineStyle() {
		super(LineStyle.values());

		setEditable(false);
		
		customRenderer = new LineStyleRenderer();
		setRenderer(customRenderer);
	}

	public static LineStyleRenderer getDefaultRenderer() {
		return new LineStyleRenderer();
	}

}
