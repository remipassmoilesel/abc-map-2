package oldtrys.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import abcmap.utils.gui.GuiUtils;

public class Leeter implements Runnable {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Leeter());
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

			System.out.println("Leeter.CustomKeyListener.keyReleased()");

			// recuperer le texte
			String text = textComponent.getText();

			// le modifier
			String leeted = text.replaceAll("e", "3");

			// remplacer letexte
			textComponent.setText(leeted);
		}

		@Override
		public void keyTyped(KeyEvent e) {

		}

		@Override
		public void keyPressed(KeyEvent e) {

		}

	}

}