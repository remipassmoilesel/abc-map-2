package org.abcmap.core.threads;

import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.log.LogLevel;
import org.abcmap.core.managers.LogManager;

/**
 * Wrapper for a task. allow to show the previous call stack for debugging.
 */
public class ManagedTask implements Runnable {

    private static final CustomLogger logger = LogManager.getLogger(ManagedTask.class);
    private static boolean debugMode = true;

    static {
        if (debugMode) {
            logger.setLevel(LogLevel.DEBUG);
        }
    }

    /**
     * Count all managed tasks launch
     */
    private static long count = 0;

    /**
     * Id of current task
     */
    private final long id;

    /**
     * Previous call stack of this managed task
     */
    private StackTraceElement[] callStack;

    /**
     * Task to run
     */
    private Runnable runnable;

    /**
     * If true, task is completed. It may end with normally, or with an exception, ...
     */
    private boolean done;

    public ManagedTask(Runnable run) {
        this.id = count++;
        runnable = run;
        callStack = Thread.currentThread().getStackTrace();

        done = false;
    }

    @Override
    public void run() {

        try {
            runnable.run();
        }

        // error happen, catch it
        catch (Throwable e) {

            StringBuilder text = new StringBuilder();
            text.append(ManagedTask.class.getSimpleName() + " #" + id + "/" + count + " - Previous calls: \n");
            for (StackTraceElement stackTraceElement : callStack) {
                text.append("\t" + stackTraceElement + "\n");
            }
            text.append("\n\n");

            logger.error(e);
            logger.error(text.toString());

            if (debugMode) {
                System.err.println(e);
                System.err.println(text);
            }

        }

        // finally mark it as done
        finally {
            done = true;
        }
    }

    public StackTraceElement[] getCallStack() {
        return callStack;
    }
}
