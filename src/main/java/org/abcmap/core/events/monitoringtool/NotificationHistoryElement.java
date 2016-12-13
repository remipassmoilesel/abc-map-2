package org.abcmap.core.events.monitoringtool;

import org.abcmap.core.events.manager.Event;
import org.abcmap.core.events.manager.EventNotificationManager;

import javax.swing.*;
import java.util.ArrayList;

public class NotificationHistoryElement {
	private EventNotificationManager om;
	private Event ev;
	private Object owner;
	private ArrayList<EventNotificationManager> receivers;

	public NotificationHistoryElement() {
		this.om = null;
		this.ev = null;
		this.owner = null;
		this.receivers = new ArrayList<>();
	}

	public void setReceivers(ArrayList<EventNotificationManager> observers) {
		receivers.addAll(observers);
	}

	public EventNotificationManager getObserverManager() {
		return om;
	}

	public void setObserverManager(EventNotificationManager om) {
		this.om = om;
	}

	public Event getNotification() {
		return ev;
	}

	public void setNotification(Event ev) {
		this.ev = ev;
	}

	public Object getOwner() {
		return owner;
	}

	public void setOwner(Object owner) {
		this.owner = owner;
	}

	public JPanel getPanel() {
		return new NotificationHistoryPanel(this);
	}

	public ArrayList<EventNotificationManager> getObservers() {
		return receivers;
	}

}
