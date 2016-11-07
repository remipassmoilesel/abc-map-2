package abcmap.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import static org.junit.Assert.*;

/**
 * Fait échoué le test si une de ses méthodes est appelée.
 * 
 * @author remipassmoilesel
 *
 */
public class FailCustomListener implements DocumentListener, KeyListener,
		ActionListener, ListSelectionListener, CaretListener, ChangeListener {

	private static int staticCount = 0;
	private int id;

	public FailCustomListener() {
		staticCount++;
		id = staticCount;
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		fail();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		fail();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		fail();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		fail();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		fail();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		fail();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		fail();
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		fail();
	}

	@Override
	public void caretUpdate(CaretEvent e) {
		fail();
	}

	@Override
	public String toString() {
		return "#" + id + "_" + super.toString();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		fail();
	}

}