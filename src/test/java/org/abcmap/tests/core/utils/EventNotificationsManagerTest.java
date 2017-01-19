package org.abcmap.tests.core.utils;

import org.abcmap.core.events.manager.HasEventNotificationManager;
import org.abcmap.core.events.manager.Event;
import org.abcmap.core.events.manager.EventNotificationManager;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class EventNotificationsManagerTest {

    private static ArrayList<Event> notificationsReceived = new ArrayList<>();

    @Test
    public void tests() throws IOException, InterruptedException {

        EventNotificationManager.setDebugMode(true);

        // source of events, observed
        NotificationTester source = new NotificationTester();

        // create observers
        int observersNumber = 10;
        ArrayList<HasEventNotificationManager> list = new ArrayList<>();
        for (int i = 0; i < observersNumber; i++) {
            NotificationTester tester = new NotificationTester();
            list.add(tester);
            source.getNotificationManager().addObserver(tester);
        }

        // launch first event
        source.getNotificationManager().fireEvent(new Event("Ding dong !", null));

        // events are transmitted in other thread, should be less than expected result (probably 0)
        int received = notificationsReceived.size();
        assertTrue("Event thread test: " + received, received < observersNumber);

        // wait until notifications are transmitted on other threads and check
        waitUntilReceived(observersNumber);
        received = notificationsReceived.size();
        assertTrue("Event test 1: " + received, received == observersNumber);

        // fire notification from observer, no events should be transmitted here
        list.get(0).getNotificationManager().fireEvent(new Event("Ding dong !", null));
        received = notificationsReceived.size();
        assertTrue("Event test 2: " + received, received == observersNumber);

        // add last observers a second time, no more events should be fired than last time
        source.getNotificationManager().addObservers(list);
        waitUntilReceived(observersNumber);
        received = notificationsReceived.size();
        assertTrue("Event test 3: " + received, received == observersNumber);

        // add more observers
        for (int i = 0; i < observersNumber; i++) {
            source.getNotificationManager().addObserver("owner", (notif) -> {
                notificationsReceived.add(notif);
            });
        }

        // Fire event. All previous observers should received it
        source.getNotificationManager().fireEvent(new Event("Ding dong !", null));

        // but not now, just after
        received = notificationsReceived.size();
        assertTrue("Event test 4: " + received, received < observersNumber * 3);

        // here !
        waitUntilReceived(observersNumber * 3);
        received = notificationsReceived.size();
        assertTrue("Event test 5: " + received, received == observersNumber * 3);
    }

    /**
     * Wait until number of notifications received reach specified number, or leave after a maximum time
     *
     * @param number
     * @throws InterruptedException
     */
    private void waitUntilReceived(int number) throws InterruptedException {
        int waitTime = 100;
        int maxTime = 3000;
        int currentTime = 0;
        while (currentTime < maxTime) {
            if (notificationsReceived.size() == number) {
                break;
            }
            Thread.sleep(waitTime);
            currentTime += waitTime;
        }
    }

    private class NotificationTester implements HasEventNotificationManager {

        private EventNotificationManager notifm = new EventNotificationManager(this);

        NotificationTester() {
            notifm.addEventListener((notification) -> {
                notificationsReceived.add(notification);
            });
        }

        @Override
        public EventNotificationManager getNotificationManager() {
            return notifm;
        }
    }


}
