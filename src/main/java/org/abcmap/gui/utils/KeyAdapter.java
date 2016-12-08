package org.abcmap.gui.utils;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

/**
 * Simple utility to avoid implements all methods of KeyListener
 *
 * @author remipassmoilesel
 */
public class KeyAdapter implements KeyListener {

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
