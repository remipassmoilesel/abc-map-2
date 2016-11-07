package abcmap.gui.dock.comps;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import abcmap.draw.basicshapes.DrawProperties;
import abcmap.draw.tools.containers.ToolContainer;
import abcmap.events.DrawManagerEvent;
import abcmap.gui.comps.color.ColorButton;
import abcmap.gui.iegroup.docks.GroupDrawingPalette;
import abcmap.gui.iegroup.docks.GroupDrawingTools;
import abcmap.managers.DrawManager;
import abcmap.managers.GuiManager;
import abcmap.managers.stub.MainManager;
import abcmap.utils.Refreshable;
import abcmap.utils.notifications.Notification;
import abcmap.utils.notifications.HasNotificationManager;
import abcmap.utils.notifications.NotificationManager;
import abcmap.utils.notifications.UpdatableByNotificationManager;
import net.miginfocom.swing.MigLayout;

/**
 * Bouton de dock spécial permettant d'afficher l'outil de dessin et les
 * couleurs sélectionnés.
 * 
 * @author remipassmoilesel
 *
 */
public class DrawIndicatorWidget extends JPanel implements
		HasNotificationManager, Refreshable {

	private NotificationManager om;
	private DrawManager drawm;
	private GuiManager guim;

	private JLabel toolLabel;
	private ColorButton fgColor;
	private ColorButton bgColor;

	public DrawIndicatorWidget() {
		super(new MigLayout("insets 3, fillx"));

		drawm = MainManager.getDrawManager();
		guim = MainManager.getGuiManager();

		// caracteristiques
		setBorder(BorderFactory.createEtchedBorder());

		// se connecter au gestionnaire de dessin
		om = new NotificationManager(this);
		om.setDefaultUpdatableObject(new UpdatableByNotificationManager() {
			@Override
			public void notificationReceived(final Notification arg) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						if (arg instanceof DrawManagerEvent)
							DrawIndicatorWidget.this.reconstruct();
					}
				});
			}
		});
		drawm.getNotificationManager().addObserver(this);

		// lorsque l'utilisateur clique sur un element, renvoi vers le panneau
		// de modification
		CustomML colorsAL = new CustomML(CustomML.SHOW_COLORS);
		CustomML toolsAL = new CustomML(CustomML.SHOW_TOOLS);

		// bouton d'indication d'outil
		toolLabel = new JLabel();
		toolLabel.addMouseListener(toolsAL);
		toolLabel.setCursor(MainManager.getGuiManager().getClickableCursor());

		// boutons d'indication de couleur
		fgColor = new ColorButton(null);
		fgColor.addMouseListener(colorsAL);

		bgColor = new ColorButton(null);
		bgColor.addMouseListener(colorsAL);

		// construction
		reconstruct();

	}

	@Override
	public void reconstruct() {

		// enlever tout
		removeAll();

		// recuperer le conteneur de l'outil courant
		ToolContainer currentTC = drawm.getCurrentToolContainer();

		// pas d'outil selectionne
		if (currentTC == null) {
			toolLabel.setIcon(new ImageIcon());
			toolLabel.setToolTipText("Veuillez sélectionner un outil.");
		}

		// un outil est selectionné
		else {
			toolLabel.setIcon(currentTC.getIcon());
			toolLabel.setToolTipText("Outil courant: "
					+ currentTC.getReadableName());
		}

		add(toolLabel, "alignx center, wrap");

		// recuperer les caracteristiques de dessin
		DrawProperties st = drawm.getNewStroke();

		// indicateurs de couleur
		fgColor.setColor(st.getFgColor());
		fgColor.setToolTipText("Couleur de premier plan: "
				+ fgColor.getStringRGB() + " (RGB)");

		bgColor.setColor(st.getBgColor());
		bgColor.setToolTipText("Couleur de second plan: "
				+ bgColor.getStringRGB() + " (RGB)");

		add(fgColor, "width 80%!, alignx center, wrap");
		add(bgColor, "width 80%!, alignx center, wrap");

		refresh();
	}

	@Override
	public void refresh() {

		// rafraichir les composants
		fgColor.revalidate();
		fgColor.repaint();
		bgColor.revalidate();
		bgColor.repaint();
		toolLabel.revalidate();
		toolLabel.repaint();

		revalidate();
		repaint();
	}

	@Override
	public NotificationManager getNotificationManager() {
		return om;
	}

	/**
	 * Ecouter les clics sur les boutons pour afficher le volet de dock
	 * correspondant au bouton.
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class CustomML extends MouseAdapter {

		public static final String SHOW_COLORS = "SHOW_COLORS";
		public static final String SHOW_TOOLS = "SHOW_TOOLS";
		private String mode;

		public CustomML(String mode) {
			this.mode = mode;
		}

		@Override
		public void mouseReleased(MouseEvent e) {

			// retrouver le parent du composant
			Dock parent = Dock
					.getDockParentForComponent(DrawIndicatorWidget.this);
			if (parent == null)
				return;

			// montrer le paneau concerné
			Class clss = SHOW_COLORS.equals(mode) ? GroupDrawingPalette.class
					: GroupDrawingTools.class;
			guim.showGroupInDock(clss);

		}
	}

}
