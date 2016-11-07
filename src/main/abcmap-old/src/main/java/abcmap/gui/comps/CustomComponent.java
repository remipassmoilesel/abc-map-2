package abcmap.gui.comps;

import java.awt.Graphics;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JButton;

import abcmap.gui.GuiColors;
import abcmap.gui.GuiCursor;
import abcmap.utils.gui.FocusPainter;
import net.miginfocom.swing.MigLayout;

/**
 * Composant simple focusable. Doit être condidéré comme un JPanel.
 */
public class CustomComponent extends JButton {

	private boolean focused;
	private FocusPainter painter;

	public CustomComponent() {
		super();

		painter = new FocusPainter(GuiColors.PANEL_BACKGROUND);

		// curseur particulier
		setCursor(GuiCursor.HAND_CURSOR);

		// style
		setOpaque(true);
		setBorder(null);
		setLayout(new MigLayout("insets 5"));

		// navigation par clavier
		setFocusable(true);

		// listeners
		addMouseListener(new CustomMouseAdapter());

	}

	/**
	 * Peindre la couleur de fond en fonction du focus
	 */
	@Override
	protected void paintComponent(Graphics g) {

		// repeindre selon le focus de l'element
		painter.draw(g, this, isFocused());

	}

	public boolean isFocused() {
		return focused;
	}

	public void setFocused(boolean hovered) {
		this.focused = hovered;
	}

	private class CustomMouseAdapter extends MouseAdapter implements FocusListener {

		@Override
		public void mouseEntered(MouseEvent e) {
			setFocused(true);
			repaint();
		}

		@Override
		public void mouseExited(MouseEvent e) {
			setFocused(false);
			repaint();
		}

		@Override
		public void focusGained(FocusEvent e) {
			setFocused(true);
			repaint();
		}

		@Override
		public void focusLost(FocusEvent e) {
			setFocused(false);
			repaint();
		}

	}

	/**
	 * Ce composant ne doit pas être considéré comme un bouton
	 */
	@Deprecated
	@Override
	public void setIcon(Icon defaultIcon) {
		super.setIcon(defaultIcon);
	}

	/**
	 * Ce composant ne doit pas être considéré comme un bouton
	 */
	@Deprecated
	@Override
	public void setText(String text) {
		super.setText(text);
	}

}
