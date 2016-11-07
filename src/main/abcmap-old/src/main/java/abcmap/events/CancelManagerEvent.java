package abcmap.events;

import abcmap.utils.notifications.Notification;

public class CancelManagerEvent extends Notification {

	public static final String LISTS_UPDATED = "LIST_UPDATED";

	public CancelManagerEvent(String event, Object value) {
		super(event, value);
	}

}
