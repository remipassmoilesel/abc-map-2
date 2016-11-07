package abcmap.clipboard;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import abcmap.draw.basicshapes.Drawable;
import abcmap.draw.basicshapes.LayerElement;
import abcmap.managers.stub.MainManager;
import abcmap.utils.Utils;
import abcmap.utils.gui.GuiUtils;

public class ClipboardBuffer implements Transferable, ClipboardOwner {

	/** Liste d'elements en mémoire */
	private ArrayList<LayerElement> elements;

	/** Représentation en image de ses elements */
	private BufferedImage image;

	/** Taille max des elements */
	private Rectangle bounds;

	private byte[] imageByteArray;

	/**
	 * Stocke les lements sans les dupliquer
	 * 
	 * @param elmts
	 */
	public ClipboardBuffer(ArrayList<LayerElement> elmts) {

		// controles
		if (elmts == null || elmts.size() < 1) {
			throw new IllegalArgumentException("Invalid list of elements: " + elmts);
		}

		this.elements = elmts;

		// position des elements par rapport à un point 0,0
		reorganizeElementsPosition();

		// creer une image depresentant ces elements
		createImageFromElements();

		imageByteArray = Utils.imageToByte(image);

	}

	/**
	 * Creer une image à partir de la liste des elements.
	 */
	private void createImageFromElements() {
		// calculer la taille de l'image
		this.bounds = new Rectangle();
		bounds.x = 0;
		bounds.y = 0;

		for (LayerElement e : elements) {

			Point p = e.getPosition();
			Dimension dim = e.getMaximumBounds().getSize();

			// control des dimensions
			dim.width += p.x;
			dim.height += p.y;

			if (dim.width > bounds.width) {
				bounds.width = dim.width;
			}
			if (dim.height > bounds.height) {
				bounds.height = dim.height;
			}

		}

		// creer l'image
		this.image = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);

		// ameliorer la qualité des graphics
		Graphics2D g2d = image.createGraphics();
		GuiUtils.applyQualityRenderingHints(g2d);

		// dessiner le fond
		g2d.setPaint(MainManager.getProjectManager().getBackgroundColor());
		g2d.fill(bounds);

		// dessiner les elements
		for (LayerElement e : elements) {
			e.draw(g2d, Drawable.RENDER_FOR_PRINTING);
		}
	}

	/**
	 * Change la position des elements pour qu'elle soit relative à un point 0,0
	 */
	private void reorganizeElementsPosition() {
		// determiner le point le plus en haut et à gauche de l'image
		// pour compensation de positions d'elements
		Point landmark = elements.get(0).getPosition();

		for (LayerElement e : elements) {
			Point p2 = e.getMaximumBounds().getLocation();

			if (landmark.x > p2.x) {
				landmark.x = p2.x;
			}
			if (landmark.y > p2.y) {
				landmark.y = p2.y;
			}
		}

		// repositionner les elements en consequence
		for (LayerElement e : elements) {

			// autres elements
			Point p = e.getPosition();
			p.x -= landmark.x;
			p.y -= landmark.y;

			e.setPosition(p);
			e.refreshShape();
		}
	}

	public byte[] getImageByteArray() {
		return imageByteArray;
	}

	public ArrayList<LayerElement> getElements() {
		return elements;
	}

	public BufferedImage getImage() {
		return image;
	}

	public Rectangle getBounds() {
		return bounds;
	}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] { DataFlavor.imageFlavor };
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return flavor.equals(DataFlavor.imageFlavor);
	}

	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {

		if (isDataFlavorSupported(flavor) == false) {
			throw new UnsupportedFlavorException(flavor);
		}

		return image;
	}

}
