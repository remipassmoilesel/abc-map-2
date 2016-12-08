package org.abcmap.gui.components.help;

import org.abcmap.gui.GuiIcons;

import javax.swing.*;

/**
 * Represent a possible interaction with software, like click, CTRL click, ... to help user
 */
public class Interaction {

    public static Interaction SIMPLE_CLICK;
    public static Interaction DOUBLE_CLICK;
    public static Interaction DRAG;

    public static Interaction PRESS_CONTROL;
    public static Interaction PRESS_SHIFT;

    private ImageIcon icon;
    private String toolTipText;

    public static void init() {
        SIMPLE_CLICK = new Interaction(GuiIcons.INTERACTION_SIMPLECLICK, "Clic simple");
        DOUBLE_CLICK = new Interaction(GuiIcons.INTERACTION_DOUBLECLICK, "Double clic");
        PRESS_CONTROL = new Interaction(GuiIcons.INTERACTION_PRESSCTRL, "Presser la touche 'Control'");
        PRESS_SHIFT = new Interaction(GuiIcons.INTERACTION_PRESSMAJ, "Presser la touche 'Majuscule'");
        DRAG = new Interaction(GuiIcons.INTERACTION_DRAG, "Cliquer puis maintenir le bouton gauche de la souris lors d'un déplacement");
    }

    private Interaction(ImageIcon icon, String toolTipText) {
        this.icon = icon;
        this.toolTipText = toolTipText;
    }

    public ImageIcon getIcon() {
        return icon;
    }

    public String getToolTipText() {
        return toolTipText;
    }

}
