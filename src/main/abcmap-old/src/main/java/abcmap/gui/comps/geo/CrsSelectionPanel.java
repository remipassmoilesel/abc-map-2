package abcmap.gui.comps.geo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import abcmap.managers.stub.MainManager;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import abcmap.geo.GeoSystemsContainer;
import abcmap.managers.MapManager;
import abcmap.utils.gui.GuiUtils;
import abcmap.utils.listeners.HasListenerHandler;
import abcmap.utils.listeners.ListenerHandler;
import net.miginfocom.swing.MigLayout;

/**
 * Panneau de sélection de système de coordonnées.
 * 
 * @author remipassmoilesel
 *
 */
public class CrsSelectionPanel extends JPanel implements
		HasListenerHandler<ActionListener> {

	private static final String OTHER_CRS = "OTHER_CRS";
	private JTextField txtSearch;
	private JButton btnSearch;
	private JTextField txtResults;
	private CoordinateReferenceSystem selectedSystem;
	private ListenerHandler<ActionListener> listenerHandler;
	private JRadioButton rdOtherCRS;
	private List<String> predefinedCodes;
	private List<String> predefinedNames;
	private ArrayList<JRadioButton> predefinedRadioBtn;
	private ArrayList<CoordinateReferenceSystem> predefinedSystems;
	private MapManager mapm;

	public CrsSelectionPanel() {
		super(new MigLayout("insets 0"));

		mapm = MainManager.getMapManager();

		// gestion des evenements
		this.listenerHandler = new ListenerHandler<>();

		// contraintes par defaut
		String wrap15 = "wrap 15px, ";
		String gapLeft = "gap 15px, ";

		// codes de CRS predefini simples
		predefinedCodes = Arrays.asList(GeoSystemsContainer.WEB_MERCATOR,
				GeoSystemsContainer.WGS_84);
		predefinedNames = Arrays.asList("Web Mercator (applications web)",
				"WGS 84 (GPS, appareils GPS)");

		predefinedSystems = new ArrayList<CoordinateReferenceSystem>();
		predefinedRadioBtn = new ArrayList<JRadioButton>();

		// choix de crs simplifié
		GuiUtils.addLabel("Choisir un système de coordonnées prédéfini: ",
				this, "span, wrap");

		// Creer les boutons et CRS prédéfinis
		ButtonGroup btnGroup = new ButtonGroup();
		for (int i = 0; i < predefinedCodes.size(); i++) {

			// recuperer code et nom du systeme
			String code = predefinedCodes.get(i);
			String name = predefinedNames.get(i);

			// creer un systeme et garder une reference vers le systeme
			CoordinateReferenceSystem sys = mapm.getCRS(code);
			if (sys == null) {
				throw new IllegalArgumentException("Invalid predefined code: "
						+ code);
			}
			predefinedSystems.add(sys);

			// creer un bouton radio
			JRadioButton radio = new JRadioButton(name);
			radio.setActionCommand(code);
			radio.addActionListener(new SimpleCrsListener());

			// enregistrer un bouton radio
			add(radio, gapLeft + "span, wrap");
			btnGroup.add(radio);
			predefinedRadioBtn.add(radio);

		}

		// bouton radio autre
		rdOtherCRS = new JRadioButton("Autre");
		rdOtherCRS.setActionCommand(OTHER_CRS);
		add(rdOtherCRS, gapLeft + "span, " + gapLeft + wrap15);
		btnGroup.add(rdOtherCRS);

		// champs de recherche de code
		GuiUtils.addLabel("Ou rechercher un système par code EPSG: ", this,
				"span, wrap");
		txtSearch = new JTextField();
		add(txtSearch, gapLeft + "width 80px");

		// bouton de recherche
		btnSearch = new JButton("Rechercher");
		btnSearch.addActionListener(new SearchCrsListener());
		add(btnSearch, "wrap");

		// affichage des resultats
		txtResults = new JTextField();
		txtResults.setEditable(false);
		add(txtResults, "width 200px, span, " + gapLeft);

	}

	/**
	 * Selectionne un systeme geodesique simple
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class SimpleCrsListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			// restransmettre tous les evenements de boutons significatifs
			String epsgCode = e.getActionCommand();
			if (epsgCode.equals(OTHER_CRS) == false) {
				selectedSystem = predefinedSystems.get(predefinedCodes
						.indexOf(epsgCode));
				fireEvent();
			}
		}

	}

	/**
	 * Recherche un systeme geodesique
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class SearchCrsListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			// recuperer la valeur du champs texte
			String code = txtSearch.getText().trim();

			// tenter de creer un crs
			selectedSystem = mapm.getCRS(code);

			// erreur lors de la recherche du code
			if (selectedSystem == null) {
				txtResults.setText("Code invalide");
			}

			else {

				// afficher le résultat
				displayNonPredefinedSystem(selectedSystem.getName().getCode());

				// depart d'un evenement
				fireEvent();
			}
		}

	}

	/**
	 * Affiche le nom du code dans la boite de résultat et sélectionne l'option
	 * "Autre". Ne déclenche pas d'evenements.
	 * 
	 * @param label
	 */
	private void displayNonPredefinedSystem(String crsName) {

		GuiUtils.throwIfNotOnEDT();

		// afficher le resultat
		txtResults.setText(crsName);

		// retour au débutdu champs
		txtResults.setCaretPosition(0);

		// deselectionner les boutons radios
		rdOtherCRS.setSelected(true);

	}

	private void fireEvent() {
		String code = selectedSystem != null ? selectedSystem.getName()
				.getCode() : "null";
		listenerHandler.fireEvent(new ActionEvent(this,
				ActionEvent.ACTION_PERFORMED, code));
	}

	public CoordinateReferenceSystem getSelectedSystem() {
		return selectedSystem;
	}

	public void setSelectedSystem(CoordinateReferenceSystem system) {

		GuiUtils.throwIfNotOnEDT();

		// modifier les valeurs
		updateSystemWithoutFire(system);

		// notifier les observateurs
		fireEvent();

	}

	public void updateSystemWithoutFire(CoordinateReferenceSystem system) {

		GuiUtils.throwIfNotOnEDT();

		String code = MapManager.getEpsgCode(system);

		if (code == null) {

		}

		// le code est prédéfini, activer le bouton puis quitter
		if (predefinedCodes.contains(code)) {
			for (JRadioButton btn : predefinedRadioBtn) {
				if (btn.getActionCommand().equals(code)) {
					GuiUtils.setSelected(btn, true);
					return;
				}
			}
		}

		// le code n'est pas prédéfini,
		displayNonPredefinedSystem(system.getName().getCode());

	}

	@Override
	public ListenerHandler<ActionListener> getListenerHandler() {
		return listenerHandler;
	}

}
