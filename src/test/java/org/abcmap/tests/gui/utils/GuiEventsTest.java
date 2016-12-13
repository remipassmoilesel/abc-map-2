package org.abcmap.tests.gui.utils;

import org.abcmap.gui.utils.GuiUtils;
import org.junit.Test;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

public class GuiEventsTest {

    /**
     * Check that special changes not fire events
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
                 * Text fields
                 */

                JTextField comp = new JTextField();
                comp.getDocument()
                        .addDocumentListener(new FailCustomListener());

                // comp.setText("new text"); // will fail
                GuiUtils.changeTextWithoutFire(comp, "new text");


                JTextField comp8 = new JTextField();
                comp8.addCaretListener(new FailCustomListener());

                // comp8.setText("new text"); // will fail
                GuiUtils.changeText(comp, "new text");


                JTextField comp2 = new JTextField();
                comp2.addKeyListener(new FailCustomListener());
                comp2.setText("new text");


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
                 * Lists
                 */

                String[] model = new String[]{"item 1", "item 2"};
                JList comp6 = new JList<>(model);
                comp6.addListSelectionListener(new FailCustomListener());

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
