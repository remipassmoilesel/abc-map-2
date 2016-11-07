package abcmap.utils.threads;

import abcmap.managers.Log;

class DistributionThread implements Runnable {

	private static final long WAITING_TIME_FOR_EXECUTION = 10;
	private ThreadManager parent;

	public DistributionThread(ThreadManager parent) {
		this.parent = parent;
	}

	@Override
	public synchronized void run() {

		while (parent.getThreadWaitingList().size() > 0) {

			// prendre la première tache de la liste
			ManagedThread task = null;
			try {
				task = parent.getThreadWaitingList().remove(0);
			} catch (IndexOutOfBoundsException e) {
				continue;
			}

			// attendre qu'il y ai de la place pour le thread
			while (parent.getThreadWaitingList().size() > ThreadManager.MAX_THREAD_NBR) {
				try {
					Thread.sleep(WAITING_TIME_FOR_EXECUTION);
				} catch (InterruptedException e) {
					Log.error(e);
				}
			}

			// démarrer la tache
			task.start();
		}
	}
}
