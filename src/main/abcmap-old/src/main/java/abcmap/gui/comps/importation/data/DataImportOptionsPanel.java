package abcmap.gui.comps.importation.data;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import abcmap.utils.Refreshable;
import net.miginfocom.swing.MigLayout;

public class DataImportOptionsPanel extends JPanel implements Refreshable {

	private JComboBox<String> cbMode;
	private JPanel bottomComponent;
	private Object wrap15;
	private String gapLeft;
	private JPanel[] optionPanels;

	public DataImportOptionsPanel() {
		super(new MigLayout("insets 0"));

		// contraites par defaut
		wrap15 = "wrap 15, ";
		gapLeft = "gapleft 15, ";

		// choix de l'action
		String[] modes = new String[] { "Rendre les données tel quel", "Créer des formes",
				"Créer des étiquettes de texte", "Créer une ligne ou un polygone" };

		optionPanels = new JPanel[] { new DataImportRenderAsIs(this),
				new DataImportCreateShapes(this), new DataImportCreateLabels(this),
				new DataImportCreateLines(this), };

		cbMode = new JComboBox<>(modes);
		cbMode.setEditable(false);
		cbMode.addActionListener(new ComboChangeListener());

		add(cbMode, wrap15);

		// composant modifié dynamiquement par la liste
		bottomComponent = new JPanel(new MigLayout("insets 0"));
		add(bottomComponent);

		// permiere mise à jour
		cbMode.setSelectedIndex(0);

	}

	private class ComboChangeListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// récuperer l'index sélectionné
			int index = cbMode.getSelectedIndex();

			// vérifier l'index
			if (index < 0 || index >= optionPanels.length) {
				index = 0;
			}

			// afficher le panneau correspondant
			setBottomComponent(optionPanels[index]);

			// rafraichir
			refresh();
		}

	}

	public void setBottomComponent(JPanel comp) {

		// enlever le précédent panneau
		remove(bottomComponent);

		// conserver la reference
		bottomComponent = comp;

		// ajouter le panneau
		add(bottomComponent);
	}

	@Override
	public void refresh() {

		bottomComponent.revalidate();
		bottomComponent.repaint();

		revalidate();
		repaint();

	}

	@Deprecated
	@Override
	public void reconstruct() {
		refresh();
	}

}
