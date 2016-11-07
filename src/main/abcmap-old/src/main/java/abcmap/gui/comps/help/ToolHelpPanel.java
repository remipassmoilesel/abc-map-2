package abcmap.gui.comps.help;

import javax.swing.JLabel;
import javax.swing.JPanel;

import abcmap.draw.tools.containers.ToolContainer;
import abcmap.utils.gui.GuiUtils;
import abcmap.utils.notifications.NotificationManager;
import net.miginfocom.swing.MigLayout;

/**
 * Panneau d'indication des actions possible avec un outil de dessin. Mise à
 * jour automatique par evenement.
 * 
 * @author remipassmoilesel
 *
 */
public class ToolHelpPanel extends JPanel {

	private NotificationManager om;
	private String messageNoHelp;

	public ToolHelpPanel() {
		super(new MigLayout("insets 5"));

		this.messageNoHelp = "<html><i>Aide indisponible.</i></html>";
	}

	public void setMessageNoHelp(String messageNoHelp) {
		this.messageNoHelp = messageNoHelp;
	}

	public void constructWith(ToolContainer tc) {

		// Pas d'action dans l'EDT
		GuiUtils.throwIfNotOnEDT();

		// tout enlever
		removeAll();

		// outil non déterminé
		if (tc == null) {
			// ajout de la description
			GuiUtils.addLabel("<i>Veuillez sélectionner un outil.</i>", this);
			refresh();
			return;
		}

		// recuperer les interactions
		InteractionSequence[] inters = tc.getInteractions();

		// interactions absentes
		if (inters == null) {
			GuiUtils.addLabel("<i>" + messageNoHelp + "</i>", this);
			refresh();
			return;
		}

		// construire le panneau d'aide
		InteractionHelpPanel interPanel = new InteractionHelpPanel(
				tc.getReadableName(), inters);
		add(interPanel, "width max");

		refresh();

	}

	public void refresh() {
		revalidate();
		repaint();
	}

}
