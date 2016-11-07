package oldtrys.gui;

import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import abcmap.utils.gui.GuiUtils;

public class TextFieldListener implements Runnable {

	public static void launch() {
		SwingUtilities.invokeLater(new TextFieldListener());
	}

	private JTextField txtW;
	private JTextComponent txtH;

	@Override
	public void run() {

		JPanel panel = new JPanel();
		txtW = new JTextField(20);
		txtH = new JTextField(20);

		txtW.getDocument().addDocumentListener(new CustomDocL());
		txtW.addCaretListener(new CustomCaretL());

		txtH.getDocument().addDocumentListener(new CustomDocL());
		txtH.addCaretListener(new CustomCaretL());

		panel.add(txtW);
		panel.add(txtH);

		GuiUtils.showThis(panel);

	}

	private class CustomCaretL implements CaretListener {
		@Override
		public void caretUpdate(CaretEvent e) {
			printDims(e);
		}
	}

	private class CustomDocL implements DocumentListener {

		@Override
		public void removeUpdate(DocumentEvent e) {
			docEvent(e);
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			docEvent(e);
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			docEvent(e);
		}

		public void docEvent(DocumentEvent e) {
			printDims(e);
		}
	}

	private void printDims(Object event) {

		try {
			int w = Integer.valueOf(txtW.getText());
			int h = Integer.valueOf(txtH.getText());

			Dimension dims = new Dimension(w, h);

			System.out.println(dims + " " + event.getClass().getSimpleName());

		} catch (Exception e2) {
			System.out.println("                                  " + "... Exception from "
					+ event.getClass().getSimpleName());
		}

	}

}
