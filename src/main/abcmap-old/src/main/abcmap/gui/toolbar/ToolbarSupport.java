package abcmap.gui.toolbar;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

public class ToolbarSupport extends JPanel {

	private static final Integer TOOLBAR_HEIGHT = 40;

	public ToolbarSupport() {
		super(new MigLayout("insets 5, gap 5"));
		this.setBorder(BorderFactory.createLineBorder(Color.lightGray));
	}

	public void addToolbar(Component c) {
		add(c, "height " + TOOLBAR_HEIGHT);
	}

	@Deprecated
	@Override
	public Component add(Component comp) {
		return super.add(comp);
	}

	@Deprecated
	@Override
	public Component add(String name, Component comp) {
		return super.add(name, comp);
	}

	@Deprecated
	@Override
	public Component add(Component comp, int index) {
		return super.add(comp, index);
	}

	@Deprecated
	@Override
	public void add(Component comp, Object constraints) {
		super.add(comp, constraints);
	}

	@Deprecated
	@Override
	public void add(Component comp, Object constraints, int index) {
		super.add(comp, constraints, index);
	}

}
