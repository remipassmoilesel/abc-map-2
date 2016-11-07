package abcmap.gui.comps.draw;

import javax.swing.JComboBox;

import abcmap.draw.styles.Texture;

public class ComboTexture extends JComboBox<Texture> {

	private TextureRenderer customRenderer;

	public ComboTexture() {
		super(Texture.values());

		setEditable(false);
		
		customRenderer = new TextureRenderer();
		setRenderer(customRenderer);
	}
	
	public static LineStyleRenderer getDefaultRenderer() {
		return new LineStyleRenderer();
	}

}
