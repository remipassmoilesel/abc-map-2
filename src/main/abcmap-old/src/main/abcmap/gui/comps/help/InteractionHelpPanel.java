package abcmap.gui.comps.help;

import javax.swing.JLabel;
import javax.swing.JPanel;

import abcmap.gui.GuiStyle;
import abcmap.utils.gui.GuiUtils;
import net.miginfocom.swing.MigLayout;

public class InteractionHelpPanel extends JPanel {

	private InteractionSequence[] interSequences;
	private String title;

	public InteractionHelpPanel(String title, InteractionSequence[] inters) {
		super(new MigLayout("insets 5, width max"));

		// titre du panneau
		this.title = title;

		// listes des interactions a détailler
		this.interSequences = inters;

		reconstruct();
	}

	private void reconstruct() {

		// tout enlever
		removeAll();

		// rien à détailler: arret
		if (interSequences == null)
			throw new IllegalArgumentException("Null interactions");

		// titre du panneau
		GuiUtils.addLabel(title, this, "span, gapbottom 10px, wrap",
				GuiStyle.TOOL_HELP_TITLE);

		// elements d'interaction + descri
		for (InteractionSequence inter : interSequences) {

			// construction de l'interaction
			for (Interaction t : inter.getSequence()) {
				JLabel label = new JLabel(t.getIcon());
				label.setToolTipText(t.getToolTipText());
				add(label);
			}

			// ajout de la description
			GuiUtils.addLabel(inter.getDescription(), this,
					"gapleft 5px, gapbottom 10px, span, wrap");

		}

		// rafraichissement du panneau
		revalidate();
		repaint();

	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setInteractions(InteractionSequence[] interactions) {
		this.interSequences = interactions;
	}

}
