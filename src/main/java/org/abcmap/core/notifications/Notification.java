package org.abcmap.core.notifications;

import org.abcmap.core.notifications.monitoringtool.NotificationHistoryElement;
import org.abcmap.core.utils.Utils;

import java.util.ArrayList;

/**
 * Event that can be sent by notification manager
 */
public class Notification {

    public static final Integer MAX_EVENT_SAVED = 200;
    private static ArrayList<NotificationHistoryElement> lastCreatedEvents;
    public static Integer instances = 0;

    private Object value;
    private String name;
    private String creationTime;
    private Integer instanceNumber;

    public Notification(String name, Object value) {

        // count instances
        instances++;

        // identification informations
        this.instanceNumber = instances;
        this.creationTime = Utils.getDate("hh:mm:ss") + " " + System.currentTimeMillis();

        // name and optionnal value
        this.name = name;
        this.value = value;

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

        Object[] keys = new Object[]{"#", "name", "value", "created",};
        Object[] values = new Object[]{instanceNumber, name, value, creationTime};

        return Utils.toString(this, keys, values);
    }

}
