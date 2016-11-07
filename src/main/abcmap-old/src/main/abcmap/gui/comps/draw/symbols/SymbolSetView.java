package abcmap.gui.comps.draw.symbols;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import abcmap.exceptions.DrawManagerException;
import abcmap.managers.DrawManager;
import abcmap.managers.stub.MainManager;
import abcmap.utils.gui.GuiUtils;
import abcmap.utils.listeners.HasListenerHandler;
import abcmap.utils.listeners.ListenerHandler;
import net.miginfocom.swing.MigLayout;

/**
 * Affichage d'un set de symboles
 * 
 * @author remipassmoilesel
 *
 */
public class SymbolSetView extends JPanel implements
		HasListenerHandler<ActionListener> {

	private DrawManager drawm;

	private Font symbolSetfont;
	private String setName;

	private ArrayList<Integer> availablesSymbolCodes;
	private ArrayList<JToggleButton> buttons;

	private Integer selectedCode;

	private Integer symbolDisplaySize;

	private int cols;

	private ListenerHandler<ActionListener> listenerHandler;

	public SymbolSetView() {
		super();

		drawm = MainManager.getDrawManager();

		// le code sélectionné
		this.selectedCode = null;

		// la font affichée
		this.setName = null;

		// la font
		this.symbolSetfont = null;

		// les symboles disponibles
		this.availablesSymbolCodes = null;

		// taille d'affichage des symboles
		this.symbolDisplaySize = 25;

		// nombre de colonnes
		this.cols = 3;

		// transmettre les clics
		listenerHandler = new ListenerHandler<>();

		reconstructPanel();

	}

	public SymbolSetView(String setname) {
		super();
		this.setName = setname;

		reconstructPanel();
	}

	public void reconstructPanel() {

		removeAll();

		// obtenir la font
		boolean exceptionHappend = false;
		if (setName != null) {
			try {
				symbolSetfont = drawm.getSymbolSetFont(setName);
			} catch (DrawManagerException e) {
				exceptionHappend = true;
			}
		}

		// pas de set, panneau vide
		if (setName == null || exceptionHappend == true) {
			GuiUtils.addLabel("<i>Pas de symbole à afficher</i>", this);
			return;
		}

		// ajuster la font pour affichage
		Map<TextAttribute, Object> map = new Hashtable<TextAttribute, Object>();
		map.put(TextAttribute.SIZE, symbolDisplaySize);
		symbolSetfont = symbolSetfont.deriveFont(map);

		// obtenir les codes disponibles
		availablesSymbolCodes = drawm.getAvailableSymbolCodesFor(setName);

		// adaper le layout à l'affichage
		this.setLayout(new MigLayout("insets 2, gap 2, wrap " + cols));

		// groupe de boutons
		ButtonGroup bg = new ButtonGroup();
		buttons = new ArrayList<JToggleButton>();

		// se mettre à l'ecoute des selection de codes
		SelectionActionDispatcher selectionActionDispatcher = new SelectionActionDispatcher();

		// itérer les symboles disponibles
		for (int i : availablesSymbolCodes) {

			// creer le bouton
			JToggleButton jt = new JToggleButton();
			jt.setFont(symbolSetfont);
			jt.setText(Character.toString((char) i));

			// selectionner le premier (av actionlistener)
			if (availablesSymbolCodes.indexOf(i) == 0)
				jt.setSelected(true);

			// conserver le numéro du symbole
			jt.setActionCommand(String.valueOf(i));

			// ecouter les selection
			jt.addActionListener(selectionActionDispatcher);

			// ajouter au groupe et à la liste
			bg.add(jt);
			buttons.add(jt);

			// ajouter au panneau
			this.add(jt, "width 60px!, height 40px!");

		}

		this.revalidate();
		this.repaint();

	}

	public void setSetName(String setName) {
		this.setName = setName;
	}

	private class SelectionActionDispatcher implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			JToggleButton src = ((JToggleButton) e.getSource());

			// transmettre l'evenement aux écouteurs
			if (src.isSelected()) {
				selectedCode = Integer.valueOf(src.getActionCommand());
				listenerHandler.fireEvent(e);
			}

		}

	}

	public int getSelectedCode() {
		return selectedCode;
	}

	public void setSelectedCode(int symbolCode) {
		setSelectedCode(symbolCode, false);
	}

	public void setSelectedCode(int symbolCode, boolean notify) {

		// verifier que le code existe
		if (availablesSymbolCodes.indexOf(symbolCode) == -1) {
			throw new IllegalArgumentException("Unknown code: " + symbolCode);
		}

		// conserver le code
		selectedCode = symbolCode;

		// rechercher le bouton correspondand et l'activer
		String code = String.valueOf(symbolCode);
		for (JToggleButton jbtn : buttons) {
			if (code.equals(jbtn.getActionCommand())) {

				// selectionner le bouton
				GuiUtils.setSelected(jbtn, true);

				// demander à voir le bouton
				scrollRectToVisible(jbtn.getBounds());

				// arret
				break;
			}
		}

		if (notify)
			listenerHandler.fireEvent(new ActionEvent(this,
					ActionEvent.ACTION_PERFORMED, ""));

	}

	public void selectFirstElement() {
		buttons.get(0).doClick();
	}

	@Override
	public ListenerHandler<ActionListener> getListenerHandler() {
		return listenerHandler;
	}

}
