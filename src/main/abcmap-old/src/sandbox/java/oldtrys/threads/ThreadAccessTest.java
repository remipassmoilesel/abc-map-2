package oldtrys.threads;

import abcmap.utils.Utils;
import abcmap.utils.threads.ThreadAccessControl;

public class ThreadAccessTest {

	public static void main(String[] args) {

		ThreadTest runnable = new ThreadTest();

		int trys = 5;
		for (int i = 0; i < trys; i++) {
			Thread t = new Thread(runnable);
			// Thread t = new Thread(new ThreadTest());

			t.start();
		}

	}

	private static class ThreadTest implements Runnable {

		private ThreadAccessControl taccess;

		public ThreadTest() {
			taccess = new ThreadAccessControl();

			taccess.setMaxAccess(3);

			taccess.setRefusedAccessAction(new Runnable() {
				public void run() {
					System.out.println("Erreur !");
				}
			});
		}

		@Override
		public void run() {

			System.out.println("Demande d'accés: " + Utils.getThreadSimpleID());

			if (taccess.askAccess() == false) {
				System.out.println("Accés refusé: " + Utils.getThreadSimpleID());
				return;
			}

			System.out.println("Accés autorisé: " + Utils.getThreadSimpleID());

			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			taccess.releaseAccess();

			System.out.println("Fin d'accés: " + Utils.getThreadSimpleID());

		}

	}
}
