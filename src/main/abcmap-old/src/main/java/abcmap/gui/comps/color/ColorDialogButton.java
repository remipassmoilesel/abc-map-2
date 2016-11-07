package abcmap.gui.comps.color;

import java.awt.Color;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.SwingUtilities;

import abcmap.gui.GuiCursor;
import abcmap.gui.GuiIcons;
import abcmap.utils.listeners.HasListenerHandler;
import abcmap.utils.listeners.ListenerHandler;
import abcmap.utils.threads.ThreadManager;

/**
 * Bouton d'ouverture d'une fenetre de choix de couleur personnalisée. Pour etre
 * a l'ecoute, utiliser getListenerHandler().add(WaitingForColor object)
 * 
 * @author remipassmoilesel
 *
 */
public class ColorDialogButton extends JButton implements HasListenerHandler<ColorEventListener> {

	private Color activeColor;
	private ListenerHandler<ColorEventListener> listenerHandler;

	public ColorDialogButton() {
		super(GuiIcons.CUSTOM_COLOR_BUTTON);
		listenerHandler = new ListenerHandler<ColorEventListener>();

		setCursor(GuiCursor.HAND_CURSOR);

		addActionListener(new CustomAL());
	}

	private class CustomAL implements ActionListener, Runnable {

		@Override
		public void actionPerformed(ActionEvent e) {
			// runLater pour appeler le dialog apres la diffusion des evenements
			SwingUtilities.invokeLater(this);
		}

		@Override
		public void run() {

			// Montrer le dialog de choix de la couleur
			Window frameParent = SwingUtilities.windowForComponent(ColorDialogButton.this);
			final Color color = JColorChooser.showDialog(frameParent, "", Color.white);

			// conserver la référence de la couleur choisie
			activeColor = color;

			// avertir les observateurs
			ThreadManager.runLater(new Runnable() {
				public void run() {
					listenerHandler.fireEvent(new ColorEvent(color, ColorDialogButton.this));
				}
			});

		}
	}

	public Color getActiveColor() {
		return activeColor;
	}

	/**
	 * Utiliser getListenerHandler()
	 */
	@Deprecated
	@Override
	public void addActionListener(ActionListener l) {
		super.addActionListener(l);
	};

	@Override
	public ListenerHandler<ColorEventListener> getListenerHandler() {
		return listenerHandler;
	}
}
