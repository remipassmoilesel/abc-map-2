package org.abcmap.gui.tools.options;

public class LegendToolOptionPanel extends ToolOptionPanel {

	/*
    public LegendToolOptionPanel() {

		GuiUtils.throwIfNotOnEDT();

		// activation du cadre
		HtmlCheckbox chkFrame = new HtmlCheckbox("Dessiner un cadre");
		chkFrame.addActionListener(new ChkFrameAL());
		add(chkFrame, largeWrap);

		// bouton de création
		JButton btnCreate = new JButton("Créer une légende");
		btnCreate.addActionListener(new CreateLegendAL());

		add(btnCreate);
	}

	private class ChkFrameAL implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

		}

	}

	private class CreateLegendAL implements ActionListener, Runnable {

		@Override
		public void actionPerformed(ActionEvent e) {
			ThreadManager.runLater(this);
		}

		@Override
		public void run() {

			GuiUtils.throwIfOnEDT();

			// recuperer le calque actif
			MapLayer lay;
			try {
				lay = projectm.getActiveLayer();
			} catch (MapLayerException e) {
				Log.error(e);
				return;
			}

			// recuperer les dimensions désirées
			Rectangle bounds = getDesiredBounds();
			if (bounds == null) {
				guim.showErrorInBox(
						"Vous devez tracer l'emplacement de la légende sur la carte avant de continuer.");
				return;
			}

			// construire la légende
			LegendBlock lb = LegendBlock.buildAndAddTo(lay, bounds.x, bounds.y, bounds.width,
					bounds.height, getBackgroundStroke());

			// enregistrement pour annulation
			ElementsCancelOp ecop = cancelm.addDrawOperation(lay, lb.getElements());
			ecop.elementsHaveBeenAdded(true);
		}

	}

	private Rectangle getDesiredBounds() {

		// verifier la classe de l'outil
		if (drawm.getCurrentTool() instanceof LegendTool == false)
			return null;

		// recuperer l'outil
		LegendTool tool = (LegendTool) drawm.getCurrentTool();

		// retourner la selection
		return tool.getDesiredBounds();
	}

	private DrawProperties getBackgroundStroke() {
		// TODO Auto-generated method stub
		return null;
	}
	*/

}
