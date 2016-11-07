package abcmap.draw.tools;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.font.TextAttribute;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.lang.StringUtils;

import abcmap.draw.shapes.Label;
import abcmap.managers.MapManager;
import abcmap.managers.stub.MainManager;
import abcmap.utils.gui.GuiUtils;

/**
 * Zone de texte spéciale pour modification d'étiquette de texte. <br>
 * <b>Attention, ne pas utiliser JTextArea, retours à la ligne intempestifs.</b>
 * 
 * @author remipassmoilesel
 *
 */
public class LabelEditorPanel extends JPanel {

	private MapManager mapm;

	private Label label;
	private Font font;
	private JEditorPane editor;

	private JLabel lblSize;
	private String sentenceRemainingChars;
	private String sentenceTooMuchChars;

	private Dimension minimalDimensions;

	private int maxChars;

	public LabelEditorPanel() {

		super(new BorderLayout());

		this.mapm = MainManager.getMapManager();

		// proprietes
		this.setVisible(false);
		this.setOpaque(true);
		this.setBorder(BorderFactory.createDashedBorder(Color.darkGray, 2, 2, 2, false));

		// l'objet a modifier
		this.label = null;

		// la zone de texte
		editor = new JEditorPane();
		add(editor, BorderLayout.CENTER);

		// ecouter les changements de contenu
		EditorListener editorListener = new EditorListener();
		editor.getDocument().addDocumentListener(editorListener);
		// editor.addCaretListener(editorListener);

		// indications sur le texte restant
		sentenceRemainingChars = "  " + "Caractère(s) restant(s): ";
		sentenceTooMuchChars = "  " + "Caractère(s) en trop: ";

		lblSize = GuiUtils.addLabel(sentenceRemainingChars, this, BorderLayout.SOUTH);

		// dimensions minimales, quelque soit la taille du texte
		this.minimalDimensions = new Dimension(200, 100);

		this.maxChars = Label.MAX_TEXT_SIZE;

	}

	/**
	 * Debut de moficiation 'une etiquette de texte
	 * 
	 * @param label
	 */
	public void startModification(Label label) {

		// affecter l'objet
		this.label = label;

		// ajout de la zone de texte a la carte
		mapm.getMapComponent().add(this);
		this.setVisible(true);

		// remplir la zone de mofication
		editor.setText(label.getText());

		// recuperer la police de l'element
		// l'adapter à l'echelle d'affichage
		font = label.getSwingFont();
		Map<TextAttribute, Object> map = new Hashtable<TextAttribute, Object>();
		map.put(TextAttribute.SIZE, label.getFontSize() * mapm.getDisplayScale());
		font = font.deriveFont(map);

		editor.setFont(font);

		// rafraichir le panneau
		refresh();

		// demander le focus
		editor.requestFocus();
		editor.requestFocusInWindow();
	}

	/**
	 * Maj des dimensions de l'element en fonction de son contenu
	 */
	public void refresh() {

		// necessite des graphics pour les calculs de police
		Graphics g = editor.getGraphics();

		// mise à jour de l'indicateur de taille de texte
		// recuperer le texte restant
		int diff = maxChars - editor.getText().length();

		// determiner l'affichage
		String txt = diff > 0 ? sentenceRemainingChars + diff : sentenceTooMuchChars + Math.abs(diff);

		// afficher
		lblSize.setText(txt);

		if (g != null) {

			// outil de mesure de la police
			FontMetrics fm = g.getFontMetrics();
			Dimension oneChar = new Dimension(fm.stringWidth("m"), fm.getHeight());

			// nouvelles dimensions
			Rectangle bounds = new Rectangle();

			// placer l'editeur à l'emplacement de l'objet
			Point labelPos = label.getPosition();
			bounds.setLocation(mapm.getMapComponent().getPointFromViewToComponentSpace(labelPos));

			String text = editor.getText();

			// calculer la hauteur en fonction du nombre de saut de ligne
			bounds.height = StringUtils.countMatches(text, System.lineSeparator()) * oneChar.height;

			// calculer la largeur en fonctions du contenu
			String[] lines = text.split(System.lineSeparator());
			for (String l : lines) {

				// calcul de la largeur: ne retenir que la ligne la plus large
				int w = fm.stringWidth(l);
				if (w > bounds.width)
					bounds.width = w;

			}

			// ajouter la hauteur du label d'indication de taille
			bounds.height += lblSize.getHeight();

			// ajuster les dimensions minimales en fonction de la taille police
			// (ex: police 150px)
			Dimension adjustedMinDim = new Dimension();
			adjustedMinDim.width = oneChar.width * 3;
			adjustedMinDim.height = oneChar.height * 2;

			if (adjustedMinDim.width < minimalDimensions.width)
				adjustedMinDim.width = minimalDimensions.width;

			if (adjustedMinDim.height < minimalDimensions.height)
				adjustedMinDim.height = minimalDimensions.height;

			if (bounds.width < adjustedMinDim.width)
				bounds.width = adjustedMinDim.width;

			if (bounds.height < adjustedMinDim.height)
				bounds.height = adjustedMinDim.height;

			// ajouter une marge
			bounds.width += oneChar.width * 2;
			bounds.height += oneChar.height * 2;

			// appliquer les changements
			this.setBounds(bounds);

		}

		// rafraichir
		lblSize.revalidate();
		lblSize.repaint();

		editor.revalidate();
		editor.repaint();

		this.revalidate();
		this.repaint();

		this.getParent().repaint();
	}

	/**
	 * Arret des modifications
	 */
	public void stopModification() {

		// masquer le composant
		this.setVisible(false);

		// enlever le composant
		mapm.getMapComponent().remove(this);

	}

	/**
	 * Recuperer le texte caracteres superflus
	 * 
	 * @return
	 */
	public String getTrimText() {
		return editor.getText().trim();
	}

	private class EditorListener implements CaretListener, DocumentListener {

		/**
		 * Mettre à jour la taille du champs + le label d'indications de taille
		 * de texte
		 */
		@Override
		public void caretUpdate(CaretEvent e) {
			refresh();
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			refresh();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			refresh();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			refresh();
		}

	}

}
