package abcmap.gui.comps.share;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import abcmap.configuration.ConfigurationConstants;
import abcmap.events.MapEvent;
import abcmap.events.ProjectEvent;
import abcmap.exceptions.MapManagerException;
import abcmap.geo.Coordinate;
import abcmap.geo.GeoConstants;
import abcmap.gui.comps.progressbar.HasProgressbarManager;
import abcmap.gui.comps.progressbar.ProgressbarManager;
import abcmap.managers.GuiManager;
import abcmap.managers.Log;
import abcmap.managers.MapManager;
import abcmap.managers.ProjectManager;
import abcmap.managers.stub.MainManager;
import abcmap.utils.gui.GuiUtils;
import abcmap.utils.notifications.Notification;
import abcmap.utils.notifications.HasNotificationManager;
import abcmap.utils.notifications.NotificationManager;
import abcmap.utils.notifications.UpdatableByNotificationManager;
import net.miginfocom.swing.MigLayout;

public class StatusBar extends JPanel implements HasNotificationManager,
		HasProgressbarManager {

	/** Si vrai, affiche les erreurs */
	private boolean showErrors = false;

	private NotificationManager notifm;

	/** Afficher les coordonnées en degrés */
	private JLabel labelPosition;

	/** Afficher l'echelle ou le zoom */
	private JLabel labelScale;

	/** Barre de progression principale du programme */
	private ProgressbarManager progressbarManager;

	/** Report de la position de la souris */
	private MousePositionListener cursorListener;

	/** Mise à jour des champs */
	private Updater updater;

	/** Position courante de la souris */
	private Coordinate mouseCoords;

	/** Etat de l'ecoutede la positions de la souris */
	private boolean mouseListening;

	private MapManager mapm;
	private ProjectManager projectm;
	private GuiManager guim;

	public StatusBar() {
		super(new MigLayout("insets 5"));

		GuiUtils.throwIfNotOnEDT();

		this.projectm = MainManager.getProjectManager();
		this.mapm = MainManager.getMapManager();
		this.guim = MainManager.getGuiManager();

		mouseCoords = new Coordinate();

		// dimensions des élements
		int height = 25;
		Dimension statusbarDim = new Dimension(500, 30);
		Dimension labelPositionDim = new Dimension(350, height);
		Dimension progressbarDim = new Dimension(150, height);
		Dimension labelScaleDim = new Dimension(150, height);

		// bordure
		this.setBorder(BorderFactory.createLineBorder(Color.gray));

		// dimensions
		setPreferredSize(statusbarDim);

		// afficher l'echelle ou le zoom
		labelScale = new JLabel();
		labelScale.setPreferredSize(labelScaleDim);
		this.add(labelScale);

		// afficher les coordonnees en degres
		labelPosition = new JLabel();
		labelPosition.setPreferredSize(labelPositionDim);
		this.add(labelPosition);

		// manager de barre de progression
		progressbarManager = new ProgressbarManager();

		// ajout de la barre de progression
		JProgressBar progressBar = progressbarManager.getProgressbar();
		progressBar.setPreferredSize(progressbarDim);
		this.add(progressBar);

		// ajout de l'étiquette de progression
		JLabel progressLabel = progressbarManager.getLabel();
		this.add(progressLabel);

		// masquer les omposants par défaut
		progressbarManager.setComponentsVisible(false);

		// ecouter la souris pour afficher la position du curseur
		cursorListener = new MousePositionListener();

		// mise à jour des elements
		this.updater = new Updater();

		// à l'écoute du projet et de la carte
		this.notifm = new NotificationManager(this);
		notifm.setDefaultUpdatableObject(updater);

		projectm.getNotificationManager().addObserver(this);
		mapm.getNotificationManager().addObserver(this);

		// ne pas initialiser maintenant, attendre une notification
		guim.addInitialisationOperation(new Runnable() {
			@Override
			public void run() {

				// mettre à jour le formulaire
				updater.run();

				// ecouter la souris
				mouseListening(true);
			}
		});
	}

	public boolean isMouseListening() {
		return mouseListening;
	}

	public void mouseListening(boolean val) {

		// dans tous les cas suppression du listener, pour éviter deux
		// enregistrements consécutifs
		mapm.getMapComponent().removeMouseListener(cursorListener);
		mapm.getMapComponent().removeMouseMotionListener(cursorListener);

		if (val == true) {
			mapm.getMapComponent().addMouseListener(cursorListener);
			mapm.getMapComponent().addMouseMotionListener(cursorListener);
		}

		mouseListening = val;

	}

	@Override
	public NotificationManager getNotificationManager() {
		return notifm;
	}

	@Override
	public ProgressbarManager getProgressbarManager() {
		return progressbarManager;
	}

	public void refresh() {
		SwingUtilities.invokeLater(updater);
	}

	/**
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class Updater implements Runnable, UpdatableByNotificationManager {

		private DecimalFormat zoomFormat;
		private DecimalFormat meterFormat;
		private DecimalFormat kmFormat;

		/**
		 * Reception d'un evenement
		 */
		@Override
		public void notificationReceived(Notification arg) {

			// ecouter le curseur pour affichage de la position
			if (arg instanceof ProjectEvent) {
				if (ProjectEvent.isNewProjectLoadedEvent(arg)
						&& mouseListening == false) {
					mouseListening(true);
				} else if (ProjectEvent.isCloseProjectEvent(arg)
						&& mouseListening == true) {
					mouseListening(false);
				}
			}

			// mettre à jour l'echelle
			if (arg instanceof MapEvent) {
				refresh();
			}
		}

		public Updater() {

			zoomFormat = new DecimalFormat("###");
			zoomFormat.setRoundingMode(RoundingMode.UP);

			meterFormat = new DecimalFormat("###");
			meterFormat.setRoundingMode(RoundingMode.UP);

			kmFormat = new DecimalFormat("###.##");
			kmFormat.setRoundingMode(RoundingMode.UP);
		}

		@Override
		public void run() {

			GuiUtils.throwIfNotOnEDT();

			// affichage du zoom hors mode géoréférencé
			String txt = "Zoom: "
					+ zoomFormat.format(mapm.getDisplayScale() * 100) + "%";

			// affichage de l'echelle en mode géoréférencé
			if (projectm.isInitialized() && mapm.isGeoreferencementEnabled()) {

				try {

					// récuperer les references
					ArrayList<Coordinate> georefs = mapm.getGeoReferences();
					if (georefs.size() < 2) {
						return;
					}

					Coordinate ref1 = new Coordinate();
					Coordinate ref2 = new Coordinate(mapm.getGeoReferences()
							.get(0));

					// ajout de 10 mm au deuxieme point à l'echelle
					ref2.longitudePx += 10d
							* ConfigurationConstants.JAVA_RESOLUTION / 25.45d
							/ mapm.getDisplayScale();

					// transformation puis calcul
					mapm.transformCoords(GeoConstants.SCREEN_TO_WORLD, ref2);

					Double dist = mapm.azimuthDistance(ref1, ref2)[1];
					String unit = "m";
					DecimalFormat format = meterFormat;

					if (dist > 1000) {
						dist /= 1000;
						unit = "km";
						format = kmFormat;
					}

					txt += " - Echelle: " + format.format(dist) + " " + unit;

				} catch (Exception e) {
					if (MainManager.isDebugMode() && showErrors) {
						Log.error(e);
					}
				}

			}

			// affecter le texte
			labelScale.setText(txt);
			labelScale.revalidate();
			labelScale.repaint();
		}

	}

	/**
	 * Ecouter le curseur sur la carte
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class MousePositionListener extends MouseAdapter {

		@Override
		public void mouseExited(MouseEvent arg0) {
			GuiUtils.throwIfNotOnEDT();
			labelPosition.setText("");
		}

		@Override
		public void mouseDragged(MouseEvent arg0) {
			reportMousePosition(arg0);
		}

		@Override
		public void mouseMoved(MouseEvent arg0) {
			reportMousePosition(arg0);
		}

		private void reportMousePosition(MouseEvent arg0) {

			GuiUtils.throwIfNotOnEDT();

			// position du curseur à l'echelle
			Point p = mapm.getScaledPoint(arg0.getPoint());

			// latitude longitude
			mouseCoords.setPixelValue(p.y, p.x);

			// afficher la position en pixels
			String pixelPosStr = mouseCoords
					.getStringRepresentation(GeoConstants.DISPLAY_PIXELS);

			// afficher en degres
			String degreesPosStr = null;
			if (mapm.isGeoreferencementEnabled()) {
				try {

					// convertir
					mapm.transformCoords(GeoConstants.SCREEN_TO_WORLD,
							mouseCoords);

					// mettre en forme
					degreesPosStr = " - Degrés: "
							+ mouseCoords
									.getStringRepresentation(GeoConstants.DISPLAY_DEGREES_MINUTES_DEC);

				} catch (MapManagerException e) {
					if (showErrors) {
						Log.error(e);
					}
					degreesPosStr = "Erreur de géoréférencement";
				}
			}

			// afficher le texte
			String positionStr = degreesPosStr == null ? pixelPosStr
					: degreesPosStr + " - " + pixelPosStr;

			labelPosition.setText("<html>Position du curseur: " + positionStr
					+ "</html>");
			labelPosition.revalidate();
			labelPosition.repaint();

		}
	}

}
