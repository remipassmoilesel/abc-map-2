package org.abcmap.core.events.manager;

import org.abcmap.core.utils.Utils;

/**
 * Event that can be sent by notification manager
 */
public class Event {

    public static Integer instances = 0;

    private Object value;
    private String name;
    private String creationTime;
    private Integer instanceNumber;

    public Event(String name) {
        this(name, null);
    }

    public Event(String name, Object value) {

        // count instances
        instances++;

        // identification informations
        this.instanceNumber = instances;
        this.creationTime = Utils.getDate("hh:mm:ss") + " " + System.currentTimeMillis();

        // name and optionnal value
        this.name = name;
        this.value = value;

    }

    /**
     * Utility to create test event methods
     *
     * @param eventClass
     * @param name
     * @param event
     * @return
     */
    protected static boolean testEvent(Class<? extends Event> eventClass, String name, Event event) {

        // test class
        if (eventClass.isInstance(event) == false) {
            return false;
        }

        // test event name
        if (event.getName().equals(name)) {
            return true;
        }

        return false;
    }

    /**
     * Get name of event
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Get value of event. Can be null.
     *
     * @return
     */
    public Object getValue() {
        return value;
    }

    /**
     * Get time of creation
     *
     * @return
     */
    public String getCreationTime() {
        return creationTime;
    }

    /**
     * Get unique instance number
     *
     * @return
     */
    public Integer getInstanceNumber() {
        return instanceNumber;
    }

    @Override
    public String toString() {

        Object[] keys = new Object[]{"#", "name", "value", "created",};
        Object[] values = new Object[]{instanceNumber, name, value, creationTime};

        return Utils.toString(this, keys, values);
    }

    /**
     * Do not override this method, all notifications should be different even if they own same fields,
     * because same notifications can be fired at different time
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
