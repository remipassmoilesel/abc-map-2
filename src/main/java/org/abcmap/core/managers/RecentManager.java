package org.abcmap.core.managers;

import org.abcmap.core.events.manager.EventNotificationManager;
import org.abcmap.core.events.manager.HasEventNotificationManager;
import org.abcmap.core.project.Project;

import java.io.IOException;

/**
 * Created by remipassmoilesel on 08/12/16.
 */
public class RecentManager extends ManagerTreeAccessUtil implements HasEventNotificationManager {

    private final EventNotificationManager notifm;

    public RecentManager() {
        notifm = new EventNotificationManager(RecentManager.class);
    }

    public void clearProfileHistory() {

    }

    public void clearProjectHistory() {

    }

    public void saveHistory() throws IOException {

    }

    public void add(Project p) {

    }

    /**
     * Add current project to recent history
     */
    public void addCurrentProject() {
        if (projectm().isInitialized()) {
            return;
        }
        add(projectm().getProject());
    }

    public EventNotificationManager getNotificationManager() {
        return notifm;
    }


}
