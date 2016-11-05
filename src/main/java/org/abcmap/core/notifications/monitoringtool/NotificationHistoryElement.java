package org.abcmap.core.notifications.monitoringtool;

import org.abcmap.core.notifications.Notification;
import org.abcmap.core.notifications.NotificationManager;

import javax.swing.*;
import java.util.ArrayList;

public class NotificationHistoryElement {
	private NotificationManager om;
	private Notification ev;
	private Object owner;
	private ArrayList<NotificationManager> receivers;

	public NotificationHistoryElement() {
		this.om = null;
		this.ev = null;
		this.owner = null;
		this.receivers = new ArrayList<>();
	}

	public void setReceivers(ArrayList<NotificationManager> observers) {
		receivers.addAll(observers);
	}

	public NotificationManager getObserverManager() {
		return om;
	}

	public void setObserverManager(NotificationManager om) {
		this.om = om;
	}

	public Notification getNotification() {
		return ev;
	}

	public void setNotification(Notification ev) {
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

	public ArrayList<NotificationManager> getObservers() {
		return receivers;
	}

}
