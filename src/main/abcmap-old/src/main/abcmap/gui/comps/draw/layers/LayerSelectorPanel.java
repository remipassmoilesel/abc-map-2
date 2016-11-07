package abcmap.gui.comps.draw.layers;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import abcmap.events.ProjectEvent;
import abcmap.exceptions.MapLayerException;
import abcmap.gui.GuiIcons;
import abcmap.managers.Log;
import abcmap.managers.ProjectManager;
import abcmap.managers.stub.MainManager;
import abcmap.project.layers.MapLayer;
import abcmap.utils.gui.GuiUtils;
import abcmap.utils.notifications.Notification;
import abcmap.utils.notifications.HasNotificationManager;
import abcmap.utils.notifications.NotificationManager;
import abcmap.utils.notifications.UpdatableByNotificationManager;
import net.miginfocom.swing.MigLayout;

/**
 * Panneau de manipulation des calques. Le panneau est directement connecté au
 * gestionnaire de projet, étant donné la spécificité du composant.
 * 
 * @author remipassmoilesel
 *
 */
public class LayerSelectorPanel extends JPanel implements
		HasNotificationManager {

	private static final int SLIDER_MIN_VALUE = 0;
	private static final int SLIDER_MAX_VALUE = 100;

	private ProjectManager projectm;

	private DefaultListModel<MapLayer> listModel;
	private JList<MapLayer> jlist;
	private JSlider opacitySlider;
	private NotificationManager om;
	private FormUpdater formUpdater;
	private boolean showExceptions;

	public LayerSelectorPanel() {

		super(new MigLayout("insets 0"));

		this.showExceptions = false;

		this.projectm = MainManager.getProjectManager();

		// objet de mise à jour des elements de formulaire
		this.formUpdater = new FormUpdater();

		// ecouter le projet
		this.om = new NotificationManager(this);
		om.setDefaultUpdatableObject(new LayerSelectorUpdater());
		projectm.getNotificationManager().addObserver(this);

		// le modele de la liste dynamique de calques
		this.listModel = new DefaultListModel<MapLayer>();

		// liste dynamique de calques
		jlist = new JList<MapLayer>(listModel);
		jlist.setAlignmentY(Component.TOP_ALIGNMENT);
		jlist.setAlignmentX(Component.LEFT_ALIGNMENT);
		jlist.setBorder(BorderFactory.createLineBorder(Color.darkGray, 1));
		jlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jlist.setVisibleRowCount(5);
		jlist.setCellRenderer(new LayerListRenderer());
		jlist.addListSelectionListener(new SelectionListener());

		// la liste est dans un scroll pane
		JScrollPane sp = new JScrollPane(jlist);
		sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		add(sp, "span, width 90%, height 130px!, wrap 8px");

		// reglage de l'opacité des calques
		GuiUtils.addLabel("Opacité du calque: ", this, "wrap");
		opacitySlider = new JSlider(JSlider.HORIZONTAL, SLIDER_MIN_VALUE,
				SLIDER_MAX_VALUE, 0);
		opacitySlider.addChangeListener(new SliderListener());
		add(opacitySlider, "grow, span, wrap 8px");

		// boutons de controle
		JButton up = new JButton(GuiIcons.LAYER_UP);
		up.addActionListener(new LayerListButtonsAL(LayerListButtonsAL.MOVE_UP));

		JButton down = new JButton(GuiIcons.LAYER_DOWN);
		down.addActionListener(new LayerListButtonsAL(
				LayerListButtonsAL.MOVE_DOWN));

		JButton rename = new JButton(GuiIcons.LAYER_RENAME);
		rename.addActionListener(new LayerListButtonsAL(
				LayerListButtonsAL.RENAME));

		JButton remove = new JButton(GuiIcons.LAYER_REMOVE);
		remove.addActionListener(new LayerListButtonsAL(
				LayerListButtonsAL.REMOVE));

		JButton newlayer = new JButton(GuiIcons.LAYER_ADD);
		newlayer.addActionListener(new LayerListButtonsAL(
				LayerListButtonsAL.NEW));

		JButton visibility = new JButton(GuiIcons.LAYER_VISIBILITY_BUTTON);
		visibility.addActionListener(new LayerListButtonsAL(
				LayerListButtonsAL.CHANGE_VISIBILITY));

		// les boutons sont placés dans un sous panneau
		JPanel subPanel = new JPanel(new MigLayout("insets 0"));
		String dim = "width 30!, height 30!";
		subPanel.add(newlayer, dim);
		subPanel.add(remove, dim);
		subPanel.add(up, dim);
		subPanel.add(down, dim);
		subPanel.add(rename, dim);
		subPanel.add(visibility, dim);

		add(subPanel);

		// rafraichir le composant
		revalidate();
		repaint();
	}

	/**
	 * Ecoute les selection de calque à la souris
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class SelectionListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {

			// pas d'action hors de l'EDT
			GuiUtils.throwIfNotOnEDT();

			// projet non initialisé: retour
			if (projectm.isInitialized() == false)
				return;

			// recuperer le calque actif
			MapLayer activeLayer;
			try {
				activeLayer = projectm.getActiveLayer();
			} catch (MapLayerException e1) {

				// le projet n'est pas initialisé:
				// repeindre la liste vide puis retour
				if (showExceptions)
					Log.debug(e1);
				jlist.repaint();
				return;
			}

			// recuperer le calque selectionné
			MapLayer lay = jlist.getSelectedValue();

			// calque null, retour
			if (lay == null)
				return;

			// selectionner le calque, uniquement si il n'est pas deja actif
			if (activeLayer.equals(lay) == false) {
				try {
					projectm.setActiveLayer(lay);
				} catch (MapLayerException e1) {
					if (showExceptions)
						Log.debug(e1);
				}
			}

			refresh();

		}
	}

	/**
	 * Ecoute les changements d'opacité des calques
	 * 
	 * @author remipassmoilesel
	 *
	 */
	public class SliderListener implements ChangeListener {

		public void stateChanged(ChangeEvent arg0) {

			// pas d'action hors de l'EDT
			GuiUtils.throwIfNotOnEDT();

			// projet non initialisé: retour
			if (projectm.isInitialized() == false)
				return;

			// recuperer la valeur
			JSlider slider = (JSlider) arg0.getSource();
			float value = Float.valueOf(slider.getValue()) / 100;

			// recuperer le calque actif
			MapLayer lay;
			try {
				lay = projectm.getActiveLayer();
			} catch (MapLayerException e) {
				if (showExceptions)
					Log.debug(e);
				return;
			}

			// appliquer la valeur d'opacité uniquement si différente
			if (lay.getOpacity() != value) {
				lay.setOpacity(value);
			}

		}
	}

	private class FormUpdater implements Runnable {

		@Override
		public void run() {

			// pas d'action hors de l'EDT
			GuiUtils.throwIfNotOnEDT();

			// pas de rafrachissement si composants null
			if (opacitySlider == null || jlist == null)
				return;

			// le projet est non initialise: valeurs par defaut
			if (projectm.isInitialized() == false) {

				// raz de l'opacité
				opacitySlider.setValue(0);
				opacitySlider.setEnabled(false);

				// raz de la liste de calques
				listModel.clear();
				jlist.setEnabled(false);

				return;
			}

			// le projet est initialisé
			else {

				// recuperer le calque actif
				MapLayer activeLayer;
				try {
					activeLayer = projectm.getActiveLayer();
				} catch (MapLayerException e) {
					if (showExceptions)
						Log.debug(e);
					return;
				}

				// mettre à jour l'opacité
				float opacity = activeLayer.getOpacity();
				int newValue = Math.round(opacity * 100);
				if (newValue != opacitySlider.getValue()) {
					GuiUtils.changeWithoutFire(opacitySlider, newValue);
					opacitySlider.revalidate();
					opacitySlider.repaint();
				}

				// mettre à jour la liste de calques
				ArrayList<MapLayer> layers = projectm.getLayers();
				listModel.clear();
				for (int i = layers.size(); i > 0; i--) {
					listModel.addElement(layers.get(i - 1));
				}

				jlist.revalidate();
				jlist.repaint();
			}

		}

	}

	private class LayerSelectorUpdater implements
			UpdatableByNotificationManager {

		@Override
		public void notificationReceived(Notification arg) {
			if (arg instanceof ProjectEvent) {
				SwingUtilities.invokeLater(formUpdater);
			}
		}

	}

	@Override
	public NotificationManager getNotificationManager() {
		return om;
	}

	public void refresh() {
		this.repaint();
		this.revalidate();
	}

}
