package abcmap.events;

import abcmap.utils.notifications.Notification;

public class GuiManagerEvent extends Notification {

	public static final String WINDOW_MODE_CHANGED = "WINDOW_MODE_CHANGED";

	public GuiManagerEvent(String event, Object value) {
		super(event, value);
	}

	public static boolean isWindowModeNotification(Notification arg) {

		if (arg instanceof GuiManagerEvent == false) {
			return false;
		}

		if (arg.getName().equals(WINDOW_MODE_CHANGED) == false) {
			return false;
		}

		return true;
	}

}
