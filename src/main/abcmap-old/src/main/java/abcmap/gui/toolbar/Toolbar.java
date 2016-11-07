package abcmap.gui.toolbar;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import abcmap.gui.ie.InteractionElement;

public class Toolbar extends JPanel {

	public Toolbar() {
		super(new MigLayout("insets 2, gap 2"));
		setBorder(BorderFactory.createLineBorder(Color.lightGray, 1));
	}

	public void addInteractionElement(InteractionElement element) {
		add(new ToolbarButton(element), "width 35!, height 35!");
	}

}
