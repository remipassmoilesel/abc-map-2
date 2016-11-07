package oldtrys.gui;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import abcmap.gui.comps.progressbar.ProgressbarManager;
import abcmap.gui.comps.progressbar.ProgressbarTask;
import abcmap.utils.gui.GuiUtils;
import abcmap.utils.threads.ThreadManager;
import net.miginfocom.swing.MigLayout;

public class ProgressbarManagerTest implements Runnable {

	public static void launch() {
		SwingUtilities.invokeLater(new ProgressbarManagerTest());
	}

	@Override
	public void run() {

		// creer un gestionnaire de barre de progression
		final ProgressbarManager pbm = new ProgressbarManager();

		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout("insets 5"));

		final int max = 50;
		final ProgressbarTask task = pbm.addTask("Test", false, 0, max, 0);

		panel.add(pbm.getLabel(), "gapright 20px");
		panel.add(pbm.getProgressbar(), "gapright 20px");

		GuiUtils.showThis(panel);

		ThreadManager.runLater(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i <= max; i++) {

					task.setCurrentValue(i);
					pbm.updateProgressbarLater(task);

					System.out.println(task);

					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});

	}

}
