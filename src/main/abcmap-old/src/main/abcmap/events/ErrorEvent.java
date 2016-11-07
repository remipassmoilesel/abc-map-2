package abcmap.events;

import abcmap.utils.notifications.Notification;

public class ErrorEvent extends Notification {

	public static final String NON_CATCHED_ERROR = "NON_CATCHED_ERROR";

	public ErrorEvent(String event, Object value) {
		super(event, value);
	}

}
