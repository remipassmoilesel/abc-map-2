package org.abcmap.gui.dialogs.loading;

import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.threads.ThreadManager;
import org.abcmap.gui.utils.GuiUtils;

import javax.swing.*;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class LoadingDialogManager implements Runnable {

    private static final CustomLogger logger = LogManager.getLogger(LoadingDialogManager.class);

    private static final long FIRST_TIME_WAITING = 1000;
    private static final long TIME_WAITING = 200;
    private final ReentrantLock lock;
    private LoadingDialog dialog;
    private ArrayList<LoadingDialogUser> users;

    LoadingDialogManager() {
        this.dialog = new LoadingDialog();
        this.users = new ArrayList<>();

        lock = new ReentrantLock();
    }

    @Override
    public void run() {

        GuiUtils.throwIfOnEDT();

        if (lock.tryLock() == false) {
            return;
        }

        try {

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (dialog != null) {
                        dialog.setVisible(true);
                    }
                }
            });

            boolean firstLoop = true;
            while (true) {

                // first time
                if (firstLoop) {
                    try {
                        Thread.sleep(FIRST_TIME_WAITING);
                    } catch (InterruptedException e) {
                        logger.error(e);
                    }
                    firstLoop = false;
                }

                // others loop
                else {
                    try {
                        Thread.sleep(TIME_WAITING);
                    } catch (InterruptedException e) {
                        logger.error(e);
                    }

                    for (LoadingDialogUser user : new ArrayList<>(users)) {
                        if (user.areYouStillWorking() == false) {
                            users.remove(user);
                        }
                    }

                    if (users.size() < 1) {
                        break;
                    }
                }

            }

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    dialog.setVisible(false);
                }
            });

            users.clear();

        } finally {
            lock.unlock();
        }
    }

    public boolean isRunning() {
        return lock.isLocked();
    }

    public void stopAtNextLoop(LoadingDialogUser user) {
        users.remove(user);
    }

    public void launch(LoadingDialogUser user) {

        users.add(user);

        if (isRunning() == false) {
            ThreadManager.runLater(this);
        }

    }

}