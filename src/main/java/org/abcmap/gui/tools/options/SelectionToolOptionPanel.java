package org.abcmap.gui.tools.options;

public class SelectionToolOptionPanel extends ToolOptionPanel {

	/*
    private final ArrayList<String> shapeNames = new ArrayList<String>();
	private final ArrayList<Class> shapeClasses = new ArrayList<Class>();

	private HtmlCheckbox chkIncludeTiles;
	private FormUpdater formUpdater;
	private JComboBox cbFilter;

	private Class selectedClassFilter;
	private boolean excludeTiles;

	/**
	 * Create the panel.

	public SelectionToolOptionPanel() {

		GuiUtils.throwIfNotOnEDT();

		// filtre de selection
		selectedClassFilter = null;

		// lister les formes possibles
		shapeNames.add("Toutes les formes");
		shapeClasses.add(null);

		List<String> names = Arrays.asList(drawm.getAvailablesShapeNames());
		List<Class> shapes = Arrays.asList(drawm.getAvailablesShapes());

		for (Class c : shapes) {
			if (c.equals(Tile.class))
				continue;
			shapeClasses.add(c);
			shapeNames.add(names.get(shapes.indexOf(c)));
		}

		// checkbox inclure les tuiles
		HtmlCheckbox chkIncludeTiles = new HtmlCheckbox(
				"Exclure les tuiles de la sélection");
		chkIncludeTiles.addActionListener(new ExcludeTilesAL());
		add(chkIncludeTiles, largeWrap);

		// filtre de selection par defaut
		selectedClassFilter = shapeClasses.get(0);

		// combo filtre de sélection
		cbFilter = new JComboBox(shapeNames.toArray());
		cbFilter.setSelectedIndex(0);
		cbFilter.addActionListener(new ComboListener());

		GuiUtils.addLabel("Filtre de sélection: ", this, "wrap");
		add(cbFilter, "wrap 15px");

		// bouton de/selectionner tout
		JButton btnSelectAll = new JButton("Sélectionner tout");
		btnSelectAll.addActionListener(new SelectionPerformer(true));
		add(btnSelectAll, "width 150px, wrap");

		JButton btnUnselectAll = new JButton("Déselectionner tout");
		btnUnselectAll.addActionListener(new SelectionPerformer(false));
		add(btnUnselectAll, "width 150px, wrap");

		// mettre à l'ecoute du gestionnaire de dessin
		this.formUpdater = new FormUpdater();
		observer.setDefaultUpdatableObject(formUpdater);

	}

	/**
	 * Inclut ou non les tuiles dans la sélection en fonction de la case à
	 * cocher correspondante.
	 * 
	 * @author remipassmoilesel
	 *

	public class ExcludeTilesAL implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			// pas d'action hors de l'EDT
			GuiUtils.throwIfNotOnEDT();

			// vérifier l'outil
			if (drawm.getCurrentTool() instanceof SelectionTool == false) {
				return;
			}

			// recuperer la valeur de la CheckBox
			excludeTiles = ((HtmlCheckbox) e.getSource()).isSelected();

			// comparer avec l'etat de l'outil
			SelectionTool currentTool = (SelectionTool) drawm.getCurrentTool();

			// changer la valeur si différente
			if (currentTool.isExcludingTiles() != excludeTiles) {
				currentTool.excludeTiles(excludeTiles);
				drawm.notifyToolModeChanged();
			}

		}

	}

	/**
	 * Appliquer un filtre de sélection en fonction de la saisie de
	 * l'utilisateur
	 * 
	 * @author remipassmoilesel
	 *

	private class ComboListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {

			// pas d'action hors de l'EDT
			GuiUtils.throwIfNotOnEDT();

			if (drawm.getCurrentTool() instanceof SelectionTool == false)
				return;

			// recuperer le composant source et l'outils
			JComboBox src = (JComboBox) arg0.getSource();
			SelectionTool tool = (SelectionTool) drawm.getCurrentTool();

			// recuperer l'index de la forme
			int i = shapeNames.indexOf(src.getSelectedItem());
			if (i < 0)
				i = 0;

			selectedClassFilter = shapeClasses.get(i);

			if (Utils.safeEquals(selectedClassFilter, tool.getShapeFilter())) {
				tool.setShapeFilter(selectedClassFilter);
				drawm.notifyToolModeChanged();
			}

		}

	}

	private class SelectionPerformer extends LayerSequentialPerformer {

		private boolean selectionValue;

		public SelectionPerformer(boolean val) {
			this.selectionValue = val;
		}

		@Override
		protected void beforeBeginUpdate() {
			// filtre de formes
			addShapeFilter(selectedClassFilter);
		}

		@Override
		protected void updateLayerElement(LayerElement elmt) {

			if (excludeTiles) {
				if (elmt instanceof Tile == false) {
					elmt.setSelected(selectionValue);
				}
			}

			else {
				elmt.setSelected(selectionValue);
			}

		}

		@Override
		protected void updatesAreDone() {
			// notifications et fraichissement
			mapm.refreshMapComponent();
			projectm.fireSelectionChanged();
		}
	}

	private class FormUpdater implements Runnable,
			UpdatableByNotificationManager {

		@Override
		public void run() {

			// pas d'action hors de l'EDT
			GuiUtils.throwIfNotOnEDT();

			// verifier l'outil courant
			if (drawm.getCurrentTool() instanceof SelectionTool == false)
				return;

			// recuperer l'outil courant
			SelectionTool st = (SelectionTool) drawm.getCurrentTool();

			// mettre à jour la checkbox d'inclusion des tuiles
			if (chkIncludeTiles.isSelected() != st.isExcludingTiles()) {
				GuiUtils.setSelected(chkIncludeTiles,
						st.isExcludingTiles());
			}

			// recuperer le nom du filtre
			Class actualFilter = st.getShapeFilter();
			String filterName;
			if (actualFilter == null)
				filterName = shapeNames.get(0);
			else {
				int i = shapeClasses.indexOf(actualFilter);
				i = (i < 0) ? 0 : i;
				filterName = shapeNames.get(i);
			}

			// changement de la checkbox
			if (cbFilter.getSelectedItem().equals(filterName) == false) {
				GuiUtils.changeWithoutFire(cbFilter, filterName);
			}

		}

		/**
		 * Reception d'une notification par l'observateur

		@Override
		public void notificationReceived(Notification arg) {
			if (arg instanceof DrawManagerEvent)
				SwingUtilities.invokeLater(this);
		}
	}

	@Override
	public NotificationManager getNotificationManager() {
		return observer;
	}

	*/
}
