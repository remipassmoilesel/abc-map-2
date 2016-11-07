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
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.geotools.styling.SelectedChannelType;

import abcmap.utils.Utils;
import abcmap.utils.gui.GuiUtils;
import abcmap.utils.sandbox.ExecutionTimeComputing;

/**
 * Tester dans le cas d'une champs de texte si le test de valeur préalable à un
 * changement fait gagner du temps. Exemple:
 * <p>
 * txt.getText() == Texte 1, <br>
 * la valeur à lui donner est Texte 2, <br>
 * est ce que le fait de lui réatribuer la même valeur prends plus de temps que
 * de tester et d'éventuellement ne pas luis réatribuer cette valeur ?
 * 
 * @author remipassmoilesel
 *
 */
public class TextFChangeValuesOnlyIfNeeded {

	/*
	 * Résultats:
	 * 
	 * Début des mesures... Nombre de changements de valeurs: 100 100 essais.
	 * Moyenne: 0.045 s. Minimum: 0.041 s. Maximum: 0.103 s.
	 * 
	 * Début des mesures... Nombre de changements de valeurs: 67 100 essais.
	 * Moyenne: 0.029 s. Minimum: 0.0 s. Maximum: 0.045 s.
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

		private JTextField txt;
		private int nbrOfChanges;
		private String text1;
		private String text2;

		public TimeComputingArea1() {

			text1 = "Texte 1";
			text2 = "Texte 2";

			txt = new JTextField(text1);

			// ecouteurs ajoutés pour simuler au mieux une situation réelle
			txt.addCaretListener(new TestListener());
			txt.addActionListener(new TestListener());

			nbrOfChanges = 0;

			// lancement de fenetre pour simuler au mieux une situation réelle
			GuiUtils.showThis(txt);
		}

		@Override
		protected void actionToMeasure() {

			try {
				SwingUtilities.invokeAndWait(new Runnable() {

					@Override
					public void run() {
						// changer la valeur uniquement lorsque la position est
						// paire
						String textToInsert = text1;
						if (currentPos % 3 == 0) {
							textToInsert = text2;
						}

						// changer systematiquement la valeur de la checkbox
						txt.setText(textToInsert);
						txt.revalidate();
						txt.repaint();
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

		private JTextField txt;
		private int nbrOfChanges;
		private String text1;
		private String text2;

		public TimeComputingArea2() {

			text1 = "Texte 1";
			text2 = "Texte 2";

			txt = new JTextField(text1);

			// ecouteurs ajoutés pour simuler au mieux une situation réelle
			txt.addCaretListener(new TestListener());
			txt.addActionListener(new TestListener());

			nbrOfChanges = 0;

			// lancement de fenetre pour simuler au mieux une situation réelle
			GuiUtils.showThis(txt);
		}

		@Override
		protected void actionToMeasure() {

			try {
				SwingUtilities.invokeAndWait(new Runnable() {

					@Override
					public void run() {
						// changer la valeur uniquement lorsque la position est
						// paire
						String textToInsert = text1;
						if (currentPos % 3 == 0) {
							textToInsert = text2;
						}

						// changer systematiquement la valeur de la checkbox
						if (txt.getText().equals(textToInsert) == false) {
							txt.setText(textToInsert);
							txt.revalidate();
							txt.repaint();
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
	private static class TestListener implements CaretListener, ActionListener {

		@Override
		public void caretUpdate(CaretEvent e) {

			try {
				Thread.sleep(20);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

}
