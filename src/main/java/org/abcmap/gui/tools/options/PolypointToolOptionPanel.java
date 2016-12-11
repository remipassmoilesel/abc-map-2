package org.abcmap.gui.tools.options;

public class PolypointToolOptionPanel extends ToolOptionPanel {

	/*
    private static final Integer[] PREDEFINED_SIZES = new Integer[] { 15, 30,
			40, 50 };

	/**
	 * La classe concernée par le panneau d'options

	private Class shapeClass;

	private GeoInfoForm geoInfForm;
	private HtmlCheckbox chkBeginArrow;
	private HtmlCheckbox chkEndArrow;
	private JComboBox cbArrowForce;

	private ButtonGroup toolModeGroup;

	public PolypointToolOptionPanel(Class mode) {

		// pas d'action hors de l'EDT
		GuiUtils.throwIfNotOnEDT();

		// le type d'objet auxquel est destiné le panneau
		this.shapeClass = mode;

		String btnWidth = "width 150px,";

		// groupe de mode de fonctionnement
		toolModeGroup = new ButtonGroup();

		// boutons de selection de mode de fonctionnement
		GuiUtils.addLabel("AnalyseMode de fonctionnement: ", this, "wrap");

		ToolModeAL toolModeAL = new ToolModeAL();

		JToggleButton tbCreate = new JToggleButton("Création de forme");
		tbCreate.setActionCommand(PolypointShapeTool.POLYPOINT_NORMAL);
		toolModeGroup.add(tbCreate);
		tbCreate.addActionListener(toolModeAL);
		add(tbCreate, gapLeft + btnWidth + "wrap");

		JToggleButton tbAdd = new JToggleButton("Ajout de noeuds");
		tbAdd.setActionCommand(PolypointShapeTool.POLYPOINT_ADD_NODES);
		toolModeGroup.add(tbAdd);
		tbAdd.addActionListener(toolModeAL);
		add(tbAdd, gapLeft + btnWidth + "wrap");

		JToggleButton tbRemove = new JToggleButton("Suppression de noeuds");
		tbRemove.setActionCommand(PolypointShapeTool.POLYPOINT_REMOVE_NODES);
		toolModeGroup.add(tbRemove);
		tbRemove.addActionListener(toolModeAL);
		add(tbRemove, gapLeft + btnWidth + largeWrap);

		// panneau d'ajout de flèches en debut et fin de forme
		if (Polyline.class.equals(mode)) {

			// ajouter des fleches
			GuiUtils.addLabel("Flêche: ", this, "wrap");

			chkBeginArrow = new HtmlCheckbox("Initiale");
			chkBeginArrow.addActionListener(new Performer(
					DrawConstants.MODIFY_BEGIN_ARROW));
			add(chkBeginArrow, gapLeft + "wrap");

			chkEndArrow = new HtmlCheckbox("Terminale");
			chkEndArrow.addActionListener(new Performer(
					DrawConstants.MODIFY_END_ARROW));
			add(chkEndArrow, gapLeft + "wrap 15px");

			// taille de la fl^eche
			GuiUtils.addLabel("Taille de la flêche: ", this, "wrap");

			cbArrowForce = new JComboBox(PREDEFINED_SIZES);
			cbArrowForce.setEditable(true);
			cbArrowForce.addActionListener(new Performer(
					DrawConstants.MODIFY_ARROW_FORCE));
			add(cbArrowForce, gapLeft + largeWrap);

		}

		// avertissement de georeferencement
		add(new AttentionPanel(), "span, " + largeWrap);

		// informations géo
		GuiUtils.addLabel("Informations géographiques: ", this, "wrap");

		geoInfForm = new GeoInfoForm();
		geoInfForm.getListenerHandler().add(new Performer(null));
		add(geoInfForm);

		// mettre à jour les formuaires en fonction du projet et du gestionnaire
		// de dessin
		FormUpdater formUpdater = new FormUpdater();
		formUpdater.addEventFilter(DrawManagerEvent.class);
		formUpdater.addEventFilter(ProjectEvent.class);
		formUpdater.addDrawingToolFilter(PolypointShapeTool.class);

		// ecouter le projet et le gest de dessin
		observer.setDefaultUpdatableObject(formUpdater);
		projectm.getNotificationManager().addObserver(this);
		drawm.getNotificationManager().addObserver(this);

		// première mise à jour
		formUpdater.run();

	}

	/**
	 * Changer le mode de l'outil en cours
	 * 
	 * @author remipassmoilesel
	 *

	private class ToolModeAL implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			JToggleButton src = ((JToggleButton) e.getSource());

			// ne prendre en compte que les evenement de selection
			if (src.isSelected() == false)
				return;

			// recuperer le mode
			String selectedToolMode = src.getActionCommand();

			// appliquer si necessaire
			if (drawm.getCurrentTool() instanceof PolypointShapeTool) {
				if (Utils.safeEquals(drawm.getToolMode(), selectedToolMode) == false) {
					drawm.setToolMode(selectedToolMode);
				}
			}

		}

	}

	private class FormUpdater extends abcmap.utils.gui.FormUpdater {

		@Override
		protected void updateFields() {

			super.updateFields();

			// mettre à jour le mode de l'outil
			updateToggleButtons();

			// recuperer la premiere forme selectionnée
			PolypointShape shp = (PolypointShape) getFirstSelectedElement(shapeClass);

			// pas de selection, mise à jour à partir de la forme temoin
			if (shp == null) {
				if (Polyline.class.equals(shapeClass)) {
					shp = drawm.getWitnessPolygon();
				}

				else if (Polygon.class.equals(shapeClass)) {
					shp = drawm.getWitnessPolygon();
				}

				if (shp == null)
					return;

			}

			if (Polyline.class.equals(shapeClass)) {
				// mettre à jour les fleches
				updateComponentWithoutFire(chkBeginArrow,
						shp.isBeginWithArrow());
				updateComponentWithoutFire(chkEndArrow, shp.isEndWithArrow());

				// mettre à jour la taille des fleches
				updateComponentWithoutFire(cbArrowForce, shp.getArrowForce());
			}

			// mettre a jour les informations geo
			geoInfForm.changeInfomodeWithoutFire(shp.getGeoInfoMode());

			// mettre à jour la taille du texte
			geoInfForm.changeTextSizeWithoutFire(shp.getGeoTextSize());

		}

		/**
		 * Mettre à jour les boutons du panneau en fonction du mode du manager

		public void updateToggleButtons() {

			// recuperer le mode actuel d'outil
			String toolMode = drawm.getToolMode();

			// enumerer les boutons
			Enumeration<AbstractButton> buttons = toolModeGroup.getElements();
			while (buttons.hasMoreElements()) {

				// recuperer le bouton
				AbstractButton btn = buttons.nextElement();

				// si le nom est le même, selection du bouton et arrêt
				if (Utils.safeEquals(toolMode, btn.getActionCommand())) {

					// selectionner seulement au besoin
					if (btn.isSelected() == false) {
						updateComponentWithoutFire(btn, true);
					}

					// arret
					break;
				}
			}
		}

	}

	private class Performer extends ShapeUpdater {

		private boolean findDynamicallyMode = false;

		public Performer(DrawConstants mode) {

			if (mode == null) {
				findDynamicallyMode = true;
			}

			else {
				setMode(mode);
			}

			addShapeFilter(shapeClass);
		}

		@Override
		protected boolean beforeActionPerformed(ActionEvent e) {

			// determiner le mode dynamiquement
			if (findDynamicallyMode) {

				GeoConstants ac = null;
				try {
					ac = GeoConstants.valueOf(e.getActionCommand());
				} catch (Exception e2) {
					Log.debug(e2);
					return false;
				}

				if (GeoConstants.DISPLAY_GEO_TEXT_SIZE.equals(ac)) {
					setMode(DrawConstants.MODIFY_TEXT_SIZE);
				}

				else {
					setMode(DrawConstants.MODIFY_GEOTEXTMODE);
				}

				return true;

			}

			else
				return true;
		}

		@Override
		protected void beforeBeginUpdate() {
			super.beforeBeginUpdate();

			// rassemblement des valeurs a affecter
			ShapeProperties pp = new ShapeProperties();

			if (Polyline.class.equals(shapeClass)) {
				pp.beginWithArrow = chkBeginArrow.isSelected();
				pp.endWithArrow = chkEndArrow.isSelected();
				pp.arrowForce = getIntegerArrowForce();
			}

			pp.geoInfoMode = geoInfForm.getSelectedInfoMode().toString();
			pp.geoInfoSize = geoInfForm.getSelectedTextsize();

			setProperties(pp);

			// mise à jour de la forme temoin du manager
			drawm.updateWitness(shapeClass, mode, pp);

		}

	}

	private Integer getIntegerArrowForce() {
		try {
			return Integer.valueOf(cbArrowForce.getSelectedItem().toString());
		} catch (Exception e) {
			return null;
		}
	}
	*/

}
