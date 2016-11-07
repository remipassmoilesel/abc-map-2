package abcmap.gui.dock.comps;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

import abcmap.gui.dock.comps.Dock.DockOrientation;
import abcmap.utils.Refreshable;
import net.miginfocom.swing.MigLayout;

/**
 * Panneau principal du menu de dock. Contient la barre de navigation et
 * l'espace central.
 * 
 * @author remipassmoilesel
 *
 */
public class DockWidgetSpacePanel extends JPanel implements Refreshable {

	/** Historique des composants affichés */
	private ArrayList<Component> componentHistory;

	/** L'index en cours d'affichage dans l'historique des composants affichés */
	private int index;

	private JPanel header;
	private DockOrientation orientation;
	private ArrayList<DockNavButton> buttons;

	public DockWidgetSpacePanel(DockOrientation orientation) {

		this.orientation = orientation;

		this.componentHistory = new ArrayList<Component>();
		this.index = -1;

		// caracteristiques d'affichage
		setLayout(new MigLayout("insets 0, gap 0"));
		setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

		// en tete avec bouton fermeture et precedent
		String migAlign = DockOrientation.WEST.equals(orientation) ? "right"
				: "left";
		header = new JPanel();
		header.setLayout(new MigLayout("align " + migAlign + ", gapx 20"));

		String[] buttonsWEST = new String[] { DockNavButton.PREVIOUS,
				DockNavButton.NEXT, DockNavButton.EXPAND_HELP,
				DockNavButton.CLOSE, };
		String[] buttonsEST = new String[] { DockNavButton.CLOSE,
				DockNavButton.EXPAND_HELP, DockNavButton.PREVIOUS,
				DockNavButton.NEXT };
		String[] btts;
		buttons = new ArrayList<DockNavButton>();

		// inverser les boutons selon orientation
		btts = DockOrientation.EST.equals(orientation) ? buttonsEST
				: buttonsWEST;

		for (String s : btts) {
			DockNavButton b = new DockNavButton(s);
			header.add(b);
			buttons.add(b);
		}

		add(header, "span, grow, push, wrap 5px");

		// bordures des elements
		Border lineBorder = BorderFactory.createLineBorder(new Color(210, 210,
				210));
		header.setBorder(lineBorder);
		setBorder(lineBorder);

	}

	/**
	 * Afficher un composant et l'ajouter à l'historique.
	 * 
	 * @param comp
	 */
	public void displayNew(Component comp) {

		// enlever tous les panneaux après index
		while (componentHistory.size() - 1 > index) {
			componentHistory.remove(componentHistory.size() - 1);
		}

		// ajouter a l'historique si le panneau est different du dernier affiché
		if (componentHistory.size() == 0
				|| componentHistory.get(componentHistory.size() - 1).equals(
						comp) == false) {
			componentHistory.add(comp);
			index++;
		}

		addWidgetPanel(comp);
	}

	@Override
	public void refresh() {
		this.revalidate();
		this.repaint();
	}

	@Override
	public void reconstruct() {
		refresh();
	}

	/**
	 * Afficher le panneau suivant sur l'espace central.
	 */
	public void displayNext() {
		index++;
		Component c = null;
		try {
			c = componentHistory.get(index);
		} catch (IndexOutOfBoundsException e) {
			index = componentHistory.size() - 1;
		}

		if (c != null) {
			addWidgetPanel(c);
			refresh();
		}

	}

	/**
	 * Afficher le panneau précédent sur l'espace central.
	 */
	public void displayPrevious() {

		index--;
		Component c = null;
		try {
			c = componentHistory.get(index);
		} catch (IndexOutOfBoundsException e) {
			index = 0;
		}

		if (c != null) {
			addWidgetPanel(c);
			refresh();
		}
	}

	private void checkButtonsValidity() {
		for (DockNavButton b : buttons) {
			b.checkValidity();
		}
	}

	/**
	 * Ajouter un composant dans un JScrollpane au centre de ce composant.
	 */
	private void addWidgetPanel(Component comp) {

		// tout enlever sauf la barre de navigation
		for (Component c : getComponents()) {
			if (c.equals(header) == false) {
				remove(c);
			}
		}

		// créer un scrollpane avec le composant
		JScrollPane sp = new JScrollPane(comp);
		sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		sp.getVerticalScrollBar().setUnitIncrement(
				Dock.VERTICAL_SCROLLBAR_UNIT_INCREMENT);
		sp.setBorder(null);

		// ajout
		add(sp, "grow, height max, wrap");

		// mis à jour des boutons de navigation
		checkButtonsValidity();

	}

	/**
	 * Retourne l'index du panneau courant affiché
	 * 
	 * @return
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Retourne la taille totale de l'historique d'affichage
	 * 
	 * @return
	 */
	public int getHistorySize() {
		return componentHistory.size();
	}

	@Deprecated
	@Override
	public void remove(Component comp) {
		super.remove(comp);
	}

	@Deprecated
	@Override
	public void remove(int index) {
		super.remove(index);
	}

	@Deprecated
	@Override
	public Component add(Component comp) {
		return super.add(comp);
	}

	@Deprecated
	@Override
	public Component add(String name, Component comp) {
		return super.add(name, comp);
	}

	@Deprecated
	@Override
	public Component add(Component comp, int index) {
		return super.add(comp, index);
	}

	@Deprecated
	@Override
	public void add(Component comp, Object constraints) {
		super.add(comp, constraints);
	}

	@Deprecated
	@Override
	public void add(Component comp, Object constraints, int index) {
		super.add(comp, constraints, index);
	}

}
