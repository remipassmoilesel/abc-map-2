package abcmap.managers;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import abcmap.clipboard.ClipboardAnalyser;
import abcmap.clipboard.ClipboardBuffer;
import abcmap.clipboard.ClipboardListener;
import abcmap.draw.basicshapes.LayerElement;
import abcmap.draw.shapes.Image;
import abcmap.draw.shapes.Label;
import abcmap.draw.shapes.Tile;
import abcmap.managers.stub.MainManager;
import abcmap.utils.Utils;
import abcmap.utils.gui.GuiUtils;

public class ClipboardManager {

	/** Le presse papier du systeme */
	private Clipboard systemCb;

	/** Outil de surveillance du presse papier */
	private ClipboardAnalyser cbAnalyser;

	/** Presse papier interne du logiciel */
	private ClipboardBuffer cbBuffer;

	private DrawManager drawm;

	public ClipboardManager() {

		this.drawm = MainManager.getDrawManager();

		// recuperer le presse papier du systeme
		this.systemCb = Toolkit.getDefaultToolkit().getSystemClipboard();

		this.cbBuffer = null;

	}

	/**
	 * 
	 * @param start
	 * @param objectWaiter
	 */
	public void watchClipBoardForImages(Boolean start, ClipboardListener listener) {

		if (start && isClipboardAnalysed()) {
			throw new IllegalStateException("Clipboard is already watching");
		}

		// activer l'analyse du presse papier
		if (start) {
			cbAnalyser = new ClipboardAnalyser();
			cbAnalyser.getListenerHandler().add(listener);
			cbAnalyser.start();
		}

		// arret de l'analyse du presse papier
		else if (cbAnalyser != null) {
			cbAnalyser.stopAnalyseLater();
		}

	}

	/**
	 * Surveillance du presse papier. Evite les bugs liés aux evenements de
	 * changement de presse papier.
	 * 
	 * @author Internet
	 * 
	 */

	public boolean isClipboardAnalysed() {
		return cbAnalyser != null && cbAnalyser.isAnalysing();
	}

	/**
	 * Enregistrer une liste d'elements dans le presse papier <b>sans les
	 * dupliquer</b>
	 * 
	 * @param toCopy
	 */
	public void copyElementsToClipboard(ArrayList<LayerElement> toCopy) {

		GuiUtils.throwIfOnEDT();

		cbBuffer = new ClipboardBuffer(toCopy);
		systemCb.setContents(cbBuffer, cbBuffer);

	}

	/**
	 * Retourne les elements contenus dans le presse papier du logiciel,
	 * <p>
	 * Ou un nouvel objet image,
	 * <p>
	 * Ou null si rien ne peut être utilisé.
	 * 
	 * @param toCopy
	 * @return
	 * @throws IOException
	 *             Si une erreur survient lors de l'écriture d'une image dans le
	 *             projet
	 */
	public ArrayList<LayerElement> getElementsFromClipboard() throws IOException {

		GuiUtils.throwIfOnEDT();

		// la liste des elements à retourner
		ArrayList<LayerElement> result = new ArrayList<LayerElement>();

		// tenter de recuperer une image du presse papier
		BufferedImage img = null;
		try {
			img = (BufferedImage) systemCb.getContents(this)
					.getTransferData(DataFlavor.imageFlavor);
		} catch (UnsupportedFlavorException e) {
			Log.error(e);
		}

		// le presse papier contenait une image
		if (img != null) {

			// obtenir un hash de l'image pour comparaison
			// comparer le hash de l'image du presse papier systeme au hash de
			// l'image du presse papier logiciel
			byte[] imageByteArray = Utils.imageToByte(img);

			// les images sont identiques, retourner les elements stockée dans
			// le presse papier logiciel
			if (cbBuffer != null
					&& Arrays.equals(imageByteArray, cbBuffer.getImageByteArray()) == true) {

				for (LayerElement elmt : cbBuffer.getElements()) {
					result.add(elmt.duplicate());
				}

				return result;
			}

			// les images sont différentes, retourner un nouvel objet image
			else {

				// creer un objet image et charger l'image source
				Image elmt = new Image();
				try {
					elmt.loadAndSaveImage(img);
					elmt.refreshShape();
				}

				catch (IOException e) {
					Log.error(e);
					throw e;
				}

				// vider le buffer
				cbBuffer = null;

				// ajouter à la liste resultat puis arret
				result.add(elmt);
				return result;

			}

		}

		else {

			// l'image est nulle, tenter de recuperer du texte
			String str = null;
			try {
				str = (String) systemCb.getContents(this).getTransferData(DataFlavor.stringFlavor);
			} catch (UnsupportedFlavorException e) {
				Log.error(e);
			}

			// le presse papier contenait du texte: creer un objet de texte
			if (str != null) {

				Label lbl = drawm.getWitnessLabel();
				lbl.setText(str);

				result.add(lbl);
				return result;
			}

		}

		// le presse papier ne contient rien de recuperable, retour null
		return null;
	}

	public Tile getClipboardContentAsTile() throws IOException {

		GuiUtils.throwIfOnEDT();

		// tenter de recuperer une image dans le presse papier systeme
		BufferedImage img = null;
		try {
			img = (BufferedImage) systemCb.getContents(this)
					.getTransferData(DataFlavor.imageFlavor);
		} catch (UnsupportedFlavorException e) {
			Log.error(e);
		}

		// le presse papier contenait une image
		if (img != null) {

			// creer une tuile et la retourner
			Tile t = new Tile();
			t.loadAndSaveImage(img, null);

			return t;
		}

		// le presse papier ne contenait rien de recuperable
		return null;
	}

}
