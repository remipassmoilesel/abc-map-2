package abcmap.gui.comps.messagebox;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class BoxMessagePanel extends JPanel {

	private JLabel label;
	private Font font;
	private Color bgColor;
	private Color fgColor;

	public BoxMessagePanel() {
		super();

		// layout
		this.setLayout(new BorderLayout());

		// element d'affichage du texte
		label = new JLabel();
		label.setOpaque(false);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setVerticalAlignment(SwingConstants.CENTER);

		// police d'affichage
		font = new Font(Font.DIALOG, Font.BOLD, 18);
		setMessageFont(font);

		// couleurs
		bgColor = Color.black;
		fgColor = Color.white;

		setOpaque(true);
		setBackground(bgColor);
		label.setForeground(fgColor);

		// taille par defaut
		setPreferredSize(new Dimension(600, 60));

		// label.setBorder(BorderFactory.createLineBorder(Color.blue));

		add(label, BorderLayout.CENTER);

	}

	public void setMessage(String text) {
		label.setText(text);
	}

	public void refresh() {

		label.revalidate();
		label.repaint();

		revalidate();
		repaint();

	}

	public void setMessageFont(Font font) {
		this.font = font;
		if (label != null) {
			label.setFont(font);
		}
	}

	@Override
	public void setBackground(Color bg) {
		bgColor = bg;
		super.setBackground(bg);
	}

}
