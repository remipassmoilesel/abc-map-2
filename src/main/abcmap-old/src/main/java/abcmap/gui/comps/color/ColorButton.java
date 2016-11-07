package abcmap.gui.comps.color;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JButton;

import abcmap.gui.GuiCursor;

public class ColorButton extends JButton {

	private Color color;
	private int borderThickness;
	private Color borderColor;

	public ColorButton(Color color) {
		super(" ");

		// caracteristiques
		this.color = color;
		this.borderThickness = 1;
		this.borderColor = new Color(150, 150, 150);

		// curseur
		setCursor(GuiCursor.HAND_CURSOR);

		updateToolTipText();

	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		Rectangle b = getBounds();

		// dessiner un fond de couleur
		Color bgColor = color != null ? color : Color.white;
		g2d.setColor(bgColor);
		g2d.fillRect(0, 0, b.width, b.height);

		// si la couleur est nulle, dessiner une diagonale
		if (color == null) {
			g2d.setColor(borderColor);
			g2d.setStroke(new BasicStroke(borderThickness + 1));
			g2d.drawLine(0, 0, b.width, b.height);
		}

		// bordure
		g2d.setStroke(new BasicStroke(borderThickness));
		g2d.setColor(borderColor);
		g2d.drawRect(borderThickness / 2, borderThickness / 2, b.width - borderThickness,
				b.height - borderThickness);

	}

	private void updateToolTipText() {
		setToolTipText("Couleur: " + getStringRGB() + " (RGB)");
	}

	public void setColor(Color color) {
		this.color = color;
		updateToolTipText();
	}

	public Color getColor() {
		return color;
	}

	public String getStringRGB() {
		if (color == null)
			return "nulle";
		return color.getRed() + ", " + color.getGreen() + ", " + color.getBlue();
	}

	@Override
	public void setEnabled(boolean b) {
		if (b) {
			setCursor(GuiCursor.HAND_CURSOR);
		} else {
			setCursor(GuiCursor.NORMAL_CURSOR);
		}
		super.setEnabled(b);
	}

}