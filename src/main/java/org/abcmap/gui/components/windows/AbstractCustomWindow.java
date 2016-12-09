package org.abcmap.gui.components.windows;

import javax.swing.JFrame;

import abcmap.configuration.ConfigurationConstants;
import abcmap.managers.GuiManager;
import abcmap.managers.stub.MainManager;
import org.abcmap.core.configuration.ConfigurationConstants;
import org.abcmap.core.managers.GuiManager;
import org.abcmap.core.managers.MainManager;

public abstract class AbstractCustomWindow extends JFrame {

	private GuiManager guim;

	public AbstractCustomWindow() {

		setTitle("");

		guim = MainManager.getGuiManager();

		guim.setWindowIconFor(this);

	}

	@Override
	public void setTitle(String arg0) {
		if (arg0.isEmpty() == false) {
			arg0 = " - " + arg0;
		}
		super.setTitle(ConfigurationConstants.SOFTWARE_NAME + arg0);
	}

}
