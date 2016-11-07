package abcmap.events;

import abcmap.utils.notifications.Notification;

public class DrawManagerEvent extends Notification {
	public static final String TOOL_CHANGED = "TOOL_CHANGED";
	public static final String DRAW_STROKE_CHANGED = "DRAW_STROKE_CHANGED";
	public static final String TOOL_MODE_CHANGED = "TOOL_MODE_CHANGED";
	public static final String WITNESSES_CHANGED = "WITNESSES_CHANGED";

	public DrawManagerEvent(String event, Object value) {
		super(event, value);
	}
}
