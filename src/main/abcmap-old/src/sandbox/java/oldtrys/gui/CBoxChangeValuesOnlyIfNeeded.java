package oldtrys.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.geotools.styling.SelectedChannelType;

import abcmap.utils.Utils;
import abcmap.utils.gui.GuiUtils;
import abcmap.utils.sandbox.ExecutionTimeComputing;

/**
 * Tester dans le cas d'une checkbox si le test de valeur préalable à un
 * changement fait gagner du temps. Exemple:
 * <p>
 * checkbox1.isSelected() == true, <br>
 * la valeur à lui donner est true, <br>
 * est ce que le fait de lui réatribuer la même valeur prends plus de temps que
 * de tester et d'éventuellement ne pas luis réatribuer cette valeur ?
 * 
 * @author remipassmoilesel
 *
 */
public class CBoxChangeValuesOnlyIfNeeded {

	/*
	 * Résultats:
	 * 
	 * Changements systématiques: Début des mesures... Nombre de changements de
	 * valeurs: 100 100 essais. Moyenne: 0.01 s. Minimum: 0.0 s. Maximum: 0.112
	 * s.
	 * 
	 * Changements si nécéssaires: Début des mesures... Nombre de changements de
	 * valeurs: 33 100 essais. Moyenne: 0.007 s. Minimum: 0.0 s. Maximum: 0.021
	 * s.
	 * 
	 * Les différences sont minimes, et il y a de fortes chances que l'appel à
	 * l'EDT prennet plus de temps que les changements de valeurs.
	 */

	public static void main(String[] args) {
		launch();
	}

	public static void launch() {

		TimeComputingArea1 tca1 = new TimeComputingArea1();
		TimeComputingArea2 tca2 = new TimeComputingArea2();

		tca1.launchAndPrintResume();
		System.out.println();
		tca2.launchAndPrintResume();
	}

	private static class TimeComputingArea1 extends ExecutionTimeComputing {

		private JCheckBox cbox;
		private boolean selectState;
		private int nbrOfChanges;

		public TimeComputingArea1() {
			cbox = new JCheckBox("Checkbox 1");

			// ecouteurs ajoutés pour simuler au mieux une situation réelle
			cbox.addActionListener(new TestListener());
			cbox.addItemListener(new TestListener());

			selectState = true;

			nbrOfChanges = 0;

			// lancement de fenetre pour simuler au mieux une situation réelle
			GuiUtils.showThis(cbox);
		}

		@Override
		protected void actionToMeasure() {

			try {
				SwingUtilities.invokeAndWait(new Runnable() {

					@Override
					public void run() {
						// changer la valeur uniquement lorsque la position est
						// paire
						if (currentPos % 3 == 0) {
							selectState = !selectState;
						}

						// changer systematiquement la valeur de la checkbox
						cbox.setSelected(selectState);
						cbox.revalidate();
						cbox.repaint();
						nbrOfChanges++;
					}
				});
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		@Override
		protected void afterMeasures() {
			System.out.println("Nombre de changements de valeurs: "
					+ nbrOfChanges);
		}

	}

	private static class TimeComputingArea2 extends ExecutionTimeComputing {

		private JCheckBox cbox;
		private boolean selectState;
		private int nbrOfChanges;

		public TimeComputingArea2() {
			cbox = new JCheckBox("Checkbox 2");

			// ecouteurs ajoutés pour simuler au mieux une situation réelle
			cbox.addActionListener(new TestListener());
			cbox.addItemListener(new TestListener());

			selectState = true;

			nbrOfChanges = 0;

			// lancement de fenetre pour simuler au mieux une situation réelle
			GuiUtils.showThis(cbox);

		}

		@Override
		protected void actionToMeasure() {

			try {
				SwingUtilities.invokeAndWait(new Runnable() {

					@Override
					public void run() {
						// changer la valeur uniquement lorsque la position est
						// paire
						if (currentPos % 3 == 0) {
							selectState = !selectState;
						}

						// ne changer la valeur que si nécéssaire
						if (cbox.isSelected() != selectState) {
							cbox.setSelected(selectState);
							cbox.revalidate();
							cbox.repaint();
							nbrOfChanges++;
						}
					}
				});
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		@Override
		protected void afterMeasures() {
			System.out.println("Nombre de changements de valeurs: "
					+ nbrOfChanges);
		}

	}

	/**
	 * Classe écouteur de test qui ralentit le code
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private static class TestListener implements ItemListener, ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// ne doit normalement pas être déclenché

			// System.err
			// .println("AB_TimeComputingArea.TestListener.actionPerformed()");

			try {
				Thread.sleep(20);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		@Override
		public void itemStateChanged(ItemEvent e) {

			// System.err
			// .println("AB_TimeComputingArea.TestListener.itemStateChanged()");

			try {
				Thread.sleep(20);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

}
