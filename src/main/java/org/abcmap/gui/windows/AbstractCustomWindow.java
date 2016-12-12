package org.abcmap.gui.windows;

import org.abcmap.core.configuration.ConfigurationConstants;
import org.abcmap.core.managers.GuiManager;
import org.abcmap.core.managers.MainManager;

import javax.swing.*;

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
