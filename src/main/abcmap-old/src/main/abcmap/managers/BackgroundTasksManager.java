package abcmap.managers;

import abcmap.managers.stub.MainManager;
import abcmap.utils.threads.ThreadAccessControl;
import abcmap.utils.threads.ThreadManager;

public class BackgroundTasksManager implements Runnable {

	private static int workerCount = 0;

	private static final long WAITING_TIME = 10000;
	private static final int LOOPS_BEFORE_GARBAGE_CALL = 0;

	private boolean enabled = false;
	private ProjectManager projectm;

	public BackgroundTasksManager() {
		workerCount++;
		this.projectm = MainManager.getProjectManager();
	}

	@Override
	public void run() {

		// éviter les appels intempestifs
		if (ThreadAccessControl.get(1).askAccess() == false) {
			return;
		}

		int loopBeforeGarbage = 0;

		while (true) {

			// patienter
			try {
				Thread.sleep(WAITING_TIME);
			} catch (InterruptedException e) {
				Log.error(e);
			}

			// if (loopBeforeGarbage >= LOOPS_BEFORE_GARBAGE_CALL) {
			// loopBeforeGarbage = 0;
			// System.gc();
			// } else {
			// loopBeforeGarbage++;
			// }

			// Enregistrement auto du projet
			if (projectm.isInitialized()
					&& projectm.getTempDirectoryFile() != null) {

				try {
					projectm.saveDescriptorOnly();
				}

				catch (Exception e) {
					Log.error(e);
				}
			}

			// arret
			if (enabled == false) {
				break;
			}

		}

		ThreadAccessControl.get(1).releaseAccess();

	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		if (enabled && isRunning() == false) {
			// Hors du circuit normal à cause des pauses
			Thread t = ThreadManager.getAnonymThread(this);
			t.setName("BgWorker-" + workerCount);
			t.start();
		}
	}

	public boolean isRunning() {
		return ThreadAccessControl.get(1).isOngoingThread();
	}
}
