package abcmap.gui;

import java.awt.Component;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.junit.Assert;
import org.junit.Test;

import abcmap.utils.gui.GuiUtils;

public class GuiUtilsTests {

	/**
	 * Vérifier la fonction de vérification de thread: EDT ou NON
	 */
	@Test(expected = IllegalStateException.class)
	public void throwIfNotOnEdtTest() {
		GuiUtils.throwIfNotOnEDT();
	}

	/**
	 * Verification de la recherche de parent de composant en fonction de sa
	 * classe. Test à développer.
	 * 
	 * @throws InvocationTargetException
	 * @throws InterruptedException
	 */
	@Test
	public void searchParentTest() throws InvocationTargetException,
			InterruptedException {

		SwingUtilities.invokeAndWait(new Runnable() {

			@Override
			public void run() {

				JPanel panel = new JPanel();
				JPanel panel2 = new JPanel();
				JPanel panel3 = new JPanel();

				panel.add(panel2);
				panel2.add(panel3);

				Assert.assertEquals(panel2,
						GuiUtils.searchParentOf(panel3, JPanel.class));

				Assert.assertEquals(null,
						GuiUtils.searchParentOf(new JPanel(), JPanel.class));
			}
		});

	}

	/**
	 * Vérification du listage d'enfant récursif
	 * 
	 * @throws InvocationTargetException
	 * @throws InterruptedException
	 */
	@Test
	public void searchChildsTest() throws InvocationTargetException,
			InterruptedException {

		SwingUtilities.invokeAndWait(new Runnable() {

			@Override
			public void run() {

				// créer des conteneurs
				JPanel panel = new JPanel();
				JPanel panel2 = new JPanel();
				JPanel panel3 = new JPanel();

				// imbrication
				panel.add(panel2);
				panel2.add(panel3);

				// résultat attendu
				List<JPanel> rslt = Arrays.asList(panel, panel2, panel3);

				// recherche
				List<Component> list = GuiUtils.listAllComponentsFrom(panel);

				Assert.assertTrue(rslt.equals(list));
			}
		});

	}
}
