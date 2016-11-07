package abcmap.gui.comps.draw;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import abcmap.draw.styles.BackgroundRenderer;
import abcmap.draw.styles.Texture;

public class TextureRenderer extends JLabel implements ListCellRenderer<Texture> {

	private int margin;
	private Color color;
	private Dimension preferredSize;
	private Texture texture;

	public TextureRenderer() {
		setOpaque(true);

		this.margin = 5;
		this.color = Color.blue;

		setBorder(BorderFactory.createLineBorder(Color.lightGray, 1));

		this.preferredSize = new Dimension(80, 30);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends Texture> list, Texture value,
			int index, boolean isSelected, boolean cellHasFocus) {

		this.texture = value;

		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}

		return this;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		// ligne nulle, retour
		if (texture == null)
			return;

		// calculer les dimensions du rectangle
		Rectangle rect = new Rectangle();
		rect.x = margin;
		rect.y = margin;
		rect.width = getWidth() - margin * 2;
		rect.height = getHeight() - margin * 2;

		// rendu
		BackgroundRenderer.fill(g, rect, texture, color);

	}

	@Override
	public Dimension getPreferredSize() {
		return preferredSize;
	}

}
