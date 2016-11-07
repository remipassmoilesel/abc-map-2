package abcmap.gui.comps.draw.symbols;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import abcmap.configuration.ConfigurationConstants;
import abcmap.managers.DrawManager;
import abcmap.managers.stub.MainManager;
import abcmap.utils.gui.GuiUtils;
import abcmap.utils.listeners.HasListenerHandler;
import abcmap.utils.listeners.ListenerHandler;
import net.miginfocom.swing.MigLayout;

public class SymbolSelector extends JPanel implements HasListenerHandler<ActionListener> {

	private DrawManager drawm;
	private SymbolSetView view;
	private ArrayList<String> availablesSets;
	private JComboBox<String> comboSetName;
	private JScrollPane scrollpane;

	private String selectedSetName;
	private Integer selectedSymbolCode;

	private ListenerHandler<ActionListener> listenerHandler;

	public SymbolSelector() {

		super(new MigLayout("insets 0"));

		// manager
		this.drawm = MainManager.getDrawManager();

		// gestionnaire d'evenements
		this.listenerHandler = new ListenerHandler<>();

		// combo de selection de set
		this.availablesSets = drawm.getAvailableSymbolSets();

		// combo de selection de set
		this.comboSetName = new JComboBox<String>(
				availablesSets.toArray(new String[availablesSets.size()]));
		comboSetName.addActionListener(new ComboListener());

		add(comboSetName, "wrap");

		// la vue des symboles
		this.view = new SymbolSetView();
		view.getListenerHandler().add(new ViewListener());
		view.setSetName(availablesSets.get(0));
		view.reconstructPanel();

		// la vue est dans un scrollpane
		scrollpane = new JScrollPane(view);
		scrollpane.setAutoscrolls(true);
		scrollpane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollpane.getVerticalScrollBar()
				.setUnitIncrement(ConfigurationConstants.SCROLLBAR_UNIT_INCREMENT);
		add(scrollpane, "width 210px!, height 200px!, wrap");

		// activer le premier set
		comboSetName.setSelectedIndex(0);
		view.selectFirstElement();
	}

	/**
	 * Affiche le set sélectionné dans le combobox. La vue est reconstruite à
	 * chaque sélection pour ne pas construire et stocker trop de vues.
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class ComboListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			// recuperer le nom du set à afficher
			selectedSetName = (String) comboSetName.getSelectedItem();

			// reconstruire la vue
			view.setSetName(selectedSetName);
			view.reconstructPanel();

			// activer le premier symbole
			view.selectFirstElement();

			// rafraichir le scrollpane
			scrollpane.revalidate();
			scrollpane.repaint();

		}

	}

	/**
	 * Lors de la selection d'un symbole dans le panneau, enregistrer les
	 * valeurs puis retansmettre l'evenement
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class ViewListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			// recuperer le code actif
			selectedSymbolCode = view.getSelectedCode();

			// transmettre l'evenement
			listenerHandler.fireEvent(e);

		}

	}

	public String getSelectedSetName() {
		return selectedSetName;
	}

	public Integer getSelectedSymbolCode() {
		return selectedSymbolCode;
	}

	@Override
	public ListenerHandler<ActionListener> getListenerHandler() {
		return listenerHandler;
	}

	public void updateValues(String symbolSetName, int symbolCode) {
		updateValues(symbolSetName, symbolCode, true);
	}

	public void updateValues(String symbolSetName, int symbolCode, boolean notify) {

		// nom du set
		GuiUtils.changeWithoutFire(comboSetName, symbolSetName);
		selectedSetName = symbolSetName;

		// code
		view.setSelectedCode(symbolCode, notify);
		selectedSymbolCode = symbolCode;

	}

}
