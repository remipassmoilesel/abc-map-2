package abcmap.importation;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import abcmap.configuration.Configuration;
import abcmap.exceptions.MapImportException;
import abcmap.managers.Log;
import abcmap.managers.stub.MainManager;
import abcmap.utils.gui.GuiUtils;

/**
 * Ne doit PAS etre lance dans l'EDT mais dans un thread
 * 
 * @author Internet
 * 
 */
public class ScreenCatcher implements Runnable {

	private Robot robot;

	/** Composants à masquer avant capture */
	private ArrayList<Component> componentsToHide;

	/** Composants à rendre visible après capture */
	private ArrayList<Component> componentsToShow = new ArrayList<Component>();

	/** Image resultat */
	private BufferedImage rslt;

	private boolean displayAgainAfterCatch = true;

	public ScreenCatcher(ArrayList<Component> comps) throws MapImportException {

		// capture d'ecran
		try {
			this.robot = new Robot();
		} catch (AWTException e) {
			Log.error(e);
			throw new MapImportException(e);
		}

		this.componentsToHide = comps;
	}

	@Override
	public void run() {

		GuiUtils.throwIfOnEDT();

		// recuperer la configuration
		Configuration config = MainManager.getConfigurationManager().getConfiguration();

		// masquer les composants
		if (componentsToHide != null) {

			// ne remontrer que les composants qui étaient visibles
			componentsToShow = new ArrayList<Component>(componentsToHide.size());

			for (final Component c : componentsToHide) {

				if (c == null) {
					continue;
				}

				if (c.isVisible()) {

					// ajout la liste pour reapparition
					componentsToShow.add(c);

					// masquage
					try {
						SwingUtilities.invokeAndWait(new Runnable() {
							@Override
							public void run() {
								c.setVisible(false);
							}
						});
					} catch (InvocationTargetException | InterruptedException e) {
						Log.error(e);
					}

				}
			}

			// attendre si des composants ont été masqués
			if (componentsToShow.size() > 0) {
				try {
					Thread.sleep(config.WINDOW_HIDDING_DELAY);
				} catch (InterruptedException e) {
					Log.error(e);
				}
			}

		}

		// capturer l'écran
		Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		rslt = robot.createScreenCapture(new Rectangle(0, 0, screen.width, screen.height));

		// re-afficher
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// rafficher les elements
				if (componentsToShow != null && displayAgainAfterCatch) {
					for (Component c : componentsToShow) {
						c.setVisible(true);
					}
				}
			}
		});

	}

	public BufferedImage getResult() {
		return rslt;
	}

	public void displayAgainAfterCatch(boolean val) {
		displayAgainAfterCatch = val;
	}

	public ArrayList<Component> getComponentsToShow() {
		return componentsToShow;
	}

}