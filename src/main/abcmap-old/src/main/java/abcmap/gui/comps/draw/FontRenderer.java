package abcmap.gui.comps.draw;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class FontRenderer extends JLabel implements ListCellRenderer<String> {

	private Dimension defaultDimensions;
	private int defaultFontSize;

	private Font fontToDisplay;
	private String textToDisplay;

	public FontRenderer() {
		super();
		this.setOpaque(true);

		// taille de la police pour affichage
		this.defaultFontSize = 13;

		// dimensions par defaut
		this.defaultDimensions = new Dimension(150, 22);

	}

	public void setDefaultFontSize(int defaultFontSize) {
		this.defaultFontSize = defaultFontSize;
	}

	public void setDefaultDimensions(Dimension defaultDimensions) {
		this.defaultDimensions = defaultDimensions;
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends String> list, String f, int arg2, boolean isSelected,
			boolean arg4) {

		// determiner le texte à afficher
		if (f != null) {
			this.fontToDisplay = new Font(f, Font.PLAIN, defaultFontSize);
			this.textToDisplay = "  " + f;
		} else {
			this.fontToDisplay = new Font(Font.DIALOG, Font.PLAIN, defaultFontSize);
			this.textToDisplay = "  " + "-- Police indisponible";
		}

		// affecter le texte et la font
		this.setFont(fontToDisplay);
		this.setText(textToDisplay);

		// affecter les dimensions
		this.setPreferredSize(defaultDimensions);

		// changement de couleur en fonction de la selection
		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}

		return this;
	}

}
