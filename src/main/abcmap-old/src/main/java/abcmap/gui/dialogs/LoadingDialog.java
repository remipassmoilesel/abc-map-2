package abcmap.gui.dialogs;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import abcmap.gui.comps.buttons.HtmlLabel;
import abcmap.managers.Log;
import abcmap.utils.gui.GuiUtils;
import abcmap.utils.threads.ThreadAccessControl;
import abcmap.utils.threads.ThreadManager;

public class LoadingDialog extends JDialog {

	private static LoadingDialogManager ldm = new LoadingDialogManager();

	@Deprecated
	public LoadingDialog() {

		// propriétés de la fenetre
		setUndecorated(true);
		setModal(true);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setSize(new Dimension(325, 140));
		setLocationRelativeTo(null);

		// bordure
		((JComponent) this.getContentPane()).setBorder(BorderFactory
				.createLineBorder(Color.DARK_GRAY, 3));

		// barre de progression
		HtmlLabel lblWait = new HtmlLabel("Veuillez patienter...");
		JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);

		JPanel mainPanel = new JPanel();
		mainPanel.add(lblWait, "wrap");
		mainPanel.add(progressBar, "wrap");

		setContentPane(mainPanel);

	}

	public static class LoadingDialogManager implements Runnable {

		private static final long FIRST_TIME_WAITING = 1000;
		private static final long TIME_WAITING = 200;
		private LoadingDialog dialog;
		private ArrayList<LoadingDialogUser> users;

		@Deprecated
		public LoadingDialogManager() {
			this.dialog = new LoadingDialog();

			// appels de la methode launch
			this.users = new ArrayList<LoadingDialogUser>();
		}

		@Override
		public void run() {

			GuiUtils.throwIfOnEDT();

			// eviter les appels intempestifs
			if (ThreadAccessControl.get(1).askAccess() == false) {
				return;
			}

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					if (dialog != null)
						dialog.setVisible(true);
				}
			});

			boolean firstLoop = true;
			while (true) {

				// premire attente
				if (firstLoop) {
					try {
						Thread.sleep(FIRST_TIME_WAITING);
					} catch (InterruptedException e) {
						Log.error(e);
					}
					firstLoop = false;
				}

				// suivantes
				else {
					try {
						Thread.sleep(TIME_WAITING);
					} catch (InterruptedException e) {
						Log.error(e);
					}

					for (LoadingDialogUser user : new ArrayList<LoadingDialogUser>(
							users)) {
						if (user.areYouStillWorking() == false) {
							users.remove(user);
						}
					}

					if (users.size() < 1) {
						break;
					}
				}

			}

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					dialog.setVisible(false);
				}
			});

			users.clear();
			ThreadAccessControl.get(1).releaseAccess();

		}

		public boolean isRunning() {
			return ThreadAccessControl.get(1).isOngoingThread();
		}

		public void stopAtNextLoop(LoadingDialogUser user) {
			users.remove(user);
		}

		public void launch(LoadingDialogUser user) {

			users.add(user);

			if (isRunning() == false) {
				ThreadManager.runLater(this);
			}

		}

	}

	public static LoadingDialogManager getManager() {
		return ldm;
	}

	/**
	 * Systeme de verification pour permettre à la fenetre d'attente de se
	 * refermer d'elle même si elle ne recoit pas de nouvelles.
	 * 
	 * @author remipassmoilesel
	 *
	 */
	public static interface LoadingDialogUser {
		public boolean areYouStillWorking();
	}

	@Deprecated
	public void setVisible(boolean b) {
		super.setVisible(b);
	}

}
