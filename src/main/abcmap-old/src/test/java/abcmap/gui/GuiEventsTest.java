package abcmap.gui;

import java.lang.reflect.InvocationTargetException;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

import org.junit.Test;

import abcmap.utils.gui.GuiUtils;

public class GuiEventsTest {

	/**
	 * Verifier que les changements programmatiques n'envoient pas d'évènements
	 * 
	 * @throws InvocationTargetException
	 * @throws InterruptedException
	 */
	@Test
	public void changeFormComponentsWithoutFire()
			throws InvocationTargetException, InterruptedException {

		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {

				/*
				 * champs de texte et document listener
				 */
				JTextField comp = new JTextField();
				comp.getDocument()
						.addDocumentListener(new FailCustomListener());

				// comp.setText("new text"); // will fail
				GuiUtils.changeTextWithoutFire(comp, "new text");
				

				/*
				 * champs de texte et document listener
				 */
				JTextField comp8 = new JTextField();
				comp8.addCaretListener(new FailCustomListener());

				// comp8.setText("new text"); // will fail
				GuiUtils.changeText(comp, "new text");

				/*
				 * Champs de texte et keylisteners
				 */
				JTextField comp2 = new JTextField();
				comp2.addKeyListener(new FailCustomListener());
				comp2.setText("new text");

				/*
				 * Checkbox et radio et toggle
				 */
				JCheckBox comp3 = new JCheckBox();
				comp3.addActionListener(new FailCustomListener());

				comp3.setSelected(true);

				JRadioButton comp4 = new JRadioButton();
				comp4.addActionListener(new FailCustomListener());

				comp4.setSelected(true);

				JToggleButton comp5 = new JToggleButton();
				comp5.addActionListener(new FailCustomListener());

				comp5.setSelected(true);

				/*
				 * Listes
				 */

				String[] model = new String[] { "item 1", "item 2" };
				JList comp6 = new JList<>(model);
				comp6.addListSelectionListener(new FailCustomListener());

				// list.setSelectedIndex(1);
				// list.setSelectedValue(model[0], true);

				GuiUtils.changeWithoutFire(comp6, model[0], true);

				/*
				 * JComboBox
				 */
				JComboBox<String> comp7 = new JComboBox<String>(model);
				comp7.addActionListener(new FailCustomListener());

				GuiUtils.changeIndexWithoutFire(comp7, 0);
				GuiUtils.changeWithoutFire(comp7, model[1]);

				/*
				 * Sliders
				 */

				JSlider slider = new JSlider(1, 20, 10);
				slider.addChangeListener(new FailCustomListener());
				// slider.setValue(7); // will fail
				GuiUtils.changeWithoutFire(slider, 20);
			}
		});

	}
}
