package org.abcmap.core.threads;

/**
 * Wrapper for a task. allow to show the previous call stack for debugging.
 */
public class ManagedTask implements Runnable {

    private static long count = 0;
    private final long id;
    private StackTraceElement[] callStack;
    private Runnable runnable;

    public ManagedTask(Runnable run) {
        this.id = count++;
        runnable = run;
        callStack = Thread.currentThread().getStackTrace();
    }

    @Override
    public void run() {

        try {
            runnable.run();
        } catch (Throwable e) {

            e.printStackTrace();

            System.err.println();
            System.err.println(ManagedTask.class.getSimpleName() + " #" + id + "/" + count + " - Previous call: ");
            for (StackTraceElement stackTraceElement : callStack) {
                System.err.println("\t" + stackTraceElement);
            }
            System.err.println();
            System.err.println();

            // TODO Throw again exception ?
            // throw e;

        }
    }

    public StackTraceElement[] getCallStack() {
        return callStack;
    }
}
