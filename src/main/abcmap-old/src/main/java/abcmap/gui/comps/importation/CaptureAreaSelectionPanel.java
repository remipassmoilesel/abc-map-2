package abcmap.gui.comps.importation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.event.DocumentEvent;

import abcmap.exceptions.InvalidInputException;
import abcmap.gui.comps.textfields.IntegerTextField;
import abcmap.utils.PrintUtils;
import abcmap.utils.Refreshable;
import abcmap.utils.gui.KeyListenerUtil;
import abcmap.utils.gui.GuiUtils;
import abcmap.utils.listeners.HasListenerHandler;
import abcmap.utils.listeners.ListenerHandler;
import net.miginfocom.swing.MigLayout;

/**
 * Composant graphique de sélection de zone de capture. Les valeurs de référence
 * sont celles des champs de texte 'width' et 'height'
 * 
 * @author remipassmoilesel
 *
 */
public class CaptureAreaSelectionPanel extends JPanel implements Refreshable,
		HasListenerHandler<ActionListener> {

	private static final int SIDE_X = 8;
	private static final int SIDE_Y = 5;

	private ArrayList<SelectionButton> buttons;
	private IntegerTextField txtWidth;
	private IntegerTextField txtHeight;

	private JPanel btnPanel;
	private JPanel txtPanel;
	private ListenerHandler<ActionListener> listenerHandler;

	public CaptureAreaSelectionPanel() {
		super(new MigLayout("insets 0, gap 5"));

		// gestion des evenements
		listenerHandler = new ListenerHandler<>();

		// panneau de boutons
		btnPanel = new JPanel(new MigLayout("insets 4, gap 4"));
		buttons = new ArrayList<SelectionButton>();
		ButtonActionListener bal = new ButtonActionListener();

		for (int y = 0; y < SIDE_Y; y++) {
			for (int x = 0; x < SIDE_X; x++) {

				// créer un bouton
				SelectionButton bt = new SelectionButton(x + 1, y + 1);
				bt.addActionListener(bal);

				// conserver la référence
				buttons.add(bt);

				// créer les contraintes de layout
				String constraints = "height 20px!, width 20px!,";
				if (x == SIDE_X - 1) {
					constraints += "wrap";
				}

				// ajout au panneau
				btnPanel.add(bt, constraints);
			}
		}

		add(btnPanel, "span, wrap");

		// champs de texte
		txtWidth = new IntegerTextField(3);
		txtHeight = new IntegerTextField(3);

		TextFieldListener tflistener = new TextFieldListener();
		KeyListenerUtil.addListener(txtWidth, tflistener);
		KeyListenerUtil.addListener(txtHeight, tflistener);

		txtPanel = new JPanel(new MigLayout("insets 5, gap 5"));

		GuiUtils.addLabel("Largeur: ", txtPanel);
		txtPanel.add(txtWidth);

		GuiUtils.addLabel("Hauteur: ", txtPanel);
		txtPanel.add(txtHeight, "wrap");

		add(txtPanel, "wrap");

		// initilialiser à la valeur 1
		setValues(1, 1);
	}

	/**
	 * Affecte une valeur au panneau et notifie les observateurs
	 * 
	 * @param width
	 * @param height
	 */
	public void setValues(int width, int height) {

		updateValuesWithourFire(width, height);

		// notification des observateurs
		fireActionPerformed();

	}

	/**
	 * Affecte une valeur au panneau sans notifier les observateurs
	 * 
	 * @param width
	 * @param height
	 */
	public void updateValuesWithourFire(int width, int height) {

		GuiUtils.throwIfNotOnEDT();

		// vérifier les valeurs en paramètre
		if (width < 1 || height < 1) {
			throw new IllegalArgumentException();
		}

		// modifier les boutons, avant les champs de texte
		changeButtonsValueWithoutFire(width, height);

		// modifier les champs de texte
		changeTextFieldsWithoutFire(width, height);

		refresh();
	}

	private void changeButtonsValueWithoutFire(int width, int height) {

		// appliquer les valeurs à tous les boutons, sélectionnés ou pas
		for (SelectionButton sb : buttons) {

			// déterminer la valeur de sélection en fonction de leur position
			boolean selected = false;
			if (sb.getWidthValue() <= width && sb.getHeightValue() <= height) {
				selected = true;
			}

			// appliquer si différente
			if (sb.isSelected() != selected) {
				GuiUtils.setSelected(sb, selected);
				sb.repaint();
			}

		}
	}

	private void changeTextFieldsWithoutFire(int width, int height) {

		// appliquer aux champs de texte
		String strWidth = String.valueOf(width);
		GuiUtils.changeText(txtWidth, strWidth);

		String strHeight = String.valueOf(height);
		GuiUtils.changeText(txtHeight, strHeight);

	}

	/**
	 * Ecoute la saisie de texte puis mets à jour les boutons et notifie les
	 * observateurs.
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class TextFieldListener extends KeyListenerUtil {

		@Override
		public void keyReleased(KeyEvent e) {

			// récupérer les caleurs saisies
			int width = -1;
			int height = -1;
			try {
				width = txtWidth.getIntegerValue();
				height = txtHeight.getIntegerValue();
			} catch (InvalidInputException e1) {

				// erreur de saisie, retour
				return;
			}

			setValues(width, height);

		}

	}

	private class ButtonActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			SelectionButton source = (SelectionButton) e.getSource();
			int w = source.getWidthValue();
			int h = source.getHeightValue();

			// appliquer aux boutons et champs de texte
			setValues(w, h);

		}

	}

	private static class SelectionButton extends JToggleButton {

		private static final Color SELECTION_COLOR = Color.blue;
		private int widthValue = 0;
		private int heightValue = 0;

		public SelectionButton(int wVal, int hVal) {
			this.widthValue = wVal;
			this.heightValue = hVal;

			setToolTipText(wVal + " x " + hVal);
		}

		@Override
		protected void paintComponent(Graphics g) {

			// le bouton est sélectionné, peinture bleu sur toute la surface
			if (isSelected()) {
				Rectangle b = getBounds();
				b.x = 0;
				b.y = 0;
				Graphics2D g2d = (Graphics2D) g;
				g2d.setColor(SELECTION_COLOR);
				g2d.fill(b);
			}

			// sinon peinture normale
			else {
				super.paintComponent(g);
			}
		}

		public int getWidthValue() {
			return widthValue;
		}

		public int getHeightValue() {
			return heightValue;
		}

	}

	/**
	 * Retourne les valeurs en cours ou null si erreur
	 * 
	 * @return
	 * @throws InvalidInputException
	 */
	public Dimension getValues() {

		Integer width;
		Integer height;
		try {
			width = txtWidth.getIntegerValue();
			height = txtHeight.getIntegerValue();
		} catch (InvalidInputException e) {
			return null;
		}

		return new Dimension(width, height);
	}

	protected void fireActionPerformed() {
		listenerHandler.fireEvent(new ActionEvent(this,
				ActionEvent.ACTION_PERFORMED, ""));
	}

	@Override
	public void refresh() {

		btnPanel.revalidate();
		btnPanel.repaint();

		txtWidth.repaint();
		txtHeight.repaint();

		this.revalidate();
		this.repaint();
	}

	@Override
	public void reconstruct() {

	}

	@Override
	public ListenerHandler<ActionListener> getListenerHandler() {
		return listenerHandler;
	}

}
