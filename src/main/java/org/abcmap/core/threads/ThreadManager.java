package org.abcmap.core.threads;

import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;

import javax.swing.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Utility to run tasks in others threads.
 */
public class ThreadManager {

    private static final CustomLogger logger = LogManager.getLogger(ThreadManager.class);

    /**
     * Thread max length
     */
    private static final int THREAD_POOL_LENGHT = 4;


    /**
     * Thread pool
     */
    private static ExecutorService executor;

    public static void init() {
        executor = Executors.newFixedThreadPool(THREAD_POOL_LENGHT);
    }

    /**
     * Run a task in a separated thread
     *
     * @param run
     */
    public static ManagedTask runLater(Runnable run) {
        return runLater(run, false);
    }

    /**
     * Wait specified time before run task. Task may be runned little after waiting time.
     *
     * @param run
     * @param timeMilliSec
     */
    public static ManagedTask runLater(Runnable run, int timeMilliSec) {
        return runLater(run, false, timeMilliSec);
    }

    /**
     * Wait specified time before run task. Task may be runned little after waiting time.
     *
     * @param timeMilliSec
     * @param run
     */
    public static ManagedTask runLater(Runnable run, boolean onEdt, int timeMilliSec) {

        ManagedTask task = new ManagedTask(run);

        ThreadManager.runLater(() -> {
            try {
                Thread.sleep(timeMilliSec);
            } catch (InterruptedException e) {
                logger.error(e);
            }

            runLater(task, onEdt);
        });

        return task;
    }

    /**
     * Execute a task in a separated thread, eventually on EDT
     *
     * @param runnable
     */
    public static ManagedTask runLater(Runnable runnable, boolean onEDT) {

        ManagedTask task = null;
        if (runnable instanceof ManagedTask) {
            task = (ManagedTask) runnable;
        } else {
            task = new ManagedTask(runnable);
        }

        if (onEDT) {
            SwingUtilities.invokeLater(task);
        } else {
            executor.execute(task);
        }

        return task;
    }
}
