package abcmap.gui.comps.draw;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import abcmap.draw.basicshapes.LayerElement;
import abcmap.managers.ProjectManager;
import abcmap.managers.stub.MainManager;
import abcmap.utils.Refreshable;
import abcmap.utils.gui.FormUpdater;
import abcmap.utils.listeners.HasListenerHandler;
import abcmap.utils.listeners.ListenerHandler;
import abcmap.utils.notifications.HasNotificationManager;
import abcmap.utils.notifications.NotificationManager;
import net.miginfocom.swing.MigLayout;

/**
 * Affiche un echantillon du premier objet sélectionné disponible
 * 
 * @author remipassmoilesel
 *
 */
public class SelectedObjectPanel extends JPanel
		implements HasNotificationManager, HasListenerHandler<ActionListener>, Refreshable {

	/** Le panneau qui affiche un échantillon de l'élement selectionné */
	private LayerElementSamplePanel sampleDisplayer;

	/** Filtres de sélection */
	private ArrayList<Class<? extends LayerElement>> filters;

	/** L'etiquette qui affiche le type d'element sélectionné */
	private JLabel lblTypeOfSample;

	/** La taille max d'un cote d'echantillon */
	private int maxSampleWidth;

	private ListenerHandler<ActionListener> listenerHandler;

	private NotificationManager notifm;
	private ProjectManager projectm;

	public SelectedObjectPanel() {
		super(new MigLayout("insets 5"));

		this.projectm = MainManager.getProjectManager();
		this.listenerHandler = new ListenerHandler<>();
		this.filters = new ArrayList<>();

		this.maxSampleWidth = 40;

		// panneau de l'echantillon de symbole
		sampleDisplayer = new LayerElementSamplePanel();
		add(sampleDisplayer, "width 50px!, height 50px!, gapright 10px");

		// affichage du type
		lblTypeOfSample = new JLabel();
		setLblTypeOfSampleText("Sélectionnez un objet");
		add(lblTypeOfSample);

		// mise à jour en fonction des changements de sélection dans le projet
		SelectionViewUpdater formUpdater = new SelectionViewUpdater();
		notifm = new NotificationManager(this);
		notifm.setDefaultUpdatableObject(formUpdater);

		projectm.getNotificationManager().addObserver(this);

	}

	private void setLblTypeOfSampleText(String text) {
		lblTypeOfSample.setText("<html><b>" + text + "</b></html>");
	}

	/**
	 * Met à jour la vue de sélection en fonction des changements de sléection
	 * du projet.
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class SelectionViewUpdater extends FormUpdater {

		@Override
		protected void updateFields() {
			super.updateFields();

			// récupérer le premier objet sélectionné
			LayerElement elmt = getFirstSelectedElement(filters);

			// pas d'element sélectionné ou erreur
			if (elmt == null) {
				sampleDisplayer.setSample(null);
				setLblTypeOfSampleText("Sélectionnez un objet");
				fireEvent();
				return;
			}

			// demander un échantillon
			LayerElement sample = elmt.getSample(maxSampleWidth, maxSampleWidth);

			// afficher l'echantillon dans le panneau de sélection
			sampleDisplayer.setSample(sample);

			// afficher le type de l'element
			setLblTypeOfSampleText(drawm.getReadableNameFor(sample.getClass()));

			fireEvent();

			refresh();
		}

	}

	/**
	 * Affiche un echantillon d'objet
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class LayerElementSamplePanel extends JPanel {

		private LayerElement sample;

		public LayerElementSamplePanel() {
			this.sample = null;
			this.setBorder(BorderFactory.createLineBorder(Color.gray));
		}

		@Override
		protected void paintComponent(Graphics g) {

			// dessiner le panneau
			super.paintComponent(g);

			// dessiner le symbole
			if (sample != null) {
				sample.draw((Graphics2D) g, LayerElement.RENDER_FOR_PRINTING);
			}
		}

		public void setSample(LayerElement sample) {

			this.sample = sample;

			// adapter la position
			if (sample != null) {

				// rafrachir une premiere fois pour avoir les dimensions
				sample.refreshShape();

				// centrer horizontalement et verticalement
				Rectangle maxBounds = sample.getMaximumBounds();

				int px = (int) ((this.getWidth() - maxBounds.width) / 2f);
				int py = (int) ((this.getHeight() - maxBounds.height) / 2f);

				// affecter et rafraichir
				sample.setPosition(px, py);
				sample.refreshShape();
			}
		}

	}

	private void fireEvent() {
		listenerHandler.fireEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, ""));
	}

	@Override
	public NotificationManager getNotificationManager() {
		return notifm;
	}

	@Override
	public void refresh() {

		sampleDisplayer.revalidate();
		sampleDisplayer.repaint();

		lblTypeOfSample.revalidate();
		lblTypeOfSample.repaint();

		this.revalidate();
		this.repaint();
	}

	@Override
	@Deprecated
	public void reconstruct() {
		refresh();
	}

	public void addFilter(Class<? extends LayerElement> class1) {
		this.filters.add(class1);
	}

	@Override
	public ListenerHandler<ActionListener> getListenerHandler() {
		return listenerHandler;
	}

}
