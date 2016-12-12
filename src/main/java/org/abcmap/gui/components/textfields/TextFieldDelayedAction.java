package org.abcmap.gui.components.textfields;

import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.threads.ThreadManager;
import org.abcmap.gui.utils.KeyAdapter;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class TextFieldDelayedAction {

    private static final CustomLogger logger = LogManager.getLogger(TextFieldDelayedAction.class);

    /**
     * Time to wait after each interaction
     */
    private int timeToAdd;

    /**
     * Actions to run after end of interactions
     */
    private ArrayList<Runnable> actions;

    /**
     * Set to true to eecute actions on EDT
     * <p>
     * By default, execute actions out of EDT
     */
    private boolean executeActionsInEDT;

    private TextListener listener;
    private TextFieldTimer timer;

    public static TextFieldDelayedAction delayedActionFor(JTextComponent txt, Runnable run, Boolean runOnEDT) {

        if (txt == null) {
            throw new NullPointerException("Textcomponent cannot be null");
        }

        if (run == null) {
            throw new NullPointerException("Runnable action cannot be null");
        }

        TextFieldDelayedAction tfda = new TextFieldDelayedAction();
        tfda.executeActionsInEDT(runOnEDT);
        tfda.addAction(run);

        txt.addKeyListener(tfda.getListener());

        return tfda;
    }

    private TextFieldDelayedAction() {
        this.timeToAdd = 200;
        this.actions = new ArrayList<>(5);
        this.executeActionsInEDT = false;
        this.listener = new TextListener();
    }

    /**
     * Something is typed in textfield, create eventually a timer and launch actions
     */
    private class TextListener extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {

            if (timer == null || timer.isRunning() == false) {
                timer = new TextFieldTimer();
            }

            timer.addTime(timeToAdd);

            if (timer.isRunning() == false) {
                ThreadManager.runLater(timer);
            }

        }

    }

    private class TextFieldTimer implements Runnable {

        private final ReentrantLock lock;
        private int counter;
        private int step;
        private int maxLimit;

        public TextFieldTimer() {
            this.counter = 0;
            this.step = 500;
            this.maxLimit = 1000;

            this.lock = new ReentrantLock();
        }

        @Override
        public void run() {

            if (lock.tryLock() == false) {
                return;
            }

            try {

                while (counter > 0) {

                    boolean error = false;
                    try {
                        Thread.sleep(step);
                    } catch (InterruptedException e) {
                        logger.error(e);
                        error = true;
                    }

                    if (error == false) {
                        counter -= step;
                    }

                }

                executeActions();

            } finally {
                lock.unlock();
            }
        }

        public void addTime(int timeMs) {
            counter += timeMs;

            if (counter > maxLimit) {
                counter = maxLimit;
            }
        }

        public boolean isRunning() {
            return lock.isLocked();
        }

    }

    private void executeActions() {

        Runnable runActions = new Runnable() {
            public void run() {
                for (Runnable runnable : actions) {
                    runnable.run();
                }
            }
        };

        if (executeActionsInEDT) {
            SwingUtilities.invokeLater(runActions);
        }

        //
        else {
            runActions.run();
        }

        //
        timer = null;
    }


    public TextListener getListener() {
        return listener;
    }

    public void setTimeToAdd(int timeToAdd) {
        this.timeToAdd = timeToAdd;
    }

    public void addAction(Runnable action) {
        this.actions.add(action);
    }

    public void executeActionsInEDT(boolean inEDT) {
        this.executeActionsInEDT = inEDT;
    }

}
