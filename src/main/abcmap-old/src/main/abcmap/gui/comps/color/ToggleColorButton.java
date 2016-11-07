package abcmap.gui.comps.color;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JToggleButton;

import abcmap.gui.GuiCursor;

/**
 * Bouton de couleur sélectionnable.
 * 
 * @author remipassmoilesel
 *
 */
public class ToggleColorButton extends JToggleButton {

	/** La couleur du bouton */
	private Color color;

	/** L'epaisseur de la bordure de sélection */
	private final static int SELECTION_BORDER = 2;

	/** L'epaisseur de la bordure de sélection */
	private final static BasicStroke SELECTION_STROKE = new BasicStroke(SELECTION_BORDER);

	/** Couleur du cadre lorsque le bouton est sélectionné */
	private static final Color SELECTED_COLOR = Color.red;

	/** Couleur du cadre lorsque le bouton est désélectionné */
	private static final Color UNSELECTED_COLOR = Color.gray;

	public ToggleColorButton() {
		this(null);
	}

	public ToggleColorButton(Color color) {
		super(" ");

		this.color = color;

		setCursor(GuiCursor.HAND_CURSOR);
	}

	@Override
	protected void paintComponent(Graphics g) {

		Graphics2D g2d = (Graphics2D) g;

		// les dimensions du composant
		Dimension dimensions = getSize();

		// dessiner l'arriere plan, de la couleur désirée ou blanc si nulle
		Color c = color != null ? color : Color.white;
		g2d.setColor(c);
		g2d.fillRect(0, 0, dimensions.width, dimensions.height);

		// premier plan dessiner une barre oblique si la couleur est nulle
		if (color == null) {
			g2d.setColor(Color.darkGray);
			g2d.setStroke(new BasicStroke(SELECTION_BORDER));
			g2d.drawLine(0, 0, dimensions.width, dimensions.height);

		}

		// dessiner un cadre blan autour de la couleur
		g2d.setColor(Color.white);
		g2d.setStroke(SELECTION_STROKE);
		g2d.drawRect(SELECTION_BORDER, SELECTION_BORDER, dimensions.width - SELECTION_BORDER * 2,
				dimensions.height - SELECTION_BORDER * 2);

		// dessin du cadre de selection
		Color cb = isSelected() ? SELECTED_COLOR : UNSELECTED_COLOR;
		g2d.setColor(cb);
		g2d.setStroke(SELECTION_STROKE);
		g2d.drawRect(SELECTION_BORDER / 2, SELECTION_BORDER / 2, dimensions.width - SELECTION_BORDER,
				dimensions.height - SELECTION_BORDER);

	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

}