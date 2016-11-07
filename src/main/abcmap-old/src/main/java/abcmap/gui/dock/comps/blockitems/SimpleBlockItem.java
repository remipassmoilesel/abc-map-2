package abcmap.gui.dock.comps.blockitems;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import abcmap.gui.GuiColors;
import abcmap.gui.GuiCursor;
import abcmap.gui.GuiIcons;
import abcmap.gui.GuiStyle;
import abcmap.gui.comps.buttons.HtmlLabel;
import abcmap.gui.ie.InteractionElement;
import abcmap.utils.Refreshable;
import abcmap.utils.gui.FocusPainter;
import abcmap.utils.gui.GuiUtils;
import abcmap.utils.gui.HasExpandableHelp;
import net.miginfocom.swing.MigLayout;

public class SimpleBlockItem extends JPanel implements HasExpandableHelp,
		Refreshable {

	/** L'element d'interaction qui determine les caractertiques du composant */
	protected InteractionElement interactionElmt;

	protected JComponent labelName;
	protected JLabel buttonHelp;
	protected Component bottomComponent;

	protected BlockItemHelpArea helpArea;
	protected boolean helpVisible = false;

	private boolean changeColorUnderFocus;
	private boolean focused;

	private FocusPainter focusPainter;

	public final static SimpleBlockItem create(InteractionElement ielmt,
			Component bottom) {
		SimpleBlockItem smi = new SimpleBlockItem(ielmt);
		smi.setBottomComponent(bottom);
		smi.reconstruct();
		return smi;
	}

	/**
	 * Préférer le constructeur avec Element d'Interaction
	 */
	protected SimpleBlockItem() {

		// layout
		super(new MigLayout("insets 5"));

		focusPainter = new FocusPainter(GuiColors.PANEL_BACKGROUND);

		// caracteristiques
		setOpaque(true);
		helpVisible = false;

		// element d'interaction
		interactionElmt = null;

		// etiquette et bouton d'aide
		labelName = new HtmlLabel("No label");
		((HtmlLabel) labelName).setStyle(GuiStyle.SIMPLE_BLOCK_ITEM_LABEL);

		// changements de couleurs en fonction du focus
		focused = false;
		changeColorUnderFocus = false;
		BackgroundColorListener bgColList = new BackgroundColorListener();
		labelName.addMouseListener(bgColList);
		labelName.addFocusListener(bgColList);

		// champs d'aide
		buttonHelp = new JLabel(GuiIcons.DI_ITEM_HELP);
		buttonHelp.addMouseListener(new HelpActionListener());
		buttonHelp.setCursor(GuiCursor.HAND_CURSOR);

		helpArea = new BlockItemHelpArea();

		// ajout des composants
		// largeur fixe pour éviter problemes de redimensionnement
		add(labelName, "width 230px!");
		add(buttonHelp, "wrap");

		expandHelp(helpVisible);

	}

	public SimpleBlockItem(InteractionElement elmt) {
		this();
		setInteractionElement(elmt);
		reconstruct(helpVisible);
	}

	public void reconstruct(boolean showHelp) {

		if (interactionElmt == null) {
			return;
		}

		// nom du composant en fonction du type de composant
		if (labelName instanceof HtmlLabel) {
			((JLabel) labelName).setText(interactionElmt.getLabel());
		}

		else if (labelName instanceof AbstractButton) {
			((AbstractButton) labelName).setText(interactionElmt.getLabel());
		}

		else {
			throw new IllegalStateException(
					"Cannot change text of label component class: "
							+ labelName.getClass());
		}

		labelName.revalidate();
		labelName.repaint();

		// aide
		helpVisible = showHelp;

		// enlever tout les composants en bas du nom
		remove(helpArea);
		if (bottomComponent != null) {
			remove(bottomComponent);
		}

		// activation bouton d'aide si disponible
		if (interactionElmt.getHelp() != null) {
			buttonHelp.setVisible(true);
			buttonHelp.setEnabled(true);
		}

		else {
			buttonHelp.setVisible(false);
			buttonHelp.setEnabled(false);
		}

		// l'aide est présente
		if (helpVisible && interactionElmt.getHelp() != null) {
			helpArea.setText(interactionElmt.getHelp());

			add(helpArea, "span, width 96%!, wrap");
			helpArea.refresh();
		}

		// composant optionnel
		if (bottomComponent != null) {
			add(bottomComponent,
					"span, width 96%!, gaptop 10px, gapleft 10px, gapbottom 20px");
		}

		// rafraichir
		refresh();

	}

	/**
	 * Peindre la couleur de fond si necessaire
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		// seulement si option ativée
		if (changeColorUnderFocus) {
			focusPainter.draw(g, this, isFocused());
		}
	}

	/**
	 * Affecter un composant graphique sous le titre de l'element
	 * 
	 * @param comp
	 */
	public void setBottomComponent(Component comp) {

		GuiUtils.throwIfNotOnEDT();

		// enlever le composant si déjà en place
		if (bottomComponent != null)
			remove(bottomComponent);

		this.bottomComponent = comp;
	}

	/**
	 * Affiche l'aide lors d'un clic
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class HelpActionListener extends MouseAdapter {
		@Override
		public void mouseReleased(MouseEvent e) {
			helpVisible = !helpVisible;
			reconstruct(helpVisible);
		}
	}

	/**
	 * Change la couleur de fond en fonction d'actions de l'utilisateur
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class BackgroundColorListener extends MouseAdapter implements
			FocusListener {

		@Override
		public void focusGained(FocusEvent e) {
			setFocused(true);
			repaint();
		}

		@Override
		public void focusLost(FocusEvent e) {
			setFocused(false);
			repaint();
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			setFocused(true);
			repaint();
		}

		@Override
		public void mouseExited(MouseEvent e) {
			setFocused(false);
			repaint();
		}
	}

	@Override
	public void expandHelp(boolean showHelp) {
		reconstruct(showHelp);
	}

	@Override
	public boolean isHelpExpanded() {
		return helpVisible;
	}

	public void addLabelListener(MouseListener ml) {
		labelName.addMouseListener(ml);
	}

	public void removeLabelListener(MouseListener ml) {
		labelName.removeMouseListener(ml);
	}

	private void setFocused(boolean val) {
		this.focused = val;
	}

	private boolean isFocused() {
		return focused;
	}

	public void changeColorUnderFocus(boolean val) {
		changeColorUnderFocus = val;
	}

	public void setInteractionElement(InteractionElement elmt) {
		this.interactionElmt = elmt;
	}

	@Override
	public void reconstruct() {
		reconstruct(helpVisible);
	}

	@Override
	public void refresh() {
		revalidate();
		repaint();
	}

}
