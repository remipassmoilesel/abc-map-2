package abcmap.utils.notifications.tool;

import java.util.ArrayList;

import javax.swing.JPanel;

import abcmap.utils.notifications.Notification;
import abcmap.utils.notifications.NotificationManager;

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

	public Notification getEvent() {
		return ev;
	}

	public void setEvent(Notification ev) {
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

	public ArrayList<NotificationManager> getReceivers() {
		return receivers;
	}

}
