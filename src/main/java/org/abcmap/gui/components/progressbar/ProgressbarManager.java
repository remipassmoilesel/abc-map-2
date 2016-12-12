package org.abcmap.gui.components.progressbar;

import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.managers.MainManager;
import org.abcmap.gui.utils.GuiUtils;

import javax.swing.*;
import java.util.ArrayList;


/**
 * Manage tasks for progress bar
 */
public class ProgressbarManager {

    private static final CustomLogger logger = LogManager.getLogger(ProgressbarManager.class);

    /**
     * Delay before update progressbar
     */
    public final static Integer WAITING_TIME = 300;

    private JProgressBar progressbar;

    /**
     * Optionnal text label associated with progress bar
     */
    private JLabel label;

    /**
     * If set to true, label is shown
     */
    private boolean showLabel;

    /**
     * Tasks list, 0 is the current task
     */
    private ArrayList<ProgressbarTask> tasks;

    private ProgressbarUpdater progressbarUpdater;

    public ProgressbarManager() {
        this.progressbar = new JProgressBar();
        this.label = new JLabel();
        this.tasks = new ArrayList<ProgressbarTask>();
        this.progressbarUpdater = new ProgressbarUpdater();
        this.showLabel = true;
    }

    private class ProgressbarUpdater implements Runnable {

        @Override
        public void run() {

            GuiUtils.throwIfNotOnEDT();

            // no tasks in progress, hide all
            if (tasks.size() <= 0) {
                setComponentsVisible(false);
                return;
            }


            ProgressbarTask task = tasks.get(0);

            // check if progress bar have not been updated recently
            if (task.getCurrentValue() < task.getMaxValue() && task.getEllapsedTimeSinceUpdated() < WAITING_TIME) {
                return;
            }

            // display progress bar and label if necessary
            if (progressbar.isVisible() == false) {
                progressbar.setVisible(true);
            }
            if (showLabel && label.isVisible() == false) {
                label.setVisible(true);
            }

            if (showLabel) {

                StringBuilder txt = new StringBuilder(20);

                txt.append("<html>");

                // display task number if there are several tasks
                if (tasks.size() > 1) {
                    txt.append(Integer.toString(tasks.size()) + " tâches en cours.");
                }

                txt.append(" " + task.getLabel());

                txt.append("</html>");

                if (label.getText().equals(txt) == false) {
                    label.setText(txt.toString());
                }

            }

            // tasks is indeterminate
            if (task.isIndeterminate()) {
                if (progressbar.isIndeterminate() == false) {
                    progressbar.setIndeterminate(true);
                }
            }

            // task is determinate and in progress
            else if (task.getMaxValue() >= task.getCurrentValue()) {

                if (progressbar.isIndeterminate() == true)
                    progressbar.setIndeterminate(false);

                // adjust values
                if (progressbar.getMinimum() != task.getMinValue()) {
                    progressbar.setMinimum(task.getMinValue());
                }
                if (progressbar.getMaximum() != task.getMaxValue()) {
                    progressbar.setMaximum(task.getMaxValue());
                }
                if (progressbar.getValue() != task.getCurrentValue()) {
                    progressbar.setValue(task.getCurrentValue());
                }

            }

            // task is finished
            else {

                try {
                    tasks.remove(task);
                } catch (IndexOutOfBoundsException e) {
                    if (MainManager.isDebugMode()) {
                        logger.error(e);
                    }
                }

                // no more tasks, hide all
                if (tasks.size() <= 0) {
                    setComponentsVisible(false);
                }
            }

            // keep last update time
            task.touch();

        }

    }

    /**
     * Update progressbar only if necessary, on EDT
     *
     * @param task
     * @return
     */
    public boolean updateProgressbarLater(ProgressbarTask task) {

        if (tasks.indexOf(task) < 0) {
            throw new IllegalArgumentException("Unknown task: " + task);
        }

        if (task.getCurrentValue() < task.getMaxValue() && task.getEllapsedTimeSinceUpdated() < WAITING_TIME) {
            return false;
        }

        updateProgressbarLater();
        return true;
    }

    /**
     * Update progressbar on EDT
     */
    private void updateProgressbarLater() {
        SwingUtilities.invokeLater(progressbarUpdater);
    }

    /**
     * Remove a task from list of tasks
     *
     * @param task
     */
    public void removeTask(ProgressbarTask task) {
        tasks.remove(task);
        updateProgressbarLater();
    }

    /**
     * Add a task in list of tasks
     *
     * @param task
     */
    public void addTask(ProgressbarTask task) {
        tasks.add(task);
        updateProgressbarLater();
    }

    /**
     * Create a new task and add it to list of tasks
     *
     * @param label
     * @param indeterminate
     * @param min
     * @param max
     * @param current
     * @return
     */
    public ProgressbarTask addTask(String label, boolean indeterminate, int min, int max, int current) {

        ProgressbarTask pbt = new ProgressbarTask(label, indeterminate, min, max, current);
        tasks.add(pbt);

        updateProgressbarLater();
        return pbt;
    }

    /**
     * Return progress bar component
     *
     * @return
     */
    public JProgressBar getProgressbar() {
        return progressbar;
    }

    /**
     * Get label component of progress bar manager
     *
     * @return
     */
    public JLabel getLabel() {
        return label;
    }

    /**
     * Hide or show components
     *
     * @param b
     */
    public void setComponentsVisible(boolean b) {

        GuiUtils.throwIfNotOnEDT();

        if (progressbar != null) {
            progressbar.setVisible(b);
        }

        if (label != null) {
            label.setVisible(b);
        }

    }

    /**
     * Show or hide components
     *
     * @param b
     */
    public void setComponentsVisibleThreadSafe(final boolean b) {
        if (progressbar != null) {
            SwingUtilities.invokeLater(() -> {
                progressbar.setVisible(b);

            });
        }
    }

}
