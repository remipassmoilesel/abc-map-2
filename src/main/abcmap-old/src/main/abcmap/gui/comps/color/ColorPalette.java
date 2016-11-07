package abcmap.gui.comps.color;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import abcmap.gui.GuiCursor;
import abcmap.utils.listeners.HasListenerHandler;
import abcmap.utils.listeners.ListenerHandler;

/**
 * Palette colorée carrée de coté "size". La palette ecoute les clics et signale
 * les changements par ListenerHandler.
 * 
 * @author remipassmoilesel
 *
 */
public class ColorPalette extends JPanel implements HasListenerHandler<ActionListener> {

	/** L'image de fond colorée */
	private BufferedImage backgroundImage;

	/** La couleur active */
	private Color activeColor;

	private ListenerHandler<ActionListener> listenersHandler;

	/** La taille du panneau (carré) */
	private int size;

	public ColorPalette() {

		this.size = 200;

		// bordure grise
		setBorder(BorderFactory.createLineBorder(Color.gray, 2));

		// ecoutr les clics de la souris
		listenersHandler = new ListenerHandler<ActionListener>();
		addMouseListener(new MouseListener());

	}

	/**
	 * Peindre le composant. Le fond coloré est une image.
	 */
	@Override
	protected void paintComponent(Graphics g) {

		// reconstruire l'image si necessaire
		if (backgroundImage == null) {
			reconstructImage();
		}

		// peindre l'image
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(backgroundImage, 0, 0, Math.round(size), Math.round(size), null);
	}

	/**
	 * Reconstruit l'image de fond en fontion de la taille
	 */
	public void reconstructImage() {

		float h = 0f;
		float s = 1f;
		float b = 1f;

		backgroundImage = new BufferedImage(Math.round(size), Math.round(size), BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = (Graphics2D) backgroundImage.getGraphics();

		// A: espace TSL
		float stepA = 0.01f;

		// B: espace du composant
		float stepB = stepA * size;

		for (int i = 0; i < size; i += stepB) {
			h += stepA;
			for (int j = 0; j < size; j += stepB) {
				b -= stepA;
				if (b < 0)
					b = 0f;
				g2d.setColor(Color.getHSBColor(h, s, b));
				g2d.fillRect(i, j, Math.round(stepB), Math.round(stepB));
			}
			b = 1f;
		}

	}

	public void setSideSize(int size) {
		this.size = size;
		reconstructImage();
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(Math.round(size), Math.round(size));
	}

	/**
	 * Affecte la couleur active et transmet un evenement
	 * 
	 * @param color
	 */
	public void setActiveColor(Color color) {
		this.activeColor = color;
		listenersHandler.fireEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, ""));
	}

	public Color getActiveColor() {
		return activeColor;
	}

	public void addActionListener(ActionListener al) {
		listenersHandler.add(al);
	}

	private class MouseListener extends MouseAdapter {
		@Override
		public void mouseEntered(MouseEvent e) {
			setCursor(GuiCursor.CROSS_CURSOR);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			Point p = e.getPoint();
			setActiveColor(new Color(backgroundImage.getRGB(p.x, p.y)));
		}

	}

	@Override
	public ListenerHandler<ActionListener> getListenerHandler() {
		return listenersHandler;
	}

}
