package abcmap.gui.comps.textfields.search;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

public class InteractiveTextField extends JTextField {

	private static final Pattern WHITE_TEXT = Pattern.compile("^\\W*$");
	private JLabel testLabel;

	private InteractivePopupDisplay popup;

	public InteractiveTextField() {

		// a l'ecoute des changements de curseur
		this.addCaretListener(new CustomCaretL());

		// popup d'affichage des interactions possibles
		this.popup = new InteractivePopupDisplay(this);

	}

	private class CustomCaretL implements CaretListener {
		@Override
		public void caretUpdate(CaretEvent e) {

			// texte vide: cacher le panneau
			Matcher m = WHITE_TEXT.matcher(getText());
			if (m.find()) {
				showPopup(false);
			}

			// texte non vide: traitement
			else {
				userHaveTypedThis(getText());
			}

			requestFocus();

		}
	}

	protected void userHaveTypedThis(String text) {

		// implementation test a remplacer
		if (testLabel == null) {
			testLabel = new JLabel();
			getPopupContentPane().add(testLabel);
		}

		testLabel.setText(text);
		testLabel.revalidate();
		testLabel.repaint();

		showPopup(true);
		refreshPopup();

	}

	protected void refreshPopup() {
		popup.revalidate();
		popup.repaint();
	}

	protected JPanel getPopupContentPane() {
		return (JPanel) popup.getContentPane();
	}

	/**
	 * N'utiliser qu'une fois l'element ajouter à une fenetre
	 */
	protected void showPopup(boolean val) {
		popup.showPopup(val);
	}

	public InteractivePopupDisplay getPopup() {
		return popup;
	}

}
