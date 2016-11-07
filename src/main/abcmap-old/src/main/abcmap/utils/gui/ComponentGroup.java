package abcmap.utils.gui;

import java.awt.Component;
import java.util.ArrayList;

import javax.swing.AbstractButton;

public class ComponentGroup {

	private ArrayList<Component> components;

	public ComponentGroup() {
		this.components = new ArrayList<Component>();
	}

	public void setEnabled(boolean val) {

		GuiUtils.throwIfNotOnEDT();

		for (Component comp : components) {
			if (comp != null && comp.isEnabled() != val) {
				comp.setEnabled(val);
			}
		}
	}

	public void setSelectedWithoutFire(boolean val) {

		GuiUtils.throwIfNotOnEDT();

		for (Component comp : components) {

			// seulement les elements concernés par la modif
			if (comp instanceof AbstractButton == false)
				continue;

			AbstractButton btn = (AbstractButton) comp;

			if (btn != null && btn.isSelected() != val) {
				GuiUtils.setSelected(btn, val);
			}
		}

	}

	public void add(Component comp) {
		components.add(comp);
	}

	public void remove(Component comp) {
		components.remove(comp);
	}

}
