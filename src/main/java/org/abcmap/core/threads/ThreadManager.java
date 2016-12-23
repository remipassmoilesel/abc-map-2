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
     * Thread pool
     */
    private static ExecutorService executor;

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
            getExecutor().execute(task);
        }

        return task;
    }

    /**
     * Create thread pool if necessary and return it.
     * Pool size will be determined by number of processors
     * but never below 2
     *
     * @return
     */
    private static ExecutorService getExecutor() {

        if (executor == null) {
            int nbProcs = Runtime.getRuntime().availableProcessors();
            int nbThreads = nbProcs;
            executor = Executors.newFixedThreadPool(nbThreads);
            //System.out.println("Initializing thread pool: " + nbProcs + " processors, threads: " + nbThreads);
        }

        return executor;
    }

    /**
     * Shutdown thread manager
     */
    public static void shutDown() {

        // stop gently
        getExecutor().shutdown();
    }
}
