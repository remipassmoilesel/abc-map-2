package oldtrys.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import abcmap.utils.gui.GuiUtils;

public class JListListener implements Runnable {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new JListListener());
	}

	private JList jlist;
	private String[] values;

	@Override
	public void run() {

		values = new String[] { "Aaaaaa", "Bbbbbbb", "Cccccc" };

		jlist = new JList<String>(values);

		jlist.addListSelectionListener(new CustomListener());

		GuiUtils.showThis(jlist);

	}

	private class CustomListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {

			System.out.println("JListListener.CustomListener.valueChanged()");

			// SwingUtilities.invokeLater(new Runnable() {
			// @Override
			// public void run() {
			//
			// try {
			// Thread.sleep(2000);
			// } catch (InterruptedException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			//
			// // jlist.setSelectedIndex(0);
			//
			// jlist.setSelectedValue(values[0], true);
			//
			// jlist.repaint();
			// }
			// });
		}

	}

}