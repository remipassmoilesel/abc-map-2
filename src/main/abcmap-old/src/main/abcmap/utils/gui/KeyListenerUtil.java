package abcmap.utils.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

/**
 * Classe utilitaire permettant de ne pas avoir à implementer toutes les
 * méthodes de l'interface KeyListener.
 * <p>
 * Utiliser les méthode addListener et removeListener permet de pouvoir modifier
 * éventuellement le listener de manière globale simplement.
 * 
 * @author remipassmoilesel
 *
 */
public class KeyListenerUtil implements KeyListener {

	public static void removeListener(JTextComponent txt, KeyListener listener) {
		txt.removeKeyListener(listener);
	}

	public static void addListener(JTextComponent txt, KeyListener listener) {
		txt.addKeyListener(listener);
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {

	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

}
