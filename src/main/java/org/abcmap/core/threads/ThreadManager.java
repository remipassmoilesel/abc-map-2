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
    private static final int THREAD_POOL_LENGHT = 20;


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
    public static void runLater(Runnable run) {
        runLater(run, false);
    }

    /**
     * Wait specified time before run task. Task may be runned little after waiting time.
     *
     * @param timeMilliSec
     * @param run
     */
    public static void runLater(int timeMilliSec, Runnable run) {
        runLater(timeMilliSec, run, false);
    }

    /**
     * Wait specified time before run task. Task may be runned little after waiting time.
     *
     * @param timeMilliSec
     * @param run
     */
    public static void runLater(int timeMilliSec, Runnable run, boolean onEdt) {

        try {
            Thread.sleep(timeMilliSec);
        } catch (InterruptedException e) {
            logger.error(e);
        }

        runLater(run, onEdt);
    }

    /**
     * Execute a task in a separated thread, eventually on EDT
     *
     * @param runnable
     */
    public static void runLater(Runnable runnable, boolean onEDT) {

        if (onEDT) {
            SwingUtilities.invokeLater(runnable);
        } else {
            executor.execute(runnable);
        }

    }
}
