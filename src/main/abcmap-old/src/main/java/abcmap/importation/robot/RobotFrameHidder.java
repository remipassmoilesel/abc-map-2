package abcmap.importation.robot;

import java.awt.Component;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import abcmap.managers.GuiManager;
import abcmap.managers.Log;
import abcmap.managers.stub.MainManager;
import abcmap.utils.gui.GuiUtils;

public class RobotFrameHidder {

	private ArrayList<Component> visibleFrames;
	private GuiManager guim;

	public RobotFrameHidder() {
		this.visibleFrames = new ArrayList<Component>(5);
		this.guim = MainManager.getGuiManager();
	}

	public void hideVisibleFrames() {

		// pas d'action sur l'EDT
		GuiUtils.throwIfOnEDT();

		visibleFrames.clear();

		try {
			SwingUtilities.invokeAndWait(new Runnable() {

				@Override
				public void run() {

					// iterer toutes les fenetres du programme
					for (Component c : guim.getVisibleWindows()) {
						c.setVisible(false);
						visibleFrames.add(c);
					}
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			Log.error(e);
		}

	}

	public void showHiddedFrames() {

		// pas d'action sur l'EDT
		GuiUtils.throwIfOnEDT();

		try {
			SwingUtilities.invokeAndWait(new Runnable() {

				@Override
				public void run() {

					// iterer toutes les fenetres qui ont été masquées
					for (Component c : visibleFrames) {
						c.setVisible(true);
					}

				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			Log.error(e);
		}
	}

}
