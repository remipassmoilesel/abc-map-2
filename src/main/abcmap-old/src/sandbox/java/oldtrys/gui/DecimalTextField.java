package oldtrys.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import abcmap.utils.gui.GuiUtils;

/**
 * Essai concernant la modification de valeurs d'un champs de texte.
 * <p>
 * setText() déclenchée par un keyListener ne lève pas d'exception
 * 
 * @author remipassmoilesel
 *
 */
public class DecimalTextField implements Runnable {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new DecimalTextField());
	}

	private JTextField textComponent;

	@Override
	public void run() {

		textComponent = new JTextField(30);
		textComponent.addKeyListener(new CustomKeyListener());

		GuiUtils.showThis(textComponent);
	}

	private class CustomKeyListener implements KeyListener {

		@Override
		public void keyReleased(KeyEvent e) {
			System.out
					.println("DecimalTextField.CustomKeyListener.keyReleased()");

			// recuperer le texte
			String text = textComponent.getText();

			// le modifier
			String formatted = text.replaceAll("[^0-9\\.]", "");

			// remplacer letexte
			textComponent.setText(formatted);
		}

		@Override
		public void keyTyped(KeyEvent e) {

		}

		@Override
		public void keyPressed(KeyEvent e) {

		}

	}

}