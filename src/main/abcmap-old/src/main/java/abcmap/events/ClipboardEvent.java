package abcmap.events;

import abcmap.utils.notifications.Notification;

public class ClipboardEvent extends Notification {

	public static final String NEW_IMAGE = "NEW_IMAGE";

	public ClipboardEvent(String event, Object value) {
		super(event, value);
	}

}
