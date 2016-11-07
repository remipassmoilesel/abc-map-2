package abcmap.gui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.EventListener;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.junit.Test;

import abcmap.utils.gui.GuiUtils;

public class RemovingGuiListenersTest {

	/**
	 * Vérifier que la méthode utilitaire de retrait des listeners fonctionne.
	 * 
	 * @throws InvocationTargetException
	 * @throws InterruptedException
	 */

	@Test
	public void removeListenersTest() throws InvocationTargetException,
			InterruptedException {

		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {

				// !! \\ il peut y avoir plus de listeners que ceux ajoutés

				/*
				 * Champs de texte
				 */
				JTextField comp1 = new JTextField();
				comp1.addCaretListener(new FailCustomListener());
				comp1.addCaretListener(new FailCustomListener());
				comp1.getDocument().addDocumentListener(
						new FailCustomListener());
				comp1.getDocument().addDocumentListener(
						new FailCustomListener());

				ArrayList<EventListener> listeners = GuiUtils
						.removeSupportedListenersFrom(comp1);

				assertTrue(listeners.size() >= 4);

				/*
				 * JList
				 */

				String[] model = new String[] { "item 1", "item 2" };
				JList comp2 = new JList<>(model);
				comp2.addListSelectionListener(new FailCustomListener());
				comp2.addListSelectionListener(new FailCustomListener());
				comp2.addListSelectionListener(new FailCustomListener());

				ArrayList<EventListener> listeners2 = GuiUtils
						.removeSupportedListenersFrom(comp2);

				assertTrue(listeners2.size() >= 3);

				/*
				 * JCombobox
				 */

				JComboBox<String> comp3 = new JComboBox<>(model);
				comp3.addActionListener(new FailCustomListener());
				comp3.addActionListener(new FailCustomListener());
				comp3.addActionListener(new FailCustomListener());
				comp3.addActionListener(new FailCustomListener());

				ArrayList<EventListener> listeners3 = GuiUtils
						.removeSupportedListenersFrom(comp3);

				assertTrue(listeners3.size() >= 4);

			}
		});

	}

}
