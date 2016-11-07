package abcmap.clipboard;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

import abcmap.events.ClipboardEvent;
import abcmap.managers.Log;
import abcmap.managers.stub.MainManager;
import abcmap.utils.Utils;
import abcmap.utils.listeners.HasListenerHandler;
import abcmap.utils.listeners.ListenerHandler;
import abcmap.utils.threads.ThreadManager;

/**
 * Analyse du presse papier système. L'analyse se fait à interval régulier
 * (quelques ms par défaut)
 * <p>
 * Les outils par défaut de Java pour détecter les changement dans le presse
 * papierne sont pas assez éfficaces.
 * 
 * @author remipassmoilesel
 *
 */
public class ClipboardAnalyser implements HasListenerHandler<ClipboardListener>, Runnable {

	/** Le presse papier système */
	private Clipboard systemCb;

	/** Delai entre vérifications */
	private int delayBetweenVerifications;

	/** Indicateur d'analyse */
	private boolean analysing;

	/** Liste des objets à l'écoute */
	private ListenerHandler<ClipboardListener> listenerHandler;

	/** Dernier digest d'image pour comparaison */
	private byte[] lastImageByteArray;

	public ClipboardAnalyser() {

		this.systemCb = Toolkit.getDefaultToolkit().getSystemClipboard();

		// 3 verification par seconde environ
		this.delayBetweenVerifications = 300;

		this.listenerHandler = new ListenerHandler<>();

		// vider le presse papier en debut d'import
		flushClipBoard();

		analysing = false;
	}

	public void start() {

		if (analysing == true) {
			throw new IllegalStateException("Already analysing");
		}

		else {
			ThreadManager.runLater(this);
		}
	}

	public boolean isAnalysing() {
		return analysing;
	}

	@Override
	public void run() {

		// éviter les appels intempestifs
		if (analysing == true) {
			return;
		} else {
			analysing = true;
		}

		// boucle sans fin en attente du flag
		while (analysing) {

			try {

				// tenter de recuperer le contenu en tant qu'image
				BufferedImage image = (BufferedImage) systemCb.getContents(this)
						.getTransferData(DataFlavor.imageFlavor);

				if (image != null) {

					// tableaux de byte pour comparaisons
					byte[] currentBytes = Utils.imageToByte(image);

					// tester si l'image est différente de la derniere image
					// importée
					if (Arrays.equals(currentBytes, lastImageByteArray) == false) {
						listenerHandler
								.fireEvent(new ClipboardEvent(ClipboardEvent.NEW_IMAGE, image));
						lastImageByteArray = currentBytes;
					}

				}
			}

			// le contenu n'est pas une image
			catch (Exception e) {
				if (MainManager.isDebugMode()) {
					Log.error(e);
				}
			}

			// attendre avant la prochaine verification
			Utils.sleep(delayBetweenVerifications);

		}
	}

	public void stopAnalyseLater() {
		analysing = false;
	}

	/**
	 * Vider le presse papier, 3 essais si erreurs
	 */
	private void flushClipBoard() {

		int trys = 3;
		int i = 0;
		boolean done;

		do {

			// essayer de vider le presspaier
			try {
				FlushingClipboardObject fco = new FlushingClipboardObject();
				systemCb.setContents(fco, fco);
				done = true;
			}

			// erreur lors du vidage, attendre puis relancer
			catch (Exception e) {
				try {
					Thread.sleep(50);
				} catch (InterruptedException e1) {
					Log.error(e);
				}
				done = false;
				i++;
			}

		} while (done == false && i < trys);

	}

	@Override
	public ListenerHandler<ClipboardListener> getListenerHandler() {
		return listenerHandler;
	}

	/**
	 * Comportement de propriétaire et de contenu implémenté comme "null" pour
	 * pouvoir réinitialiser le presse papier.
	 */

	private static class FlushingClipboardObject implements ClipboardOwner, Transferable {
		@Override
		public void lostOwnership(Clipboard clipboard, Transferable contents) {
		}

		@Override
		public Object getTransferData(DataFlavor flavor)
				throws UnsupportedFlavorException, IOException {
			return null;
		}

		@Override
		public DataFlavor[] getTransferDataFlavors() {
			return null;
		}

		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return false;
		}
	}

}