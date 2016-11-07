package abcmap.gui.comps.help;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import abcmap.gui.GuiCursor;
import abcmap.gui.GuiIcons;
import abcmap.utils.gui.GuiUtils;
import net.miginfocom.swing.MigLayout;

public class AttentionPanel extends JPanel {

	private String shortmessage;
	private String longmessage;
	private JButton buttonManual;
	private JLabel lblMessage;
	private Dimension shortPreferredSize;
	private Dimension longPreferredSize;

	public AttentionPanel() {
		super(new MigLayout("insets 2"));

		// bordure rouge
		// setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(200,
		// 0, 0)));

		// messages
		this.shortmessage = "<html><b>Attention: ...</b></html>";
		this.longmessage = "<html><b>Attention:</b> Assurez vous d'avoir lu "
				+ "l'intégralité du manuel avant de poursuivre.</html>";

		// montrer le message lors d'un clic
		ShowMessageListener showMouseListener = new ShowMessageListener();

		// icone
		JLabel lblAttention = new JLabel(GuiIcons.ATTENTION);
		lblAttention.addMouseListener(showMouseListener);
		add(lblAttention, "gapright 5px");

		// message
		lblMessage = GuiUtils.addLabel(shortmessage, this, "wrap");
		lblMessage.setCursor(GuiCursor.HAND_CURSOR);
		lblMessage.addMouseListener(showMouseListener);

		// dimensions
		shortPreferredSize = new Dimension(200, 30);
		longPreferredSize = new Dimension(200, 50);

		// bouton d'affichage du manuel
		buttonManual = new JButton("Afficher le manuel");

		setToolTipText(longmessage);

	}

	private class ShowMessageListener extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {

			// recuperer le texte affiché
			String displayed = lblMessage.getText();

			// afficher le message correpondant
			if (shortmessage.equals(displayed)) {
				lblMessage.setText(longmessage);
				add(buttonManual, "span, align center");

				setMinimumSize(longPreferredSize);
				setPreferredSize(longPreferredSize);
			}

			else {
				lblMessage.setText(shortmessage);
				remove(buttonManual);

				setMinimumSize(shortPreferredSize);
				setPreferredSize(shortPreferredSize);
			}

			lblMessage.revalidate();
			lblMessage.repaint();

			revalidate();
			repaint();
		}
	}

}
