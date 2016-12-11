package org.abcmap.gui.ie.toolbar;

import abcmap.gui.ie.display.windowmode.WindowModeSelector;
import abcmap.gui.ie.display.zoom.ResetZoom;
import abcmap.gui.ie.display.zoom.ZoomIn;
import abcmap.gui.ie.display.zoom.ZoomOut;
import abcmap.gui.toolbar.Toolbar;
import org.abcmap.gui.components.toolbar.Toolbar;
import org.abcmap.gui.toProcess.gui.ie.display.windowmode.WindowModeSelector;
import org.abcmap.gui.toProcess.gui.ie.display.zoom.ResetZoom;
import org.abcmap.gui.toProcess.gui.ie.display.zoom.ZoomIn;
import org.abcmap.gui.toProcess.gui.ie.display.zoom.ZoomOut;

public class DisplayToolbar extends Toolbar {

	public DisplayToolbar() {

		// zoom
		addInteractionElement(new ZoomIn());
		addInteractionElement(new ZoomOut());
		addInteractionElement(new ResetZoom());

		// mode d'affichage
		WindowModeSelector ws = new WindowModeSelector();
		add(ws.getPrimaryGUI());
	}

}
