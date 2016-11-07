package abcmap.gui.toolOptionPanels;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import abcmap.cancel.ElementsCancelOp;
import abcmap.draw.basicshapes.DrawProperties;
import abcmap.draw.legend.LegendBlock;
import abcmap.draw.tools.LegendTool;
import abcmap.exceptions.MapLayerException;
import abcmap.gui.comps.buttons.HtmlCheckbox;
import abcmap.managers.Log;
import abcmap.project.layers.MapLayer;
import abcmap.utils.gui.GuiUtils;
import abcmap.utils.threads.ThreadManager;

public class LegendToolOptionPanel extends ToolOptionPanel {

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

}
