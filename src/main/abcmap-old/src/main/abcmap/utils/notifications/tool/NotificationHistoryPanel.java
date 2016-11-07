package abcmap.utils.notifications.tool;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import abcmap.gui.comps.buttons.HtmlLabel;
import abcmap.utils.gui.GuiUtils;
import abcmap.utils.notifications.Notification;
import abcmap.utils.notifications.NotificationManager;
import net.miginfocom.swing.MigLayout;

public class NotificationHistoryPanel extends JPanel {

	private NotificationHistoryElement elmt;
	private Notification ev;
	private NotificationManager om;
	private Object owner;

	public NotificationHistoryPanel(NotificationHistoryElement elmt) {

		// construction sur l'EDT
		GuiUtils.throwIfNotOnEDT();

		this.elmt = elmt;
		this.ev = elmt.getEvent();
		this.om = elmt.getObserverManager();
		this.owner = elmt.getOwner();

		// layout manager
		setLayout(new MigLayout("insets 10"));

		// bordure
		setBorder(BorderFactory.createLineBorder(Color.lightGray));

		// titre: classe de l'evenement
		GuiUtils.addLabel("<h4>#" + ev.getInstanceNumber() + ": "
				+ ev.getClass().getSimpleName() + " - " + ev.hashCode()
				+ "</h4>", this, "span, wrap");

		// nom + valeur
		GuiUtils.addLabel(
				"Nom - Valeur: <b>" + ev.getName() + " - " + ev.getValue(),
				this, "span, wrap");

		// date de création de l'evenement
		GuiUtils.addLabel("Date de création: " + ev.getCreationTime(), this,
				"span, wrap");

		// numero d'instance
		GuiUtils.addLabel("Instance n°: " + ev.getInstanceNumber(), this,
				"wrap");

		// classe du proprietaire
		GuiUtils.addLabel("Classe du proprietaire de l'OM transmetteur: "
				+ owner.getClass(), this, "wrap");

		// identifiant de l'observermanager
		GuiUtils.addLabel(
				"Transmetteur: " + om.getClass() + " - " + om.hashCode(), this,
				"wrap");

		// recepteurs
		JPanel receivers = new ReceiverDisplayPanel(elmt.getReceivers());
		add(receivers, "span, width 98%!");

	}

	/**
	 * Afficher les destinateires des evenement
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private static class ReceiverDisplayPanel extends JPanel implements
			ActionListener {

		private ArrayList<NotificationManager> receivers;
		private boolean receiversAreShowed;
		private JButton buttonShowReceivers;
		private HtmlLabel labelNbrReceivers;

		public ReceiverDisplayPanel(ArrayList<NotificationManager> receivers) {

			GuiUtils.throwIfNotOnEDT();

			// caracteristiques et indicateurs
			this.receivers = receivers;
			this.setLayout(new MigLayout());

			// Indicateur de nombre de recepeteurs
			this.labelNbrReceivers = new HtmlLabel(receivers.size()
					+ " destinataire(s).");

			// bouton afficher / masquer
			this.buttonShowReceivers = new JButton(
					"Afficher / masquer les recepteurs");
			buttonShowReceivers.addActionListener(this);

			// changement de l'affichage
			this.receiversAreShowed = false;
			showReceivers(receiversAreShowed);

		}

		@Override
		public void actionPerformed(ActionEvent e) {

			// inverser l'indicateur
			receiversAreShowed = !receiversAreShowed;

			// changement de l'affichage
			showReceivers(receiversAreShowed);

		}

		public void showReceivers(boolean state) {

			// tout enlever
			this.removeAll();

			// bouton + label
			this.add(labelNbrReceivers);
			this.add(buttonShowReceivers, "wrap");

			// afficher les destinataires
			if (state == true) {

				int i = 0;

				// iterer les destinataires
				for (NotificationManager om : receivers) {

					String className = om.getClass().getName();
					String owner = om.getOwner().getClass().getName();
					String text = "<html>#" + i + ":";
					text += "<br>Classe du receveur: " + owner;
					text += "<br>Classe de l'observateur receveur: "
							+ className;
					text += "</html>";

					HtmlLabel label = new HtmlLabel(text);
					label.setBorder(BorderFactory
							.createLineBorder(Color.lightGray));

					this.add(label, "span, width 98%!, wrap");

					i++;
				}
			}

			// rafraichissement
			revalidate();
			repaint();

		}

	}

}
