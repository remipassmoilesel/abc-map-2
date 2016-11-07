package abcmap.events;

import abcmap.utils.notifications.Notification;

public class RecentHistoryEvent extends Notification {
	public static final String HISTORY_CLEARED = "HISTORY_CLEARED";
	public static final String HISTORY_LOADED = "HISTORY_LOADED";

	public static final String PROJECT_ADDED = "PROJECT_ADDED";
	public static final String PROJECT_REMOVED = "PROJECT_REMOVED";

	public static final String PROFILE_ADDED = "PROFILE_ADDED";
	public static final String PROFILE_REMOVED = "PROFILE_REMOVED";

	public RecentHistoryEvent(String event, Object value) {
		super(event, value);
	}
}
