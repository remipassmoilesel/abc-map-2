package abcmap.gui.comps.messagebox;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import abcmap.managers.Log;
import abcmap.utils.threads.ThreadManager;

public class MessageBoxManager {

	private JFrame frame;
	private BoxMessagePanel messagePanel;
	private JPopupMenu popup;
	private Integer defaultTime;

	public MessageBoxManager() {
		this(null);
	}

	public MessageBoxManager(JFrame parent) {

		// la fenetre parent
		this.frame = parent;

		// le panneau ou seront affichés les messages
		this.messagePanel = new BoxMessagePanel();

		// fermer si click
		CloseOnClick coc = new CloseOnClick();
		messagePanel.addMouseListener(coc);

		this.defaultTime = 3000;

	}

	public void setFrame(JFrame frame) {
		this.frame = frame;
	}

	public void showMessage(String message) {
		showMessage(defaultTime, message);
	}

	public Integer getDefaultTime() {
		return defaultTime;
	}

	public void setDefaultTime(Integer defaultTime) {
		this.defaultTime = defaultTime;
	}

	/**
	 * Threadsafe
	 * 
	 * @param timeMilliSec
	 * @param message
	 */
	public void showMessage(Integer timeMilliSec, final String message) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {

				// verifier que le parent ne soit pas null
				if (frame == null)
					return;

				// que faire si déjà visible ?
				// TODO

				// creer le composant qui affichera les message à chaque appel
				popup = new JPopupMenu();
				popup.add(messagePanel);
				popup.pack();

				// affecter le texte
				messagePanel.setMessage("<html><center>" + message + "</center></html>");
				messagePanel.refresh();

				// dimensions du parent
				Dimension df = frame.getSize();
				Dimension dm = messagePanel.getPreferredSize();

				// position x du message, centré
				int x = (df.width - dm.width) / 2;

				// position y du message, au dessus du bas de la fenetre
				int y = (int) (df.height - (df.height * 0.20f) - dm.height);

				// montrer le message
				try {
					popup.show(frame.getContentPane(), x, y);
				} catch (Exception e) {
					Log.error(e);
				}

				// lancer un timer de fermeture
				ThreadManager.runLater(defaultTime, new ClosingTask(popup), true);
			}
		});
	}

	public void setBackgroundColor(Color background) {
		messagePanel.setBackground(background);
		messagePanel.refresh();
	}

	/**
	 * Ferme un JPopupmenu si le jpopupmenu est encore actif
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class ClosingTask implements Runnable {

		private JPopupMenu origin;

		public ClosingTask(JPopupMenu origin) {
			this.origin = origin;
		}

		public void run() {
			if (popup == origin)
				popup.setVisible(false);
		}
	}

	/**
	 * Fermeture de la box en cas de click
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class CloseOnClick extends MouseAdapter {
		@Override
		public void mouseReleased(MouseEvent e) {
			popup.setVisible(false);
		}
	}

}
