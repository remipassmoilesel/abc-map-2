package abcmap.gui.ie.position;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import abcmap.events.MapEvent;
import abcmap.events.ProjectEvent;
import abcmap.gui.comps.geo.CoordinatesPanel;
import abcmap.gui.ie.InteractionElement;
import abcmap.utils.gui.GuiUtils;
import abcmap.utils.notifications.Notification;
import abcmap.utils.notifications.NotificationManager;
import abcmap.utils.notifications.UpdatableByNotificationManager;
import net.miginfocom.swing.MigLayout;

public class MoveElementsByCoordinates extends InteractionElement {

	private CoordinatesPanel cpanel;
	private ElementMover mover;
	private MoveElementsUpdater updater;

	public MoveElementsByCoordinates() {

		this.label = "Déplacement par coordonnées";
		this.help = "Déplacer des élements sélectionnés en saisissant des coordonnées dans les champs ci-dessous.";

		// affichage particulier pendant une recherche
		this.displaySimplyInSearch = false;

	}

	@Override
	protected Component createPrimaryGUI() {

		// panneau principal
		JPanel panel = new JPanel(new MigLayout("insets 5"));
		GuiUtils.addLabel("Référence de positionnement: ", panel, "wrap");

		// elements de choix de la reference de positionnement

		// objet de changement de reference
		ReferenceChoiceAL ral = new ReferenceChoiceAL();

		// deplacer par le centre
		JRadioButton byCenter = new JRadioButton("Par le centre");
		byCenter.setActionCommand(ElementMover.MOVE_BY_CENTER);
		byCenter.setSelected(true);
		byCenter.addActionListener(ral);
		panel.add(byCenter, "wrap");

		// deplacer par le coin haut gauche
		JRadioButton byUlc = new JRadioButton("Par le coin haut gauche");
		byUlc.setActionCommand(ElementMover.MOVE_BY_ULC);
		byUlc.addActionListener(ral);
		panel.add(byUlc, "wrap 10px");

		// groupe pour le choix de reference
		ButtonGroup bg = new ButtonGroup();
		bg.add(byCenter);
		bg.add(byUlc);

		// panneau de coordonnées
		GuiUtils.addLabel("Coordonnées: ", panel, "wrap");

		cpanel = new CoordinatesPanel();
		cpanel.getListenerHandler().add(mover);
		panel.add(cpanel, "wrap");

		// objet de deplacement des elements selectionnés
		mover = new ElementMover();

		updater = new MoveElementsUpdater();
		notifm.setDefaultUpdatableObject(updater);

		// à l'ecoute
		// des changements de sélection dans le projet
		projectm.getNotificationManager().addObserver(this);
		// des changements de caracteristiques geographiques
		mapm.getNotificationManager().addObserver(this);

		// mettre a jour le formulaire
		updater.updateDegreeForm();

		return panel;

	}

	private class ElementMover implements Runnable, ActionListener {

		public static final String MOVE_BY_CENTER = "MOVE_BY_CENTER";
		public static final String MOVE_BY_ULC = "MOVE_BY_ULC";
		private String mode;

		public ElementMover() {
			this.mode = MOVE_BY_CENTER;
		}

		/**
		 * Reception d'une notification de saisie de coordonnées dnas
		 * l'interface.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {

		}

		@Override
		public void run() {

		}

		public void setMode(String mode) {
			this.mode = mode;
		}

		public String getMode() {
			return mode;
		}
	}

	/**
	 * Mise à jour du panneau de coordonnées en fonction des changements de
	 * sélection dans le projet et en fonction des changements du gestionnaire
	 * de géoreferencement.
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class MoveElementsUpdater implements UpdatableByNotificationManager {

		@Override
		public void notificationReceived(Notification arg) {
			// activer ou desactiver le formulaire de saisie en degrés
			if (arg instanceof MapEvent) {
				updateDegreeForm();
			}

			// changer le contenu du formulaire en fonction de la sélection
			// active
			else if (arg instanceof ProjectEvent) {
				updateCoordinateForm();
			}
		}

		public void updateCoordinateForm() {
			// TODO Auto-generated method stub

		}

		public void updateDegreeForm() {
			boolean georefMode = mapm.isGeoreferencementEnabled();
			if (cpanel != null && cpanel.isDegreesFormEnabled() != georefMode) {
				cpanel.setDegreesFormEnabled(georefMode);
			}
		}

	}

	/**
	 * Objet de changement de reference de positionnement.
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class ReferenceChoiceAL implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			String mode = e.getActionCommand();
			if (mover.getMode() != null
					&& mover.getMode().equals(mode) == false) {
				mover.setMode(mode);
			}

		}

	}

	@Override
	public NotificationManager getNotificationManager() {
		return notifm;
	}

}
