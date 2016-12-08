package org.abcmap.gui.components.texture;

import org.abcmap.core.draw.Texture;
import org.abcmap.gui.components.linestyle.LineStyleRenderer;

import javax.swing.*;

/**
 * Texture selection
 */
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
