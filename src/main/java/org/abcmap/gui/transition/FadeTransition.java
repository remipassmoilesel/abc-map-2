package org.abcmap.gui.transition;

import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.threads.ThreadManager;
import org.abcmap.gui.utils.GuiUtils;

import java.awt.*;

/**
 * Created by remipassmoilesel on 09/01/17.
 */
public class FadeTransition extends Transition {

    private static final CustomLogger logger = LogManager.getLogger(FadeTransition.class);

    private boolean inProgress;
    private Component componentToRepaint;
    private String direction;
    private float currentTransparency;
    private float opacityStep;

    public FadeTransition(Component comp) {
        this.currentTransparency = 1f;
        this.opacityStep = 0.1f;
        this.componentToRepaint = comp;
        this.inProgress = false;
    }

    @Override
    public void start(String dir, Runnable whenFinished) {

        if (inProgress == true) {
            logger.error("Transition already in progress, abort");
            return;
        }

        inProgress = true;

        ThreadManager.runTimerOnEDT(100, () -> {

            // set direction
            this.direction = dir;

            // fade in
            if (Transition.FADE_IN.equals(direction)) {
                currentTransparency -= opacityStep;
            }
            // fade out
            else if (Transition.FADE_OUT.equals(direction)) {
                currentTransparency += opacityStep;
            }
            // error
            else {
                throw new IllegalArgumentException("Unknown transition: " + direction);
            }

            boolean continueTransition = true;

            // check min value and stop if necessary
            if (currentTransparency < 0) {
                currentTransparency = 0;
                continueTransition = false;
            }

            // check max value and stop if necessary
            if (currentTransparency > 1) {
                currentTransparency = 1;
                continueTransition = false;
            }

            // repaint comp
            componentToRepaint.repaint();

            // unlock transition
            inProgress = continueTransition;

            if (continueTransition == false && whenFinished != null) {
                try {
                    whenFinished.run();
                } catch (Exception e) {
                    logger.error(e);
                }
            }

            return continueTransition;

        });

    }

    /**
     * Apply transparency on graphics object
     *
     * @param g2d
     */
    public void applyTransparency(Graphics2D g2d) {

        GuiUtils.throwIfNotOnEDT();

        AlphaComposite composite = AlphaComposite.SrcOver.derive(currentTransparency);
        g2d.setComposite(composite);
    }

    /**
     * Set directly transparency, for example to  at beginnng of transition
     *
     * @param transparency
     */
    public void setTransparency(float transparency) {
        this.currentTransparency = transparency;
    }
}
