package org.abcmap.core.managers;

import com.thoughtworks.xstream.XStream;
import org.abcmap.core.configuration.ConfigurationConstants;
import org.abcmap.core.events.RecentManagerEvent;
import org.abcmap.core.events.manager.EventNotificationManager;
import org.abcmap.core.events.manager.HasEventNotificationManager;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.project.Project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Save recent opened or saved elements in order to allow user to open it again quickly
 */
public class RecentManager extends ManagerTreeAccessUtil implements HasEventNotificationManager {

    private final EventNotificationManager notifm;

    private static final CustomLogger logger = LogManager.getLogger(RecentManager.class);

    /**
     * Path where history is saved
     */
    private final Path historyPath;

    /**
     * Utility used to serialize history
     */
    private final XStream xmlSerializer;

    /**
     * Container where history is stored
     */
    private HistoryContainer container;

    public RecentManager() {
        notifm = new EventNotificationManager(RecentManager.class);

        this.historyPath = ConfigurationConstants.HISTORY_PATH;

        this.xmlSerializer = new XStream();

        try {
            loadHistory();
        } catch (IOException e) {
            logger.error(e);
            this.container = new HistoryContainer();
        }
    }

    /**
     * Try to load history from disk or throw an error
     *
     * @throws IOException
     */
    public void loadHistory() throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(historyPath, ConfigurationConstants.DEFAULT_CHARSET)) {
            this.container = (HistoryContainer) xmlSerializer.fromXML(reader);
        }
    }

    /**
     * Clear all history in memory.
     * <p>
     * Call saveHistory() to write it on disk after.
     *
     * @throws IOException
     */
    public void clearAllHistory() throws IOException {
        clearProfileHistory();
        clearProjectHistory();
    }

    /**
     * Clear profile history in memory
     * <p>
     * You have to call save() after to write it on disk
     */
    public void clearProfileHistory() {
        container.getProfileHistory().clear();
    }

    /**
     * Clear profile history in memory
     * <p>
     * You have to call save() after to write it on disk
     */
    public void clearProjectHistory() {
        container.getProjectHistory().clear();
    }

    /**
     * Try to write history to disk or throw an error
     *
     * @throws IOException
     */
    public void saveHistory() throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(historyPath, ConfigurationConstants.DEFAULT_CHARSET)) {
            writer.write(xmlSerializer.toXML(container));
            writer.flush();
            writer.close();
        }
    }

    /**
     * Return project history
     * <p>
     * Element at 0 index is the most recent
     *
     * @return
     */
    public ArrayList<String> getProjectHistory() {
        return new ArrayList<>(container.getProjectHistory());
    }

    /**
     * Return project history
     * <p>
     * Element at 0 index is the most recent
     *
     * @return
     */
    public ArrayList<String> getProfileHistory() {
        return new ArrayList<>(container.getProfileHistory());
    }

    /**
     * Add current project to recent history
     */
    public boolean addCurrentProject() {

        if (projectm().isInitialized() == false) {
            return false;
        }

        add(projectm().getProject());

        return true;
    }

    /**
     * Add a project to history.
     * <p>
     * History must have
     *
     * @param p
     */
    public void add(Project p) {

        if (p.getFinalPath() == null) {
            throw new NullPointerException("Project must have a final path");
        }

        String pathStr = p.getFinalPath().toAbsolutePath().toString();
        addProjectPath(pathStr);
    }

    /**
     * Add a project path in history. Paths are added on top position
     * <p>
     * If path was in history, it will be deleted and added again.
     *
     * @param pathStr
     */
    public void addProjectPath(String pathStr) {

        // remove path if it was already here
        container.getProjectHistory().remove(pathStr);

        // add it on top of list
        container.getProjectHistory().add(0, pathStr);
    }

    /**
     * Add a profile path in history. Paths are added on top position
     * <p>
     * If path was in history, it will be deleted and added again.
     *
     * @param pathStr
     */
    public void addProfilePath(String pathStr) {

        // remove path if it was already here
        container.getProfileHistory().remove(pathStr);

        // add it on top of list
        container.getProfileHistory().add(0, pathStr);
    }

    public EventNotificationManager getNotificationManager() {
        return notifm;
    }

    /**
     * Fire an event meaning that history changed
     */
    public void fireHistoryChanged() {
        notifm.fireEvent(new RecentManagerEvent(RecentManagerEvent.HISTORY_CHANGED));
    }

    static class HistoryContainer implements Serializable {

        /**
         * List of recent projects
         */
        private ArrayList<String> projectHistory = new ArrayList<>();

        /**
         * List of recent profiles
         */
        private ArrayList<String> profileHistory = new ArrayList<>();

        public ArrayList<String> getProjectHistory() {
            return projectHistory;
        }

        public ArrayList<String> getProfileHistory() {
            return profileHistory;
        }

    }

}
