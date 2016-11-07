package oldtrys.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.SwingUtilities;

import abcmap.utils.Utils;
import abcmap.utils.gui.GuiUtils;

/**
 * Essai concernant les listeners
 * <p>
 * L'appel de la méthode setSelected ne déclenche pas de ActionListener, mais
 * déclenche un ItemListener
 * 
 * @author remipassmoilesel
 *
 */
public class CheckBoxListener implements Runnable {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new CheckBoxListener());
	}

	private JCheckBox checkbox;

	@Override
	public void run() {

		checkbox = new JCheckBox(Utils.generateLoremIpsum(20));

		checkbox.addActionListener(new CustomActionListener());
		checkbox.addItemListener(new CustomItemListener());

		GuiUtils.showThis(checkbox);

	}

	private void changeLater() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {

				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				checkbox.setSelected(!checkbox.isSelected());
			}
		});
	}

	private class CustomItemListener implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent e) {
			System.out
					.println("CheckBoxListener.CustomItemListener.itemStateChanged()");

			changeLater();
		}

	}

	private class CustomActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			System.out
					.println("CheckBox.CustomActionListener.actionPerformed()");

			changeLater();

		}

	}

}