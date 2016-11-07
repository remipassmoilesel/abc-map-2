package abcmap.utils.notifications;

import java.util.ArrayList;

import abcmap.utils.Utils;
import abcmap.utils.notifications.tool.NotificationHistoryElement;

public class Notification {

	public static final Integer MAX_EVENT_SAVED = 200;
	private static ArrayList<NotificationHistoryElement> lastCreatedEvents;
	public static Integer instances = 0;

	private Object value;
	private String name;
	private String creationTime;
	private Integer instanceNumber;

	public Notification(String event, Object value) {

		// compter l'instance
		Notification.instances++;

		// elements d'identification
		this.instanceNumber = Notification.instances;
		this.creationTime = Utils.getDate("hh:mm:ss") + " " + System.currentTimeMillis();

		// valeurs
		this.value = value;
		this.name = event;

	}

	public Object getName() {
		return name;
	}

	public Object getValue() {
		return value;
	}

	public String getCreationTime() {
		return creationTime;
	}

	public Integer getInstanceNumber() {
		return instanceNumber;
	}

	@Override
	public String toString() {

		Object[] keys = new Object[] { "#", "name", "value", "created", };
		Object[] values = new Object[] { instanceNumber, name, value, creationTime };

		return Utils.toString(this, keys, values);
	}

}
