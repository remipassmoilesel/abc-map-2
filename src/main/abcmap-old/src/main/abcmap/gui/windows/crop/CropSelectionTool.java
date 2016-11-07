package abcmap.gui.windows.crop;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import abcmap.draw.basicshapes.Handle;
import abcmap.draw.basicshapes.RectangleShape;

public class CropSelectionTool extends MouseAdapter {

	private CropConfigurationWindow cropWindow;
	private boolean modifying;
	private boolean drawing;
	private Handle activeHandle;
	private Point selectionOrigin;
	private int minimalWidth;
	private CropSelectionRectangle selection;

	public CropSelectionTool(CropConfigurationWindow csw) {

		this.cropWindow = csw;

		this.minimalWidth = 50;
		this.selection = cropWindow.getSelection();

	}

	@Override
	public void mousePressed(MouseEvent e) {

		// repondre seulement au clic gauche
		if (SwingUtilities.isLeftMouseButton(e) == false)
			return;

		// position de la souris
		Point m = e.getPoint();

		// raz des indicateurs de travaux
		drawing = false;
		modifying = false;

		// si clic sur une poignee, modification
		for (Handle h : selection.getHandles()) {
			if (h.getInteractionArea().contains(m)) {

				// garder la reference de la poignee active
				activeHandle = h;

				// action
				modifying = true;
			}
		}

		// sinon tracage
		if (modifying == false) {
			drawing = true;

			selection.setPosition(m);
			selection.setDimensions(new Dimension(0, 0));
			selection.refreshShape();

			selectionOrigin = new Point(m.x, m.y);
		}

		// rafraichir le parent
		cropWindow.refreshImagePane();
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {

		// repondre seulement au clic gauche
		if (SwingUtilities.isLeftMouseButton(arg0) == false)
			return;

		// recuperer la position de la souris
		Point m = arg0.getPoint();

		// recuperer la selection
		Rectangle selectionRect = selection.getRectangle();

		// les poignees
		ArrayList<Handle> handles = selection.getHandles();

		// Nouvelles dimensions du rectangle
		Point newPos = new Point(selection.getPosition());
		Dimension newDim = new Dimension(selection.getDimensions());

		// moitié de l'epaisseur du trait
		int ht = selection.getStroke().getHalfThickness();

		// position du coin bas droit du rectangle interne
		Point brc = new Point();
		brc.x = selectionRect.x + selectionRect.width;
		brc.y = selectionRect.y + selectionRect.height;

		/*
		 * Redimensionner le rectangle
		 */
		if (modifying == true) {

			// poignee en haut a gauche: modifier position et dimensions
			if (RectangleShape.ULC_HANDLE_INDEX == handles.indexOf(activeHandle)) {

				// mettre à jour le coin haut gauche
				newPos.setLocation(m);

				// calculer les nouvelles dimensions
				newDim.width = brc.x - m.x - ht;
				newDim.height = brc.y - m.y - ht;

			}

			// poignee du coin en bas droite
			else if (RectangleShape.BRC_HANDLE_INDEX == handles.indexOf(activeHandle)) {
				newDim.width = m.x - selectionRect.x;
				newDim.height = m.y - selectionRect.y;
			}

			// poignee du milieu
			else if (RectangleShape.MIDDLE_HANDLE_INDEX == handles.indexOf(activeHandle)) {

				newPos = new Point(m);
				newPos.x -= selectionRect.width / 2;
				newPos.y -= selectionRect.height / 2;

			}

			// respecter les dimensions minimales
			if (newDim.width < minimalWidth) {
				newDim.width = minimalWidth;
				newPos.x = brc.x - minimalWidth - ht;
			}

			if (newDim.height < minimalWidth) {
				newDim.height = minimalWidth;
				newPos.y = brc.y - minimalWidth - ht;
			}

			// rafraichir la forme
			selection.setPosition(newPos);
			selection.setDimensions(newDim);
			selection.refreshShape();

		}

		/*
		 * Dessiner un rectangle
		 */
		else if (drawing == true) {

			// Utiliser une copie du point d'origine
			Point originCopy = new Point(selectionOrigin);

			// calcul des dimensions du rectangle
			int w = m.x - originCopy.x;
			int h = m.y - originCopy.y;
			Dimension dim = new Dimension(w, h);

			// compenser les dimensions si elles sont negatives
			// pour obtenir toujours des valeurs positives
			if (dim.width < 0) {
				int x = originCopy.x + dim.width;
				originCopy.setLocation(x, originCopy.y);
				dim.width = -dim.width;
			}

			// compenser les dimensions si elles sont negatives
			// pour obtenir toujours des valeurs positives
			if (dim.height < 0) {
				int y = originCopy.y + dim.height;
				originCopy.setLocation(originCopy.x, y);
				dim.height = -dim.height;
			}

			// respecter la position minimale
			if (selectionOrigin.x < 0)
				selectionOrigin.setLocation(0, originCopy.y);

			if (selectionOrigin.y < 0)
				selectionOrigin.setLocation(originCopy.x, 0);

			// respecter les dimensions minimales
			if (dim.width < minimalWidth) {
				dim.width = minimalWidth;
			}

			if (dim.height < minimalWidth) {
				dim.height = minimalWidth;
			}

			// modifier la forme
			selection.setPosition(originCopy);
			selection.setDimensions(dim);
			selection.refreshShape();

		}

		// valider la sélection actuelle
		cropWindow.validVisualSelection();

		// rafraichir le parent
		cropWindow.refreshImagePane();

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {

		// repondre seulement au clic gauche
		if (SwingUtilities.isLeftMouseButton(arg0) == false)
			return;

		if (drawing = true) {
			drawing = false;

			// si le tracé est trop petit, cacher le tracé
			Dimension selDim = cropWindow.getSelection().getRectangle().getSize();
			if (selDim.width < minimalWidth && selDim.height < minimalWidth) {
				cropWindow.hideSelection();
				cropWindow.refreshImagePane();
			}

		}

		// pas de validation ici, sinon decalage de position
		// cropWindow.validVisualSelection();

		modifying = false;
	}

	public boolean isDrawing() {
		return drawing;
	}

	public boolean isResizing() {
		return modifying;
	}
}
