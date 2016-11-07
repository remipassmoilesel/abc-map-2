package abcmap.gui.ie.ressources;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import abcmap.configuration.ConfigurationConstants;
import abcmap.gui.ie.InteractionElement;
import abcmap.managers.Log;
import abcmap.utils.gui.GuiUtils;

public class GoToOpenStreetMap extends AbstractGoToWebsite {

	public GoToOpenStreetMap() {
		super(Mode.GO_TO_OPENSTREETMAP);
	}

}
