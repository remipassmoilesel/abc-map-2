package abcmap.gui.comps.textfields.search;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import abcmap.gui.GuiStyle;
import abcmap.gui.comps.CustomComponent;
import abcmap.gui.dock.comps.blockitems.HideableBlockItem;
import abcmap.gui.ie.InteractionElement;
import abcmap.utils.Utils;
import abcmap.utils.gui.GuiUtils;
import abcmap.utils.threads.ThreadManager;
import net.miginfocom.swing.MigLayout;

/**
 * Element de présentation du résultat: nom de la commande, icone, raccourci,
 * ...
 * 
 * @author remipassmoilesel
 *
 */
public class IESearchResultPanel extends JPanel {

	private static final String SIMPLE = "SIMPLE";
	private static final String SUB_BOX = "SUB_BOX";

	private InteractionElement ielement;
	private InteractivePopupDisplay popupParent;

	private String displayMode;
	private int maxWidth;
	private int interactionGuiWidth;

	public IESearchResultPanel(InteractivePopupDisplay popupParent,
			InteractionElement ie) {

		// pas d'action dans l'EDT
		GuiUtils.throwIfNotOnEDT();

		this.ielement = ie;
		this.popupParent = popupParent;

		// largeurs des composants
		this.maxWidth = CommandSearchTextField.POPUP_WIDTH_PX - 20;
		this.interactionGuiWidth = 300;

		// layout et style
		this.setBorder(BorderFactory.createLineBorder(Color.lightGray, 1));
		this.setLayout(new MigLayout("insets 5"));

		// construire le GUI par defaut
		displayAsSimpleSearchResult();

	}

	/**
	 * Afficher l'element de manière simple, comme résultat de recherche
	 * classique.
	 */
	private void displayAsSimpleSearchResult() {

		// pas d'action dans l'EDT
		GuiUtils.throwIfNotOnEDT();

		displayMode = SIMPLE;
		removeAll();

		// designation et icone de la commande
		CustomComponent comp = new CustomComponent();
		comp.addActionListener(new CustomActionListener());

		// icone de l'element
		if (ielement.getMenuIcon() != null) {
			comp.add(new JLabel(ielement.getMenuIcon()), "width 25px!");
		}

		// om de l'element
		GuiUtils.addLabel(ielement.getLabel(), comp, null,
				GuiStyle.SEARCH_RESULT_LABEL);

		// ajout du composant icone/texte/action
		this.add(comp, "width " + maxWidth + "px!, wrap");

		// raccourci clavier
		if (ielement.getAccelerator() != null) {

			String str = "Raccourci: "
					+ Utils.keystrokeToString(ielement.getAccelerator());

			GuiUtils.addLabel(str, this, "width max, wrap",
					GuiStyle.SEARCH_RESULT_TEXT);

		}

		// aide de l'element
		if (ielement.getHelp() != null) {
			GuiUtils.addLabel("Aide: " + ielement.getHelp(), this,
					"gapx 10, span, width max, wrap",
					GuiStyle.SEARCH_RESULT_TEXT);

		}

		// message si commande non utilsable en mode recherche
		if (ielement.getNoSearchMessage() != null) {
			GuiUtils.addLabel("Attention: " + ielement.getNoSearchMessage(),
					this, "gapx 10, span, width max, wrap",
					GuiStyle.SEARCH_RESULT_NO_SEARCH);
		}

		// ajuster la hauteur de la popup
		popupParent.adjustHeight();

		revalidate();
		repaint();

	}

	/**
	 * Afficher le résultat de manière plus complexe, avec son IHM de dock.
	 */
	private void displayAsComplexInteractionGui() {

		// pas d'action dans l'EDT
		GuiUtils.throwIfNotOnEDT();

		displayMode = SUB_BOX;
		removeAll();

		// bouton de retour en arrière
		JButton jbt = new JButton("Fermer");
		jbt.addActionListener(new CustomActionListener());
		add(jbt, "wrap");

		// recuperer le gui
		Component gui = ielement.getBlockGUI();

		// si le gui est retractable, étendre
		if (gui instanceof HideableBlockItem) {
			((HideableBlockItem) gui).refresh(false, true);
		}

		// ajout du GUI
		add(gui, "width " + interactionGuiWidth);

		// ajuster la hauteur de la popup
		popupParent.adjustHeight();

		revalidate();
		repaint();

	}

	private class CustomActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {

			// L'element est affichable simplement, lors d'un clic executer
			// son action
			if (ielement.isDisplayableSimpleInSearch()) {
				ThreadManager.runLater(ielement);
			}

			// l'element est complexe
			else {

				// étendre dans une sous boite
				if (SIMPLE.equals(displayMode)) {
					displayAsComplexInteractionGui();
				}
				// ou afficher simplement
				else if (SUB_BOX.equals(displayMode)) {
					displayAsSimpleSearchResult();
				}
			}

		}
	}
}
