package abcmap.importation;

import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import abcmap.exceptions.MapImportException;
import abcmap.managers.ConfigurationManager;
import abcmap.managers.GuiManager;
import abcmap.managers.ImportManager;
import abcmap.managers.Log;
import abcmap.managers.stub.MainManager;
import abcmap.utils.Utils;
import abcmap.utils.gui.GuiUtils;
import abcmap.utils.threads.ThreadAccessControl;

/**
 * Objet permettant le lancement de la configuration visuelle de recadrage.
 * 
 * @author remipassmoilesel
 *
 */
public class CropConfigurator {

	private ArrayList<Component> visibleFrames;
	private String mode;

	private ImportManager importm;
	private ConfigurationManager configm;

	private GuiManager guim;

	public static final String FOR_SCREEN_IMPORT = "FOR_SCREEN_IMPORT";

	public static final String FOR_DIRECTOY_IMPORT = "FOR_DIRECTOY_IMPORT";

	public CropConfigurator(String mode) {

		this.importm = MainManager.getImportManager();
		this.configm = MainManager.getConfigurationManager();
		this.guim = MainManager.getGuiManager();

		this.mode = mode;
	}

	public void start() throws IOException, MapImportException {

		// toujours lancer hors de l'EDT
		GuiUtils.throwIfOnEDT();

		// pas d'execution intempestive
		if (ThreadAccessControl.get(0).askAccess() == false) {
			return;
		}

		// l'image qui sera affichée en fond
		BufferedImage bg = null;

		// recuperer les fenetres visibles
		visibleFrames = MainManager.getGuiManager().getVisibleWindows();

		// parametrer à partir d'une image de dossier
		if (CropConfigurator.FOR_DIRECTOY_IMPORT.equals(mode)) {

			// verifier si le dossier existe
			File dir = new File(configm.getDirectoryImportPath());
			if (dir.isDirectory() == false) {
				ThreadAccessControl.get(0).releaseAccess();

				// le dossier n'existe pas: exception
				throw new IOException("Invalid directory: "
						+ dir.getAbsolutePath());
			}

			// récuperer la premiere image du dossier
			File img = null;
			for (File f : dir.listFiles()) {
				String ext = Utils.getExtension(f.getAbsolutePath());
				if (importm.isValidExtensionsForTile(ext)) {
					img = f;
					break;
				}
			}

			// lire l'image
			try {
				bg = ImageIO.read(img);
			}

			catch (Exception e) {
				ThreadAccessControl.get(0).releaseAccess();
				throw new IOException(e);
			}

			// masquer les fenetres
			for (Component c : visibleFrames) {
				c.setVisible(false);
			}

			// attendre que l'effet soit visible
			try {
				Thread.sleep(configm.getWindowHidingDelay());
			} catch (InterruptedException e) {
				Log.error(e);
			}

		}

		// configuration sur l'écran
		else if (CropConfigurator.FOR_SCREEN_IMPORT.equals(mode)) {
			bg = importm.catchScreen(visibleFrames, false);
		}

		// mode inconnu
		else {
			throw new IllegalArgumentException("Invalid mode: " + mode);
		}

		// enregistrer une taille image plus petite si necesaire

		// ouverture de la fenetre de selection de zone
		final BufferedImage finalBg = bg;
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				guim.showCropWindow(finalBg);
			}
		});

	}

	public void stop() {

		// toujours lancer hors de l'EDT
		GuiUtils.throwIfOnEDT();

		// pas d'execution intempestive
		if (ThreadAccessControl.get(0).askAccess() == false) {
			return;
		}

		// arreter
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {

				// fermer la fenêtre de configuration
				guim.getCropWindow().setVisible(false);

				// attendre un peu pour effet 'soft'
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					Log.error(e);
				}

				// restaurer la visibilité des fenêtres
				for (Component c : visibleFrames) {
					c.setVisible(true);
				}

				// arrêt
				ThreadAccessControl.get(0).releaseAccess();

			}
		});

	}

}
