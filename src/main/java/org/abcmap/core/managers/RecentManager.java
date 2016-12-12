package org.abcmap.core.managers;

import org.abcmap.core.notifications.HasNotificationManager;
import org.abcmap.core.notifications.NotificationManager;

import java.io.IOException;

/**
 * Created by remipassmoilesel on 08/12/16.
 */
public class RecentManager implements HasNotificationManager {
    private final NotificationManager notifm;


    public RecentManager() {
        notifm = new NotificationManager(RecentManager.class);
    }

    public void clearProfileHistory() {

    }

    public void clearProjectHistory() {

    }

    public void saveHistory() throws IOException {

    }

    public NotificationManager getNotificationManager() {
        return notifm;
    }
}
