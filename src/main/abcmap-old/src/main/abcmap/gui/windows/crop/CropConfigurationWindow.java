package abcmap.gui.windows.crop;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Area;

import javax.swing.SwingUtilities;

import abcmap.events.ConfigurationEvent;
import abcmap.managers.ConfigurationManager;
import abcmap.managers.GuiManager;
import abcmap.managers.ImportManager;
import abcmap.managers.stub.MainManager;
import abcmap.utils.gui.GuiUtils;
import abcmap.utils.notifications.Notification;
import abcmap.utils.notifications.HasNotificationManager;
import abcmap.utils.notifications.NotificationManager;
import abcmap.utils.notifications.UpdatableByNotificationManager;
import abcmap.utils.threads.ThreadManager;

/**
 * Configuration visuelle de recadrage.
 * 
 * @author remipassmoilesel
 *
 */
public class CropConfigurationWindow extends FullScreenPictureWindow
		implements HasNotificationManager {

	private ImportManager importm;
	private NotificationManager observer;

	private WindowListener closeWL;
	private CropDimensionsDialog dialog;
	private ConfigurationManager configm;
	private CropSelectionRectangle selection;
	private CropSelectionTool selectionTool;
	private GuiManager guim;
	private float transparencyCoeff;
	private Area veilAroundSelection;

	public CropConfigurationWindow() {

		this.importm = MainManager.getImportManager();
		this.configm = MainManager.getConfigurationManager();
		this.guim = MainManager.getGuiManager();

		// la selection
		this.selection = new CropSelectionRectangle();

		// transparence du voile autour de la selection
		transparencyCoeff = 0.6f;

		// l'outil de traçage
		this.selectionTool = new CropSelectionTool(this);
		imagePane.addMouseListener(selectionTool);
		imagePane.addMouseMotionListener(selectionTool);

		// ecouter pour stopper la config
		this.closeWL = new WindowListener();
		this.addWindowListener(closeWL);

		// dialog avec valeurs numeriques de la selection
		this.dialog = new CropDimensionsDialog(this);
		dialog.addWindowListener(closeWL);

		// ecouter le controleur de configuration
		this.observer = new NotificationManager(this);
		observer.setDefaultUpdatableObject(new CropSelectionUpdater());

	}

	/**
	 * Mise à jour du GUI en fonction des changements de parametres de
	 * configuration
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class CropSelectionUpdater implements UpdatableByNotificationManager, Runnable {

		@Override
		public void notificationReceived(Notification arg) {
			// Reception d'un avis de changement de config
			if (arg instanceof ConfigurationEvent) {
				SwingUtilities.invokeLater(this);
			}
		}

		/**
		 * Mise à jour sur l'EDT
		 */
		@Override
		public void run() {

			// recuperer les dimensions
			Rectangle r = configm.getCropRectangle();

			// mettre à jour le dialog
			dialog.refresh();

			// mettre à jour la selection visuelle
			updateVisualSelection(r);

			// rafraichir l'affichage
			refreshImagePane();

		}

	}

	/**
	 * Affiche ou masque l'ensemble des fenêtre de configuration de recadrage.
	 * 
	 */
	@Override
	public void setVisible(boolean value) {

		// erreur si hors de l'EDT
		GuiUtils.throwIfNotOnEDT();

		// montrer ou cacher cette fenêtre
		super.setVisible(value);

		// montrer l'ensemble
		if (value) {

			// mettre à l'écoute de la configuration
			configm.getNotificationManager().addObserver(this);

			// maj de la selection
			updateVisualSelection(configm.getCropRectangle());

			// affichage de cette fenêtre et mise en premier plan
			this.toFront();

			// mettre à la position
			dialog.moveToDefaultPosition();

			// afficher le dialog de dimensions
			dialog.setVisible(true);

			this.repaint();
		}

		else {

			// fermer le dialog
			dialog.setVisible(false);

			// deconnecter la fenetre de la configuration
			configm.getNotificationManager().removeObserver(CropConfigurationWindow.this);

			// arreter la configuration dans un thread
			ThreadManager.runLater(new Runnable() {
				@Override
				public void run() {
					importm.stopCropConfiguration();
				}
			});
		}

	}

	@Override
	protected void paintImagePane(Graphics2D g2d) {
		super.paintImagePane(g2d);

		// dessin du voile transparent
		Graphics2D g2dT = (Graphics2D) g2d.create();
		GuiUtils.applyQualityRenderingHints(g2dT);
		g2dT.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparencyCoeff));
		g2dT.setColor(Color.white);
		g2dT.fill(veilAroundSelection);

		// dessiner le rectangle
		selection.draw(g2d, null);
	}

	public void computeVeilShape() {

		// recuperer les dimensions totales du panneau
		Dimension d = imagePane.getSize();

		// creer une zone de la taille du panneau
		veilAroundSelection = new Area(new Rectangle(0, 0, d.width, d.height));

		// soustraire la selection
		veilAroundSelection.subtract(new Area(selection.getRectangle()));
	}

	/**
	 * Mettre à jour la sélection visuelle à partir du rectangle passé en
	 * paramètre. Le rectangle doit être dans l'espace de coordonnées l'image,
	 * pas de l'écran.
	 */
	public void updateVisualSelection(Rectangle r) {

		// mettre à jour la selection si pas d'operation en cours
		if (selectionTool.isDrawing() == false && selectionTool.isResizing() == false) {

			// transformer le rectangle
			Rectangle transR = transformToScreenSpace(r);

			// recuperer puis affecter les coordonnées
			selection.setPosition(transR.getLocation());
			selection.setDimensions(transR.getSize());

		}

		// rafraichir la forme
		selection.refreshShape();

		// recalculer le voile
		computeVeilShape();

	}

	/**
	 * Valide les valeurs de la selection visuelle et les enregistre dans le
	 * manager d'import
	 */
	public void validVisualSelection() {

		// récupérer la sélection
		Rectangle selectionRect = selection.getMaximumBounds();

		// la transformer
		selectionRect = transformToImageSpace(selectionRect);

		// affecter les valeurs
		configm.setCropRectangle(selectionRect);

		// recalculer le voile
		computeVeilShape();

	}

	/**
	 * Affecte des coordonnées hors de l'écran à la sélection
	 */
	public void hideSelection() {
		selection.setPosition(new Point(-50, -50));
		selection.setDimensions(new Dimension(10, 10));
		selection.refreshShape();
	}

	/**
	 * Ecouter la fenêtre pour fermeture de cette fenêtre et du dialog
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class WindowListener extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent arg0) {
			setVisible(false);
		}
	}

	public CropSelectionRectangle getSelection() {
		return selection;
	}

	@Override
	public NotificationManager getNotificationManager() {
		return observer;
	}

}
