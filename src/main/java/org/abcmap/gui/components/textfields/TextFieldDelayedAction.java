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

/**
 * Allow to add listener on a textfield which will not be ran too much times.
 * <p>
 * Listeners are executed only after the last interaction on text field, and not before.
 */
public class TextFieldDelayedAction {

    private static final CustomLogger logger = LogManager.getLogger(TextFieldDelayedAction.class);

    /**
     * Time to wait after each interaction
     */
    private int timeToAddAfterInteractions;

    /**
     * Listeners to run after end of interactions
     */
    private ArrayList<Runnable> listeners;

    /**
     * Set to true to execute actions on EDT
     * <p>
     * By default, execute actions out of EDT
     */
    private boolean executeActionsInEDT;

    /**
     * Listen input on text field
     */
    private TextListener textFieldListener;

    /**
     * Timer used to fire event after the last input
     */
    private TextFieldTimer timer;

    /**
     * Maximum time to wait before execute listeners after the last interaction
     */
    private int maximumTimeToWaitAfterLastInteraction;

    /**
     * Create a delayed action for specified text field
     *
     * @param txt
     * @param run
     * @param runOnEDT
     * @return
     */
    public static TextFieldDelayedAction delayedActionFor(JTextComponent txt, Runnable run, Boolean runOnEDT) {

        if (txt == null) {
            throw new NullPointerException("Textcomponent cannot be null");
        }

        if (run == null) {
            throw new NullPointerException("Runnable action cannot be null");
        }

        TextFieldDelayedAction tfda = new TextFieldDelayedAction();
        tfda.executeActionsInEDT(runOnEDT);
        tfda.addListener(run);

        txt.addKeyListener(tfda.getTextFieldListener());

        return tfda;
    }

    private TextFieldDelayedAction() {
        this.timeToAddAfterInteractions = 400;
        this.maximumTimeToWaitAfterLastInteraction = 500;
        this.listeners = new ArrayList<>(5);
        this.executeActionsInEDT = false;
        this.textFieldListener = new TextListener();
    }

    /**
     * Something is typed in textfield, create eventually a timer and launch actions
     */
    private class TextListener extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {

            // create timer if needed
            if (timer == null || timer.isRunning() == false) {
                timer = new TextFieldTimer();
            }

            // add time before run listener
            timer.addTime(timeToAddAfterInteractions);

            // launch timer if needed
            if (timer.isRunning() == false) {
                ThreadManager.runLater(timer);
            }

        }

    }

    /**
     * Special timer on which you can add time after it was launched
     */
    private class TextFieldTimer implements Runnable {

        private final ReentrantLock lock;

        /**
         * Amount of time to wait before run actions
         * <p>
         * Each time an interaction is done, some time is added to this counter
         */
        private int counter;

        /**
         * Time to wait before check counter
         */
        private int step;

        public TextFieldTimer() {
            this.counter = 0;
            this.step = 300;
            this.lock = new ReentrantLock();
        }

        @Override
        public void run() {

            // prevent too much calls
            if (lock.tryLock() == false) {
                return;
            }

            try {

                while (counter > 0) {

                    try {
                        Thread.sleep(step);
                        counter -= step;
                    } catch (InterruptedException e) {
                        logger.error(e);
                    }

                }

                executeListeners();

            } finally {
                lock.unlock();
            }
        }

        public void addTime(int timeMs) {
            counter += timeMs;

            if (counter > maximumTimeToWaitAfterLastInteraction) {
                counter = maximumTimeToWaitAfterLastInteraction;
            }
        }

        public boolean isRunning() {
            return lock.isLocked();
        }

    }

    /**
     * Run listeners, this method should be called after the last interaction
     */
    private void executeListeners() {

        // create a runnable which will run all listeners
        Runnable runActions = () -> {
            for (Runnable runnable : listeners) {
                try {
                    runnable.run();
                } catch (Exception e) {
                    logger.error(e);
                }
            }
        };

        // if specified, run it on EDT
        if (executeActionsInEDT) {
            SwingUtilities.invokeLater(runActions);
        }

        // else run it on current Thread
        else {
            runActions.run();
        }

        // reset timer
        timer = null;
    }

    /**
     * Return the main text field listener. Each time a key is released, this listener add time to counter,
     * <p>
     * in order to run listeners after the last user interaction.
     *
     * @return
     */
    public TextListener getTextFieldListener() {
        return textFieldListener;
    }

    /**
     * Set amount of time to add at each user interaction on counter,
     * <p>
     * in order to run listeners after the last user interaction.
     *
     * @param timeToAddAfterInteractions
     */
    public void setTimeToAddAfterInteractions(int timeToAddAfterInteractions) {
        this.timeToAddAfterInteractions = timeToAddAfterInteractions;
    }

    /**
     * Add a listener to execute after the last user interaction
     *
     * @param toExecuteAfterLastInteraction
     */
    public void addListener(Runnable toExecuteAfterLastInteraction) {
        this.listeners.add(toExecuteAfterLastInteraction);
    }

    /**
     * If set to true, actions will be run on EDT
     *
     * @param inEDT
     */
    public void executeActionsInEDT(boolean inEDT) {
        this.executeActionsInEDT = inEDT;
    }

}
