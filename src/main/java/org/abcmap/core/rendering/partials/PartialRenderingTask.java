package org.abcmap.core.rendering.partials;

abstract class PartialRenderingTask implements Runnable {

    private static final byte INITIALIZED = 0;
    private static final byte FINISHED = 1;

    protected byte status = INITIALIZED;

    @Override
    public abstract void run();

    public boolean isFinished() {
        return status == FINISHED;
    }
    public void markAsFinished() {
        status = FINISHED;
    }
}